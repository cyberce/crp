package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAssessmentSearchCriteria;
import org.teiath.service.crp.CreatePassengersRouteAssessmentService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.crp.validator.RouteAssessmentsValidator;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class PassengersRateVM
		extends BaseVM {

	static Logger log = Logger.getLogger(PassengersRateVM.class.getName());

	@WireVariable
	private CreatePassengersRouteAssessmentService createPassengersRouteAssessmentService;
	@Wire("#empty")
	private Label empty;
	@Wire("#usersListbox")
	private Listbox usersListbox;
	@Wire("#passsengersRateWin")
	private Window win;

	private Route route;
	private Validator routeAssessmentsValidator;
	private RouteAssessmentSearchCriteria routeAssessmentSearchCriteria = new RouteAssessmentSearchCriteria();
	private ListModelList<RouteAssessment> assessmentsList;
	private RouteAssessment selectedRouteAssessment;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		routeAssessmentsValidator = new RouteAssessmentsValidator();
		route = new Route();
		route.setUser(loggedUser);

		try {
			route = createPassengersRouteAssessmentService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
		}

		routeAssessmentSearchCriteria = new RouteAssessmentSearchCriteria();
		routeAssessmentSearchCriteria.setOrderField("id");
		routeAssessmentSearchCriteria.setOrderDirection("asc");
		routeAssessmentSearchCriteria.setRoute(route);
		routeAssessmentSearchCriteria.setAssessedType(RouteAssessment.RATING_FOR_PASSENGER);
		assessmentsList = new ListModelList<>();
		try {
			SearchResults<RouteAssessment> results = createPassengersRouteAssessmentService
					.searchRouteAssessmentsByCriteria(routeAssessmentSearchCriteria);
			Collection<RouteAssessment> assessments = results.getData();

			assessmentsList.addAll(assessments);

			for (RouteAssessment routeAssessment : assessmentsList) {
				Clients.evalJavaScript(
						"doLoad('" + routeAssessment.getRatingId() + "', '" + routeAssessment.getRating() + "')");
			}

			if (assessmentsList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_DRIVER_LIST);
	}

	@Command
	public void onOpenPassengerRatePopup() {
		if (selectedRouteAssessment != null) {
			Map params = new HashMap();
			params.put("ROUTE_ASSESSMENT", selectedRouteAssessment);
			Executions.createComponents("/zul/crp/passenger_rate_popup.zul", win, params);
		}
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Validator getRouteAssessmentsValidator() {
		return routeAssessmentsValidator;
	}

	public void setRouteAssessmentsValidator(Validator routeAssessmentsValidator) {
		this.routeAssessmentsValidator = routeAssessmentsValidator;
	}

	public ListModelList<RouteAssessment> getAssessmentsList() {
		return assessmentsList;
	}

	public void setAssessmentsList(ListModelList<RouteAssessment> assessmentsList) {
		this.assessmentsList = assessmentsList;
	}

	public RouteAssessment getSelectedRouteAssessment() {
		return selectedRouteAssessment;
	}

	public void setSelectedRouteAssessment(RouteAssessment selectedRouteAssessment) {
		this.selectedRouteAssessment = selectedRouteAssessment;
	}
}
