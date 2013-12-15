package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.dao.UserRoleDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.data.domain.crp.License;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.util.PasswordService;

import java.util.Collection;

@Service("createUserService")
@Transactional
public class CreateUserServiceImpl
		implements CreateUserService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private UserRoleDAO userRoleDAO;

	@Override
		 @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		 public void saveUser(User user)
			throws ServiceException {
		try {
			user.setPassword(PasswordService.encrypt(user.getPassword()));
			user.setCode(PasswordService.encrypt(user.getUserName()));
			userDAO.save(user);

			//Create and send Email
			String mailSubject = emailProperties.getUserCreateSubject();
			String mailBody = emailProperties.getUserCreateBody().replace("$1", user.getPassword());
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			htmlMessageBuiler.append(mailBody);
			htmlMessageBuiler.append(" <a href=\"https://carpooling.teiath.gr/\">συνδέσμου</a>.");
			htmlMessageBuiler.append("</body> </html>");
			mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, htmlMessageBuiler.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public UserRole getDefaultUserRole(int defaultUserRoleCode)
			throws ServiceException {
		UserRole userRole;
		try {
			userRole = userRoleDAO.findDefaultUserRole(defaultUserRoleCode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return userRole;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void notifyAdmins(User user)
			throws ServiceException {
		try {
			Collection<User> admins;

			admins = userDAO.findAdmins();

			for (User admin : admins) {
				//Create and send Email
				String mailSubject = emailProperties.getDriverApplicationSubject();
				String mailBody = emailProperties.getDriverApplicationBody().replace("$1", user.getFullName());
				mailManager.sendMail(emailProperties.getFromAddress(), admin.getEmail(), mailSubject, mailBody);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
