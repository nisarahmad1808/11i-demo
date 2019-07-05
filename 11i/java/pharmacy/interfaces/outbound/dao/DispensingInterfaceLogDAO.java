package inpatientWeb.pharmacy.interfaces.outbound.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.devices.util.DeviceUtils;
import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceLogModel;

@Repository
public class DispensingInterfaceLogDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String RESPONSEEXCEPTION = "ResponseException";
	
	public DispensingInterfaceLogDAO(){
		if(null==namedParameterJdbcTemplate)
			namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) EcwAppContext.getObject("namedParameterJdbcTemplate");
	}
	
	/**
	 * API returns Med Dispensing Interface log
	 * @exception DataAccessException
	 * @exception ClassCastException
	 * @exception IllegalArgumentException
	 * @exception UnsupportedOperationException
	 * @param outboundLogQuery
	 * @param outboundLogCountQuery
	 * @param params
	 * @return Map - Key : MedDispensingLogData , Value :  MedDispensingLogDataList
	 * 			   - Key : TotalCount			, Value :  Total number of records
	 */
	public Map<String, Object> getMedicationDispenseLog(String outboundLogQuery, String outboundLogCountQuery, Map<String, Object> params) {
		Map<String, Object> map = new HashMap<>();
		map.put("MedDispensingLogData", RESPONSEEXCEPTION);
		int recordCount = 0;
		List<DispensingInterfaceLogModel> list = null;
		final DeviceUtils deviceUtils = new DeviceUtils();
		try {
			if(null != outboundLogQuery && !outboundLogQuery.isEmpty()){

				list = namedParameterJdbcTemplate.query(outboundLogQuery, params, new RowMapper<DispensingInterfaceLogModel>() {
							@Override
							public DispensingInterfaceLogModel mapRow(ResultSet rs, int arg1) throws SQLException {
								DispensingInterfaceLogModel dispensingInterface = new DispensingInterfaceLogModel();
								dispensingInterface.setStatus(rs.getString("status"));
								dispensingInterface.setApprovedDate(deviceUtils.convertDateFormatDbToUI(rs.getObject("createddatetime")));
								dispensingInterface.setHl7Message(rs.getString("hl7message"));
								dispensingInterface.setData(rs.getString("data"));
								dispensingInterface.setFacility(rs.getString("Name"));
								
								return dispensingInterface;
							}
						});
				// Fetching total number of records
				recordCount = namedParameterJdbcTemplate.queryForObject(outboundLogCountQuery, params, Integer.class);
			}
			
			map.put("MedDispensingLogData", list);
			map.put("TotalCount", recordCount);
		}catch (DataAccessException | ClassCastException | IllegalArgumentException | UnsupportedOperationException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
		}
		return map;
	}
}
