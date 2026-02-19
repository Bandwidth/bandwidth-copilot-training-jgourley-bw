package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.exception.ResourceNotFoundException;
import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecipeService
 * Uses Mockito to mock RecipeRepository dependencies
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @InjectMocks
    private RecipeService recipeService;
    
    private Recipe testRecipe;
    private Recipe italianRecipe;
    private Recipe mexicanRecipe;
    
    @BeforeEach
    void setUp() {
        // Create test recipes with different properties
        testRecipe = new Recipe("Pasta Carbonara", "Classic Italian pasta dish", 10, 15, 4, "Easy", "Italian");
        testRecipe.setId(1L);
        
        italianRecipe = new Recipe("Margherita Pizza", "Traditional Italian pizza", 20, 12, 2, "Medium", "Italian");
        italianRecipe.setId(2L);
        
        mexicanRecipe = new Recipe("Beef Tacos", "Delicious beef tacos", 15, 10, 4, "Easy", "Mexican");
        mexicanRecipe.setId(3L);
    }
    
    // ==================== getAllRecipes() Tests ====================
    
    @Test
    void testGetAllRecipes_WhenRecipesExist_ThenReturnsAllRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe, italianRecipe, mexicanRecipe);
        when(recipeRepository.findAll()).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getAllRecipes();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Pasta Carbonara", result.get(0).getName());
        assertEquals("Margherita Pizza", result.get(1).getName());
        assertEquals("Beef Tacos", result.get(2).getName());
        verify(recipeRepository, times(1)).findAll();
    }
    
    @Test
    void testGetAllRecipes_WhenNoRecipesExist_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getAllRecipes();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findAll();
    }
    
    // ==================== getRecipeById() Tests ====================
    
    @Test
    void testGetRecipeById_WhenRecipeExists_ThenReturnsRecipe() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        
        // Act
        Optional<Recipe> result = recipeService.getRecipeById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("Pasta Carbonara", result.get().getName());
        assertEquals("Italian", result.get().getCuisineType());
        assertEquals("Easy", result.get().getDifficultyLevel());
        verify(recipeRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetRecipeById_WhenRecipeDoesNotExist_ThenReturnsEmpty() {
        // Arrange
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Recipe> result = recipeService.getRecipeById(999L);
        
        // Assert
        assertFalse(result.isPresent());
        verify(recipeRepository, times(1)).findById(999L);
    }
    
    // ==================== getRecipesByDifficulty() Tests ====================
    
    @Test
    void testGetRecipesByDifficulty_WhenEasyRecipesExist_ThenReturnsEasyRecipes() {
        // Arrange
        List<Recipe> easyRecipes = Arrays.asList(testRecipe, mexicanRecipe);
        when(recipeRepository.findByDifficultyLevel("Easy")).thenReturn(easyRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Easy");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getDifficultyLevel().equals("Easy")));
        verify(recipeRepository, times(1)).findByDifficultyLevel("Easy");
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenMediumRecipesExist_ThenReturnsMediumRecipes() {
        // Arrange
        List<Recipe> mediumRecipes = Collections.singletonList(italianRecipe);
        when(recipeRepository.findByDifficultyLevel("Medium")).thenReturn(mediumRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Medium");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Medium", result.get(0).getDifficultyLevel());
        verify(recipeRepository, times(1)).findByDifficultyLevel("Medium");
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenNoRecipesMatchDifficulty_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByDifficultyLevel("Hard")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Hard");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findByDifficultyLevel("Hard");
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenDifficultyIsNull_ThenCallsRepository() {
        // Arrange
        when(recipeRepository.findByDifficultyLevel(null)).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty(null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findByDifficultyLevel(null);
    }
    
    // ==================== getRecipesByCuisine() Tests ====================
    
    @Test
    void testGetRecipesByCuisine_WhenItalianRecipesExist_ThenReturnsItalianRecipes() {
        // Arrange
        List<Recipe> italianRecipes = Arrays.asList(testRecipe, italianRecipe);
        when(recipeRepository.findByCuisineType("Italian")).thenReturn(italianRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Italian");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getCuisineType().equals("Italian")));
        verify(recipeRepository, times(1)).findByCuisineType("Italian");
    }
    
    @Test
    void testGetRecipesByCuisine_WhenMexicanRecipesExist_ThenReturnsMexicanRecipes() {
        // Arrange
        List<Recipe> mexicanRecipes = Collections.singletonList(mexicanRecipe);
        when(recipeRepository.findByCuisineType("Mexican")).thenReturn(mexicanRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Mexican");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mexican", result.get(0).getCuisineType());
        verify(recipeRepository, times(1)).findByCuisineType("Mexican");
    }
    
    @Test
    void testGetRecipesByCuisine_WhenNoRecipesMatchCuisine_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByCuisineType("Thai")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Thai");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findByCuisineType("Thai");
    }
    
    @Test
    void testGetRecipesByCuisine_WhenCuisineTypeIsNull_ThenCallsRepository() {
        // Arrange
        when(recipeRepository.findByCuisineType(null)).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine(null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findByCuisineType(null);
    }
    
    // ==================== searchRecipes() Tests ====================
    
    @Test
    void testSearchRecipes_WhenSearchTermMatches_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> matchingRecipes = Collections.singletonList(testRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("pasta")).thenReturn(matchingRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("pasta");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("pasta"));
        verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("pasta");
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsCaseInsensitive_ThenFindsRecipe() {
        // Arrange
        List<Recipe> matchingRecipes = Collections.singletonList(testRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("PASTA")).thenReturn(matchingRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("PASTA");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("PASTA");
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsPartialMatch_ThenFindsRecipes() {
        // Arrange
        List<Recipe> matchingRecipes = Arrays.asList(testRecipe, italianRecipe, mexicanRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("a")).thenReturn(matchingRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("a");
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("a");
    }
    
    @Test
    void testSearchRecipes_WhenNoRecipesMatch_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByNameContainingIgnoreCase("xyz123")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("xyz123");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("xyz123");
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsEmpty_ThenCallsRepository() {
        // Arrange
        when(recipeRepository.findByNameContainingIgnoreCase("")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("");
        
        // Assert
        assertNotNull(result);
        verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("");
    }
    
    // ==================== saveRecipe() Tests ====================
    
    @Test
    void testSaveRecipe_WhenNewRecipe_ThenReturnsSavedRecipe() {
        // Arrange
        Recipe newRecipe = new Recipe("New Recipe", "Test description", 5, 10, 2, "Easy", "American");
        Recipe savedRecipe = new Recipe("New Recipe", "Test description", 5, 10, 2, "Easy", "American");
        savedRecipe.setId(4L);
        when(recipeRepository.save(newRecipe)).thenReturn(savedRecipe);
        
        // Act
        Recipe result = recipeService.saveRecipe(newRecipe);
        
        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("New Recipe", result.getName());
        assertEquals("American", result.getCuisineType());
        verify(recipeRepository, times(1)).save(newRecipe);
    }
    
    @Test
    void testSaveRecipe_WhenUpdatingExistingRecipe_ThenReturnsUpdatedRecipe() {
        // Arrange
        testRecipe.setDescription("Updated description");
        when(recipeRepository.save(testRecipe)).thenReturn(testRecipe);
        
        // Act
        Recipe result = recipeService.saveRecipe(testRecipe);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated description", result.getDescription());
        verify(recipeRepository, times(1)).save(testRecipe);
    }
    
    @Test
    void testSaveRecipe_WhenRecipeHasAllFields_ThenSavesSuccessfully() {
        // Arrange
        Recipe completeRecipe = new Recipe("Complete Recipe", "Full description", 15, 20, 6, "Hard", "Mediterranean");
        completeRecipe.setImageUrl("http://example.com/image.jpg");
        when(recipeRepository.save(completeRecipe)).thenReturn(completeRecipe);
        
        // Act
        Recipe result = recipeService.saveRecipe(completeRecipe);
        
        // Assert
        assertNotNull(result);
        assertEquals("Complete Recipe", result.getName());
        assertEquals("http://example.com/image.jpg", result.getImageUrl());
        verify(recipeRepository, times(1)).save(completeRecipe);
    }
    
    // ==================== deleteRecipe() Tests ====================
    
    @Test
    void testDeleteRecipe_WhenRecipeExists_ThenDeletesRecipe() {
        // Arrange
        Long recipeId = 1L;
        when(recipeRepository.existsById(recipeId)).thenReturn(true);
        doNothing().when(recipeRepository).deleteById(recipeId);
        
        // Act
        recipeService.deleteRecipe(recipeId);
        
        // Assert
        verify(recipeRepository, times(1)).existsById(recipeId);
        verify(recipeRepository, times(1)).deleteById(recipeId);
    }
    
    @Test
    void testDeleteRecipe_WhenRecipeDoesNotExist_ThenThrowsException() {
        // Arrange
        Long nonExistentId = 999L;
        when(recipeRepository.existsById(nonExistentId)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> recipeService.deleteRecipe(nonExistentId)
        );
        
        assertEquals("Recipe not found with id: 999", exception.getMessage());
        verify(recipeRepository, times(1)).existsById(nonExistentId);
        verify(recipeRepository, never()).deleteById(nonExistentId);
    }
    
    @Test
    void testDeleteRecipe_WhenMultipleRecipes_ThenDeletesCorrectOne() {
        // Arrange
        Long recipeId = 2L;
        when(recipeRepository.existsById(recipeId)).thenReturn(true);
        doNothing().when(recipeRepository).deleteById(recipeId);
        
        // Act
        recipeService.deleteRecipe(recipeId);
        
        // Assert
        verify(recipeRepository, times(1)).existsById(recipeId);
        verify(recipeRepository, times(1)).deleteById(recipeId);
        verify(recipeRepository, never()).deleteById(1L);
        verify(recipeRepository, never()).deleteById(3L);
    }
}
