angular.module('globalDatePicker', [])

/* Below are the values which will be used in customerSearchLookup*/

.value('LOOKUP_VALUES', {
	"departmentLookup": [
                            "Mechanical",
                            "Emergency",
                            "Imaging",
                            "Med Surg",
                            "Registration",
                            "Circulation / Support",
                            "Laboratory",
                            "Chapel",
                            "Cardio Pulm",
                            "Admin",
                            "Dietary",
                            "Education",
                            "Bus Office",
                            "HR",
                            "Mat Management",
                            "Labor & amp;Delivery",
                            "Housekeeping",
                            "Central Sterile",
                            "Employee Health",
                            "Outpatient Surgery",
                            "Surgery",
                            "Plant OPS",
                            "Central Plant",
                            "Physical Therapy",
                            "Women Services",
                            "Front Desk",
                            "HIM"
                        ],
	"unitLookup": [
                            "Triage",
                            "Main ER",
                            "Promt Care",
                            "Waiting Room",
                            "Ortho Unit",
                            "Neuro Unit",
                            "ICU A",
                            "Telementry Unit",
                            "CT",
                            "MRI",
                            "Ultrasound",
                            "Post Partum",
                            "Med"
                        ],
	"areaLookup": [
                            "M-T1",
                            "M-T2",
                            "M-T3",
                            "A-R1",
                            "A-R2",
                            "A-R3",
                            "B-T1",
                            "B-T2",
                            "D-T3",
                            "D-R1",
                            "F-R2",
                            "F-R3",
                            "C-R3",
                            "C-R3"
                        ],
	"bedLookup": [
		{
			"bedNumber": 101,
			"bedArea": "M-T1",
			"bedType": "Waiting Room"
                        }, {
			"bedNumber": 102,
			"bedArea": "M-T2",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 103,
			"bedArea": "M-T3",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 104,
			"bedArea": "A-R1",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 105,
			"bedArea": "A-R2",
			"bedType": "Nursing Station"
                        }, {
			"bedNumber": 106,
			"bedArea": "A-R3",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 107,
			"bedArea": "B-T1",
			"bedType": "Nursing Station"
                        }, {
			"bedNumber": 108,
			"bedArea": "B-T2",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 109,
			"bedArea": "D-T3",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 110,
			"bedArea": "D-R1",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 111,
			"bedArea": "F-R2",
			"bedType": "Nursing Station"
                        }, {
			"bedNumber": 112,
			"bedArea": "F-R3",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 113,
			"bedArea": "C-R3",
			"bedType": "Patient Room"
                        }, {
			"bedNumber": 114,
			"bedArea": "C-R3",
			"bedType": "Patient Room"
                        }
                    ]
})

.value('FILTER_OPTION_VALUES', {
	"typeFilterOptions": ["All", "Business", "Clinical", "Operational"],
	"disciplineFilterOptions": ["All", "Emergency", "Imaging", "Inpatient", "Surgery", "PT"],
	"departmentFilterOptions": ["All", "Mechanical Emergency", "Imaging", "Med Surg", "Registration", "Circulation/Support", "Laboratory", "Chapel"],
	"areaTypeFilterOptions": ["All", "Patient Room", "Waiting Room", "Hallway Nursing Station", "Vending Machine", "Medication Dispencing Machine"],
	"bedStatusFilterOptions": ["All"],
	"bedSizeFilterOptions": ["All", "Adult", "Crib", "Paediatric", "Speciality"],
	"bedAttributeFilterOptions": ["DI", "BRL", "TD", "LPD", "RTB"],
	"facilityFilterOptions": ["All", "Northborough Hospital", "Marlborough Hospital", "Westborough Hospital", "Shrewsbury Hospital"]
})

