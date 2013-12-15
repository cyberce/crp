package org.teiath.data.dao;

import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;

import java.util.Collection;

public interface PopularPlaceDAO {

	public Collection<PopularPlace> findAll();

	public void save(PopularPlace popularPlace);

	public PopularPlace findById(Integer id);

	public void delete(PopularPlace popularPlace);

	public SearchResults<PopularPlace> search(SearchCriteria criteria);
}
