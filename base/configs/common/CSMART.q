# CSMART.q
# Queries for retrieving saved experiments and recipes in CSMART.
# See org.cougaar.tools.csmart.core.db.DBUtils, org.cougaar.tools.csmart.ui.viewer.OrganizerHelper
database=${org.cougaar.configuration.database}
username=${org.cougaar.configuration.user}
password=${org.cougaar.configuration.password}

queryAssemblyID = \
 SELECT ASSEMBLY_ID, DESCRIPTION \
   FROM V4_ASB_ASSEMBLY \
  WHERE ASSEMBLY_TYPE = ':assembly_type'

# Get list of Agents in this Society
queryAgentNames = \
SELECT DISTINCT A.COMPONENT_NAME \
   FROM V4_ALIB_COMPONENT A, \
        V4_ASB_COMPONENT_HIERARCHY AGENTS \
  WHERE AGENTS.ASSEMBLY_ID :assemblyMatch \
    AND A.COMPONENT_TYPE = 'agent' \
    AND (A.COMPONENT_ALIB_ID=AGENTS.COMPONENT_ALIB_ID \
     OR  A.COMPONENT_ALIB_ID=AGENTS.PARENT_COMPONENT_ALIB_ID)

#queryAgentData = \
# SELECT DISTINCT ALIB.COMPONENT_LIB_ID, ALIB.COMPONENT_TYPE \
#   FROM  V4_ALIB_COMPONENT ALIB, V4_ASB_COMPONENT_HIERARCHY HIER \
#  WHERE HIER.ASSEMBLY_ID :assemblyMatch \
#   AND ALIB.COMPONENT_TYPE='agent'

queryNodes = \
 SELECT DISTINCT ALIB.COMPONENT_NAME \
   FROM V4_ALIB_COMPONENT ALIB \
  WHERE ALIB.COMPONENT_TYPE='node'

queryComponentArgs = \
 SELECT ARGUMENT \
   FROM V4_ASB_COMPONENT_ARG \
   WHERE COMPONENT_ALIB_ID=':comp_alib_id' \
   AND ASSEMBLY_ID :assemblyMatch \
ORDER BY ARGUMENT_ORDER, ARGUMENT

queryExptsWithSociety = \
 SELECT C.NAME \
   FROM V4_EXPT_TRIAL_ASSEMBLY A, V4_ASB_ASSEMBLY B, V4_EXPT_EXPERIMENT C \
  WHERE A.ASSEMBLY_ID = B.ASSEMBLY_ID \
   AND C.EXPT_ID = A.EXPT_ID \
   AND B.DESCRIPTION = ':societyName:'

queryHosts = \
 SELECT H.COMPONENT_ALIB_ID \
   FROM V4_ASB_COMPONENT_HIERARCHY H, V4_ALIB_COMPONENT C \
  WHERE H.COMPONENT_ALIB_ID = C.COMPONENT_ALIB_ID \
    AND C.COMPONENT_TYPE = 'host' \
    AND H.ASSEMBLY_ID :assemblyMatch

queryHostNodes = \
  SELECT HC.COMPONENT_NAME AS HOST_NAME, NC.COMPONENT_NAME AS NODE_NAME \
    FROM V4_ALIB_COMPONENT HC, V4_ASB_COMPONENT_HIERARCHY H, V4_ALIB_COMPONENT NC \
   WHERE HC.COMPONENT_ALIB_ID = H.PARENT_COMPONENT_ALIB_ID \
   AND NC.COMPONENT_ALIB_ID = H.COMPONENT_ALIB_ID \
   AND H.ASSEMBLY_ID :assemblyMatch \
   AND HC.COMPONENT_TYPE = 'host'

queryLibRecipes = \
  SELECT MOD_RECIPE_LIB_ID, NAME \
    FROM V4_LIB_MOD_RECIPE \
ORDER BY NAME

queryRecipeByName = \
  SELECT NAME \
    FROM V4_LIB_MOD_RECIPE \
   WHERE NAME = ':recipe_name:'

queryRecipe = \
  SELECT MOD_RECIPE_LIB_ID, NAME, JAVA_CLASS \
    FROM V4_LIB_MOD_RECIPE \
   WHERE MOD_RECIPE_LIB_ID = ':recipe_id'

