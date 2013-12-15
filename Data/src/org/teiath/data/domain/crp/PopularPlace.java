package org.teiath.data.domain.crp;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "crp_popular_places")
public class PopularPlace
		implements Serializable {

	@Id
	@Column(name = "place_id")
	@SequenceGenerator(name = "popular_places_seq", sequenceName = "crp_popular_places_place_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "popular_places_seq")
	private Integer id;

	@Column(name = "place_name", length = 2000, nullable = false)
	private String name;
	@Column(name = "place_address", length = 2000, nullable = false)
	private String address;

	public PopularPlace() {
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
