package com.bot.cookbetter.version2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {

    private static final String HOST_NAME = "cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com";
    private static final int PORT = 3306;
    private static final String USER_NAME = "cookbetter";
    private static final String PASSWORD = "cookbetter";
    private static final String DEFAULT_SCHEMA = "cookbetter";

    final static Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String connectionUrl = "jdbc:mysql://";
            connectionUrl += HOST_NAME + ":" + PORT;
            connectionUrl += "/" + DEFAULT_SCHEMA;
            connectionUrl += "?useUnicode=true&characterEncoding=UTF-8";
            connectionUrl += "&user=" + USER_NAME;
            connectionUrl += "&password=" + PASSWORD;

            Connection conn = DriverManager.getConnection(connectionUrl);

            return conn;
        }
        catch(Exception e) {
            logger.error("Error establishing database connection.");
            e.printStackTrace();
            return null;
        }
    }

}
