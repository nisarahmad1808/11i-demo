<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="//return//monographFields">
	
		<HTML>
			<HEAD>
				<meta charset="UTF-8" />
				<style type = "text/css">
					.monographBody #nam {
						display: inline-block;
						color: #664000 !important; 
						font-size: 1.6em;
						font-weight: bold;
						margin-bottom: 0.75em;
						padding-top: 0.5ex;
					}
					.monographBody .monographFieldName {
						font-size: 14pt;
						font-weight: bold;
						float: left;
						color: #036;
						padding-right: 0.5em;
                        display: inline-block;
					}
					.monographBody .monographField {
    					margin-bottom: 0.75em;
                        display: block;
					}
					.monographBody .list {
                    	margin-top: 0.75em !important;
    					margin-bottom: 0.75em;
                    }
					#exp {
                        margin-left: 2em;  
						display: inline-block;      
					}
                    .monographBody p, .project-10i .monographBody #fbnlist, .project-10i .monographBody .reference {
                    	font-weight: normal !important;
                    }
                    #dot p:first-child, #cross-reference p, #cpg p:first-child, #pai p:first-child, #war p:first-child, #pri p:first-child, #adr p:first-child, #prrlist, #dri p:first-child, #mop p:first-child, 
                    #rer p:first-child, #fee p:first-child, #phk p:first-child, #phksp p:first-child, #rftlist, #rfs div:first-child, #atclist, #goilist-ext, .collapsible:first-child, #briggs-list, #calclist, 
                    #add-preg-lac, #toxlist, #dorp p:first-child, #dora p:first-child, #dop p:first-child {
                    	padding-top: 2em;
                    }
                    #dot p, #cross-reference p, #cpg p, #pai p, #adr p, #dri p, #war p, #rer p:not(:first-child), #phk p, #phksp p, #mst .collapsible-wrap p, #doha p:first-child, #sts p:first-child,
                    #coi p:first-child, #doa p:first-child, #dop p:first-child, #use p:first-child, #str p:first-child {
                    	text-indent: 0em !important;
                    }
                    #rer p, .reference {
                    	margin-left: 2em;
                    }
                    #dora p:last-child, #dot p:last-child, #war p:not(:first-child), #adr p:not(:first-child), #dri p:not(:first-child), #mop p:not(:first-child), #rer p:not(:first-child), #foc p:not(:first-child),
                    #fee p:not(:first-child), #phk p:not(:first-child), #phksp p:not(:first-child), #rfs div:not(:first-child),#exp {
                    	padding-top: 0.75em;
                    }
                    .collapsible-wrap p:not(:first-child), #dic p:not(:first-child), .collapsible:not(:first-child){
                    	margin-top: 0.5em !important;
                    }
                    #dop p:nth-child(2){
                    	margin-top: 0.5em !important;
                    }
                    #mop p:last-child {
                    	margin-top: 0em !important;
                    }
                    #ubnlist ul, #thclist ul, #fbnlist ul, #cbnlist ul, #synlist ul {
                     	list-style-type: none;
                     	display: inline;
                   	}
					.monographBody {
					    background: white;
					    font-family: arial, sans-serif;
					    font-size: 10pt;
					    padding-left: 5mm;
					}
					.monographBody .statement {
						margin-top: 5px;
					}
					.collapsible-title, #pai p:not(:last-child) {
    					font-size: 10pt;
    					font-weight: bold;
    					margin-left: 2em;         
					}
					.monographBody p {
						margin-top: 0.75em !important;
					}
					.monographBody .collapsible-wrap p {
						margin-left: 4em !important;
					}
					.monographBody .collapsible-wrap li {
						margin-left: 2em !important;
					}
					.monographBody .collapsible-wrap li p, #doe p:first-child {
						margin-left: 0em !important;
					}
					#thclist li, #cbnlist li, #ubnlist li, #fbnlist li, #synlist li {
						display: inline;
					}
					#thclist li +li:before, #cbnlist li +li:before, #ubnlist li +li:before, #fbnlist li +li:before, #synlist li +li:before{
						content: "; ";
					}
					#thclist li:first-child, #cbnlist li:first-child, #ubnlist li:first-child, #fbnlist li:first-child, #synlist li:first-child {
						margin-left: -2em;
					}
				</style>
			</HEAD>
			<Body>
				<div class="monographBody">
					<xsl:for-each select="field">
						<div class="monographField">
							<xsl:if test= "fieldName[text() != 'Name']">
								<div class="monographFieldName">
									<xsl:value-of select="fieldName[text() != 'Name']" />
								</div>
								<xsl:value-of select="content" disable-output-escaping="yes" /> 	
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</Body>
		</HTML>
	</xsl:template>
</xsl:stylesheet>