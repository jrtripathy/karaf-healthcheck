package com.healthedge.connector.healthcheck.jmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.healthedge.connector.healthcheck.model.HealthCheckBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility for http call.
 *
 * @author jtripathy
 *
 */
public class HealthCheckHttpCall {

	final static Logger LOGGER = LoggerFactory.getLogger(HealthCheckHttpCall.class);

	private static final HttpRequestFactory REQUEST_FACTORY = new NetHttpTransport()
	.createRequestFactory(new HttpRequestInitializer() {
		@Override
		public void initialize(HttpRequest request) {
			request.setParser(new JsonObjectParser(new JacksonFactory()));
		}
	});


	public HealthCheckBean getHealthStatus() throws Exception {
		LOGGER.info("inside getHealthStatus");

		HealthCheckBean healthCheckBean = new HealthCheckBean();
		HttpRequest request = getGETRequest("http://localhost:8181/connector/rest/healthcheck");
		HttpResponse response = request.execute();

		if (response.getStatusCode() == 200) {
			ObjectMapper mapper = new ObjectMapper();
			healthCheckBean = mapper.readValue(response.parseAsString(), HealthCheckBean.class);
		}
		if (response != null) {
			response.disconnect();
		}

		LOGGER.info("getHealthStatus(): [" + healthCheckBean + "]");
		return healthCheckBean;
	}

	/**
	 * Gets HTTP GET Request.
	 *
	 * @param url String
	 *
	 * @return HttpRequest
	 *
	 * @throws IOException
	 */
	public HttpRequest getGETRequest(String url) throws IOException {
		return REQUEST_FACTORY.buildGetRequest(new GenericUrl(url));
	}
}