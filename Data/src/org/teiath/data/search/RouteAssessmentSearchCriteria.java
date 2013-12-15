package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;

public class RouteAssessmentSearchCriteria
		extends SearchCriteria {

	private Route route;
	private Integer assessedType;
	private User assessedUser;
	private User assessorUser;

	public RouteAssessmentSearchCriteria() {
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Integer getAssessedType() {
		return assessedType;
	}

	public void setAssessedType(Integer assessedType) {
		this.assessedType = assessedType;
	}

	public User getAssessedUser() {
		return assessedUser;
	}

	public void setAssessedUser(User assessedUser) {
		this.assessedUser = assessedUser;
	}

	public User getAssessorUser() {
		return assessorUser;
	}

	public void setAssessorUser(User assessorUser) {
		this.assessorUser = assessorUser;
	}
}
