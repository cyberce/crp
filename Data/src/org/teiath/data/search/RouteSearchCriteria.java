package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Vehicle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;

public class RouteSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Integer recurring;
	private Integer status;
	private Date dateFrom;
	private Date dateTo;
	private Date timeFrom;
	private Date timeTo;
	private Integer availability;
	private Integer objectTransportAllowed;
	private Integer smokingAllowed;
	private Integer peopleNumber;
	private BigDecimal maxAmount;
	private String tags;
	private Vehicle vehicle;
	private User user;
	private Boolean ameaAccessible;
	private Boolean enabled;
	private String code;
	private String searchLocationFrom;
	private String searchLocationTo;
	private int searchRadiusFrom;
	private int searchRadiusTo;
	private Integer sortOrder;
	private Integer userType;
	private Boolean completed;

	public RouteSearchCriteria() {
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
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

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(Date timeFrom) {
		this.timeFrom = timeFrom;
	}

	public Date getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(Date timeTo) {
		this.timeTo = timeTo;
	}

	public Integer getRecurring() {
		return recurring;
	}

	public void setRecurring(Integer recurring) {
		this.recurring = recurring;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getObjectTransportAllowed() {
		return objectTransportAllowed;
	}

	public void setObjectTransportAllowed(Integer objectTransportAllowed) {
		this.objectTransportAllowed = objectTransportAllowed;
	}

	public Integer getSmokingAllowed() {
		return smokingAllowed;
	}

	public void setSmokingAllowed(Integer smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public Integer getPeopleNumber() {
		return peopleNumber;
	}

	public void setPeopleNumber(Integer peopleNumber) {
		this.peopleNumber = peopleNumber;
	}

	public User getUser() {
		return user;
	}

	public Boolean getAmeaAccessible() {
		return ameaAccessible;
	}

	public void setAmeaAccessible(Boolean ameaAccessible) {
		this.ameaAccessible = ameaAccessible;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void materializeGeometry(String start, String end) {
		StringTokenizer startStringTokenizer = new StringTokenizer(start, "|");
		StringTokenizer endStringTokenizer = new StringTokenizer(end, "|");

		String lat, lng;
		StringTokenizer st;

		if (startStringTokenizer.hasMoreTokens()) {
			String token = startStringTokenizer.nextToken();
			st = new StringTokenizer(token, "),(");
			if (st.hasMoreTokens()) {
				lat = st.nextToken().trim();
				lng = st.nextToken().trim();

				System.out.println("Route Point: " + lat + " - " + lng);

				this.searchLocationFrom = "POINT(" + lat + " " + lng + ")";
			}
		}

		if (endStringTokenizer.hasMoreTokens()) {
			String token = endStringTokenizer.nextToken();
			st = new StringTokenizer(token, "),(");
			if (st.hasMoreTokens()) {
				lat = st.nextToken().trim();
				lng = st.nextToken().trim();

				System.out.println("Route Point: " + lat + " - " + lng);

				this.searchLocationTo = "POINT(" + lat + " " + lng + ")";
			}
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSearchLocationFrom() {
		return searchLocationFrom;
	}

	public void setSearchLocationFrom(String searchLocationFrom) {
		this.searchLocationFrom = searchLocationFrom;
	}

	public String getSearchLocationTo() {
		return searchLocationTo;
	}

	public void setSearchLocationTo(String searchLocationTo) {
		this.searchLocationTo = searchLocationTo;
	}

	public int getSearchRadiusFrom() {
		return searchRadiusFrom;
	}

	public void setSearchRadiusFrom(int searchRadiusFrom) {
		this.searchRadiusFrom = searchRadiusFrom;
	}

	public int getSearchRadiusTo() {
		return searchRadiusTo;
	}

	public void setSearchRadiusTo(int searchRadiusTo) {
		this.searchRadiusTo = searchRadiusTo;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
}
