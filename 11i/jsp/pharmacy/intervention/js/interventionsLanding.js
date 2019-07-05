var controllerFn=function ($scope,$http, $modal,$ocLazyLoad,interventionLandingService,PRINTTYPE,FORMAT,toastrService) { 
	var vm= this ;
	var patientId= $scope.objPharmacyTabs.patientId;
	var interventionservice=interventionLandingService;
	vm.printOptions={showPreview:true,dataDestination:PRINTTYPE.DATA,format:12};// Object Created for Print Directive Integration
	vm.exportOptions={filename:'ExportIntervention',format:FORMAT.EXCEL};// Object Created for Export Directive Integration
	vm.securitySetting=[];
	vm.initialize= function(){
		vm.interventionStatus='All';
		interventionservice.getInterventionDetailsByStatus(vm.interventionStatus,patientId, function(response){
			vm.interventionsTable=response.Interventions;
			vm.securitySettings=response.securitySettings;
		});
	    	
	}
	vm.refreshListWithStatus= function(){
		interventionservice.getInterventionDetailsByStatus(vm.interventionStatus,patientId,function(response){
			vm.interventionsTable=response.Interventions;
		});
		
	}
	vm.setPrintData=function(){
		return $.Deferred(function(dfd) {
			interventionservice.getPtInterventionPrintData(vm.interventionStatus,patientId,function(response){
				vm.printOptions.Htmldata=response.PrintHtml;
				dfd.resolve();
			});

		});
	}
	vm.setExportData=function(){
	    return $.Deferred(function(dfd) {
	    	interventionservice.getInterventionDetailsByStatus(vm.interventionStatus,patientId, function(response){
    			dfd.resolve(createExportJsonLanding(response.Interventions));
    		});
    		
	    });
	}
	function  createExportJsonLanding(exportData){
		if(exportData==undefined ||exportData=='' ||exportData.length==0){
			return;
		}
		var exportObj=[];
		angular.forEach(exportData, function (item) {
					exportObj.push({'Int Name':item.interventionName,
									'Interventions':item.reason,
									'Duration':item.duration+" "+item.durationUnits,
									'Status':item.status,
									'Saving Value':item.savingValue,
									'Time Spend Value':item.timeSpendValue,
									'Entered by':item.createdBy,
									'Entered Date':item.createdOn
									});
						});
		return exportObj;
	};
    vm.showInterventionPopUp = function(mode,interventionId){
		var interventionId=angular.copy(interventionId);
		if(mode!=undefined && mode=='Edit'){
			if(vm.securitySettings!== undefined && vm.securitySettings.InterventionEditAccess!==1){
				toastrService.ecwMessage(toastrService.INFORMATION, "User does not have edit access,opening in read only mode");
			}
				if(interventionId>0){
					var  medOrderCntlDetails = { 
							patientId:patientId,
							mode:mode,
							interventionId:interventionId
					}
					loadScreen(medOrderCntlDetails);
				}else {
					interventionservice.getInterventionIdByPtId(patientId, function(response){
						interventionId=response.interventionId;
						var  medOrderCntlDetails = { 
								patientId:patientId,
								mode:mode,
								interventionId:interventionId
						}
						loadScreen(medOrderCntlDetails);
					});
					
				}	
				 
			// Add Mode Opening 
		}else{
			var  medOrderCntlDetails = { 
					patientId:patientId,
					mode:mode,
					interventionId:interventionId
			}
			loadScreen(medOrderCntlDetails);
		}

	}; 
	function loadScreen (medOrderCntlDetails){
		var dependency =['/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/css/add-new-intervention-modal.css',
		                 "/mobiledoc/inpatientWeb/assets/js/toastrService.js",
		                 '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/js/intervention.js',
		                 '/mobiledoc/inpatientWeb/assets/js/lookup.js'];
		$ocLazyLoad.load(
				{	name	: 'AddNewIntervention',
					cache	: true,
					files	: dependency
				}).then(function() {
					$modal.open({
						templateUrl	: '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/views/addnewintervention.html',
						controller	: 'intervensionPopUpcntl as  vm',
						windowClass	: 'app-modal-window1 add-new-intervention',
						backdrop	: 'static',
						resolve     : {	ptOrdersVisitDt : function(){ return _.clone(medOrderCntlDetails); }}
					}).result.then(function (result) {
						setTimeout(function () {
							vm.refreshListWithStatus();
						},800);
				    },function (result) {
				    	// On Model Close	
				    });
				});
	}

	setTimeout(function(){
        $(".interventions-hgt").perfectScrollbar();
    },500);
    vm.showDevAlert = function(){
		ecwAlert("This functionality is under development.");
	}


};
var serviceFn=function interventionLandingService($http,ipMedHelperURLService,toastrService){
	var service = this;
	//Fetch Intervention wrt to Status
	service.getInterventionDetailsByStatus= function(status,patientId,onSuccess){
		var url = 'interventionReasonController.go/reasons/getAllInterventionByStatus';
		var reqObj={
				status:status,
				patientId:patientId
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp);
					}
				}
				else{
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
					return "";
				}
			}
			else {
				toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				return "";
			}
		});

	};
	service.getInterventionIdByPtId= function(data,onSuccess){
		var url = 'interventionReasonController.go/reasons/getInterventionIdByPtId';
		var reqObj={
				patientId:data
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(angular.isFunction(onSuccess) && resp.Intervention!=null){
						onSuccess(resp.Intervention);
					}else{
						toastrService.ecwMessage(toastrService.INFORMATION, "No Intervention Found");
					}
				}
				else{
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
					return "";
				}
			}
			else {
				toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				return "";
			}
		});
	};
	service.getPtInterventionPrintData= function(status,patientId,onSuccess){
		var url = 'interventionReasonController.go/reasons/getPtInterventionPrintData';
		var reqObj={
				status:status,
				patientId:patientId
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp);
					}
				}
				else{
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
					return "";
				}
			}
			else {
				toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				return "";
			}
		});

	};
} ;

var app = angular.module('interventionModule', ['ui.bootstrap','ipMedHelper','oc.lazyLoad','ecw.ip.print','ecw.ip.export']);
app.service('interventionLandingService',['$http','ipMedHelperURLService','toastrService',serviceFn]);
app.controller('interventionTabCtrl', ['$scope','$http', '$modal','$ocLazyLoad','interventionLandingService','PRINTTYPE','FORMAT','toastrService', controllerFn]);
