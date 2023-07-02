package com.abnamro.recipe.repository;

import com.abnamro.recipe.model.Recipe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface RecipeRepository extends ElasticsearchRepository<Recipe, UUID> {

}
