package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.*;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.EditRouteService;
import org.teiath.service.crp.PopularPlaceService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.crp.validator.RouteValidator;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class EditRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditRouteVM.class.getName());

	@Wire("#gb3")
	Groupbox popup;
	@Wire("#days")
	Textbox days;
	@Wire("#l1")
	Checkbox c1;
	@Wire("#l2")
	Checkbox c2;
	@Wire("#l3")
	Checkbox c3;
	@Wire("#l4")
	Checkbox c4;
	@Wire("#l5")
	Checkbox c5;
	@Wire("#l6")
	Checkbox c6;
	@Wire("#l7")
	Checkbox c7;
	@Wire("#recurringRG")
	Radiogroup recurringRG;
	@Wire("#startingPoint")
	Textbox startingPoint;
	@Wire("#destinationPoint")
	Textbox destinationPoint;
	@Wire("#routePathHolder")
	private Textbox routePathHolder;
	@Wire("#routeSegmentAddressesHolder")
	private Textbox routeSegmentAddressesHolder;

	@WireVariable
	private EditRouteService editRouteService;
	@WireVariable
	private PopularPlaceService popularPlaceService;

	private Validator routeValidator;
	private Route route;
	private ListModelList<Vehicle> vehiclesList;
	private RouteInterestSearchCriteria routeInterestSearchCriteria;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		routeValidator = new RouteValidator();
		route = new Route();
		route.setUser(loggedUser);

		vehiclesList = new ListModelList<>();
		try {
			route = editRouteService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
			vehiclesList.addAll(editRouteService.getVehiclesByUser(loggedUser));
			routeInterestSearchCriteria = new RouteInterestSearchCriteria();
			routeInterestSearchCriteria.setRoute(route);
			//1: Approved Reservations
			routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}

		if (route.isRecurring()) {
			String[] days = route.getDays().split(" ");
			for (String day : days) {
				if (c1.getValue().equals(day)) {
					c1.setChecked(true);
				}
				if (c2.getValue().equals(day)) {
					c2.setChecked(true);
				}
				if (c3.getValue().equals(day)) {
					c3.setChecked(true);
				}
				if (c4.getValue().equals(day)) {
					c4.setChecked(true);
				}
				if (c5.getValue().equals(day)) {
					c5.setChecked(true);
				}
				if (c6.getValue().equals(day)) {
					c6.setChecked(true);
				}
				if (c7.getValue().equals(day)) {
					c7.setChecked(true);
				}
			}
			popup.setOpen(true);
		}

		String path = "";
		String points = "";
		String addresses = "";
		List<String> stopAddresses = new ArrayList<>();

		Comparator<RouteSegment> comparator = new Comparator<RouteSegment>() {
			public int compare(RouteSegment c1, RouteSegment c2) {
				return Integer.valueOf(c1.getIndex()).compareTo(Integer.valueOf(c2.getIndex()));
			}
		};

		Comparator<RouteSegmentWaypoint> waypointComparator = new Comparator<RouteSegmentWaypoint>() {
			public int compare(RouteSegmentWaypoint c1, RouteSegmentWaypoint c2) {
				return c1.getIndex().compareTo(c2.getIndex());
			}
		};

		List<RouteSegment> routeSegments = new ArrayList<>(route.getRouteSegments());
		Collections.sort(routeSegments, comparator);

		RouteSegment routeSegment;
		for (int i=0,j=routeSegments.size(); i<j;i++) {
			routeSegment = routeSegments.get(i);
			path += routeSegment.getStartLat() + "," + routeSegment.getStartLng() + "|" + routeSegment.getEndLat() + "," + routeSegment.getEndLng();
			if (routeSegment.getRouteSegmentWaypoints() != null) {
				List<RouteSegmentWaypoint> waypoints = new ArrayList<>(routeSegment.getRouteSegmentWaypoints());
				Collections.sort(waypoints, waypointComparator);
				for (RouteSegmentWaypoint wp: waypoints) {
					path += "|" + wp.getLat() + "," + wp.getLng();
				}
			}
			path+= "#";

			if (i==0) {
				points += routeSegment.getStartLat() + "," + routeSegment.getStartLng() + "|";
				addresses += routeSegment.getStartAddress() + "|";
			}
			if (i>0 && i<routeSegments.size()-1) {
				points += routeSegment.getStartLat() + "," + routeSegment.getStartLng() + "|";
				addresses += routeSegment.getStartAddress() + "|";
			}
			if (i == routeSegments.size()-1) {
				if (routeSegments.size() > 1) {
					points += routeSegment.getStartLat() + "," + routeSegment.getStartLng() + "|";
					addresses += routeSegment.getStartAddress() + "|";
				}
				points += routeSegment.getEndLat() + "," + routeSegment.getEndLng() + "|";
				addresses += routeSegment.getEndAddress() + "|";
			}
		}
		path = path.substring(0, path.length() - 1);
		points = points.substring(0, points.length() - 1);
		addresses = addresses.substring(0, addresses.length() - 1);

		int index;
		if (!path.isEmpty()) {
			Clients.evalJavaScript("doLoad('" + path + "', '" + points + "', '" + addresses + "')");
		}

		try {
			// Send popular places array to design
			Collection<PopularPlace> popularPlaces = popularPlaceService.fetchAll();
			String popularPlacesJSArray = "";
			if (! popularPlaces.isEmpty()) {
				popularPlacesJSArray = "var prefPlaces = new Array(" + popularPlaces.size() + ");";
				index = 0;
				for (PopularPlace popularPlace : popularPlaces) {
					popularPlacesJSArray += "prefPlaces[" + index + "] = new Array(2);";
					popularPlacesJSArray += "prefPlaces[" + index + "][0] = '" + popularPlace.getName() + "';";
					popularPlacesJSArray += "prefPlaces[" + index + "][1] = '" + popularPlace.getAddress() + "';";
					index++;
				}
			}

			// Send my places array to design
			Collection<UserPlace> myPlaces = editRouteService.getPlacesByUser(loggedUser);
			String myPlacesJSArray = "";
			if (! myPlaces.isEmpty()) {
				myPlacesJSArray = "var myPlaces = new Array(" + myPlaces.size() + ");";
				index = 0;
				for (UserPlace userPlace : myPlaces) {
					myPlacesJSArray += "myPlaces[" + index + "] = new Array(2);";
					myPlacesJSArray += "myPlaces[" + index + "][0] = '" + userPlace.getName() + "';";
					myPlacesJSArray += "myPlaces[" + index + "][1] = '" + userPlace.getAddress() + "';";
					index++;
				}
			}

			Clients.evalJavaScript(popularPlacesJSArray + myPlacesJSArray + "loadData(prefPlaces, myPlaces);");
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Command
	public void onRecurring() {
		if (recurringRG.getSelectedIndex() == 0) {
			popup.setOpen(true);
		} else {
			popup.setOpen(false);
		}
	}

	@Command
	public void onSave(
			@ContextParam(ContextType.TRIGGER_EVENT)
			InputEvent event) {
		route.getRouteSegments().clear();
		route.materializeGeometry(((Textbox) event.getTarget()).getValue(), routeSegmentAddressesHolder.getValue(), routePathHolder.getValue());

		route.setStartingPoint(startingPoint.getText());
		route.setDestinationPoint(destinationPoint.getText());

		if (route.getContributionAmount() == null) {
			route.setContributionAmount(BigDecimal.ZERO);
		}

		if (route.isRecurring()) {
			route.setDays(route.getDays() + days.getValue());
		}

		if (!route.isRecurring()) {
			route.setDays(null);
			route.setRouteEndDate(route.getRouteDate());
		}
		SearchResults<RouteInterest> results = null;
		try {
			results = editRouteService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> reservations = results.getData();
			if (route.getTotalSeats() >= reservations.size()) {
				try {
					editRouteService.saveRoute(route);
					Messagebox.show(Labels.getLabel("route.message.success"), Labels.getLabel("common.messages.save_title"),
							Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
						public void onEvent(Event evt) {
							ZKSession.sendRedirect(PageURL.ROUTE_LIST);
						}
					});
				} catch (ServiceException e) {
					log.error(e.getMessage());
					Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
							Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
					ZKSession.sendRedirect(PageURL.ROUTE_LIST);
				}
			}
			else {
				Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("validation.routes.notEnoughSeats"), Labels.getLabel("route")),
						Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.ROUTE_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public Validator getRouteValidator() {
		return routeValidator;
	}

	public void setRouteValidator(Validator routeValidator) {
		this.routeValidator = routeValidator;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public ListModelList<Vehicle> getVehiclesList() {
		return vehiclesList;
	}

	public void setVehiclesList(ListModelList<Vehicle> vehiclesList) {
		this.vehiclesList = vehiclesList;
	}
}
