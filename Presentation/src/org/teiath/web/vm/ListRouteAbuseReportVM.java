package org.teiath.web.vm;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.domain.crp.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAbuseReportSearchCriteria;
import org.teiath.data.search.UserActionSearchCriteria;
import org.teiath.service.crp.ListRouteAbuseReportsService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListRouteAbuseReportVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListRouteAbuseReportVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;

	@WireVariable
	private ListRouteAbuseReportsService listRouteAbuseReportsService;

	private RouteAbuseReportSearchCriteria routeAbuseReportSearchCriteria;
	private ListModelList<RouteAbuseReport> reportsList;
	private RouteAbuseReport selectedRouteAbuseReport;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("routeAbuseReportSearchCriteria") != null) {
			routeAbuseReportSearchCriteria = (RouteAbuseReportSearchCriteria) ZKSession.getAttribute("routeAbuseReportSearchCriteria");
			ZKSession.removeAttribute("routeAbuseReportSearchCriteria");

			selectedRouteAbuseReport= null;
			reportsList = new ListModelList<>();
			try {
				SearchResults<RouteAbuseReport> results = listRouteAbuseReportsService
						.searchRouteAbuseReportsByCriteria(routeAbuseReportSearchCriteria);
				Collection<RouteAbuseReport> reports = results.getData();
				reportsList.addAll(reports);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeAbuseReportSearchCriteria.getPageNumber());
				if (reportsList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			//Initial search criteria
			routeAbuseReportSearchCriteria = new RouteAbuseReportSearchCriteria();
			routeAbuseReportSearchCriteria.setPageSize(paging.getPageSize());
			routeAbuseReportSearchCriteria.setPageNumber(0);
			routeAbuseReportSearchCriteria.setOrderField("reportDate");
			routeAbuseReportSearchCriteria.setOrderDirection("descending");

			reportsList = new ListModelList<>();

			try {
				SearchResults<RouteAbuseReport> results = listRouteAbuseReportsService
						.searchRouteAbuseReportsByCriteria(routeAbuseReportSearchCriteria);
				Collection<RouteAbuseReport> reports = results.getData();
				reportsList.addAll(reports);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeAbuseReportSearchCriteria.getPageNumber());
				if (reportsList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("abuse.reports")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		routeAbuseReportSearchCriteria.setOrderField(listheader.getId());
		routeAbuseReportSearchCriteria.setOrderDirection(listheader.getSortDirection());
		routeAbuseReportSearchCriteria.setPageNumber(0);
		selectedRouteAbuseReport = null;
		reportsList.clear();

		try {
			SearchResults<RouteAbuseReport> results = listRouteAbuseReportsService
					.searchRouteAbuseReportsByCriteria(routeAbuseReportSearchCriteria);
			Collection<RouteAbuseReport> reports = results.getData();
			reportsList.addAll(reports);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeAbuseReportSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("abuse.reports")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedRouteAbuseReport")
	public void onPaging() {
		if (reportsList != null) {
			routeAbuseReportSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<RouteAbuseReport> results = listRouteAbuseReportsService
						.searchRouteAbuseReportsByCriteria(routeAbuseReportSearchCriteria);
				selectedRouteAbuseReport = null;
				reportsList.clear();
				reportsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeAbuseReportSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("abuse.reports")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedRouteAbuseReport")
	public void onSearch() {
		selectedRouteAbuseReport = null;
		reportsList.clear();
		routeAbuseReportSearchCriteria.setPageNumber(0);
		routeAbuseReportSearchCriteria.setPageSize(paging.getPageSize());

		try {
			SearchResults<RouteAbuseReport> results = listRouteAbuseReportsService
					.searchRouteAbuseReportsByCriteria(routeAbuseReportSearchCriteria);
			Collection<RouteAbuseReport> reports = results.getData();
			reportsList.addAll(reports);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeAbuseReportSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("abuse.reports")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedRouteAbuseReport", "routeAbuseReportSearchCriteria"})
	public void onResetSearch() {
		dateFrom.setRawValue(null);
		dateTo.setRawValue(null);
		routeAbuseReportSearchCriteria.setDateFrom(null);
		routeAbuseReportSearchCriteria.setDateTo(null);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("routeAbuseReportId", selectedRouteAbuseReport.getId());
		ZKSession.setAttribute("routeAbuseReportSearchCriteria", routeAbuseReportSearchCriteria);
		ZKSession.sendRedirect(PageURL.VIEW_ABUSE);
	}

	@Command
	@NotifyChange("selectedRouteAbuseReport")
	public void onToggleBan() {
		if (selectedRouteAbuseReport != null) {
			Messagebox.show(! selectedRouteAbuseReport.getReportedUser().isBanned() ? Labels
					.getLabel("user.messages.inactivate_title") : Labels.getLabel("user.messages.restore_title"),
					! selectedRouteAbuseReport.getReportedUser().isBanned() ? Labels
							.getLabel("user.messages.inactivate_confirm") : Labels
							.getLabel("user.messages.restore_confirm"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							if (selectedRouteAbuseReport != null) {
								try {
									listRouteAbuseReportsService.toggleBan(selectedRouteAbuseReport.getReportedUser());
									selectedRouteAbuseReport = null;
									onSearch();
								} catch (ServiceException e) {
									Messagebox.show(MessageBuilder
											.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
											Labels.getLabel("common.messages.save_title"), Messagebox.OK,
											Messagebox.ERROR);
								}
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	public RouteAbuseReportSearchCriteria getRouteAbuseReportSearchCriteria() {
		return routeAbuseReportSearchCriteria;
	}

	public void setRouteAbuseReportSearchCriteria(RouteAbuseReportSearchCriteria routeAbuseReportSearchCriteria) {
		this.routeAbuseReportSearchCriteria = routeAbuseReportSearchCriteria;
	}

	public ListModelList<RouteAbuseReport> getReportsList() {
		return reportsList;
	}

	public void setReportsList(ListModelList<RouteAbuseReport> reportsList) {
		this.reportsList = reportsList;
	}

	public RouteAbuseReport getSelectedRouteAbuseReport() {
		return selectedRouteAbuseReport;
	}

	public void setSelectedRouteAbuseReport(RouteAbuseReport selectedRouteAbuseReport) {
		this.selectedRouteAbuseReport = selectedRouteAbuseReport;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}
}
