package org.teiath.web.vm.crp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.*;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewRouteService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class ViewRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewRouteVM.class.getName());

	@Wire("#days")
	private Label days;
	@Wire("#ameaAccessibleLabel")
	private Label ameaAccessibleLabel;
	@Wire("#objectAllowedLabel")
	private Label objectAllowedLabel;
	@Wire("#smokingAllowedLabel")
	private Label smokingAllowedLabel;
	@Wire("#inquiryButton")
	private Button inquiryButton;
	@Wire("#reservationList")
	private Vbox reservationList;
	@Wire("#stopsList")
	private Vbox stopsList;
	@Wire("#vehicleTypeLabel")
	private Label vehicleTypeLabel;
	@Wire("#recurringEndDateRow")
	private Row recurringEndDateRow;

	@WireVariable
	private ViewRouteService viewRouteService;

	private Route route;

	private RouteInterest routeInterest;
	private RouteInterestSearchCriteria routeInterestSearchCriteria;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			if (Executions.getCurrent().getParameter("code") != null) {
				route = viewRouteService.getRouteByCode(Executions.getCurrent().getParameter("code"));
			} else if (ZKSession.getAttribute("routeCode") != null)
				route = viewRouteService.getRouteByCode((String) ZKSession.getAttribute("routeCode"));
			else {
				route = viewRouteService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
			}

			if (route.getVehicle().getType() == Vehicle.CAR)
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.car"));
			else
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.motorcycle"));

			if (loggedUser.getId() == route.getUser().getId()) {
				inquiryButton.setVisible(false);
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
			if (route.isObjectTransportAllowed()) {
				objectAllowedLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				objectAllowedLabel.setValue(Labels.getLabel("common.no"));
			}
			if (route.isSmokingAllowed()) {
				smokingAllowedLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				smokingAllowedLabel.setValue(Labels.getLabel("common.no"));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}

		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		routeInterestSearchCriteria.setRoute(route);
		//2: Pending Reservations
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_PENDING);

		if (! route.getRouteInterests().isEmpty()) {
			for (RouteInterest r : route.getRouteInterests()) {
				reservationList.appendChild(new Label(r.getUser().getLastName() + " " + r.getUser().getFirstName()));
			}
		} else {
			reservationList.appendChild(new Label(Labels.getLabel("route.noPassengers")));
		}

		String path = "";
		String points = "";
		String addresses = "";
		List<String> stops = new ArrayList<>();

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

		List<RouteSegment> routeSegments = new ArrayList<>(route.getRouteSegments());
		Collections.sort(routeSegments, comparator);

		RouteSegment routeSegment;
		int index = 1;
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
				stopsList.appendChild(new Label((index++) + ". " + routeSegment.getStartAddress()));
			}
			if (i == routeSegments.size()-1) {
				if (routeSegments.size() > 1) {
					points += routeSegment.getStartLat() + "," + routeSegment.getStartLng() + "|";
					stopsList.appendChild(new Label((index++) + ". " + routeSegment.getStartAddress()));
					addresses += routeSegment.getStartAddress() + "|";
				}
				points += routeSegment.getEndLat() + "," + routeSegment.getEndLng() + "|";
				addresses += routeSegment.getEndAddress() + "|";
			}
		}
		path = path.substring(0, path.length() - 1);
		points = points.substring(0, points.length() - 1);
		addresses = addresses.substring(0, addresses.length() - 1);

		if (routeSegments.size() < 2) {
			stopsList.appendChild(new Label(Labels.getLabel("route.noStops")));
		}

		if (!path.isEmpty()) {
			Clients.evalJavaScript("doPreview('" + path + "', '" + points + "', '" + addresses + "')");
		}
	}

	@Command
	public void onInquiry() {
		ZKSession.setAttribute("routeId", route.getId());
		ZKSession.sendRedirect(PageURL.ROUTE_INQUIRY);
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromNotification") != null) {
			ZKSession.removeAttribute("fromNotification");
			ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
		} else if (ZKSession.getAttribute("fromUserAction") != null) {
			ZKSession.removeAttribute("fromUserAction");
			ZKSession.sendRedirect(PageURL.USER_ACTION_LIST);
		} else {
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
