package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy on 5/26/17.
 */
public class MPUtil {
    private static final Logger logger = Logger.getLogger(MPUtil.class.getName());

    public String testMPConnection(){
        String result = StatusType.NOT_OK.name();
        Connection con = null;
        try {
            Properties mpConfig = HealthCheckUtil.loadConfig("com.healthedge.market.prominence.enrollment.cfg");
            if(mpConfig != null) {
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection(mpConfig.getProperty("mp.db.url"),
                        mpConfig.getProperty("mp.db.username"),
                        HealthCheckUtil.decrypt(mpConfig.getProperty("mp.db.password")) );
                DatabaseMetaData dbMeta = con.getMetaData();
                logger.log(Level.INFO,"Using DriverManager " + dbMeta.getDatabaseProductName() + ", URL " + dbMeta.getURL()
                        + ", UserName " + dbMeta.getUserName());
                Statement stmt = con.createStatement();
                String query = "select 1 from dual";
                logger.info("stmt: " + stmt.isClosed());
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = StatusType.OK.name();
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in getting connection: ", e);
        } finally {
            HealthCheckUtil.closeConnectionNoThrow(con);
        }
        return result;
    }
}
