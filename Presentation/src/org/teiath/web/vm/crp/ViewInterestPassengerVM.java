package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewRouteInterestPassengerService;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ViewInterestPassengerVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewInterestPassengerVM.class.getName());

	@WireVariable
	private ViewRouteInterestPassengerService viewRouteInterestPassengerService;

	@Wire("#smokingAllowedLabel")
	private Label smokingAllowedLabel;
	@Wire("#commentsListBoxRow")
	private Row commentsListBoxRow;
	@Wire("#commentsLabelRow")
	private Row commentsLabelRow;
	@Wire("#labelRow")
	private Row labelRow;
	@Wire("#listBoxRow")
	private Row listBoxRow;
	@Wire("#vehicleTypeLabel")
	private Label vehicleTypeLabel;

	private RouteInterest routeInterest;
	private RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();
	private ListModelList<RouteInterest> reservationsList;
	private ListModelList<RouteAssessment> driverComments;

	@AfterCompose
	@NotifyChange("routeInterest")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		driverComments = new ListModelList<>();
		try {
			routeInterest = viewRouteInterestPassengerService
					.getRouteInterestById((Integer) ZKSession.getAttribute("routeInterestId"));
			if (routeInterest.getRoute().isSmokingAllowed()) {
				smokingAllowedLabel.setValue(Labels.getLabel("common.no"));
			} else {
				smokingAllowedLabel.setValue(Labels.getLabel("common.yes"));
			}

			if (routeInterest.getRoute().getVehicle().getType() == Vehicle.CAR)
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.car"));
			else
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.motorcycle"));

			Collection<RouteAssessment> comments = viewRouteInterestPassengerService
					.findDriverComments(routeInterest.getRoute().getUser());
			driverComments.addAll(comments);

			if (driverComments.isEmpty()) {
				commentsListBoxRow.setVisible(false);
				commentsLabelRow.setVisible(true);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}

		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		routeInterestSearchCriteria.setRoute(routeInterest.getRoute());
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
		reservationsList = new ListModelList<>();
		try {
			SearchResults<RouteInterest> results = viewRouteInterestPassengerService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> reservations = results.getData();
			reservationsList.addAll(reservations);
			if (reservationsList.isEmpty()) {
				listBoxRow.setVisible(false);
				labelRow.setVisible(true);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_INTEREST_PASSENGER_LIST);
	}

	public RouteInterest getRouteInterest() {
		return routeInterest;
	}

	public void setRouteInterest(RouteInterest routeInterest) {
		this.routeInterest = routeInterest;
	}

	public ListModelList<RouteInterest> getReservationsList() {
		return reservationsList;
	}

	public void setReservationsList(ListModelList<RouteInterest> reservationsList) {
		this.reservationsList = reservationsList;
	}

	public ListModelList<RouteAssessment> getDriverComments() {
		return driverComments;
	}

	public void setDriverComments(ListModelList<RouteAssessment> driverComments) {
		this.driverComments = driverComments;
	}
}
