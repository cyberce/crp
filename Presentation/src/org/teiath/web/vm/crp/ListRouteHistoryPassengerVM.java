package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
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
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings("UnusedDeclaration")
public class ListRouteHistoryPassengerVM
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
	@Wire("#driverCombobox")
	private Combobox driverCombobox;

	@WireVariable
	private ListRoutesHistoryService listRoutesHistoryService;

	private RouteInterestSearchCriteria routeInterestSearchCriteria;
	private ListModelList<RouteInterest> routeInterestsList;
	private ListModelList<Vehicle> vehiclesList;
	private Vehicle selectedVehicle;
	private ListModelList<User> driverList;
	private User selectedDriver;
	private RouteInterest selectedRouteInterest;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		if (ZKSession.getAttribute("myRouteInterestsSearchCriteria") != null) {
			routeInterestSearchCriteria = (RouteInterestSearchCriteria) ZKSession.getAttribute("myRouteInterestsSearchCriteria");
			ZKSession.removeAttribute("myRouteInterestsSearchCriteria");

			routeInterestsList = new ListModelList<>();
			getVehiclesList();
			getDriverList();
			if (routeInterestSearchCriteria.getVehicle() != null)
				selectedVehicle = routeInterestSearchCriteria.getVehicle();
			if (routeInterestSearchCriteria.getDriver() != null)
				selectedDriver = routeInterestSearchCriteria.getDriver();
			selectedRouteInterest = null;

			try {
				SearchResults<RouteInterest> results = listRoutesHistoryService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				Collection<RouteInterest> routeInterests = results.getData();
				routeInterestsList.addAll(routeInterests);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		} else {
			//Initial search criteria
			routeInterestSearchCriteria = new RouteInterestSearchCriteria();
			paging.setPageSize(10);
			routeInterestSearchCriteria.setPageSize(paging.getPageSize());
			routeInterestSearchCriteria.setPageNumber(0);
			routeInterestSearchCriteria.setPassenger(loggedUser);
			routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_APPROVED);
/*			GregorianCalendar calendar = new GregorianCalendar(2009, 12, 30);
			routeInterestSearchCriteria.setDateFrom(calendar.getTime());
			routeInterestSearchCriteria.setDateTo(new Date());*/

			routeInterestsList = new ListModelList<>();


			try {
				SearchResults<RouteInterest> results = listRoutesHistoryService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				Collection<RouteInterest> routesInterests = results.getData();
				routeInterestsList.addAll(routesInterests);

				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
				if (routeInterestsList.isEmpty()) {
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

		routeInterestSearchCriteria.setOrderField("rout." + listheader.getId());
		routeInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		routeInterestSearchCriteria.setPageNumber(0);
		selectedRouteInterest = null;
		routeInterestsList.clear();

		try {
			SearchResults<RouteInterest> results = listRoutesHistoryService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> routeInterests = results.getData();
			routeInterestsList.addAll(routeInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedRouteInterest")
	public void onPaging() {
		if (routeInterestsList != null) {
			routeInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<RouteInterest> results = listRoutesHistoryService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				selectedRouteInterest = null;
				routeInterestsList.clear();
				routeInterestsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedRouteInterest")
	public void onSearch() {
		selectedRouteInterest = null;
		routeInterestsList.clear();
		routeInterestSearchCriteria.setPageNumber(0);
		routeInterestSearchCriteria.setPageSize(paging.getPageSize());
		routeInterestSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
		routeInterestSearchCriteria.setDriver(selectedDriver.getId() != - 1 ? selectedDriver : null);

		try {
			SearchResults<RouteInterest> results = listRoutesHistoryService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> routeInterests = results.getData();
			routeInterestsList.addAll(routeInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedVehicle", "routeInterestSearchCriteria", "selectedDriver"})
	public void onResetSearch() {
		selectedDriver = driverList.get(0);
		selectedVehicle = vehiclesList.get(0);
		routeInterestSearchCriteria.setDateFrom(null);
		routeInterestSearchCriteria.setDateTo(null);
		routeInterestSearchCriteria.setVehicle(null);
		routeInterestSearchCriteria.setDriver(null);
		routeInterestSearchCriteria.setRouteCode(null);
		dateFrom.setValue(null);
		dateTo.setValue(null);
		vehicleCombobox.setSelectedItem(null);
		driverCombobox.setSelectedItem(null);
		selectedRouteInterest = null;
	}

	@Command
	public void onView() {
		if (selectedRouteInterest != null) {
			ZKSession.setAttribute("historyType", "passenger");
			ZKSession.setAttribute("routeId", selectedRouteInterest.getRoute().getId());
			routeInterestSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			routeInterestSearchCriteria.setDriver(selectedDriver.getId() != - 1 ? selectedDriver : null);
			ZKSession.setAttribute("myRouteInterestsSearchCriteria", routeInterestSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_HISTORY_ROUTE_VIEW);
		}
	}

	@Command
	public void onRate() {
		if (selectedRouteInterest != null) {
			ZKSession.setAttribute("routeId", selectedRouteInterest.getRoute().getId());
			routeInterestSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			routeInterestSearchCriteria.setDriver(selectedDriver.getId() != - 1 ? selectedDriver : null);
			ZKSession.setAttribute("myRouteInterestsSearchCriteria", routeInterestSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_DRIVER_RATE);
		}
	}

	@Command
	public void onReportAbuse() {
		if (selectedRouteInterest != null) {
			ZKSession.setAttribute("routeId", selectedRouteInterest.getRoute().getId());
			routeInterestSearchCriteria.setVehicle(selectedVehicle.getId() != - 1 ? selectedVehicle : null);
			routeInterestSearchCriteria.setDriver(selectedDriver.getId() != - 1 ? selectedDriver : null);
			ZKSession.setAttribute("myRouteInterestsSearchCriteria", routeInterestSearchCriteria);
			ZKSession.sendRedirect(PageURL.ROUTE_REPORT_ABUSE);
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<RouteInterest> results = null;
		try {
			results = listRoutesHistoryService.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<RouteInterest> routeInterests = results.getData();

		Report report = ReportToolkit.requestRouteHistoryPassengerReport(routeInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public ListModelList<RouteInterest> getRouteInterestsList() {
		return routeInterestsList;
	}

	public void setRouteInterestsList(ListModelList<RouteInterest> routeInterestsList) {
		this.routeInterestsList = routeInterestsList;
	}

	public RouteInterestSearchCriteria getRouteInterestSearchCriteria() {
		return routeInterestSearchCriteria;
	}

	public void setRouteInterestSearchCriteria(RouteInterestSearchCriteria routeInterestSearchCriteria) {
		this.routeInterestSearchCriteria = routeInterestSearchCriteria;
	}

	public RouteInterest getSelectedRouteInterest() {
		return selectedRouteInterest;
	}

	public void setSelectedRouteInterest(RouteInterest selectedRouteInterest) {
		this.selectedRouteInterest = selectedRouteInterest;
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
				vehiclesList.addAll(listRoutesHistoryService.getVehiclesByPassenger(loggedUser));
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

	public ListModelList<User> getDriverList() {

		if (driverList == null) {
			driverList = new ListModelList<>();
			selectedDriver = new User();
			selectedDriver.setId(- 1);
			selectedDriver.setFirstName("");
			selectedDriver.setLastName("");
			driverList.add(selectedDriver);
			try {
				Collection<User> drivers = listRoutesHistoryService.getDriversByPassenger(loggedUser);
				driverList.addAll(drivers);
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicle")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return driverList;
	}

	public void setDriverList(ListModelList<User> driverList) {
		this.driverList = driverList;
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

	public User getSelectedDriver() {
		return selectedDriver;
	}

	public void setSelectedDriver(User selectedDriver) {
		this.selectedDriver = selectedDriver;
	}
}
