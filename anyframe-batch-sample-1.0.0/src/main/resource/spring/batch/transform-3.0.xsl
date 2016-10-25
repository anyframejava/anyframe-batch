<xsl:stylesheet version="2.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xslt="http://xml.apache.org/xslt"
                xmlns="http://www.anyframejava.org/schema/batch" >
<xsl:output method="xml" indent="yes" xslt:indent-amount="4" cdata-section-elements="script" />
<xsl:strip-space elements="*"/>

<xsl:template match="/">
	<xsl:apply-templates select="batch/job"/>
</xsl:template>

<xsl:template match="job">
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:tx="http://www.springframework.org/schema/tx" xmlns:jee="http://www.springframework.org/schema/jee"
	xsi:schemaLocation="http://www.anyframejava.org/schema/batch http://www.anyframejava.org/schema/batch/schema.xsd 
	                    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
	                    http://www.springframework.org/schema/aop 
                        http://www.springframework.org/schema/aop/spring-aop-2.0.xsd
	                    http://www.springframework.org/schema/jee 
                        http://www.springframework.org/schema/jee/spring-jee-2.0.xsd">
	
	<job id="{@id}" name="{@name}" xmlns="http://www.anyframejava.org/schema/batch" >
		<xsl:if test="@concurrent">
			<xsl:attribute name="concurrent">
				<xsl:value-of select="@concurrent"/>
			</xsl:attribute>
		</xsl:if>
		
		<xsl:apply-templates select="description"/>
		
		<xsl:if test="listener">
			<listeners>
				<xsl:apply-templates select="listener"/>
			</listeners>
		</xsl:if>
		
		<xsl:apply-templates select="step"/>
	</job>
	
</beans>
</xsl:template>

<xsl:template match="description">
	<description><xsl:value-of select="."/></description>
</xsl:template>

<xsl:template match="listener">
	<listener><xsl:value-of select="."/></listener>
</xsl:template>

<xsl:template match="step[@type='java']">
	<step id="{@id}" type="{@type}" class="{@class}">
		<xsl:apply-templates select="description"/>
		
		<xsl:if test="stepparam">
			<parameters>
				<xsl:apply-templates select="stepparam"/>
			</parameters>
		</xsl:if>
		
		<xsl:if test="listener">
			<listeners>
				<xsl:apply-templates select="listener"/>
			</listeners>
		</xsl:if>
		
		<xsl:if test="resource">
			<resources>
				<xsl:apply-templates select="resource"/>
			</resources>
		</xsl:if>
	</step>
</xsl:template>

<xsl:template match="step[@type='parallel']">
	<step id="{@id}" type="{@type}">
		<xsl:apply-templates select="description"/>
		
		<xsl:apply-templates select="thread"/>
	</step>
</xsl:template>

<xsl:template match="thread">
	<step id="{concat(concat(../@id,'_'), @seq)}" type="java" class="{../@class}">
		<xsl:apply-templates select="description"/>
		
		<parameters>
			<parameter key="sequence">
				<xsl:value-of select="@seq"/>
			</parameter>
			<xsl:apply-templates select="stepparam"/>
		</parameters>
		
		<xsl:if test="listener">
			<listeners>
				<xsl:apply-templates select="listener"/>
			</listeners>
		</xsl:if>
		
		<xsl:if test="resource|../resource">
			<resources>
				<xsl:apply-templates select="resource"/>
				<xsl:apply-templates select="../resource"/>
			</resources>
		</xsl:if>
	</step>
</xsl:template>

<xsl:template match="step[@type='delete']">
	<step id="{@id}" type="delete">
		<xsl:apply-templates select="description"/>
		
		<xsl:if test="resource">
			<resources>
				<xsl:for-each select="resource">
					<resource url="{@url}"/>
				</xsl:for-each>
			</resources>
		</xsl:if>
	</step>

</xsl:template>

<xsl:template match="step[@type='shell']">
	<step id="{@id}" type="shell">
		<xsl:apply-templates select="description"/>
		
		<script><xsl:copy-of select="text()" /></script>
	</step>
</xsl:template>

<xsl:template match="stepparam">
	<parameter key="{@id}"><xsl:value-of select="."/></parameter>
</xsl:template>

<xsl:template match="resource[@inout='IN']">
	<reader>
		<xsl:copy-of select="@*[name()!='inout' and name()!='charSet']"/>
		<xsl:if test="@charSet">
			<xsl:attribute name="charset">
				<xsl:value-of select="@charSet"/>
			</xsl:attribute>
		</xsl:if>
	</reader>
</xsl:template>

<xsl:template match="resource[@inout='OUT']">
	<writer>
		<xsl:copy-of select="@*[name()!='inout' and name()!='charSet']"/>
		<xsl:if test="@charSet">
			<xsl:attribute name="charset">
				<xsl:value-of select="@charSet"/>
			</xsl:attribute>
		</xsl:if>
	</writer>
</xsl:template>

<xsl:template match="resource[@inout='INOUT']">
	<updater>
		<xsl:copy-of select="@*[name()!='inout' and name()!='charSet']"/>
		<xsl:if test="@charSet">
			<xsl:attribute name="charset">
				<xsl:value-of select="@charSet"/>
			</xsl:attribute>
		</xsl:if>
	</updater>
</xsl:template>

<xsl:template match="resource[@type='DB']">
	<reader id="{@id}" type="DB" url="{@ref-datasource}"/>
	<writer id="{@id}" type="DB" url="{@ref-datasource}"/>
</xsl:template>

<xsl:template match="resource">
	<reader id="{@id}" type="DB" url="{@ref-datasource}"/>
	<writer id="{@id}" type="DB" url="{@ref-datasource}"/>
</xsl:template>

</xsl:stylesheet>