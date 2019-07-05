package inpatientWeb.admin.pharmacySettings.formularySetup.dao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.CwMobile.CwUtils;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.catalog.StringUtil;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.SqlTranslator;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.FORMULARY_TABLE;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderDtlTblColumn;
import inpatientWeb.Global.medicationhelper.msclinical.service.MSClinicalService;
import inpatientWeb.Global.medicationhelper.service.MedicationHelperService;
import inpatientWeb.Global.medicationhelper.service.MigrateRxService;
import inpatientWeb.Global.medicationhelper.util.RxUtil;
import inpatientWeb.Global.rxOrderRadixTree.CPOERxSearchInterface;
import inpatientWeb.Global.rxOrderRadixTree.RxCacheCSUpdateRequest;
import inpatientWeb.Global.rxOrderRadixTree.RxOrderTreeServices;
import inpatientWeb.Global.rxOrderRadixTree.Utils.QueryBuilderUtils;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplatePNRIndication;
import inpatientWeb.admin.pharmacySettings.configureDictionary.service.ConfigurationDictionaryService;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.CostPerFormulary;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.DispenseStockAreaQuantityMappingModal;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.FormularyBrands;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.FormularyConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyHelper;
import inpatientWeb.admin.pharmacySettings.pharmacyUtility.model.PriceRuleParam;
import inpatientWeb.cpoe.orders.medication.util.MedicationDosageReqParam;
import inpatientWeb.cpoe.util.CPOEEnum;
import inpatientWeb.eMAR.modal.LotSearchData;
import inpatientWeb.locationMgmt.model.LogicalParam;
import inpatientWeb.locationMgmt.service.LogicalLookupService;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.daoImpl.WorkQueueDAOImpl;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;


@Repository
@Scope("prototype")
@Lazy  
public class FormularySetupDao  
{	  
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private int newRouteId = -1;
	private int newFormulationId = -1;
	private int newFrequencyId = -1;
	private String strDrugItemIdName = "";
	private List<TemplateForAssoProductLotDetails> tmplAssoProdLotList = null;
	private  String isIVDiluent = "0";
	@Autowired
	 private LogicalLookupService logicalLookupService;
	 @Autowired
	 private RxOrderTreeServices rxOrderTreeService;
	 @Autowired
	 private ConfigurationDictionaryService dictionaryService; 
	 
	 @Autowired
	 private MigrateRxService migrateRxService;
	 
	@Autowired
	private AuditLogService auditLogService; 
	
	@Autowired
	private MedicationHelperService medHelperService;	
	
	
	private static final String FORMULARY_SETUP_MODULE = "FormularySetupModule";
	private static final String FORMULARY_SETUP_PRE_REQ = "Common Setting -> Pre Requisite";
	private static final String EXTMAPPINGCODE="ext_mapping_code";
	private static final String DELFLAG = "delflag";	 
	private static final String DRUG_NAME_ID = "drugnameid";
	private static final String ADDED_DRUG_ID = "addeddrugid";
	private static final String DRUGNAMEID = "drugNameID";
	private static final String VOLUME = "volume";
	private static final String IS_ACTIVE = "isactive"; 
	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";
	private static final String FORMULATION = "formulation";
	private static final String DISPENSE_SIZE = "dispensesize";
	private static final String DISPENSE_SIZE_UOM = "dispensesizeuom";
	private static final String DOSE_UOM = "doseuom";
	private static final String STRENGTH = "strength";
	private static final String STRENGTH_UOM = "strengthuom";
	private static final String ITEM_ID = "itemId";
	private static final String ITEM_NAME = "itemName";
	private static final String RX_NORM = "rxnorm";
	private static final String SECONDARY_CLASSIFICATION = "secondaryClassification";
	private static final String TERTIARY_CLASSIFICATION = "tertiaryClassification";
	private static final String CSA_SCHEDULE = "csa_schedule";
	private static final String LOOK_ALIKE = "lookAlike";
	private static final String TITLE_PREVIEW = "TitlePreview";
	private static final String TITLE_STYLE = "TitleStyle";
	private static final String VARIANCE_LIMIT = "varianceLimit";
	private static final String VARIANCE_LIMIT_UNIT = "varianceLimitUnit";
	private static final String VARIANCE_LIMIT_AFTER = "varianceLimitAfter";
	private static final String VARIANCE_LIMIT_AFTER_UNIT = "varianceLimitAfterUnit";
	private static final String IS_CALCULATE = "isCalculate";
	private static final String IS_VFC = "isVfc";
	private static final String LEFT_SIDE_ID = "leftsideid"; 
	private static final String ISCALCULATE = "iscalculate";
	private static final String MODIFIED = "Modified";
	private static final String CREATED = "Created";
	private static final String DELETE = "Delete";
	
	private static final String DRUG_TO_FOOD = "drugToFood";
	private static final String DRUG_TO_ALCOHOL = "drugToAlcohol";
	private static final String DUPLICATE_THERAPY = "duplicateTherapy";
	
	private static final String ORDERENTRY_INSTR = "orderentry_instr";
	private static final String EMAR_INSTR = "emar_instr";
	private static final String PHARMACY_INSTR = "pharmacy_instr";
	private static final String INTERNAL_NOTES = "internal_notes";
	private static final String INSTRUCTIONS = "instructions";
	
	private static final String USER_ID = "userid";
	private static final String USERID = "userId";
	private static final String WHERE_ONE_ONE = " where 1=1 ";
	private static final String SEARCH_VALUE_LEFT = "searchValueLeft";
	private static final String SEARCH_VALUE_RIGHT = "searchValueRight";
	private static final String ROUTE_ID = "routeid";
	private static final String ROUTE_NAME = "routename";
	private static final String GENERIC_DRUG = "genericDrug";
	private static final String ROUTED_GENERIC_ITEMID = "routedGenericItemId";
	private static final String ASSIGNED_BRAND_ITEMID = "assigned_brand_itemid";
	private static final String CHARGE_CODE = "chargecode";
	private static final String ROUTED_DRUG_ID = "routedDrugId";
	private static final String DRUG_NAME = "drugName";
	private static final String ROUTE_CODE = "routecode";
	private static final String ROUTE_DESC = "routedesc";
	private static final String UNIT_COST = "unitCost";
	private static final String NDC10 = "ndc10";
	private static final String TXT_DRUG_ID = "txtDrugId";
	private static final String FORMULARY_ID = "formularyid";
	private static final String FACILITY_ID = "facilityid";
	private static final String ORDER_TYPE = "ordertype";
	private static final String PACK_SIZE = "packsize";
	private static final String PACKSIZE_UNIT = "packsizeunitcode";
	private static final String PACK_TYPE = "packType";
	private static final String MANUFACTURE_NAME = "manufacturerName";
	private static final String MANUFACTURE_IDENTIFIER = "manufacturerIdentifier";
	private static final String STATUS = "status";
	private static final String MARKET_END_DATE = "marketEndDate";
	private static final String PRODUCT_NAME = "productName";
	private static final String ORDER_TYPE_ID = "ordertypeid";
	private static final String ORDERTYPE_SETUP_ID = "ordertypesetupid";
	private static final String NOTES = "notes";
	private static final String AHFS_CLASS_ID = "ahfsClassID";
	private static final String AHFS_CLASS_NAME = "ahfsClassName";
	private static final String ASSO_PRODUCT_ID = "assoProductId";
	private static final String ROUTED_GENERIC_ITEM_ID = "routedgenericitemid";
	private static final String N_ROUTED_GENERIC_ITEM_ID = "nRoutedGenericItemId";
	private static final String GENERIC_DRUG_NAME = "genericDrugName";
	private static final String N_FORMULARY_ID = "nFormularyId";
	private static final String MVX_CODE = "mvxCode";
	private static final String CREATED_BY = "createdby";
	private static final String CREATED_ON = "createdon";
	private static final String MODIFIED_BY = "modifiedby";
	private static final String MODIFIED_ON = "modifiedon";
	private static final String CHARGE_TYPE_ID = "chargeTypeId";
	private static final String SELECT = " SELECT ";
	
	private static final String ITEMID = "itemid";
	private static final String ITEMNAME = "itemname";
	private static final String FORMULARYID = "formularyId";
	private static final String FORMULARY_ROUTED_GENERIC_ITEMID = "formularyRoutedGenericItemId";
	private static final String INTERACTING_ROUTED_GENERIC_ITEMID = "interactingRoutedGenericItemId";
	private static final String UPDATE = " UPDATE ";
	
	private static final String ISDEFAULT = "isDefault";
	private static final String SLIDING_SCLAE = "slidingScale";
	private static final String RIGHT_SIDE_ID="rightsideid";
	private static final String PACK_QUANTITY="packQuantity";
	private static final String ORDERTYPE="orderType";
	private static final String LOTNO = "lotno";
	private static final String LOTTYPE="lotType";
	private static final String LOTENQUIRY="lotEnquiry";
	private static final String LOCKOUT_INTERVAL_UOM="lockout_interval_uom";
	private static final String LOCKOUT_INTERVAL_DOSE="lockout_interval_dose";
	private static final String IVFORMULARYID = "ivformularyid";
	private static final String ITEMID1 = "itemID";
	private static final String ISSEARCHABLE = "issearchable";
	private static final String ISRESTRICTEDOUTSIDEOS="isrestrictedoutsideOS";
	private static final String ISRESTRICTED="isrestricted";
	private static final String ISPPD="isppd";
	private static final String ISDRUGTYPEBULK="isdrugtypebulk";
	private static final String IS_DEFAULT="isdefault";
	
	private static final String ISCHARGEABLEATDISPENSE = "ischargeableatdispense";
	private static final String ISAVAILABLEFORALL = "isavailableforall";
	private static final String ISSERVICEFORALL = "isserviceforall";
	private static final String ISTITRATIONALLOWED="isTitrationAllowed";
	private static final String ISREFRIGERATION="isRefrigeration";
	private static final String EXPIRESON="expiresOn";
	private static final String ISPCA="isPCA";
	private static final String ISIMMUNIZATION="isImmunization";
	private static final String DRUG_ALIAS = "drugAlias";
	private static final String ISSLIDINGSCALE="isSlidingScale";
	private static final String ISSINGLEDOSAGEPACK="isSingleDosagePack";
	private static final String ISRENEWINEXPIRINGTAB="isRenewInExpiringTab";
	private static final String ISIVDILUENT1="isIVDiluent";
	private static final String ISCHANGERATE="isChangeRate";
	private static final String ISADDITIVE="isAdditive";
	private static final String IP_DRUGFORMULARY_DRUGINTERACTION="ip_drugformulary_druginteraction";
	private static final String INTERMITTEN_DOSE_UOM="intermitten_dose_uom";
	private static final String INTERMITTEN_DOS="intermitten_dose";
	private static final String INTERACTINGGPI="interactingGPI";
	private static final String FREQID="freqid";
	private static final String FREQCODE="freqCode";
	private static final String FREQDESC = "freqDesc";
	private static final String FOUR_HR_LIMIT_UOM="four_hr_limit_uom";
	private static final String FOUR_HR_LIMIT="four_hr_limit";
	private static final String FORMULATIONID="formulationid";
	private static final String FORMULARYGPI="formularyGPI";
	private static final String FACILITYID="facilityId";
	private static final String EXT_MAPPING_ID="ext_mapping_id";
	private static final String EXT_MAPPING_DESC="ext_mapping_desc";
	private static final String EXPIRYDATE="expirydate";
	private static final String DUALVERIFYREQD="dualverifyreqd";
	private static final String DRUGCLASSTYPE="drugClassType";
	private static final String COST_TO_PROC="cost_to_proc";
	private static final String CHARGETYPEID="chargetypeid";
	private static final String BOLUS_LOADINGDOSE_UOM="bolus_loadingdose_uom";
	private static final String BOLUS_LOADINGDOSE="bolus_loadingdose";
	private static final String CPT_CODE_ITEMID = "cptcodeitemid";
	private static final String TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING = "ip_dispense_stockarea_formulary_qtymapping";
	private static final String TBL_IP_DRUGFORMULARY = "ip_drugformulary";
	
	public static final int NUM_0 = 0;
	public static final int NUM_1 = 1;
	public static final int NUM_2 = 2;
	public static final int NUM_3 = 3;
	public static final int NUM_100 = 100;
	
	List<TemplateForMedicationItems> mappedItemsList = null;
	
	/**
	 * @param tmplID : Template Id
	 * @return List of template with id and name
	 */
	 public List<Template> getFormularySetupDrugList(FormularySearchParam fSearchParamObj)
	 {
		String searchValue = fSearchParamObj.getSearchValue();
		String status = fSearchParamObj.getStatus();
		int recordsPerPage = fSearchParamObj.getRecordsPerPage();
		int selectedPage = fSearchParamObj.getSelectedPage();
		String sortBy = fSearchParamObj.getSortBy();
		String sortOrder = fSearchParamObj.getSortOrder();
		
		Map<String,Object> paramMap = new HashMap<>();
    	int start = selectedPage * recordsPerPage - recordsPerPage;
    	
    	StringBuilder strSQL = new StringBuilder(" "); 
    	
    	String top = " row_number() over (order by drug.modifiedon desc) as RowNumber,";
    	if("brandName".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by drug.assigned_brand_itemname "+sortOrder+") as RowNumber,";
    	}
    	else if("genericName".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by i.itemname "+sortOrder+") as RowNumber,";
    	}
    	else if("ndc".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by drug.ndc "+sortOrder+") as RowNumber,";
    	}
    	else if("upc".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by drug.upc "+sortOrder+") as RowNumber,";
    	}
    	else if("modifiedby".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by u1.ufname "+sortOrder+") as RowNumber,";
    	}
    	else if("modifiedon".equalsIgnoreCase(sortBy))
    	{
    		top = " row_number() over (order by drug.modifiedon "+sortOrder+") as RowNumber,";
    	}
    	StringBuilder strSelect = new StringBuilder(SELECT+top+" drug.id,drug.drugnameid,i.itemname,i.itemid,i1.itemname as addeddrugname,i1.itemid as addeddrugid,drug.ndc,drug.upc,drug.isactive,drug.formulation,drug.strength,drug.strengthuom,");
    	strSelect.append(" drug.isgenericavailable as genericDrug,drug.chargecode,drug.assigned_brand_itemid,drug.routedGenericItemId,drug.volume,drug.routeid,drug.routename,drug.assigned_brand_itemname,drug.iscustommed, ");
    	strSelect.append(" drug.isDisplayPkgSize,drug.isDisplayPkgType,drug.packsize,drug.packsizeunitcode,drug.packType,drug.modifiedon,drug.modifiedby ");
    	strSelect.append(" ,u1.ufname,u1.ulname,drug.genericDrugName,drug.rxnorm,drug.csa_schedule " );
    	StringBuilder strFromLeft = new StringBuilder(" FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.routedGenericItemId = i.itemid ");
    	strFromLeft.append(" and drug.delflag=0 left join ip_items i1 on drug.itemid = i1.itemid and i1.deleteflag=0 ");
    	strFromLeft.append(" inner join users u1 on drug.modifiedby = u1.uid ");
    	
    	StringBuilder strFromRight = new StringBuilder(" FROM ip_drugformulary drug ");
    	strFromRight.append(" INNER JOIN ip_items i ON drug.assigned_brand_itemid = i.itemid and drug.delflag=0 ");
    	strFromRight.append(" and drug.assigned_brand_itemid!=drug.routedGenericItemId left join ip_items i1 on drug.itemid = i1.itemid and i1.deleteflag=0 ");
    	
    	StringBuilder strWhereLeft = new StringBuilder(WHERE_ONE_ONE);
    	StringBuilder strWhereRight = new StringBuilder(WHERE_ONE_ONE);
    
    	if("1".equals(status))
		{
			strWhereLeft.append(" and  drug.isactive=1 ");
			strWhereRight.append(" and drug.isactive=1  ");
		}  
		else if("0".equals(status))
		{
			strWhereLeft.append(" and drug.isactive=0  ");
			strWhereRight.append("  and drug.isactive=0 ");
		}
		
    	if(!"".equals(searchValue) && "NDC".equalsIgnoreCase(searchValue))
    	{
    		strWhereLeft.append(" and drug.ndc like :searchValueLeft  ");
    		strWhereRight.append(" and drug.ndc like :searchValueRight  ");
    		paramMap.put(SEARCH_VALUE_LEFT, "%"+searchValue+"%");
        	paramMap.put(SEARCH_VALUE_RIGHT, "%"+searchValue+"%");   
    	}
    	else if(!"".equals(searchValue))
    	{
    		strWhereLeft.append("  and (i.itemname like :searchValueLeft or drug.assigned_brand_itemname like :searchValueLeft or i1.itemname like :searchValueLeft) ");
    		strWhereRight.append(" and (i.itemname like :searchValueRight  or drug.assigned_brand_itemname like :searchValueRight or i1.itemname like :searchValueLeft) ");
    		paramMap.put(SEARCH_VALUE_LEFT, "%"+searchValue+"%");
        	paramMap.put(SEARCH_VALUE_RIGHT, "%"+searchValue+"%");
    	}
        	
    	strSQL.append(" select * from ( ");
    	strSQL.append(strSelect.toString() + strFromLeft + strWhereLeft);
    	strSQL.append(")as q  ");
		String query  = strSQL.toString();
	   	query = "SELECT TOP  " + recordsPerPage + " * FROM  (" + query;
		query += ") AS x WHERE RowNumber > " + start;
		strSQL.setLength(0);
		strSQL.append(query);
		PharmacyHelper.generatePharmacyLog(query);
    	return getFormularySearchResult(paramMap, strSQL);
	}

	private List<Template> getFormularySearchResult(Map<String, Object> paramMap, StringBuilder strSQL) {
		return namedParameterJdbcTemplate.query(strSQL.toString(),paramMap,new ResultSetExtractor<List<Template>>(){  
			 @Override  
		     public List<Template> extractData(ResultSet rs) throws SQLException {  
					List<Template> list = new ArrayList<>();  
					ArrayList<Integer> aFormularyIdList = new ArrayList<>();
					while(rs.next()){  
						boolean bFetchGenericDrugName = false;
						if(!aFormularyIdList.contains(rs.getInt("id")))
						{
							PharmacyHelper.generatePharmacyLog("---------------inside condition------------");
							Template tmpl=new Template();  
							aFormularyIdList.add(rs.getInt("id"));
							tmpl.setDrugNameID(rs.getString(DRUG_NAME_ID));
							tmpl.setItemId(rs.getString(ADDED_DRUG_ID));
							tmpl.setItemName(rs.getString("addeddrugname"));
							tmpl.setId(rs.getInt("id"));					
							tmpl.setNdc(rs.getString("ndc"));
							tmpl.setUpc(rs.getString("upc"));
							String strActive = rs.getString(IS_ACTIVE); 
							if("1".equals(strActive))
							{
								tmpl.setStatus(ACTIVE);
							}
							else if("0".equals(strActive))  
							{
								tmpl.setStatus(INACTIVE);
							}
							
							tmpl.setFormulation(rs.getString(FORMULATION));
							tmpl.setStrength(rs.getString(STRENGTH));
							tmpl.setStrengthUom(rs.getString(STRENGTH_UOM));
							tmpl.setIsGenericDrug(rs.getString(GENERIC_DRUG));
							tmpl.setRoutedGenericItemId(rs.getString(ROUTED_GENERIC_ITEMID));
							tmpl.setVolume(rs.getString(VOLUME)); 
							tmpl.setRouteID(rs.getString(ROUTE_ID));
							tmpl.setRouteName(rs.getString(ROUTE_NAME));
							tmpl.setIscustommed(rs.getString("iscustommed"));
							
							String itemName = rs.getString(ITEM_NAME); 
							String strGenName = "";//TBD
							try{ // to be deleted
								
								if(!"1".equals(rs.getString("iscustommed")))
								{
									CPOERxSearchInterface cpoeObj = rxOrderTreeService.getGenericDrugOfADrug(rs.getString(ADDED_DRUG_ID));
									if(cpoeObj!=null)
									{
										PharmacyHelper.generatePharmacyLog("---------------inside cpoe------------");
										strGenName = Util.trimStr(cpoeObj.getDrugName());
										PharmacyHelper.generatePharmacyLog("---------------generic name from cpoe------------"+strGenName);
										if("".equals(strGenName))
										{
											strGenName = getRoutedGenericItemName(rs.getString(ROUTED_GENERIC_ITEMID));
											PharmacyHelper.generatePharmacyLog("---------------generic name from db------------"+strGenName);
										}
										String brandName = Util.trimStr(getDrugBrandName(itemName,rs.getInt(ASSIGNED_BRAND_ITEMID)));
										PharmacyHelper.generatePharmacyLog("---------------brand name from cpoe------------"+brandName);
										if("".equals(brandName))
										{
											brandName = Util.trimStr(rs.getString("assigned_brand_itemname"));
											PharmacyHelper.generatePharmacyLog("---------------brand name from db------------"+brandName);
										}
										tmpl.setGenericName(strGenName); 
										tmpl.setGenericDrugName(strGenName); 
										tmpl.setBrandName(brandName);
										tmpl.setDrugName(strGenName);
									}
								}
							}
							catch(Exception ex){
								bFetchGenericDrugName = false;
								EcwLog.AppendExceptionToLog(ex);
							}
							if("1".equals(rs.getString("iscustommed")) && !bFetchGenericDrugName)
							{
								strGenName = Util.trimStr(rs.getString("genericDrugName"));
								if("".equals(strGenName))
								{
									strGenName = getRoutedGenericItemName(rs.getString(ROUTED_GENERIC_ITEMID));
									PharmacyHelper.generatePharmacyLog("---------------generic name from db------------"+strGenName);
								}
								tmpl.setGenericName(strGenName); 
								tmpl.setGenericDrugName(strGenName); 
								tmpl.setBrandName(rs.getString("assigned_brand_itemname"));
								tmpl.setDrugName(strGenName);
							}
							tmpl.setChargecode(rs.getString(CHARGE_CODE));
							tmpl.setIsDisplayPkgSize(rs.getString("isDisplayPkgSize"));
							tmpl.setIsDisplayPkgType(rs.getString("isDisplayPkgType"));
							tmpl.setPacksize(rs.getString("packsize"));
							tmpl.setPacksizeunitcode(rs.getString("packsizeunitcode"));
							tmpl.setPackType(rs.getString("packType"));
							tmpl.setRxnorm(rs.getString("rxnorm"));
							tmpl.setCsa_schedule(rs.getString(CSA_SCHEDULE));
							try
							{
								tmpl.setModifiedby(rs.getString("modifiedby"));
								tmpl.setModifiedbyusername(Util.trimStr(rs.getString("ufname")+" "+Util.trimStr(rs.getString("ulname"))));
			         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString("modifiedon"),rs.getInt("modifiedby")));
			         			
							}catch(RuntimeException | ParseException ex ){
								 EcwLog.AppendExceptionToLog(ex);
							}
							list.add(tmpl);
						} 
					}  
					aFormularyIdList.clear();
					return list;  
				}
		    });  
	}
	 
	 public List<Template> getFormularySetupDrugListForComponent(FormularySearchParam fSearchParamObj)
	 {
		 String searchBy = fSearchParamObj.getSearchBy();
		 String searchValue = fSearchParamObj.getSearchValue();
		 int recordsPerPage = fSearchParamObj.getRecordsPerPage();
		 int selectedPage = fSearchParamObj.getSelectedPage();
			
		 Map<String,Object> paramMap = new HashMap<>();
		 List<Template> tmplList = null;
    	int start = selectedPage * recordsPerPage - recordsPerPage;
    	
    	StringBuilder strSQL = new StringBuilder(" "); 
    	
    	String top = " row_number() over (order by itemid) as RowNumber,";
    	String strSelect = " SELECT distinct i.itemname,i.itemid,drug.routedGenericItemId ";
    	StringBuilder strFromLeft = new StringBuilder(" FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.routedGenericItemId = i.itemid and drug.delflag=0  and drug.isactive=1");
    	
    	String strWhereLeft = WHERE_ONE_ONE;
    
    	if(!"".equals(searchValue) && "name".equalsIgnoreCase(searchBy))
    	{
    		strWhereLeft += " and ( i.itemname like :searchValueLeft or drug.assigned_brand_itemname like :searchValueLeft)  ";
    		paramMap.put(SEARCH_VALUE_LEFT, "%"+searchValue+"%");
    	}
        	
    	strSQL.append(SELECT+top+" q.itemname,q.itemid,q.routedGenericItemId from (");
    	strSQL.append(strSelect + strFromLeft + strWhereLeft);
    	strSQL.append(")as q  ");
    	
		String query  = strSQL.toString();
	   	query = "SELECT TOP " + recordsPerPage + " * FROM (" + query;
		query += ") AS x WHERE RowNumber > " + start;
		strSQL.setLength(0);
		strSQL.append(query);
    	
		tmplList = namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Template>(){
			@Override
			public Template mapRow(ResultSet rs, int arg1) throws SQLException {
				Template tmpl=new Template();
				tmpl.setRoutedGenericItemId(rs.getString("routedGenericItemId"));
				tmpl.setItemId(rs.getString(ITEMID));
				tmpl.setItemName(rs.getString(ITEMNAME));
				tmpl.setDrugName(rs.getString(ITEMNAME));
				return tmpl;
			}
        });
        return tmplList;
	}
	
	public List<TemplateForDrug> getDrugDetails(final String strDrugId){
		Map<String,Object> paramMap = new HashMap<>();
        List<TemplateForDispenseUOM> dispenseUOM = null;
        List<TemplateForAhfs> ahfsList = null;
        
        StringBuilder sql = new StringBuilder();
        int nformularyId = -1;
        int nRoutedGenericItemId = 0;
        isIVDiluent = "0";
    	if(!"".equals(strDrugId))
    	{ 
    		nformularyId = Integer.parseInt(strDrugId);
    	}
    	PharmacyHelper.generatePharmacyLog("getDrugDetails is called");
    	nRoutedGenericItemId = getRoutedGenericItemId(nformularyId);
    	PharmacyHelper.generatePharmacyLog("nRoutedGenericItemId "+nRoutedGenericItemId);
     	sql.append("SELECT facilityid FROM ip_drugformulary_facilities WHERE formularyid=:txtDrugId and delflag=0");
    	paramMap.put(TXT_DRUG_ID,strDrugId);
    	paramMap.put(FORMULARY_ID,strDrugId);
    	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	final  List<TemplateForFacilities> facilityList  = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForFacilities>() {
			@Override
			public TemplateForFacilities mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForFacilities tmpl=new TemplateForFacilities();
				tmpl.setFacilityId(rs.getString(FACILITY_ID));
				return tmpl; 
			} 
    	});
    	
    	sql.setLength(0);
    	//to check whether drug formulary is diluent or not
    	sql.append(" select isIVDiluent from ip_drugformulary_common_settings where routedGenericItemId=:nRoutedGenericItemId and delflag=0 and isIVDiluent=1 ");
    	List<Map<String, Object>> aSize = null;
        aSize = namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap);
    	if(!aSize.isEmpty())
    	{
    		isIVDiluent = "1";
    	}
    	
    	sql.setLength(0);
    	sql.append("SELECT serviceid FROM ip_drugformulary_regservicetype WHERE formularyid=:txtDrugId and delflag=0");
    	final List<TemplateForServiceTypes> serviceTypeList  = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForServiceTypes>() {
			@Override
			public TemplateForServiceTypes mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForServiceTypes tmpl=new TemplateForServiceTypes();
				tmpl.setServiceTypeId(rs.getString("serviceid"));
				return tmpl;
			}
    	});
    	
    	//dispense uom list from external 
    	sql.setLength(0);
    	sql.append("SELECT id,dispenseuom FROM ip_drugformulary_dispenseuom_external WHERE formularyid=:txtDrugId and delflag=0");
    	dispenseUOM = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForDispenseUOM>() {
			@Override
			public TemplateForDispenseUOM mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDispenseUOM tmpl=new TemplateForDispenseUOM();
				tmpl.setId(rs.getString("id"));
				tmpl.setDispenseuom(rs.getString("dispenseuom"));
				return tmpl;
			}
    	});
    	
//        	final String drugAliasData = new com.google.gson.Gson().toJson(drugAliasList);//not in use
    	final String dispenseUOMData = new com.google.gson.Gson().toJson(dispenseUOM);
    	
    	
    	//order type setup data
    	 sql.setLength(0);
    	 sql.append(" SELECT id,formularyid,ordertype,isnonbillable as isnonbill,autostop as autostop1,autostopuom as autostopuom1,chargetypeid,createdby as createdby1");
    	 sql.append(",createdon as createdon1,modifiedon as modifiedon1,modifiedby as modifiedby1,isslidingscale from ip_drugformulary_OrderTypeSetup where formularyid=:formularyid and delflag=0");
     	
    	 final List<TemplateForOTS> otsList = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForOTS>() {
			@Override
			public TemplateForOTS mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForOTS tmpl=new TemplateForOTS();
				Map<String,Object> paramMapOTS = new HashMap<>();
				tmpl.setOrderType(rs.getString(ORDER_TYPE));
				tmpl.setNonBillable(rs.getString("isnonbill"));
				tmpl.setAutoStop(rs.getString("autostop1"));
				tmpl.setAutoStopUOM(rs.getString("autostopuom1"));
				tmpl.setSlidingscale(rs.getString("isslidingscale"));
				
				StringBuilder strSql = new StringBuilder("select ots.routeid,route.routecode,route.routedesc,ots.ordertypeid from ip_drugformulary_OTS_Route ots ");
				strSql.append(" inner join ip_drugformulary_routes route on ots.routeid = route.id ");
				strSql.append(" where ots.delflag=0 and route.deleteflag=0 and ots.formularyid=:formularyid and ots.ordertypeid=:ordertypeid ");
				paramMapOTS.put(FORMULARY_ID,strDrugId);
				paramMapOTS.put(ORDER_TYPE_ID,rs.getString("id"));
				List<TemplateRoutes> routeList = namedParameterJdbcTemplate.query(strSql.toString(),paramMapOTS, new RowMapper<TemplateRoutes>(){
					@Override
					public TemplateRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplateRoutes tmpl=new TemplateRoutes();
						tmpl.setRouteID(rs.getString(ROUTE_ID));
						tmpl.setRouteName(rs.getString(ROUTE_CODE));
						tmpl.setRouteDesc(rs.getString(ROUTE_DESC));
						return tmpl;
					}
				});
				
				String strChgType = rs.getString(CHARGETYPEID);
            	if("0".equals(strChgType))
            	{
            		strChgType = "";
            	}
            	final String strRouteList = new com.google.gson.Gson().toJson(routeList);
            	tmpl.setRouteList(strRouteList);
				tmpl.setChargeTypeId(strChgType);
				tmpl.setId(rs.getString("id"));
				tmpl.setFormularyid(rs.getString(FORMULARY_ID)); 
				try
				{
         			tmpl.setCreatedby(rs.getString("createdby1"));
					tmpl.setModifiedby(rs.getString("modifiedby1"));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString("createdon1"),rs.getInt("createdby1")));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString("modifiedon1"),rs.getInt("modifiedby1")));
         			
				}catch(RuntimeException | ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
				return tmpl;
			}
     	});
     	
     	//ahfs classification
     	//order type setup data
    	sql.setLength(0);
   	    sql.append("SELECT id,formularyid,ahfsClassID,ahfsClassName,createdby,createdon,modifiedon,modifiedby from ip_drugformulary_ahfs_classification where formularyid=:formularyid and delflag=0");
   	    ahfsList = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForAhfs>() {
			@Override
			public TemplateForAhfs mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForAhfs tmpl=new TemplateForAhfs();
				tmpl.setAhfsClassID(rs.getString(AHFS_CLASS_ID));
				tmpl.setAhfsClassName(rs.getString(AHFS_CLASS_NAME));
				tmpl.setId(rs.getString("id"));
				return tmpl;
			}
    	});
   	    //mapped drug list
   	    sql.setLength(0);
   	    sql.append(" select a.mapped_itemid,b.itemname,a.isPrimary,a.csa_schedule, ");
   	    sql.append(" a.drugNameID,a.genericDrugNameID,a.routedDrugID,a.routedGenericItemId,a.routeID ");
   		sql.append(" from ip_drugformulary_med_mapping a inner join ip_items b on a.mapped_itemid = b.itemid ");
   		sql.append(" where a.formularyid=:formularyid and b.deleteFlag = 0 and a.delflag=0 ");
   		
	   	 mappedItemsList = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForMedicationItems>() {
				@Override
				public TemplateForMedicationItems mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForMedicationItems tmpl=new TemplateForMedicationItems();
					tmpl.setItemId(rs.getString("mapped_itemid"));
					tmpl.setItemName(rs.getString("itemname"));
					tmpl.setIsPrimary(rs.getString("isPrimary"));
					tmpl.setCsa_schedule(rs.getString("csa_schedule"));					
					tmpl.setDrugNameID(rs.getString("drugNameID"));
					tmpl.setGenericDrugNameID(rs.getString("genericDrugNameID"));
					tmpl.setRoutedDrugID(rs.getString("routedDrugID"));
					tmpl.setRoutedGenericItemId(rs.getString("routedGenericItemId"));
					tmpl.setRouteID(rs.getString("routeID"));
					return tmpl;
				}
	 	});
   	    final List<TemplateForNotes> notesList = loadCommonSettingsForNotes(getRoutedGenericItemId(nformularyId),nformularyId);
   	 
   	    final String ahfsData = new com.google.gson.Gson().toJson(ahfsList); 
     	
   	    return getFormularyData(paramMap, facilityList, serviceTypeList, dispenseUOMData, otsList, notesList,
				ahfsData);
	}

	private List<TemplateForDrug> getFormularyData(Map<String, Object> paramMap,
			final List<TemplateForFacilities> facilityList, final List<TemplateForServiceTypes> serviceTypeList,
			final String dispenseUOMData, final List<TemplateForOTS> otsList, final List<TemplateForNotes> notesList,
			final String ahfsData) {
		StringBuilder strQB = new StringBuilder("SELECT ipd.id,ipd.DrugNameID,ipd.itemId,ipd.ddid,ipd.gpi,ipd.rxnorm,ipd.genericDrugItemID,ipd.maxDose24Hours,ipd.maxDose24HoursUOM,");
		strQB.append("ipd.primaryClassification,ipd.secondaryClassification,ipd.tertiaryClassification,ipd.medIdCode,ipd.isgenericavailable,ipd.isactive,");
		strQB.append("ipd.formulation,ipd.strength,ipd.strengthuom,ipd.volume,ipd.volumeuom,ipd.chargecode,");
		strQB.append("ipd.csa_schedule,ipd.ndc,ipd.upc,ipd.stockarea,");
		strQB.append("ipd.isavailableforall,ipd.isserviceforall,ipd.isMedication,ipd.isTPN,ipd.isIV,ipd.isIVPGB,");
		strQB.append("ipd.createdby,ipd.createdon,ipd.modifiedby,ipd.modifiedon,ipd.delflag,ipd.userId,ipd.notes,ipd.genericDrugName,");
		strQB.append("ipd.genericDrugNameID,ipd.isSingleDosagePackage,ipd.HCPCSCodeRange,");
		strQB.append("ipd.HCPCSCodeUnit,ipd.HCPCSCodeType,ipd.isAllowToAdminWithoutApproval,ipi.itemName,ipd.dispenseForm,");
		strQB.append("ipd.dispenseSize,ipd.dispenseSizeUom,ipd.routedgenericitemid,ipd.isppd,ipd.ahfsClassification, ");
		strQB.append("ipd.routeid,ipd.routeName,ipd.doseUom,ipd.doseSize,ipd.isCalculate,ipd.cptcodeitemid,ipd.ahfsClassId,ipd.assigned_brand_itemid,ip1.itemname as routedgenericitemidname,ipd.primaryppid, ");
		strQB.append(" ipd.isdrugtypebulk_formulary,ipd.ischargeableatdispense_formulary,ip1.routeddrugid,ipd.iscustommed, ");
		strQB.append(" ipd.isDisplayPkgSize,ipd.isDisplayPkgType,ipd.packsize,ipd.packsizeunitcode,ipd.packType,ipd.assigned_brand_itemname ");
		strQB.append("FROM ip_drugformulary ipd inner join ip_items ipi on ipd.itemId = ipi.itemid  ");
		strQB.append(" inner join ip_items ip1 on ipd.routedgenericitemid = ip1.itemid and ip1.deleteflag=0 ");
		strQB.append(" where ipd.id=:txtDrugId and ipd.delflag=0 ");
		return namedParameterJdbcTemplate.query(strQB.toString(), paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrug tmpl=new TemplateForDrug();
				boolean isCustomMeds = false;
				String strGenName = "";
				tmpl.setIsIVDiluent(isIVDiluent);//this is required to update volumn on select of associated product (primary)
				tmpl.setId(rs.getString("id"));
				tmpl.setDrugNameID(rs.getString(DRUG_NAME_ID));
				tmpl.setItemId(rs.getString(ITEM_ID));
				tmpl.setDdid(rs.getString("ddid"));
				tmpl.setGpi(rs.getString("gpi"));
				tmpl.setRxnorm(rs.getString(RX_NORM));
				tmpl.setGenericDrugItemID(rs.getString("genericDrugItemID"));
				tmpl.setMaxDose24Hours(rs.getString("maxDose24Hours"));
				tmpl.setMaxDose24HoursUOM(rs.getString("maxDose24HoursUOM"));
				tmpl.setPrimaryClassification(rs.getString("primaryClassification"));
				tmpl.setSecondaryClassification(rs.getString(SECONDARY_CLASSIFICATION));
				tmpl.setTertiaryClassification(rs.getString(TERTIARY_CLASSIFICATION));
				tmpl.setMedIdCode(rs.getString("medIdCode"));
				tmpl.setIsgenericavailable(rs.getString("isgenericavailable"));
				tmpl.setIsDisplayPkgSize(rs.getString("isDisplayPkgSize"));
				tmpl.setIsDisplayPkgType(rs.getString("isDisplayPkgType"));
				tmpl.setPacksize(rs.getString("packsize"));
				tmpl.setPacksizeunitcode(rs.getString("packsizeunitcode"));
				tmpl.setPackType(rs.getString("packType"));
				tmpl.setIsCalculate(rs.getString(IS_CALCULATE)); 
				tmpl.setIsactive(rs.getString(IS_ACTIVE));
				tmpl.setFormulation(rs.getString(FORMULATION));
				tmpl.setStrength(rs.getString(STRENGTH));
				tmpl.setStrengthuom(rs.getString(STRENGTH_UOM));
				tmpl.setVolume(rs.getString(VOLUME));
				tmpl.setVolumeuom(rs.getString("volumeuom"));
				tmpl.setChargecode(rs.getString(CHARGE_CODE));
				tmpl.setCsa_schedule(rs.getString(CSA_SCHEDULE));
				tmpl.setNdc(rs.getString("ndc"));
				tmpl.setUpc(rs.getString("upc"));
				tmpl.setStockarea(rs.getString("stockarea"));
				tmpl.setIsavailableforall(rs.getString(ISAVAILABLEFORALL));
				tmpl.setIsserviceforall(rs.getString(ISSERVICEFORALL));
				tmpl.setIsMedication(rs.getString("isMedication"));
				tmpl.setIsTPN(rs.getString("isTPN"));
				tmpl.setIsIV(rs.getString("isIV"));
				tmpl.setIsIVPGB(rs.getString("isIVPGB"));
				tmpl.setDelflag(rs.getString(DELFLAG));
				tmpl.setUserId(rs.getString(USERID));
				tmpl.setNotes(rs.getString(NOTES));
//					tmpl.setDrugAliasArr(drugAliasData);//not in use
				tmpl.setOtsData(otsList);
				tmpl.setGenericDrugNameID(rs.getString("genericDrugNameID"));
				tmpl.setFacilityList(facilityList); 
				tmpl.setServiceTypeList(serviceTypeList);
				tmpl.setisSingleDosagePackage(rs.getString("isSingleDosagePackage"));
				tmpl.setHcpcsCodeRange(rs.getString("HCPCSCodeRange"));
				tmpl.setHcpcsCodeUnit(Util.trimStr(rs.getString("HCPCSCodeUnit")).equals("0")?"":Util.trimStr(rs.getString("HCPCSCodeUnit")));
				tmpl.setHcpcsCodeType(rs.getString("HCPCSCodeType"));
				tmpl.setIsAllowToAdminWithoutApproval(rs.getString("isAllowToAdminWithoutApproval"));
				tmpl.setDispenseForm(rs.getString("dispenseForm"));
				tmpl.setDispenseSize(RxUtil.preSanitizeStringAsDouble(rs.getString("dispenseSize"),true));
				tmpl.setDispenseSizeUom(rs.getString("dispenseSizeUom"));
				tmpl.setRoutedGenericItemId(rs.getString(ROUTED_GENERIC_ITEM_ID));
				tmpl.setIsppd(rs.getString(ISPPD));
				tmpl.setAhfsClassificationList(ahfsData);//list of all saved in ahfs classification
				tmpl.setMappedMedicationData(mappedItemsList);
				tmpl.setAhfsClassification(rs.getString("ahfsClassification"));
				tmpl.setAhfsClassID(rs.getString("ahfsClassId"));
				tmpl.setDispenseuomlist(dispenseUOMData); 
				tmpl.setRouteID(rs.getString(ROUTE_ID));
				tmpl.setRouteName(rs.getString("routeName"));
				tmpl.setDoseUom(rs.getString("doseUom"));
				tmpl.setDoseSize(RxUtil.preSanitizeStringAsDouble(rs.getString("doseSize"),true));
				if("1".equals(rs.getString("iscustommed")))
				{
					isCustomMeds = true;
				} 
				//assigned brand name
		 		String routedgenericitemidname = rs.getString("routedgenericitemidname");
		 		PharmacyHelper.generatePharmacyLog("routedgenericitemidname "+routedgenericitemidname);
		 		String strSavedAssignedBrandName = "";
				if(!isCustomMeds)
				{
					String strDrugData = MSClinicalService.getDosesDataFromMSClinicalForFormulary(rs.getString("gpi"),rs.getString(ROUTE_ID),"GPI",rs.getString(DRUG_NAME_ID));
			 		tmpl.setDrugDataFromCloud(strDrugData);
			 		int brandNameId = rs.getInt(ASSIGNED_BRAND_ITEMID);//nisar
			 		getBrandNamesData(tmpl, routedgenericitemidname, brandNameId,strSavedAssignedBrandName);//this method is used to set list of brand names coming from ms-clinical
				} 
				if(isCustomMeds) 
				{
					strGenName = Util.trimStr(rs.getString("genericDrugName"));
				}
				strSavedAssignedBrandName = rs.getString("assigned_brand_itemname");
				
				try{
					if(!isCustomMeds)
					{
						CPOERxSearchInterface cpoeObj = rxOrderTreeService.getGenericDrugOfADrug(rs.getString("itemId"));
						if(cpoeObj!=null)
						{
							PharmacyHelper.generatePharmacyLog("---------------inside cpoe------------");
							strGenName = Util.trimStr(cpoeObj.getDrugName());
							PharmacyHelper.generatePharmacyLog("---------------generic name from cpoe------------"+strGenName);
							if("".equals(strGenName))
							{
								strGenName = getRoutedGenericItemName(rs.getString("routedgenericitemid"));
								PharmacyHelper.generatePharmacyLog("---------------generic name from db------------"+strGenName);
							}
						}
					}
				}
				catch(Exception ex){
					EcwLog.AppendExceptionToLog(ex);
				}
				tmpl.setGenericDrugName(strGenName);
				tmpl.setAssigned_brand_itemname(strSavedAssignedBrandName);
				tmpl.setItemName(rs.getString("itemName"));
		 		tmpl.setNotesData(notesList);
		 		tmpl.setCptcodeitemid(rs.getString(CPT_CODE_ITEMID));
		 		try
				{
		 			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
		 			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
		 			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
		 			
				}
				catch(ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
		 		tmpl.setPrimaryppid(rs.getString("primaryppid"));
		 		tmpl.setIsdrugtypebulk(rs.getString("isdrugtypebulk_formulary"));
		 		tmpl.setIschargeableatdispense(rs.getString("ischargeableatdispense_formulary"));
		 		tmpl.setRoutedDrugId(rs.getString("routeddrugid"));
		 		tmpl.setIscustommed(rs.getString("iscustommed"));
				return tmpl;
			}
		});
	}
	private String getBrandNamesData(TemplateForDrug tmpl, String routedgenericitemidname, int brandNameId,
			String strSavedAssignedBrandName) {
		if(!"".equals(routedgenericitemidname) && routedgenericitemidname!=null){
			List<inpatientWeb.Global.ecw.ambulatory.json.JSONObject> listobj = rxOrderTreeService.getAssignedBrandNameForFormulary(routedgenericitemidname);
            ArrayList<FormularyBrands> brandNames =  new ArrayList<>();
            try{
	            for(inpatientWeb.Global.ecw.ambulatory.json.JSONObject cpoeSearchObj : listobj){ 
	          
	                FormularyBrands brandObj = new FormularyBrands();
	                if(brandNameId > 0 && brandNameId == cpoeSearchObj.getInt("itemID"))
					{
	                	strSavedAssignedBrandName = cpoeSearchObj.getString("drugName");
					}
	                 brandObj.setItemid(cpoeSearchObj.getInt("itemID"));
	                 brandObj.setItemName(cpoeSearchObj.getString("drugName"));
	                 brandNames.add(brandObj);
	          
	            }
            }catch(JSONException ex){
            	EcwLog.AppendExceptionToLog(ex);
            }
    		tmpl.setBrandNames(brandNames);
    		PharmacyHelper.generatePharmacyLog("brandNames "+brandNames);
		}
		return strSavedAssignedBrandName;
	}
	
	//get Associated Product details
	public List<TemplateForAssoProducts> getAssociatedProductDetails(final String strDrugId){
		Map<String,Object> paramMap = new HashMap<>();
        StringBuilder sqlB = new StringBuilder();
    	sqlB.append("select ida.id,ida.formularyid,ida.ndc,ida.upc,ida.ppid,ida.packsize as packsize1,ida.packsizeunitcode as packsizeunitcode1,ida.packQuantity as packQuantity1,ida.packType as packType1, ");
    	sqlB.append(" ida.awp,ida.manufacturerName as manufacturerName1,ida.manufacturerIdentifier as manufacturerIdentifier1,ida.mvxCode as mvxCode1,ida.cost_to_proc as cost_to_proc1,ida.unitCost,");
    	sqlB.append(" ida.status as status1,ida.isPrimary as isPrimary1,ida.marketEndDate as marketEndDate1,ida.createdby,ida.createdon,ida.modifiedby,ida.modifiedon,ida.delflag,ida.userId,ida.notes,");
    	sqlB.append(" ida.itemId,i.itemName as drugName,ida.routedDrugId,ida.productName as productName1,ida.isvfc,ida.isSingleDosagePack as isSingleDosagePack1,ida.awup,ida.ndc10 ");
    	sqlB.append(" from ip_drugformulary_associatedProducts ida inner join ip_items i on ida.itemid = i.itemid where ida.formularyid=:txtDrugId and ida.delflag=0 and i.deleteflag=0 order by ida.id ");
    	paramMap.put(TXT_DRUG_ID,strDrugId);
    	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForAssoProducts>() {
			@Override
			public TemplateForAssoProducts mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForAssoProducts tmpl=new TemplateForAssoProducts();
				tmpl.setId(rs.getInt("id"));
				tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
				tmpl.setNdc(rs.getString("ndc"));
				tmpl.setUpc(rs.getString("upc"));
				tmpl.setPpid(rs.getString("ppid"));
				tmpl.setPackSize(rs.getString("packsize1"));
				tmpl.setPackSizeUnitCode(rs.getString("packsizeunitcode1"));
				tmpl.setPackQuantity(rs.getString("packQuantity1")); 
				tmpl.setPackType(rs.getString("packType1"));
				tmpl.setAwp(rs.getString("awp"));
				tmpl.setAwup(rs.getString("awup"));
				tmpl.setNdc10(rs.getString(NDC10));
				tmpl.setManufacturerName(rs.getString("manufacturerName1"));
				tmpl.setManufacturerIdentifier(rs.getString("manufacturerIdentifier1"));
				tmpl.setMvxCode(rs.getString("mvxCode1"));
				tmpl.setCost_to_proc(rs.getDouble("cost_to_proc1"));
				tmpl.setUnitcost(rs.getDouble(UNIT_COST));
				tmpl.setStatus(rs.getInt("status1"));
				tmpl.setIsprimary(rs.getInt("isPrimary1"));
				tmpl.setMarketEndDate(rs.getString("marketEndDate1"));
				tmpl.setUserId(rs.getInt(USERID));
				tmpl.setNotes(rs.getString(NOTES));
				tmpl.setItemId(rs.getString(ITEM_ID));
				tmpl.setDrugName(rs.getString(DRUG_NAME));
				tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
				tmpl.setProductName(rs.getString("productName1"));
				tmpl.setIsVfc(rs.getInt(IS_VFC));
				tmpl.setIsSingleDosagePack(rs.getInt("isSingleDosagePack1"));
				try
				{
         			tmpl.setCreatedby(rs.getInt(CREATED_BY));
					tmpl.setModifiedby(rs.getInt(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
					tmplAssoProdLotList = getAssoProductLotDetails(rs.getInt(FORMULARY_ID), rs.getInt("id"));
				}
				catch(RuntimeException | ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
				final String lotDetails = new com.google.gson.Gson().toJson(tmplAssoProdLotList);
				tmpl.setLotDetails(lotDetails);
				return tmpl;
			} 
    	});
	}
	
	//get associated product by line wise
	public List<TemplateForAssoProducts> getAssociatedProductDetailsById(final String strDrugId,final String strId){
		Map<String,Object> paramMap = new HashMap<>();
	
        StringBuilder sqlB = new StringBuilder();
    	sqlB.append("select ida.id,ida.formularyid,ida.ndc,ida.upc,ida.ppid,ida.packsize as packsize1,ida.packsizeunitcode as packsizeunitcode1,ida.packQuantity as packQuantity1,ida.packType as packType1, ");
    	sqlB.append(" ida.awp,ida.manufacturerName as manufacturerName1,ida.manufacturerIdentifier as manufacturerIdentifier1,ida.mvxCode as mvxCode1,ida.cost_to_proc as cost_to_proc1,ida.unitCost,");
    	sqlB.append(" ida.status as status1,ida.isPrimary as isPrimary1,ida.marketEndDate as marketEndDate1,ida.createdby,ida.createdon,ida.modifiedby,ida.modifiedon,ida.delflag,ida.userId,ida.notes,");
    	sqlB.append(" ida.itemId,i.itemName as drugName,ida.routedDrugId,ida.productName as productName1,ida.isvfc,ida.isSingleDosagePack as isSingleDosagePack1,ida.awup,ida.ndc10 ");
    	sqlB.append(" from ip_drugformulary_associatedProducts ida inner join ip_items i on ida.itemid = i.itemid where ida.formularyid=:txtDrugId and ida.id=:txtAssoProductId and ida.delflag=0 and i.deleteflag=0");
    	paramMap.put(TXT_DRUG_ID,strDrugId);
    	paramMap.put("txtAssoProductId",strId);
    	
    	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForAssoProducts>() {
			@Override
			public TemplateForAssoProducts mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForAssoProducts tmpl=new TemplateForAssoProducts();
				tmpl.setId(rs.getInt("id"));
				tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
				tmpl.setNdc(rs.getString("ndc"));
				tmpl.setUpc(rs.getString("upc"));
				tmpl.setPpid(rs.getString("ppid"));
				tmpl.setPackSize(rs.getString("packsize1"));
				tmpl.setPackSizeUnitCode(rs.getString("packsizeunitcode1"));
				tmpl.setPackQuantity(rs.getString("packQuantity1")); 
				tmpl.setPackType(rs.getString("packType1"));
				tmpl.setAwp(rs.getString("awp"));
				tmpl.setAwup(rs.getString("awup"));
				tmpl.setNdc10(rs.getString(NDC10));
				tmpl.setManufacturerName(rs.getString("manufacturerName1"));
				tmpl.setManufacturerIdentifier(rs.getString("manufacturerIdentifier1"));
				tmpl.setMvxCode(rs.getString("mvxCode1"));
				tmpl.setCost_to_proc(rs.getDouble("cost_to_proc1"));
				tmpl.setUnitcost(rs.getDouble(UNIT_COST));
				tmpl.setStatus(rs.getInt("status1"));
				tmpl.setIsprimary(rs.getInt("isPrimary1"));
				tmpl.setMarketEndDate(rs.getString("marketEndDate1"));
				tmpl.setUserId(rs.getInt(USERID));
				tmpl.setNotes(rs.getString(NOTES));
				tmpl.setItemId(rs.getString(ITEM_ID));
				tmpl.setDrugName(rs.getString(DRUG_NAME));
				tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
				tmpl.setProductName(rs.getString("productName1"));
				tmpl.setIsVfc(rs.getInt("isvfc"));
				tmpl.setIsSingleDosagePack(rs.getInt("isSingleDosagePack1"));
				try
				{
         			tmpl.setCreatedby(rs.getInt(CREATED_BY));
					tmpl.setModifiedby(rs.getInt(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
					tmplAssoProdLotList = getAssoProductLotDetails(rs.getInt(FORMULARY_ID), rs.getInt("id"));
				}
				catch(RuntimeException | ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
				final String lotDetails = new com.google.gson.Gson().toJson(tmplAssoProdLotList);
				tmpl.setLotDetails(lotDetails);
				return tmpl;
			} 
    	});
	}

	
	//get Associated Product details - ndc code
	public List<TemplateForAssoProducts> getAssociatedProductDetailsByNDC(final String strNDC){
		Map<String,Object> paramMap = new HashMap<>();
        List<TemplateForAssoProducts> tmplAssoProductsList = null;
        int nFormularyId = 0;
        
        StringBuilder sqlB = new StringBuilder();
    	sqlB.append("select id from ip_drugformulary where ndc=:ndc and delflag=0 ");
		paramMap.put("ndc", strNDC);
		List<Template> tmplList = namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<Template>(){
			@Override
			public Template mapRow(ResultSet rs, int arg1) throws SQLException {
				Template tmpl=new Template();
				tmpl.setId(rs.getInt("id"));
				return tmpl;
			}
		});
		Iterator<Template> it = tmplList.iterator();
		if(it.hasNext())
		{
			Template t1 = it.next();
			nFormularyId = t1.getId();
		}
		if(nFormularyId > 0)
		{
			sqlB.setLength(0); 
			sqlB.append("select ida.id,ida.formularyid,ida.ndc,ida.ppid,ida.packsize,ida.packsizeunitcode,ida.packQuantity,ida.packType, ");
        	sqlB.append(" ida.awp,ida.manufacturerName,ida.manufacturerIdentifier,ida.mvxCode,ida.cost_to_proc,ida.unitCost,");
        	sqlB.append(" ida.status,ida.isPrimary,ida.marketEndDate,ida.createdby,ida.createdon,ida.modifiedby,ida.modifiedon,ida.delflag,");
        	sqlB.append("ida.userId,ida.notes,ida.itemId,i.itemName as drugName,ida.routedDrugId,ida.rxnorm,ida.upc,ida.awup,ida.ndc10 ");
        	sqlB.append(" from ip_drugformulary_associatedProducts ida inner join ip_items i on ida.itemid = i.itemid ");
        	sqlB.append(" inner join ip_drugformulary drug on drug.id = ida.formularyid and drug.isActive=1 and drug.delflag=0 ");
        	sqlB.append(" where ida.formularyid=:formularyid and ida.status=1 and ida.delflag=0 and i.deleteflag=0");
        	paramMap.put(FORMULARY_ID,nFormularyId);
        	tmplAssoProductsList = namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForAssoProducts>() {
				@Override
				public TemplateForAssoProducts mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProducts tmpl=new TemplateForAssoProducts();
					tmpl.setId(rs.getInt("id"));
					tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
					tmpl.setNdc(rs.getString("ndc"));
					tmpl.setPpid(rs.getString("ppid"));
					tmpl.setPackSize(rs.getString(PACK_SIZE));
					tmpl.setPackSizeUnitCode(rs.getString(PACKSIZE_UNIT));
					tmpl.setPackQuantity(rs.getString(PACK_QUANTITY)); 
					tmpl.setPackType(rs.getString(PACK_TYPE));
					tmpl.setAwp(rs.getString("awp"));
					tmpl.setAwup(rs.getString("awup"));
					tmpl.setNdc10(rs.getString(NDC10));
					tmpl.setManufacturerName(rs.getString(MANUFACTURE_NAME));
					tmpl.setManufacturerIdentifier(rs.getString(MANUFACTURE_IDENTIFIER));
					tmpl.setMvxCode(rs.getString(MVX_CODE));
					tmpl.setCost_to_proc(rs.getDouble(COST_TO_PROC));
					tmpl.setUnitcost(rs.getDouble(UNIT_COST));
					tmpl.setStatus(rs.getInt(STATUS));
					tmpl.setIsprimary(rs.getInt("isPrimary"));
					tmpl.setMarketEndDate(rs.getString(MARKET_END_DATE));
					tmpl.setUserId(rs.getInt(USERID));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setItemId(rs.getString(ITEM_ID));
					tmpl.setDrugName(rs.getString(DRUG_NAME));
					tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
					tmpl.setRxnorm(rs.getString(RX_NORM));
					tmpl.setUpc(rs.getString("upc"));
					try
					{
						tmpl.setCreatedby(rs.getInt(CREATED_BY));
						tmpl.setModifiedby(rs.getInt(MODIFIED_BY));
	         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
	         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
	         			
						tmplAssoProdLotList = getAssoProductLotDetails(rs.getInt(FORMULARY_ID), rs.getInt("id"));
					}
					catch (RuntimeException | ParseException e) {
			            EcwLog.AppendExceptionToLog(e);
			        }
					final String lotDetails = new com.google.gson.Gson().toJson(tmplAssoProdLotList);
					tmpl.setLotDetails(lotDetails);
					return tmpl; 
				} 
        	});
		}
		
        return tmplAssoProductsList;
	}
	
	
	
	
	//to get pre-requisite vitals details
	public List<TemplateForPreVitals> getPreRequisiteVitalsDetails(int nformularyId,final String routedGenericItemId){
		Map<String,Object> paramMap = new HashMap<>();
        StringBuilder strQ = new StringBuilder();
    	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,routedGenericItemId);
    	paramMap.put(N_FORMULARY_ID,nformularyId);
    	//CHECK FOR data is present for routed generic item id in common setting table
		strQ.setLength(0);
		strQ.append(" SELECT pre.id,pre.leftsideid,pre.rightsideid,it.itemname,pre.createdby,pre.modifiedby,pre.createdon,pre.modifiedon ");
		strQ.append(" FROM ip_formulary_prerequisite pre inner join ip_items it on pre.rightsideid = it.itemid ");
		strQ.append(" WHERE pre.routedgenericitemid=:nRoutedGenericItemId and pre.delflag=0 and it.deleteflag=0 ");
    	return namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<TemplateForPreVitals>() {
			@Override
			public TemplateForPreVitals mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForPreVitals tmpl=new TemplateForPreVitals();
				tmpl.setId(rs.getString("id"));
				tmpl.setLeftsideid(rs.getString(LEFT_SIDE_ID));
				tmpl.setRightsideid(rs.getString(RIGHT_SIDE_ID));
				tmpl.setRoutedgenericitemid(routedGenericItemId); 
				tmpl.setItemname(rs.getString(ITEMNAME));
				
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}catch (RuntimeException | ParseException e) {
		            EcwLog.AppendExceptionToLog(e);
		        }
				return tmpl;
			}
    	});
	}
	
	// to get pre-requisite vitals details
	public List<TemplateForPreVitals> getPreRequisiteVitalsDetails(Long nformularyId) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(N_FORMULARY_ID, nformularyId);
		int nRoutedGenericItemid = getRoutedGenericItemId(nformularyId.intValue());
		paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemid);
		
		StringBuilder strQ = new StringBuilder();			
		strQ.append(" SELECT pre.id,pre.leftsideid,pre.rightsideid, pre.routedGenericItemId ");
		strQ.append(" FROM ip_formulary_prerequisite pre ");			
		strQ.append(" WHERE pre.routedGenericItemId=:nRoutedGenericItemId and delflag=0");
		return namedParameterJdbcTemplate.query(strQ.toString(), paramMap,
		new RowMapper<TemplateForPreVitals>() {
			@Override
			public TemplateForPreVitals mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForPreVitals tmpl = new TemplateForPreVitals();
				tmpl.setId(rs.getString("id"));
				tmpl.setLeftsideid(rs.getString(LEFT_SIDE_ID));
				tmpl.setRightsideid(rs.getString(RIGHT_SIDE_ID));
				tmpl.setRoutedgenericitemid(rs.getString(ROUTED_GENERIC_ITEMID));
				return tmpl;
			}
		});
	}
	/*
	 * 	Method Name : getSlidingScale
	 *  Parameter1 : strDrugId is the formulary id
	 *  return value is TemplateForOTS pojo object with sliding scale value
	 * */
	public List<TemplateForOTS> getSlidingScale(final String strDrugId)
	{
		Map<String,Object> paramMap = new HashMap<>();
    	//here order type=1 is for medication logic changed
    	//IPUSA-11244 Move SS flag is at the MEDICATION Order type level
    	StringBuilder sqlB = new StringBuilder("select isslidingscale from ip_drugformulary_ordertypesetup where formularyid=:formularyid and ordertype=1 and delflag=0");
     	paramMap.put(FORMULARY_ID,strDrugId);
     	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForOTS>() {
			@Override
			public TemplateForOTS mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForOTS tmpl=new TemplateForOTS();
				tmpl.setSlidingscale(rs.getString("isslidingscale"));
				return tmpl;
			}
     	});
	}
	
	public List<TemplateForOTS> getOrderTypeSetupDetails(final String strDrugId){
		Map<String,Object> paramMap = new HashMap<>();
        List<TemplateForOTS> otsList = null;
    	//order type setup data
    	StringBuilder sqlB = new StringBuilder("SELECT id,formularyid,ordertype,isnonbillable,autostop,autostopuom,chargetypeid,createdby,createdon,modifiedon,modifiedby ");
    	sqlB.append(" ,isChangeRate,isTitrationAllowed ,isRefrigeration,expiresOn,expiresOnUOM,isPCA,isSlidingScale,slidingScale ");
    	sqlB.append(" ,bolus_loadingdose,bolus_loadingdose_uom,intermitten_dose,intermitten_dose_uom,lockout_interval_dose,lockout_interval_uom,four_hr_limit,four_hr_limit_uom  ");
    	sqlB.append(" from ip_drugformulary_OrderTypeSetup where formularyid=:formularyid and delflag=0");
     	paramMap.put(FORMULARY_ID,strDrugId);
     	otsList = namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForOTS>() {
			@Override
			public TemplateForOTS mapRow(ResultSet rs, int arg1) throws SQLException {
				boolean isIVOrderType = false;
				boolean isComplexOrderType = false;
				TemplateForOTS tmpl=new TemplateForOTS();
				Map<String,Object> paramMapOTS = new HashMap<>();
				Map<String,Object> paramMapOTSForm = new HashMap<>();
				tmpl.setOrderType(rs.getString(ORDER_TYPE));
				tmpl.setNonBillable(rs.getString("isnonbillable"));
				tmpl.setAutoStop(rs.getString("autostop"));
				tmpl.setAutoStopUOM(rs.getString("autostopuom"));
				tmpl.setId(rs.getString("id")); 
				
				if(FormularyConstants.FORMULARY_IV.equals(rs.getString(ORDER_TYPE)))
				{
					isIVOrderType = true;
				}
				else if(FormularyConstants.FORMULARY_COMPLEXORDER.equals(rs.getString(ORDER_TYPE)))
				{
					isComplexOrderType = true;
				}
				
				tmpl.setSlidingscale(rs.getString(ISSLIDINGSCALE));
				
				
				//route list
				StringBuilder strSql = new StringBuilder("select ots.isrecommended,ots.routeid,route.routecode,route.routedesc,ots.ordertypeid from ip_drugformulary_OTS_Route ots ");
				strSql.append(" inner join ip_drugformulary_routes route on ots.routeid = route.id ");
				strSql.append(" where ots.delflag=0 and route.deleteflag=0 and ots.formularyid=:formularyid and ots.ordertypeid=:ordertypeid ");
				paramMapOTS.put(FORMULARY_ID,strDrugId);
				paramMapOTS.put(ORDER_TYPE_ID,rs.getString("id"));
				List<TemplateRoutes> routeList = namedParameterJdbcTemplate.query(strSql.toString(),paramMapOTS, new RowMapper<TemplateRoutes>(){
					@Override
					public TemplateRoutes mapRow(ResultSet rs1, int arg1) throws SQLException {
						TemplateRoutes tmpl=new TemplateRoutes();
						tmpl.setExtMappingId(rs1.getString("isrecommended"));
						tmpl.setRouteID(rs1.getString(ROUTE_ID));
						tmpl.setRouteName(rs1.getString(ROUTE_CODE));
						tmpl.setRouteDesc(rs1.getString(ROUTE_DESC));
						return tmpl;
					}
				});
				
				//formulation list
				strSql.setLength(0);
				paramMapOTSForm.clear();
				strSql.append(" select ots.formulationid,form.formulationcode,ots.ordertypeid,form.formulationdesc from ip_drugformulary_OTS_Formulation ots ");
				strSql.append(" inner join ip_drugformulary_formulation form on ots.formulationid = form.id "); 
				strSql.append(" where ots.delflag=0 and form.deleteflag=0 and ots.formularyid=:formularyid and ots.ordertypeid=:ordertypeid ");
				
				paramMapOTSForm.put(FORMULARY_ID,strDrugId);
				paramMapOTSForm.put(ORDER_TYPE_ID,rs.getString("id"));
				List<TemplateForOTFormulation> formulationList = namedParameterJdbcTemplate.query(strSql.toString(),paramMapOTSForm, new RowMapper<TemplateForOTFormulation>(){
					@Override
					public TemplateForOTFormulation mapRow(ResultSet rs2, int arg1) throws SQLException {
						TemplateForOTFormulation tmpl=new TemplateForOTFormulation();
						tmpl.setOrderTypeFormulationid(rs2.getString(FORMULATIONID));
						tmpl.setOrderTypeFormulationCode(rs2.getString("formulationcode"));
						tmpl.setOrderTypeFormulationDescr(rs2.getString("formulationdesc"));
						return tmpl;
					}
				});
				
				//frequency list
				strSql.setLength(0);
				paramMapOTS.clear();  
				paramMapOTS.put(FORMULARY_ID,strDrugId);
				paramMapOTS.put(ORDER_TYPE_ID,rs.getString("id"));
				strSql.append(" select ots.isrecommended,ots.freqid,freq.freqCode,ots.ordertypeid,freq.scheduletype,freq.freqDesc from ip_drugformulary_OTS_Frequency ots ");
				strSql.append(" inner join ip_drugformulary_frequency freq on ots.freqid = freq.id "); 
				strSql.append(" where ots.delflag=0 and freq.deleteflag=0 and ots.formularyid=:formularyid and ots.ordertypeid=:ordertypeid ");
				List<TemplateForOTFrequency> frequencyList = namedParameterJdbcTemplate.query(strSql.toString(),paramMapOTS, new RowMapper<TemplateForOTFrequency>(){
					@Override
					public TemplateForOTFrequency mapRow(ResultSet rs3, int arg1) throws SQLException {
						TemplateForOTFrequency tmpl=new TemplateForOTFrequency();
						tmpl.setExtMappingId(rs3.getString("isrecommended"));
						tmpl.setOrderTypeFrequencyid(rs3.getString(FREQID));
						tmpl.setOrderTypeFrequencyCode(rs3.getString(FREQCODE));
						tmpl.setScheduleType(rs3.getString("scheduletype"));
						tmpl.setOrderTypeFrequencyDescr(rs3.getString(FREQDESC));
						return tmpl;
					}
				});
				
				
				String strChgType = rs.getString(CHARGETYPEID);
            	if("0".equals(strChgType))
            	{
            		strChgType = "";
            	}
            	final String strRouteList = new com.google.gson.Gson().toJson(routeList);
            	final String strFormulationList = new com.google.gson.Gson().toJson(formulationList);
            	final String strFrequencyList = new com.google.gson.Gson().toJson(frequencyList);
            	
            	if(isIVOrderType)
                {
					tmpl.setChangeRate(rs.getString(ISCHANGERATE));
					tmpl.setTitAllowed(rs.getString(ISTITRATIONALLOWED));
					tmpl.setRefri(rs.getString(ISREFRIGERATION));
					tmpl.setExpiresOn(rs.getString(EXPIRESON));
					tmpl.setExpiresOnUom(rs.getString("expiresOnUom"));
					tmpl.setPca(rs.getString(ISPCA));
					tmpl.setSliding_scale_notes(rs.getString(SLIDING_SCLAE));
					tmpl.setBolusLoadingdose(rs.getString(BOLUS_LOADINGDOSE));
					tmpl.setBolusLoadingdoseuom(rs.getString(BOLUS_LOADINGDOSE_UOM));
					tmpl.setIntermitten_dose(rs.getString(INTERMITTEN_DOS));
					tmpl.setIntermitten_dose_uom(rs.getString(INTERMITTEN_DOSE_UOM));
					tmpl.setLockout_interval_dose(rs.getString(LOCKOUT_INTERVAL_DOSE));
					tmpl.setLockout_interval_uom(rs.getString(LOCKOUT_INTERVAL_UOM));
					tmpl.setFourhrs_limit(rs.getString(FOUR_HR_LIMIT));
					tmpl.setFourhrs_limit_uom(rs.getString(FOUR_HR_LIMIT_UOM));
					
					//iv diluent
 					StringBuilder strSql1 = new StringBuilder("select ii.drugname as drugName,idd.formularyid,idd.ivformularyid,idd.ordertypesetupid,");
 					strSql1.append(" idd.dispensesize,idd.dispensesizeuom,idd.dose,idd.doseuom,ii.itemname,drug.strength as orgstrength, drug.dispensesizeuom as orgstrengthuom,");
 					strSql1.append(" drug.volume as org_volume,drug.routeName as org_routename,drug.formulation as org_formulation,drug.iscalculate ");
 					strSql1.append(" from ip_drugformulary_diluent idd inner join ip_drugformulary drug on idd.ivformularyid = drug.id and idd.delflag=0 and drug.delflag=0 and drug.isactive=1 ");
 					strSql1.append(" inner join ip_items ii on drug.routedgenericitemid = ii.itemid and idd.formularyid=:formularyid and idd.ordertypesetupid=:ordertypesetupid order by drug.id ");
 					
 					paramMapOTS.clear();  
 					paramMapOTS.put(FORMULARY_ID,strDrugId);
 					paramMapOTS.put(ORDERTYPE_SETUP_ID,rs.getString("id"));
 					List<TemplateForIVDiluent> tmplIVDiluent = namedParameterJdbcTemplate.query(strSql1.toString(),paramMapOTS, new RowMapper<TemplateForIVDiluent>(){
 						@Override
 						public TemplateForIVDiluent mapRow(ResultSet rs1, int arg1) throws SQLException {
 							TemplateForIVDiluent tmpl=new TemplateForIVDiluent();
 							tmpl.setFormularyid(rs1.getInt(FORMULARY_ID));
 							tmpl.setIvformularyid(rs1.getInt(IVFORMULARYID));
 							tmpl.setOrdertypesetupid(rs1.getInt(ORDERTYPE_SETUP_ID));
 							tmpl.setDispenseSize(RxUtil.preSanitizeStringAsDouble(rs1.getString(DISPENSE_SIZE),true));
 							tmpl.setDispenseSizeUOM(rs1.getString(DISPENSE_SIZE_UOM));
 							tmpl.setDose(RxUtil.preSanitizeStringAsDouble(rs1.getString("dose"),true));
 							tmpl.setDoseuom(rs1.getString(DOSE_UOM));
 							tmpl.setItemname(rs1.getString(ITEMNAME));
 							tmpl.setDrugName(rs1.getString(DRUG_NAME));
 							tmpl.setOrg_strength(rs1.getString("orgstrength"));
 							tmpl.setOrg_strengthuom(rs1.getString("orgstrengthuom"));
 							tmpl.setOrg_formulation(rs1.getString("org_formulation"));
 							tmpl.setOrg_routeName(rs1.getString("org_routename"));
 							tmpl.setOrg_volume(rs1.getString("org_volume"));
 							tmpl.setIsCalculation(rs1.getInt(ISCALCULATE));
 							return tmpl;
 						}
 					});
                	final String strDiluent = new com.google.gson.Gson().toJson(tmplIVDiluent);
                	tmpl.setIvdiluentsaveddata(strDiluent);
                	
                	//iv rate
 					strSql1 = new StringBuilder("select formularyid,ordertypesetupid,strength,strengthuom from ip_drugformulary_iv_rate where delflag=0 "); 
 					strSql1.append(" and formularyid=:formularyid and ordertypesetupid=:ordertypesetupid ");
 					paramMapOTS.clear(); 
 					paramMapOTS.put(FORMULARY_ID,strDrugId);
 					paramMapOTS.put(ORDERTYPE_SETUP_ID,rs.getString("id"));
 					List<TemplateForIVRate> tmplIVRate = namedParameterJdbcTemplate.query(strSql1.toString(),paramMapOTS, new RowMapper<TemplateForIVRate>(){
 						@Override
 						public TemplateForIVRate mapRow(ResultSet rs1, int arg1) throws SQLException {
 							TemplateForIVRate tmpl=new TemplateForIVRate();
 							tmpl.setFormularyid(rs1.getInt(FORMULARY_ID));
 							tmpl.setOrdertypesetupid(rs1.getInt(ORDERTYPE_SETUP_ID));
 							tmpl.setStrength(rs1.getString(STRENGTH));
 							tmpl.setStrengthuom(rs1.getString(STRENGTH_UOM));
 							return tmpl;
 						}
 					});
                	final String strIVRate = new com.google.gson.Gson().toJson(tmplIVRate);
                	tmpl.setIvratesaveddata(strIVRate);
                
                }
            	// for complex order
            	if(isComplexOrderType)
                {
					tmpl.setChangeRate(rs.getString(ISCHANGERATE));
					tmpl.setTitAllowed(rs.getString(ISTITRATIONALLOWED));
					tmpl.setRefri(rs.getString(ISREFRIGERATION));
					tmpl.setExpiresOn(rs.getString(EXPIRESON));
					tmpl.setExpiresOnUom(rs.getString("expiresOnUom"));
					tmpl.setPca(rs.getString(ISPCA));
					tmpl.setSliding_scale_notes(rs.getString(SLIDING_SCLAE));
					tmpl.setBolusLoadingdose(rs.getString(BOLUS_LOADINGDOSE));
					tmpl.setBolusLoadingdoseuom(rs.getString(BOLUS_LOADINGDOSE_UOM));
					tmpl.setIntermitten_dose(rs.getString(INTERMITTEN_DOS));
					tmpl.setIntermitten_dose_uom(rs.getString(INTERMITTEN_DOSE_UOM));
					tmpl.setLockout_interval_dose(rs.getString(LOCKOUT_INTERVAL_DOSE));
					tmpl.setLockout_interval_uom(rs.getString(LOCKOUT_INTERVAL_UOM));
					tmpl.setFourhrs_limit(rs.getString(FOUR_HR_LIMIT));
					tmpl.setFourhrs_limit_uom(rs.getString(FOUR_HR_LIMIT_UOM));
					
					//iv diluent
 					StringBuilder strSql1 = new StringBuilder("select ii.drugname as drugName,idd.formularyid,idd.ivformularyid,idd.ordertypesetupid,");
 					strSql1.append(" idd.dispensesize,idd.dispensesizeuom,idd.dose,idd.doseuom,ii.itemname,drug.strength as orgstrength, drug.dispensesizeuom as orgstrengthuom,");
 					strSql1.append(" drug.volume as org_volume,drug.routeName as org_routename,drug.formulation as org_formulation,drug.iscalculate ");
 					strSql1.append(" from ip_drugformulary_diluent idd inner join ip_drugformulary drug on idd.ivformularyid = drug.id and idd.delflag=0 and drug.delflag=0 and drug.isActive=1");
 					strSql1.append(" inner join ip_items ii on drug.routedgenericitemid = ii.itemid and idd.formularyid=:formularyid and idd.ordertypesetupid=:ordertypesetupid order by drug.id ");
 					
 					paramMapOTS.clear();  
 					paramMapOTS.put(FORMULARY_ID,strDrugId);
 					paramMapOTS.put(ORDERTYPE_SETUP_ID,rs.getString("id"));
 					List<TemplateForIVDiluent> tmplIVDiluent = namedParameterJdbcTemplate.query(strSql1.toString(),paramMapOTS, new RowMapper<TemplateForIVDiluent>(){
 						@Override
 						public TemplateForIVDiluent mapRow(ResultSet rs1, int arg1) throws SQLException {
 							TemplateForIVDiluent tmpl=new TemplateForIVDiluent();
 							tmpl.setFormularyid(rs1.getInt(FORMULARY_ID));
 							tmpl.setIvformularyid(rs1.getInt(IVFORMULARYID));
 							tmpl.setOrdertypesetupid(rs1.getInt(ORDERTYPE_SETUP_ID));
 							tmpl.setDispenseSize(RxUtil.preSanitizeStringAsDouble(rs1.getString(DISPENSE_SIZE),true));
 							tmpl.setDispenseSizeUOM(rs1.getString(DISPENSE_SIZE_UOM));
 							tmpl.setDose(RxUtil.preSanitizeStringAsDouble(rs1.getString("dose"),true));
 							tmpl.setDoseuom(rs1.getString(DOSE_UOM));
 							tmpl.setItemname(rs1.getString(ITEMNAME));
 							tmpl.setDrugName(rs1.getString(DRUG_NAME));
 							tmpl.setOrg_strength(rs1.getString("orgstrength"));
 							tmpl.setOrg_strengthuom(rs1.getString("orgstrengthuom"));
 							tmpl.setOrg_formulation(rs1.getString("org_formulation"));
 							tmpl.setOrg_routeName(rs1.getString("org_routename"));
 							tmpl.setOrg_volume(rs1.getString("org_volume"));
 							tmpl.setIsCalculation(rs1.getInt(ISCALCULATE));
 							return tmpl;
 						}
 					});
                	final String strDiluent = new com.google.gson.Gson().toJson(tmplIVDiluent);
                	tmpl.setTpn_ivdiluentsaveddata(strDiluent);
                	
                	//iv rate
 					strSql1 = new StringBuilder("select formularyid,ordertypesetupid,strength,strengthuom from ip_drugformulary_iv_rate where delflag=0 "); 
 					strSql1.append(" and formularyid=:formularyid and ordertypesetupid=:ordertypesetupid ");
 					paramMapOTS.clear();  
 					paramMapOTS.put(FORMULARY_ID,strDrugId);
 					paramMapOTS.put(ORDERTYPE_SETUP_ID,rs.getString("id"));
 					List<TemplateForIVRate> tmplIVRate = namedParameterJdbcTemplate.query(strSql1.toString(),paramMapOTS, new RowMapper<TemplateForIVRate>(){
 						@Override
 						public TemplateForIVRate mapRow(ResultSet rs1, int arg1) throws SQLException {
 							TemplateForIVRate tmpl=new TemplateForIVRate();
 							tmpl.setFormularyid(rs1.getInt(FORMULARY_ID));
 							tmpl.setOrdertypesetupid(rs1.getInt(ORDERTYPE_SETUP_ID));
 							tmpl.setStrength(rs1.getString(STRENGTH));
 							tmpl.setStrengthuom(rs1.getString(STRENGTH_UOM));
 							return tmpl;
 						}
 					});
                	final String strIVRate = new com.google.gson.Gson().toJson(tmplIVRate);
                	tmpl.setTpn_ivratesaveddata(strIVRate);
                
                }
            	tmpl.setRouteList(strRouteList);
            	tmpl.setFormulationList(strFormulationList);
            	tmpl.setFrequencyList(strFrequencyList);
				tmpl.setChargeTypeId(strChgType);
				tmpl.setId(rs.getString("id"));
				tmpl.setFormularyid(rs.getString(FORMULARY_ID)); 
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}	catch(ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
				
				return tmpl;
			}
     	});
        return otsList;
	}
	
	
	public List<TemplateForOTS> getIVAndComplexOrderDiluentData(){
        List<TemplateForOTS> otsList = null;
    	//order type setup data
    	otsList = new ArrayList<>();
    	TemplateForOTS tmpl=new TemplateForOTS();
    	List<Template> ivDiluentDrugList = null;
    	List<Template> drugListForComplexOrder = null;
    	ivDiluentDrugList = getIVDiluentDrugList();
    	drugListForComplexOrder = getAllDrugList();
    	final String strIvDiluentDrugList = new com.google.gson.Gson().toJson(ivDiluentDrugList);
    	final String strAllDrugList = new com.google.gson.Gson().toJson(drugListForComplexOrder);
		tmpl.setIvDiluentDrugList(strIvDiluentDrugList);
		tmpl.setDrugListForComplexOrder(strAllDrugList);
		otsList.add(tmpl);
        return otsList;
	}
	
	public List<TemplateForFacilities> getFacilitiesList(int trUserId){
        List<TemplateForFacilities> tmplList = new ArrayList<>();
    	final LogicalParam lParam = new LogicalParam();
    	lParam.setUserId(trUserId);
    	final List<Map<String,Object>> listFacilityMap = logicalLookupService.getFacility(lParam);
    	for(int i=0;i<listFacilityMap.size();i++){
			TemplateForFacilities tmpl=new TemplateForFacilities();
			tmpl.setId(String.valueOf(listFacilityMap.get(i).get(FACILITYID)));
			tmpl.setName(String.valueOf(listFacilityMap.get(i).get("facilityName"))); 
			tmplList.add(tmpl);
		}
        return tmplList;
	}
	
	public List<TemplateForFormularyDrugRoutes> getFormularyDrugRoutesList(){ 
		Map<String,Object> paramMap = new HashMap<>();
    	String sql = "SELECT id,facilitytype,NAME FROM ip_drugformulary_OTS_Route where delflag=0 order by name desc ";
    	return namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<TemplateForFormularyDrugRoutes>() {
			@Override
			public TemplateForFormularyDrugRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForFormularyDrugRoutes tmpl=new TemplateForFormularyDrugRoutes();
				tmpl.setId(rs.getString("id")); 
				return tmpl;
			}
    	});
	}
	
	public Map<String,String> insertUpdateDrugDetails(String jsonDrugData,String action,int nTrUserId)throws IOException{
		TemplateForDrug drug = new ObjectMapper().readValue(jsonDrugData, TemplateForDrug.class);
		return insertUpdateDrugDetails(drug,action,nTrUserId);
	}
		
	private Map<String,String> insertUpdateDrugDetails(TemplateForDrug drug,String action,int nTrUserId){
		Map<String, Object> paramMapNew = new HashMap<>();
		Map<String,Object> paramMap = new HashMap<>();
		StringBuilder strQ = new StringBuilder("");
		int newId = 0;
		String strItemId = ""; 
		String strRoutedGenericId = "";
		int nRoutedGenericItemId = -1;
		String strItemName = "";
		Map<String, Object> aMapDrugInfo = null;
		int newItemId = 0;
		int nRouteID = 0; 
		Map<String,String> resultMap = new HashMap<>();
        try {   
        	boolean isInsertRecord = true;
        	strItemId = Util.trimStr(drug.getItemId());
        	String sDrugName = Util.trimStr(drug.getDrugName());
        	String strRouteId = Util.trimStr(drug.getRouteID());
        	String sRouteName = Util.trimStr(drug.getRouteName());
        	String iscustommed = Util.trimStr(drug.getIscustommed());
        	
        	if("".equals(sDrugName))
    		{
    			sDrugName = drug.getGenericDrugName(); 
    		}
        	if("1".equals(iscustommed))   
        	{  
        		if(("".equals(strItemId) || "0".equals(strItemId)) && !"".equals(sDrugName) && !"".equals(sRouteName) && !"".equals(strRouteId) && !"0".equals(strRouteId))
        		{
        			nRouteID = Integer.parseInt(strRouteId);
        			newItemId = migrateRxService.createItemIDOfCustomRx(sDrugName, sRouteName, nRouteID); 
        		}
        		else
        		{
        			newItemId = Integer.parseInt(strItemId);
        		}
        		if(newItemId > 0 && !Util.trimStr(sDrugName).equals("") &&  !Util.trimStr(sRouteName).equals(""))
        		{
        			migrateRxService.updateCustomRxItemName(newItemId, sDrugName, sRouteName);
        		}
        	}
        	else
        	{
        		if(!"".equals(strItemId))
            	{
        			newItemId = Integer.parseInt(strItemId);
            	}
        	}
        	
        	PharmacyHelper.generatePharmacyLog("insertUpdateDrugDetails is called");
        	PharmacyHelper.generatePharmacyLog("newItemId ["+newItemId+"]");
        	
        	if(newItemId > 0)
        	{
        		strQ.append("select routedgenericid,itemname from ip_items where itemid=:itemid and deleteflag=0 ");
        		paramMap.put(ITEMID, newItemId);
        		List<Template> tmplList = namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<Template>(){
    				@Override
    				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
    					Template tmpl=new Template();
    					tmpl.setRoutedGenericId(rs.getString("routedgenericid"));
    					tmpl.setItemName(rs.getString(ITEMNAME));
    					return tmpl;
    				}
        		});
        		Iterator<Template> it = tmplList.iterator();
        		if(it.hasNext())
        		{
        			Template t1 = it.next();
        			strRoutedGenericId = t1.getRoutedGenericId();
        			strItemName = t1.getItemName();
        		}
				strQ.setLength(0);
				paramMap.clear();
				
				strQ.append("select itemid,itemname from ip_items where routeddrugid=:routeddrugid and routedgenericid=routeddrugid");
        		paramMap.put("routeddrugid", strRoutedGenericId);
        		List<Template> tmplList1 = namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<Template>(){
    				@Override
    				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
    					Template tmpl=new Template();
    					tmpl.setItemId(rs.getString(ITEMID));
    					tmpl.setItemName(rs.getString(ITEMNAME));
    					return tmpl;
    				}
        		});
        		Iterator<Template> it1 = tmplList1.iterator();
        		if(it1.hasNext())
        		{
        			Template t2 = it1.next();
        			nRoutedGenericItemId = Util.trimStr(t2.getItemId()).equals("")?-1:Integer.parseInt(Root.TrimInteger(t2.getItemId())); 
        		}
        		PharmacyHelper.generatePharmacyLog("nRoutedGenericItemId ["+nRoutedGenericItemId+"]");
        		if(nRoutedGenericItemId == -1)
        		{
	        		//to get locally stored itemid from routeddrugid - ms-clinical
	            	if (strRoutedGenericId != null && !"".equals(strRoutedGenericId)) {
	    				aMapDrugInfo = medHelperService.getDrugDetailMapFromRoutedDrugID(StringUtil.getIntValue(strRoutedGenericId), "");
	    				if(aMapDrugInfo != null && !aMapDrugInfo.isEmpty()){
	    					nRoutedGenericItemId = Util.getIntValue(aMapDrugInfo,ITEMID1,0);
	    				}
	    			}
	            	if(nRoutedGenericItemId<=0)
	            	{
	            		throw new InvalidParameterException("Invalid Routed Generic Item Id");
	            	}
        		}
        		if("1".equals(iscustommed))
        		{//this assignment is for custom drugs only
        			nRoutedGenericItemId = newItemId;
        		}
        		
        		//this is used to insert data into common setting table with default values
        		setDataToDrugFormularyCommonSettingWithDefault(-1,newItemId,nRoutedGenericItemId,nTrUserId);
        		
	    		strQ.setLength(0);
	    		strQ.append(" insert into ip_drugformulary");
	    		strQ.append(" (drugNameID,itemId,ddid,gpi,rxnorm,genericDrugItemID,maxDose24Hours,maxDose24HoursUOM,");
				strQ.append(" primaryClassification,secondaryClassification,tertiaryClassification,medIdCode,isgenericavailable,iscalculate,isactive,formulation,");
				strQ.append(" strength,strengthuom,volume,volumeuom,chargecode,csa_schedule,ndc,upc,stockarea,");
				strQ.append(" isavailableforall,isserviceforall,isMedication,isTPN,isIV,isIVPGB,createdby,createdon,modifiedby,modifiedon,userId,notes,genericDrugName,genericDrugNameID,");
				strQ.append(" isSingleDosagePackage,HCPCSCodeRange,HCPCSCodeUnit,HCPCSCodeType,isAllowToAdminWithoutApproval,dispenseForm,dispenseSize,dispenseSizeUom,routedGenericItemId,ahfsClassification,");
				strQ.append(" ahfsClassID,cptcodeitemid,routeid,routename,doseUom,doseSize,assigned_brand_itemid,assigned_brand_itemname,iscustommed,primaryppid,isdrugtypebulk_formulary,ischargeableatdispense_formulary,");
				strQ.append(" isDisplayPkgSize,");
				strQ.append(" isDisplayPkgType, ");
				strQ.append(" packsize, ");
				strQ.append(" packsizeunitcode, ");
				strQ.append(" packType ");
				strQ.append(" ) ");
				strQ.append(" values(:drugNameID,:itemId,:ddid,:gpi,:rxnorm,:genericDrugItemID,:maxDose24Hours,:maxDose24HoursUOM,:primaryClassification,:secondaryClassification,");
				strQ.append(" :tertiaryClassification,:medIdCode,:isgenericavailable,:isCalculate,:isactive,:formulation,:strength,:strengthuom,:volume,:volumeuom,");
				strQ.append(" :chargecode,:csa_schedule,:ndc,:upc,:stockarea,");
				strQ.append(" :isavailableforall,:isserviceforall,");
				strQ.append(" :isMedication,:isTPN,:isIV,:isIVPGB,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:notes,:genericDrugName,:genericDrugNameID,");
				strQ.append(" :isSingleDosagePackage,:HCPCSCodeRange,:HCPCSCodeUnit,:HCPCSCodeType,:isAllowToAdminWithoutApproval,:dispenseForm,:dispenseSize,:dispenseSizeUom,:routedGenericItemId,");
				strQ.append(" :ahfsClassification,:ahfsClassID,(case when :cptcodeitemid = '' or :cptcodeitemid = 0 then NULL else :cptcodeitemid end),:routeid,:routename,:doseUom,:doseSize,:assigned_brand_itemid,:assigned_brand_itemname,:iscustommed,:primaryppid,:isdrugtypebulk,:ischargeableatdispense,");
				strQ.append(" :isDisplayPkgSize,");
				strQ.append(" :isDisplayPkgType,");
				strQ.append(" :packsize, ");
				strQ.append(" :packsizeunitcode, ");
				strQ.append(" :packType) ");
	        	
	        	if("modify".equalsIgnoreCase(action))
	        	{
	        		strQ.setLength(0);
	        		strQ.append(" update ip_drugformulary set drugNameID=:drugNameID,itemId=:itemId,ddid=:ddid,gpi=:gpi,rxnorm=:rxnorm,genericDrugItemID=:genericDrugItemID, ");
					strQ.append(" maxDose24Hours=:maxDose24Hours,maxDose24HoursUOM=:maxDose24HoursUOM,primaryClassification=:primaryClassification, ");
					strQ.append(" secondaryClassification=:secondaryClassification,tertiaryClassification=:tertiaryClassification,medIdCode=:medIdCode, ");
					strQ.append(" isgenericavailable=:isgenericavailable,iscalculate=:isCalculate,isactive=:isactive,formulation=:formulation,strength=:strength,strengthuom=:strengthuom, ");
					strQ.append(" volume=:volume,volumeuom=:volumeuom,chargecode=:chargecode,csa_schedule=:csa_schedule,ndc=:ndc,upc=:upc, ");
					strQ.append(" stockarea=:stockarea, ");
					strQ.append(" isavailableforall=:isavailableforall,isserviceforall=:isserviceforall,isMedication=:isMedication, ");
					strQ.append(" isTPN=:isTPN,isIV=:isIV,isIVPGB=:isIVPGB,");
					strQ.append(" modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,notes=:notes,genericDrugName=:genericDrugName,genericDrugNameID=:genericDrugNameID,");
					strQ.append(" isSingleDosagePackage=:isSingleDosagePackage,HCPCSCodeRange=:HCPCSCodeRange,HCPCSCodeUnit=:HCPCSCodeUnit,HCPCSCodeType=:HCPCSCodeType,isAllowToAdminWithoutApproval=:isAllowToAdminWithoutApproval,dispenseForm=:dispenseForm,");
					strQ.append(" dispenseSize=:dispenseSize,dispenseSizeUom=:dispenseSizeUom,routedGenericItemId=:routedGenericItemId,ahfsClassification=:ahfsClassification,ahfsClassID=:ahfsClassID,cptcodeitemid=(case when :cptcodeitemid='' or :cptcodeitemid=0 then NULL else :cptcodeitemid end),");
					strQ.append(" routeid= :routeid,routename= :routename,doseUom=:doseUom,doseSize=:doseSize,assigned_brand_itemid=:assigned_brand_itemid,assigned_brand_itemname=:assigned_brand_itemname,iscustommed=:iscustommed,primaryppid=:primaryppid,");
					strQ.append(" isdrugtypebulk_formulary=:isdrugtypebulk,ischargeableatdispense_formulary=:ischargeableatdispense,");
					strQ.append(" isDisplayPkgSize=:isDisplayPkgSize,");
					strQ.append(" isDisplayPkgType=:isDisplayPkgType, ");
					strQ.append(" packsize=:packsize, ");
					strQ.append(" packsizeunitcode=:packsizeunitcode, ");
					strQ.append(" packType=:packType ");
					strQ.append(" where id=:drug_id ");
		        	isInsertRecord = false;
	        	}
	        	paramMapNew.put(DRUGNAMEID,drug.getDrugNameID());
	        	paramMapNew.put(ITEM_ID,newItemId);
	        	paramMapNew.put("ddid",drug.getDdid());
	        	paramMapNew.put("gpi",drug.getGpi());
	        	paramMapNew.put(RX_NORM,drug.getRxnorm());
	        	paramMapNew.put("genericDrugItemID",drug.getGenericDrugItemID());
	        	paramMapNew.put("maxDose24Hours",drug.getMaxDose24Hours());
	        	paramMapNew.put("maxDose24HoursUOM",drug.getMaxDose24HoursUOM());
	        	paramMapNew.put("primaryClassification",drug.getPrimaryClassification());
	        	paramMapNew.put(SECONDARY_CLASSIFICATION,drug.getSecondaryClassification());
	        	paramMapNew.put(TERTIARY_CLASSIFICATION,drug.getTertiaryClassification());
	        	paramMapNew.put("medIdCode",drug.getMedIdCode());
	        	paramMapNew.put("isgenericavailable",drug.getIsgenericavailable());
				paramMapNew.put(IS_CALCULATE,drug.getIsCalculate());        	
				paramMapNew.put(IS_ACTIVE,drug.getIsactive());
	        	paramMapNew.put(FORMULATION,drug.getFormulation());
	        	paramMapNew.put(STRENGTH,drug.getStrength());
	        	paramMapNew.put(STRENGTH_UOM,drug.getStrengthuom());
	        	paramMapNew.put(VOLUME,drug.getVolume());
	        	paramMapNew.put("volumeuom",drug.getVolumeuom());
	        	paramMapNew.put(CHARGE_CODE,drug.getChargecode());
	        	if("N/A".equalsIgnoreCase(drug.getCsa_schedule()))
				{
	        		paramMapNew.put(CSA_SCHEDULE,"0");
				}
	        	else
	        	{
	        		paramMapNew.put(CSA_SCHEDULE,drug.getCsa_schedule());
	        	}
	        	paramMapNew.put("ndc",drug.getNdc());
	        	paramMapNew.put("upc",drug.getUpc());
	        	paramMapNew.put("stockarea",drug.getStockarea());
	        	paramMapNew.put(ISAVAILABLEFORALL,drug.getIsavailableforall());
	        	paramMapNew.put(ISSERVICEFORALL,drug.getIsserviceforall());
	        	paramMapNew.put("isMedication",drug.getIsMedication());
	        	paramMapNew.put("isTPN",drug.getIsTPN());
	        	paramMapNew.put("isIV",drug.getIsIV());
	        	paramMapNew.put("isIVPGB",drug.getIsIVPGB());
	
	        	paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	    		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
	    		paramMapNew.put(NOTES,drug.getNotes());
	    		paramMapNew.put(GENERIC_DRUG_NAME,sDrugName);
	    		paramMapNew.put("genericDrugNameID",drug.getGenericDrugNameID());
	    		paramMapNew.put("isSingleDosagePackage",drug.getisSingleDosagePackage());
	    		paramMapNew.put("HCPCSCodeRange",drug.getHcpcsCodeRange());
	    		paramMapNew.put("HCPCSCodeUnit",drug.getHcpcsCodeUnit());
	    		paramMapNew.put("HCPCSCodeType",drug.getHcpcsCodeType());
	    		paramMapNew.put("isAllowToAdminWithoutApproval",drug.getIsAllowToAdminWithoutApproval());
	    		paramMapNew.put("dispenseForm",drug.getDispenseForm());
    		    paramMapNew.put("dispenseSize",RxUtil.preSanitizeStringAsDouble(drug.getDispenseSize(),true));
	    		paramMapNew.put("dispenseSizeUom",drug.getDispenseSizeUom());
	    		paramMapNew.put(ROUTED_GENERIC_ITEMID, nRoutedGenericItemId);
	    		paramMapNew.put("ahfsClassification", drug.getAhfsClassification());
	    		paramMapNew.put(AHFS_CLASS_ID, drug.getAhfsClassID());
	    		
	    		paramMapNew.put(CPT_CODE_ITEMID, drug.getCptcodeitemid());
	    		
	    		paramMapNew.put(ROUTE_ID, drug.getRouteID());
	    		paramMapNew.put(ROUTE_NAME, drug.getRouteName()); 
	    		paramMapNew.put("doseUom", drug.getDoseUom());
    		    paramMapNew.put("doseSize", RxUtil.preSanitizeStringAsDouble(drug.getDoseSize(),true));
	    		paramMapNew.put(ASSIGNED_BRAND_ITEMID, drug.getAssigned_brand_itemid());
	    		paramMapNew.put("assigned_brand_itemname", drug.getAssigned_brand_itemname());
	    		paramMapNew.put("iscustommed", drug.getIscustommed());
	    		paramMapNew.put("primaryppid", drug.getPrimaryppid());
	    		paramMapNew.put(ISDRUGTYPEBULK,drug.getIsdrugtypebulk());
	        	paramMapNew.put(ISCHARGEABLEATDISPENSE,drug.getIschargeableatdispense());
	        	paramMapNew.put("isDisplayPkgSize",drug.getIsDisplayPkgSize());
	        	paramMapNew.put("isDisplayPkgType",drug.getIsDisplayPkgType());
	        	
	        	paramMapNew.put("packsize",drug.getPacksize());
	        	paramMapNew.put("packsizeunitcode",drug.getPacksizeunitcode());
	        	paramMapNew.put("packType",drug.getPackType());
	    		
	    		paramMapNew.put("drug_id",drug.getId());
	   		
	       		if(isInsertRecord)
	       		{
	       			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strQ.toString(), paramMapNew, "id");
	       		}
	       		else
	       		{
	       			namedParameterJdbcTemplate.update(strQ.toString(), paramMapNew);
	       			newId = Integer.parseInt(drug.getId()); 
	       		}
	       		PharmacyHelper.generatePharmacyLog("newly generated formulary id ["+newId+"]");
	       		PharmacyHelper.generatePharmacyLog("------strItemName ["+strItemName+"]");
	       		PharmacyHelper.generatePharmacyLog("Assigned brand itemid ["+drug.getAssigned_brand_itemid()+"]");
	       		PharmacyHelper.generatePharmacyLog("itemid ["+ drug.getItemId()+"]");
	       		
	       		//add/update mapped medication list
	       		insertUpdateMappedMedication(newId,drug.getMappedMedicationData(),nTrUserId);
	       		
	   			
	   			//rxOrderTreeService.addOrUpdateNewDrugInFormulary(newItemId);//TBD : required to add newly created items into cache - it is getting called from formularysetupservice.insertUpdateCustomDrugDetails method
	   			resultMap.put("formularyId", String.valueOf(newId));
	   			resultMap.put("itemId", String.valueOf(newItemId));
	   			
	   			if(newId > 0)
	   			{//used to update pkg size, pkg type into cache
	   				rxOrderTreeService.updatePkgSizePkgTypeAndBrandNameOnFormularyId(newId);
	   			}
        	}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return resultMap;
	}
	
	private int setDataToDrugFormularyCommonSettingWithDefault (int nFormularyId,int itemId,int routedGenericItemId,int nTrUserId) throws IOException, JSONException
	{
		TemplateForDrug tmpl = new TemplateForDrug();
		tmpl.setLookAlike("0");
		tmpl.setTitlePreview("");
		tmpl.setTitleStyle("");
		tmpl.setIssearchable("1");
		tmpl.setIsrestricted("0");
		tmpl.setIsRT("0");
		tmpl.setIsIVDiluent("0");
		tmpl.setVarianceLimit("");
		tmpl.setVarianceLimitUnit("");
		tmpl.setVarianceLimitAfter("");
		tmpl.setVarianceLimitAfterUnit("");
		tmpl.setDualverifyreqd("0");
		tmpl.setIsdrugtypebulk("0");
		tmpl.setIschargeableatdispense("0");
		tmpl.setIsrestrictedoutsideOS("0");
		tmpl.setDelflag("0");
		tmpl.setUserId(String.valueOf(nTrUserId));
		tmpl.setNotes("");
		tmpl.setItemId(String.valueOf(itemId));
		tmpl.setIsAdditive("0");
		tmpl.setIsMandatoryForOE("0");
		tmpl.setIsImmunization("0");
		tmpl.setIsppd("0");
		tmpl.setIsRenewInExpiringTab("0");
		tmpl.setImmItemId("0");
		tmpl.setImmItemName("");
		tmpl.setRoutedGenericItemId(String.valueOf(routedGenericItemId));
		final String strJsonData = new com.google.gson.Gson().toJson(tmpl);
		return insertIntoCommonSettingWithDefaultValues(nFormularyId,routedGenericItemId, strJsonData, nTrUserId);
	}
	/** 
	 * This method is used to mapped medication to formulary
	 * @param nFormularyId
	 * @param strMedData
	 * @param nTrUserId
	 */
	private void insertUpdateMappedMedication(int nFormularyId,List<TemplateForMedicationItems> strMedData,int nTrUserId)
	{
		Map<String,Object> paramMapNew = new HashMap<>();
		StringBuilder strNewQueryB = new StringBuilder();
		int nRoutedGenericItemId = 0;
		try {
			
			//first will delete all and then insert new
			strNewQueryB.append(
					"delete from ip_drugformulary_med_mapping where formularyid=:formularyId and delflag=0 ");
			paramMapNew.put(FORMULARYID, nFormularyId);
			namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
			
			for (int cnt = 0; strMedData!=null && cnt < strMedData.size(); cnt++) {
				TemplateForMedicationItems tempMedItemObj = strMedData.get(cnt);
				String itemId = tempMedItemObj.getItemId();
				String isPrimary  = tempMedItemObj.getIsPrimary();
				String drugNameID  = tempMedItemObj.getDrugNameID();
				String genericDrugNameID  = tempMedItemObj.getGenericDrugNameID();
				String routedDrugID  = tempMedItemObj.getRoutedDrugID();
				String routeID  = tempMedItemObj.getRouteID();
				String routedGenericItemId  = tempMedItemObj.getRoutedGenericItemId();
				String csa_schedule  = tempMedItemObj.getCsa_schedule();
				
				paramMapNew.clear();
				strNewQueryB.setLength(0);
				if("".equals(Util.trimStr(routedGenericItemId)) || "0".equals(Util.trimStr(routedGenericItemId)))
				{
					strNewQueryB.append("select itemid,itemname from ip_items where routeddrugid=:routeddrugid and routedgenericid=routeddrugid");
					paramMapNew.put("routeddrugid", routedDrugID);
	        		List<Template> tmplList1 = namedParameterJdbcTemplate.query(strNewQueryB.toString(), paramMapNew, new RowMapper<Template>(){
	    				@Override
	    				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
	    					Template tmpl=new Template();
	    					tmpl.setItemId(rs.getString(ITEMID));
	    					tmpl.setItemName(rs.getString(ITEMNAME));
	    					return tmpl;
	    				}
	        		});
	        		Iterator<Template> it1 = tmplList1.iterator();
	        		if(it1.hasNext())
	        		{
	        			Template t2 = it1.next();
	        			nRoutedGenericItemId = Util.trimStr(t2.getItemId()).equals("")?-1:Integer.parseInt(Root.TrimInteger(t2.getItemId())); 
	        		}
	        		PharmacyHelper.generatePharmacyLog("nRoutedGenericItemId ["+nRoutedGenericItemId+"]");
				}
				else if(!"".equals(Util.trimStr(routedGenericItemId)) && !"0".equals(Util.trimStr(routedGenericItemId)))
				{
					nRoutedGenericItemId = Integer.parseInt(routedGenericItemId);
				}
				
				paramMapNew.clear();  
				strNewQueryB.setLength(0);
				strNewQueryB.append(
						"delete from ip_drugformulary_med_mapping where formularyid=:formularyId and mapped_itemid=:mappedItemId and delflag=0 ");
				paramMapNew.put(FORMULARYID, nFormularyId);
				paramMapNew.put("mappedItemId", itemId);
				
				namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
				strNewQueryB.setLength(0);
				strNewQueryB.append(" insert into ip_drugformulary_med_mapping(createdby,createdon,modifiedby,modifiedon,formularyid,mapped_itemid,userid,isPrimary,");
				strNewQueryB.append("drugNameID,genericDrugNameID,routedDrugID,routeID,routedGenericItemId,csa_schedule)");
				
				strNewQueryB.append("values(:createdby,:createdon,:modifiedby,:modifiedon,:formularyId,:mappedItemId,:userid,:isPrimary,");
				strNewQueryB.append( ":drugNameID,:genericDrugNameID,:routedDrugID,:routeID,:routedGenericItemId,:csa_schedule)");
				
				paramMapNew.put(MODIFIED_BY, String.valueOf(nTrUserId));
				paramMapNew.put(MODIFIED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(CREATED_BY, String.valueOf(nTrUserId));  
				paramMapNew.put(CREATED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(USER_ID, String.valueOf(nTrUserId));
				paramMapNew.put("isPrimary",isPrimary);
				paramMapNew.put("drugNameID",drugNameID);
				paramMapNew.put("genericDrugNameID",genericDrugNameID);
				paramMapNew.put("routedDrugID",routedDrugID);
				paramMapNew.put("routeID",routeID);
				paramMapNew.put("routedGenericItemId",nRoutedGenericItemId);
				paramMapNew.put("csa_schedule",csa_schedule);
				namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
				strNewQueryB.setLength(0);
			}
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
	}
	
	
	//insert update ingredient details
	public void updateIngredientDetails(String strIngredientJsonData,int nFormularyId)
	{
		Map<String,Object> paramMap = new HashMap<>();
		StringBuilder strQueryB = new StringBuilder("update ip_drugformulary set Ingredients_count=:Ingredients_count ");
		JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(strIngredientJsonData);
        int nIngredientCount = jo.get("ingredientCount").getAsInt();
        if(nIngredientCount==1)
        {
        	 JsonArray ingredientListArr = jo.getAsJsonArray("ingredientList");
        	 for(int cnt = 0;cnt < ingredientListArr.size();cnt++)
    		 {
    			 JsonObject jsonIngName = (JsonObject)ingredientListArr.get(cnt);
    			 JsonArray ingredientComponentArr = jsonIngName.get("ingredientComponent").getAsJsonArray();
    			 for(int compCnt = 0;compCnt < ingredientComponentArr.size();compCnt++)
    			 {
    				JsonObject jsonComponent = (JsonObject)ingredientComponentArr.get(compCnt);
    				String componentUom = jsonComponent.get("componentUOM").getAsString();
    				String componentValue = jsonComponent.get("componentValue").getAsString();
    				switch(compCnt)
    				{
    					case NUM_0:	strQueryB.append(",Ingredient1_value=:Ingredient1_value,Ingredient1_uom=:Ingredient1_uom");
    						 	paramMap.put("Ingredient1_value", componentValue);
    						 	paramMap.put("Ingredient1_uom", componentUom);
    						 	break;
    					case NUM_1: strQueryB.append(",Ingredient2_value=:Ingredient2_value,Ingredient2_uom=:Ingredient2_uom");
    					 		paramMap.put("Ingredient2_value", componentValue);
    						 	paramMap.put("Ingredient2_uom", componentUom);
    					 		break;
    					case NUM_2: strQueryB.append(",Ingredient3_value=:Ingredient3_value,Ingredient3_uom=:Ingredient3_uom");
	        					paramMap.put("Ingredient3_value", componentValue);
    						 	paramMap.put("Ingredient3_uom", componentUom);
						 		break;
    					case NUM_3: strQueryB.append(",Ingredient4_value=:Ingredient4_value,Ingredient4_uom=:Ingredient4_uom");
            					paramMap.put("Ingredient4_value", componentValue);
    						 	paramMap.put("Ingredient4_uom", componentUom);
    					 		break;
    					default : break;
    				}
    				
    			 }
    		 } 
        }
        paramMap.put("Ingredients_count", nIngredientCount);
        paramMap.put(FORMULARYID, nFormularyId);
        strQueryB.append(" where id=:formularyId and delflag=0 ");
        namedParameterJdbcTemplate.update(strQueryB.toString(), paramMap);
	}
	
	//insert update dispense uom
	public void insertUpdateDispenseUOM(String strDispenseUOMArr,int nFormularyId,int nTrUserId)
	{
		if(nFormularyId > 0)
		{
			Map<String, Object> paramMapNew = new HashMap<>();
			JsonParser jsonParser = new JsonParser();
			JsonObject jo = (JsonObject) jsonParser.parse(strDispenseUOMArr);
			JsonArray dispenseUOMArr = jo.getAsJsonArray("dispenseUOMArr");
			for (int cnt = 0; cnt < dispenseUOMArr.size(); cnt++) {
				String strUOM = dispenseUOMArr.get(cnt).getAsString();
				if (!"".equals(strUOM)) {
					List<Map<String, Object>> aSize = null;
					boolean isInsertRecord = false;
					StringBuilder strNewQueryB = new StringBuilder();
					paramMapNew.clear();
					strNewQueryB.append(
							"select id from ip_drugformulary_dispenseuom_external where formularyid=:formularyId and dispenseuom=:dispenseuom and delflag=0 ");
					paramMapNew.put(FORMULARYID, nFormularyId);
					paramMapNew.put("dispenseuom", strUOM);
					aSize = namedParameterJdbcTemplate.queryForList(strNewQueryB.toString(), paramMapNew);

					if (aSize.isEmpty()) {
						isInsertRecord = true;
					}
					strNewQueryB.setLength(0);
					strNewQueryB.append(
							"update ip_drugformulary_dispenseuom_external set modifiedby=:modifiedby,modifiedon=:modifiedon)");
					strNewQueryB.append("where formularyid=:formularyId and dispenseuom=:dispenseuom and delflag=0 ");

					if (isInsertRecord) {
						strNewQueryB.setLength(0);
						strNewQueryB.append(
								"insert into ip_drugformulary_dispenseuom_external(createdby,createdon,modifiedby,modifiedon,formularyid,dispenseuom,userid)");
						strNewQueryB.append(
								"values(:createdby,:createdon,:modifiedby,:modifiedon,:formularyId,:dispenseuom,:userid)");
					}
					paramMapNew.put(MODIFIED_BY, String.valueOf(nTrUserId));
					paramMapNew.put(MODIFIED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
					paramMapNew.put(CREATED_BY, String.valueOf(nTrUserId));  
					paramMapNew.put(CREATED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
					paramMapNew.put(USER_ID, String.valueOf(nTrUserId));
					namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
				}
			}
		}
	}
	
	//drug classification - primary secondary and tertiary
	public void insertUpdateDrugClassification(String strDrugClassiJsonData,int nFormularyId,int nTrUserId)
	{
		Map<String, Object> paramMapNew = new HashMap<>();
		List<Map<String, Object>> aSize = new ArrayList<>();
		Map<String,Object> paramMap = new HashMap<>();
		boolean isInsertRecord = false;		
		boolean isDataFound = false;
		String actionToLog = "";
		try
		{
			StringBuilder strQueryB = new StringBuilder("update ip_drugformulary set ");
			JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject)jsonParser.parse(strDrugClassiJsonData);
        	JsonArray drugClassiArr = jo.getAsJsonArray("drugClassificationList");
        	for(int cnt = 0;cnt < drugClassiArr.size();cnt++)
    		 {
        		 actionToLog = "";
    			 isInsertRecord = false;
    			 JsonObject jsonIngName = (JsonObject)drugClassiArr.get(cnt);
    			 inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(jsonIngName.toString());
    			 String drugClassName = jsonIngName.get("drugClassName").getAsString();
    			 String drugClassID = jsonIngName.get("drugClassID").getAsString();
    			 String drugClassType = Util.trimStr(jsonIngName.get(DRUGCLASSTYPE).getAsString());
    			 if("TC2".equalsIgnoreCase(drugClassType))
				 {
    				 strQueryB.append("primaryclassification=:primaryclassification ,");
    				 paramMap.put("primaryclassification", drugClassName);
    				 isDataFound = true;
				 }
    			 else if("TC4".equalsIgnoreCase(drugClassType))
				 {
    				 strQueryB.append("secondaryClassification=:secondaryClassification ,");
    				 paramMap.put(SECONDARY_CLASSIFICATION, drugClassName);
    				 isDataFound = true;
				 }
    			 else if("TC6".equalsIgnoreCase(drugClassType))
				 {
    				 strQueryB.append("tertiaryClassification=:tertiaryClassification ");
    				 paramMap.put(TERTIARY_CLASSIFICATION, drugClassName);
    				 isDataFound = true;
				 }    
    			 if(isDataFound)
    			 {
				 	StringBuilder strNewQueryB = new StringBuilder();
    			 	paramMapNew.clear();
    			 	strNewQueryB.append("select id from ip_drugformulary_drug_classification where formularyid=:formularyId and drugClassType=:drugClassType and delflag=0 ");
    			 	paramMapNew.put(FORMULARYID, nFormularyId);
    			 	paramMapNew.put(DRUGCLASSTYPE, drugClassType);
 	        		aSize = namedParameterJdbcTemplate.queryForList(strNewQueryB.toString(), paramMapNew); 
 	        		
 	        		if(aSize.isEmpty())
 	        		{
 	        			isInsertRecord = true;
 	        		}
 	        		paramMapNew.clear();
 	        		strNewQueryB.setLength(0);
 	        		strNewQueryB.append("update ip_drugformulary_drug_classification set drugClassID=:drugClassID,drugClassName=:drugClassName,modifiedby=:modifiedby,modifiedon=:modifiedon)");
            		strNewQueryB.append("where formularyid=:formularyId and drugClassType=:drugClassType and delflag=0 ");	
            		actionToLog = MODIFIED;
            		if(isInsertRecord)
 	        		{
            			actionToLog = CREATED;
            			strNewQueryB.setLength(0);
            			strNewQueryB.append("insert into ip_drugformulary_drug_classification(drugClassID,drugClassName,createdby,createdon,modifiedby,modifiedon,formularyid,drugClassType,userid)");
            			strNewQueryB.append("values(:drugClassID,:drugClassName,:createdby,:createdon,:modifiedby,:modifiedon,:formularyId,:drugClassType,:userid)");
 	        		}
 	        		paramMapNew.put("drugClassID", drugClassID);
 	        		paramMapNew.put("drugClassName", drugClassName);
 	            	paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
 	            	paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
 	            	paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
 	            	paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
     	            paramMapNew.put(FORMULARYID, nFormularyId);
     	            paramMapNew.put(DRUGCLASSTYPE, drugClassType);
     	            paramMapNew.put(USER_ID,String.valueOf(nTrUserId));
 	                namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
    			 }
    			 auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonForLog, "Specific Setting -> Product Details -> Classification");
    		 }
    		 if(isDataFound)
    		 {
        		 paramMap.put(FORMULARYID, nFormularyId);
                 strQueryB.append(" where id=:formularyId and delflag=0 ");
                 namedParameterJdbcTemplate.update(strQueryB.toString(), paramMap);
    		 } 
		}
		catch(Exception ex)
		{
			 EcwLog.AppendExceptionToLog(ex);
		}
	}
	
	//ahfs classification
	public void insertUpdateAhfsClassification(String strAhfsClassiJsonData,int nFormularyId,int nTrUserId)
	{
		Map<String, Object> paramMapNew = new HashMap<>();
		List<Map<String, Object>> aSize = null;
		boolean isInsertRecord = false;		
		JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(strAhfsClassiJsonData);
    	JsonArray ahfsClassiArr = jo.getAsJsonArray("ahfsClassificationList");
		 for(int cnt = 0;cnt < ahfsClassiArr.size();cnt++)
		 {
			 isInsertRecord = false;
			 JsonObject jsonIngName = (JsonObject)ahfsClassiArr.get(cnt);
			 String ahfsClassName = jsonIngName.get(AHFS_CLASS_NAME).getAsString();
			 String ahfsClassID = jsonIngName.get(AHFS_CLASS_ID).getAsString();
			 if(!"".equals(ahfsClassID))
			 {
			 	StringBuilder strNewQueryB = new StringBuilder();
			 	paramMapNew.clear();
			 	strNewQueryB.append("select id from ip_drugformulary_ahfs_classification where formularyid=:formularyId and ahfsClassID=:ahfsClassID and delflag=0 ");
			 	paramMapNew.put(FORMULARYID, nFormularyId);
			 	paramMapNew.put(AHFS_CLASS_ID, ahfsClassID);
        		aSize = namedParameterJdbcTemplate.queryForList(strNewQueryB.toString(), paramMapNew); 
        		strNewQueryB.setLength(0);
        		if(aSize.isEmpty())
        		{
        			isInsertRecord = true;
        		}
        		paramMapNew.clear();
    		 	strNewQueryB.setLength(0);  
    			strNewQueryB.append("update ip_drugformulary_ahfs_classification set ahfsClassName=:ahfsClassName,modifiedby=:modifiedby,modifiedon=:modifiedon)");
    			strNewQueryB.append("where formularyid=:formularyId and ahfsClassID=:ahfsClassID and delflag=0 ");	
        		if(isInsertRecord)
        		{
        			strNewQueryB.setLength(0);
        			strNewQueryB.append("insert into ip_drugformulary_ahfs_classification(ahfsClassName,createdby,createdon,modifiedby,modifiedon,formularyid,ahfsClassID,userid)");
        			strNewQueryB.append("values(:ahfsClassName,:createdby,:createdon,:modifiedby,:modifiedon,:formularyId,:ahfsClassID,:userid)");
        		}
				paramMapNew.put(AHFS_CLASS_NAME, ahfsClassName);
				paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
				paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
				paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
			    paramMapNew.put(FORMULARYID, nFormularyId);
 	            paramMapNew.put(AHFS_CLASS_ID, ahfsClassID);
 	            paramMapNew.put(USER_ID,String.valueOf(nTrUserId));
                namedParameterJdbcTemplate.update(strNewQueryB.toString(), paramMapNew);
			 }
        }
	}
	//pre-requisite vitals
	public int insertUpdateForPreVitals(int nFormularyId,String jsonVitalsObj,int nRoutedGenericItemId,int nTrUserId) throws org.json.JSONException, JSONException{
		int newId = 0;
		StringBuilder strSql = new StringBuilder();
		StringBuilder strQ = new StringBuilder();
    	JSONArray arrJSON = new JSONArray(jsonVitalsObj);
    	
    	if(arrJSON.length() > 0)
    	{
    		deletePreVitalsData(nFormularyId, nRoutedGenericItemId, nTrUserId);
    	}
    	for(int i=0;i<arrJSON.length();i++)
    	{
    		JSONObject jsonData = arrJSON.getJSONObject(i);
    		inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(arrJSON.getJSONObject(i).toString());
    		String vitalId =String.valueOf(jsonData.getInt(RIGHT_SIDE_ID));
    		String actionToLog="";
    		
    		if(!"".equals(vitalId)) 
    		{
        		//CHECK FOR data is present for routed generic item id in common setting table
        		List<Map<String, Object>> aSize = null;
        		
        		MapSqlParameterSource paramMapFac1 = new MapSqlParameterSource();
        		paramMapFac1.addValue(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
        		paramMapFac1.addValue(N_FORMULARY_ID,nFormularyId);    	
        		paramMapFac1.addValue(RIGHT_SIDE_ID,vitalId);
        		strQ.setLength(0);
            	strQ.append("select routedgenericitemid from ip_formulary_prerequisite where routedgenericitemid=:nRoutedGenericItemId and rightsideid=:rightsideid and delflag=0 ");
        		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapFac1);
        		if(aSize.isEmpty())
        		{
        			actionToLog = CREATED;
        			strQ.setLength(0);
        			strQ.append(" select ifp.routedgenericitemid from ip_formulary_prerequisite ifp ");
        			strQ.append(" where ifp.routedgenericitemid=:nRoutedGenericItemId and ifp.rightsideid=:rightsideid and ifp.delflag=0 ");
            		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapFac1);
            		if(!aSize.isEmpty())
            		{//if data is found 
            			strSql.append("INSERT INTO ip_formulary_prerequisite(leftsideid,rightsideid,createdby,createdon,modifiedby,modifiedon,userid,");
	            		strSql.append("routedgenericitemid)");
	                	strSql.append("values(:leftsideid,:rightsideid,:createdby,:createdon,:modifiedby,:modifiedon,:userid,");
	                	strSql.append(":nRoutedGenericItemId)");
            		}
            		else
            		{
            			strSql.append("INSERT INTO ip_formulary_prerequisite(leftsideid,rightsideid,createdby,createdon,modifiedby,modifiedon,userid,");
	            		strSql.append(" routedgenericitemid)");
	                	strSql.append("values(:leftsideid,:rightsideid,:createdby,:createdon,:modifiedby,:modifiedon,:userid,");
            			strSql.append(" :nRoutedGenericItemId)");
            		}
        			
            	}
            	else
            	{
            		actionToLog = MODIFIED;
            		strSql.append("update ip_formulary_prerequisite set leftsideid=:leftsideid,rightsideid=:rightsideid,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userid");
            		strSql.append(" where routedgenericitemid=:nRoutedGenericItemId and rightsideid=:rightsideid and delflag=0 ");
            	}
            
            	paramMapFac1.addValue(LEFT_SIDE_ID,"1");//purposely hard coded for vitals
            	paramMapFac1.addValue(CREATED_BY,String.valueOf(nTrUserId));
            	paramMapFac1.addValue(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
            	paramMapFac1.addValue(MODIFIED_BY,String.valueOf(nTrUserId)); 
            	paramMapFac1.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
            	paramMapFac1.addValue(USER_ID,String.valueOf(nTrUserId)); 
        		paramMapFac1.addValue(IS_DEFAULT,"1");
            	namedParameterJdbcTemplate.update(strSql.toString(), paramMapFac1);
            	strSql.setLength(0);
            	newId ++;
            	
            	//audit log
            	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonForLog, FORMULARY_SETUP_PRE_REQ);
    		}
    	}
        return newId;
	}

	private void deletePreVitalsData(int nFormularyId, int nRoutedGenericItemId, int nTrUserId) throws JSONException {
		List<TemplateForPreVitals> tmpllist = getPreRequisiteVitalsDetails(nFormularyId,String.valueOf(nRoutedGenericItemId));
		final String preqList = new com.google.gson.Gson().toJson(tmpllist);
		inpatientWeb.Global.ecw.ambulatory.json.JSONArray jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(preqList);
		if(jsonArray.length() > 0)
		{
			inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = jsonArray.getJSONObject(0);
			auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, FORMULARY_SETUP_PRE_REQ);
			
			MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
			StringBuilder sql= new StringBuilder("update ip_formulary_prerequisite set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userid ");
			sql.append(" where routedgenericitemid=:routedgenericitemid ");
			sql.append(" and delflag=0  ");
			paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
			paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
			paramMapFac.addValue(USER_ID,String.valueOf(nTrUserId));
			paramMapFac.addValue(ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
			namedParameterJdbcTemplate.update(sql.toString(), paramMapFac);
		}
	}
	
	//order type setup
	public int insertUpdateAssociatedProducts(int drugId,String jsonData,int nTrUserId) throws JSONException, UnsupportedEncodingException{
		Map<String, Object> paramMapNew = new HashMap<>();
		Map<String, Object> paramMap = new HashMap<>();
   		int newId = 0;
   		int updNewId = 0;
		StringBuilder strSql = new StringBuilder("");
    	JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(jsonData);
        JsonArray jsonDiluentArr = jo.getAsJsonArray("AssociatedProducts");
        
        Map<String, Object> aMapDrugInfo = null;
        
        for(int cnt = 0;cnt < jsonDiluentArr.size();cnt++)
        {
        	boolean isInsertRecord = true;
        	int nItemId = 0;
        	String actionToLog = "";
        	
        	inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(jsonDiluentArr.get(cnt).toString());
        	JsonObject jsonProduct = (JsonObject)jsonDiluentArr.get(cnt);
        	String idAssociatedProduct = jsonProduct.get("id")!=null?jsonProduct.get("id").getAsString():"";
        	String ndc  = jsonProduct.get("ndc").getAsString();
        	String upc = jsonProduct.get("upc")!=null?jsonProduct.get("upc").getAsString():"";
        	String ppid = jsonProduct.get("ppid")!=null?jsonProduct.get("ppid").getAsString():"";
        	String packSize = jsonProduct.get("packSize").getAsString();
        	String packSizeUnitCode = jsonProduct.get("packSizeUnitCode").getAsString();
        	String packQuantity = jsonProduct.get(PACK_QUANTITY).getAsString();
        	String packType = jsonProduct.get(PACK_TYPE).getAsString();
        	String marketEndDate = jsonProduct.get(MARKET_END_DATE).getAsString();
        	String awp = jsonProduct.get("awp").getAsString();
        	String awup = jsonProduct.get("awup")!=null?jsonProduct.get("awup").getAsString():"";
        	String ndc10 = jsonProduct.get(NDC10)!=null?jsonProduct.get(NDC10).getAsString():"";
        	String manufacturerName = jsonProduct.get(MANUFACTURE_NAME)!=null?jsonProduct.get(MANUFACTURE_NAME).getAsString():"";
        	String manufacturerIdentifier = jsonProduct.get(MANUFACTURE_IDENTIFIER).getAsString();
        	String mvxCode = jsonProduct.get(MVX_CODE)!=null? jsonProduct.get(MVX_CODE).getAsString():"";
        	String costToProc = jsonProduct.get(COST_TO_PROC)!=null?jsonProduct.get(COST_TO_PROC).getAsString():"";
        	String unitCost = jsonProduct.get("unitcost")!=null?jsonProduct.get("unitcost").getAsString():"";
        	String status = jsonProduct.get(STATUS)!=null?jsonProduct.get(STATUS).getAsString():"1";
        	String isPrimary = jsonProduct.get("isprimary")!=null?jsonProduct.get("isprimary").getAsString():"0";
        	String routedDrugId = jsonProduct.get(ROUTED_DRUG_ID).getAsString();
        	String rxnorm = jsonProduct.get(RX_NORM)!=null?jsonProduct.get(RX_NORM).getAsString():"";
        	String productName = "";
        	String isVfc = "";
        	String isSingleDosagePack = "";
        	productName = jsonProduct.get(PRODUCT_NAME).getAsString();
        	isVfc = jsonProduct.get(IS_VFC).getAsString();
        	isSingleDosagePack = jsonProduct.get(ISSINGLEDOSAGEPACK)!=null?jsonProduct.get(ISSINGLEDOSAGEPACK).getAsString():"";
        	 
        	//to get locally stored itemid from routeddrugid - ms-clinical
        	if (routedDrugId != null && !"".equals(routedDrugId)) {
				aMapDrugInfo = medHelperService.getDrugDetailMapFromRoutedDrugID(StringUtil.getIntValue(routedDrugId), "");
				if(aMapDrugInfo != null && !aMapDrugInfo.isEmpty()){
					nItemId = Util.getIntValue(aMapDrugInfo,ITEMID1,0);
				}
			}
        	if(nItemId<=0)
        	{
        		throw new InvalidParameterException("Invalid Item Id");
        	}
        	
        	JsonArray jsonLotEnquiryArr = null;
        	if(jsonProduct.get(LOTENQUIRY)!=null && jsonProduct.get(LOTENQUIRY).isJsonArray())
        	{
        		jsonLotEnquiryArr = jsonProduct.get(LOTENQUIRY).getAsJsonArray();
        	}
        	
        	if(!"".equals(idAssociatedProduct) && !"0".equals(idAssociatedProduct))
        	{
	        	strSql.setLength(0);            	
	        	strSql.append("select id from ip_drugformulary_associatedProducts where formularyid=:formularyid and id=:idAssociatedProduct and delflag=0 ");
	        	paramMap.put(FORMULARY_ID,drugId);
	        	paramMap.put("idAssociatedProduct",idAssociatedProduct);
	        	paramMapNew.put("id",idAssociatedProduct);
	        	
	        	List<Template> tmplList = namedParameterJdbcTemplate.query(strSql.toString(), paramMap, new RowMapper<Template>(){
					@Override
					public Template mapRow(ResultSet rs, int arg1) throws SQLException {
						Template tmpl=new Template();
						tmpl.setId(rs.getInt("id"));
						return tmpl;
					}
	    		});
	    		Iterator<Template> it = tmplList.iterator();
	    		if(it.hasNext())
	    		{
	    			Template t1 = it.next();
	    			updNewId = t1.getId();
	    			isInsertRecord = false;
	    		}
	    		paramMap.clear();
        	}
    		
    		strSql.setLength(0);
        	strSql.append(" insert into ip_drugformulary_associatedProducts(ppid,packsize,packsizeunitcode,packquantity,packtype,awp,");
        	strSql.append(" manufacturerName,manufacturerIdentifier,mvxCode,cost_to_proc,unitcost,status,isprimary,marketEndDate,createdby,createdon,modifiedby,modifiedon,userId,notes,itemid,routedDrugId,productName,isVfc,isSingleDosagePack,formularyid,ndc,rxnorm,upc,awup,ndc10)");
        	strSql.append(" values(:ppid,:packsize,:packsizeunitcode,:packquantity,:packtype,:awp,");
        	strSql.append(" :manufacturerName,:manufacturerIdentifier,:mvxCode,:cost_to_proc,:unitcost,:status,:isprimary,:marketEndDate,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:notes,:itemid,:routedDrugId,:productName,:isVfc,:isSingleDosagePack,:formularyid,:ndc,:rxnorm,:upc,:awup,:ndc10)"); 
        	
        	paramMapNew.put(FORMULARY_ID,drugId);
        	paramMapNew.put("ppid",ppid);
        	paramMapNew.put(PACK_SIZE,packSize);
        	paramMapNew.put(PACKSIZE_UNIT,packSizeUnitCode);
        	paramMapNew.put("packquantity",packQuantity);
        	paramMapNew.put("packtype",packType);
        	paramMapNew.put("awp",awp);
        	paramMapNew.put("awup",awup);
        	paramMapNew.put(NDC10,ndc10);
        	paramMapNew.put(MANUFACTURE_NAME,manufacturerName);
        	paramMapNew.put(MANUFACTURE_IDENTIFIER,manufacturerIdentifier);
        	paramMapNew.put(MVX_CODE,mvxCode);
        	paramMapNew.put(COST_TO_PROC,costToProc);
        	paramMapNew.put("unitcost",unitCost);
        	paramMapNew.put(STATUS,status);
        	paramMapNew.put("isprimary",isPrimary);
        	paramMapNew.put(MARKET_END_DATE,marketEndDate);
        	
        	paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    		paramMapNew.put(NOTES,"");
    		paramMapNew.put(ITEMID,nItemId);
    		paramMapNew.put(ROUTED_DRUG_ID,routedDrugId);
    		paramMapNew.put(PRODUCT_NAME,productName);
    		paramMapNew.put(IS_VFC,isVfc);
    		paramMapNew.put(ISSINGLEDOSAGEPACK,isSingleDosagePack);
    		paramMapNew.put(RX_NORM,rxnorm);
    		paramMapNew.put("upc",upc);
    		paramMapNew.put("ndc",ndc);
    		
        	if(!isInsertRecord)
        	{
        		strSql.setLength(0);
            	strSql.append(" update ip_drugformulary_associatedProducts set ppid=:ppid,packsize=:packsize,packsizeunitcode=:packsizeunitcode,packquantity=:packquantity,packtype=:packtype,awp=:awp,");
            	strSql.append(" manufacturerName=:manufacturerName,manufacturerIdentifier=:manufacturerIdentifier,mvxCode=:mvxCode,cost_to_proc=:cost_to_proc,unitcost=:unitcost,status=:status,");
            	strSql.append(" isprimary=:isprimary,marketEndDate=:marketEndDate,modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,notes=:notes,itemid=:itemid,routedDrugId=:routedDrugId,");
            	strSql.append(" productName=:productName,isVfc=:isVfc,isSingleDosagePack=:isSingleDosagePack,rxnorm=:rxnorm,upc=:upc,awup=:awup,ndc10=:ndc10 where formularyid=:formularyid and id=:id and delflag=0 ");
            	
            	namedParameterJdbcTemplate.update(strSql.toString(), paramMapNew);
       			newId = updNewId;
       			actionToLog = MODIFIED;
        	}
        	else
        	{
        		actionToLog = CREATED;
        		newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql.toString(), paramMapNew, "id");
        	}
        	paramMapNew.clear();
        	insertUpdateAssociatedProductsLotEnquiry(drugId,newId,jsonLotEnquiryArr,nTrUserId);
        	
        	//audit log
        	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonForLog, "Specific Setting -> Associated Product");
        }
        return newId;
    }
	
	//insert update lot enquiry
	public int insertUpdateAssociatedProductsLotEnquiry(int drugId,int nAssoProdId,JsonArray jsonLotEnquiryArr,int nTrUserId){
		Map<String, Object> paramMapNew = new HashMap<>();
   		int newId = 0;
		StringBuilder strSql = new StringBuilder("");
    	if(jsonLotEnquiryArr==null || drugId <= 0)
    	{
    		return -1;
    	}
    	
    	for(int cnt = 0;cnt < jsonLotEnquiryArr.size();cnt++)
        {
        	boolean isInsertRecord = true;
        	int updNewId = 0;
        	JsonObject jsonProduct = (JsonObject)jsonLotEnquiryArr.get(cnt);
        	String lotNo = jsonProduct.get(LOTNO).getAsString();
        	String lotType= jsonProduct.get(LOTTYPE).getAsString();
        	String lotExpDate = jsonProduct.get("expiryDate").getAsString();
        	if(!"".equals(lotNo) && !"".equals(lotExpDate))
    		{
            	strSql.setLength(0);            	
            	strSql.append("select id from ip_drugformulary_asso_products_lot_enquiry where assoProductId=:assoProductId and formularyid=:formularyid and lotno=:lotno  and delflag=0 ");
           
            	paramMapNew.put(ASSO_PRODUCT_ID,nAssoProdId);
            	paramMapNew.put(FORMULARY_ID,drugId); 
            	paramMapNew.put(LOTNO,lotNo);
            	
            	List<Template> tmplList = namedParameterJdbcTemplate.query(strSql.toString(), paramMapNew, new RowMapper<Template>(){
    				@Override
    				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
    					Template tmpl=new Template();
    					tmpl.setId(rs.getInt("id"));
    					return tmpl;
    				}
        		});
        		Iterator<Template> it = tmplList.iterator();
        		if(it.hasNext())
        		{
        			Template t1 = it.next();
        			updNewId = t1.getId();
        			isInsertRecord = false;
        		}
            	 
        		strSql.setLength(0);
            	strSql.append(" insert into ip_drugformulary_asso_products_lot_enquiry(expirydate,createdby,createdon,modifiedby,modifiedon,userId,notes,lotType,assoProductId,formularyid,lotno)");
            	strSql.append(" values(:expirydate,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:notes,:lotType,:assoProductId,:formularyid,:lotno)"); 
            	
            	paramMapNew.put(EXPIRYDATE,lotExpDate);
            	paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        		paramMapNew.put(NOTES,"");
            	paramMapNew.put(LOTTYPE,lotType);
            	
            	if(!isInsertRecord)
            	{
            		strSql.setLength(0);
                	strSql.append(" update ip_drugformulary_asso_products_lot_enquiry set expirydate=:expirydate,modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,notes=:notes,lotType=:lotType ");
                	strSql.append(" where assoProductId=:assoProductId and formularyid=:formularyid and lotno=:lotno "); 
                	namedParameterJdbcTemplate.update(strSql.toString(), paramMapNew);
           			newId = updNewId;
            	}
            	else
            	{
            		newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql.toString(), paramMapNew, "id");
            	} 
    		}
        }
        return newId;
    }
	
	//delete OTS routes
	public void deleteOTSRoutes(int nDrugId,int nOTSId,int nTrUserId){
    	if(nOTSId > 0)
    	{
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_OTS_Route set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:formularyid and ordertypeid=:ordertypeid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USERID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,nDrugId);
        	paramMapFac.addValue(ORDER_TYPE_ID,nOTSId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
    	}
	}
	
	private boolean isDataFoundInArray(String[] strInArr,String strRoute)
	{
		boolean isFound = false;
		for(int j=0;j<strInArr.length;j++)
		{
			if(Util.trimStr(strInArr[j]).equals(strRoute))
			{
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	private List<String> checkIfExist(List<TemplateForOTS> tmplList)
	{
		boolean isInsertRecord = true;
		List<String> strList = new ArrayList<>();
		String updId = "0";
		if(!tmplList.isEmpty())
    	{
    		Iterator<TemplateForOTS> it = tmplList.iterator();
    		if(it.hasNext())
    		{
    			TemplateForOTS tempOTS = it.next();
    			updId = tempOTS.getId();
    			if(!"0".equals(Util.trimStr(updId)))
    			{
    				isInsertRecord = false;	
    			}
    		}
    	}
		strList.add(updId);
		strList.add(String.valueOf(isInsertRecord));
		return strList;
	}
	private void callToProcessOTSFrequency(int drugId,int newId,int nTrUserId,String strSelectedOTSFrequencyList,String recommendedFrequency,String vendorName)
	{
		if(!"".equals(strSelectedOTSFrequencyList))
		{
			String[] strFrequencyArr = Util.trimStr(strSelectedOTSFrequencyList).split(",");
			String[] strRecommFrequencyArr = Util.trimStr(recommendedFrequency).split(",");
			deleteOTSFrequency(drugId,newId,nTrUserId);
			
			for(int cnt=0;cnt<strFrequencyArr.length;cnt++)
			{
				String strFrequencyId = strFrequencyArr[cnt];
				boolean isRecommended = false;
				isRecommended = isDataFoundInArray(strRecommFrequencyArr,strFrequencyId);
				insertUpdateOTSFrequency(drugId,newId,strFrequencyId,nTrUserId,isRecommended,vendorName);
			}
		}
		else
		{//if there is no checkbox selected then it will call to delete
			deleteOTSFrequency(drugId,newId,nTrUserId);
		}
	}
	
	private void callToProcessOTSFormulation(int drugId,int newId,int nTrUserId,String strSelectedOTSFormulationList)
	{
		if(!"".equals(strSelectedOTSFormulationList))
		{
			String[] strFormulationArr = Util.trimStr(strSelectedOTSFormulationList).split(",");
			deleteOTSFormulation(drugId,newId,nTrUserId);
			
			for(int cnt=0;cnt<strFormulationArr.length;cnt++)
			{
				String strFormulationId = strFormulationArr[cnt];
				insertUpdateOTSFormulation(drugId,newId,strFormulationId,nTrUserId);
			}
		}
		else
		{
			deleteOTSFormulation(drugId,newId,nTrUserId);
		}
	}
	
	private void callToProcessOTSRoute(int drugId,int newId,int nTrUserId,String strSelectedOTSRouteList,String recommendedRoutes,String vendorName)
	{
		//route list
		if(!"".equals(strSelectedOTSRouteList))
		{
			String[] strRoutesArr = Util.trimStr(strSelectedOTSRouteList).split(",");
			String[] strRecommRoutesArr = Util.trimStr(recommendedRoutes).split(",");
			//before insert and update it will remove all data 
			deleteOTSRoutes(drugId,newId,nTrUserId);
	    	
			for(int cnt=0;cnt<strRoutesArr.length;cnt++)
			{
				boolean isRecommended = true;
				String strRouteId = strRoutesArr[cnt];
				isRecommended = isDataFoundInArray(strRecommRoutesArr,strRouteId);
				insertUpdateOTSRoutes(drugId,newId,strRouteId,nTrUserId,isRecommended,vendorName);
			}
		}
		else
		{//if there is no checkbox selected then it will call to delete
			deleteOTSRoutes(drugId,newId,nTrUserId);
		}
	}
	//insert update OTS routes
	private int insertUpdateOTSRoutes(int nDrugId,int nOTSId,String nRouteId,int nTrUserId,boolean isRecommended,String vendorName){
		Map<String,Object> paramMapNew = new HashMap<>();
		Map<String,Object> paramMap = new HashMap<>();
        try {
        	if(nDrugId > 0 && nOTSId > 0 && !"0".equals(Root.TrimInteger(nRouteId)))
        	{
	        	boolean isInsertRecord = true;
	        	
	        	String strSql = "select id from ip_drugformulary_OTS_Route where formularyid=:formularyid and ordertypeid=:ordertypeid and routeId=:routeId and delflag=0 ";
	        	paramMap.put(FORMULARY_ID,nDrugId);
	        	paramMap.put(ORDER_TYPE_ID,nOTSId);
	        	paramMap.put("routeId",nRouteId);
	        	
	        	List<TemplateRoutes> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateRoutes>() {
					@Override
					public TemplateRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplateRoutes tmpl=new TemplateRoutes();
						tmpl.setId(rs.getInt("id")); 
						newRouteId = rs.getInt("id");
						return tmpl;
					}
	        	});
	        	if(newRouteId > 0 && !tmplList.isEmpty())
	        	{
	        		isInsertRecord = false;
	        	}
	        	
        		StringBuilder strSqlB =new StringBuilder(" insert into ip_drugformulary_OTS_Route(ordertypeid,routeid,formularyid,createdby,createdon,modifiedby,modifiedon,userId,isRecommended,vendorName) ");
        		strSqlB.append("values(:ordertypeid,:routeid,:formularyid,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:isRecommended,:vendorName)"); 
        		
        		paramMapNew.put(ORDER_TYPE_ID,nOTSId);
	        	paramMapNew.put(ROUTE_ID,nRouteId);
	        	paramMapNew.put(FORMULARY_ID,nDrugId);
	        	paramMapNew.put(USERID,String.valueOf(nTrUserId));
	        	paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	        	paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        	paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        	paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        	paramMapNew.put("isRecommended",(isRecommended?1:0));
	        	paramMapNew.put("vendorName",vendorName);
	        	paramMapNew.put("id",newRouteId);
	        	
        		if(!isInsertRecord)
	        	{
        			strSqlB.setLength(0);
        			strSqlB.append(" update ip_drugformulary_OTS_Route set ordertypeid=:ordertypeid,routeid=:routeid,formularyid=:formularyid,modifiedby=:modifiedby,");
        			strSqlB.append(" modifiedon=:modifiedon,userId =:userId,isRecommended=:isRecommended,vendorName=:vendorName where id=:id "); 
        			namedParameterJdbcTemplate.update(strSql, paramMapNew);
	        	}
        		else
        		{
        			newRouteId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSqlB.toString(), paramMapNew, "id");
        		}
        	}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return newRouteId;
	}
	
	
	//insert update Facilities
	public int insertUpdateFacilitiesList(int nDrugId,String facilitiesList,int nTrUserId){
		int newId = 0;
		int nFacilityId = 0;
		String strFacId = "";
        try {
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_facilities set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:formularyid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USERID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,nDrugId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
        	
    		String[] facilityArr = facilitiesList.split(",");
    		for(int fac = 0; fac < facilityArr.length; fac++)
    		{
    			strFacId = Util.trimStr(facilityArr[fac]);
    			if(!"".equals(strFacId))
    			{
    				nFacilityId = Integer.parseInt(strFacId);
    			}
    			if(nFacilityId > 0)
    			{
		        	String strSql = " insert into ip_drugformulary_facilities(facilityid,formularyid,createdby,createdon,modifiedby,modifiedon,userId) "
		            			+" values(:facilityid,:formularyid,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"; 
		        	
		        	Map<String, Object> paramMapNew = new HashMap<>();
	        		paramMapNew.put(FACILITY_ID,nFacilityId);
		        	paramMapNew.put(FORMULARY_ID,nDrugId); 
	        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
		        	
	        		newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
    			}
    		}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return newId;
	}
	
	
	//insert update service type list
	public int insertUpdateServiceTypeList(int nDrugId,String serviceTypeList,int nTrUserId){
		int newId = 0;
		int nServiceTypeId = 0;
		String strServiceTypeId = "";
        try {
        	
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_regservicetype set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:formularyid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USERID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,nDrugId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
        	
    		String[] serviceTypeArr = serviceTypeList.split(",");
    		for(int fac = 0; fac < serviceTypeArr.length; fac++)
    		{
    			strServiceTypeId = Util.trimStr(serviceTypeArr[fac]);
    			if(!"".equals(strServiceTypeId))
    			{
    				nServiceTypeId = Integer.parseInt(strServiceTypeId);
    			}
    			if(nServiceTypeId > 0)
    			{
		        	String strSql = " insert into ip_drugformulary_regservicetype(serviceid,formularyid,createdby,createdon,modifiedby,modifiedon,userId) "
		            			+" values(:serviceid,:formularyid,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"; 
		        	
		        	Map<String, Object> paramMapNew = new HashMap<>();
	        		paramMapNew.put("serviceid",nServiceTypeId);
		        	paramMapNew.put(FORMULARY_ID,nDrugId); 
	        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
		        	
	        		newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
    			}
    		}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return newId;
	}
	
	//delete OTS formulation
	public void deleteOTSFormulation(int nDrugId,int nOTSId,int nTrUserId){
    	if(nOTSId > 0)
    	{
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_OTS_Formulation set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:formularyid and ordertypeid=:ordertypeid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USERID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,nDrugId);
        	paramMapFac.addValue(ORDER_TYPE_ID,nOTSId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
    	}
	}
	
	//insert update OTS formulation
	private int insertUpdateOTSFormulation(int nDrugId,int nOTSId,String nFormId,int nTrUserId){
			Map<String,Object> paramMapNew = new HashMap<>();
			Map<String,Object> paramMap = new HashMap<>();
	        try {
	        	if(nDrugId > 0 && nOTSId > 0 && !"0".equals(Root.TrimInteger(nFormId)))
	        	{
		        	boolean isInsertRecord = false; 
		        	
		        	String strSql = "select id,formulationid from ip_drugformulary_OTS_Formulation where formularyid=:formularyid and ordertypeid=:ordertypeid and formulationid=:formulationid and delflag=0 ";
		        	paramMap.put(FORMULARY_ID,nDrugId);
		        	paramMap.put(ORDER_TYPE_ID,nOTSId);
		        	paramMap.put(FORMULATIONID,nFormId);
		        	
		        	List<TemplateForOTFormulation> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateForOTFormulation>() {
						@Override
						public TemplateForOTFormulation mapRow(ResultSet rs, int arg1) throws SQLException {
							TemplateForOTFormulation tmpl=new TemplateForOTFormulation();
							tmpl.setOrderTypeFormulationid(rs.getString(FORMULATIONID)); 
							newFormulationId = rs.getInt("id");
							return tmpl;
						}
		        	});
		        	
		        	if(tmplList.isEmpty())
		        	{
		        		isInsertRecord = true;
		        		strSql = " insert into ip_drugformulary_OTS_Formulation(ordertypeid,formulationid,formularyid,createdby,createdon,modifiedby,modifiedon,userId) "
		            			+" values(:ordertypeid,:formulationid,:formularyid,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"; 
		        	}
		        	else
		        	{
		        		strSql = " update ip_drugformulary_OTS_Formulation set ordertypeid=:ordertypeid,formulationid=:formulationid,formularyid=:formularyid,modifiedby=:modifiedby,modifiedon=:modifiedon,"
		        				+" userId=:userId where id=:id "; 
		        	}
		        	paramMapNew.put(ORDER_TYPE_ID,nOTSId);
		        	paramMapNew.put(FORMULATIONID,nFormId);
		        	paramMapNew.put(FORMULARY_ID,nDrugId);
		        	if(isInsertRecord)
		        	{
		        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
		        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
		        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
		        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
		        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
		        	}
		        	else
		        	{
		        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
		        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
		        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
		        		paramMapNew.put("id",newFormulationId);
		        	}
		       		if(isInsertRecord)
		       		{
		       			newFormulationId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
		       		}
		       		else
		       		{
		       			namedParameterJdbcTemplate.update(strSql, paramMapNew);
		       		}
	        	}
	        } catch (Exception e) {
	            EcwLog.AppendExceptionToLog(e);
	        }
	        return newFormulationId;
		}
	
	//insert update iv diluent details
	public int insertUpdateIVDiluent(int drugId,int nOTSId,JsonArray jsonDiluentArr,int nTrUserId)
	{
		Map<String,Object> paramMapNew = new HashMap<>();
		int ivformularyId = -1;
		int ivDiluentId = -1;
		String dispensesize = "";
		String dispensesizeuom = "";
		String dose = "";
		String doseuom = "";
		
    	if(nOTSId > 0)
    	{
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_diluent set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userid where formularyid=:formularyid and ordertypesetupid=:ordertypesetupid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USER_ID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,drugId);
        	paramMapFac.addValue(ORDERTYPE_SETUP_ID,nOTSId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
        	
        	for(int i=0;i<jsonDiluentArr.size();i++)
        	{        		
        		JsonObject jsonProduct = (JsonObject)jsonDiluentArr.get(i);
        		//product
        		ivformularyId = jsonProduct.get(IVFORMULARYID).getAsInt();
        		dispensesize = jsonProduct.get(DISPENSE_SIZE).getAsString();
        		dispensesizeuom = jsonProduct.get(DISPENSE_SIZE_UOM).getAsString();
        		dose = jsonProduct.get("dose").getAsString();
        		doseuom = jsonProduct.get(DOSE_UOM).getAsString();
	        	
	        	paramMapNew.put(FORMULARY_ID,drugId);
	        	paramMapNew.put(IVFORMULARYID,ivformularyId);
	        	
	        	paramMapNew.put(DISPENSE_SIZE,RxUtil.preSanitizeStringAsDouble(dispensesize,true));
	        	paramMapNew.put(DISPENSE_SIZE_UOM,dispensesizeuom);
	        	paramMapNew.put("dose",RxUtil.preSanitizeStringAsDouble(dose,true));
	        	paramMapNew.put(DOSE_UOM,doseuom);
	        	
        		paramMapNew.put(ORDERTYPE_SETUP_ID,nOTSId);
        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        		
	        	String strSql = " insert into ip_drugformulary_diluent(formularyid,ivformularyid,ordertypesetupid,dispensesize,dispensesizeuom,dose,doseuom,createdby,createdon,modifiedby,modifiedon,userId) "
            			+" values(:formularyid,:ivformularyid,:ordertypesetupid,:dispensesize,:dispensesizeuom,:dose,:doseuom,:createdby,:createdon,:modifiedby,:modifiedon,:userId)";
	        	ivDiluentId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
        	}
    	}
        return ivDiluentId;
	}
	
	
	//insert update iv rate details
	public int insertUpdateIVRate(int drugId,int nOTSId,JsonArray jsonIVRateArr,int nTrUserId)
	{
		Map<String,Object> paramMapNew = new HashMap<>();
		String ivrate = null;
		String ivrateuom = null;
		int ivRateId = -1;
		
    	if(nOTSId > 0)
    	{
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_iv_rate set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userid where formularyid=:formularyid and ordertypesetupid=:ordertypesetupid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USER_ID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,drugId);
        	paramMapFac.addValue(ORDERTYPE_SETUP_ID,nOTSId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
        	
        	for(int i=0;i<jsonIVRateArr.size();i++)
        	{        		
        		JsonObject jsonIVRate = (JsonObject)jsonIVRateArr.get(i);
        		//product
        		ivrate = jsonIVRate.get("ivrate").getAsString();
        		ivrateuom = jsonIVRate.get("ivrateuom").getAsString();
        			
        		String strSql = " insert into ip_drugformulary_iv_rate(formularyid,ordertypesetupid,strength,strengthuom,createdby,createdon,modifiedby,modifiedon,userId) "
	            			+" values(:formularyid,:ordertypesetupid,:strength,:strengthuom,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"; 

        		paramMapNew.put(FORMULARY_ID,drugId);
	        	paramMapNew.put(ORDERTYPE_SETUP_ID,nOTSId);
        		paramMapNew.put(STRENGTH,ivrate);
        		paramMapNew.put(STRENGTH_UOM,ivrateuom);
        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        	
        		ivRateId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
        	}
    	}
        return ivRateId;
	}
	
	
	//delete OTS frequency
	public void deleteOTSFrequency(int nDrugId,int nOTSId,int nTrUserId){
    	if(nOTSId > 0)
    	{
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="update ip_drugformulary_OTS_Frequency set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:formularyid and ordertypeid=:ordertypeid and delflag=0 ";
        	paramMapFac.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapFac.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapFac.addValue(USERID,String.valueOf(nTrUserId));
        	paramMapFac.addValue(FORMULARY_ID,nDrugId);
        	paramMapFac.addValue(ORDER_TYPE_ID,nOTSId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
    	}
	}
	//insert update OTS frequency
	private int insertUpdateOTSFrequency(int nDrugId,int nOTSId,String nFreqId,int nTrUserId,boolean isRecommended,String vendorName){
		Map<String,Object> paramMapNew = new HashMap<>();
		Map<String,Object> paramMap = new HashMap<>();
		
        try {
        	if(nDrugId > 0 && nOTSId > 0 && !"0".equals(Root.TrimInteger(nFreqId)))
        	{
	        	boolean isInsertRecord = true; 
	        	String strSql = "select id,freqid from ip_drugformulary_OTS_Frequency where formularyid=:formularyid and ordertypeid=:ordertypeid and freqid=:freqid and delflag=0 ";
	        	paramMap.put(FORMULARY_ID,nDrugId);
	        	paramMap.put(ORDER_TYPE_ID,nOTSId);
	        	paramMap.put(FREQID,nFreqId);
	        	
	        	List<TemplateForOTFrequency> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateForOTFrequency>() {
					@Override
					public TemplateForOTFrequency mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplateForOTFrequency tmpl=new TemplateForOTFrequency();
						tmpl.setOrderTypeFrequencyid(rs.getString(FREQID)); 
						newFrequencyId = rs.getInt("id");
						return tmpl;
					}
	        	});
	        	if(newFrequencyId > 0 && !tmplList.isEmpty())
	        	{ 
	        		isInsertRecord = false;
	        	}
	        	
	        	paramMapNew.put(ORDER_TYPE_ID,nOTSId);
	        	paramMapNew.put(FREQID,nFreqId);
	        	paramMapNew.put(FORMULARY_ID,nDrugId);
        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        		paramMapNew.put("isRecommended",(isRecommended?1:0));
        		paramMapNew.put("vendorName",vendorName);
	        	paramMapNew.put("id",newFrequencyId);
	        	
	        	strSql = " insert into ip_drugformulary_OTS_Frequency(ordertypeid,freqid,formularyid,createdby,createdon,modifiedby,modifiedon,userId,isRecommended,vendorName) "
	            		+" values(:ordertypeid,:freqid,:formularyid,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:isRecommended,:vendorName)"; 
	        	
	        	if(!isInsertRecord)
	        	{
	        		strSql = " update ip_drugformulary_OTS_Frequency set ordertypeid=:ordertypeid,freqid=:freqid,formularyid=:formularyid,modifiedby=:modifiedby,modifiedon=:modifiedon,"
	        				+" userId=:userId,isRecommended=:isRecommended,vendorName=:vendorName  where id=:id "; 
	        		namedParameterJdbcTemplate.update(strSql, paramMapNew);
	        	}
        		else
        		{
        			newFrequencyId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
        		}
        	}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return newFrequencyId;
	}
	
	//route list from ms-clinical
	public int insertUpdateRouteListExternal(int formularyId,String dosingRouteList,int nTrUserId){
		int newId = 0;
    	if(!"".equals(dosingRouteList))
    	{
        	JsonParser jsonParser = new JsonParser();
			JsonObject jo = (JsonObject) jsonParser.parse(dosingRouteList);
			JsonArray frequencyListArr = jo.getAsJsonArray("dosingRouteList");
			for (int cnt = 0; cnt < frequencyListArr.size(); cnt++) {
				Map<String, Object> paramMapNew = new HashMap<>(); 
				Map<String, Object> paramMap = new HashMap<>();
				
				JsonObject jsonIngName = (JsonObject) frequencyListArr.get(cnt);
				String routeID = jsonIngName.get("routeID").getAsString();
				String routeName = jsonIngName.get("routeName").getAsString();
				boolean isInsertRecord = true;
				
	        	String strSql = "select ext_mapping_id from ip_drugformulary_routes_external where ext_mapping_id=:ext_mapping_id1 and formularyid=:formularyid and delflag=0 ";
	        	paramMap.put("ext_mapping_id1",routeID);
	        	paramMap.put(FORMULARY_ID,formularyId);
	        	
	        	List<TemplateRoutes> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateRoutes>() {
					@Override
					public TemplateRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplateRoutes tmpl=new TemplateRoutes();
						tmpl.setRouteID(rs.getString(EXT_MAPPING_ID)); 
						return tmpl;
					}
	        	});
	        	if(!tmplList.isEmpty())
	        	{
	        		isInsertRecord = false;
	        	}
	        	
        		strSql = " insert into ip_drugformulary_routes_external(formularyid,ext_mapping_id,ext_mapping_code,ext_mapping_desc,createdby,createdon,modifiedby,modifiedon,userId) "
            			+" values(:formularyid,:ext_mapping_id,:ext_mapping_code1,:ext_mapping_desc1,:createdby,:createdon,:modifiedby,:modifiedon,:userId)";
        		
        		paramMapNew.put(FORMULARY_ID,formularyId);
	        	paramMapNew.put(EXT_MAPPING_ID,routeID);
	        	paramMapNew.put("ext_mapping_code1",routeName);
	        	paramMapNew.put("ext_mapping_desc1",routeName);
        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        		
	        	if(!isInsertRecord)
	        	{
	        		strSql = " update ip_drugformulary_routes_external set ext_mapping_id=:ext_mapping_id,ext_mapping_code=:ext_mapping_code1,ext_mapping_desc=:ext_mapping_desc1,modifiedby=:modifiedby,modifiedon=:modifiedon,"
	        				+" userId=:userId where ext_mapping_id=:ext_mapping_id and formularyid=:formularyid  "; 
	        		namedParameterJdbcTemplate.update(strSql, paramMapNew);
	        	}
	        	else
	        	{
	        		newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
	        	}
			}
    	}
        return newId;
	}
	//insert update immunization vis data
	public int insertUpdateFormularyImmunizationData(int formularyId,String strVisData,int nTrUserId) throws org.json.JSONException{

		Map<String, Object> paramMapNew = new HashMap<>();
		Map<String,Object> paramMap = new HashMap<>();
		StringBuilder strQ = new StringBuilder("");
		int newId = 0;
		List<Map<String, Object>> aSize = null;
		boolean isInsertRecord = true;
		
    	String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);
    	JSONArray arrJSON = new JSONArray(strVisData);
    	for(int i=0;i<arrJSON.length();i++)
    	{
    		JSONObject jsonData = arrJSON.getJSONObject(i);
    		strQ.setLength(0);
    		paramMap.clear();
			isInsertRecord = true;
			int nItemId = jsonData.getInt(ITEM_ID);
			
			if(nItemId > 0)
			{
				String strDateOnVIS = jsonData.getString("DateOnVIS");
				strDateOnVIS = CwUtils.ConvertDateFormat(strDateOnVIS, "MM/dd/yyyy", "yyyy-MM-dd HH:mm:ss");
				
				strQ.append("select itemid from ip_drugformulary_ItemVISMapping where formularyid=:formularyid and itemid=:itemid and delflag=0 ");
        		paramMap.put(FORMULARY_ID, formularyId);
        		paramMap.put(ITEMID, nItemId);
        		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMap);
        		if(!aSize.isEmpty())
        		{
        			isInsertRecord = false;
        		}
        		
        		strQ.setLength(0);
				paramMap.clear();
				strQ.append(" insert into ip_drugformulary_ItemVISMapping(dateonvis,viscvxcode,displayindex,createdby,createdon,modifiedon,modifiedby,userid,notes,formularyid,itemid)");
				strQ.append(" values(:dateonvis,:viscvxcode,:displayindex,:createdby,:createdon,:modifiedon,:modifiedby,:userid,:notes,:formularyid,:itemid)");
    		
    			if(!isInsertRecord)
        		{
        			strQ.setLength(0);
    				paramMap.clear();
    				strQ.append(" update ip_drugformulary_ItemVISMapping set dateonvis=:dateonvis,viscvxcode=:viscvxcode,displayindex=:displayindex,modifiedon=:modifiedon,modifiedby=:modifiedby,userid=:userid,notes=:notes ");
    				strQ.append(" where formularyid=:formularyid and itemid=:itemid and delflag=0 ");
        		}
        		paramMapNew.put("dateonvis",strDateOnVIS);
        		paramMapNew.put("viscvxcode",jsonData.getString("VISCVXCode"));
        		paramMapNew.put("displayindex",jsonData.getString("displayIndex"));
    			paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(CREATED_ON,currentDateTime);
        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        		paramMapNew.put(MODIFIED_ON,currentDateTime);
        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
        		paramMapNew.put(NOTES,"");
        		paramMapNew.put(FORMULARY_ID,formularyId);
        		paramMapNew.put(ITEMID,nItemId);
        		
	       		if(isInsertRecord)
	       		{
	       			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strQ.toString(), paramMapNew, "itemVISId");
	       		}
	       		else
	       		{
	       			namedParameterJdbcTemplate.update(strQ.toString(), paramMapNew);
	       			newId = nItemId;//assigned itemid because uid is not required to display
	       		}
			}
		}
        return newId;
	}
	
	//frequency list from ms-clinical
	public int insertUpdateFrequencyListExternal(int formularyId, String frequencyList, int nTrUserId) {
	
		int newId = 0;
		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(frequencyList);
		JsonArray frequencyListArr = jo.getAsJsonArray("frequencyList");
		for (int cnt = 0; cnt < frequencyListArr.size(); cnt++) {
			Map<String, Object> paramMapNew = new HashMap<>(); 
			Map<String, Object> paramMap = new HashMap<>();
			
			JsonObject jsonIngName = (JsonObject) frequencyListArr.get(cnt);
			String extMappingCode = jsonIngName.get(EXTMAPPINGCODE).getAsString();
			String extMappingDesc = jsonIngName.get(EXT_MAPPING_DESC).getAsString();
			boolean isInsertRecord = false;

			String strSql = "select ext_mapping_desc from ip_drugformulary_frequency_external where ext_mapping_desc=:ext_mapping_desc and formularyid=:formularyid and delflag=0 ";
			paramMap.put(EXT_MAPPING_DESC, extMappingDesc);
			paramMap.put(FORMULARY_ID, formularyId);

			List<TemplateFrequency> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap,
					new RowMapper<TemplateFrequency>() {
						@Override
						public TemplateFrequency mapRow(ResultSet rs, int arg1) throws SQLException {
							TemplateFrequency tmpl = new TemplateFrequency();
							tmpl.setExt_mapping_desc(rs.getString(EXT_MAPPING_DESC));
							return tmpl;
						}
					});
			
			if (tmplList.isEmpty()) {
				isInsertRecord = true; 
			}

			strSql = " insert into ip_drugformulary_frequency_external(formularyid,ext_mapping_code,ext_mapping_desc,createdby,createdon,modifiedby,modifiedon,userId) "
					+ " values(:formularyid,:ext_mapping_code,:ext_mapping_desc,:createdby,:createdon,:modifiedby,:modifiedon,:userId)";
			
			if (!isInsertRecord) {
				strSql = " update ip_drugformulary_frequency_external set ext_mapping_code=:ext_mapping_code,modifiedby=:modifiedby,modifiedon=:modifiedon,"
						+ " userId=:userId where ext_mapping_desc=:ext_mapping_desc and formularyid=:formularyid ";
			}
			paramMapNew.put(FORMULARY_ID, formularyId);
			paramMapNew.put(EXTMAPPINGCODE, extMappingCode);
			paramMapNew.put(EXT_MAPPING_DESC, extMappingDesc);
			if (isInsertRecord) {
				paramMapNew.put(CREATED_BY, String.valueOf(nTrUserId));
				paramMapNew.put(CREATED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(MODIFIED_BY, String.valueOf(nTrUserId));
				paramMapNew.put(MODIFIED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(USERID, String.valueOf(nTrUserId));
			} else {
				paramMapNew.put(MODIFIED_BY, String.valueOf(nTrUserId));
				paramMapNew.put(MODIFIED_ON, IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
				paramMapNew.put(USERID, String.valueOf(nTrUserId));
			}
			if (isInsertRecord) {
				newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
			} else {
				namedParameterJdbcTemplate.update(strSql, paramMapNew);
			}
	}
		return newId;
	}
	
	//frequency list from ms-clinical
		public int insertUpdateFormulationListExternal(int formularyId,String formulationList,int nTrUserId) throws IOException{
			Map<String,Object> paramMapNew = new HashMap<>();
			Map<String,Object> paramMap = new HashMap<>();
			int newId = 0;
        	if(!"".equals(formulationList))
        	{
	        	boolean isInsertRecord = true;
	        	TemplateFormulation formulation = new ObjectMapper().readValue(formulationList, TemplateFormulation.class);
	        	 
	        	String strSql = "select ext_mapping_desc from ip_drugformulary_formulation_external where ext_mapping_desc=:ext_mapping_desc and formularyid=:formularyid and delflag=0 ";
	        	paramMap.put(EXT_MAPPING_DESC,formulation.getExt_mapping_desc());
	        	paramMap.put(FORMULARY_ID,formularyId);
	        	
	        	List<TemplateFormulation> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateFormulation>() {
					@Override
					public TemplateFormulation mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplateFormulation tmpl=new TemplateFormulation();
						tmpl.setExt_mapping_desc(rs.getString(EXT_MAPPING_DESC));
						return tmpl;
					}
	        	});
	        	if(!tmplList.isEmpty())
	        	{
	        		isInsertRecord = false;
	        	}
	        	
	        	if(isInsertRecord)
	        	{
	        		strSql = " insert into ip_drugformulary_formulation_external(formularyid,ext_mapping_id,ext_mapping_code,ext_mapping_desc,createdby,createdon,modifiedby,modifiedon,userId) "
	            			+" values(:formularyid,:ext_mapping_id,:ext_mapping_code,:ext_mapping_desc,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"; 
	        	}
	        	else
	        	{
	        		strSql = " update ip_drugformulary_formulation_external set ext_mapping_id=:ext_mapping_id,ext_mapping_code=:ext_mapping_code,ext_mapping_desc=:ext_mapping_desc,modifiedby=:modifiedby,modifiedon=:modifiedon,"
	        				+" userId=:userId where ext_mapping_desc=:ext_mapping_desc and formularyid=:formularyid "; 
	        	}
	        	paramMapNew.put(FORMULARY_ID,formularyId);
	        	paramMapNew.put(EXT_MAPPING_ID,formulation.getExt_mapping_id());
	        	paramMapNew.put(EXTMAPPINGCODE,formulation.getExt_mapping_code());
	        	paramMapNew.put(EXT_MAPPING_DESC,formulation.getExt_mapping_desc());
	        	if(isInsertRecord)
	        	{
	        		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
	        	}
	        	else
	        	{
	        		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        		paramMapNew.put(USERID,String.valueOf(nTrUserId));
	        		paramMapNew.put("id",formularyId);
	        	}
	        	if(isInsertRecord)
	       		{
	       			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql, paramMapNew, "id");
	       		}
	       		else
	       		{
	       			namedParameterJdbcTemplate.update(strSql, paramMapNew);
	       		}
        	}
	        return newId;
		}
	
	//drug alias // not in use
	public int insertUpdateDrugAlias(String jsonDrugData,int nTrUserId) throws IOException{
		MapSqlParameterSource  paramMapNew = new MapSqlParameterSource ();
		MapSqlParameterSource  paramMapDup = new MapSqlParameterSource ();
		int newId = 0;
    	boolean isInsertRecord = true;
    	TemplateForDrugAlias drug = new ObjectMapper().readValue(jsonDrugData, TemplateForDrugAlias.class);
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	 
    	String strSql = "select code from ip_drug_alias where code=:code and formularyid=:formularyid and delflag=0 ";
    	paramMapDup.addValue("code",drug.getCode());
    	paramMapDup.addValue(FORMULARY_ID,drug.getFormularyid());
        
    	List<TemplateForDrugAlias> tmplListDup = namedParameterJdbcTemplate.query(strSql, paramMapDup, new RowMapper<TemplateForDrugAlias>() {
			@Override
			public TemplateForDrugAlias mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrugAlias tmpl=new TemplateForDrugAlias();
				tmpl.setCode(rs.getString("code"));
				return tmpl;
			}
    	});
    	
    	if(!tmplListDup.isEmpty())
    	{
    		strSql = " update ip_drug_alias set formularyid =:formularyid,modifiedby =:modifiedby,modifiedon =:modifiedon,"
    				+" userId =:userId,notes =:notes where code=:code "; 
        	isInsertRecord = false;
    	}
    	else
    	{	
    		strSql = " insert into ip_drug_alias(formularyid,code,createdby,createdon,modifiedby,modifiedon,userId,notes) "
    			+" values(:formularyid,:code,:createdby,:createdon,:modifiedby,:modifiedon,:userId,:notes)"; 
    	}
    	
    	paramMapNew.addValue(FORMULARY_ID,drug.getFormularyid());
    
    	
    	if(isInsertRecord)
    	{
    		paramMapNew.addValue("code",drug.getCode());
    		paramMapNew.addValue(CREATED_BY,String.valueOf(nTrUserId));
    		paramMapNew.addValue(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.addValue(USERID,String.valueOf(nTrUserId));
    		paramMapNew.addValue(NOTES,drug.getNotes());
    	}
    	else
    	{
    		paramMapNew.addValue(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.addValue(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.addValue(USERID,String.valueOf(nTrUserId));
    		paramMapNew.addValue(NOTES,drug.getNotes());
    		paramMapNew.addValue("code",drug.getCode());
    	}
   		namedParameterJdbcTemplate.update(strSql, paramMapNew, keyHolder);
   		if(isInsertRecord)
   		{
   			newId = keyHolder.getKey().intValue();
   		}
   		else
   		{
   			newId = Integer.parseInt(drug.getId()); 
   		}
        return newId;
	}
	//get drug list counter
	public int getFormularySetupDrugCounter(FormularySearchParam fSearchParamObj)
	{
		String searchBy = fSearchParamObj.getSearchBy();
		String searchValue = fSearchParamObj.getSearchValue();
		String status = fSearchParamObj.getStatus();
		Map<String,Object> paramMap = new HashMap<>();
        StringBuilder strSQL = new StringBuilder();
		String strSelect = " SELECT count(distinct drug.id) as cnt ";
    	String strFromLeft = " FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.routedGenericItemId = i.itemid and drug.delflag=0 ";
    	strFromLeft += " left join ip_items i1 on drug.itemid = i1.itemid and i1.deleteflag=0 ";
    	String strWhereLeft = WHERE_ONE_ONE;
    	if(!"".equals(status))
    	{
    		if("1".equals(status))
    		{
    			strWhereLeft += " and drug.isactive=1 ";
    		}  
    		else if("0".equals(status))
    		{
    			strWhereLeft += " and drug.isactive=0 ";
    		}
    	}
    	if(!"".equals(searchValue))
    	{
        	if("NDC".equalsIgnoreCase(searchBy))
        	{
        		strWhereLeft += " and drug.ndc like :searchValueLeft  ";
        	}
        	else if("NAME".equalsIgnoreCase(searchBy))
        	{
        		strWhereLeft += " and ( i.itemname like :searchValueLeft or drug.assigned_brand_itemname like :searchValueLeft) ";
        	}
        	
        	paramMap.put(SEARCH_VALUE_LEFT, "%"+searchValue+"%");
        	paramMap.put(SEARCH_VALUE_RIGHT, "%"+searchValue+"%");
    	}
    	strSQL.append(" select * from (");
    	strSQL.append(strSelect + strFromLeft + strWhereLeft);
    	strSQL.append(")as q ");
    	try
    	{
    		return namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, Integer.class);	
		}catch (DataAccessException e) {
			return 0;
		}	
	}
	
	//get druglist from component
	public int getFormularySetupDrugCounterForComponent(FormularySearchParam fSearchParamObj)
	{
		String searchBy = fSearchParamObj.getSearchBy();
		String searchValue = fSearchParamObj.getSearchValue();
		Map<String,Object> paramMap = new HashMap<>();
        StringBuilder strSQL = new StringBuilder();
		String strSelect = " SELECT count(distinct i.itemid)as q ";
    	String strFromLeft = "  FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.routedGenericItemId = i.itemid and drug.delflag=0 and drug.isActive=1 ";
    	String strWhereLeft = WHERE_ONE_ONE;
    	if(!"".equals(searchValue))
    	{
    		if("NAME".equalsIgnoreCase(searchBy))
        	{
    			strWhereLeft += " and ( i.itemname like :searchValueLeft or drug.assigned_brand_itemname like :searchValueLeft) ";
        	}
        	paramMap.put(SEARCH_VALUE_LEFT, "%"+searchValue+"%");
    	}
    	strSQL.append(" select * from (");
    	strSQL.append(strSelect + strFromLeft + strWhereLeft);
    	strSQL.append(")as q ");
    	try
    	{
    		return namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, Integer.class);
		}catch (DataAccessException e) {
			return 0;
		}
    	
	}
	
	//get order type setup routes
	public List<TemplateForOTRoutes> getOrderTypeSetupRoutes(String formularyId){
		Map<String,Object> paramMap = new HashMap<>();
    	// Order type setup - Routes
    	StringBuilder strSQL = new StringBuilder("");
    	strSQL.append(" SELECT ip_drugformulary_routes_external.id as ext_mapping_id,ip_drugformulary_routes.id,ip_drugformulary_routes.routecode,");
    	strSQL.append(" ip_drugformulary_routes.routedesc,ip_drugformulary_routes.isDefault ");
    	strSQL.append(" FROM ip_drugformulary ");
    	strSQL.append(" INNER JOIN ip_drugformulary_routes_external ON ip_drugformulary.id = ip_drugformulary_routes_external.formularyid "); 
    	strSQL.append(" and ip_drugformulary.id=:formularyId and ip_drugformulary.delflag=0 ");
    	strSQL.append(" right JOIN ip_drugformulary_routes ON ip_drugformulary_routes_external.ext_mapping_code = ip_drugformulary_routes.extmappingcode "); 
    	strSQL.append(" where ip_drugformulary_routes.status=1 and ip_drugformulary_routes.deleteflag=0 ");
    	strSQL.append(" order by ip_drugformulary_routes_external.id desc ");
    	
    	paramMap.put(FORMULARYID,formularyId);
    	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForOTRoutes>(){
			@Override
			public TemplateForOTRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForOTRoutes tmpl=new TemplateForOTRoutes();
				tmpl.setExtMappingId(rs.getString(EXT_MAPPING_ID ));
				tmpl.setOrderTypeRouteid(rs.getString("id"));
				tmpl.setOrderTypeRouteCode(rs.getString(ROUTE_CODE));
				tmpl.setOrderTypeRouteDescr(rs.getString(ROUTE_DESC));
				tmpl.setIsDefault(rs.getInt(ISDEFAULT));
				return tmpl;
			}
        });
	}
	
	//get order type setup frequency
	 public List<TemplateForOTFrequency> getOrderTypeSetupFrequency(String formularyId){
		Map<String,Object> paramMap = new HashMap<>();
    	StringBuilder strSQL = new StringBuilder("");
    	// Order type setup - Frequency
    	strSQL.append(" SELECT ip_drugformulary_frequency_external.id as ext_mapping_id,ip_drugformulary_frequency.id, ");
    	strSQL.append(" ip_drugformulary_frequency.freqCode,ip_drugformulary_frequency.freqDesc ");
    	strSQL.append(" FROM ip_drugformulary ");
    	strSQL.append(" INNER JOIN ip_drugformulary_frequency_external ON ip_drugformulary.id = ip_drugformulary_frequency_external.formularyid  ");
    	strSQL.append(" AND ip_drugformulary.id=:formularyId and ip_drugformulary.delflag=0 ");
    	strSQL.append(" right JOIN ip_drugformulary_frequency ON ip_drugformulary_frequency_external.ext_mapping_desc = ip_drugformulary_frequency.extfreqmappingcode ");
    	strSQL.append(" where  ip_drugformulary_frequency.deleteflag=0 AND ip_drugformulary_frequency.status=1 ");
    	strSQL.append(" order by ip_drugformulary_frequency_external.id desc,ip_drugformulary_frequency.freqCode ");
    	
    	paramMap.put(FORMULARYID,formularyId);
    	
    	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForOTFrequency>(){
			@Override
			public TemplateForOTFrequency mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForOTFrequency tmpl=new TemplateForOTFrequency();
				tmpl.setExtMappingId(rs.getString(EXT_MAPPING_ID ));
				tmpl.setOrderTypeFrequencyid(rs.getString("id"));
				tmpl.setOrderTypeFrequencyCode(rs.getString(FREQCODE));
				tmpl.setOrderTypeFrequencyDescr(rs.getString(FREQDESC));
				return tmpl;
			}
        });
	}
	
	//get order type setup formulation
	public List<TemplateForOTFormulation> getOrderTypeSetupFormulation(String formularyId){
		Map<String,Object> paramMap = new HashMap<>();
    	StringBuilder strSQL = new StringBuilder("");
    	// Order type setup - Formulation
    	strSQL.append(" SELECT ip_drugformulary_formulation.id,ip_drugformulary_formulation.formulationcode,ip_drugformulary_formulation.formulationdesc "); 
    	strSQL.append(" FROM ip_drugformulary INNER JOIN ip_drugformulary_formulation_external ON ip_drugformulary.id = ip_drugformulary_formulation_external.formularyid and ip_drugformulary.delflag=0 "); 
		strSQL.append(" INNER JOIN ip_drugformulary_formulation  ON ip_drugformulary_formulation_external.ext_mapping_desc = ip_drugformulary_formulation.extmappingcode ");
		strSQL.append(" WHERE ip_drugformulary_formulation.deleteflag=0  and ip_drugformulary.id=:formularyId and ip_drugformulary_formulation.status=1 ");
		paramMap.put(FORMULARYID,formularyId);
		
		return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForOTFormulation>(){
			@Override
			public TemplateForOTFormulation mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForOTFormulation tmpl=new TemplateForOTFormulation();
				tmpl.setOrderTypeFormulationid(rs.getString("id"));
				tmpl.setOrderTypeFormulationCode(rs.getString("formulationcode"));
				tmpl.setOrderTypeFormulationDescr(rs.getString("formulationdesc"));
				return tmpl;
			}
        });
	}
	
	public List<TemplateForOTS> getOrderTypeSetupData(String formularyId)
	{
		TemplateForOTS ots = new TemplateForOTS();
		List<TemplateForOTS> otsList = null;
		List<TemplateForOTRoutes> tmplList = getOrderTypeSetupRoutes(formularyId);
		List<TemplateForOTFrequency> tmplFreqList = getOrderTypeSetupFrequency(formularyId);
		List<TemplateForOTFormulation> tmplFormList = getOrderTypeSetupFormulation(formularyId);
		final String routelist = new com.google.gson.Gson().toJson(tmplList);
		final String freqList = new com.google.gson.Gson().toJson(tmplFreqList);
		final String formList = new com.google.gson.Gson().toJson(tmplFormList);
		
		ots.setRouteList(routelist);
		ots.setFrequencyList(freqList);
		ots.setFormulationList(formList);
		ots.setFormularyid(formularyId);
		otsList = new ArrayList<>();
		otsList.add(ots);		
		return otsList;
	}
	
	
	/*
	 * initialize common setting with default values 
	 * @param nFormularyId
	 * @param jsonDrugData
	 * @param nTrUserId
	 * @author NisarAhmad
	 * @return id of ip_drugfomulary_common_settings table.
	 * */
	
	private int insertIntoCommonSettingWithDefaultValues(int nFormularyId,int nRoutedGenericItemId,String jsonDrugData,int nTrUserId) throws IOException, JSONException
	{
		Map<String, Object> paramMapNew = new HashMap<>();
		StringBuilder strQ = new StringBuilder("");
		List<Map<String, Object>>aSize = null; 
		int no = 0;
		if(nFormularyId > 0 && nRoutedGenericItemId <= 0)
		{
			nRoutedGenericItemId =  getRoutedGenericItemId(nFormularyId);
		}
		paramMapNew.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
		strQ.append(" select routedgenericitemid from ip_drugformulary_common_settings where routedgenericitemid=:nRoutedGenericItemId and delflag=0 ");
		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapNew);
		if(aSize.isEmpty())
		{
			no = updateCommonSettingDrugData(nFormularyId,nRoutedGenericItemId,jsonDrugData,nTrUserId);
		}
		return no;
	}
	
	//update drug formulary common setting based on routed generic itemid
	public int updateCommonSettingDrugData(int nFormularyId,int nRoutedGenericItemId,String jsonDrugData,int nTrUserId) throws IOException, JSONException{
		Map<String, Object> paramMapNew = new HashMap<>();
		StringBuilder strQ = new StringBuilder("");
		List<Map<String, Object>>aSize = null; 
		boolean isInsertRecord = false;
		String actionToLog = "";

		//CHECK FOR data is present for routed generic item id in common setting table
    	paramMapNew.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	paramMapNew.put(N_FORMULARY_ID,nFormularyId);    	
    	
    	strQ.append(" select routedgenericitemid, drugAlias as oldDrugAliasName from ip_drugformulary_common_settings where routedgenericitemid=:nRoutedGenericItemId and delflag=0 ");
		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapNew);
		String oldDrugAliasName = "";
		if(aSize.isEmpty())
		{
			isInsertRecord = true;
			strQ.setLength(0);
			strQ.append(" insert into ip_drugformulary_common_settings(lookAlike,titlePreview,titleStyle,isRT,varianceLimit,");
			strQ.append(" varianceLimitUnit,varianceLimitAfter,varianceLimitAfterUnit,dualverifyreqd,");
			strQ.append(" issearchable,");
			strQ.append(" isrestrictedoutsideOS,isrestricted,isIVDiluent,");
			strQ.append(" createdby,createdon,modifiedby,modifiedon,userId,isAdditive,routedgenericitemid,isMandatoryForOE,isImmunization,isppd,isRenewInExpiringTab,imm_itemid,drugAlias)");
			strQ.append(" values(:lookAlike,:titlePreview,:titleStyle,:isRT,:varianceLimit,");
			strQ.append(" :varianceLimitUnit,:varianceLimitAfter,:varianceLimitAfterUnit,:dualverifyreqd,");
			strQ.append(" :issearchable, ");
			strQ.append(" :isrestrictedoutsideOS,:isrestricted,:isIVDiluent,");
			strQ.append(" :createdby,:createdon,:modifiedby,:modifiedon,:userId,:isAdditive,:nRoutedGenericItemId,:isMandatoryForOE,:isImmunization,:isppd,:isRenewInExpiringTab,:immItemId,:drugAlias) ");
		}
		else
		{
			strQ.setLength(0);
			strQ.append(" update ip_drugformulary_common_settings set lookAlike=:lookAlike,titlePreview=:titlePreview,titleStyle=:titleStyle,isRT=:isRT,varianceLimit=:varianceLimit, ");
			strQ.append(" varianceLimitUnit=:varianceLimitUnit,varianceLimitAfter=:varianceLimitAfter,varianceLimitAfterUnit=:varianceLimitAfterUnit,dualverifyreqd=:dualverifyreqd,");
			strQ.append(" issearchable=:issearchable,");
			strQ.append(" isrestrictedoutsideOS=:isrestrictedoutsideOS,isrestricted=:isrestricted,isIVDiluent=:isIVDiluent, ");
			strQ.append(" modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,isAdditive=:isAdditive, ");
			strQ.append(" isMandatoryForOE=:isMandatoryForOE,isImmunization=:isImmunization,isppd=:isppd,isRenewInExpiringTab=:isRenewInExpiringTab,imm_itemid=:immItemId,drugAlias=:drugAlias ");
			strQ.append(" where routedgenericitemid=:nRoutedGenericItemId ");
			oldDrugAliasName = Util.getStrValue(aSize.get(0), "oldDrugAliasName", "");
		}
		
			
    	TemplateForDrug drug = new ObjectMapper().readValue(jsonDrugData, TemplateForDrug.class);
    	paramMapNew.put(LOOK_ALIKE,drug.getLookAlike());
    	paramMapNew.put("titlePreview",drug.getTitlePreview());
    	paramMapNew.put("titleStyle",drug.getTitleStyle());
    	paramMapNew.put("isRT",drug.getIsRT());
    	paramMapNew.put(VARIANCE_LIMIT,drug.getVarianceLimit());
    	paramMapNew.put(VARIANCE_LIMIT_UNIT,drug.getVarianceLimitUnit());
    	paramMapNew.put(VARIANCE_LIMIT_AFTER,drug.getVarianceLimitAfter());
    	paramMapNew.put(VARIANCE_LIMIT_AFTER_UNIT,drug.getVarianceLimitAfterUnit());
    	paramMapNew.put(DUALVERIFYREQD,drug.getDualverifyreqd());
    	paramMapNew.put(ISSEARCHABLE,drug.getIssearchable());
    	paramMapNew.put(ISRESTRICTEDOUTSIDEOS,drug.getIsrestrictedoutsideOS());
    	paramMapNew.put(ISRESTRICTED,drug.getIsrestricted());
    	paramMapNew.put(ISIVDILUENT1,drug.getIsIVDiluent());
    	
		
		if(isInsertRecord)
    	{
			actionToLog = CREATED;
    		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    	}
    	else
    	{
    		actionToLog = MODIFIED;
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));	
    	}
		paramMapNew.put(ISADDITIVE, drug.getIsAdditive());
		paramMapNew.put("isMandatoryForOE", drug.getIsMandatoryForOE());
		paramMapNew.put(ISIMMUNIZATION, drug.getIsImmunization());
		paramMapNew.put("immItemId",drug.getImmItemId());
		paramMapNew.put(DRUG_ALIAS,drug.getDrugAlias());
		paramMapNew.put(ISPPD, drug.getIsppd());
		paramMapNew.put(ISRENEWINEXPIRINGTAB, drug.getIsRenewInExpiringTab());
		
		namedParameterJdbcTemplate.update(strQ.toString(), paramMapNew);
	 
       //update the cache. Changes Made by Malav For CPOE
		RxCacheCSUpdateRequest rxCacheCSUpdateRequest = new RxCacheCSUpdateRequest();
		rxCacheCSUpdateRequest.setRoutedGenericItemId(String.valueOf(nRoutedGenericItemId));
		rxCacheCSUpdateRequest.setOldDrugAliasName(oldDrugAliasName);
		rxCacheCSUpdateRequest.setNewDrugAliasName(drug.getDrugAlias());
        rxOrderTreeService.updateCommonSetting(rxCacheCSUpdateRequest);

       //audit log
       inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(jsonDrugData);
   	   auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonForLog, "Common Setting -> Drug Details");
   		
        return nRoutedGenericItemId;
	}
	
	public int insertUpdateNotesData(int nFormularyId,String jsonNotesObj,int nTrUserId) throws IOException, JSONException{
		Map<String, Object> paramMapNew = new HashMap<>();
		List<Map<String, Object>> aSize = null;
		int newId = 0;
		StringBuilder strQ = new StringBuilder();
		int nRoutedGenericItemId = 0;
		String actionToLog = "";
    	boolean isInsertRecord = false;
    	inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(jsonNotesObj);
    	TemplateForNotes drug = new ObjectMapper().readValue(jsonNotesObj, TemplateForNotes.class);
    	nRoutedGenericItemId = getRoutedGenericItemId(nFormularyId);
    	//CHECK FOR data is present for routed generic item id in common setting table
    	paramMapNew.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	paramMapNew.put(N_FORMULARY_ID,nFormularyId);    	
    	
    	strQ.append("select routedgenericitemid from ip_drugformulary_notes where routedgenericitemid=:nRoutedGenericItemId  ");
    	strQ.append(" and formularyid=:nFormularyId ");
    	strQ.append(" and delflag=0 ");
		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapNew);
		if(aSize.isEmpty())
		{
			isInsertRecord = true;
		}
		
		strQ.setLength(0);
		strQ.append(" insert into ip_drugformulary_notes(orderentry_instr,emar_instr,pharmacy_instr,internal_notes,instructions,createdby,createdon,modifiedby,");
		strQ.append(" modifiedon,userId,notes,routedgenericitemid,formularyid)");
		
		strQ.append(" values(:orderentry_instr,:emar_instr,:pharmacy_instr,:internal_notes,:instructions,");
		strQ.append(" :createdby,:createdon,:modifiedby,");
		strQ.append(":modifiedon,:userId,:notes,:nRoutedGenericItemId,:nFormularyId) "); 
    	
    	if(!isInsertRecord)
    	{
    		strQ.setLength(0);
			strQ.append("update ip_drugformulary_notes set orderentry_instr=:orderentry_instr,emar_instr=:emar_instr,pharmacy_instr=:pharmacy_instr,");
			strQ.append(" internal_notes=:internal_notes,instructions=:instructions,modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,notes=:notes");
			strQ.append(" where routedgenericitemid=:nRoutedGenericItemId and formularyId=:nFormularyId ");
    	}
    	paramMapNew.put(ORDERENTRY_INSTR,drug.getOrderentry());
    	paramMapNew.put(EMAR_INSTR,drug.getEmar());
    	paramMapNew.put(PHARMACY_INSTR,drug.getPharmacy());
    	paramMapNew.put(INTERNAL_NOTES,drug.getInternal());
    	paramMapNew.put(INSTRUCTIONS,drug.getInstructions());
    	
    	if(isInsertRecord)
    	{
    		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    		paramMapNew.put(NOTES,drug.getNotes());
    	}
    	else
    	{
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    		paramMapNew.put(NOTES,drug.getNotes());
    		
    	}
    	
   		if(isInsertRecord)
   		{
   			actionToLog = CREATED;
   			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strQ.toString(), paramMapNew, "id");
   		}
   		else
   		{
   			actionToLog = MODIFIED;
   			namedParameterJdbcTemplate.update(strQ.toString(), paramMapNew);
   			newId = Integer.parseInt(drug.getId()); 
   		}
   		//audit log
    	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonForLog, "Specific Setting -> Notes");
        return newId;
	}
	
	public int insertUpdateCommonSettingNotesData(int nFormularyId,int nRoutedGenericItemId,String jsonNotesObj,int nTrUserId) throws IOException{
		Map<String, Object> paramMapNew = new HashMap<>();
		List<Map<String, Object>> aSize = null;
		int newId = 0;
		StringBuilder strQ = new StringBuilder();
    	boolean isInsertRecord = false;
    	TemplateForNotes drug = new ObjectMapper().readValue(jsonNotesObj, TemplateForNotes.class);
    	
    	//CHECK FOR data is present for routed generic item id in common setting table
    	paramMapNew.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	paramMapNew.put(N_FORMULARY_ID,nFormularyId);    	
    	
    	strQ.append("select routedgenericitemid from ip_drugformulary_notes where routedgenericitemid=:nRoutedGenericItemId  ");
    	strQ.append(" and delflag=0 ");
		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMapNew);
		if(aSize.isEmpty())
		{
			isInsertRecord = true;
		}
		
		strQ.setLength(0);
		strQ.append(" insert into ip_drugformulary_notes(orderentry_instr,emar_instr,pharmacy_instr,internal_notes,instructions,createdby,createdon,modifiedby,");
		strQ.append(" modifiedon,userId,notes,routedgenericitemid,formularyid)");
		
		strQ.append(" values(:orderentry_instr,:emar_instr,:pharmacy_instr,:internal_notes,:instructions,");
		strQ.append(" :createdby,:createdon,:modifiedby,");
		strQ.append(":modifiedon,:userId,:notes,:nRoutedGenericItemId,:nFormularyId) "); 
    	
    	if(!isInsertRecord)
    	{
    		strQ.setLength(0);
			strQ.append("update ip_drugformulary_notes set orderentry_instr=:orderentry_instr,emar_instr=:emar_instr,pharmacy_instr=:pharmacy_instr,");
			strQ.append(" internal_notes=:internal_notes,instructions=:instructions,modifiedby=:modifiedby,modifiedon=:modifiedon,userId=:userId,notes=:notes");
			strQ.append(" where routedgenericitemid=:nRoutedGenericItemId and formularyId=:nFormularyId ");
    	}
    	
    	
    	paramMapNew.put(ORDERENTRY_INSTR,drug.getOrderentry());
    	paramMapNew.put(EMAR_INSTR,drug.getEmar());
    	paramMapNew.put(PHARMACY_INSTR,drug.getPharmacy());
    	paramMapNew.put(INTERNAL_NOTES,drug.getInternal());
    	paramMapNew.put(INSTRUCTIONS,drug.getInstructions());
    	
    	if(isInsertRecord)
    	{
    		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    		paramMapNew.put(NOTES,drug.getNotes());
    	}
    	else
    	{
    		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
    		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
    		paramMapNew.put(USERID,String.valueOf(nTrUserId));
    		paramMapNew.put(NOTES,drug.getNotes());
    		
    	}
    	
   		if(isInsertRecord)
   		{
   			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strQ.toString(), paramMapNew, "id");
   		}
   		else
   		{
   			namedParameterJdbcTemplate.update(strQ.toString(), paramMapNew);
   			newId = Integer.parseInt(drug.getId()); 
   		}
        return newId;
	}	
	//to load associated brand name
	public List<TemplateForDrug> loadBrandNames(final int nRoutedGenericItemId){
		Map<String,Object> paramMap = new HashMap<>();
    	StringBuilder strB = new StringBuilder("  SELECT ipi.itemName,ipd.routedgenericitemid ");
    	strB.append(" FROM ip_drugformulary ipd inner join ip_items ipi on ipd.itemid = ipi.itemid "); 
    	strB.append(" where ipd.routedgenericitemid=:nRoutedGenericItemId and ipd.delflag=0  ");
    	
    	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	
    	return namedParameterJdbcTemplate.query(strB.toString(), paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrug tmpl=new TemplateForDrug();
				String itemName = rs.getString(ITEM_NAME);
				if(!"".equals(itemName) && itemName!=null){
		    		List<CPOERxSearchInterface> listobj = rxOrderTreeService.getListOfEquivalentDrugs(itemName);
                    ArrayList<FormularyBrands> brandNames =  new ArrayList<>();
                    for(CPOERxSearchInterface cpoeSearchObj : listobj){ 
                          if(!cpoeSearchObj.isIsGeneric()){
                                FormularyBrands brandObj = new FormularyBrands();
                                 brandObj.setItemid(cpoeSearchObj.getItemid());
                                 brandObj.setItemName(cpoeSearchObj.getItemName());
                                 brandNames.add(brandObj);
                          }
                    }
		    		tmpl.setBrandNames(brandNames); 
				}
				tmpl.setItemName(itemName);
				return tmpl;
			}
        });
	}
	
	public int getRoutedGenericItemId(int nformularyId)
	{
		int nRoutedGenericItemId = 0;
		Map<String,Object> paramMap = new HashMap<>();
		StringBuilder strQ = new StringBuilder();
		List<TemplateForDrug> tmplList = null;
		paramMap.put(N_FORMULARY_ID,nformularyId);
    	strQ.append("select routedgenericitemid from ip_drugformulary where id=:nFormularyId and delflag=0 ");
    	paramMap.put(ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	tmplList = namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrug tmpl=new TemplateForDrug();
				tmpl.setRoutedGenericItemId(rs.getString(ROUTED_GENERIC_ITEM_ID));
				return tmpl;
			}
    	});
    	Iterator <TemplateForDrug> it = tmplList.iterator();
    	if(it.hasNext())
    	{
    		TemplateForDrug temp = it.next();
    		nRoutedGenericItemId = Integer.parseInt(temp.getRoutedGenericItemId());
    	}
		return nRoutedGenericItemId;
	}
	
	public List<TemplateForCommonSetting> loadCommonSettings(final int nformularyId){
		 List<TemplateForCommonSetting> tmplCommList = null;
		 tmplCommList = loadCommonSettings(getRoutedGenericItemId(nformularyId),nformularyId);
		 return tmplCommList;
	}
	//to load common settings
	public List<TemplateForCommonSetting> loadCommonSettings(final int nRoutedGenericItemId,final int nformularyId){
		Map<String,Object> paramMap = new HashMap<>();
        List<TemplateForCommonSetting> tmplCommList = null;
        List<TemplatePNRIndication> tmplPrnIndication = null;
        strDrugItemIdName = "";
        
    	String sql = "select ipd.id,ipi.itemname,ipd.createdon,ipd.createdby,ipd.modifiedon,ipd.modifiedby from ip_drugformulary ipd inner join ip_items ipi on ipd.routedgenericitemid = ipi.itemID "
    				+" where ipd.routedgenericitemid=:nRoutedGenericItemId and ipd.delflag=0 and ipi.deleteflag=0 ";
    	
    	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	paramMap.put(N_FORMULARY_ID,nformularyId);
    	final List<TemplateForDrug> tmplListNew = namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrug tmpl=new TemplateForDrug();
				tmpl.setItemName(rs.getString(ITEMNAME));
				
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}catch(ParseException ex){
					 EcwLog.AppendExceptionToLog(ex);
				}
				return tmpl;
			} 
    	});
    	Iterator <TemplateForDrug> it = tmplListNew.iterator();  
    	if(it.hasNext())
    	{
    		TemplateForDrug temp = it.next();
    		strDrugItemIdName = (temp.getItemName());
    	}
    	List<TemplatePNRIndication> tmplPrnIndicationList = dictionaryService.getPNRIndicationCountList();
    	
    	StringBuilder strB = new StringBuilder(" SELECT ipd.lookAlike,ipd.TitlePreview,ipd.TitleStyle,ipd.isRT,ipd.varianceLimit,ipd.varianceLimitUnit,ipd.varianceLimitAfter,");
    	 strB.append(" ipd.varianceLimitAfterUnit,ipd.dualverifyreqd,ipd.issearchable,ipd.isrestricted,ipd.isIVDiluent,");
    	 strB.append(" ipd.isrestrictedoutsideOS,ipd.createdby,ipd.createdon,ipd.modifiedby,ipd.modifiedon,ipd.delflag,ipd.userId,ipd.notes,ipd.isAdditive, ");
    	 strB.append(" ipd.isMandatoryForOE,ipd.isImmunization,ipd.isppd,ipd.isRenewInExpiringTab ");
    	 strB.append(" , ipd.imm_itemid as ImmItemId ");
    	 strB.append(" ,it.itemName as ImmItemName ");
    	 strB.append(" ,ipd.drugAlias as drugAlias ");
    	 strB.append(" FROM ip_drugformulary_common_settings ipd ");
    	 strB.append(" left join items it on ipd.imm_itemid = it.itemid and it.deleteflag=0 "); 
    	 strB.append(" where ipd.routedgenericitemid=:nRoutedGenericItemId and ipd.delflag=0 ");
    	 
    	
    	 List<TemplateForDrug> tmplList = namedParameterJdbcTemplate.query(strB.toString(), paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				String itemName = null;
				TemplateForDrug tmpl=new TemplateForDrug();
				tmpl.setLookAlike(rs.getString(LOOK_ALIKE));
				tmpl.setTitlePreview(rs.getString(TITLE_PREVIEW));
				tmpl.setTitleStyle(rs.getString(TITLE_STYLE));
				tmpl.setIssearchable(rs.getString(ISSEARCHABLE));
				tmpl.setIsrestricted(rs.getString(ISRESTRICTED));
				tmpl.setIsRT(rs.getString("isRT"));
				tmpl.setIsIVDiluent(rs.getString(ISIVDILUENT1));
				tmpl.setVarianceLimit(rs.getString(VARIANCE_LIMIT));
				tmpl.setVarianceLimitUnit(rs.getString(VARIANCE_LIMIT_UNIT));
				tmpl.setVarianceLimitAfter(rs.getString(VARIANCE_LIMIT_AFTER));
				tmpl.setVarianceLimitAfterUnit(rs.getString(VARIANCE_LIMIT_AFTER_UNIT));
				tmpl.setDualverifyreqd(rs.getString(DUALVERIFYREQD));
				tmpl.setIsrestrictedoutsideOS(rs.getString(ISRESTRICTEDOUTSIDEOS));
				tmpl.setDelflag(rs.getString(DELFLAG));
				tmpl.setUserId(rs.getString(USERID));
				tmpl.setNotes(rs.getString(NOTES));
				itemName = strDrugItemIdName;
				tmpl.setItemName(itemName);
				tmpl.setIsAdditive(rs.getString(ISADDITIVE));
				tmpl.setIsMandatoryForOE(rs.getString("IsMandatoryForOE"));
				tmpl.setIsImmunization(rs.getString(ISIMMUNIZATION));
				tmpl.setIsppd(rs.getString(ISPPD));
				tmpl.setIsRenewInExpiringTab(rs.getString(ISRENEWINEXPIRINGTAB));
				tmpl.setImmItemId(rs.getString("ImmItemId"));
				tmpl.setImmItemName(rs.getString("ImmItemName"));
				tmpl.setDrugAlias(rs.getString(DRUG_ALIAS));
				
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}
				catch (ParseException e) {
		            EcwLog.AppendExceptionToLog(e);
		        }
				return tmpl;
			}
        });
    	
    	//if data is not saved then
    	if(tmplList!=null && tmplList.isEmpty())
    	{
    		tmplList = loadBrandNames(nRoutedGenericItemId);
    	} 
    	
    	//get saved prn indication data
    	StringBuilder sqlB = new StringBuilder("select idpd.prn_indication_id,idp.prnName from ip_drugformulary_selected_prn_data idpd inner join ip_drugformulary_prnindication idp ");
    	sqlB.append(" on idpd.prn_indication_id = idp.id where idpd.delflag =0 and idp.deleteFlag= 0 ");
    	sqlB.append(" and idpd.routedgenericitemid=:nRoutedGenericItemId and idp.prnstatus=1 ");
    	
    	tmplPrnIndication = namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplatePNRIndication>() {
			@Override
			public TemplatePNRIndication mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplatePNRIndication tmpl=new TemplatePNRIndication();
				tmpl.setId(rs.getInt("prn_indication_id"));
				tmpl.setPrnName(rs.getString("prnName"));
				return tmpl;
			} 
    	});
    	
    	//common settings
    	TemplateForCommonSetting tmplForCommonSetting = new TemplateForCommonSetting();
    	tmplForCommonSetting.setDrugDetails(tmplList);  
    	//tmplForCommonSetting.setNotes(notes); // not in use because notes section shifted to Specific setting
    	tmplForCommonSetting.setOrgDrugDetails(tmplListNew);
    	tmplForCommonSetting.setPrnIndicationData(tmplPrnIndicationList);
    	tmplForCommonSetting.setSavedPRNIndicationData(tmplPrnIndication);
    	tmplCommList =new ArrayList<>();
    	tmplCommList.add(tmplForCommonSetting);		
        return tmplCommList;
	}
	
	
	////to load common settings
	public List<TemplateForDrug> loadCommonSettingsForDrugDetails(final int nRoutedGenericItemId,final int nformularyId){
		Map<String,Object> paramMap = new HashMap<>();
        List<TemplateForDrug> tmplList = null;
        List<TemplateForDrug> tmplListNew = null;
        strDrugItemIdName = "";
    	String sql = "select ipd.id,ipi.itemname,ipd.createdon,ipd.createdby,ipd.modifiedon,ipd.modifiedby from ip_drugformulary ipd inner join ip_items ipi on ipd.routedgenericitemid = ipi.itemID "
    				+" where ipd.routedgenericitemid=:nRoutedGenericItemId and ipd.id=:nFormularyId and ipd.delflag=0 and ipi.deleteflag=0 ";
    	
    	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
    	paramMap.put(N_FORMULARY_ID,nformularyId);
    	tmplListNew = namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				TemplateForDrug tmpl=new TemplateForDrug();
				tmpl.setItemName(rs.getString(ITEMNAME));
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}catch (ParseException e) {
		            EcwLog.AppendExceptionToLog(e);
		        }
				return tmpl;
			} 
    	});
    	Iterator <TemplateForDrug> it = tmplListNew.iterator();
    	if(it.hasNext())
    	{
    		TemplateForDrug temp = it.next();
    		strDrugItemIdName = (temp.getItemName());
    	}
    	
    	StringBuilder strB = new StringBuilder(" SELECT ipd.lookAlike,ipd.TitlePreview,ipd.TitleStyle,ipd.isRT,ipd.varianceLimit,ipd.varianceLimitUnit,ipd.varianceLimitAfter,");
    	 strB.append(" ipd.varianceLimitAfterUnit,ipd.dualverifyreqd,ipd.issearchable,ipd.isrestricted,ipd.isIVDiluent,");
    	 strB.append(" ipd.isrestrictedoutsideOS,ipd.createdby,ipd.createdon,ipd.modifiedby,ipd.modifiedon,ipd.delflag,ipd.userId,ipd.notes,ipd.isAdditive, ");
    	 strB.append(" ipd.isMandatoryForOE,ipd.isImmunization,ipd.isppd,ipd.isRenewInExpiringTab ");
    	 strB.append(" , ipd.imm_itemid as ImmItemId ");
    	 strB.append(" ,it.itemName as ImmItemName ");
    	 strB.append(" ,ipd.drugAlias as drugAlias ");
    	 strB.append(" FROM ip_drugformulary_common_settings ipd ");
    	 strB.append(" left join items it on ipd.imm_itemid = it.itemid and it.deleteflag=0 "); 
    	 strB.append(" where ipd.routedgenericitemid=:nRoutedGenericItemId and ipd.delflag=0 ");
    	
    	tmplList = namedParameterJdbcTemplate.query(strB.toString(), paramMap, new RowMapper<TemplateForDrug>() {
			@Override
			public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
				String itemName = null;
				TemplateForDrug tmpl=new TemplateForDrug();
				tmpl.setLookAlike(rs.getString(LOOK_ALIKE));
				tmpl.setTitlePreview(rs.getString(TITLE_PREVIEW));
				tmpl.setTitleStyle(rs.getString(TITLE_STYLE));
				tmpl.setIssearchable(rs.getString(ISSEARCHABLE));
				tmpl.setIsrestricted(rs.getString(ISRESTRICTED));
				tmpl.setIsRT(rs.getString("isRT"));
				tmpl.setIsIVDiluent(rs.getString(ISIVDILUENT1));
				tmpl.setVarianceLimit(rs.getString(VARIANCE_LIMIT));
				tmpl.setVarianceLimitUnit(rs.getString(VARIANCE_LIMIT_UNIT));
				tmpl.setVarianceLimitAfter(rs.getString(VARIANCE_LIMIT_AFTER));
				tmpl.setVarianceLimitAfterUnit(rs.getString(VARIANCE_LIMIT_AFTER_UNIT));
				tmpl.setDualverifyreqd(rs.getString(DUALVERIFYREQD));
				tmpl.setIsrestrictedoutsideOS(rs.getString(ISRESTRICTEDOUTSIDEOS));
				tmpl.setDelflag(rs.getString(DELFLAG));
				tmpl.setUserId(rs.getString(USERID));
				tmpl.setNotes(rs.getString(NOTES));
				itemName = strDrugItemIdName;
				tmpl.setItemName(itemName);
				tmpl.setIsAdditive(rs.getString(ISADDITIVE));
				tmpl.setIsMandatoryForOE(rs.getString("IsMandatoryForOE"));
				tmpl.setIsImmunization(rs.getString(ISIMMUNIZATION));
				tmpl.setIsppd(rs.getString(ISPPD));
				tmpl.setIsRenewInExpiringTab(rs.getString(ISRENEWINEXPIRINGTAB));
				tmpl.setImmItemId(rs.getString("ImmItemId"));
				tmpl.setImmItemName(rs.getString("ImmItemName"));
				tmpl.setDrugAlias(rs.getString(DRUG_ALIAS));
				
				try
				{
         			tmpl.setCreatedby(rs.getString(CREATED_BY));
					tmpl.setModifiedby(rs.getString(MODIFIED_BY));
         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
         			
				}
				catch(RuntimeException | ParseException ex ){
					 EcwLog.AppendExceptionToLog(ex);
				}
				return tmpl;
			}
        });
    	
    	//if data is not saved then
    	if(tmplList!=null && tmplList.isEmpty())
    	{
    		tmplList = loadBrandNames(nRoutedGenericItemId);
    	}
        return tmplList;
	}
	
	//load common setting for notes
	public List<TemplateForNotes> loadCommonSettingsForNotes(final int nRoutedGenericItemId,final int nformularyId){
			Map<String,Object> paramMap = new HashMap<>();
	        strDrugItemIdName = "";
	        StringBuilder strQ = new StringBuilder();
        	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
        	paramMap.put(N_FORMULARY_ID,nformularyId);
        	strQ.append(" SELECT ipn.orderentry_instr,ipn.emar_instr,pharmacy_instr,ipn.internal_notes,ipn.instructions,ipn.createdby,");
			strQ.append(" ipn.createdon,ipn.modifiedby,ipn.modifiedon,ipn.userId,ipn.notes,ipn.formularyid ");
			strQ.append(" FROM ip_drugformulary_notes ipn inner join ip_drugformulary drug on ipn.formularyid = drug.id ");
			strQ.append(" WHERE ipn.routedgenericitemid=:nRoutedGenericItemId and ipn.delflag=0 and drug.delflag=0 ");
			strQ.append(" and ipn.formularyid=:nFormularyId ");
        	
        	return namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<TemplateForNotes>() {
				@Override
				public TemplateForNotes mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForNotes tmpl=new TemplateForNotes();
					tmpl.setOrderentry(rs.getString(ORDERENTRY_INSTR));
					tmpl.setEmar(rs.getString(EMAR_INSTR));
					tmpl.setPharmacy(rs.getString(PHARMACY_INSTR));
					tmpl.setInternal(rs.getString(INTERNAL_NOTES));
					tmpl.setInstructions(rs.getString(INSTRUCTIONS));
					tmpl.setUserId(rs.getString(USERID));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setFormularyId(rs.getString(FORMULARY_ID));
					try
					{
	         			tmpl.setCreatedby(rs.getString(CREATED_BY));
						tmpl.setModifiedby(rs.getString(MODIFIED_BY));
	         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
	         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
	         			
					}catch(RuntimeException | ParseException ex ){
						 EcwLog.AppendExceptionToLog(ex);
					}
					return tmpl;
				}
        	});	
		}
	
	//method to get IV Diluent drugs list
	public List<Template> getIVDiluentDrugList(){
		Map<String,Object> paramMap = new HashMap<>();
    	StringBuilder strSQL = new StringBuilder(" ");
    	strSQL.append(" SELECT drug.id,drug.drugnameid,i1.itemname,i.itemid,drug.ndc,drug.upc,drug.isactive,drug.formulation,drug.strength,drug.strengthuom,drug.dispensesize,drug.dosesize,drug.doseuom,drug.iscalculate,");
    	strSQL.append(" case when i.routeddrugid = i.routedgenericid then 1 else 0 end as genericDrug,drug.routeid,drug.routename,drug.volume,drug.dispensesizeuom,i.drugname "); 
    	strSQL.append(" FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.itemid = i.itemid ");
    	strSQL.append(" and drug.delflag=0  inner join ip_drugformulary_common_settings cset on drug.routedGenericItemId = cset.routedgenericitemid and cset.delflag = 0 ");
    	strSQL.append(" AND cset.isivdiluent=1 and drug.isActive=1 ");
    	strSQL.append(" inner join ip_items i1 on i1.itemid = cset.routedgenericitemid and i1.deleteFlag = 0 ");
    	strSQL.append(" order by drug.id  ");
    	
    	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Template>(){
			@Override
			public Template mapRow(ResultSet rs, int arg1) throws SQLException {
				Template tmpl=new Template();
				tmpl.setDrugNameID(rs.getString(DRUG_NAME_ID));
				tmpl.setDrugName(rs.getString("drugname"));
				tmpl.setItemId(rs.getString(ITEMID));
				tmpl.setItemName(rs.getString(ITEMNAME));
				tmpl.setId(rs.getInt("id")); 
				tmpl.setNdc(rs.getString("ndc"));
				tmpl.setUpc(rs.getString("upc"));
				String strActive = rs.getString(IS_ACTIVE);
				if("1".equals(strActive))
				{
					tmpl.setStatus(ACTIVE);
				}
				else if("0".equals(strActive))  
				{
					tmpl.setStatus(INACTIVE);
				}
				
				tmpl.setFormulation(rs.getString(FORMULATION));
				tmpl.setStrength(rs.getString(STRENGTH));
				tmpl.setStrengthUom(rs.getString(STRENGTH_UOM));
				tmpl.setIsGenericDrug(rs.getString(GENERIC_DRUG));
				tmpl.setRouteID(rs.getString(ROUTE_ID));
				tmpl.setRouteName(rs.getString(ROUTE_NAME));
				tmpl.setVolume(rs.getString(VOLUME));
				tmpl.setIsCalculate(rs.getString(ISCALCULATE));
				tmpl.setDispensesize(RxUtil.preSanitizeStringAsDouble(rs.getString(DISPENSE_SIZE),true));
				tmpl.setDispensesizeuom(rs.getString(DISPENSE_SIZE_UOM));
				tmpl.setDose(RxUtil.preSanitizeStringAsDouble(rs.getString("dosesize"),true));
				tmpl.setDoseuom(rs.getString(DOSE_UOM));
				return tmpl;
			}
        });
	}
	
	//method to get drugs list for all
	public List<Template> getAllDrugList(){
		Map<String,Object> paramMap = new HashMap<>();
    	StringBuilder strSQL = new StringBuilder(" ");
    	strSQL.append(" SELECT drug.id,drug.drugnameid,i1.itemname,i.itemid,drug.ndc,drug.upc,drug.isactive,drug.formulation,drug.strength,drug.strengthuom,drug.dispensesize,drug.dosesize,drug.doseuom,drug.iscalculate, ");
    	strSQL.append(" case when i.routeddrugid = i.routedgenericid then 1 else 0 end as genericDrug,drug.routeid,drug.routename,drug.volume,drug.dispensesizeuom,i.drugname "); 
    	strSQL.append(" FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.itemid = i.itemid and drug.delflag=0 ");
    	strSQL.append(" inner join ip_drugformulary_common_settings cset on drug.routedGenericItemId = cset.routedgenericitemid and cset.delflag = 0 ");
    	strSQL.append(" AND cset.isAdditive=1 and drug.isActive=1 ");
    	strSQL.append(" inner join ip_items i1 on i1.itemid = cset.routedgenericitemid and i1.deleteFlag = 0 ");
    	strSQL.append(" order by drug.id ");
    	
    	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Template>(){
			@Override
			public Template mapRow(ResultSet rs, int arg1) throws SQLException {
				Template tmpl=new Template();
				tmpl.setDrugNameID(rs.getString(DRUG_NAME_ID));
				tmpl.setDrugName(rs.getString("drugname"));
				tmpl.setItemId(rs.getString(ITEMID));
				tmpl.setItemName(rs.getString(ITEMNAME));
				tmpl.setId(rs.getInt("id")); 
				tmpl.setNdc(rs.getString("ndc"));
				tmpl.setUpc(rs.getString("upc"));
				String strActive = rs.getString(IS_ACTIVE);
				if("1".equals(strActive))
				{
					tmpl.setStatus(ACTIVE);
				}
				else if("0".equals(strActive))  
				{
					tmpl.setStatus(INACTIVE);
				}
				
				tmpl.setFormulation(rs.getString(FORMULATION));
				tmpl.setStrength(rs.getString(STRENGTH));
				tmpl.setStrengthUom(rs.getString(STRENGTH_UOM));
				tmpl.setIsGenericDrug(rs.getString(GENERIC_DRUG));
				tmpl.setRouteID(rs.getString(ROUTE_ID));
				tmpl.setRouteName(rs.getString(ROUTE_NAME));
				tmpl.setVolume(rs.getString(VOLUME));
				tmpl.setIsCalculate(rs.getString(ISCALCULATE));
				tmpl.setDispensesize(RxUtil.preSanitizeStringAsDouble(rs.getString(DISPENSE_SIZE),true));
				tmpl.setDispensesizeuom(rs.getString(DISPENSE_SIZE_UOM));
				tmpl.setDose(RxUtil.preSanitizeStringAsDouble(rs.getString("dosesize"),true));
				tmpl.setDoseuom(rs.getString(DOSE_UOM));
				return tmpl;
			}
        });
	}
	
	/**
	 * This method is used to return the list of equivalent added drug into formulary have same itemname,strength and formulation - already exist/duplicate check
	 * @param drug
	 * @return
	 */
	public List<TemplateForDrug> getEquivalentFormularyDrugDataUsingItem(TemplateForDrug drug)
	{
		List<TemplateForDrug> tmplList = null;
        try {
        	PharmacyHelper.generatePharmacyLog("getEquivalentFormularyDrugDataUsingItem is called");
        	final String strStrength = drug.getStrength();
        	final String strFormulation = drug.getFormulation();
        	final String itemName = drug.getItemName();
        	PharmacyHelper.generatePharmacyLog("itemName ["+itemName+"]");
        	String strGpi = drug.getGpi();
			String strSql = "select ipd.strength,ipd.strengthuom,ipd.formulation,ipd.itemid,ipi.itemname,ipd.ndc,ipd.upc from ip_drugformulary ipd "
        				+" inner join ip_items ipi on ipd.itemid = ipi.itemid where ipd.gpi = :gpi and ipd.delflag=0 and ipi.deleteFlag = 0 ";
        	Map<String,Object> paramMap = new HashMap<>();
        	paramMap.put("gpi", strGpi);
        	tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplateForDrug>() {
				@Override
				public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForDrug tmpl=new TemplateForDrug();
					try
					{
						if(Util.trimStr(rs.getString(STRENGTH)).equals(strStrength) && Util.trimStr(rs.getString(FORMULATION)).equals(strFormulation))
						{
							tmpl.setItemId(Util.trimStr(rs.getString(ITEMID)));
							tmpl.setItemName(Util.trimStr(rs.getString(ITEMNAME)));
							tmpl.setNdc(Util.trimStr(rs.getString("ndc")));
							tmpl.setUpc(Util.trimStr(rs.getString("upc")));
						}
					}
					catch(Exception ext1)
					{
						 EcwLog.AppendExceptionToLog(ext1);
					}
					return tmpl;
				}
        	});
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return tmplList;
	}
	
	//this method gives list of avaiable list of formulary ids - depending on the formulation
	public ArrayList<String> getAvailableDrugs(int formularyid){
		Map<String, Object> paramMapNew = new HashMap<>();
		ArrayList<String> formularyIds = new ArrayList<>();
        try {
			
        	String strSql = "select a.itemid,b.ext_mapping_id from ip_drugformulary a inner join ip_drugformulary_FORMULATION_external b on a.id = b.formularyid "
        				+" where a.id=:formularyid and a.delflag=0 and b.delflag=0 ";
        	paramMapNew.put(FORMULARY_ID,formularyid);
        	
        	List<TemplateForDrug> tmplList = namedParameterJdbcTemplate.query(strSql, paramMapNew, new RowMapper<TemplateForDrug>() {
				@Override
				public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForDrug tmpl=new TemplateForDrug();
					try
					{
						tmpl.setItemId(Util.trimStr(rs.getString(ITEMID)));
						tmpl.setFormulation(Util.trimStr(rs.getString(EXT_MAPPING_ID)));
					}
					catch(Exception ext1)
					{
						 EcwLog.AppendExceptionToLog(ext1);
					}
					return tmpl;
				}
        	});
    		String itemid = tmplList.get(0).getItemId();
    		String formulationid = tmplList.get(0).getFormulation();
    		
    		strSql = "select a.id from ip_drugformulary a inner join ip_drugformulary_FORMULATION_external b on a.id = b.formularyid "
    				+" where a.itemid=:itemid and b.ext_mapping_id=:formulationid and a.delflag=0 and b.delflag=0 ";
    		paramMapNew.put(ITEMID,itemid);
    		paramMapNew.put(FORMULATIONID,formulationid);
    		List<TemplateForDrug> tmplList1 = namedParameterJdbcTemplate.query(strSql, paramMapNew, new RowMapper<TemplateForDrug>() {
			@Override
				public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForDrug tmpl=new TemplateForDrug();
					try
					{
						tmpl.setId(rs.getString("id"));
					}
					catch(RuntimeException ex ){
						 EcwLog.AppendExceptionToLog(ex);
					}
					return tmpl;
				}
        	});
    		for(int n=0;n<tmplList1.size();n++)
    		{
    			formularyIds.add(tmplList1.get(n).getId());
    		}
        } catch (Exception e) {
            EcwLog.AppendExceptionToLog(e);
        }
        return formularyIds;
	}
	public String getDrugBrandName(String itemName,int brandNameId)
	{
		if(!"".equals(itemName) && brandNameId > 0){ //Changes done by malav
			CPOERxSearchInterface cpoeRxSerachInterface = rxOrderTreeService.getExactValueForKey(brandNameId);
			if(cpoeRxSerachInterface != null){
				return cpoeRxSerachInterface.getDrugName();
			}
		}
		return "";
	}
	
	//get list of drug as per order type : not in use : TBD
	public List<Template> getDrugListAsPerOrderType(String ordertype){
		Map<String,Object> paramMap = new HashMap<>();
        List<Template> tmplList = null;
        String top = "";
        try {
        	StringBuilder strSQL = new StringBuilder(" ");
        	
        	strSQL.append(SELECT+top+" drug.id,drug.drugnameid,i.itemname,i.itemid,drug.ndc,drug.ipc,drug.isactive,drug.formulation,drug.strength,drug.strengthuom, ");
        	strSQL.append(" case when i.routeddrugid = i.routedgenericid then 1 else 0 end as genericDrug,drug.chargecode,drug.assigned_brand_itemid "); 
        	strSQL.append(" FROM ip_drugformulary drug INNER JOIN ip_items i ON drug.itemid = i.itemid and drug.delflag=0 ");
        	if(!"".equals(ordertype))
        	{
        		if(FormularyConstants.FORMULARY_MEDICATION.equals(ordertype))
        		{//medication
        			strSQL.append(" and drug.ismedication=1 ");
        		}  
        		else if(FormularyConstants.FORMULARY_IV.equals(ordertype))
        		{//iv
        			strSQL.append(" and drug.isiv=1 ");
        		}
        		else if(FormularyConstants.FORMULARY_COMPLEXORDER.equals(ordertype))
        		{//complex order
        			strSQL.append(" and drug.istpn=1 ");
        		}
        	}
        	strSQL.append(" order by drug.id ");
        	
        	tmplList = namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Template>(){
				@Override
				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
					Template tmpl=new Template();
					tmpl.setDrugNameID(rs.getString(DRUG_NAME_ID));
					tmpl.setItemId(rs.getString(ITEMID));
					tmpl.setItemName(rs.getString(ITEMNAME));
					tmpl.setId(rs.getInt("id")); 
					tmpl.setNdc(rs.getString("ndc"));
					tmpl.setUpc(rs.getString("upc"));
					String strActive = rs.getString(IS_ACTIVE);
					if("1".equals(strActive))
					{
						tmpl.setStatus(ACTIVE);
					}
					else if("0".equals(strActive))  
					{
						tmpl.setStatus(INACTIVE);
					}
					
					tmpl.setFormulation(rs.getString(FORMULATION));
					tmpl.setStrength(rs.getString(STRENGTH));
					tmpl.setStrengthUom(rs.getString(STRENGTH_UOM));
					tmpl.setIsGenericDrug(rs.getString(GENERIC_DRUG));
					
					String itemName = rs.getString(ITEM_NAME);
					if(!"".equals(itemName)){
						CPOERxSearchInterface cpoeObj = rxOrderTreeService.getGenericDrugOfADrug(itemName);
						tmpl.setGenericName(cpoeObj.getItemName()); 
						String brandName = getDrugBrandName(itemName,rs.getInt(ASSIGNED_BRAND_ITEMID));
						tmpl.setBrandName(brandName);
					}
					tmpl.setChargecode(rs.getString(CHARGE_CODE));
					
					return tmpl;
				}
            });
        }catch(Exception ex ){
			 EcwLog.AppendExceptionToLog(ex);
		}
        return tmplList;
	}
	
	//associated products
	//order type setup
		public int insertUpdateOrderTypeSetup(int drugId,String jsonOrderTypeData,int nTrUserId){
			int newId = 0;
			String strSelectedOTSRouteList = "";
			String strSelectedOTSFrequencyList = "";
			String strSelectedOTSFormulationList = "";
			String recommendedRoutes = "";
			String recommendedFrequency = "";
			StringBuilder strSql = new StringBuilder("");
			boolean isInsertRecord = false;
			Map<String, Object> paramMapNew = new HashMap<>();
			StringBuilder strQ = new StringBuilder();
			Map<String,Object> paramMap = new HashMap<>();
			String updId = null;
			String actionToLog = "";
	        try {
	        	String vendorName = IPItemkeyDAO.getIPItemKeyValueFromName("VendorNameForClinicalData", "MSCLINICAL");
	        	
	        	JsonNode json = new ObjectMapper().readTree(jsonOrderTypeData);
	        	JsonNode registrationFields = json.get("orderTypeSetup");

	        	Iterator<String> fieldNames = registrationFields.getFieldNames();
	        	while(fieldNames.hasNext()){
	        	    String fieldName = fieldNames.next();
        	    	JsonNode medicationsFields = registrationFields.get(fieldName);
        	    	String strMedData = medicationsFields.toString();
        	    	TemplateForOTS ots = new ObjectMapper().readValue(strMedData, TemplateForOTS.class);
        	    	boolean isAddToLog = false;
        	    	
    	    		String strChgType = ots.getOrderType();
    	    		String strChecked = ots.getIsChecked();
    	    		
    	    		strQ.setLength(0);
    	    		paramMap.clear();
    	    		strQ.append("select formularyid,id from ip_drugformulary_ordertypesetup where formularyid=:formularyid and ordertype=:ordertype and delflag=0 ");
    	    		paramMap.put(FORMULARY_ID, ots.getFormularyid());
    	    		paramMap.put(ORDER_TYPE, strChgType);
    	    		
    	        	List<TemplateForOTS> tmplList = namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<TemplateForOTS>() {
    					@Override
    					public TemplateForOTS mapRow(ResultSet rs, int arg1) throws SQLException {
    						TemplateForOTS tmpl=new TemplateForOTS();
    						tmpl.setId(rs.getString("id"));
    						tmpl.setFormularyid(rs.getString(FORMULARY_ID)); 
    						return tmpl;
    					}
    	        	});
    	        	List<String>strList = checkIfExist(tmplList);
    	        	updId = strList.get(0);
    	        	isInsertRecord = Boolean.valueOf(strList.get(1));
    	    		
    	    		if("true".equalsIgnoreCase(strChecked))
    	    		{
	    	    		strSql.setLength(0);
	    	    		strSql.append("INSERT INTO ip_drugformulary_OrderTypeSetup(ordertype,isnonbillable,autostop,autostopuom,chargetypeid,createdby,createdon,modifiedon,modifiedby,userid,formularyid,");
	            		strSql.append("isChangeRate,isTitrationAllowed,isRefrigeration,expiresOn,expiresOnUOM,");
	            		strSql.append("isPCA,isSlidingScale,slidingScale,");
	            		strSql.append("bolus_loadingdose,bolus_loadingdose_uom,intermitten_dose,");
	            		strSql.append("intermitten_dose_uom,lockout_interval_dose,lockout_interval_uom,");
	            		strSql.append("four_hr_limit,four_hr_limit_uom)");	                		
	                	strSql.append("values(:ordertype,:isnonbillable,:autostop,:autostopuom,(case when :chargetypeid='' or :chargetypeid=0 then NULL else :chargetypeid end),:createdby,:createdon,:modifiedon,:modifiedby,:userid,:formularyid,");
	                	strSql.append(":isChangeRate,:isTitrationAllowed,:isRefrigeration,:expiresOn,:expiresOnUOM,:isPCA,:isSlidingScale,:slidingScale,");
	            		strSql.append(":bolus_loadingdose,:bolus_loadingdose_uom,:intermitten_dose,:intermitten_dose_uom,:lockout_interval_dose,:lockout_interval_uom,");
	            		strSql.append(":four_hr_limit,:four_hr_limit_uom)");
	    	    		
	                	if(!isInsertRecord)
	                	{
	                		strSql.setLength(0);
	                		strSql.append("update ip_drugformulary_OrderTypeSetup set ordertype=:ordertype,isnonbillable=:isnonbillable,");
	                		strSql.append("autostop=:autostop,autostopuom=:autostopuom,chargetypeid=(case when :chargetypeid='' or :chargetypeid=0 then NULL else :chargetypeid end),modifiedon=:modifiedon,modifiedby=:modifiedby,userid=:userid, ");
	                		strSql.append("isChangeRate=:isChangeRate,isTitrationAllowed=:isTitrationAllowed,isRefrigeration=:isRefrigeration,expiresOn=:expiresOn,expiresOnUOM=:expiresOnUOM,");
	                		strSql.append("isPCA=:isPCA,isSlidingScale=:isSlidingScale,slidingScale=:slidingScale, ");
	                		strSql.append("bolus_loadingdose=:bolus_loadingdose,bolus_loadingdose_uom=:bolus_loadingdose_uom,intermitten_dose=:intermitten_dose,");
	                		strSql.append("intermitten_dose_uom=:intermitten_dose_uom,lockout_interval_dose=:lockout_interval_dose,lockout_interval_uom=:lockout_interval_uom,");
	                		strSql.append("four_hr_limit=:four_hr_limit,four_hr_limit_uom=:four_hr_limit_uom ");
	                    	strSql.append(" where id=:id and formularyid=:formularyid ");
	                	}
	                	
	                	if("0".equals(strChgType))
	                	{
	                		strChgType = "";
	                	}
	                	paramMapNew.put(ORDER_TYPE,strChgType);
	                	paramMapNew.put("isnonbillable",ots.getNonBillable());
	                	paramMapNew.put("autostop",ots.getAutoStop());
	                	paramMapNew.put("autostopuom",ots.getAutoStopUOM());
	                	paramMapNew.put(CHARGETYPEID,ots.getChargeTypeId());
	                	
	                	strSelectedOTSRouteList = ots.getRouteList();
	                	strSelectedOTSFrequencyList = ots.getFrequencyList();
	                	strSelectedOTSFormulationList = ots.getFormulationList();
	                	recommendedRoutes = ots.getRecommendedRoutes();
	                	recommendedFrequency = ots.getRecommendedFrequency();
	                	
	            		paramMapNew.put(CREATED_BY,String.valueOf(nTrUserId));
	            		paramMapNew.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	            		paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
	            		paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	            		paramMapNew.put(USER_ID,String.valueOf(nTrUserId));
	            		paramMapNew.put(ISCHANGERATE,ots.getChangeRate());
	            		paramMapNew.put(ISTITRATIONALLOWED,ots.getTitAllowed());
	            		paramMapNew.put(ISREFRIGERATION,ots.getRefri());
	            		paramMapNew.put(EXPIRESON,ots.getExpiresOn());
	            		paramMapNew.put("expiresOnUOM",ots.getExpiresOnUom());
	            		paramMapNew.put(ISPCA,ots.getPca());
	            		paramMapNew.put(ISSLIDINGSCALE,ots.getSlidingscale());
	            		paramMapNew.put(SLIDING_SCLAE,ots.getSliding_scale_notes());
	            		paramMapNew.put(BOLUS_LOADINGDOSE,ots.getBolusLoadingdose());
	            		paramMapNew.put(BOLUS_LOADINGDOSE_UOM,ots.getBolusLoadingdoseuom());
	            		paramMapNew.put(INTERMITTEN_DOS,ots.getIntermitten_dose());
	            		paramMapNew.put(INTERMITTEN_DOSE_UOM,ots.getIntermitten_dose_uom());
	            		paramMapNew.put(LOCKOUT_INTERVAL_DOSE,ots.getLockout_interval_dose());
	            		paramMapNew.put(LOCKOUT_INTERVAL_UOM,ots.getLockout_interval_uom());
	            		paramMapNew.put(FOUR_HR_LIMIT,ots.getFourhrs_limit());
	            		paramMapNew.put(FOUR_HR_LIMIT_UOM,ots.getFourhrs_limit_uom());
	            		paramMapNew.put("id",updId);
	            		paramMapNew.put(FORMULARY_ID,ots.getFormularyid());
	                	
	                	if(isInsertRecord)
	               		{
	                		actionToLog = CREATED;
	               			newId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSql.toString(), paramMapNew, "id");
	               		}
	               		else
	               		{
	               			actionToLog = MODIFIED;
	               			namedParameterJdbcTemplate.update(strSql.toString(), paramMapNew);
	               			newId = Integer.parseInt(updId); 
	               		}
	               		strSql.setLength(0);
	               		
	               		//routes list
	               		callToProcessOTSRoute(drugId,newId,nTrUserId,strSelectedOTSRouteList,recommendedRoutes,vendorName);
	               		
	           			//frequency list
	               		callToProcessOTSFrequency(drugId,newId,nTrUserId,strSelectedOTSFrequencyList,recommendedFrequency,vendorName);
	
	           			//formulation list
	               		callToProcessOTSFormulation(drugId,newId,nTrUserId,strSelectedOTSFormulationList);
	           			
	           			
	               		if(FormularyConstants.FORMULARY_IV.equals(strChgType))
	               		{//iv diluent
	               			JsonParser jsonParser = new JsonParser();
	                        JsonObject jo = (JsonObject)jsonParser.parse(strMedData);
	                        JsonArray jsonDiluentArr = jo.getAsJsonArray("diluent");
	                        JsonArray jsonIVRateArr = jo.getAsJsonArray("iv_rate");
	                        
	               			insertUpdateIVDiluent(drugId,newId,jsonDiluentArr,nTrUserId);
	               			insertUpdateIVRate(drugId,newId,jsonIVRateArr,nTrUserId);
	               		}
	               		if(FormularyConstants.FORMULARY_COMPLEXORDER.equals(strChgType))
	               		{//complex order
	               			JsonParser jsonParser = new JsonParser();
	                        JsonObject jo = (JsonObject)jsonParser.parse(strMedData);
	                        JsonArray jsonDiluentArr = jo.getAsJsonArray("diluent");
	                        JsonArray jsonIVRateArr = jo.getAsJsonArray("iv_rate");
	                        
	               			insertUpdateIVDiluent(drugId,newId,jsonDiluentArr,nTrUserId);
	               			insertUpdateIVRate(drugId,newId,jsonIVRateArr,nTrUserId);
	               		}
	               		isAddToLog = true;
	    	    	}
    	    		else if(Integer.parseInt(updId) > 0)
    	    		{
    	    			actionToLog = DELETE;
    	    			strSql.setLength(0);
    	    			strSql.append("update ip_drugformulary_ordertypesetup set delflag=1 where formularyid=:formularyid and ordertype=:ordertype ");
        	    		namedParameterJdbcTemplate.update(strSql.toString(), paramMap);
               			newId = Integer.parseInt(updId); 
               			isAddToLog = true;
    	    		}
		        	//audit log
    	    		if(isAddToLog)
    	    		{
    	    			inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonOTS = new inpatientWeb.Global.ecw.ambulatory.json.JSONObject(jsonOrderTypeData);
    	    			auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, actionToLog, jsonOTS, "Specific Setting -> OrderType");
    	    		}
	        	}
	        	
	        } catch (Exception e) {
	            EcwLog.AppendExceptionToLog(e);
	        }
	        return newId;
		}
		//get associated product lot details
		public List<TemplateForAssoProductLotDetails> getAssoProductLotDetails(int formularyId,int assoProdId){
			Map<String,Object> paramMap = new HashMap<>();
        	StringBuilder strSQL = new StringBuilder("");
        	strSQL.append(" SELECT id,lotno,lotType,expirydate,delflag,assoProductId from ip_drugformulary_asso_products_lot_enquiry where formularyid=:formularyId and assoProductId=:assoProductId and delflag=0 ");
        	paramMap.put(FORMULARYID,formularyId);
        	paramMap.put(ASSO_PRODUCT_ID,assoProdId);
        	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForAssoProductLotDetails >(){
				@Override
				public TemplateForAssoProductLotDetails  mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProductLotDetails  tmpl=new TemplateForAssoProductLotDetails ();
					tmpl.setId(rs.getString("id"));
					tmpl.setLotno(rs.getString(LOTNO));
					tmpl.setLotType(rs.getString(LOTTYPE));
					String strExpiryDate = (rs.getString(EXPIRYDATE));
					if(!"".equals(strExpiryDate))
					{
						strExpiryDate = CwUtils.ConvertDateFormat(strExpiryDate,FormularyConstants.DB_DATE_FORMAT,FormularyConstants.APP_DATE_FORMAT);
					}
					tmpl.setExpiryDate(strExpiryDate);
					tmpl.setDelflag(rs.getString(DELFLAG));
					tmpl.setAssoProductId(rs.getString(ASSO_PRODUCT_ID));
					return tmpl;
				}
            });
		}
		
		//get associated product lot details for audit log
		public List<TemplateForAssoProductLotDetails> getAssoProductLotDetailsByLotNo(final String formularyId,String assoProdId,String lotNo){
			Map<String,Object> paramMap = new HashMap<>();
        	StringBuilder strSQL = new StringBuilder("");
        	strSQL.append(" SELECT id,lotno,lotType,expirydate,delflag,assoProductId from ip_drugformulary_asso_products_lot_enquiry ");
        	strSQL.append(" where formularyid=:formularyId  and assoProductId=:assoProductId and delflag=0 ");
        	
        	if(!"".equals(lotNo))
        	{
        		strSQL.append(" and lotno=:lotno ");
        	}
        	paramMap.put(FORMULARYID,formularyId);
        	paramMap.put(ASSO_PRODUCT_ID,assoProdId);
        	paramMap.put(LOTNO,lotNo);
        	
        	return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForAssoProductLotDetails >(){
				@Override
				public TemplateForAssoProductLotDetails  mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProductLotDetails  tmpl=new TemplateForAssoProductLotDetails ();
					tmpl.setId(rs.getString("id"));
					tmpl.setLotno(rs.getString(LOTNO));
					tmpl.setLotType(rs.getString(LOTTYPE));
					String strExpiryDate = (rs.getString(EXPIRYDATE));
					if(!"".equals(strExpiryDate))
					{
						strExpiryDate = CwUtils.ConvertDateFormat(strExpiryDate,FormularyConstants.DB_DATE_FORMAT,FormularyConstants.APP_DATE_FORMAT);
					}
					tmpl.setExpiryDate(strExpiryDate);
					tmpl.setDelflag(rs.getString(DELFLAG));
					tmpl.setAssoProductId(rs.getString(ASSO_PRODUCT_ID));
					tmpl.setFormularyId(formularyId);
					return tmpl;
				}
            });
		}
		
		public List<TemplateForAssoProductsWithLotDetails> getAssoProductDetailsLotDetails(LotSearchData lotSearchData){
			Map<String,Object> paramMap = new HashMap<>();
	        List<TemplateForAssoProductsWithLotDetails> tmplList = null;
	        
        	StringBuilder strSQL = new StringBuilder("");
        	
        	strSQL.append("SELECT row_number() over (order by asspro.id) as RowNumber, asspro.id, asspro.formularyid, asspro.itemid, i.itemName as drugName,");
        	strSQL.append(" asspro.routedDrugId, asspro.ndc, asspro.upc, asspro.ppid, asspro.packsize, asspro.packsizeunitcode, asspro.packQuantity, asspro.packType,");
        	strSQL.append(" asspro.awp,asspro.awup,asspro.manufacturerName, asspro.manufacturerIdentifier, asspro.mvxCode, asspro.cost_to_proc, asspro.unitCost, asspro.status, asspro.isVFC, asspro.isPrimary, asspro.marketEndDate, asspro.delflag, asspro.userId, asspro.notes, asspro.productName, asspro.isSingleDosagePack, lotdet.assoProductId, lotdet.lotno, lotdet.expirydate, lotdet.lotType, lotdet.id as lotId ");
        	strSQL.append(" FROM ip_drugformulary_associatedProducts AS asspro INNER JOIN ip_drugformulary_asso_products_lot_enquiry AS lotdet ON asspro.formularyid = lotdet.formularyid AND asspro.id = lotdet.assoProductId ");
        	strSQL.append(" inner join ip_items i on asspro.itemid = i.itemid ");
        	strSQL.append(" WHERE (asspro.formularyid=:formularyid) AND (asspro.delflag = 0) AND (lotdet.delflag = 0) AND asspro.status = 1");
			if (StringUtils.isNotBlank(lotSearchData.getSearchValue())) {
				strSQL.append(" AND lotdet.lotno like :lotno ");
				paramMap.put(LOTNO, lotSearchData.getSearchValue() + "%");
			}
			StringBuilder query  = new StringBuilder();
			int start = lotSearchData.getSelectedPage() * lotSearchData.getRecordsPerPage() - lotSearchData.getRecordsPerPage();
			query.append("SELECT TOP " + lotSearchData.getRecordsPerPage() + " * FROM (" + strSQL.toString() + ")");
			query.append(" AS x WHERE RowNumber > " + start);
			strSQL.setLength(0);
			strSQL.append(query);
        	paramMap.put(FORMULARY_ID,lotSearchData.getFormularyId());
        	tmplList = namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<TemplateForAssoProductsWithLotDetails>() {
				@Override
				public TemplateForAssoProductsWithLotDetails mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProductsWithLotDetails tmpl=new TemplateForAssoProductsWithLotDetails();
					tmpl.setId(rs.getInt("id"));
					tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
					tmpl.setNdc(rs.getString("ndc"));
					tmpl.setUpc(rs.getString("upc"));
					tmpl.setPpid(rs.getString("ppid"));
					tmpl.setPackSize(rs.getString(PACK_SIZE));
					tmpl.setPackSizeUnitCode(rs.getString(PACKSIZE_UNIT));
					tmpl.setPackQuantity(rs.getString(PACK_QUANTITY)); 
					tmpl.setPackType(rs.getString(PACK_TYPE));
					tmpl.setAwp(rs.getString("awp"));
					tmpl.setAwup(rs.getString("awup"));
					tmpl.setManufacturerName(rs.getString(MANUFACTURE_NAME));
					tmpl.setManufacturerIdentifier(rs.getString(MANUFACTURE_IDENTIFIER));
					tmpl.setMvxCode(rs.getString(MVX_CODE));
					tmpl.setCost_to_proc(rs.getDouble(COST_TO_PROC));
					tmpl.setUnitcost(rs.getDouble(UNIT_COST));
					tmpl.setStatus(rs.getInt(STATUS));
					tmpl.setIsprimary(rs.getInt("isPrimary"));
					tmpl.setMarketEndDate(rs.getString(MARKET_END_DATE));
					tmpl.setUserId(rs.getInt(USERID));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setItemId(rs.getString(ITEM_ID));
					tmpl.setDrugName(rs.getString(DRUG_NAME));
					tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
					tmpl.setProductName(rs.getString(PRODUCT_NAME));
					tmpl.setIsVfc(rs.getInt("isvfc"));
					tmpl.setIsSingleDosagePack(rs.getInt(ISSINGLEDOSAGEPACK));
					tmpl.setLotId(rs.getString("lotId"));
					tmpl.setLotno(rs.getString(LOTNO));
					tmpl.setLotType(rs.getString(LOTTYPE));
					String strExpiryDate = (rs.getString(EXPIRYDATE));
					if(!"".equals(strExpiryDate))
					{
						strExpiryDate = CwUtils.ConvertDateFormat(strExpiryDate,FormularyConstants.DB_DATE_FORMAT,FormularyConstants.APP_DATE_FORMAT);
					}
					tmpl.setExpiryDate(strExpiryDate);
					return tmpl;
				} 
        	});
	        return tmplList;
		}
		
		public Integer getAssoProductDetailsLotDetailsCount(LotSearchData lotSearchData){
			Map<String,Object> paramMap = new HashMap<>();
	        List<Integer> tmplList = null;
	        
        	StringBuilder strSQL = new StringBuilder("");
        	
        	strSQL.append("SELECT count(*) as count ");
        	strSQL.append("FROM ip_drugformulary_associatedProducts AS asspro INNER JOIN ip_drugformulary_asso_products_lot_enquiry AS lotdet ON asspro.formularyid = lotdet.formularyid AND asspro.id = lotdet.assoProductId ");
        	strSQL.append("inner join ip_items i on asspro.itemid = i.itemid ");
        	strSQL.append("WHERE (asspro.formularyid=:formularyid) AND (asspro.delflag = 0) AND (lotdet.delflag = 0) AND asspro.status = 1");
			if (StringUtils.isNotBlank(lotSearchData.getSearchValue())) {
				strSQL.append(" AND lotdet.lotno like :lotno ");
				paramMap.put(LOTNO, lotSearchData.getSearchValue() + "%");
			}
			paramMap.put(FORMULARY_ID,lotSearchData.getFormularyId());
        	tmplList = namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Integer>() {
				@Override
				public Integer mapRow(ResultSet rs, int arg1) throws SQLException {
					return NumberUtils.toInt(rs.getString("count"), 0);
				} 
        	});
        	return tmplList.get(0);
		}
		
		/*delete methods*/
		public int deleteDrugDetails(String strDrugId,int nRoutedGenericItemId,String strItemName,int nTrUserId) throws JSONException {
			int newId = 0;
			List<Map<String, Object>> aSize = null;
			StringBuilder strQ = null;
			Map<String,Object> paramMap = new HashMap<>();
			boolean bCallToDeleteFormularyFromCache = true;
			inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = null;
			inpatientWeb.Global.ecw.ambulatory.json.JSONArray jsonArray = null;
			int nFormularyId = 0;
        	if(!"".equals(strDrugId))
        	{
        		nFormularyId = Integer.parseInt(strDrugId);
        	}
        	//audit log
        	//specific setting - product details
        	List<TemplateForDrug> tmpllist= getDrugDetails(strDrugId);
        	if(tmpllist!=null && !tmpllist.isEmpty())
        	{
        		final String drugList = new com.google.gson.Gson().toJson(tmpllist);
        		jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(drugList);
        		jsonForLog = jsonArray.getJSONObject(0);
        		auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Product Details");
        	}
        	
        	//order type setup
        	List<TemplateForOTS> tmplOTSlist = getOrderTypeSetupDetails(strDrugId);
        	if(tmplOTSlist!=null && !tmplOTSlist.isEmpty())
        	{
	        	final String strOTSList = new com.google.gson.Gson().toJson(tmplOTSlist);
	        	jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(strOTSList);
	        	jsonForLog = jsonArray.getJSONObject(0);
	        	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Order Type");
        	}
        	
        	//associated product
        	List<TemplateForAssoProducts> tmplAssolist = getAssociatedProductDetails(strDrugId);
    		if(tmplAssolist!=null && !tmplAssolist.isEmpty())
    		{
	        	final String assoProdList = new com.google.gson.Gson().toJson(tmplAssolist);
	        	jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(assoProdList);
	        	jsonForLog = jsonArray.getJSONObject(0);
	        	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Associated Product Details");
    		}
        	//notes
    		List<TemplateForNotes> notesList = loadCommonSettingsForNotes(nRoutedGenericItemId,nFormularyId);
    		if(notesList!=null && !notesList.isEmpty())
    		{
    			final String strNotesList = new com.google.gson.Gson().toJson(notesList);
	        	jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(strNotesList);
	        	jsonForLog = jsonArray.getJSONObject(0);
	        	auditLogService.logEvent(nTrUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Notes");
    		}
        	
        	
        	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
        	strQ = new StringBuilder(" SELECT routedgenericitemid  from ip_drugformulary where routedgenericitemid=:nRoutedGenericItemId and delflag=0 ");
    		aSize = namedParameterJdbcTemplate.queryForList(strQ.toString(), paramMap);
    		if(aSize.size() > 1)
    		{
    			bCallToDeleteFormularyFromCache = false;
    		}
        	Map<String, Object> paramMapNew = new HashMap<>();
        	String sql="update ip_drugformulary set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where id=:txtDrugId ";
        	paramMapNew.put(MODIFIED_BY,String.valueOf(nTrUserId));
        	paramMapNew.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
        	paramMapNew.put(USERID,String.valueOf(nTrUserId));
        	paramMapNew.put(TXT_DRUG_ID,strDrugId);
        	newId = Util.insertAndReturnId(namedParameterJdbcTemplate, sql, paramMapNew, "id");
        	
        	//delete it from notes
        	sql="update ip_drugformulary_notes set delflag=1,modifiedby=:modifiedby,modifiedon=:modifiedon,userid=:userId where formularyid=:txtDrugId ";
        	Util.insertAndReturnId(namedParameterJdbcTemplate, sql, paramMapNew, "id");
        	
        	//delete it from drugdruginteraction
        	sql="update ip_drugformulary_druginteraction set deleteflag=1,modifiedby=:modifiedby,modifieddate=:modifiedon where formularyid=:txtDrugId ";
        	Util.insertAndReturnId(namedParameterJdbcTemplate, sql, paramMapNew, "id");
        	
        	
        	if(bCallToDeleteFormularyFromCache)
        	{
        		rxOrderTreeService.deleteFormularyDrug(strItemName);//as per malav searchkey as itemname
        	}
	        return newId;
		}
		/*delete associated products*/
		public int deleteAssociatedProducts(String formularyId,String assoProdId,String ndc,int trUserId){
			int newId = 0;
        	if(!"".equals(assoProdId) && !"0".equals(assoProdId))
        	{
        		List<TemplateForAssoProducts> tmpllist = getAssociatedProductDetailsById(formularyId,assoProdId);
        		try
        		{
        			deleteAssociatedProductsLotDetails("",formularyId,assoProdId,"",trUserId);//this method is used to delete associated lot details before deleting asso product entry
	        		if(tmpllist!=null && !tmpllist.isEmpty())
	        		{
			        	final String assoProdList = new com.google.gson.Gson().toJson(tmpllist);
			        	inpatientWeb.Global.ecw.ambulatory.json.JSONArray jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(assoProdList);
			        	if(jsonArray.length() > 0)
			        	{
			        		inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = jsonArray.getJSONObject(0);
			        		auditLogService.logEvent(trUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Associated Product Details");
			        	}
	        		}
        		}
        		catch(RuntimeException | JSONException ex)
        		{
        			EcwLog.AppendExceptionToLog(ex);
        		}  
	        	Map<String, Object> paramMapNew = new HashMap<>();
	        	String sql="delete from ip_drugformulary_associatedProducts where id=:id and formularyid=:formularyid and ndc=:ndc and delflag=0 ";
	        	
	        	paramMapNew.put("id",assoProdId);
	        	paramMapNew.put(FORMULARY_ID,formularyId);
	        	paramMapNew.put("ndc",ndc);
	        	namedParameterJdbcTemplate.update(sql, paramMapNew);
       			newId = Integer.parseInt(assoProdId);
        	}
	        return newId;
		}
		/*delete pre requisite*/
		public int deletePrerequisite(int formularyId,String vitalId,int trUserId) throws JSONException{
			int newId = 0;
			int routedGenericItemId = 0;
        	if(!"".equals(vitalId) && !"0".equals(vitalId))
        	{
	        	Map<String, Object> paramMapNew = new HashMap<>();
	        	routedGenericItemId = getRoutedGenericItemId(formularyId);
	        	
	        	List<TemplateForPreVitals> tmpllist = getPreRequisiteVitalsDetailsById(String.valueOf(routedGenericItemId),vitalId);
	        	if(tmpllist!=null && !tmpllist.isEmpty())
	        	{
		        	final String preqList = new com.google.gson.Gson().toJson(tmpllist);
		        	inpatientWeb.Global.ecw.ambulatory.json.JSONArray jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(preqList);
		        	if(jsonArray.length() > 0)
		        	{
		        		inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = jsonArray.getJSONObject(0);
		        		auditLogService.logEvent(trUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, FORMULARY_SETUP_PRE_REQ);
		        	}
	        	}
	        	
	        	String sql="delete from ip_formulary_prerequisite where rightsideid=:rightsideid and routedgenericitemid=:nRoutedGenericItemId and delflag=0 ";
	        	paramMapNew.put(RIGHT_SIDE_ID,vitalId);
	        	paramMapNew.put(N_ROUTED_GENERIC_ITEM_ID,routedGenericItemId);
	        	namedParameterJdbcTemplate.update(sql, paramMapNew);
       			newId = Integer.parseInt(vitalId);
        	}
	        return newId;
		}
		
		/* to fetch prerequisite before delete and add it to log
		 * */
		public List<TemplateForPreVitals> getPreRequisiteVitalsDetailsById(final String routedGenericItemId,final String vitalId){
			Map<String,Object> paramMap = new HashMap<>();
	        StringBuilder strQ = new StringBuilder();
        	paramMap.put(N_ROUTED_GENERIC_ITEM_ID,routedGenericItemId);
        	paramMap.put(RIGHT_SIDE_ID,vitalId);
        	//CHECK FOR data is present for routed generic item id in common setting table
    		strQ.append(" SELECT pre.id,pre.leftsideid,pre.rightsideid,it.itemname ");
    		strQ.append(" FROM ip_formulary_prerequisite pre inner join ip_items it on pre.rightsideid = it.itemid ");
    		strQ.append(" WHERE pre.routedgenericitemid=:nRoutedGenericItemId and pre.rightsideid=:rightsideid and pre.delflag=0 and it.deleteflag=0 ");
    		
        	return namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<TemplateForPreVitals>() {
				@Override
				public TemplateForPreVitals mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForPreVitals tmpl=new TemplateForPreVitals();
					tmpl.setId(rs.getString("id"));
					tmpl.setLeftsideid(rs.getString(LEFT_SIDE_ID));
					tmpl.setRightsideid(rs.getString(RIGHT_SIDE_ID));
					tmpl.setRoutedgenericitemid(routedGenericItemId); 
					tmpl.setItemname(rs.getString(ITEMNAME));
					return tmpl;
				}
        	});
		}
		
		/*delete associated products lot details*/
		public int deleteAssociatedProductsLotDetails(String lotId,String formularyId,String assoProdId,String lotno,int trUserId) throws JSONException{
			int newId = 0;
    		List<TemplateForAssoProductLotDetails> tmpllist = getAssoProductLotDetailsByLotNo(formularyId, assoProdId, lotno);
    		if(tmpllist!=null && !tmpllist.isEmpty())
    		{
    			final String lotList = new com.google.gson.Gson().toJson(tmpllist);
    			inpatientWeb.Global.ecw.ambulatory.json.JSONArray jsonArray = new inpatientWeb.Global.ecw.ambulatory.json.JSONArray(lotList);
	        	if(jsonArray.length() > 0)
	        	{
	        		inpatientWeb.Global.ecw.ambulatory.json.JSONObject jsonForLog = jsonArray.getJSONObject(0);
	        		auditLogService.logEvent(trUserId, FORMULARY_SETUP_MODULE, DELETE, jsonForLog, "Specific Setting -> Associated Product -> Lot Details");
	        	}
    		}
    		
        	Map<String, Object> paramMapNew = new HashMap<>();
        	StringBuilder sql= new StringBuilder("delete from ip_drugformulary_asso_products_lot_enquiry where assoproductid=:assoproductid and formularyid=:formularyid and delflag=0 ");
        	if(!"".equals(lotId) && !"0".equals(lotId))
        	{
        		sql.append(" and id=:id ");
        	}
        	if(!"".equals(lotno))
        	{
        		sql.append(" and lotno=:lotno ");
        	}
        	paramMapNew.put("id",lotId);
        	paramMapNew.put("assoproductid",assoProdId);
        	paramMapNew.put(FORMULARY_ID,formularyId);
        	paramMapNew.put(LOTNO,lotno);
        	namedParameterJdbcTemplate.update(sql.toString(), paramMapNew);
	        return newId;
		}
		
		/**
		 * get Drug formulary Information by itemId
		 * @param nItemId
		 * @author Sandip Dalsaniya
		 * @return list of formulary info
		 */	
		public List<Map<String, Object>> getDrugFormularyByItemId(final int nItemId,final int formularyId) 
		{
			StringBuilder strQuery 	= new StringBuilder();
			strQuery.append(QueryBuilderUtils.sqlSelect)
					.append("df.id,i.itemID,i.itemid as ipitemid,i.drugnameid,i.itemName as Description,i.itemDesc, df.strength as strength, df.strengthuom as strengthUom, df.doseSize as Dose,")
					.append("df.doseUom as DoseUnit,df.formulation,df.ndc,df.upc,df.ddid,df.isTPN,df.ismedication,df.isIV,i.parentID,")
					.append("df.routedgenericitemid,df.ingredients_count,df.ingredient1_uom,df.ingredient1_value,df.ingredient2_uom,df.ingredient2_value, ")
					.append("df.ingredient3_uom,df.ingredient3_value,df.ingredient4_uom,df.ingredient4_value,df.doseUom,df.doseSize, df.dispenseSize as dispense, ")
					.append("df.dispenseSizeUom as dispenseUom, df.rxnorm")
					.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY.table()).append(" df")
					.append(QueryBuilderUtils.sqlInnerJoin).append(CPOEEnum.IP_TABLE.ITEMS.table()).append(" i")
					.append(QueryBuilderUtils.sqlOn).append("keyName IN ('RxMSClinical', 'RxCustomRx')").append(QueryBuilderUtils.sqlAnd).append("df.itemId = i.itemID")
					.append(QueryBuilderUtils.sqlWhere).append("df.isactive=1").append(QueryBuilderUtils.sqlAnd).append("df.delflag=0");
			Map<String, Object> mapParam = new HashMap<String, Object>(NUM_2);
			if(formularyId==0 && nItemId==0){
				return Collections.emptyList();
			}
			if(nItemId!=0){
				strQuery.append(QueryBuilderUtils.sqlAnd).append("df.itemId=:itemId ");
				mapParam.put(ITEM_ID, nItemId);
			}
			if(formularyId!=0){
				strQuery.append(QueryBuilderUtils.sqlAnd).append("df.id=:formularyId");
				mapParam.put(FORMULARYID, formularyId);
			}
		    return namedParameterJdbcTemplate.queryForList(strQuery.toString(), mapParam);
		}
		
		/**
		 * get Drug formulary Information by itemId,facility and formularyId
		 * @author Sandip Dalsaniya
		 * @param nItemId
		 * @param facilityId
		 * @param formularyId
		 * @return list of setups configured for item or product.
		 */
		public List<Map<String, Object>> getDrugFormularyWithOTSetupByItemId(final MedicationDosageReqParam reqParam) 
		{
			if(reqParam.getFormularyId()==0 && reqParam.getItemId()==0){
				return Collections.emptyList();
			}
			
			StringBuilder strQuery 	= new StringBuilder();
			Map<String, Object> mapParam = new HashMap<>(NUM_2);
			strQuery.append(QueryBuilderUtils.sqlSelectAll).append(QueryBuilderUtils.sqlFrom).append("(")
					.append(QueryBuilderUtils.sqlSelect)
					.append("df.id,i.itemID,i.itemid as ipitemid,i.drugnameid,i.itemName as Description,i.itemDesc, df.strength as strength, df.strengthuom as strengthUom, df.doseSize as Dose,")
					.append("df.doseUom as DoseUnit,df.formulation,df.ndc,df.upc,df.ddid,i.parentID,")
					.append("CASE WHEN (df.ismedication=1 AND o.orderType=1) THEN 1 ELSE 0 END AS ismedication,")
					.append("CASE WHEN (df.isIV=1 AND o.orderType=2) THEN 1 ELSE 0 END AS isIV,")
					.append("CASE WHEN (df.isTPN=1 AND o.orderType=3) THEN 1 ELSE 0 END AS isTPN,")
					.append("df.routedgenericitemid,df.ingredients_count,df.ingredient1_uom,df.ingredient1_value,df.ingredient2_uom,df.ingredient2_value,")
					.append("df.ingredient3_uom,df.ingredient3_value,df.ingredient4_uom,df.ingredient4_value,df.doseUom,df.doseSize, df.dispenseSize as dispense, ")
					.append("df.dispenseSizeUom as dispenseUom, df.rxnorm, o.isSlidingScale, df.isCalculate")
					.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY.table()).append(" df");
			if(reqParam.getFacilityId()!=0){
				strQuery.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUG_FORMULARY_FACILITIES.table()).append(" f");
				strQuery.append(QueryBuilderUtils.sqlOn).append("f.formularyid=df.id and f.delflag=0 and f.facilityid=:"+FACILITY_ID);
				mapParam.put(FACILITY_ID, reqParam.getFacilityId());
			}
			if(reqParam.isDoNotSubstitute()){
				strQuery.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUG_FORMULARY_ASSOCIATEDPRODUCTS.table()).append(" dfa");
				strQuery.append(QueryBuilderUtils.sqlOn).append("dfa.formularyid = df.id and dfa.itemid = :associateProductItemID");
				mapParam.put("associateProductItemID", reqParam.getItemId());
			}
			strQuery.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUGFORMULARY_ORDERTYPESETUP.table()).append(" o")
					.append(QueryBuilderUtils.sqlOn).append("df.id=o.formularyid")
					.append(QueryBuilderUtils.sqlInnerJoin).append(CPOEEnum.IP_TABLE.ITEMS.table()).append(" i")
					.append(QueryBuilderUtils.sqlOn).append("keyName IN ('RxMSClinical', 'RxCustomRx')").append(QueryBuilderUtils.sqlAnd).append("df.itemId = i.itemID")
					.append(QueryBuilderUtils.sqlWhere).append("df.isactive=1").append(QueryBuilderUtils.sqlAnd).append("df.delflag=0");
			
			if(reqParam.getFormularyId() > 0){
				strQuery.append(QueryBuilderUtils.sqlAnd).append("df.id=:formularyId");
				mapParam.put(FORMULARYID, reqParam.getFormularyId());
			}else if(!reqParam.isDoNotSubstitute()){
				if(reqParam.getRoutedGenericItemID()!=0){
					strQuery.append(QueryBuilderUtils.sqlAnd).append("df.routedGenericItemID=:"+ROUTED_GENERIC_ITEMID);
					mapParam.put(ROUTED_GENERIC_ITEMID, reqParam.getRoutedGenericItemID());
				}else if(reqParam.getItemId() > 0){
					strQuery.append(QueryBuilderUtils.sqlAnd).append("df.itemID=:"+ITEM_ID);
					mapParam.put(ITEM_ID, reqParam.getItemId());
				}
			}
			
			strQuery.append(" ) ").append(QueryBuilderUtils.sqlAs).append(" temp ")
			.append(QueryBuilderUtils.sqlWhere).append(" isMedication = 1 OR isIV = 1 OR isTPN = 1");
			
		    return namedParameterJdbcTemplate.queryForList(strQuery.toString(), mapParam);
		}
		public ArrayList<MedOrderDetail> getAvailableProductsByItemId(long routedGenericItemId, int orderType, String formulationExternal, final int facilityId){
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			ArrayList<MedOrderDetail> formularyItems = new ArrayList<MedOrderDetail>();
			try {
				StringBuilder strSQL = new StringBuilder();
				strSQL.append(" select df.id, df.ddid, df.itemid, df.drugnameid, df.formulation, df.strength, df.ndc,df.upc, df.isavailableforall,df.isserviceforall, df.isCalculate, ");
				strSQL.append(" df.doseUOM as formDoseUOM, df.doseSize as formDoseSize, df.dispenseSize as formDispenseSize, df.dispenseSizeUom as formDispenseUOM, df.rxnorm, df.genericDrugName, ");
				strSQL.append(" dfap.awup, dfap.cost_to_proc, idfo.chargeTypeId, idff.formularyid as facFormularyId,");
				strSQL.append(" dfn.orderentry_instr, dfn.emar_instr, dfn.internal_notes, ");
				strSQL.append(" i.itemName, ");
				strSQL.append(" dfcs.isIVDiluent, dfcs.isAdditive, ");
				strSQL.append(" cpt.chargecode, ");
				strSQL.append(" dfap.packsizeunitcode as  packsizeunitcode, ");
				strSQL.append(" dfap.packsize as packsize, ");
				strSQL.append(" dfap.packType as packType, ");				
				strSQL.append(" df.isdrugtypebulk_formulary as  isdrugtypebulk_formulary");				
				strSQL.append(" FROM ip_drugformulary df ");
				strSQL.append(" left outer join ip_items i on i.itemid = df.itemid");
				strSQL.append(" left outer join ip_drugformulary_associatedProducts dfap on df.id = dfap.formularyid and df.ndc = dfap.ndc and dfap.status = 1 and dfap.delflag = 0");
				strSQL.append(" left outer join ip_drugformulary_OrderTypeSetup idfo on idfo.formularyid = df.id and idfo.ordertype = :orderType AND idfo.delflag = 0 ");
				strSQL.append(" left outer join ip_drugformulary_facilities idff on idff.formularyid = df.id and idff.facilityid = :facilityId and idff.delflag = 0");
				strSQL.append(" left outer join ip_drugformulary_notes dfn on df.id = dfn.formularyid");
				strSQL.append(" left outer join ip_drugformulary_common_settings dfcs on dfcs.routedgenericitemid = df.routedgenericitemid");
				strSQL.append(" left outer join edi_cptcodes cpt on cpt.itemid = df.cptcodeitemid");
				if(!"".equals(formulationExternal)){
					strSQL.append(" join ip_drugformulary_formulation_external idfe on idfe.formularyid = df.id and idfe.ext_mapping_desc = :formulationExternal and idfe.delflag = 0");
				}
				strSQL.append(" WHERE df.routedGenericItemId=:routedGenericItemId and df.delflag=0 and df.isactive = 1");

				paramMap.put(ROUTED_GENERIC_ITEMID,routedGenericItemId);
				paramMap.put(ORDERTYPE,orderType);
				paramMap.put(FACILITYID,facilityId);
				paramMap.put("formulationExternal", formulationExternal);
				formularyItems = (ArrayList<MedOrderDetail>)namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new ResultSetExtractor<ArrayList<MedOrderDetail>>() {

					@Override
					public ArrayList<MedOrderDetail> extractData(ResultSet rs) throws SQLException {

						ArrayList<MedOrderDetail> result = new ArrayList<MedOrderDetail>();
						while(rs.next()){

							int isAvailableForAll = rs.getInt(ISAVAILABLEFORALL);
							int facFormularyId = rs.getInt("facFormularyId");

							if(isAvailableForAll > 0 || facFormularyId > 0 ){ //Add in the list only if it is available for facility

								MedOrderDetail product = new MedOrderDetail();
								product.setItemName(rs.getString(ITEM_NAME));
								product.setGenericName(rs.getString(GENERIC_DRUG_NAME));
								product.setBrandName(rxOrderTreeService.getAssignedBrandName(rs.getInt("id")));
								product.setDrugFormularyId(rs.getInt("id"));
								product.setOrderDDID(rs.getInt("ddid"));
								product.setIpItemId(rs.getInt(ITEMID));

								product.setDrugNameId(rs.getInt(DRUG_NAME_ID));
								product.setOrderFormulation(rs.getString(FORMULATION));
								product.setChargeCode(rs.getString(CHARGE_CODE));
								product.setCalculate(rs.getInt(IS_CALCULATE) == 1);
								product.setIvDiluent(rs.getInt(ISIVDILUENT1) == 1);
								product.setAdditive(rs.getInt(ISADDITIVE) == 1);
								
								product.setFormularyDoseSize(RxUtil.preSanitizeStringAsDouble(rs.getString("formDoseSize"), true));
								product.setFormularyDoseUOM(rs.getString("formDoseUOM"));
								product.setFormularyDispenseSize(RxUtil.preSanitizeStringAsDouble(rs.getString("formDispenseSize"), true));
								product.setFormularyDispenseUOM(rs.getString("formDispenseUOM"));
								product.setRxNorm(rs.getString(RX_NORM));
								
								product.seteMARInstructions(rs.getString(EMAR_INSTR));
								product.setInternalNotes(Util.ifNullEmpty(rs.getString(INTERNAL_NOTES), ""));

								int chargeTypeId = rs.getInt(CHARGE_TYPE_ID);
								double awup = rs.getDouble("awup");
								double costToProc = rs.getDouble(COST_TO_PROC);
								

								double costWithChargeType = 0.0;
								PriceRuleParam priceRuleParam = null;
								if(chargeTypeId > 0){
									costWithChargeType = getCostCalculatedWithChargeType(chargeTypeId, awup, costToProc, 1, facilityId);
									priceRuleParam = getCostCalculatedWithChargeType(chargeTypeId, awup, costToProc, facilityId);
								}
								
								product.setOrderStrength(rs.getString(STRENGTH));
								product.setOrderNDCCode(rs.getString("ndc"));
								product.setUpcCode(rs.getString("upc"));
								product.setAwup(rs.getDouble("awup"));
								product.setUnitCostCalculatedWithChargeType(costWithChargeType);
								product.setPriceRuleParam(priceRuleParam);
								product.setDrugTypeBulk(rs.getInt("isdrugtypebulk_formulary") == 1);
								product.setCalculate(rs.getInt("isCalculate") == 1);
								product.setPackSize(rs.getString("packsize"));
								product.setPackSizeUnitcode(rs.getString("packsizeunitcode"));
								product.setDispense("");

								result.add(product);

							}
						}
						return result;
					}

				});

			} 
	        catch (Exception e) {
	            EcwLog.AppendExceptionToLog(e);
	        }
			return formularyItems;
		}
		
		public PriceRuleParam getCostCalculatedWithChargeType(int chargeTypeId, final double awup, final double costToProc, int facilityId){

			PriceRuleParam result = new PriceRuleParam();

			try{
				Map<String,Object> paramMap = new HashMap<String,Object>();

				StringBuilder strSQL = new StringBuilder();
				strSQL.append(" SELECT idp.id as priceruleId,costMin, costMax, markup, additionalCharge, priceCharge, priceCode, idp.chargeSize");
				strSQL.append(" FROM ip_drug_chargecode idc ");
				strSQL.append(" JOIN ip_drug_pricerule idp ON idp.charegtypeid = idc.id");
				strSQL.append(" LEFT OUTER JOIN ip_drug_chargecode_facility idpf ON idpf.drugchargecodeid = idc.id and facilityid = :facilityId and idpf.deleteFlag = 0");
				strSQL.append(" WHERE idc.delflag = 0 and idp.deleteflag = 0 and idc.id = :chargeTypeId");

				paramMap.put(CHARGE_TYPE_ID, chargeTypeId);
				paramMap.put(FACILITYID, facilityId);

				result = (PriceRuleParam) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new ResultSetExtractor<PriceRuleParam>(){

					@Override
					public PriceRuleParam extractData(ResultSet rs) throws SQLException {
						PriceRuleParam result = new PriceRuleParam();
						while(rs.next()){
							double costMin = rs.getDouble("costMin");
							double costMax = rs.getDouble("costMax");
							int markUp = rs.getInt("markup");
							int chargePerDose = rs.getInt("chargeSize");
							double costToBeConsidered = awup;
							
							double minimumCharge = rs.getDouble("priceCharge");
							int priceCode = rs.getInt("priceCode");
							
							if(priceCode == NUM_2){
								costToBeConsidered = costToProc;
							}
							double additionalCharge = rs.getDouble("additionalCharge");
							
							if(costMin <= costToBeConsidered && costMax >= costToBeConsidered){
								result.setCostToBeConsidered(costToBeConsidered);
								result.setAdditionalCharge(additionalCharge);
								result.setMarkUp(markUp);
								result.setChargePerDose(chargePerDose == NUM_2);
								result.setMinimumCharge(minimumCharge);
								result.setPriceRuleId(rs.getInt("priceruleId"));
								break;
							}

						}
						return result;
					}

				});

			}catch(Exception ex ){
				 EcwLog.AppendExceptionToLog(ex);
			}
			return result;
		}
		
		public double getCostCalculatedWithChargeType(int chargeTypeId, double unitCost, double costToProc, double dispense, int facilityId){
			
			double result = 0;
			
			PriceRuleParam priceRuleParam = getCostCalculatedWithChargeType(chargeTypeId, unitCost, costToProc, facilityId) ;
			
			if(priceRuleParam.isChargePerDose()){
				result = (((priceRuleParam.getCostToBeConsidered() * dispense) * priceRuleParam.getMarkUp()/NUM_100) + priceRuleParam.getAdditionalCharge());
			} else {
				result = ((priceRuleParam.getCostToBeConsidered() * priceRuleParam.getMarkUp()/NUM_100) + priceRuleParam.getAdditionalCharge()) * dispense;
			}
			
			if(result < priceRuleParam.getMinimumCharge()){
				result = priceRuleParam.getMinimumCharge();
			}
			
			DecimalFormat df = new DecimalFormat("#.###");
			result = Double.parseDouble(df.format(result));
			return result;
		}
		
		/**
		 * get TC2 ClassId by ItemId
		 * @param nItemId
		 * @return 
		 */
		public String getTC2ClassIdByItemId(final int nItemId) 
		{
			StringBuilder strSql  = new StringBuilder();
			strSql.append("	SELECT  MAX(dfdc.drugClassID) as drugClassID from ip_drugformulary_drug_classification dfdc ")
				  .append(" INNER JOIN ip_drugformulary df ON df.id = dfdc.formularyid AND dfdc.drugClassType = 'TC2' WHERE df.itemId = :itemID and df.delflag=0 ");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put(ITEMID1, nItemId);
			return namedParameterJdbcTemplate.queryForObject(strSql.toString(), mapParam, String.class);
		}
		
		/**
		 * get List of ItemID of Therapetic Equivalent Drugs By TC2 ClassId
		 * @param strTC2ClassId
		 * @return 
		 */
		public List<Integer> getItemIdsByTC2ClassId(final String strTC2ClassId) 
		{
			StringBuilder strSql 	= new StringBuilder();
			strSql.append(" SELECT distinct df.itemId from ip_drugformulary df INNER JOIN ip_drugformulary_drug_classification dfdc ")
				  .append(" ON df.id = dfdc.formularyid WHERE dfdc.drugClassID = :strTC2ClassId AND df.isactive = 1 and df.delflag=0 ");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put("strTC2ClassId", strTC2ClassId);
			return namedParameterJdbcTemplate.query(strSql.toString(), mapParam, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
					return rs.getInt(ITEM_ID);
				}
			});
		}
		
		
		/**
		 * get List of Routed Generic ItemID of Therapetic Equivalent Drugs By TC2 ClassId
		 * @param strTC2ClassId
		 * @return 
		 */
		public List<Integer> getRoutedGenericItemIdsByTC2ClassId(final String strTC2ClassId) 
		{
			StringBuilder strSql 	= new StringBuilder();
			strSql.append(" SELECT distinct df.routedGenericitemId from ip_drugformulary df INNER JOIN ip_drugformulary_drug_classification dfdc ")
				  .append(" ON df.id = dfdc.formularyid WHERE dfdc.drugClassID = :strTC2ClassId AND df.isactive = 1 and df.delflag=0 ");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put("strTC2ClassId", strTC2ClassId);
			return namedParameterJdbcTemplate.query(strSql.toString(), mapParam, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
					return rs.getInt(ROUTED_GENERIC_ITEMID);
				}
			});
		}
		
		/**
		 * This method is used to get drug formulary common settings by routed generic itemid
		 * @param routedgenericitemid
		 * @return
		 */
		public List<Map<String,Object>> getFormularyCommonSettings(int routedgenericitemid,int formularyId) {
			Map<String,Object> paramMap = new HashMap<>(NUM_2);
	        StringBuilder strSQL = new StringBuilder();
	        	strSQL.append(QueryBuilderUtils.sqlSelect)
	        	.append("isRenewInExpiringTab,isrestrictedoutsideOS ") 
        		.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY_COMMON_SETTINGS.table())
        		.append(QueryBuilderUtils.sqlWhere).append("routedgenericitemid= :routedgenericitemid")
        		.append(QueryBuilderUtils.sqlAnd).append("delflag=0");
	        	paramMap.put(ROUTED_GENERIC_ITEM_ID, routedgenericitemid);
	        	if(formularyId>0){
	        		strSQL.append(QueryBuilderUtils.sqlAnd).append("formularyId=:formularyId");	
	        		paramMap.put(FORMULARYID, formularyId);
	        	}
	        return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<Map<String,Object>>() {
				@Override
				public Map<String,Object> mapRow(ResultSet rs, int arg1) throws SQLException {
					Map<String,Object> mpRow = new HashMap<>();
					mpRow.put(ISRENEWINEXPIRINGTAB,rs.getInt(ISRENEWINEXPIRINGTAB));
					mpRow.put(ISRESTRICTEDOUTSIDEOS,rs.getInt(ISRESTRICTEDOUTSIDEOS));
					return mpRow;
				}
	        });
	    }
		
		public List<TemplateForNotes> getFormularyNoteList(int formularyId){
			Map<String,Object> paramMap = new HashMap<>(1);
			paramMap.put(FORMULARY_ID, formularyId);
	        StringBuilder strQuery = new StringBuilder("");
	        strQuery.append(SELECT)
	        		.append(" orderentry_instr,emar_instr,pharmacy_instr,internal_notes,instructions,notes,formularyid ")
			  		.append(" from ip_drugformulary_notes ")
	        		.append(" where formularyId=:").append(FORMULARY_ID).append(" and delflag=0"); 
	        return namedParameterJdbcTemplate.query(strQuery.toString(), paramMap, new RowMapper<TemplateForNotes>() {
				@Override
				public TemplateForNotes mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForNotes tmpl=new TemplateForNotes();
					tmpl.setOrderentry(rs.getString(ORDERENTRY_INSTR));
					tmpl.setEmar(rs.getString(EMAR_INSTR));
					tmpl.setPharmacy(rs.getString(PHARMACY_INSTR));
					tmpl.setInternal(rs.getString(INTERNAL_NOTES));
					tmpl.setInstructions(rs.getString(INSTRUCTIONS));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setFormularyId(rs.getString(FORMULARY_ID)); 
					return tmpl;
				}
	        });
		} 
		
		//added for dispense data
		/**
		 * @param formularyId
		 * @return Map<String, Object>
		 * API to get object of drug dispense code from formulary  
		 */
		public Map<String, Object> getDrugDispenseDetailFromFormularyDetails(int formularyId)
		{
			Map<String,Object> dispenseDrugMap = new HashMap<String,Object>();
			StringBuilder strBuilderQuery = new StringBuilder("SELECT id,drugdispensecode FROM   ")
					.append(TBL_IP_DRUGFORMULARY)
					.append(" WHERE id=:formularyId AND delflag=0 "); 
				dispenseDrugMap.put(FORMULARYID, formularyId);
		    return namedParameterJdbcTemplate.queryForMap(strBuilderQuery.toString(), dispenseDrugMap);
		}
		/**
	  	 * @param formularyId
	  	 * @param userId
	  	 * @return List<DispenseMachineQuantityModal> 
	  	 * API to get active dispense detail with quantity 
	  	 */
			public List<DispenseStockAreaQuantityMappingModal> getAllDispenseDetails(int formularyId,int userId){
			Map<String,Object> paramMap = new HashMap<String,Object>();
				final String userTimeZone=IPTzUtils.getTimeZoneForResource(userId);
				List<DispenseStockAreaQuantityMappingModal> dispenseStockAreaQtyDetails = null;
				StringBuilder sql = null;
		      		sql=new StringBuilder();
		      		sql.append(" SELECT ipstockarea.id AS id ,ipstockarea.name AS name,ipstockarea.location AS location,ipstockqtymapp.id AS quantityId,formularyid,quantity,ipstockqtymapp.modifiedby AS modifiedby, ");
			    	sql.append(" ipstockqtymapp.modifieddatetime AS modifieddatetime  FROM   ");
			    	sql.append(" ( ");
			    	sql.append(" SELECT id,name,location FROM ip_dispense_stockarea  WHERE ip_dispense_stockarea.deleteflag=0  ");
			    	sql.append(" ) ipstockarea ");
			    	sql.append(" LEFT JOIN   ");
			    	sql.append(" ( ");
			    	sql.append("  SELECT id,formularyid,dispensestockid,drugdispensecode,quantity,modifieddatetime,");
			    	sql.append(SqlTranslator.concatColsForFullName("u1")).append(" AS modifiedby");
			    	sql.append("  FROM ip_dispense_stockarea_formulary_qtymapping  ");
			    	sql.append("  LEFT JOIN users u1 on u1.uid = ip_dispense_stockarea_formulary_qtymapping.modifiedby  ");
			    	sql.append("  WHERE ip_dispense_stockarea_formulary_qtymapping.formularyid=:formularyId ");
			    	sql.append("   ) ipstockqtymapp ON  ");
			    	sql.append("   ipstockarea.id=ipstockqtymapp.dispensestockid;  ");
		  	    	paramMap.put(FORMULARYID,formularyId);
		  	    	dispenseStockAreaQtyDetails = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<DispenseStockAreaQuantityMappingModal>() {
		  			@Override
		  			public DispenseStockAreaQuantityMappingModal mapRow(ResultSet rs, int arg1) {
		  				DispenseStockAreaQuantityMappingModal dispenseStockAreaQuantityMappingModal=new DispenseStockAreaQuantityMappingModal();
		  				try {
		  				dispenseStockAreaQuantityMappingModal.setDispenseStockId(rs.getString("id"));
		  				dispenseStockAreaQuantityMappingModal.setName(rs.getString("name"));
		  				dispenseStockAreaQuantityMappingModal.setLocation(rs.getString("location"));
		  				dispenseStockAreaQuantityMappingModal.setFormularyId(rs.getString(FORMULARY_ID));
		  				dispenseStockAreaQuantityMappingModal.setModifiedby(rs.getString(MODIFIED_BY));
						dispenseStockAreaQuantityMappingModal.setModifiedTime(IPTzUtils.convertDateStrInTz(rs.getString("modifieddatetime"), IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone ));
						dispenseStockAreaQuantityMappingModal.setDispenseQtyId(rs.getString("quantityId"));
		  				dispenseStockAreaQuantityMappingModal.setQuantity(rs.getString("quantity"));
						} catch (SQLException | ParseException e) {
							EcwLog.AppendExceptionToLog(e);
						}
		  				
		  				return dispenseStockAreaQuantityMappingModal;
		  			}
		      	});
		      return dispenseStockAreaQtyDetails;
		  }
				
				/**
				 * @param dispenseMap
				 * @return StatusMap
				 * API  to save or update drug  dispense detail 
				 */
				public StatusMap saveUpdateDispenseDetails(Map<String,Object> dispenseMap){
					StatusMap statusMap = new StatusMap();
					boolean isInsertRecord = true;
					int dispenseStockAreaQtyMappingId=0;
					StringBuilder query=null;
			        try {
			        	statusMap.setFail();
			        	query=new StringBuilder();
			    		query.append(" SELECT drugdispensecode FROM   ");
			    		query.append(TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING);
						query.append(" WHERE id=:dispLocRowId AND deleteflag=0 ");
						
						List<Map<String, Object>> data=namedParameterJdbcTemplate.queryForList(query.toString(), dispenseMap);
			        	query = new StringBuilder();
			        	if(!data.isEmpty())
			        	{
					        		query.append(UPDATE);
					        		query.append(TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING);
			        				query.append(" SET formularyid=:formularyId,modifiedby=:modifiedBy,quantity=:gridQuantity,modifieddatetime=:modifiedTime,drugdispensecode=:drugdispenseCode ");
			        				query.append(" WHERE id=:dispLocRowId ");
			        				isInsertRecord = false;
			        	}
			        	else
			        	{	
			        		query.append(" INSERT INTO ip_dispense_stockarea_formulary_qtymapping(formularyid,dispensestockid,drugdispensecode,quantity,createdby,createddatetime,modifiedby,modifieddatetime,deleteflag) ");
							query.append(" VALUES(:formularyId,:dispenseStockId,:drugdispenseCode,:gridQuantity,:createdBy,:createdTime,:modifiedBy,:modifiedTime,:deleteFlag); ");
			        	}
			        	
			        	if(isInsertRecord){
			        		dispenseStockAreaQtyMappingId = Util.insertAndReturnId(namedParameterJdbcTemplate, query.toString(), dispenseMap, "dispenseStockAreaQtyMappingId");
			        		dispenseMap.put("dispenseFormularyMappingId", dispenseStockAreaQtyMappingId);
			        		statusMap.setSuccess();
			        		statusMap.setCustom("message", "Drug dispense code details saved successfully");
			       		}else{
			       			namedParameterJdbcTemplate.update(query.toString(), dispenseMap);
			       			statusMap.setSuccess();
			       			statusMap.setCustom("message", "Drug dispense code details updated successfully");
			       		}
			        } 
			        catch(DataAccessException ex ){
						 EcwLog.AppendExceptionToLog(ex);
						 statusMap.setFail("Something went wrong while saving/updating drug dispense details");
					}
			        return statusMap;
				}
				
				
				/**
				 * @param namedParameters
				 * @return List<String>
				 * API to check uniqueness of dispense code while saving 
				 */
				public List<String> checkDispenseCodeInFormularyDetail(Map<String, Object> namedParameters) {
					StringBuilder strBuilderQuery = new StringBuilder(" SELECT drugdispensecode FROM   ").append(TBL_IP_DRUGFORMULARY)
							.append(" WHERE drugdispensecode=:drugdispenseCode AND id<>:formularyId AND delflag=0 ");
					return namedParameterJdbcTemplate.queryForList(strBuilderQuery.toString(), namedParameters,String.class);
				}
				/**
				 * @param namedParameters
				 * @return int
				 * API to update the dispense code in locaionwise of dispense details
				 */
				public int updateDispenseLocationWiseQuantity(Map<String, Object> namedParameters) {
					StringBuilder query = new StringBuilder(UPDATE).append(TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING);
								query.append("  SET drugdispensecode=:drugdispenseCode,modifieddatetime=:modifiedTime,modifiedby=:modifiedBy,quantity=:gridQuantity WHERE id=:dispLocRowId");
					return namedParameterJdbcTemplate.update(query.toString(), namedParameters);
				}
				/**
				 * @param formularyId
				 * @param drugDispenseCode
				 * @return int
				 * API to update the dispense code in ip_drug_formulary 
				 */
				public int updateDrugDispenseCodeInFormulary(int formularyId,String drugDispenseCode) {
						Map<String,Object> dispenseParam = new HashMap<String,Object>();
					StringBuilder strBuilderQuery = new StringBuilder("UPDATE ").append(" ip_drugformulary  ");
							strBuilderQuery.append(" SET  drugdispensecode=:drugDispenseCode");
							strBuilderQuery.append(" WHERE id=:formularyId AND delflag=0 ");
							dispenseParam.put(FORMULARYID, formularyId);
							dispenseParam.put("drugDispenseCode", drugDispenseCode);
					return namedParameterJdbcTemplate.update(strBuilderQuery.toString(), dispenseParam);
				}
				
				/**
				 * @param formularyId
				 * @param drugDispenseCode
				 * @return int
				 * @purpose update dispense code in dispense formulary location quantity details
				 */
				public int updateDispenseCodeForQuantityWise(int formularyId,String drugDispenseCode) {
					Map<String,Object> dispenseParam = new HashMap<String,Object>();
					StringBuilder strBuilderQuery = new StringBuilder("UPDATE ").append(" ip_dispense_stockarea_formulary_qtymapping ");
					strBuilderQuery.append(" SET drugdispensecode=:drugDispenseCode  WHERE formularyid=:formularyId");
					dispenseParam.put(FORMULARYID, formularyId);
					dispenseParam.put("drugDispenseCode", drugDispenseCode);
				return namedParameterJdbcTemplate.update(strBuilderQuery.toString(), dispenseParam);
			}
		
		/**
		 * @param orderId
		 * @return List<CostPerFormulary> which has formularywise cost calculated with charge type based on ordertype
		 */
		public List<CostPerFormulary> getCostPerFormularyByOrderId(long medOrderId){

			List<CostPerFormulary> result = new ArrayList<CostPerFormulary>();

			try{
				Map<String, Object> orderDetails = getOrderDetails(medOrderId);

				int orderType = Util.getIntValue(orderDetails, ORDERTYPE);
				final int facilityId = Util.getIntValue(orderDetails, FACILITYID);
				final HashMap<String,Object> paramMapHCPCS = new HashMap<String,Object>();
				if(orderType > 0){
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("medOrderId", medOrderId);
					paramMap.put(ORDERTYPE, orderType);
					paramMap.put(FACILITYID, facilityId);

					StringBuilder strSQL = new StringBuilder();
					strSQL.append("SELECT ");
					strSQL.append(" df.id as formularyid, ");
					strSQL.append(" df.ndc as formularyndc, ");
					strSQL.append(" dfap.ndc as assocprodndc, ");
					strSQL.append(" df.cptcodeitemid as cptcodeitemid,");
					strSQL.append(" df.chargecode as chargecode,");
					strSQL.append(" dfap.awup as awup, ");
					strSQL.append(" dfap.cost_to_proc as cost_to_proc,");
					strSQL.append(" idfo.chargeTypeId as chargeTypeId,");
					strSQL.append(" ipmd.orderDose as orderDose, ");
					strSQL.append(" ipmd.formularyDose as formDoseSize, ");
					strSQL.append(" ipmd.formularyDoseUOM as formDoseUOM,");
					strSQL.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DISPENSE_UOM.getColumnName()).append(" as formDispenseUOM,");
					strSQL.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DISPENSE_SIZE.getColumnName()).append(" as formDispenseSize,");
					strSQL.append(" ipmd.").append(MedOrderDtlTblColumn.ACTUAL_DISPENSE.getColumnName()).append(" as actualDispense,");		
					strSQL.append(" ipmd.").append(MedOrderDtlTblColumn.ORDER_DISPENSE.getColumnName()).append(" as dispense,");
					strSQL.append(" ipmd.dispenseQty, ");
					strSQL.append(" dfap.ppid, ");
					strSQL.append(" cpt.HCPCSCodeRange as HCPCSCodeRange, ");
					strSQL.append(" cpt.HCPCSCodeType as HCPCSCodeType, ");
					strSQL.append(" cpt.HCPCSCodeUnit as HCPCSCodeUnit, ");
					strSQL.append(" ipptmed.issFlag as issFlag, ");
					strSQL.append(" ipptmed.configuredISSFlag as configuredISSFlag, ");
					strSQL.append(" ipptmed.minDose as minDose, ");
					strSQL.append(" ipptmed.maxDose as maxDose, ");
					strSQL.append(" dfap.packsizeunitcode as  packsizeunitcode, ");
					strSQL.append(" dfap.packsize as packsize, ");
					strSQL.append(" dfap.packType as packType, ");
					strSQL.append(" df.isCalculate as isCalculate, ");					
					strSQL.append(" df.isdrugtypebulk_formulary as  isdrugtypebulk_formulary ");
					strSQL.append(" FROM ");
					strSQL.append(" ip_ptMedicationOrders ipptmed ");
					strSQL.append(" INNER JOIN ip_ptmedicationorders_detail ipmd ");
					strSQL.append(" ON ipptmed.medorderid = ipmd.ptMedOrderId ");
					strSQL.append(" INNER JOIN ip_drugformulary df on df.id = ipmd.drugformularyid");
					strSQL.append(" INNER JOIN edi_cptcodes cpt on cpt.ItemId = df.cptcodeitemid");
					strSQL.append(" LEFT OUTER JOIN ip_drugformulary_associatedProducts dfap on dfap.formularyid = df.id and dfap.delflag = 0");
					strSQL.append(" LEFT OUTER JOIN ip_drugformulary_OrderTypeSetup idfo on idfo.formularyid = df.id and idfo.ordertype = :orderType AND idfo.delflag = 0");
					strSQL.append(" WHERE ");
					strSQL.append(" ipmd.ptmedorderid = :medOrderId and df.delflag=0 and df.isactive = 1 ");

					result = namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<CostPerFormulary>(){

						@Override
						public CostPerFormulary mapRow(ResultSet rs, int arg1) throws SQLException {
							CostPerFormulary costPerFormulary = new CostPerFormulary();
							int formularyId = rs.getInt(FORMULARYID);
							int chargeTypeId = rs.getInt(CHARGE_TYPE_ID);
							double awup = rs.getDouble("awup");
							double costToProc = rs.getDouble(COST_TO_PROC);
							WorkQueueDAOImpl workQueueDAOImpl =(WorkQueueDAOImpl) EcwAppContext.getObject(WorkQueueDAOImpl.class);
							double dispense = rs.getDouble("actualDispense") <= 0 ? 1 : rs.getDouble("actualDispense"); 
							if((rs.getInt("isdrugtypebulk_formulary") == 1) && WorkQueueDAOImpl.convertUOM(rs.getString("formDispenseUOM")).equalsIgnoreCase(WorkQueueDAOImpl.convertUOM(rs.getString("packsizeunitcode")))) {
								String strPackSize =  RxUtil.sanitizeStringAsDouble(rs.getString("packsize"), true);
								double orderDispense = Double.parseDouble(rs.getString("dispense"));	
								double packSizeUnit = Math.ceil(orderDispense / Double.parseDouble(strPackSize));
								dispense = (Double.parseDouble(strPackSize) * packSizeUnit);								
							}
							String formularyNDC = rs.getString("formularyndc");
							String assocProdNDC = rs.getString("assocprodndc");

							PriceRuleParam priceRuleParam = getCostCalculatedWithChargeType(chargeTypeId, awup, costToProc, facilityId);
							costPerFormulary.setFormularyId(formularyId);
							costPerFormulary.setChargeTypeId(chargeTypeId);
							costPerFormulary.setDispense(dispense);
							costPerFormulary.setPrimaryNDC(formularyNDC);
							costPerFormulary.setAssocProdNDC(assocProdNDC);
							costPerFormulary.setPriceRuleParam(priceRuleParam);
							costPerFormulary.setCptItemId(rs.getInt(CPT_CODE_ITEMID));
							costPerFormulary.setChargeCode(rs.getString(CHARGE_CODE));
							costPerFormulary.setAwup(awup);
							costPerFormulary.setCostToProcure(costToProc);
							costPerFormulary.setPpId(rs.getString("ppid"));
							costPerFormulary.setDrugTypeBulk(rs.getInt("isdrugtypebulk_formulary") == 1);
							costPerFormulary.setCalculate(rs.getInt("isCalculate") == 1);
							String OrderDose = "";
							if(rs.getInt("issFlag") == 1) {
								OrderDose = Util.ifNullEmpty(rs.getString("maxDose"), "0");								
								if("0".equals(OrderDose))
									EcwLog.AppendToLog("[FormularySetupDao] - Setting orderDose 0 if maxDose is black : FormularySetupDao.java");
							} else {
								OrderDose = rs.getString(MedOrderDtlTblColumn.ORDER_DOSE.getColumnName());
							}	
							paramMapHCPCS.put("orderDose", OrderDose);
							paramMapHCPCS.put("formularyDose", rs.getString("formDoseSize"));
							paramMapHCPCS.put("formularyDoseUOM", rs.getString("formDoseUOM"));
							paramMapHCPCS.put("orderDispense", rs.getString("dispense"));
							paramMapHCPCS.put("packSize", rs.getString("packsize"));				
							paramMapHCPCS.put("isCalcuted", costPerFormulary.isCalculate());
							paramMapHCPCS.put("isBulk", costPerFormulary.isDrugTypeBulk());
							paramMapHCPCS.put("hcpcsCodeRange", rs.getString("HCPCSCodeRange"));
							paramMapHCPCS.put("hcpcsCodeType", rs.getString("HCPCSCodeType"));
							paramMapHCPCS.put("hcpcsCodeUnit", rs.getString("HCPCSCodeUnit"));
							double totalHcpcsUnit = workQueueDAOImpl.getTotalHCPCSUnitCalculated(paramMapHCPCS);							
							costPerFormulary.setTotalHcpcsUnit(totalHcpcsUnit);

							return costPerFormulary;
						}
					});
				}

			}catch(Exception ex ){
				 EcwLog.AppendExceptionToLog(ex);
			}
			return result;

		}

		public Map<String, Object> getOrderDetails(long medOrderId){

			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("medOrderId", medOrderId);

			StringBuilder strSQL = new StringBuilder();
			strSQL.append("select enc.facilityid, ipmo.ordertype");
			strSQL.append(" from ip_ptmedicationorders ipmo");
			strSQL.append(" inner join ip_ptorders ipo on ipo.orderid = ipmo.ptorderid");
			strSQL.append(" inner join enc on enc.encounterid = ipo.episodeEncId ");
			strSQL.append(" WHERE ipmo.medorderid = :medOrderId");

			return namedParameterJdbcTemplate.queryForMap(strSQL.toString(), paramMap);
		}
		
		/**
		 * This API used for to get all configured IV Diluent Rate list in formulary setup
		 * @param formularyId
		 * @param nSigTypeId
		 * @return list of IV Rates
		 */
		public List<TemplateForIVRate> getFormularyIVDiluentRateList(int formularyId, int nSigTypeId)
		{	
			Map<String,Object> paramMapOTS = new HashMap<>();
			StringBuilder strSql = new StringBuilder("SELECT ");
			strSql.append(" drugIV.id as id,drugIV.formularyid as formularyid,drugIV.strength as ivrstrength,drugIV.strengthuom as ivrstrengthuom ")
			.append(" FROM ip_drugformulary_iv_rate drugIV")
			.append(" INNER JOIN ip_drugformulary drug on drugIV.formularyid = drug.id and drugIV.delflag=0 and drug.delflag=0 ")
			.append(" INNER JOIN ip_items ii on drug.itemid = ii.itemid ")
			.append(" INNER JOIN ip_drugformulary_ordertypesetup ots on drug.id = ots.[formularyid ] and ots.id = drugIV.ordertypesetupid and ots.delflag=0 ")
			.append(" WHERE ").append(" ots.[ordertype ] = "+nSigTypeId+" AND ").append(" drugIV.formularyid=:formularyid ");
			paramMapOTS.put(FORMULARY_ID, formularyId); 					
			return namedParameterJdbcTemplate.query(strSql.toString(),paramMapOTS, new RowMapper<TemplateForIVRate>(){
				@Override
				public TemplateForIVRate  mapRow(ResultSet rs, int arg) throws SQLException {
					TemplateForIVRate  tmpl=new TemplateForIVRate ();
					tmpl.setId(rs.getInt("id"));
					tmpl.setFormularyid(rs.getInt(FORMULARY_ID));				
					tmpl.setStrength(rs.getString("ivrstrength"));
					tmpl.setStrengthuom(rs.getString("ivrstrengthuom"));
					return tmpl;
				}
			});
		}
		
		public Map<String,String> getRoutedGenericId(String formularyId) {
			StringBuilder selectInterQuery = null;
			Map<String,Object> paramMap = new HashMap<>();
			selectInterQuery = new StringBuilder();
			selectInterQuery.append("select items.routedGenericId,formu.gpi ").append(" from ip_drugformulary formu ")
							.append(" INNER JOIN ip_items items on formu.itemId = items.itemId ")
							.append(" where formu.id = :formularyId and formu.delflag=0 ");
			paramMap.put(FORMULARYID, formularyId);
			return namedParameterJdbcTemplate.query(selectInterQuery.toString(), paramMap, new ResultSetExtractor<Map<String,String>>() {
				@Override
				public Map<String,String> extractData(ResultSet ors) throws SQLException {
					Map<String,String> idMap = new HashMap<>();
					while (ors.next()) {
						idMap.put("routedGenericId", ors.getString("routedGenericId"));
						idMap.put("gpi", ors.getString("gpi"));
					}
					return idMap;
				}
			});
		}
		
		public int saveDrugDrugInteraction(MapSqlParameterSource mapSqlParamSrc){
			StringBuilder insertDrugInterQuery = null;
			int autoIncrInteraction = 0;
			insertDrugInterQuery = new StringBuilder();
			/**********************************************
			 * Insert drug drug interaction for formulary *
			 *********************************************/
			insertDrugInterQuery.append("INSERT INTO ").append(IP_DRUGFORMULARY_DRUGINTERACTION)
								.append(" (formularyId,formularyRoutedGenericItemId,formularyDrugNameDesc,formularyGPI,formularyDDID,interactingRoutedGenericItemId,interactingDrugNameDesc,interactingGPI,interactingDDID,interactionlevelOriginal,interactionlevelModified,interactionType,interactionDesc,createdBy,createdDate) ")
								.append(" values (:formularyId,:formularyRoutedGenericItemId,:formularyDrugNameDesc,:formularyGPI,:formularyDDID,:interactingRoutedGenericItemId,:interactingDrugNameDesc,:interactingGPI,:interactingDDID,:interactionlevelOriginal,:interactionlevelModified,:interactionType,:interactionDesc,:createdBy,:createdDate) ");
			autoIncrInteraction = Util.executeAndReturnKey(insertDrugInterQuery.toString(), mapSqlParamSrc);
			return autoIncrInteraction;
		}
		
		
		public boolean updateDrugDrugInteraction(MapSqlParameterSource mapSqlParamSrc){
			StringBuilder insertDrugInterQuery = null;
			int noOfRowsAffected = 0;
			insertDrugInterQuery = new StringBuilder();
			/**********************************************
			 * update drug drug interaction for formulary *
			 *********************************************/
			insertDrugInterQuery.append(UPDATE).append(IP_DRUGFORMULARY_DRUGINTERACTION)
								.append(" SET interactionlevelModified = :interactionlevelModified ,modifiedBy = :modifiedBy,modifiedDate = :modifiedDate")
								.append(" WHERE id = :id ");
			noOfRowsAffected = namedParameterJdbcTemplate.update(insertDrugInterQuery.toString(), mapSqlParamSrc);
			return noOfRowsAffected > 0 ? true : false;
		}
		
		public boolean deleteDrugDrugInteraction(MapSqlParameterSource mapSqlParamSrc){
			StringBuilder insertDrugInterQuery = null;
			int noOfRowsAffected = 0;
			insertDrugInterQuery = new StringBuilder();
			/**********************************************
			 * delete drug drug interaction for formulary *
			 *********************************************/
			insertDrugInterQuery.append(UPDATE).append(IP_DRUGFORMULARY_DRUGINTERACTION)
								.append(" SET deleteFlag = :deleteFlag,modifiedBy = :modifiedBy,modifiedDate = :modifiedDate")
								.append(" WHERE id = :id ");
			noOfRowsAffected = namedParameterJdbcTemplate.update(insertDrugInterQuery.toString(), mapSqlParamSrc);
			return noOfRowsAffected > 0 ? true : false;
		}	
		
		
		public List<Map<String, Object>> getSavedDrugDrugInteraction(List<Integer> routedGenericItemIds) {
			if(routedGenericItemIds.isEmpty()){
				return Collections.emptyList();
			}
			StringBuilder selectQuery = new StringBuilder();
			Map<String,Object> paramMap = new HashMap<>();
			/**********************************************
			 * select drug drug interaction for formulary *
			 *********************************************/
			selectQuery.append("SELECT inter.id,inter.formularyId,inter.formularyRoutedGenericItemId,inter.formularyDrugNameDesc,inter.formularyGPI,inter.formularyDDID,inter.interactingRoutedGenericItemId,inter.interactingDrugNameDesc,inter.interactingGPI,inter.interactingDDID,inter.interactionlevelOriginal,inter.interactionlevelModified,inter.createdBy,inter.createdDate,inter.modifiedBy,inter.modifiedDate,inter.interactionDesc  ")
					.append(" FROM ip_drugformulary drug ")
					.append(" INNER JOIN ip_drugformulary_druginteraction inter ON inter.formularyId = drug.id ")
					.append(" WHERE deleteFlag = 0 AND (formularyRoutedGenericItemId IN (:routedGenericItemId) OR interactingRoutedGenericItemId in (:routedGenericItemId))")
					.append(" ORDER BY  inter.id ");

			paramMap.put(ROUTED_GENERIC_ITEMID, routedGenericItemIds);
			return namedParameterJdbcTemplate.query(selectQuery.toString(), paramMap, new ResultSetExtractor<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> extractData(ResultSet ors) throws SQLException {
					List<Map<String, Object>> savedInteractionList = new ArrayList<>();
					Map<String, Object> interctionObj = null;
					while (ors.next()) {
						interctionObj = new HashMap<>();
						interctionObj.put("id", ors.getInt("id"));
						interctionObj.put(FORMULARYID, ors.getInt(FORMULARYID));
						interctionObj.put(FORMULARY_ROUTED_GENERIC_ITEMID, ors.getInt(FORMULARY_ROUTED_GENERIC_ITEMID));
						interctionObj.put("formularyDrugNameDesc", ors.getString("formularyDrugNameDesc"));
						interctionObj.put(FORMULARYGPI, ors.getString(FORMULARYGPI));
						interctionObj.put("formularyDDID", ors.getInt("formularyDDID"));
						interctionObj.put(INTERACTING_ROUTED_GENERIC_ITEMID, ors.getInt(INTERACTING_ROUTED_GENERIC_ITEMID));
						interctionObj.put("interactingDrugNameDesc", ors.getString("interactingDrugNameDesc"));
						interctionObj.put(INTERACTINGGPI, ors.getString(INTERACTINGGPI));
						interctionObj.put("interactingDDID",ors.getInt("interactingDDID"));
						interctionObj.put("interactionlevelOriginal", ors.getInt("interactionlevelOriginal"));
						interctionObj.put("interactionlevelModified", ors.getInt("interactionlevelModified"));
						interctionObj.put("interactionDesc", ors.getString("interactionDesc"));
						interctionObj.put("createdBy", ors.getInt("createdBy"));
						interctionObj.put("createdDate", ors.getDate("createdDate"));
						interctionObj.put("modifiedBy", ors.getInt("modifiedBy"));
						interctionObj.put("modifiedDate", ors.getDate("modifiedDate"));
						savedInteractionList.add(interctionObj);
					}
					return savedInteractionList;
				}
			});
		}
		
		public Boolean checkDuplicateInteraction(Map<String, Object> inputParam) {
			StringBuilder selectQuery = new StringBuilder();
			final int formularyRoutedGenericItemId = Util.getIntValue(inputParam, FORMULARY_ROUTED_GENERIC_ITEMID);
			final int interactingRoutedGenericItemId = Util.getIntValue(inputParam, INTERACTING_ROUTED_GENERIC_ITEMID);
			inputParam.put(INTERACTING_ROUTED_GENERIC_ITEMID, interactingRoutedGenericItemId);
			inputParam.put(FORMULARY_ROUTED_GENERIC_ITEMID, formularyRoutedGenericItemId);
			/**********************************************
			 * select drug drug interaction for formulary *
			 *********************************************/
			selectQuery.append("SELECT id,formularyId,formularyRoutedGenericItemId,formularyDrugNameid,formularyDrugNameDesc,formularyGPI,formularyDDID,interactingRoutedGenericItemId,interactingDrugNameid,interactingDrugNameDesc,interactingGPI,interactingDDID,interactionlevelOriginal,interactionlevelModified,createdBy,createdDate,modifiedBy,modifiedDate,interactionDesc ")
						.append(" from ip_drugformulary_druginteraction ")
//						.append(" WHERE deleteFlag = 0 AND (replace(formularyGPI,'-','') = :formularyGPI OR replace(interactingGPI,'-','') = :formularyGPI)")
						.append(" WHERE deleteFlag = 0 AND (formularyRoutedGenericItemId = :formularyRoutedGenericItemId OR interactingRoutedGenericItemId = :formularyRoutedGenericItemId)")
						.append(" ORDER BY  id ");
			return namedParameterJdbcTemplate.query(selectQuery.toString(), inputParam, new ResultSetExtractor<Boolean>() {
				@Override
				public Boolean extractData(ResultSet ors) throws SQLException {
					while (ors.next()) {
						if(ors.getInt(FORMULARY_ROUTED_GENERIC_ITEMID) == interactingRoutedGenericItemId || ors.getInt(INTERACTING_ROUTED_GENERIC_ITEMID) == interactingRoutedGenericItemId)
						return true;
					}
					return false;
				}
			});
		}
		
		/**
		 * API to get routedGenericItemId from list of GPIs
		 * @param inputParam
		 * @return StatusMap
		 */
		public Map<String,Integer> getRoutedGenricItemIds(List<String> genericProductIds){
			StringBuilder selectInterQuery = null;
			Map<String,Object> paramMap = new HashMap<>();
			selectInterQuery = new StringBuilder();
			selectInterQuery.append("select formu.routedGenericItemId,formu.gpi ").append(" from ip_drugformulary formu")
							.append(" where  replace(formu.gpi,'-','') IN (:genericProductIds) and formu.delflag=0 ");
			paramMap.put("genericProductIds", genericProductIds);
			return namedParameterJdbcTemplate.query(selectInterQuery.toString(), paramMap, new ResultSetExtractor<Map<String,Integer>>() {
				@Override
				public Map<String,Integer> extractData(ResultSet ors) throws SQLException{
					Map<String,Integer> idMap = new HashMap<>();
					while (ors.next()) {
						idMap.put(ors.getString("gpi").replace("-", ""),ors.getInt(ROUTED_GENERIC_ITEMID));
					}
					return idMap;
				}
			});
		}
		
		
		/**
		 * API to save or update enabled interactions
		 * @param inputParam
		 * @return StatusMap
		 */
		public boolean saveOrUpdateEnableInteraction(Map<String,Object> inputParam){
			Integer id = 0;
			StringBuilder query=null;
	        try {
	        	query=new StringBuilder();
	        	query.append(" SELECT id FROM ip_drugformulary_interaction_setting  ");
	        	query.append(" WHERE routedGenericItemId=:routedGenericItemId AND deleteflag=0 ");
				
				id =  namedParameterJdbcTemplate.query(query.toString(), inputParam, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet ors) throws SQLException {
					int no=0;
					while (ors.next()) {
						no = ors.getInt("id");
					}
					return no;
				}
				});
	        	
	        	if(id > 0)
	        	{
	        		inputParam.put("id", id);
	        		query.setLength(0);
	        		query.append(" UPDATE ip_drugformulary_interaction_setting SET drugToFood = :drugToFood,drugToAlcohol=:drugToAlcohol,duplicateTherapy=:duplicateTherapy, modifiedby=:modifiedBy,modifiedDate=:modifiedDate");
					query.append(" WHERE id=:id");
		        	namedParameterJdbcTemplate.update(query.toString(), inputParam);
		        	return true;
	        	}
	        	query.setLength(0);
	        	query.append(" INSERT INTO ip_drugformulary_interaction_setting(routedGenericItemId,drugToFood,drugToAlcohol,duplicateTherapy,createdby,createdDate) ");
				query.append(" VALUES(:routedGenericItemId,:drugToFood,:drugToAlcohol,:duplicateTherapy,:createdBy,:createdDate); ");
				Util.insertAndReturnId(namedParameterJdbcTemplate, query.toString(), inputParam, "id");
		        return true;
	        }
	        catch(Exception ex ){
				 EcwLog.AppendExceptionToLog(ex);
				 return false;
			}
		}
		
		
		/**
		 * API to fetch saved enabled interactions
		 * @param inputParam
		 * @return StatusMap
		 */
		public Map<String, Object> fetchEnableInteraction(Map<String,Object> inputParam){
			StringBuilder selectInterQuery = null;
			selectInterQuery = new StringBuilder();
			final int routedGenericItemId = Util.getIntValue(inputParam, ROUTED_GENERIC_ITEMID);
			selectInterQuery.append("SELECT routedGenericItemId,drugToFood,drugToAlcohol,duplicateTherapy FROM ip_drugformulary_interaction_setting WHERE routedGenericItemId = :routedGenericItemId AND deleteFlag=0");
			return namedParameterJdbcTemplate.query(selectInterQuery.toString(), inputParam, new ResultSetExtractor<Map<String, Object>>() {
				@Override
				public Map<String, Object> extractData(ResultSet ors) throws SQLException {

					Map<String, Object> interactionSetting = new HashMap<String, Object>();
					interactionSetting.put(DRUG_TO_FOOD,  1);
					interactionSetting.put(DRUG_TO_ALCOHOL, 1);
					interactionSetting.put(DUPLICATE_THERAPY, 1);
					interactionSetting.put(ROUTED_GENERIC_ITEMID, routedGenericItemId);
					while (ors.next()) {
						interactionSetting = new HashMap<String, Object>();
						interactionSetting.put(DRUG_TO_FOOD,  ors.getInt(DRUG_TO_FOOD));
						interactionSetting.put(DRUG_TO_ALCOHOL, ors.getInt(DRUG_TO_ALCOHOL));
						interactionSetting.put(DUPLICATE_THERAPY, ors.getInt(DUPLICATE_THERAPY));
						interactionSetting.put(ROUTED_GENERIC_ITEMID, ors.getInt(ROUTED_GENERIC_ITEMID));
					}
					return interactionSetting;
				}
			});
		}
		
		/**
		 * get AHFS Classification By ItemId And Strength
		 * @param nItemId
		 * @param strength
		 * @return
		 */
		public String getAHFSClassificationByItemIdAndStrength(final int nItemId, final String strength) 
		{
			StringBuilder strSql  = new StringBuilder();
			strSql.append("select MAX(ahfsClassification) as ahfsClassification from ip_drugformulary df where df.itemId = :itemID and df.delflag=0 and df.strength = :strength AND isactive = 1");
			Map<String, Object> mapParam = new HashMap<>(NUM_2);
			mapParam.put(ITEMID1, nItemId);
			mapParam.put(STRENGTH, strength);
			return namedParameterJdbcTemplate.queryForObject(strSql.toString(), mapParam, String.class);
		}
		
		/**
		 * get AHFS Classification By ItemId
		 * @param nItemId
		 * @return
		 */
		public String getAHFSClassificationByItemId(final int nItemId) 
		{
			StringBuilder strSql  = new StringBuilder();
			strSql.append("select MAX(ahfsClassification) as ahfsClassification from ip_drugformulary df where df.itemId = :itemID and df.delflag=0 AND isactive = 1");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put(ITEMID1, nItemId);
			return namedParameterJdbcTemplate.queryForObject(strSql.toString(), mapParam, String.class);
		}
		
		/**
		 * get AHFS Classification By FormularyId
		 * @param nFormularyId
		 * @return
		 */
		public String getAHFSClassificationByFormularyId(final int nFormularyId) 
		{
			StringBuilder strSql  = new StringBuilder();
			strSql.append("select MAX(ahfsClassification) as ahfsClassification from ip_drugformulary df where df.id = :formularyId and df.delflag=0 AND isactive = 1");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put(FORMULARYID, nFormularyId);
			return namedParameterJdbcTemplate.queryForObject(strSql.toString(), mapParam, String.class);
		}
		
		/**
		 * Get Selected Routes By FormularyId
		 * @author Sandip Dalsaniya
		 * @param formularyId : formulary id of medication
		 * @return list of {@link TemplateForOTRoutes}
		 */
		public List<TemplateForOTRoutes> getActiveRoutes(final int formularyId,final int orderTypeId){
			StringBuilder strQuery = new StringBuilder();
			strQuery.append(QueryBuilderUtils.sqlSelect)
			.append("ots.id,ots.routeid,route.routecode,ots.ordertypeid,ots.isRecommended,route.routeDesc")
			.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY_OTS_ROUTE.table()).append(" ots")
			.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUG_FORMULARY_ROUTES.table()).append(" route")
			.append(QueryBuilderUtils.sqlOn).append("ots.routeid = route.id")
			.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUGFORMULARY_ORDERTYPESETUP.table()).append(" os")
			.append(QueryBuilderUtils.sqlOn).append("ots.ordertypeid = os.id")
			.append(QueryBuilderUtils.sqlWhere).append("ots.delflag=0").append(QueryBuilderUtils.sqlAnd).append("route.deleteflag=0")
			.append(QueryBuilderUtils.sqlAnd).append("ots.formularyid=:").append(FORMULARYID);
			Map<String, Object> paramMap = new HashMap<>(1);
			paramMap.put(FORMULARYID,formularyId);
			if(orderTypeId!=0){
				strQuery.append(QueryBuilderUtils.sqlAnd).append("os.ordertype=:").append(ORDER_TYPE);
				paramMap.put(ORDER_TYPE,orderTypeId);
			}
        	return namedParameterJdbcTemplate.query(strQuery.toString(), paramMap, new RowMapper<TemplateForOTRoutes>(){
				@Override
				public TemplateForOTRoutes mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForOTRoutes tmpl=new TemplateForOTRoutes();
					tmpl.setOrderTypeRouteid(rs.getString(ROUTE_ID));
					tmpl.setOrderTypeRouteCode(rs.getString(ROUTE_CODE));
					tmpl.setOrderTypeRouteDescr(rs.getString("routeDesc"));
					return tmpl;
				}
            });
		}
		
		/**
		 * Get Selected Frequencies By FormularyId
		 * @author Sandip Dalsaniya
		 * @param formularyId : formulary id of medication
		 * @return list of {@link TemplateForOTFrequency}
		 */
		public List<TemplateForOTFrequency> getActiveFrequencies(final int formularyId,final int orderTypeId){
			StringBuilder strQuery = new StringBuilder();
			strQuery.append(QueryBuilderUtils.sqlSelect)
			.append("ots.id,ots.freqid,freq.freqCode,ots.ordertypeid,ots.isRecommended,freq.freqDesc,freq.scheduleType")
			.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY_OTS_FREQUENCY.table()).append(" ots")
			.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUG_FORMULARY_FREQUENCY.table()).append(" freq")
			.append(QueryBuilderUtils.sqlOn).append("ots.freqid = freq.id")
			.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUGFORMULARY_ORDERTYPESETUP.table()).append(" os")
			.append(QueryBuilderUtils.sqlOn).append("ots.ordertypeid = os.id")
			.append(QueryBuilderUtils.sqlWhere).append("ots.delflag=0").append(QueryBuilderUtils.sqlAnd).append("freq.deleteflag=0")
			.append(QueryBuilderUtils.sqlAnd).append("ots.formularyid=:").append(FORMULARYID);
			Map<String, Object> paramMap = new HashMap<>(1);
			paramMap.put(FORMULARYID,formularyId);
			if(orderTypeId!=0){
				strQuery.append(QueryBuilderUtils.sqlAnd).append("os.ordertype=:").append(ORDER_TYPE);	
				paramMap.put(ORDER_TYPE,orderTypeId);
			}
        	return namedParameterJdbcTemplate.query(strQuery.toString(), paramMap, new RowMapper<TemplateForOTFrequency>(){
				@Override
				public TemplateForOTFrequency mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForOTFrequency tmpl=new TemplateForOTFrequency();
					tmpl.setOrderTypeFrequencyid(rs.getString(FREQID));
					tmpl.setOrderTypeFrequencyCode(rs.getString(FREQCODE));
					tmpl.setOrderTypeFrequencyDescr(rs.getString(FREQDESC));
					tmpl.setScheduleType(rs.getString("scheduletype"));
					return tmpl;
				}
            });
		}
		public void setPRNIndicationData(int nRoutedGenericItemId,String selectedPRNIds,int nTrUserId)
		{
			String[] prnIdArr = selectedPRNIds.split(",");
			deletePRNIndicationsData(nRoutedGenericItemId);
			if(prnIdArr.length > 0)
			{
				for(String ids:prnIdArr)
				{
					if(!"".equals(ids))
					{
						int nPRNId = Integer.parseInt(ids);
						if(nPRNId > 0)
						{
							insertUpdatePRNIndicationData(nRoutedGenericItemId,nPRNId,nTrUserId);
						}
					}
				}
			}
		}
		
		//delete OTS routes
		public void deletePRNIndicationsData(int nRoutedGenericItemId){
        	MapSqlParameterSource paramMapFac = new MapSqlParameterSource();
        	String sql="delete from ip_drugformulary_selected_prn_data where routedgenericitemid=:routedGenericItemId and delflag=0 ";
        	paramMapFac.addValue(ROUTED_GENERIC_ITEMID,nRoutedGenericItemId);
        	namedParameterJdbcTemplate.update(sql, paramMapFac);
		}
		
		//insert update prn indication data
		private int insertUpdatePRNIndicationData(int nRoutedGenericItemId,int prnIndicationId,int nTrUserId){
			Map<String,Object> paramMap = new HashMap<>();
			int newPRNId = 0;
        	if(nRoutedGenericItemId > 0 && prnIndicationId > 0)
        	{
	        	boolean isInsertRecord = true;
	        	
	        	String strSql = "select id from ip_drugformulary_selected_prn_data where routedgenericitemid=:routedGenericItemId and prn_indication_id=:prnIndicationId and delflag=0 ";
	        	paramMap.put(ROUTED_GENERIC_ITEMID,nRoutedGenericItemId);
	        	paramMap.put("prnIndicationId",prnIndicationId);
	        	paramMap.put(CREATED_BY,String.valueOf(nTrUserId));
	        	paramMap.put(CREATED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        	paramMap.put(MODIFIED_BY,String.valueOf(nTrUserId));
	        	paramMap.put(MODIFIED_ON,IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT) );
	        	paramMap.put(USERID,String.valueOf(nTrUserId));
	        	
	        	List<TemplatePRNIndication> tmplList = namedParameterJdbcTemplate.query(strSql, paramMap, new RowMapper<TemplatePRNIndication>() {
					@Override
					public TemplatePRNIndication mapRow(ResultSet rs, int arg1) throws SQLException {
						TemplatePRNIndication tmpl=new TemplatePRNIndication();
						tmpl.setId(rs.getInt("id")); 
						return tmpl;
					}
	        	});
	        	if(!tmplList.isEmpty())
	        	{
	        		isInsertRecord = false;
	        	}
	        	
	        	
        		StringBuilder strSqlB =new StringBuilder(" insert into ip_drugformulary_selected_prn_data(routedgenericitemid,prn_indication_id,createdby,createdon,modifiedby,modifiedon,userId) ");
        		strSqlB.append("values(:routedGenericItemId,:prnIndicationId,:createdby,:createdon,:modifiedby,:modifiedon,:userId)"); 
	        	
        		if(!isInsertRecord)
	        	{
        			strSqlB.setLength(0);
        			strSqlB.append(" update ip_drugformulary_selected_prn_data set routedgenericitemid=:routedGenericItemId,prn_indication_id=:prnIndicationId,modifiedby=:modifiedby,");
        			strSqlB.append(" modifiedon=:modifiedon,userId =:userId where routedgenericitemid=:routedGenericItemId and prn_indication_id=:prnIndicationId and delflag=0 "); 
        			namedParameterJdbcTemplate.update(strSql, paramMap);
	        	}
        		else
        		{
        			newPRNId = Util.insertAndReturnId(namedParameterJdbcTemplate, strSqlB.toString(), paramMap, "id");
        		}
        	}
	        return newPRNId;
		}
		/**
		 * @param nRoutedGenericItemId
		 * getPRNIndicationData this method will return prn indication data as routed generic itemid
		 * @return
		 * 
		 */
		public List<TemplatePNRIndication> getPRNIndicationData(int nRoutedGenericItemId)
		{
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put(N_ROUTED_GENERIC_ITEM_ID,nRoutedGenericItemId);
			StringBuilder sqlB = new StringBuilder("select idpd.prn_indication_id,idp.prnName from ip_drugformulary_selected_prn_data idpd inner join ip_drugformulary_prnindication idp ");
        	sqlB.append(" on idpd.prn_indication_id = idp.id where idpd.delflag =0 and idp.deleteFlag= 0 ");
        	sqlB.append(" and idpd.routedgenericitemid=:nRoutedGenericItemId and idp.prnstatus=1 ");
        	
        	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplatePNRIndication>() {
				@Override
				public TemplatePNRIndication mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplatePNRIndication tmpl=new TemplatePNRIndication();
					tmpl.setId(rs.getInt("prn_indication_id"));
					tmpl.setPrnName(rs.getString("prnName"));
					return tmpl;
				} 
	    	});
		}
		/**
		 * Get formulation based on formulary Id
		 * @param formularyId
		 * @return
		 */
		public String getFormulationExternalByFormularyId(int formularyId){
			Map<String,Object> paramMap = new HashMap<>();			
	        String sFormulation = null;  
	        List<String> tmplList = null;
        	StringBuilder strSql = new StringBuilder("SELECT ");
        	strSql.append(" ext_mapping_desc ");
        	strSql.append(" FROM ");
        	strSql.append(" ip_drugformulary_formulation_external ");
        	strSql.append(" WHERE ");
        	strSql.append(" formularyId =:"+N_FORMULARY_ID+" AND ");	        	
        	strSql.append(" delflag = 0 ");        	
        	paramMap.put(N_FORMULARY_ID,formularyId);	        
        	tmplList = namedParameterJdbcTemplate.query(strSql.toString(), paramMap, new RowMapper<String>(){
				@Override
				public String mapRow(ResultSet rs, int arg1) throws SQLException {
					return rs.getString(EXT_MAPPING_DESC);
				}
            });
        	if(!tmplList.isEmpty()) {
        		sFormulation = tmplList.get(0);
        	} else {
        		sFormulation = "";
        	}	       
	        return sFormulation;		
		}
		
		/**
		 * get Drug from formulary based on itemId , strength and facilityId
		 * @param ipitemid
		 * @param strength
		 * @param facilityId
		 * @return
		 */
		public List<Map<String, Object>> getDrugFormularyByItemIdAndStrength(final int itemId, final String strength, final int facilityId) 
		{
			StringBuilder strQuery 	= new StringBuilder();
			strQuery.append(QueryBuilderUtils.sqlSelect)
					.append("df.id,i.itemID,i.drugnameid,i.itemName,i.itemDesc, df.strength as strength, df.strengthuom as strengthUom, df.doseSize,")
					.append("df.doseUom ,df.formulation,df.ndc,df.upc,df.ddid,df.isTPN,df.ismedication,df.isIV,i.parentID,")
					.append("df.routedgenericitemid,df.ingredients_count,df.ingredient1_uom,df.ingredient1_value,df.ingredient2_uom,df.ingredient2_value,")
					.append("df.ingredient3_uom,df.ingredient3_value,df.ingredient4_uom,df.ingredient4_value,df.doseUom,df.doseSize, df.dispenseSize , df.dispenseSizeUom, df.isCalculate")
					.append(QueryBuilderUtils.sqlFrom).append(FORMULARY_TABLE.DRUG_FORMULARY.table()).append(" ").append("df")
					.append(QueryBuilderUtils.sqlInnerJoin).append(CPOEEnum.IP_TABLE.ITEMS.table()).append(" ").append("i")
					.append(QueryBuilderUtils.sqlOn).append("df.itemid = i.itemID")
					.append(QueryBuilderUtils.sqlAnd).append("i.deleteFlag = 0")
					.append(QueryBuilderUtils.sqlInnerJoin).append(FORMULARY_TABLE.DRUG_FORMULARY_FACILITIES.table()).append(" ").append("idff")
					.append(QueryBuilderUtils.sqlOn).append("df.id = idff.formularyid")
					.append(QueryBuilderUtils.sqlAnd).append("idff.facilityid = :facilityId")
					.append(QueryBuilderUtils.sqlAnd).append("idff.delflag = 0")
					.append(QueryBuilderUtils.sqlWhere).append("df.itemid = :itemId")
					.append(QueryBuilderUtils.sqlAnd).append("df.strength = :strength")
					.append(QueryBuilderUtils.sqlAnd).append("df.delflag = 0")
					.append(QueryBuilderUtils.sqlAnd).append("df.isActive = 1");
			Map<String, Object> mapParam = new HashMap<String, Object>(NUM_2);
			mapParam.put(ITEM_ID, itemId);
			mapParam.put(FACILITYID, facilityId);
			mapParam.put(STRENGTH, strength);
			return namedParameterJdbcTemplate.queryForList(strQuery.toString(), mapParam);
		}
		
		/**
		 * This method is using to check whether record/data is found in table or not.
		 * @param tableName
		 * @param columnName
		 * @param deleteColumnName
		 * @param targetColumnValue
		 * @return
		 */
		public boolean queryToCheckResult(String tableName,String columnName,String deleteColumnName,String targetColumnValue){
			Map<String,Object> paramMap = new HashMap<>();
			StringBuilder sqlB = new StringBuilder();
			boolean isDataFound = false;
        	if(!"".equals(tableName) && !"".equals(columnName) && !"".equals(targetColumnValue))
        	{
	        	sqlB.append("select count(1)as cnt from ").append(tableName).append(" where ")
	        	.append(columnName).append("=:").append(columnName);
	        	
	        	if(!"".equals(deleteColumnName))
		        {
		        	sqlB.append(" and ").append(deleteColumnName).append("=0");
		        }
	        	paramMap.put(columnName,targetColumnValue);
	        	
        		List<Template> tmplList = namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<Template>() {
					@Override
					public Template mapRow(ResultSet rs, int arg1) throws SQLException {
						Template tmpl = null;
						if(rs.getInt("cnt") > 0)
						{
							tmpl = new Template();
							tmpl.setId(rs.getInt("cnt"));
						}
						return tmpl;
					}
	        	});
	        	if(tmplList!=null && !tmplList.isEmpty() && tmplList.get(0)!=null)
	        	{
	        		isDataFound = true;
	        	}
        	}
	        return isDataFound;
		}
		
		/**
		 * This method is used to check whether formulary is active or not.
		 * @param nFormularyId
		 * @return
		 */
		public List<Integer> isFormularyActive(final int nFormularyId) 
		{
			StringBuilder strSql 	= new StringBuilder();
			strSql.append(" select count(1) as cnt from ip_drugformulary where id=:nFormularyId and isactive = 1 and delflag=0 ");
			Map<String, Object> mapParam = new HashMap<String, Object>(1);
			mapParam.put(N_FORMULARY_ID,nFormularyId);
			return namedParameterJdbcTemplate.query(strSql.toString(), mapParam, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
					return rs.getInt("cnt");
				}
			});
		}
		
		/**
		 * This method is used to check duplicate alias name.
		 * @param drugAlias
		 * @param routedGenericItemId
		 * @return
		 */
		public String checkForDuplicateDrugAlias(final String drugAlias,final String routedGenericItemId ) 
		{
			StringBuilder strSql 	= new StringBuilder();
			strSql.append(" select count(1) as cnt from ip_drugformulary_common_settings where drugAlias=:drugAlias and routedgenericitemid!=:routedgenericitemid and delflag=0 ");
			Map<String, Object> mapParam = new HashMap<>(1);
			mapParam.put(DRUG_ALIAS,drugAlias);
			mapParam.put(ROUTED_GENERIC_ITEM_ID,routedGenericItemId);
			List<Integer> tmplList = null;
			tmplList = namedParameterJdbcTemplate.query(strSql.toString(), mapParam, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
					return NumberUtils.toInt(rs.getString("cnt"), 0);
				}
			});
			if(Integer.valueOf(tmplList.get(0).toString()) > 0)
			{
				return "true";
			}
			else
			{
				return "false";
			}
		}
		public Map<String, Object> getDrugIntructionFromFormulary (int routedGenericID){
			StringBuilder sbDataQuery = new StringBuilder();
			sbDataQuery.append("SELECT orderentry_instr, emar_instr, instructions from ip_drugformulary_notes where routedgenericitemid = :routedgenericitemid and delflag = 0");
			Map<String, Object> mpInputData = new HashMap<>();
			mpInputData.put(ROUTED_GENERIC_ITEM_ID, routedGenericID);
			try {
				return namedParameterJdbcTemplate.queryForMap(sbDataQuery.toString(), mpInputData);
			} catch (IncorrectResultSizeDataAccessException irsd) {
				// if the query does not return exactly one row
				return Collections.emptyMap();
			}
		}
		
		public String getRoutedGenericItemName(String strItemId)
		{
			String strItemName = "";
			Map<String,Object> paramMap = new HashMap<>();
			StringBuilder strQ = new StringBuilder();
			strQ.append("select itemname from ip_items where itemid=:itemid and deleteflag=0 ");
    		paramMap.put(ITEMID, strItemId);
    		List<Template> tmplList = namedParameterJdbcTemplate.query(strQ.toString(), paramMap, new RowMapper<Template>(){
				@Override
				public Template mapRow(ResultSet rs, int arg1) throws SQLException {
					Template tmpl=new Template();
					tmpl.setItemName(rs.getString(ITEMNAME));
					return tmpl;
				}
    		});
    		Iterator<Template> it = tmplList.iterator();
    		if(it.hasNext())
    		{
    			Template t1 = it.next();
    			strItemName = t1.getItemName();
    		}
    		return strItemName;
		}
		
		//get Associated Product details based on GPI
		public List<TemplateForAssoProducts> getAssociatedProductDetailsByGPI(final String strGPI){
			Map<String,Object> paramMap = new HashMap<>();
	        paramMap.put("gpi", strGPI);
	        StringBuilder sqlB = new StringBuilder();
			sqlB.append("select ida.id,ida.formularyid,ida.ndc,ida.ppid,ida.packsize,ida.packsizeunitcode,ida.packQuantity,ida.packType, ");
        	sqlB.append(" ida.awp,ida.manufacturerName,ida.manufacturerIdentifier,ida.mvxCode,ida.cost_to_proc,ida.unitCost,");
        	sqlB.append(" ida.status,ida.isPrimary,ida.marketEndDate,ida.createdby,ida.createdon,ida.modifiedby,ida.modifiedon,ida.delflag,");
        	sqlB.append("ida.userId,ida.notes,ida.itemId,i.itemName as drugName,ida.routedDrugId,ida.rxnorm,ida.upc,ida.awup,ida.ndc10 ");
        	sqlB.append(" from ip_drugformulary_associatedProducts ida inner join ip_items i on ida.itemid = i.itemid ");
        	sqlB.append(" inner join ip_drugformulary drug on drug.id = ida.formularyid and drug.isActive=1 and drug.delflag=0 ");
        	sqlB.append(" where drug.gpi=:gpi and ida.status=1 and ida.delflag=0 and i.deleteflag=0");
        	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForAssoProducts>() {
				@Override
				public TemplateForAssoProducts mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProducts tmpl=new TemplateForAssoProducts();
					tmpl.setId(rs.getInt("id"));
					tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
					tmpl.setNdc(rs.getString("ndc"));
					tmpl.setPpid(rs.getString("ppid"));
					tmpl.setPackSize(rs.getString(PACK_SIZE));
					tmpl.setPackSizeUnitCode(rs.getString(PACKSIZE_UNIT));
					tmpl.setPackQuantity(rs.getString(PACK_QUANTITY)); 
					tmpl.setPackType(rs.getString(PACK_TYPE));
					tmpl.setAwp(rs.getString("awp"));
					tmpl.setAwup(rs.getString("awup"));
					tmpl.setNdc10(rs.getString(NDC10));
					tmpl.setManufacturerName(rs.getString(MANUFACTURE_NAME));
					tmpl.setManufacturerIdentifier(rs.getString(MANUFACTURE_IDENTIFIER));
					tmpl.setMvxCode(rs.getString(MVX_CODE));
					tmpl.setCost_to_proc(rs.getDouble(COST_TO_PROC));
					tmpl.setUnitcost(rs.getDouble(UNIT_COST));
					tmpl.setStatus(rs.getInt(STATUS));
					tmpl.setIsprimary(rs.getInt("isPrimary"));
					tmpl.setMarketEndDate(rs.getString(MARKET_END_DATE));
					tmpl.setUserId(rs.getInt(USERID));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setItemId(rs.getString(ITEM_ID));
					tmpl.setDrugName(rs.getString(DRUG_NAME));
					tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
					tmpl.setRxnorm(rs.getString(RX_NORM));
					tmpl.setUpc(rs.getString("upc"));
					try
					{
						tmpl.setCreatedby(rs.getInt(CREATED_BY));
						tmpl.setModifiedby(rs.getInt(MODIFIED_BY));
	         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
	         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
	         			
						tmplAssoProdLotList = getAssoProductLotDetails(rs.getInt(FORMULARY_ID), rs.getInt("id"));
					}
					catch (RuntimeException | ParseException e) {
			            EcwLog.AppendExceptionToLog(e);
			        }
					final String lotDetails = new com.google.gson.Gson().toJson(tmplAssoProdLotList);
					tmpl.setLotDetails(lotDetails);
					return tmpl;
				} 
        	});
		}
		
		//get Associated Product details based on routed drug id
		public List<TemplateForAssoProducts> getAssociatedProductDetailsByRoutedDrugId(final String strRoutedDrugId){
			Map<String,Object> paramMap = new HashMap<>();
	        paramMap.put("routedDrugId", strRoutedDrugId);
	        StringBuilder sqlB = new StringBuilder();
			sqlB.append("select ida.id,ida.formularyid,ida.ndc,ida.ppid,ida.packsize,ida.packsizeunitcode,ida.packQuantity,ida.packType, ");
        	sqlB.append(" ida.awp,ida.manufacturerName,ida.manufacturerIdentifier,ida.mvxCode,ida.cost_to_proc,ida.unitCost,");
        	sqlB.append(" ida.status,ida.isPrimary,ida.marketEndDate,ida.createdby,ida.createdon,ida.modifiedby,ida.modifiedon,ida.delflag,");
        	sqlB.append("ida.userId,ida.notes,ida.itemId,i.itemName as drugName,ida.routedDrugId,ida.rxnorm,ida.upc,ida.awup,ida.ndc10 ");
        	sqlB.append(" from ip_drugformulary_associatedProducts ida inner join ip_items i on ida.itemid = i.itemid ");
        	sqlB.append(" inner join ip_drugformulary drug on drug.id = ida.formularyid and drug.isActive=1 and drug.delflag=0 ");
        	sqlB.append(" where ida.routedDrugId=:routedDrugId and ida.status=1 and ida.delflag=0 and i.deleteflag=0");
        	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForAssoProducts>() {
				@Override
				public TemplateForAssoProducts mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForAssoProducts tmpl=new TemplateForAssoProducts();
					tmpl.setId(rs.getInt("id"));
					tmpl.setFormularyid(rs.getInt(FORMULARY_ID));
					tmpl.setNdc(rs.getString("ndc"));
					tmpl.setPpid(rs.getString("ppid"));
					tmpl.setPackSize(rs.getString(PACK_SIZE));
					tmpl.setPackSizeUnitCode(rs.getString(PACKSIZE_UNIT));
					tmpl.setPackQuantity(rs.getString(PACK_QUANTITY)); 
					tmpl.setPackType(rs.getString(PACK_TYPE));
					tmpl.setAwp(rs.getString("awp"));
					tmpl.setAwup(rs.getString("awup"));
					tmpl.setNdc10(rs.getString(NDC10));
					tmpl.setManufacturerName(rs.getString(MANUFACTURE_NAME));
					tmpl.setManufacturerIdentifier(rs.getString(MANUFACTURE_IDENTIFIER));
					tmpl.setMvxCode(rs.getString(MVX_CODE));
					tmpl.setCost_to_proc(rs.getDouble(COST_TO_PROC));
					tmpl.setUnitcost(rs.getDouble(UNIT_COST));
					tmpl.setStatus(rs.getInt(STATUS));
					tmpl.setIsprimary(rs.getInt("isPrimary"));
					tmpl.setMarketEndDate(rs.getString(MARKET_END_DATE));
					tmpl.setUserId(rs.getInt(USERID));
					tmpl.setNotes(rs.getString(NOTES));
					tmpl.setItemId(rs.getString(ITEM_ID));
					tmpl.setDrugName(rs.getString(DRUG_NAME));
					tmpl.setRoutedDrugId(rs.getString(ROUTED_DRUG_ID));
					tmpl.setRxnorm(rs.getString(RX_NORM));
					tmpl.setUpc(rs.getString("upc"));
					try
					{
						tmpl.setCreatedby(rs.getInt(CREATED_BY));
						tmpl.setModifiedby(rs.getInt(MODIFIED_BY));
	         			tmpl.setCreatedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(CREATED_ON),rs.getInt(CREATED_BY)));
	         			tmpl.setModifiedon(IPTzUtils.formatDBDateInUserTZ(rs.getString(MODIFIED_ON),rs.getInt(MODIFIED_BY)));
	         			
						tmplAssoProdLotList = getAssoProductLotDetails(rs.getInt(FORMULARY_ID), rs.getInt("id")); 
					}
					catch (RuntimeException | ParseException e) {
			            EcwLog.AppendExceptionToLog(e);
			        }
					final String lotDetails = new com.google.gson.Gson().toJson(tmplAssoProdLotList);
					tmpl.setLotDetails(lotDetails);
					return tmpl;
				} 
        	});
		}
		/**
		 * This method is used to fetch billing information based on cptcodeitemid
		 * @param drugAlias
		 * @return
		 */
		public List<TemplateForBillingInfo> getBillingInfo(final int cptcodeitemid) 
		{
			Map<String,Object> paramMap = new HashMap<>();
	        paramMap.put("cptcodeitemid", cptcodeitemid);
	        StringBuilder sqlB = new StringBuilder();
			sqlB.append(" select hcpcscoderange, hcpcscodetype, hcpcscodeunit from edi_cptcodes where itemid=:cptcodeitemid ");
        	return namedParameterJdbcTemplate.query(sqlB.toString(), paramMap, new RowMapper<TemplateForBillingInfo>() {
				@Override
				public TemplateForBillingInfo mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForBillingInfo tmpl=new TemplateForBillingInfo();
					tmpl.setHcpcscoderange(rs.getString("hcpcscoderange"));
					tmpl.setHcpcscodetype(rs.getString("hcpcscodetype"));
					tmpl.setHcpcscodeunit(rs.getInt("hcpcscodeunit"));
					return tmpl;
				} 
        	});
		}
		/*
		 * This method is used to return formulary drug title with specific format
		 * @param drugId
		 * @return
		 * */
		public String getFormularyDrugTitle(String drugId)
		{
			StringBuilder strTitle = new StringBuilder();
			Map<String,Object> paramMap = new HashMap<>();
			StringBuilder sql = new StringBuilder();
			sql.append(" select drug.strength,drug.strengthuom,drug.formulation,drug.isDisplayPkgSize,drug.isDisplayPkgType,");
	   	    sql.append(" drug.packsize,drug.packsizeunitcode,drug.packType,drug.routeName,drug.genericDrugName ");
	   		sql.append(" from ip_drugformulary drug ");
	   		sql.append(" where drug.id = :formularyid and drug.delflag=0 ");
    		paramMap.put("formularyid", drugId);
    		List<TemplateForDrug> tmplList = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<TemplateForDrug>(){
				@Override
				public TemplateForDrug mapRow(ResultSet rs, int arg1) throws SQLException {
					TemplateForDrug tmpl=new TemplateForDrug();
					tmpl.setStrength(rs.getString("strength"));
					tmpl.setStrengthuom(rs.getString("strengthuom"));
					tmpl.setFormulation(rs.getString("formulation"));
					tmpl.setIsDisplayPkgSize(rs.getString("isDisplayPkgSize"));
					tmpl.setIsDisplayPkgType(rs.getString("isDisplayPkgType"));
					tmpl.setPacksize(rs.getString("packsize"));
					tmpl.setPacksizeunitcode(rs.getString("packsizeunitcode"));
					tmpl.setPackType(rs.getString("packType"));
					tmpl.setRouteName(rs.getString("routeName"));
					tmpl.setGenericDrugName(rs.getString("genericDrugName"));
					return tmpl;
				}
    		});
    		Iterator<TemplateForDrug> it = tmplList.iterator();
    		if(it.hasNext())
    		{ 
    			TemplateForDrug t1 = it.next();
    			strTitle.append(t1.getGenericDrugName());
    			
    			if(!"".equals(Util.trimStr(t1.getStrength())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getStrength()));
				}
    			if(!"".equals(Util.trimStr(t1.getStrengthuom())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getStrengthuom()));
				}
    			if(!"".equals(Util.trimStr(t1.getFormulation())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getFormulation()));
				}
    			if("1".equals(Util.trimStr(t1.getIsDisplayPkgSize())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getPacksize()));
    				
    				if(!"".equals(Util.trimStr(t1.getPacksizeunitcode())))
    				{
        				strTitle.append(" "+Util.trimStr(t1.getPacksizeunitcode()));
    				}
				}
    			if("1".equals(Util.trimStr(t1.getIsDisplayPkgType())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getPackType()));
				}
    			if(!"".equals(Util.trimStr(t1.getRouteName())))
				{
    				strTitle.append(" "+Util.trimStr(t1.getRouteName()));
				}
    		}
			return strTitle.toString();
		}
}