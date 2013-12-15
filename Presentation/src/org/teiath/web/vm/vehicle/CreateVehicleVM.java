package org.teiath.web.vm.vehicle;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.vehicle.CreateVehicleService;
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
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class CreateVehicleVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateVehicleVM.class.getName());

	@WireVariable
	private CreateVehicleService createVehicleService;

	private Vehicle vehicle;

	@AfterCompose
	@NotifyChange("vehicle")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		vehicle = new Vehicle();
	}

	@Command
	public void onSave() {
		try {
			vehicle.setUser(loggedUser);
			createVehicleService.saveVehicle(vehicle);
			Messagebox.show(Labels.getLabel("vehicle.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.USER_EDIT);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicle.add")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.USER_EDIT);
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
}
