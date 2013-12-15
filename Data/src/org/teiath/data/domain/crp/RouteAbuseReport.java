package org.teiath.data.domain.crp;

import org.teiath.data.domain.AbuseReport;
import org.teiath.data.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "crp_route_abuse_reports")
@PrimaryKeyJoinColumn(name = "abuse_report_id")
public class RouteAbuseReport
		extends AbuseReport {

	@Column(name = "route_starting_point", length = 2000, nullable = true)
	private String startingPoint;
	@Column(name = "route_destination_point", length = 2000, nullable = true)
	private String destination;
	@Column(name = "route_date", nullable = true)
	private Date routeDate;
	@Column(name = "route_time", nullable = true)
	private Date routeTime;
	@Column(name = "route_code", length = 2000, nullable = true)
	private String routeCode;
	@Column(name = "route_vehicle", length = 2000, nullable = true)
	private String vehicle;
	@Column(name = "route_vehicle_color", length = 2000, nullable = true)
	private String vehicleColor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User reportedUser;


	public RouteAbuseReport() {
	}

	public String getVehicleColor() {
		return vehicleColor;
	}

	public void setVehicleColor(String vehicleColor) {
		this.vehicleColor = vehicleColor;
	}

	public String getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(String startingPoint) {
		this.startingPoint = startingPoint;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
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

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public User getReportedUser() {
		return reportedUser;
	}

	public void setReportedUser(User reportedUser) {
		this.reportedUser = reportedUser;
	}
}
