<link rel="stylesheet" type="text/css" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/pharmacytab.css"> 
<link rel="stylesheet" href="/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/workQueue/css/resource.css">
<script type="text/javascript">
var patientId = ${patientId};
var encounterId = ${encounterId};
var encounterType = ${encounterType};
var episodeEncounterId = ${episodeEncounterId};
var departmentId = ${departmentId};
var serviceType = ${serviceType};
</script>
	<div class="clearfix"></div>
	<div ng-controller="pharmacyTabsCtrl as objPharmacyTabs" ng-init="objPharmacyTabs.init();">
		<div >
			<div class="col-sm-12 nopadding">
				<div class="collapse in" id="top-pane">
					<div class="light-whitebg light-bluebg patient-data-pane">
							<div><pt-dashboard-top-header encounter-id="objPharmacyTabs.encounterId" discipline-name="Pharmacy" enc-type="objPharmacyTabs.encounterType" /></div>
							<div class="clearfix"></div>
						</div>
					</div>
					<div class="green-bg">
						<div class="col-lg-12 col-sm-12 nopadding">
							<ul class="green-navbg">
								<li ng-repeat="tab in objPharmacyTabs.tabitems track by $index" ng-class="{'active':objPharmacyTabs.selectedTab.name==tab.name}">
									<a href="{{'#' + tab.name}}" target="_self" data-toggle="tab" ng-click="objPharmacyTabs.loadTab(tab)" class="wht-anchor">{{tab.displayName}} </a>
								</li> 
							</ul>
						</div>
						<div class="clearfix"></div>
					</div>
					<div class="clearfix"></div>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="clearfix"></div>
			<div class="relative">
				<div class="left-wrap">
					<div class="col-sm-12 nopadding lgt-gry">
						<div class="col-sm-12 nopadding med-tiles">
							<div class="row-fluid">
								<div class="span12 col-sm-12 nopadding">
									<div class="dashboard-content">
										<div class="tab-content">
											<div class="tab-pane" ng-class="{'active':objPharmacyTabs.selectedTabName}">
		           								<div ng-include src="objPharmacyTabs.tabsURL"></div>
		           			 				</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="graph-popup">
							<div class="col-sm-12 head">
								<div class="col-sm-6 nopadding">
									<p class="fnt14 bold mt8">Temperature (F)</p>
								</div>
								<div class="col-sm-6 nopadding">
									<i class="icon black-cross pull-right mt10"></i>
								</div>
							</div>
							<div class="col-sm-12 nopadding graph">
								<i class="img-graph ml15 mt20"></i>
							</div>
							<div class="col-sm-12 nopadding">
								<div class="chart-table">
								    <table class="table table-bordered nomargin">
										<thead>
											<tr>
												<th class="text-center">Date</th>
												<th class="text-center">Time</th>
												<th class="text-center">Temperature</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>05/05/2015</td>
												<td>11:30:00</td>
												<td>102 F</td>
											</tr>
											<tr>
												<td>05/05/2015</td>
												<td>11:30:00</td>
												<td>102 F</td>
											</tr>
											<tr>
												<td>05/05/2015</td>
												<td>11:30:00</td>
												<td>102 F</td>
											</tr>
											<tr>
												<td>05/05/2015</td>
												<td>11:30:00</td>
												<td>102 F</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

<script>
	$(document).on('click', '#ESIdrop > li > a', function() {
		var esiDiv = $(this).html();
		$('#chngESI').html(esiDiv);
	});
	
	$('.get-txt-trim').tooltip({
	
		position: {
			my: "right top",
			at: "right bottom"
		}
	});
	
	$('.symp-name').tooltip({
	
		position: {
			my: "right-20 top",
			at: "right-10 bottom"
		}
	});
	$('.dx-name').tooltip({
	
		position: {
			my: "right+50 top",
			at: "right+50 bottom"
		},
		content: function() {
			return $(this).prop('title');
		}
	});
</script>