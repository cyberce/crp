package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.SmsMessage;

import java.util.Collection;

@Repository("smsMessageDAO")
public class SmsMessageDAOImpl
		implements SmsMessageDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<SmsMessage> findAll() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from SmsMessage smsMessage order by smsMessage.id asc";
		Query query = session.createQuery(hql);

		return query.list();
	}
}
