package com.abnamro.recipe.model;

import com.abnamro.recipe.dto.RecipeDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import java.util.List;
import java.util.UUID;

@Data
@Document(indexName = "recipe")
public class Recipe {
    @Id
    private UUID id;
    @Field
    private String name;
    @Field
    private Boolean isVegetarian;
    @Field
    private Integer serving;
    @Field
    private List<String> ingredients;
    @Field
    private String instructions;

    public static Recipe fromDto(RecipeDto dto) {
        Recipe recipe = new Recipe();
        // TODO: use spring UUID generator
        recipe.id = UUID.randomUUID();
        recipe.name = dto.getName();
        recipe.ingredients = dto.getIngredients();
        recipe.serving = dto.getServing();
        recipe.isVegetarian = dto.getIsVegetarian();
        recipe.instructions = dto.getInstructions();
        return recipe;
    }

    public RecipeDto recipeDto() {
        return new RecipeDto()
                .id(this.id)
                .name(this.name)
                .ingredients(this.ingredients)
                .serving(this.serving)
                .isVegetarian(this.isVegetarian)
                .instructions(this.instructions);
    }
}
