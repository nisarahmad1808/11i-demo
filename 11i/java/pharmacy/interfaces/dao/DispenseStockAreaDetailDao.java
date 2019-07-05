package inpatientWeb.pharmacy.interfaces.dao;

import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DISPENSINGSTOCKAREATABLES;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Lazy
@Scope("prototype")
public class DispenseStockAreaDetailDao {
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate ;
	/**
	 * @param patientLocationMap
	 * @purpose API to get  stock area detail based on patient Location details
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String,Object>> getStockAreaDetailBasedOnPatientLocation(Map<String,Object> patientLocationMap){
		StringBuilder stockAreaQuery = new StringBuilder("SELECT dispsa.name,dispsa.location,dispsa.chargeondispense,dispsa.updateinventory,dispsa.usecharge");
		stockAreaQuery.append(" FROM "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA + " AS dispsa INNER JOIN "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_LOC_MAPPING +" AS dispsalocmap ON dispsa.id=dispsalocmap.dispensestockid"); 
		stockAreaQuery.append(" WHERE  dispsa.deleteflag=0 AND dispsalocmap.deleteflag=0 AND"); 
		stockAreaQuery.append(" dispsa.facilityid=:facilityId AND dispsalocmap.unitid=:unitId AND dispsalocmap.roomid=:roomId AND dispsalocmap.bedid=:bedId");
		return namedParameterJdbcTemplate.queryForList(stockAreaQuery.toString(), patientLocationMap);
	}

	/**
	 * @param medicationLocationMap
	 * @purpose API to get List of stock area detail based on drug dispense code and patient location
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String,Object>>  getStockAreaDetailBasedOnMedicationLocation(Map<String,Object> medicationLocationMap){
		StringBuilder stockAreaQuery = new StringBuilder("SELECT dispsa.name,dispsa.location,dispsa.chargeondispense,dispsa.updateinventory,dispsa.usecharge,dispsaqtymap.quantity");
		stockAreaQuery.append(" FROM "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA + " AS dispsa INNER JOIN "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING +" AS dispsaqtymap ON dispsa.id=dispsaqtymap.dispensestockid");
		stockAreaQuery.append(" INNER JOIN "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_LOC_MAPPING +" AS dispsalocmap ON dispsa.id=dispsalocmap.dispensestockid");
		stockAreaQuery.append(" WHERE dispsa.deleteflag=0 AND dispsaqtymap.deleteflag=0 AND dispsaqtymap.drugdispensecode=:drugDispenseCode AND ");
		stockAreaQuery.append(" dispsa.facilityid=:facilityId AND dispsalocmap.unitid=:unitId AND dispsalocmap.roomid=:roomId AND dispsalocmap.bedid=:bedId");
		return namedParameterJdbcTemplate.queryForList(stockAreaQuery.toString(),medicationLocationMap);
	}

	
	/**
	 * @param dispenseStockId
	 * @purpose API to get List of drug dispense formulary detail based on stock area  
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String,Object>> getListOfDrugDispenseFormularyBasedOnStockArea(int dispenseStockId){
		Map<String,Object> paramMap= new HashMap<String, Object>();
		StringBuilder stockAreaQuery = new StringBuilder("SELECT dispsaqtymap.quantity,dispsaqtymap.drugdispensecode,dispsaqtymap.modifiedby,dispsaqtymap.modifieddatetime,ipitems.itemname");
		stockAreaQuery.append(" FROM "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DRUGFORMULARY + " AS dformulary INNER JOIN "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING +" AS dispsaqtymap ON dformulary.id=dispsaqtymap.formularyid"); 
		stockAreaQuery.append(" INNER JOIN "+DISPENSINGSTOCKAREATABLES.TBL_IP_ITEMS + "  AS ipitems ON dformulary.routedgenericitemid=ipitems.itemid ");
		stockAreaQuery.append(" WHERE dformulary.delflag=0 AND dispsaqtymap.deleteflag=0 AND dispsaqtymap.dispensestockid=:dispenseStockId"); 
		paramMap.put("dispenseStockId", dispenseStockId);
		return namedParameterJdbcTemplate.queryForList(stockAreaQuery.toString(), paramMap);
	}
}
