package com.abnamro.recipe.delegate;

import com.abnamro.recipe.dto.RecipeDto;
import com.abnamro.recipe.model.Recipe;
import com.abnamro.recipe.repository.ESCustomSearchRepository;
import com.abnamro.recipe.repository.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test the Delegate Service using Mocks
 */
@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceDelegateImplTest {

    @Mock
    RecipeRepository mockRepo;

    @Mock
    ESCustomSearchRepository mockESRepo;

    @InjectMocks
    RecipeServiceDelegateImpl service;

    RecipeDto dto = new RecipeDto()
            .name("my recipe")
            .ingredients(List.of("ing1", "ing2", "ing3"))
            .serving(5)
            .isVegetarian(false)
            .instructions("some long instruction");

    Recipe recipe = Recipe.fromDto(dto);

    @Test
    public void testCreateRecipe() {
        // Given:
        when(mockRepo.save(Mockito.any())).thenReturn(recipe);

        // When:
        ResponseEntity<RecipeDto> response = service.createRecipe(dto);

        // Then:
        Mockito.verify(mockRepo, times(1)).save(Mockito.any());
        assertEquals(response.getBody().getName(), dto.getName());
    }

    @Test
    public void testDeleteRecipe() {
        // Given:
        doNothing().when(mockRepo).deleteById(Mockito.any());
        when(mockRepo.findById(Mockito.any())).thenReturn(Optional.of(recipe));

        // When:
        ResponseEntity<Void> response = service.deleteRecipeById(recipe.getId());

        // Then:
        Mockito.verify(mockRepo, times(1)).findById(recipe.getId());
        Mockito.verify(mockRepo, times(1)).deleteById(recipe.getId());
        assertEquals(response.getStatusCode().value(), 204);
    }

    @Test
    public void testUpdateRecipe() {
        // Given:
        when(mockRepo.save(Mockito.any())).thenReturn(recipe);
        when(mockRepo.findById(Mockito.any())).thenReturn(Optional.of(recipe));

        RecipeDto newDto = new RecipeDto()
                .ingredients(List.of("ing4", "ing5", "ing6"))
                .serving(5);
        // When:
        ResponseEntity<RecipeDto> response = service.updateRecipe(recipe.getId(), newDto);

        // Then:
        Mockito.verify(mockRepo, times(1)).findById(recipe.getId());
        Mockito.verify(mockRepo, times(1)).save(Mockito.any());
        assertEquals(response.getBody().getServing(), newDto.getServing());
        assertEquals(response.getBody().getIngredients(), newDto.getIngredients());
        assertEquals(response.getBody().getName(), dto.getName());
    }

    @Test
    public void testGetRecipeById() {
        // Given:
        when(mockRepo.findById(Mockito.any())).thenReturn(Optional.of(recipe));

        // When:
        ResponseEntity<RecipeDto> response = service.getRecipeById(recipe.getId());

        // Then:
        Mockito.verify(mockRepo, times(1)).findById(recipe.getId());
        assertEquals(response.getBody().getName(), dto.getName());
    }

    @Test
    public void testCustomFilter() {
        // Given:
        when(mockESRepo.customFilter(Mockito.any(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(List.of(recipe));

        // When:
        ResponseEntity<List<RecipeDto>> response =
                service.queryRecipes(true, 5, "incl", "excl", "instruction");

        // Then:
        Mockito.verify(mockESRepo, times(1)).customFilter(Mockito.any(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        assertEquals(response.getBody().get(0).getName(), dto.getName());
    }
}