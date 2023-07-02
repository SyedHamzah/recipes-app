package com.abnamro.recipe.delegate;

import com.abnamro.recipe.api.RecipeApiDelegate;
import com.abnamro.recipe.dto.RecipeDto;
import com.abnamro.recipe.model.Recipe;
import com.abnamro.recipe.repository.ESCustomSearchRepository;
import com.abnamro.recipe.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeServiceDelegateImpl implements RecipeApiDelegate {

    Logger logger = LoggerFactory.getLogger(RecipeServiceDelegateImpl.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ESCustomSearchRepository customSearchRepository;

    @Override
    public ResponseEntity<RecipeDto> createRecipe(final RecipeDto recipeDto) {
        Recipe savedRecipe = recipeRepository.save(Recipe.fromDto(recipeDto));
        return ResponseEntity.of(Optional.of(savedRecipe.recipeDto()));
    }

    @Override
    public ResponseEntity<RecipeDto> getRecipeById(final UUID id) {
        return ResponseEntity.of(recipeRepository.findById(id).map(Recipe::recipeDto));
    }

    @Override
    public ResponseEntity<Void> deleteRecipeById(UUID id) {
        int status = recipeRepository.findById(id).map(recipe -> {
                    recipeRepository.deleteById(id);
                    return 204;
                }).orElse(404);

        return new ResponseEntity(HttpStatusCode.valueOf(status));
    }
    @Override
    public ResponseEntity<List<RecipeDto>> queryRecipes(Boolean vegetarian,
                                                         Integer serving,
                                                         String includeIngredient,
                                                         String excludeIngredient,
                                                         String instruction) {

        var recipes = customSearchRepository.customFilter(vegetarian, serving, includeIngredient, excludeIngredient, instruction)
                .stream().map(Recipe::recipeDto).collect(Collectors.toList());
        return ResponseEntity.of(Optional.of(recipes));
    }

    @Override
    public ResponseEntity<RecipeDto> updateRecipe(final UUID id, final RecipeDto recipeDto) {
        try {
            validateRecipe(recipeDto);
        } catch (Exception e) {
            logger.error("Unable to parse input");
            throw e;
        }
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        copyNonNullProperties(existingRecipe, recipeDto);

        return ResponseEntity.of(Optional.of(
                recipeRepository.save(existingRecipe).recipeDto()));
    }

    private void copyNonNullProperties(Recipe recipe, RecipeDto recipeDto) {
        // TODO: Try BeanUtilsBean.copyProperties()
        Optional.ofNullable(recipeDto.getIsVegetarian()).ifPresent(recipe::setIsVegetarian);
        Optional.ofNullable(recipeDto.getServing()).ifPresent(recipe::setServing);
        Optional.ofNullable(recipeDto.getIngredients()).ifPresent(recipe::setIngredients);
        Optional.ofNullable(recipeDto.getInstructions()).ifPresent(recipe::setInstructions);
    }

    private static void validateRecipe(RecipeDto recipeDto) {
        // do some validation
    }



}
