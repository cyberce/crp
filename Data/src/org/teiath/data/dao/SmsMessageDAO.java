package org.teiath.data.dao;

import org.teiath.data.domain.SmsMessage;

import java.util.Collection;

public interface SmsMessageDAO {

	public Collection<SmsMessage> findAll();
}
