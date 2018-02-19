package com.bot.cookbetter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalizeOptions {

    private String userID;
    private String age;
    private String all1,all2,all3;
    private String res1,res2,res3;
    private String dis1,dis2,dis3;
    private String goal1,goal2,goal3;

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
            case 2:
                this.all2=val;
            case 3:
                this.all3=val;
        }
    }

    public void setDietRestrictions(int number,String value){
        switch(number){
            case 1:
                this.res1=value;
            case 2:
                this.res2=value;
            case 3:
                this.res3=value;
        }

    }

    public void setDisease(int number,String value){
        switch(number)
        {
            case 1:
                this.dis1=value;
            case 2:
                this.dis2=value;
            case 3:
                this.dis3=value;

        }

    }

    public void setGoals(int number,String value){
        switch(number)
        {
            case 1:
                this.goal1=value;
            case 2:
                this.goal2=value;
            case 3:
                this.goal3=value;

        }

    }

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


    }

    public void submitPreferences() {
        logger.info(this.userID+"has submitted the preferences");
    }


}