/* Directive for providing search field and all input field lookups*/
.directive('customSearchLookup', function ($document, LOOKUP_VALUES, FILTER_OPTION_VALUES) {
		return {
			restrict: "E",
			require: ['?ngModel'],
			scope: {
				dropLookupType: '@lookupType',
				showFilterType: '@showFilter',
				userCustomOptions: '=customOptions'
			},
			link: function (scope, elem) {
				elem.css({
					'display': '-webkit-box'
				});
			},
			templateUrl: './module/global-lookup-module/view/global-lookup-template.html',
			controller: function ($scope, $timeout) {

				$scope.showFilterSection = false; //This boolean handles the show and hide for right filter section of the lookup dropdown

				/* Below if checks for show-filter attribute of a directive. If defined then and then only shows right filter section else hides it.*/
				if ($scope.showFilterType)
					$scope.showFilterSection = true;


				/*Below defined objects(arrays) are used for displaying options in the right panel filter fields*/
				$scope.typeFilterOptions = FILTER_OPTION_VALUES.typeFilterOptions;
				$scope.disciplineFilterOptions = FILTER_OPTION_VALUES.disciplineFilterOptions;
				$scope.departmentFilterOptions = FILTER_OPTION_VALUES.departmentFilterOptions;
				$scope.areaTypeFilterOptions = FILTER_OPTION_VALUES.areaTypeFilterOptions;
				$scope.bedStatusFilterOptions = FILTER_OPTION_VALUES.bedStatusFilterOptions;
				$scope.bedSizeFilterOptions = FILTER_OPTION_VALUES.bedSizeFilterOptions;
				$scope.bedAttributeFilterOptions = FILTER_OPTION_VALUES.bedAttributeFilterOptions;
				$scope.facilityFilterOptions = FILTER_OPTION_VALUES.facilityFilterOptions;

				/*Below flags are used for displaying right filter panel fields based on type of dropdown*/
				$scope.showDepartmentFilter = false;
				$scope.showUnitFilter = false;
				$scope.showAreaFilter = false;
				$scope.showBedFilter = false;

				switch ($scope.dropLookupType) {
					case 'department':
						if ($scope.userCustomOptions) {
							($scope.userCustomOptions.departmentLookup) ? $scope.lookupMenu = $scope.userCustomOptions.departmentLookup: $scope.lookupMenu = LOOKUP_VALUES.departmentLookup;
						} else {
							$scope.lookupMenu = LOOKUP_VALUES.departmentLookup;
						}
						($scope.showFilterType === 'departmentFilter') ? $scope.showDepartmentFilter = true: $scope.showDepartmentFilter = false;
						break;
					case 'unit':
						if ($scope.userCustomOptions) {
							($scope.userCustomOptions.unitLookup) ? $scope.lookupMenu = $scope.userCustomOptions.unitLookup: $scope.lookupMenu = LOOKUP_VALUES.unitLookup;
						} else {
							$scope.lookupMenu = LOOKUP_VALUES.unitLookup;
						}
						($scope.showFilterType === 'unitFilter') ? $scope.showUnitFilter = true: $scope.showUnitFilter = false;
						break;
					case 'area':
						if ($scope.userCustomOptions) {
							($scope.userCustomOptions.areaLookup) ? $scope.lookupMenu = $scope.userCustomOptions.areaLookup: $scope.lookupMenu = LOOKUP_VALUES.areaLookup;
						} else {
							$scope.lookupMenu = LOOKUP_VALUES.areaLookup;
						}
						($scope.showFilterType === 'areaFilter') ? $scope.showAreaFilter = true: $scope.showAreaFilter = false;
						break;
					case 'bed':
						if ($scope.userCustomOptions) {
							($scope.userCustomOptions.bedLookup) ? $scope.bedTableData = $scope.userCustomOptions.bedLookup: $scope.bedTableData = LOOKUP_VALUES.bedLookup;
						} else {
							$scope.bedTableData = LOOKUP_VALUES.bedLookup;
						}
						($scope.showFilterType === 'bedFilter') ? $scope.showBedFilter = true: $scope.showBedFilter = false;
						break;
				};

				//This function gets invoked on keyup of the lookup field.
				$scope.getDropdown = function (e) {
					if ($(e.target).val() !== "") {
						angular.element('.lookupmenu-container').parent('.dropdown-container').removeClass('open');
						angular.element(e.target).closest('.global-lookup').find('.dropdown-container').addClass('open');
					} else {
						angular.element(e.target).closest('.global-lookup').find('.dropdown-container').removeClass('open');
					}
				}

				//This function gets invoked on click of a lookup button.
				$scope.showFilterDropdown = function (e) {
					angular.element('.lookupmenu-container').parent('.dropdown-container').removeClass('open');
					angular.element(e.target).closest('.global-lookup').find('.dropdown-container').addClass('open');
				}

				//This function is used to handle the selection of dropdown value.
				$scope.setDropdownValue = function (textSelected, e) {
					$scope.lookupSearch = textSelected;
					angular.element(e.target).closest('.global-lookup').find('.dropdown-container').removeClass('open');
				}

				//This is to handle document click event
				$(document).on('click keyup', function (e) {
					var lookupContainer = angular.element('.lookupmenu-container');
					var openlookup = angular.element('.lookup-search, .open-filter-search');
					if (!lookupContainer.is(e.target) && !openlookup.is(e.target) && lookupContainer.has(e.target).length === 0) {
						angular.element('.lookupmenu-container').parent('.dropdown-container').removeClass('open');
					}

					var multiselectContainer = angular.element('.multiselect-container');
					var linkMultiLookup = angular.element('.multiselect-native-select .multiselect');
					if (!multiselectContainer.is(e.target) && !linkMultiLookup.is(e.target) && multiselectContainer.has(e.target).length === 0) {
						angular.element('.lookupmenu-container').parent('.btn-group').removeClass('open');
					}
				});

				var directiveTimer = $timeout(function () {
					//                    angular.element('.global-lookup-dropdown').perfectScrollbar();
					//                    angular.element('.global-lookup-table-dropdown >.simpleTable >.tablebody').perfectScrollbar();
					angular.element('#example-getting-started').multiselect();
				}, 300);

				//This clearouts all the timers in the directive.
				$scope.$on('$destroy', function () {
					$timeout.cancel(directiveTimer);
				});

			}
		}
	})
	/*
	Directive for providing date time picker globally. Use this directive
	*/
	.directive('dateAndTimePicker', function () {
		return {
			restrict: "EA",
			require: '?ngModel',
			link: function (scope, elem) {
				$(elem).datetimepicker({
					showTime: false,
					showButtonPanel: true,
					controlType: 'select',
					oneLine: true,
					timeFormat: 'HH:mm'
				});
				$(elem).inputmask('[99/99/9999] [h:s]');

				$(".icon-inputcalender").click(function () {
					$(this).parent().parent().find('.date-and-time').datetimepicker("show");
				})

				$(".icon-calender-sm").click(function () {
					$(this).closest('td').find('input[type="text"].only-date').datetimepicker("show");
					$(this).closest('th').find('input[type="text"].only-date-nw').datetimepicker("show");
				})
			}
		}
	})

