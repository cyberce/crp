package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.crp.CreateRouteService;
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
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class CreateRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateRouteVM.class.getName());

	@Wire("#daysGB")
	Groupbox popup;
	@Wire("#days")
	Textbox days;
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
	private CreateRouteService createRouteService;
	@WireVariable
	private PopularPlaceService popularPlaceService;

	private Validator routeValidator;
	private Route route;
	private ListModelList<Vehicle> vehiclesList;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		routeValidator = new RouteValidator();
		route = new Route();
		route.setUser(loggedUser);
		route.setObjectTransportAllowed(true);
		route.setAmeaAccessible(true);
		route.setEnabled(true);

		vehiclesList = new ListModelList<>();
		try {
			vehiclesList.addAll(createRouteService.getVehiclesByUser(loggedUser));
			if (vehiclesList.isEmpty()) {
				Messagebox.show(Labels.getLabel("route.nullVehicle"), Labels.getLabel("common.messages.update"),
						Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
					public void onEvent(Event evt) {
					}
				});
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}

		try {
			// Send popular places array to design
			Collection<PopularPlace> popularPlaces = popularPlaceService.fetchAll();
			String popularPlacesJSArray = "";
			if (! popularPlaces.isEmpty()) {
				popularPlacesJSArray = "var prefPlaces = new Array(" + popularPlaces.size() + ");";
				int index = 0;
				for (PopularPlace popularPlace : popularPlaces) {
					popularPlacesJSArray += "prefPlaces[" + index + "] = new Array(2);";
					popularPlacesJSArray += "prefPlaces[" + index + "][0] = '" + popularPlace.getName() + "';";
					popularPlacesJSArray += "prefPlaces[" + index + "][1] = '" + popularPlace.getAddress() + "';";
					index++;
				}
			}

			// Send my places array to design
			Collection<UserPlace> myPlaces = createRouteService.getPlacesByUser(loggedUser);
			String myPlacesJSArray = "";
			if (! myPlaces.isEmpty()) {
				myPlacesJSArray = "var myPlaces = new Array(" + myPlaces.size() + ");";
				int index = 0;
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

		route.materializeGeometry(((Textbox) event.getTarget()).getValue(), routeSegmentAddressesHolder.getValue(), routePathHolder.getValue());

		if (route.getContributionAmount() == null) {
			route.setContributionAmount(BigDecimal.ZERO);
		}

		if (route.isRecurring()) {
			route.setDays(days.getValue());
		} else {
			route.setRouteEndDate(route.getRouteDate());
		}

		route.setStartingPoint(startingPoint.getText());
		route.setDestinationPoint(destinationPoint.getText());

		try {
			createRouteService.saveRoute(route);
			Messagebox.show(Labels.getLabel("route.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.ROUTE_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
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
