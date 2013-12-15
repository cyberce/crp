package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.RouteSegment;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@XmlRootElement(name = "route")
@JsonRootName(value = "route")
public class ServiceRoute {

	private String startingPoint;
	private String destinationPoint;
	private Date routeDate;
	private Date routeTime;
	private Boolean flexible;
	private Integer totalSeats;
	private Boolean objectTransportAllowed;
	private Boolean smokingAllowed;
	private BigDecimal contributionAmount;
	private String tags;
	private String comment;
	private Boolean recurring;
	private String days;
	private Date routeEndDate;
	private Date routeCreationDate;
	private String vehicleName;
	private String vehiclePlate;
	private String driverName;
	private String code;
	private Boolean ameaAccessible;
	private Boolean enabled;
	private Set<RouteInterest> routeInterests;
	private Set<RouteAssessment> routeAssessments;
	private Set<RouteSegment> routeSegments;
	private ServiceStopList stops;

	public ServiceRoute() {
	}

	public String getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(String startingPoint) {
		this.startingPoint = startingPoint;
	}

	public String getDestinationPoint() {
		return destinationPoint;
	}

	public void setDestinationPoint(String destinationPoint) {
		this.destinationPoint = destinationPoint;
	}

	public Date getRouteDate() {
		return routeDate;
	}

	public void setRouteDate(Date routeDate) {
		this.routeDate = routeDate;
	}

	public Date getRouteTime() {
		return routeTime;
	}

	public void setRouteTime(Date routeTime) {
		this.routeTime = routeTime;
	}

	public Boolean isFlexible() {
		return flexible;
	}

	public void setFlexible(Boolean flexible) {
		this.flexible = flexible;
	}

	public Integer getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Boolean isObjectTransportAllowed() {
		return objectTransportAllowed;
	}

	public void setObjectTransportAllowed(Boolean objectTransportAllowed) {
		this.objectTransportAllowed = objectTransportAllowed;
	}

	public Boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public void setSmokingAllowed(Boolean smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public BigDecimal getContributionAmount() {
		return contributionAmount;
	}

	public void setContributionAmount(BigDecimal contributionAmount) {
		this.contributionAmount = contributionAmount;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public Date getRouteEndDate() {
		return routeEndDate;
	}

	public void setRouteEndDate(Date routeEndDate) {
		this.routeEndDate = routeEndDate;
	}

	public Date getRouteCreationDate() {
		return routeCreationDate;
	}

	public void setRouteCreationDate(Date routeCreationDate) {
		this.routeCreationDate = routeCreationDate;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Set<RouteInterest> getRouteInterests() {
		return routeInterests;
	}

	public void setRouteInterests(Set<RouteInterest> routeInterests) {
		this.routeInterests = routeInterests;
	}

	public Set<RouteAssessment> getRouteAssessments() {
		return routeAssessments;
	}

	public void setRouteAssessments(Set<RouteAssessment> routeAssessments) {
		this.routeAssessments = routeAssessments;
	}

	public Set<RouteSegment> getRouteSegments() {
		return routeSegments;
	}

	public void setRouteSegments(Set<RouteSegment> routeSegments) {
		this.routeSegments = routeSegments;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean isAmeaAccessible() {
		return ameaAccessible;
	}

	public void setAmeaAccessible(Boolean ameaAccessible) {
		this.ameaAccessible = ameaAccessible;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public ServiceStopList getStops() {
		return stops;
	}

	public void setStops(ServiceStopList stops) {
		this.stops = stops;
	}
}
