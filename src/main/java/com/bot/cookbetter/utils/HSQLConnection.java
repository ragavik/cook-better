package com.bot.cookbetter.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class HSQLConnection {

    public void setConnection() throws Exception{
        // Database connection
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        //String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:C:/Users/Kapil/Documents/GitHub/cook-better\\test", "", "");

        String query = "select * from data where title is not null";

    }
}
