angular.module("commonDrugAdmin", ["ui.bootstrap","globalDatePicker","common.services"])

.controller('drugadminModalCtrl',['$modal', '$scope', '$modalInstance', '$http', '$timeout','getJSONService',
    function ($modal, $scope, $modalInstance, $http, $timeout, getJSONService) {
	
    var timer = $timeout(function(){
		$('.get-txt-trim').tooltip({
	  		position: {
	  			my: "right top",
	  			at: "right bottom"
	  		}
	  	})
	  	$('.details-wrapper').perfectScrollbar();
	}, 500)
	var timer2
 	$scope.$on(
        "$destroy",
        function( event ) {
            $timeout.cancel( timer );
            $timeout.cancel( timer2 );
        }
    );
    function ranageFunction(element, range){
    	var doseval = $(element).val();
  		if (doseval > range) {
  			$(element).closest('.bp-range').removeClass('bp-grey-brdr').addClass('red-brdr');
  		} else {
  			$(element).closest('.bp-range').removeClass('red-brdr').addClass('bp-grey-brdr');
  		}
    }
 	$scope.onKeyUpRange11 = function(e){
  		ranageFunction(e.target, 140)
 	}
 	$scope.onKeyUpRange12 = function(e){
  		ranageFunction(e.target, 90)
 	}
 	$scope.vitalsClick = function(e){
 		$(e.currentTarget).toggleClass('active');
  		var rpane = $('.vitals-right-panel');
  		if (rpane.hasClass('visible')) {
  			rpane.animate({
  				"right": "-742px"
  			}, "2000").removeClass('visible');
  			$('.exla-icon1').closest('tr').removeClass('active');
  			$('.exla-icon1').addClass('red-exla-icon');
  			$('.exla-icon1').removeClass('red-exla-bg');
  		} else {
  			rpane.animate({
  				"right": "0px"
  			}, "2000").addClass('visible');
  			$('.exla-icon1').closest('tr').addClass('active');
  			$('.exla-icon1').removeClass('red-exla-icon');
  			$('.exla-icon1').addClass('red-exla-bg');
  		}
 	}
 	$scope.closeVitals = function(e){
 		$('.exla-icon1').addClass('red-exla-icon');
  		$('.exla-icon1').removeClass('red-exla-bg');
  		$(this).toggleClass('active');
  		var rpane = $('.vitals-right-panel');
  		if (rpane.hasClass('visible')) {
  			rpane.animate({
  				"right": "-742px"
  			}, "2000").removeClass('visible');
  			$('.drugs-table').find('tr').removeClass('active');
  		}
 	}
 	$scope.openHistoryPopup = function(e){
 		var _that = e.target;
  		$('.history-popup').show().animate({}, 100, function () {
  			$(this).position({
  				of: _that,
  				my: 'top+18',
  				at: 'left+5',
  				collision: "flipfit",
  			}).animate({
  				"opacity": 1
  			}, 100)
  		});
 	}
 	$scope.closeHistory = function(){
 		$('.history-popup').hide();
 	}
	$scope.selectedItem = '';
	$scope.openRightPanel = function(item){
		$scope.selectedItem = item;
		item.selected = true;
		$('.details-right-panel').show();
  		rpane = $('.details-right-panel');
  		rpane.animate({
  			"right": "0px"
  		}, "2000").addClass('visible');
	}
	$scope.okRightPanel = function(){
		$scope.selectedItem.selected = false;
		rpane = $('.details-right-panel');
		rpane.animate({
  			"right": "-380px"
  		}, "2000").removeClass('visible');
	}
	$scope.cancelRightPanel = function(){
		$scope.selectedItem.selected = false;
		rpane = $('.details-right-panel');
		rpane.animate({
  			"right": "-380px"
  		}, "2000").removeClass('visible');
	}
	$scope.activeDetails = true;
	$scope.activeReaction = false;
	$scope.detailsBtn = function(e){
		$scope.activeDetails = true;
		$scope.activeReaction = false;
		timer2 = $timeout(function(){
			$('.details-wrapper').perfectScrollbar();
		}, 10)
	}
	$scope.reactionBtn = function(e){
		$scope.activeDetails = false;
		$scope.activeReaction = true;
	}
	$scope.noteVerifyOpen = function(e){
		var _that = e.target;
	  	$('.noteverified-info').show().animate({}, 100, function () {
	  		$(this).position({
	  			of: _that,
	  			my: 'left-30 top+21',
	  			at: 'right top',
	  			collision: "flipfit"
	  		}).animate({
	  			"opacity": 1
	  		}, 100);
	  	});
	}
	$scope.noteVerifyHide = function(e){
		$('.noteverified-info').hide();
	}
	$scope.selectedType = "Select Types"; 
	$scope.headerDropdown = 
	[
		{
			"name": "All"
		},
		{
			"name": "Medication"
		},
		{
			"name": "Immunization"
		},
		{
			"name": "RT"
		},
		{
			"name": "PRNS"
		}
	]
  	$scope.openDropdown = function(ev){
  		var _that = $(ev.currentTarget);
  		angular.element('.medicines-dropdown').show().animate({}, 100, function () {
  			$(this).position({
  				of: _that,
  				my: 'left-70 top+28',
  				at: 'left-70 top',
  				collision: "flipfit",
  			}).animate({
  				"opacity": 1
  			}, 100)
  		});
  	}
	$scope.showText = "Not Given";
    $scope.showText1 = "Given";
  	$scope.optionClick = function(selectedOpt, e){
		var a = e.target;
		$scope.show = selectedOpt;
		$scope.showText = angular.element(a).text();
		$('.medicines-dropdown').hide();
	}	
	
	$scope.btnClick = function(selectedOpt, e){
		var a = e.target;
		$scope.show = selectedOpt;
		$('.medicines-dropdown').hide();
	}
    $scope.loginClick = function (temp) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'module/pharmacy/pharmacy-tabs/schedule/view/modal/pharmacy-loginModal.html',
            controller: 'loginModalCtrl',
            size: 'sm',
           	resolve: {
                resolveTab: function () {
                    return temp;
                }
            }
        });
    };	
	$scope.drugInterClick = function (temp) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'module/pharmacy/pharmacy-tabs/schedule/view/modal/pharmacy-drug-interactionl.html',
            controller: 'drugInteractionModalCtrl',
            windowClass: 'large-modal',
            size: 'dashboard-size-modal',
            cls: 'global',
            resolve: {
                resolveTab: function () {
                    return temp;
                }
            }
        });
    };	


	getJSONService.getJsonData('module/pharmacy/pharmacy-tabs/schedule/server/modal-drug-admin-landing.json').then(function (response){ 
        $scope.drugAdminLandingData = response;
    })

	getJSONService.getJsonData('module/pharmacy/pharmacy-tabs/schedule/server/modal-drug-admin-details-tab.json').then(function (response){ 
        $scope.detailsTab = response;
    })

	getJSONService.getJsonData('module/pharmacy/pharmacy-tabs/schedule/server/modal-drug-admin-reaction-tab.json').then(function (response){ 
        $scope.reactionTab = response;
    })
	
	getJSONService.getJsonData('module/pharmacy/pharmacy-tabs/schedule/server/modal-vitalTable.json').then(function (response){ 
        $scope.vitaltable = response;
    })
	
  	$('.medicine-tooltip').hide();
  	$('.system-ready-tooltip').hide();
	$scope.hideFlag=false;
	$scope.timeFuntionality = function(event){
		var	barcode = $(event.target);
  		var currentTime = $('#phar-overduetime').val();
  		$('.system-ready-tooltip').show(0).delay(1000).hide(0);
  		$('body').delay(2500).queue(function () {
  			$('#drug-admin-table2 .icon-scan-red').addClass('scanned').clearQueue();
  			$('.open-noteverified-info.icon-noteverified').addClass('scanned').clearQueue();
  		});
		if (currentTime == '0500') {
  			$('.icon-clock-phamarcy').addClass("hide");
			$scope.hideFlag=true;
				//barcode.closest('.det-view').find('.phr-reason').show();
			$('.phr-reason').find('.txt-medicn-reason').prop("disabled", true);
			$('.phr-reason').find('.input-group-btn button').prop("disabled", true);
  		} else if (currentTime > '0500') {
  			$('.icon-clock-phamarcy').addClass("hide");
			$('.medication-overdue-tooltip').show();
			$('.medication-beforedue-tooltip').hide();
			$('.phry-med-hide').hide();	
  		} else if (currentTime < '0500') {
			$('.phry-med-hide').hide();
			$('.medication-beforedue-tooltip').show();
			$('.medication-overdue-tooltip').hide();
  		}
	};

	$scope.yesMedication = function(){
		$('.phry-med-hide').show();
		$('.medication-overdue-tooltip').hide();
	}
	$scope.noMedication = function(){
		$('.phry-med-hide').show();
		$('.medication-overdue-tooltip').hide();
	}
	$scope.okOverdue = function(){
		$('.phry-med-hide').show();
		$(".medication-beforedue-tooltip").hide();
	}
	$scope.cancelOverdue = function(){
		$('.phry-med-hide').show();
		$(".medication-beforedue-tooltip").hide();
	}
	$scope.yesOverdue = function(){
		$('.phry-med-hide').show();
		$(".medication-beforedue-tooltip").hide();
	}
	$scope.noOverdue = function(){
		$('.phry-med-hide').show();
		$(".medication-beforedue-tooltip").hide();
	}
	$scope.mandetoryModel = true;
	$scope.showMandatory = function(mandetoryStatus){
		if(mandetoryStatus == false){
			angular.forEach($scope.vitaltable, function(item){
				if(!item.mandatory){
					item.showRow = true;
				}
			})
		}else{
			angular.forEach($scope.vitaltable, function(item){
				if(!item.mandatory){
					item.showRow = false;
				}
			})
		}
	};
}])

.controller('loginModalCtrl',['$modal', '$scope', '$modalInstance', '$http', '$timeout',
    function ($modal, $scope, $modalInstance, $http, $timeout) {

	$scope.enterPass = function(){
		$(".password-error").css("display", "block");
	}
	$scope.ok = function(){
		$(document).find('.icon-noteverified.warfarin').addClass('greenicon');
		$modalInstance.dismiss('ok');
	}
	$scope.cancel = function(){
		$modalInstance.dismiss('cancel');
	}
}])

.controller('drugInteractionModalCtrl',['$modal', '$scope', '$modalInstance', '$http', '$timeout',
    function ($modal, $scope, $modalInstance, $http, $timeout) {

	$http({
		method: 'GET',
		url: 'module/pharmacy/pharmacy-tabs/schedule/server/modal-drug-interaction-Table.json'
	}).then(function successCallback(response) {
		$scope.drugInter = response.data;
	});	
	$scope.ok = function(){
		$modalInstance.dismiss('ok');
	}
	$scope.cancel = function(){
		$modalInstance.dismiss('cancel');
	}

}])

