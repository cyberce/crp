package org.teiath.data.dao;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteNotificationCriteria;

import java.util.Collection;

public interface RouteNotificationCriteriaDAO {

	public void save(RouteNotificationCriteria routeNotificationCriteria);

	public void delete(RouteNotificationCriteria routeNotificationCriteria);

	public Collection<RouteNotificationCriteria> checkCriteria(Route route);

	public RouteNotificationCriteria findById(Integer id);
}
