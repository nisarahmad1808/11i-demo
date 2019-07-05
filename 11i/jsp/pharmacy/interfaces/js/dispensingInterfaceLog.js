/**
 * This controller is used to show medication dispense logs. 
 */
(function () {
	'use strict';
	
	function dispensingInterfaceLogService($http){
		var service = this;
		service.getMedicationDispenseLog = function(filters) {
			var url = '/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getDispensingInterfaceLog';
			var aPromise = $http({
				method : 'POST',
				data : JSON.stringify(filters),
				url : makeURL(url)
			});
			return aPromise;
		}
	}
	
	function dispensingInterfaceLogController($modal, $http, dateRangeOption, dispensingInterfaceService, toastrService){
		
		var medDispenseCtrl = this;
		
		medDispenseCtrl.noRecordFound = false;

		//pagination
		medDispenseCtrl.totalRecords = 0;
		medDispenseCtrl.recordsPerPage = 15; 
		medDispenseCtrl.selectedPage = 1;
		
		//status
		medDispenseCtrl.selectedStatus = "Select";
		
		//date range
		var todayDate = '';
		medDispenseCtrl.rangeDateOpt = dateRangeOption;
		medDispenseCtrl.dateRange = {
	        startDate: null,
	        endDate: null
	    };
		
		//facility lookup
		medDispenseCtrl.selectedFacility = {facilityId : 0,facilityName : ""};
		
		//provider lookup
		medDispenseCtrl.providerForFilter = {provider: {}, providerList: []};
		medDispenseCtrl.providerForFilter.provider = {Id: 0 , Name:''};
	 	
		medDispenseCtrl.selectedFacility = {facilityId : 0,facilityName : ""}
		
		medDispenseCtrl.loadDefaultFacility = function() {
			if (medDispenseCtrl.selectedFacility.facilityId === 0 || medDispenseCtrl.selectedFacility.facilityId === "") {
				medDispenseCtrl.selectedFacility.facilityId = global.facilityId;
				medDispenseCtrl.selectedFacility.facilityName = getFacilityName(global.facilityId);
			}
		};
		
		//patient lookup
		medDispenseCtrl.filterPatient = {patient : "" };
		medDispenseCtrl.filterPatient.patient = { id : 0 };
		
		medDispenseCtrl.clearPatient = function (){
			medDispenseCtrl.filterPatient = {patient : "" };
			medDispenseCtrl.filterPatient.patient = { id : 0 };
		};
		
		medDispenseCtrl.setDefaultTodayDate = function() {
            todayDate=getDateTime().substring(0,10);
			medDispenseCtrl.dateRange.startDate = todayDate;
			medDispenseCtrl.dateRange.endDate = todayDate;
		};


		var filterOptionsTemp = {};

		medDispenseCtrl.filterOptions = {
			selectedPage : 1,
			recordsPerPage : 15,
			selectedStatus : 'Select',
			providerId : 0,
			facilityId : 0,
			patientId : 0,
			startDate : 0,
			endDate : 0,
			typeOfCall : ''
		};
		
		medDispenseCtrl.initializeEvents = function(){
			medDispenseCtrl.setDefaultTodayDate();
			medDispenseCtrl.loadDefaultFacility();
			medDispenseCtrl.getMedicationDispenseLogReport('onPageLoad');
		};
		
		medDispenseCtrl.getMedicationDispenseLogReport = function(typeOfCall){
			
			medDispenseCtrl.filterOptions.selectedPage = medDispenseCtrl.selectedPage = 1;
			medDispenseCtrl.filterOptions.recordsPerPage =   medDispenseCtrl.recordsPerPage = 15;
			medDispenseCtrl.filterOptions.selectedStatus = medDispenseCtrl.selectedStatus;
			medDispenseCtrl.filterOptions.providerId = !medDispenseCtrl.providerForFilter.provider.Id ? 0 : medDispenseCtrl.providerForFilter.provider.Id;
			medDispenseCtrl.filterOptions.facilityId = !medDispenseCtrl.selectedFacility.facilityId ? 0 : medDispenseCtrl.selectedFacility.facilityId;
			medDispenseCtrl.filterOptions.patientId = medDispenseCtrl.filterPatient.patient.id;
			
			if(medDispenseCtrl.dateRange.startDate !== todayDate){
				medDispenseCtrl.filterOptions.startDate = medDispenseCtrl.dateRange.startDate == null ? 0 : medDispenseCtrl.dateRange.startDate.format('MM/DD/YYYY');
			}else{
				medDispenseCtrl.filterOptions.startDate = medDispenseCtrl.dateRange.startDate == null ? 0 : medDispenseCtrl.dateRange.startDate;
			}
			if(medDispenseCtrl.dateRange.endDate !== todayDate){
				medDispenseCtrl.filterOptions.endDate = medDispenseCtrl.dateRange.endDate == null ? 0 : medDispenseCtrl.dateRange.endDate.format('MM/DD/YYYY');
			}else{
				medDispenseCtrl.filterOptions.endDate = medDispenseCtrl.dateRange.endDate == null ? 0 : medDispenseCtrl.dateRange.endDate;
			}

			medDispenseCtrl.filterOptions.typeOfCall = typeOfCall;
			
			filterOptionsTemp = medDispenseCtrl.filterOptions;
			
			if((medDispenseCtrl.filterOptions.startDate != null || medDispenseCtrl.filterOptions.startDate !== 0 ) && (medDispenseCtrl.filterOptions.endDate != null || medDispenseCtrl.filterOptions.endDate !== 0)){
				var startDateVal= new Date(medDispenseCtrl.filterOptions.startDate);
				var endDateVal = new Date(medDispenseCtrl.filterOptions.endDate); 
				
				var timeDiff = Math.abs(endDateVal.getTime() - startDateVal.getTime());
				var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
				if(diffDays > 30 ){
					toastrService.ecwMessage(toastrService.ERROR, "Selected date range should not exceed more than 30 days. Please try again."); 
					return;
				}
			}
		
			try{
				if((medDispenseCtrl.filterOptions.selectedStatus === 'Select') && (medDispenseCtrl.filterOptions.providerId === 0) 
						&&(medDispenseCtrl.filterOptions.facilityId === 0 ) && (medDispenseCtrl.filterOptions.patientId === 0)  && 
						(medDispenseCtrl.filterOptions.startDate === 0) && (medDispenseCtrl.filterOptions.endDate === 0)){
						toastrService.ecwMessage(toastrService.INFORMATION, "Please select atleast one filter.."); 
				}else{
					var promise = dispensingInterfaceService.getMedicationDispenseLog(medDispenseCtrl.filterOptions);
					promise.then(function(resp) {
						if(resp.data !== "ResponseException" ){
							if(resp.data.MedDispensingLogData.length > 0){
								medDispenseCtrl.medDispenseLogList = resp.data.MedDispensingLogData;
								medDispenseCtrl.totalRecords = resp.data.TotalCount;
								medDispenseCtrl.noRecordFound = false;
							}
							else{
								medDispenseCtrl.medDispenseLogList = {};
								medDispenseCtrl.totalRecords = 0;
								medDispenseCtrl.noRecordFound = true;
							}
						}else{
							toastrService.ecwMessage(toastrService.ERROR, "Some exception occurred ! Please try again."); 
							medDispenseCtrl.medDispenseLogList = {};
							medDispenseCtrl.totalRecords = 0;
							medDispenseCtrl.noRecordFound = true;
						}
					});	
				}
				
			}catch(err) {
				toastrService.ecwMessage(toastrService.ERROR, "Some exception occurred ! Please try again."); 
				medDispenseCtrl.medDispenseLogList = {};
				medDispenseCtrl.totalRecords = 0;
				medDispenseCtrl.noRecordFound = true;
			}
		};
		
		//on next and prev
		medDispenseCtrl.getMedicationDispenseData = function(){
			try {	
				filterOptionsTemp.selectedPage = medDispenseCtrl.selectedPage;
				var promise = dispensingInterfaceService.getMedicationDispenseLog(filterOptionsTemp);
				promise.then(function(resp) {
					if(resp.data !== "ResponseException" ){
						medDispenseCtrl.medDispenseLogList = resp.data.MedDispensingLogData;
						medDispenseCtrl.totalRecords = resp.data.TotalCount;
						medDispenseCtrl.noRecordFound = false;
					}else{
						toastrService.ecwMessage(toastrService.ERROR, "Some exception occurred ! Please try again."); 
						medDispenseCtrl.medDispenseLogList = {};
						medDispenseCtrl.totalRecords = 0;
						medDispenseCtrl.noRecordFound = true; 
					}
				});
			} catch (e) {
				toastrService.ecwMessage(toastrService.ERROR, "Some exception occurred ! Please try again."); 
				medDispenseCtrl.medDispenseLogList = {};
				medDispenseCtrl.totalRecords = 0;
				medDispenseCtrl.noRecordFound = true;
			}
		};
		
	medDispenseCtrl.openDetails = function(data, index){
			var obj = {};
			obj.list = data; 
			obj.index = index; 
		      var modalInstance = $modal.open({
		        animation: true, 
		        templateUrl: '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/interfaces/view/dispensingInterfaceLogModal.html',
		        controller: 'dispensingInterfaceDetailsController',
		        controllerAs  : 'dispInterfaceDetailsCtrl', 
		        backdrop: 'static',
		        keyboard: false,  
		        size: 'dashboard_size',
		        windowClass: 'global new-intervention', 
		        resolve: {
		          itemObj: function () {
		            return obj; 
		          }
		        }
		      });
	    };
	}
	
	function dispensingInterfaceLogDetailsController(dispensingInterfaceService, itemObj, patientdataService){
		var medDispenseDetailsCtrl = this;  
		medDispenseDetailsCtrl.list = itemObj.list;
		medDispenseDetailsCtrl.selectedIndex = itemObj.index; 
		
		medDispenseDetailsCtrl.loadDescData = function(obj){
			medDispenseDetailsCtrl.itemObj = obj; 
		};
		
		medDispenseDetailsCtrl.nextData = function(){
			medDispenseDetailsCtrl.selectedIndex++;
			medDispenseDetailsCtrl.loadDescData(medDispenseDetailsCtrl.list[medDispenseDetailsCtrl.selectedIndex]);
		};
		
		medDispenseDetailsCtrl.previousData = function(){
			medDispenseDetailsCtrl.selectedIndex--;
			medDispenseDetailsCtrl.loadDescData(medDispenseDetailsCtrl.list[medDispenseDetailsCtrl.selectedIndex]);
		};
		
		medDispenseDetailsCtrl.initDescData = function(){
			medDispenseDetailsCtrl.loadDescData(medDispenseDetailsCtrl.list[medDispenseDetailsCtrl.selectedIndex]);
		};

	}
	
	/**
     * This function is used to prettify JSON means add <span class="json-keys"> to a json keys.
     * pattern for regex will identify each keys into JSON and if match found then it will put <span> to that match so that into the UI key can looks bold.
     * apart from that this function also remove first and last braces from JSON.
     */
    function prettify() {
        function syntaxHighlight(json) {
        	if(json && json !== ""){
        		json = JSON.stringify(JSON.parse(json),undefined,4);
        		json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        		json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        			if (/^"/.test(match)) {
        				if (/:$/.test(match)) {
        					match = '<span class="json-key">' + match.replace(/"/g,"") + '</span>';
        				}else{
        					match = match.replace(/"/g,"");
        				}
        			}
        			return match;
        		});
        		json = json.substring(1,json.length -1);
        		return json;
        	}
        	else {
        		return "";
        	}
        }
        return syntaxHighlight;
    }
	
	angular.module('dispensingInterfaceModule',['ui.bootstrap', 'daterangepicker', 'ui.mask', 'providerlookup', 'ecw.dir.inlineFacilityLookup', 'patientlookupinline', 'stafflookup' , 'inPatient.dir.filterTemplate' , 'ecw.ip.dir.divPaginationControl', 'toastrMsg', 'ecw.service.patientdataService', 'inpatientWebApp'])
	.service('dispensingInterfaceService',['$http',dispensingInterfaceLogService])
	.controller('dispensingInterfaceController', ['$modal', '$http', 'dateRangeOption', 'dispensingInterfaceService', 'toastrService', dispensingInterfaceLogController])
	.controller("dispensingInterfaceDetailsController", ['dispensingInterfaceService', 'itemObj', 'patientdataService', dispensingInterfaceLogDetailsController])
	.filter('prettify',[prettify]);
})();