Database = ${oplan.test.database}
Username=${oplan.test.database.user}
Password=${oplan.test.database.password}
oplanid = 092MP

# get AlpLoc info
%AlpLocQueryHandler
AlpLocQuery = select alploc_code, location_name, latitude, longitude from alploc

# get GeoLoc info
%GeoLocQueryHandler
GeoLocQuery.oracle = select geoloc_code, location_name, installation_type_code, civil_aviation_code, latitude, longitude, country_state_code, country_state_long_name  from geoloc where geoloc_code in (select relates_to from drop_relationship where relation_name = 'LOCATION')

GeoLocQuery.mysql = select distinct g.geoloc_code, g.location_name, g.installation_type_code, g.civil_aviation_code, g.latitude, g.longitude, g.country_state_code, g.country_state_long_name from geoloc g, drop_relationship dr where g.geoloc_code = dr.relates_to and dr.relation_name = 'LOCATION'

GeoLocQuery.postgresql = select distinct g.geoloc_code, g.location_name, g.installation_type_code, g.civil_aviation_code, g.latitude, g.longitude, g.country_state_code, g.country_state_long_name from geoloc g, drop_relationship dr where g.geoloc_code = dr.relates_to\:\:char(4) and dr.relation_name = 'LOCATION'

# get Oplan info
%OplanQueryHandler
OplanInfoQuery = select operation_name, priority, c0_date from oplan where oplan_id = ':oplanid'

#get OrgActivities
%OrgActivityQueryHandler
activity = ACTIVITY
opTempo = OPTEMPO
location = LOCATION
OrgActivityQuery = select relation_name, force, force_type, relates_to, relates_to_type, start_day, end_day, last_modified from drop_relationship where oplan = ':oplanid' and relation_name in (':activity', ':opTempo', ':location')








