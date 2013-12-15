package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;

import java.util.Collection;

@Repository("popularPlaceDAO")
public class PopularPlaceDAOImpl
		implements PopularPlaceDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<PopularPlace> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<PopularPlace> popularPlaces;

		String hql = "from PopularPlace popularPlace order by popularPlace.name asc";
		Query query = session.createQuery(hql);

		popularPlaces = query.list();

		return popularPlaces;
	}

	@Override
	public void save(PopularPlace popularPlace) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(popularPlace);
	}

	@Override
	public PopularPlace findById(Integer id) {
		PopularPlace popularPlace;

		Session session = sessionFactory.getCurrentSession();
		popularPlace = (PopularPlace) session.get(PopularPlace.class, id);

		return popularPlace;
	}

	@Override
	public void delete(PopularPlace popularPlace) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(popularPlace);
		session.flush();
	}

	@Override
	public SearchResults<PopularPlace> search(SearchCriteria searchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<PopularPlace> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(PopularPlace.class);

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(searchCriteria.getPageNumber() * searchCriteria.getPageSize());
		criteria.setMaxResults(searchCriteria.getPageSize());

		//Sorting
		if (searchCriteria.getOrderField() != null) {
			if (searchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(searchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(searchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}
}
