package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.service.crp.ViewRouteAbuseReportService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class ViewRouteAbuseReportVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewRouteAbuseReportVM.class.getName());

	@Wire("#banButton")
	private Button banButton;
	@Wire("#restoreButton")
	private Button restoreButton;

	@WireVariable
	private ViewRouteAbuseReportService viewRouteAbuseReportService;

	private RouteAbuseReport routeAbuseReport;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			routeAbuseReport = viewRouteAbuseReportService
					.getRouteAbuseReportById((Integer) ZKSession.getAttribute("routeAbuseReportId"));
			if (routeAbuseReport.getReportedUser().isBanned()) {
				restoreButton.setDisabled(false);
			} else {
				banButton.setDisabled(false);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBan() {
		Messagebox.show(Labels.getLabel("abuse.banQuestion"), Labels.getLabel("common.messages.ban_title"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						try {
							viewRouteAbuseReportService.banUser(routeAbuseReport.getReportedUser());
							ZKSession.sendRedirect(PageURL.ROUTE_ABUSE_REPORTS_LIST);
						} catch (ServiceException e) {
							Messagebox.show(MessageBuilder
									.buildErrorMessage(e.getMessage(), Labels.getLabel("common.messages.save_title")),
									Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
						}
						break;
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void onRestore() {
		Messagebox.show(Labels.getLabel("abuse.restoreQuestion"), Labels.getLabel("common.messages.restore_title"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						try {
							viewRouteAbuseReportService.restoreUser(routeAbuseReport.getReportedUser());
							ZKSession.sendRedirect(PageURL.ROUTE_ABUSE_REPORTS_LIST);
						} catch (ServiceException e) {
							Messagebox.show(MessageBuilder
									.buildErrorMessage(e.getMessage(), Labels.getLabel("common.messages.save_title")),
									Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
						}
						break;
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ROUTE_ABUSE_REPORTS_LIST);
	}

	public RouteAbuseReport getRouteAbuseReport() {
		return routeAbuseReport;
	}

	public void setRouteAbuseReport(RouteAbuseReport routeAbuseReport) {
		this.routeAbuseReport = routeAbuseReport;
	}
}
