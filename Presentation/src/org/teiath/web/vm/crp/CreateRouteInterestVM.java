package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.service.crp.CreateRouteInterestService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class CreateRouteInterestVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateRouteInterestVM.class.getName());

	@Wire("#routeInquiryWin")
	private Window win;
	@Wire("#extraPassengerNameBox")
	private Textbox extraPassengerNameBox;

	@WireVariable
	private CreateRouteInterestService createRouteInterestService;

	private RouteInterest routeInterest;
	private Route route;
	private ListModelList<ExtraPassenger> extraPassengers;
	private ExtraPassenger selectedPassenger;

	@AfterCompose
	@NotifyChange("routeInterest")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		extraPassengers = new ListModelList<>();
		ExtraPassenger inquirer = new ExtraPassenger();
		inquirer.setName(loggedUser.getFullName());
		extraPassengers.add(inquirer);

		try {
			route = createRouteInterestService.getRouteById((Integer) Executions.getCurrent().getArg().get("ROUTE_ID"));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
		routeInterest = new RouteInterest();
		routeInterest.setUser(loggedUser);
		routeInterest.setRoute(route);
	}

	@Command
	@NotifyChange("extraPassengers")
	public void addExtraPassenger() {
		if ((extraPassengerNameBox.getValue() != null) && (extraPassengerNameBox.getValue() != "")) {
			ExtraPassenger extraPassenger = new ExtraPassenger();
			extraPassenger.setName(extraPassengerNameBox.getValue());
			extraPassengers.add(extraPassenger);
			extraPassengerNameBox.setValue(null);
		}
	}

	@Command
	public void removeExtraPassenger() {
		if (!selectedPassenger.equals(extraPassengers.get(0)))
			extraPassengers.remove(selectedPassenger);
		else {

		}
	}

	@Command
	public void onSubmit() {
		routeInterest.setInterestDate(new Date());
		try {
			routeInterest.setNumberOfPassengers(extraPassengers.getSize());
			createRouteInterestService.saveRouteInterest(routeInterest, extraPassengers);
			if ((route.getTotalSeats() - route.getReservations()) < routeInterest.getNumberOfPassengers()) {
				Messagebox.show(Labels.getLabel("routeInterest.message.notEnoughSeats"),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							public void onEvent(Event evt) {
								win.detach();
							}
						});
			} else {
				Messagebox.show(Labels.getLabel("routeInterest.message.success"),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							public void onEvent(Event evt) {
								win.detach();
							}
						});
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			win.detach();
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
						win.detach();
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public RouteInterest getRouteInterest() {
		return routeInterest;
	}

	public void setRouteInterest(RouteInterest routeInterest) {
		this.routeInterest = routeInterest;
	}

	public ListModelList<ExtraPassenger> getExtraPassengers() {
		return extraPassengers;
	}

	public void setExtraPassengers(ListModelList<ExtraPassenger> extraPassengers) {
		this.extraPassengers = extraPassengers;
	}

	public ExtraPassenger getSelectedPassenger() {
		return selectedPassenger;
	}

	public void setSelectedPassenger(ExtraPassenger selectedPassenger) {
		this.selectedPassenger = selectedPassenger;
	}
}
