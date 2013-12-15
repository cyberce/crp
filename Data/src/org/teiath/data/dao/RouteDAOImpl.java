package org.teiath.data.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.RouteSegment;
import org.teiath.data.domain.sys.SysParameter;
import org.teiath.data.fts.FullTextSearch;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.search.RouteSearchCriteria;

import java.util.*;

@Repository("routeDAO")
public class RouteDAOImpl
		implements RouteDAO {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SysParameterDAO sysParameterDAO;

	@Override
	public Route findById(Integer id) {
		Route route;

		Session session = sessionFactory.getCurrentSession();
		route = (Route) session.get(Route.class, id);

		return route;
	}

	@Override
	public Route findByCode(String code) {
		Route route;

		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(Route.class);

		criteria.add(Restrictions.eq("code", code));
		route = (Route) criteria.uniqueResult();

		return route;
	}

	@Override
	public SearchResults<Route> search(RouteSearchCriteria routeSearchCriteria, boolean spatialSearch) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Route> results = new SearchResults<>();
		SysParameter sysParameter = sysParameterDAO.findById(1);

		Criteria criteria = session.createCriteria(Route.class);
		criteria.createAlias("user", "usr");

		//banned
		criteria.add(Restrictions.eq("usr.banned", false));

		//code
		if ((routeSearchCriteria.getCode() != null) && (! routeSearchCriteria.getCode().equals(""))) {
			criteria.add(Restrictions.eq("code", routeSearchCriteria.getCode()));
		}

		//User type
		if ((routeSearchCriteria.getUserType() != null) && (routeSearchCriteria.getUserType() != - 1)) {
			criteria.add(Restrictions.eq("usr.userType", routeSearchCriteria.getUserType()));
		}

		//enabled
		if (routeSearchCriteria.getEnabled() != null) {
			if (routeSearchCriteria.getEnabled()) {
				criteria.add(Restrictions.eq("enabled", true));
			} else {
				criteria.add(Restrictions.eq("false", true));
			}
		}

		//User
		if (routeSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user.id", routeSearchCriteria.getUser().getId()));
		}

		//Recurring
		if (routeSearchCriteria.getRecurring() != null) {
			if (routeSearchCriteria.getRecurring() == 0) {
				criteria.add(Restrictions.eq("recurring", true));
			} else if (routeSearchCriteria.getRecurring() == 1) {
				criteria.add(Restrictions.eq("recurring", false));
			}
		}

		//Vehicle
		if (routeSearchCriteria.getVehicle() != null) {
			criteria.add(Restrictions.eq("vehicle.id", routeSearchCriteria.getVehicle().getId()));
		}

		//Status
		if (routeSearchCriteria.getStatus() != null) {
			Date currentDate = new Date();
			if (routeSearchCriteria.getStatus() == 0) {
				criteria.add(Restrictions.gt("routeDate", currentDate));
			} else if (routeSearchCriteria.getStatus() == 1) {
				criteria.add(Restrictions
						.and(Restrictions.lt("routeDate", currentDate), Restrictions.gt("routeEndDate", currentDate)));
			}
		}

		//DateFrom restriction
		if (routeSearchCriteria.getDateFrom() != null) {
			criteria.add(Restrictions.ge("routeDate", routeSearchCriteria.getDateFrom()));
		}

		//DateTo restriction
		if (routeSearchCriteria.getDateTo() != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("routeEndDate"), Restrictions.le("routeEndDate", routeSearchCriteria.getDateTo())));
		}

		//Completed route restriction
		if (routeSearchCriteria.getCompleted() != null) {
			if (routeSearchCriteria.getCompleted()) {
				criteria.add(Restrictions.lt("routeEndDate", new Date()));
			}
		}

		//Time restriction
		if ((routeSearchCriteria.getTimeFrom() != null) && (routeSearchCriteria.getTimeTo() != null)) {
			criteria.add(Restrictions.ge("routeTime", routeSearchCriteria.getTimeFrom()));
			criteria.add(Restrictions.le("routeTime", routeSearchCriteria.getTimeTo()));
		}

		//People restriction
		if (routeSearchCriteria.getPeopleNumber() != null) {
			criteria.add(Restrictions.ge("totalSeats", routeSearchCriteria.getPeopleNumber()));
		}

		//Objects allowed
		if (routeSearchCriteria.getObjectTransportAllowed() != null) {
			if (routeSearchCriteria.getObjectTransportAllowed() == 1) {
				criteria.add(Restrictions.eq("objectTransportAllowed", true));
			} else {
				criteria.add(Restrictions.eq("objectTransportAllowed", false));
			}
		}

		//Contribution amount
		if (routeSearchCriteria.getMaxAmount() != null) {
			criteria.add(Restrictions.le("contributionAmount", routeSearchCriteria.getMaxAmount()));
		}

		//Smoking
		if (routeSearchCriteria.getSmokingAllowed() != null) {
			if (routeSearchCriteria.getSmokingAllowed() == 1) {
				criteria.add(Restrictions.eq("smokingAllowed", true));
			} else if (routeSearchCriteria.getSmokingAllowed() == 0) {
				criteria.add(Restrictions.eq("smokingAllowed", false));
			}
		}

		//Accessibility
		if (routeSearchCriteria.getAmeaAccessible() != null) {
			if (routeSearchCriteria.getAmeaAccessible()) {
				criteria.add(Restrictions.eq("ameaAccessible", true));
			}
		}

		//Tags
		if (routeSearchCriteria.getTags() != null) {
			FullTextSearch fullTextSearch = new FullTextSearch();
			Collection<String> keywords = fullTextSearch.transformKeyword(routeSearchCriteria.getTags());

			if (! keywords.isEmpty()) {
				StringBuffer tagQuery = new StringBuffer();
				int threshold;
				for (String keyword : keywords) {
					threshold = (int) Math.ceil(keyword.length() * (sysParameter.getLevenshteinPercent() / 100));
					tagQuery.append(
							"SELECT distinct link FROM indx_tags WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
					tagQuery.append(" UNION ");
				}
				tagQuery = tagQuery.replace(tagQuery.lastIndexOf(" UNION "), tagQuery.length(), "");
				tagQuery.insert(0, "(");
				tagQuery.append(")");

				List resultset = session.createSQLQuery(tagQuery.toString()).list();

				if (! resultset.isEmpty()) {
					criteria.add(Restrictions.in("id", resultset.toArray()));
				} else {
					criteria.add(Restrictions.in("id", new Object[] {- 1})); // display nothing
				}
			}

			//			criteria.add(Restrictions.ilike("tags", routeSearchCriteria.getTags()));
		}

		if (spatialSearch && (routeSearchCriteria.getCode() == null || routeSearchCriteria.getCode().isEmpty())) {
			DetachedCriteria dc = DetachedCriteria.forClass(Route.class);
			dc.createAlias("routePath", "routePathAlias");
			dc.add(Restrictions.sqlRestriction(
					"ST_DWithin(geography(path), ST_GeogFromText('SRID=4326;" + routeSearchCriteria
							.getSearchLocationFrom() + "'), " + (routeSearchCriteria.getSearchRadiusFrom()) + ")"));

//			dc.add(Restrictions.sqlRestriction(
//					"ST_DWithin(geography(location), ST_GeogFromText('SRID=4326;" + routeSearchCriteria
//							.getSearchLocationFrom() + "'), " + (routeSearchCriteria.getSearchRadiusFrom() + 20) + ")"));
			dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			dc.setProjection(Projections.id());

			criteria.add(Subqueries.propertyIn("id", dc));

			DetachedCriteria dc2 = DetachedCriteria.forClass(Route.class);
			dc2.createAlias("routePath", "routePathAlias");
			dc2.add(Restrictions.sqlRestriction(
					"ST_DWithin(geography(path), ST_GeogFromText('SRID=4326;" + routeSearchCriteria
							.getSearchLocationTo() + "'), " + (routeSearchCriteria.getSearchRadiusTo()) + ")"));
//			dc2.add(Restrictions.sqlRestriction(
//					"ST_DWithin(geography(location), ST_GeogFromText('SRID=4326;" + routeSearchCriteria
//							.getSearchLocationTo() + "'), " + (routeSearchCriteria.getSearchRadiusTo() + 20) + ")"));
			dc2.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			dc2.setProjection(Projections.id());

			criteria.add(Subqueries.propertyIn("id", dc2));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Sorting
		if (routeSearchCriteria.getOrderField() != null) {
			if (routeSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(routeSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(routeSearchCriteria.getOrderField()));
			}
		}

		if (routeSearchCriteria.getSearchLocationFrom() != null && routeSearchCriteria
				.getSearchLocationTo() != null && (routeSearchCriteria.getCode() == null || routeSearchCriteria.getCode().isEmpty())) {
			//Fetch data
			results.setData(criteria.list());
			Collection<Route> acceptedRoutes = new ArrayList<>();

			// Search Rating
			DistanceOp distanceOp;
			Geometry geomFrom;
			Geometry geomTo;
			WKTReader fromText = new WKTReader();
			try {
				geomFrom = fromText.read(routeSearchCriteria.getSearchLocationFrom());
				geomFrom.setSRID(4326);

				geomTo = fromText.read(routeSearchCriteria.getSearchLocationTo());
				geomTo.setSRID(4326);
			} catch (ParseException e) {
				throw new RuntimeException("Not a WKT string");
			}

			double distanceFrom;
			double distanceTo;
			double routeLength;
			int routeStops;
			boolean acceptRoute;
			for (Route route : results.getData()) {
				distanceFrom = Double.MAX_VALUE;
				distanceTo = Double.MAX_VALUE;
				routeStops = 0;

				// Examine route's direction and reject wrong direction routes
				SQLQuery qry1 = session.createSQLQuery("select ST_Length(ST_Line_Substring(path, ST_Line_Locate_Point(path, ST_StartPoint(path)), ST_Line_Locate_Point(path, ST_SetSRID(ST_MakePoint(" + routeSearchCriteria.getSearchLocationFrom().replace(" ", ", ").replace("POINT(", "") + ",4326)))) from crp_route_paths where route_id = " + route.getId());
				Double length1 = (Double) qry1.list().get(0);
				SQLQuery qry2 = session.createSQLQuery("select ST_Length(ST_Line_Substring(path, ST_Line_Locate_Point(path, ST_StartPoint(path)), ST_Line_Locate_Point(path, ST_SetSRID(ST_MakePoint(" + routeSearchCriteria.getSearchLocationTo().replace(" ", ", ").replace("POINT(", "") + ",4326)))) from crp_route_paths where route_id = " + route.getId());
				Double length2 = (Double) qry2.list().get(0);

				acceptRoute = length1 <= length2;

				if (acceptRoute) {
					// Calculate hit ratio
   				    Geometry routeStart, routeEnd;
					for (RouteSegment routeSegment : route.getRouteSegments()) {
						try {
							routeStart = fromText.read("POINT(" + routeSegment.getStartLat() + " " + routeSegment.getStartLng() + ")");
							routeStart.setSRID(4326);

							routeEnd = fromText.read("POINT(" + routeSegment.getEndLat() + " " + routeSegment.getEndLng() + ")");
							routeEnd.setSRID(4326);
						} catch (ParseException e) {
							throw new RuntimeException("Not a WKT string");
						}

						distanceOp = new DistanceOp(routeEnd, geomFrom);
						distanceFrom = distanceOp.distance();

						distanceOp = new DistanceOp(routeEnd, geomTo);
						distanceTo = distanceOp.distance();

						routeStops++;
					}

					SQLQuery qryLength = session.createSQLQuery("select ST_Length(path) from crp_route_paths where route_id = " + route.getId());
					routeLength = (Double) qryLength.list().get(0);

					route.setRouteRating((distanceFrom + distanceTo) * (sysParameter
							.getRouteDistanceWeight() / 100) + (routeLength * sysParameter
							.getRouteLengthWeight() / 100) + routeStops * (sysParameter.getRouteStopsWeight() / 100));

					acceptedRoutes.add(route);
					System.out.println("Rating: " + route.getRouteRating());
				}
			}

			if (routeSearchCriteria.getSortOrder() == 1) { // Hit Ratio
				Comparator<Route> comparator = new Comparator<Route>() {
					public int compare(Route c1, Route c2) {
						return c1.getRouteRating().compareTo(c2.getRouteRating());
					}
				};

//				List<Route> list = new ArrayList<>(results.getData());
				List<Route> list = new ArrayList<>(acceptedRoutes);
				Collections.sort(list, comparator);
				results.setData(list);
				results.setTotalRecords(list.size());
			}
		} else {
			// Paging
			criteria.setFirstResult(routeSearchCriteria.getPageNumber() * routeSearchCriteria.getPageSize());
			criteria.setMaxResults(routeSearchCriteria.getPageSize());

			// Fetch data
			results.setData(criteria.list());
			for (Route route : results.getData()) {
				route.setRouteRating(0.0);
			}
		}

		return results;
	}

	@Override
	public Collection<Route> findCreatedRoutes(Date dateFrom, Date dateTo) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Route> routes;
		Criteria criteria = session.createCriteria(Route.class);

		criteria.add(Restrictions.ge("routeCreationDate", dateFrom));
		criteria.add(Restrictions.le("routeCreationDate", dateTo));
		routes = criteria.list();

		return routes;
	}

	@Override
	public Collection<Route> findCompletedRoutes(Date dateFrom, Date dateTo) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Route> routes;
		Criteria criteria = session.createCriteria(Route.class);

		criteria.add(Restrictions.ge("routeDate", dateFrom));
		criteria.add(Restrictions.le("routeDate", dateTo));
		routes = criteria.list();

		return routes;
	}

	@Override
	public SearchResults<Route> findCommonRoutes(Route route, RouteInterest routeInterest,
	                                             RouteInterestSearchCriteria routeInterestSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Route> results = new SearchResults<>();
		String orderField;
		String orderDirection;

		//sorting
		if (routeInterestSearchCriteria.getOrderField() != null) {

			orderField = routeInterestSearchCriteria.getOrderField();
			if (routeInterestSearchCriteria.getOrderDirection().equals("ascending")) {
				orderDirection = "ASC";
			} else {
				orderDirection = "DESC";
			}
		} else {
			orderField = "id";
			orderDirection = "ASC";
		}

		String hql = "select route " +
				"from Route as route, RouteInterest as routeInterest " +
				"where route.user.id=:driverId " +
				"and route.id = routeInterest.route.id " +
				"and routeInterest.user.id=:passengerId " +
				"and routeInterest.status=:routeInterestStatus " +
				"order by route." + orderField + " " + orderDirection;

		Query query = session.createQuery(hql);
		query.setParameter("driverId", route.getUser().getId());
		query.setParameter("passengerId", routeInterest.getUser().getId());
		query.setParameter("routeInterestStatus", routeInterest.STATUS_APPROVED);

		//Total records
		results.setTotalRecords(query.list().size());

		//paging
		query.setFirstResult(routeInterestSearchCriteria.getPageNumber() * routeInterestSearchCriteria.getPageSize());
		query.setMaxResults(routeInterestSearchCriteria.getPageSize());

		//Fetch data
		results.setData(query.list());

		return results;
	}

	@Override
	public void save(Route route) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(route);
	}

	@Override
	public void delete(Route route) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(route);
	}
}
