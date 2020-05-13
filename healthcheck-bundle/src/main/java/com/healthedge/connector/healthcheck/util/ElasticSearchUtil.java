package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtripathy
 *
 */
public class ElasticSearchUtil {
    private static final Logger LOGGER = Logger.getLogger(ElasticSearchUtil.class.getName());

    public String testESHealth() {
        String result = StatusType.NOT_OK.name();
        HttpURLConnection urlConnection = null;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String url = "http://"+ip.getHostName()+":9200/_cat/health";
            LOGGER.info("URL: " + url);
            URL esURL = new URL(url);
            urlConnection = (HttpURLConnection)esURL.openConnection();
            urlConnection.setDoOutput(true);
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

            if(!isNullOrTrimmedEmpty(sbf.toString()) && HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()
                    && (sbf.toString().contains("green") || sbf.toString().contains("yellow")) ){
                LOGGER.info(sbf.toString());
                result = StatusType.OK.name();
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in testESHealth: ", e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    private boolean isNullOrTrimmedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
