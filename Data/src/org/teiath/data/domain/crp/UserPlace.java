package org.teiath.data.domain.crp;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "crp_places")
public class UserPlace
		implements Serializable {

	@Id
	@Column(name = "place_id")
	@SequenceGenerator(name = "places_seq", sequenceName = "crp_places_place_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "places_seq")
	private Integer id;

	@Column(name = "user_place_name", length = 2000, nullable = false)
	private String name;
	@Column(name = "user_place_address", length = 2000, nullable = false)
	private String address;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public UserPlace() {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
