package inpatientWeb.pharmacy.serviceImpl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.UnprocessableEntityException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants;
import inpatientWeb.Global.medicationhelper.util.RxUtil;
import inpatientWeb.Global.progressnotes.dao.DiagnosisDao;
import inpatientWeb.Global.progressnotes.service.IPVitalsService;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.admin.pharmacySettings.generalSettings.model.PharmacySettingOrderSetting;
import inpatientWeb.admin.pharmacySettings.generalSettings.service.PharmacySettingsService;
import inpatientWeb.lis.labresult.LabResultService;
import inpatientWeb.pharmacy.beans.PatientProfile;
import inpatientWeb.pharmacy.beans.RenalFunction;
import inpatientWeb.pharmacy.dao.PatientProfileDAO;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants;
import inpatientWeb.pharmacy.interaction.dao.InteractionDao;
import inpatientWeb.pharmacy.service.AllergyService;
import inpatientWeb.pharmacy.service.PatientProfileService;
import inpatientWeb.registration.appointment.service.AppointmentService;
import inpatientWeb.utils.Age;
import inpatientWeb.utils.AgeCalculator;
import inpatientWeb.utils.EncounterHelper;
import inpatientWeb.utils.IPConstants;
import inpatientWeb.utils.PatientHelper;
import inpatientWeb.utils.Util;

/**
 * @author Dishagna Bhavsar
 * The Class PatientProfileServiceImpl.
 */
@Service
@Scope("prototype")
@Lazy
public class PatientProfileServiceImpl implements PatientProfileService {
	
	@Autowired
	AllergyService allergyService;
	
	@Autowired
	EncounterHelper	encounterHelper;
	
	@Autowired
	DiagnosisDao diagnosisDao;
	
	@Autowired
	PatientProfileDAO patientProfileDao;
	
	@Autowired
	PharmacySettingsService pharmacySettingService;
	

	@Autowired
	private InteractionDao interactionDao;
	
	
	private DecimalFormat defaultDecimalFormatter = RxUtil.getDefaultDecimalFormatter();
	
	private static final int YEAR_MUL_FACTOR = 12;
	private static final int MONTH_MUL_FACTOR = 30;
	private static final double HT_CM_MUL_FACTOR = 2.54;
	private static final double HT_INCH_MUL_FACTOR = 0.393701;
	private static final double DEFAULT_VAL = 0.0;
	private static final String KEY_ENCOUNTER = "encounter";
	private static final String ALLERGEN_TYPE_DRUG = "drug";
	

