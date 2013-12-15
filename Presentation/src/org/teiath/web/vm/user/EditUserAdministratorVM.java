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
public class EditUserAdministratorVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditUserAdministratorVM.class.getName());

	@Wire("#licensePhoto")
	private Image licensePhoto;
	@Wire("#typeLabel")
	private Label typeLabel;

	@WireVariable
	private EditUserService editUserService;

	private User user;
	private String licenseCode;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		user = new User();

		try {
			user = editUserService.getUserById((Integer) ZKSession.getAttribute("userId"));

			if (user.getLicense() != null) {
				if (user.getLicense().getCode() != null) {
					licenseCode = user.getLicense().getCode();
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
			ZKSession.setAttribute("userId", user.getId());
			ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
		} catch (IOException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.setAttribute("userId", user.getId());
			ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
		}
	}

	@Command
	public void onSave() {

		if (licenseCode != null) {
			user.getLicense().setPending(true);
			user.getLicense().setCode(licenseCode);
		}

		try {
			editUserService.saveUser(user);
			Messagebox.show(Labels.getLabel("user.message.edit.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.setAttribute("userId", user.getId());
					ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.setAttribute("userId", user.getId());
			ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
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
						ZKSession.setAttribute("userId", user.getId());
						ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
					case Messagebox.NO:
						break;
				}
			}
		});
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
	public void onDeleteLicenceImage() {
		licensePhoto.setSrc("/img/noImage.png");
		user.getLicense().setImageBytes(null);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}
}
