<?xml version="1.0"?>

<!--
<copyright>
 Copyright 2001-2004 BBNT Solutions, LLC
 under sponsorship of the Defense Advanced Research Projects Agency (DARPA).

 This program is free software; you can redistribute it and/or modify
 it under the terms of the Cougaar Open Source License as published by
 DARPA on the Cougaar Open Source Website (www.cougaar.org).

 THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 PERFORMANCE OF THE COUGAAR SOFTWARE.
</copyright>
-->

<!--
This XSL script generates a new in-memory "society.xsl" script with
all the necessary template rules to expand a specific "mySociety.xml"
configuration.

This is a "meta" script, since it's a script that generates another
script, so it's a bit confusing to at first...

This script searches the "mySociety.xml" file for agent and node
elements and their "template" attributes.  The default template
for an agent is "SimpleAgent.xsl", and the default for a node
is "NodeAgent.xsl".  With just these defaults this script will
generate:
  <?xml version="1.0"?>
  <xsl:stylesheet version="1.0">
    <xsl:include href="SimpleAgent.xsl"/>
    <xsl:include href="NodeAgent.xsl"/>
    // plus check for unknown agent/node templates
  </xsl:stylesheet>
which is saved in the Cougaar release as:
  $COUGAAR_INSTALL_PATH/configs/common/society.xsl
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <!--
  optional xsl parameters, passed by:
    -Dorg.cougaar.society.xsl.param.$name=$value
  -->
  <xsl:param name="defaultAgent">SimpleAgent.xsl</xsl:param>
  <xsl:param name="defaultNode">NodeAgent.xsl</xsl:param>

  <xsl:output method="xsl" indent="yes"/>

  <xsl:key name="templates" match="agent|node" use="@template"/>

  <!-- top-level rule -->
  <xsl:template match="/">
    <xsl:comment>
      <xsl:text> Machine generated by "make_society.xsl"! </xsl:text>
    </xsl:comment>
    <!-- start stylesheet -->
    <xsl:element name="xsl:stylesheet">
      <xsl:attribute name="version">
        <xsl:text>1.0</xsl:text>
      </xsl:attribute>
      <!-- get top-level "find-templates" boolean -->
      <xsl:variable 
        name="find-templates"
        select="not(/*/@find-templates = 'false')"/>
      <xsl:if test="$find-templates">
        <!-- add template includes -->
        <xsl:call-template name="addTemplates"/>
      </xsl:if>
      <!-- 
      Inline raw xsl.

      This is a powerful feature that allows the XML file to specify
      its own templates and parameters.  Another option is to use
      an xsl-spreadsheet command:
        <xml-stylesheet type="text/xml" href="society.xsl"?>
      -->
      <xsl:for-each select="/*/xsl:stylesheet/*">
        <xsl:element name="{name()}" namespace="{namespace-uri()}">
          <xsl:copy-of select="node()|@*" />
        </xsl:element>
      </xsl:for-each>
      <xsl:if test="$find-templates">
        <!-- check for unknown templates (sanity check) -->
        <xsl:call-template name="checkForUnknownTemplates"/>
      </xsl:if>
    </xsl:element>
    <!-- end stylesheet -->
  </xsl:template>

  <!-- disable the default printing of element values -->
  <xsl:template match="text()"/>

  <!--
  Find agent/node template rules and include them.
  -->
  <xsl:template name="addTemplates">
    <xsl:comment> use "include" to check for conflicts </xsl:comment>
    <!-- check for default agent/node -->
    <xsl:variable 
      name="needs-defaultAgent"
      select="//agent[not(@template) and not(@type)]"/>
    <xsl:variable 
      name="needs-defaultNode"
      select="//node[not(@template) and not(@type)]"/>
    <!-- default agent -->
    <xsl:if test="$needs-defaultAgent">
      <xsl:element name="xsl:include">
        <xsl:attribute name="href">
          <xsl:value-of select="$defaultAgent"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>
    <!-- default node -->
    <xsl:if test="$needs-defaultNode">
      <xsl:element name="xsl:include">
        <xsl:attribute name="href">
          <xsl:value-of select="$defaultNode"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>
    <!-- unique explicit templates (uses a key and the "Muenchian Method") -->
    <xsl:for-each select="//node|//agent">
      <xsl:if test="@template and count(. | key('templates', @template)[1]) = 1">
        <!-- filter out our defaults -->
        <xsl:if test="not(@template = $defaultAgent and $needs-defaultAgent) and not(@template = $defaultNode and $needs-defaultNode)">
          <xsl:element name="xsl:include">
            <xsl:attribute name="href">
              <xsl:value-of select="@template"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!--
  Add rule to check for unknown agent/node templates.

  This is usually a sanity-check, since the above "addTemplates"
  should include all the templates.  However, if the output of this
  script is saved, then this rule will make sure that any changes to
  "mySociety.xml" that would require new includes will be identified.
  -->
  <xsl:template name="checkForUnknownTemplates">
    <xsl:comment> check for unknown templates </xsl:comment>
    <xsl:element name="xsl:template">
      <xsl:attribute name="match">agent|node</xsl:attribute>
      <xsl:element name="xsl:message">
        <xsl:element name="xsl:text">
          <xsl:text>Unknown template "</xsl:text>
        </xsl:element>
        <xsl:element name="xsl:value-of">
          <xsl:attribute name="select">
            <xsl:text>@template</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="xsl:text">
          <xsl:text>" specified by </xsl:text>
        </xsl:element>
        <xsl:element name="xsl:value-of">
          <xsl:attribute name="select">
            <xsl:text>name()</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="xsl:text">
          <xsl:text> "</xsl:text>
        </xsl:element>
        <xsl:element name="xsl:value-of">
          <xsl:attribute name="select">
            <xsl:text>@name</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="xsl:text">
          <xsl:text>", need to run "make_society.xsl" again?</xsl:text>
        </xsl:element>
      </xsl:element>
      <xsl:element name="xsl:apply-templates"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
