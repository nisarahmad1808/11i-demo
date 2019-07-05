<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>
<div class="main-wrap" ng-app="phamacyinterventionsModule">
				<div class="main-cont">
						<div class="blue-nav blue-pad">
							<div class="dropdown referal-drop pull-left">
								<a href="#">
									   Interventions
									   <b class="caret"></b>
								   </a>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="clearfix"></div>
		<div class="tab-content" id="interventions-module"  ng-controller="pharmacyinterventionsCtrl as vm" ng-init="vm.initialize()">
							<div class="collapse in clearfix" id="filter">
								<form class="form-horizontal" role="form">
									<div class="pharmacy-filter det-view clearfix">
										<div class="col-lg-12 nopadding clearfix">
											<div class="form-group nomargin">
												<div class="col-md-3 nopadright">
													<label class="control-label col-md-3 nopadleft nopadtop mt3">Patient</label>
													<div class="col-md-9 nopadding">
<%-- 														<eCW:patientLookup patientName="vm.selectPatientForFilter" --%>
<%-- 														topclass="vm.selectPatientForFilter" --%>
<%-- 														clear-patient-data="vm.clearPatientDataForFilter()" --%>
<%-- 														patientClick="vm.setPatientInfoForFilter()"> --%>
<%-- 														</eCW:patientLookup> --%>
														<div patientlookupinline patient-name="vm.selectPatientForFilter" topclass="filterPatient1" patient-click="vm.setPatientInfoForFilter()"  clear-patient-data="vm.clearPatientDataForFilter()" ></div>
														</div>
												</div>
												<div class="col-md-3">
													<label class="control-label col-md-4 nopadtop mt3">Intervention</label>
													<div class="col-md-8 nopadding">
														<div class="select-box">
															<b class="caret selection-click"></b>
															<span class="selection-field selection-click" ng-bind="vm.filterOptionObj.interventionRsn.reason">Select</span>
															<ul class="sel-optn  selection-options" perfect-scrollbar refresh-on-resize="true" refresh-on-change="vm.interventionReasonsData" ng-style="{ 'height' :{true:'75px',false:'{{75 - vm.interventionReasonsData.length * 20}}}px'}[vm.interventionReasonsData.length >=3 ] }">
																 <li ng-repeat="item in vm.interventionReasonsData" id="{{item.reason}}" ng-click="vm.selectFilterReason(item)" >{{item.reason}}</li>
															</ul>
														</div>
													</div>
												</div>
												<div class="col-md-3 nopadleft">
													<label class="control-label col-md-2 nopadleft nopadtop mt3">Category</label>
													<div class="col-md-10 nopadding">
														<div class="select-box">
															<b class="caret selection-click"></b>
															<span class="selection-field selection-click" ng-bind="vm.filterOptionObj.filterCategoryObj.listName">Select</span>
															<ul class="sel-optn  selection-options">
																 <li ng-repeat="item in vm.interventionCategory" id="{{item.listName}}" ng-click="vm.interventionFilterCategoryClicked(item)">{{item.listName}}</li>
															</ul>
														</div>
													</div>
												</div>
												<div class="col-sm-3 nopadding checkbox-logical">
													<label class="col-md-1 nopadding control-label mt2">Status</label>
													<div class="col-md-11 nopadright">
														<div class="doc-att pull-left mr8">
															<label class="radio-inline">
																<input name="lrbutton" type="radio" checked="" ng-model="vm.filterOptionObj.interventionStatus" ng-value="'All'">All
															</label>
														</div>
														<div class="doc-att pull-left mr8">
															<label class="radio-inline">
																<input name="lrbutton" type="radio" ng-model="vm.filterOptionObj.interventionStatus" ng-value="'Pending'" >Pending
															</label>
														</div>
														<div class="doc-att pull-left mr8">
															<label class="radio-inline">
																<input name="lrbutton" type="radio" ng-model="vm.filterOptionObj.interventionStatus" ng-value="'In Progress'" >In Progress
															</label>
														</div>
														<div class="doc-att pull-left">
															<label class="radio-inline">
																<input name="lrbutton" type="radio" ng-model="vm.filterOptionObj.interventionStatus" ng-value="'Completed'" >Completed
															</label>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="col-lg-12 mt10 nopadding clearfix">
											<div class="form-group nomargin">
												<div class="col-md-3 nopadright">
													<label class="control-label col-md-3 nopadleft nopadtop mt3">Assigned To</label>
													<div class="col-md-9 nopadding">
														<div class="input-group input-group-sm">
															<eCW:staffLookup staffName="vm.filteredstaff"
																staffClick="vm.setFilteredStaff()"
																placeHolder="Staff Name"
																topclass="staffLookup1"
																clearStaffData="vm.clearFilteredStaff()">
															</eCW:staffLookup>
														</div>
													</div>
												</div>
												<div class="col-md-3">
													<label class="control-label nopadtop mt3 col-md-4">Entered By</label>
													<div class="col-md-8 nopadding">
														<div class="select-box">
															<b class="caret selection-click"></b>
															<span class="selection-field selection-click" ng-bind="vm.filterOptionObj.EnteredBy.patientName">Select</span>
															<ul class="sel-optn  selection-options" perfect-scrollbar refresh-on-resize="true" refresh-on-change="vm.EnteredBy" ng-style="{ 'height' :{true:'75px',false:'{{75 - vm.EnteredBy.length * 20}}}px'}[vm.EnteredBy.length >=3 ] }">
																 <li ng-repeat="item in vm.EnteredBy" ng-click="vm.filterEnteredByClicked(item)">{{item.ptdetails.patientName}}</li>
															</ul>
														</div>


													</div>
												</div>
												<div class="col-md-3 nopadleft">
													<label class="control-label nopadtop mt3 col-md-2">Date</label>
													<div class="col-md-10 nopadding">
														<div class="col-md-5 nopadding">
															<div class="input-group input-group-sm dateTimeRange">
																<input 
																	type="text"
																	date-range-picker 
																	ng-model="vm.filterOptionObj.fromDateTime"
																	class="form-control singletime-entry-input masked ng-pristine ng-untouched ng-valid ng-isolate-scope ng-valid-mask ng-not-empty" 
																	options="vm.singleDateOption" 
																	ui-mask="?1?9/?3?9/?9?9?9?9 ?2?9:?5?9" 
																	ui-options="{dateFormat: vm.singleDateOption.locale.format}"
																	placeholder="__/__/____ __:__"
																	/>
																<span class="input-group-btn">
																<button class="btn btn-default ng-datepicker-trigger" type="button">
																	<i class="icon icon-inputcalender"></i>
																	</button>
																</span>
															</div>
														</div>
														<label class="col-md-2 control-label text-center mt3">to</label>
														<div class="col-md-5 nopadding">
															<div class="input-group input-group-sm dateTimeRange">
																<input 
																	type="text"
																	date-range-picker 
																	ng-model="vm.filterOptionObj.toDateTime"
																	class="form-control singletime-entry-input masked ng-pristine ng-untouched ng-valid ng-isolate-scope ng-valid-mask ng-not-empty" 
																	options="vm.singleDateOption" 
																	ui-mask="?1?9/?3?9/?9?9?9?9 ?2?9:?5?9" 
																	ui-options="{dateFormat: vm.singleDateOption.locale.format}"
																	placeholder="__/__/____ __:__"
																	/>
																<span class="input-group-btn">
																<button class="btn btn-default ng-datepicker-trigger" type="button">
																	<i class="icon icon-inputcalender"></i>
																	</button>
																</span>
															</div>
														</div>
													</div>
												</div>
												<div class="col-lg-3 col-md-3 nopadding doc-att">
													<div class="pull-left mr10">
														<label class="checkbox-inline">
															<input type="checkbox" id="grpbypatients" value="option1">Group by Patients
														</label>
													</div>
													<div class="col-md-7 nopadding pull-left" id ="filterTempletes">
														<ipfiltertemplate
														allconfigfilterdetails="vm.jsonFilterDetails"
														screen-name="PharmacyIntervention"
														call-after-filter-details-fetch="vm.fetchFilterDetails()"
														selected-filter-details="vm.populateConfigFilterValues()">
														</ipfiltertemplate>
														<div class="pull-right nopadleft mr5 mt3">
														<button type="button" class="btn-blue btn-xs pull-left" ng-click="vm.filterInterventionData();">
															<i class="icon icon-wfilter"></i> Filter
														</button>
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
								<!--start header button section-->
								<div class="darkpad clearfix">
									<div class="relative pull-left">
										<button type="button" class="btn btn-default new-btn pull-left mr5 drkpad-btns" ng-click="vm.assignToPopUP($event)">Assign</button>
										<div class="relative pull-left">
											<button class="btn btn btn-default new-btn drkpad-btns pull-left mr5 dropdown-toggle" id="changeStatusBtn"  data-toggle="dropdown">
												Change Status
											</button>
											<ul class="dropdown-menu cpselect1 mt5 w110" id="StatusList">
												<li class="nooverflow">
													<div class="ststuslist widthzero nomarleft">
														<div class="col-sm-12 nopadding">
															<ul>
																<li class="admit-pop">
																	<label class="checkbox-inline fnt11 padl5" ng-click="vm.changeStatusForInterventionList('Pending')">Pending</label>
																</li>
																<li class="discharge-pop">
																	<label class="checkbox-inline fnt11 padl5" ng-click="vm.changeStatusForInterventionList('In Progress')">In Progress</label>
																</li>
																<li>
																	<label class="checkbox-inline fnt11 padl5" ng-click="vm.changeStatusForInterventionList('Completed')">Completed</label>
																</li>
															</ul>
														</div>
														<div class="clearfix"></div>
													</div>
												</li>
											</ul>
										</div>
											<button type="button" class="btn btn-default new-btn pull-left drkpad-btns mr5 " ecwexport format="{{vm.exportOptions.format}}" exportjson="" filename="{{vm.exportOptions.filename}}" before-export="vm.setExportData()" >Export</button>
										    <!-- Print Directive Integration -->
										    <div class="relative pull-left mr5">
										    <print-button unique-id="printInterventionReport"  print-label="Print"  before-print="vm.setPrintData" show-preview="{{vm.printOptions.showPreview}}" print-data-destination="vm.printOptions.dataDestination" format="vm.printOptions.format" module="Pharmacy" plain-data="vm.printOptions.Htmldata">
										    </print-button>
										    </div>
										    <button type="button" class="btn btn-default new-btn drkpad-btns pull-left" id="intervation-add-btn" ng-show="vm.securitySettings.InterventionAddAccess===1" ng-click="vm.addIntervention($event)">Add</button>
											<div class="mt7 pull-left select-filter-text-interventions" ng-if="vm.isFilterApplied">
											<button type="button" class="close cancelbtn4 fnt16 pull-left ml10" ng-click="vm.closeFilter()">x</button>
											<span class="fnt11 ml10">Filter Applied :</span>
											<span class="fnt11 fnbld getfiltername"  ng-bind="vm.jsonFilterDetails.selectedFilter.filterName"></span>
										</div>
										<div class="clearfix"></div>
									</div>
								</div>
								<div class="col-sm-12 nopadding">
									<div class="main-dualheaderdiv white-bg inter-table">
										<table class="dualheader-table1 table table-bordered nomargin" id="interventionsTable">
											<thead>
												<tr>
													<th class="w2-p">
														<div class="select-allcheck">
															<div class="pad6 text-center">
																<input type="checkbox" ng-click="vm.checkAll()" ng-model="vm.isAllSelected" />
															</div>
														</div>
													</th>
													<th class="w10p">
														<div class="upper relative">Patient Name
														</div>
													</th>
													<th class="w5p">
														<div class="upper relative">Gender
														</div>
													</th>
													<th class="w3p">
														<div class="upper relative">Age
														</div>
													</th>
													<th class="w5p">
														<div class="upper relative">Category
														</div>
													</th>
													<th class="w29p">
														<div class="upper relative">Interventions
														</div>
													</th>
													<th class="w5p">
														<div class="upper relative">Duration
														</div>
													</th>
													<th class="w8p">
														<div class="upper relative">Status
														</div>
													</th>
													<th class="w8p">
														<div class="upper relative">Saving Value
														</div>
													</th>
													<th class="w8p">
														<div class="upper relative">Time Spend Value
														</div>
													</th>
													<th class="w10p">
														<div class="upper relative">Entered By
														</div>
													</th>
													<th class="w8p">
														<div class="upper relative">Entered Date
														</div>
													</th>
													<th class="w10p">
														<div class="upper relative">Assigned To
														</div>
													</th>
													<th class="w2-p">

													</th>
												</tr>
											</thead>
										</table>
										<div class="hgtscroll interventions-tbl-hgt loclistview-tblhgt relative">
											<table class="dualheader-table2 table table-bordered clearfix nomargin" id="interventionTable">
												<tbody>
													<tr ng-repeat="item in vm.interventionsData | filter:vm.categoryFilter" ng-class="{'brdbtm' : $last, 'highlight-row' : item.selected}" ng-class-even="'even'" ng-class-odd="'odd'" >
														<td class="w2-p">
															<div class="select-allcheck" >
																<div class="pad6 text-center">
																	<input type="checkbox" class="mt2" ng-model="item.isSelected" ng-change="vm.patientToggled(vm.interventionsData,item,false)" />
																</div>
															</div>
														</td>
														<td class="w10p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper textunderline">
																{{item.ptdetails.patientName}}
															</div>
														</td>
														<td class="w5p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.ptdetails.patientGender}}
															</div>
														</td>
														<td class="w3p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.ptdetails.patientAge}}
															</div>
														</td>
														<td class="w5p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper white-tag">
																{{item.interventionName}}
															</div>
														</td>
														<td class="w29p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.reason}}
															</div>
														</td>
														<td class="w5p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.duration}} {{item.durationUnits}}
															</div>
														</td>
														<td class="w8p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.status}}
															</div>
														</td>
														<td class="w8p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.savingValue}}
															</div>
														</td>
														<td class="w8p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.timeSpendValue}}
															</div>
														</td>
														<td class="w10p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.createdBy}}
															</div>
														</td>
														<td class="w8p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.createdOn}}
															</div>
														</td>
														<td class="w10p" ng-click="vm.showInterventionPopUp('Edit',item.interventionId,item.patientId)">
															<div class="tdupper">
																{{item.assignedToUserName}}

															</div>
														</td>
														<td class="w2-p text-center">
															<i class="icon icon-trash mt5" ng-show="vm.securitySettings.InterventionDeleteAccess===1 && item.status!='Completed'" ng-click="vm.showConfirmDialog(item)"></i>
														</td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
