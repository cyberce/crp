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
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.sms.SmsManager;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.sys.SysParameterService;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("editRouteService")
@Transactional
public class EditRouteServiceImpl
		implements EditRouteService {

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
	private UserPlaceDAO userPlaceDAO;
	@Autowired
	private SmsManager smsManager;
	@Autowired
	private SysParameterService sysParameterService;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;


	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveRoute(Route route)
			throws ServiceException {

		SearchResults<RouteInterest> results;
		RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();

		routeInterestSearchCriteria.setRoute(route);
		try {
			routeDAO.save(route);
			results = routeInterestDAO.search(routeInterestSearchCriteria);
			for (RouteInterest routeInterest : results.getData()) {

				//Create and save Notification
				Notification notification = new Notification();
				notification.setUser(routeInterest.getUser());
				notification.setRoute(route);
				notification.setType(Notification.TYPE_ROUTES);
				notification.setSentDate(new Date());
				notification.setTitle("Τροποποίηση Διαδρομής");
				String body = notificationProperties.getNotificationEditBody()
						.replace("$1", route.getUser().getFullName()).replace("$2", "διαδρομή");
				notification.setBody(body);
				notificationDAO.save(notification);

				if (routeInterest.getUser().isEmailNotifications()) {
					//Create and send Email
					String mailSubject = emailProperties.getRouteEditSubject()
							.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
					//TODO mail: currently only content
					StringBuilder htmlMessageBuiler = new StringBuilder();
					htmlMessageBuiler.append("<html> <body>");
					String mailBody = emailProperties.getRouteEditBody().replace("$1", route.getUser().getFullName())
							.replace("$2", "διαδρομή");
					htmlMessageBuiler.append(mailBody);
					htmlMessageBuiler.append("<br>");
					htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
							.format(route.getRouteDate()));
					htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm").format(route.getRouteTime()));
					htmlMessageBuiler.append("<br>Ονοματεπώνυμο οδηγού: " + route.getUser().getFullName());
					htmlMessageBuiler.append("<br>e-mail: " + route.getUser().getEmail());
					if (route.getUser().getMobileNumber() != null) {
						htmlMessageBuiler
								.append("<br>Τηλέφωνο επικοινωνίας: " + route.getUser().getAveragePassengerRating());
					}
					htmlMessageBuiler.append("<br>Αφετηρία: " + route.getStartingPoint());
					htmlMessageBuiler.append("<br>Προορισμός: " + route.getDestinationPoint());
					htmlMessageBuiler.append("<br>Όχημα: " + route.getVehicle().getFullName());
					htmlMessageBuiler.append("</body> </html>");
					mailManager
							.sendMail(emailProperties.getFromAddress(), routeInterest.getUser().getEmail(), mailSubject,
									htmlMessageBuiler.toString());
				}

				if ((routeInterest.getStatus() == RouteInterest.STATUS_APPROVED) && (routeInterest.getUser().isSmsNotifications()) && sysParameterService.fetchSystemParameters().isSmsEnabledEditRoute()) {
					smsManager.sendSMS(routeInterest.getUser().getMobileNumber().toString(),
							smsManager.fetchMessage(SmsMessage.ROUTE_CHANGE)
									.replace("$1", route.getUser().getFullName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException {
		Collection<UserPlace> places;

		try {
			places = userPlaceDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return places;
	}

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findById(routeId);
			route.getRouteSegments().iterator();
			route.getRouteInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public Route getRouteByCode(String code)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findByCode(code);
			route.getRouteSegments().iterator();
			route.getRouteInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteInterest> results;

		try {
			results = routeInterestDAO.search(criteria);
			for (RouteInterest routeInterest : results.getData()) {
				routeInterest.getUser().setAveragePassengerRating(
						routeAssessmentDAO.getPassengerAverageRating(routeInterest.getUser()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
