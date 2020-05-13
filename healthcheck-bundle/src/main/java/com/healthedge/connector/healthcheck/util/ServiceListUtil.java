package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.command.SystemCommandExecutor;
import com.healthedge.connector.healthcheck.model.ServiceBean;
import com.healthedge.connector.healthcheck.model.StatusType;
import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy on 5/25/17.
 */
public class ServiceListUtil {
    private static final Logger LOGGER = Logger.getLogger(ServiceListUtil.class.getName());

    public List<ServiceBean> getServicesStatus(FeaturesService featuresService,
                                               BundleContext bundleContext,
                                               String inURL){
        List<ServiceBean> servicesList = new ArrayList<>();
        List<String> endPointList = new ArrayList<>();
        try {
            inURL = inURL.substring(0, inURL.indexOf("/rest"));
            String basePath = System.getProperty("karaf.base");
            String baseUrl = inURL + "/services";
            for(Feature f : featuresService.listInstalledFeatures()){
                if(f.getName().endsWith("repos")) {
                    List<BundleInfo> bundleInfos = f.getBundles();
                    if (!bundleInfos.isEmpty()) {
                        for (BundleInfo featureBundleInfo : bundleInfos) {
                            if(featureBundleInfo.getLocation().contains("-bundle") && filteredBundle(featureBundleInfo.getLocation())) {
                                Bundle bundle = bundleContext.getBundle(featureBundleInfo.getLocation());
                                List<String> uriList = getServiceLinksFromCache(basePath + "/data/cache/bundle" + bundle.getBundleId());
                                if(!uriList.isEmpty()){
                                    endPointList.addAll(uriList);
                                }
                            }
                        }
                    }
                }
            }
            // look for connector blueprints
            List<String> connList = getServiceLinksFromXML(basePath + "/connector-blueprints-deploy");
            if(!connList.isEmpty()){
               endPointList.addAll(connList);
            }
            // look for custom blueprints
            List<String> customList = getServiceLinksFromXML(basePath + "/custom-blueprints-deploy");
            if(!customList.isEmpty()){
                endPointList.addAll(customList);
            }

            populateServiceList(servicesList, endPointList, baseUrl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Healthcheck service list: ", e);
        }

        if (!servicesList.isEmpty()) {
            servicesList.sort((s1, s2) -> s1.getServiceName().compareTo(s2.getServiceName()));
        }
        return servicesList;
    }

    private void populateServiceList(List<ServiceBean> servicesList,  List<String> endPointList,
                                     String baseUrl){
        //LOGGER.info(endPointList.toString());
        HashSet<String> endPointSet = new HashSet<>(endPointList);
        for(String endPoint : endPointSet){
            if(!endPoint.contains("payor.server") && endPoint.indexOf('/') != -1) {
                String url = baseUrl + endPoint + "?wsdl";
                if(endPoint.contains("rest")){
                    //try rest url.
                   url = baseUrl + endPoint + "?_wadl";
                }

                String status = getStatus(url);
                ServiceBean bean = new ServiceBean();
                bean.setServiceName(endPoint.substring(endPoint.lastIndexOf('/')+1));
                bean.setAddress(baseUrl + endPoint);
                bean.setStatus(status);
                servicesList.add(bean);
            }
        }
    }

    private String getStatus(String url){

        String result = StatusType.NOT_OK.name();
        HttpURLConnection connection = null;
        try {
            LOGGER.info("Endpoint: " + url);
            URL wsdlURL = new URL(url);
            connection = (HttpURLConnection) wsdlURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            LOGGER.info("ResponseCode: " + connection.getResponseCode());
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                result = StatusType.OK.name();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Healthcheck WSDL: ", e);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private List<String> getServiceLinksFromCache(String path) throws Exception{
        List<String> paths = new ArrayList<>();

        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add("cd " + path + ";find -name *.jar");
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = commandExecutor.executeCommand();
        List<String> stdout = commandExecutor.getStandardOutputFromCommand();
        //LOGGER.info("1: " + stdout.toString());
        String jarPath = stdout.get(0).replaceFirst(".", "");
        int index = jarPath.lastIndexOf('/');

        commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add("cd " + path+jarPath.substring(0, index) + ";unzip -p bundle.jar *.xml | grep address=");
        // execute the 2nd command
        commandExecutor = new SystemCommandExecutor(commands);
        result = commandExecutor.executeCommand();
        stdout = commandExecutor.getStandardOutputFromCommand();
        //LOGGER.info("2: " + stdout.toString());

        if(!stdout.isEmpty()) {
            for (String s : stdout) {
                int bIndex = s.indexOf("address=")+9;
                int eIndex = s.indexOf("\"", bIndex);
                if(eIndex != -1){
                    s = s.substring(bIndex, eIndex);
                }else{
                    s = s.substring(bIndex);
                }

                if(!isNullOrTrimmedEmpty(s) && !s.contains("wsdlURL")){
                    paths.add(s.trim());
                }
            }
        }

        return paths;
    }

    private boolean isNullOrTrimmedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    private List<String> getServiceLinksFromXML(String path) throws Exception{
        List<String> paths = new ArrayList<>();

        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add("cd " + path + ";grep address= *.xml");
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = commandExecutor.executeCommand();
        List<String> stdout = commandExecutor.getStandardOutputFromCommand();
        //LOGGER.info("1: " + stdout.toString());

        if(!stdout.isEmpty()) {
            for (String s : stdout) {
                int bIndex = s.indexOf("address=")+9;
                int eIndex = s.indexOf("\"", bIndex);
                if(eIndex != -1){
                    s = s.substring(bIndex, eIndex);
                }else{
                    s = s.substring(bIndex);
                }

                if(!isNullOrTrimmedEmpty(s) && !s.contains("wsdlURL")){
                    paths.add(s.trim());
                }
            }
        }

        return paths;
    }

    private boolean filteredBundle(String location){
        boolean bResult = true;
        if(location.contains("-payor")){
            bResult = false;
        }else if(location.contains("-cm-bundle")){
            bResult = false;
        }else if(location.contains("connector-config")){
            bResult = false;
        }else if(location.contains("-schema")){
            bResult = false;
        }else if(location.contains("-weblogic")){
            bResult = false;
//        }else if(location.contains("-claimsxten")){
//            bResult = false;
        }else if(location.contains("-marketprominence")){
            bResult = false;
        }else if(location.contains("-healthx-") || location.contains("-hx-")){
            bResult = false;
        }else if(location.contains("-bam-")){
            bResult = false;
        }else if(location.contains("connector-extract-")){
            bResult = false;
        }

        return bResult;
    }

    /*public List<ServiceBean> getServicesStatus(){
        List<ServiceBean> servicesList = new ArrayList<>();
        try {
            // need http protocol
            Document doc = Jsoup.connect("http://"+ip.getHostName()+":9191/connector/services").get();
            // get page title
            String title = doc.title();
            LOGGER.info("title : " + title);

            // get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                // get the value from href attribute
                ServiceBean bean = new ServiceBean();
                //System.out.println("\nlink : " + link.attr("href"));
                //System.out.println("text : " + link.text());
                bean.setServiceName(link.attr("href"));
                bean.setStatus(getStatus(link.attr("href")));
                servicesList.add(bean);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error in Healthcheck service list: ", e);
        }

        return servicesList;
    } */
}
