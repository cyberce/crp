package org.teiath.data.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "crp_abuse_reports")
@Inheritance(strategy = InheritanceType.JOINED)
public class AbuseReport
		implements Serializable {

	@Id
	@Column(name = "abuse_report_id")
	@SequenceGenerator(name = "abuse_reports_seq", sequenceName = "crp_abuse_reports_abuse_report_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "abuse_reports_seq")
	private Integer id;

	@Column(name = "abuse_report_title", length = 2000)
	private String title;
	@Column(name = "abuse_report_description", length = 4000)
	private String description;
	@Column(name = "report_date", nullable = false)
	private Date reportDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	//Reporter User
	private User reporterUser;

	public AbuseReport() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public User getReporterUser() {
		return reporterUser;
	}

	public void setReporterUser(User reporterUser) {
		this.reporterUser = reporterUser;
	}
}
