package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.*;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.CreateRouteService;
import org.teiath.service.crp.PopularPlaceService;
import org.teiath.service.crp.SearchRoutesService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.HexColorBuilder;
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
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class SearchRoutesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(SearchRoutesVM.class.getName());

	@Wire("#routeSearchWin")
	private Window win;
	@Wire("#routesListbox")
	private Listbox routesListbox;
	@Wire("#availabilityRG")
	private Radiogroup availabilityRG;
	@Wire("#toolbar")
	private Hbox toolbar;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;
	@Wire("#timeFrom")
	private Timebox timeFrom;
	@Wire("#timeTo")
	private Timebox timeTo;
	@Wire("#peopleNumberIntbox")
	private Intbox peopleNumberIntbox;
	@Wire("#objectAllowedRG")
	private Radiogroup objectAllowedRG;
	@Wire("#contributionAmountIntbox")
	private Intbox contributionAmountIntbox;
	@Wire("#smokingAllowedRG")
	private Radiogroup smokingAllowedRG;
	@Wire("#tags")
	private Textbox tags;
	@Wire("#peopleNumberDiv")
	private Div peopleNumberDiv;
	@Wire("#searchPoint")
	private Label searchPoint;
	@Wire("#searchRadius")
	private Label searchRadius;
	@Wire("#inquiryButton")
	private Toolbarbutton inquiryButton;
	@Wire("#startCoordsHolder")
	private Textbox startCoordsHolder;
	@Wire("#endCoordsHolder")
	private Textbox endCoordsHolder;
	@Wire("#fromRadius")
	private Intbox fromRadius;
	@Wire("#toRadius")
	private Intbox toRadius;
	@Wire("#orderBy")
	private Listbox orderBy;
	@Wire("#viewAllbtn")
	private Button viewAllbtn;
	@Wire("#pageSize")
	private Intbox pageSize;
	@Wire("#startError")
	private Label startError;
	@Wire("#endError")
	private Label endError;
	@Wire("#fromRadiusError")
	private Label fromRadiusError;
	@Wire("#toRadiusError")
	private Label toRadiusError;
	@Wire("#fromDateError")
	private Label fromDateError;
	@Wire("#toDateError")
	private Label toDateError;

	@WireVariable
	private SearchRoutesService searchRoutesService;
	@WireVariable
	private PopularPlaceService popularPlaceService;
	@WireVariable
	private CreateRouteService createRouteService;

	private RouteSearchCriteria routeSearchCriteria;
	private ListModelList<Route> routesList;
	private Route selectedRoute;
	private String geometryValue;
	SearchResults<Route> results;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		routeSearchCriteria = new RouteSearchCriteria();
		routeSearchCriteria.setSmokingAllowed(- 1);
		routeSearchCriteria.setAmeaAccessible(false);
		routeSearchCriteria.setEnabled(true);
		routeSearchCriteria.setUserType(- 1);
		routeSearchCriteria.setCode("");
		routesList = new ListModelList<>();

		routesListbox.getPagingChild().setMold("os");
		routesListbox.getPagingChild().setAutohide(false);
		routesListbox.getPagingChild().setDetailed(true);
		routesListbox.setPageSize(pageSize.getValue());

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
	@NotifyChange("selectedRoute")
	public void onSearch(
			@ContextParam(ContextType.TRIGGER_EVENT)
			InputEvent event) {
		boolean hasError = false;

		if ((startCoordsHolder.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			startError.setValue(Labels.getLabel("validation.common.emptyDates"));
			startError.setVisible(true);
			hasError = true;
		} else {
			startError.setVisible(false);
		}

		if ((fromRadius.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			fromRadiusError.setValue(Labels.getLabel("validation.common.emptyDates"));
			fromRadiusError.setVisible(true);
			hasError = true;
		} else {
			fromRadiusError.setVisible(false);
		}

		if ((endCoordsHolder.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			endError.setValue(Labels.getLabel("validation.common.emptyDates"));
			endError.setVisible(true);
			hasError = true;
		} else {
			endError.setVisible(false);
		}

		if ((toRadius.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			toRadiusError.setValue(Labels.getLabel("validation.common.emptyDates"));
			toRadiusError.setVisible(true);
			hasError = true;
		} else {
			toRadiusError.setVisible(false);
		}

		if ((dateFrom.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			fromDateError.setValue(Labels.getLabel("validation.common.emptyDates"));
			fromDateError.setVisible(true);
			hasError = true;
		} else {
			fromDateError.setVisible(false);
		}

		if ((dateTo.getText().isEmpty()) && (routeSearchCriteria.getCode().equals(""))) {
			toDateError.setValue(Labels.getLabel("validation.common.emptyDates"));
			toDateError.setVisible(true);
			hasError = true;
		} else {
			toDateError.setVisible(false);
		}

		if ((routeSearchCriteria.getDateTo() != null) & (routeSearchCriteria.getDateFrom() != null)) {
			if ((routeSearchCriteria.getDateTo().before(routeSearchCriteria.getDateFrom()))) {
				toDateError.setValue(Labels.getLabel("validation.common.invalidDate"));
				toDateError.setVisible(true);
				hasError = true;
			} else {
				toDateError.setVisible(false);
			}
		}

		if (! hasError) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

			routesListbox.setPageSize(pageSize.getValue());

			String start = startCoordsHolder.getValue();
			String end = endCoordsHolder.getValue();

			System.out.println(start);
			System.out.println(end);

			selectedRoute = null;
			routesList.clear();
			if (! start.isEmpty() && ! end.isEmpty()) {
				routeSearchCriteria.materializeGeometry(start, end);
			}
			if (routeSearchCriteria.getCode().equals("")) {
				routeSearchCriteria.setSearchRadiusFrom(fromRadius.getValue());
				routeSearchCriteria.setSearchRadiusTo(toRadius.getValue());
				routeSearchCriteria.setSortOrder(Integer.parseInt(orderBy.getSelectedItem().getValue().toString()));
			}

			if (loggedUser == null) {
				inquiryButton.setVisible(false);
			}

			try {
				results = searchRoutesService.searchRoutes(routeSearchCriteria, true);
				for (Route route : results.getData()) {
					if (loggedUser != null) {

						if (route.getUser().getId() != loggedUser.getId()) {
							if (!route.getRouteInterests().isEmpty()) {
								for (RouteInterest routeInterest : route.getRouteInterests()) {
									if (routeInterest.getUser().getId() == loggedUser.getId())
										route.setInquiredByUser(true);
									else
										route.setInquiredByUser(false);

								}
							}else {
								route.setInquiredByUser(false);
							}
						} else {
							route.setInquiredByUser(true);
						}
					}
				}

				Collection<Route> routes = results.getData();
				Map colorMap = HexColorBuilder.hexCodeGenerator(results.getData().size());

				if (results.getTotalRecords() != 0) {
					routesList.addAll(routes);
					viewAllbtn.setVisible(true);
					String path = "";
					String colors = "";
					String points = "";
					String addresses = "";
					String routeData = "";
					RouteSegment routeSegment;

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

					List<RouteSegment> routeSegments;
					int index = 0;
					for (Route route : routes) {
						colors += colorMap.get(index) + "*";

						routeSegments = new ArrayList<>(route.getRouteSegments());
						Collections.sort(routeSegments, comparator);

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
						path += "*";
						points = points.substring(0, points.length() - 1);
						points += "*";
						addresses = addresses.substring(0, addresses.length() - 1);
						addresses += "*";

						routeData += route.getId() + "~<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Ημερομηνία: </label>" + dateFormat.format(route.getRouteDate()) + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Ώρα: </label>" + timeFormat.format(route.getRouteTime()) + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Κωδικός Διαδρομής: </label>" + route.getCode() + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Αφετηρία: </label>" + route.getStartingPoint() + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Προορισμός: </label>" + route.getDestinationPoint() + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Όχημα: </label>" + route.getVehicle().getFullName() + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Οδηγός: </label>" + route.getUser().getFullName() + "</div>" +
								"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Διαθέσιμες θέσεις: </label>" + route.getTotalSeats() + "</div>" + "#";


						index++;
					}
					path = path.substring(0, path.length() - 1);
					points = points.substring(0, points.length() - 1);
					addresses = addresses.substring(0, addresses.length() - 1);
					colors = colors.substring(0, colors.length() - 1);
					routeData = routeData.substring(0, routeData.length() - 1);



					Clients.evalJavaScript("doPreview('" + path + "', '" + colors + "', '" + points + "', '" + addresses + "', '" + routeData + "');");
					routesListbox.setVisible(true);
					toolbar.setVisible(true);
				} else {
					Clients.evalJavaScript("doClearResults();");
					routesListbox.setVisible(false);
					toolbar.setVisible(false);
					viewAllbtn.setVisible(false);
					Messagebox.show(MessageBuilder
							.buildErrorMessage(Labels.getLabel("route.notFound"), Labels.getLabel("route")),
							Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("routeSearchCriteria")
	public void onResetSearch() {
		startError.setVisible(false);
		endError.setVisible(false);
		fromRadiusError.setVisible(false);
		toRadiusError.setVisible(false);
		fromDateError.setVisible(false);
		toDateError.setVisible(false);

		dateFrom.setRawValue(null);
		dateTo.setRawValue(null);
		timeFrom.setRawValue(null);
		timeTo.setRawValue(null);
		fromRadius.setRawValue(null);
		toRadius.setRawValue(null);
		startCoordsHolder.setRawValue(null);
		endCoordsHolder.setRawValue(null);
		availabilityRG.setSelectedItem(null);
		peopleNumberIntbox.setRawValue(null);
		objectAllowedRG.setSelectedItem(null);
		routeSearchCriteria.setMaxAmount(null);
		routeSearchCriteria.setSmokingAllowed(- 1);
		routeSearchCriteria.setCode("");
		tags.setValue("");
		peopleNumberDiv.setVisible(false);
		pageSize.setRawValue(10);
		orderBy.setSelectedIndex(0);
		Clients.evalJavaScript("doClearAll();");
		routesList.clear();
		routesListbox.setVisible(false);
		toolbar.setVisible(false);
		viewAllbtn.setVisible(false);
		routeSearchCriteria.setUserType(- 1);
	}

	@Command
	public void availabilityEnabled() {
		peopleNumberDiv.setVisible(true);
	}

	@Command
	@NotifyChange("selectedRoute")
	public void onDisplayAll() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		Collection<Route> routes = results.getData();

		if (results.getTotalRecords() != 0) {
			String path = "";
			String colors = "";
			String points = "";
			String addresses = "";
			String routeData = "";
			RouteSegment routeSegment;

			Map colorMap = HexColorBuilder.hexCodeGenerator(results.getData().size());

			routesList.addAll(routes);
			viewAllbtn.setVisible(true);

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

			List<RouteSegment> routeSegments;
			int index = 0;
			for (Route route : routes) {
				colors += colorMap.get(index) + "*";

				routeSegments = new ArrayList<>(route.getRouteSegments());
				Collections.sort(routeSegments, comparator);

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
				path += "*";
				points = points.substring(0, points.length() - 1);
				points += "*";
				addresses = addresses.substring(0, addresses.length() - 1);
				addresses += "*";

				routeData += route.getId() + "~<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Ημερομηνία: </label>" + dateFormat.format(route.getRouteDate()) + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Ώρα: </label>" + timeFormat.format(route.getRouteTime()) + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Κωδικός Διαδρομής: </label>" + route.getCode() + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Αφετηρία: </label>" + route.getStartingPoint() + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Προορισμός: </label>" + route.getDestinationPoint() + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Όχημα: </label>" + route.getVehicle().getFullName() + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Οδηγός: </label>" + route.getUser().getFullName() + "</div>" +
						"<div style=\"padding: 2px\"><label style=\"font-weight: bold;\">Διαθέσιμες θέσεις: </label>" + route.getTotalSeats() + "</div>" + "#";


				index++;
			}
			path = path.substring(0, path.length() - 1);
			points = points.substring(0, points.length() - 1);
			addresses = addresses.substring(0, addresses.length() - 1);
			colors = colors.substring(0, colors.length() - 1);
			routeData = routeData.substring(0, routeData.length() - 1);

			Clients.evalJavaScript("doPreview('" + path + "', '" + colors + "', '" + points + "', '" + addresses + "', '" + routeData + "');");
		}
	}

	@Command
	@NotifyChange("routeSearchCriteria")
	public void availabilityDisabled() {
		peopleNumberDiv.setVisible(false);
		routeSearchCriteria.setPeopleNumber(null);
	}

	@Command
	public void onView() {
		if (selectedRoute != null) {
			Map<String, Object> params = new HashMap();
			params.put("ROUTE_ID", selectedRoute.getId());
			Executions.createComponents(PageURL.SEARCH_ROUTE_VIEW, win, params);
		}
	}

	@Command
	public void onClickRoute(
			@ContextParam(ContextType.TRIGGER_EVENT)
			InputEvent event) {
		String routeId = ((Textbox) event.getTarget()).getValue();

		Map<String, Object> params = new HashMap();
		params.put("ROUTE_ID", Integer.parseInt(routeId));
		Executions.createComponents(PageURL.SEARCH_ROUTE_VIEW, win, params);
	}

	@Command
	public void onInquiry() {
		if (selectedRoute != null) {
			if (selectedRoute.getTotalSeatsTaken() == selectedRoute.getTotalSeats()) {
				Messagebox
						.show(Labels.getLabel("route.noSeatsWarning"), Labels.getLabel("common.messages.inquiry_title"),
								Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
							public void onEvent(Event evt) {
								switch ((Integer) evt.getData()) {
									case Messagebox.YES:
										Map<String, Object> params = new HashMap();
										params.put("ROUTE_ID", selectedRoute.getId());
										Executions.createComponents(PageURL.ROUTE_INQUIRY, win, params);
										break;
									case Messagebox.NO:
										break;
								}
							}
						});
			} else {
				Map<String, Object> params = new HashMap();
				params.put("ROUTE_ID", selectedRoute.getId());
				Executions.createComponents(PageURL.ROUTE_INQUIRY, win, params);
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
		routeSearchCriteria.setPageNumber(0);
		selectedRoute = null;
		routesList.clear();

		try {
			SearchResults<Route> results = searchRoutesService
					.searchRoutes(routeSearchCriteria, ! geometryValue.isEmpty());
			Collection<Route> routes = results.getData();
			routesList.addAll(routes);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onRouteSelect() {
		if (selectedRoute != null) {
			Clients.evalJavaScript("doBoundMap(" + selectedRoute.getId() + ");");
		}
		if (loggedUser != null) {

			if (selectedRoute.getUser().getId() != loggedUser.getId()) {
				if (!selectedRoute.getRouteInterests().isEmpty()) {
					for (RouteInterest routeInterest : selectedRoute.getRouteInterests()) {
						if (routeInterest.getUser().getId() == loggedUser.getId())
							inquiryButton.setDisabled(true);
						else
							inquiryButton.setDisabled(false);

					}
				}else {
					inquiryButton.setDisabled(false);
				}
			} else {
				inquiryButton.setDisabled(true);
			}
		}


	}

	@Command
	public void onPrintPDF() {
		SearchResults<Route> results = null;
		routeSearchCriteria.setPageNumber(0);
		routeSearchCriteria.setPageSize(0);
		try {
			results = searchRoutesService.searchRoutes(routeSearchCriteria, false);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<Route> routes = results.getData();

		Report report = ReportToolkit.requestRoutesReport(routes, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public Route getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(Route selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public ListModelList<Route> getRoutesList() {
		return routesList;
	}

	public void setRoutesList(ListModelList<Route> routesList) {
		this.routesList = routesList;
	}

	public RouteSearchCriteria getRouteSearchCriteria() {
		return routeSearchCriteria;
	}

	public void setRouteSearchCriteria(RouteSearchCriteria routeSearchCriteria) {
		this.routeSearchCriteria = routeSearchCriteria;
	}

	//	public Validator getDateValidator() {
	//		return dateValidator;
	//	}
	//
	//	public void setDateValidator(Validator dateValidator) {
	//		this.dateValidator = dateValidator;
	//	}
}
