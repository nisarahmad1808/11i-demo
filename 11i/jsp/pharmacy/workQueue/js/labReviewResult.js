function labReviewService($http, $modal){
	var labService = this;
	labService.getLabReviewData = function (obj){
		var params = $.param({
			patientId : obj.patientId,
			facilityId : obj.facilityId,
			startDate : obj.startDate,
			endDate : obj.endDate,
			pharmacyReviewStatus : obj.reviewStatus,
			recordsPerPage : obj.recordsPerPage,
			selectedPage : obj.selectedPage,
			DisplayLabResultsInPharmacyQueue: true,
			userId: global.TrUserId
		});
		var url = makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getAllLabReviewList'); 
		return $http({
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			method: 'POST',
			url: url,
			data: params
		});
	},
	
	labService.markLabAsReviewed = function (reportIdList){
		var params = $.param({
			reportIdList: JSON.stringify(reportIdList),
			userId: global.TrUserId
		});
		var url = makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/markLabAsReviewed'); 
		return $http({
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			method: 'POST',
			url: url,
			data: params
		});
	}
	
}	
angular.module('phLabResultsModule', ['ui.bootstrap', 'daterangepicker','ui.mask','ecw.dir.inlineFacilityLookup','inPatient.dir.filterTemplate','patientlookup'])
.controller("phLabResultCtrl", function ($scope, $window, $filter, $http,$ocLazyLoad, dateRangeOption,$modal,labReviewService) {
    labResultObj = this;
	labResultObj.rangeDateOpt = dateRangeOption;
    labResultObj.dateRange = {
        startDate: null,
        endDate: null
    };
    labResultObj.totalCount = 0;
    labResultObj.recordsPerPage = 18;
    labResultObj.selectedPage = 1;
    
    labResultObj.selectedFacility =  {facilityId : 0 , facilityName : ""};
    
    labResultObj.jsonFilterDetails = {toSaveFilter : {}, selectedFilter : {}, configuredFilters : {} };
    
    labResultObj.setPatientInfo = function() {
		labResultObj.selectedPatientId = labResultObj.patientForFilter.patient.id;
	}

	labResultObj.clearPatientData = function (){
		labResultObj.selectedPatientId = 0;
		labResultObj.patientForFilter = {patient:{name:"",id:'0'}};
	}
	
    labResultObj.init = function(){
    	labResultObj.selectedPatientId = 0;
    	labResultObj.reviewStatus = "0";
    	labResultObj.getFilteredLabResult();
    }
    labResultObj.clearFilter = function(){
    	labResultObj.selectedFacility =  {facilityId : 0 , facilityName : ""};
    	labResultObj.reviewStatus = "0";
    	labResultObj.clearPatientData();
        labResultObj.dateRange = {startDate: null,endDate: null };
    };
  
    labResultObj.getFilteredLabResult = function (){
    	var param =[];
    	if(!angular.isString(labResultObj.dateRange.startDate)) { 
			labResultObj.dateRange.startDate = labResultObj.dateRange.startDate == null ? null : labResultObj.dateRange.startDate.format('YYYY-MM-DD');
		}
		if(!angular.isString(labResultObj.dateRange.endDate)) { 
			labResultObj.dateRange.endDate =  labResultObj.dateRange.endDate == null ? null : labResultObj.dateRange.endDate.format('YYYY-MM-DD');
		}
		
		param.startDate = labResultObj.dateRange.startDate;
		param.endDate =  labResultObj.dateRange.endDate;
		param.facilityId = !labResultObj.selectedFacility.facilityId ?'0':labResultObj.selectedFacility.facilityId;
		param.patientId = !labResultObj.selectedPatientId ?'0':labResultObj.selectedPatientId;
		param.recordsPerPage = labResultObj.recordsPerPage;
		param.selectedPage = labResultObj.selectedPage;
		param.reviewStatus = !labResultObj.reviewStatus?'0':labResultObj.reviewStatus;
		labReviewService.getLabReviewData(param).then(
		function(response) {			
			if (response && response !== null && response!=="") {
				if(response.data){
					labResultObj.phAllActiveData = response.data.labReviewList;
					labResultObj.totalCount = response.data.totalLabCount;
					labResultObj.selectedPage = response.data.selectedPage;
					   labResultObj.fetchFilterDetails();
				}
			} else {
				showIPMessage('Error occured while loading Lab Review Result', 'inputelm2','ErrorMsg');
			}		
		},
		function(response) {
			showIPMessage('Error occured while loading Lab Review Result', 'inputelm2','ErrorMsg');
		});
		
    }
    
    labResultObj.fetchFilterDetails = function() {
		if(labResultObj.jsonFilterDetails.toSaveFilter.filterValues != null && labResultObj.jsonFilterDetails.toSaveFilter.filterValues != undefined){
			labResultObj.jsonFilterDetails.toSaveFilter.filterValues["Facility"] = JSON.stringify(labResultObj.selectedFacility) ;		
			labResultObj.jsonFilterDetails.toSaveFilter.filterValues["Patient"] = JSON.stringify(labResultObj.patientForFilter);
			if(!angular.isString(labResultObj.dateRange.startDate)) { 
				labResultObj.dateRange.startDate = labResultObj.dateRange.startDate == null ? null : labResultObj.dateRange.startDate.format('YYYY-MM-DD');
			}
			if(!angular.isString(labResultObj.dateRange.endDate)) { 
				labResultObj.dateRange.endDate =  labResultObj.dateRange.endDate == null ? null : labResultObj.dateRange.endDate.format('YYYY-MM-DD');
			}
			labResultObj.jsonFilterDetails.toSaveFilter.filterValues["StartDate"] = labResultObj.dateRange.startDate == null ? "" :  labResultObj.dateRange.startDate;
			labResultObj.jsonFilterDetails.toSaveFilter.filterValues["EndDate"] = labResultObj.dateRange.endDate == null ? "" :  labResultObj.dateRange.endDate;
			labResultObj.jsonFilterDetails.toSaveFilter.filterValues["ReviewStatus"] = labResultObj.reviewStatus ;
			
		}
	};
	
labResultObj.populateConfigFilterValues = function(){
		labResultObj.clearFilter();
		labResultObj.isFilterApplied = true ;
		labResultObj.dateRange.startDate =  labResultObj.jsonFilterDetails.selectedFilter.filterValues["StartDate"] == "" ? null :  labResultObj.jsonFilterDetails.selectedFilter.filterValues["StartDate"];
		labResultObj.dateRange.endDate =  labResultObj.jsonFilterDetails.selectedFilter.filterValues["EndDate"] == "" ? null :  labResultObj.jsonFilterDetails.selectedFilter.filterValues["EndDate"];
		labResultObj.reviewStatus = labResultObj.jsonFilterDetails.selectedFilter.filterValues["ReviewStatus"]==""?'0':labResultObj.jsonFilterDetails.selectedFilter.filterValues["ReviewStatus"];
		labResultObj.selectedFacility = JSON.parse(labResultObj.jsonFilterDetails.selectedFilter.filterValues["Facility"]);
		labResultObj.patientForFilter = JSON.parse(labResultObj.jsonFilterDetails.selectedFilter.filterValues["Patient"]);
		if(!labResultObj.patientForFilter || labResultObj.patientForFilter.patient == ""){
			labResultObj.clearPatientData();
		}else{
			 labResultObj.setPatientInfo();
		}
		labResultObj.getFilteredLabResult();
	};
	
    labResultObj.openNoteAttachModal = function(obj)
	{  
    	$ocLazyLoad.load({
			name  : 'ecw.ip.notesAndAttachmentApp',
			files : Global.getDependencies("notesAndAttachment"),
		    cache: true}).then(function(){
				var modalInstance = $modal.open({
													templateUrl      : makeURL("/mobiledoc/inpatientWeb/assets/patientDashboard/tabs/results/views/notesAndAttachments.html"),
													controller       : 'notesAndAttachmentCtrl',
													controllerAs     : 'notesAttachment',
													animation        : true,
													bindToController : true,
													backdrop         : "static",
													keyboard         : false,
													cache            : false,
													windowClass		 : 'app-modal-window  config-w720',
													resolve          : {
														selectedObj: function () {
									                           return {reportId:obj.reportId,labType : 0,episodeEncId : obj.episodeEncID};
									                   }}
												});
				modalInstance.result.then(function(result) {});
			});
	};
	
	labResultObj.checkAll = function () {
    	angular.forEach(labResultObj.phAllActiveData, function (item) {
            item.selected = labResultObj.isAllSelected;
        });
    };
    labResultObj.patientToggled = function () {
        labResultObj.isAllSelected = labResultObj.phAllActiveData.every(function (item) {
            return item.selected;
        });
    };
    labResultObj.markAsReviewed = function (){
    	var reviewList = [];
    	var count = 0;
    	var reviewCount = 0;
    	angular.forEach(labResultObj.phAllActiveData, function (item) {
            if(!(!item.selected)){
            	count++;
            	if(item.reviewStatus==1){
            		reviewCount++;
            	}else{
            		reviewList.push(item.reportId);
            	}
            	
            } 
        });
    	if(count==0){
    		showIPMessage('Please Select Lab For Review', 'inputelm2','ErrorMsg');
    	}else if(count==reviewCount){
    		showIPMessage('Lab Is Already Reviewed', 'inputelm2','ErrorMsg');
    	}else if(reviewList.length>0){
    		labReviewService.markLabAsReviewed(reviewList).then(
				function(response) {
					labResultObj.getFilteredLabResult();
			});
    	}
    	
    };
    
    labResultObj.selectTempDropAllactive = function () {
        $('#select-temp-drop-result').hide();
    };

    $('#select-temp-drop-result').hide();
    labResultObj.showTempDropAllActive = function (e) {
        e.stopPropagation();
        var _that = e.target;
        $('#select-temp-drop-result').toggle().animate({}, 100, function () {
            $(this).position({
                of: _that,
                my: 'right top+25',
                at: 'right+1 top',
                collision: "flipfit"
            }).animate({
                "opacity": 1
            }, 100)
        });
    }

    angular.element(document).on('click', function (e) {
        var container = $('#select-temp-drop-result');
        if (!container.is(e.target) /* if the target of the click isnt the container... */ && container.has(e.target).length === 0) /*nor a descendant of the container*/ {
            container.hide();
        }
    });

    $('.all-active-green').hide();
    $('.select-filter-text-all-active-list').hide();

    labResultObj.closeModal = function () {
        $('.btn-group').removeClass('open');
    };

    labResultObj.closeFilter = function () {
    	labResultObj.isFilterApplied = false ;
    	labResultObj.clearFilter();
		labResultObj.getFilteredLabResult();
        $('.select-filter-text-all-active-list').hide();
        $('.all-active-green').hide();
        $('#darkhistory').hide();
    };
}).service('labReviewService',['$http',labReviewService]);

$(window).resize(function () {
    var myPatientWHgt = $(window).height();
    $('.ph-resultList-cust-scroll').css('height', myPatientWHgt - 248);
    $('#ph-result-module .icon-greenfilter').on('click', function () {
        var collapseInOutHgt = $('html').height() - 50;
        if ($(this).hasClass('collapsed')) {
            collapseInOutHgt = $('html').height() - 153;
            $('.ph-resultList-cust-scroll').height(collapseInOutHgt - 153);
        } else {

            $('.ph-resultList-cust-scroll').height(collapseInOutHgt - 150);
        }
    });
    $('.ph-resultList-cust-scroll').perfectScrollbar({
        suppressScrollX: 'false'
    });
});
$(window).resize();