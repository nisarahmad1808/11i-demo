$(document).ready(function(){
/* Add Table Row Color On Collapsed Start*/
$(document).on('click', '.icon-panright', function () {
    if ($(this).hasClass('collapsed')) {
        $(this).closest('tr').removeClass('selected');
    } else {
        $(this).closest('tr').addClass('selected');
    }
});
/* Add Table Row Color On Collapsed End*/

/*Table Scroll Start */
$('#drug-interaction-table').perfectScrollbar();
$('.right-side').perfectScrollbar();
/*Table Scroll End */

$('.override-div').hide();
$('#drug-interaction-table').find('.icon-error').hide();
$('.radio-fun input[type=radio]').on('click', function () {
    if (this.value == 'override') {
        $(this).closest('td').find('.override-div').show();
        $(this).closest('td').find('.icon-error').show();
    } else if (this.value == 'cancel') {
        $(this).closest('td').find('.override-div').hide();
        $(this).closest('td').find('.icon-check').addClass('hide');
        $(this).closest('td').find('.icon-error').hide();
    }
});

$('.reason-overriding li a').on('click', function () {
    $(this).closest('.input-group').find('input').val($(this).text());
    $(this).closest('.input-group-btn').removeClass('open');
    if ($(this).hasClass('approved')) {
        $(this).closest('td').find('.icon-error').hide();
        $(this).closest('td').find('.icon-check').removeClass('hide');
    }
});

$("ul.category-list li").on('click', function () {
    $("li.active").removeClass("active");
    $(this).addClass("active");
});

$('.reason li a').on('click', function () {
    $(this).closest('.input-group').find('input').val($(this).text());
    $(this).closest('.input-group-btn').removeClass('open');
    if ($(this).hasClass('approved')) {
        $('#drug-interaction-table').find('.icon-error').hide();
        $('#drug-interaction-table').find('.icon-check').removeClass('hide');
    }
});

$('.override-div1').hide();
$('#override-btn').on('click', function () {
    $('.override-div1').show();
});

$('#cancel-btn').on('click', function () {
    $('.override-div1').hide();
    $('#drug-interaction-table').find('.icon-error').show();
    $('#drug-interaction-table').find('.icon-check').addClass('hide');
});

$('.selectAll').on('click', function () {
    $(".checkBoxClass").prop('checked', $(this).prop('checked'));
});

/*--- Tabe Structure for Drug Interaction Modal > left list of drug interaction ---*/

$('.drug-interactions-list li').on('click',function(){
	var tabId;
	var linkId = $(this).find('a').attr('href');
	tabId = linkId.substring(1, linkId.length);
	$('.drug-tab-panes .tab-pane').each(function(){
		if($(this).attr('id') == tabId){
		   $(this).addClass('active').siblings('.tab-pane').removeClass('active');
		}
	});
	if($('#drugToFood').hasClass('active')){
		$('.no-delete').hide();
	}
	if($('#drugToDrug').hasClass('active')){
		$('.no-delete').show();
	}
	tabId = null;
});

$('.override-div2').hide();
$('#override-btn2').on('click', function () {
    $('.override-div2').show();
});

$('#cancel-btn2').on('click', function () {
    $('.override-div2').hide();
});
	});