package org.teiath.data.domain;

import javax.persistence.*;

@Entity
@Table(name = "sms_messages")
public class SmsMessage {

	public final static int DRIVER_APPROVE = 1;
	public final static int DRIVER_DISAPPROVE = 2;
	public final static int ROUTE_CHANGE = 3;
	public final static int ROUTE_INTEREST_APPROVE = 4;
	public final static int ROUTE_INTEREST_REMOVE = 5;
	public final static int ROUTE_REMOVE = 6;

	@Id
	@Column(name = "sms_id")
	@SequenceGenerator(name = "sms_messages_seq", sequenceName = "sms_messages_sms_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sms_messages_seq")
	private Integer id;

	@Column(name = "message", nullable = false, length = 2000)
	private String message;

	public SmsMessage() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
