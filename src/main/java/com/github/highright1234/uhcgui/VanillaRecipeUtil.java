package com.github.highright1234.uhcgui;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class VanillaRecipeUtil {

    public ItemStack getRecipe(ItemStack[]... items) {

        Iterator<Recipe> recipes = Bukkit.recipeIterator();

        /*==================================================*/
        while (recipes.hasNext()) {

            Recipe recipe = recipes.next();
            ItemStack result = recipe.getResult();

            /*==================================================*/
            if (recipe instanceof ShapedRecipe) {

                String[] shape = ((ShapedRecipe) recipe).getShape();
                ItemStack[][] patched_shape = new ItemStack[shape.length][3];

                int currentShape = 0;
                for (String shape_ : shape) {
                    char[] keys = shape_.toCharArray();
                    patched_shape[currentShape] = new ItemStack[keys.length];
                    int currentKey = 0;
                    for (char key : keys) {
                        patched_shape[currentShape][currentKey] = ((ShapedRecipe) recipe).getIngredientMap().get(key);
                        currentKey++;
                    }
                    currentShape++;
                }

                if (isPass_Shaped(patched_shape, items)) {
                    return result;
                }

                /*==================================================*/
            } else if (recipe instanceof ShapelessRecipe) {

                List<ItemStack> in = new ArrayList<>();
                for (ItemStack[] i : items) {
                    for (ItemStack j : i) {
                        if (j == null) continue;
                        in.add(j);
                    }
                }

                List<ItemStack> recipeItems = ((ShapelessRecipe) recipe).getIngredientList();

                if (recipeItems.size() != in.size()) continue;

                if (isPass_Shapeless(in, recipeItems)) {
                    return result;
                }

            }
            /*==================================================*/
        }
        /*==================================================*/
        return null;
    }

    private boolean isPass_Shaped(ItemStack[][] recipe, ItemStack[][] items) {
        int same = 0;
        for ( ItemStack[] materials : recipe ) {
            for ( ItemStack[] in : items) {
                int same_ = 0;
                List<ItemStack> items0 = Lists.newArrayList(in);

                material : for ( ItemStack material : materials ) {
                    for (ItemStack item : items0) {
                        if (isPass(material, item)) {
                            same_++;
                            items0.remove(item);
                            continue material;
                        }
                    }
                }

                if (same_ == materials.length) same++;
            }
        }
        return same == recipe.length;
    }

    private boolean isPass_Shapeless(List<ItemStack> recipe, List<ItemStack> items) {
        int same = 0;
        material : for ( ItemStack material : recipe ) {
            for (ItemStack item : items) {
                if (isPass(material, item)) same++;
                continue material;
            }
        }
        return same == recipe.size();
    }

    private boolean isPass(ItemStack value1, ItemStack value2) {
        if (value1 == null) {
            return value2 == null;
        }
        return value1.getType() == value2.getType() &&
                value1.getAmount() >= value2.getAmount();
    }
}
