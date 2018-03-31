package com.bot.cookbetter.version2;

import java.util.Set;

public class Recipe {

    private int ID;

    private String name;

    private Set<Ingredient> ingredients;


    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}

