# Used by glm/src/org/cougaar/mlm/plugin/ldm/OrgActivityQueryHandler
# Referenced by the glm/src/org/cougaar/mlm/plugin/GLSInitServlet
# Note that the word select must be lowercase cause of bad plugin

# This version does not depend on a pre-existing CSMART configuration

Database=${org.cougaar.refconfig.database}
Username=${org.cougaar.refconfig.user}
Password=${org.cougaar.refconfig.password}
oplanid = 093FF
activity = ACTIVITY_TYPE
opTempo = OPTEMPO
location = LOCATION

# get AlpLoc info
%AlpLocQueryHandler
AlpLocQuery = select alploc_code, location_name, latitude, longitude from alploc

# get GeoLoc info
%GeoLocQueryHandler
GeoLocQuery = \
select DISTINCT \
    GEOLOC_CODE, \
    LOCATION_NAME, \
    INSTALLATION_TYPE_CODE, \
    CIVIL_AVIATION_CODE, \
    LATITUDE, \
    LONGITUDE, \
    COUNTRY_STATE_CODE, \
    COUNTRY_STATE_LONG_NAME \
FROM geoloc, \
     oplan_agent_attr \
WHERE ATTRIBUTE_NAME = 'LOCATION' \
    AND GEOLOC_CODE=ATTRIBUTE_VALUE

# get Oplan info
%OplanQueryHandler
OplanInfoQuery = \
select DISTINCT OPERATION_NAME, \
   PRIORITY, \
   C0_DATE \
FROM oplan OPLAN
WHERE OPLAN_ID = '093FF' 

#Get Orgactivities
%OrgActivityQueryHandler

OrgActivityQuery.mysql = \
select DISTINCT ATTRIBUTE_NAME AS RELATION_NAME, \
    ORG_ID AS FORCE, \
    'FORCE_TYPE' AS FORCE_TYPE, \
    ATTRIBUTE_VALUE AS RELATES_TO, \
    'RELATES_TO_TYPE' AS RELATES_TO_TYPE, \
    START_CDAY AS START_DAY, \
    END_CDAY AS END_DAY, \
    OP.C0_DATE AS LAST_MODIFIED \
 FROM oplan_agent_attr ATTR, \
      oplan OP \
 WHERE \
  ATTR.OPLAN_ID = '093FF' \
  AND OP.OPLAN_ID = '093FF' \
  AND ATTRIBUTE_NAME IN ('ACTIVITY_TYPE','OPTEMPO','LOCATION')
