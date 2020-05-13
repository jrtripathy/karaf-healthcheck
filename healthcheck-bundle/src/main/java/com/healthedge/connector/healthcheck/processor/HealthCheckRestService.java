package com.healthedge.connector.healthcheck.processor;

import com.healthedge.connector.healthcheck.model.HealthCheckBean;

/**
 * Created by jtripathy on 5/23/17.
 */

public interface HealthCheckRestService {

    /**
     * Returns overall status of connector features and components.
     * @return HealthCheckBean
     */
    HealthCheckBean getStatus(String inURL);
}
