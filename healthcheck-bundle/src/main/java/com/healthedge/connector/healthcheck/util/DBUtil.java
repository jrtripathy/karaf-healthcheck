package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy
 */
public class DBUtil {

	private static final Logger logger = Logger.getLogger(DBUtil.class.getName());

	public String testConnection(DataSource dataSource, String fName){
		String result = StatusType.NOT_OK.name();
		if(dataSource != null) {
			Connection con = null;
			try {
				//System.setProperty("java.security.egd", "file:///dev/urandom");
				con = dataSource.getConnection();
				HealthCheckUtil.writeInfos(con);
				Statement stmt = con.createStatement();
				String query = getQuery(con.getMetaData().getDatabaseProductName());
				//logger.info("stmt: " + stmt.isClosed());
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()){
					result = StatusType.OK.name();
				}
				rs.close();
				stmt.close();
			} catch (SQLRecoverableException e) {
				String configName = null;
				if("oltp".equalsIgnoreCase(fName)){
					configName = "com.healthedge.connector.payor.oltp.cfg";
				}else if("dw".equalsIgnoreCase(fName)){
					configName = "com.healthedge.connector.dw.cfg";
				}else if("cm".equalsIgnoreCase(fName)){
					configName = "com.healthedge.connector.caremanager.oltp.cfg";
				}
				result = tryPrimitiveWay(configName);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in getting connection from DS: ", e);
			} finally {
				HealthCheckUtil.closeConnectionNoThrow(con);
			}
		}
		return result;
	}

	private String tryPrimitiveWay(String configName){
		String result = StatusType.NOT_OK.name();
		if(configName != null) {
			Connection con = null;
			try {
				Properties config = HealthCheckUtil.loadConfig(configName);
				if (config != null) {
					String url = config.getProperty("url");
					if (url.contains("sqlserver")) {
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					} else {
						Class.forName("oracle.jdbc.OracleDriver");
					}
					con = DriverManager.getConnection(url, config.getProperty("username"),
							HealthCheckUtil.decrypt(config.getProperty("password")) );
					DatabaseMetaData dbMeta = con.getMetaData();
					logger.log(Level.INFO, "Falling back to DriverManager " + dbMeta.getDatabaseProductName() + ", URL " + dbMeta.getURL()
							+ ", UserName " + dbMeta.getUserName());
					Statement stmt = con.createStatement();
					String query = getQuery(dbMeta.getDatabaseProductName());
					ResultSet rs = stmt.executeQuery(query);
					if (rs.next()) {
						result = StatusType.OK.name();
					}
					rs.close();
					stmt.close();
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in getting connection from DM: ", e);
			} finally {
				HealthCheckUtil.closeConnectionNoThrow(con);
			}
		}
		return result;
	}

	private String getQuery(String type){
		String query = "select 1 from dual";
		if(type.equals("Microsoft SQL Server") || type.equals("SQL Server") || type.equals("SQLServer")){
			query = "select 1";
		}
		return query;
	}
}
