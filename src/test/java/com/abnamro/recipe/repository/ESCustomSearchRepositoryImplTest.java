package com.abnamro.recipe.repository;

import com.abnamro.recipe.dto.RecipeDto;
import com.abnamro.recipe.model.Recipe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.List;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test the CoolService using Mocks
 */
@RunWith(MockitoJUnitRunner.class)
public class ESCustomSearchRepositoryImplTest {

    @Mock
    ElasticsearchOperations esOperations;

    @Mock
    SearchHit<Recipe> hit;

    @Mock
    SearchHits<Recipe> hits;

    @Captor
    ArgumentCaptor<StringQuery> queryCaptor;

    @InjectMocks
    ESCustomSearchRepositoryImpl customRepo;

    RecipeDto dto = new RecipeDto()
            .name("my recipe")
            .ingredients(List.of("ing1", "ing2", "ing3"))
            .serving(5)
            .isVegetarian(false)
            .instructions("some long instruction");

    Recipe recipe = Recipe.fromDto(dto);

    @Test
    public void testQueryWithMustFilters() {
        // Given:
        when(hit.getContent()).thenReturn(recipe);
        when(hits.stream()).thenReturn(Stream.of(hit));
        when(esOperations.search(Mockito.any(Query.class), eq(Recipe.class))).thenReturn(hits);

        // When:
        List<Recipe> recipeList = customRepo.customFilter(true, 5, "incl", null, "instruction");
        String expectedQuery = "{\"bool\": {\"must\": [{\"term\":{\"isVegetarian\":\"true\"}}," +
                "{\"term\":{\"serving\":5}},{\"match\":{\"ingredients.keyword\":\"incl\"}}," +
                "{\"wildcard\":{\"instructions\":\"*instruction*\"}}]}}";

        // Then:
        verify(esOperations).search(queryCaptor.capture(), eq(Recipe.class));
        assertEquals(queryCaptor.getValue().getSource(), expectedQuery);
    }

    @Test
    public void testQueryWithMustNotFilters() {
        // Given:
        when(hit.getContent()).thenReturn(recipe);
        when(hits.stream()).thenReturn(Stream.of(hit));
        when(esOperations.search(Mockito.any(Query.class), eq(Recipe.class))).thenReturn(hits);

        // When:
        List<Recipe> recipeList = customRepo.customFilter(true, 5, "incl", "excl", "instruction");
        String expectedQuery = "{\"bool\": {\"must\": [{\"term\":{\"isVegetarian\":\"true\"}}," +
                "{\"term\":{\"serving\":5}},{\"match\":{\"ingredients.keyword\":\"incl\"}}," +
                "{\"wildcard\":{\"instructions\":\"*instruction*\"}}]," +
                "\"must_not\":[{\"term\":{\"ingredients.keyword\":\"excl\"}}]}}";
        // Then:
        verify(esOperations).search(queryCaptor.capture(), eq(Recipe.class));
        assertEquals(queryCaptor.getValue().getSource(), expectedQuery);
    }
}