<!-- 							<div class="add-templatestyle" id="select-temp-drop-interventions"> -->
<!-- 								<div class="col-sm-12 nopadding addtable"> -->
<!-- 									<table class="table table-bordered nomargin green-table-style"> -->
<!-- 										<tbody class="all-active-filter"> -->
<!-- 											<tr> -->
<!-- 												<td> -->
<!-- 													<div class="pull-left"> -->
<!-- 														Quarterly report order -->
<!-- 													</div> -->

<!-- 													<i class="icon icon-trash ml5 pull-right"></i> -->
<!-- 												</td> -->
<!-- 											</tr> -->
<!-- 											<tr> -->
<!-- 												<td> -->
<!-- 													<div class="pull-left"> -->
<!-- 														Basic emergency order -->
<!-- 													</div> -->
<!-- 													<i class="icon icon-trash ml5 pull-right"></i> -->
<!-- 												</td> -->
<!-- 											</tr> -->
<!-- 											<tr> -->
<!-- 												<td> -->
<!-- 													<div class="pull-left"> -->
<!-- 														Monthly order -->
<!-- 													</div> -->
<!-- 													<i class="icon icon-trash ml5 pull-right"></i> -->
<!-- 												</td> -->
<!-- 											</tr> -->
<!-- 										</tbody> -->
<!-- 									</table> -->
<!-- 								</div> -->
<!-- 							</div> -->
							<div class="clearfix"></div>
							<div class="foot-control mt14 brdrtop det-view clearfix">
						<div-pagination-control 
									total-items="vm.totalInterventionsRecords" 
									current-page="vm.filterOptionObj.selectedPage" 
									items-per-page="vm.filterOptionObj.recordsPerPage" 
									on-page-change="vm.filterInterventionData();" 
									class="pagination pagination-sm pad10 nomargin"> 
						</div-pagination-control>
							</div>
							<div class="inter-reasons-popup add" style="display: block; top: 134px;" ng-show="vm.showAddPopUp" >
											<div class="add-edit neweditpopup-arrow"  >
											<button type="button" class="close" ng-click="vm.closePopUp()" aria-hidden="true">&times;</button>
												<div class="col-sm-12 det-view btmbdrr nopadb">
													<div class="pad10 clearfix">
														<div class="form-group clearfix">
 															<label class="control-label col-sm-2 " style="margin-top: -20px;">Patient</label>
				 							<div class="col-sm-12"> 								 
