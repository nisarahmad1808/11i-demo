<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>

<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/jsp/inpatientWeb/staticContent/pharmacy/interfaces/css/dispensingInterfaceLog.css">
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/jsp/inpatientWeb/staticContent/pharmacy/interfaces/css/dispensingInterfaceLogModal.css">

<div class="main-cont" ng-controller="dispensingInterfaceController  as medDispenseCtrl"

	ng-init="medDispenseCtrl.initializeEvents()">
      <div class="blue-nav blue-pad">
        <div class="dropdown referal-drop pull-left">
          <a href="#">
            Medication Dispense Logs
            <b class="caret"></b>
          </a>
        </div>
        <div class="clearfix"></div>
      </div>
      <div class="clearfix"></div>
      <div class="tab-content" id="medication-dispense-logs-module">
        <div class="collapse in clearfix" id="filter">
          <form class="form-horizontal" role="form">
            <div class="pharmacy-filter det-view clearfix">
              <div class="col-lg-12 nopadding clearfix">
                <div class="form-group nomargin">
                  <div class="col-md-3 nopadright">
                    <label class="control-label col-md-3 nopadleft nopadtop mt3">Status</label>
                    <div class="col-md-9 nopadding">
                    <div class="select-box">
						<b class="caret selection-click"></b> <span
							class="selection-field selection-click"
							ng-bind="medDispenseCtrl.selectedStatus"></span>
						<ul
							class="sel-optn selection-options dropdown-menu-custom">
							<li ng-click="medDispenseCtrl.selectedStatus = 'Select'">Select</li>
							<li ng-click="medDispenseCtrl.selectedStatus = 'Pending'">Pending</li>
							<li ng-click="medDispenseCtrl.selectedStatus = 'Failed'">Failed</li>
							<li ng-click="medDispenseCtrl.selectedStatus = 'Success'">Success</li>
						</ul>
					</div>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <label class="control-label nopadtop mt3 col-md-2">Provider</label>
                    <div class="col-md-7 nopadding">
						<provider
                             provider-name="medDispenseCtrl.providerForFilter"
                             topclass="providerForFilter" filter-Position="right"
                             all-Option="true" max-Count="5"
                             multi-Selection="false" ms-input-height="26px">
                         </provider>
                    </div>
                  </div>
                  <div class="col-md-3 nopadleft">
                    <label class="control-label col-md-4">
                      <div class="select-box dropdown pull-right nobg noborder">
                        <span class="selection-field selection-click mr5">Facility</span>
                      </div>
                    </label>
                    <div class="col-md-8 nopadding">
                       <inline-facility-lookup multi-select="false" selected-facility="medDispenseCtrl.selectedFacility" on-select="medDispenseCtrl.onSelectFacility(medDispenseCtrl.selectedFacility)"></inline-facility-lookup>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-lg-12 mt10 nopadding clearfix">
                <div class="form-group nomargin">
                  <div class="col-md-3 nopadright">
                    <label class="control-label col-md-3 nopadleft nopadtop mt3 nowrap">Patient Name</label>
                    <div class="col-md-9 nopadding">
                   		 <div patientlookupinline patient-name="medDispenseCtrl.filterPatient" topclass="filterPatient" patient-click="medDispenseCtrl.selectPatient()"  clear-patient-data="medDispenseCtrl.clearPatient()" ></div>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <label class="control-label nopadtop mt3 col-md-2">Date</label>
                    
                    <div class="col-md-7 nopadding">
                     <div class="">
                          <div class="input-group input-group-sm">
                              <input date-range-picker type="text" class="form-control nopadright" ng-model="medDispenseCtrl.dateRange" options="medDispenseCtrl.rangeDateOpt" ui-mask="?1?9/?3?9/?9?9?9?9 to ?1?9/?3?9/?9?9?9?9" ui-options="{dateFormat: rangeDateOpt.locale.format}">
                              <span class="input-group-btn">
                          <button class="btn btn-default" type="button">
                              <span class="icon icon-inputcalender"></span>
                              </button>
                              </span>
                          </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-5 col-md-3 nopadding">
					<div class="filterrightmar nomartop relative clearfix padr20">
						<div class="pull-right relative mr20">
							<div class="pull-right nopadleft mr5 mt3">
								<button class="btn btn-blue btn-xs pull-left lnheight18 mr5"
									type="button" ng-click="medDispenseCtrl.getMedicationDispenseLogReport('onFilterClick')">
									<i class="icon icon-wfilter"></i> Filter
								</button>
							</div>
						</div>
					</div>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="bdr-btmnone darkblue-nav green-nav mb-1">
          <a href="#" class="icon icon-greenfilter location-filter" data-target="#filter" data-toggle="collapse">filter</a>
          <div class="col-sm-12 nopadding">
            <div class="simpleTable device-simpleTable white-bg inter-table">
				<div class="tablehead blackheader main-dualheaderdiv">
				   <table class="table table-bordered">
						<thead>
		                  <tr>
		                    <th class="w8p">
		                      <div class="tupper relative">Med Order Type
		                      </div>
		                    </th>
		                    <th class="w5p">
		                      <div class="tupper relative">Status
		                      </div>
		                    </th>
		                    <th class="w8p">
		                      <div class="tupper relative">Provider
		                      </div>
		                    </th>
		                    <th class="w10p">
		                      <div class="tupper relative">Patient Name
		                      </div>
		                    </th>
		                    <th class="w17p">
		                      <div class="tupper relative">Patient Location
		                      </div>
		                    </th>
		                    <th class="w19p">
		                      <div class="tupper relative">Drug Description
		                      </div>
		                    </th>
		                    <th class="w10p">
		                      <div class="tupper relative">Approved by
		                      </div>
		                    </th>
		                    <th class="w10p">
		                      <div class="tupper relative">Approved Date
		                      </div>
		                    </th>
		                    <th class="w14p">
		                      <div class="tupper relative">Facility
		                      </div>
		                    </th>
		                  </tr>
	                	</thead>
					</table>
				</div>
				<div class="thgtscroll interventions-tbl-hgt loclistview-tblhgt relative">
					<table class="table table-bordered dualheader-table2"  id="interventionTable">
						<tbody>
		                  	<tr ng-if="medDispenseCtrl.noRecordFound">
								<td class="text-center" colspan="9" title="{{medDispenseCtrl.meOrderType}}">No records found</td>
							</tr>
		                    <tr ng-repeat="item in medDispenseCtrl.medDispenseLogList track by $index" ng-class="{'brdbtm' : $last, 'highlight-row' : item.selected}" ng-class-even="'even'"
		                      ng-class-odd="'odd'">
		                      <td class="w8p">
		                        <div class="tdupper capitalize" ng-bind="item.meOrderType" title="{{item.meOrderType}}"></div>
		                      </td>
		                      <td class="w5p" ng-class="{'success':item.status == 'success', 'failed':item.status == 'failed' }">
		                        <div class="tdupper capitalize" title="{{item.status}}" ng-bind="item.status" ></div>
		                      </td>
		                      <td class="w8p">
		                       <div class="tdupper capitalize" title="{{item.provider}}" ng-bind="item.provider"></div>
		                      </td>
		                      <td class="w10p">
		                        <div class="tdupper capitalize" title="{{item.patientName}}" ng-bind="item.patientName"></div>
		                      </td>
		                      <td class="w17p">
		                        <div class="tdupper capitalize" title="{{item.location}}" ng-bind="item.location"></div>
		                      </td>
		                      <td class="w19p">
		                        <div class="tdupper capitalize" title="{{item.drugDesc}}" ng-bind="item.drugDesc"></div>
		                      </td>
		                      <td class="w10p">
		                        <div class="tdupper capitalize" title="{{item.approvedBy}}" ng-bind="item.approvedBy"></div>
		                      </td>
		                      <td class="w10p">
		                        <div class="tdupper capitalize" title="{{item.approvedDate}}" ng-bind="item.approvedDate"></div>
		                      </td>
		                      <td class="w14p">
		                        <div class="tdupper capitalize"> 
		                          <span class="facilityellipse pull-left" title="{{item.facility}}" ng-bind="item.facility"></span>
		                          <i class="icon icon-grey-info pull-right mt3" title="Click here to see more details" ng-click="medDispenseCtrl.openDetails(medDispenseCtrl.medDispenseLogList,$index);"></i>
		                        </div>
		                      </td>
		                    </tr>
		                </tbody>
					</table>
				</div>
			</div>
          </div>
        </div>
        <div class="clearfix"></div>
        <div class="foot-control mt14 brdrtop det-view clearfix">
          <div class="pull-left custy-pagey">
            <div class="pull-right">
				<div-pagination-control total-items="medDispenseCtrl.totalRecords"
					current-page="medDispenseCtrl.selectedPage"
					items-per-page="medDispenseCtrl.recordsPerPage"
					on-page-change="medDispenseCtrl.getMedicationDispenseData();"
					class="pagination pagination-sm pad0 nomargin">
				</div-pagination-control>
			</div>
      </div>
</div>
