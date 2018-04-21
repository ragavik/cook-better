package com.bot.cookbetter;

import com.bot.cookbetter.app.CookBetterApplication;
import com.bot.cookbetter.utils.UserOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CookBetterApplication.class)
public class UserOptionsTest {

    UserOptions userOptions;

    @Test
    public void searchRecipeTest() {
        userOptions = new UserOptions("123");
        userOptions.setIngredient(1, "Beef");
        userOptions.setIngredient(2, "Butter");
        userOptions.setIngredient(3, "Chilli");

        try {
            userOptions.startSearch("");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
