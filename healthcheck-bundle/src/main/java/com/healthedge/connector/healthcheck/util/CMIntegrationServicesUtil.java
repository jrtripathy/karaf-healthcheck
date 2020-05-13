package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtripathy
 *
 */
public class CMIntegrationServicesUtil {
    private static final Logger LOGGER = Logger.getLogger(CMIntegrationServicesUtil.class.getName());

    public String testISEndPoint() {
        String result = StatusType.NOT_OK.name();
        HttpURLConnection urlConnection = null;
        try {
            Properties cmConfig = HealthCheckUtil.loadConfig("com.healthedge.connector.caremanager.cfg");
            if(cmConfig != null) {
                String url = cmConfig.getProperty("hrcm.endpoint.url");
                url = url + "/integrationServicesService/integrationServicesPort?wsdl";
                LOGGER.info("URL: " + url);
                URL cmURL = new URL(url);
                urlConnection = (HttpURLConnection) cmURL.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic " +
                        Base64.encodeString(cmConfig.getProperty("hrcm.username") +
                                ":" + HealthCheckUtil.decrypt(cmConfig.getProperty("hrcm.password"))));
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                LOGGER.info("ResponseCode: " + urlConnection.getResponseCode());
                if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
                    result = StatusType.OK.name();
                }
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in testISEndPoint: ", e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }
}
