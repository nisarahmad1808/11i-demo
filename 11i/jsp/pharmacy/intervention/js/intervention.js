var controllerFn=function intervensionPopUpController($http,$ocLazyLoad,$modal,$modalInstance,ipMedHelperURLService,interventionFetchService,ptOrdersVisitDt,toastrService){
	var vm = this;
	var medOrderSrc =interventionFetchService;
	vm.consultedList=[];
	vm.selectedMedOrder=[];
	vm.selectedAllergies=[];
	vm.showSelectedList=false;
	vm.patientDetails={};
	vm.assignedToDetails={};
	vm.fetchedStatus='';
	vm.interventionLogs=[];
	vm.topSection=true;// For Patient dtls top Panel Configurable
	vm.closeModal = function()
	{
		$modalInstance.close({status:'SUCCESS'});
	};
	vm.dismissModal= function()
	{
		$modalInstance.dismiss('cancel');	
	};
	vm.initialize = function(){
		vm.intervention={};
		vm.intervention.selectedReason={};
		vm.intervention.intTypeName="Select";
		vm.intervention.reasonName="Select";
		vm.mode=ptOrdersVisitDt.mode;
		var medDatadetails = {
				patientId:ptOrdersVisitDt.patientId
				//pharmacyStatus:ptOrdersVisitDt.pharmacyStatus
		};
		if(ptOrdersVisitDt.mode=='Add'){
			vm.intervention.durationUnits='Mins';
			vm.intervention.duration='00';
			vm.intervention.status='Pending';
			vm.intervention.severity='Low';// Changing default to Low as per SB
			vm.intervention.notes="";
			vm.showCheckedList=false;
			vm.assignedToDetails.userId=global.TrUserId;
			vm.assignedToDetails.fullName=global.fullUserName;
			medOrderSrc.getInterventionCategories(function(data){
				vm.interventionCategory=data;
			});
			medOrderSrc.getMedicationOrders(medDatadetails,function(data){
				vm.medicationOrders=data.MedOrderData;
				vm.patientDetails.patientName=data.ptdetails.patientName;
				vm.patientDetails.patientDob=data.ptdetails.patientDob;
				vm.patientDetails.patientEmail=data.ptdetails.patientEmail;
				vm.patientDetails.patientGender=data.ptdetails.patientGender;
				vm.patientDetails.patientMobile=data.ptdetails.patientMobile;
				vm.patientDetails.patientAge=data.ptdetails.patientAge;
				vm.patientDetails.patientMRN=data.ptdetails.patientMRN;
			});
			medOrderSrc.getAllergyForPatient(ptOrdersVisitDt.patientId,function(data){
				vm.Allergies=data;
			});
		}else if (ptOrdersVisitDt.mode=='Edit'){
			medOrderSrc.getInterventionDetails(ptOrdersVisitDt.interventionId,function(result){
				var response=result.Intervention;
				vm.securitySettings=result.SecuritySettings;
				vm.intervention.duration=response.duration;
				vm.intervention.durationUnits=response.durationUnits;
				vm.intervention.status=response.status;
				vm.intervention.severity=response.severity;
				vm.intervention.notes=response.notes;
				vm.intervention.savingValue=response.savingValue;
				vm.intervention.timeSpendValue=response.timeSpendValue;
				vm.assignedToDetails.userId=response.assignedTo;
				vm.assignedToDetails.fullName=response.assignedToUserName;
				vm.patientDetails.patientName=response.ptdetails.patientName;
				vm.patientDetails.patientDob=response.ptdetails.patientDob;
				vm.patientDetails.patientEmail=response.ptdetails.patientEmail;
				vm.patientDetails.patientGender=response.ptdetails.patientGender;
				vm.patientDetails.patientMobile=response.ptdetails.patientMobile;
				vm.patientDetails.patientAge=response.ptdetails.patientAge;
				vm.patientDetails.patientMRN=response.ptdetails.patientMRN;
				vm.fetchedStatus=response.status;
				vm.showCheckedList=false;
				angular.forEach(response.consultedUsers,function(item){
					var consultantUser ={
							id:item.id,
							name:item.name 

						}
					vm.consultedList.push(consultantUser);
				});
				medOrderSrc.getInterventionCategories(function(data){
					vm.interventionCategory=data;
				angular.forEach(data, function (item) {
					if(item.categoryCode == response.interventionTypeCode){
						vm.intervention.intTypeName=item.listName;//Intervention Name binding for edit mode.
						vm.interventionCategoryListClicked(item,ptOrdersVisitDt.mode,response.reasonId);
					}
				});
			});
				if(response.interventionTypeCode=='DG'){
					medOrderSrc.getMedicationOrders(medDatadetails,function(data){
						vm.medicationOrders=data.MedOrderData;
						angular.forEach(response.orderIds, function(orderId){
							angular.forEach(vm.medicationOrders, function (item) {
								if(item.orderId == orderId){
									vm.selectedMedOrder.push(item);
								}
							});					
						});
					});
				} else if(response.interventionTypeCode=='AG'){
					medOrderSrc.getAllergyForPatient(ptOrdersVisitDt.patientId,function(data){
						vm.Allergies=data;
						angular.forEach(response.allergies, function(allergy){
							angular.forEach(vm.Allergies, function (item) {
								if(item.drug == allergy){
									vm.selectedAllergies.push(item);
								}
							});					

						});
					});
				}
			});
			try{
				medOrderSrc.getInterventionLogs(ptOrdersVisitDt.interventionId,function(response){
					vm.interventionLogs=response;
				});
			}catch(e){}
		}
	
	};
	vm.interventionCategoryListClicked= function(item, mode, reasonId){
		if(item!=undefined && item!=''){
			vm.intervention.intType=item;
			medOrderSrc.getALLInterventionReasons(item,function(response){
				vm.interventionReasonsData=response;
				if(mode != undefined && mode=='Edit' && reasonId>0){
					angular.forEach(response, function (item) {
						if(item.id == reasonId){
							vm.intervention.reasonName=item.reason;//Intervention Name binding for edit mode.
							vm.intervention.selectedReason.id=reasonId;
						}
					});
				} 
			});
		}
	};
	
	vm.toggleSelectionMedOrder= function(item){
		if(item!=undefined && item!=''){

		    var idx = vm.selectedMedOrder.indexOf(item);
		    // Is currently selected
		    if (idx > -1) {
		    	vm.selectedMedOrder.splice(idx, 1);
		    }
		    // Is newly selected
		    else {
		    	vm.selectedMedOrder.push(item);
		    }
		
		}
		
	};
	vm.toggleSelectionAllergies= function(item){
		if(item!=undefined && item!=''){

		    var idx = vm.selectedAllergies.indexOf(item);
		    // Is currently selected
		    if (idx > -1) {
		    	vm.selectedAllergies.splice(idx, 1);
		    }
		    // Is newly selected
		    else {
		    	vm.selectedAllergies.push(item);
		    }
		
		}
		
	};
	vm.listReasonClicked= function(item){
		if(!item.isActive){
			toastrService.ecwMessage(toastrService.INFORMATION, "Intervention Reason is inactive.");
			return;
		}
		if(vm.intervention.selectedReason.id==item.id)
		{
			return;
		}
		vm.intervention.selectedReason=item;
		if(vm.intervention.selectedReason!= undefined && vm.intervention.selectedReason !=null){
			vm.intervention.duration=item.duration;
			vm.intervention.durationUnits=item.units;
			vm.intervention.savingValue=item.savingValue;
			vm.intervention.timeSpendValue=item.timeSpendValue;
		}
	
	};
	vm.showMedOrderList= function(){
		
		if(vm.showCheckedList){
			
			vm.showCheckedList=false;
			$('.inttype').css("display", "none");
			
		}else {
			vm.showCheckedList=true;
			$('.inttype').css("display", "");
		}
		
	};

	vm.addConsultedCallBack = function()
	{
		//var localCopy = {};
		var localCopy ={
				id:vm.consulted.staff.id,
				name:vm.consulted.staff.name 

			}
		//angular.copy(vm.consulted, localCopy);
		vm.consultedList.push(localCopy);
	};
	
	vm.removeFromConsultedList = function(originalList,removeItem)
	{
		var index = vm.consultedList.indexOf(removeItem);
		vm.consultedList.splice(index, 1);     

	}
	vm.addAssignedToCallBack = function()
	{
		vm.assignedToDetails.userId=vm.assignedTo.staff.id;
		vm.assignedToDetails.fullName=vm.assignedTo.staff.name;
		
	};
	vm.showSelectedMedList= function(){
		if(vm.showSelectedList){
			$('.showList').hide();			
			vm.showSelectedList=false;
		}else{
			if(vm.intervention.intType.categoryCode==='DG'){
				if(vm.selectedMedOrder.length<=0){
					return;
				}
			}
			if(vm.intervention.intType.categoryCode==='AG'){
				if(vm.selectedAllergies.length<=0){
					return ;
				}
			}
			vm.showSelectedList=true;
			$('.showList').show();
		}
	}
	vm.hideOnOutsideClick= function(){
		$('.showList').hide();			
		vm.showSelectedList=false;
	}
	vm.clearNotes=function(){
		vm.intervention.notes="";
	}
	//Spell Checker Integration
	//vm.spellCheckFlag = false;
	vm.spellChecking = function (txtBoxid) {
		var val = $("#"+txtBoxid).val();
		vm.spellCheckObjForAT = {
			misSpelledText : val,
			correctedSpellText : "",
			spellCheckOnElement : txtBoxid
		};
		vm.spellCheckFlagForAT = true;
    }
	vm.applySpellChecking = function () {
		vm.spellCheckFlagForAT = false;
		$("#" + vm.spellCheckObjForAT.spellCheckOnElement).val(vm.spellCheckObjForAT.correctedSpellText);
		if(vm.spellCheckObjForAT.spellCheckOnElement!=undefined && vm.spellCheckObjForAT.spellCheckOnElement=='intNotes'){
			vm.intervention.notes = vm.spellCheckObjForNotes.correctedSpellText;
		}
		
    }

//	vm.appendTimeStamp = function() {
//        var currentDate = new Date();
//        var time = formatAMPM(currentDate, true);
//        var fullDate = (currentDate.getMonth() + 1) + "/" + currentDate.getDate() + "/" + currentDate.getFullYear() + " " + time.toUpperCase() + " >";
//
//        if (vm.intervention.actionTaken.length >= 1)
//        	vm.intervention.actionTaken = vm.intervention.actionTaken + ("\n" + global.TrUserName + fullDate);
//        else
//        	vm.intervention.actionTaken = vm.intervention.actionTaken + global.TrUserName + fullDate;
//    };
	function validateInterventionBeforeSave(){
		if(!vm.intervention.intType){
			toastrService.ecwMessage(toastrService.INFORMATION,"please specify intervention type");
			return false;
		}
		if(!vm.intervention.selectedReason.id || vm.intervention.selectedReason.id<=0 ){
			toastrService.ecwMessage(toastrService.INFORMATION,"please specify intervention reason");
			return false;
		}
		if(!vm.assignedToDetails.userId || vm.assignedToDetails.userId<=0 ){
			toastrService.ecwMessage(toastrService.INFORMATION,"please specify intervention valid assign To");
			return false;
		}
		// parse float required in order to align with the intervention reason configuration
		if (!vm.intervention.duration || parseFloat(vm.intervention.duration)<=0){
			toastrService.ecwMessage(toastrService.INFORMATION,"please specify intervention valid duration");
			return false;
		}
		if(vm.intervention.intType.categoryCode==='DG'){
			if(!vm.selectedMedOrder || vm.selectedMedOrder.length<=0){
				toastrService.ecwMessage(toastrService.INFORMATION,"please select at least one drug");
				return false;
			}
		}
		if(vm.intervention.intType.categoryCode==='AG'){
			if(!vm.selectedAllergies || vm.selectedAllergies.length<=0){
				toastrService.ecwMessage(toastrService.INFORMATION,"please select at least one pt.allergy");
				return false;
			}
		}
		return true;
		
	}
	vm.saveInterventionData= function(mode){
		if(validateInterventionBeforeSave()){
			var url = 'interventionReasonController.go/reasons/saveInterventionData';
			var userIds=[];
			var orderIds=[];
			var allergies=[];
			if(vm.consultedList !== undefined && vm.consultedList!==''){
				angular.forEach(vm.consultedList, function (item) {
					if(item.id > 0 ){
						userIds.push(angular.copy(item.id));
					}
				});
			}
			if(vm.selectedMedOrder !==undefined && vm.selectedMedOrder!==''){
				angular.forEach(vm.selectedMedOrder, function (item) {
					if(item.orderId > 0 ){
						orderIds.push(angular.copy(item.orderId));
					}
				});
			}
			if(vm.selectedAllergies !==undefined && vm.selectedAllergies!==''){
				angular.forEach(vm.selectedAllergies, function (item) {
					if(item.drug != undefined && item.drug != ''){
						allergies.push(angular.copy(item.drug));
					}
				});
			}
			var requestDataTemp={
					interventionId:ptOrdersVisitDt.interventionId,
					interventionTypeId:vm.intervention.intType.id,
					interventionTypeCode:vm.intervention.intType.categoryCode,
					reasonId: vm.intervention.selectedReason.id,
					patientId:ptOrdersVisitDt.patientId,
					duration:vm.intervention.duration,
					durationUnits:vm.intervention.durationUnits,
					savingValue:vm.intervention.savingValue,
					timeSpendValue:vm.intervention.timeSpendValue,
					status:vm.intervention.status,
					severity:vm.intervention.severity,
					notes:vm.intervention.notes,
					assignedTo:vm.assignedToDetails.userId,
					consultedIds:userIds,
					orderIds:orderIds,
					allergies:angular.toJson(allergies),
					mode: mode
			}
			var reqObj={
					requestData:requestDataTemp
			}
			ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
				if(resp && resp.status === "success")  {
						toastrService.ecwMessage(toastrService.SUCCESS,resp.Message);
				}
				else {
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				}
				vm.closeModal();// Changed model closing only after successful validations
			});
		}
	};
	
};
var serviceFn=function interventionFetchService($http,ipMedHelperURLService,toastrService){

	var service= this;
	service.getMedicationOrders= function(data,onSuccess){

		var url = 'interventionReasonController.go/reasons/getMedOrderData';
		var reqObj={
				patientId:data.patientId
				//pharmacyStatus:data.pharmacyStatus
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
	service.getAllergyForPatient= function(data,onSuccess){

		var url = 'interventionReasonController.go/reasons/getAllergyForPatient';
		var reqObj={
				patientId:data,
				//pharmacyStatus:data.pharmacyStatus
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.Allergies);
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
	//Fetch Active intervention reasons
	service.getInterventionReasons= function(data,onSuccess){
		var url = 'interventionReasonController.go/reasons/intervensionReasons';
		var reqObj={
				category:data
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.reasons);
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
	//Fetch all Active-Inactive intervention reasons
	service.getALLInterventionReasons= function(data,onSuccess){
		var url = 'interventionReasonController.go/reasons/intervensionReasons';
		var reqObj={
				category:data,
				fetchType:'ALL'
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.reasons);
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

	//Fetch Intervention wrt to InterventionId
	service.getInterventionDetails= function(interventionId,onSuccess){

		var url = 'interventionReasonController.go/reasons/getInterventionDetails';
		var reqObj={
				interventionId:interventionId
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
	service.getInterventionCategories = function(onSuccess){
		var url = 'interventionReasonController.go/getIntervensionCategories';
		ipMedHelperURLService.httpPostCall(url,'',function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.categories);
					}
				}
				else{
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				}
			}
			else {
				toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
			}
		});
		
	};
	service.getInterventionLogs = function(data,onSuccess){
		var url = 'interventionReasonController.go/reasons/getInterventionLogs';
		var reqObj={
				interventionId:data
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.LogDetails);
					}
				}
				else{
					toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
				}
			}
			else {
				toastrService.ecwMessage(toastrService.ERROR, "Error fetching data");
			}
		});
		
	};

} ;

