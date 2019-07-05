<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
  						padding: 10px 0px 10px 3px;
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
                    	padding-left: 3px;
                    }
			</style>
			</HEAD>
			<div id="interventionReport" style="display:inline-table" >
										<table class="header"  id="interventionsTable">
											<thead>
												<tr>
													<th width="8%" >Patient Name</th>
													<th width="5%" >Gender</th>
													<th width="5%" >Age</th>
													<th width="6%" >Category</th>
													<th width="15%" >Interventions</th>
													<th width="6%" >Duration</th>
													<th width="9%" >Status</th>
													<th width="7%" >Saving Value</th>
													<th width="7%" >Time Spend Value</th>
													<th width="11%" >Entered By</th>
													<th width="11%" >Entered Date</th>
													<th width="11%" >Assigned To</th>
												</tr>
											</thead>
										</table>
												<xsl:for-each select="//return/interventions/intervention">
													<tr >
													   <table class="drugDataTable">
                           								 <tbody>
                           								<tr>
														<td width="8%" ><xsl:value-of select="./patientName" /></td>
														<td width="5%" ><xsl:value-of select="./gender" /></td>
														<td width="5%" ><xsl:value-of select="./age" /></td>
														<td width="6%" ><xsl:value-of select="./category" /></td>
														<td width="15%" ><xsl:value-of select="./reason" /></td>
														<td width="6%" ><xsl:value-of select="./duration" /></td>
														<td width="9%" ><xsl:value-of select="./status" /></td>
														<td width="7%" ><xsl:value-of select="./savingValue" /></td>
														<td width="7%" ><xsl:value-of select="./timeSpendValue" /></td>
														<td width="11%" ><xsl:value-of select="./enteredBy" /></td>
														<td width="11%" ><xsl:value-of select="./enteredDate" /></td>
														<td width="11%" ><xsl:value-of select="./assignedTo" /></td>
														</tr></tbody></table>
													</tr>
												</xsl:for-each>
			</div>
		</HTML>
	</xsl:template>
</xsl:stylesheet>