package com.healthedge.connector.healthcheck.util;

import com.healthedge.connector.healthcheck.model.StatusType;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jtripathy on 5/24/17.
 */
public class ActiveMQUtil {
    private static final Logger LOGGER = Logger.getLogger(ActiveMQUtil.class.getName());
    private String username;
    private String password;
    private String url;

    public String testJMS() {
        String result = StatusType.NOT_OK.name();

        try {
            String msg = "healthcheck_test";
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection(username, password);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TemporaryQueue tempQueue = session.createTemporaryQueue();

            MessageProducer producer = session.createProducer(tempQueue);
            TextMessage sendMessage = session.createTextMessage(msg);
            producer.send(sendMessage);

            MessageConsumer consumer = session.createConsumer(tempQueue);
            Message receivedMessage = consumer.receive();
            if (receivedMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) receivedMessage;
                LOGGER.info("Received Msg: " + textMessage.getText());
                if(msg.equals(textMessage.getText())){
                    result = StatusType.OK.name();
                }
            }

            consumer.close();
            tempQueue.delete();

            connection.stop();
            connection.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Healthcheck ActiveMQ: ", e);
        } finally {}

        return result;
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
}
