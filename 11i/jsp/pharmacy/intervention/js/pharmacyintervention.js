(function () {
	var controllerFn=function intervensionPopUpController($http,$ocLazyLoad,$window,$modal,ipMedHelperURLService,pharmacyinterventionService,singleTimeOpt,interventionFetchService,IPFileTransferService,PRINTTYPE,FORMAT,toastrService){
    	var vm = this;
    	var service=pharmacyinterventionService;
    	vm.showAddPopUp=false;
    	vm.singleDateOption=singleTimeOpt;
    	vm.jsonFilterDetails={toSaveFilter :{},selectedFilter : {},configuredFilters :{} };
    	vm.filterOptionObj={selectedPage : 1,recordsPerPage : 15, interventionStatus:'All',toDateTime: null,fromDateTime: null,interventionRsn:{reason:'Select'},EnteredBy:{patientName:'Select'},filterCategoryObj:{listName:'Select'}};
    	vm.isFilterApplied = false ;
    	vm.printOptions={showPreview:true,dataDestination:PRINTTYPE.DATA,format:12};// Object Created for Print Directive Integration
    	vm.exportOptions={filename:'ExportIntervention',format:FORMAT.EXCEL};// Object Created for Export Directive Integration
    	vm.securitySettings=[];
    	vm.initialize= function(){
    		vm.selectedChkArray = [];
    		service.getInterventionReasons(vm.generateFilterObject(),function(response){
    			vm.interventionsData=response.Interventions;
    			vm.totalInterventionsRecords= response.TotalCount;
    			vm.securitySettings=response.SecuritySetting;		
    			angular.forEach(vm.interventionsData, function(item){
    				item.isSelected = false;
    			});
    		});
    		service.getTopPanelData(function(response){
    			vm.interventionCategory=response.Categories;
    			vm.EnteredBy=response.EnteredBy;
    		});
    	}
    	vm.refreshList= function(){
    		service.getInterventionReasons(vm.generateFilterObject(),function(response){
    			vm.interventionsData=response.Interventions;
    			vm.totalInterventionsRecords=  response.TotalCount;
    			vm.totalPages =  Math.ceil(vm.totalInterventionsRecords / vm.filterOptionObj.recordsPerPage);
    			if(vm.totalPages < vm.filterOptionObj.selectedPage){
					vm.filterOptionObj.selectedPage = 1;
				}
    			vm.selectedChkArray=[];// clearing on screen refresh
    		});
    		
    	};
    	
    	vm.setExportData=function(){
    	    return $.Deferred(function(dfd) {
    	    	service.getInterventionReasons(vm.generateFilterObject(),function(response){
        			var data=createExportJson(response.Interventions);
        			dfd.resolve(data);
        		});
        		
    	    });
    	}
    	
    	function  createExportJson(exportData){
    		if(exportData==undefined ||exportData=='' ||exportData.length==0){
    			return;
    		}
    		var exportObj=[];
    		angular.forEach(exportData, function (item) {
  					exportObj.push({'Patient Name':item.ptdetails.patientName,
  									'Age':item.ptdetails.patientAge,
  									'Gender':item.ptdetails.patientGender,
  									'Intervention Type':item.interventionName,
  									'Intervention Reason':item.reason,
  									'Duration':item.duration+" "+item.durationUnits,
  									'Entered by':item.createdBy,
  									'Entered Date':item.createdOn,
  									'Saving Value':item.savingValue,
  									'Time Spend Value':item.timeSpendValue,
  									'Status':item.status,
  									'Assigned To':item.assignedToUserName
  									});
  						});
    		return exportObj;
    	};
    	vm.setPrintData=function(){
    	    return $.Deferred(function(dfd) {
    	    	service.getInterventionReasons(vm.generateFilterObject(),function(response){
        			vm.printOptions.Htmldata=response.PrintHtml;
        			dfd.resolve();
        		});
        		
    	    });
    	   }
    	
    	
    	 vm.checkAll = function () {
    		 var itemSelectedFlag=false;
    		 if(vm.isAllSelected){
    			 itemSelectedFlag=true;
    		 }
    		  angular.forEach(vm.interventionsData, function (item) {
    	          item.isSelected = itemSelectedFlag;
    	         vm.patientToggled(vm.interventionsData,item,itemSelectedFlag);        
    		  });
    	  };

    	  vm.patientToggled = function (data, obj, checkAllFlag) {
    	      if(obj!=undefined && obj!=''){
    			    var idx = vm.selectedChkArray.indexOf(obj);
    			    // Is currently selected
    			    if (idx > -1) {
    			    	if(checkAllFlag)return;
    			    	
    			    	vm.selectedChkArray.splice(idx, 1);
    			    	//Added for MultiSelect checkbox to uncheck on removal 
    			    	vm.isAllSelected=false;
    			    }
    			    // Is newly selected
    			    else {
    			    	vm.selectedChkArray.push(obj);
    			    	if(vm.interventionsData.length===vm.selectedChkArray.length){
    			    		vm.isAllSelected=true;
    			    	}
    			    }
    			
    			}
    	  };
      	// Middle Panel Functions Starts
    	  vm.changeStatusForInterventionList= function(status){
    	  		if(!validateChangeInIntervention()){
    	  			var interventionIds=[];
    	  			if(vm.selectedChkArray !=undefined && vm.selectedChkArray!=''){
    	  				for(var i=0;i<vm.selectedChkArray.length;i++){
    	  					var item= vm.selectedChkArray[i];
    	  					if(item.interventionId > 0 ){
    	  						interventionIds.push(angular.copy(item.interventionId));
    	  					}
    	  				}				
    	  			}
    	  			var url = 'PharmacyInterventionController.go/changeForIntervention';
    	  			var reqObj={
    	  					interventionIds:interventionIds,
    	  					value:status,
    	  					changeMode:'Status'
    	  			}
    	  			ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
    	  				if(resp!='' && resp!= undefined && resp.status == "success"){
    	  					toastrService.ecwMessage(toastrService.SUCCESS, "Succesfully Updated.");
    	  					// Refresh List On Success
    	  					vm.refreshList();
    	  				}
    	  				else {
    	  					toastrService.ecwMessage(toastrService.ERROR, resp.message);
    	  				}
    	  			});
    	  		}
    	  		angular.element("#changeStatusBtn").click();
    	  	};
//    	  	vm.changeStatusBtnClick=function(){
//    	  		$('#StatusList').attr('style', 'display:');
//    	  	};
    	  	/**
    	  	 *  Change Assign To at Pharmacy Intervention
    	  	 */
    	  	vm.assignToPopUP= function(event){
    	  		var _that = event.target;
    	  		var positionThat = $(_that).offset().top;
    	  		var positionLeft = $(_that).offset().left;
    	  		vm.showAsssignedToPopUp=true;
    			$('#assignToBtn').show().css({'top' : positionThat-3,'left' : positionLeft});
    			
    	  	};
    	  	vm.setAssignedTo= function(){
    	  		if(vm.staffName.staff!=undefined && vm.staffName.staff.id>0){
    	  			vm.assignedStaffId	= vm.staffName.staff.id;	
    	  		}
    	  	}
    	  	vm.changeAssignedTo = function() 
    	  	{
    	  		if(!validateChangeInIntervention() && !validateAssignTo() &&  vm.selectedChkArray  && vm.assignedStaffId >0){
    	  			var interventionIds=[];
        	  			for(var i=0;i<vm.selectedChkArray.length;i++){
        	  				var item= vm.selectedChkArray[i];
        	  				if(item.interventionId > 0 ){
        	  					interventionIds.push(angular.copy(item.interventionId));
        	  				}
        	  			}				
        	  		var url = 'PharmacyInterventionController.go/changeForIntervention';
        	  		var reqObj={
        	  				interventionIds:interventionIds,
        	  				value:vm.assignedStaffId,
        	  				changeMode:'AssignedTo'
        	  		}
        	  		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
        	  			if(resp!='' && resp!= undefined && resp.status == "success"){
        	  				toastrService.ecwMessage(toastrService.SUCCESS, "Intervention assigned successfully");
        	  				// Refresh List On Success
        	  				vm.refreshList();
        	  			}
        	  			else {
        	  			toastrService.ecwMessage(toastrService.ERROR, resp.message);
        	  			}
        	  		});
        	  		vm.showAsssignedToPopUp=false;
    	  		}
    	  	};
    		vm.addIntervention= function(event){
    	  		var _that = event.target;
    	  		var positionThat = $(_that).offset().top;
    	  		var positionLeft = $(_that).offset().left;
    	  		vm.showAddPopUp=true;
    			$('.inter-reasons-popup').show().css({'top' : positionThat-3,'left' : positionLeft+25});
    			
    	  	};
    	  	// patient Look Integration for adding intervention
    	  	vm.setPatientInfo = function() {
    			vm.selectedPatientId = vm.selectedPatient.patient.id;
    			vm.showAddPopUp=false;
    			if(vm.selectedPatientId>0){
    				vm.showInterventionPopUp('Add',-1,vm.selectedPatientId);	
    			}
    			
    		};
    	// Middle Panel Functions Ends
    	vm.showInterventionPopUp = function(mode,interventionId,patientId){
    		if(mode!=undefined && mode=='Edit'){
    			if(vm.securitySettings!==undefined && vm.securitySettings.InterventionEditAccess!==1){
    				toastrService.ecwMessage(toastrService.INFORMATION, "User does not have edit access,opening in read only mode");
    			}
    				if(interventionId>0){
    					var  medOrderCntlDetails = { 
    							patientId:patientId,
    							mode:mode,
    							interventionId:interventionId
    					}
    					loadScreen(medOrderCntlDetails);
    				}
    				else{
    					toastrService.ecwMessage(toastrService.ERROR, "Error loading pop up");
    				}
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
    		var dependency =['/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/css/add-new-intervention-modal.css','/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/js/intervention.js','/mobiledoc/inpatientWeb/assets/js/lookup.js'];
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
    							vm.refreshList();
    						},800);
    				    },function (result) {
    				    	// On Model Close	
    				    });
    				});
    	}
    	vm.deleteProcessObj='';
    	vm.showConfirmDialog = function(obj){
    		vm.deleteProcessObj = obj;
    		showIPMessage("Are you sure you want to delete intervention ?",'','ConfirmMsg','deleteConfirmDeleteCallback','');
    	}; 

    	deleteConfirmDeleteCallback = function(status){ 
    		if(status === 'yes'){
    			var url = 'PharmacyInterventionController.go/deleteIntervention';
    			var reqObj={
    					interventionId:vm.deleteProcessObj.interventionId,
    			}
    			ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
    				if(resp!='' && resp!= undefined && resp.status == "success"){
    					if(vm.deleteProcessObj!=undefined && vm.deleteProcessObj!=''){
    						var idx = vm.interventionsData.indexOf(vm.deleteProcessObj);
    						if(idx > -1) {
    							vm.interventionsData.splice(idx, 1);
    						}
    					}
    					toastrService.ecwMessage(toastrService.SUCCESS, resp.Message);
    				}
    				else {
    					toastrService.ecwMessage(toastrService.ERROR, "Delete Failed");
    				}
    			});

    		}       
    	};

   	function  validateChangeInIntervention(){
	 //Validation Logic to Check no Intervention with Status completed is changed
	  if(vm.selectedChkArray!= undefined && vm.selectedChkArray.length>0){
		  for(var i=0;i<vm.selectedChkArray.length;i++){
			  var item= vm.selectedChkArray[i];
			  if(item.status=='Completed'){
				  toastrService.ecwMessage(toastrService.WARNING, "Cannot change the Intervention with status complete");
				  return true;
			  }
		  }
	  }else{
		  toastrService.ecwMessage(toastrService.INFORMATION, "Please select at least one intervention");
		  return true;
	  }
	  return false;
  }
   	function  validateAssignTo(){
   		if(vm.selectedChkArray!= undefined && vm.selectedChkArray.length>0){
   			for(var i=0;i<vm.selectedChkArray.length;i++){
   				var item= vm.selectedChkArray[i];
   				if(item.assignedTo===vm.assignedStaffId){
   					toastrService.ecwMessage(toastrService.WARNING, "Intervention already assigned to " + item.assignedToUserName);
   					vm.showAsssignedToPopUp=false;// to close assign to pop up
   					return true;
   				}
   			}
   		}else{
   			toastrService.ecwMessage(toastrService.INFORMATION, "Please select the at least one Intervention");
   			vm.showAsssignedToPopUp=false;// to close assign to pop up
   			return true;
   		}
   		return false;
   	} 	
  	vm.clearPatientData = function (){
		vm.selectedPatientId = 0;
	}
  	
  vm.closePopUp= function(){
  		vm.showAddPopUp=false;
  		vm.showAsssignedToPopUp=false;
  	};
  vm.clearStaff= function(){
		vm.assignedStaffId= 0;
	}
	// Top Panel Functions Starts(Filter Section)
	vm.setFilteredStaff = function() 
	{
		if(vm.filteredstaff.staff.id != undefined && vm.filteredstaff.staff.id>0){
		vm.filterOptionObj.AssignedTo	= vm.filteredstaff.staff.id;
		}else{
			vm.filterOptionObj.AssignedTo=0;
		}
		
	};
	vm.clearFilteredStaff=function(){
		if(vm.filterOptionObj.AssignedTo != undefined)
			vm.filterOptionObj.AssignedTo=0;
	}
	vm.setPatientInfoForFilter = function() {
		vm.filterOptionObj.PatientId = vm.selectPatientForFilter.patient.id;
	};
	vm.clearPatientDataForFilter= function(){
		if(vm.filterOptionObj.PatientId != undefined)
			vm.filterOptionObj.PatientId=0;
	}
	
	vm.interventionFilterCategoryClicked= function(item){
		if(item!=undefined && item!=''){
			vm.filterOptionObj.filterCategoryObj=item;
			interventionFetchService.getInterventionReasons(item,function(response){
				vm.interventionReasonsData=response;
			});
		}
	};
	vm.filterEnteredByClicked= function(item){
		if(item!=undefined && item!=""){
			vm.filterOptionObj.EnteredBy=item.ptdetails;
		}
	};
	vm.selectFilterReason= function(item){
		vm.filterOptionObj.interventionRsn=item;
	};
  vm.fetchFilterDetails= function(){
	  if(vm.jsonFilterDetails.toSaveFilter.filterValues!=undefined && vm.jsonFilterDetails.toSaveFilter.filterValues!=null){
		  
		  vm.jsonFilterDetails.toSaveFilter.filterValues['AssignedTo']=JSON.stringify(vm.filteredstaff.staff);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['Patient']=JSON.stringify(vm.selectPatientForFilter.patient);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['EnteredBy']=JSON.stringify(vm.filterOptionObj.EnteredBy);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['GroupByPatient']="false"//Default 
		  vm.jsonFilterDetails.toSaveFilter.filterValues['Status']= JSON.stringify(vm.filterOptionObj.interventionStatus);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['StartDate']=vm.filterOptionObj.fromDateTime == null ? "" : (!angular.isString(vm.filterOptionObj.fromDateTime) ? vm.filterOptionObj.fromDateTime.format('YYYY-MM-DD') : vm.filterOptionObj.fromDateTime);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['EndDate']=vm.filterOptionObj.toDateTime == null ? "" : (!angular.isString(vm.filterOptionObj.toDateTime) ? vm.filterOptionObj.toDateTime.format('YYYY-MM-DD') : vm.filterOptionObj.toDateTime);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['Category']=JSON.stringify(vm.filterOptionObj.filterCategoryObj);
		  vm.jsonFilterDetails.toSaveFilter.filterValues['Intervention']=JSON.stringify(vm.filterOptionObj.interventionRsn);
	  }
  }
  vm.populateConfigFilterValues= function(){
		vm.isFilterApplied = true ;
	  	vm.filterOptionObj.fromDateTime = vm.jsonFilterDetails.selectedFilter.filterValues["StartDate"] == "" ? null :  vm.jsonFilterDetails.selectedFilter.filterValues["StartDate"];
	  	vm.filterOptionObj.toDateTime = vm.jsonFilterDetails.selectedFilter.filterValues["EndDate"] == "" ? null : vm.jsonFilterDetails.selectedFilter.filterValues["EndDate"];
	  	vm.filterOptionObj.interventionStatus = JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Status"]);
	  	vm.filterOptionObj.filterCategoryObj = vm.jsonFilterDetails.selectedFilter.filterValues["Category"]!=""?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Category"]):{};
	  	vm.filterOptionObj.interventionRsn = vm.jsonFilterDetails.selectedFilter.filterValues["Intervention"]!=""?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Intervention"]):{};
	  	vm.filterOptionObj.EnteredBy=vm.jsonFilterDetails.selectedFilter.filterValues["EnteredBy"]!=""?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["EnteredBy"]):{};
		vm.filterOptionObj.groupByPatient =vm.jsonFilterDetails.selectedFilter.filterValues["GroupByPatient"]!=""?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["GroupByPatient"]):false;//Default requirement pending
		vm.filteredstaff.staff= vm.jsonFilterDetails.selectedFilter.filterValues["AssignedTo"]!=""?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["AssignedTo"]):{};
		vm.filterOptionObj.AssignedTo =vm.filteredstaff.staff!=undefined && vm.filteredstaff.staff.id>0?JSON.parse(vm.filteredstaff.staff.id):0;
		vm.selectPatientForFilter.patient =vm.jsonFilterDetails.selectedFilter.filterValues["Patient"]!="" ?JSON.parse(vm.jsonFilterDetails.selectedFilter.filterValues["Patient"]):{};
		vm.filterOptionObj.PatientId=vm.selectPatientForFilter.patient!=undefined && vm.selectPatientForFilter.patient.id>0 ?JSON.parse(vm.selectPatientForFilter.patient.id):0;
  }
  vm.generateFilterObject= function(){
	  var filterObj = {
			selectedPage: vm.filterOptionObj.selectedPage!= undefined && vm.filterOptionObj.selectedPage>0 ? vm.filterOptionObj.selectedPage:0,
			recordsPerPage:vm.filterOptionObj.recordsPerPage!=undefined && vm.filterOptionObj.recordsPerPage>0 ? vm.filterOptionObj.recordsPerPage:0,
			assignedTo: vm.filterOptionObj.AssignedTo!=undefined && vm.filterOptionObj.AssignedTo > 0 ?vm.filterOptionObj.AssignedTo:0,
			patientId: vm.filterOptionObj.PatientId!=undefined && vm.filterOptionObj.PatientId>0 ? vm.filterOptionObj.PatientId:0,
			interventionTypeCode:vm.filterOptionObj.filterCategoryObj!=undefined ? vm.filterOptionObj.filterCategoryObj.categoryCode:"",
			startDate: vm.filterOptionObj.fromDateTime!=undefined && vm.filterOptionObj.fromDateTime!= null ?vm.filterOptionObj.fromDateTime:"",
			endDate: vm.filterOptionObj.toDateTime!=undefined && vm.filterOptionObj.toDateTime!= null ?vm.filterOptionObj.toDateTime:"",
			reasonId:vm.filterOptionObj.interventionRsn!=undefined && vm.filterOptionObj.interventionRsn.id>0?vm.filterOptionObj.interventionRsn.id:0,
			enteredBy:vm.filterOptionObj.EnteredBy!=undefined  && vm.filterOptionObj.EnteredBy.patientId>0?vm.filterOptionObj.EnteredBy.patientId:0 ,
			status:vm.filterOptionObj.interventionStatus
		};
	  return filterObj;
	  
  }
  vm.filterInterventionData=function(){
	  initializeToDefaults();
	  vm.fetchFilterDetails();
	  vm.refreshList();
  }
  function initializeToDefaults(){
		 vm.selectedChkArray=[];
		  vm.isAllSelected=false;
	}
  vm.closeFilter= function(){
	  vm.isFilterApplied = false ;
		vm.selectPatientForFilter.patient={};
		vm.filterOptionObj = { 
				selectedPage : 1,
				recordsPerPage : 15,
				interventionStatus : 'All',
				toDateTime : "",
				fromDateTime : "",
				assignedToId : 0,
				PatientId : 0,
				EnteredBy:{},
				groupByPatient:false,
				interventionRsn:{reason:'Select'},
				EnteredBy:{patientName:'Select'},
				filterCategoryObj:{listName:'Select'}
				
		};
		
  }
