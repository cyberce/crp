package org.teiath.service.values;

import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListValuesService {

	public SearchResults<PopularPlace> searchPopularPlacesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException;

	public Collection<PopularPlace> getPopularPlaces()
			throws ServiceException;

	public void deletePopularPlace(PopularPlace popularPlace)
			throws ServiceException, DeleteViolationException;
}
