package com.healthedge.connector.healthcheck.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jtripathy on 5/25/17.
 */
public class FeatureBean implements Serializable {

    private String featureName;
    private String featureVersion;
    private List<BundleBean> bundleList;

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureVersion() {
        return featureVersion;
    }

    public void setFeatureVersion(String featureVersion) {
        this.featureVersion = featureVersion;
    }

    public List<BundleBean> getBundleList() {
        return bundleList;
    }

    public void setBundleList(List<BundleBean> bundleList) {
        this.bundleList = bundleList;
    }
}
