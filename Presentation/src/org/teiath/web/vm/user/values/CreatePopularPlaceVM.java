package org.teiath.web.vm.user.values;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.CreatePopularPlaceService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class CreatePopularPlaceVM {

	static Logger log = Logger.getLogger(CreatePopularPlaceVM.class.getName());

	@WireVariable
	private CreatePopularPlaceService createPopularPlaceService;

	private PopularPlace popularPlace;

	@AfterCompose
	@NotifyChange("popularPlace")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		popularPlace = new PopularPlace();
	}

	@Command
	public void onSave() {
		try {
			createPopularPlaceService.savePopularPlace(popularPlace);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.VALUES_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
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
						ZKSession.sendRedirect(PageURL.VALUES_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public PopularPlace getPopularPlace() {
		return popularPlace;
	}

	public void setPopularPlace(PopularPlace popularPlace) {
		this.popularPlace = popularPlace;
	}
}
