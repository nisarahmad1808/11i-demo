<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/ph-resultslist.css">
<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>
<div class="tab-content" id="ph-result-module" ng-controller="phLabResultCtrl as labResultObj" ng-init="labResultObj.init();">
	<div class="collapse in" id="ph-result-filter">
		<div class="fileter-wrap det-view padTB green-wrap pt10">
			<div class="col-sm-12 clearfix">
				<div class="col-sm-2 nopadleft">
					<div class="form-group clearfix">
						<label class="control-label nopadtop mt3 col-sm-3">Facility </label>
						<div class="col-sm-8 nopadding">
							<inline-facility-lookup multi-select="false" selected-facility="labResultObj.selectedFacility"
							 on-select="labResultObj.onSelectFacility(selectedFacility)"></inline-facility-lookup>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadleft">
					<div class="form-group clearfix">
						<label class="control-label col-lg-3 col-md-3 nopadleft nopadtop mt3">Patient</label>
						<div class="col-lg-9 col-md-9 nopadright">
							<eCW:patientLookup patientName="labResultObj.patientForFilter"
								topclass="labResultObj.patientForFilter"
								clear-patient-data="labResultObj.clearPatientData()"
								patientClick="labResultObj.setPatientInfo()">
							</eCW:patientLookup>
						</div>
					</div>
				</div>
				<div class="col-sm-3 nopadleft">
					<div class="form-group clearfix">
						<label class="control-label nopadtop nopadleft mt3 col-lg-4 col-md-4">Start &amp; End Date</label>
						<div class="col-lg-8 col-md-8 nopadding">
							<div class="date-rangewidth">
								<div class="input-group input-group-sm">
									<input date-range-picker type="text" class="form-control nopadright" 
										   ng-model="labResultObj.dateRange" options="labResultObj.rangeDateOpt" ui-mask="?1?9/?3?9/?9?9?9?9 to ?1?9/?3?9/?9?9?9?9" 
										   ui-options="{dateFormat: labResultObj.rangeDateOpt.locale.format}"> <span class="input-group-btn">
                                <button class="btn btn-default" type="button">
                                    <span class="icon icon-inputcalender"></span> </button>
									</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-2 nopadleft">
					<div class="form-group nomargin">
						<div class="col-sm-12 nopadding doc-att">
							<label class="radio-inline">
								<input name="status" type="radio" ng-model="labResultObj.reviewStatus" id="allval" value="0" ng-click="labResultObj.getFilteredLabResult();" checked >All
							</label>
							<label class="radio-inline">
								<input name="status" type="radio" ng-model="labResultObj.reviewStatus" id="active" value="1" ng-click="labResultObj.getFilteredLabResult();" >Reviewed
							</label>
							<label class="radio-inline">
								<input name="status" type="radio" ng-model="labResultObj.reviewStatus" id="inActive" value="2" ng-click="labResultObj.getFilteredLabResult();">Unreviewed
							</label>
						</div>
					</div>
				</div>
				<div class="col-sm-2 nopadleft">
					<div class="form-group clearfix">
						<div class="col-lg-12 col-md-12 nopadding">
							<div class="col-sm-12 col-lg-12 nopadding">
								<div class="pull-right relative">
									<ipfiltertemplate
										allconfigfilterdetails="labResultObj.jsonFilterDetails"
										screen-name="PharmacyLabReview"  
										call-after-filter-details-fetch="labResultObj.fetchFilterDetails()"
										selected-filter-details="labResultObj.populateConfigFilterValues()">
									</ipfiltertemplate>
									<div class="pull-right nopadleft mr5 mt3">
										<button class="btn btn-blue btn-xs pull-left lnheight18 mr5" type="button" ng-click="labResultObj.getFilteredLabResult();">
											<i class="icon icon-wfilter"></i> Filter
										</button>
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
	<div class="darkblue-nav green-nav mb-1"> <a href="#" class="icon icon-greenfilter" data-target="#ph-result-filter" data-toggle="collapse">filter</a>
		<div class="darkpad">
			<div class="relative pull-left mr10"></div>
			<div class="pull-left select-filter-text">
				<div class="mt7 pull-left select-filter-text-all-active-list" ng-if="labResultObj.isFilterApplied" >
					<!-- cancelbtn4 -->
					<button type="button" class="close fnt16 pull-left ml10" ng-click="labResultObj.closeFilter()">x</button>
					<span class="fnt11 ml10">Filter Applied :</span> <span class="fnt11 fnbld getfiltername"></span>
				 </div>
			</div>
			<button type="button" class="btn new-btn drkpad-btns pull-right mr10" ng-click="labResultObj.markAsReviewed();">Mark as Reviewed</button>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="clearfix"></div>
	<div class="row">
		<div class="col-sm-12">
			<div class="simpleTable">
				<div class="tablehead blackheader">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th class="w2p"></th>
								<th class="w2p text-center">
									<input type="checkbox" ng-click="labResultObj.checkAll()" ng-model="labResultObj.isAllSelected"> </th>
								<th class="w2p"></th>
								<th class="w15p">Patient Name</th>
								<th class="w4p">Age</th>
								<th class="w5p">Gender</th>
								<th class="w9p">Date &amp; Time </th>
								<th class="w16p">Lab Name</th>
								<th class="w6p">Priority</th>
								<th class="w12p">Reason for Test</th>
								<th class="w5p">Reviewed</th>
							</tr>
						</thead>
					</table>
				</div>
				<div class="tablebody hgtscroll relative ph-resultList-cust-scroll">
					<table class="table table-bordered">
						<tbody ng-repeat="row in labResultObj.phAllActiveData">
							<tr ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}">
								<td class="w2p text-center"> <i class="icon-panright" data-toggle="collapse" ng-class="{'collapsed': $index!=-1}" data-target="#worktab{{row.reportId}}"></i>
								</td>
								<td class="w2p text-center">
									<input type="checkbox"  ng-model="row.selected" data-ng-if="!row.reviewStatus" ng-click="labResultObj.patientToggled()"> 
								</td>
								<td class="w2p text-center"> <i class="icon icon-attachment" ng-if="row.showPaperClip"  data-ng-click="labResultObj.openNoteAttachModal(row)"></i> </td> 
								<!-- data-ng-if="row.checkattach" -->
								<td class="w15p" ng-bind="(row.ptfname+' '+row.ptlname)"></td>
								<td class="w4p">{{row.age}}</td>
								<td class="w5p">{{row.gender}}</td>
								<td class="w9p">{{row.orderDate}}</td>
								<td class="w16p">{{row.itemName}}</td>
								<td class="w6p">
									<div class="tdupper nopadding pt2 pl5">
									<i class="icon prio-red" ng-show="row.priorityName == 'STAT'">{{row.priorityName}}</i>
									<i class="icon prio-gray" prio-gray ng-show="row.priorityName == 'Routine'">{{row.priorityName}}</i>
									<i class="icon prio-yellow" ng-show="row.priorityName == 'ASAP'">{{row.priorityName}}</i>
									</div>
								</td>
								<td class="w12p">{{row.reason}}</td>
								<td class="w5p"><i class="icon icon-green-chk ml15" data-ng-if="row.reviewStatus"></i></td>
							</tr>
							<tr>
								<td class="light-collapsebg nopadding" colspan="11">
									<div class="col-sm-12 collapse pb10" ng-class="{}" id="worktab{{row.reportId}}">
										<div class="simpleTable">
											<div class="tablehead">
												<table class="table collapse-white-table drk-table-bdr">
													<thead>
														<tr>
															<th class="w16p drk-table-hdr">Result Name</th>
															<th class="w10p drk-table-hdr">Value</th>
															<th class="drk-table-hdr">Reference Range</th>
														</tr>
													</thead>
												</table>
											</div>
											<div class="tablebody">
												<table class="table collapse-white-table nested-data-table">
													<tbody>
														<tr ng-repeat="item in row.labAttributes">
															<td class="w16p">{{item.itemName}}</td>
															<td class="w10p">{{item.value}}</td>
															<td>{{item.range}}</td>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="foot-control mt14 brdrtop det-view clearfix">
		<div-pagination-control 
				total-items="labResultObj.totalCount" 
				current-page="labResultObj.selectedPage" 
				items-per-page="labResultObj.recordsPerPage" 
				on-page-change="labResultObj.getFilteredLabResult();" 
				class="pagination pagination-sm pad10 nomargin"> 
		</div-pagination-control>
	</div>
	<div class="clearfix"></div>
</div>

