<%@taglib uri="/WEB-INF/tlds/ipdata.tld" prefix="eCW"%>
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/medication-orders-modal.css">
<script type="text/javascript">
	var orderId = ${orderId};
	var patientId = ${patientId};
	var encounterId = ${encounterId};
	var encounterType = ${encounterType};
	var pharmacyStatus = ${pharmacyStatus};
	var concurrentUserId = ${concurrentUserId};
</script>
<div id="medi-orders-modal">
	<div class="modal fade" id="mediOrderModal" role="dialog" data-backdrop="false" ng-controller="medOrdersCtrl as vm" ng-init="vm.init();">
		<div class="modal-dialog" style="width: 1300px;">
			<div class="modal-content global-property overflow-hidd">
				<div class="modal-header clearfix">
					<button type="button" class="close" data-dismiss="modal"
						ng-click="vm.closeMedOrders();" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Medication Orders</h4>
				</div>
				<div class="modal-body hgt546 med-orders">
					<div class="col-sm-12 brdrbtm graybg nopadding">
						<div class="sect3 nomargin">
							<patient-Info-Header-Directive
								patient-id="vm.patientId"
								encounter-id="vm.encounterId"
								enc-type="vm.encounterType">
							</patient-Info-Header-Directive>
						</div>
					</div>
					<div class="col-sm-12 nopadding">
						<div class="col-sm-12 nopadding tabinner4">
							<div class="col-sm-12 nopadding">
								<div class="pad10 clearfix">
									<div class="col-sm-3 nopadleft tag-tab tag-width">
										<ul class="nav nav-tabs nav-justified" role="tablist">
											<li class="active clk1"><a aria-controls="lisinopril del" role="tab" data-toggle="tab" data-target="#medOrder-tab1">Details </a></li>
											<li ng-show="vm.medOrderData.PCA"><a aria-controls="lisinopril del" role="tab" data-toggle="tab" data-target="#medOrder-tab3">PCA Details</a></li>
											<li class="nobdr-rgt clk3"><a aria-controls="lisinopril alert" role="tab" data-toggle="tab" data-target="#medOrder-tab2" ng-class="vm.alertStyle">Alerts</a></li>
											<!-- <li class="nobdr-rgt clk3"><a aria-controls="lisinopril stock" role="tab" data-toggle="tab" data-target="#lisinopril-stock">Stock</a></li> -->
										</ul>
									</div>
									<div class="col-sm-9 mt5 nopadding">
										<span class="fnt11 pr15 brdrright complex-iv">Order Type: <span class="fntbld">{{vm.medOrderData.orderType}}</span></span> 
										<span class="fnt11 ml15 pr15 brdrright">Rx Number: <span class="fntbld">{{vm.medOrderData.rxNumber}}</span></span>
										<span class="fnt11 ml15 pr15 brdrright">SCR: <span class="fntbld" ng-bind="vm.scrLabValue.Value"></span>&nbsp;<span class="fntbld" ng-bind="vm.scrLabValue.Units"></span></span> 
										<span class="fnt11 ml15 pr15 brdrright">CrCL: <span class="fntbld" ng-bind="vm.crclLabValue.Value"></span>&nbsp;<span class="fntbld" ng-bind="vm.crclLabValue.Units"></span></span>
										<span class="ml10 order-completed"><i class="icon icon-pharmacy-interventions greenico" ng-show="vm.interventionCounts.CompletedCount > 0" title="Intervention Completed : {{vm.interventionCounts.CompletedCount}}"></i></span> 
										<span class="ml10 order-progress"><i class="icon icon-pharmacy-interventions red" ng-show="vm.interventionCounts.InProgressCount > 0" title="Intervention In Progress : {{vm.interventionCounts.InProgressCount}}"></i></span>
										<span class="ml10 order-progress"><i class="icon icon-pharmacy-interventions grey" ng-show="vm.interventionCounts.PendingCount > 0" title="Intervention Pending : {{vm.interventionCounts.PendingCount}}"></i></span>
										<drug-interaction-component episode-enc-id="{{vm.episodeEncounterId}}" module-enc-Id="{{vm.encounterId}}" title="Interactions" called-from="Pharmacy" ></drug-interaction-component>
									</div>
								</div>
								<div class="tab-content relative">
									<div role="tabpanel" class="tab-pane active" id="medOrder-tab1">
										<div class="col-sm-12 greybg-top">
											<div class="pull-left brdrright padlr15">
												<div class="fnt11">
													<b>Ordering Provider:</b> <span class="bold">{{vm.medOrderData.orderingProvider}}</span>
												</div>
												<div class="fnt11">
													<b>Priority:</b> <span class="bold">
														<i class="icon priority-red " ng-show="vm.medOrderData.priorityName == 'STAT'">{{vm.medOrderData.priorityName}}</i>
														<i class="icon priority-gray" prio-gray ng-show="vm.medOrderData.priorityName != 'STAT' && vm.medOrderData.priorityName != 'ASAP'">{{vm.medOrderData.priorityName}}</i>
														<i class="icon priority-yellow" ng-show="vm.medOrderData.priorityName == 'ASAP'">{{vm.medOrderData.priorityName}}</i>
													</span>
												</div>
											</div>
											<div class="pull-left brdrright padlr15">
												<div class="fnt11">Include Now: <span class="bold" ng-if="vm.medOrderData.includeNow">Yes</span><span class="bold" ng-if="!vm.medOrderData.includeNow">No</span></div>
												<div class="fnt11"> Last Administered Time: <span class="bold">{{vm.medOrderData.lastTaken}}</span>
												</div>
											</div>
											<div class="pull-left brdrright padlr15">
												<div>
													<label class="checkbox-inline fnt11 nopadding">
														<input type="checkbox" class="mr5" ng-model="vm.medOrderData.substitute" ng-checked="vm.medOrderData.substitute == 'true'" ng-disabled="true">
														Do Not Substitute 
														
													</label>  
												</div>
												<div>
													<label class="checkbox-inline fnt11 nopadding"> 
														<input type="checkbox" class="mr5" ng-model="vm.medOrderData.ownMed" ng-checked="vm.medOrderData.ownMed == 'true'" ng-disabled="true">
														POM
													</label>
												</div>
											</div>
											<div class="pull-left brdrright padlr15">
												<div class="fnt11"> Non Billable: <span class="bold" ng-if="vm.medOrderData.billable == false">Yes</span><span class="bold" ng-if="vm.medOrderData.billable == true">No</span>
												</div>
												<label class="checkbox-inline fnt11 nopadding">
													<input type="checkbox" class="mr5" ng-model="vm.medOrderData.dualVerification" ng-checked="vm.medOrderData.dualVerification == true" ng-disabled="vm.disableDualVerification || vm.medOrderData.discontinued">
													Dual Verification 
												</label>
											</div>
											<div class="pull-left padlr15">
												<div class="fnt11">
													<label class="checkbox-inline fnt11 nopadding">
														<input type="checkbox" class="mr5" ng-model="vm.medOrderData.requestToRenew" ng-checked="vm.medOrderData.requestToRenew == true" ng-disabled="true">
														Request to Renew 
													</label>
												</div>
												<!-- <div class="fnt11">
													<label class="checkbox-inline fnt11 nopadding">
														<input type="checkbox" class="mr5" ng-model="vm.medOrderData.floorStock" ng-checked="vm.medOrderData.floorStock == true" ng-disabled="true">
														Floor Stock
													</label>
												</div> -->
											</div>
											<div class="pull-left padlr15 tritation lftbrd hgt33" >
												<div class="fnt11">
													<label class="checkbox-inline fnt11 nopadding">
														<input type="checkbox" class="mr5" ng-model="vm.medOrderData.generateBarcode" ng-disabled="vm.medOrderData.discontinued">
														Generate Barcode
													</label>
												</div>
												<div class="fnt11">
													Order Status: 
													<span class="bold" ng-if="vm.medOrderData.orderStatusName==='Discontinued'">													
														<font color="red" ng-bind="vm.medOrderData.orderStatusName"></font>
													</span>
													<span ng-if="vm.medOrderData.orderStatusName!=='Discontinued'">
													  	<font ng-bind="vm.medOrderData.orderStatusName"></font>
													</span>													
												</div>
											</div>
										</div>
										<div class="col-sm-12 nopadding mt10 mb10">
											<div class="col-sm-5 nopadright">
												<div class="simpleTable">
													<div class="tablehead">
														<table class="table table-bordered">
															<thead>
																<tr>
																	<th>Order Details: (i)</th>
																	<th class="w20p">Order Dose</th>
																	<th class="w20p" ng-if="vm.medOrderData.orderType != 'Medication'">Order Dispense</th>
																</tr>
															</thead>
														</table>
													</div>
													<div class="tablebody table-scroll-hgt complex-iv">
														<table class="table table-bordered">
															<tbody>
																<tr ng-if="vm.medOrderData.orderType != 'Medication'" ng-repeat="item in vm.leftMedOrderDetailList">
																	<td class="wrap-txt">{{item.itemName}} {{item.orderStrength}} {{item.orderFormulation}}</td>
																	<td class="w20p">{{item.orderDose}} {{item.formularyDoseUOM}}</td>
																	<td class="w20p" ng-if="vm.medOrderData.orderType != 'Medication'">{{item.dispense}} {{item.formularyDispenseUOM}}</td>
																</tr>
																<tr ng-if="vm.medOrderData.orderType == 'Medication'">
																	<td class="wrap-txt">
																		<span ng-bind="vm.medOrderData.brandName"></span>&nbsp;
																		<span ng-bind="vm.medOrderData.medOrderDetailList[0].orderStrength"></span>
																		<span ng-bind="vm.medOrderData.medOrderDetailList[0].orderFormulation"></span>																		
																	</td>
																	<td class="w20p feild-bg">{{vm.medOrderData.dose}} {{vm.medOrderData.doseUnit}} <span ng-if="vm.medOrderData.ISSFlag===true" class="pull-right" style="display:-webkit-inline-box;"><insulin-scale-view show-icon="true" iss-json-data="vm.medOrderData.issTemplateData.doseData"></insulin-scale-view></span></td>
																</tr>
																
															</tbody>
														</table>
													</div>
													<div class="tablebody complex-iv">
														<table class="table table-bordered">
															<tbody>
																<tr>
																	<td colspan="2" style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Dose: {{vm.medOrderData.dose}} {{vm.medOrderData.doseUnit}}</b></td>
																</tr>
																<tr ng-if="vm.medOrderData.totalVolume > 0">
																	<td style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Volume: {{vm.medOrderData.totalVolume}} {{vm.totalVolumeUnit}}</b></td>
																	<td style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Dispensed Volume: {{vm.totalDispensedVolumeOrg}} {{vm.totalVolumeUnit}}</b></td>
																</tr>
															</tbody>
														</table>
													</div>
												</div>
											</div>
											<div class="col-sm-7">
												<div class="simpleTable">
													<div class="tablehead">
														<table class="table table-bordered">
															<thead>
																<tr>
																	<th><span class="pull-left">Product</span> <i class="dispense icon icon-edit pull-right mt8" ng-show="vm.medOrderData.discontinued == false" ng-click="vm.openEditDispense();"></i></th>
																	<th class="w15p">Order Dose</th>
																	<th class="w15p">Order Dispense</th>
																	<th class="w15p">Actual Dispense</th>
																	<th class="w10p">Cost($)</th>
																	<th class="w15p">Charge Code</th>
																</tr>
															</thead>
														</table>
													</div>
													<div class="tablebody table-scroll-hgt complex-iv">
														<table class="table table-bordered">
															<tbody>
																<tr ng-repeat="item in vm.medOrderData.medOrderDetailList">
																	<td class="wrap-txt">
																		<span ng-bind="item.genericName"></span>
																		<span ng-if="item.brandName !=''"> ( <span ng-bind="item.brandName"></span> ) </span>
																		<span ng-bind="item.orderStrength"></span>
																		<span ng-bind="item.orderFormulation"></span>													
																	</td>
																	<td class="w15p"><span ng-bind="item.orderDose"></span>&nbsp;<span ng-bind="item.formularyDoseUOM"></span></td>
																	<td class="w15p feild-bg"><span ng-bind="item.dispense"></span>&nbsp;<span ng-bind="item.formularyDispenseUOM"></span></td>
																	<td class="w15p" style="display:none"><span ng-bind="item.dispenseQty"></span>&nbsp;<span ng-bind="item.formularyDispenseSize"></span>&nbsp;<span ng-bind="item.formularyDispenseUOM"></span></td>
																	<td class="w15p"><span ng-bind="item.actualDispense"></span>&nbsp;<span ng-bind="item.formularyDispenseUOM"></span></td>
																	<td class="w10p">$ <span ng-bind="item.totalCostCalculatedWithChargeType"></span><!-- <i class="icon icon-info2 pull-right mt3" ng-mouseover="vm.setMedInfo(item);"></i> --></td>
																	<td class="w15p"><span ng-bind="item.chargeCode"></span></td>
																</tr>
															</tbody>
														</table>
													</div>
													<div class="tablebody complex-iv">
														<table class="table table-bordered">
															<tbody>
																<tr>
																	<td  style="width:50%;background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Dose: {{vm.totalDose}} {{vm.medOrderData.doseUnit}}</b></td>
																	<td  style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Cost: $ {{vm.medOrderData.totalPrice}}</b></td>
																</tr>
																<tr ng-if="vm.totalVolume > 0 || vm.totalDispensedVolume > 0">
																	<td  style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Volume: {{vm.totalVolume}} {{vm.totalVolumeUnit}}</b></td>
																	<td  style="background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Dispensed Volume: {{vm.totalDispensedVolume}} {{vm.totalVolumeUnit}}</b></td>
																	
																</tr>
															</tbody>
														</table>
													</div>
												</div>
											</div>
										</div>
										<div class="col-sm-12" style="height:auto;">
											<div class="det-view brdrtop">
												<div class="col-sm-2 nopadding">
													<div class="form-group">
														<label class="col-sm-3 nopadding control-label text-left label-bold">Route</label>
														<div class="col-sm-9 nopadding textwrap-medprofile" ng-bind="vm.medOrderData.route"></div>
													</div>
												</div>
												<div class="col-sm-2 nopadding">
													<div class="form-group">
														<label class="col-sm-5 nopadding control-label label-bold">Frequency</label>
														<div class="col-sm-7 nopadding textwrap-medprofile" title="{{vm.medOrderData.frequency}}" ng-bind="vm.medOrderData.frequency"></div>
													</div>
												</div>
												<div class="col-sm-1 nopadding ratefield" ng-if="vm.medOrderData.orderType != 'Medication'">
													<div class="form-group">
														<label class="col-sm-6 nopadding control-label label-bold">Rate</label>
														<div class="col-sm-6 nopadding textwrap-medprofile">
															<span ng-bind="vm.medOrderData.ivRate"></span>&nbsp;<span ng-bind="vm.medOrderData.ivRateUOM"></span>															
														</div>
													</div>
												</div>																							
												<div class="col-sm-1 nopadding ratefield" ng-if="vm.medOrderData.orderType == 'Complex'">
													<div class="form-group">
														<label class="col-sm-10 nopadding control-label label-bold">Titration</label>
														<div class="nopadding">
															<input type="checkbox" class="mr5" ng-disabled="true" ng-model="vm.medOrderData.titrationAllowed" ng-checked="vm.medOrderData.titrationAllowed == true">
														</div>
													</div>
												</div>
												<div class="col-sm-3 nopadding">
													<div class="form-group">
														<label class="col-sm-8 nopadding control-label label-bold">PRN Indication</label>
														<div class="col-sm-4 nopadding textwrap-medprofile" ng-bind="vm.medOrderData.prnIndication"></div>
													</div>
												</div>	
												<div class="col-sm-3 nopadding">
													<div class="form-group">
														<label class="col-sm-8 nopadding control-label label-bold">Indication</label>
														<div class="col-sm-4 nopadding textwrap-medprofile"></div>
													</div>
												</div>
											</div>
										</div>
										<div class="col-sm-12 greybg-top mt10">
											<div class="col-sm-2 nopadright brderirght width-175">
												<div class="simpleTable schedule-table">
													<div class="tablehead">
														<table class="table table-bordered">
															<thead>
																<tr>
																	<th>Schedule</th>
																</tr>
															</thead>
														</table>
													</div>
													<div class="tablebody schedule-table-scroll">
														<table class="table table-bordered">
															<tbody>
																<tr ng-repeat="schedule in vm.medOrderData.frequencySchedule track by $index">
																	<td class="feild-bg">
																		<div class="det-view nopadding brdrright">
																			<div class="pull-right addschedule">
																				<div class="form-group nomargin clearfix">
																					<div class="input-group input-group-sm">
																						<input type="text" class="form-control global-time-hm nobordR clearable schedule_{{schedule.id}}" ng-model="schedule.scheduledTime" ng-readonly="vm.medOrderData.discontinued"> 
																						<span class="input-group-addon linehgt21 nobrdr" ng-show="vm.medOrderData.discontinued == false"><i class="icon time-icon" ng-click="vm.showTimePicker(schedule.id)"></i></span>
																					</div>
																				</div>
																			</div>
																		</div>
																	</td>
																</tr>
																<tr ng-if="vm.medOrderData.frequencySchedule.length == 0">
																<td style="white-space:initial;">
																	Frequency is {{vm.medOrderData.frequency}}. <span style="color:red">(Schedule Not Found)</span>
																</td>
																</tr>
															</tbody>
														</table>
													</div>
												</div>
											</div>
											<div class="col-sm-10 width-1124">
												<div class="det-view nopadding">
													<div class="col-sm-12 nopadding">
														<div class="col-sm-5 nopadding">
															<div class="form-group clearfix">
																<label class="pull-left control-label nopadding label-bold">Start Date &amp; Time</label>
																<div class="col-sm-7 nopadding">
																	{{vm.medOrderData.startDateTime}}
																</div>
															</div>
														</div>
														<div class="col-sm-4 nopadding">
															<div class="form-group">
																<label class="col-sm-6 control-label nopadding label-bold">Stop Date &amp; Time</label>
																<div class="col-sm-6 nopadding">
																	{{vm.medOrderData.stopDateTime}}
																</div>
															</div>
														</div>
														<div class="col-sm-3 nopadding">
															<div class="form-group">
																<label class="col-sm-4 control-label nopadding label-bold">Duration</label>
																<div class="col-sm-3 nopadding">
																	<!-- <input type="text" class="form-control clearable"> -->
																	{{vm.medOrderData.duration}} {{vm.medOrderData.durationUnit}}
																</div>
															</div>
														</div>
													</div>
													<div class="col-sm-12 nopadding brdrtop mt3 pt5 label-bold">
														<div class="col-sm-6 nopadding">
															<div class="form-group">
																<label class="col-sm-12 control-label text-left nopadding">Order Instructions</label>
																<div class="col-sm-12 nopadleft">
																	<textarea class="form-control txtarea-hgt78 fnt11" ng-model="vm.medOrderData.orderEntryInstruction">{{vm.medOrderData.orderEntryInstruction}}</textarea>
																</div>
															</div>
														</div>
														<div class="col-sm-6 nopadding label-bold">
															<div class="form-group">
																<label class="col-sm-12 control-label text-left nopadding">Internal Notes</label>
																<div class="col-sm-12 nopadding">
																	<textarea class="form-control txtarea-hgt78 fnt11" ng-model="vm.medOrderData.internalNotes">{{vm.medOrderData.internalNotes}}</textarea>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="col-sm-12 pt6 pb7 grey-bg">
											<div class="col-sm-5">
												<button type="button" class="btn btn-lgrey btn-xs ml5 pull-right" ng-disabled="vm.securitySettings.PWQPrintLabelAccess != 1 || vm.medOrderData.discontinued || vm.medOrderData.pharmacyStatus != 2" ng-click="vm.showPrintLabel();">Print Label</button>
												<drug-monograph-component drug-name = "vm.medOrderData.drugName" drug-id = "vm.rxInfoObj.id" drug-id-type = "vm.rxInfoObj.idType" patient-id = "{{vm.patientId}}" title = "Drug Monograph"> </drug-monograph-component>
												<rx-education-component drug-desc = "vm.medOrderData.drugName" drug-id = "vm.rxInfoObj.id" drug-id-type = "vm.rxInfoObj.idType" patient-id = "{{vm.patientId}}" title = "Rx Education"> </rx-education-component> 
												<button type="button" class="btn btn-lgrey btn-xs ml5" id="logsDetials" ng-click="vm.showDevAlert();">Logs</button>
												<!-- <button type="button" class="btn btn-lgrey btn-xs ml5">Link</button> -->
												<button type="button" class="btn btn-lgrey btn-xs ml5" id="vitals-labs">Vitals</button>
											</div>										
										</div>
									</div>
									<div role="tabpanel" class="tab-pane" id="medOrder-tab2">
										<div class="padlr10 clearfix">
											<div class="col-sm-12 nopadding" ng-if="vm.reflexAlerts.length == 0">
												<h4 class="green-header" style="color:red;">No Reflex Orders found for {{vm.medOrderData.drugName}} </h4>
											</div>
											<div class="col-sm-12 nopadding" ng-if="vm.reflexAlerts.length > 0">
												<h4 class="green-header">{{vm.medOrderData.drugName}} reflexed below orders</h4>
												<div class="col-sm-12 nopadding clearfix">
													<div class="simpleTable">
														<div class="tablehead">
															<table class="table table-bordered">
																<thead>
																	<tr>
																		<th class="w40p">Orders</th>
																		<th class="w40p">Details</th>
																		<th class="w40p">Action</th>
																	</tr>
																</thead>
															</table>
														</div>
														<div class="tablebody">
															<table class="table table-bordered">
																<tbody>
																	<tr ng-repeat="reflexAlert in vm.reflexAlerts">
																		<td class="w40p">{{reflexAlert.itemName}} <span class="txtred">*</span></td>
																		<td class="w40p">{{reflexAlert.freqCode}}</td>
																		<td class="w40p">{{reflexAlert.action}}</td>
																	</tr>
																</tbody>
															</table>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
											</div>
										</div>
									</div>
									
									<div role="tabpanel" class="tab-pane" id="medOrder-tab3">
                                        <div class="col-sm-8 nopadding">
                                            <div class="simpleTable mt10" ng-repeat="pcaDetails in vm.medOrderData.medOrderDetailList" ng-if="pcaDetails.PCA">
                                                <div class="tablehead">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th colspan="4">{{pcaDetails.itemName}}
                                                                    <button class="pull-right dark-grey-btn mt6" type="button" ng-show="vm.medOrderData.drugTypeBulk">Bulk</button>
                                                                    <i class="icon icon-schedule-change hide pull-right mt7 mr10"></i>
                                                                    <i class="icon icon-dual-verification hide pull-right mt7 mr10"></i>
                                                                </th>
                                                            </tr>
                                                        </thead>
                                                    </table>
                                                </div>
                                                <div class="tablebody medctn-order-table">
                                                    <table class="table table-bordered">
                                                        <tbody>
                                                            <tr>
                                                                <td colspan="4">
                                                                    <h4 class="green-header pull-left">PCA Dosage Details</h4>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="w125">Bolus<span class="txtred">*</span></td>
                                                                <td class="feild-bg">
                                                                    <div class="pull-left w50p brdrright" > {{pcaDetails.bolusLoadingDose}} </div>
                                                                    <div class="select-box w50p pull-left"> {{pcaDetails.bolusLoadingDoseUOM}}</div>
                                                                </td>
                                                                <td class="w125">Lockout Interval <span class="txtred">*</span></td>
                                                                <td class="feild-bg noborder-right">
                                                                    <div class="pull-left w50p brdrright"> {{pcaDetails.lockoutIntervalDose}} </div>
                                                                    <div class="select-box w50p pull-left"> {{pcaDetails.lockoutIntervalDoseUOM}}</div>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td class="w125">Intermittent Dose 
                                                                	<span class="txtred">*</span>
                                                                </td>
                                                                <td class="feild-bg">
                                                                    <div class="pull-left w50p brdrright" > {{pcaDetails.intermittenDose}} </div>
                                                                    <div class="select-box w50p pull-left"> {{pcaDetails.intermittenDoseUOM}}</div>
                                                                </td>
                                                                <td class="w125">4 Hour Limit<span class="txtred">*</span></td>
                                                                <td class="feild-bg noborder-right">
                                                                    <div class="pull-left w50p brdrright" > {{pcaDetails.fourHourLimit}} </div>
                                                                    <div class="select-box w50p pull-left"> {{pcaDetails.fourHourLimitUOM}}</div>
                                                                </td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
									
									<div role="tabpanel" class="tab-pane" id="lisinopril-stock">
										<div class="padlr10 clearfix">
											<div class="det-view">
												<div class="row">
													<div class="col-sm-4">
														<div class="form-group">
															<label for="" class="control-label col-sm-3 nopadleft mt3">Drug Alerts <span class="txtred">*</span></label>
															<div class="col-sm-9 nopadding">
																<input type="text" class="form-control">
															</div>
														</div>
													</div>
													<div class="col-sm-3 nopadleft">
														<div class="form-group">
															<label for="" class="control-label col-sm-3 mt3 nopadleft">PA Value </label>
															<div class="col-sm-5 nopadleft">
																<input type="text" class="form-control" value="35">
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-12 mt10">
														<h4 class="green-header">Quantity Available</h4>
														<div class="form-group clearfix mt10">
															<div class="col-sm-2">
																<label for="" class="control-label col-sm-3 mt3 nopadleft">ER1
																</label>
																<div class="col-sm-9 nopadding pr5">
																	<input type="text" class="form-control" value="15">
																</div>
															</div>
															<div class="col-sm-2">
																<label for="" class="control-label col-sm-3 mt3 nopadleft">ER2</label>
																<div class="col-sm-9 nopadding pr5">
																	<input type="text" class="form-control" value="15">
																</div>
															</div>
														</div>
														<div class="form-group clearfix mt10">
															<div class="col-sm-2">
																<label for="" class="control-label col-sm-3 mt3 nopadleft">QR1</label>
																<div class="col-sm-9 nopadding pr5">
																	<input type="text" class="form-control" value="15">
																</div>
															</div>
															<div class="col-sm-2">
																<label for="" class="control-label col-sm-3 mt3 nopadleft">MS1</label>
																<div class="col-sm-9 nopadding pr5">
																	<input type="text" class="form-control" value="15">
																</div>
															</div>
														</div>
														<div class="form-group clearfix mt10">
															<div class="col-sm-2">
																<label for="" class="control-label col-sm-3 mt3 nopadleft">L&amp;D</label>
																<div class="col-sm-9 nopadding pr5">
																	<input type="text" class="form-control" value="15">
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="vital-tab-right-panel visible">
						<div class="col-sm-12 nopadding hgt546">
							<h4 class="green-header mb5 pl10">Vitals</h4>
							<div class="col-sm-12 nopadding">
								<div class="simpleTable mt10">
									<div class="tablebody" perfect-scrollbar style='height:509px;'>
										<table class="table table-bordered">
                                        <tbody>
                                            <tr ng-repeat="vital in vm.vitals">
                                                <td class="w40p">
                                                	{{vital.itemName}}
                                                	<span class="txtred" ng-if="vital.isMandatory == true">*</span>
                                                </td>
                                                <td class="w30p">{{vital.value}}</td>
                                                <td class="w30p">{{vital.reportedTime}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
									</div>
								</div>
							</div>
							<div class="clearfix"></div>							
						</div>
						<div class="col-sm-12 pt10 pb10 brdrtop2">
							<div class="pull-left">
								<div class="doc-att mt2">
									<label class="fntblde">
	                                    <input type="checkbox" ng-model="vm.showMandatoryPreReq" ng-checked = "vm.showMandatoryPreReq == true" ng-click="vm.getPreRequisites()"> <span class="fnt11">Show Mandatory Only</span>
	                                </label>
								</div>
							</div>
							<div class="pull-right">
								<button type="button" class="btn btn-blue btn-xs ml5 close-vital-lab">OK</button>
								<button type="button" class="btn btn-lgrey btn-xs close-vital-lab">Cancel</button>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer nomargin ref_foot">
					<div class="pull-left col-sm-6 det-view nopadding">
						<div class="form-group nomargin">
							<label class="col-sm-1 control-label nopadding mt3" ng-show="vm.batchesList.length > 0">Batch</label>
							<div class="col-sm-4 nopadding " ng-show="vm.batchesList.length > 0">
								<div class="select-box nopadding drop-up">
									<b class="caret selection-click drop-up"></b>
									<span class="selection-field selection-click drop-up">{{vm.batchesList.length == 1 ? vm.batchesList[0].batchName : ""}}</span>
									<ul class="sel-optn selection-options">
										 <li ng-repeat="batch in vm.batchesList">{{batch.batchName}}</li>
									</ul>
								</div>
							</div>
							<div class="dropup payment-dropup pull-left mr5 ml10">
								<div class="btn-group pull-left">
									<button type="button" class="btn btn-default btn-lgrey btn-xs intervantion" ng-click="vm.showInterventionPopUp('Edit')" id="red-intervantion">Interventions</button>
									<button type="button" class="btn btn-default dropdown-toggle btn-lgrey btn-xs" id="addBtn" data-toggle="dropdown" aria-expanded="false">
										<span class="caret"></span>
									</button>
									<ul class="dropdown-menu w190">
										<li class="hlight"><a href="#" id="add-interven" ng-click="vm.showInterventionPopUp('Add')">Add/Create New Intervention</a></li>
									</ul>
								</div>
							</div>
						</div>
						<button type="button" class="btn btn-lgrey btn-xs showbillbtn pull-left ml5" ng-click="vm.showBillingData();">Billing Data</button>
						
						<button type="button" class="btn btn-lgrey btn-xs showbillbtn pull-left ml5" ng-click="vm.chargeBillingDataCapture();" ng-show="false" ng-disabled="vm.medOrderData.pharmacyStatus != 2" >Charge Capture</button>	
						
					</div>
					<div class="pull-right col-sm-6 nopadding">
						<div class="col-sm-12 nopadding">
							<button type="button" class="btn btn-lgrey btn-xs pull-right" id="closeOrders" data-dismiss="modal" ng-click="vm.closeMedOrders();">Close</button>
							<div class="dropup nopadleft pr10 keep-open pull-right">
								<button type="button" class="btn btn-lgrey btn-xs dropdown-toggle" data-toggle="dropdown" ng-disabled="vm.medOrderData.pharmacyStatus == 2 || vm.securitySettings.PWQMedicationOrderUpdateAccess != 1 || vm.medOrderData.discontinued" ng-click="vm.openPendingReasons();">Save &amp; Mark as Pending</button>
								<ul class="dropdown-menu savepending pull-right savepending-arrow" id="ulPendingReasons">
                                        <div class="col-sm-12 mt10">
                                            <div class="det-view clearfix">
                                                <div class="form-group nomargin clearfix">
                                                    <label class="col-sm-2 control-label nopadding mt3">Reason</label>
                                                    <div class="col-sm-10 nopadding">
                                                        <div class="input-group input-group-sm nopadding opaq-input">
                                                            <input type="text" class="form-control" ng-model="vm.selectedReason.reasonName">
                                                            <div class="input-group-btn dropdown" id="divReasonDropdown">
                                                                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
																	<i class="icon caret"></i>
                                                                </button>
                                                                <ul class="dropdown-menu pull-right">
                                                                	<li ng-repeat="row in vm.pendingReasonList" ng-click="vm.setReason(row)"><a href="#">{{row.reasonName}}</a></li>
                                                                </ul>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-sm-12 brdrtop2 pt10 pb10">
                                            <button type="button" class="btn btn-lgrey btn-xs pull-right close-savepending" ng-click="vm.closePendingReasons();">Cancel</button>
                                            <button type="button" class="btn btn-blue btn-xs pull-right mr5 close-savepending" ng-click="vm.saveAsPending();">OK</button>
                                        </div>
                                    </ul>
							</div>
							<button type="button" class="btn btn-blue btn-xs pull-right lisi-verified mr10" id="verify" ng-click="vm.verifyAndNext();" ng-disabled="vm.securitySettings.PWQMedicationOrderUpdateAccess != 1 || vm.securitySettings.PWQMedicationOrderVerifyAccess != 1">Verify and Next</button>
							<!-- <button type="button" class="btn btn-lgrey btn-xs pull-right mr10" id="openalert">Discontinue Order</button> -->
						</div>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
		</div>
		
		<div class="edit-products-popup">
			<div class="col-sm-12">
				<div class="simpleTable mb10">
					<div class="tablehead">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th class="w20p">Available Products</th>
									<th class="w10p">Dose Size</th>
									<th class="w10p">Dispense Size</th>
									<th class="w10p">Order Dispense</th>
									<th class="w10p">Order Dose</th>
									<th class="w10p">Qty</th>
									<th class="w5p">Cost($)</th>
									<th class="w10p">Total Cost($)</th>
									<th>Internal Notes</th>
								</tr>
							</thead>
						</table>
					</div>
					<div class="tablebody table-scroll-hgt complex-iv">
						<table class="table table-bordered">
							<tbody>
								<tr ng-repeat="item in vm.medOrderData.availableProducts">
									<td class="w20p text-left wrap-txt">
										<span ng-bind="item.genericName"></span>&nbsp;
										<span ng-if="item.brandName !=''">( <span ng-bind="item.brandName"></span> )</span>&nbsp;
										<span ng-bind="item.orderStrength"></span>&nbsp;
										<span ng-bind="item.orderFormulation"></span>
									</td>
									<td class="w10p"><span ng-bind="item.formularyDoseSize"></span>&nbsp;<span ng-bind="item.formularyDoseUOM"></span></td>
									<td class="w10p"><span ng-bind="item.formularyDispenseSize"></span>&nbsp;<span ng-bind="item.formularyDispenseUOM"></span></td>
									<td class="w10p feild-bg text-left">
										<div class="col-sm-12 nopadding">
											<div class="col-sm-8 nopadding">
												<div class="form-group nomargin clearfix">
													<div class="input-group input-group-sm">
														<input type="text" class="form-control noborder clearable" drug-size-validations="{'validate':true,'allowDecimal':true}" ng-model="item.dispense" ng-change="vm.calculateDispenseQty(item);vm.calculateTotalMarkUpCostWithDispense(item);">
													</div>
												</div>
											</div>
											<div class="col-sm-4">
												<label class="control-label"><span ng-bind="item.formularyDispenseUOM"></span></label>
											</div>
										</div>
									</td>
									<td class="w10p" ng-if="item.calculate == true"><span ng-if="item.orderDose != ''"><span ng-bind="item.orderDose"></span>&nbsp;<span ng-bind="item.formularyDoseUOM"></span></span></td>
									<td class="w10p feild-bg text-left" ng-if="item.calculate == false">
										<div class="col-sm-12 nopadding">
											<div class="col-sm-8 nopadding">
												<div class="form-group nomargin clearfix">
													<div class="input-group input-group-sm">
														<input type="text" class="form-control noborder clearable" drug-size-validations="{'validate':true,'allowDecimal':true}" ng-model="item.orderDose">
													</div>
												</div>
											</div>
											<div class="col-sm-4">
												<label class="control-label"><span ng-bind="item.formularyDoseUOM"></span></label>
											</div>
										</div>
									</td>
									<td class="w10p feild-bg text-left" style="vertical-align: middle">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													<input type="text" class="form-control noborder clearable" drug-size-validations="{'validate':true,'allowDecimal':true}" ng-model="item.dispenseQty" ng-change="vm.calculateDispense(item);vm.calculateTotalMarkUpCostWithDispense(item);">
												</div>
											</div>
										</div>
									</td>
									<td class="w5p">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													{{item.unitCostCalculatedWithChargeType}}
												</div>
											</div>
										</div>
									</td>
									<td class="w10p">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													{{item.totalCostCalculatedWithChargeType}}
												</div>
											</div>
										</div>
									</td>
									<td class="text-left wrap-txt">
										<div class="col-sm-12 nopadding">
											<div class="col-sm-10 nopadding">{{item.internalNotes}}</div>
											<div class="col-sm-2 nopadding" ng-if="item.internalNotes.length > 0"><img src="/mobiledoc/jsp/inpatientWeb/staticContent/img/admin-greynote.png" title="Copy eMAR Instructions to clip-board" ng-click="vm.copyEMARInst(item.internalNotes);"></div>
										</div>
									</td>
								</tr>
								
								<tr ng-if="vm.medOrderData.availableDiluents.length > 0">
									<td colspan="9" class="text-left" style="background-color: #dcdcdc"><b>Available Diluents /Additives</b></td>
								</tr>
							
								<tr ng-repeat="item in vm.medOrderData.availableDiluents">
									<td class="w20p text-left wrap-txt">
										<span ng-bind="item.genericName"></span>
										<span ng-if="item.brandName !=''">( <span ng-bind="item.brandName"></span> )</span>&nbsp;
										<span ng-bind="item.orderStrength"></span>&nbsp;
										<span ng-bind="item.orderFormulation"></span>
									</td>
									<td class="w10p"><span ng-bind="item.formularyDoseSize"></span>&nbsp;<span ng-bind="item.formularyDoseUOM"></span></td>
									<td class="w10p"><span ng-bind="item.formularyDispenseSize"></span>&nbsp;<span ng-bind="item.formularyDispenseUOM"></span></td>
									<td class="w10p feild-bg text-left" style="vertical-align: middle;">
										<div class="col-sm-12 nopadding">
											<div class="col-sm-8 nopadding">
												<div class="form-group nomargin clearfix">
													<div class="input-group input-group-sm">
														<input type="text" drug-size-validations="{'validate':true,'allowDecimal':true}" class="form-control noborder clearable" ng-model="item.dispense" ng-change="vm.calculateDispenseQty(item);vm.calculateTotalMarkUpCostWithDispense(item);">
													</div>
												</div>
											</div>
											<div class="col-sm-4">
												<label class="control-label">{{item.formularyDispenseUOM}}</label>
											</div>
										</div>	
										
									</td>
									<td class="w10p">{{item.dispense}} <span ng-if="item.dispense.length > 0">{{item.formularyDispenseUOM}}</span></td>
									<td class="w10p feild-bg text-left" style="vertical-align: middle">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													<input type="text" class="form-control noborder clearable" drug-size-validations="{'validate':true,'allowDecimal':true}" ng-model="item.dispenseQty" ng-change="vm.calculateDispense(item);vm.calculateTotalMarkUpCostWithDispense(item);">
												</div>
											</div>
										</div>
									</td>
									<td class="w5p">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													{{item.unitCostCalculatedWithChargeType}}
												</div>
											</div>
										</div>
									</td>
									<td class="w10p">
										<div class="det-view nopadding">
											<div class="form-group nomargin clearfix">
												<div class="input-group input-group-sm">
													{{item.totalCostCalculatedWithChargeType}}
												</div>
											</div>
										</div>
									</td>
									<td class="text-left wrap-txt">
										<div class="col-sm-12 nopadding">
											<div class="col-sm-10 nopadding">{{item.internalNotes}}</div>
											<div class="col-sm-2 nopadding" ng-if="item.internalNotes.length > 0"><img src="/mobiledoc/jsp/inpatientWeb/staticContent/img/admin-greynote.png" title="Copy eMAR Instructions to clip-board" ng-click="vm.copyEMARInst(item.internalNotes);"></div>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					
					<div class="tablehead">
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td colspan="9" style="text-align:left;background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Dose: {{vm.medOrderData.dose}} {{vm.medOrderData.doseUnit}}</b></td>
								</tr>
								<tr ng-if="vm.totalVolume > 0 || vm.totalDispensedVolume > 0">
									<td colspan="5" style="text-align:left;background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Ordered Volume: {{vm.medOrderData.totalVolume}} {{vm.totalVolumeUnit}}</b></td>
									<td colspan="4" style="text-align:left;background-color: #dcdcdc;border:1px solid #c0bfbf;"><b>Total Dispensed Volume: {{vm.totalDispensedVolume}} {{vm.totalVolumeUnit}}</b></td>
								</tr>
							</tbody>
						</table>
					</div>

				</div>
			</div>
			<div class="clearfix"></div>
			<div class="popup-footer clearfix">
				<button type="button" class="btn btn-lgrey btn-xs pull-right close-btn" ng-click="vm.closeEditDispense();">Cancel</button>
				<button type="button" class="btn btn-blue btn-xs pull-right mr5 close-btn" ng-click="vm.updateDispense();">OK</button>
			</div>
		</div>
		
		<div class="modal fade" id="showBillingDataModal" role="dialog" data-backdrop="false">
			<div class="modal-dialog">
				<div class="modal-content global-property">
					<div class="modal-header clearfix">
						<button type="button" class="close" ng-click="vm.hideBillingData();">&times;</button>
						<h4 class="modal-title" id="myModalLabel">Billing Data</h4>
						<span class="fnt11 ml10 mt7 txtwhite pull-left"><!--  (Sean Ramos, MD - 10/16/2015 08:30:00, NP)  --></span>
					</div>
					<div class="modal-body grey-bg hgt590">
						<div class="col-sm-12">
							<div cpt-list episode-id="vm.medOrderData.episodeEncounterId" encounter-id="vm.medOrderData.encounterId" episode-Type="vm.medOrderData.episodeType" facility-id="vm.medOrderData.facilityId" encounter-type-id="vm.medOrderData.encounterType" 
								servicetype-id="vm.medOrderData.encServiceType"  practice-id="vm.medOrderData.encPracticeId"  dept-id="vm.medOrderData.encDeptId" patienttype-id="vm.medOrderData.encPatientType" allcolumns="true" list-Height="400" >
							</div>
						</div>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
		</div>
		
		<div class="assigned-to-tooltip dispenseinfo dispenseinfo-arrow" style="padding:5px;" >
	    	<div class="det-view">
		    	<div class="form-group clearfix">
			        <div class="col-sm-12 pt5 pb5 nopadding">
			            <eCW:staffLookup staffName="vm.selectedAssignedTo"
							staffClick="vm.setAssignedTo()" filterPosition="right"
							placeHolder="Staff Name" topclass="staffLookup"
							clearStaffData="vm.clearAssignedTo()">
						</eCW:staffLookup>
			        </div>
		        </div>
	        </div>
		</div>
    
    	<div class="proceed-prompt collection-prompt" id="medOrderConcPrompt" style="display:none;">
			<div class="col-sm-12 nopadding h100p">
				<div class="left-prompt col-sm-2">
					<i class="icon icon-alert-info"></i>
				</div>
				<div class="prompt-message text-center col-sm-10 nopadright">
					<p class=" msg-txt">{{vm.concurrentUserLog.userName}} is currently accessing this order. Do you want to continue?
					</p>
					<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center yes-collection" ng-click="vm.getMedOrder()">Yes</button>
					<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center" ng-click="vm.hideConcPrompt()">No</button>
				</div>
			</div>
		</div>
		<!-- Start MedOrder Logs popup -->
		<div class="modal fade" id="logsModal" role="dialogs" data-backdrop="false"> 
			<div class="modal-dialog large_width">
			   <div class="modal-content global-property">
			       <div class="modal-header clearfix">
			           <button type="button" class="close" ng-click="vm.closeLogs();" aria-hidden="true">&times;</button>
			           <h4 class="modal-title">Logs</h4>
			       </div>
			       <div class="modal-body hgt546">
			           <div class="container-fluid">
			               <div class="row">
			                   <div class="col-sm-12 mt10">
			                       <div class="simpleTable">
			                           <div class="tablehead">
			                               <table class="table table-bordered">
			                                   <thead>
			                                       <tr>
			                                           <th class="w320">Order</th>                                           
			                                           <th class="w110">Ordering Provider</th>
			                                           <th class="w115">Order Date/Time</th>
			                                           <th class="w115">Start Date/Time</th>
			                                           <th class="w110">Stop Date/Time</th>
			                                           <th class="w140">Verified By</th>
			                                           <th class="w120 nowrap">Verified Date/Time</th>
			                                       </tr>
			                                   </thead>
			                               </table>
			                           </div>
			                           <div class="tablebody" id="logsDetailTable" perfect-scrollbar style='height:508px;'>
			                               <table class="table table-bordered">
			                                   <tbody>
			                                       <tr ng-repeat="item in vm.medicationOrderLogsArray">
			                                           <td class="w320" ng-bind="item.drugName"></td>                                           
			                                           <td class="w110" ng-bind="item.orderingProviderName"></td>
			                                           <td class="w115" ng-bind="item.orderDateTime"></td>
			                                           <td class="w115" ng-bind="item.startDateTime"></td>
			                                           <td class="w110" ng-bind="item.stopDateTime"></td>
			                                           <td class="w140" ng-bind="item.verifiedBy"></td>
			                                           <td class="w120" ng-bind="item.verifiedDateTime"></td>
			                                       </tr>                                                                             
			                                   </tbody>
			                               </table>
			                           </div>
			                       </div>
			                   </div>
			               </div>
			           </div>
			       </div>
			       <div class="modal-footer grey-bg">
			           <button type="button" class="btn btn-lgrey btn-xs pull-right ml5" ng-click="vm.closeLogs();">Cancel</button>
			           <button type="button" class="btn btn-blue btn-xs pull-right" ng-click="vm.closeLogs();">OK</button>
			       </div>
			   </div>  
			</div>
		</div>	
		<!-- End MedOrder Logs Popup -->
		<!--Start print log modal   -->
		<div class="modal fade" id="printModal" role="dialogs" data-backdrop="false"> 
			<div class="modal-dialog small_wdth w295">
			   <div class="modal-content global-property">
			       <div class="modal-header clearfix">
			           	<button type="button" class="close" ng-click="vm.hidePrintLabel();">&times;</button>
			           <h4 class="modal-title">Printing Label</h4>
			       </div>
			       <div class="modal-body hgt95 ">
			           <div class="container-fluid">
			               <div class="row">
			                   <div class="col-sm-12 mt10">
			                       <div class="form-group mb10">
										<label class="control-label mt3 col-sm-8">Please specify number of copies to be printed:</label>
										<div class="col-sm-2 nopadding mt6">
											<input type="text" id="noOfLabel" class="form-control srch-function clearable" only-numbers="{'allowNegative':false,'allowDecimal':false}" ng-model="vm.numberOfPrint" maxlength="2" autocomplete="off">
										</div>
								   </div>
			                   </div>
			               </div>
			           </div>
			       </div>
			       <div class="modal-footer grey-bg brdrtop2">
						<div class="pull-right">
							<button type="button" class="btn btn-lgrey btn-xs pull-left"  ng-click="vm.setPrintData();">Print</b></button>
							<button type="button" class="btn btn-blue btn-xs" ng-click="vm.hidePrintLabel();">Close</button>
						</div>
				   </div>
			   </div>  
			</div>
		</div>
		<div class="info-tooptip info-tooptip-arrow">
		    <div class="col-sm-12 padtb10 pull-left">
		        <p class="fnt11">Unit  Cost: <span class="fntbld">$ {{vm.unitCost}}</span></p>
		        <p class="fnt11 pt5">Unit(s) Charged: <span class="fntbld">{{vm.unitCharged}}</span></p>
		    </div>
		</div>
		<!--End print log modal  -->
	</div>
</div>
<script type="text/javascript">
	$("#mediOrderModal").modal({
		animation: true,
		windowClass: 'large-modal',
		//backdrop: 'static',
		keyboard: false,
        cls: 'global',
	});
</script>