(function() {
	angular.module('rxInformationApp',['ecw.dir.perfectscrollbar','ecw.ip.ipFn', 'ecw.ip.print']).controller('rxInfoController', function ($scope, $http, $modalInstance, rxInfoObj, ipFn, PRINTTYPE){
		var rxInfoCtrl = this;
		
		/* initialization function */
		rxInfoCtrl.init = function() {
			rxInfoCtrl.highLightedRow = "";
			rxInfoCtrl.print = {format:12,destination:PRINTTYPE.DATA};
			rxInfoCtrl.masterEncounterId = rxInfoObj.masterEncounterId;
			rxInfoCtrl.patientId = rxInfoObj.patientId;
			rxInfoCtrl.activeTab = 1;
			rxInfoCtrl.hasData = false;
			rxInfoCtrl.disMeds = rxInfoObj.disMeds;
			setDrugListForRxInfo();
		}
	
		function setDrugListForRxInfo() {
			rxInfoCtrl.drugList = [];
			if(rxInfoCtrl.disMeds.StartData){		
				rxInfoCtrl.drugList = getRxInfoObject(rxInfoCtrl.disMeds.StartData);
	    	}
	    	if(rxInfoCtrl.disMeds.ContData){
	    		rxInfoCtrl.drugList = getRxInfoObject(rxInfoCtrl.disMeds.ContData);
	    	}
	    	if(rxInfoCtrl.disMeds.StopData){
	    		rxInfoCtrl.drugList = getRxInfoObject(rxInfoCtrl.disMeds.StopData);
	    	}
	    	if(rxInfoCtrl.disMeds.HoldData){
	    		rxInfoCtrl.drugList = getRxInfoObject(rxInfoCtrl.disMeds.HoldData);
			}
		}
		
		function getRxInfoObject(dataMap) {
			$.each(dataMap,function(k,v){
				var obj = {drugName: '', drugDesc: ''};
				if(!ipFn.isUNE(v.rxDetailsObj) && !ipFn.isUNE(v.rxDetailsObj.itemsObj)) {
					obj.drugName = v.rxDetailsObj.itemsObj.drugName;
					obj.drugDesc = getNotNullValues(v.rxDetailsObj.itemsObj.drugName) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.strength) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.strengthUnit) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.formulationDesc ) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.take) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.frequencyDesc  ) + ' ' +
				  				   getNotNullValues(v.rxDetailsObj.routeDesc);
				}
				
				obj.medOrder = v;           		
				rxInfoCtrl.drugList.push(obj); 
			});
			return rxInfoCtrl.drugList;
		}
		
		function getNotNullValues(obj) {
			 return (ipFn.isUNE(obj) || obj.includes('null')) ? '' : obj;	
		}
		
		rxInfoCtrl.close = function() {
			  $modalInstance.close(true);
			  rxInfoCtrl.print.printData = {};
		}
	
		rxInfoCtrl.sectionHighLight = function(rowId, row) {
			$("#"+ rxInfoCtrl.highLightedRow).removeClass("rowHighLight");
			$("#"+ rowId).addClass("rowHighLight");
			rxInfoCtrl.highLightedRow = rowId;
			rxInfoCtrl.drugName = rxInfoCtrl.drugList[row].drugName;
			rxInfoCtrl.medOrder = rxInfoCtrl.drugList[row].medOrder;
	
			rxInfoCtrl.loadTab(rxInfoCtrl.activeTab);
		}
		
		//event to highlight the first row when the list of drugs is rendered
		$scope.$on('ngRepeatFinished', function() {
			$(".rxInfoDrugList").find('tr:first').addClass('rowHighLight');
			rxInfoCtrl.highLightedRow = 'drugDesc0';
			rxInfoCtrl.drugName = rxInfoCtrl.drugList[0].drugName;
			rxInfoCtrl.medOrder = rxInfoCtrl.drugList[0].medOrder;

			rxInfoCtrl.loadTab(1);
		});
	
		rxInfoCtrl.loadTab=function(tab){
			rxInfoCtrl.activeTab = tab;
			resetRxEduData();
			resetMonographData();
			
			if(tab===1){
				getRxEducationData(rxInfoCtrl.medOrder);
			} else if(tab ===2) {
				getDrugMonographData(rxInfoCtrl.medOrder);
			}
		}
		
		function getRxEducationData(medOrder) {
			var requestDataJsonObject = getRequestJson(Object.keys(medOrder));
			
			if(ipFn.isUNE(requestDataJsonObject.id) || "0" === requestDataJsonObject.id) {
				$('.rxEduInfo').html('');
				rxInfoCtrl.hasData = false;
				showIPMessage('Invalid RxNorm', '', 'ErrorMsg', '', '');
			} 
			else {
				var requestData = {data: [], "requestToken" : "requestToken", "authToken" : "authToken"};
				requestData.data.push(requestDataJsonObject);
				
				$http({
					method: 'POST', 
					url: '/mobiledoc/inpatientWeb/rxEducation/getRxEducationData', 
					data : JSON.stringify(requestData)
				}).success(function (response) {
					if(response && response.data && response.data.data && response.data.data.length > 0) {
						$('.rxEduInfo').html(response.data.data[0].document);
						rxInfoCtrl.hasData = true;
						rxInfoCtrl.print.printData = response.data.data[0].document;
					} else {
						resetRxEduData();
						showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
					}
				}).error(function(response){
					resetRxEduData();
					showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
				});	
			}
		}
		
		function resetRxEduData() {
			$('.rxEduInfo').html('');
			rxInfoCtrl.hasData = false;
			rxInfoCtrl.print.printData = {};
		}
		
		//function to set request json
		function getRequestJson(jsonKeysArray) {
			var requestIdObject
			for(var i=0; i<jsonKeysArray.length; i++) {
				var lowerCaseKey = jsonKeysArray[i].toLowerCase();
				if(lowerCaseKey.includes('rxnorm') && !ipFn.isUNE(rxInfoCtrl.medOrder[jsonKeysArray[i]])) {
					return {'id':rxInfoCtrl.medOrder[jsonKeysArray[i]], 'idType':'RxNorm'};
				} else if(lowerCaseKey.includes("gpi") && !ipFn.isUNE(rxInfoCtrl.medOrder[jsonKeysArray[i]])) {
					return {'id':rxInfoCtrl.medOrder[jsonKeysArray[i]], 'idType':'GPI'};
				}/* else if(lowerCaseKey.includes("ddid") && !ipFn.isUNE(rxInfoCtrl.medOrder[jsonKeysArray[i]])) {
					return {'id':rxInfoCtrl.medOrder[jsonKeysArray[i]], 'idType':'DDID'};
				}*/ else if(lowerCaseKey.includes("ndc") && !ipFn.isUNE(rxInfoCtrl.medOrder[jsonKeysArray[i]])) {
					return {'id':rxInfoCtrl.medOrder[jsonKeysArray[i]], 'idType':'NDC'};
				}
			}
		}
		
		function getDrugMonographData(medOrder) {	
			var requestDataJsonObject = getRequestJson(Object.keys(medOrder));
			
			if(ipFn.isUNE(requestDataJsonObject.id) || "0" === requestDataJsonObject.id) {
				$('.monographInfo').html('');
				showIPMessage('Invalid RxNorm', '', 'ErrorMsg', '', '');
			} else {
				var requestData = {drugName : rxInfoCtrl.drugName, patientId: rxInfoCtrl.patientId};
				requestData.id = requestDataJsonObject.id;
				requestData.idType = requestDataJsonObject.idType;
				
				$http({
					method: 'POST', 
					url: '/mobiledoc/inpatientWeb/drugMonograph/getDrugMonographData', 
					data : JSON.stringify(requestData),
					headers: {
		                'Content-Type': 'application/json',
		                'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
		            }
				}).success(function (response) {
					if(response && response !== 'Failure') {
						$('.monographInfo').html(response);
						rxInfoCtrl.hasData = true;
						rxInfoCtrl.print.printData = response;
					} else {
						resetMonographData();
						showIPMessage('Error occurred while processing the request', '', 'ErrorMsg', '', '');
					}
				}).error(function(response){
					resetMonographData();
					showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
				});	
			}
		}
		
		function resetMonographData() {
			$('.monographInfo').html('');
			rxInfoCtrl.hasData = false;
			rxInfoCtrl.print.printData = {};
		}
	});
}) ();


angular.module('rxInformationApp').directive("ngRepeatEnd", function($rootScope) {
	 return {
		 restrict: "A",
	     link: function (scope, element, attrs) {
	        if(scope.$last) {
	        	$rootScope.$broadcast(attrs.ngRepeatEnd);
	        }
	     }
	 };
});