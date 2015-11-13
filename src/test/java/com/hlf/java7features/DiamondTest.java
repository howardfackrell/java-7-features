package com.hlf.java7features;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by howard.fackrell on 11/5/15.
 */
public class DiamondTest {

    private <T> List<T> list(T...ts) {
        ArrayList<T> list = new ArrayList<>();
        for (T t : ts) {
            list.add(t);
        }
        return list;
    }

    @Test
    public void diamondOpperator() {
        Map<String, List<String>> favoriteFoods = new HashMap<>();

        favoriteFoods.put("Howard", list("pizza", "strawberries", "curry"));
        favoriteFoods.put("Adam", list("salad", "peperoni", "kittens"));
        favoriteFoods.put("Karen", list("yogurt", "cookies"));

        assertTrue(favoriteFoods.get("Adam").contains("kittens"));
    }


}
