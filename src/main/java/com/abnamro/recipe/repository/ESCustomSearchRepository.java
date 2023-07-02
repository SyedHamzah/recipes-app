package com.abnamro.recipe.repository;

import com.abnamro.recipe.model.Recipe;

import java.util.List;

public interface ESCustomSearchRepository {
    List<Recipe> customFilter(Boolean vegetarian, Integer serving,
                              String includeIngredient, String excludeIngredient, String instruction);
}
