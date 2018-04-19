package com.bot.cookbetter.model;

import java.util.Date;

public class Recipe {
    private String[] directions;
    private double fat;
    private Date date;
    private String[] categories;
    private double calories;
    private String desc;
    private double protein;
    private double rating;
    private String title;
    private String[] ingredients;
    private double sodium;


    public Recipe(){
    }

    public Recipe(String[] directions, double fat, Date date, String[] categories, double calories, String desc, double protein, double rating, String title, String[] ingredients, double sodium) {
        this.directions = directions;
        this.fat = fat;
        this.date = date;
        this.categories = categories;
        this.calories = calories;
        this.desc = desc;
        this.protein = protein;
        this.rating = rating;
        this.title = title;
        this.ingredients = ingredients;
        this.sodium = sodium;
    }

    public String[] getDirections() {
        return directions;
    }

    public double getFat() {
        return fat;
    }

    public Date getDate() {
        return date;
    }

    public String[] getCategories() {
        return categories;
    }

    public double getCalories() {
        return calories;
    }

    public String getDesc() {
        return desc;
    }

    public double getProtein() {
        return protein;
    }

    public double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public double getSodium() {
        return sodium;
    }

    public boolean equals(Object o) {
        return (o instanceof Recipe) && (((Recipe) o).getTitle()).equals(this.getTitle());
    }
}
