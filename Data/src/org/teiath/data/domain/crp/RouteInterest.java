package org.teiath.data.domain.crp;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "crp_route_interests")
public class RouteInterest
		implements Serializable {

	public final static int STATUS_PENDING = 0;
	public final static int STATUS_APPROVED = 1;
	public final static int STATUS_REJECTED = 2;

	@Id
	@Column(name = "rint_id")
	@SequenceGenerator(name = "route_interests_seq", sequenceName = "crp_route_interests_rint_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_interests_seq")
	private Integer id;

	@Column(name = "rint_number_passengers", nullable = false)
	private Integer numberOfPassengers;
	@Column(name = "rint_obj_transport_required", nullable = false)
	private boolean objectTransportRequired;
	@Column(name = "rint_description", length = 4000, nullable = true)
	private String routeDescription;
	@Column(name = "rint_comment", length = 4000, nullable = true)
	private String comment;
	@Column(name = "rint_interest_date", nullable = false)
	private Date interestDate;
	@Column(name = "rint_status", nullable = false)
	private int status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rout_id", nullable = false)
	private Route route;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "routeInterest")
	private List<ExtraPassenger> extraPassengers;

	public RouteInterest() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumberOfPassengers() {
		return numberOfPassengers;
	}

	public void setNumberOfPassengers(Integer numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}

	public boolean isObjectTransportRequired() {
		return objectTransportRequired;
	}

	public void setObjectTransportRequired(boolean objectTransportRequired) {
		this.objectTransportRequired = objectTransportRequired;
	}

	public String getRouteDescription() {
		return routeDescription;
	}

	public void setRouteDescription(String routeDescription) {
		this.routeDescription = routeDescription;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<ExtraPassenger> getExtraPassengers() {
		return extraPassengers;
	}

	public void setExtraPassengers(List<ExtraPassenger> extraPassengers) {
		this.extraPassengers = extraPassengers;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == RouteInterest.class && this.id
				.equals(((RouteInterest) obj).getId());
	}
}
