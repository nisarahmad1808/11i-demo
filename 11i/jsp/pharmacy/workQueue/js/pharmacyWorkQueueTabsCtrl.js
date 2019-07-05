function pharmacyQueueTabsService($http, $modal){
	var service = this;

	service.getInitialData = function (){

		return $http({
			method: 'POST',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getInitDataForWorkQueueTabs'),
			data:{"loggedInUserId" : global.TrUserId}
		});
	};
	
	service.getRecordCountForTabs = function(){
		return $http({
			method: 'GET',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getRecordCountForTabs')
		});
	};
}

angular.module("PharmacyWorkQueueTabs", ['oc.lazyLoad'])
.controller('pharmacyWorkQueueTabsCtrl', function ($scope, $ocLazyLoad, $http, pharmacyQueueTabsService){

	var objPharmacyQueueTabs = this;
	objPharmacyQueueTabs.objPharmacyQueue = null;
	var servicePharmacyQueueTabs = pharmacyQueueTabsService;
	
	objPharmacyQueueTabs.workQueueRefreshTime = -1;
	
	objPharmacyQueueTabs.tabCounts = {};
	objPharmacyQueueTabs.workQueueData = {};
	objPharmacyQueueTabs.tabitems=[];
	objPharmacyQueueTabs.selectedTab = {};
	objPharmacyQueueTabs.selectedTabName="";
	
	objPharmacyQueueTabs.setIntervalForNewOrders = "";
	
	objPharmacyQueueTabs.hasMyPatientsAccess = false;

	objPharmacyQueueTabs.init = function (){
		objPharmacyQueueTabs.populateTabItems();
		var tabIndex = 0;
		if(1===global.checkFromMedProfile){
			for (var i=0; i<objPharmacyQueueTabs.tabitems.length; i++) {
				if (global.selectedTab.name == objPharmacyQueueTabs.tabitems[i].name) {
					tabIndex = i;
				}
			}
		}
		objPharmacyQueueTabs.loadTab(objPharmacyQueueTabs.tabitems[tabIndex]);
		objPharmacyQueueTabs.setInitialData();
	}

	objPharmacyQueueTabs.populateTabItems = function (){

		var url = "/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getAllActiveList";
		var dependency ="/mobiledoc/inpatientWeb/assets/locationMgmt/js/logiLocationLookupService.js,"+
						"/mobiledoc/inpatientWeb/assets/locationMgmt/js/logiLocationGroupingLookupService.js,"+
						"/mobiledoc/inpatientWeb/assets/locationMgmt/js/locationLookup-tpl.js,"+
						"/mobiledoc/inpatientWeb/assets/locationMgmt/js/inlineFacilityLookup-tpl.js,"+
						"/mobiledoc/inpatientWeb/assets/locationMgmt/js/inlineDepartmentLookup-tpl.js," +
						"/mobiledoc/inpatientWeb/assets/locationMgmt/js/inlineUnitLookup-tpl.js,"+
						"/mobiledoc/inpatientWeb/assets/registration/js/ptLookupDirective.js,"+
						"/mobiledoc/jsp/inpatientWeb/templates/ipFilterTemplate-tpl.js,"+
						"/mobiledoc/inpatientWeb/assets/pharmacy/workQueue/js/allActiveCtrl.js,"+
						"/mobiledoc/jsp/inpatientWeb/templates/deletePrompt-tpl.js,"+
						"/mobiledoc/jsp/inpatientWeb/admin/pharmacySettings/configureInsulinScale/js/ConfigureInsulinScale.js,"+
						"/mobiledoc/jsp/inpatientWeb/admin/pharmacySettings/configureInsulinScale/js/InsulinScaleComponent.js,"+
						"/mobiledoc/jsp/inpatientWeb/staticContent/MedHelper/js/ipMedHelper.js,"+
						"/mobiledoc/inpatientWeb/assets/js/toastrService.js,"+
						"/mobiledoc/jsp/inpatientWeb/staticContent/MedHelper/js/showMessageBoxDirective.js,"+
						"/mobiledoc/inpatientWeb/assets/pharmacy/workQueue/js/labReviewResult.js";
		var tab_All = {
				name: "All",
				displayName: "All",
				url: url,
				dependency: dependency,
				recCount: 0
		};
		var tab_Pending = {
				name: "Pending",
				displayName: "Pending",
				url: url,
				dependency: dependency,
				recCount: 0
		};
		var tab_MyPatients = {
				name: "MyPatients",
				displayName: "My Patients",
				url: "/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/launchMyPatientsList",
				dependency: "/mobiledoc/inpatientWeb/assets/pharmacy/workQueue/js/myPatientsCtrl.js",
				recCount: 0
		};
		var tab_Historical= {
				name: "Verified",
				displayName: "Verified",
				url: url,
				dependency: dependency,
				recCount: 0
		};
		var tab_Lab= {
				name: "Lab Review",
				displayName: "Lab Review",
				url: "/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/launchLabReview",
				dependency: dependency,
				recCount: 0
		};
		objPharmacyQueueTabs.tabitems.push(tab_All);
		objPharmacyQueueTabs.tabitems.push(tab_Pending);
		objPharmacyQueueTabs.tabitems.push(tab_MyPatients);
		objPharmacyQueueTabs.tabitems.push(tab_Historical);
		objPharmacyQueueTabs.tabitems.push(tab_Lab);
	};
	
	objPharmacyQueueTabs.setInitialData = function(){
		servicePharmacyQueueTabs.getInitialData().then(
				function successCallback(response) {
					
					objPharmacyQueueTabs.hasMyPatientsAccess = response.data.hasMyPatientsAccess;
					
					var workQueueRefreshTime = response.data.workQueueRefreshTime == null || response.data.workQueueRefreshTime == undefined || response.data.workQueueRefreshTime == "" ? 0 : response.data.workQueueRefreshTime;
					var workQueueRefreshTimeUnit = response.data.workQueueRefreshTimeUnit == null || response.data.workQueueRefreshTimeUnit == undefined ? "" : response.data.workQueueRefreshTimeUnit;
					
					if(workQueueRefreshTime > 0){
						if(workQueueRefreshTimeUnit == "SS"){
							objPharmacyQueueTabs.workQueueRefreshTime = workQueueRefreshTime * 1000;
						} else if(workQueueRefreshTimeUnit == "MI"){
							objPharmacyQueueTabs.workQueueRefreshTime = workQueueRefreshTime * 60 * 1000 ;
						}
						
						if(objPharmacyQueueTabs.workQueueRefreshTime > 0){
							objPharmacyQueueTabs.setIntervalForNewOrders  = setInterval(objPharmacyQueueTabs.checkForNewOrders, objPharmacyQueueTabs.workQueueRefreshTime);
						}
					}
					
					var tabCounts = response.data.recordCounts;
					
					/*if(tabCounts.ALL != "" && tabCounts.ALL != null && tabCounts.ALL != undefined) {
						objPharmacyQueueTabs.tabitems[0].recCount = tabCounts.ALL;
					} else {
						objPharmacyQueueTabs.tabitems[0].recCount = 0;
					}*/
					
					var unverifiedCount = 0;
					if(tabCounts.UNVERIFIED != "" && tabCounts.UNVERIFIED != null && tabCounts.UNVERIFIED != undefined) {
						unverifiedCount = tabCounts.UNVERIFIED;
					}
					
					var pendingCount = 0;
					if(tabCounts.PENDING != "" && tabCounts.PENDING != null && tabCounts.PENDING != undefined) {
						objPharmacyQueueTabs.tabitems[1].recCount = tabCounts.PENDING;
						pendingCount = tabCounts.PENDING;
					} else {
						objPharmacyQueueTabs.tabitems[1].recCount = 0;
					}
					
					objPharmacyQueueTabs.tabitems[0].recCount = unverifiedCount + pendingCount;
					
					if(tabCounts.VERIFIED != "" && tabCounts.VERIFIED != null && tabCounts.VERIFIED != undefined) {
						objPharmacyQueueTabs.tabitems[3].recCount = tabCounts.VERIFIED;
					} else {
						objPharmacyQueueTabs.tabitems[3].recCount = 0;
					}
					if(tabCounts.LABCOUNT != "" && tabCounts.LABCOUNT != null && tabCounts.LABCOUNT != undefined) {
						objPharmacyQueueTabs.tabitems[4].recCount = tabCounts.LABCOUNT;
					} else {
						objPharmacyQueueTabs.tabitems[4].recCount = 0;
					}
				}, 
				function errorCallback(response) {
					showIPMessage('Error occured while fetching Record Count For Pharmacy Queue Tabs', 'inputelm2','ErrorMsg');
				}
		);
	};
	
	objPharmacyQueueTabs.loadTab = function(tab) {
		objPharmacyQueueTabs.selectedTabName=tab.name;
		objPharmacyQueueTabs.setSelectedTab(tab);
		var files=[];
		files=tab.dependency.split(',');
		$ocLazyLoad.load({
			name: tab.name, // Module Name
			files :files,
			cache: true,
		}).then(function() {
			objPharmacyQueueTabs.tabsURL=makeURL(tab.url);
		}, function(e) {
		});   	  	    			 
	};

	objPharmacyQueueTabs.setSelectedTab = function(tab) {
		objPharmacyQueueTabs.selectedTab = tab;
		global.selectedTab = tab;
		for (var i=0; i<objPharmacyQueueTabs.tabitems.length; i++) {
			if (objPharmacyQueueTabs.selectedTab.name == objPharmacyQueueTabs.tabitems[i].name) {
				objPharmacyQueueTabs.tabitems[i].isActive = true; 
			} else {
				objPharmacyQueueTabs.tabitems[i].isActive = false; 
			}
		}	     
	};
	
	objPharmacyQueueTabs.checkForNewOrders = function(){

		var reqURL = window.location.href;
		
		if(reqURL.indexOf("r=pharmacyWorkQueue.go/launchPharmacyQueue") > 0){

			var tabIndex = -1;
			var msg = "";
			
			if(objPharmacyQueueTabs.selectedTab.name == "All") {
				tabIndex = 0;
			}
			
			if(objPharmacyQueueTabs.selectedTab.name == "Pending") {
				tabIndex = 1;
				msg = "pending ";
			}
			
			if(objPharmacyQueueTabs.selectedTab.name == "Verified") {
				tabIndex = 3;
				msg = "verified ";
			}
			
			if(tabIndex >= 0){
				servicePharmacyQueueTabs.getRecordCountForTabs().then(
					function successCallback(response) {
						
						var oldCounts = angular.copy(objPharmacyQueueTabs.tabitems);
						var newCounts = response.data;
						
						if(newCounts.ALL != "" && newCounts.ALL != null && newCounts.ALL != undefined) {
							objPharmacyQueueTabs.tabitems[0].recCount = newCounts.ALL;
						}
						
						if(newCounts.PENDING != "" && newCounts.PENDING != null && newCounts.PENDING != undefined) {
							objPharmacyQueueTabs.tabitems[1].recCount = newCounts.PENDING;
						}
						
						if(newCounts.VERIFIED != "" &&newCounts.VERIFIED != null && newCounts.VERIFIED != undefined) {
							objPharmacyQueueTabs.tabitems[3].recCount = newCounts.VERIFIED;
						}
						
						if(objPharmacyQueueTabs.tabitems[tabIndex].recCount > oldCounts[tabIndex].recCount){
							//var newCount = objPharmacyQueueTabs.tabitems[tabIndex].recCount - oldCounts[tabIndex].recCount
							//objPharmacyQueueTabs.showNewOrdersMessage(newCount, msg);
							
							objPharmacyQueueTabs.refreshRecords();
						}
					},
					function errorCallback(response) {
						showIPMessage('Error occured while checking for new orders', 'inputelm2','ErrorMsg');
					}
				);
			}
		} else {
			clearInterval(objPharmacyQueueTabs.setIntervalForNewOrders);
		}
	};
	
	objPharmacyQueueTabs.showNewOrdersMessage = function(newCount, msg){
		showIPMessage("There are "+newCount+" new "+msg+"orders. Do you want to refresh the page?", 'inputelm3','ConfirmMsg', 'confirmNewOrdersMessage', 'pharmacyWorkQueueTabsCtrl as objPharmacyQueueTabs');
	};
	
	$scope.confirmNewOrdersMessage = function(isConfirm){
		if(isConfirm == "yes"){
			objPharmacyQueueTabs.refreshRecords();
		}
	};
	
	objPharmacyQueueTabs.refreshRecords = function(){
		objPharmacyQueueTabs.objPharmacyQueue.setLastRefreshTime();
		objPharmacyQueueTabs.objPharmacyQueue.getWorkQueueData();
	};
		
}).service('pharmacyQueueTabsService',['$http',pharmacyQueueTabsService]);


