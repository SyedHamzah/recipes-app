package com.abnamro.recipe.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHits;
import com.abnamro.recipe.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ESCustomSearchRepositoryImpl implements ESCustomSearchRepository {

    Logger logger = LoggerFactory.getLogger(ESCustomSearchRepositoryImpl.class);

    @Autowired
    private ElasticsearchOperations esOperations;
    @Override
    public List<Recipe> customFilter(Boolean vegetarian, Integer serving, String includeIngredient, String excludeIngredient, String instruction) {
        String strQuery = "{\"bool\": {\"must\": [";
        List<String> termConditions = new ArrayList<>();
        if (vegetarian != null) {
            termConditions.add(String.format("{\"term\":{\"isVegetarian\":\"%s\"}}", vegetarian));
        }
        if (serving != null) {
            termConditions.add(String.format("{\"term\":{\"serving\":%s}}", serving));
        }
        if (includeIngredient != null) {
            termConditions.add(String.format("{\"match\":{\"ingredients.keyword\":\"%s\"}}", includeIngredient));
        }
        if (instruction != null) {
            termConditions.add(String.format("{\"wildcard\":{\"instructions\":\"*%s*\"}}", instruction));
        }
        strQuery += String.join(",", termConditions) + "]";

        if (excludeIngredient != null) {
            strQuery += String.format(",\"must_not\":[{\"term\":{\"ingredients.keyword\":\"%s\"}}]", excludeIngredient);
        }

        strQuery += "}}";
        logger.debug(String.format("strQuery = ", strQuery));
        SearchHits<Recipe> searchHits = esOperations.search(new StringQuery(strQuery), Recipe.class);
        return searchHits.stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }
}
