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
XSL utility methods for selecting components.

This stylesheet is imported by agent templates, such as
"SimpleAgent.xsl".
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <!-- utility methods: -->

  <!--
  Find components at the specified priority and insertion point, where
  the default insertion point selects agent-level components.

  The default component priority is "COMPONENT", and the default
  insertionpoint is "Node.AgentManager.Agent.PluginManager.Plugin".
  If the name attribute is not specified, then it is generated as
  the classname+"("+comma-separated-arguments+")".

  We want "${insertionpoint}[^\.]*+", i.e. all direct subcomponents.
  -->
  <xsl:template name="find">
    <xsl:param name="priority">COMPONENT</xsl:param>
    <xsl:param name="insertionpoint">Node.AgentManager.Agent.</xsl:param>
    <xsl:param name="default_priority">COMPONENT</xsl:param>
    <xsl:param name="default_insertionpoint">Node.AgentManager.Agent.PluginManager.Plugin</xsl:param>
    <xsl:for-each select="component[(@priority=$priority) or (not(@priority) and ($default_priority=$priority))]">
      <xsl:variable name="ip">
        <xsl:choose>
          <xsl:when test="@insertionpoint">
            <xsl:value-of select="@insertionpoint"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$default_insertionpoint"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:if test="starts-with($ip, $insertionpoint)">
        <xsl:variable name="ext">
          <xsl:value-of select="substring-after(@ip, $insertionpoint)"/>
        </xsl:variable>
        <xsl:if test="not(contains($ext, '.'))">
          <xsl:element name="{name(.)}" namespace="{namespace-uri()}">
            <xsl:copy-of select="@*"/>
            <xsl:if test="not(@insertionpoint)">
              <xsl:attribute name="insertionpoint">
                <xsl:value-of select="$default_insertionpoint"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(@priority)">
              <xsl:attribute name="priority">
                <xsl:value-of select="$default_priority"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(@name)">
              <xsl:attribute name="name">
                <xsl:value-of select="@class"/>
                <xsl:text>(</xsl:text>
                <xsl:for-each select="argument">
                  <xsl:value-of select="normalize-space(.)"/>
                  <xsl:if test="position() != last()">,</xsl:if> 
                </xsl:for-each>
                <xsl:text>)</xsl:text>
              </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="argument">
              <xsl:element name="{name(.)}" namespace="{namespace-uri()}">
                <xsl:value-of select="normalize-space(.)"/>
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!--
  Find all components at HIGH/INTERNAL/BINDER/COMPONENT/LOW priorities.

  This assumes that there are no second-level child components, otherwise
  their components won't be printed, e.g. for "findAll("a"):
    <component .. insertionpoint="a.b"/>     will match
    <component .. insertionpoint="a.b.c"/>   will be skipped!
  This could be fixed to recursively find components and select subcomponents
  in the correct order, but we don't require this.  Besides, it should be
  possible to load the container well before the child components (e.g.
  container at HIGH, load siblings, then child components at LOW).
  -->
  <xsl:template name="findAll">
    <xsl:param name="insertionpoint">Node.AgentManager.Agent.</xsl:param>
    <xsl:call-template name="find">
      <xsl:with-param name="priority">HIGH</xsl:with-param>
      <xsl:with-param name="insertionpoint"><xsl:value-of select="$insertionpoint"/></xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="find">
      <xsl:with-param name="priority">INTERNAL</xsl:with-param>
      <xsl:with-param name="insertionpoint"><xsl:value-of select="$insertionpoint"/></xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="find">
      <xsl:with-param name="priority">BINDER</xsl:with-param>
      <xsl:with-param name="insertionpoint"><xsl:value-of select="$insertionpoint"/></xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="find">
      <xsl:with-param name="priority">COMPONENT</xsl:with-param>
      <xsl:with-param name="insertionpoint"><xsl:value-of select="$insertionpoint"/></xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="find">
      <xsl:with-param name="priority">LOW</xsl:with-param>
      <xsl:with-param name="insertionpoint"><xsl:value-of select="$insertionpoint"/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>