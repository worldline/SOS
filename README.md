# 52°North SOS

README file for the 52°North Sensor Observation Service (SOS) version 4.1

This is the repository of the [52°North Sensor Observation Service (SOS)][1].

## Changes done by Worldline

The request deleteObservation has been modified to delete permanently the observation in the database instead of set the flag "deleted" to true : 
* change the request format. You need to provide :
	- procedure identifier
	- observable property identifier
	- result time 
* You do not need the observation identifier. You can find the request examples in webapp folder. 

* Formats KVP, JSON, SOAP and POX are concerned by the changes. 
	
* Example KVP :
	
	http://localhost:8080/service?service=SOS&version=2.0.0&request=DeleteObservation&procedureIdentifier=http://www.52north.org/test/procedure/9&observableProperty=http://www.52north.org/test/observableProperty/9_3&resultTime=2012-11-19T13:44:00%2b01:00
	
* Example SOAP : 
		
	```
		<?xml version="1.0" encoding="UTF-8"?>
		<env:Envelope
			xmlns:env="http://www.w3.org/2003/05/soap-envelope"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.w3.org/2003/05/soap-envelope http://www.w3.org/2003/05/soap-envelope/soap-envelope.xsd">
			<env:Body>
				<sosdo:DeleteObservation
					xmlns:sosdo="http://www.opengis.net/sosdo/1.0" version="2.0.0" service="SOS">
					<sosdo:procedureIdentifier>http://www.52north.org/test/procedure/9</sosdo:procedureIdentifier>
					<sosdo:observableProperty>http://www.52north.org/test/observableProperty/9_3</sosdo:observableProperty>
					<sosdo:resultTime>2012-11-19T13:30:00+01:00</sosdo:resultTime>
				</sosdo:DeleteObservation>
			</env:Body>
		</env:Envelope>
	```
	
* Example POX 
	
	```
		<?xml version="1.0" encoding="UTF-8"?>
		<sosdo:DeleteObservation
			xmlns:sosdo="http://www.opengis.net/sosdo/1.0" version="2.0.0" service="SOS">
			<sosdo:procedureIdentifier>http://www.52north.org/test/procedure/9</sosdo:procedureIdentifier>
			<sosdo:observableProperty>http://www.52north.org/test/observableProperty/9_3</sosdo:observableProperty>
			<sosdo:resultTime>2012-11-19T13:30:00+01:00</sosdo:resultTime>
		</sosdo:DeleteObservation>
	```
* Example JSON : none
	
* it implies : 
	- series suppression if the observation was the last remaining or series update else
	- the other things done by the original 52°North Sensor Observation Service delete request stay unchanged

The 52°North SOS is a reference implementation of the
[OGC Sensor Observation Service specification (version 2.0)][2]. It was
implemented during the [OGC Web Services Testbed,  Phase 9 (OWS-9)][3] and
tested  to be compliant to this specification within the [OGC CITE testing][4]
in December of 2012.

## Build Status
* Master: [![Master Build Status](https://travis-ci.org/52North/SOS.png?branch=master)](https://travis-ci.org/52North/SOS)
* Develop: [![Develop Build Status](https://travis-ci.org/52North/SOS.png?branch=develop)](https://travis-ci.org/52North/SOS)

## Branches

This project follows the  [Gitflow branching model](http://nvie.com/posts/a-successful-git-branching-model/). "master" reflects the latest stable release.
Ongoing development is done in branch [develop](../../tree/develop) and dedicated feature branches (feature-*).

## Code Compilation

This project is managed with Maven3. Simply run `mvn clean install`
to create a deployable .WAR file. `mvn clean install -Pdevelop`
additionally enables integration tests.

## Distributions

Here you can find some information that relates to the distributions of the 52°North SOS.

### Download

The latest release of 52°North SOS can be downloaded from this website:

    http://52north.org/downloads/sensor-web/sos

### Contents
  * `/src` :                 The source files of 52°North SOS modules
  * `/bin` :                 Executable binary of 52°North SOS webapp module
  * `LICENSE` :              The license of 52°North SOS
  * `NOTICE` :               Third Party libraries and their licenses
  * `README` :               This file
  * `RELEASE-NOTES` :        The release notes of the 52°North SOS

No printer friendly documentation exist for this release. Instead, refer to the [wiki documentation][5].

### Installation

No printer friendly installation guide exist for this release. Instead, refer to the [wiki documentation][5].

## Support and Contact

You can get support in the community mailing list and forums:

    http://52north.org/resources/mailing-list-and-forums/

If you encounter any issues with the software or if you would like to see
certain functionality added, let us know at:

 - Carsten Hollmann (c.hollmann@52north.org)
 - Christian Autermann (c.autermann@52north.org)
 - Eike Hinderk Jürrens [@EHJ-52n](e.h.juerrens@52north.org)

The Sensor Web Community
52°North Inititative for Geospatial Open Source Software GmbH, Germany

--
[1]: http://52north.org/communities/sensorweb/sos/index.html
[2]: https://portal.opengeospatial.org/files/?artifact_id=47599
[3]: http://www.ogcnetwork.net/ows-9
[4]: http://cite.opengeospatial.org/test_engine
[5]: https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation
