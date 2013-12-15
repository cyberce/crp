package org.teiath.data.domain.crp;

import org.teiath.data.domain.Assessment;
import org.teiath.data.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "crp_route_assessments")
@PrimaryKeyJoinColumn(name = "assessment_id")
public class RouteAssessment
		extends Assessment {

	public final static int RATING_FOR_DRIVER = 0;
	public final static int RATING_FOR_PASSENGER = 1;

	@Column(name = "assessedType", nullable = false)
	private int assessedType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User assessedUser;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rout_id", nullable = false)
	private Route assessedRoute;

	@javax.persistence.Transient
	private String ratingId;

	public RouteAssessment() {
	}

	public int getAssessedType() {
		return assessedType;
	}

	public void setAssessedType(int assessedType) {
		this.assessedType = assessedType;
	}

	public Route getAssessedRoute() {
		return assessedRoute;
	}

	public void setAssessedRoute(Route assessedRoute) {
		this.assessedRoute = assessedRoute;
	}

	public User getAssessedUser() {
		return assessedUser;
	}

	public void setAssessedUser(User assessedUser) {
		this.assessedUser = assessedUser;
	}

	public String getRatingId() {
		ratingId = "rate" + this.getId();
		return ratingId;
	}

	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}
}