var app = angular.module('interventionPopUp',['ipMedHelper', 'stafflookup','perfect_scrollbar','toastrMsg','ip.dir.onlyNumbers']);
app.service('interventionFetchService',['$http','ipMedHelperURLService','toastrService',serviceFn]);
app.controller('intervensionPopUpcntl', [ '$http','$ocLazyLoad', '$modal','$modalInstance','ipMedHelperURLService','interventionFetchService','ptOrdersVisitDt','toastrService',controllerFn]);
$(document).ready(function () {
	 
//	$('.inttype').hide();
//	$('.lisinoprilclick').on('click', function () {
//		$('.inttype').show();
//	});
	/*to on document click close popup*/
	$(document).mouseup(function (e) {
		var container = $('.inttype');

		if (!container.is(e.target) && container.has(e.target).length === 0) {
			container.hide();
		}
	});
	/*end*/
	$('.allergy-sce').hide();
	$('.allergyclick').on('click', function () {
		$('.drug-sec').hide();
		$('#orderdrug').hide();
        $('.allergy-sce').show();
	});
	$('.drugclick').on('click', function () {
		$('.drug-sec,#orderdrug').show();
		$('.allergy-sce').hide();
	});
	$('.generalclick').on('click', function () {
		$('.drug-sec').show();
		$('.allergy-sce,#orderdrug').hide();
	});
	$('.add-chip-Tags').perfectScrollbar();

	$(document).on('click','.sub-chip-remove',function(e){
	    $(this).parent('.sub-chips').remove();
	});

	
});