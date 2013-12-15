package org.teiath.web.vm.crp.terms;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.crp.CreateRouteAssessmentService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ToSVM
		extends BaseVM {


	@Wire("#tosPopupWin")
	private Window win;


	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onAcceptTerms() {
		BindUtils.postGlobalCommand(null, null, "acceptTerms", null);
		win.detach();
	}

	@Command
	public void onRejectTerms() {
		ZKSession.sendPureRedirect(
				"/");
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}
}
