function medicationProfileService($http, $modal){
	var service = this;

	service.getMedProfileData = function (patientId, pharmacyStatus){
		return $http({
			method: 'GET',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMedicationProfileData/'+patientId+'/'+pharmacyStatus+'/'+global.TrUserId)
		});
	};
	
	service.getMedProfile = function (patientId, pharmacyStatus){
		return $http({
			method: 'GET',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMedicationProfile/'+patientId+'/'+global.TrUserId)
		});
	};
	
	service.getConcurrentUserLog = function(orderId, patientId, trUserId){
		return $http({
			method: 'GET',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getConcurrentUserLog/'+orderId +'/'+patientId+'/'+trUserId)
		});
	};
	service.getPrintData= function(objReqParams){
		return $http({
			method: "POST",
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getPrintData'),
			data:objReqParams		
		});
	};
}

angular.module('MedicationProfileModule', ['ui.bootstrap','oc.lazyLoad','ecw.ip.print','perfect_scrollbar'])
.controller("medicationProfileCtrl", function ($scope, $filter, $ocLazyLoad, $http, $modal,$window, ordersService, medicationProfileService,PRINTTYPE) {
	var objMedProfile = this ;
	var serviceMedProfile = medicationProfileService;
	objMedProfile.patientId = 0;
	
	objMedProfile.encounterId = 0;
	objMedProfile.encounterType = 0;
	
	objMedProfile.allMeds = [];
	objMedProfile.allMedsCount = 0;
	objMedProfile.verifiedMeds = [];
	objMedProfile.verifiedMedsCount = 0;
	objMedProfile.activeMeds = [];
	objMedProfile.activeMedsCount = 0;
	
	objMedProfile.pharmacyStatus = -1;
	
	objMedProfile.medPharmacyStatus = "";
	objMedProfile.medOrderedBy = "";
	
	objMedProfile.selectedOrderId = 0;
	objMedProfile.selectedPharmacyStatus = -1;
	
	objMedProfile.ptOrderDtls = {};
	
	objMedProfile.drugLookup = "";
	
	objMedProfile.concurrentUserLog = {};
	objMedProfile.printOptions={showPreview:true,dataDestination:PRINTTYPE.DATA,format:12};// Object Created for Print Directive Integration
	objMedProfile.init = function(){
		objMedProfile.patientId = patientId;
		objMedProfile.encounterId = encounterId;
		objMedProfile.encounterType = encounterType;
		objMedProfile.episodeEncounterId = episodeEncounterId;
		
		objMedProfile.medProfileData = {};
		
		objMedProfile.ptOrderDtls = {"nTrUserId" : global.TrUserId, "nPatientId" : objMedProfile.patientId, "nEncounterId" : objMedProfile.encounterId, "nEncounterType" : objMedProfile.encounterType};
		
		
		objMedProfile.ordersVisitDetails = {moduleEncId : objMedProfile.encounterId,episodeEncId : objMedProfile.episodeEncounterId, patientId :  objMedProfile.patientId, moduleEncType : objMedProfile.encounterType, trUserId : global.TrUserId};
		
		objMedProfile.getMedProfile();
	};
	
	objMedProfile.getMedProfile = function(){
		
		serviceMedProfile.getMedProfile(objMedProfile.patientId, objMedProfile.pharmacyStatus).then(
				function successCallback(response) {
					objMedProfile.medProfileData = response.data;
				}, 
				function errorCallback(response) {
					showIPMessage('Error occured while loading Medication Profile', 'inputelm2','ErrorMsg');
				}
		);

	};
	objMedProfile.setPrintData=function(){
		return $.Deferred(function(dfd) {
			serviceMedProfile.getPrintData({patientId:objMedProfile.patientId}).success(function(response){
				if(response.status==="success"){
					objMedProfile.printOptions.Htmldata=response.PrintHtml;	
				}else{
					showIPMessage('Error occured while loading Medication Print data');
				}
				dfd.resolve();
			});
		});
	}
	
	objMedProfile.getPendingMeds = function (){
		serviceMedProfile.getMedProfileData(objMedProfile.patientId, objMedProfile.pharmacyStatus).then(
				function successCallback(response) {
					objMedProfile.medProfileData.allMeds = response.data.allMeds;
					objMedProfile.medProfileData.allMedsCount = response.data.allMedsCount;
					
					objMedProfile.medProfileData.allIVs = response.data.allIVs;
					objMedProfile.medProfileData.allIVsCount = response.data.allIVsCount;
					
					objMedProfile.medProfileData.allComplexIVs = response.data.allComplexIVs;
					objMedProfile.medProfileData.allComplexIVsCount = response.data.allComplexIVsCount;
				}, 
				function errorCallback(response) {
					showIPMessage('Error occured while loading Medication Profile', 'inputelm2','ErrorMsg');
				}
		);
		
	}
	
	objMedProfile.setMedInfo = function (item){
		objMedProfile.medPharmacyStatus = item.pharmacyStatus;
		objMedProfile.medOrderedBy = item.orderingProviderName;
	};
	
	objMedProfile.openMedOrdersPopup = function(orderId, pharmacyStatus){
		
		objMedProfile.selectedOrderId = orderId;
		objMedProfile.selectedPharmacyStatus = pharmacyStatus;
		
		serviceMedProfile.getConcurrentUserLog(orderId, objMedProfile.patientId, global.TrUserId).then(
				function successCallback(response) {
					objMedProfile.concurrentUserLog = response.data;
					if(objMedProfile.concurrentUserLog.id != 0){
						if(objMedProfile.concurrentUserLog.allowContinue){
							objMedProfile.showConcPrompt();
						} else {
							showIPMessage(objMedProfile.concurrentUserLog.userName + ' is currently accessing this order', 'inputelm2','ErrorMsg');
						}
					} else {
						objMedProfile.continueWithOrder();
					}
				}, 
				function errorCallback(response) {
					showIPMessage('Error occured while checking for concurrent user', 'inputelm2','ErrorMsg');
				}
		);
	};
	
	objMedProfile.showConcPrompt = function(){
		$('#concPrompt').show();
	};
	
	objMedProfile.hideConcPrompt = function(){
		$('#concPrompt').hide();
	};
	
	objMedProfile.continueWithOrder = function(){
		
		$('#concPrompt').hide();
		
		$ocLazyLoad.load({
			name: "MedicationProfile", // Module Name
			files :["/mobiledoc/jsp/inpatientWeb/staticContent/cpoe/templates/patientInfoHeaderController.js",
			        '/mobiledoc/jsp/inpatientWeb/devices/configuration/js/printLabel-tpl.js',  
                    '/mobiledoc/jsp/inpatientWeb/devices/configuration/js/angular-barcode.js',
                    '/mobiledoc/inpatientWeb/assets/templates/billing/js/cptlist.js',
                    '/mobiledoc/jsp/inpatientWeb/admin/pharmacySettings/pharmacyUtility/js/pharmacyUitilityService.js',
                    '/mobiledoc/inpatientWeb/assets/templates/billing/js/cptlookup.js','/mobiledoc/jsp/inpatientWeb/admin/pharmacySettings/configureInsulinScale/js/InsulinScaleComponent.js', '/mobiledoc/jsp/inpatientWeb/staticContent/MedHelper/js/showMessageBoxDirective.js',
                    "/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/js/medOrdersCtrl.js", "/mobiledoc/inpatientWeb/assets/cpoe/templates/errorInfoPopupService.js", "/mobiledoc/jsp/inpatientWeb/templates/deletePrompt-tpl.js","/mobiledoc/inpatientWeb/assets/js/toastrService.js","/mobiledoc/jsp/inpatientWeb/staticContent/MedHelper/js/ipMedHelper.js",
                    '/mobiledoc/jsp/inpatientWeb/admin/pharmacySettings/configureInsulinScale/js/ConfigureInsulinScale.js'],
			cache: true
		}).then(function() {
			var concurrentUserId = objMedProfile.concurrentUserLog.userId == null || objMedProfile.concurrentUserLog.userId == undefined ? 0 : objMedProfile.concurrentUserLog.userId;
			objMedProfile.popupURL=makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/launchMedicationOrders/"+objMedProfile.selectedOrderId+"/"+objMedProfile.patientId+"/"+objMedProfile.encounterId+"/"+objMedProfile.encounterType+"/"+objMedProfile.selectedPharmacyStatus+"/"+concurrentUserId);
		}, function(e) {
		});
	};
	
	
	objMedProfile.openPharmacyWorkQueue =function(){
		global.checkFromMedProfile = 1;
		$window.location = '#/r=pharmacyWorkQueue.go/launchPharmacyQueue';
	};
	objMedProfile.loadNewOrdersPopUp = function(){
		lazyLoadModal({
			name		:"ecw.ip.controller.stagingOrderModal",
			serie       : true,
			files		: CPOE.getDependencies("stagingOrdersPopup"),
			url			: "/mobiledoc/inpatientWeb/assets/cpoe/orders/view/stagingOrderModal-tpl.html",
			cntrl		:"stagingOrderModalController as orderPopupCtrl",
			class		:"w1300 hidden-content",    
			resolveFn 	: {ptOrdersVisitDt : function () {return _.clone(objMedProfile.ordersVisitDetails);}},
			closeFn	  	: function(){} ,	
			cancelFn  	: function(){}
		});
	};
	
	lazyLoadModal = function (modalToOpen){
		$ocLazyLoad.load(
		{	name	: modalToOpen.name,
			cache	: false,
			files	: modalToOpen.files
		}).then(function() {
		$modal.open({
    		templateUrl	: modalToOpen.url,
    		controller	: modalToOpen.cntrl,
    		windowClass	: modalToOpen.class,
    		backdrop	: 'static',
    		resolve		: modalToOpen.resolveFn		            					
	            		
	    }).result.then(function (result){
	    		modalToOpen.closeFn();	
	    },function (result) {
	    		modalToOpen.cancelFn();
	    });
		}, function(e) {
		});
	};
	
	$(window).resize();
	
	
}).service('medicationProfileService',['$http',medicationProfileService]);

$(document).on('mouseover', '.icon.icon-info2', function () {
    var _that = this;
    $('.info-tooptip').show().animate({}, 100, function () {
        $(this).position({
            of: _that,
            my: 'right+20 top+28',
            at: 'right top',
        }).animate({
            "opacity": 1
        }, 100)
    });

});
$(document).on('mouseleave', '.icon.icon-info2', function () {
    $('.info-tooptip').hide();
});
$(window).resize(function () {
    var winHgt = $('html').height() - 170;
    var verifyHgt = $('html').height() - 170;
    $('.left-section').height(winHgt);
    $('.markorders').height(verifyHgt);
});
