package com.bot.cookbetter.version2;

import java.util.HashSet;
import java.util.Set;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Recipe {

    private int ID;

    private String name = "";

    private double rating;

    private Set<Ingredient> ingredients = new HashSet<>();

    private String directions;

    private String ingredientMeasurement;

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getDirections() { return directions;}

    public String getIngredientMeasuremnet() { return ingredientMeasurement;}

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setDirections(String directions) { this.directions = directions; }

    public void setIngredientMeasurement(String ingredientMeasurement) { this.ingredientMeasurement = ingredientMeasurement;}

    public void setRating(double rating) { this.rating = rating; }

    public double getRating() { return rating; }

    @Override
    public String toString() {
        return "Recipe{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}

