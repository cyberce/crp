package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.CreateRouteAbuseReportService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
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
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ReportAbuseVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ReportAbuseVM.class.getName());

	@Wire("#driverLabel")
	private Label driverLabel;
	@Wire("#userListBox")
	private Hbox userListBox;
	@Wire("#selectedUserCombo")
	private Combobox selectedUserCombo;

	@WireVariable
	private CreateRouteAbuseReportService createRouteAbuseReportService;

	private Route route;
	private ListModelList<RouteInterest> routeInterests;
	private RouteInterestSearchCriteria routeInterestSearchCriteria;
	private RouteInterest selectedRouteInterest;
	private RouteAbuseReport routeAbuseReport;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		route = new Route();
		route.setUser(loggedUser);
		routeAbuseReport = new RouteAbuseReport();

		try {
			route = createRouteAbuseReportService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
			if (loggedUser.getId() == route.getUser().getId()) {
				driverLabel.setVisible(false);
				userListBox.setVisible(true);
			} else {
				driverLabel.setVisible(true);
				userListBox.setVisible(false);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route.reportAbuse")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
		}
	}

	@Command
	public void onSave() {

		routeAbuseReport.setDestination(route.getDestinationPoint());
		routeAbuseReport.setRouteCode(route.getCode());
		routeAbuseReport.setRouteDate(route.getRouteDate());
		routeAbuseReport.setRouteTime(route.getRouteTime());
		routeAbuseReport.setStartingPoint(route.getStartingPoint());
		routeAbuseReport.setVehicle(route.getVehicle().getFullName());
		routeAbuseReport.setVehicleColor(route.getVehicle().getColor());
		routeAbuseReport.setReportDate(new Date());
		routeAbuseReport.setReporterUser(loggedUser);

		if (loggedUser.getId() == route.getUser().getId()) {

			routeAbuseReport.setReportedUser(selectedRouteInterest.getUser());
		} else {
			routeAbuseReport.setReportedUser(route.getUser());
		}

		try {
			createRouteAbuseReportService.saveReport(routeAbuseReport);
			Messagebox
					.show(Labels.getLabel("route.abuserReport.success"), Labels.getLabel("common.messages.save_title"),
							Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
						public void onEvent(Event evt) {
							if (loggedUser.getId() == route.getUser().getId()) {
								ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
							} else {
								ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
							}
						}
					});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route.reportAbuse")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			if (loggedUser.getId() == route.getUser().getId()) {
				ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
			} else {
				ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
			}
		}
	}

	@Command
	public void onCancel() {
		if (loggedUser.getId() == route.getUser().getId()) {
			Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
					Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
						case Messagebox.NO:
							break;
					}
				}
			});
		} else {
			Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
					Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	public ListModelList<RouteInterest> getRouteInterets() {
		if (routeInterests == null) {
			routeInterests = new ListModelList<>();
			routeInterestSearchCriteria = new RouteInterestSearchCriteria();
			routeInterestSearchCriteria.setRoute(route);
			routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
			try {
				SearchResults<RouteInterest> results = createRouteAbuseReportService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				Collection<RouteInterest> reservations = results.getData();
				routeInterests.addAll(reservations);
//				selectedRouteInterest = routeInterests.get(0);
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route.reportAbuse")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return routeInterests;
	}

	public RouteAbuseReport getRouteAbuseReport() {
		return routeAbuseReport;
	}

	public void setRouteAbuseReport(RouteAbuseReport routeAbuseReport) {
		this.routeAbuseReport = routeAbuseReport;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public RouteInterestSearchCriteria getRouteInterestSearchCriteria() {
		return routeInterestSearchCriteria;
	}

	public void setRouteInterestSearchCriteria(RouteInterestSearchCriteria routeInterestSearchCriteria) {
		this.routeInterestSearchCriteria = routeInterestSearchCriteria;
	}

	public RouteInterest getSelectedRouteInterest() {
		return selectedRouteInterest;
	}

	public void setSelectedRouteInterest(RouteInterest selectedRouteInterest) {
		this.selectedRouteInterest = selectedRouteInterest;
	}
}
