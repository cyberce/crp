package org.teiath.web.vm;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.East;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.West;

public class AuxMaterialVM {


	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void api() {
		ZKSession.sendRedirect("/zul/aux_material/api.zul");
	}

	@Command
	public void manual() {
		ZKSession.sendRedirect("/zul/aux_material/manual.zul");
	}

	@Command
	public void presentation() {
		ZKSession.sendRedirect("/zul/aux_material/presentation.zul");
	}
}
