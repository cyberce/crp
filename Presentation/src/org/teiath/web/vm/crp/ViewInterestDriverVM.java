package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewRouteInterestDriverService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ViewInterestDriverVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewInterestDriverVM.class.getName());

	@WireVariable
	private ViewRouteInterestDriverService viewRouteInterestDriverService;

	@Wire("#labelRow")
	private Row labelRow;

	@Wire("#pagingRow")
	private Row pagingRow;

	@Wire("#paging")
	private Paging paging;

	@Wire("#listBoxRow")
	private Row listBoxRow;

	@Wire("#commentsListBoxRow")
	private Row commentsListBoxRow;

	@Wire("#commentsLabelRow")
	private Row commentsLabelRow;

	@Wire("#passengerRatingsListBoxRow")
	private Row passengerRatingsListBoxRow;

	@Wire("#passengerRatingslabelRow")
	private Row passengerRatingslabelRow;

	@Wire("#userPhoto")
	private Image userPhoto;

	private RouteInterest routeInterest;
	private Route route;
	private ListModelList<Route> commonRoutes;
	private ListModelList<RouteAssessment> passengerComments;
	private ListModelList<RouteAssessment> passengerRatings;
	private ListModelList<ExtraPassenger> extraPassengers;
	private RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();

	@AfterCompose
	@NotifyChange("routeInterest")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		commonRoutes = new ListModelList<>();
		passengerComments = new ListModelList<>();
		passengerRatings = new ListModelList<>();
		extraPassengers = new ListModelList<>();
		routeInterestSearchCriteria.setPageSize(paging.getPageSize());
		routeInterestSearchCriteria.setPageNumber(0);

		try {
			routeInterest = viewRouteInterestDriverService
					.getRouteInterestById((Integer) ZKSession.getAttribute("routeInterestId"));
			if (routeInterest.getUser().getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", routeInterest.getUser().getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			extraPassengers.addAll(viewRouteInterestDriverService.getRounteInterestPassengers(routeInterest));

			route = routeInterest.getRoute();
			SearchResults<Route> results = viewRouteInterestDriverService
					.findCommonRoutes(route, routeInterest, routeInterestSearchCriteria);
			Collection<Route> routes = results.getData();
			commonRoutes.addAll(routes);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
			if (commonRoutes.isEmpty()) {
				pagingRow.setVisible(false);
				listBoxRow.setVisible(false);
				labelRow.setVisible(true);
			}

			Collection<RouteAssessment> comments = viewRouteInterestDriverService
					.findPassengerComments(routeInterest.getUser());
			passengerComments.addAll(comments);
			Collection<RouteAssessment> ratings = viewRouteInterestDriverService
					.findPassengerRatings(routeInterest.getUser(), route.getUser());
			passengerRatings.addAll(ratings);

			if (passengerComments.isEmpty()) {
				commentsListBoxRow.setVisible(false);
				commentsLabelRow.setVisible(true);
			}

			if (passengerRatings.isEmpty()) {
				passengerRatingsListBoxRow.setVisible(false);
				passengerRatingslabelRow.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		} catch (IOException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	@Command
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();
		routeInterestSearchCriteria.setOrderField(listheader.getId());
		routeInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		routeInterestSearchCriteria.setPageNumber(0);
		commonRoutes.clear();

		try {
			SearchResults<Route> results = viewRouteInterestDriverService
					.findCommonRoutes(route, routeInterest, routeInterestSearchCriteria);
			Collection<Route> routes = results.getData();
			commonRoutes.addAll(routes);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(routeInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onPaging() {
		if (commonRoutes != null) {
			routeInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Route> results = viewRouteInterestDriverService
						.findCommonRoutes(route, routeInterest, routeInterestSearchCriteria);
				commonRoutes.clear();
				Collection<Route> routes = results.getData();
				commonRoutes.addAll(routes);
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
	public void onBack() {

		if (ZKSession.getAttribute("fromSubmenu") != null) {
			ZKSession.removeAttribute("fromSubmenu");
			ZKSession.sendRedirect(PageURL.INCOMING_ROUTE_INTERESTS);
		} else {
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	public RouteInterest getRouteInterest() {
		return routeInterest;
	}

	public void setRouteInterest(RouteInterest routeInterest) {
		this.routeInterest = routeInterest;
	}

	public ViewRouteInterestDriverService getViewRouteInterestDriverService() {
		return viewRouteInterestDriverService;
	}

	public void setViewRouteInterestDriverService(ViewRouteInterestDriverService viewRouteInterestDriverService) {
		this.viewRouteInterestDriverService = viewRouteInterestDriverService;
	}

	public ListModelList<Route> getCommonRoutes() {
		return commonRoutes;
	}

	public void setCommonRoutes(ListModelList<Route> commonRoutes) {
		this.commonRoutes = commonRoutes;
	}

	public ListModelList<RouteAssessment> getPassengerComments() {
		return passengerComments;
	}

	public void setPassengerComments(ListModelList<RouteAssessment> passengerComments) {
		this.passengerComments = passengerComments;
	}

	public ListModelList<RouteAssessment> getPassengerRatings() {
		return passengerRatings;
	}

	public void setPassengerRatings(ListModelList<RouteAssessment> passengerRatings) {
		this.passengerRatings = passengerRatings;
	}

	public ListModelList<ExtraPassenger> getExtraPassengers() {
		return extraPassengers;
	}

	public void setExtraPassengers(ListModelList<ExtraPassenger> extraPassengers) {
		this.extraPassengers = extraPassengers;
	}
}
