package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAssessmentSearchCriteria;

import java.util.Collection;

public interface RouteAssessmentDAO {

	public RouteAssessment findById(Integer id);

	public RouteAssessment findDriversRating(Route route, User assessor);

	public SearchResults<RouteAssessment> search(RouteAssessmentSearchCriteria criteria);

	public void save(RouteAssessment routeAssessment);

	public void delete(RouteAssessment routeAssessment);

	public Double getPassengerAverageRating(User user);

	public Double getDriverAverageRating(User user);

	public Collection<RouteAssessment> getPassengerComments(User user);

	public Collection<RouteAssessment> getDriverComments(User user);

	public Collection<RouteAssessment> getPassengerRatings(User passenger, User driver);

	public Collection<RouteAssessment> getDriverRatingsByRoute(Route route);

	public Collection<RouteAssessment> getPassengerRatingsByRoute(Route route);
}
