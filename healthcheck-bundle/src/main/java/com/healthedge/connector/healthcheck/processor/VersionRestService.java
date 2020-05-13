package com.healthedge.connector.healthcheck.processor;

import com.healthedge.connector.healthcheck.model.ConnectorVersionBean;

/**
 * Created by jtripathy on 5/23/17.
 */

public interface VersionRestService {

    /**
     * Returns versions of connector features installed.
     * @return ConnectorVersionBean
     */
    ConnectorVersionBean getVersions();
}
