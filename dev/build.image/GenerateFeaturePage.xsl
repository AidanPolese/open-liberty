<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<!-- This stylesheet will take a server.xml and a feature list and for each
	     feature defined in the server.xml will write out an <entry/> element to
	     the xml doc that is used to do bulk uploads to was-dev -->
	<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes"/>
	<xsl:param name="feature.name"/>
	<xsl:param name="wlp.version"/>
	<xsl:param name="feature.list.location"/>
	
	<!-- Parse the feature list and store the doc to pass to templates to get info
	     for each feature -->
	<xsl:param name="feature.list.document" select="document($feature.list.location)"/>
	<xsl:template match="@*|node()">
		<!-- Match all nodes so that they don't print anything - our other 
		     templates will match the nodes we actually want to print -->
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
	
	<xsl:template match="/">
		<!-- Wrap everything in an entries tag -->
		<entries><xsl:text>
</xsl:text>
			<xsl:apply-templates/>
		</entries>
	</xsl:template>
	
	<xsl:template match="/server/featureManager/feature">
		<!-- This template matches all of the features we want to have an entry 
		     for, we just need to grab this feature out of the feature list and
		     let another template generate the XML. -->
		<xsl:apply-templates select="$feature.list.document/featureInfo/feature[@name=current()]">
			<xsl:with-param name="allFeatures" select="/server/featureManager"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="/featureInfo/feature">
		<xsl:param name="allFeatures"/>
		<!-- This template will write the entry XML that we want. -->
<xsl:text>	</xsl:text><entry><xsl:text>
		</xsl:text><post_title><xsl:value-of select="displayName"/></post_title><xsl:text>
		
		<!-- Wrap the excerpt and content in CDATA tags -->
		</xsl:text><post_excerpt><xsl:text disable-output-escaping="yes">&lt;![CDATA[
</xsl:text><xsl:value-of select="description"/>
		<xsl:text disable-output-escaping="yes">
		]]&gt;</xsl:text></post_excerpt><xsl:text>
		</xsl:text><post_content><xsl:text disable-output-escaping="yes">&lt;![CDATA[
</xsl:text>
<h2 id="ibm-wasdev-feature-desc-title">Description</h2><xsl:text>
</xsl:text>
<div id="ibm-wasdev-feature-desc-content"><xsl:text>
	</xsl:text>
	
	<xsl:value-of select="description"/>
	
	Enables: <xsl:choose>
	 			<xsl:when test="enables">
					<xsl:for-each select="enables">
						<xsl:if test="position() > 1">
							<xsl:text>, </xsl:text>
						</xsl:if>
		 				<xsl:choose>
							<xsl:when test="$allFeatures/feature=current()">
								<a>
									<xsl:attribute name="href">https://www.ibmdw.net/wasdev/repo/feature_<xsl:value-of select="translate(.,'.','-')"/></xsl:attribute>
									<xsl:value-of select="."/>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="."/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>&lt;none&gt;</xsl:otherwise>
			</xsl:choose>
	Enabled by: <xsl:choose>
				<xsl:when test="/featureInfo/feature[enables=current()/@name]">
					<xsl:for-each select="/featureInfo/feature[enables=current()/@name]">
						<xsl:if test="position() > 1">
							<xsl:text>, </xsl:text>
						</xsl:if>
		 				<xsl:choose>
							<xsl:when test="$allFeatures/feature=current()/@name">
								<a>
									<xsl:attribute name="href">https://www.ibmdw.net/wasdev/repo/feature_<xsl:value-of select="translate(@name,'.','-')"/></xsl:attribute>
									<xsl:value-of select="@name"/>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name"/>
							</xsl:otherwise>
						</xsl:choose>

					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>&lt;none&gt;</xsl:otherwise>
			</xsl:choose>
<xsl:text>
</xsl:text>	
</div><xsl:text>
</xsl:text>
<h2 id="ibm-wasdev-feature-instructions-title">Command Line Install</h2><xsl:text>
</xsl:text>
<div id="ibm-wasdev-feature-instructions-content"><xsl:text>
</xsl:text>
	
	To install the feature from the command line type:

	<code>bin/featureManager install https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/<xsl:value-of select="$wlp.version"/>/<xsl:value-of select="symbolicName"/>.esa</code><xsl:text>
</xsl:text>
	
</div><xsl:text>
</xsl:text>
<h2 id="ibm-wasdev-feature-notes-title">Config Instructions</h2><xsl:text>
</xsl:text>
<div id="ibm-wasdev-feature-notes-content"><xsl:text>
</xsl:text>To use the feature at runtime add the following to your server.xml
[code]<featureManager><xsl:text>
  </xsl:text><feature><xsl:value-of select="@name"/></feature><xsl:text>
</xsl:text>
</featureManager>[/code]
	
</div>
		<xsl:text disable-output-escaping="yes">
		]]&gt;</xsl:text></post_content><xsl:text>
		</xsl:text><post_name>feature_<xsl:value-of select="translate(@name,'.','-')"/></post_name><xsl:text>
		</xsl:text><post_type>asset</post_type><xsl:text>
		</xsl:text><custom_fields><xsl:text>
			</xsl:text><asset_download_url>https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/<xsl:value-of select="$wlp.version"/>/<xsl:value-of select="symbolicName"/>.esa</asset_download_url><xsl:text>
		</xsl:text></custom_fields><xsl:text>
		</xsl:text><categories><xsl:text>
			</xsl:text><category>Features</category><xsl:text>
		</xsl:text></categories><xsl:text>
		</xsl:text><post_status>draft</post_status><xsl:text>
	</xsl:text></entry><xsl:text>
</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