//Top Panel Functions Ends
  $('.closemodal').on('click', function () {
      $('.btn-group ').removeClass('open');
  });
  setTimeout(function () {
      function windowResize() {
          var htmlhgt = $('html').height();
          var tablehgt = htmlhgt - 275;
          $('.interventions-tbl-hgt').height(tablehgt);
          $('.interventions-tbl-hgt').perfectScrollbar({
              suppressScrollX: 'false'
          });
      }
      windowResize();
  }, 500);

  /* When use collapse functionality that time manage Location-list-view table height */
  var htmllochgt = $('html').height();
  $('.location-filter').on('click', function (e) {
      if ($(this).hasClass('collapsed')) {
          $('.loclistview-tblhgt').animate({
              height: htmllochgt - 275
          }, 400);
      } else {
          $('.loclistview-tblhgt').animate({
              height: htmllochgt - 195
          }, 400);
      }
  });

  /* functionality for filter dropdown start */
  $('#select-temp-drop-interventions').hide();
  $(document).on('click', '#template-order-interventions', function (e) {
      e.stopPropagation();
      var _that = this;
      $('#select-temp-drop-interventions').toggle().animate({}, 100, function () {
          $(this).position({
              of: _that,
              my: 'right top+25',
              at: 'right+1 top',
              collision: "flipfit"
          });
      });
  });

