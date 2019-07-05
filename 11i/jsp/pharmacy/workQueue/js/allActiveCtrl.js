function pharmacyQueueService($http, $modal){
	var service = this;

	service.getWorkQueueData = function (filterOptions){
		return $http({
			method: 'POST',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getWorkQueueData'),
			data:{
				"selectedPage": filterOptions.selectedPage,
				"recordsPerPage": filterOptions.recordsPerPage,
				"priority": filterOptions.selectedPriority,
				"pharmacyStatuses": filterOptions.pharmacyStatuses,
				"selectedFacility": filterOptions.facilityId,
				"isUnassigned": filterOptions.isUnassigned,
				"sortByPatients": filterOptions.sortByPatient,
				"deptId": filterOptions.departmentId,
				"unitId": filterOptions.unitId,
				"startDate": filterOptions.startDate,
				"endDate": filterOptions.endDate,
				"assignedToId": filterOptions.assignedToId,
				"patientId": filterOptions.selectedPatient,
				"selectedSortedBy": filterOptions.selectedSortedBy,
				"sortedOrder": filterOptions.sortedOrder,
				"workFlowType": filterOptions.workFlowType,
				"loggedInUserId": global.TrUserId
			}
		});
	};
	
	service.updatePharmacyStatus = function(statusId, selectedElement, reasonId){

		reasonId = reasonId == null || reasonId == undefined ? 0 : reasonId;
		var param = $.param({
			status: statusId,
			reasonId: reasonId
			
		});
		return $http({
			method: 'POST',
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/updatePharmacyStatus?'+param+"&verificationId="+ selectedElement+"&verifiedBy="+global.TrUserId+"&modifiedBy="+global.TrUserId)
		});
	};
	
	service.assignOrders = function(assignToId, selectedElements ){
		
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/assignOrders?assignToId="+assignToId+"&workQueueIds="+ selectedElements+"&assignedBy="+global.TrUserId+"&modifiedBy="+global.TrUserId)
		});
	};
}

angular.module('pharmacyAllActiveModule', ['ui.bootstrap', 'daterangepicker','ui.mask', 'ecw.dir.inlineFacilityLookup','stafflookup',
			'patientlookup', 'patientlookupinline', 'ecw.dir.inlineDepartmentLookup','ecw.dir.inlineUnitLookup','providerlookup',
			'inPatient.dir.filterTemplate','insulinScaleViewApp','perfect_scrollbar'])
