package com.bot.cookbetter.version2;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Ingredient {

    private String name;

    private boolean exisits;

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public boolean isExisits() {
        return exisits;
    }

    public void setExisits(boolean exisits) {
        this.exisits = exisits;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", exisits=" + exisits +
                '}';
    }
}
