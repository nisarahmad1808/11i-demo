<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
<HTML>
	<HEAD>
	<style>
	@page {
  						margin: 10mm;  
  					}
  					table {
        				font-size: 11px;  
        				width: 100%;
    				} 
    				div {
    					width: 100%;
    				}
  					body {
  						font-family: sans-serif !important; 
  						font-size: 11px; 
  						color: #000000;
  					} 
  					.header{
  						border-collapse: collapse; 
  						border: 1px solid #959595;
  						background-color: #dddddd !important; 
  					}
  					.header th{
  						padding: 5px 0px 5px 2px;
  						border-right: 1px solid #a6a6a6;
  						font-weight: bold;
    					font-size: 10px;
    					text-align: left;
  					} 
  					.drugDataTable {
  						border-collapse: collapse; 
  						border-bottom: 1px solid #a6a6a6;
  						border-right: 1px solid #a6a6a6;
  						border-left: 1px solid #a6a6a6; 
  						width: 100%;
  						height: 100%;
                    }
                    .drugDataTable tr {
                    	page-break-inside: avoid;
                    }
                    .drugDataTable td {
                    	border-collapse: collapse; 
                    	border-bottom: 1px solid #a6a6a6;
  						border-right: 1px solid #a6a6a6;
  						border-left: 1px solid #a6a6a6;  
  					}
                    .drugDataTable td:not(:first-child) {
                    	padding-left: 2px;
                    }
	</style>
	</HEAD><xsl:if test="//return/patientInfo">
		<h2 align="center">Medication Profile for <xsl:value-of select="//return/patientInfo/patientname" /> DOB: <xsl:value-of select="//return/patientInfo/dob" /> Gender: <xsl:value-of select="//return/patientInfo/gender" /> </h2>
	</xsl:if>
	 <xsl:for-each select="//return/Orders">
      <xsl:choose>
        <xsl:when test="medUnverified"><h3>Medication &#x2192; Unverified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/medUnverified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
        <xsl:when test="ivUnverified"><h3>IV &#x2192; Unverified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/ivUnverified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
        <xsl:when test="complexUnverified"><h3>Complex Order &#x2192; Unverified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/complexUnverified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
        <xsl:when test="medVerified"><h3>Medication &#x2192; Verified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/medVerified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
        <xsl:when test="ivVerified"><h3>IV &#x2192; Verified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/ivVerified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
        <xsl:when test="complexVerified"><h3>Complex Order &#x2192; Verified</h3>
        <div id="PharmacyReport" style="display:inline-table">
                    <table class="header">
                        <thead>
                            <tr >
                            	<th width="6%" >Priority</th>
                               	<th width="8%">Pharmacy Status</th>
								<th width="6%" >Tag</th>
								<th width="9%">OrderType</th>
								<th width="14%">Order</th>
								<th width="25%">Details</th>
								<th width="12%">Start DateTime</th>
								<th width="12%">End DateTime</th>
								<th width="10%">Ordering Provider</th>
								
                            </tr>
                        </thead>
                    </table>
                           <xsl:for-each select="//return/Orders/complexVerified/Order">
                           <tr>
                            <table class="drugDataTable">
                            <tbody>
                           	<tr>
                                <td width="6%" ><xsl:value-of select="./priority" /></td>
                                <td width="8%"><xsl:value-of select="./pharmacyOrderStatus" /></td>
                                <td width="6%"><xsl:value-of select="./tag" /></td>
                                <td width="9%"><xsl:value-of select="./pharmacyOrderType" /></td>
                                <td width="14%"><xsl:value-of select="./order" /></td>
                                <td width="25%"><xsl:value-of select="./details" /></td>
                                <td width="12%"><xsl:value-of select="./startDateTime" /></td>
                                <td width="12%"><xsl:value-of select="./endDateTime" /></td>
                                <td width="10%"><xsl:value-of select="./orderingProvider" /></td>
                            </tr>
                            </tbody>
                            </table>
                            </tr>
                            </xsl:for-each>
            </div>
        </xsl:when>
	<xsl:otherwise></xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
</HTML>
	</xsl:template>
</xsl:stylesheet>