<%-- 											<eCW:patientLookup patientName="vm.selectedPatient" --%>
<%-- 														topclass="vm.selectedPatient" --%>
<%-- 														clear-patient-data="vm.clearPatientData()" --%>
<%-- 														patientClick="vm.setPatientInfo()"> --%>
<%-- 											</eCW:patientLookup> --%>
											<div patientlookupinline patient-name="vm.selectedPatient" topclass="filterPatient2" patient-click="vm.setPatientInfo()"  clear-patient-data="vm.clearPatientData()" ></div>
											</div>
 														</div> 
												</div>
												<div class="clearfix"></div>
											</div>
										</div>
										
									</div>
				<!-- Change AssignTo templete start  -->
				<div class="assign-div add" id="assignToBtn" style="top: 134px;" ng-show="vm.showAsssignedToPopUp" >
											<div class="assign-arrow">
											<button type="button" class="close" ng-click="vm.closePopUp()" aria-hidden="true">&times;</button>
												<div class="col-sm-12 det-view btmbdrr nopadding">
													<div class="pad10 clearfix">
														<div class="form-group clearfix">
 															<label class="control-label col-sm-3
 															 nopadleft" >Assign to</label>
 															 <div class="col-sm-9 nopadleft"> 
																	<eCW:staffLookup staffName="vm.staffName"
																			staffClick="vm.setAssignedTo()"
																			placeHolder="Assign To"
																			topclass="staffLookup2"
																			clearStaffData="vm.clearStaff()">
																		</eCW:staffLookup>
															</div>
 														</div> 
													</div>
												<div class="col-sm-12 brdrtop2">
													<div class="pull-right mt10 mb10">
														<button type="button" class="btn btn-blue btn-xs"
															ng-click="vm.changeAssignedTo()">OK</button>
														<button type="button" class="btn btn-lgrey btn-xs"
															ng-click="vm.closePopUp()">Cancel</button>
													</div>
													<div class="clearfix"></div>
												</div>
												<div class="clearfix"></div>
											</div>
										</div>
										
									</div>

		</div>
					</div>
			</div>
