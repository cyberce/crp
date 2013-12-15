package org.teiath.webservices.viewers;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import org.teiath.data.properties.EmailProperties;
import org.teiath.webservices.model.ServiceNotification;
import org.teiath.webservices.model.ServiceNotificationList;
import org.teiath.webservices.model.ServiceRoute;
import org.teiath.webservices.model.ServiceRouteList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RssViewer
		extends AbstractRssFeedView {

	@Autowired
	private EmailProperties emailProperties;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
		feed.setTitle("RSS Web Service");
		feed.setDescription("Feed provided by T.E.I of Athens");
		feed.setLink("www.teiath.gr");
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
	                                    HttpServletResponse response)
			throws Exception {

		List<Item> items = new ArrayList<>();
		response.setContentType("application/xml;charset=UTF-8");
		if (model.get("serviceRouteList") instanceof ServiceRouteList) {
			ServiceRouteList serviceRouteList = (ServiceRouteList) model.get("serviceRouteList");

			if (serviceRouteList != null) {
				// Create feed items
				Item item;
				for (ServiceRoute serviceRoute : serviceRouteList.getServiceRoutes()) {
					item = new Item();
					item.setAuthor("TEI of Athens Carpooling Service");
					item.setTitle(serviceRoute.getStartingPoint() + "-" + serviceRoute.getDestinationPoint());
					item.setPubDate(serviceRoute.getRouteCreationDate());
					item.setLink(emailProperties.getDomain() + "previewRoute?code=" + serviceRoute.getCode());
					items.add(item);
				}
			}
		}

		if (model.get("serviceRoute") instanceof ServiceRoute) {
			ServiceRoute serviceRoute = (ServiceRoute) model.get("serviceRoute");

			if (serviceRoute != null) {
				// Create feed items
				Item item;
				item = new Item();
				item.setAuthor(serviceRoute.getDriverName());
				item.setTitle(serviceRoute.getStartingPoint() + "-" + serviceRoute.getDestinationPoint());
				item.setPubDate(serviceRoute.getRouteCreationDate());
				item.setLink(emailProperties.getDomain() + "previewRoute?code=" + serviceRoute.getCode());
				items.add(item);
			}
		}

		if (model.get("serviceNotificationList") instanceof ServiceNotificationList) {
			ServiceNotificationList serviceNotificationList = (ServiceNotificationList) model.get("serviceNotificationList");

			if (serviceNotificationList != null) {
				// Create feed items
				Item item;
				for (ServiceNotification serviceNotification : serviceNotificationList.getServiceNotifications()) {
					item = new Item();
					item.setAuthor("TEI of Athens Carpooling Service");
					item.setTitle(serviceNotification.getTitle());
					item.setComments(serviceNotification.getBody());
					item.setPubDate(serviceNotification.getSentDate());
					if (serviceNotification.getRouteCode() != null)
						item.setLink(emailProperties.getDomain() + "previewRoute?code=" + serviceNotification.getRouteCode());
					else
						item.setLink(emailProperties.getDomain());
					items.add(item);
				}
			}
		}

		return items;
	}
}
