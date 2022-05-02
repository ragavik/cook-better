package com.bot.cookbetter.utils;

import com.bot.cookbetter.version2.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class  PersonalizeOptions {

    public String userID;
    public String age;
    public String all1,all2,all3;
    public String res1,res2,res3;
    public String dis1,dis2,dis3;
    public String goal1,goal2,goal3;

    // TODO: remove age (unused)

    final Logger logger = LoggerFactory.getLogger(RequestHandlerUtil.class);

    public PersonalizeOptions(String userID) {
        this.userID = userID;
    }
    public void setAge(String val){
        this.age=val;
    }

    public void setAllegies(int number,String val){
        switch(number){
            case 1:
                this.all1=val;
                break;
            case 2:
                this.all2=val;
                break;
            case 3:
                this.all3=val;
                break;
        }
    }

    public void setDietRestrictions(int number,String value){
        switch(number){
            case 1:
                this.res1=value;
                break;
            case 2:
                this.res2=value;
                break;
            case 3:
                this.res3=value;
                break;
        }

    }

    public void setDisease(int number,String value){
        switch(number)
        {
            case 1:
                this.dis1=value;
                break;
            case 2:
                this.dis2=value;
                break;
            case 3:
                this.dis3=value;
                break;
        }

    }

    public void setGoals(int number,String value){
        switch(number)
        {
            case 1:
                this.goal1=value;
                break;
            case 2:
                this.goal2=value;
                break;
            case 3:
                this.goal3=value;
                break;

        }

    }

    /*
    // For testing
    public void printDetails() {
        logger.info(this.userID);
        logger.info(this.age);
        logger.info(this.all1);
        logger.info(this.all2);
        logger.info(this.all3);
        logger.info(this.res1);
        logger.info(this.res2);
        logger.info(this.res3);
        logger.info(this.dis1);
        logger.info(this.dis2);
        logger.info(this.dis3);
        logger.info(this.goal1);
        logger.info(this.goal2);
        logger.info(this.goal3);
    }*/

    public void submitPreferences(String response_url) throws Exception {
        Connection conn = DatabaseUtil.getConnection();
        String query = "";
        String selectQuery = "select * from personalize where userid = '"+this.userID + "'";
        ResultSet rs = conn.prepareStatement(selectQuery).executeQuery();
        int count=0;
        while(rs.next()){
            count++;
            break;
        }
        if(count == 1){
            query += "update personalize set ";
            query += " allergy_1='" + (this.all1 == null ? "-1" : this.all1) + "'";
            query += ", allergy_2='" + (this.all2 == null ? "-1" : this.all2) + "'";
            query += ", allergy_3='" + (this.all3 == null ? "-1" : this.all3) + "'";
            query += ", diet_res_1='" + (this.res1 == null ? "-1" : this.res1) + "'";
            query += ", diet_res_2='" + (this.res2 == null ? "-1" : this.res2) + "'";
            query += ", diet_res_3='" + (this.res3 == null ? "-1" : this.res3) + "'";
            query += ", dis_1='" + (this.dis1 == null ? "-1" : this.dis1) + "'";
            query += ", dis_2='" + (this.dis2 == null ? "-1" : this.dis2) + "'";
            query += ", dis_3='" + (this.dis3 == null ? "-1" : this.dis3) + "'";
            query += ", goal_lose_wt='" + (this.goal1 == null ? "-1" : this.goal1) + "'";
            query += ", goal_gain_wt='" + (this.goal2 == null ? "-1" : this.goal2) + "'";
            query += ", goal_gain_muscle='" + (this.goal3 == null ? "-1" : this.goal3) + "'";
            query += " where userid = '"+this.userID + "'";
        }
        else{
            query += "insert into personalize values ('"+ this.userID + "'";
            query += ", '-1'"; // Temporary - for unused agegroup column
            query += ",'"+ (this.all1 == null ? "-1" : this.all1) + "'";
            query += ",'"+ (this.all2 == null ? "-1" : this.all2) + "'";
            query += ",'"+ (this.all3 == null ? "-1" : this.all3) + "'";
            query += ",'"+ (this.res1 == null ? "-1" : this.res1) + "'";
            query += ",'"+ (this.res2 == null ? "-1" : this.res2) + "'";
            query += ",'"+ (this.res3 == null ? "-1" : this.res3) + "'";
            query += ",'"+ (this.dis1 == null ? "-1" : this.dis1) + "'";
            query += ",'"+ (this.dis2 == null ? "-1" : this.dis2) + "'";
            query += ",'"+ (this.dis3 == null ? "-1" : this.dis3) + "'";
            query += ",'"+ (this.goal1 == null ? "-1" : this.goal1) + "'";
            query += ",'"+ (this.goal2 == null ? "-1" : this.goal2) + "'";
            query += ",'"+ (this.goal3 == null ? "-1" : this.goal3) + "'";
            query += ");";
        }

        conn.prepareStatement(query).executeUpdate();

        JSONObject response = new JSONObject();
        JSONArray attachments = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("color", "#36a64f");
        item.put("text", "Your preferences have been saved successfully! :smile: \nNow try /searchrecipes to get personalized recipes!");
        attachments.put(item);
        response.put("attachments", attachments);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);

        conn.close();
    }


}