.controller("pharmacyAllActiveCtrl", function ($scope, $timeout, $compile, $window, $filter, $http, pharmacyQueueService, dateRangeOption, toastrService) {

	var vm = this ;
	angular.element('.config-preview').hide();
	vm.dirOptions = {};
	vm.filterPatient={}; 
	vm.filterPatient.patient={};
	var servicePharmacyQueue = pharmacyQueueService;
	vm.selectedTab = $scope.objPharmacyQueueTabs.selectedTabName;
	$scope.objPharmacyQueueTabs.objPharmacyQueue = vm;
	vm.rangeDateOpt = dateRangeOption;
	vm.selectedSortedBy = "";
	vm.sortedOrder = "";
	vm.dateRange = {
        startDate: null,
        endDate: null
    };
	vm.selectedFacility = {facilityId : 0,facilityName : ""}
	vm.jsonFilterDetails = {toSaveFilter : {}, selectedFilter : {}, configuredFilters : {} };
	
	vm.filterOptions = {
			selectedPage : 1,
			recordsPerPage : 10,
			selectedPriority : 0,
			pharmacyStatuses : [],
			facilityId : 0,
			isUnassigned : 0,
			sortByPatient : 0,
			departmentId : 0,
			unitId : 0,
			startDate : 0,
			endDate : 0,
			assignedToId : 0,
			selectedPatient : 0,
			workFlowType : 0,
			selectedSortedBy :vm.selectedSortedBy,
			sortedOrder:vm.sortedOrder
	};
	
	vm.isFilterApplied = false ;
	
	vm.lastRefreshTime = "";
	
	vm.staffNameFilter	= {staff : {}};
	
	vm.assignedStaffId	= -1;
	vm.staffName	= {staff : {}};
	
	vm.priorityName = "All";
	
	vm.phAllActiveData = [];
	vm.pharmacyStatusList = [];
	vm.orderPriorityList = [];
	vm.pendingReasonList = [];
	vm.hasUpdateMedOrderAccess = false;

	vm.selectedReason = 0;
	vm.selectedStatus = 0;
	
	vm.totalWorkQueueRecords = 0;
	vm.totalPages = 0;
	vm.tabId = 0;
	vm.workFlowType = 0;
	
	//vm.selectedFacility = {};
	vm.selectedDept = {departmentId:0};
	vm.selectedUnit = {unitId:0};	
	
	vm.selectPatient= function(){		
		vm.filterOptions.selectedPatient =	vm.filterPatient.patient.id;					
	}

	vm.clearPatient = function (){
		vm.filterOptions.selectedPatient = 0;
		vm.filterPatient = {};
		vm.filterPatient.patient = {};
	};
	
	vm.getAssignedStaff = function() 
	{
		vm.assignedStaffId	= vm.staffName.staff.id;
		return vm.assignedStaffId;
	};
	vm.setOrderTypeRecord = function(type) {
		vm.workFlowType = type;
		if(vm.workFlowType == 2 || vm.workFlowType == 0) {	
			vm.isFilterApplied = false ;
			vm.filterOptions = {
					selectedPage : 1,
					recordsPerPage : 10,
					selectedPriority : 0,
					departmentId : 0,
					unitId : 0					
			};
			vm.setPharmacyStatus();
			vm.selectedDept = {departmentId:0};
			vm.selectedUnit = {unitId:0};
		}
	};
	
	vm.clearStaff		= function()
	{
		vm.stafflookup.name	= "";
	};
	
	vm.onSelectFacility= function(){
		vm.selectedDept = {departmentId:0};
		vm.selectedUnit = {unitId:0};
	};
	
	vm.populateConfigFilterValues = function(){
		
		vm.dateRange = {
				startDate: null,
				endDate: null
		};
		
		vm.isFilterApplied = true ;
		
		vm.dateRange.startDate = vm.jsonFilterDetails.selectedFilter.filterValues["StartDate"] == "" ? null :  vm.jsonFilterDetails.selectedFilter.filterValues["StartDate"];
		vm.dateRange.endDate = vm.jsonFilterDetails.selectedFilter.filterValues["EndDate"] == "" ? null : vm.jsonFilterDetails.selectedFilter.filterValues["EndDate"];
		
		vm.filterOptions.pharmacyStatuses = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["PharmacyStatus"]);
		vm.filterOptions.selectedPriority = vm.jsonFilterDetails.selectedFilter.filterValues["Priority"];
		vm.filterOptions.isUnassigned = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Unassigned"]);
		vm.filterOptions.sortByPatient = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["GroupByPatient"]);
		
		vm.staffNameFilter = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["AssignedTo"]);
		vm.selectedFacility = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Facility"]);
		vm.filterPatient = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Patient"]);
		vm.selectedDept = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Department"]);
		vm.selectedUnit = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Unit"]);
		
		if(vm.filterOptions.selectedPriority == 0){
			vm.priorityName = "All";
		} else {
			angular.forEach(vm.orderPriorityList, function(row) {
				if(row.id == vm.filterOptions.selectedPriority){
					vm.priorityName = row.priorityName;
					return;
				}
			});
		}
		
		vm.setAssignedTo();
		
		vm.getWorkQueueData();
	};
	
	vm.fetchFilterDetails = function (){
		vm.setFilterForSaving();
	};
	
	vm.closeFilter = function (){
		
		vm.isFilterApplied = false ;
		
		vm.filterOptions = { 
				selectedPage : 1,
				recordsPerPage : 10,
				selectedPriority : 0,
				pharmacyStatuses: [],				
				isUnassigned : 0,
				sortByPatient : 0,
				departmentId : 0,
				unitId : 0,
				startDate : "",
				endDate : "",
				assignedToId : 0,
				workFlowType : 0,
				selectedPatient : 0,
				selectedSortedBy :"",
				sortedOrder:""
		};
		
		vm.setPharmacyStatus();
		
		vm.staffNameFilter = {staff : {}};
		vm.filterPatient = {};
		vm.filterPatient.patient = {};
		
		vm.selectedDept = {departmentId:0};
		vm.selectedUnit = {unitId:0};		
		vm.pharmacyStatusName = "All";
		vm.priorityName = "All";
		$("#spnPriority").text("All");
		$(".clear-txt").text("");
		
		$("#divAssignedTo").find("input").val("");
		
		vm.getWorkQueueData();
		
	};
	
	vm.setPharmacyStatus = function(){
		if(vm.selectedTab.toUpperCase() === "PENDING"){		
			vm.filterOptions.pharmacyStatuses = [1];
		} else if(vm.selectedTab.toUpperCase() === "VERIFIED"){
			vm.filterOptions.pharmacyStatuses = [2];
		} else if(vm.selectedTab.toUpperCase() === "ALL"){
			vm.filterOptions.pharmacyStatuses = [1,3,5];
		}
	};
	vm.setDefaultTodayDate = function() {
        todayDate=getDateTime().substring(0,10);  
        lastDate = vm.addDays(todayDate, -30);
        vm.dateRange.startDate = lastDate;     
        vm.dateRange.endDate = todayDate;
	};
	vm.loadDefaultFacility = function() {
		if (vm.selectedFacility.facilityId === 0 || vm.selectedFacility.facilityId === "") {
			vm.selectedFacility.facilityId = global.facilityId;
			vm.selectedFacility.facilityName = getFacilityName(global.facilityId);
		}
	};
	vm.addDays = function(s, days) {
		  var d = vm.parseMDY(s);
		  d.setDate(d.getDate() + Number(days));
		  return vm.formatMDY(d);
	};
	// Given a string in m/d/y format, return a Date
	vm.parseMDY = function(s) {
	  var b = s.split(/\D/);
	  return new Date(b[2], b[0]-1, b[1]);
	};

	// Given a Date, return a string in m/d/y format
	vm.formatMDY = function(d) {
	  function z(n){return (n<10?'0':'')+n}
	  if (isNaN(+d)) return d.toString();
	  return z(d.getMonth()+1) + '/' + z(d.getDate()) + '/' + d.getFullYear();
	};
	
	vm.isValidDate = function(dateString)
	{
	    // First check for the pattern
	    if(!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(dateString))
	        return false;

	    // Parse the date parts to integers
	    var parts = dateString.split("/");
	    var day = parseInt(parts[1], 10);
	    var month = parseInt(parts[0], 10);
	    var year = parseInt(parts[2], 10);

	    // Check the ranges of month and year
	    if(year < 1000 || year > 3000 || month == 0 || month > 12)
	        return false;

	    var monthLength = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

	    // Adjust for leap years
	    if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
	        monthLength[1] = 29;

	    // Check the range of the day
	    return day > 0 && day <= monthLength[month - 1];
	};
	vm.init = function (){
		vm.loadDefaultFacility();
		vm.setDefaultTodayDate();
		
		vm.setPharmacyStatus();
	
		vm.getWorkQueueData();
		
		$(window).resize();
	};	
	
	vm.getWorkQueueData = function(){
		if(1===global.checkFromMedProfile){
			if(!(!global.pharmacyQueueFilter)){
				vm.applyPharmacyQueueFilter();
			}
		}
		
		if(vm.filterOptions.pharmacyStatuses.length === 0){
			vm.setPharmacyStatus();
		}
		
		vm.filterOptions.facilityId = vm.selectedFacility.facilityId == null || vm.selectedFacility.facilityId == undefined ? 0 : vm.selectedFacility.facilityId;
		vm.filterOptions.departmentId = vm.selectedDept.departmentId == null || vm.selectedDept.departmentId == undefined ? 0 : vm.selectedDept.departmentId;
		vm.filterOptions.unitId = vm.selectedUnit.unitId == null || vm.selectedUnit.unitId == undefined ? 0 : vm.selectedUnit.unitId;
		
		if((vm.filterOptions.facilityId == 0 && (vm.filterOptions.departmentId != 0 || vm.filterOptions.unitId != 0 ))
			||
			((vm.filterOptions.facilityId == 0 || vm.filterOptions.departmentId == 0) && vm.filterOptions.unitId != 0 )
		){
			showIPMessage("Please select a facility and reset Department and/or Unit");
			return;
		}
		var isFound = vm.isValidDate(vm.dateRange.startDate);
		if(isFound) {
			vm.filterOptions.startDate = vm.dateRange.startDate.replace(/(\d\d)\/(\d\d)\/(\d{4})/, "$3-$1-$2");
			vm.filterOptions.endDate =  vm.dateRange.endDate.replace(/(\d\d)\/(\d\d)\/(\d{4})/, "$3-$1-$2");
		} else {
			vm.filterOptions.startDate = vm.dateRange.startDate == null ? "" : (!angular.isString(vm.dateRange.startDate) ? vm.dateRange.startDate.format('YYYY-MM-DD') : vm.dateRange.startDate);
			vm.filterOptions.endDate =  vm.dateRange.endDate == null ? "" :  (!angular.isString(vm.dateRange.endDate) ? vm.dateRange.endDate.format('YYYY-MM-DD') : vm.dateRange.endDate);
		}
		
		vm.filterOptions.selectedPatient = (vm.filterPatient.patient.id == "" || vm.filterPatient.patient.id == null || vm.filterPatient.patient.id == undefined) ? 0 : vm.filterPatient.patient.id;
		vm.filterOptions.workFlowType = vm.workFlowType;
		servicePharmacyQueue.getWorkQueueData(vm.filterOptions).then(
			function successCallback(response) {
				
				vm.phAllActiveData = response.data.workQueue;
				vm.totalWorkQueueRecords = response.data.totalWorkQueueRecords;
				
				$scope.objPharmacyQueueTabs.workQueueCount = response.data.totalWorkQueueRecords;
				
				vm.recordsPerPage = response.data.recordsPerPage;
				vm.totalPages = response.data.totalPages;
				
				if(vm.totalPages < vm.filterOptions.selectedPage){
					vm.filterOptions.selectedPage = 1;
				}

				vm.pharmacyStatusList = response.data.pharmacyStatusList;
				vm.orderPriorityList = response.data.orderPriorityList;
				vm.pendingReasonList = response.data.pendingReasonList;
				
				vm.hasUpdateMedOrderAccess = response.data.hasUpdateMedOrderAccess;
				
				$timeout(function() {
					angular.element('#order-status').multiselect({
						includeSelectAllOption: true,
						allSelectedText: 'All Selected',
			            onChange: function(option, checked, select) {
			            }
					});
					/*angular.element('#order-status').multiselect('selectAll', false);
					angular.element('#order-status').multiselect('updateButtonText');*/
					 
					angular.element('#order-status').multiselect('rebuild');
				}, 1, false);
			}, 
			function errorCallback(response) {
				showIPMessage('Error occured while loading Pharmacy Work Queue Data', 'inputelm2','ErrorMsg');
			}
		);
		
		vm.setFilterForSaving();
	};
	vm.setSearchByValue = function(selectedSort, sortedOrder)
	{	
		vm.selectedSortedBy = selectedSort;
		if(sortedOrder)
			vm.sortedOrder = "DESC";
		else
			vm.sortedOrder = "ASC";
		vm.filterOptions.selectedSortedBy = vm.selectedSortedBy;
		vm.filterOptions.sortedOrder = vm.sortedOrder;
		vm.getWorkQueueData();
	}
	vm.applyPharmacyQueueFilter = function (){
		vm.dateRange.startDate = global.pharmacyQueueFilter.startDate;
		vm.dateRange.endDate = global.pharmacyQueueFilter.endDate;
		vm.filterOptions.pharmacyStatuses = global.pharmacyQueueFilter.pharmacyStatuses
		vm.filterOptions.selectedPriority = global.pharmacyQueueFilter.selectedPriority;
		vm.filterOptions.isUnassigned = global.pharmacyQueueFilter.isUnassigned;
		vm.filterOptions.sortByPatient = global.pharmacyQueueFilter.groupByPatient;
		
		vm.staffNameFilter = global.pharmacyQueueFilter.staffNameFilter;
		vm.selectedFacility = global.pharmacyQueueFilter.facilityInfo;
		vm.filterPatient = global.pharmacyQueueFilter.patientInfo ;
		vm.selectedDept = global.pharmacyQueueFilter.selectedDept;
		vm.selectedUnit = global.pharmacyQueueFilter.selectedUnit;
		vm.orderPriorityList = global.pharmacyQueueFilter.orderPriorityList;
		vm.workFlowType = global.pharmacyQueueFilter.workFlowType;
		if(vm.filterOptions.selectedPriority == 0){
			vm.priorityName = "All";
		} else {
			angular.forEach(vm.orderPriorityList, function(row) {
				if(row.id == vm.filterOptions.selectedPriority){
					vm.priorityName = row.priorityName;
					return;
				}
			});
		}
		
		vm.setAssignedTo();
		
		global.pharmacyQueueFilter = {};
		global.checkFromMedProfile = "";
	};
	
	vm.setFilterForBackToWorkQueue = function (){
		global.pharmacyQueueFilter={};
		global.pharmacyQueueFilter.assignedTo = vm.staffNameFilter;
		global.pharmacyQueueFilter.selectedDept = vm.selectedDept;
		global.pharmacyQueueFilter.selectedUnit = vm.selectedUnit;
		global.pharmacyQueueFilter.facilityInfo = vm.selectedFacility;
		global.pharmacyQueueFilter.patientInfo = vm.filterPatient;
		global.pharmacyQueueFilter.startDate = vm.dateRange.startDate;
		global.pharmacyQueueFilter.endDate = vm.dateRange.endDate;
		global.pharmacyQueueFilter.pharmacyStatuses = vm.filterOptions.pharmacyStatuses;
		global.pharmacyQueueFilter.selectedPriority = vm.filterOptions.selectedPriority ;
		global.pharmacyQueueFilter.groupByPatient = vm.filterOptions.sortByPatient ;
		global.pharmacyQueueFilter.staffNameFilter = vm.staffNameFilter;
		global.pharmacyQueueFilter.isUnassigned = vm.filterOptions.isUnassigned ;
		global.pharmacyQueueFilter.orderPriorityList = vm.orderPriorityList; 
		global.pharmacyQueueFilter.workFlowType = vm.workFlowType;
		
	};
	
	vm.setFilterForSaving = function() {
		if(vm.jsonFilterDetails.toSaveFilter.filterValues != null && vm.jsonFilterDetails.toSaveFilter.filterValues != undefined){
			vm.jsonFilterDetails.toSaveFilter.filterValues["AssignedTo"] = JSON.stringify(vm.staffNameFilter) ;
			vm.jsonFilterDetails.toSaveFilter.filterValues["Department"] = JSON.stringify(vm.selectedDept);
			vm.jsonFilterDetails.toSaveFilter.filterValues["Unit"] = JSON.stringify(vm.selectedUnit) ;		
			vm.jsonFilterDetails.toSaveFilter.filterValues["Facility"] = JSON.stringify(vm.selectedFacility) ;		
			vm.jsonFilterDetails.toSaveFilter.filterValues["Patient"] = JSON.stringify(vm.filterPatient);
			
			vm.jsonFilterDetails.toSaveFilter.filterValues["StartDate"] = vm.dateRange.startDate == null ? "" : (!angular.isString(vm.dateRange.startDate) ? vm.dateRange.startDate.format('YYYY-MM-DD') : vm.dateRange.startDate);
			vm.jsonFilterDetails.toSaveFilter.filterValues["EndDate"] = vm.dateRange.endDate == null ? "" :  (!angular.isString(vm.dateRange.endDate) ? vm.dateRange.endDate.format('YYYY-MM-DD') : vm.dateRange.endDate);
				
			vm.jsonFilterDetails.toSaveFilter.filterValues["PharmacyStatus"] = JSON.stringify(vm.filterOptions.pharmacyStatuses);
			vm.jsonFilterDetails.toSaveFilter.filterValues["Priority"] = vm.filterOptions.selectedPriority + "" ;
			vm.jsonFilterDetails.toSaveFilter.filterValues["GroupByPatient"] = JSON.stringify(vm.filterOptions.sortByPatient) ;
			vm.jsonFilterDetails.toSaveFilter.filterValues["Unassigned"] = JSON.stringify(vm.filterOptions.isUnassigned ) ;
			
		}
	};
	
	vm.setAssignedTo = function() {
		if(vm.staffNameFilter.staff.id != undefined && vm.staffNameFilter.staff.id != null ){
			vm.filterOptions.assignedToId = vm.staffNameFilter.staff.id;
			
			$("#divAssignedTo").find("input").val(vm.staffNameFilter.staff.name);
		} else {
			vm.filterOptions.assignedToId = 0;
			$("#divAssignedTo").find("input").val("");
		}
		
	};
	
	vm.clearAssignedTo = function() {
		vm.filterOptions.assignedToId = 0;
	};

	vm.setOrderPriority = function(priority){
		if(priority){
			vm.filterOptions.selectedPriority = priority.id;
			vm.priorityName = priority.priorityName;
		} else {
			vm.filterOptions.selectedPriority = 0;
			vm.priorityName = "All";
		}
		
	};
	vm.clearFilter = function(){
		vm.closeFilter();
	};
	vm.setReason = function(reason){
		vm.selectedReason = reason.id;
	};

	vm.filterWorkQueueData = function(){
		if(!vm.ValidateMmandatoryFeilds()){	
			vm.isFilterApplied = true ;
			vm.filterOptions = {
					selectedPage : 1,
					recordsPerPage : 10,
					selectedPriority:vm.filterOptions.selectedPriority,
					pharmacyStatuses:vm.filterOptions.pharmacyStatuses
			};
			//vm.setPharmacyStatus();
			vm.getWorkQueueData();
		}
	};
	vm.ValidateMmandatoryFeilds = function() {
		vm.emptyField_flag = false;
		vm.filterOptions.facilityId = vm.selectedFacility.facilityId == null || vm.selectedFacility.facilityId == undefined ? 0 : vm.selectedFacility.facilityId;
		var isFound = vm.isValidDate(vm.dateRange.startDate);
		if(isFound) {
			vm.filterOptions.startDate = vm.dateRange.startDate.replace(/(\d\d)\/(\d\d)\/(\d{4})/, "$3-$1-$2");
			vm.filterOptions.endDate =  vm.dateRange.endDate.replace(/(\d\d)\/(\d\d)\/(\d{4})/, "$3-$1-$2");
		} else {
			vm.filterOptions.startDate = vm.dateRange.startDate == null ? "" : (!angular.isString(vm.dateRange.startDate) ? vm.dateRange.startDate.format('YYYY-MM-DD') : vm.dateRange.startDate);
			vm.filterOptions.endDate =  vm.dateRange.endDate == null ? "" :  (!angular.isString(vm.dateRange.endDate) ? vm.dateRange.endDate.format('YYYY-MM-DD') : vm.dateRange.endDate);
		}
		if((vm.filterOptions.startDate != null || vm.filterOptions.startDate !== 0 ) && (vm.filterOptions.endDate != null || vm.filterOptions.endDate !== 0)){
			var startDateVal= new Date(vm.filterOptions.startDate);
			var endDateVal = new Date(vm.filterOptions.endDate); 			
			var timeDiff = Math.abs(endDateVal.getTime() - startDateVal.getTime());
			var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 			
		}	
		if (vm.filterOptions.facilityId == 0) {		
			toastrService.ecwMessage(toastrService.ERROR, "Please specify facility.");
			vm.emptyField_flag = true;
		} else if (vm.dateRange.endDate === null && vm.dateRange.startDate === null) {
			toastrService.ecwMessage(toastrService.ERROR, "Please specify order start date range.");
			vm.emptyField_flag = true;
		} else if(diffDays > 30 ) {			
			toastrService.ecwMessage(toastrService.ERROR, "Selected date range can not exceed more than 30 days.");
			vm.emptyField_flag = true;
		}
		return vm.emptyField_flag;
	};
	
	vm.getSelectedWorkQueues = function(){
		var selectedElements = [];
		angular.forEach(vm.phAllActiveData, function(row) {
			if(row.selected){
				selectedElements.push(row.id);
			}
		});
		
		return selectedElements ;
	};
	
	vm.getSelectedElement = function(){
		var selectedEl = 0;
		
		angular.forEach(vm.phAllActiveData, function(row) {
			if(row.selected){
				selectedEl = row.id;
				return;
			}
		});
		
		return selectedEl ;
	};
	
	vm.updateStatus = function(status){
		
		var selectedElements = vm.getSelectedWorkQueues();
		
		if(selectedElements.length == 0 || selectedElements.length > 1){
			showIPMessage("Please select one order to update status");
		} else {

			if(status.status.toUpperCase() == "PENDING"){
				vm.selectedStatus = status.id;
				$("#addReasonModal").modal("show");
			} else {
				
				var selectedEl = vm.getSelectedElement();

				servicePharmacyQueue.updatePharmacyStatus(status.id, selectedEl).then(
					function successCallback(response) {
						vm.getWorkQueueData();
					}
				);
			}
		}
	};

	vm.updateReason = function(){
		
		var selectedEl = vm.getSelectedElement();

		servicePharmacyQueue.updatePharmacyStatus(vm.selectedStatus, selectedEl, vm.selectedReason).then(
			function successCallback(response) {
				vm.getWorkQueueData();
				$("#addReasonModal").modal("hide");
			}
		);
	};
	
	vm.setResetDept=function(){

		if(vm.selectedDept.deptName == ""){
			vm.selectedDept = {departmentId:0};
		}
		vm.selectedUnit = {unitId:0};
	}
	
	vm.setResetUnit=function(){
		
		if(vm.selectedUnit.unitName == ""){
			vm.selectedUnit = {};
		}
	}

	vm.groupByName = function (byName) {
		if (byName) {
			vm.poppedEle = vm.phAllActiveData.pop();
			vm.unShiftEle = vm.phAllActiveData.unshift(vm.poppedEle);
		} else {
			vm.shiftEle = vm.phAllActiveData.shift();
			vm.phAllActiveData.push(vm.shiftEle);
		}
	}
	
	vm.launchPharmacyTab = function (patientId, encounterId, encounterType, episodeEncounterId, departmentId, serviceType){	
		vm.setFilterForBackToWorkQueue();
		var UrlPath = "#r=pharmacyWorkQueue.go/launchPharmacyTab/" + patientId +"/"+encounterId+"/"+encounterType+"/"+episodeEncounterId+"/"+departmentId+"/"+serviceType;
		$window.location = UrlPath;
	}

	vm.selectTempDropAllactive = function () {
		$('#select-temp-drop-allactive').hide();
		$('.clearable').val('');
	};

	$('#select-temp-drop-allactive').hide();

	vm.showTempDropAllActive = function (e) {
		e.stopPropagation();
		var _that = e.target;
		$('#select-temp-drop-allactive').toggle().animate({}, 100, function () {
			$(this).position({
				of: _that,
				my: 'right top+25',
				at: 'right+1 top',
				collision: "flipfit"
			}).animate({
				"opacity": 1
			}, 100)
		});
	};

	angular.element(document).on('click', function (e) {
		var container = $('#select-temp-drop-allactive');
		if (!container.is(e.target) /* if the target of the click isnt the container... */ && container.has(e.target).length === 0) /*nor a descendant of the container*/ {
			container.hide();
		}
	});

	/* for underAge Icon pop up function starts here */
	$('.signal-dropdown').hide();
	vm.underAgeDropDown = function (e) {
		var _that = e.target;
		$('.signal-dropdown').toggle().animate({}, 100, function () {
			$(this).position({
				of: _that,
				my: 'right top',
				at: 'right+173 top+20',
				collision: "flipfit"
					//offset: '10 10'
			}).animate({
				"opacity": 1
			}, 100)
		});
		e.stopPropagation();
	}

	angular.element(document).on('click', function (e) {
		var signalDrop = $('.signal-dropdown');
		if (!signalDrop.is(e.target) && signalDrop.has(e.target).length === 0) /*nor a descendant of the container*/ {
			signalDrop.hide();
		}
	});

	/* for underAge Icon pop up --function-- ends here--*/

	vm.showIcon = function (e) {
		$(e.currentTarget).closest('tr').find('.upper').removeClass('active');
		$(e.currentTarget).closest('.upper').addClass('active');
	};

	vm.alertPopupShow = function (e, alertName) {
		if (alertName == "DI") {
			var _that = $(e.target);

			$('.signal').show().animate({}, 100, function () {
				$(this).position({
					of: _that,
					my: 'right top',
					at: 'right+20 bottom+10',
					collision: "flipfit"
				}).animate({
					"opacity": 1
				}, 100)
			});
		}
		e.stopPropagation();
	};

	vm.alertPopupHide = function () {
		$('.signal').hide();
	};

	vm.chkGroupBy = function (byNameStatus) {
		if (byNameStatus == undefined) {
			vm.byName = true;
		} else if (byNameStatus == false) {
			vm.byName = true;
		} else {
			vm.byName = false;
		}
	};

	vm.orders = ['Quarterly report order', 'Basic emergency order', 'Monthly order'];
	vm.addNewOrder = function (newOrder) {
		vm.orders.push(newOrder);
		$('.btn-group ').removeClass('open');
	}

	vm.closeModal = function () {
		$('.btn-group ').removeClass('open');
	};

	vm.checkAll = function () {
		angular.forEach(vm.phAllActiveData, function (item) {
			item.selected = vm.isAllSelected;
		});
	};

	vm.patientToggled = function () {
		vm.isAllSelected = vm.phAllActiveData.every(function (item) {
			return item.selected;
		});
	};
	
	vm.showAssignPopup = function (e) {
		
		var selectedElements = vm.getSelectedWorkQueues();
		
		if(selectedElements.length == 0  || selectedElements.length > 1 ){
			showIPMessage("Please select only one order to assign.");
			return;
		}
		
        var _that = e.target;
        $('.assign-div').show().animate({}, 100, function () {
            $(this).position({
                of: _that,
                my: 'right-10 top+40',
                at: 'right top',
            }).animate({
                "opacity": 1
            }, 100)
        });
    };
    
    vm.closeAssignPopup = function () {
        $('.assign-div').hide();
    };
    
    vm.showAssignPrompt = function () {
    	if(vm.assignedStaffId == -1){
    		showIPMessage("Please select pharmacist.");
    		return;
    	}
        $('#assignprompt').show();
    };
    
    vm.hideAssignPrompt = function () {
        $('#assignprompt').hide();
    };
    
    vm.confirmAssign = function () {
    	
    	var selectedElements = vm.getSelectedWorkQueues();
		
    	vm.hideAssignPrompt();
    	servicePharmacyQueue.assignOrders(vm.getAssignedStaff(), selectedElements).then(
				function successCallback(response) {
					showIPMessage("Orders assigned successfully.");
					vm.getWorkQueueData();
					vm.closeAssignPopup();
				}
		);
    };
    
    vm.setLastRefreshTime = function(){
    	vm.lastRefreshTime = (new Date()).toLocaleString();
    };
    
    vm.openSignPreview = function (data, e) {
        var _that = angular.element(e.target);
        vm.issTempalteData = [];
        vm.issTempalteData = data;      
        angular.element('.config-preview').show().animate({}, 100, function () {
          angular.element('.config-preview').position({ of: _that,
            my: 'right+240 top+25',
            at: 'right top',
            collision: "flipfit",
          })
        });
      }

      vm.closeBlock = function () {
        $('.config-preview').hide();
      }
	
}).service('pharmacyQueueService',['$http',pharmacyQueueService]).directive('outerHeight', function ($timeout, $window) {
	  return {
		    restrict: "EAC",
		    scope: {
		      options: '=',
		      group: '='
		    },
		    link: function (scope, element, attrs) {
		      setTimeout(function () {
		        $('.ord-hgt' + attrs.id).css('height', element.innerHeight());
		        $('.ord-hgt-status' + attrs.id).css('height', element.innerHeight());
		      }, 100);

		      angular.extend(scope.options, {
		        calculateDynamicHeight: function () {
		          setTimeout(function () {
		            var ele = document.getElementsByClassName('tdupper-orderdetails');
		            $('.tdupper-orderdetails').css('height', 'auto');
		            for (var i = 0; i < ele.length; i++) {
		              h = $('.ord-det' + ele[i].id).innerHeight();
		              $('.ord-hgt' + ele[i].id).css('height', h);
		              $('.ord-hgt-status' + ele[i].id).css('height', 'auto');
		            }
		            var e = document.getElementsByClassName('withgroup-orderdetails');
		            for (var j in e) {
		              h1 = $('#' + e[j].id).innerHeight();
		              if (e[j].id != null) {
		                $('.grp-ord-hgt-status' + e[j].id.substring(3)).css('height', 'auto');
		                $('.grp-ord-hgt' + e[j].id.substring(3)).css('height', h1);
		              }
		            }
		          }, 100)
		        },
		      });

		      angular.element($window).bind('resize', function () {
		        scope.options.calculateDynamicHeight();
		      })
		    }
		  }
		});

function resizeQueueTable(){
	var collapseInOutHgt = $('html').height();
	if ($(this).hasClass('collapsed')) {
		$('.ph-allActive-cust-scroll').height(collapseInOutHgt - 250);
	} else {
		$('.ph-allActive-cust-scroll').height(collapseInOutHgt - 370);
	}
}

$(window).resize(function () {
	
	resizeQueueTable();
	
	$('#ph-allActive-module .icon-greenfilter').on('click', function () {
		resizeQueueTable();
	});
	
	
	
});


