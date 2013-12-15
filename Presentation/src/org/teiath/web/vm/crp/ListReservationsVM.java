package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ListRouteInterestsDriverService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListReservationsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListReservationsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;

	@WireVariable
	private ListRouteInterestsDriverService listRouteInterestsDriverService;

	private RouteInterestSearchCriteria routeInterestSearchCriteria;
	private ListModelList<RouteInterest> reservationsList;
	private RouteInterest selectedReservation;
	private Route route;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		try {
			route = listRouteInterestsDriverService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		routeInterestSearchCriteria.setRoute(route);
		//1: Approved Reservations
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
		routeInterestSearchCriteria.setOrderField("interestDate");
		routeInterestSearchCriteria.setOrderDirection("descending");

		reservationsList = new ListModelList<>();

		try {
			SearchResults<RouteInterest> results = listRouteInterestsDriverService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> reservations = results.getData();
			reservationsList.addAll(reservations);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			if (reservationsList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		String sortField = listheader.getId();
		switch (sortField) {
			case "passengerLastname":
				routeInterestSearchCriteria.setOrderField("usr.lastName");
				break;
			case "passengerName":
				routeInterestSearchCriteria.setOrderField("usr.firstName");
				break;
		}

		routeInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		routeInterestSearchCriteria.setPageNumber(0);
		selectedReservation = null;
		reservationsList.clear();

		try {
			SearchResults<RouteInterest> results = listRouteInterestsDriverService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> reservations = results.getData();
			reservationsList.addAll(reservations);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedReservation")
	public void onPaging() {
		if (reservationsList != null) {
			routeInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<RouteInterest> results = listRouteInterestsDriverService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				selectedReservation = null;
				reservationsList.clear();
				reservationsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onView() {
		if (selectedReservation != null) {
			ZKSession.setAttribute("reservationId", selectedReservation.getId());
			ZKSession.sendRedirect(PageURL.RESERVATION_VIEW);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_LIST);
	}

	@Command
	public void onPrintPDF() {
		SearchResults<RouteInterest> results = null;
		routeInterestSearchCriteria.setPageNumber(0);
		routeInterestSearchCriteria.setPageSize(0);
		try {
			results = listRouteInterestsDriverService.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<RouteInterest> routeInterests = results.getData();

		Report report = ReportToolkit.requestRouteInterestsReport(routeInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public RouteInterestSearchCriteria getRouteInterestSearchCriteria() {
		return routeInterestSearchCriteria;
	}

	public void setRouteInterestSearchCriteria(RouteInterestSearchCriteria routeInterestSearchCriteria) {
		this.routeInterestSearchCriteria = routeInterestSearchCriteria;
	}

	public ListModelList<RouteInterest> getReservationsList() {
		return reservationsList;
	}

	public void setReservationsList(ListModelList<RouteInterest> reservationsList) {
		this.reservationsList = reservationsList;
	}

	public RouteInterest getSelectedReservation() {
		return selectedReservation;
	}

	public void setSelectedReservation(RouteInterest selectedReservation) {
		this.selectedReservation = selectedReservation;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