	/* (non-Javadoc)
	 * @see inpatientWeb.pharmacy.service.PatientProfileService#getPatientProfile(int)
	 */
	@Override
	public PatientProfile getPatientProfile(long episodeEncId, long moduleEncId){
		PatientProfile patientProfile = new PatientProfile();
		try {
			if(episodeEncId <= 0) {
				return patientProfile;
			}
			
			patientProfile.setAllergyList(allergyService.getAllergensForInteraction(KEY_ENCOUNTER, moduleEncId, ALLERGEN_TYPE_DRUG));
			patientProfile.setPatientProblems(setPatientDiagnosis(episodeEncId));

			//Get patient problems for pregnancy and Lactating
			inpatientWeb.Global.ecw.ambulatory.CwMobile.CwEncounter enc = new inpatientWeb.Global.ecw.ambulatory.CwMobile.CwEncounter();
			String patientID = enc.getPIDForEncounterID(null, String.valueOf(episodeEncId));
			int patientId = Util.parseSafeStringToInt(patientID, false);
			setPatientData(patientProfile, patientId);
			List<RenalFunction> renalFunctionList = new ArrayList<>();
			
			//Get creatinine clearance
			int facilityId = interactionDao.getFacilityIdByEnc(episodeEncId);
			if(facilityId > 0) {
				
				
				LabResultService labResultService =(LabResultService) EcwAppContext.getObject(LabResultService.class);
				Map<String, Object> pharmacySetting = pharmacySettingService.getPharmacyOrderSettings(facilityId);
				PharmacySettingOrderSetting pharmacyOrderSetting = (PharmacySettingOrderSetting) pharmacySetting.get("orderSettings");
				if(pharmacyOrderSetting.getScrLoincId() != null && !pharmacyOrderSetting.getScrLoincId().isEmpty()) {
					Map<String, Object> labResult = labResultService.getLabResultForPharmacy(patientId, pharmacyOrderSetting.getScrLoincId());
					
					RenalFunction renalFunction = new RenalFunction();
					renalFunction.setValue(Util.getDoubleValue(labResult, "Value", 0));
					renalFunction.setUnit(Util.getStrValue(labResult, "Units", ""));
					renalFunction.setType(InteractionConstants.RENAL_TYPES.SERUM_CREATININE.type());
					renalFunctionList.add(renalFunction);
				}
				
				if(pharmacyOrderSetting.getCrclLoincId() != null && !pharmacyOrderSetting.getCrclLoincId().isEmpty() ) {
					Map<String, Object> labResult = labResultService.getLabResultForPharmacy(patientId, pharmacyOrderSetting.getCrclLoincId());
					
					RenalFunction renalFunction = new RenalFunction();
					renalFunction.setValue(Util.getDoubleValue(labResult, "Value", 0));
					renalFunction.setUnit(Util.getStrValue(labResult, "Units", ""));
					renalFunction.setType(InteractionConstants.RENAL_TYPES.CREATININE_CLEARANCE.type());
					renalFunctionList.add(renalFunction);
				}
				patientProfile.setRenalFunctionList(renalFunctionList);
			}
			
			//Set patient age
			Date patientDobInDbForm = PatientHelper.getPatientDOB(patientId);
			Age ageObj = AgeCalculator.calculateAge(patientDobInDbForm);
			patientProfile.setAgeInDays(convertAgeInDays(ageObj));
			
			//Set patient Sex
			patientProfile.setGender(patientProfileDao.getPatientSexFromEncounterId(episodeEncId));
			
			//Set patient height and weight
			Map<String,Object> patientVitals = getFomattedPatientVitals((int) episodeEncId); 
			if(patientVitals == null){
				return patientProfile;
			}
			patientProfile.setWeightInKgs(Util.getDoubleValue(patientVitals, "weightInKg", 0));
			patientProfile.setWeightInLbs(Util.getDoubleValue(patientVitals, "weightInLbs", 0));
			convertHeight(patientProfile , patientVitals);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return patientProfile;
	}
	
	private int convertAgeInDays(Age age){
		int ageInDays = age.getDays();
		ageInDays += age.getMonths() * MONTH_MUL_FACTOR;
		ageInDays += age.getYears() * YEAR_MUL_FACTOR * MONTH_MUL_FACTOR;
		return ageInDays;
	}
	
	private PatientProfile convertHeight(PatientProfile patientProfile,Map<String,Object> vitals){
		String uom = Util.getStrValue(vitals, "heightUnit");
		String strValue = Util.getStrValue(vitals,"height");
		if ("".equals(strValue.trim())) {
			patientProfile.setHeightInCms(DEFAULT_VAL);
			patientProfile.setHeightInInches(DEFAULT_VAL);
		} else if (uom.toLowerCase().startsWith("cm")) {
			patientProfile.setHeightInCms(Double.parseDouble(strValue));
			patientProfile.setHeightInInches(Double.parseDouble(strValue) * HT_INCH_MUL_FACTOR);
		} else {
			patientProfile.setHeightInInches(Double.parseDouble(strValue));
			patientProfile.setHeightInCms(Double.parseDouble(strValue) * HT_CM_MUL_FACTOR);
		}
		return patientProfile;
	}
	
	private PatientProfile setPatientData(PatientProfile patientProfile, int patientId){
		int dsbdEncID =	PatientHelper.getDashBoardEncounterIdForPatient(patientId);
		Map<String,Object> dsbdEncData= encounterHelper.getEncounterData(dsbdEncID);
		String pregnantStr = Util.getStrValue(dsbdEncData, "PregnantFlag");
		String breastFeedStr = Util.getStrValue(dsbdEncData, "BreastFeedingFlag");
		if("Y".equalsIgnoreCase(pregnantStr)){
			patientProfile.setPregnant(true);
		}
		if("Y".equalsIgnoreCase(breastFeedStr)){
			patientProfile.setLactating(true);
		}
		return patientProfile;
	}
	
	private List<Map<String, String>> setPatientDiagnosis(long episodeEncId){
		List<Map<String, Object>> diagnosisList = diagnosisDao.getAssesmentsByEpisodeEncounter(1, (int) episodeEncId);
		diagnosisList.addAll(diagnosisDao.getAssesmentsByEpisodeEncounter(0, (int) episodeEncId));
		List<Map<String, String>> patientDiagnosis = new ArrayList<>();
		Map<String, String> diagnosisMap = null;
		for(Map<String, Object> diagnosis : diagnosisList){
			String icdType = "ICD9";
			String icdCode = "";
			diagnosisMap = new HashMap<>();
			if("10".equals(Util.getStrValue(diagnosis, "codetype", ""))){
				icdType = "ICD10";
			}
			icdCode = Util.getStrValue(diagnosis, "MedicalCode");
			diagnosisMap.put(icdType, icdCode);
			patientDiagnosis.add(diagnosisMap);
		}
		return patientDiagnosis;
	}

	@Override
	public Map<String, Object> getFomattedPatientVitals(int episodeEncId) {
		try {
			int activeEncounterId = ((AppointmentService) (EcwAppContext.getObject(AppointmentService.class))).getActiveEncounterId(episodeEncId);
			if (activeEncounterId < 1) {
				EcwLog.AppendToLog("No active encounter found for given episode encounter : " + episodeEncId);
				throw new UnprocessableEntityException(MedicationConstants.ACTIVE_ENCOUNTER_NOT_FOUND);
			}
			Map<String, Object> formattedVitals = new HashMap<>();
			Map<String, Object> latestPatientVitals = getLatestPatientVitals(activeEncounterId);
			double weightInKg = getPatientWeightInKg(latestPatientVitals);
			formattedVitals.put("weightInKg", defaultDecimalFormatter.format(weightInKg));
			formattedVitals.put("weightInLbs", defaultDecimalFormatter.format(convertWeight(weightInKg, WeightUnit.KG, WeightUnit.LBS)));
			
			formattedVitals.put("height", defaultDecimalFormatter.format(getPatientHeight(latestPatientVitals)));
			formattedVitals.put("heightUnit", latestPatientVitals.get("heightUnit"));
			
			formattedVitals.put("bmi", defaultDecimalFormatter.format(getPatientBMI(latestPatientVitals)));
			formattedVitals.put("bmiUnit", latestPatientVitals.get("bmiUnit"));
			return formattedVitals;
		} catch (UnprocessableEntityException uee) {
			throw uee;
		} catch (RuntimeException re) {
			EcwLog.AppendExceptionToLog(re);
			throw new UnprocessableEntityException(MedicationConstants.PATIENT_VITALS_NOT_FOUND);
		}
	}
	
	/**
	 * Get patient's height, weight (KG), and BMI
	 * 
	 * @param moduleEncId
	 * @return
	 */
	private Map<String, Object> getLatestPatientVitals(int moduleEncId) {
		try {
			Map<String, Object> vitals = new HashMap<>();
			List<Map<String, Object>> latestVitalValues = ((IPVitalsService) EcwAppContext.getObject(IPVitalsService.class)).getLatestUniqueVitalsValues(moduleEncId);
			if (Util.getSize(latestVitalValues) < 1) {
				return vitals;
			}
			for (Map<String, Object> vital : latestVitalValues) {
				if ((IPConstants.SM_VITAL_TYPE_WT.equalsIgnoreCase(Util.getStrValue(vital, "vitalType")))) {
					vitals.put("weight", Util.getStrValue(vital, "value", "0"));
					vitals.put("weightUnit", vital.get("unitName"));
				} else if ((IPConstants.SM_VITAL_TYPE_Ht.equalsIgnoreCase(Util.getStrValue(vital, "vitalType")))) {
					vitals.put("height", Util.getStrValue(vital, "value", "0"));
					vitals.put("heightUnit", vital.get("unitName"));
				} else if ((IPConstants.SM_VITAL_TYPE_BMI.equalsIgnoreCase(Util.getStrValue(vital, "vitalType")))) {
					vitals.put("bmi", Util.getStrValue(vital, "value", "0"));
					vitals.put("bmiUnit", vital.get("unitName"));
				}
			}
			return vitals;
		} catch (UnprocessableEntityException uee) {
			throw uee;
		} catch (RuntimeException re) {
			EcwLog.AppendExceptionToLog(re);
			throw new UnprocessableEntityException(MedicationConstants.PATIENT_VITALS_NOT_FOUND);
		}
	}
		
	private double getPatientWeightInKg(Map<String, Object> latestPatientWeightMap) {
		String weightStr = Util.getStrValue(latestPatientWeightMap, "weight");
		if (Util.isNullEmpty(weightStr)) {
			throw new UnprocessableEntityException(MedicationConstants.PATIENT_WEIGHT_NOT_FOUND); 
		}
		double weight = 0.0;
		try {
			weight = Double.parseDouble(weightStr);
		} catch (NumberFormatException nfe) {
			throw new UnprocessableEntityException(MedicationConstants.INVALID_PATIENT_WEIGHT);
		}
		String weightUnit = Util.getStrValue(latestPatientWeightMap, "weightUnit");
		if ("Kg".equalsIgnoreCase(weightUnit)) {
			return weight;
		} else if ("lbs".equalsIgnoreCase(weightUnit)) {
			return convertWeight(weight, WeightUnit.LBS, WeightUnit.KG);
		} else if ("gm".equalsIgnoreCase(weightUnit)) {
			return convertWeight(weight, WeightUnit.GRAM, WeightUnit.KG);
		}
		throw new UnprocessableEntityException("Invalid weight unit found. Weight Unit : " + weightUnit);
	}
	
	private double getPatientHeight(Map<String, Object> patientVitals) {
		String heightStr = Util.getStrValue(patientVitals, "height");
		if (Util.isNullEmpty(heightStr)) {
			return 0;
		}
		try {
			return Double.parseDouble(heightStr);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	private double getPatientBMI(Map<String, Object> patientVitals) {
		String bmiStr = Util.getStrValue(patientVitals, "bmi");
		if (Util.isNullEmpty(bmiStr)) {
			return 0;
		}
		try {
			return Double.parseDouble(bmiStr);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	private static double convertWeight(double sourceWeight, WeightUnit sourceWeightUnit, WeightUnit destinationWeight) {
		if (sourceWeightUnit.getCompareToKG() > destinationWeight.getCompareToKG()) {
			return (sourceWeight / sourceWeightUnit.getCompareToKG()) * destinationWeight.getCompareToKG();
		}
		return (sourceWeight * destinationWeight.getCompareToKG()) / sourceWeightUnit.getCompareToKG();
	}
	
	
}
