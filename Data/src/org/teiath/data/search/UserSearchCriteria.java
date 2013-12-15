package org.teiath.data.search;

import java.io.Serializable;
import java.util.Date;

public class UserSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private String userKeyword;
	private String userName;
	private Integer userType;
	private Date dateFrom;
	private Date dateTo;
	private Boolean pendingDrivers;

	public UserSearchCriteria() {
	}

	public String getUserKeyword() {
		return userKeyword;
	}

	public void setUserKeyword(String userKeyword) {
		this.userKeyword = userKeyword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Boolean getPendingDrivers() {
		return pendingDrivers;
	}

	public void setPendingDrivers(Boolean pendingDrivers) {
		this.pendingDrivers = pendingDrivers;
	}
}
