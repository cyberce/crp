package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.License;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class EditUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditUserVM.class.getName());

	@Wire("#emailRow")
	private Row emailRow;
	@Wire("#userPhoto")
	private Image userPhoto;
	@Wire("#licensePhoto")
	private Image licensePhoto;
	@Wire("#genderCombo")
	private Combobox genderCombo;
	@Wire("#genderMale")
	private Comboitem genderMale;
	@Wire("#genderFemale")
	private Comboitem genderFemale;
	@Wire("#typeLabel")
	private Label typeLabel;
	@Wire("#editUserWin")
	private Window win;
	@Wire("#mobile")
	private Textbox mobile;
	@Wire("#smsCheckbox")
	private Checkbox smsCheckbox;
	@Wire("#smsTooltip")
	private Label smsTooltip;
	@Wire("#userPasswordResetRow")
	private Row userPasswordResetRow;

	@WireVariable
	private EditUserService editUserService;

	private User user;
	private UserEditValidator userValidator;
	private Vehicle selectedVehicle;
	private ListModelList<Vehicle> vehicles;
	private ListModelList<Vehicle> vehicleToDelete;
	private UserPlace selectedUserPlace;
	private ListModelList<UserPlace> places;
	private ListModelList<UserPlace> placeToDelete;
	private Boolean unlincensed;
	private String licenseCode;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		user = new User();
		userValidator = new UserEditValidator();
		if (smsCheckbox.isChecked())
			mobile.setConstraint("/^(69\\d{8})/:  Ο αριθμός θα πρέπει να είναι της μορφής 69xxxxxxxx");

		try {
			user = editUserService.getUserById(loggedUser.getId());

			vehicleToDelete = new ListModelList<>();
			placeToDelete = new ListModelList<>();

			if (user.getUserType() != null) {
				if (user.getUserType() == User.USER_TYPE_EXTERNAL) {
					emailRow.setVisible(true);
					userPasswordResetRow.setVisible(true);
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
			}

			if (user.getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", user.getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			if (user.getGender() != null) {
				if (user.getGender() == User.GENDER_MALE) {
					genderCombo.setValue(Labels.getLabel("user.male"));
				} else {
					genderCombo.setValue(Labels.getLabel("user.female"));
				}
			}

			if (user.getLicense() != null) {
				if (user.getLicense().getCode() != null) {
					licenseCode = user.getLicense().getCode();
				}

				if (user.getLicense().getPending() == null) {
					unlincensed = true;
				} else {
					unlincensed = false;
				}

				if (user.getLicense().getImageBytes() != null) {
					AImage licenseImage = new AImage("", user.getLicense().getImageBytes());
					licensePhoto.setContent(licenseImage);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
		} catch (IOException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
		}
	}

	@Command
	public void resetPassword() {
		Map params = new HashMap();
		params.put("USER", user);
		Executions.createComponents("/zul/user/user_password_reset_popup.zul", win, params);
	}

	@Command
	public void onSave() {
		if (genderCombo.getValue().equals(Labels.getLabel("user.male"))) {
			user.setGender(User.GENDER_MALE);
		} else {
			user.setGender(User.GENDER_FEMALE);
		}

		if (licenseCode != null) {
			user.getLicense().setPending(true);
			user.getLicense().setCode(licenseCode);
		}

		try {
			editUserService.saveUser(user);

			if (! vehicleToDelete.isEmpty()) {
				editUserService.deleteVehicles(vehicleToDelete);
			}
			if (! placeToDelete.isEmpty()) {
				editUserService.deletePlaces(placeToDelete);
			}
			Messagebox.show(Labels.getLabel("user.message.edit.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.USER_PROFILE);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
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
						ZKSession.sendRedirect(PageURL.USER_PROFILE);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void onCreateVehiclePopup() {
		if ((vehicles == null) || (vehicles.getSize() < 10)) {
			Map params = new HashMap();
			params.put("USER", user);
			Executions.createComponents("/zul/user/vehicles/vehicle_create_popup.zul", win, params);
		}
	}

	@Command
	public void onCreatePlacePopup() {
		if ((places == null) || (places.getSize() < 10)) {
			Map params = new HashMap();
			params.put("USER", user);
			Executions.createComponents("/zul/user/places/userPlace_create_popup.zul", win, params);
		}
	}

	@GlobalCommand
	@NotifyChange("selectedVehicle")
	public void receiveVehicle() {
		vehicles.clear();
		try {
			vehicles.addAll(editUserService.getVehiclesByUser(loggedUser));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicles")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@GlobalCommand
	@NotifyChange("selectedUserPlace")
	public void receivePlace() {
		places.clear();
		try {
			places.addAll(editUserService.getPlacesByUser(loggedUser));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicles")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onEditVehiclePopup() {
		if (selectedVehicle != null) {
			Map params = new HashMap();
			params.put("VEHICLE", selectedVehicle);
			Executions.createComponents("/zul/user/vehicles/vehicle_edit_popup.zul", win, params);
		}
	}

	@Command
	public void onEditPlacePopup() {
		if (selectedUserPlace != null) {
			Map params = new HashMap();
			params.put("PLACE", selectedUserPlace);
			Executions.createComponents("/zul/user/places/userPlace_edit_popup.zul", win, params);
		}
	}

	@Command
	public void onDeleteVehicle() {
		try {
			if ((selectedVehicle != null) && (! editUserService.vehicleOnRoute(selectedVehicle))) {
				Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
						Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
						Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event evt) {
						switch ((Integer) evt.getData()) {
							case Messagebox.YES:
								vehicles.remove(selectedVehicle);
								vehicleToDelete = new ListModelList<>();
								vehicleToDelete.add(selectedVehicle);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {

									}
								});
								break;
							case Messagebox.NO:
								break;
						}
					}
				});
			} else {
				Messagebox.show("Το όχημα δε μπορεί να διαγραφεί διότι χρησιμοποιείται σε διαδρομή.",
						Labels.getLabel("common.messages.delete_title"), Messagebox.OK, Messagebox.EXCLAMATION);
			}
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onDeletePlace() {
		if ((selectedUserPlace != null)) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							places.remove(selectedUserPlace);
							placeToDelete = new ListModelList<>();
							placeToDelete.add(selectedUserPlace);
							Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
									Labels.getLabel("common.messages.confirm"), Messagebox.OK, Messagebox.INFORMATION,
									new EventListener<Event>() {
										public void onEvent(Event evt) {
										}
									});
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onImageUpload(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		Media media = upEvent.getMedia();
		if (user.getApplicationImage() == null) {
			ApplicationImage applicationImage = new ApplicationImage();
			user.setApplicationImage(applicationImage);
		}
		user.getApplicationImage().setImageBytes(media.getByteData());
		userPhoto.setContent((AImage) media);
	}

	@Command
	public void onLicenseImageUpload(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		Media media = upEvent.getMedia();
		License license = new License();
		user.setLicense(license);
		user.getLicense().setImageBytes(media.getByteData());
		user.getLicense().setPending(true);
		licensePhoto.setContent((AImage) media);
	}

	@Command
	public void onDeleteImage() {
		userPhoto.setSrc("/img/default-avatar.png");
		if (user.getApplicationImage() != null) {
			user.setApplicationImage(null);
		}
	}

	@Command
	public void onDeleteLicenceImage() {
		licensePhoto.setSrc("/img/noImage.png");
		user.getLicense().setImageBytes(null);
	}

	@Command
	public void toggleMobileConstraint() {
		if (smsCheckbox.isChecked()) {
			mobile.setConstraint("no empty, /^(69\\d{8})/:  Το πεδίο θα πρέπει να συμπληρωθεί υποχρεωτικά.");
			smsTooltip.setVisible(true);
		} else {
			smsTooltip.setVisible(true);
			mobile.setConstraint("");
			smsTooltip.setVisible(false);
		}
	}

	public ListModelList<Vehicle> getVehicles() {
		if (vehicles == null) {
			vehicles = new ListModelList<>();
			try {
				vehicles.addAll(editUserService.getVehiclesByUser(loggedUser));
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("vehicles")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return vehicles;
	}

	public ListModelList<UserPlace> getPlaces() {
		if (places == null) {
			places = new ListModelList<>();
			try {
				places.addAll(editUserService.getPlacesByUser(loggedUser));
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.places")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return places;
	}

	public UserEditValidator getUserValidator() {
		return userValidator;
	}

	public void setUserValidator(UserEditValidator userValidator) {
		this.userValidator = userValidator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}

	public void setSelectedVehicle(Vehicle selectedVehicle) {
		this.selectedVehicle = selectedVehicle;
	}

	public ListModelList<Vehicle> getVehicleToDelete() {
		return vehicleToDelete;
	}

	public void setVehicleToDelete(ListModelList<Vehicle> vehicleToDelete) {
		this.vehicleToDelete = vehicleToDelete;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	public UserPlace getSelectedUserPlace() {
		return selectedUserPlace;
	}

	public void setSelectedUserPlace(UserPlace selectedUserPlace) {
		this.selectedUserPlace = selectedUserPlace;
	}

	public ListModelList<UserPlace> getPlaceToDelete() {
		return placeToDelete;
	}

	public void setPlaceToDelete(ListModelList<UserPlace> placeToDelete) {
		this.placeToDelete = placeToDelete;
	}
}
