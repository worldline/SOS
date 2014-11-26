/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ext.deleteobservation;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.series.SeriesDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationDAO extends DeleteObservationAbstractDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationDAO.class);
	
    private HibernateSessionHolder hibernateSessionHolder = new HibernateSessionHolder();

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = new DeleteObservationResponse();
        response.setVersion(request.getVersion());
        response.setService(request.getService());

        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateSessionHolder.getSession();
            transaction = session.beginTransaction();
            AbstractObservation observation = null;

            LOGGER.debug("request : " + request);
            String procedureIdentifier = request.getProcedureIdentifier();
            String observableProperty = request.getObservableProperty();
            DateTime resultTime = request.getResultTime();
           
            try {
                observation = getObservationCriteriaFor(procedureIdentifier, observableProperty, resultTime, session);
            } catch (HibernateException he) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new InvalidParameterValueException(getInvalidParamsMap(procedureIdentifier, observableProperty, resultTime));
            }
            OmObservation so = null;
            if (observation != null) {
              /*  so =
                        HibernateObservationUtilities
                                .createSosObservationsFromObservations(Collections.singleton(observation), null,
                                        request.getVersion(), null, session).iterator().next();*/
                so = HibernateObservationUtilities.createSosObservationFromObservation(observation, request.getVersion(), null, session);
                session.delete(observation);
                checkSeriesForFirstLatest(observation, session);
                session.flush();
            } else {
                throw new InvalidParameterValueException(getInvalidParamsMap(procedureIdentifier, observableProperty, resultTime));
            }
            transaction.commit();
            //response.setObservationId(request.getObservationIdentifier());
            response.setProcedureIdentifier(procedureIdentifier);
            response.setObservableProperty(observableProperty);
            response.setResultTime(resultTime);
            response.setDeletedObservation(so);
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while updating deleted observation flag data!");
        } catch(ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage(
                    "Error while updating deleted observation flag data!");
        } finally {
            hibernateSessionHolder.returnSession(session);
        }
        return response;
    }

    public Map<String, String> getInvalidParamsMap(String procedureIdentifier, String observableProperty, DateTime resultTime) {
        Map<String, String> invalidParameters =  new HashMap<>();
        invalidParameters.put(DeleteObservationConstants.PROCEDURE_PARAM, procedureIdentifier);
        invalidParameters.put(DeleteObservationConstants.OBSERVABLE_PARAM, observableProperty);
        invalidParameters.put(DeleteObservationConstants.RESULT_TIME_PARAM, resultTime.toString());
        return invalidParameters;
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

	/**
	 * Check if {@link Series} should be updated
	 * 
	 * @param observation
	 *            Deleted observation
	 * @param session
	 *            Hibernate session
	 */
	private void checkSeriesForFirstLatest(AbstractObservation observation, Session session) {
		if (observation instanceof SeriesObservation) {
			Series series = ((SeriesObservation)observation).getSeries();
			if (series.getFirstTimeStamp().equals(observation.getPhenomenonTimeStart()) || series.getLastTimeStamp().equals(observation.getPhenomenonTimeEnd())) {
				new SeriesDAO().updateSeriesAfterObservationDeletion(series, (SeriesObservation)observation, session);
			}
		}
	}

    private AbstractObservation getObservationCriteriaFor(String procedure, String observableProperty, DateTime resultTime, Session session) {
        resultTime = resultTime.toDateTime(DateTimeZone.UTC);
        Date date = new Date(resultTime.getMillis());

        Criteria criteria;
        if (HibernateHelper.isEntitySupported(SeriesObservation.class, session)) {
            criteria = session.createCriteria(SeriesObservation.class).add(Restrictions.eq(AbstractObservation.RESULT_TIME, date));
            Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
            seriesCriteria.createCriteria(AbstractObservation.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));
            seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).add(eq(ObservableProperty.IDENTIFIER, observableProperty));
        } else {
            criteria = session.createCriteria(Observation.class).add(Restrictions.eq(AbstractObservation.RESULT_TIME, date));
            criteria.createCriteria(AbstractObservation.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));
            criteria.createCriteria(AbstractObservation.OBSERVABLE_PROPERTY).add(eq(ObservableProperty.IDENTIFIER, observableProperty));
        }
        LOGGER.debug("QUERY getCriteria(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractObservation) criteria.uniqueResult();
    }
}
