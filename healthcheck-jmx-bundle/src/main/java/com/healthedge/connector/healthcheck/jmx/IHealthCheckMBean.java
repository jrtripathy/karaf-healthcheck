package com.healthedge.connector.healthcheck.jmx;

import javax.management.openmbean.TabularData;

/**
 * Created by jtripathy on 6/29/17.
 */
public interface IHealthCheckMBean {

    TabularData health() throws Exception;
}
