package org.teiath.data.dao;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.crp.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;
import org.teiath.data.search.UserActionSearchCriteria;

public interface UserActionDAO {

	public UserAction findById(Integer id);

	public SearchResults<UserAction> search(UserActionSearchCriteria criteria);

	public void save(UserAction userAction);

}