/*
Directive for providing time picker globally.
*/
.directive('timePicker', function () {
	return {
		restrict: "EA",
		require: '?ngModel',
		link: function (scope, elem) {
			$(elem).timepicker({
				showTime: false,
				showButtonPanel: true,
				controlType: 'select',
				oneLine: true,
				timeFormat: 'HHmm'
			});
			$(elem).inputmask('[hs]');
			$(".icon-Timestamp").click(function () {
				$(this).parent().parent().find('.only-time').datetimepicker("show");
			})
			$(".icon-Timestamp").click(function () {
				$(this).closest('td').find('input[type="text"].only-time').datetimepicker("show");
			})
		}
	}
})

/*
Directive for providing dob picker globally. Use this for date of birth.
*/
.directive('dateOfBirthPicker', function () {
		return {
			restrict: "EA",
			link: function (scope, elem) {
				$(elem).datepicker({
					showButtonPanel: true,
					controlType: 'select',
					oneLine: true,
					timeFormat: 'HHmm',
					changeYear: true,
					yearRange: "1950:2015"
				});
				$(elem).inputmask('[99/99/9999]');
				$(".icon-inputcalender").on('click', function (e) {
					$(this).parent().parent().find('.birth-date').datepicker("show");
				});
				$('.ui-datepicker').on('click', function (e) {
					e.stopPropagation();
				});
			}
		}
	})
	/*

	/*
	Directive for providing only date picker globally. 
	*/
	.directive('datePicker', function () {
		return {
			restrict: "EA",
			link: function (scope, elem) {
				$(elem).datepicker({
					showButtonPanel: true,
					controlType: 'select',
					oneLine: true,
					timeFormat: 'HHmm',
					changeYear: true
				});
				$(elem).inputmask('[99/99/9999]');
				$(".icon-inputcalender, .icon-calender-sm").on('click', function (e) {
					$(this).parent().parent().find('.only-date').datepicker("show");
				});
				$('.ui-datepicker').on('click', function (e) {
					e.stopPropagation();
				});
			}
		}
	})
	/*

	Directive for providing toggle functionality for right panel on "New Master Enc" button click in registration module.
	*/
	.directive('toggleRightPanelMasterEnc', function () {
		return {
			restrict: "EA",
			link: function (scope, elem) {
				elem.click(function () {
					angular.element(elem).toggleClass('active');
					if (angular.element(elem).hasClass('active')) {
						angular.element(elem).parent().find('.toggle-panel').animate({
							"right": "0px"
						}, 500);
						angular.element(elem).animate({
							"right": "340px"
						}, 500);
					} else {
						angular.element(elem).parent().find('.toggle-panel').animate({
							"right": "-340px"
						}, 500);
						angular.element(elem).animate({
							"right": "0px"
						}, 500);
					}
				})

			}
		}
	})
	/*
	Directive for providing toggle functionality for left panel on "New Master Enc" button click in registration module.
	*/
	.directive('toggleLeftPanelMasterEnc', function () {
		return {
			restrict: "EA",
			link: function (scope, elem) {
				elem.click(function () {
					var leftSec = angular.element(elem).parent().parent().find('.leftdocumentList');
					var rightSec = angular.element(elem).parent().parent().find('.rightdocumentview');
					//var docList = angular.element(elem).parent().parent().find('.showdocumentList');
					$(elem).toggleClass('active');
					if (leftSec.hasClass('visible')) {
						leftSec.animate({
							"left": "-420px"
						}, "2000").removeClass('visible');
						rightSec.animate({
							"margin-left": "0px"
						}, "2000");
						//docList.hide();
					} else {
						leftSec.animate({
							"left": "0px"
						}, "2000").addClass('visible');
						rightSec.animate({
							"margin-left": "420px"
						}, "2000");
						//docList.show();
					}
				})

			}
		}
	})



