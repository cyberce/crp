package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.Vehicle;

import java.io.Serializable;
import java.util.Date;

public class RouteInterestSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Route route;
	private Integer status;
	private Date dateFrom;
	private Date dateTo;
	private User passenger;
	private User driver;
	private String routeCode;
	private Vehicle vehicle;

	public RouteInterestSearchCriteria() {
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public User getPassenger() {
		return passenger;
	}

	public void setPassenger(User passenger) {
		this.passenger = passenger;
	}

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
}
