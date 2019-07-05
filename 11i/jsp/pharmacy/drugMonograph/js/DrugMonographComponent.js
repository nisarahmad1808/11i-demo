(function() {
	angular.module('DrugMonographApp',['ecw.dir.perfectscrollbar','ecw.ip.ipFn','ecw.ip.print']).component('drugMonographComponent', {
		bindings: {
			masterEncounterId: '@?',
			patientId: '@',
			drugId: '<',
			drugIdType: '<',
			drugDesc: '<?',	
			drugName: '<',
			title: '@'
		},
		controller: drugMonographController,
		controllerAs: 'drugMonographCtrl',
	    templateUrl: '/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/drugMonograph/view/DrugMonographTemplate.html',
	    transclude: true
	});
	
	function drugMonographController($http, $scope, ipFn, PRINTTYPE, $q) {
		var vm = this;
		vm.modalOpen = false;
		vm.print = {format:12,destination:PRINTTYPE.DATA};
		
		/*function to load monograph data*/
		vm.loadDrugMonographData = function() {
			vm.modalOpen = true;
			vm.hasData = false;
	
			if(ipFn.isUNE(vm.drugId) || "0" === vm.drugId || ipFn.isUNE(vm.drugIdType)) {
				$('#monographInfo').html('');
				vm.hasData = false;
				showIPMessage('Invalid Request Id for Rx Monograph', '', 'ErrorMsg', '', '');
			} else {
				var requestData = {drugName : vm.drugName, patientId: vm.patientId};
				requestData.id = vm.drugId;
				requestData.idType = vm.drugIdType;
				
				$http({
					method: 'POST', 
					url: '/mobiledoc/inpatientWeb/drugMonograph/getDrugMonographData', 
					data : JSON.stringify(requestData),
					headers: {
		                'Content-Type': 'application/json',
		                'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
		            }
				}).success(function (response) {
					if(response && response != 'Failure') {
						$('#monographInfo').html(response);
						vm.print.monographData = disableLinks(response);
						vm.hasData = true;
					} else {
						clearData();
						showIPMessage('Error occurred while processing the request', '', 'ErrorMsg', '', '');
					}
				}).error(function(response) {
					clearData();
					showIPMessage('Error occurred while processing the request', '','ErrorMsg', '', '');
				});	
			}
		}

		vm.setDrugDesc = function() {
			vm.drugDesc ? $('#drugName').html(vm.drugDesc) : $('#drugName').html(vm.drugName) ;
		}
		
		//function to disable links for print preview
		function disableLinks(response) {
			var re = new RegExp('href=\"(.*?)\"', 'igm');
			var match = "";
			
			while ((match = re.exec(response)) !== null) {
				response = response.replace(match[0], "href=\"javascript:void(0)\"");
			}
			return response;
		}
		
		//function to reset data on failure
		function clearData() {
			$('#monographInfo').html('');
			vm.hasData = false;
			vm.print.monographData = {};
		}
		
		/*funcion to close drug monograph modal*/
		vm.close = function() {
			$scope.$dismiss;
			vm.print.monographData = {};
			vm.modalOpen = false;
		}
	}
})();	