package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ExtraPassengerDAO;
import org.teiath.data.dao.NotificationDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.zul.ListModelList;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service("createRouteInterestService")
@Transactional
public class CreateRouteInterestServiceImpl
		implements CreateRouteInterestService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	ExtraPassengerDAO extraPassengerDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findById(routeId);
			route.getRouteInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveRouteInterest(RouteInterest routeInterest, ListModelList<ExtraPassenger> extraPassengers)
			throws ServiceException {
		try {
			routeInterestDAO.save(routeInterest);

			//save Extra Passengers
			for (ExtraPassenger extraPassenger : extraPassengers) {
				ExtraPassenger newExtraPassenger = new ExtraPassenger();
				newExtraPassenger.setRouteInterest(routeInterest);
				newExtraPassenger.setName(extraPassenger.getName());
				extraPassengerDAO.save(newExtraPassenger);
			}

			//Create and save Notification
			Notification notification = new Notification();
			notification.setUser(routeInterest.getRoute().getUser());
			notification.setRoute(routeInterest.getRoute());
			notification.setType(Notification.TYPE_ROUTES);
			notification.setSentDate(new Date());
			notification.setTitle("Νεα εκδήλωση ενδιαφέροντος για διαδρομή");
			String body = notificationProperties.getNotificationInterestBody()
					.replace("$1", routeInterest.getUser().getFullName()).replace("$2", "διαδρομή");
			notification.setBody(body);
			notificationDAO.save(notification);

			if (routeInterest.getRoute().getUser().isEmailNotifications()) {
				//Create and send Email
				String mailSubject = emailProperties.getRouteInterestSubject()
						.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
				StringBuilder htmlMessageBuiler = new StringBuilder();
				htmlMessageBuiler.append("<html> <body>");
				String mailBody = emailProperties.getRouteInterestBody()
						.replace("$1", routeInterest.getUser().getFullName()).replace("$2", "διαδρομή");
				htmlMessageBuiler.append(mailBody);
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Κωδικός: "  + routeInterest.getRoute().getCode());
				htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
						.format(routeInterest.getRoute().getRouteDate()));
				htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm").format(routeInterest.getRoute().getRouteTime()));

				htmlMessageBuiler.append("<br>Ονοματεπώνυμο συνεπιβάτη: " + routeInterest.getUser().getFullName());
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>e-mail: " + routeInterest.getUser().getEmail());
				htmlMessageBuiler.append("<br>");
				if (routeInterest.getUser().getMobileNumber() != null) {
					htmlMessageBuiler.append("<br>Τηλέφωνο επικοινωνίας: " + routeInterest.getUser()
							.getAveragePassengerRating());
				}
				if (routeInterest.getUser().getAveragePassengerRating() != null) {
					htmlMessageBuiler
							.append("<br>Μέση αξιολόγηση: " + routeInterest.getUser().getAveragePassengerRating());
				}
				htmlMessageBuiler.append("</body> </html>");
				mailManager.sendMail(emailProperties.getFromAddress(), routeInterest.getRoute().getUser().getEmail(),
						mailSubject, htmlMessageBuiler.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