/*
Directive for providing date time picker globally. Dont "del and use" as it has been used previously. Use the above equivalent directive "dateAndTimePicker".
*/
.directive('dateTimePicker', function () {
	return {
		restrict: "EA",
		require: '?ngModel',
		link: function (scope, elem) {
			$(elem).datetimepicker({
				showTime: false,
				showButtonPanel: true,
				controlType: 'select',
				oneLine: true,
				timeFormat: 'HH:mm'
			});
			$(elem).inputmask('[99/99/9999] [h:s]');

			$(".icon-inputcalender").click(function () {
				$(this).parent().parent().find('.only-date').datetimepicker("show");
			})


			$(".icon-calender-sm").click(function () {
				$(this).closest('td').find('input[type="text"].only-date').datetimepicker("show");
				$(this).closest('th').find('input[type="text"].only-date-nw').datetimepicker("show");

				$(this).closest('.snd-date-picker').find('input[type="text"].only-date-nw').datetimepicker("show");
			})

			$('.ui-datepicker').on('click', function (e) {
				e.stopPropagation();
			});
		}
	}
})

/*
Directive for providing dob picker globally. Dont "del and use" as it has been used previously. Use the above equivalent directive "dateOfBirthPicker".
*/
.directive('dobPicker', function () {
	return {
		restrict: "EA",
		link: function (scope, elem) {
			$(elem).datepicker({
				showButtonPanel: true,
				controlType: 'select',
				oneLine: true,
				timeFormat: 'HHmm',
				changeYear: true,
				yearRange: "1950:2015"
			});
			$(elem).inputmask('[99/99/9999]');
			$(".icon-inputcalender").on('click', function (e) {
				$(this).parent().parent().find('.only-date').datepicker("show");
			});
			$('.ui-datepicker').on('click', function (e) {
				e.stopPropagation();
			});
		}
	}
})



