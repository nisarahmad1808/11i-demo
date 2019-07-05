
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/Dual-headertable.css">
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/all-active.css">
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/mypatientlist.css">
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/pharmacy-censuslist.css" type="text/css" media="all" />

<div class="clearfix"></div>
	<div class="main-cont" ng-controller="pharmacyWorkQueueTabsCtrl as objPharmacyQueueTabs" ng-init="objPharmacyQueueTabs.init();">
		<div class="blue-nav blue-pad">
			<div class="dropdown referal-drop pull-left">
				<a href="#">Pharmacy<b class="caret"></b></a>
			</div>
			<div class="blue-tabnav pull-right mr20">
				<ul class="nav">
					<li ng-repeat="tab in objPharmacyQueueTabs.tabitems track by $index" ng-hide="objPharmacyQueueTabs.hasMyPatientsAccess != true && tab.name == 'MyPatients' " ng-class="{'active':objPharmacyQueueTabs.selectedTab.name==tab.name}">
						<a href="{{'#' + tab.name}}" target="_self" data-toggle="tab" ng-click="objPharmacyQueueTabs.loadTab(tab)">{{tab.displayName}} <span style='border:0px!important;' ng-if="(tab.recCount != '' && tab.recCount != '0')">&nbsp;{{tab.recCount}} &nbsp;</span></a>
					</li> 
				</ul>
			</div>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<div class="dashboard-content">
			<div class="tab-content">
				<div  class="tab-pane"  ng-class="{'active':objPharmacyQueueTabs.selectedTabName}">
	          		<div ng-include src="objPharmacyQueueTabs.tabsURL"></div>
	          	</div>
			</div>
		</div>
	</div>
</div>
