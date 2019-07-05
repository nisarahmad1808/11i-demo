function pharmacyTabsService($http, $modal){
	var service = this;
	
	service.httpPostParam = function(url,reqParam){
		var param=$.param(reqParam);
		return $http({
			headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},	
			method: 'POST',                                                                                                                                                                  
			url: url, 
			data: param
		});
	}
	
	service.httpPost = function(url,reqParam){		
		return $http({
			method: 'POST',                                                                                                                                                                  
			url: url, 
			data: {"data" :JSON.stringify(reqParam)}
		});
	}
	
	
	service.httpGet = function(url,reqParam){
		return $http({
			method : "GET",
			url : url,
			param: reqParam
		});
	}
	
}

angular.module("PharmacyTabs", ['oc.lazyLoad','ecw.progressnote.dashboard', 'daterangepicker'])
.controller('pharmacyTabsCtrl', function ($scope, $ocLazyLoad, $http, ipPtDashboardService, pharmacyTabsService){

	var objPharmacyTabs = this;
	var servicePharmacyTabs = pharmacyTabsService;
	var enctypes;
	objPharmacyTabs.tabitems=[];
	objPharmacyTabs.selectedTabName="";
	objPharmacyTabs.patientId = 0;
	
	objPharmacyTabs.encounterId = 0;
	objPharmacyTabs.encounterType = 0;
	objPharmacyTabs.headerData={};

	objPharmacyTabs.init = function (){
		objPharmacyTabs.TrUserId = global.TrUserId;
		
		objPharmacyTabs.patientId = patientId;
		objPharmacyTabs.encounterId = encounterId;
		objPharmacyTabs.encounterType = encounterType;
		objPharmacyTabs.episodeEncounterId = episodeEncounterId;
		objPharmacyTabs.departmentId = departmentId;
		objPharmacyTabs.serviceType = serviceType;
		objPharmacyTabs.populateTabItems();
		objPharmacyTabs.loadTab(objPharmacyTabs.tabitems[0]);
		enctypes=objPharmacyTabs.encounterType;
		
		//Set parameters in ipptdashboardservice
		
		ipPtDashboardService.getTabData(objPharmacyTabs.encounterId ,objPharmacyTabs.encounterType,objPharmacyTabs.patientId,objPharmacyTabs.TrUserId,'', objPharmacyTabs.departmentId, objPharmacyTabs.serviceType,0);
		
		//getHeaderPatientInfo(objPharmacyTabs.encounterId,objPharmacyTabs.encounterType);
	};

	objPharmacyTabs.populateTabItems = function (){

		var url = "";
		var dependency = "";
		
		var tab_MedicationProfile = {
				name: "MedicationProfile",
				displayName: "Medication Profile",
				url: "/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/launchMedicationProfile/"+objPharmacyTabs.patientId+"/"+objPharmacyTabs.encounterId+"/"+objPharmacyTabs.encounterType+"/"+objPharmacyTabs.episodeEncounterId,
				dependency:  "/mobiledoc/inpatientWeb/assets/cpoe/templates/navDirectiveController.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/positionDirective.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/orders/js/orders.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/orderFilterController.js," +
						"/mobiledoc/jsp/inpatientWeb/templates/deletePrompt-tpl.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/cpoeOrderTypeDirective.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/autocomplete-tpl.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/orderScheduleSlots.js," +
						"/mobiledoc/inpatientWeb/assets/cpoe/templates/cpoeOrderGrids.js,"+
						"/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/js/medicationProfileCtrl.js,"+
						"/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/rxEducation/js/RxEducationComponent.js, "+
						"/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/drugMonograph/js/DrugMonographComponent.js, "+
						"/mobiledoc/inpatientWeb/assets/cpoe/global/constant/js/cpoeConstantService.js, "+
						"/mobiledoc/jsp/inpatientWeb/templates/DrugInteractionComponent.js, "+
						"/mobiledoc/jsp/inpatientWeb/templates/interactionCommonServices.js, "+
						"/mobiledoc/jsp/inpatientWeb/templates/interactionConstants.js", 
				recCount: ""
		};
/*		var tab_Admission = {
				name: "Admission",
				displayName: "Admission",
				url: url,
				dependency: dependency,
				recCount: ""
		};*/
		var tab_Orders = {
				name: "Orders",
				displayName: "Orders",
				url: "/mobiledoc/inpatientWeb/assets/cpoe/orders/view/ordersTab.html",
				dependency: Global.getDependencies("orders"),
				recCount: ""
		};
		var tab_Schedule = {
				name: "Schedule",
				displayName: "Schedule",
				url: '/mobiledoc/inpatientWeb/assets/cpoe/schedule/view/pnOrdersSchedule.html',
				dependency: Global.getDependencies("schedule"),
				recCount: ""
		};
		var tab_Vitals = {
				name: "Vitals",
				displayName: "Vitals",
				url: '/mobiledoc/jsp/inpatientWeb/staticContent/progressnotes/view/vitalsTemplate.html',
				dependency: '/mobiledoc/jsp/inpatientWeb/staticContent/progressnotes/js/vitals.controller.js,'
					 + '/mobiledoc/jsp/inpatientWeb/Views/progressnotes/pnpanel/vitals/js/ipVitalsAddController-tpl.js,'
					 + '/mobiledoc/jsp/inpatientWeb/Views/progressnotes/pnpanel/vitals/js/ipVitalsController-tpl.js,'
					 + '/mobiledoc/inpatientWeb/assets/progressnotes/pnpanel/js/pnPanelModal.js,'
					 + '/mobiledoc/inpatientWeb/assets/progressnotes/pnpanel/js/EncDetailsService.js',
				recCount: ""
		};
		var tab_io = {
				name: "io",
				displayName: "I/O",
				url: '/mobiledoc/inpatientWeb/assets/patientDashboard/tabs/io/views/ptIntakeOutputTemplate.html',
				dependency: Global.getDependencies("io"),
				recCount: ""
		};
		var tab_emar = {
				name: "emar",
				displayName: "eMAR",
				url: "/mobiledoc/jsp/inpatientWeb/staticContent/eMAR/view/eMARMain.html",
				dependency: "/mobiledoc/jsp/inpatientWeb/staticContent/eMAR/js/eMARMain.js,/mobiledoc/inpatientWeb/assets/cpoe/templates/positionDirective.js,/mobiledoc/inpatientWeb/assets/cpoe/templates/cpoeOrderGrids.js",
				recCount: ""
		};
		var tab_Results = {
				name: "Results",
				displayName: "Results",
				url:  '/mobiledoc/inpatientWeb/assets/patientDashboard/tabs/results/views/patientResultsTemplate.html',
				dependency: Global.getDependencies("results"),
				recCount: ""
		};
		var tab_ProgressNote = {
				name: "ProgressNote",
				displayName: "Progress Note",
				url: '/mobiledoc/jsp/inpatientWeb/staticContent/progressnotes/view/progressNoteTemplate.html',
				dependency: '/mobiledoc/jsp/inpatientWeb/staticContent/progressnotes/js/progressNotes.controller.js,/mobiledoc/jsp/inpatientWeb/staticContent/progressnotes/js/progressNotes.service.js',
				recCount: ""
		};
		var tab_MedReconciliation = {
				name: "MedReconciliation",
				displayName: "Med Reconciliation",
				url: "/mobiledoc/inpatientWeb/InPatient-Reconcile.go/getMedReconciliationTab?encounterId=" + objPharmacyTabs.encounterId+"&patientId="+objPharmacyTabs.patientId+"&encType="+objPharmacyTabs.encounterType + "&departmentId="+objPharmacyTabs.departmentId+"&serviceTypeId="+objPharmacyTabs.serviceType,
				dependency: "/mobiledoc/inpatientWeb/assets/medReconciliation/js/medReconciliation.js,/mobiledoc/inpatientWeb/assets/MedHelper/js/ipMedHelper.js",
				recCount: ""
		};
		var tab_Flowsheet = {
				name: "Flowsheet",
				displayName: "Flowsheet",
				url: "/mobiledoc/inpatientWeb/assets/patientDashboard/tabs/flowsheet/views/flowsheet.html",
				dependency: Global.getDependencies("flowsheet"),
				recCount: ""
		};
		var tab_Interventions = {
				name: "Interventions",
				displayName: "Interventions",
				url: "/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/views/interventionsLanding.html",
				dependency: "/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/css/pharmacy-settings-tabs.css,"+
							"/mobiledoc/jsp/inpatientWeb/staticContent/MedHelper/js/ipMedHelper.js,"+
							"/mobiledoc/inpatientWeb/assets/js/toastrService.js,"+
							"/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/js/interventionsLanding.js",
				recCount: ""
		};

		objPharmacyTabs.tabitems.push(tab_MedicationProfile);
		//objPharmacyTabs.tabitems.push(tab_Admission);
		objPharmacyTabs.tabitems.push(tab_Orders);
		objPharmacyTabs.tabitems.push(tab_Schedule);
		objPharmacyTabs.tabitems.push(tab_Vitals);
		objPharmacyTabs.tabitems.push(tab_io);
		objPharmacyTabs.tabitems.push(tab_emar);
		objPharmacyTabs.tabitems.push(tab_Results);
		objPharmacyTabs.tabitems.push(tab_ProgressNote);
		objPharmacyTabs.tabitems.push(tab_MedReconciliation);
		objPharmacyTabs.tabitems.push(tab_Flowsheet);
		objPharmacyTabs.tabitems.push(tab_Interventions);
	};

	objPharmacyTabs.loadTab = function(tab) {
		
		objPharmacyTabs.unloadHighcharts();
		
		objPharmacyTabs.selectedTabName=tab.name;
		objPharmacyTabs.setSelectedTab(tab);
		var files=[];
		files=tab.dependency.split(',');
		$ocLazyLoad.load({
			name: tab.name, // Module Name
			files :files,
			cache: true,
			serie: true,
		}).then(function() {
			objPharmacyTabs.tabsURL = makeURL(tab.url);
		}, function(e) {
		});   	  	    			 
	};

	objPharmacyTabs.setSelectedTab = function(tab) {
		objPharmacyTabs.selectedTab = tab;
		for (var i=0; i<objPharmacyTabs.tabitems.length; i++) {
			if (objPharmacyTabs.selectedTab.name == objPharmacyTabs.tabitems[i].name) {
				objPharmacyTabs.tabitems[i].isActive = true; 
			} else {
				objPharmacyTabs.tabitems[i].isActive = false; 
			}
		}	     
	};
	
	objPharmacyTabs.unloadHighcharts = function() {
    	try{
    		var jsloader = new DJSLoader();
    		jsloader.removejscssfile('/mobiledoc/jsp/catalog/js/highstock-src.js','js');
    		jsloader.removejscssfile('/mobiledoc/jsp/catalog/js/exporting.src.js','js');
    		window.HighchartsAdapter = null;
    		window.Highcharts = null;
		}catch(e){}
    }
}).service('pharmacyTabsService',['$http',pharmacyTabsService]);
