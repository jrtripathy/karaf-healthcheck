package com.healthedge.connector.healthcheck.processor.impl;

import com.healthedge.connector.healthcheck.model.BundleBean;
import com.healthedge.connector.healthcheck.model.ConnectorVersionBean;
import com.healthedge.connector.healthcheck.model.FeatureBean;
import com.healthedge.connector.healthcheck.processor.VersionRestService;
import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.ConfigFileInfo;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by jtripathy on 5/23/17.
 */
public class VersionRestServiceImpl implements VersionRestService{
    private static final Logger LOGGER = Logger.getLogger(VersionRestServiceImpl.class.getName());
    private FeaturesService featuresService;
    private BundleContext bundleContext;

    public VersionRestServiceImpl() {
        LOGGER.info(VersionRestServiceImpl.class.getName()+" is up and running");
    }

    @Override
    public ConnectorVersionBean getVersions(){
        ConnectorVersionBean versionBean = new ConnectorVersionBean();
        populateVersions(versionBean);
        versionBean.setInstalledFeatures(getFeatureList());
        return versionBean;
    }

    private List<FeatureBean> getFeatureList(){
        List<FeatureBean> featureList = new ArrayList<>();
        Map<String, Bundle> xmlBundleMap  = new HashMap <> ();
        for (Bundle b : bundleContext.getBundles()) {
            if(b.getSymbolicName().endsWith(".xml")) {
                xmlBundleMap.put (b.getSymbolicName(), b);
            }
        }
        try{
            for(Feature f : featuresService.listInstalledFeatures()){
                if(f.getName().endsWith("repos")) {
                    FeatureBean featureBean = new FeatureBean();
                    List<BundleBean> bundleList = new ArrayList<>();
                    featureBean.setFeatureName(f.getName());
                    featureBean.setFeatureVersion(f.getVersion());
                    for(BundleInfo bundleInfo : f.getBundles()){
                        if(bundleInfo.getLocation().contains("bundle")) {
                            //int index = bundleInfo.getLocation().indexOf("/");
                            //bundleList.add(bundleInfo.getLocation().substring(index + 1));
                            Bundle bundle = bundleContext.getBundle(bundleInfo.getLocation());
                            if(bundle != null) {
                                BundleBean bundleBean = new BundleBean();
                                bundleBean.setBundleId(Long.toString(bundle.getBundleId()));
                                bundleBean.setBundleName(bundle.getSymbolicName());
                                bundleBean.setBundleVersion(bundle.getVersion().toString());
                                bundleBean.setBundleStatus(getState(bundle.getState()));
                                bundleList.add(bundleBean);
                            }
                        }
                    }
                    List<BundleBean> xmlList = getXMLBundles(f, xmlBundleMap);
                    if(!xmlList.isEmpty()){
                        bundleList.addAll(xmlList);
                    }
                    bundleList.sort((b1, b2) -> b1.getBundleId().compareTo(b2.getBundleId()));
                    featureBean.setBundleList(bundleList);
                    featureList.add(featureBean);
                }
            }
        }catch (Exception exp){
            LOGGER.log(Level.SEVERE, "Error in getFeatureList: ", exp);
        }

        featureList.sort((f1, f2) -> f1.getFeatureName().compareTo(f2.getFeatureName()));
        return featureList;
    }

    private List<BundleBean> getXMLBundles(Feature feature, Map<String, Bundle> xmlBundleMap){
        List<BundleBean> bundleList = new ArrayList<>();
        List <String> configXMLFileNames = feature.getConfigurationFiles().stream()
                .filter(cf -> cf.getFinalname().endsWith(".xml"))
                .map(new Function<ConfigFileInfo, String>() {
                    @Override
                    public String apply(ConfigFileInfo f) {
                        try {
                            return f.getFinalname().trim();
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                }).collect(Collectors.toList());
        for (String configXMLFileName : configXMLFileNames) {
            for (Map.Entry<String, Bundle> entry : xmlBundleMap.entrySet()) {
                if (configXMLFileName.contains(entry.getKey())) {
                    Bundle bundle = entry.getValue();
                    BundleBean bundleBean = new BundleBean();
                    bundleBean.setBundleId(Long.toString(bundle.getBundleId()));
                    bundleBean.setBundleName(bundle.getSymbolicName());
                    bundleBean.setBundleVersion(bundle.getVersion().toString());
                    bundleBean.setBundleStatus(getState(bundle.getState()));
                    bundleList.add(bundleBean);
                    break;
                }
            }
        }
        return bundleList;
    }

    private void populateVersions(ConnectorVersionBean versionBean){
        Path cfgPath = Paths.get(System.getProperty("user.home"), ".connector");
        if(Files.exists(cfgPath)){
            try {
                Properties ret = new Properties();
                ret.load(Files.newInputStream(cfgPath));
                if(ret.getProperty("CONNECTOR_VERSION") != null) {
                    versionBean.setConnectorVersion(ret.getProperty("CONNECTOR_VERSION"));
                }
                if(ret.getProperty("CUSTOM_VERSION") != null) {
                    versionBean.setCustomVersion(ret.getProperty("CUSTOM_VERSION"));
                }
                if(ret.getProperty("KARAF_HOME") != null) {
                    versionBean.setKarafHome(ret.getProperty("KARAF_HOME"));
                }
            }catch (Exception exp){
                LOGGER.log(Level.SEVERE, "Error in populateVersions: ", exp);
            }
        }
    }

    private String getState(int state) {
        switch (state) {
            case Bundle.UNINSTALLED : return "UNINSTALLED";
            case Bundle.INSTALLED : return "INSTALLED";
            case Bundle.RESOLVED: return "RESOLVED";
            case Bundle.STARTING : return "STARTING";
            case Bundle.STOPPING : return "STOPPING";
            case Bundle.ACTIVE : return "ACTIVE";
            default : return "UNKNOWN";
        }
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
