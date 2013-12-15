package org.teiath.web.vm.vehicle;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.vehicle.CreateVehicleService;
import org.teiath.web.util.MessageBuilder;
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
public class CreateVehiclePopupVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateVehiclePopupVM.class.getName());

	@WireVariable
	private CreateVehicleService createVehicleService;

	@Wire("#vehicleCreatePopupWin")
	private Window win;

	private Vehicle vehicle;
	private User user;

	@AfterCompose
	@NotifyChange("vehicle")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		vehicle = new Vehicle();
		vehicle.setType(Vehicle.CAR);
		user = (User) Executions.getCurrent().getArg().get("USER");
	}

	@Command
	public void onSave() {
		try {
			vehicle.setUser(user);
			createVehicleService.saveVehicle(vehicle);
			Messagebox.show(Labels.getLabel("vehicle.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					BindUtils.postGlobalCommand(null, null, "receiveVehicle", null);
					win.detach();
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

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