/* Directive for timestamp date picker */
.directive('timestampFun', function () {
	return {
		restrict: "E",
		templateUrl: 'module/common-template/timestamp-template.html',
		link: function (scope, elem) {
			$('.globaltime').hide();

			$('.firststamp').each(function () {
				$(this).on('click', function () {

					if ($(this).hasClass('documented')) {

						$('.globalclk').find('.green-header').parent().addClass('hide');

					} else {

						$('.globalclk').find('.green-header').parent().removeClass('hide');

					}

					var _that = this;
					$('.globaltime').show().animate({}, 100, function () {
						$(this).position({
							of: _that,
							my: 'right+10 top+5',
							at: 'right bottom+5',
							collision: "flipfit",
						}).animate({
							"opacity": 1
						}, 100)
					});
					$('.upnotch').show();
					$('.globaltime').removeClass('vishidden');
					$('.globalclk').addClass('vishidden');
					//$('.globalclk').hide();
				});

			});

			// second time stamp flow: globaltime show
			$('.globalclk').hide();
			$('.secondstamp').each(function () {

				$(this).on('click', function () {
					var _new = this;

					$('.globalclk').show().animate({}, 100, function () {
						$(this).position({
							of: _new,
							my: 'right+18 bottom+172',
							at: 'right top',
							collision: "flipfit",
						}).animate({
							"opacity": 1
						}, 100)
					});

					$('.upnotch').show();
					$('.globaltime').addClass('vishidden');
					$('.globalclk').removeClass('vishidden');
				});
			});

			$(document).on('click', '.cancelprebtn', function () {
				$('.globalclk').addClass('vishidden');
			});

			$(document).on('click', '.globalclk-arrow', function (e) {
				e.stopPropagation();
			});

		}
	}
})

/* Directive for prefect scrollbar */
.directive('perfectScrollBar', function () {
	return {
		restrict: 'C',
		link: function (scope, element, attrs) {
			$(element).perfectScrollbar();
		}
	}
})

/*
Use to close the element when we click anywhere in the page
*/
.directive('onOutsideElementClick', function ($document) {
		return {
			restrict: 'A',
			link: function (scope, element, attrs) {
				element.on('click', function (e) {
					e.stopPropagation();
				});

				var onClick = function () {
					scope.$apply(function () {
						scope.$eval(attrs.onOutsideElementClick);
					});
				};

				$document.on('click', onClick);

				scope.$on('$destroy', function () {
					$document.off('click', onClick);
				});
			}
		};
	})
	/*
	Directive for providing click outside functionality globally.
	It is implemented in annotation popup in l&D dashboard.
	*/
	.directive('closeBodyclick', function ($parse, $document) {
		var dir = {
			compile: function ($element, attr) {
				// Parse the expression to be executed
				// whenever someone clicks _off_ this element.
				var fn = $parse(attr["closeBodyclick"]);
				return function (scope, element, attr) {
					// add a click handler to the element that
					// stops the event propagation.
					element.bind("click", function (event) {
						event.stopPropagation();
					});
					angular.element($document[0].body).bind("click",
						function (event) {
							scope.$apply(function () {
								fn(scope, {
									$event: event
								});
							});
						});
				};
			}
		};
		return dir;
	})


/*
    Directive for providing toggle functionality for left panel on Document Selection modal in registration module.
    */
.directive('toggleLeftPanelDoc', function () {
	return {
		restrict: "EA",
		link: function (scope, elem) {
			elem.click(function () {
				var leftSec = angular.element(elem).parent();
				var rightSec = angular.element(elem).parent().parent().find('.doc-right-panel');
				$(elem).toggleClass('active');
				if (leftSec.hasClass('visible')) {
					leftSec.animate({
						"margin-left": "0"
					}, "2000").removeClass('visible');
					rightSec.animate({
						"width": "873px",
						"margin-left": "365px"
					}, "2000");
				} else {
					leftSec.animate({
						"margin-left": "-366px"
					}, "2000").addClass('visible');
					rightSec.animate({
						"width": "100%",
						"margin-left": "0px"
					}, "2000");
				}
			})

		}
	}
})

.directive('ngScroll', function () {
	return {
		restrict: 'A',
		link: function (scope, element, attrs) {
			element.bind('scroll', function () {
				scope.$apply(function () {
					scope.$eval(attrs.ngScroll);
				});
			});
		}
	};
})
.directive('clearable', function() {
 	return {
 		restrict: 'C',
 		compile: function() {
 			function tog(v) {
 				return v ? 'addClass' : 'removeClass';
 			}
 			$(document).on('input', '.clearable', function() {
 				$(this)[tog(this.value)]('x');
 			}).on('mousemove', '.x', function(e) {
 				$(this)[tog(this.offsetWidth - 18 < e.clientX - this.getBoundingClientRect().left)]('onX').on('click', function() {
 					$(this).removeClass('x onX').val('').change();
 				});
 			});
 		}
 	};
 });