package com.healthedge.connector.healthcheck.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jtripathy on 5/23/17.
 */
public class HealthCheckBean implements Serializable{

    private String allStatus = StatusType.OK.name();
    private String oltpConnectionStatus = StatusType.NA.name();
    private String dwConnectionStatus = StatusType.NA.name();
    private String cmDBConnectionStatus = StatusType.NA.name();
    private String cmIntegrationServicesStatus = StatusType.NA.name();
    private String mpDBConnectionStatus = StatusType.NA.name();
    private String payorRemoteConnectionStatus = StatusType.NA.name();
    private String wlClassicServiceStatus = StatusType.NA.name();
    private String jmsConnectionStatus = StatusType.NA.name();
    private String fileFetchStatus = StatusType.NA.name();
    private String elasticsearchStatus = StatusType.NA.name();
    private List<ServiceBean> servicesList;

    public String getAllStatus() {
        return allStatus;
    }

    public void setAllStatus(String allStatus) {
        this.allStatus = allStatus;
    }

    public String getOltpConnectionStatus() {
        return oltpConnectionStatus;
    }

    public void setOltpConnectionStatus(String oltpConnectionStatus) {
        this.oltpConnectionStatus = oltpConnectionStatus;
    }

    public String getDwConnectionStatus() {
        return dwConnectionStatus;
    }

    public void setDwConnectionStatus(String dwConnectionStatus) {
        this.dwConnectionStatus = dwConnectionStatus;
    }

    public String getCmDBConnectionStatus() {
        return cmDBConnectionStatus;
    }

    public void setCmDBConnectionStatus(String cmDBConnectionStatus) {
        this.cmDBConnectionStatus = cmDBConnectionStatus;
    }

    public String getCmIntegrationServicesStatus() {
        return cmIntegrationServicesStatus;
    }

    public void setCmIntegrationServicesStatus(String cmIntegrationServicesStatus) {
        this.cmIntegrationServicesStatus = cmIntegrationServicesStatus;
    }

    public String getMpDBConnectionStatus() {
        return mpDBConnectionStatus;
    }

    public void setMpDBConnectionStatus(String mpDBConnectionStatus) {
        this.mpDBConnectionStatus = mpDBConnectionStatus;
    }

    public String getPayorRemoteConnectionStatus() {
        return payorRemoteConnectionStatus;
    }

    public void setPayorRemoteConnectionStatus(String payorRemoteConnectionStatus) {
        this.payorRemoteConnectionStatus = payorRemoteConnectionStatus;
    }

    public String getWlClassicServiceStatus() {
        return wlClassicServiceStatus;
    }

    public void setWlClassicServiceStatus(String wlClassicServiceStatus) {
        this.wlClassicServiceStatus = wlClassicServiceStatus;
    }

    public String getJmsConnectionStatus() {
        return jmsConnectionStatus;
    }

    public void setJmsConnectionStatus(String jmsConnectionStatus) {
        this.jmsConnectionStatus = jmsConnectionStatus;
    }

    public String getFileFetchStatus() {
        return fileFetchStatus;
    }

    public void setFileFetchStatus(String fileFetchStatus) {
        this.fileFetchStatus = fileFetchStatus;
    }

    public String getElasticsearchStatus() {
        return elasticsearchStatus;
    }

    public void setElasticsearchStatus(String elasticsearchStatus) {
        this.elasticsearchStatus = elasticsearchStatus;
    }

    public List<ServiceBean> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<ServiceBean> servicesList) {
        this.servicesList = servicesList;
    }
}
