database=${org.cougaar.configuration.database}
username=${org.cougaar.configuration.user}
password=${org.cougaar.configuration.password}

queryAssemblyID = \
 SELECT ASSEMBLY_ID, DESCRIPTION \
   FROM V4_ASB_ASSEMBLY \
  WHERE ASSEMBLY_TYPE = ':assembly_type'

queryAgentNames = \
SELECT A.COMPONENT_NAME \
   FROM V4_ALIB_COMPONENT A, \
        V4_ASB_AGENT AGENTS \
  WHERE AGENTS.ASSEMBLY_ID :assemblyMatch \
    AND A.COMPONENT_ALIB_ID=AGENTS.COMPONENT_ALIB_ID

queryAgentData = \
 SELECT DISTINCT ALIB.COMPONENT_LIB_ID, ALIB.COMPONENT_TYPE \
   FROM  V4_ALIB_COMPONENT ALIB, V4_ASB_COMPONENT_HIERARCHY HIER \
  WHERE HIER.ASSEMBLY_ID :assemblyMatch \
   AND ALIB.COMPONENT_TYPE='agent'

queryNodes = \
 SELECT DISTINCT ALIB.COMPONENT_NAME \
   FROM V4_ALIB_COMPONENT ALIB, V4_ASB_COMPONENT_HIERARCHY HIER \
  WHERE HIER.ASSEMBLY_ID :assemblyMatch \
   AND ALIB.COMPONENT_TYPE='node'

queryHosts = \
 SELECT COMPONENT_LIB_ID \
   FROM V4_LIB_COMPONENT \
  WHERE COMPONENT_TYPE = ':component_type'

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

queryExperiment = \
 SELECT ASSEMBLY_ID \
   FROM V4_EXPT_TRIAL_ASSEMBLY \
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

queryPluginNames = \
 SELECT DISTINCT LC.COMPONENT_CLASS, AC.COMPONENT_ALIB_ID, AC.COMPONENT_LIB_ID \
   FROM V4_ASB_COMPONENT_HIERARCHY HIER, V4_ALIB_COMPONENT AC, V4_ALIB_COMPONENT APC, V4_LIB_COMPONENT LC \
   WHERE APC.COMPONENT_NAME =':agent_name' \
   AND HIER.COMPONENT_ALIB_ID = AC.COMPONENT_ALIB_ID \
   AND HIER.ASSEMBLY_ID :assemblyMatch \
   AND HIER.PARENT_COMPONENT_ALIB_ID = APC.COMPONENT_ALIB_ID \
   AND AC.COMPONENT_LIB_ID = LC.COMPONENT_LIB_ID \
   AND AC.COMPONENT_TYPE='plugin'

queryPluginArgs = \
 SELECT ARGUMENT \
   FROM V4_ASB_COMPONENT_ARG \
   WHERE COMPONENT_ALIB_ID=':comp_alib_id' \
   AND ASSEMBLY_ID :assemblyMatch

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





