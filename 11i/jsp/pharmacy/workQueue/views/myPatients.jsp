<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/mypatientlist.css">
<div class="tab-content" id="ph-myPatient-module" ng-controller="myPatientsCtrl as objMyPatients" ng-init="objMyPatients.init();">
	<div class="collapse in" id="ph-pending-filter1">
		<div class="fileter-wrap det-view green-wrap pt10 clearfix">
			<div class="col-sm-12 nopadding clearfix">
				<div class="col-sm-3 nopadding">
					<div class="col-sm-12 nopadding">
						<div class="form-group clearfix">
							<label class="control-label mt3 col-sm-3">Facility</label>
							<div class="col-sm-9 nopadleft">
								<inline-facility-lookup multi-select="false"
									selected-facility="objMyPatients.selectedFacility"
									on-select="objMyPatients.onSelectFacility(objMyPatients.selectedFacility)"></inline-facility-lookup>
							</div>
						</div>
					</div>
				
					<div class="col-sm-12 nopadding">
						<div class="form-group clearfix">
							<label class="control-label mt3 col-sm-3">Template</label>
							<div class="col-sm-9 nopadding">
								<div class="form-group nomargin for-new-order">
									<div class="input-group input-group-sm lookup-drop dropdown patient-lookup relative with-control">
										<ul class="tagit ui-widget ui-widget-content ui-corner-all nobdrig">
											<li class="tagit-new open">
												<i class="icon-Search searchicn top7"></i> 
												<input type="text" class="fnt11 form-control patient-input pl12 nobrdr clearable" ng-model="objMyPatients.selectedTemplateName">
											</li>
										</ul>
										<div class="input-group-btn last">
											<button type="button" class="btn btn-default dropdown-toggle pt5" data-toggle="dropdown">
												<span class="caret"></span>
											</button>
											<ul class="orders dropdown-menu pull-right drugdropdown">
												<li ng-repeat="item in objMyPatients.flowsheetTemplates" ng-click="objMyPatients.showPatients(item)"><a href="#">{{item.name}}</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadding">
					<div class="col-sm-12 nopadding">
						<div class="form-group clearfix">
							<label class="control-label mt3 col-sm-4">Department</label>
							<div class="col-sm-8 nopadleft">
								<inline-department-lookup  facility-id="objMyPatients.selectedFacility.facilityId" on-select="objMyPatients.setResetDept()" selected-department="objMyPatients.selectedDept" show-filter="false"></inline-department-lookup>								
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-4 nopadding">
					<div class="form-group clearfix">
						<label class="control-label mt3 col-sm-2"> Order Date</label>
						<div class="col-sm-8 nopadding">
							<div class="input-group input-group-sm">
								<input date-range-picker type="text"
									class="form-control nopadright"
									ng-model="objMyPatients.dateRange"
									options="objMyPatients.rangeDateOpt"
									ui-mask="?1?9/?3?9/?9?9?9?9 to ?1?9/?3?9/?9?9?9?9"
									ui-options="{dateFormat: rangeDateTimeOpt.locale.format}">
								<span class="input-group-btn"> 
									<button class="btn btn-default" type="button">
										<span class="icon icon-inputcalender"></span>
									</button>
								</span>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-2 nopadding">
					<div class="pull-right relative">
						<ipfiltertemplate
							allconfigfilterdetails="objMyPatients.jsonFilterDetails"
							screen-name="PharmacyMyPatients"  
							call-after-filter-details-fetch="objMyPatients.fetchFilterDetails()"
							selected-filter-details="objMyPatients.populateConfigFilterValues()">
						</ipfiltertemplate>
						<div class="pull-right nomartop relative mr20">
							<div class="dropdown relative pull-left setting-dropdown"></div>
							<button class="btn btn-blue btn-xs pull-left lnheight18 mr5" type="button"  ng-click="objMyPatients.showPatients(objMyPatients.selectedTemplate);">
								<i class="icon icon-wfilter"></i> Filter
							</button>
							<button class="btn btn-blue btn-xs pull-left lnheight18 mr5" type="button" ng-click="objMyPatients.clearFilter();">Clear</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="darkblue-nav green-nav mb-1">
		<a href="#" class="icon icon-greenfilter" data-target="#ph-pending-filter1" data-toggle="collapse">filter</a>
		<div class="darkpad">
			<div class="relative pull-left mr10"></div>
			<div class="pull-left select-filter-text" style="height:15px;">
				<!-- <button type="button" class="btn new-btn drkpad-btns pull-left ml10 mr10" ng-click="objMyPatients.showAssignPopup($event)">Assign</button> -->
			</div>
			<div class="mt7 pull-left select-filter-text-all-active-list" ng-if="objMyPatients.isFilterApplied">
				<button type="button" class="close fnt16 pull-left ml10" ng-click="objMyPatients.closeFilter()">x</button>
				<span class="fnt11 ml10">Filter Applied :</span>
				<span class="fnt11 fnbld getfiltername">{{objMyPatients.jsonFilterDetails.selectedFilter.filterName}}</span>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>

	<div class="col-sm-12 nopadding">
		<div class="clearfix"></div>
		<div class="customMultiTable table-titlebg">
			<div ng-if="objMyPatients.isMyPatientDataFound===false" style="text-align: center;">No record found</div>
			<div class="allMultiTableHeader" ng-if="objMyPatients.isMyPatientDataFound===true">
				<table>
					<tbody>
						<tr>
							<th class="w52"><input type="checkbox" class="pull-right nomargin" ng-model="objMyPatients.allSelected" ng-click="objMyPatients.checkAllOrders();" /></th>
							<th class="relative">Order Details
								<div class="pharm-leftscroll pull-right "><i class="icon table-left-arrow" ng-click="objMyPatients.scrollDate('L');"></i> </div>
							</th>
							<th width="120px" ng-repeat="dates in objMyPatients.datesToShow track by $index">{{dates}}</th>
							<th class="text-center w20 scroll-right"><i class="icon table-right-arrow  ml-5" ng-click="objMyPatients.scrollDate('R');"></i></th>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="hgtscroll relative ph-mypatient-scroll-table">
			<div class="allMultiTableBody table-data hgtscroll ph-myPatient-scroll relative ph-myPatient-table-scroll" id="order-details-table-body" ng-if="objMyPatients.isMyPatientDataFound===true">				
				<table ng-repeat="patient in objMyPatients.patientData track by $index" class="table table-bordered margin-bottom nomargin">
					<thead class="header-border-bottom">
						<tr>
							<th class="w52">
								<span> <i class="icon icon-panright text-center mt1 mr5 collapsed iconPatientRow" data-toggle="collapse" id="icon_{{patient.patientId}}" data-target="#data_{{patient.patientId}}" ng-click="objMyPatients.getFlowsheetItems(patient, $index)"></i>
								</span> 
								<span> <input type="checkbox" class="pull-right" ng-model="patient.selected" ng-click="objMyPatients.checkEntity()" />
								</span>
							</th>
							<th colspan="10" class="nopadding">
								<div class="col-sm-12 nopadding light-bluebg brdrbtm">
									<div class="subheader">
										<div class="pull-left">
											<img ng-src="{{patient.profilepic}}"
												style="height:54px; width:53px;">
										</div>
										<div class="w230 brdrright pull-left mt8">
											<div class="pull-left pl10 fnt13">
												<span class="fntbluebld13 mr5"><a
													ng-click="objMyPatients.launchPharmacyTab(patient.patientId, patient.orderEncounterId, patient.orderEncType, patient.orderEpisodeEncId, patient.deptId, patient.serviceType);">{{patient.ptFullName}}</a></span>
												<i class="icon icon-grey-info ml5"></i>
											</div>
											<div class="pull-left fnt11 mt5 pl10">
												DOB: <b>{{patient.ptDOB}}</b> <span class="pl10">AC#:
													<b>{{patient.masterEncounterId}}</b>
												</span>
											</div>
										</div>
										<div class="w200 brdrright pull-left mt8">
											<div class="fnt11 mt5 pl10">
												MRN: <b class="pl5">{{patient.ptControlNo}}</b>
											</div>
											<div class="fnt11 mt5 pl10">
												Room/Bed: <b>{{patient.roomName}}<span ng-if="patient.roomName != '' && patient.bedName != ''">/</span>{{patient.bedName}}</b>
											</div>
										</div>
										<div class="w200 brdrright pull-left mt8">
											<div class="fnt11 mt5 pl10">
												Att Provider: <b>{{patient.attnDocFullName}}</b>
											</div>
											<div class="fnt11 mt5 pl10">
												Adm Date: <b>{{patient.admissionDate}}</b>
											</div>
										</div>
										<div class="w210 brdrright clearfix pull-left mt8">
											<div class="fnt11 mt5 pl10">
												Primary Dx: <b>{{patient.chiefComplaint}}</b>
											</div>
											<div class="fnt11 mt5 pl10">
												Allergies: <span class="text-red fntbold">{{patient.allergies}}</span>
											</div>
										</div>

										<div class="w300 pull-left mt8">
											<div class="fnt11 mt5 pull-left pl10" ng-repeat = "vital in patient.vitalDetails">
												{{vital.vitalType}}: <b>{{vital.value}} {{vital.unitName}}</b>
											</div>
										</div>

										<div class="w110 brdrleft clearfix pull-left mt8">
											<div class="fnt11 mt5 pl10">
												Code Status: <b>{{patient.codeStatus}}</b>
											</div>
											<div class="fnt11 mt5 pl10">
												Isolation: <span class="text-red fntbold">{{patient.Isolation}}</span>
											</div>
										</div>

									</div>
								</div>

							</th>
						</tr>
					</thead>
					<tbody class="collapse nopadding collapseclass patientRow" id="data_{{patient.patientId}}">
						<tr ng-repeat="templateItem in patient.patientContent">
							<td class="w30"></td>
							<td><span class="pl5"> {{templateItem.itemName}}</span></td>

							<td class="w120 nopadding" ng-repeat=" date in objMyPatients.datesToShow">
								<span ng-repeat="data in templateItem.dataListPerDates" ng-if="data.date == date">
		              				<span class="pl5">
										<span class='number-bg' ng-show="templateItem.valDateTime == true && data.time != '' " ng-repeat="value in data.values ">{{value.time}}</span>
										<span ng-show="templateItem.valDateTime == false" class="pl5">{{data.valueToShow.value}}</span>
									</span>
									<span class='number-bg pull-right brdrleft nobg browsstyle' ng-show="data.values.length > 1">
	                  					<i class="icon icon-browser pt6" ng-mouseover="objMyPatients.valuePopover($event, data.values, templateItem.valDateTime)" ng-mouseleave="objMyPatients.closePopover()"></i>
	              					</span>
              					</span>
							</td>
							<td class="text-center w20 scroll-right"></td>
						</tr>
					</tbody>
				</table>
			</div>
		 </div>
		<div class="clearfix"></div>	  
		<div class="foot-control mt14 brdrtop det-view clearfix">
			<div-pagination-control 
					total-items="objMyPatients.totalPatientRecords" 
					current-page="objMyPatients.filterOptions.selectedPage" 
					items-per-page="objMyPatients.filterOptions.recordsPerPage" 
					on-page-change="objMyPatients.getMyPatientData();" class="pagination pagination-sm pad10 nomargin"> 
			</div-pagination-control>
		</div>										
	  </div>		      							
	</div>		  		
	<div class="assign-div1 absolute-dropdown" style="display:none;">
		<div class="assign-arrow">
			<div class="col-lg-12 nopadding">
				<div class="col-lg-12 col-md-12 nopadright det-view">
					<div class="form-group clearfix">
						<label class="control-label mt3 col-sm-3">Assign to</label>
						<div class="col-sm-9 nopadleft">
							<eCW:staffLookup staffName="objMyPatients.assignTo"
								staffClick="objMyPatients.setAssignTo()"
								filterPosition="right" placeHolder="Staff Name"
								topclass="staffLookup"
								clearStaffData="objMyPatients.clearAssignTo()">
							</eCW:staffLookup>
						</div>
					</div>
				</div>
				<div class="col-sm-12 brdrtop2">
					<div class="pull-right mt10 mb10">
						<button type="button" class="btn btn-blue btn-xs" ng-click="objMyPatients.showAssignConfirm()">OK</button>
						<button type="button" class="btn btn-lgrey btn-xs" ng-click="objMyPatients.closeAssignPopup()">Cancel</button>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="proceed-prompt collection-prompt" id="startingprompt" style="display:none;">
		<div class="col-sm-12 nopadding h100p">
			<div class="left-prompt col-sm-2">
				<i class="icon icon-alert-info"></i>
			</div>
			<div class="prompt-message text-center col-sm-10 nopadright">
				<p class=" msg-txt">Do you want to assign these patients to {{objMyPatients.assignTo.staff.name}}?
				</p>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center yes-collection" ng-click="objMyPatients.assignPatients();">Yes</button>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center" ng-click="objMyPatients.closeAssignConfirm()">No</button>
			</div>
		</div>
	</div>

	<div class="info-popup width-520 " id="value-row-popup" style="display: none;">
		<div class="value-row-arrow"></div>
		<div class="col-sm-12 mt10 mb10">
			<div class="simpleTable">
				<div class="tablehead">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th class="w25p nopadright" ng-if="objMyPatients.showValueColumn">Values</th>
								<th class="w25p nopadright">Time</th>
								<th class="nopadright">User</th>
							</tr>
						</thead>
					</table>
				</div>
				<div class="tablebody">
					<table class="table table-bordered">

						<tbody ng-repeat="data in objMyPatients.valuesToShow track by $index" class="nobrdr">
							<tr>
								<td class="w25p nopadright" ng-if="objMyPatients.showValueColumn">{{data.value}}
								</td>
								<td class="w25p nopadright">{{data.time}}</td>
								<td class="nopadright">{{data.user}}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

</div>