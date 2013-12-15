package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.crp.CreateRouteAssessmentService;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class DriverRateVM
		extends BaseVM {

	static Logger log = Logger.getLogger(DriverRateVM.class.getName());

	@WireVariable
	private CreateRouteAssessmentService createRouteAssessmentService;

	@Wire
	private Intbox rating;

	private Route route;
	private RouteAssessment routeAssessment;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		route = new Route();
		route.setUser(loggedUser);
		routeAssessment = new RouteAssessment();

		try {
			route = createRouteAssessmentService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
		}

		try {
			routeAssessment = createRouteAssessmentService.getDriversRating(route, loggedUser);

			if (routeAssessment != null) {
				rating.setValue(routeAssessment.getRating());
				Clients.evalJavaScript("doEdit('" + routeAssessment.getRating() + "')");
			}

			if (routeAssessment == null) {
				routeAssessment = new RouteAssessment();
				rating.setValue(0);
				Clients.evalJavaScript("doLoad()");
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
		}

		if (routeAssessment == null) {
			routeAssessment = new RouteAssessment();
		}

		try {
			route = createRouteAssessmentService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
		}
	}

	@Command
	public void onSave() {

		routeAssessment.setAssessedType(RouteAssessment.RATING_FOR_DRIVER);
		routeAssessment.setAssessmentDate(new Date());
		routeAssessment.setUser(loggedUser);
		routeAssessment.setAssessedUser(route.getUser());
		routeAssessment.setAssessedRoute(route);
		routeAssessment.setRating(rating.getValue());
		try {
			createRouteAssessmentService.saveAssessment(routeAssessment);
			Messagebox.show(Labels.getLabel("route.rating.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
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
						ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_PASSENGER_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public RouteAssessment getRouteAssessment() {
		return routeAssessment;
	}

	public void setRouteAssessment(RouteAssessment routeAssessment) {
		this.routeAssessment = routeAssessment;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
