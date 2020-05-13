package com.healthedge.connector.healthcheck.model;

import java.io.Serializable;

/**
 * Created by jtripathy on 5/25/17.
 */
public class BundleBean implements Serializable {

    private String bundleId;
    private String bundleName;
    private String bundleVersion;
    private String bundleStatus;

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public String getBundleStatus() {
        return bundleStatus;
    }

    public void setBundleStatus(String bundleStatus) {
        this.bundleStatus = bundleStatus;
    }
}
