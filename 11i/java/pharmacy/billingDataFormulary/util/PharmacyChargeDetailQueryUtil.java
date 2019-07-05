package inpatientWeb.pharmacy.billingDataFormulary.util;

public class PharmacyChargeDetailQueryUtil {

	public static StringBuffer getChargeDetaiForFormularylQuery(){
		StringBuffer query = new StringBuffer();
		query.append("SELECT ");		
		query.append(" idf.strength as formStrength,");
		query.append(" idf.drugdispenseCode as drugdispenseCode,");
		query.append(" idf.isCalculate as isCalculate,");
		query.append(" dfap.awup as awup,");
		query.append(" dfap.cost_to_proc,");
		query.append(" idfo.isnonbillable as isnonbillable,");
		query.append(" idfo.chargeTypeId as chargeTypeId,");	
		query.append(" cpt.chargeCode as chargeCode, ");
		query.append(" cpt.HCPCSCodeRange as HCPCSCodeRange, ");
		query.append(" cpt.HCPCSCodeType as HCPCSCodeType, ");
		query.append(" cpt.HCPCSCodeUnit as  HCPCSCodeUnit, ");
		query.append(" cpt.itemId as itemid, ");
		query.append(" idf.doseSize as formDoseSize, ");
		query.append(" idf.doseUom as formDoseUOM, ");
		query.append(" idf.dispenseSize as formDispenseSize, ");
		query.append(" idf.dispenseSizeUom as formDispenseUOM, ");				
		query.append(" idf.cptcodeitemid as  cptcodeitemid, ");	
		query.append(" dfap.packsizeunitcode as  packsizeunitcode, ");
		query.append(" idf.isCalculate as isCalculated, ");
		query.append(" idffac.facilityid as facilityid, ");
		query.append(" dfap.packsize as packsize, ");
		query.append(" dfap.packType as packType, ");
		query.append(" idf.isdrugtypebulk_formulary as  isdrugtypebulk_formulary ");
		query.append(" FROM ");
		query.append(" ip_drugformulary idf ");
		query.append(" LEFT OUTER JOIN ");
		query.append(" ip_drugformulary_OrderTypeSetup idfo ");
		query.append(" ON idfo.[formularyid ] = idf.id AND ");
		query.append(" idfo.[ordertype ] =:ordertype AND ");
		query.append(" idfo.delflag = 0 ");
		query.append(" LEFT OUTER JOIN ");
		query.append(" ip_drugformulary_associatedProducts dfap ");
		query.append(" ON idf.id = dfap.formularyid AND ");
		query.append(" idf.ndc = dfap.ndc AND ");
		query.append(" dfap.delflag = 0");
		query.append(" LEFT OUTER JOIN");
		query.append(" ip_drugformulary_facilities idffac");
		query.append(" ON idffac.formularyId = idf.id AND");
		query.append(" idffac.delflag = 0");
		query.append(" LEFT OUTER JOIN ");
		query.append(" edi_cptcodes cpt ");
		query.append(" ON cpt.itemid = idf.cptcodeitemid");
		query.append(" WHERE ");
		query.append(" idf.id =:formularyId AND ");
		query.append(" idffac.facilityid =:facilityid AND ");
		query.append(" idf.isactive = 1 AND ");
		query.append(" idf.delflag = 0 ");

		return query;
	}
}
