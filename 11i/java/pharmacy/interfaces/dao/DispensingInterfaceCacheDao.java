package inpatientWeb.pharmacy.interfaces.dao;

import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceModel;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class DispensingInterfaceCacheDao {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DispensingInterfaceCacheDao(){
		if(null==namedParameterJdbcTemplate)
			namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) EcwAppContext.getObject("namedParameterJdbcTemplate");
	}
	
	/**
	 * API will get all the item keys configured for a given medication dispensing interface id
	 * @exception EmptyResultDataAccessException
	 * @param interfaceId
	 * @return Map<String,Object> - Key : Item key name - Value : Item key value
	 */
	public Map<String,Object> getDispensingInterfaceItemKeys(int interfaceId){
		 Map<String,Object> interfaceItemKeyMap = null;
		 StringBuilder strBuilderQuery = new StringBuilder();
		 try{
			 strBuilderQuery.append("SELECT name, value FROM ");
			 strBuilderQuery.append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_ITEMKEYS + " WHERE delflag=0 AND dispensinginterfaceid=:dispensingInterfaceId");    
			 SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("dispensingInterfaceId",interfaceId);
			 interfaceItemKeyMap = namedParameterJdbcTemplate.query(strBuilderQuery.toString(),namedParameter, new ResultSetExtractor<Map<String, Object>>() {
				@Override
				public Map<String, Object> extractData(ResultSet rs)throws SQLException{
					HashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
					while (rs.next()) {
						resultMap.put(rs.getString("name"), rs.getString("value"));
					}
					return resultMap;
				}
			});
		}catch(EmptyResultDataAccessException ex){
			//Empty Result Set
		}
		return interfaceItemKeyMap;
	}
	
	/**
	 * API to get the interface user id
	 * @exception EmptyResultDataAccessException
	 * @return int - User id of interface user 
	 */
	public int getInterfaceUserId(){
		int interfaceUserId=0;
		try{
			Map<String,Object> dataMap = new HashMap<String, Object>();
			StringBuilder query = new StringBuilder();
			query.append("SELECT uid FROM users WHERE uname=:interfaceUserName AND delFlag=0 AND status=0 AND UserType=11");
			dataMap.put("interfaceUserName", "INTERFACE");
			interfaceUserId=namedParameterJdbcTemplate.queryForObject(query.toString(),dataMap,Integer.class);
		}catch(EmptyResultDataAccessException e){
			//Empty Result Set
		}
		return interfaceUserId;
	}
	/**
	 * API will get the interface details for a given medication dispensing interface
	 * @exception EmptyResultDataAccessException
	 * @param enabledDispensingInterface
	 * @return DispensingInterfaceModel - Pojo class to store the interface details
	 */
	public DispensingInterfaceModel getInterfaceDetailObject(String enabledDispensingInterface) {
		StringBuilder query = new StringBuilder();
		DispensingInterfaceModel dispensingInterfaceModel = null;
		try{
			query.append("SELECT id, interfacename, interfacetype, connectionip,connectionport, connectionurl, modifiedtime, modifiedby, mandatoryfield ");
			query.append(" FROM " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACES);
			query.append(" WHERE interfacename =:interfaceName and delflag = 0 and isactive =1");

			SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("interfaceName",enabledDispensingInterface);
			dispensingInterfaceModel = namedParameterJdbcTemplate.query(query.toString(), namedParameter, new ResultSetExtractor<DispensingInterfaceModel>() {
				@Override
				public DispensingInterfaceModel extractData(ResultSet rs) throws SQLException{
					DispensingInterfaceModel dispensingInterfaceModel = DispensingInterfaceModel.getInterfaceDetailInstance();
					if(rs.next()){
						dispensingInterfaceModel.setInterfaceId(rs.getInt("id"));
						dispensingInterfaceModel.setInterfaceName(rs.getString("interfacename"));
						dispensingInterfaceModel.setInterfaceType(rs.getString("interfacetype"));
						dispensingInterfaceModel.setConnectionIp(rs.getString("connectionip"));
						dispensingInterfaceModel.setConnectionPort(rs.getInt("connectionport"));
						dispensingInterfaceModel.setConnectionUrl(rs.getString("connectionurl"));
						dispensingInterfaceModel.setModifiedTime((Date) rs.getObject("modifiedtime"));
						dispensingInterfaceModel.setModifiedBy(rs.getInt("modifiedby"));
						dispensingInterfaceModel.setMandatoryFields(rs.getString("mandatoryfield"));
					}
					return dispensingInterfaceModel;
				}
			});
		}catch (EmptyResultDataAccessException ex) {
			//Empty Result Set
		}
		return dispensingInterfaceModel;
	}
	
	/**
	 * API will get the HL7 message segment details for the given medication dispensing interface id
	 * @exception DataAccessException
	 * @param dispensingInterfaceId
	 * @return Map<String,Object> - Key : Segment name - Value : Segment data 
	 */
	public Map<String, Object> getDispensingHl7SegmentDetail(int dispensingInterfaceId) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> map = null;
		try {
			query.append("SELECT segmentname, segmentdata ");
			query.append("FROM " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_HL7SEGMENTDETAILS);
			query.append(" WHERE dispensinginterfaceid =:dispensingInterfaceId and delflag = 0");
			SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("dispensingInterfaceId",dispensingInterfaceId);
			map = namedParameterJdbcTemplate.query(query.toString(),namedParameter, new ResultSetExtractor<Map<String, Object>>() {
				@Override
				public Map<String, Object> extractData(ResultSet rs) throws SQLException{
					HashMap<String, Object> mapRet = new LinkedHashMap<String, Object>();
					while (rs.next()) {
						mapRet.put(rs.getString("segmentname"), rs.getString("segmentdata"));
					}
					return mapRet;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			map = null;
		}
		return map;
	}
	/**
	 * API will get the message component type details for the given medication dispensing interface id
	 * @exception DataAccessException
	 * @param dispensingInterfaceId
	 * @return Map<String,Object> - Key : Component message name - Value : Segment names mapped
	 */
	public Map<String, Object> getDispensingMessageComponentTypeSegment(int dispensingInterfaceId) {
		Map<String, Object> map = null;
		try{
			StringBuilder query = new StringBuilder();
			query.append("SELECT name, segmentnames ");
			query.append("FROM " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_COMPONENTS);
			query.append(" WHERE dispensinginterfaceid=:dispensinginterfaceid");

			SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("dispensinginterfaceid", dispensingInterfaceId);
			map = namedParameterJdbcTemplate.query(query.toString(),namedParameter, new ResultSetExtractor<Map<String, Object>>() {
			@Override
			public Map<String, Object> extractData(ResultSet rs) throws SQLException {
				Map<String, Object> map = new LinkedHashMap<>();
				while (rs.next()) {
					map.put(rs.getString("name"),rs.getString("segmentnames"));
				}
				return map;
			}
			});
		}catch (EmptyResultDataAccessException ex) {
			map = null;
		}
		return map;
	}
}
