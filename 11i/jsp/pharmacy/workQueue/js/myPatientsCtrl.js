function myPatientsService($http, $modal){
	
	var service = this;
	
	service.getMyPatientsInitData = function(){

		return $http({
			method: 'GET',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMyPatientsInitData/'+global.TrUserId)
		});
	};
	
	service.getPatientsByFilter = function(filterOptions ){

		return $http({
			method: 'POST',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getPatientsByFilter'),
			data: JSON.stringify(filterOptions)
		});
	};
	
	service.getFlowsheetItems = function(patient, flowsheet, datesToShow){

		return $http({
			method: 'POST',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getFlowsheetItems'),
			data:{"datesToShow" : JSON.stringify(datesToShow), "userId" : global.TrUserId, "flowsheetId":flowsheet.id, "patientId":patient.patientId, "episodeEncounterId" : patient.orderEpisodeEncId }
			
		});
	};
	
}

angular.module("myPatientsModule", ['ui.bootstrap', 'daterangepicker','ui.mask', 'ecw.dir.inlineFacilityLookup','stafflookup','ecw.dir.inlineDepartmentLookup','providerlookup','inPatient.dir.filterTemplate'])
.controller('myPatientsCtrl', function ($scope, $window, $filter, $http, dateRangeOption, myPatientsService){
	
	var objMyPatients = this;
	
	objMyPatients.filterOptions = {
			selectedFacility: 0,
			flowsheetId: 0,
			deptId: 0,
			startDate: null,
			endDate: null,
			selectedPage : 1,
			recordsPerPage : 10,
			loggedInUserId : global.TrUserId
	};
    
	objMyPatients.jsonFilterDetails = {toSaveFilter : {}, selectedFilter : {}, configuredFilters : {} };
	
    objMyPatients.rangeDateOpt = dateRangeOption;
    objMyPatients.dateRange = {
        startDate: null,
        endDate: null
    };
    
    objMyPatients.allSelected = false;
    objMyPatients.patientData = [];
    
    objMyPatients.flowsheetTemplates = [];
    objMyPatients.selectedTemplateName = "";
	objMyPatients.selectedTemplate = {};
    
	objMyPatients.selectedFacility = {facilityId : 0 , facilityName : ""};
	objMyPatients.selectedDept = {departmentId:0};
	
	objMyPatients.valuesToDisplay = [];
	objMyPatients.showValueColumn = true;
	
	objMyPatients.selectedPatient = {};
	objMyPatients.selectedIndex = -1;
	objMyPatients.isMyPatientDataFound = false;
	objMyPatients.totalPatientRecords = 0;
	
    objMyPatients.init = function (){
		
    	objMyPatients.getInitialData();
		
	};
	
	objMyPatients.onSelectFacility= function(){
		objMyPatients.selectedDept = {};
	};
	
	objMyPatients.getInitialData = function(){
		
		myPatientsService.getMyPatientsInitData().then(
			function successCallback(response) {
				objMyPatients.flowsheetTemplates = response.data.flowsheetTemplates;
				objMyPatients.datesToShow = response.data.datesToShow;
			}, 
			function errorCallback(response) {
				showIPMessage('Error occured while loading Flowsheet Templates', 'inputelm2','ErrorMsg');
			}
		);
		
		objMyPatients.setFilterForSaving();
	};
	
	objMyPatients.showPatients = function (template) {
		
    	objMyPatients.selectedTemplateName = template.name;
    	objMyPatients.selectedTemplate = template;
    	
    	if(objMyPatients.selectedTemplate.id == null || objMyPatients.selectedTemplate.id == undefined){
    		showIPMessage('Please select a template to get the patient list', 'inputelm2','ErrorMsg');
    		return;
    	}
        
    	objMyPatients.filterOptions.flowsheetId = objMyPatients.selectedTemplate.id;
    	objMyPatients.filterOptions.selectedFacility = objMyPatients.selectedFacility.facilityId == null || objMyPatients.selectedFacility.facilityId == undefined ? 0 : objMyPatients.selectedFacility.facilityId;
    	objMyPatients.filterOptions.deptId = objMyPatients.selectedDept.departmentId == null || objMyPatients.selectedDept.departmentId == undefined ? 0 : objMyPatients.selectedDept.departmentId;
		try{
			objMyPatients.filterOptions.startDate = objMyPatients.dateRange.startDate == null ? null : (!angular.isString(objMyPatients.dateRange.startDate) ? objMyPatients.dateRange.startDate.format('YYYY-MM-DD') : objMyPatients.dateRange.startDate);
			objMyPatients.filterOptions.endDate =  objMyPatients.dateRange.endDate == null ? null : (!angular.isString(objMyPatients.dateRange.endDate) ? objMyPatients.dateRange.endDate.format('YYYY-MM-DD') : objMyPatients.dateRange.endDate);
		}catch(e){
			
		}
    	
        myPatientsService.getPatientsByFilter(objMyPatients.filterOptions).then(
    			function successCallback(response) {

    				objMyPatients.patientData = response.data;  				
    				if(objMyPatients.patientData.length > 0) {
    					objMyPatients.isMyPatientDataFound = true; 
    					objMyPatients.totalPatientRecords = response.data[0].totalCount;
    				} else {
    					objMyPatients.isMyPatientDataFound = false;
    				}
    			}, 
    			function errorCallback(response) {
    				showIPMessage('Error occured while loading Flowsheet Templates', 'inputelm2','ErrorMsg');
    			}
        );
        
        objMyPatients.setFilterForSaving();
    };
    objMyPatients.getMyPatientData = function() {
    	
    	objMyPatients.filterOptions.flowsheetId = objMyPatients.selectedTemplate.id;
    	objMyPatients.filterOptions.selectedFacility = objMyPatients.selectedFacility.facilityId == null || objMyPatients.selectedFacility.facilityId == undefined ? 0 : objMyPatients.selectedFacility.facilityId;
    	objMyPatients.filterOptions.deptId = objMyPatients.selectedDept.departmentId == null || objMyPatients.selectedDept.departmentId == undefined ? 0 : objMyPatients.selectedDept.departmentId;
		try{
			objMyPatients.filterOptions.startDate = objMyPatients.dateRange.startDate == null ? null : (!angular.isString(objMyPatients.dateRange.startDate) ? objMyPatients.dateRange.startDate.format('YYYY-MM-DD') : objMyPatients.dateRange.startDate);
			objMyPatients.filterOptions.endDate =  objMyPatients.dateRange.endDate == null ? null : (!angular.isString(objMyPatients.dateRange.endDate) ? objMyPatients.dateRange.endDate.format('YYYY-MM-DD') : objMyPatients.dateRange.endDate);
		}catch(e){
			
		}
    	
        myPatientsService.getPatientsByFilter(objMyPatients.filterOptions).then(
    			function successCallback(response) {

    				objMyPatients.patientData = response.data;			
    				if(objMyPatients.patientData.length > 0) {
    					objMyPatients.isMyPatientDataFound = true;
    					objMyPatients.totalPatientRecords = response.data[0].totalCount;
    				} else {
    					objMyPatients.isMyPatientDataFound = false;
    				}
    			}, 
    			function errorCallback(response) {
    				showIPMessage('Error occured while loading Flowsheet Templates', 'inputelm2','ErrorMsg');
    			}
        );
        
        objMyPatients.setFilterForSaving();
    }
	objMyPatients.getFlowsheetItems = function (patient, index) {
		
		if(!$("#data_"+patient.patientId).hasClass('collapse in')){
	        myPatientsService.getFlowsheetItems(patient, objMyPatients.selectedTemplate, objMyPatients.datesToShow).then(
	    			function successCallback(response) {
	
	    				objMyPatients.patientData[index].patientContent = response.data;
	    			}, 
	    			function errorCallback(response) {
	    				showIPMessage('Error occured while loading Flowsheet Templates', 'inputelm2','ErrorMsg');
	    			}
	        );
	        
	        $(".patientRow").not("#data_"+patient.patientId).removeClass('collapse in').addClass('collapse');
	        $(".iconPatientRow").not("#icon_"+patient.patientId).addClass('collapsed');
	        
	        objMyPatients.selectedPatient = patient;
	    	objMyPatients.selectedIndex = index;
		}
    };
    
    objMyPatients.populateConfigFilterValues = function(){
		
    	objMyPatients.isFilterApplied = true ;
    	
    	objMyPatients.dateRange = {
	        startDate: null,
	        endDate: null
    	};
		
    	objMyPatients.dateRange.startDate = objMyPatients.jsonFilterDetails.selectedFilter.filterValues["StartDate"] == "" ? null :  objMyPatients.jsonFilterDetails.selectedFilter.filterValues["StartDate"];
    	objMyPatients.dateRange.endDate = objMyPatients.jsonFilterDetails.selectedFilter.filterValues["EndDate"] == "" ? null :  objMyPatients.jsonFilterDetails.selectedFilter.filterValues["EndDate"];
		
    	objMyPatients.selectedFacility = JSON.parse(objMyPatients.jsonFilterDetails.selectedFilter.filterValues["Facility"]);
    	objMyPatients.selectedDept = JSON.parse(objMyPatients.jsonFilterDetails.selectedFilter.filterValues["Department"]);
		objMyPatients.selectedTemplate = JSON.parse(objMyPatients.jsonFilterDetails.selectedFilter.filterValues["SelectedTemplate"]);
		
		objMyPatients.showPatients(objMyPatients.selectedTemplate);
	};
	
	objMyPatients.fetchFilterDetails = function (){
		objMyPatients.setFilterForSaving();
	};
	
	objMyPatients.setFilterForSaving = function() {
		if(objMyPatients.jsonFilterDetails.toSaveFilter.filterValues != null && objMyPatients.jsonFilterDetails.toSaveFilter.filterValues != undefined){
			
			objMyPatients.jsonFilterDetails.toSaveFilter.filterValues["SelectedTemplate"] = JSON.stringify(objMyPatients.selectedTemplate) ;
			objMyPatients.jsonFilterDetails.toSaveFilter.filterValues["Department"] = JSON.stringify(objMyPatients.selectedDept);
			objMyPatients.jsonFilterDetails.toSaveFilter.filterValues["Facility"] = JSON.stringify(objMyPatients.selectedFacility) ;		
			
			objMyPatients.jsonFilterDetails.toSaveFilter.filterValues["StartDate"] = objMyPatients.dateRange.startDate == null ? "" :  (!angular.isString(objMyPatients.dateRange.startDate) ? objMyPatients.dateRange.startDate.format('YYYY-MM-DD') : objMyPatients.dateRange.startDate);
			objMyPatients.jsonFilterDetails.toSaveFilter.filterValues["EndDate"] = objMyPatients.dateRange.endDate == null ? "" :  (!angular.isString(objMyPatients.dateRange.endDate) ? objMyPatients.dateRange.endDate.format('YYYY-MM-DD') : objMyPatients.dateRange.endDate);
		}
	};
	
	objMyPatients.closeFilter = function (){
		
		objMyPatients.isFilterApplied = false ;
		
		objMyPatients.filterOptions = {
			selectedFacility: 0,
			flowsheetId: 0,
			deptId: 0,
			selectedPage : 1,
			recordsPerPage : 10,
			startDate: null,
			endDate: null
		};
		
		objMyPatients.dateRange = {
	        startDate: null,
	        endDate: null
		};
		
		objMyPatients.selectedFacility = {facilityId : 0 , facilityName : ""};
		objMyPatients.selectedDept = {};
		objMyPatients.selectedTemplate = {}; 
		objMyPatients.selectedTemplateName = "";
		
		//objMyPatients.showPatients(objMyPatients.selectedTemplate);
		
	};	
	objMyPatients.clearFilter = function() {
		objMyPatients.closeFilter();
	}
	objMyPatients.setResetDept=function(){

		if(objMyPatients.selectedDept.deptName == ""){
			objMyPatients.selectedDept = {departmentId:0};
		}
		objMyPatients.selectedUnit = {unitId:0};
	}
	
	objMyPatients.launchPharmacyTab = function (patientId, encounterId, encounterType, episodeEncounterId, departmentId, serviceType){	
		var UrlPath = "#r=pharmacyWorkQueue.go/launchPharmacyTab/" + patientId +"/"+encounterId+"/"+encounterType+"/"+episodeEncounterId+"/"+departmentId+"/"+serviceType;;
		$window.location = UrlPath;
	};
	
    objMyPatients.valuePopover = function (e,data, showValueColumn) {
    	objMyPatients.valuesToShow = data;
    	objMyPatients.showValueColumn = !showValueColumn;
		var _that = angular.element(e.target);
		$('#value-row-popup').show().animate({}, 100, function () {
			$(this).position({
				of: _that,
				my: 'left+23 top-15',
				at: 'left top'
			});
		});
		
		$('#value-row-popup .value-row-arrow').show().animate({}, 100, function () {
			$(this).position({
				of: _that,
				my: 'left+23 top+10',
				at: 'left top'
			});
		});

    };
    
    objMyPatients.scrollDate = function(flag){
    	var d = null;
    	if(flag == 'L'){
    		d = objMyPatients.datesToShow[0];
    		var dt = new Date(d);
    		dt.setTime(dt.getTime()- (24*60*60*1000));
    		objMyPatients.datesToShow.unshift(dt.toLocaleDateString('en-US', {year:"numeric", month:"2-digit", day:"2-digit"}));
        	objMyPatients.datesToShow.pop();
    	} else {
    		d = objMyPatients.datesToShow[objMyPatients.datesToShow.length - 1];
    		var dt = new Date(d);
    		dt.setTime(dt.getTime() + (24*60*60*1000));
    		objMyPatients.datesToShow.push(dt.toLocaleDateString('en-US', {year:"numeric", month:"2-digit", day:"2-digit"}));
    		objMyPatients.datesToShow.shift();
    	}
    	
    	if(objMyPatients.selectedIndex != -1 ){
	    	myPatientsService.getFlowsheetItems(objMyPatients.selectedPatient, objMyPatients.selectedTemplate, objMyPatients.datesToShow).then(
				function successCallback(response) {
	
					objMyPatients.patientData[objMyPatients.selectedIndex].patientContent = response.data;
				}, 
				function errorCallback(response) {
					showIPMessage('Error occured while loading Flowsheet Templates', 'inputelm2','ErrorMsg');
				}
	        );
    	}
    	
    };

    //close confirmation popover on mouse leave
    objMyPatients.closePopover = function(){
        $('#value-row-popup .value-row-arrow').hide();
         $('#value-row-popup').hide();
    }
    
    objMyPatients.checkAllOrders = function () {
        for (var row of objMyPatients.patientData) {
            row.selected = objMyPatients.allSelected;
        }
    };

    objMyPatients.checkEntity = function () {
    	
    	 for (var row of objMyPatients.patientData) {
             if(!row.selected){
            	 objMyPatients.allSelected = false;
            	 return;
             }
         }
    	 objMyPatients.allSelected = true;
    };
    
    /* perfect scrollbar on window resize code starts here  */
    $('#ph-pending-filter1').on('hidden.bs.collapse', function () {
        var htmlhgt = $('html').height();
        var tablehgt = htmlhgt - 295;
        $('.ph-mypatient-scroll-table').height(tablehgt);
        $('.ph-mypatient-scroll-table').perfectScrollbar({
            suppressScrollX: 'false'
        });
    });
    
    $(window).resize();
	$('.ph-mypatient-scroll-table').perfectScrollbar();   

}).service('myPatientsService',['$http',myPatientsService]);

$(window).resize(function () {
	var calculatedHgt = $('html').height() - 295;
    $('.ph-mypatient-scroll-table').height(calculatedHgt);     
});
