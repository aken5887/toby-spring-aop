package me.toby.spring.user.domain;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    User user;

    @Before
    public void setup() {
        user = new User();
    }

    @Test
    public void upgradeLevelTest(){
        Arrays.asList(Level.values())
                .stream().forEach(level -> {
                    if(level.nextLevel() != null){
                        user.setLevel(level);
                        user.upgradeLevel();
                        Assert.assertEquals(user.getLevel(), level.nextLevel());
                    }
                });
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
       Arrays.asList(Level.values())
               .stream().forEach((level) -> {
                   if(level.nextLevel() == null){
                     user.setLevel(level);
                     user.upgradeLevel();
                   }
               });
    }
}