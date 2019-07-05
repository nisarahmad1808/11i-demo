var oldInitDate;
$(document).ready(function () {
    if($('.bithdate-selection-input').length != 0){
            $('.bithdate-selection-input').each(function(){
                if(typeof($(this).data('daterangepicker')) === 'undefined'){
                    $(this).daterangepicker({
				singleDatePicker: true,
				"showDropdowns": true,				
				ranges: {
					'Days': '',
					'Today': [moment(), moment()],
					'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')]
				},
				"maxDate":  moment(),
				alwaysShowCalendars: true,
				"locale": {
					"customRangeLabel": "",
					format: 'MM/DD/YYYY',
					applyLabel: 'OK',
					"monthNames": [
						"January",
						"February",
						"March",
						"April",
						"May",
						"June",
						"July",
						"August",
						"September",
						"October",
						"November",
						"December"
					]
				}
			
				});
                }
            });
                    
			$('.birthdate-selection-button').click(function () {
				$(this).parents('.input-group').find('input').data('daterangepicker').show();
                oldInitDate = $(this).parents('.input-group').find('input').val();

			});
			$('.bithdate-selection-input').on('click focus', function () {
				$(this).data('daterangepicker').hide();
			});
		}
    if($('.departuredate-selection-input').length != 0){    		
			$('.departuredate-selection-button').click(function () {
				$(this).parents('.input-group').find('input').data('daterangepicker').show();
               oldInitDate = $(this).parents('.input-group').find('input').val();
			});
			$('.departuredate-selection-input').on('click focus', function () {
				$(this).data('daterangepicker').hide();
			});
            $('.departuredate-selection-input').each(function(){
                if(typeof($(this).data('daterangepicker')) === 'undefined'){
			         $(this).daterangepicker({
					singleDatePicker: true,
					"showDropdowns": true,
					// autoUpdateInput: false,
					"timePicker": true,
					"timePicker24Hour": true,
					"timePickerSeconds": false,
					"minDate":  moment(),
					"ranges": {
						'Days': '',
						'Today (Now)': [moment(), moment()],
						'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],
					},
					showCustomRangeLabel: true,
					"locale": {
						"format": "MM/DD/YYYY HH:mm",
						"separator": " - ",
						"applyLabel": "OK",
						"cancelLabel": "Cancel",
						"fromLabel": "From",
						"toLabel": "To",
						"customRangeLabel": "",
						"weekLabel": "W",
						"daysOfWeek": [
							"Su",
							"Mo",
							"Tu",
							"We",
							"Th",
							"Fr",
							"Sa"
						],
						"monthNames": [
							"January",
							"February",
							"March",
							"April",
							"May",
							"June",
							"July",
							"August",
							"September",
							"October",
							"November",
							"December"
						],
						"firstDay": 1
					},
					"alwaysShowCalendars": true,
					});
                }
            });
		}
    /* First Section to intialise */
    pickerApply();
    if($('.singledate-input').length != 0){
        $('.singledate-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
                $(this).daterangepicker({
            singleDatePicker: true,
            "showDropdowns": true,
            ranges: {
                'Days': '',
                'Today': [moment(), moment()],
                'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],
            },
            //showCustomRangeLabel: true,
            alwaysShowCalendars: true,
            "locale": {
                "customRangeLabel": "",
                format: 'MM/DD/YYYY',
                applyLabel: 'OK',
                "monthNames": [
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December"
                ]

            }
            });
            }
        });

        $('.singledate-button').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.singledate-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });
}

    if($('.singletime-entry-input').length != 0){
        $('.singletime-entry-button').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.singletime-entry-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });
        $('.singletime-entry-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
                 $(this).daterangepicker({
                singleDatePicker: true,
                "showDropdowns": true,
                // autoUpdateInput: false,
                "timePicker": true,
                "timePicker24Hour": true,
                "timePickerSeconds": false,
                // "dateLimit": {
                //     "days": 7
                // },
                "ranges": {
                    'Days': '',
                    'Today (Now)': [moment(), moment()],
                    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                    'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],
                },
                showCustomRangeLabel: true,
                "locale": {
                    "format": "MM/DD/YYYY HHmm",
                    "separator": " - ",
                    "applyLabel": "OK",
                    "cancelLabel": "Cancel",
                    "fromLabel": "From",
                    "toLabel": "To",
                    "customRangeLabel": "",
                    "weekLabel": "W",
                    "daysOfWeek": [
                        "Su",
                        "Mo",
                        "Tu",
                        "We",
                        "Th",
                        "Fr",
                        "Sa"
                    ],
                    "monthNames": [
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                    ],
                    "firstDay": 1
                },
                "alwaysShowCalendars": true,
                "startDate": "01/26/2017 0000",
               // "endDate": "01/26/2017 2359"
                });
            }
        });}

        if($('.day-with-datetime-input').length != 0){
            $('.day-with-datetime-button').click(function () {
                $(this).parents('.input-group').find('input').data('daterangepicker').show();
                oldInitDate = $(this).parents('.input-group').find('input').val();
            });
            $('.day-with-datetime-input').on('click focus', function () {
                $(this).data('daterangepicker').hide();
            });
            $('.day-with-datetime-input').each(function(){
                if(typeof($(this).data('daterangepicker')) === 'undefined'){
                     $(this).daterangepicker({
                    singleDatePicker: true,
                    "showDropdowns": true,
                    // autoUpdateInput: false,
                    "timePicker": true,
                    "timePicker24Hour": true,
                    "timePickerSeconds": false,
                    // "dateLimit": {
                    //     "days": 7
                    // },
                    "ranges": {
                        'Days': '',
                        'Today (Now)': [moment(), moment()],
                        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                        'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],
                    },
                    showCustomRangeLabel: true,
                    "locale": {
                        "format": "ddd, MM/DD/YYYY HH:mm",
                        "separator": " - ",
                        "applyLabel": "OK",
                        "cancelLabel": "Cancel",
                        "fromLabel": "From",
                        "toLabel": "To",
                        "customRangeLabel": "",
                        "weekLabel": "W",
                        "daysOfWeek": [
                            "Su",
                            "Mo",
                            "Tu",
                            "We",
                            "Th",
                            "Fr",
                            "Sa"
                        ],
                        "monthNames": [
                            "January",
                            "February",
                            "March",
                            "April",
                            "May",
                            "June",
                            "July",
                            "August",
                            "September",
                            "October",
                            "November",
                            "December"
                        ],
                        "firstDay": 1
                    },
                    "alwaysShowCalendars": true,
                    "startDate": "Thu, 01/26/2017 0000",
                   // "endDate": "01/26/2017 2359"
                    });
                }
            });}

    if($('.date-range-selection-input').length != 0){
        $('.date-range-selection-button').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();            
        });
        $('.date-range-selection-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });

        $('.date-range-selection-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
                $(this).daterangepicker({
                "showDropdowns": true,
                "timePicker": true,
                timePicker24Hour: true,
                "ranges": {
                    'Days': '',
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                    'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],

                    'Weeks': '',
                    'This Week': [moment().startOf('week'), moment().endOf('week')],
                    'Last Week': [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')],
                    'Next Week': [moment().add(1, 'week').startOf('week'), moment().add(1, 'week').endOf('week')],
                    'Months': '',
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                    'Next Month': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')]
                },
                showCustomRangeLabel: true,
                "locale": {
                    "format": "MM/DD/YYYY HH:mm",
                    "separator": " - ",
                    "applyLabel": "OK",
                    "cancelLabel": "Cancel",
                    "fromLabel": "From",
                    "toLabel": "To",
                    "customRangeLabel": "",
                    "weekLabel": "W",
                    "daysOfWeek": [
                        "Su",
                        "Mo",
                        "Tu",
                        "We",
                        "Th",
                        "Fr",
                        "Sa"
                    ],
                    "monthNames": [
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                    ],
                    "firstDay": 1,
                },
                "alwaysShowCalendars": true,
            });
            }
        })}

    if($('.double-timeentry-input').length != 0){
        $('.double-timeentry-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
             $(this).daterangepicker({
                timeRangeSingleDay: true,
                singleDatePicker: true,
                "showDropdowns": true,
                 //autoUpdateInput: false,
                "timePicker": true,
                "timePicker24Hour": true,
                "timePickerSeconds": false,
                // "dateLimit": {
                //     "days": 7
                // },
                "ranges": {
                    'Days': '',
                    'Today (Now)': [moment(), moment()],
                    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                    'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')]
                },
                showCustomRangeLabel: true,
                "locale": {
                    "format": "MM/DD/YYYY HH:mm HH:mm",
                    "separator": " - ",
                    "applyLabel": "OK",
                    "cancelLabel": "Cancel",
                    "fromLabel": "From",
                    "toLabel": "To",
                    "customRangeLabel": "",
                    "weekLabel": "W",
                    "daysOfWeek": [
                        "Su",
                        "Mo",
                        "Tu",
                        "We",
                        "Th",
                        "Fr",
                        "Sa"
                    ],
                    "monthNames": [
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                    ],
                    "firstDay": 1
                },
                "alwaysShowCalendars": true,
               // "startDate": "01/26/2017 0000",
               // "endDate": "01/26/2017 2359"
                });  
            }
        });


        var hourMinFrst, hourMinScnd;
        $('.doubletime-button').click(function () {
            setflag = true;
            var timeRangeEntry = $(this).parents('.input-group').find('input').val();
            if(timeRangeEntry != ' '){
                if(hourMinFrst || hourMinScnd){
                    hourMinFrst = timeRangeEntry.split(' ')[1].split(':');
                    hourMinScnd = timeRangeEntry.split(' ')[2].split(':');
                }
            }
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.double-timeentry-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });
}

    if($('.oneday-rangeinput').length != 0){
        $('.oneday-rangeinput').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
                $(this).daterangepicker({
                "showDropdowns": true,
                "timePicker": true,
                timePicker24Hour: true,
                "ranges": {
                    'Days': '',
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                    'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],

                      'Hours':'',
                      'Last 3 Hrs': [moment().subtract(3, 'hours'), moment()],
                     'Last 6 Hrs': [moment().subtract(6, 'hours'), moment()],
                     'Last 12 Hrs': [moment().subtract(12, 'hours'), moment()],
                     'Next 3 Hrs': [moment().add(3, 'hours'), moment()],
                     'Next 6 Hrs': [moment().add(6, 'hours'), moment()],
                     'Next 12 Hrs': [moment().add(12, 'hours'), moment()],

                },
                "dateLimit": {
                     "days": 1
                 },
                showCustomRangeLabel: true,
                "locale": {
                    "format": "MM/DD/YYYY HH:mm",
                    "separator": " - ",
                    "applyLabel": "OK",
                    "cancelLabel": "Cancel",
                    "fromLabel": "From",
                    "toLabel": "To",
                    "customRangeLabel": "",
                    "weekLabel": "W",
                    "daysOfWeek": [
                        "Su",
                        "Mo",
                        "Tu",
                        "We",
                        "Th",
                        "Fr",
                        "Sa"
                    ],
                    "monthNames": [
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                    ],
                    "firstDay": 1,
                },
                "alwaysShowCalendars": true,
            });
            }
        });

        $('.oneday-rangebutton').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.oneday-rangeinput').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });		
}

    if($('.always-show-input').length != 0){
        $('.always-show-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){
                $(this).daterangepicker({
            singleDatePicker: true,
            "showDropdowns": true,
            parentEl: $('.always-show-input').parent(), // Always Pass Parent Element  
            alwaysShow: true,
            ranges: {
                'Days': '',
                'Today': [moment(), moment()],
                'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],
            },
            //showCustomRangeLabel: true,
            alwaysShowCalendars: true,
            "locale": {
                "customRangeLabel": "",
                format: 'MM/DD/YYYY',
                applyLabel: 'OK',
                "monthNames": [
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December"
                ]

            },
            //"startDate": "01/26/2017",
            });
            }
        });

        $('.always-show-button').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.always-show-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });
        $('.always-show-button').trigger('click');		
}

    if($('.dates-selection-input').length != 0){
        $('.dates-selection-input').each(function(){
            if(typeof($(this).data('daterangepicker')) === 'undefined'){

             $(this).daterangepicker({
                "showDropdowns": true,
                "ranges": {
                    'Days': '',
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                    'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],

                    'Weeks': '',
                    'This Week': [moment().startOf('week'), moment().endOf('week')],
                    'Last Week': [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')],
                    'Next Week': [moment().add(1, 'week').startOf('week'), moment().add(1, 'week').endOf('week')],
                    'Months': '',
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                    'Next Month': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')]
                },
                showCustomRangeLabel: true,
                "locale": {
                    "format": "MM/DD/YYYY HH:mm",
                    "separator": " - ",
                    "applyLabel": "OK",
                    "cancelLabel": "Cancel",
                    "fromLabel": "From",
                    "toLabel": "To",
                    "customRangeLabel": "",
                    "weekLabel": "W",
                    "daysOfWeek": [
                        "Su",
                        "Mo",
                        "Tu",
                        "We",
                        "Th",
                        "Fr",
                        "Sa"
                    ],
                    "monthNames": [
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                    ],
                    "firstDay": 1,
                },
                "alwaysShowCalendars": true,
            });

        }
        });
        $('.dates-selection-button').click(function () {
            $(this).parents('.input-group').find('input').data('daterangepicker').show();
            oldInitDate = $(this).parents('.input-group').find('input').val();
        });
        $('.dates-selection-input').on('click focus', function () {
            $(this).data('daterangepicker').hide();
        });		
}

if($('.datetime-range-selection-input').length != 0){
    $('.datetime-range-selection-button').click(function () {
        $(this).parents('.input-group').find('input').data('daterangepicker').show();
        oldInitDate = $(this).parents('.input-group').find('input').val();            
    });
    $('.datetime-range-selection-input').on('click focus', function () {
        $(this).data('daterangepicker').hide();
    });

    $('.datetime-range-selection-input').each(function(){
        if(typeof($(this).data('daterangepicker')) === 'undefined'){
            $(this).daterangepicker({
            "showDropdowns": true,
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerSeconds": true,
            "linkedCalendars": false,
            "ranges": {
                'Days': '',
                'Today': [moment().startOf('day'), moment().endOf('day')],
                'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                'Tomorrow': [moment().add(1, 'days'), moment().add(1, 'days')],

                'Weeks': '',
                'This Week': [moment().startOf('week'), moment().endOf('week')],
                'Last Week': [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')],
                'Next Week': [moment().add(1, 'week').startOf('week'), moment().add(1, 'week').endOf('week')],
                'Months': '',
                'This Month': [moment().startOf('month'), moment().endOf('month')],
                'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                'Next Month': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')]
            },
            showCustomRangeLabel: true,
            "locale": {
                "format": "MM/DD/YYYY HH:mm:ss",
                "separator": " - ",
                "applyLabel": "OK",
                "cancelLabel": "Cancel",
                "fromLabel": "From",
                "toLabel": "To",
                "customRangeLabel": "",
                "weekLabel": "W",
                "daysOfWeek": [
                    "Su",
                    "Mo",
                    "Tu",
                    "We",
                    "Th",
                    "Fr",
                    "Sa"
                ],
                "monthNames": [
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December"
                ],
                "firstDay": 1,
            },
            "alwaysShowCalendars": true,
        });
        }
    })}

    inputMaskInit();
});
$(document).on('click', '.selection-click-date', function (e) {
    $(document).find('.options-wrapper').removeClass('open');

    if ($(this).parents('.select-box-date').find('.options-wrapper').hasClass('open')) {
        $(this).parents('.select-box-date').find('.options-wrapper').removeClass('open');
    } else {
        $(this).parents('.select-box-date').find('.options-wrapper').addClass('open');
        setTimeout(function () {
            $('.scroll-point').perfectScrollbar();
        }, 100);
    }
});
$(document).on('click', function (e) {
    var container = $('.select-box-date');
    if (!container.is(e.target) && container.has(e.target).length === 0) {
        container.find('.options-wrapper').removeClass('open');
    }
});
$(document).on('click', function (e) {
    var doubleE = $('.double-timeentry-input');
    if (!doubleE.is(e.target) && doubleE.has(e.target).length === 0) {
     setflag = false;   
    }
});
function inputMaskInit(){

        $('.singledate-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.singletime-entry-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 hs]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.day-with-datetime-input').each(function(){
           // if(!$(this).hasClass('masked')){
              //  $(this).val('');
                $(this).inputmask({
                    mask: '[aaa m/d/9999 h:s]',
                    clearMaskOnLostFocus: false
                });
             //   $(this).addClass('masked');
           // }
        });

        $('.date-range-selection-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 h:s] [to] [m/d/9999 h:s]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.datetime-range-selection-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 h:s:s] [to] [m/d/9999 h:s:s]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.double-timeentry-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 h:s] [to] [h:s]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.oneday-rangeinput').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 h:s] [to] [m/d/9999 h:s]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.always-show-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.dates-selection-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999] [to] [m/d/9999]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.bithdate-selection-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

        $('.departuredate-selection-input').each(function(){
            if(!$(this).hasClass('masked')){
                $(this).val('');
                $(this).inputmask({
                    mask: '[m/d/9999 h:s]',
                    clearMaskOnLostFocus: false
                });
                $(this).addClass('masked');
            }
        });

}
function pickerApply(){
        $('.singledate-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('MM/DD/YYYY'));                
            }
        });
        $('.singletime-entry-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('MM/DD/YYYY HHmm'));
            }
        });
        $('.day-with-datetime-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('dddd, MM/DD/YYYY HH:mm'));
            }
        });
        $('.date-range-selection-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                if(picker.startDate && picker.endDate){
                    $(this).val(picker.startDate.format('MM/DD/YYYY HH:mm') + ' to ' + picker.endDate.format('MM/DD/YYYY HH:mm'));                
                }
            }
        });
        $('.datetime-range-selection-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                if(picker.startDate && picker.endDate){
                    $(this).val(picker.startDate.format('MM/DD/YYYY HH:mm:ss') + ' to ' + picker.endDate.format('MM/DD/YYYY HH:mm:ss'));                
                }
            }
        });
        $('.double-timeentry-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                if(picker.startDate){
                    var timeArray = picker.startDate.format('MM/DD/YYYY HH:mm HH:mm').split(' ');
                    timeArray[2] = getTimeValue+''+getminuteValue;
                    var setTime = timeArray.join(' ');
                    $(this).val(setTime);
                }
            }
        });
        $('.oneday-rangeinput').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                if(picker.startDate && picker.endDate){
                    $(this).val(picker.startDate.format('MM/DD/YYYY HH:mm') + ' to ' + picker.endDate.format('MM/DD/YYYY HH:mm'));
                }
            } 
        });
        $('.always-show-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('MM/DD/YYYY'));
            }
        });
        $('.dates-selection-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                if(picker.startDate && picker.endDate){
                    $(this).val(picker.startDate.format('MM/DD/YYYY') + ' to ' + picker.endDate.format('MM/DD/YYYY'));                
                }
            }
        });   
        $('.bithdate-selection-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('MM/DD/YYYY'));
            }
        }); 
        $('.departuredate-selection-input').on('apply.daterangepicker cancel.daterangepicker', function (ev, picker) {
            if(ev.type == 'cancel'){
                $(this).val(oldInitDate);
            }else{
                $(this).val(picker.startDate.format('MM/DD/YYYY HH:mm'));
            }
        });
}
