package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAbuseReportDAO;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.ServiceException;

@Service("viewRouteAbuseReportService")
@Transactional
public class ViewRouteAbuseReportServiceImpl
		implements ViewRouteAbuseReportService {

	@Autowired
	RouteAbuseReportDAO routeAbuseReportDAO;
	@Autowired
	UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

	@Override
	public RouteAbuseReport getRouteAbuseReportById(Integer routeAbuseReportId)
			throws ServiceException {
		RouteAbuseReport routeAbuseReport;

		try {
			routeAbuseReport = routeAbuseReportDAO.findById(routeAbuseReportId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAbuseReport;
	}

	@Override
	public void banUser(User user)
			throws ServiceException {
		user.setBanned(true);
		try {
			userDAO.save(user);

			//Create and send Email
			String mailSubject = emailProperties.getUserBanSubject();
			String mailBody = emailProperties.getUserBanBody();
			mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void restoreUser(User user)
			throws ServiceException {
		user.setBanned(false);
		try {
			userDAO.save(user);

			//Create and send Email
			String mailSubject = emailProperties.getUserRestoreSubject();
			String mailBody = emailProperties.getUserRestoreBody();
			mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
