package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.domain.crp.RouteNotificationCriteria;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.crp.EditRouteNotificationCriteriaService;
import org.teiath.service.crp.PopularPlaceService;
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
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.StringTokenizer;

@SuppressWarnings("UnusedDeclaration")
public class EditNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditNotificationCriteriaVM.class.getName());

	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;
	@Wire("#appliances")
	private Comboitem appliances;
	@Wire("#electronics")
	private Comboitem electronics;
	@Wire("#furniture")
	private Comboitem furniture;
	@Wire("#books")
	private Comboitem books;
	@Wire("#various")
	private Comboitem various;
	@Wire
	private Textbox startCoordsHolder;
	@Wire
	private Textbox endCoordsHolder;
	@Wire
	private Intbox fromRadius;
	@Wire
	private Intbox toRadius;

	@WireVariable
	private EditRouteNotificationCriteriaService editRouteNotificationCriteriaService;
	@WireVariable
	private PopularPlaceService popularPlaceService;

	private RouteNotificationCriteria routeNotificationCriteria;

	@AfterCompose
	@NotifyChange("routeNotificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		routeNotificationCriteria = new RouteNotificationCriteria();

		try {
			routeNotificationCriteria = editRouteNotificationCriteriaService
					.getRouteNotificationCriteriaById((Integer) ZKSession.getAttribute("notificationCriteriaId"));

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
			Collection<UserPlace> myPlaces = editRouteNotificationCriteriaService.getPlacesByUser(loggedUser);
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

			Clients.evalJavaScript(
					popularPlacesJSArray + myPlacesJSArray + "loadData(prefPlaces, myPlaces, '" + routeNotificationCriteria
							.getFromAddress() + "', '" + routeNotificationCriteria.getToAddress() + "');");
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications.criteria")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_LIST);
		}
	}

	@Command
	public void onSave() {

		try {
			if (routeNotificationCriteria.getMaxAmount() == null) {
				routeNotificationCriteria.setMaxAmount(BigDecimal.ZERO);
			}

			if ((startCoordsHolder.getValue() != null) && (!startCoordsHolder.getText().isEmpty())) {
				StringTokenizer stringTokenizer = new StringTokenizer(startCoordsHolder.getText(), "|");
				if (stringTokenizer.hasMoreTokens()) {
					routeNotificationCriteria
							.setFromCoordinates(stringTokenizer.nextToken().replace("(", "").replace(")", ""));
				}
				if (stringTokenizer.hasMoreTokens()) {
					routeNotificationCriteria.setFromAddress(stringTokenizer.nextToken());
				}
			}

			if ((!endCoordsHolder.getText().isEmpty()) && (endCoordsHolder.getValue() != null)) {
				StringTokenizer stringTokenizer = new StringTokenizer(endCoordsHolder.getText(), "|");
				if (stringTokenizer.hasMoreTokens()) {
					routeNotificationCriteria
							.setToCoordinates(stringTokenizer.nextToken().replace("(", "").replace(")", ""));
				}
				if (stringTokenizer.hasMoreTokens()) {
					routeNotificationCriteria.setToAddress(stringTokenizer.nextToken());
				}
			}

			if (fromRadius.getValue() != null) {
				routeNotificationCriteria.setFromRadius(fromRadius.getValue());
			}

			if (toRadius.getValue() != null) {
				routeNotificationCriteria.setToRadius(toRadius.getValue());
			}
			editRouteNotificationCriteriaService.saveRouteNotificationCriteria(routeNotificationCriteria);
			Messagebox.show(Labels.getLabel("notifications.message.success"),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						public void onEvent(Event evt) {
							ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_LIST);
						}
					});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_LIST);
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
						ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public EditRouteNotificationCriteriaService getEditRouteNotificationCriteriaService() {
		return editRouteNotificationCriteriaService;
	}

	public void setEditRouteNotificationCriteriaService(
			EditRouteNotificationCriteriaService editRouteNotificationCriteriaService) {
		this.editRouteNotificationCriteriaService = editRouteNotificationCriteriaService;
	}

	public RouteNotificationCriteria getRouteNotificationCriteria() {
		return routeNotificationCriteria;
	}

	public void setRouteNotificationCriteria(RouteNotificationCriteria routeNotificationCriteria) {
		this.routeNotificationCriteria = routeNotificationCriteria;
	}
}