queryRecipes = \
  SELECT LMR.MOD_RECIPE_LIB_ID, LMR.NAME, LMR.JAVA_CLASS \
    FROM V4_EXPT_TRIAL_MOD_RECIPE ETMR, V4_LIB_MOD_RECIPE LMR \
   WHERE ETMR.MOD_RECIPE_LIB_ID = LMR.MOD_RECIPE_LIB_ID \
     AND ETMR.TRIAL_ID = ':trial_id' \
     AND ETMR.EXPT_ID = ':expt_id' \
ORDER BY ETMR.RECIPE_ORDER

queryRecipeProperties = \
  SELECT ARG_NAME, ARG_VALUE \
    FROM V4_LIB_MOD_RECIPE_ARG \
   WHERE MOD_RECIPE_LIB_ID = ':recipe_id' \
ORDER BY ARG_ORDER

# Change this to query from expt_trial_config_assembly
# This query used in OrganizerHelper to load the 
# experiment from the DB
queryExperiment = \
 SELECT ASSEMBLY_ID \
   FROM V4_EXPT_TRIAL_CONFIG_ASSEMBLY \
  WHERE EXPT_ID = ':expt_id' \
    AND TRIAL_ID = ':trial_id'

queryExptDescriptions = \
 SELECT DISTINCT A.EXPT_ID, A.DESCRIPTION \
   FROM V4_EXPT_EXPERIMENT A, V4_EXPT_TRIAL B \
  WHERE A.EXPT_ID = B.EXPT_ID

queryTrials = \
 SELECT TRIAL_ID, DESCRIPTION \
   FROM V4_EXPT_TRIAL \
  WHERE EXPT_ID = ':expt_id'

querySocietyName = \
 SELECT DESCRIPTION \
   FROM V4_ASB_ASSEMBLY \
  WHERE ASSEMBLY_ID = ':assembly_id:'

querySocietyByName = \
 SELECT DESCRIPTION \
   FROM V4_ASB_ASSEMBLY \
  WHERE DESCRIPTION = ':society_name:'

queryPluginNames = \
 SELECT DISTINCT LC.COMPONENT_CLASS, AC.COMPONENT_ALIB_ID, AC.COMPONENT_LIB_ID, AC.COMPONENT_NAME, AC.COMPONENT_TYPE \
   FROM V4_ASB_COMPONENT_HIERARCHY HIER, V4_ALIB_COMPONENT AC, V4_ALIB_COMPONENT APC, V4_LIB_COMPONENT LC \
   WHERE APC.COMPONENT_NAME =':agent_name' \
   AND HIER.COMPONENT_ALIB_ID = AC.COMPONENT_ALIB_ID \
   AND HIER.ASSEMBLY_ID :assemblyMatch \
   AND HIER.PARENT_COMPONENT_ALIB_ID = APC.COMPONENT_ALIB_ID \
   AND AC.COMPONENT_LIB_ID = LC.COMPONENT_LIB_ID \
   AND AC.COMPONENT_TYPE :comp_type:

queryComponents = \
 SELECT A.COMPONENT_NAME, C.COMPONENT_CLASS, \
        A.COMPONENT_ALIB_ID, H.INSERTION_ORDER AS INSERTION_ORDER \
   FROM V4_ALIB_COMPONENT A, \
        V4_ALIB_COMPONENT P, \
        V4_ASB_COMPONENT_HIERARCHY H, \
        V4_LIB_COMPONENT C \
  WHERE H.ASSEMBLY_ID :assemblyMatch \
    AND A.COMPONENT_ALIB_ID = H.COMPONENT_ALIB_ID \
    AND P.COMPONENT_ALIB_ID = H.PARENT_COMPONENT_ALIB_ID \
    AND C.COMPONENT_LIB_ID = A.COMPONENT_LIB_ID \
    AND C.INSERTION_POINT = ':insertion_point' \
    AND P.COMPONENT_NAME = ':parent_name' \
ORDER BY INSERTION_ORDER

queryAgentRelationships = \
  SELECT SUPPORTED_COMPONENT_ALIB_ID, ROLE, START_DATE, END_DATE \
    FROM V4_ASB_AGENT_RELATION \
   WHERE SUPPORTING_COMPONENT_ALIB_ID=':agent_name' \
     AND ASSEMBLY_ID :assemblyMatch
   
