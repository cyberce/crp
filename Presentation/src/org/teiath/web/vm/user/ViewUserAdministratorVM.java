package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ViewUserAdministratorVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewUserAdministratorVM.class.getName());

	@Wire("#userViewrWin")
	private Window win;
	@Wire("#genderLabel")
	private Label genderLabel;
	@Wire("#licensePhoto")
	private Image licensePhoto;

	@WireVariable
	private ViewUserService viewUserService;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			user = viewUserService.getUserById((Integer) ZKSession.getAttribute("userId"));

			if (user.getLicense().getImageBytes() == null) {
				licensePhoto.setSrc("/img/noImage.png");
			} else {
				AImage aImage = new AImage("", user.getLicense().getImageBytes());
				licensePhoto.setContent(aImage);
			}

			if (user.getGender() != null) {
				if (user.getGender() == User.GENDER_MALE) {
					genderLabel.setValue(Labels.getLabel("user.male"));
				} else if (user.getGender() == User.GENDER_FEMALE){
					genderLabel.setValue(Labels.getLabel("user.female"));
				}
			}

		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.USER_LIST);
	}

	@Command
	public void onImageView() {
		try {
			AImage aImage = new AImage("", user.getLicense().getImageBytes());
			ZKSession.setAttribute("aImage", aImage);
			Executions.createComponents("/zul/crp/image_view.zul", win, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Command
	public void onUpdate() {
		ZKSession.setAttribute("userId", user.getId());
		ZKSession.sendRedirect(PageURL.USER_EDIT_ADMINISTRATOR);
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
