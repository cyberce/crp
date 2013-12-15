package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.SmsMessage;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteInterest;
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

@Service("listRouteInterestsPassengerService")
@Transactional
public class ListRouteInterestsPassengerServiceImpl
		implements ListRouteInterestsPassengerService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	UserDAO userDAO;
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

	@Override
	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteInterest> results;

		try {
			results = routeInterestDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<Vehicle> getVehiclesByPassenger(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByPassenger(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	public Collection<User> getDriversByPassenger(User user)
			throws ServiceException {
		Collection<User> users;

		try {
			users = userDAO.findDriversByPassenger(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return users;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteRouteInterest(RouteInterest routeInterest)
			throws ServiceException {
		try {
			routeInterestDAO.delete(routeInterest);

			//Create and save Notification
			Notification notification = new Notification();
			notification.setUser(routeInterest.getRoute().getUser());
			notification.setRoute(routeInterest.getRoute());
			notification.setType(Notification.TYPE_ROUTES);
			notification.setSentDate(new Date());
			notification.setTitle("Απόσυρση ενδιαφέροντος για διαδρομή");
			String body = notificationProperties.getNotificationInterestWithdrawBody()
					.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2", "διαδρομή");
			notification.setBody(body);
			notificationDAO.save(notification);

			if (routeInterest.getRoute().getUser().isEmailNotifications()) {
				//Create and send Email
				String mailSubject = emailProperties.getRouteInterestWithdrawSubject()
						.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
				StringBuilder htmlMessageBuiler = new StringBuilder();
				htmlMessageBuiler.append("<html> <body>");
				String mailBody = emailProperties.getRouteInterestWithdrawBody()
						.replace("$1", routeInterest.getUser().getFullName()).replace("$2", "διαδρομή");
				htmlMessageBuiler.append(mailBody);
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Κωδικός: "  + routeInterest.getRoute().getCode());
				htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
						.format(routeInterest.getRoute().getRouteDate()));
				htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm").format(routeInterest.getRoute().getRouteTime()));
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Ονοματεπώνυμο συνεπιβάτη: " + routeInterest.getUser().getFullName());
				if (routeInterest.getUser().getAveragePassengerRating() != null) {
					htmlMessageBuiler
							.append("<br>Μέση αξιολόγηση: " + routeInterest.getUser().getAveragePassengerRating());
				}
				htmlMessageBuiler.append("</body> </html>");
				mailManager.sendMail(emailProperties.getFromAddress(), routeInterest.getRoute().getUser().getEmail(),
						mailSubject, htmlMessageBuiler.toString());
			}

			if ((routeInterest.getStatus() == RouteInterest.STATUS_APPROVED) && (routeInterest.getRoute().getUser().isSmsNotifications()) && sysParameterService.fetchSystemParameters().isSmsEnabledDisapproveInterest()) {
				smsManager.sendSMS(routeInterest.getUser().getMobileNumber().toString(),
						smsManager.fetchMessage(SmsMessage.ROUTE_INTEREST_REMOVE)
								.replace("$1", routeInterest.getUser().getFullName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
