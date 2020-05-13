package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy on 5/24/17.
 */
public class PayorEJBCallUtil {
    private static final Logger LOGGER = Logger.getLogger(PayorEJBCallUtil.class.getName());
    private String username;
    private String password;
    private String url;
    private String contextFactory;

    public String testRemoteEJBLookup() {
        String result = StatusType.NOT_OK.name();
        Context ctx = null;
        try {
            final Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
            p.put(Context.PROVIDER_URL, url);
            p.put(Context.SECURITY_PRINCIPAL, username);
            p.put(Context.SECURITY_CREDENTIALS, password);

            ctx = new InitialContext(p);
            Object obj = ctx.lookup(getEJBJndi());
            result = StatusType.OK.name();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Healthcheck EJB Lookup: ", e);
        } finally {
            closeContext(ctx);
        }
        return result;
    }

    private String getEJBJndi() {
        return "com.healthedge.connector.generated.webservice.wl.EnrollmentServiceWeaklyTypedHome" ;
    }

    private final void closeContext(Context context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                // ignore
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContextFactory() {
        return contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }
}
