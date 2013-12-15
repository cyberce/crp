package org.teiath.data.domain.sys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sys_parameters")
public class SysParameter
		implements Serializable {

	@Id
	@Column(name = "param_id")
	@SequenceGenerator(name = "params_seq", sequenceName = "sys_parameters_param_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "params_seq")
	private Integer id;

	@Column(name = "route_distance_weight_percent", nullable = false)
	private Integer routeDistanceWeight;
	@Column(name = "route_stops_weight_percent", nullable = false)
	private Integer routeStopsWeight;
	@Column(name = "route_length_weight_percent", nullable = false)
	private Integer routeLengthWeight;
	@Column(name = "levenshtein_percent", nullable = false)
	private Integer levenshteinPercent;
	@Column(name = "sms_enabled_edit_route", nullable = false)
	private boolean smsEnabledEditRoute;
	@Column(name = "sms_enabled_approve_interest", nullable = false)
	private boolean smsEnabledApproveInterest;
	@Column(name = "sms_enabled_disapprove_interest", nullable = false)
	private boolean smsEnabledDisapproveInterest;
	@Column(name = "sms_enabled_delete_route", nullable = false)
	private boolean smsEnabledDeleteRoute;
	@Column(name = "sms_enabled_driver", nullable = false)
	private boolean smsEnabledDriver;

	public SysParameter() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRouteDistanceWeight() {
		return routeDistanceWeight;
	}

	public void setRouteDistanceWeight(Integer routeDistanceWeight) {
		this.routeDistanceWeight = routeDistanceWeight;
	}

	public Integer getRouteStopsWeight() {
		return routeStopsWeight;
	}

	public void setRouteStopsWeight(Integer routeStopsWeight) {
		this.routeStopsWeight = routeStopsWeight;
	}

	public Integer getRouteLengthWeight() {
		return routeLengthWeight;
	}

	public void setRouteLengthWeight(Integer routeLengthWeight) {
		this.routeLengthWeight = routeLengthWeight;
	}

	public Integer getLevenshteinPercent() {
		return levenshteinPercent;
	}

	public void setLevenshteinPercent(Integer levenshteinPercent) {
		this.levenshteinPercent = levenshteinPercent;
	}

	public boolean isSmsEnabledEditRoute() {
		return smsEnabledEditRoute;
	}

	public void setSmsEnabledEditRoute(boolean smsEnabledEditRoute) {
		this.smsEnabledEditRoute = smsEnabledEditRoute;
	}

	public boolean isSmsEnabledApproveInterest() {
		return smsEnabledApproveInterest;
	}

	public void setSmsEnabledApproveInterest(boolean smsEnabledApproveInterest) {
		this.smsEnabledApproveInterest = smsEnabledApproveInterest;
	}

	public boolean isSmsEnabledDeleteRoute() {
		return smsEnabledDeleteRoute;
	}

	public void setSmsEnabledDeleteRoute(boolean smsEnabledDeleteRoute) {
		this.smsEnabledDeleteRoute = smsEnabledDeleteRoute;
	}

	public boolean isSmsEnabledDriver() {
		return smsEnabledDriver;
	}

	public void setSmsEnabledDriver(boolean smsEnabledDriver) {
		this.smsEnabledDriver = smsEnabledDriver;
	}

	public boolean isSmsEnabledDisapproveInterest() {
		return smsEnabledDisapproveInterest;
	}

	public void setSmsEnabledDisapproveInterest(boolean smsEnabledDisapproveInterest) {
		this.smsEnabledDisapproveInterest = smsEnabledDisapproveInterest;
	}
}
