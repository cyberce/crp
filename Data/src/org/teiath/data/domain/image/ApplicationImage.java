package org.teiath.data.domain.image;

import javax.persistence.*;

@Entity
@Table(name = "application_images")
public class ApplicationImage {

	@Id
	@Column(name = "application_image_id")
	@SequenceGenerator(name = "application_images_seq", sequenceName = "application_images_application_image_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_images_seq")
	private Integer id;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	public ApplicationImage() {
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

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == ApplicationImage.class && this.id
				.equals(((ApplicationImage) obj).getId());
	}
}
