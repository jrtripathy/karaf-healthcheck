package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.server.jasypt.ConnectorPBEConfigFactory;
import com.healthedge.connector.server.jasypt.ConnectorPBEConfigFactoryImpl;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy
 */
public class HealthCheckUtil {

	private static final Logger logger = Logger.getLogger(HealthCheckUtil.class.getName());
	static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	static ConnectorPBEConfigFactory fact = new ConnectorPBEConfigFactoryImpl();
	static PBEConfig pbe = fact.getInstance();

	static {
		encryptor.setAlgorithm(pbe.getAlgorithm());
		encryptor.setPassword(pbe.getPassword());
		encryptor.setSaltGenerator (pbe.getSaltGenerator());
	}

	private static boolean isExists(String fileName){
		boolean bResult = false;
		Path cfgPath = Paths.get(System.getProperty("karaf.etc"), fileName);
		if(Files.exists(cfgPath)){
			bResult = true;
		}
		return bResult;
	}

	public static Properties loadConfig(String configName) throws IOException {
		Properties ret = new Properties();
		Path p = Paths.get(System.getProperty("karaf.etc"), configName);
		if (Files.exists(p)) {
			ret.load(Files.newInputStream(p));
		}
		return ret;
	}

	public static void closeConnectionNoThrow(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (Exception exp) {
				// ignore
				//logger.log(Level.WARNING, "Ignoring exception closing connection", exp);
			}
		}
	}

	public static void writeInfos(Connection con) throws SQLException {
		DatabaseMetaData dbMeta = con.getMetaData();
		logger.log(Level.INFO,"Using datasource " + dbMeta.getDatabaseProductName() + ", URL " + dbMeta.getURL()
				+ ", UserName " + dbMeta.getUserName());
	}

	public static String decrypt(String pass){
		String clear = null;
		pass = pass.trim();
		if((pass.startsWith("ENC(")) && (pass.endsWith(")"))) {
			pass = pass.substring("ENC(".length(), pass.length() - ")".length());
			clear = encryptor.decrypt(pass);
		}else {
			clear = pass;
		}
		return clear;
	}
}
