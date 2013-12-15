package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationCriteriaService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class ViewNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewNotificationCriteriaVM.class.getName());

	@WireVariable
	private ViewNotificationCriteriaService viewNotificationCriteriaService;

	private NotificationCriteria notificationCriteria;

	@AfterCompose
	@NotifyChange("notificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			notificationCriteria = viewNotificationCriteriaService
					.getNotificationCriteriaById((Integer) ZKSession.getAttribute("notificationCriteriaId"));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_LIST);
	}

	public NotificationCriteria getNotificationCriteria() {
		return notificationCriteria;
	}

	public void setNotificationCriteria(NotificationCriteria notificationCriteria) {
		this.notificationCriteria = notificationCriteria;
	}
}
