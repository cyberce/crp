package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ViewUserService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ViewUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewUserVM.class.getName());

	@Wire("#userViewrWin")
	private Window win;
	@Wire("#userPhoto")
	private Image userPhoto;
	@Wire("#genderLabel")
	private Label genderLabel;
	@Wire("#typeLabel")
	private Label typeLabel;
	@Wire("#licensePhoto")
	private Image licensePhoto;
	@Wire("#vehicles")
	private Div vehiclesDiv;
	@Wire("#places")
	private Div placesDiv;
	@Wire("#smsNotificationsLabel")
	private Label smsNotificationsLabel;
	@Wire("#emailNotificationsLabel")
	private Label emailNotificationsLabel;

	@WireVariable
	private ViewUserService viewUserService;

	private User user;
	private ListModelList<Vehicle> vehicles;
	private Vehicle selectedVehicle;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			user = viewUserService.getUserById(loggedUser.getId());

			if (user.getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", user.getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			if (user.getLicense() != null) {
				if (user.getLicense().getImageBytes() != null) {
					AImage aImage = new AImage("", user.getLicense().getImageBytes());
					licensePhoto.setContent(aImage);

				}
			}
			 else {
				licensePhoto.setSrc("/img/noImage.png");
			}

			if (user.getGender() != null) {
				if (user.getGender() == User.GENDER_MALE) {
					genderLabel.setValue(Labels.getLabel("user.male"));
				} else {
					genderLabel.setValue(Labels.getLabel("user.female"));
				}
			}

			if (user.isEmailNotifications()) {
				emailNotificationsLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				emailNotificationsLabel.setValue(Labels.getLabel("common.no"));
			}

			if (user.isSmsNotifications()) {
				smsNotificationsLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				smsNotificationsLabel.setValue(Labels.getLabel("common.no"));
			}

			if (user.getUserType() == User.USER_TYPE_EXTERNAL) {
				typeLabel.setValue(Labels.getLabel("user.external"));
			} else if (user.getUserType() == User.USER_TYPE_STUDENT) {
				typeLabel.setValue(Labels.getLabel("user.student"));
			} else if (user.getUserType() == User.USER_TYPE_PROFESSOR) {
				typeLabel.setValue(Labels.getLabel("user.professor"));
			} else if (user.getUserType() == User.USER_TYPE_ADMINISTRATION_CLERK) {
				typeLabel.setValue(Labels.getLabel("user.administrationClerk"));
			}

			int index = 1;
			Hlayout hlayout = new Hlayout();
			hlayout.setStyle("height: 80px");
			for (Vehicle vehicle : viewUserService.getVehiclesByUser(loggedUser)) {
				Vlayout vlayout = new Vlayout();
				vlayout.setStyle(" width: 180px; font-weight: bold; border-left: 1px solid grey");
				vlayout.appendChild(new Label("Μάρκα: " + vehicle.getBrand()));
				vlayout.appendChild(new Label("Μοντέλο: " + vehicle.getModel()));
				vlayout.appendChild(new Label("Έτος: " + vehicle.getYear()));
				vlayout.appendChild(new Label("Χρώμα: " + vehicle.getColor()));

				if (index == 4) {
					hlayout.appendChild(vlayout);
					vehiclesDiv.appendChild(hlayout);
					hlayout = new Hlayout();
					hlayout.setStyle("height: 100px");
					index = 1;
				} else {
					hlayout.appendChild(vlayout);
					index++;
				}
			}
			if (! hlayout.getChildren().isEmpty()) {
				vehiclesDiv.appendChild(hlayout);
			}

			int placeIndex = 1;
			Hlayout placeHlayout = new Hlayout();
			placeHlayout.setStyle("height: 80px");
			for (UserPlace place : viewUserService.getPlacesByUser(loggedUser)) {
				Vlayout placeVlayout = new Vlayout();
				placeVlayout.setStyle(" width: 180px; font-weight: bold; border-left: 1px solid grey");
				placeVlayout.appendChild(new Label("Όνομα: " + place.getName()));
				placeVlayout.appendChild(new Label("Διεύθυνση: " + place.getAddress()));

				if (placeIndex == 4) {
					placeHlayout.appendChild(placeVlayout);
					placesDiv.appendChild(placeHlayout);
					placeHlayout = new Hlayout();
					placeHlayout.setStyle("height: 100px");
					placeIndex = 1;
				} else {
					placeHlayout.appendChild(placeVlayout);
					placeIndex++;
				}
			}
			if (! placeHlayout.getChildren().isEmpty()) {
				placesDiv.appendChild(placeHlayout);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		} catch (IOException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCreate() {
		if ((vehicles == null) || (vehicles.getSize() < 10)) {
			ZKSession.sendRedirect(PageURL.VEHICLE_CREATE);
		}
	}

	@Command
	public void onEdit() {
		if (selectedVehicle != null) {
			ZKSession.setAttribute("vehicleId", selectedVehicle.getId());
			ZKSession.sendRedirect(PageURL.VEHICLE_EDIT);
		}
	}

	@Command
	public void onUpdate() {
		ZKSession.sendRedirect(PageURL.USER_EDIT);
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}

	public void setSelectedVehicle(Vehicle selectedVehicle) {
		this.selectedVehicle = selectedVehicle;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
