package com.bot.cookbetter.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserOptions {
    private String userID;
    private String ing1,ing2,ing3;
    private String recipeType;
    private String specialOccasion;
    private boolean quickMeal;


    final Logger logger = LoggerFactory.getLogger(RequestHandlerUtil.class);

    public UserOptions(String userID){
        this.userID = userID;
    }

    public void setIngredient(int num, String value){
        switch(num){
            case 1: this.ing1 = value;
                break;

            case 2: this.ing2 = value;
                break;

            case 3: this.ing3 = value;
                break;

        }
    }

    public void setRecipeType(String value){
        this.recipeType = value;
    }

    public void setQuickMeal(String value){
        if("yes".equals(value))
            this.quickMeal = true;
        else
            this.quickMeal = false;
    }

    public void setSpecialOccasion(String value){
        this.specialOccasion = value;
    }

    public void printDetails(){
        logger.info(this.userID);
        logger.info(this.ing1);
        logger.info(this.ing2);
        logger.info(this.ing3);
        logger.info(""+this.quickMeal);
        logger.info(this.recipeType);
        logger.info(this.specialOccasion);

    }

    public void startSearch(){
        logger.info("Search has started!!!");
    }
}
