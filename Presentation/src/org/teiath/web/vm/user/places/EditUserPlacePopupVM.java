package org.teiath.web.vm.user.places;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.EditUserPlaceService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class EditUserPlacePopupVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditUserPlacePopupVM.class.getName());

	@WireVariable
	private EditUserPlaceService editUserPlaceService;

	@Wire("#placeEditPopupWin")
	private Window win;

	private UserPlace userPlace;

	@AfterCompose
	@NotifyChange("userPlace")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		userPlace = (UserPlace) Executions.getCurrent().getArg().get("PLACE");
	}

	@Command
	public void onSave() {
		try {
			editUserPlaceService.saveUserPlace(userPlace);
			Messagebox.show(Labels.getLabel("place.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					BindUtils.postGlobalCommand(null, null, "receivePlace", null);
					win.detach();
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.places")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_EDIT);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
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

	public UserPlace getUserPlace() {
		return userPlace;
	}

	public void setUserPlace(UserPlace userPlace) {
		this.userPlace = userPlace;
	}
}
