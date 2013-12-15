package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.crp.CreateRouteAssessmentService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class RatePassengerVM
		extends BaseVM {

	static Logger log = Logger.getLogger(RatePassengerVM.class.getName());

	@Wire("#ratePopupWin")
	private Window win;

	@WireVariable
	private CreateRouteAssessmentService createRouteAssessmentService;

	@Wire
	private Intbox rating;

	private RouteAssessment selectedRouteAssessment;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		selectedRouteAssessment = (RouteAssessment) Executions.getCurrent().getArg().get("ROUTE_ASSESSMENT");

		if (selectedRouteAssessment.getRating() != null) {
			rating.setValue(selectedRouteAssessment.getRating());
			Clients.evalJavaScript("doEdit('" + selectedRouteAssessment.getRating() + "')");
		}

		if (selectedRouteAssessment.getRating() == null) {
			rating.setValue(0);
			Clients.evalJavaScript("doLoad()");
		}
	}

	@Command
	public void onSave() {
		selectedRouteAssessment.setAssessmentDate(new Date());
		if (rating.getValue() != 0) {
			selectedRouteAssessment.setRating(rating.getValue());
		}
		try {
			createRouteAssessmentService.saveAssessment(selectedRouteAssessment);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		win.detach();
		ZKSession.sendRedirect(PageURL.ROUTE_PASSENGER_RATE);
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event evt) {
						switch ((Integer) evt.getData()) {
							case Messagebox.YES:
								win.detach();
							case Messagebox.NO:
								break;
						}
					}
				});
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public RouteAssessment getSelectedRouteAssessment() {
		return selectedRouteAssessment;
	}

	public void setSelectedRouteAssessment(RouteAssessment selectedRouteAssessment) {
		this.selectedRouteAssessment = selectedRouteAssessment;
	}
}
