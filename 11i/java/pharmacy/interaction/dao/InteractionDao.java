package inpatientWeb.pharmacy.interaction.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.cpoe.util.OrdersUtil;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_FORMAT;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_TABLES;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.ORDER_TYPES;
import inpatientWeb.pharmacy.interaction.model.DrugInteraction;
import inpatientWeb.pharmacy.interaction.model.DrugOrderDetails;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Repository
@Scope("prototype")
@Lazy
public class InteractionDao {
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired 
	private OrdersUtil ordersUtil;

	/**
	 * get ordered medications stagingOrderId
	 * 
	 * @param oRequestMap
	 * @return itemsMap
	 */
	public List<Long> getStaggingMedOrders(Map<String, Object> oRequestMap){
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT PO.stagingOrderId ");
		strSelectQuery.append(" FROM  ip_ptStagingOrders AS PO ");
		strSelectQuery.append(" INNER JOIN ip_ptStagingMedicationOrders MEDICATION ON PO.stagingOrderId = MEDICATION.ptOrderId ");   
		strSelectQuery.append(" INNER JOIN enc ENC ON ENC.encounterId = PO.episodeEncId AND ENC.deleteFlag = 0 AND PO.episodeEncId  = :episodeEncId ");
		strSelectQuery.append(" INNER JOIN users U ON u.uid = PO.documentedById ");
		strSelectQuery.append(" LEFT JOIN ip_ptstagingorders_action_map ACTMAP ON ACTMAP.stagingOrderId = PO.stagingOrderId AND  ACTMAP.deleteFlag = 0 "); 
		strSelectQuery.append(" WHERE PO.deleteflag = 0 ");
		strSelectQuery.append(" AND PO.orderStatus IN (:orderStatus) ");
		strSelectQuery.append(" AND (PO.documentedById  = :uid OR MEDICATION.isHomeMed = 1) ");
		strSelectQuery.append(" AND (ACTMAP.isReconciled != 1 OR ACTMAP.isReconciled is null) ");
		return  namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<Long>>() {
			public List<Long> extractData(ResultSet ors)throws SQLException{

				List<Long> orderList = new ArrayList<>();
				while (ors.next()) {
					orderList.add(ors.getLong("stagingOrderId"));
				}
				return orderList;
			}
		});
	}
	
	/**
	 * get active medication details like routedDrugId, itemId
	 * 
	 * @param oRequestMap
	 * @return itemsMap
	 */
	public List<Long> getActiveMedOrders(Map<String, Object> oRequestMap){
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT PO.orderId "); 
		strSelectQuery.append(" FROM  ip_ptOrders AS PO  ");
		strSelectQuery.append(" INNER JOIN ip_ptMedicationOrders MEDICATION ON PO.orderId = MEDICATION.ptOrderId ");  
		strSelectQuery.append(" INNER JOIN enc ENC ON ENC.encounterId = PO.episodeEncId AND ENC.deleteFlag = 0 AND PO.episodeEncId  = :episodeEncId  ");  
		strSelectQuery.append(" WHERE PO.deleteflag = 0 AND PO.orderStatus IN (:orderStatuss) ");
		return namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<Long>>() {
			public List<Long> extractData(ResultSet ors) throws SQLException{

				List<Long> orderList = new ArrayList<>();
				while (ors.next()) {
					orderList.add(ors.getLong("orderId"));
				}
				return orderList;
			}
		});
		
	}
	
	/**
	 * get ordered medication details like routedDrugId, itemId
	 * 
	 * @param oRequestMap
	 * @return itemsMap
	 */
	public List<DrugOrderDetails> getStagingMedOrderDetails(Map<String, Object> oRequestMap) {
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT formu.gpi,medOrder.ptOrderId,formu.itemid,items.itemName ,routes.extmappingId as routeID,formu.formulation,formu.strength,formu.strengthuom,medOrder.order_duration,medOrder.order_duration_type,medOrder.order_dose,medOrder.issFlag,medOrder.maxDose,medOrder.order_dose_UOM,medOrder.FrequencyCode,items.routedGenericId,ISNULL(formu_set.drugToFood,1) as drugToFood,ISNULL(formu_set.drugToAlcohol,1) as drugToAlcohol,ISNULL(formu_set.duplicateTherapy,1) as duplicateTherapy,formu_set.routedGenericItemId,medOrder.orderType, medOrder.isHomeMed, items.routedDrugId, formu.iscustommed, cset.isivdiluent, medOrder.ivRate, medOrder.ivRateUOM, medOrder.totalVolume, medOrderDet.orderDose as detailedDose ");
		strSelectQuery.append(" FROM ip_ptstagingmedicationorders medOrder ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_routes routes ON medOrder.routeId = routes.id ");
		strSelectQuery.append(" INNER JOIN ip_ptstagingmedicationorders_detail medOrderDet ON medOrder.medOrderId = medOrderDet.ptmedorderid ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary formu ON medOrderDet.DrugFormularyId = formu.id ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_common_settings cset ON formu.routedGenericItemId = cset.routedGenericItemId ");
		strSelectQuery.append(" LEFT JOIN ip_drugformulary_interaction_setting formu_set ON formu.routedGenericItemId = formu_set.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_items items ON formu.itemid = items.itemID ");
		strSelectQuery.append(" WHERE medOrder.ptOrderId IN (:stagingMedOrderIds)");
		strSelectQuery.append(" AND medOrder.deleteflag=0 ");
		return namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<DrugOrderDetails>>() {
		public List<DrugOrderDetails> extractData(ResultSet ors) throws SQLException{

			DrugOrderDetails orderDetails = null;
			List<DrugOrderDetails> orderDetailsList = new ArrayList<>();
			String strIvRate = "";
			while (ors.next()) {
				orderDetails = new DrugOrderDetails();
				orderDetails.setGpi(ors.getString("gpi"));
				orderDetails.setStrength(ors.getString("strength"));
				orderDetails.setStrengthUOM(ors.getString("strengthuom"));
				orderDetails.setFormulation(ors.getString("formulation"));
				orderDetails.setRouteId(ors.getInt("routeID"));
				orderDetails.setDrugName(ors.getString("itemName") + " " + ors.getString("strength") + " " + ors.getString("formulation"));
				orderDetails.setRoutedGenricId(ors.getString("routedGenericId"));
				orderDetails.setDrugToFood( ors.getInt("drugToFood"));
				orderDetails.setDrugToAlcohol(ors.getInt("drugToAlcohol"));
				orderDetails.setDuplicateTherapy(ors.getInt("duplicateTherapy"));
				orderDetails.setPtOrderId(ors.getInt("ptOrderId"));
				orderDetails.setOrderDuration(ors.getString("order_duration"));
				orderDetails.setOrderDurationType(ors.getString("order_duration_type"));
				orderDetails.setOrderDose(ors.getString("order_dose"));
				if(ors.getInt("issFlag") ==1){
					orderDetails.setOrderDose(ors.getString("maxDose"));
				}
				orderDetails.setOrderDoseUOM(ors.getString("order_dose_UOM"));
				orderDetails.setFrequencyCode(ors.getString("FrequencyCode"));
				orderDetails.setOrderType(ors.getInt("orderType"));
				orderDetails.setHomeMed(ors.getInt("isHomeMed"));
				orderDetails.setRoutedDrugId(ors.getString("routedDrugId"));
				orderDetails.setCustomMed(ors.getInt("iscustommed"));
				orderDetails.setIvDiluent(ors.getInt("isivdiluent"));
				strIvRate = ors.getString("ivRate");
				if(!"".equals(strIvRate.trim())){
					orderDetails.setIvRate(Double.parseDouble(strIvRate));
				}
				orderDetails.setIvRateUOM(ors.getString("ivRateUOM"));
				orderDetails.setTotalVolume(ors.getDouble("totalVolume"));
				orderDetails.setDetailedDose(ors.getDouble("detailedDose"));
				orderDetailsList.add(orderDetails);
			}
			return orderDetailsList;
		}
		});
	}
	
	
	/**
	 * get ordered medication details like routedDrugId, itemId
	 * 
	 * @param oRequestMap
	 * @return itemsMap
	 */
	public List<DrugOrderDetails> getActiveMedOrderDetails(Map<String, Object> oRequestMap) {
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT formu.gpi,medOrder.ptOrderId,formu.itemid,items.itemName ,routes.extmappingId as routeID,formu.formulation,formu.strength,formu.strengthuom,medOrder.order_duration,medOrder.order_duration_type,medOrder.order_dose,medOrder.issFlag,medOrder.maxDose,medOrder.order_dose_UOM,medOrder.FrequencyCode,items.routedGenericId,ISNULL(formu_set.drugToFood,1) as drugToFood,ISNULL(formu_set.drugToAlcohol,1) as drugToAlcohol,ISNULL(formu_set.duplicateTherapy,1) as duplicateTherapy,formu_set.routedGenericItemId,medOrder.orderType, items.routedDrugId, formu.iscustommed, cset.isivdiluent, medOrder.ivRate, medOrder.ivRateUOM, medOrder.totalVolume, medOrderDet.orderDose as detailedDose ");
		strSelectQuery.append(" FROM ip_ptmedicationorders medOrder ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_routes routes ON medOrder.routeId = routes.id ");
		strSelectQuery.append(" INNER JOIN ip_ptmedicationorders_detail medOrderDet ON medOrder.medOrderId = medOrderDet.ptmedorderid ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary formu ON medOrderDet.DrugFormularyId = formu.id ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_common_settings cset ON formu.routedGenericItemId = cset.routedGenericItemId ");
		strSelectQuery.append(" LEFT JOIN ip_drugformulary_interaction_setting formu_set ON formu.routedGenericItemId = formu_set.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_items items ON formu.itemid = items.itemID ");
		strSelectQuery.append(" WHERE medOrder.ptOrderId IN (:activeMedOrderIds)");
		strSelectQuery.append(" AND medOrder.deleteflag=0 ");
		return namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<DrugOrderDetails>>() {
			public List<DrugOrderDetails> extractData(ResultSet ors) throws SQLException{

				DrugOrderDetails orderDetails = null;
				List<DrugOrderDetails> orderDetailsList = new ArrayList<>();
				String strIvRate = "";
				while (ors.next()) {
					orderDetails = new DrugOrderDetails();
					orderDetails.setGpi(ors.getString("gpi"));
					orderDetails.setStrength(ors.getString("strength"));
					orderDetails.setStrengthUOM(ors.getString("strengthuom"));
					orderDetails.setFormulation(ors.getString("formulation"));
					orderDetails.setRouteId(ors.getInt("routeID"));
					orderDetails.setDrugName(ors.getString("itemName") + " " + ors.getString("strength") + " " + ors.getString("formulation"));
					orderDetails.setRoutedGenricId(ors.getString("routedGenericId"));
					orderDetails.setDrugToFood( ors.getInt("drugToFood"));
					orderDetails.setDrugToAlcohol(ors.getInt("drugToAlcohol"));
					orderDetails.setDuplicateTherapy(ors.getInt("duplicateTherapy"));
					orderDetails.setPtOrderId(ors.getInt("ptOrderId"));
					orderDetails.setOrderDuration(ors.getString("order_duration"));
					orderDetails.setOrderDurationType(ors.getString("order_duration_type"));
					orderDetails.setOrderDose(ors.getString("order_dose"));
					if(ors.getInt("issFlag") ==1){
						orderDetails.setOrderDose(ors.getString("maxDose"));
					}
					orderDetails.setOrderDoseUOM(ors.getString("order_dose_UOM"));
					orderDetails.setFrequencyCode(ors.getString("FrequencyCode"));
					orderDetails.setOrderType(ors.getInt("orderType"));
					orderDetails.setRoutedDrugId(ors.getString("routedDrugId"));
					orderDetails.setCustomMed(ors.getInt("iscustommed"));
					orderDetails.setIvDiluent(ors.getInt("isivdiluent"));
					strIvRate = ors.getString("ivRate");
					if(!"".equals(strIvRate.trim())){
						orderDetails.setIvRate(Double.parseDouble(strIvRate));
					}
					orderDetails.setIvRateUOM(ors.getString("ivRateUOM"));
					orderDetails.setTotalVolume(ors.getDouble("totalVolume"));
					orderDetails.setDetailedDose(ors.getDouble("detailedDose"));
					orderDetailsList.add(orderDetails);
				}
				return orderDetailsList;
			}
		});
	}
	
	public List<DrugOrderDetails> getStagingCustomMedOrderDetails(Map<String, Object> oRequestMap) {
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT formu.gpi,medOrder.ptOrderId,formu.itemid,items.itemName ,routes.extmappingId as routeID,formu.formulation,formu.strength,formu.strengthuom,medOrder.order_duration,medOrder.order_duration_type,medOrder.order_dose,medOrder.issFlag,medOrder.maxDose,medOrder.order_dose_UOM,medOrder.FrequencyCode,items.routedGenericId,ISNULL(formu_set.drugToFood,1) as drugToFood,ISNULL(formu_set.drugToAlcohol,1) as drugToAlcohol,ISNULL(formu_set.duplicateTherapy,1) as duplicateTherapy,formu_set.routedGenericItemId,medOrder.orderType, medOrder.isHomeMed, items.routedDrugId, items.routedDrugId, formu.iscustommed, cset.isivdiluent, medOrder.ivRate, medOrder.ivRateUOM, medOrder.totalVolume, medOrderDet.orderDose as detailedDose  ");
		strSelectQuery.append(" FROM ip_ptstagingmedicationorders medOrder ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_routes routes ON medOrder.routeId = routes.id ");
		strSelectQuery.append(" INNER JOIN ip_ptstagingmedicationorders_detail medOrderDet ON medOrder.medOrderId = medOrderDet.ptmedorderid ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary formu ON medOrderDet.DrugFormularyId = formu.id ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_common_settings cset ON formu.routedGenericItemId = cset.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_med_mapping customDrug ON customDrug.formularyid = formu.id ");
		strSelectQuery.append(" LEFT JOIN ip_drugformulary_interaction_setting formu_set ON formu.routedGenericItemId = formu_set.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_items items ON customDrug.mapped_itemid = items.itemID ");
		strSelectQuery.append(" WHERE medOrder.ptOrderId IN (:stagingMedOrderIds)");
		strSelectQuery.append(" AND medOrder.deleteflag=0 ");
		return namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<DrugOrderDetails>>() {
		public List<DrugOrderDetails> extractData(ResultSet ors) throws SQLException{

			DrugOrderDetails orderDetails = null;
			List<DrugOrderDetails> orderDetailsList = new ArrayList<>();
			String strIvRate = "";
			while (ors.next()) {
				orderDetails = new DrugOrderDetails();
				orderDetails.setGpi(ors.getString("gpi"));
				orderDetails.setStrength(ors.getString("strength"));
				orderDetails.setStrengthUOM(ors.getString("strengthuom"));
				orderDetails.setFormulation(ors.getString("formulation"));
				orderDetails.setRouteId(ors.getInt("routeID"));
				orderDetails.setDrugName(ors.getString("itemName"));
				orderDetails.setCustomMed(ors.getInt("iscustommed"));
				if(orderDetails.getCustomMed() != 1){
					orderDetails.setDrugName(orderDetails.getDrugName()+ " " + ors.getString("strength") + " " + ors.getString("formulation"));
				}
				orderDetails.setRoutedGenricId(ors.getString("routedGenericId"));
				orderDetails.setDrugToFood( ors.getInt("drugToFood"));
				orderDetails.setDrugToAlcohol(ors.getInt("drugToAlcohol"));
				orderDetails.setDuplicateTherapy(ors.getInt("duplicateTherapy"));
				orderDetails.setPtOrderId(ors.getInt("ptOrderId"));
				orderDetails.setOrderDuration(ors.getString("order_duration"));
				orderDetails.setOrderDurationType(ors.getString("order_duration_type"));
				orderDetails.setOrderDose(ors.getString("order_dose"));
				if(ors.getInt("issFlag") ==1){
					orderDetails.setOrderDose(ors.getString("maxDose"));
				}
				orderDetails.setOrderDoseUOM(ors.getString("order_dose_UOM"));
				orderDetails.setFrequencyCode(ors.getString("FrequencyCode"));
				orderDetails.setOrderType(ors.getInt("orderType"));
				orderDetails.setHomeMed(ors.getInt("isHomeMed"));
				orderDetails.setRoutedDrugId(ors.getString("routedDrugId"));
				orderDetails.setIvDiluent(ors.getInt("isivdiluent"));
				strIvRate = ors.getString("ivRate");
				if(!"".equals(strIvRate.trim())){
					orderDetails.setIvRate(Double.parseDouble(strIvRate));
				}
				orderDetails.setIvRateUOM(ors.getString("ivRateUOM"));
				orderDetails.setTotalVolume(ors.getDouble("totalVolume"));
				orderDetails.setDetailedDose(ors.getDouble("detailedDose"));
				orderDetailsList.add(orderDetails);
			}
			return orderDetailsList;
		}
		});
	}
	
	public List<DrugOrderDetails> getActiveCustomMedOrderDetails(Map<String, Object> oRequestMap) {
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT formu.gpi,medOrder.ptOrderId,formu.itemid,items.itemName ,routes.extmappingId as routeID,formu.formulation,formu.strength,formu.strengthuom,medOrder.order_duration,medOrder.order_duration_type,medOrder.order_dose,medOrder.issFlag,medOrder.maxDose,medOrder.order_dose_UOM,medOrder.FrequencyCode,items.routedGenericId,ISNULL(formu_set.drugToFood,1) as drugToFood,ISNULL(formu_set.drugToAlcohol,1) as drugToAlcohol,ISNULL(formu_set.duplicateTherapy,1) as duplicateTherapy,formu_set.routedGenericItemId,medOrder.orderType, items.routedDrugId, formu.iscustommed, cset.isivdiluent, medOrder.ivRate, medOrder.ivRateUOM, medOrder.totalVolume, medOrderDet.orderDose as detailedDose ");
		strSelectQuery.append(" FROM ip_ptmedicationorders medOrder ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_routes routes ON medOrder.routeId = routes.id ");
		strSelectQuery.append(" INNER JOIN ip_ptmedicationorders_detail medOrderDet ON medOrder.medOrderId = medOrderDet.ptmedorderid ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary formu ON medOrderDet.DrugFormularyId = formu.id ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_common_settings cset ON formu.routedGenericItemId = cset.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_drugformulary_med_mapping customDrug ON customDrug.formularyid = formu.id ");
		strSelectQuery.append(" LEFT JOIN ip_drugformulary_interaction_setting formu_set ON formu.routedGenericItemId = formu_set.routedGenericItemId ");
		strSelectQuery.append(" INNER JOIN ip_items items ON customDrug.mapped_itemid = items.itemID ");
		strSelectQuery.append(" WHERE medOrder.ptOrderId IN (:activeMedOrderIds)");
		strSelectQuery.append(" AND medOrder.deleteflag=0 ");
		return namedParameterJdbcTemplate.query(strSelectQuery.toString(), oRequestMap, new ResultSetExtractor<List<DrugOrderDetails>>() {
			public List<DrugOrderDetails> extractData(ResultSet ors) throws SQLException{

				DrugOrderDetails orderDetails = null;
				List<DrugOrderDetails> orderDetailsList = new ArrayList<>();
				String strIvRate = "";
				while (ors.next()) {
					orderDetails = new DrugOrderDetails();
					orderDetails.setGpi(ors.getString("gpi"));
					orderDetails.setStrength(ors.getString("strength"));
					orderDetails.setStrengthUOM(ors.getString("strengthuom"));
					orderDetails.setFormulation(ors.getString("formulation"));
					orderDetails.setRouteId(ors.getInt("routeID"));
					orderDetails.setDrugName(ors.getString("itemName"));
					orderDetails.setCustomMed(ors.getInt("iscustommed"));
					if(orderDetails.getCustomMed() != 1){
						orderDetails.setDrugName(orderDetails.getDrugName() + " " + ors.getString("strength") + " " + ors.getString("formulation"));
					}
					orderDetails.setRoutedGenricId(ors.getString("routedGenericId"));
					orderDetails.setDrugToFood( ors.getInt("drugToFood"));
					orderDetails.setDrugToAlcohol(ors.getInt("drugToAlcohol"));
					orderDetails.setDuplicateTherapy(ors.getInt("duplicateTherapy"));
					orderDetails.setPtOrderId(ors.getInt("ptOrderId"));
					orderDetails.setOrderDuration(ors.getString("order_duration"));
					orderDetails.setOrderDurationType(ors.getString("order_duration_type"));
					orderDetails.setOrderDose(ors.getString("order_dose"));
					if(ors.getInt("issFlag") ==1){
						orderDetails.setOrderDose(ors.getString("maxDose"));
					}
					orderDetails.setOrderDoseUOM(ors.getString("order_dose_UOM"));
					orderDetails.setFrequencyCode(ors.getString("FrequencyCode"));
					orderDetails.setOrderType(ors.getInt("orderType"));
					orderDetails.setRoutedDrugId(ors.getString("routedDrugId"));
					orderDetails.setIvDiluent(ors.getInt("isivdiluent"));
					strIvRate = ors.getString("ivRate");
					if(!"".equals(strIvRate.trim())){
						orderDetails.setIvRate(Double.parseDouble(strIvRate));
					}
					orderDetails.setIvRateUOM(ors.getString("ivRateUOM"));
					orderDetails.setTotalVolume(ors.getDouble("totalVolume"));
					orderDetails.setDetailedDose(ors.getDouble("detailedDose"));
					orderDetailsList.add(orderDetails);
				}
				return orderDetailsList;
			}
		});
	}
	
	/**
	 * Insert drug interaction action and other details
	 * 
	 * @param valuesList
	 * @param userId
	 * @return status
	 * @throws Exception 
	 */
	public int insertDrugInteraction(List<DrugInteraction> valuesList, int userId){
		StringBuilder insertDrugInterQuery = null;
		int autoIncrInteraction = 0;
		MapSqlParameterSource mapSqlParamSrc = null;
		Map<String, Object> queryData = null;
		String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
		insertDrugInterQuery = new StringBuilder();
		/***************************************
		 * Insert interaction override action *
		 ***************************************/
		insertDrugInterQuery.append("INSERT INTO ").append(INTERACTION_TABLES.DRUG_INTERACTION.tabel()).append(" (encounterId,severityCode,description,overrideReason,interactionCategory,overrideBy,overrideDateTime) ").append(" values (:encounterId,:severityCode,:description,:overrideReason,:interactionCategory,:overrideBy,:overrideDateTime) ");

		for (DrugInteraction drugInteraction : valuesList) {
			queryData = new HashMap<>();
			queryData.put("encounterId", drugInteraction.getEncounterId());
			queryData.put("severityCode", drugInteraction.getSeverityCode());
			queryData.put("description", drugInteraction.getDescription());
			queryData.put("overrideReason", drugInteraction.getOverrideReason());
			queryData.put("interactionCategory", drugInteraction.getInteractionCategory());
			queryData.put("overrideBy", drugInteraction.getOverrideBy());
			queryData.put("overrideDateTime", formatedDate);
			mapSqlParamSrc = ordersUtil.convertMapToMapSqlParameterSource(queryData);
			autoIncrInteraction = Util.executeAndReturnKey(insertDrugInterQuery.toString(), mapSqlParamSrc);

			if (!drugInteraction.getOrderIdList().isEmpty()) {
				MapSqlParameterSource mapSqlParamdet = null;
				Map<String, Object> queryDetData = null;
				StringBuilder insertDetailQuery = new StringBuilder();
				/*********************************************************
				 * Insert interaction override into the detail tables*
				 *********************************************************/
				insertDetailQuery.append("INSERT INTO ").append(INTERACTION_TABLES.DRUG_INTERACTION_DETAILS.tabel()).append(" (interactionId,medOrderId,medOrderIdType)").append("values (:interactionId,:medOrderId,:medOrderIdType)");

				for (Map<String, Object> order : drugInteraction.getOrderIdList()) {
					queryDetData = new HashMap<>();
					queryDetData.put("interactionId", autoIncrInteraction);
					queryDetData.put("medOrderId", Util.getIntValue(order, INTERACTION.ORDER_ID.text()));
					if(Util.getBooleanValue(order,INTERACTION.IS_STAGING_ORDER.text())){
						queryDetData.put("medOrderIdType", ORDER_TYPES.STAGING.id());	
					}else if(!Util.getBooleanValue(order,INTERACTION.IS_STAGING_ORDER.text())){
						queryDetData.put("medOrderIdType", ORDER_TYPES.ACTIVE.id());	
					}else{
						queryDetData.put("medOrderIdType", ORDER_TYPES.DEFAULT.id());	
					}
					mapSqlParamdet = ordersUtil.convertMapToMapSqlParameterSource(queryDetData);
					Util.executeAndReturnKey(insertDetailQuery.toString(), mapSqlParamdet);
				}
			}
		}
		return autoIncrInteraction;
	}
	
	/**
	 * update drug interaction action and other details
	 * 
	 * @param valuesList
	 * @param userId
	 * @return status
	 * @throws Exception 
	 */
	public boolean updateInteractionAction(Map<String, Object> inputParam){
		StringBuilder updateInteractionQuery = new StringBuilder();
		MapSqlParameterSource mapSqlParamSrc = null;
		int noOfRowsAffected = 0;
		updateInteractionQuery.append("UPDATE ").append(INTERACTION_TABLES.DRUG_INTERACTION.tabel()).append(" SET  ");
		if (Util.getIntValue(inputParam, INTERACTION.ACKNOWLEDGE_FLAG.text(), 0) > 0) {
			updateInteractionQuery.append(" acknowledgeFlag=:acknowledgeFlag,  ");
		}
		if (Util.getIntValue(inputParam, INTERACTION.ACKNOWLEDGE_BY.text(), 0) > 0) {
			updateInteractionQuery.append(" acknowledgeBy=:acknowledgeBy, ");
		}
		updateInteractionQuery.append(" acknowledgeDateTime=:acknowledgeDateTime ");
		updateInteractionQuery.append(" where interactionId=:interactionId ");

		mapSqlParamSrc = ordersUtil.convertMapToMapSqlParameterSource(inputParam);
		noOfRowsAffected = namedParameterJdbcTemplate.update(updateInteractionQuery.toString(), mapSqlParamSrc);
		return noOfRowsAffected > 0 ? true : false;
	}

	/**
	 * Insert drug interaction action logs with interaction HTML
	 * 
	 * @param encounterId
	 * @param action
	 * @param interactionHtml
	 * @param userId
	 * @return status
	 * @throws Exception 
	 */
	public boolean logDrugInteraction(int encounterId, int interactionId, String action, String interactionHtml, int userId){
		StringBuilder insertDrugInterQuery = null;
		int intAutoIncrementId = 0;
		MapSqlParameterSource mapSqlParamSrc = null;
		Map<String, Object> queryData = null;
		String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
		insertDrugInterQuery = new StringBuilder();
		insertDrugInterQuery.append("INSERT INTO ")
							.append(INTERACTION_TABLES.DRUG_INTERACTION_LOG.tabel())
							.append(" (encounterId,interactionId,provideraction,loguserid,eventdatetime,dataHtml) ")
							.append(" values (:encounterId,:interactionId,:provideraction,:loguserid,:eventdatetime,:dataHtml) ");
		queryData = new HashMap<>();
		queryData.put("encounterId", encounterId);
		queryData.put("interactionId", interactionId);
		queryData.put("provideraction", action);
		queryData.put("loguserid", userId);
		queryData.put("eventdatetime", formatedDate);
		queryData.put("dataHtml", interactionHtml);
		mapSqlParamSrc = ordersUtil.convertMapToMapSqlParameterSource(queryData);
		intAutoIncrementId = Util.executeAndReturnKey(insertDrugInterQuery.toString(), mapSqlParamSrc);
		return intAutoIncrementId > 0 ? true : false;
	}

	/**
	 * This method is use to fetch overridden reason
	 * 
	 * @param encounterId
	 * @return respMap
	 */
	public List<Map<String, Object>> fetchOverriddenInteraction(Map<String, Object> requestMap) {
		StringBuilder selectInterQuery = null;
		selectInterQuery = new StringBuilder();
		selectInterQuery.append("SELECT inter.interactionId,inter.severityCode,inter.description,inter.overrideReason,inter.interactionCategory,inter.overrideBy,inter.acknowledgeFlag,inter.acknowledgeBy,usrd.ufname as ackfname,usrd.ulname as acklname,usr.ufname as overfname,usr.ulname as overlname")
						.append(" from ").append(INTERACTION_TABLES.DRUG_INTERACTION.tabel()).append(" inter ")
						.append(" LEFT JOIN users usrd ON  usrd.uid = inter.acknowledgeBy ")
						.append(" LEFT JOIN users usr ON  usr.uid = inter.overrideBy")
						.append(" WHERE inter.encounterId = :episodeEncId ")
						.append(" ORDER BY  inter.interactionId ");
		return namedParameterJdbcTemplate.query(selectInterQuery.toString(), requestMap, new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet ors) throws SQLException{

				Map<String, Object> interactions = null;
				List<Map<String, Object>> overrideInteractionList = new ArrayList<Map<String, Object>>();
				while (ors.next()) {
					interactions = new HashMap<String, Object>();
					interactions.put("interactionId", ors.getInt("interactionId"));
					interactions.put("severityCode", ors.getInt("severityCode"));
					interactions.put("description", ors.getString("description"));
					interactions.put("overrideReason", ors.getInt("overrideReason"));
					interactions.put("interactionCategory", ors.getInt("interactionCategory"));
					interactions.put("overrideBy", ors.getString("overfname") + " " + ors.getString("overlname"));
					interactions.put("isAcknowledge", ors.getInt("acknowledgeFlag"));
					interactions.put("acknowledgeBy", ors.getString("ackfname") + " " + ors.getString("acklname"));
					overrideInteractionList.add(interactions);
				}
				return overrideInteractionList;
			}
		});
	}

	
	/**
	 * Fetch override inter details.
	 *
	 * @param requestMap the request map
	 * @return the list
	 */
	public List<Long> fetchOverrideInterDetails(Map<String, Object> requestMap) {
		StringBuilder selectInterQuery = null;
		selectInterQuery = new StringBuilder();
		selectInterQuery.append("SELECT interactionId,medOrderId ").append(" from ").append(INTERACTION_TABLES.DRUG_INTERACTION_DETAILS.tabel()).append(" where interactionId = :interactionId ").append(" ORDER BY medOrderId ");
		return namedParameterJdbcTemplate.query(selectInterQuery.toString(), requestMap, new ResultSetExtractor<List<Long>>() {
			@Override
			public List<Long> extractData(ResultSet ors) throws SQLException{
				List<Long> overriddenOrderList = new ArrayList<>();
				while (ors.next()) {
					overriddenOrderList.add(ors.getLong("medOrderId"));
				}
				return overriddenOrderList;
			}
		});
	}
	
	/**
	 * Gets the facility id by enc.
	 *
	 * @param encounterId the encounter id
	 * @return the facility id by enc
	 */
	public int getFacilityIdByEnc(long encounterId){
		int facilityId=0;
		StringBuilder query = new StringBuilder();
		Map<String,Object> paramMap = new HashMap<>();
		query.append("select e.facilityId from enc e where e.encounterID=:encounterId");
		paramMap.put("encounterId", encounterId);
		facilityId=namedParameterJdbcTemplate.queryForObject(query.toString(), paramMap,  Integer.class);
		return facilityId;
	}
}