//  $('.select-filter-text-interventions').hide();
//  $('.green-table-style .all-active-filter tr td ').on('click', function () {
//      $('.select-filter-text-interventions').show();
//      $('.all-active-green').show();
//      $('#darkhistory').show();
//
//  });
//  $('.green-table-style tr td').on('click', function () {
//      var selectFilter = $(this).text();
//      $('.getfiltername').text(selectFilter);
//  });

//  $('.all-active-green').hide();
//  $('.select-filter-text-interventions').hide();
//  $('.green-table-style .all-active-filter tr td ').on('click', function () {
//      $('.select-filter-text-interventions').show();
//      $('.all-active-green').show();
//
//  });

//  $(document).on('click', function (e) {
//      var container = $('#select-temp-drop-interventions');
//      if (!container.is(e.target) /* if the target of the click isnt the container... */ && container.has(e.target).length === 0) /*nor a descendant of the container*/ {
//          container.hide();
//      }
//  });
    };
    var serviceFn=function pharmacyinterventionService($http,ipMedHelperURLService,toastrService){
    	var service= this;
    	service.getInterventionReasons= function(filterObj,onSuccess){
    		var url = 'PharmacyInterventionController.go/getAllInterventions';
    		ipMedHelperURLService.httpPostCall(url,filterObj,function (resp){
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
    	service.getTopPanelData= function(onSuccess){
    		var url = 'PharmacyInterventionController.go/getTopPanelData';
    		var reqObj={
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

    };
  var app = angular.module('phamacyinterventionsModule', ['ui.bootstrap','toastrMsg','patientlookupinline','stafflookup','daterangepicker','FileTransfer','ecw.ip.print','inPatient.dir.filterTemplate','ecw.ip.export'])
    .directive("perfectScrollBar", function(){
       return {
        restrict: 'C',
        link: function (scope, element, attrs) {
            $(element).perfectScrollbar();
        }
    };
});
app.service('pharmacyinterventionService',['$http','ipMedHelperURLService','toastrService',serviceFn]);  
app.controller('pharmacyinterventionsCtrl', ['$http', '$ocLazyLoad','$window','$modal' ,'ipMedHelperURLService','pharmacyinterventionService','singleTimeOpt','interventionFetchService','IPFileTransferService','PRINTTYPE','FORMAT','toastrService', controllerFn]);
})();