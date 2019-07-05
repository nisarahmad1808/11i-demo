package inpatientWeb.pharmacy.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class OrderPriority implements RowMapper<OrderPriority> {
	private int id;
	private String priorityName;
	private int deleteFlag;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPriorityName() {
		return priorityName;
	}
	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@Override
	public OrderPriority mapRow(ResultSet rs, int arg1) throws SQLException {
		OrderPriority orderPriority = new OrderPriority();
		orderPriority.setId(rs.getInt("priorityid"));
		orderPriority.setPriorityName(rs.getString("priorityName"));
		orderPriority.setDeleteFlag(rs.getInt("deleteFlag"));
		return orderPriority;
	}

}
