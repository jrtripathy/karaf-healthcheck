package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtripathy
 *
 */
public class FileFetchUtil {
    private static final Logger LOGGER = Logger.getLogger(ServiceListUtil.class.getName());
    private String username;
    private String password;
    private String url;

    public String testFecthFile() {
        String result = StatusType.NOT_OK.name();
        HttpURLConnection urlConnection = null;
        try {
            url = url.replace("Upload", "Fetch")+"?docId=1111";
            LOGGER.info("URL: " + url);
            URL fileURL = new URL(url);
            urlConnection = (HttpURLConnection)fileURL.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeString(username+":"+password));
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer sbf = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                sbf.append(inputLine);
            }
            reader.close();
            is.close();

            LOGGER.info(sbf.toString());
            LOGGER.info("ResponseCode: " + urlConnection.getResponseCode());
            if(!isNullOrTrimmedEmpty(sbf.toString()) && (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()
            || HttpURLConnection.HTTP_ACCEPTED == urlConnection.getResponseCode())){
                result = StatusType.OK.name();
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in FileFetch: ", e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    public String testWLClassicService() {
        String result = StatusType.NOT_OK.name();
        HttpURLConnection urlConnection = null;
        try {
            String classicUrl = url.substring(0, url.indexOf("he-srvlt")) + "integration-web-services/MemberServiceStronglyTyped";
            LOGGER.info("Endpoint: " + classicUrl);
            URL wsdlURL = new URL(classicUrl);
            urlConnection = (HttpURLConnection) wsdlURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            LOGGER.info("ResponseCode: " + urlConnection.getResponseCode());
            if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
                result = StatusType.OK.name();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in testWLClassicService: ", e);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private boolean isNullOrTrimmedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
