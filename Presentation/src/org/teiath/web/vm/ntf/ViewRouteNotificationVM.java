package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.Notification;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationService;
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
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class ViewRouteNotificationVM
		extends BaseVM {

	@Wire("#notificationCriteriaDetails")
	private Rows notificationCriteriaDetails;
	@Wire("#notificationCriteriaLabel")
	private Label notificationCriteriaLabel;
	@Wire("#viewRouteNotificationWin")
	private Window win;
	@Wire("#transitionButton")
	private Toolbarbutton transitionButton;

	static Logger log = Logger.getLogger(ViewRouteNotificationVM.class.getName());

	@WireVariable
	private ViewNotificationService viewNotificationService;

	private Notification notification;

	@AfterCompose
	@NotifyChange("notification")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			notification = viewNotificationService
					.getNotificationById((Integer) ZKSession.getAttribute("notificationId"));

			if (notification.getRoute() == null)
				transitionButton.setVisible(false);

			if (notification.getNotificationCriteria() != null) {

				notificationCriteriaLabel.setVisible(true);

				if (notification.getNotificationCriteria().getTitle() != null) {

					Label titleLabel = new Label();
					titleLabel.setValue("Κριτήριο ειδοποίησης:");
					Label criterionTitle = new Label();
					criterionTitle.setValue(notification.getNotificationCriteria().getTitle());

					criterionTitle.setWidth("100px");
					titleLabel.setWidth("100px");
					Row titleRow = new Row();
					titleRow.appendChild(titleLabel);
					titleRow.appendChild(criterionTitle);
					notificationCriteriaDetails.appendChild(titleRow);

					Label descriptionLabel = new Label();
					descriptionLabel.setValue("Περιγραφή κριτηρίου:");
					Label criterionDescription = new Label();
					criterionDescription.setValue(notification.getNotificationCriteria().getDescription());
					Row descriptionRow = new Row();
					descriptionRow.appendChild(descriptionLabel);
					descriptionRow.appendChild(criterionDescription);
					notificationCriteriaDetails.appendChild(descriptionRow);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATIONS_LIST);
	}

	@Command
	public void onTransition() {
		if (notification.getRoute() != null) {
			if ((notification.getType() == Notification.TYPE_ROUTES) && (loggedUser.getId() == notification.getRoute()
					.getUser().getId())) {
				ZKSession.setAttribute("routeId", notification.getRoute().getId());
				ZKSession.setAttribute("fromNotification", true);
				ZKSession.sendRedirect(PageURL.ROUTE_VIEW);
			} else if (notification.getType() == Notification.TYPE_ROUTES) {
				Map<String, Object> params = new HashMap();
				params.put("ROUTE_ID", notification.getRoute().getId());
				Executions.createComponents(PageURL.SEARCH_ROUTE_VIEW, win, params);
			}
		}
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
