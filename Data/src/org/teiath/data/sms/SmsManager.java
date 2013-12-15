package org.teiath.data.sms;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.SmsMessageDAO;
import org.teiath.data.dao.SysParameterDAO;
import org.teiath.data.domain.SmsMessage;
import org.teiath.data.properties.SmsProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;

@Service("smsManager")
@Transactional
public class SmsManager {

	@Autowired
	private SmsProperties smsProperties;
	@Autowired
	private SmsMessageDAO smsMessageDAO;

	public SmsManager() {
	}

	public static HashMap<Integer, String> smsMessages = null;

	public String fetchMessage(int id) {
		if (smsMessages == null) {
			smsMessages = new HashMap<>();
			Collection<SmsMessage> messages = smsMessageDAO.findAll();
			for (SmsMessage message : messages) {
				smsMessages.put(message.getId(), message.getMessage());
			}
		}

		return smsMessages.get(id);
	}

	public void sendSMS(String toMobilePhone, String message) {
		try {
			StringEntity myEntity = new StringEntity(message, ContentType.create("text/plain", "UTF-8"));

			URI uri = new URIBuilder().setScheme(smsProperties.getProtocol()).setHost(smsProperties.getHost())
					.setPath(smsProperties.getUrl()).setParameter("username", smsProperties.getUsername())
					.setParameter("api_password", smsProperties.getPassword())
					.setParameter("api_token", smsProperties.getApiToken()).setParameter("to", "30" + toMobilePhone)
					.setParameter("unicode", "0").setParameter("message", EntityUtils.toString(myEntity)).build();

			HttpGet request = new HttpGet(uri);
			request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			CloseableHttpClient httpclient = HttpClients.createDefault();

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}
			};

			httpclient.execute(request, responseHandler);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
