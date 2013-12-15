package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewHistoryRouteService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ViewHistoryRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewHistoryRouteVM.class.getName());

	@WireVariable
	private ViewHistoryRouteService viewHistoryRouteService;

	@Wire("#listBoxRow")
	private Row listBoxRow;
	@Wire("#smokingAllowedLabel")
	private Label smokingAllowedLabel;
	@Wire("#labelRow")
	private Row labelRow;
	@Wire("#driverRatingsListBoxRow")
	private Row driverRatingsListBoxRow;
	@Wire("#driverRatingsLabelRow")
	private Row driverRatingsLabelRow;
	@Wire("#passengersRatingsListBoxRow")
	private Row passengersRatingsListBoxRow;
	@Wire("#passengersRatingsLabelRow")
	private Row passengersRatingsLabelRow;
	@Wire("#vehicleTypeLabel")
	private Label vehicleTypeLabel;

	private Route route;
	private RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();
	private ListModelList<RouteInterest> reservationsList;
	private ListModelList<RouteAssessment> driverRatings;
	private ListModelList<RouteAssessment> passengerRatings;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		driverRatings = new ListModelList<>();
		passengerRatings = new ListModelList<>();
		try {
			route = viewHistoryRouteService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
			if (route.isSmokingAllowed()) {
				smokingAllowedLabel.setValue(Labels.getLabel("common.no"));
			} else {
				smokingAllowedLabel.setValue(Labels.getLabel("common.yes"));
			}

			if (route.getVehicle().getType() == Vehicle.CAR)
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.car"));
			else
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.motorcycle"));

			Collection<RouteAssessment> driverRatingsl = viewHistoryRouteService.findDriverRatings(route);
			driverRatings.addAll(driverRatingsl);
			for (RouteAssessment routeAssessment : driverRatings) {
				Clients.evalJavaScript(
						"doLoadDriver('" + routeAssessment.getRatingId() + "', '" + routeAssessment.getRating() + "')");
			}

			Collection<RouteAssessment> passengerRatingsl = viewHistoryRouteService.findPassengerRatings(route);
			passengerRatings.addAll(passengerRatingsl);
			for (RouteAssessment routeAssessment : passengerRatings) {
				Clients.evalJavaScript(
						"doLoad('" + routeAssessment.getRatingId() + "', '" + routeAssessment.getRating() + "')");
			}

			if (driverRatings.isEmpty()) {
				driverRatingsListBoxRow.setVisible(false);
				driverRatingsLabelRow.setVisible(true);
			}

			if (passengerRatings.isEmpty()) {
				passengersRatingsListBoxRow.setVisible(false);
				passengersRatingsLabelRow.setVisible(true);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			if (ZKSession.getAttribute("historyType").equals("passenger")) {
				ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
			} else {
				ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
			}
		}

		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		routeInterestSearchCriteria.setRoute(route);
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
		reservationsList = new ListModelList<>();
		try {
			SearchResults<RouteInterest> results = viewHistoryRouteService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> reservations = results.getData();
			reservationsList.addAll(reservations);
			if (reservationsList.isEmpty()) {
				listBoxRow.setVisible(false);
				labelRow.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("historyType").equals("passenger")) {
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
		} else {
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
		}
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public ListModelList<RouteInterest> getReservationsList() {
		return reservationsList;
	}

	public void setReservationsList(ListModelList<RouteInterest> reservationsList) {
		this.reservationsList = reservationsList;
	}

	public ListModelList<RouteAssessment> getDriverRatings() {
		return driverRatings;
	}

	public void setDriverRatings(ListModelList<RouteAssessment> driverRatings) {
		this.driverRatings = driverRatings;
	}

	public ListModelList<RouteAssessment> getPassengerRatings() {
		return passengerRatings;
	}

	public void setPassengerRatings(ListModelList<RouteAssessment> passengerRatings) {
		this.passengerRatings = passengerRatings;
	}
}
