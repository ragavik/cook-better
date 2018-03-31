package com.bot.cookbetter.utils;

import com.bot.cookbetter.version2.Ingredient;

import java.util.Set;

public class RecipeCompare {

    private Set<Ingredient> missingIngredients;

    private Set<Ingredient> requiredIngredients;

    public Set<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    public void addMissingIngredient(Ingredient ingredient){
        this.missingIngredients.add(ingredient);
    }

    public Set<Ingredient> getRequiredIngredients() {
        return requiredIngredients;
    }


    public void addRequiredIngredient(Ingredient ingredient){
        this.missingIngredients.add(ingredient);
    }

}
