package org.teiath.web.vm.reports;

import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class SelectRouteReportVM
		extends BaseVM {

	@Wire("#typesCombo")
	Combobox typesCombo;
	@Wire("#dateFromRow")
	private Row dateFromRow;
	@Wire("#dateToRow")
	private Row dateToRow;

	private Date dateFrom;
	private Date dateTo;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onSelectReportType() {

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			dateFromRow.setVisible(true);
			dateToRow.setVisible(true);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			dateFromRow.setVisible(true);
			dateToRow.setVisible(true);
		}
	}

	@Command
	public void onPrintPDF() {

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCreatedRoutesReport(dateFrom, dateTo, ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCompletedRoutesReport(dateFrom, dateTo, ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onPrintXLS() {
		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCreatedRoutesReport(dateFrom, dateTo, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.CREATED_ROUTES);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCompletedRoutesReport(dateFrom, dateTo, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.COMPLETED_ROUTES);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		}
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
