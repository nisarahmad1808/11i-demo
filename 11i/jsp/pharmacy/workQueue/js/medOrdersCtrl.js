function medicationOrdersService($http, $modal,ipMedHelperURLService,toastrService){
	var service = this;
	
	/***********************************************************
	 * objects to assign drug interaction component attributes
	 ***********************************************************/
	service.drugList 				= [];
	service.orderList 				= [];
	service.autoPopup 				= false;

	service.getMedOrderDetails = function (orderId, pharmacyStatus, patientId, concurrentUserId){
		return $http({
			method: 'GET',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMedOrderDetails/"+orderId+"/"+patientId+"/"+pharmacyStatus+"/"+global.TrUserId+"/"+concurrentUserId)
		});
	};
	
	service.getMedOrder = function (orderId, patientId, concurrentUserId){
		return $http({
			method: 'GET',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMedOrder?patientId="+patientId+"&orderId="+orderId+"&userId="+global.TrUserId+"&concurrentUserId="+concurrentUserId)
		});
	};
	
	service.getPendingReasons = function (){
		return $http({
			method: 'GET',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getPendingReasons?tmp="+Math.random())
		});
	};
	
	//Added to fetch InterventionId wrt PatientId
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
						toastrService.ecwMessage(toastrService.INFORMATION, "No Interventions Found");
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
	//refresh Intervention Counts
	service.refreshInterventionCountPanel = function(data,onSuccess){

		var url = 'interventionReasonController.go/reasons/getInterventionCounts';
		var reqObj={
				orderId:data
		}
		ipMedHelperURLService.httpPostCall(url,reqObj,function (resp){
			if(resp!='' && resp!= undefined){
				if (resp.status == "success") {
					if(onSuccess){
						onSuccess(resp.InterventionCounts);
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
	
	service.saveOrder = function(medOrderData, pharmacyStatus, reasonId){
		medOrderData.emarSchedule = [];
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/saveOrder"),
			data:{"medOrder" : JSON.stringify(medOrderData), "userId" : global.TrUserId, "pharmacyStatus" : pharmacyStatus, "reasonId" : reasonId }
		});
	};
	
	service.getPreRequisites = function (medOrder, patientId, showMandatory){

		return $http({
			method: 'GET',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getPreRequisitesForOrder?patientId="+patientId+ "&userId="+global.TrUserId+"&orderId="+medOrder.orderId+"&showMandatory="+showMandatory)
		});
	};
	
	service.isInterventionsCompleted = function (orderId){

		return $http({
			method: 'GET',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/isInterventionsCompleted?orderId="+orderId)
		});
	};
	
	service.isMedAvailableForFacility = function (medOrderData){
		medOrderData.emarSchedule = [];
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/isMedAvailableForFacility"),
			data:{"medOrder" : JSON.stringify(medOrderData), "userId" : global.TrUserId}
		});
	};
	
	service.clearSessionLog = function(orderId, patientId){
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/clearSessionLog"),
			data:{"orderId" : orderId, "patientId": patientId, "userId" : global.TrUserId}
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
			url: makeURL('/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getOrderLabelPrintData'),
			data:objReqParams		
		});
	};
	/******************************************************************
	 *  Added to fetch medication order logs details pharmacy work queue
	 ******************************************************************/
	service.getMedicationOrderLogsData = function (patientId, concurrentUserId){
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/getMedOrderLogs"),
			data:{"patientId": patientId, "userId" : global.TrUserId, "concurrentUserId": concurrentUserId}			
		});
	};
	
	service.chargeCaptureBillingData = function(medOrderData, pharmacyStatus){
		medOrderData.emarSchedule = [];
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/chargeCaptureBillingData"),
			data:{"medOrder" : JSON.stringify(medOrderData), "userId" : global.TrUserId, "pharmacyStatus" : pharmacyStatus}
		});
	};
	
	service.checkPharmacyPrinterSetup = function (objReqParams){
		return $http({
			method: 'POST',
			url: makeURL("/mobiledoc/inpatientWeb/pharmacyWorkQueue.go/pharmacyPrinterSetup"),
			data:objReqParams 
		});
	};
}

