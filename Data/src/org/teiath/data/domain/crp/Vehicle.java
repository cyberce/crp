package org.teiath.data.domain.crp;

import org.springframework.context.annotation.DependsOn;
import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "crp_vehicles")
public class Vehicle
		implements Serializable {

	public final static int CAR = 0;
	public final static int MOTORCYCLE = 1;

	@Id
	@Column(name = "vehi_id")
	@SequenceGenerator(name = "vehicles_seq", sequenceName = "crp_vehicles_vehi_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicles_seq")
	private Integer id;

	@Column(name = "vehi_brand", length = 50, nullable = false)
	private String brand;
	@Column(name = "vehi_model", length = 50, nullable = false)
	private String model;
	@Column(name = "vehi_year", length = 50, nullable = true)
	private String year;
	@Column(name = "vehi_color", length = 50, nullable = true)
	private String color;
	@Column(name = "vehi_type", nullable = false)
	private Integer type;
	@Column(name = "vehi_plate_number", length = 50, nullable = false)
	private String plateNumber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String fullName;

	@DependsOn({"brand", "model"})
	public String getFullName() {
		return brand + " " + model;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Vehicle() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Vehicle.class && this.id
				.equals(((Vehicle) obj).getId());
	}
}
