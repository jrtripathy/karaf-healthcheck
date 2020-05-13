package com.healthedge.connector.healthcheck.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jtripathy on 5/23/17.
 */
public class ConnectorVersionBean implements Serializable {

    private String connectorVersion = "UNKNOWN";
    private String customVersion = "UNKNOWN";
    private String karafHome = "UNKNOWN";
    private List<FeatureBean> installedFeatures;

    public String getConnectorVersion() {
        return connectorVersion;
    }

    public void setConnectorVersion(String connectorVersion) {
        this.connectorVersion = connectorVersion;
    }

    public String getCustomVersion() {
        return customVersion;
    }

    public void setCustomVersion(String customVersion) {
        this.customVersion = customVersion;
    }

    public String getKarafHome() {
        return karafHome;
    }

    public void setKarafHome(String karafHome) {
        this.karafHome = karafHome;
    }

    public List<FeatureBean> getInstalledFeatures() {
        return installedFeatures;
    }

    public void setInstalledFeatures(List<FeatureBean> installedFeatures) {
        this.installedFeatures = installedFeatures;
    }
}
