package inpatientWeb.pharmacy.billingdata.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 10, 2018
 */
public class OrderDetails implements Serializable, RowMapper<OrderDetails> {
	
	private static final long serialVersionUID = -5964335675829515183L;
	
	private int orderedById;
	private Date orderDateTime;

	public int getOrderedById() {
		return orderedById;
	}

	public void setOrderedById(int orderedById) {
		this.orderedById = orderedById;
	}
	
	public Date getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(Date orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	@Override
	public OrderDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrderDetails orderDetails = new OrderDetails();
		orderDetails.setOrderedById(rs.getInt("orderedById"));
		orderDetails.setOrderDateTime(rs.getTimestamp("orderDateTime"));
		return orderDetails;
	}
	
}
