package com.healthedge.connector.healthcheck.processor.impl;

import com.healthedge.connector.healthcheck.model.HealthCheckBean;
import com.healthedge.connector.healthcheck.model.ServiceBean;
import com.healthedge.connector.healthcheck.model.StatusType;
import com.healthedge.connector.healthcheck.processor.HealthCheckRestService;
import com.healthedge.connector.healthcheck.util.*;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.osgi.framework.BundleContext;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy on 5/23/17.
 */
public class HealthCheckRestServiceImpl implements HealthCheckRestService{
    private static final Logger LOGGER = Logger.getLogger(HealthCheckRestServiceImpl.class.getName());
    private FeaturesService featuresService;
    private BundleContext bundleContext;
    private DataSource hrOLTP;
    private DataSource hrDW;
    private DataSource hrCM;
    private PayorEJBCallUtil ejbCallUtil;
    private ActiveMQUtil activeMQUtil;
    private FileFetchUtil fileFetchUtil;

    public HealthCheckRestServiceImpl() {
        LOGGER.info(HealthCheckRestServiceImpl.class.getName()+" is up and running");
    }

    @Override
    public HealthCheckBean getStatus(String inURL){
        LOGGER.info("inRestURL: " + inURL);
        Feature[] installedFeatures = null;
        try {
            installedFeatures = featuresService.listInstalledFeatures();
        }catch (Exception ex){
            LOGGER.log(Level.SEVERE, "Error in getting installed features: ", ex);
        }
        DBUtil dbUtil = new DBUtil();
        ServiceListUtil serviceListUtil = new ServiceListUtil();
        HealthCheckBean healthCheckBean = new HealthCheckBean();
        if(isInstalled(installedFeatures, "connector-payor-repos")) {
            healthCheckBean.setPayorRemoteConnectionStatus(ejbCallUtil.testRemoteEJBLookup());
            healthCheckBean.setOltpConnectionStatus(dbUtil.testConnection(hrOLTP, "oltp"));
            healthCheckBean.setDwConnectionStatus(dbUtil.testConnection(hrDW, "dw"));
            healthCheckBean.setFileFetchStatus(fileFetchUtil.testFecthFile());
        }
        if(isInstalled(installedFeatures, "connector-classicproxy-repos")) {
            healthCheckBean.setWlClassicServiceStatus(fileFetchUtil.testWLClassicService());
        }
        if(isInstalled(installedFeatures, "connector-jms-repos")) {
            healthCheckBean.setJmsConnectionStatus(activeMQUtil.testJMS());
        }
        if(isInstalled(installedFeatures, "connector-cm-repos")){
            healthCheckBean.setCmDBConnectionStatus(dbUtil.testConnection(hrCM, "cm"));
            CMIntegrationServicesUtil isUtil = new CMIntegrationServicesUtil();
            healthCheckBean.setCmIntegrationServicesStatus(isUtil.testISEndPoint());
        }
        if(isInstalled(installedFeatures, "connector-marketprominence-repos")){
            MPUtil mpUtil = new MPUtil();
            healthCheckBean.setMpDBConnectionStatus(mpUtil.testMPConnection());
        }
        if(isInstalled(installedFeatures, "elasticsearch")){
            ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil();
            healthCheckBean.setElasticsearchStatus(elasticSearchUtil.testESHealth());
        }

        healthCheckBean.setServicesList(serviceListUtil.getServicesStatus(featuresService, bundleContext, inURL));
        checkForAllOK(healthCheckBean);

        return healthCheckBean;
    }

    private void checkForAllOK(HealthCheckBean healthCheckBean){
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getOltpConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getDwConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getCmDBConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getMpDBConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getPayorRemoteConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getJmsConnectionStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getFileFetchStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getWlClassicServiceStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getCmIntegrationServicesStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }
        if(StatusType.NOT_OK.name().equals(healthCheckBean.getElasticsearchStatus())){
            healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
            return;
        }

        for(ServiceBean serviceBean : healthCheckBean.getServicesList()){
            if(StatusType.NOT_OK.name().equals(serviceBean.getStatus())){
                healthCheckBean.setAllStatus(StatusType.NOT_OK.name());
                break;
            }
        }
    }

    private boolean isInstalled(Feature[] installedFeatures, String name){
        boolean bResult = false;
        if(installedFeatures != null) {
            for (Feature f : installedFeatures) {
                if (f.getName().equals(name)) {
                    bResult = true;
                    break;
                }
            }
        }
        return bResult;
    }

    public DataSource getHrOLTP() {
        return hrOLTP;
    }

    public void setHrOLTP(DataSource hrOLTP) {
        this.hrOLTP = hrOLTP;
    }

    public DataSource getHrDW() {
        return hrDW;
    }

    public void setHrDW(DataSource hrDW) {
        this.hrDW = hrDW;
    }

    public DataSource getHrCM() {
        return hrCM;
    }

    public void setHrCM(DataSource hrCM) {
        this.hrCM = hrCM;
    }

    public PayorEJBCallUtil getEjbCallUtil() {
        return ejbCallUtil;
    }

    public void setEjbCallUtil(PayorEJBCallUtil ejbCallUtil) {
        this.ejbCallUtil = ejbCallUtil;
    }

    public ActiveMQUtil getActiveMQUtil() {
        return activeMQUtil;
    }

    public void setActiveMQUtil(ActiveMQUtil activeMQUtil) {
        this.activeMQUtil = activeMQUtil;
    }

    public FileFetchUtil getFileFetchUtil() {
        return fileFetchUtil;
    }

    public void setFileFetchUtil(FileFetchUtil fileFetchUtil) {
        this.fileFetchUtil = fileFetchUtil;
    }

    public FeaturesService getFeaturesService() {
        return featuresService;
    }

    public void setFeaturesService(FeaturesService featuresService) {
        this.featuresService = featuresService;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
