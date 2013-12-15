package org.teiath.data.domain.crp;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "licenses")
public class License
		implements Serializable {

	@Id
	@Column(name = "license_id")
	@SequenceGenerator(name = "licenses_seq", sequenceName = "licenses_license_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "licenses_seq")
	private Integer id;

	@Column(name = "license_code", length = 2000, nullable = true)
	private String code;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	@Column(name = "pending", nullable = true)
	private Boolean pending;

	public License() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}
}
