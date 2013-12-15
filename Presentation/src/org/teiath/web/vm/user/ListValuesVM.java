package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListValuesService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;

@SuppressWarnings("UnusedDeclaration")
public class ListValuesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListValuesVM.class.getName());

	@Wire("#paging")
	private Paging paging;

	@WireVariable
	private ListValuesService listValuesService;

	private SearchCriteria searchCriteria;
	private User user;
	private ListModelList<PopularPlace> popularPlaces;
	private PopularPlace selectedPopularPlace;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onCreatePopularPalces() {
		ZKSession.sendRedirect(PageURL.POPULAR_PLACE_CREATE);
	}

	@Command
	public void onEditPopularPalces() {
		if (selectedPopularPlace != null) {
			ZKSession.setAttribute("popularPlaceId", selectedPopularPlace.getId());
			ZKSession.sendRedirect(PageURL.POPULAR_PLACE_EDIT);
		}
	}

	@Command
	public void onDeletePopularPalces() {
		if (selectedPopularPlace != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deletePopularPlace(selectedPopularPlace);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("popular.palces")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("popular.palces")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	@NotifyChange("selectedPopularPlace")
	public void onPaging() {
		if (popularPlaces != null) {
			searchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<PopularPlace> results = listValuesService.searchPopularPlacesByCriteria(searchCriteria);
				selectedPopularPlace = null;
				popularPlaces.clear();
				popularPlaces.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	public ListModelList<PopularPlace> getPopularPlaces() {
		if (popularPlaces == null) {
			popularPlaces = new ListModelList<>();
			//Initial search criteria
			searchCriteria = new SearchCriteria();
			searchCriteria.setPageSize(paging.getPageSize());
			searchCriteria.setPageNumber(0);
			searchCriteria.setOrderField("address");
			searchCriteria.setOrderDirection("ascending");
			try {
				SearchResults<PopularPlace> results = listValuesService.searchPopularPlacesByCriteria(searchCriteria);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
				popularPlaces.addAll(results.getData());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("popular.palces")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return popularPlaces;
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		searchCriteria.setOrderField(listheader.getId());
		searchCriteria.setOrderDirection(listheader.getSortDirection());
		searchCriteria.setPageNumber(0);
		selectedPopularPlace = null;
		popularPlaces.clear();

		try {
			SearchResults<PopularPlace> results = listValuesService.searchPopularPlacesByCriteria(searchCriteria);
			selectedPopularPlace = null;
			popularPlaces.clear();
			popularPlaces.addAll(results.getData());
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(searchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
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

	public PopularPlace getSelectedPopularPlace() {
		return selectedPopularPlace;
	}

	public void setSelectedPopularPlace(PopularPlace selectedPopularPlace) {
		this.selectedPopularPlace = selectedPopularPlace;
	}

	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
}
