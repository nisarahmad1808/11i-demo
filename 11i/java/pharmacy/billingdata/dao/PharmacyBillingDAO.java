package inpatientWeb.pharmacy.billingdata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.pharmacy.billingdata.model.OrderDetails;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingDetail;

/**
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 10, 2018
 */

@Repository
@Scope("prototype")
public class PharmacyBillingDAO {

	private final static String TABLE_NAME = "ip_pharmacy_billing_detail";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	// fetch by scheduleId -> single, may return multiple records if we have schedule of 40 mg and given 20mg, 10mg, 10mg
	// fetch orderID -> List
	
	public int savePharmacyBillingDetail(PharmacyBillingDetail pharmacyBillingDetails){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ip_pharmacy_billing_detail (billingDataId, formularyId, orderId, ndcCode, emarDrugScheduleId, action, successFlag, dispense, createdBy, createdDate, moduleType,awup,fee,priceRuleId,costToProcure,cptItemId) ");
		query.append("VALUES(:billingDataId, :formularyId, :orderId, :ndcCode, :emarDrugScheduleId, :action, :successFlag, :dispense, :createdBy, :createdDate, :moduleType,:awup,:fee,:priceRuleId,:costToProcure,:cptItemId)");
		
		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("billingDataId", pharmacyBillingDetails.getBillingDataId());
		paramMap.put("formularyId",pharmacyBillingDetails.getFormularyId());
		paramMap.put("orderId", pharmacyBillingDetails.getOrderId());
		paramMap.put("ndcCode",pharmacyBillingDetails.getNdcCode());
		paramMap.put("emarDrugScheduleId",pharmacyBillingDetails.geteMARDrugAdminScheduleId());
		paramMap.put("action",pharmacyBillingDetails.getAction());
		paramMap.put("successFlag",pharmacyBillingDetails.getSuccessFlag());
		paramMap.put("dispense",pharmacyBillingDetails.getDispense());
		paramMap.put("createdBy",pharmacyBillingDetails.getCreatedBy());
		paramMap.put("createdDate",pharmacyBillingDetails.getCreatedDate());
		paramMap.put("moduleType",pharmacyBillingDetails.getModuleType());
		paramMap.put("awup",pharmacyBillingDetails.getAwup());
		paramMap.put("fee",pharmacyBillingDetails.getFee());
		paramMap.put("priceRuleId",pharmacyBillingDetails.getPriceRuleId());
		paramMap.put("costToProcure",pharmacyBillingDetails.getCostToProcure());
		paramMap.put("cptItemId",pharmacyBillingDetails.getCptItemId());
		return namedParameterJdbcTemplate.update(query.toString(), paramMap);
	}
	
	public int updatePharmacyBillingDetail(PharmacyBillingDetail pharmacyBillingDetails){
		StringBuilder query = new StringBuilder();
		query.append("UPDATE ip_pharmacy_billing_detail set billingDataId=:billingDataId, formularyId=:formularyId, emarDrugScheduleId=:emarDrugScheduleId, action=:action, successFlag=:successFlag, dispense=:dispense, modifiedBy=:modifiedBy, modifiedDate=:modifiedDate ");
		query.append("WHERE orderId=:orderId and ndcCode=:ndcCode");
		
		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("billingDataId", pharmacyBillingDetails.getBillingDataId());
		paramMap.put("formularyId",pharmacyBillingDetails.getFormularyId());
		paramMap.put("orderId", pharmacyBillingDetails.getOrderId());
		paramMap.put("ndcCode",pharmacyBillingDetails.getNdcCode());
		paramMap.put("emarDrugScheduleId",pharmacyBillingDetails.geteMARDrugAdminScheduleId());
		paramMap.put("action",pharmacyBillingDetails.getAction());
		paramMap.put("successFlag",pharmacyBillingDetails.getSuccessFlag());
		paramMap.put("dispense",pharmacyBillingDetails.getDispense());
		paramMap.put("modifiedBy",pharmacyBillingDetails.getModifiedBy());
		paramMap.put("modifiedDate",pharmacyBillingDetails.getModifiedDate());
		return namedParameterJdbcTemplate.update(query.toString(), paramMap);
	}
	
	public PharmacyBillingDetail getPharmacyBillingDetail(long orderId, String ndcCode){
		StringBuilder query = new StringBuilder();
		query.append(" SELECT id, billingDataId, formularyId, orderId, ndcCode, emarDrugScheduleId, action, successFlag, dispense, createdBy, createdDate, modifiedBy, modifiedDate, deleteFlag ");
		query.append(String.format(" FROM %s AS pbd ", TABLE_NAME));
		query.append(" where pbd.orderId =:orderId and pbd.ndcCode =:ndcCode");
		query.append(" AND pbd.billingDataId IS NOT NULL AND  pbd.formularyId IS NOT NULL AND  pbd.orderId IS NOT NULL ");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("orderId", orderId);
		paramMap.put("ndcCode", ndcCode);
		List<PharmacyBillingDetail> pharmacyBillingDetailList = namedParameterJdbcTemplate.query(query.toString(), paramMap, new PharmacyBillingDetail());
		if(pharmacyBillingDetailList == null || pharmacyBillingDetailList.isEmpty())
			return null;
		
		return pharmacyBillingDetailList.get(0);
	}	 
	
	public int deletePharmacyBillingDetail(PharmacyBillingDetail pharmacyBillingDetails, boolean isDeleteByBillingId){
		StringBuilder updateQuery = new StringBuilder();
		Map<String,Object> paramMap=new HashMap<>();
		updateQuery.append("update ip_pharmacy_billing_detail set deleteFlag = 1, modifiedBy=:modifiedBy, modifiedDate=:modifiedDate ");
		if(isDeleteByBillingId){
			updateQuery.append("where billingDataId=:billingDataId");
			paramMap.put("billingDataId", pharmacyBillingDetails.getBillingDataId());
		} else {
			updateQuery.append("where orderId=:orderId");
			paramMap.put("orderId", pharmacyBillingDetails.getOrderId());
		}
		paramMap.put("modifiedBy", pharmacyBillingDetails.getModifiedBy());
		paramMap.put("modifiedDate", pharmacyBillingDetails.getModifiedDate());
		return namedParameterJdbcTemplate.update(updateQuery.toString(), paramMap);
	}
	
	public OrderDetails getOrderDetails(long medOrderId){
		StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		query.append(" ippt.orderedById, ippt.orderDateTime ");
		query.append(" FROM ");
		query.append(" ip_ptmedicationorders ptmo inner join ip_ptorders ippt on ptmo.ptOrderId = ippt.orderId ");
		query.append(" where ptmo.medOrderId =:medOrderId ");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("medOrderId", medOrderId);
		
		return namedParameterJdbcTemplate.query(query.toString(), paramMap,new OrderDetails()).get(0);
	}
	
	public int getPracticeId(int encounterId){
		StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		query.append(" enc.practiceId ");
		query.append(" FROM ip_EncInfo ipenc ");
		query.append(" INNER JOIN enc ON enc.encounterID = ipenc.encounterId AND enc.deleteFlag=0 ");
		query.append(" where ipenc.encounterId=:encounterId ");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("encounterId", encounterId);
		
		List<Integer> tmplList;
		tmplList = namedParameterJdbcTemplate.query(query.toString(), paramMap, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int arg1) throws SQLException {
				return NumberUtils.toInt(rs.getString("practiceId"), 0);
			} 
    	});
		if(tmplList == null || tmplList.isEmpty()) {
			return 0;
		}
    	return tmplList.get(0);
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	
	
}