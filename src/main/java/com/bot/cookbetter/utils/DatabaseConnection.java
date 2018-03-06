package com.bot.cookbetter.utils;
import java.sql.*;


public class DatabaseConnection {



        public void insertPersonalizeData(PersonalizeOptions p){
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con=DriverManager.getConnection(
                        "jdbc:mysql://aa4b27uftifuva.c9oonpekeh8v.us-east-1.rds.amazonaws.com:3306/recipes","cookbetter","cookbetter");
//here sonoo is database name, root is username and password
                Statement stmt=con.createStatement();
                String sql = "INSERT INTO recipes.personalize values(?, ?, ?,?,?)";
                if(p.all1!=null || p.dis1!=null|| p.goal1!=null) {

                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, p.userID);
                    preparedStatement.setString(2, p.age);
                    preparedStatement.setString(3, p.all1);
                    preparedStatement.setString(4, p.dis1);
                    preparedStatement.setString(5, p.goal1);

                    preparedStatement.executeUpdate();
                }

                if(p.all2!=null || p.dis2!=null || p.goal2!=null) {

                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, p.userID);
                    preparedStatement.setString(2, p.age);
                    preparedStatement.setString(3, p.all2);
                    preparedStatement.setString(4, p.dis2);
                    preparedStatement.setString(5, p.goal2);

                    preparedStatement.executeUpdate();
                }

                if(p.all3!=null || p.dis3!=null || p.goal3!=null) {

                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, p.userID);
                    preparedStatement.setString(2, p.age);
                    preparedStatement.setString(3, p.all3);
                    preparedStatement.setString(4, p.dis3);
                    preparedStatement.setString(5, p.goal3);

                    preparedStatement.executeUpdate();
                }
                con.close();
            }catch(Exception e){ System.out.println(e);}
        }
    }

