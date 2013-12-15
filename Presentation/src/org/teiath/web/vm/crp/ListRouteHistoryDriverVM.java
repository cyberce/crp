package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.ListRoutesHistoryService;
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
public class ListRouteHistoryDriverVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListRouteHistoryPassengerVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;
	@Wire("#vehicleCombobox")
	private Combobox vehicleCombobox;
	@Wire("#driverListbox")
	private Listbox driverListbox;

	@WireVariable
	private ListRoutesHistoryService listRoutesHistoryService;

	private RouteSearchCriteria routeSearchCriteria;
	private ListModelList<Route> routeList;
	private ListModelList<Vehicle> vehiclesList;
	private Vehicle selectedVehicle;
	private Route selectedRoute;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		if (ZKSession.getAttribute("myRoutesSearchCriteria") != null) {
			routeSearchCriteria = (RouteSearchCriteria) ZKSession.getAttribute("myRoutesSearchCriteria");
			ZKSession.removeAttribute("myRoutesSearchCriteria");

			routeList = new ListModelList<>();
			getVehiclesList();
			if (routeSearchCriteria.getVehicle() != null)
				selectedVehicle = routeSearchCriteria.getVehicle();
			selectedRoute = null;

			try {
				SearchResults<Route> results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
				Collection<Route> routes = results.getData();
				routeList.addAll(routes);
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
			paging.setPageSize(10);
			routeSearchCriteria.setPageSize(paging.getPageSize());
			routeSearchCriteria.setPageNumber(0);
			routeSearchCriteria.setUser(loggedUser);
			routeSearchCriteria.setCompleted(true);

			routeList = new ListModelList<>();

			try {
				SearchResults<Route> results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
				Collection<Route> routes = results.getData();
				routeList.addAll(routes);

				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeSearchCriteria.getPageNumber());
				if (routeList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
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
		routeList.clear();

		try {
			SearchResults<Route> results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
			Collection<Route> routes = results.getData();
			routeList.addAll(routes);
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
		if (routeList != null) {
			routeSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Route> results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
				selectedRoute = null;
				routeList.clear();
				routeList.addAll(results.getData());
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
		routeList.clear();
		routeSearchCriteria.setPageNumber(0);
		routeSearchCriteria.setPageSize(paging.getPageSize());
		routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
		try {
			SearchResults<Route> results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
			Collection<Route> routes = results.getData();
			routeList.addAll(routes);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("routeSearchCriteria")
	public void onResetSearch() {
		routeSearchCriteria.setDateFrom(null);
		routeSearchCriteria.setDateTo(null);
		routeSearchCriteria.setVehicle(null);
		selectedVehicle = vehiclesList.get(0);
		dateFrom.setValue(null);
		dateTo.setValue(null);
		vehicleCombobox.setSelectedItem(null);
		selectedRoute = null;
		routeSearchCriteria.setPageNumber(0);
		routeSearchCriteria.setPageSize(paging.getPageSize());
		routeSearchCriteria.setCode(null);
	}

	@Command
	public void onView() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("historyType", "driver");
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_ROUTE_VIEW);
		}
	}

	@Command
	public void onRate() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_PASSENGER_RATE);
		}
	}

	@Command
	public void onReportAbuse() {
		if (selectedRoute != null) {
			ZKSession.setAttribute("routeId", selectedRoute.getId());
			routeSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			ZKSession.setAttribute("myRoutesSearchCriteria", routeSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_REPORT_ABUSE);
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Route> results = null;
		try {
			results = listRoutesHistoryService.searchRoutesByCriteria(routeSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<Route> routes = results.getData();

		Report report = ReportToolkit.requestRouteHistoryDriverReport(routes, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public RouteSearchCriteria getRouteSearchCriteria() {
		return routeSearchCriteria;
	}

	public void setRouteSearchCriteria(RouteSearchCriteria routeSearchCriteria) {
		this.routeSearchCriteria = routeSearchCriteria;
	}

	public ListModelList<Route> getRouteList() {
		return routeList;
	}

	public void setRouteList(ListModelList<Route> routeList) {
		this.routeList = routeList;
	}

	public Route getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(Route selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
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
				vehiclesList.addAll(listRoutesHistoryService.getVehiclesByDriver(loggedUser));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicle")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return vehiclesList;
	}

	public void setVehiclesList(ListModelList<Vehicle> vehiclesList) {
		this.vehiclesList = vehiclesList;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}

	public void setSelectedVehicle(Vehicle selectedVehicle) {
		this.selectedVehicle = selectedVehicle;
	}
}
