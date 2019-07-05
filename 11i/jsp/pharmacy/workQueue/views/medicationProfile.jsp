<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/in-medreconciliation-new.css">
<link href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/ph-medprofile.css" rel="stylesheet">
<script type="text/javascript">
var patientId = ${patientId};
var encounterId = ${encounterId};
var encounterType = ${encounterType};
var episodeEncounterId = ${episodeEncounterId};
</script>
<div class="container-fluid" ng-controller="medicationProfileCtrl as objMedProfile" ng-init="objMedProfile.init();">
    <div class="row">
        <div class="col-sm-8 brdrright2 left-section" perfect-scrollbar>
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="green-header mb10">Unverified and Pending Orders</h4>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <form class="form-horizontal">
                        <div class="det-view nopadding clearfix">
                            <div class="col-sm-3">
                                <div class="form-group">
                                    <label class="control-label nopadleft mt3 pull-left">Drug</label>
                                    <div class="col-sm-9 nopadding">
                                        <div class="input-group input-group-sm">
                                            <input type="text" class="form-control pl20 clearable" ng-model="objMedProfile.drugLookup">
                                            <i class="icon-Search searchicn"></i>
                                            <!-- <span class="input-group-btn">
											<button type="button" class="btn btn-default"><i class="icon_brows icon-browser"></i>
                                   </button>
								</span>-->
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-sm-5 nopadding">
                                <div class="form-group">
                                    <label class="control-label nopadleft mt3 col-md-4">Pharmacy Status</label>
                                    <div class="col-md-8 nopadding doc-att">
                                        <label class="radio-inline"><input type="radio" name="pharmacyStatus" ng-model="objMedProfile.pharmacyStatus" value="-1" ng-click="objMedProfile.getPendingMeds();">All</label>
                                        <label class="radio-inline"><input type="radio" name="pharmacyStatus" ng-model="objMedProfile.pharmacyStatus" value="1" ng-click="objMedProfile.getPendingMeds();">Pending</label>
                                        <label class="radio-inline"><input type="radio" name="pharmacyStatus" ng-model="objMedProfile.pharmacyStatus" value="3" ng-click="objMedProfile.getPendingMeds();">Unverified</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <div class="simpleTable oddrows">
                        <div class="tablehead">
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th class="w5p">Priority</th>
                                        <th class="w6p ph-status">Pharmacy Status</th>
                                        <th class="w6p ph-status">Order Status</th>
                                        <th class="w5p">Tag</th>
                                        <th class="w9p">Start Date &amp; Time</th>
                                        <th class="w15p">Order</th>
                                        <th class="w9p">Details</th>
                                        <th class="w9p">Ordering Provider</th>
                                        <!-- <th class="w2p"></th> -->
                                    </tr>
                                </thead>
                            </table>
                        </div>
                        <div class="tablebody">
                            <table class="table table-bordered reco-table nomargin">
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="header-td">
                                            <div>
                                                <a class="pull-left icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#medi-col-one"></a>
                                                <span class="fnt13 textblue pull-left">MEDICATIONS</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.allMedsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="6" class="nopadding">
                                            <div class="accordian-body collapse in" id="medi-col-one">
                                                <div class="tablebody med-profile-table">
                                                    <table class="table table-bordered">
                                                        <tbody>
                                                            <tr class="medi-orders" ng-repeat="row in objMedProfile.medProfileData.allMeds | orderBy:sortType:sortReverse | filter:objMedProfile.drugLookup" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, objMedProfile.pharmacyStatus);">
                                                                <td class="w5p">{{row.priorityName}}</td>
                                                                <td class="w6p">{{row.pharmacyStatus}}</td>
                                                                <td class="w6p textwrap-medprofile"><span ng-if="row.orderStatusName==='Discontinued'"><font color="red" ng-bind="row.orderStatusName"></font></span><span ng-if="row.orderStatusName!=='Discontinued'"><font ng-bind="row.orderStatusName"></font></span></td>
                                                                <td class="w5p" title="{{row.tag}}">{{row.tag == '' ? "Others" : row.tag}}</td>
                                                                <td class="w9p">{{row.startDateTime}}</td>
                                                                <td class="w15p textwrap-medprofile"><span class="pull-left" title="{{row.itemName}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}}</a></span>
                                                                				<i class="icon icon-pharmacy-interventions greenico ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Completed'"></i>
																				<i class="icon icon-pharmacy-interventions red ml3 mt3 pull-left" ng-if="row.interventionStatus == 'In Progress'"></i>
																				<i class="icon icon-pharmacy-interventions grey ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Pending'"></i>
                                                                				<i class="icon icon-info2 pull-right mt3" ng-mouseover="objMedProfile.setMedInfo(row);"></i>
                                                                </td>
                                                                <td class="w9p textwrap-medprofile"><span title="{{row.orderDetail}}">{{row.orderDetail}}</span></td>
                                                                <td class="w9p">{{row.orderingProviderName}}</td>
                                                                <!-- <td class="w2p text-center"></td> -->
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="6" class="header-td">
                                            <div>
                                                <a class="pull-left icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#medi-col-two"></a>
                                                <span class="fnt13 textblue pull-left">IV</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.allIVsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="6" class="nopadding">
                                            <div class="accordian-body collapse in" id="medi-col-two">
                                                <div class="tablebody med-profile-table">
                                                    <table class="table table-bordered">
                                                        <tbody>
                                                            <tr class="medi-orders" ng-repeat="row in objMedProfile.medProfileData.allIVs | orderBy:sortType:sortReverse | filter:objMedProfile.drugLookup" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, objMedProfile.pharmacyStatus);">
                                                                <td class="w5p">{{row.priorityName}}</td>
                                                                <td class="w6p">{{row.pharmacyStatus}}</td>
                                                                <td class="w6p textwrap-medprofile"><span ng-if="row.orderStatusName==='Discontinued'"><font color="red" ng-bind="row.orderStatusName"></font></span><span ng-if="row.orderStatusName!=='Discontinued'"><font ng-bind="row.orderStatusName"></font></span></td>
                                                                <td class="w5p">{{row.tag == '' ? "Others" : row.tag}}</td>
                                                                <td class="w9p">{{row.startDateTime}}</td>
                                                                <td class="w15p textwrap-medprofile"><span class="pull-left" title="{{row.itemName}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}}</a></span>
                                                                				<i class="icon icon-pharmacy-interventions greenico ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Completed'"></i>
																				<i class="icon icon-pharmacy-interventions red ml3 mt3 pull-left" ng-if="row.interventionStatus == 'In Progress'"></i>
																				<i class="icon icon-pharmacy-interventions grey ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Pending'"></i>
                                                                				<i class="icon icon-info2 pull-right mt3" ng-mouseover="objMedProfile.setMedInfo(row);"></i
                                                                </td>
                                                                <td class="w9p textwrap-medprofile"><span title="{{row.orderDetail}}">{{row.orderDetail}}</span></td>
                                                                <td class="w9p">{{row.orderingProviderName}}</td>
                                                                <!-- <td class="w2p text-center"></td> -->
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                                                        
                                    <tr>
                                        <td colspan="6" class="header-td">
                                            <div>
                                                <a class="pull-left icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#medi-col-three"></a>
                                                <span class="fnt13 textblue pull-left">COMPLEX ORDERS</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.allComplexIVsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="6" class="nopadding">
                                            <div class="accordian-body collapse in" id="medi-col-three">
                                                <div class="tablebody med-profile-table">
                                                    <table class="table table-bordered">
                                                        <tbody>
                                                            <tr class="medi-orders" ng-repeat="row in objMedProfile.medProfileData.allComplexIVs | orderBy:sortType:sortReverse | filter:objMedProfile.drugLookup" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, objMedProfile.pharmacyStatus);">
                                                                <td class="w5p">{{row.priorityName}}</td>
                                                                <td class="w6p">{{row.pharmacyStatus}}</td>
                                                                <td class="w6p textwrap-medprofile"><span ng-if="row.orderStatusName==='Discontinued'"><font color="red" ng-bind="row.orderStatusName"></font></span><span ng-if="row.orderStatusName!=='Discontinued'"><font ng-bind="row.orderStatusName"></font></span></td>
                                                                <td class="w5p">{{row.tag == '' ? "Others" : row.tag}}</td>
                                                                <td class="w9p">{{row.startDateTime}}</td>
                                                                <td class="w15p textwrap-medprofile"><span class="pull-left" title="{{row.itemName}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}}</a></span>
                                                                				<i class="icon icon-pharmacy-interventions greenico ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Completed'"></i>
																				<i class="icon icon-pharmacy-interventions red ml3 mt3 pull-left" ng-if="row.interventionStatus == 'In Progress'"></i>
																				<i class="icon icon-pharmacy-interventions grey ml3 mt3 pull-left" ng-if="row.interventionStatus == 'Pending'"></i>
                                                                				<i class="icon icon-info2 pull-right mt3" ng-mouseover="objMedProfile.setMedInfo(row);"></i>
                                                                </td>
                                                                <td class="w9p textwrap-medprofile"><span title="{{row.orderDetail}}">{{row.orderDetail}}</span></td>
                                                                <td class="w9p">{{row.orderingProviderName}}</td>
                                                                <!-- <td class="w2p text-center"></td> -->
                                                            </tr>
                                                        </tbody>
                                                    </table>
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
        </div>
        <div class="col-sm-4 markorders" perfect-scrollbar>
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <h4 class="green-header nomarbtm mt15">Verified Orders</h4>
                    </div>
                    <div class="pull-right">
                    	<button type="button" class="btn btn-blue btn-xs mt7 pull-right" ng-click="objMedProfile.openPharmacyWorkQueue();">Pharmacy Work Queue</button>
                    	<button type="button" class="btn btn-lgrey btn-xs mt7 pull-right mr5" ng-click="objMedProfile.loadNewOrdersPopUp();">New Orders</button>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 mt5">
                    <div class="simpleTable">
                        <div class="tablehead">
                            <table class="table table-bordered">
                                <thead>
                                    <th class="w50p">Name</th>
                                    <th class="w18p">Order Status</th>
                                    <th class="w18p">Start Date</th>
                                    <th class="w18p">End Date</th>
                                    <!-- <th class="w6p"></th> -->
                                </thead>
                            </table>
                        </div>
                        <div class="tablebody" id="verifiedOrdersTable">
                            <table class="table table-bordered reco-table nomargin">
                                <tbody>
                                    <tr>
                                        <td colspan="4" class="header-td">
                                            <div>
                                                <a class="pull-left  icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#verify-section1"></a>
                                                <span class="fnt13 textblue mr30 pull-left">MEDICATIONS</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.verifiedMedsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="4">
                                            <div class="accordian-body collapse in" id="verify-section1">
                                                <table class="table table-bordered admit-innertable">
                                                    <tbody>
                                                        <tr class="sub-row" ng-repeat="row in objMedProfile.medProfileData.verifiedMeds | orderBy:sortType:sortReverse" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, 2);">
                                                            <td class="w50p textwrap-medprofile"><span title="{{row.itemName}} {{row.orderDetail}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}} {{row.orderDetail}}</a></span></td>
                                                            <td class="w18p textwrap-medprofile">{{row.orderStatusName}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.startDateTime}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.endDateTime}}</td>
                                                            <!-- <td class="w6p text-center"></td> -->
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="4" class="header-td">
                                            <div>
                                                <a class="pull-left  icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#verify-section4"></a>
                                                <span class="fnt13 textblue mr30 pull-left">IV</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.verifiedIVsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="4">
                                            <div class="accordian-body collapse in" id="verify-section4">
                                                <table class="table table-bordered admit-innertable">
                                                	<tbody>
                                                        <tr class="sub-row" ng-repeat="row in objMedProfile.medProfileData.verifiedIVs | orderBy:sortType:sortReverse" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, 2);">
                                                            <td class="w50p textwrap-medprofile"><span title="{{row.itemName}} {{row.orderDetail}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}} {{row.orderDetail}}</a></span></td>
                                                            <td class="w18p textwrap-medprofile">{{row.orderStatusName}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.startDateTime}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.endDateTime}}</td>
                                                            <!-- <td class="w6p text-center"></td> -->
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    
                                    
                                    <tr>
                                        <td colspan="4" class="header-td">
                                            <div>
                                                <a class="pull-left  icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#verify-section5"></a>
                                                <span class="fnt13 textblue mr30 pull-left">COMPLEX ORDERS</span>
                                                <span class="green-span pull-right">{{objMedProfile.medProfileData.verifiedComplexIVsCount}}</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="4">
                                            <div class="accordian-body collapse in" id="verify-section5">
                                                <table class="table table-bordered admit-innertable">
                                                    <tbody>
                                                        <tr class="sub-row" ng-repeat="row in objMedProfile.medProfileData.verifiedComplexIVs | orderBy:sortType:sortReverse" ng-class-even="'even'" ng-class-odd="'odd'" ng-class="{'highlight-row': row.selected}" ng-click="objMedProfile.openMedOrdersPopup(row.orderId, 2);">
                                                            <td class="w50p textwrap-medprofile"><span title="{{row.itemName}} {{row.orderDetail}}"><a class="anchor {{row.orderStatusName}}">{{row.itemName}} {{row.orderDetail}}</a></span></td>
                                                            <td class="w18p textwrap-medprofile">{{row.orderStatusName}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.startDateTime}}</td>
                                                            <td class="w18p textwrap-medprofile">{{row.endDateTime}}</td>
                                                            <!-- <td class="w6p text-center"></td> -->
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 mt10">
                    <div class="pull-left">
                        <h4 class="green-header nomarbtm mt15">Active Orders</h4>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 mt5">
                    <div class="simpleTable">
                        <div class="tablehead">
                            <table class="table table-bordered">
                                <thead>
                                    <th class="w50p">Name</th>
                                    <th class="w18p">Start Date</th>
                                    <th class="w18p">End Date</th>
                                    <th class="w6p"></th>
                                </thead>
                            </table>
                        </div>
                        <div class="tablebody" id="activeOrdersTable">
                            <table class="table table-bordered reco-table nomargin">
                                <tbody>

                                    <tr>
                                        <td colspan="4" class="header-td">
                                            <div>
                                                <a class="pull-left  icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#discharge-section2"></a>
                                                <span class="fnt13 textblue mr30 pull-left">LABS</span>
                                                <span class="green-span pull-right">01</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="4">
                                            <div class="accordian-body collapse in" id="discharge-section2">
                                                <table class="table table-bordered admit-innertable">
                                                    <tbody>
                                                        <!-- <tr class="sub-row">
                                                            <td class="w50p"><span class="pull-left">WBC (4-12)</span><span class="pull-right badge grey mt2">New</span></td>
                                                            <td class="w18p">10/16/2015</td>
                                                            <td class="w18p">10/16/2015</td>
                                                            <td class="w6p text-center"><i class="icon icon-edit"></i></td>
                                                        </tr> -->
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="4" class="header-td">
                                            <div>
                                                <a class="pull-left  icon icon-panright accordion-toggle mt3 mr10" data-toggle="collapse" data-target="#discharge-section3"></a>
                                                <span class="fnt13 textblue mr30 pull-left">NURSING</span>
                                                <span class="green-span pull-right">01</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="hidden-row">
                                        <td colspan="4">
                                            <div class="accordian-body collapse in" id="discharge-section3">
                                                <table class="table table-bordered admit-innertable">
                                                    <tbody>
                                                        <!-- <tr class="sub-row">
                                                            <td class="w50p"><span class="pull-left">Heart Rate (BPM)</span><span class="pull-right badge grey mt2">New</span></td>
                                                            <td class="w18p">10/16/2015</td>
                                                            <td class="w18p">10/16/2015</td>
                                                            <td class="w6p text-center"><i class="icon icon-edit"></i></td>
                                                        </tr> -->
                                                       
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12 grey-footer-ph white-bg brdrtop2">        	
            <div class="pull-left">
                <button type="button" class="btn btn-lgrey btn-xs mr5">Scan Document</button>
                <print-button unique-id="printmedicationprofile"  print-label="Print"  before-print="objMedProfile.setPrintData" show-preview="{{objMedProfile.printOptions.showPreview}}" print-data-destination="objMedProfile.printOptions.dataDestination" format="objMedProfile.printOptions.format" module="Pharmacy" plain-data="objMedProfile.printOptions.Htmldata"></print-button>
            </div>
            <div class="pull-right">
                <i class="icon icon-pharmacy-interventions grey mt2 mr3"></i>
                <span class="fnt11 fntw700 mt5">Intervention</span>
            </div>
        </div>
    </div>
    
    <div class="info-tooptip info-tooptip-arrow">
	    <div class="col-sm-12 padtb10">
	        <p class="fnt11">Ordered Physician: <span class="fntbld">{{objMedProfile.medOrderedBy}}</span></p>
	        <p class="fnt11 pt5">Pharmacy Status: <span class="fntbld">{{objMedProfile.medPharmacyStatus}}</span></p>
	    </div>
	</div>
	
	<div class="proceed-prompt collection-prompt" id="concPrompt" style="display:none;">
		<div class="col-sm-12 nopadding h100p">
			<div class="left-prompt col-sm-2">
				<i class="icon icon-alert-info"></i>
			</div>
			<div class="prompt-message text-center col-sm-10 nopadright">
				<p class=" msg-txt">{{objMedProfile.concurrentUserLog.userName}} is currently accessing this order. Do you want to continue?
				</p>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center yes-collection" ng-click="objMedProfile.continueWithOrder()">Yes</button>
				<button class="btn btn-lgrey btn-xs mt10 close-prompt pull-center" ng-click="objMedProfile.hideConcPrompt()">No</button>
			</div>
		</div>
	</div>

<div ng-include src="objMedProfile.popupURL"></div>

</div>

<div id="intervention-modal"></div>
<script type="text/javascript">
$('.info-tooptip').hide();
</script>