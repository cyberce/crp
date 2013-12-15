package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.PopularPlaceDAO;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listValuesService")
@Transactional
public class ListValuesServiceImpl
		implements ListValuesService {

	@Autowired
	PopularPlaceDAO popularPlaceDAO;

	@Override
	public SearchResults<PopularPlace> searchPopularPlacesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException {
		SearchResults<PopularPlace> results;

		try {
			results = popularPlaceDAO.search(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<PopularPlace> getPopularPlaces()
			throws ServiceException {
		Collection<PopularPlace> popularPlaces;

		try {
			popularPlaces = popularPlaceDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return popularPlaces;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deletePopularPlace(PopularPlace popularPlace)
			throws ServiceException, DeleteViolationException {
		try {
			popularPlaceDAO.delete(popularPlace);
		}
		/*catch (org.hibernate.exception.ConstraintViolationException e) {
			throw new DeleteViolationException();
		} */ catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
