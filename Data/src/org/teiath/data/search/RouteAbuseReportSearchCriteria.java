package org.teiath.data.search;

import java.io.Serializable;
import java.util.Date;

public class RouteAbuseReportSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Date dateFrom;
	private Date dateTo;

	public RouteAbuseReportSearchCriteria() {
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
}
