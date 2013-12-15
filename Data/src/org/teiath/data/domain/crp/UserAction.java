package org.teiath.data.domain.crp;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "crp_user_actions")
public class UserAction {
	public final static int TYPE_CREATE = 0;
	public final static int TYPE_DELETE = 1;
	public final static int TYPE_LOGIN = 3;
/*	public final static int TYPE_ROOMMATES = 3;*/

	@Id
	@Column(name = "user_action_id")
	@SequenceGenerator(name = "user_actions_seq", sequenceName = "crp_user_actions_user_action_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_actions_seq")
	private Integer id;

	@Column(name = "user_action_date", nullable = false)
	private Date date;
	@Column(name = "user_action_type", nullable = false)
	private int type;
	@Column(name = "route_starting_point", length = 2000, nullable = true)
	private String startingPoint;
	@Column(name = "route_destination_point", length = 2000, nullable = true)
	private String destination;
	@Column(name = "route_date", nullable = true)
	private Date routeDate;
	@Column(name = "route_code", length = 2000, nullable = true)
	private String routeCode;
	@Column(name = "route_vehicle", length = 2000, nullable = true)
	private String vehicle;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public UserAction() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == UserAction.class && this.id
				.equals(((UserAction) obj).getId());
	}
}