queryAgentAssetClass = \
  SELECT AGENT_ORG_CLASS \
    FROM V4_LIB_AGENT_ORG \
   WHERE AGENT_LIB_NAME = ':agent_name'

queryAllAgentNames = \
  SELECT DISTINCT C.COMPONENT_NAME \
    FROM V4_ALIB_COMPONENT C \
    WHERE C.COMPONENT_TYPE = 'agent' \
    ORDER BY C.COMPONENT_NAME

# Get all experiments that have the given Society 
# where Society is specified by name (assembly description)
queryExptsWithSociety = \
  SELECT DISTINCT E.NAME \
    FROM V4_EXPT_EXPERIMENT E, V4_EXPT_TRIAL T, V4_ASB_ASSEMBLY A, V4_EXPT_TRIAL_CONFIG_ASSEMBLY C, V4_EXPT_TRIAL_ASSEMBLY R \
	WHERE E.EXPT_ID = T.EXPT_ID \
        AND ((T.TRIAL_ID = R.TRIAL_ID \
	AND R.ASSEMBLY_ID = A.ASSEMBLY_ID) \
	OR \
	(T.TRIAL_ID = C.TRIAL_ID \
	AND C.ASSEMBLY_ID = A.ASSEMBLY_ID)) \
	AND A.DESCRIPTION = ':societyName'

# Get all experiments that have the given Recipe 
# where Recipe is specified by name
queryExptsWithRecipe = \
  SELECT DISTINCT E.NAME \
    FROM V4_EXPT_EXPERIMENT E, V4_EXPT_TRIAL_MOD_RECIPE R, V4_EXPT_TRIAL T, V4_LIB_MOD_RECIPE M \
	WHERE E.EXPT_ID = T.EXPT_ID \
        AND T.TRIAL_ID = R.TRIAL_ID \
	AND M.MOD_RECIPE_LIB_ID = R.MOD_RECIPE_LIB_ID \
	AND M.NAME = ':recipeName'


# Get all experiments that have the given Society
# where Society is specified by name

queryExptsWithSociety = \
	SELECT C.NAME \
	FROM V4_EXPT_TRIAL_ASSEMBLY A, V4_ASB_ASSEMBLY B, V4_EXPT_EXPERIMENT C \
	WHERE A.ASSEMBLY_ID = B.ASSEMBLY_ID \
	AND C.EXPT_ID = A.EXPT_ID \
	AND B.DESCRIPTION = ':societyName'

# get the property groups for a plugin

queryPGId = \
	SELECT DISTINCT PG_ATTRIBUTE_LIB_ID \
	FROM V4_ASB_AGENT_PG_ATTR \
	WHERE COMPONENT_ALIB_ID = ':agent_name' \
	AND ASSEMBLY_ID :assemblyMatch

queryPGAttrs = \
	SELECT PG_NAME, ATTRIBUTE_NAME, ATTRIBUTE_TYPE, AGGREGATE_TYPE \
	FROM V4_LIB_PG_ATTRIBUTE \
	WHERE PG_ATTRIBUTE_LIB_ID = ':pgAttrLibId'

queryPGValues = \
	SELECT ATTRIBUTE_VALUE, START_DATE, END_DATE \
	FROM V4_ASB_AGENT_PG_ATTR \
	WHERE PG_ATTRIBUTE_LIB_ID = ':pgAttrLibId' \
	AND COMPONENT_ALIB_ID = ':agent_name' \
	AND ASSEMBLY_ID :assemblyMatch \
	ORDER BY ATTRIBUTE_ORDER

queryCMTAssembly = \
	SELECT '1' \
	FROM V4_EXPT_TRIAL A, V4_EXPT_TRIAL_CONFIG_ASSEMBLY B \
	WHERE A.EXPT_ID = ':expt_id:' \
	AND A.TRIAL_ID = B.TRIAL_ID \
	AND ASSEMBLY_ID LIKE 'CMT%'

queryAgentAssetData = \
        SELECT '1' \
        FROM V4_ASB_AGENT \
        WHERE ASSEMBLY_ID = ':assembly_id' \
        AND COMPONENT_ALIB_ID = ':agent:'