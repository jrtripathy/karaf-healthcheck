package com.healthedge.connector.healthcheck.model;

import java.io.Serializable;

/**
 * Created by jtripathy on 5/25/17.
 */
public class ServiceBean implements Serializable {

    private String serviceName;
    private String address;
    private String status;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
