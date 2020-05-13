package com.healthedge.connector.healthcheck.jmx;

import com.healthedge.connector.healthcheck.model.HealthCheckBean;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

/**
 * Created by jtripathy on 6/29/17.
 */
public class HealthCheckMBean implements IHealthCheckMBean{

    public HealthCheckMBean() {}

    @Override
    public TabularData health() throws Exception {
        HealthCheckBean healthCheckBean = new HealthCheckHttpCall().getHealthStatus();
        //OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(HealthCheckMBean.class);

        TabularType tt = new TabularType("HealthStatus", "HealthStatus", ct, new String[]{"healthId", "level", "message", "resource"});
        TabularDataSupport rc = new TabularDataSupport(tt);


        return rc;
    }
}
