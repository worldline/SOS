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
package org.n52.sos.config;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.FileSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.NumericSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * Abstract implementation that does everything exept object instantiation.
 * <p/>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractSettingValueFactory implements SettingValueFactory {

    private static final Set<String> VALID_FALSE_VALUES = ImmutableSet.of("false", "no", "off", "0");

    private static final Set<String> VALID_TRUE_VALUES = ImmutableSet.of("true", "yes", "on", "1");

    @Override
    public SettingValue<Boolean> newBooleanSettingValue(BooleanSettingDefinition setting, String stringValue) {
        return newBooleanSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Boolean> newBooleanSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newBooleanSettingValue().setValue(parseBoolean(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<Integer> newIntegerSettingValue(IntegerSettingDefinition setting, String stringValue) {
        return newIntegerSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Integer> newIntegerSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newIntegerSettingValue().setValue(parseInteger(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<Double> newNumericSettingValue(NumericSettingDefinition setting, String stringValue) {
        return newNumericSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Double> newNumericSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newNumericSettingValue().setValue(parseDouble(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<String> newStringSettingValue(StringSettingDefinition setting, String stringValue) {
        return newStringSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<String> newStringSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newStringSettingValue().setValue(parseString(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<File> newFileSettingValue(FileSettingDefinition setting, String stringValue) {
        return newFileSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<File> newFileSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newFileSettingValue().setValue(parseFile(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<URI> newUriSettingValue(UriSettingDefinition setting, String stringValue) {
        return newUriSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<URI> newUriSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newUriSettingValue().setValue(parseUri(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<?> newSettingValue(SettingDefinition<?, ?> setting, String value) {
        switch (setting.getType()) {
        case BOOLEAN:
            return newBooleanSettingValueFromGenericDefinition(setting, value);
        case FILE:
            return newFileSettingValueFromGenericDefinition(setting, value);
        case INTEGER:
            return newIntegerSettingValueFromGenericDefinition(setting, value);
        case NUMERIC:
            return newNumericSettingValueFromGenericDefinition(setting, value);
        case STRING:
            return newStringSettingValueFromGenericDefinition(setting, value);
        case URI:
            return newUriSettingValueFromGenericDefinition(setting, value);
        default:
            throw new IllegalArgumentException(String.format("Type %s not supported", setting.getType()));
        }
    }

    /**
     * Parses the a string to a {@code Boolean}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Boolean parseBoolean(String stringValue) throws IllegalArgumentException {
        if (nullOrEmpty(stringValue)) {
            return Boolean.FALSE;
        }
        stringValue = stringValue.trim().toLowerCase();
        if (VALID_FALSE_VALUES.contains(stringValue)) {
            return Boolean.FALSE;
        } else if (VALID_TRUE_VALUES.contains(stringValue)) {
            return Boolean.TRUE;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a valid boolean value", stringValue));
        }
    }

    /**
     * Parses the a string to a {@code File}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected File parseFile(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : new File(stringValue);
    }

    /**
     * Parses the a string to a {@code Integer}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Integer parseInteger(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : Integer.valueOf(stringValue, 10);
    }

    /**
     * Parses the a string to a {@code Double}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Double parseDouble(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : Double.parseDouble(stringValue);
    }

    /**
     * Parses the a string to a {@code URI}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected URI parseUri(String stringValue) throws IllegalArgumentException {
        if (nullOrEmpty(stringValue)) {
            return null;
        }
        try {
            return new URI(stringValue);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses the a string to a {@code String}.
     * <p/>
     * 
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected String parseString(String stringValue) {
        return nullOrEmpty(stringValue) ? null : stringValue;
    }

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Boolean> newBooleanSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Integer> newIntegerSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<String> newStringSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<File> newFileSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<URI> newUriSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Double> newNumericSettingValue();

    /**
     * @param stringValue
     *            <p/>
     * @return <code>stringValue == null || stringValue.trim().isEmpty()</code>
     */
    protected boolean nullOrEmpty(String stringValue) {
        return stringValue == null || stringValue.trim().isEmpty();
    }
}
