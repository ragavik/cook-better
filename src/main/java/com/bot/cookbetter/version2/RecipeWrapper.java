package com.bot.cookbetter.version2;

/**
 * @author snaraya7 (Shrikanth N C)
 */
public class RecipeWrapper implements Comparable<RecipeWrapper> {

    private Recipe recipe;
    private int score;

   public RecipeWrapper(Recipe recipe, int score){
        this.recipe = recipe;
        this.score = score;

    }

    public int getScore() {
        return score;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    @Override
    public int compareTo(RecipeWrapper o) {

        return getScore() - o.getScore();
    }
}
