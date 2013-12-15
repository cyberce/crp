package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.ListRoutesService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
import org.teiath.web.facebook.crp.FacebookToolKitRoutes;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ListRoutesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListRoutesVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#interests")
	private Listheader interests;
	@Wire("#reservations")
	private Listheader reservations;
	@Wire("#editButton")
	private Toolbarbutton editButton;
	@Wire("#deleteButton")
	private Toolbarbutton deleteButton;
	@Wire("#shareButton")
	private Toolbarbutton shareButton;
	@Wire("#reservationsButton")
	private Toolbarbutton reservationsButton;
	@Wire("#interestsButton")
	private Toolbarbutton interestsButton;

	@WireVariable
	private ListRoutesService listRoutesService;
	@WireVariable
	private EditUserService editUserService;

	private RouteSearchCriteria routeSearchCriteria;
	private ListModelList<Route> routesList;
	private ListModelList<Vehicle> vehiclesList;
	private Vehicle selectedVehicle;
	private Route selectedRoute;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {
			interests.setLabel(null);
			interests.setLabel("Συνεπιβάτες");
		}

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {
			reservations.setLabel(null);
			reservations.setLabel("Κρατήσεις");
		}

		if (ZKSession.getAttribute("myRoutesSearchCriteria") != null) {
			routeSearchCriteria = (RouteSearchCriteria) ZKSession.getAttribute("myRoutesSearchCriteria");
			ZKSession.removeAttribute("myRoutesSearchCriteria");

			routesList = new ListModelList<>();
			getVehiclesList();
			if (routeSearchCriteria.getVehicle() != null)
				selectedVehicle = routeSearchCriteria.getVehicle();
			selectedRoute = null;

			try {
				SearchResults<Route> results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
				Collection<Route> routes = results.getData();
				routesList.addAll(routes);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		} else {
			//Initial search criteria
			routeSearchCriteria = new RouteSearchCriteria();
			routeSearchCriteria.setPageSize(paging.getPageSize());
			routeSearchCriteria.setPageNumber(0);
			routeSearchCriteria.setUser(loggedUser);
			routeSearchCriteria.setRecurring(- 1);
			routeSearchCriteria.setStatus(- 1);
			routeSearchCriteria.setOrderField("routeCreationDate");
			routeSearchCriteria.setOrderDirection("descending");


			routesList = new ListModelList<>();

			try {
				SearchResults<Route> results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
				Collection<Route> routes = results.getData();
				routesList.addAll(routes);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeSearchCriteria.getPageNumber());
				if (routesList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		if (!loggedUser.isTermsViewed()) {
			Window window = (Window) Executions.createComponents("/zul/crp/terms/termsLoginPopup.zul", null, null);
			window.doModal();
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		routeSearchCriteria.setOrderField(listheader.getId());
		routeSearchCriteria.setOrderDirection(listheader.getSortDirection());
		selectedRoute = null;
		routesList.clear();

		try {
			SearchResults<Route> results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
			Collection<Route> routes = results.getData();
			routesList.addAll(routes);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedRoute")
	public void onPaging() {
		if (routesList != null) {
			routeSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Route> results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
				selectedRoute = null;
				routesList.clear();
				routesList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	@Command
	@NotifyChange("selectedRoute")
	public void onSearch() {
		selectedRoute = null;
		routesList.clear();
		routeSearchCriteria.setPageNumber(0);
		routeSearchCriteria.setPageSize(paging.getPageSize());
		//routeSearchCriteria.setVehicle(selectedVehicle);
		routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);

		try {
			SearchResults<Route> results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
			Collection<Route> routes = results.getData();
			routesList.addAll(routes);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange({"selectedVehicle", "routeSearchCriteria"})
	public void onResetSearch() {
		selectedVehicle = vehiclesList.get(0);
		routeSearchCriteria.setRecurring(- 1);
		routeSearchCriteria.setStatus(- 1);
		routeSearchCriteria.setCode(null);
	}

	@Command
	public void onView() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_VIEW);
		}
	}

	@Command
	public void onReservations() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_RESERVATIONS);
		}
	}

	@Command
	public void onInterests() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	@Command
	public void onEdit() {
		if (loggedUser.isLicensed()) {
			if (selectedRoute != null) {
				ZKSession.setAttribute("routeId", selectedRoute.getId());
				routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
				ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
				ZKSession.sendRedirect(PageURL.ROUTE_EDIT);
			}
		} else {
			Messagebox.show(MessageBuilder
					.buildErrorMessage(Labels.getLabel("route.notLicensed"), Labels.getLabel("route")),
					Labels.getLabel("route.edit"), Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@Command
	public void onDelete() {
		if (selectedRoute != null) {
			Messagebox.show(Labels.getLabel("route.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listRoutesService.deleteRoute(selectedRoute);
								listRoutesService.sendDeleteNotifications(selectedRoute);
								listRoutesService.createUserAction(selectedRoute);
								Messagebox.show(Labels.getLabel("route.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
										ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
										ZKSession.sendRedirect(PageURL.ROUTE_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								log.error(e.getMessage());
								routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
								ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
								ZKSession.sendRedirect(PageURL.ROUTE_LIST);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onCreate() {
		if (loggedUser.isLicensed()) {
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_CREATE);
		} else {
			Messagebox.show(MessageBuilder
					.buildErrorMessage(Labels.getLabel("route.notLicensed"), Labels.getLabel("route")),
					Labels.getLabel("route.create"), Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@Command
	public void facebookShare() {
		if (selectedRoute != null) {
			//stelnoume to route id ws state wste na mas stalei pisw apo to facebook
			ZKSession.fireNewWindow(FacebookToolKitRoutes.getLoginRedirectURL() + "&state=" + selectedRoute.getId());
			//ZKSession.sendPureRedirect(FacebookToolKitRoutes.getLoginRedirectURL() + "&state=" + selectedRoute.getId());
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Route> results = null;
		routeSearchCriteria.setPageNumber(0);
		routeSearchCriteria.setPageSize(0);
		try {
			results = listRoutesService.searchRoutesByCriteria(routeSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<Route> routes = results.getData();

		Report report = ReportToolkit.requestRoutesReport(routes, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	@GlobalCommand
	public void acceptTerms() {
		loggedUser.setTermsViewed(true);
		try {
			editUserService.saveUser(loggedUser);
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		} catch (ServiceException e) {
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedRoute")
	public void onSelectRoute() {
		if (!selectedRoute.isRecurring()) {
			if (selectedRoute.getRouteDate().before(new Date())) {
				editButton.setVisible(false);
				shareButton.setVisible(false);
				deleteButton.setVisible(false);
				reservationsButton.setVisible(false);
				interestsButton.setVisible(false);
			} else {
				editButton.setVisible(true);
				shareButton.setVisible(true);
				deleteButton.setVisible(true);
				reservationsButton.setVisible(true);
				interestsButton.setVisible(true);
			}
		} else if (selectedRoute.isRecurring()) {
			if (selectedRoute.getRouteEndDate() != null) {
				if (selectedRoute.getRouteEndDate().before(new Date())) {
					editButton.setVisible(false);
					shareButton.setVisible(false);
					deleteButton.setVisible(false);
					reservationsButton.setVisible(false);
					interestsButton.setVisible(false);
				}
				else {
					editButton.setVisible(true);
					shareButton.setVisible(true);
					deleteButton.setVisible(true);
					reservationsButton.setVisible(true);
					interestsButton.setVisible(true);
				}
			}
			else {
				editButton.setVisible(true);
				deleteButton.setVisible(true);
				shareButton.setVisible(true);
				reservationsButton.setVisible(true);
				interestsButton.setVisible(true);
			}
		}
		else {
			editButton.setVisible(true);
			shareButton.setVisible(true);
			deleteButton.setVisible(true);
			reservationsButton.setVisible(true);
			interestsButton.setVisible(true);
		}
	}

	public ListModelList<Vehicle> getVehiclesList() {
		if (vehiclesList == null) {
			vehiclesList = new ListModelList<>();
			selectedVehicle = new Vehicle();
			selectedVehicle.setId(- 1);
			selectedVehicle.setBrand("");
			selectedVehicle.setModel("");
			vehiclesList.add(selectedVehicle);
			try {
				vehiclesList.addAll(listRoutesService.getVehiclesByUser(loggedUser));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicle")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return vehiclesList;
	}

	public ListModelList<Route> getRoutesList() {
		return routesList;
	}

	public void setRoutesList(ListModelList<Route> routesList) {
		this.routesList = routesList;
	}

	public Route getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(Route selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public RouteSearchCriteria getRouteSearchCriteria() {
		return routeSearchCriteria;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public void setRouteSearchCriteria(RouteSearchCriteria routeSearchCriteria) {
		this.routeSearchCriteria = routeSearchCriteria;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}

	public void setSelectedVehicle(Vehicle selectedVehicle) {
		this.selectedVehicle = selectedVehicle;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}
}
