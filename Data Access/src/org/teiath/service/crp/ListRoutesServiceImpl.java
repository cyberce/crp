package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.SmsMessage;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.UserAction;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.data.sms.SmsManager;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.sys.SysParameterService;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("listRoutesService")
@Transactional
public class ListRoutesServiceImpl
		implements ListRoutesService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;
	@Autowired
	private SmsManager smsManager;
	@Autowired
	private SysParameterService sysParameterService;
	@Autowired
	UserActionDAO userActionDAO;

	@Override
	public SearchResults<Route> searchRoutesByCriteria(RouteSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Route> results;

		try {
			results = routeDAO.search(criteria, false);
			for (Route route : results.getData()) {
				route.getRouteInterests().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteRoute(Route route)
			throws ServiceException {
		SearchResults<RouteInterest> results;
		RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();

		routeInterestSearchCriteria.setRoute(route);
		try {
			results = routeInterestDAO.search(routeInterestSearchCriteria);
			for (RouteInterest routeInterest : results.getData()) {

				//Create and save Notification
				Notification notification = new Notification();
				notification.setUser(routeInterest.getUser());
				notification.setType(Notification.TYPE_ROUTES);
				notification.setSentDate(new Date());
				notification.setTitle("Κατάργηση Διαδρομής");
				String body = notificationProperties.getNotificationDeleteBody()
						.replace("$1", route.getUser().getFullName()).replace("$2", "διαδρομή");
				notification.setBody(body);
				notificationDAO.save(notification);

				if (routeInterest.getUser().isEmailNotifications()) {
					//Create and send Email
					String mailSubject = emailProperties.getRouteDeleteSubject()
							.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
					StringBuilder htmlMessageBuiler = new StringBuilder();
					htmlMessageBuiler.append("<html> <body>");
					String mailBody = emailProperties.getRouteDeleteBody().replace("$1", route.getUser().getFullName())
							.replace("$2", "διαδρομή");
					htmlMessageBuiler.append(mailBody);
					htmlMessageBuiler.append("<br>");
					htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
							.format(route.getRouteDate()));
					htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm").format(route.getRouteTime()));
					htmlMessageBuiler.append("<br>Ονοματεπώνυμο οδηγού: " + route.getUser().getFullName());
					htmlMessageBuiler.append("<br>Αφετηρία: " + route.getStartingPoint());
					htmlMessageBuiler.append("<br>Προορισμός: " + route.getDestinationPoint());
					htmlMessageBuiler.append("<br>Όχημα: " + route.getVehicle().getFullName());
					htmlMessageBuiler.append("</body> </html>");
					mailManager
							.sendMail(emailProperties.getFromAddress(), routeInterest.getUser().getEmail(), mailSubject,
									htmlMessageBuiler.toString());
				}

				if ((routeInterest.getStatus() == RouteInterest.STATUS_APPROVED) && (routeInterest.getUser()
						.isSmsNotifications()) && sysParameterService.fetchSystemParameters().isSmsEnabledDeleteRoute()) {
					smsManager.sendSMS(routeInterest.getUser().getMobileNumber().toString(),
							smsManager.fetchMessage(SmsMessage.ROUTE_REMOVE)
									.replace("$1", route.getUser().getFullName()));
				}
			}

			routeDAO.delete(route);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteRouteInterests(Route route)
			throws ServiceException {
		try {
			routeInterestDAO.deleteByRoute(route);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void sendDeleteNotifications(Route route)
			throws ServiceException {

		try {
			for (RouteInterest routeInterest : route.getRouteInterests()) {

				if (routeInterest.getStatus() == RouteInterest.STATUS_APPROVED) {
					//Create and save Notification
					Notification notification = new Notification();
					notification.setUser(routeInterest.getUser());
					notification.setType(Notification.TYPE_ROUTES);
					notification.setSentDate(new Date());
					notification.setTitle("Κατάργηση Διαδρομής");
					String body = notificationProperties.getNotificationDeleteBody()
							.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2",
									routeInterest.getRoute().getStartingPoint()
											.substring(0, routeInterest.getRoute().getStartingPoint().indexOf(',')))
							.replace("$3", routeInterest.getRoute().getDestinationPoint()
									.substring(0, routeInterest.getRoute().getDestinationPoint().indexOf(',')));
					notification.setBody(body);
					notificationDAO.save(notification);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createUserAction(Route route)
			throws ServiceException {

		//create and save user action trace
		UserAction userAction = new UserAction();
		userAction.setDate(new Date());
		userAction.setUser(route.getUser());
		userAction.setStartingPoint(route.getStartingPoint());
		userAction.setDestination(route.getDestinationPoint());
		userAction.setRouteDate(route.getRouteDate());
		userAction.setRouteCode(route.getCode());
		userAction.setVehicle(route.getVehicle().getFullName());
		userAction.setType(UserAction.TYPE_DELETE);
		userActionDAO.save(userAction);
	}
}
