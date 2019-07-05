(function() {
	angular.module('RxEducationApp',['ecw.dir.perfectscrollbar','ecw.ip.ipFn', 'ecw.ip.print']).component('rxEducationComponent', {
		bindings: {
			masterEncounterId: '@?',
			drugId: '<',
			drugIdType: '<',
			drugDesc: '<',
			patientId: '@',	    	
			title: '@'
		},
		controller: rxEducationController,
		controllerAs: 'rxEducationCtrl',
	    templateUrl: '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/rxEducation/view/RxEducationTemplate.html',
	    transclude: true
	});
	
	function rxEducationController($scope, $http, ipFn, PRINTTYPE) {
		var vm = this;
		vm.modalopen = false;
		vm.print = {format:12,destination:PRINTTYPE.DATA};
		
		/*function to load rx education data*/
		vm.loadRxEducationDetails = function() {
			vm.modalopen = true;
			vm.hasData = false;
		
			if(ipFn.isUNE(vm.drugId) || "0" === vm.drugId || ipFn.isUNE(vm.drugIdType)) {
				$('#rxEduData').html('');
				vm.hasData = false;
				showIPMessage('Invalid Request Id for Rx Education', '', 'ErrorMsg', '', '');
			} 
			else {
				var requestData = {data: [{'id':vm.drugId, 'idType': vm.drugIdType}], "requestToken" : "requestToken", "authToken" : "authToken"};
				
				$http({
					method: 'POST', 
					url: '/mobiledoc/inpatientWeb/rxEducation/getRxEducationData', 
					data : JSON.stringify(requestData)
				}).success(function (response) {
					if(response && response.data && response.data.data && response.data.data.length > 0) {
						$('#rxEduData').html(response.data.data[0].document);						
						vm.print.rxEducationData = clearTitleTagFromBody(response.data.data[0].document);
						vm.hasData = true;
					} else {
						clearData();
						showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
					}
				}).error(function(response){
					clearData();
					showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
				});	
			}
		}
		
		function clearTitleTagFromBody(responseStr){
			responseStr = responseStr.replace(/title=\"[^\n]+\"/gm,"");
			return responseStr;
		}
			
		//function to reset data on failure
		function clearData() {
			$('#rxEduData').html('');
			vm.hasData = false;
			vm.print.rxEducationData = {};
		}
		
		vm.setDrugDesc = function() {
			$('#rxName').html(vm.drugDesc);
		}
		
		//funcion to close rx education modal
		vm.close = function() {
			$scope.$dismiss;
			vm.print.rxEducationData = {};
			vm.modalopen = false;
		}
	}
})();

	
	