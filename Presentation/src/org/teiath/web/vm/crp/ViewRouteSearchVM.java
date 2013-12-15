package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.*;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewRouteSearchService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

public class ViewRouteSearchVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewRouteSearchVM.class.getName());

	@Wire("#routeSearchViewWin")
	private Window win;
	@Wire("#days")
	private Label days;
	@Wire("#objectAllowedLabel")
	private Label objectAllowedLabel;
	@Wire("#smokingAllowedLabel")
	private Label smokingAllowedLabel;
	@Wire("#listBoxRow")
	private Row listBoxRow;
	@Wire("#labelRow")
	private Row labelRow;
	@Wire("#stopsList")
	private Vbox stopsList;
	@Wire("#ameaAccessibleLabel")
	private Label ameaAccessibleLabel;
	@Wire("#reservationList")
	private Vbox reservationList;
	@Wire("#vehicleTypeLabel")
	private Label vehicleTypeLabel;
	@Wire("#recurringEndDateRow")
	private Row recurringEndDateRow;

	@WireVariable
	private ViewRouteSearchService viewRouteSearchService;

	private Route route;

	private RouteInterest routeInterest;
	private RouteInterestSearchCriteria routeInterestSearchCriteria;
	private ListModelList<RouteInterest> reservationsList;
	private ListModelList<RouteAssessment> driverComments;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		int routeId = (Integer) Executions.getCurrent().getArg().get("ROUTE_ID");

		driverComments = new ListModelList<>();

		try {
			route = viewRouteSearchService.getRouteById(routeId);
			Clients.evalJavaScript("doLoad('" + route.getUser().getAverageDriverRating() + "')");
			if (route.isObjectTransportAllowed()) {
				objectAllowedLabel.setValue(Labels.getLabel("common.yes"));
			}
			if (route.isSmokingAllowed()) {
				objectAllowedLabel.setValue(Labels.getLabel("common.yes"));
			}
			if (route.isRecurring()) {
				days.setValue("Κάθε " + route.getDays());
				if (route.getRouteEndDate() != null) {
					recurringEndDateRow.setVisible(true);
				}
			}
			if (route.getAmeaAccessible()) {
				ameaAccessibleLabel.setValue(Labels.getLabel("common.yes"));
			}
			if (route.getVehicle().getType() == Vehicle.CAR)
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.car"));
			else
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.motorcycle"));

			Collection<RouteAssessment> comments = viewRouteSearchService.findDriverComments(route.getUser());
			driverComments.addAll(comments);
			if (driverComments.isEmpty()) {
				listBoxRow.setVisible(false);
				labelRow.setVisible(true);
			}

			if (! route.getRouteInterests().isEmpty()) {
				for (RouteInterest r : route.getRouteInterests()) {
					if (r.getStatus() == RouteInterest.STATUS_APPROVED) {
						reservationList
								.appendChild(new Label(r.getUser().getLastName() + " " + r.getUser().getFirstName()));
					}
				}
			} else {
				reservationList.appendChild(new Label(Labels.getLabel("route.noPassengers")));
			}

			String path = "";
			List<String> stops = new ArrayList<>();

			Comparator<RouteSegment> comparator = new Comparator<RouteSegment>() {
				public int compare(RouteSegment c1, RouteSegment c2) {
					return Integer.valueOf(c1.getIndex()).compareTo(Integer.valueOf(c2.getIndex()));
				}
			};

			List<RouteSegment> routeList = new ArrayList<>(route.getRouteSegments());
			Collections.sort(routeList, comparator);

			for (RouteSegment p : routeList) {
// todo
//				switch (p.getType()) {
//					case RouteSegment.DEPARTURE:
//						path += "(" + p.getAddress() + ")D|";
//						break;
//					case RouteSegment.ARRIVAL:
//						path += "(" + p.getAddress() + ")A*";
//						stops.add(p.getAddress());
//						break;
//					case RouteSegment.WAYPOINT:
//						path += "(" + p.getLocation().getX() + ", " + p.getLocation().getY() + ")W|";
//						break;
//				}
			}

			if (! stops.isEmpty()) {
				for (int i = 0, j = stops.size() - 1; i < j; i++) {
					stopsList.appendChild(new Label((i + 1) + ". " + stops.get(i)));
				}
			} else {
				stopsList.appendChild(new Label(Labels.getLabel("route.noStops")));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onClose() {
		win.detach();
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public ListModelList<RouteInterest> getReservationsList() {
		return reservationsList;
	}

	public void setReservationsList(ListModelList<RouteInterest> reservationsList) {
		this.reservationsList = reservationsList;
	}

	public ListModelList<RouteAssessment> getDriverComments() {
		return driverComments;
	}

	public void setDriverComments(ListModelList<RouteAssessment> driverComments) {
		this.driverComments = driverComments;
	}
}
