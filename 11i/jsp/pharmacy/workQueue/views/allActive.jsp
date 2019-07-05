<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="tab-content" ng-controller="pharmacyAllActiveCtrl as vm" ng-init="vm.init();" id="ph-allActive-module">
	<div class="collapse in" id="ph-allActive-filter">
		<div class="fileter-wrap det-view padTB green-wrap pt10">
			<div class="col-sm-12 clearfix">
				<div class="col-sm-3 nopadleft">
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3">Priority </label>
						<div class="col-sm-8 nopadding">
							<div class="select-box">
								<b class="caret selection-click"></b>
								<span class="selection-field selection-click clear-txt" id="spnPriority">{{vm.priorityName}}</span>
								<ul class="sel-optn selection-options" id="ulPriority">
									<li ng-click="vm.setOrderPriority()">All</li>
									<li ng-repeat="orderPriority in vm.orderPriorityList" ng-click="vm.setOrderPriority(orderPriority)" ng-selected="orderPriority.id == vm.filterOptions.selectedPriority">{{orderPriority.priorityName}}</li>
								</ul>
							</div>
						</div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3">Facility <span class="red-txt">*</span></label>
						<div class="col-sm-8 nopadding">
							<inline-facility-lookup multi-select="false" selected-facility="vm.selectedFacility" on-select="vm.onSelectFacility(vm.selectedFacility)"></inline-facility-lookup>
						</div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3">Patient</label>
						<div class="col-sm-8 nopadding">							
							<div patientlookupinline patient-name="vm.filterPatient" topclass="filterPatient" patient-click="vm.selectPatient()"  clear-patient-data="vm.clearPatient()" ></div>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadright">
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3 nopadleft">Assigned To</label>
						<div class="col-sm-8 nopadding" id="divAssignedTo">
							<eCW:staffLookup staffName="vm.staffNameFilter"
								staffClick="vm.setAssignedTo()" filterPosition="right"
								placeHolder="Staff Name" topclass="staffLookup"
								clearStaffData="vm.clearAssignedTo()">
							</eCW:staffLookup>
						</div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3">Department</label>
						<div class="col-sm-8 nopadding"><fieldset ng-disabled="vm.workFlowType==2 || vm.workFlowType==0">
							<inline-department-lookup  facility-id="vm.selectedFacility.facilityId" on-select="vm.setResetDept()" selected-department="vm.selectedDept" show-filter="false"></inline-department-lookup>
						</fieldset></div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3 nopadleft"></label>
						<div class="col-sm-8 nopadding">
							<div class="doc-att">
								<label class="checkbox-inline control-label">
									<input type="checkbox" ng-model="vm.filterOptions.sortByPatient" ng-true-value="1" ng-false-value="0">Group by Patients
								</label>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadding">
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-4 nopadleft">Pharmacy Status</label>
						<div class="col-sm-7 nopadding">
							<select id="order-status" multiple="multiple" ng-model="vm.filterOptions.pharmacyStatuses">
								<option ng-repeat="pharmacyStatus in vm.pharmacyStatusList" ng-if="pharmacyStatus.status != 'Verified'"  value="{{pharmacyStatus.id}}">{{pharmacyStatus.status}}</option>
							</select>
						</div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-4 nopadding">Unit </label>
						<div class="col-sm-7 nopadding"><fieldset ng-disabled="vm.workFlowType==2 || vm.workFlowType==0">
							<inline-unit-lookup facility-id="vm.selectedDept.facilityId" department-id="vm.selectedDept.departmentId" on-select="vm.setResetUnit()" selected-unit="vm.selectedUnit" disabled="false"  show-filter="false"></inline-unit-lookup>
						</fieldset></div>
					</div>
					
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-4"></label>
						<div class="col-sm-12 nopadding">
							<div class="doc-att">
								<label class="checkbox-inline control-label">
									<input type="checkbox" ng-true-value="1" ng-false-value="0" ng-model="vm.filterOptions.isUnassigned">Unassigned Patient
								</label>
								<label class="radio-inline">
									<input type="radio" name="status" ng-model="vm.workFlowType" id="allval" value="0" ng-click="vm.setOrderTypeRecord('0');">All</label>
								<label class="radio-inline">
									<input type="radio" name="status" ng-model="vm.workFlowType" id="inpatient" value="1" ng-click="vm.setOrderTypeRecord('1');">Inpatient</label>
								<label class="radio-inline">
									<input type="radio" name="status" ng-model="vm.workFlowType" id="outpatient" value="2" ng-click="vm.setOrderTypeRecord('2');">Outpatient</label>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadleft">
					<div class="form-group clearfix">
						<label class="control-label nopadtop nopadleft mt3 col-lg-4 col-md-4">Order Start Date Range <span class="red-txt">*</span></label>
						<div class="col-lg-8 col-md-8 nopadding">
							<div class="date-rangewidth">
                                <div class="input-group input-group-sm">
                                    <input date-range-picker type="text" class="form-control nopadright" ng-model="vm.dateRange" options="vm.rangeDateOpt" ui-mask="?1?9/?3?9/?9?9?9?9 to ?1?9/?3?9/?9?9?9?9" ui-options="{dateFormat: rangeDateOpt.locale.format}">
                                    <span class="input-group-btn">
                                <button class="btn btn-default" type="button">
                                    <span class="icon icon-inputcalender"></span>
                                    </button>
                                    </span>
                                </div>
                            </div>
                            <label class="control-label nopadtop nopadleft mt3 col-lg-4 col-md-4"><span class="red-txt">*</span> Required Fields</label>
						</div>						
					</div>
					<div class="form-group clearfix">
						<div class="col-lg-12 col-md-12 nopadding">
							<div class="col-sm-12 col-lg-12 nopadding">
								<div class="pull-right relative">
									<ipfiltertemplate
										allconfigfilterdetails="vm.jsonFilterDetails"
										screen-name="PharmacyWorkQueue"  
										call-after-filter-details-fetch="vm.fetchFilterDetails()"
										selected-filter-details="vm.populateConfigFilterValues()">
									</ipfiltertemplate>
									<div class="pull-right nopadleft mr5 mt3">
										<button class="btn btn-blue btn-xs pull-left lnheight18 mr5" type="button" ng-click="vm.filterWorkQueueData();">
											<i class="icon icon-wfilter"></i> Filter
										</button>
										<button class="btn btn-blue btn-xs pull-left lnheight18 mr5" type="button" ng-click="vm.clearFilter();">Clear</button>
									</div>
								</div>
							</div>
						</div>
					</div>
                    <div class="clearfix"></div>
				</div>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="darkblue-nav green-nav mb-1"> <a href="#" class="icon icon-greenfilter" data-toggle="collapse" data-target="#ph-allActive-filter">filter</a>
		<div class="darkpad">
			<div class="relative pull-left mr10"></div>
			<div class="pull-right select-filter-text mr10" ng-if="vm.lastRefreshTime != ''" style="font-weight: bold">
				Last Refresh Time: {{vm.lastRefreshTime}}
			</div>
			<div class="pull-left select-filter-text">
				<button type="button" class="btn new-btn drkpad-btns pull-left ml10 mr5" ng-if="vm.hasUpdateMedOrderAccess == true" ng-click="vm.showAssignPopup($event);">Assign</button>
				<div class="relative pull-left" >
					<div class="btn-group custMnu" ng-if="vm.hasUpdateMedOrderAccess == true">
						<button type="button" class="btn new-btn edit-note assign-provi drkpad-btns" data-toggle="dropdown">Change Status</button>
						<button type="button" class="btn new-btn dropdown-toggle new-caret drkpad-btns" data-toggle="dropdown" id="thisunique">
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						</button>
						<ul class="dropdown-menu dateselect mt-1 listproduct default-drop dropdown-width141">
							<li ng-repeat="row in vm.pharmacyStatusList" ng-if="row.status == 'Pending'"><a href="#" class="printOrCensus" ng-click="vm.updateStatus(row)">{{row.status}}</a></li>
						</ul>
					</div>
				</div>
				<div class="mt7 pull-left select-filter-text-all-active-list" ng-if="vm.isFilterApplied">
					<button type="button" class="close fnt16 pull-left ml10" ng-click="vm.closeFilter()">x</button>
					<span class="fnt11 ml10">Filter Applied :</span>
					<span class="fnt11 fnbld getfiltername">{{vm.jsonFilterDetails.selectedFilter.filterName}}</span>
				</div>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="clearfix"></div>
	<div class="main-dualheaderdiv">
		<div class="main-dualheaderdiv white-bg all-active-tab-table">
			<table class="dualheader-table1 table table-bordered nomargin">
				<thead>
					<tr>
						<th class="w30">
							<!-- <div class="select-allcheck checkbox-class text-center pt10">
								<input type="checkbox" class="checkbox-style" ng-click="checkAll()" ng-model="isAllSelected" />
							</div> -->
						</th>
						<th class="sortable w16p">
							<div class="upper relative left" ng-click="sortType = 'patientName'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Patient Name
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'patientName' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'patientName' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="upper right">MRN</div>
							<div class="lower relative left" ng-click="sortType = 'patientGender'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Gender
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'patientGender' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'patientGender' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="lower right">Age</div>
						</th>
						<th class="sortable w28p">
							<div class="upper left">Order Details </div>
							<div class="upper right">Order Status</div>
							<div class="lower left" ng-if="vm.selectedTab == 'All' || vm.selectedTab == 'Verified'">Phar-Status</div>
							<div class="lower left" ng-if="vm.selectedTab == 'Pending'">Reason</div>
							<div class="lower right">Order Set</div>
						</th>
						<th class="sortable w16p">
							<div class="upper">Chief Complaint</div>
							<div class="lower">Admission Dx</div>
						</th>
						<th class="sortable w10p">
							<div class="upper relative" ng-click="sortType = 'orderingProviderName'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Ordering Provider
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'orderingProviderName' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'orderingProviderName' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="lower relative" ng-click="sortType = 'orderDateTime'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Order Date &amp; Time
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'orderDateTime' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'orderDateTime' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
						</th>
						<th class="sortable w10p">
							<div class="upper relative" ng-click="sortType = 'nurseName'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Assigned Nurse
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'nurseName' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'nurseName' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="lower" ng-if="vm.selectedTab == 'Pending'"> Assigned To </div>
							<div class="lower" ng-if="vm.selectedTab != 'Pending'"></div>
						</th>
						<th class="sortable w6p">
							<div class="upper relative" ng-click="sortType = 'priorityName'; sortReverse = !sortReverse;vm.showIcon($event);vm.setSearchByValue(sortType, sortReverse);">Priority
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'priorityName' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'priorityName' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="lower"></div>
						</th>
						<th class="sortable w10p">
							<div class="upper">Department</div>
							<div class="lower">Area/Bed</div>
						</th>
						<th class="sortable w10p">
							<div class="upper">Facility</div>
							<div class="lower">Unit</div>
						</th>
						<!-- <th class="sortable w14p">
							<div class="upper relative">Message</div>
							<div class="lower relative left" ng-click="sortType = 'sentby'; sortReverse = !sortReverse;showIcon($event)">Sent by
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'sentby' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'sentby' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
							<div class="lower relative right" ng-click="sortType = 'senton'; sortReverse = !sortReverse;showIcon($event)">Sent on
								<div class="sort-posi">
									<i ng-class="{'asc-sort-hover':sortType === 'senton' && !sortReverse}" class="icon asc-sort"></i>
									<i ng-class="{'des-sort-hover':sortType === 'senton' && sortReverse}" class="icon des-sort"></i>
								</div>
							</div>
						</th>
						<th class="sortable w10p">
							<div class="upper">Alerts</div>
							<div class="lower"></div>
						</th> -->
					</tr>
				</thead>
			</table>
    		<div class="hgtscroll  relative ph-allActive-cust-scroll" perfect_scrollbar>
				<table class="dualheader-table2 table table-bordered nomarbot">
					<tbody>		
						<tr ng-if="vm.totalWorkQueueRecords == 0">
								<td class="text-center">No records found</td>
						</tr>										
						<tr ng-repeat="row in vm.phAllActiveData | orderBy:sortType:sortReverse" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}">
							<td class="checkbox-align w30">
								<div class="text-center">
									<input type="checkbox" class="mt0 checkbox-style" ng-model="row.selected" ng-change="vm.patientToggled()" />
								</div>
							</td>
							<td class="w16p">
								<div class="ord-hgt{{row.id}} tdupper left ellipse-style cursor-pointer" ng-attr-title="{{row.patientName}}"><a ng-click="vm.launchPharmacyTab(row.patientId, row.encounterId, row.encounterType, row.episodeEncounterId, row.departmentId, row.serviceType);" class="anchor {{row.disStatus}}">{{row.patientName}}</a>
									<!-- <span ng-if="row.underAgeIcon" class=" pull-right">
										<i class="icon icon-circle-info" ng-click="underAgeDropDown($event)"></i>
									</span> -->
								</div>
								<div class="ord-hgt{{row.id}} tdupper right {{row.disStatus}}">{{row.patientMRN}}</div>
								<div class="clearfix"></div>
								<div class="tdlower left {{row.disStatus}}">{{row.patientGender}}</div>
								<div class="tdlower right {{row.disStatus}}">{{row.patientAge}}</div>
							</td>
							<td class="w28p">
								<div class="flex-div">								
									<div id="{{row.id}}" class="tdupper ph-orderDetails-table cursor-pointer ellipse-style left tdupper-orderdetails ord-det{{row.id}} {{row.disStatus}}" ng-attr-title="{{row.orderDetail}}" outer-height options="vm.dirOptions"><span class="pull-left w85p">{{row.orderDetail}}</span>
										<span ng-if="row.ISSFlag===true" class="pull-right" on-outside-element-click="vm.closeBlock()"><i class="icon sliding-scale mt5" ng-click="vm.openSignPreview(row.issTemplateData.doseData, $event)">SS</i></span>
										<table ng-if="row.patientSuppliedFlag===true ||  row.substituteFlag===true || row.itemsObj.restricted===true || row.itemsObj.lookAlikeSoundAlike===true">
											<tr>
												<td class="pull-left" style="display:flex;">
													<span ng-if="row.patientSuppliedFlag===true" class="icon icon-home-medication2 mr10"></span>
													<span ng-if="row.substituteFlag===true" class="nosub mr10">NO SUB</span>
													<span ng-if="row.itemsObj.restricted===true" class="rdRound mr5">RD</span>
													<i ng-if="row.itemsObj.lookAlikeSoundAlike===true" class="icon icon-look-like mr2 mt2"></i>													
												</td>																								
											</tr>
										</table>
									</div>
									<div class="ord-hgt{{row.id}} tdupper ph-orderDetails-table cursor-pointer ellipse-style right {{row.disStatus}}"><span ng-if="row.orderStatusName==='Discontinued'"><font color="red" ng-bind="row.orderStatusName"></font></span><span ng-if="row.orderStatusName!=='Discontinued'"><font ng-bind="row.orderStatusName"></font></span></div>
									<div class="tdlower left ellipse-style {{row.disStatus}}" ng-if="vm.selectedTab == 'All' || vm.selectedTab == 'Verified'">{{row.pharmacyStatus}}</div>
									<div class="tdlower left ellipse-style {{row.disStatus}}" ng-if="vm.selectedTab == 'Pending'">{{row.pendingReason}}</div>
									<div class="tdlower right ellipse-style {{row.disStatus}}">{{row.orderSetName}}</div>
								</div>
							</td>
							<td class="w16p">
								<div class="ord-hgt{{row.id}} tdupper cursor-pointer ellipse-style {{row.disStatus}}" ng-attr-title="{{row.chiefComplaint}}">{{row.chiefComplaint}}</div>
								<div class="tdlower cursor-pointer ellipse-style {{row.disStatus}}" ng-attr-title="{{row.admittingDiagnosis}}">{{row.admittingDiagnosis}}</div>
							</td>
							<td class="w10p">
								<div class="ord-hgt{{row.id}} tdupper {{row.disStatus}}">{{row.orderingProviderName}}</div>
								<div class="tdlower {{row.disStatus}}">{{row.orderDateTime}}</div>
							</td>
							<td class="w10p">
								<div class="ord-hgt{{row.id}} tdupper {{row.disStatus}}">{{row.nurseName}}</div>
								<div class="tdlower {{row.disStatus}}" ng-if="vm.selectedTab == 'Pending'">{{row.assignedToName}}</div>
								<div class="tdlower {{row.disStatus}}" ng-if="vm.selectedTab != 'Pending'"></div>
							</td>
							<td class="w6p">
								<div class="ord-hgt{{row.id}} tdupper nopadding pt2 pl5">
									<i class="icon prio-red {{row.disStatus}}" ng-show="row.priorityName == 'STAT'">{{row.priorityName}}</i>
									<i class="icon prio-gray {{row.disStatus}}" prio-gray ng-show="row.priorityName != 'STAT' && row.priorityName != 'ASAP'">{{row.priorityName}}</i>
									<i class="icon prio-yellow {{row.disStatus}}" ng-show="row.priorityName == 'ASAP'">{{row.priorityName}}</i>
								</div>
								<div class="tdlower {{row.disStatus}}"></div>
							</td>
							<td class="w10p">
								<div class="ord-hgt{{row.id}} tdupper ellipse-style {{row.disStatus}}" ng-attr-title="{{row.departmentName}}">
									{{row.departmentName}}
								</div>
								<div class="tdlower ellipse-style {{row.disStatus}}">
									{{row.areaName}}<span ng-if="row.areaName != '' || row.bedName != '' "> / </span>{{row.bedName}}
								</div>
							</td>
							<td class="w10p">
								<div class="ord-hgt{{row.id}} tdupper ellipse-style {{row.disStatus}}" ng-attr-title="{{row.facilityName}}">
									{{row.facilityName}}
								</div>
								<div class="tdlower ellipse-style {{row.disStatus}}" ng-attr-title="{{row.unitName}}">
									{{row.unitName}}
								</div>
							</td>
							<!-- <td class="w14p">
								<div class="tdupper ellipse-style {{row.disStatus}}" ng-attr-title="{{row.message}}">{{row.message}}</div>
								<div class="clearfix"></div>
								<div class="tdlower left ellipse-style {{row.disStatus}}">{{row.sentby}}</div>
								<div class="tdlower right ellipse-style {{row.disStatus}}">{{row.senton}}</div>
							</td>
							<td class="w10p">
								<div class="tdupper nopadding pl5 {{row.disStatus}}">
									<span ng-repeat="alert in row.alert" class="mr10" ng-class="{special: $middle}">
										<i class="icon alert-gray" ng-mouseover="vm.alertPopupShow($event,alert.alertName)" ng-mouseleave="vm.alertPopupHide()">{{alert.alertName}}</i>
										<span class="alert-redcircle text-center alert-numberpos">{{alert.alertNum}}</span>
									</span>
								</div>
								<div class="clearfix"></div>
								<div class="tdlower"></div>
							</td> -->
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="clearfix"></div>
	<div class="foot-control mt14 brdrtop det-view clearfix" style="padding: 1px;">
		<div-pagination-control 
				total-items="vm.totalWorkQueueRecords" 
				current-page="vm.filterOptions.selectedPage" 
				items-per-page="vm.filterOptions.recordsPerPage" 
				on-page-change="vm.getWorkQueueData();" 
				class="pagination pagination-sm pad10 nomargin"> 
	</div-pagination-control>
	</div>
	
	<div class="assign-div absolute-dropdown" style="display:none;width: 325px;">
    	<div class="assign-arrow">
			<div class="col-lg-12 nopadding">
				<div class="col-lg-12 col-md-12 nopadright det-view">
					<div class="form-group clearfix">
						<label class="control-label mt3 col-sm-3">Assign to</label>
						<div class="col-sm-9 nopadleft">
							<eCW:staffLookup staffName="vm.staffName"
								staffClick="vm.getAssignedStaff()" filterPosition="right"
								placeHolder="Staff Name" topclass="staffLookup"
								clearStaffData="vm.clearStaff()">
							</eCW:staffLookup>
						</div>
					</div>
				</div>
				<div class="col-sm-12 brdrtop2">
					<div class="pull-right mt10 mb10">
						<button type="button" class="btn btn-blue btn-xs" ng-click="vm.showAssignPrompt()">OK</button>
						<button type="button" class="btn btn-lgrey btn-xs" ng-click="vm.closeAssignPopup()">Cancel</button>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	
	<div class="modal fade" id="addReasonModal" data-backdrop="false">
		<div class="modal-dialog" style="width:500px;">
			<div class="modal-content global-property">
				<div class="modal-header clearfix">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
					<h4 class="modal-title">Add Reason</h4>
				</div>
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row">
							<div class="col-sm-12">
								<div class="det-view">
									<div class="form-group nomargin clearfix">
										<label class="col-sm-2 control-label nopadding mt3">Reason</label>
										<div class="col-sm-10 nopadding">
											<div class="select-box">
												<b class="caret selection-click"></b>
												<span class="selection-field selection-click">Select</span>
												<ul class="sel-optn selection-options">
													<li ng-repeat="row in vm.pendingReasonList" ng-click="vm.setReason(row)">{{row.reasonName}}</li>
												</ul>
											</div>
		
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer grey-bg brdrtop2">
					<button type="button" class="btn btn-lgrey btn-xs pull-right ml5" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-blue btn-xs pull-right mr5 ceftriaxone-btn" ng-click="vm.updateReason();">OK</button>
				</div>
			</div>
		</div>
	</div>
	
	<div class="proceed-prompt collection-prompt" id="assignprompt" style="display:none;">
		<div class="col-sm-12 nopadding h100p">
			<div class="left-prompt col-sm-2">
				<i class="icon icon-alert-info"></i>
			</div>
			<div class="prompt-message text-center col-sm-10 nopadright">
				<p class=" msg-txt">Do you want to assign this order to {{vm.staffName.staff.name}}?
				</p>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center yes-collection" ng-click="vm.confirmAssign()">Yes</button>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center" ng-click="vm.hideAssignPrompt()">No</button>
			</div>
		</div>
	</div>
	<div class="config-preview">
		<div class="up-popuparrow">
			<div class="container-fluid">
				<div class="row">
					<div class="col-sm-12">
						<div class="simpleTable mb10 mt10">
							<div class="tablehead">
								<table class="table table-bordered">
									<thead>
										<tr>
											<th class="w10p">From</th>
											<th class="w10p">To</th>
											<th class="w18p">Dose Units</th>
											<th>Notes</th>
										</tr>
									</thead>
								</table>
							</div>
							<div class="tablebody tempDetailshgt perfect-scrollBar">
								<table class="table table-bordered">
									<tbody>
										<tr ng-repeat="row in vm.issTempalteData track by $index">
											<td class="w10p" ng-bind="row.levelFrom"></td>
											<td class="w10p" ng-bind="row.levelTo"></td>
											<td class="w18p" ng-bind="row.doseUnit"></td>
											<td ng-bind="row.notes"></td>
										</tr>													
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>	
</div>