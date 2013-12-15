package org.teiath.data.domain.crp;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "extra_passengers")
public class ExtraPassenger {

	@Id
	@Column(name = "extra_passenger_id")
	@SequenceGenerator(name = "extra_passengers_seq", sequenceName = "extra_passengers_extra_passenger_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extra_passengers_seq")
	private Integer id;

	@Column(name = "passenger_name", length = 2000, nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rint_id", nullable = true)
	private RouteInterest routeInterest;

	public ExtraPassenger() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RouteInterest getRouteInterest() {
		return routeInterest;
	}

	public void setRouteInterest(RouteInterest routeInterest) {
		this.routeInterest = routeInterest;
	}
}
