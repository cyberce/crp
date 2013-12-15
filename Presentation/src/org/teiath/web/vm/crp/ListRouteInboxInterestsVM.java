package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ListRouteInboxInterestsService;
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
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ListRouteInboxInterestsVM
		extends BaseVM

{

	static Logger log = Logger.getLogger(ListRoutesVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;

	@WireVariable
	private ListRouteInboxInterestsService listRouteInboxInterestsService;

	private RouteInterestSearchCriteria routeInterestSearchCriteria;
	private ListModelList<RouteInterest> routeInterestsList;
	private ListModelList<Vehicle> vehicles;
	private Vehicle selectedVehicle;
	private RouteInterest selectedRouteInterest;
	private RouteAssessment routeAssessment;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		paging.setPageSize(10);
		routeInterestSearchCriteria.setPageSize(paging.getPageSize());
		routeInterestSearchCriteria.setPageNumber(0);
		routeInterestSearchCriteria.setDriver(loggedUser);
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_PENDING);
		routeInterestSearchCriteria.setOrderField("interestDate");
		routeInterestSearchCriteria.setOrderDirection("descending");

		routeInterestsList = new ListModelList<>();

		try {
			SearchResults<RouteInterest> results = listRouteInboxInterestsService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> routeInterests = results.getData();
			routeInterestsList.addAll(routeInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			if (routeInterestsList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {

		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		String sortField = listheader.getId();
		switch (sortField) {
			case "interestDate":
				routeInterestSearchCriteria.setOrderField("interestDate");
				break;
			case "rating":
				routeInterestSearchCriteria.setOrderField("usr.averageDriverRating");
				break;
		}

		routeInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		routeInterestSearchCriteria.setPageNumber(0);
		routeInterestsList.clear();

		try {
			SearchResults<RouteInterest> results = listRouteInboxInterestsService
					.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
			Collection<RouteInterest> routeInterests = results.getData();
			routeInterestsList.addAll(routeInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedRouteInterest")
	public void onPaging() {
		if (routeInterestsList != null) {
			routeInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<RouteInterest> results = listRouteInboxInterestsService
						.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
				selectedRouteInterest = null;
				routeInterestsList.clear();
				routeInterestsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	@Command
	public void onView() {
		if (selectedRouteInterest != null) {
			ZKSession.setAttribute("routeInterestId", selectedRouteInterest.getId());
			ZKSession.setAttribute("fromSubmenu", true);
			ZKSession.sendRedirect(PageURL.INTEREST_VIEW);
		}
	}

	@Command
	public void onAccept() {

		if (selectedRouteInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.approveQuestion"),
					Labels.getLabel("commmon.messages.approve_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								if (listRouteInboxInterestsService.hasEnoughSeats(selectedRouteInterest.getRoute(), selectedRouteInterest)) {
									routeAssessment = new RouteAssessment();
									routeAssessment.setAssessedRoute(selectedRouteInterest.getRoute());
									routeAssessment.setAssessedType(RouteAssessment.RATING_FOR_PASSENGER);
									routeAssessment.setAssessedUser(selectedRouteInterest.getUser());
									routeAssessment.setAssessmentDate(new Date());
									routeAssessment.setComment("");
									routeAssessment.setUser(selectedRouteInterest.getRoute().getUser());
									listRouteInboxInterestsService.approveRouteInterest(selectedRouteInterest);
									listRouteInboxInterestsService.createRouteAssessement(routeAssessment);
									Messagebox.show("Αποδοχή", Labels.getLabel("common.messages.confirm"), Messagebox.OK,
											Messagebox.INFORMATION, new EventListener<Event>() {
										public void onEvent(Event evt) {
											switch ((Integer) evt.getData()) {
												case Messagebox.OK:
													ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
											}
										}
									});
								} else {
									Messagebox.show("Δεν υπάρχουν ελεύθερες θέσεις", Labels.getLabel("common.messages.save_error"),
											Messagebox.OK, Messagebox.EXCLAMATION, new EventListener<Event>() {
										public void onEvent(Event evt) {
											switch ((Integer) evt.getData()) {
												case Messagebox.OK:
													ZKSession.sendRedirect(PageURL.INCOMING_ROUTE_INTERESTS);
											}
										}
									});
								}
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("reservation")),
										Labels.getLabel("common.messages.save_error"), Messagebox.OK, Messagebox.ERROR);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onReject() {
		if (selectedRouteInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.rejectQuestion"),
					Labels.getLabel("commmon.messages.reject_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listRouteInboxInterestsService.rejectRouteInterest(selectedRouteInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.rejectConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.INCOMING_ROUTE_INTERESTS);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.INCOMING_ROUTE_INTERESTS);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<RouteInterest> results = null;
		try {
			results = listRouteInboxInterestsService.searchRouteInterestsByCriteria(routeInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<RouteInterest> routeInterests = results.getData();

		Report report = ReportToolkit.requestIncomingRouteInterestsReport(routeInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public static Logger getLog() {
		return log;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
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

	public RouteInterestSearchCriteria getRouteInterestSearchCriteria() {
		return routeInterestSearchCriteria;
	}

	public void setRouteInterestSearchCriteria(RouteInterestSearchCriteria routeInterestSearchCriteria) {
		this.routeInterestSearchCriteria = routeInterestSearchCriteria;
	}

	public ListModelList<RouteInterest> getRouteInterestsList() {
		return routeInterestsList;
	}

	public void setRouteInterestsList(ListModelList<RouteInterest> routeInterestsList) {
		this.routeInterestsList = routeInterestsList;
	}

	public RouteInterest getSelectedRouteInterest() {
		return selectedRouteInterest;
	}

	public void setSelectedRouteInterest(RouteInterest selectedRouteInterest) {
		this.selectedRouteInterest = selectedRouteInterest;
	}
}
