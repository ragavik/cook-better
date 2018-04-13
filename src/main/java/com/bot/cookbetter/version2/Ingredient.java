package com.bot.cookbetter.version2;

import java.util.Set;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Ingredient {

    private String name;
    private  boolean exists;

    public Ingredient(String name) {
        this.name = name;
        this.exists = Util.getAllIngredientNames().contains(getName());
    }

    public String getName() {
        return name;
    }

    public boolean isExisits() {

        return this.exists;
    }

    public void setExisits(boolean exisits) {
        this.exists = exisits;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", exisits=" + exists +
                '}';
    }
}