angular.module('MedicationOrdersModule', ['ui.bootstrap','oc.lazyLoad', 'stafflookup', 'inPatient.dir.cptList','toastrMsg','insulinScaleViewApp','ip.dir.onlyNumbers', 'ip.dir.drugSizeValidations', 'ecw.service.pharmacyuitilityservice'])
.controller("medOrdersCtrl", function ($scope, $filter, $ocLazyLoad, $http, $modal, toastrService, medicationOrdersService, PharmacyUitilityService) {
	
	var vm = this ;
	var parent = $scope.objMedProfile;
	var serviceMedOrders = medicationOrdersService;
	vm.episodeEncounterId = parent.episodeEncounterId;
	vm.medOrderData = {};
	vm.medOrderData.medOrderDetailList = [];
	vm.leftMedOrderDetailList = [];
	vm.leftMedList = [];
	vm.orderPriorityMasterList = [];
	
	vm.txtOrderSearch = "";
	
	vm.pendingReasonList = [];
	vm.selectedReason = {};
	
	vm.patientId = 0;
	
	vm.encounterId = 0;
	vm.encounterType = 0;
	vm.pharmacyStatus = 0;
	
	vm.currentMedIndex = 0;
	
	vm.selectedAssignedTo = {};
	
	vm.totalDispensedVolume = 0;
	vm.totalDispensedVolumeOrg = 0;
	vm.totalVolume = 0;
	vm.totalDose = 0;
	
	vm.showMandatoryPreReq = true;
	
	vm.batchesList = [];
	
	vm.disableDualVerification = false;
	
	vm.scheduleValue = "";
	
	vm.alertStyle = "";
	
	vm.selectedMedication = {};
	
	vm.totalVolumeUnit = "";
	vm.numberOfPrint=0;
	vm.scrLabValue = "";
	vm.crclLabValue = "";
	
	vm.setPrintData=function(){
		if(vm.numberOfPrint>0){
			serviceMedOrders.getPrintData({orderId:vm.orderId,numberOfPrint:vm.numberOfPrint,medOrderId:vm.medOrderData.rxNumber}).success(function(response){
				if(response.status==="success"){
					toastrService.ecwMessage(toastrService.SUCCESS,"Label printed successfully");
				}else{
					toastrService.ecwMessage(toastrService.ERROR,!response.message?"Error occured while Printing Label.":response.message);
				}
			});
		}else{
			toastrService.ecwMessage(toastrService.ERROR,"Please specify number of copies to be printed.");
		}
	}
	
	
	vm.setAssignedTo = function() {
		vm.medOrderData.assignedToId = vm.selectedAssignedTo.staff.id;
		vm.medOrderData.assignedToName = vm.selectedAssignedTo.staff.name;
		$('.assigned-to-tooltip').hide();
	};
	
	vm.clearAssignedTo = function() {
		vm.medOrderData.assignedToId = 0;
		vm.medOrderData.assignedToName = "";
	};
	
	vm.init = function(){
		vm.orderId = orderId;
		vm.patientId = patientId;
		vm.encounterId = encounterId;
		vm.encounterType = encounterType;
		vm.pharmacyStatus = pharmacyStatus;
		vm.concurrentUserId = concurrentUserId;
		vm.getMedOrderDetails();
	};
	
	vm.getMedOrderDetails = function(){

		serviceMedOrders.getMedOrderDetails(vm.orderId, vm.pharmacyStatus, vm.patientId, vm.concurrentUserId).then(
				function successCallback(response) {
					
					vm.setMedOrderData(response.data);
					
					vm.leftMedList = response.data.leftMedList;
					vm.orderPriorityMasterList = response.data.orderPriorityMasterList;
					angular.forEach(vm.leftMedList, function(value, key) {
						if(value.orderId == vm.medOrderData.orderId){
							vm.currentMedIndex = key;
						}
					});
				},
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while loading Medication Order Details");
				}
		);
	};
	
	vm.getMedOrder = function(){
		vm.hideConcPrompt();
		serviceMedOrders.getMedOrder(vm.selectedMedication.orderId, vm.patientId, vm.concurrentUserId).then(
				function successCallback(response) {
					vm.setMedOrderData(response.data);
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while loading Medication Order Details");
				}
		);
	};
	
	vm.setMedOrderData = function(data){
		
		vm.medOrderData = data.medOrder;
		vm.rxInfoObj = getRequestJsonForRxInformation(Object.keys(vm.medOrderData.medOrderDetailList[0]), vm.medOrderData.medOrderDetailList[0]);
		
		angular.forEach(vm.medOrderData.medOrderDetailList, function(orderDetail, orderDetailKey) {
			vm.leftMedOrderDetailList.push(orderDetail);
		});

		vm.orderId = vm.medOrderData.orderId;
		vm.frequencyList = data.frequencyList;
		vm.vitals = data.preRequisites.vitals;
		vm.labs = data.preRequisites.labs;
		vm.batchesList = data.batchesList;
		vm.reflexAlerts = data.reflexAlerts;
		vm.interventionCounts= data.InterventionCounts;
		vm.securitySettings = data.SecuritySettings;
		vm.SecuritySettingsIntervention=data.SecuritySettingsIntervention;
		vm.scrLabValue = data.scrLabValue;
		vm.crclLabValue = data.crclLabValue;
		
		if(vm.reflexAlerts.length > 0){
			vm.alertStyle = "phar-ver-alerts";
		} else {
			vm.alertStyle = "";
		}
		
		vm.disableDualVerification = false;
		if(vm.medOrderData.dualVerification){
			vm.disableDualVerification = true;
		}
		vm.scheduleValue = "";
		angular.forEach(vm.medOrderData.emarSchedule, function(schedule, scheduleKey){
			vm.scheduleValue +=  vm.scheduleValue == "" ? schedule.scheduleTime : ", "+schedule.scheduleTime ;
		});
		
		angular.forEach(vm.medOrderData.medOrderDetailList, function(orderDetail, orderDetailKey) {
			if(vm.convertUOM(orderDetail.formularyDispenseUOM) == "ml"){
				vm.totalVolumeUnit = "ml";
				return;
			}
		});
		
		vm.calculateDoseVolume(vm.medOrderData.medOrderDetailList, false);
		vm.totalDispensedVolumeOrg = vm.totalDispensedVolume;
		
		vm.showMandatoryPreReq = true;
		
		//vm.createPharamacyLabelReqObject();
	};
	
	//function to set request json for Rx Education and Drug Monograph
	var getRequestJsonForRxInformation = function(jsonKeysArray, medOrderObj) {
		for(var i=0; i<jsonKeysArray.length; i++) {
			var lowerCaseKey = jsonKeysArray[i].toLowerCase();
			if(lowerCaseKey.includes('rxnorm') && medOrderObj[jsonKeysArray[i]]) {
				return {'id':medOrderObj[jsonKeysArray[i]], 'idType':'RxNorm'};
			} else if(lowerCaseKey.includes("gpi") && medOrderObj[jsonKeysArray[i]]) {
				return {'id':medOrderObj[jsonKeysArray[i]], 'idType':'GPI'};
			} /*else if(lowerCaseKey.includes("ddid") && medOrderObj[jsonKeysArray[i]]) {
				return {'id':medOrderObj[jsonKeysArray[i]], 'idType':'DDID'};
			}*/ else if(lowerCaseKey.includes("ndc") && medOrderObj[jsonKeysArray[i]]) {
				return {'id':medOrderObj[jsonKeysArray[i]], 'idType':'NDC'};
			}	
		}
	}
	
	vm.initMedDispense = function(){
		
		angular.forEach(vm.medOrderData.availableProducts, function(avlbProduct, avlbProductKey) {
			
			avlbProduct.dispenseQty = "";
			avlbProduct.dispense = "";
			avlbProduct.orderDose = "";
			avlbProduct.orderDispense = "";
			avlbProduct.actualDispense = "";
			
			angular.forEach(vm.medOrderData.medOrderDetailList, function(orderDetail, orderDetailKey) {
				if(orderDetail.drugFormularyId == avlbProduct.drugFormularyId && !orderDetail.ivDiluent && !orderDetail.additive){
					avlbProduct.dispenseQty = orderDetail.dispenseQty;					
					avlbProduct.dispense = orderDetail.dispense;
					avlbProduct.orderDose = orderDetail.orderDose;
					if(orderDetail.drugTypeBulk) {
						var packSizeUnit = Math.ceil(orderDetail.dispense/orderDetail.packSize);
						avlbProduct.actualDispense  = Number(packSizeUnit * orderDetail.packSize);
					} else {
						avlbProduct.actualDispense = orderDetail.actualDispense
					}
					return;
				}
			});
			if(avlbProduct.actualDispense != '' && avlbProduct.actualDispense > 0){
				avlbProduct.totalCostCalculatedWithChargeType = vm.calculateTotalMarkUpCostWithDispense(avlbProduct);
			}
		});
		
		angular.forEach(vm.medOrderData.availableDiluents, function(avlbDiluent, avlbDiluentKey) {
			
			avlbDiluent.dispenseQty = "";
			avlbDiluent.dispense = "";
			avlbDiluent.orderDose = "";
			avlbDiluent.orderDispense = "";
			avlbDiluent.actualDispense = "";
			
			angular.forEach(vm.medOrderData.medOrderDetailList, function(orderDetail, orderDetailKey) {
				if(orderDetail.drugFormularyId == avlbDiluent.drugFormularyId &&  (orderDetail.ivDiluent || orderDetail.additive)){
					avlbDiluent.dispenseQty = orderDetail.dispenseQty;
					avlbDiluent.dispense = orderDetail.dispense;
					avlbDiluent.orderDose = orderDetail.orderDose;
					if(orderDetail.drugTypeBulk) {
						var packSizeUnit = Math.ceil(orderDetail.dispense/orderDetail.packSize);
						avlbDiluent.actualDispense  = Number(packSizeUnit * orderDetail.packSize);
					} else {
						avlbDiluent.actualDispense = orderDetail.actualDispense
					}
					return;
				}
			});
			if(avlbDiluent.actualDispense != '' && avlbDiluent.actualDispense > 0){
				avlbDiluent.totalCostCalculatedWithChargeType = vm.calculateTotalMarkUpCostWithDispense(avlbDiluent);
			}
		});
	};
	
	vm.calculateDoseVolume = function(medOrderDetailList, validate){
		totalVolume = 0;
		totalVolumeDispense = 0;
		totalDose = 0;
		
		var isCalculate = true;
		
		angular.forEach(medOrderDetailList, function(obj, key) {
			var convertedDoseUOM = vm.convertUOM(obj.formularyDoseUOM);
			var convertedDispenseUOM = vm.convertUOM(obj.formularyDispenseUOM);
			var convertedOrderDispenseUOM = vm.convertUOM(obj.formularyDispenseUOM);
			var doseSize = obj.formularyDoseSize;
			var dispenseSize = obj.formularyDispenseSize;
			var orderDose = obj.orderDose;			
			
			var addInVolume = false;
			
			if(convertedDoseUOM == "gm"){
				doseSize = parseFloat(obj.formularyDoseSize) * 1000;
				orderDose = parseFloat(obj.orderDose) * 1000;
			}
			
			if(convertedDispenseUOM == "gm"){
				dispenseSize = parseFloat(obj.formularyDispenseSize) * 1000;
			}
			
			if(convertedDispenseUOM == "ml"){
				addInVolume = true;
			}
			
			//if(obj.ivDiluent || obj.additive || obj.ivDiluentId > 0 || addInVolume){
			if(addInVolume){
				obj.dispenseQty = obj.dispenseQty == "" ? 1 : obj.dispenseQty;
				totalVolume += parseFloat(obj.dispense);
				totalVolumeDispense += parseFloat(dispenseSize) * obj.dispenseQty;
			}
			
			if(!vm.isNullEmpty(doseSize)  && !vm.isNullEmpty(orderDose) && !obj.ivDiluent && !obj.additive && obj.ivDiluentId == 0){
				if( convertedDoseUOM === vm.convertUOM(vm.medOrderData.doseUnit)){
					if(vm.medOrderData.ISSFlag)
						totalDose = orderDose;	
					else
						totalDose += parseFloat(orderDose);
				}
			}
			
			if(!obj.calculate){
				isCalculate = false;
			}
		});
		
		if(vm.convertUOM(vm.medOrderData.doseUnit) === "gm"){
			totalDose = totalDose / 1000;
		}
		
		if(validate){
			if(isCalculate){
				if(totalDose != vm.medOrderData.dose && vm.medOrderData.totalVolume == 0){
					showIPMessage("Edited order dose does not match 'Ordered Dose'. Please make changes to continue.", "inputelm2","ErrorMsg");
					return false;
				}
				
				if(vm.medOrderData.orderType != 'Medication' && (totalVolumeDispense < totalVolume)){
					showIPMessage("Dispensed Volume is less than Ordered Volume. Please enter valid values.", "inputelm2","ErrorMsg");
					return false;
				}
				
				if(vm.medOrderData.orderType != 'Medication' && (totalVolume != vm.medOrderData.totalVolume)){
					if(confirm("Total Ordered Volume is not equal to the changed volume. Do you wish to continue?")){
						//toastrService.ecwMessage(toastrService.WARNING, "Total Ordered Volume is not equal to the changed volume.");
						return true;
					} else {
						return false;
					}
				}
			} else {
				showIPMessage("Calculation flag is off for one of the medication. Please make sure that you have entered valid values.", "inputelm2","ErrorMsg");
				return true;
			}
			
		} else {			
				vm.totalVolume = isNaN(totalVolume) ? "" : totalVolume;
				vm.totalDispensedVolume = isNaN(totalVolumeDispense) ? "" : totalVolumeDispense ;				
				if(vm.medOrderData.ISSFlag)
					vm.totalDose = totalDose;
				else
					vm.totalDose = isNaN(totalDose) ? "" : totalDose;				
		}
		return true;
		
	};
	
	vm.convertUOM = function(uom){
		if(uom == null || uom == undefined){
			uom = "";
		}
		uom = uom.toLowerCase();
		if(uom == "mg" || uom == "milligram" || uom == "milligrams"){
			return "mg";
		}
		if(uom == "gram" || uom == "grams" || uom == "g" || uom == "gm" || uom == "gms"){
			return "gm";
		}
		if(uom == "ml" || uom == "cc" || uom == "milliliter"){
			return "ml";
		}
		return uom;
	};
	
	vm.clearSessionLog = function(){
		serviceMedOrders.clearSessionLog(vm.orderId, vm.patientId).then(
				function successCallback(response) {
					//vm.setMedOrderData(response.data);
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while clearing session lock");
				}
		);
	};
	
	vm.showOrder = function(med, index){
		
		vm.clearSessionLog();
		
		vm.currentMedIndex = index;
		if(vm.currentMedIndex == vm.leftMedList.length){
			vm.closeMedOrders();
			return ;
		}
		
		vm.selectedMedication = med;
		
		serviceMedOrders.getConcurrentUserLog(med.orderId, vm.patientId, global.TrUserId).then(
			function successCallback(response) {
				vm.concurrentUserLog = response.data;
				
				if(vm.concurrentUserLog.id != 0){
					if(vm.concurrentUserLog.allowContinue){
						vm.showConcPrompt();
					} else {
						showIPMessage(vm.concurrentUserLog.userName + ' is currently accessing this order', 'inputelm2','ErrorMsg');
					}
				}else {
					vm.getMedOrder();
				}
			}, 
			function errorCallback(response) {
				toastrService.ecwMessage(toastrService.ERROR, "Error occured while checking for concurrent user");
			}
		);
		
	};
	
	vm.showConcPrompt = function(){
		$('#medOrderConcPrompt').show();
	};
	
	vm.hideConcPrompt = function(){
		$('#medOrderConcPrompt').hide();
	};
	
	
	vm.validateDispense = function(){
		
		if(vm.medOrderData.discontinued){
			return true;
		}
		
		var isCalculate = true;
		angular.forEach(vm.medOrderData.medOrderDetailList, function(orderDetail, orderDetailKey) {
			if(!orderDetail.calculate){
				isCalculate = false;
				return;
			}
		});
		
		if(!isCalculate){
			showIPMessage("Calculation flag is off for one of the medication. Please make sure that you have entered valid values.", "inputelm2","ErrorMsg");
			return true;
		}
		
		if(vm.medOrderData.medOrderDetailList.length == 0){
			showIPMessage('Cannot proceed since Dispense Value is blank', 'inputelm2','ErrorMsg');
			return false;
		}
		
		angular.forEach(vm.medOrderData.medOrderDetailList, function(value, key){
			if(value.dispenseQty == ""){
				showIPMessage('Cannot proceed since Dispense Value is blank', 'inputelm2','ErrorMsg');
				return false;
			}
		});

		if(vm.totalDose != vm.medOrderData.dose && vm.medOrderData.totalVolume == 0){
			showIPMessage("Dispensed Dose doesn't match Ordered Dose. Please enter valid values.", "inputelm2","ErrorMsg");
			return false;
		}
		
		if(vm.medOrderData.orderType != 'Medication' && (vm.totalDispensedVolume < vm.medOrderData.totalVolume)){
			showIPMessage("Dispensed Volume is less than Ordered Volume. Please enter valid values.", "inputelm2","ErrorMsg");
			return false;
		}
		
		return true;
		
	};
	
	vm.calculateTotalMarkUpCostWithDispense = function(item){
		var result = 0;
		var disp = 0;		
		var dispenseQty = Math.ceil(item.dispense/item.formularyDispenseSize);
		disp = Number(Number(item.formularyDispenseSize) * Number(dispenseQty));		
	    if(disp != "") {
	    	if(item.priceRuleParam != null && item.priceRuleParam != undefined){
				if(item.priceRuleParam.chargePerDose){
					result = (((item.priceRuleParam.costToBeConsidered * disp) * item.priceRuleParam.markUp/100) + item.priceRuleParam.additionalCharge);
				} else {
					result = ((item.priceRuleParam.costToBeConsidered * item.priceRuleParam.markUp/100) + item.priceRuleParam.additionalCharge) * disp;
				}
	
				if(result < item.priceRuleParam.minimumCharge){
					result = item.priceRuleParam.minimumCharge;
				}
			}

			result = result.toFixed(3);
			item.totalCostCalculatedWithChargeType = result;*/
			
		} else {
			item.totalCostCalculatedWithChargeType = "0";
		}
		return result;
	};
	
	vm.updateDispense = function(){
		
		var tempDispenseList = [];
		var productDispenseChanged = false;
		var diluentDispenseChanged = false;
		var dispBlank = false;
		
		var displayIndex = 1;
		
		angular.forEach(vm.medOrderData.availableProducts, function(value, key){
			
			vm.calculateDispenseQty(value);
			
			if((!vm.isNullEmpty(value.dispenseQty) && vm.isNullEmpty(value.dispense)) || (!vm.isNullEmpty(value.dispense) && vm.isNullEmpty(value.dispenseQty))){
				
				dispBlank = true;
				return;
			}
			
			if( !vm.isNullEmpty(value.dispenseQty) && value.dispenseQty != "0"){
				var temp = {};
				temp.ipItemId = value.ipItemId;
				temp.itemName = value.itemName;
				temp.brandName = value.brandName;
				temp.genericName = value.genericName;
				temp.orderStrength = value.orderStrength;
				temp.orderFormulation = value.orderFormulation;
				temp.dispenseQty = value.dispenseQty;
				temp.dispense = value.dispense == null || value.dispense == undefined ? value.formularyDispenseSize : value.dispense;
				temp.orderDose = value.orderDose;
				temp.calculate = value.calculate;
				if(temp.orderDose == "" && temp.calculate){
					temp.orderDose = value.formularyDoseSize * (value.dispense/value.formularyDispenseSize);
				}
				temp.formularyDispenseUOM = value.formularyDispenseUOM;
				temp.orderNDCCode = value.orderNDCCode;
				temp.orderDDID = value.orderDDID;
				temp.drugFormularyId = value.drugFormularyId;
				temp.nonBillable = value.nonBillable;
				temp.chargeCode = value.chargeCode;
				temp.rxNorm = value.rxNorm;
				temp.ivDiluent = false;
				temp.additive = false;
				temp.ivDiluentId = 0;
				temp.formularyDoseSize = value.formularyDoseSize;
				temp.formularyDoseUOM = value.formularyDoseUOM;
				temp.formularyDispenseSize = value.formularyDispenseSize;
				temp.formularyDispenseUOM = value.formularyDispenseUOM;
				temp.totalCostCalculatedWithChargeType = value.totalCostCalculatedWithChargeType;
				temp.actualDispense = value.formularyDispenseSize * value.dispenseQty;
				temp.displayIndex = displayIndex++;
				tempDispenseList.push(temp);

				productDispenseChanged = true;
			}
		});
		
		if(dispBlank){
			showIPMessage('Order Dispense or Dispense Qty is blank.', 'inputelm2','ErrorMsg');
			return;
		}
		
		angular.forEach(vm.medOrderData.availableDiluents, function(value, key){
			
			vm.calculateDispenseQty(value);
			
			if((!vm.isNullEmpty(value.dispenseQty) && vm.isNullEmpty(value.dispense)) || (!vm.isNullEmpty(value.dispense) && vm.isNullEmpty(value.dispenseQty))){
				dispBlank = true;
				return;
			}
			
			if( !vm.isNullEmpty(value.dispenseQty) && value.dispenseQty != "0"){
				var temp = {};
				temp.ipItemId = value.ipItemId;
				temp.itemName = value.itemName;
				temp.brandName = value.brandName;
				temp.genericName = value.genericName;
				temp.orderStrength = value.orderStrength;
				temp.orderFormulation = value.orderFormulation;
				temp.dispenseQty = value.dispenseQty;
				temp.orderNDCCode = value.orderNDCCode;
				temp.orderDDID = value.orderDDID;
				temp.drugFormularyId = value.drugFormularyId;
				temp.nonBillable = value.nonBillable;
				temp.chargeCode = value.chargeCode;
				temp.rxNorm = value.rxNorm;
				temp.ivDiluent = value.ivDiluent;
				temp.additive = value.additive;
				temp.ivDiluentId = -1; //Storing 1 for now
				temp.dispense = value.dispense == null || value.dispense == undefined ? value.formularyDispenseSize : value.dispense;
				temp.orderDose = temp.dispense;
				temp.ivVolume = temp.dispense;
				temp.formularyDispenseUOM =  value.formularyDispenseUOM;
				temp.formularyDoseSize = value.formularyDoseSize;
				temp.formularyDoseUOM = value.formularyDoseUOM;
				temp.formularyDispenseSize = value.formularyDispenseSize;
				temp.formularyDispenseUOM = value.formularyDispenseUOM;
				temp.totalCostCalculatedWithChargeType = value.totalCostCalculatedWithChargeType;
				temp.actualDispense = value.formularyDispenseSize * value.dispenseQty;
				temp.calculate = value.calculate;
				temp.displayIndex = displayIndex++;
				tempDispenseList.push(temp);

				diluentDispenseChanged = true;
			}
		});
		
		if(dispBlank){
			showIPMessage('Order Dispense or Dispense Qty is blank.', 'inputelm2','ErrorMsg');
			return;
		}
		
		var closeWindow = true;
		
		if(tempDispenseList.length == 0 && !vm.isNullEmpty(vm.medOrderData.dose)){
			showIPMessage("Edited order dose does not match 'Ordered Dose'. Please make changes to continue.", "inputelm2","ErrorMsg");
			closeWindow = false;
		}
		
		if( productDispenseChanged || diluentDispenseChanged ){
			if(vm.calculateDoseVolume(tempDispenseList, true)){
				if(vm.medOrderData.orderType != 'Medication' && !diluentDispenseChanged){
					angular.forEach(vm.medOrderData.medOrderDetailList, function(value, key){
						if(value.ivDiluent == false && value.additive == false && value.ivDiluentId == 0){
							var index = vm.medOrderData.medOrderDetailList.indexOf(value);
							vm.medOrderData.medOrderDetailList.splice(index, 1);
						}
					});
					
					angular.forEach(tempDispenseList, function(value, key){
						vm.medOrderData.medOrderDetailList.push(value);
					});
					
				} else {
					vm.medOrderData.medOrderDetailList = [];
					vm.medOrderData.medOrderDetailList = tempDispenseList;
				}
				
				var totalPrice = 0;
				angular.forEach(vm.medOrderData.medOrderDetailList, function(value, key){
					totalPrice += parseFloat(value.totalCostCalculatedWithChargeType);
				});
				
				vm.medOrderData.totalPrice = totalPrice.toFixed(3);
				
				vm.calculateDoseVolume(vm.medOrderData.medOrderDetailList, false);
			} else {
				closeWindow = false;
			}
		}
		if(closeWindow){
			vm.closeEditDispense();
		}
	};
	
	vm.verifyAndNext = function(){
		
		if(vm.validateDispense()){
			
			if(vm.medOrderData.medOrderDetailList.length > 1){
				vm.showMultiDispenseMessage();
			} else {
				vm.continueVerify();
			}
		}
	};
	
	vm.showMultiDispenseMessage = function(){
		showIPMessage("You are using multiple products for dispensing. Do you wish to continue?", 'inputelm3','ConfirmMsg', 'confirmMultiMed', 'medOrdersCtrl as vm');
	};
	
	$scope.confirmMultiMed = function(isConfirm){
		if(isConfirm == "yes"){
			vm.continueVerify();
		}
	};
	
	vm.continueVerify = function(){
		
		serviceMedOrders.isMedAvailableForFacility(vm.medOrderData).then(
				function successCallback(response) {
					if(response.data == false && !vm.medOrderData.discontinued){
						showIPMessage('One of the dispensed medication is not available for facility.', 'inputelm2','ErrorMsg');
					} else {
						serviceMedOrders.isInterventionsCompleted(vm.medOrderData.orderId).then(
							function successCallback(response) {
								if(response.data == false){
									showIPMessage('One or more Interventions associated with this order are not completed.', 'inputelm2','ErrorMsg');
								}
								
								serviceMedOrders.saveOrder(vm.medOrderData, 2, 0).then(
										function successCallback(response) {
											toastrService.ecwMessage(toastrService.SUCCESS, "Order has been verified successfully.");
											
											if(response.data.PyxisAndLabelResponse.HL7MsgResponse.Status==='success'){
												toastrService.ecwMessage(toastrService.SUCCESS, "Message has been send to dispensing system successfully.");
												vm.leftMedList[vm.currentMedIndex].pharmacyStatusId = 2;
												vm.showOrder(vm.leftMedList[vm.currentMedIndex + 1], vm.currentMedIndex + 1);
											}else if(response.data.PyxisAndLabelResponse.HL7MsgResponse.Status==='failed'){
												toastrService.ecwMessage(toastrService.ERROR, response.data.PyxisAndLabelResponse.HL7MsgResponse.Message);
											}
										/*	if(response.data.printLabelMsg == true){
												toastrService.ecwMessage(toastrService.WARNING, "This order is not picked in any batch . Please print the label manually.");
											}*/
											
										}, 
										function errorCallback(response) {
											toastrService.ecwMessage(toastrService.ERROR, "Error occured while loading Medication Order Details");
										}	
									);
							}, 
							function errorCallback(response) {
								toastrService.ecwMessage(toastrService.ERROR, "Error occured while checking Intervention Status");
							}	
						
						);
					}
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while checking medication availability for facility");
				}
			);
	};
	
	vm.showSkipMessage = function(){
		showIPMessage("Data won't be saved if you skip. Are you sure you want to continue?", 'inputelm3','ConfirmMsg', 'skipMed', 'medOrdersCtrl as vm');
	};
	
	$scope.skipMed = function(isConfirm){
		if(isConfirm == "yes"){
			vm.showOrder(vm.leftMedList[vm.currentMedIndex + 1], vm.currentMedIndex + 1);
		}
	};
	
	vm.closeMedOrders = function(){
		$("#mediOrderModal").modal("hide");
		vm.clearSessionLog();
		parent.getMedProfile();
	};
	
	vm.openPendingReasons = function(){
		serviceMedOrders.getPendingReasons().then(
				function successCallback(response) {
					vm.pendingReasonList = response.data;
					vm.selectedReason = {};
					$("#ulPendingReasons").show();
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while getting Pending Reasons");
				}
		);
	};
	
	vm.setReason = function(reason){
		vm.selectedReason = reason;
		$("#divReasonDropdown").find("div.dropdown-backdrop").remove();
	};
	
	vm.closePendingReasons = function(){
		$("#ulPendingReasons").hide();
	};
	
	vm.saveAsPending = function(){
		if(vm.selectedReason.id == null || vm.selectedReason.id == undefined){
			showIPMessage('Please select a reason.', 'inputelm2','ErrorMsg');
			return;
		}
		if(vm.validateDispense()){
			serviceMedOrders.saveOrder(vm.medOrderData, 1, vm.selectedReason.id).then(
				function successCallback(response) {
					toastrService.ecwMessage(toastrService.SUCCESS, "Details saved successfully.");
					vm.leftMedList[vm.currentMedIndex].pharmacyStatusId = 1;
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while saving data");
				}	
			);
		}
		
		$("#ulPendingReasons").hide();
	};
	vm.refreshInterventionCountPanel= function(){
		serviceMedOrders.refreshInterventionCountPanel(vm.orderId,function(response){
			vm.interventionCounts=response;
		});
	};
	vm.showInterventionPopUp = function(mode,interventionId){
		var interventionId=interventionId;
		var patientId=vm.patientId;
		if(mode!=undefined && mode=='Edit'){
			if(vm.SecuritySettingsIntervention!==undefined && vm.SecuritySettingsIntervention.InterventionEditAccess!==1){
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
					serviceMedOrders.getInterventionIdByPtId(patientId, function(response){
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
			if(vm.SecuritySettingsIntervention!==undefined && vm.SecuritySettingsIntervention.InterventionAddAccess!==1){
				toastrService.ecwMessage(toastrService.INFORMATION, "User does not have security access");
				angular.element('#addBtn').click();//To Hide the window
				return;
			}
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
		                 '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/intervention/js/intervention.js',
		                 '/mobiledoc/jsp/inpatientWeb/templates/spellCheck-tpl.js',
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
							vm.refreshInterventionCountPanel();
						},800);
				    },function (result) {
				    	// On Model Close	
				    });
				});
	}


	
	vm.showDevAlert = function(){		
		vm.medicationOrderLogsArray = [];
		$("#logsModal").modal({animation: true,windowClass: 'large_width',keyboard: false,cls: 'global',});
		serviceMedOrders.getMedicationOrderLogsData(vm.patientId, vm.concurrentUserId).then(
				function successCallback(response) {
					vm.medicationOrderLogsArray = response.data;					
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while getting Med Order Logs");
				}
		);
	};
	vm.closeLogs = function(){
		$('#logsModal').modal('hide');		
        $('.modal-backdrop.fade').remove();
	};
	
	vm.showAssignedTo = function(){
		$("#ulAssignedTo").show();
	};
	
	vm.showPrintLabel = function(){
		serviceMedOrders.checkPharmacyPrinterSetup({facilityId:vm.medOrderData.facilityId,orderTypeId:vm.medOrderData.orderTypeInt}).success(function(response){
				if(response.status==='success' && response.data >0){
					$("#printModal").modal('show');
					$("#noOfLabel").focus();
					
				}else{
					toastrService.ecwMessage(toastrService.ERROR,"Please configure printer setup for this patient facility and orderType");
				}
			});
		
	};
	
	vm.hidePrintLabel = function(){
		$("#printModal").modal('hide');
	};
	vm.getPreRequisites = function(){
		
		serviceMedOrders.getPreRequisites(vm.medOrderData, vm.patientId, vm.showMandatoryPreReq).then(
				function successCallback(response) {

					vm.vitals = response.data.vitals;
					vm.labs = response.data.labs;
				}, 
				function errorCallback(response) {
					toastrService.ecwMessage(toastrService.ERROR, "Error occured while getting Pre-requisites");
				}
		);
		
	};
	
	vm.setMedInfo = function(item) {
		vm.unitCost = item.costCalculatedWithChargeType;
		vm.unitCharged = item.toTalHCPCSUnit;
	};
	
	vm.setGenerateBarcode = function(val){
		vm.medOrderData.generateBarcode = val;
	};
	
	vm.setFrequency = function(frequency){
		vm.medOrderData.frequencySchedule = frequency.scheduledTimeList;
	};
	
	vm.calculateDispense = function(item){
		if(item.calculate){
			item.dispenseQty= Math.ceil(item.dispenseQty);
			if( !vm.isNullEmpty(item.formularyDispenseSize)){
				if(item.dispenseQty != ""){
					item.dispense = Math.ceil(item.dispenseQty * item.formularyDispenseSize);
				} else {
					item.dispense = "";
				}
				if(item.dispense != ""){
					item.orderDose = item.formularyDoseSize * (parseFloat(item.dispense)/parseFloat(item.formularyDispenseSize));
				}
			}
		}
	};
	
	vm.calculateDispenseQty = function(item){
		if(item.calculate){
			if( !vm.isNullEmpty( item.formularyDispenseSize)){
				if(item.dispense != ""){
					vm.calculateDispenseDoseEditDispense(item, 'dispense');
					
					item.dispenseQty = Math.ceil(item.dispense/item.formularyDispenseSize);
				} else {
					item.orderDose = "";
					item.dispenseQty = "";
				}
				
			}
		}
	};
	vm.calculateDispenseDoseEditDispense = function(item, sFlag) {	
	var params=$.param({doseSize : item.orderDose, doseSizeUOM : item.formularyDoseUOM, dispenseSize : item.dispense, dispenseSizeUOM : item.formularyDispenseUOM, otsDoseSize : item.formularyDoseSize, otsDispenseSize : item.formularyDispenseSize, keyFlag : sFlag});				
	if(item.calculate)
	{
		if(item.formularyDoseSize !='')
		{
			if(item.formularyDispenseSize !='')
			{
				PharmacyUitilityService.calculateDispenseDose(params).then(			
				function(response) {					
					if (response && response != null && response!="") {
						if(response.data.doseSize !='') {
							item.orderDose  = response.data.doseSize;							
						} 	
				     }
				});					
			  }
		   }
		}	  
	};	
	vm.openEditDispense = function(){
		
		vm.initMedDispense();
		
	    var _that = $(".dispense.icon-edit");
	    $('.edit-products-popup').show().animate({}, 100, function() {
	        $(this).position({ of: _that,
	            my: 'right bottom',
	            at: 'right+400 top+235',
	            collision: "flipfit",
	        }).animate({
	            "opacity": 1
	        }, 100);
	    });
	};
	
	vm.closeEditDispense = function(){
		$('.edit-products-popup').hide();
	};
	
	vm.showTimePicker = function(id){
		 $(".global-time-hm").timepicker();
		$(".schedule_"+id).timepicker("show");
	};
	
	vm.showBillingData = function(){
		$("#showBillingDataModal").modal('show');
	};
	
	vm.hideBillingData = function(){
		$("#showBillingDataModal").modal('hide');
	};
	
	vm.copyEMARInst = function(eMARInstructions){
		 var $temp = $("<input>");
		  $("body").append($temp);
		  $temp.val(eMARInstructions).select();
		  document.execCommand("copy");
		  $temp.remove();
	};
	
	vm.chargeBillingDataCapture = function() {
		if(vm.medOrderData.pharmacyStatus == 2){
			showIPMessage("Are you sure you want to capture charge for this order?<br><font size=1 color='#c0c0c0'>Verified by "+vm.medOrderData.verifiedByName+" on "+vm.medOrderData.verifiedDateTime+" </font>",'','ConfirmMsg', 'captureChargeBillingDataCallback', '');		
		}
	};
	captureChargeBillingDataCallback = function(status){
		if(status === 'yes'){
			serviceMedOrders.chargeCaptureBillingData(vm.medOrderData, vm.medOrderData.pharmacyStatus).then(
					function successCallback(response) {
						if(response.data == true){
							toastrService.ecwMessage(toastrService.SUCCESS, "Charges [$"+vm.medOrderData.totalPrice+"] have been dropped successfully for this patient's account. You can validate the same in HIM > Coding And Abstraction");
						} else {
							toastrService.ecwMessage(toastrService.ERROR, "Error occured while posting charges.");
						}
						
					}, 
					function errorCallback(response) {
						toastrService.ecwMessage(toastrService.ERROR, "Error occured while posting charges");
					}	
				);
		}
	};
	
	$('.assigned-to-tooltip').hide();
    $(document).on('click', '.assigned-to-btn', function () {
    	if($(".assigned-to-tooltip").css("display") != "none"){
    		$('.assigned-to-tooltip').hide();
    	} else {
	        var _that = this;
	        $('.assigned-to-tooltip').show().animate({}, 100, function () {
	            $(this).position({
	                of: _that,
	                my: 'left-25 top+28',
	                at: 'left top',
	            }).animate({
	                "opacity": 1
	            }, 100)
	        });
    	}

    });

	$(document).on("click", ".schedule-btn", function () {
	        var _that = $(this);
	    $('.schedule-popup').show().animate({}, 100, function () {
	        $(this).position({
				of: _that,
				my: 'right bottom',
				at: 'right top+215',
				collision: "flipfit",
			}).animate({
				"opacity": 1
			}, 100);
	    });
	});
	
	$('.close-btn').on('click', function(){
	    $('.schedule-popup').hide();
	});


	$(document).mouseup(function (e)  {
		var container = $('.edit-products-popup');
		var errorPopup = $('#prompt-red');
		if ((!container.is(e.target) && container.has(e.target).length === 0) && (!errorPopup.is(e.target) && errorPopup.has(e.target).length === 0) ) {
			container.hide(); 
		}
	});
	
	$('#vitals-labs').on('click', function (e) {
        e.stopPropagation();
        var formsection = $('.vital-tab-right-panel');
        if (formsection.hasClass('visible')) {
            formsection.animate({
                "right": "0"
            }, "2000").removeClass('visible');
        } else {
            formsection.animate({
                "right": "-400px"
            }, "2000").addClass('visible');
        }
        $('.med-date-time').hide();
    });
    $('.close-vital-lab').on('click', function (e) {
        e.stopPropagation();
        var formsection = $('.vital-tab-right-panel');
        if (formsection.hasClass('visible')) {
            formsection.animate({
                "right": "0"
            }, "2000").removeClass('visible');
        } else {
            formsection.animate({
                "right": "-400px"
            }, "2000").addClass('visible');
        }
    });
    
 // script for Time format (HH:mm)
    $(".global-time-hm").inputmask('[h:s]');

	$('.medication-orders-list, #logsDetailTable,.lisinopril-tbody,.dispense-table, .schedule-table-scroll, .table-scroll-hgt').perfectScrollbar();
	$('.medication-orders-list, #logsDetailTable,.lisinopril-tbody,.dispense-table, .schedule-table-scroll, .table-scroll-hgt').removeClass("ps-active-x");
	
	vm.isNullEmpty = function(value){
		return value == "" || value == null || value == undefined;
	};

}).service('medicationOrdersService',['$http','$modal', 'ipMedHelperURLService','toastrService',medicationOrdersService]);

function floatDigit(){
	var charCode = (event.which) ? event.which : event.keyCode;
	if (charCode > 31 && ((charCode < 48 || charCode > 57) && charCode != 46)){
		return false;
	}
	return true;
}

