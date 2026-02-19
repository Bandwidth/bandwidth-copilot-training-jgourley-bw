package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.exception.ResourceNotFoundException;
import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing recipes.
 * Provides business logic for recipe operations including CRUD operations,
 * searching, filtering by difficulty and cuisine, and recipe recommendations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    /**
     * Retrieves all recipes from the database.
     *
     * @return a list of all recipes
     */
    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        log.debug("Fetching all recipes");
        List<Recipe> recipes = recipeRepository.findAll();
        log.debug("Found {} recipes", recipes.size());
        return recipes;
    }
    
    /**
     * Retrieves all recipes with pagination support.
     *
     * @param pageable pagination information including page number, size, and sorting
     * @return a page of recipes
     */
    @Transactional(readOnly = true)
    public Page<Recipe> getAllRecipes(@NotNull Pageable pageable) {
        log.debug("Fetching recipes with pagination: {}", pageable);
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        log.debug("Found {} recipes on page {} of {}", 
                recipes.getNumberOfElements(), 
                recipes.getNumber(), 
                recipes.getTotalPages());
        return recipes;
    }
    
    /**
     * Retrieves a recipe by its unique identifier.
     *
     * @param id the unique identifier of the recipe (must be positive)
     * @return an Optional containing the recipe if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Recipe> getRecipeById(@NotNull @Min(1) Long id) {
        log.debug("Fetching recipe with id: {}", id);
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            log.warn("Recipe not found with id: {}", id);
        }
        return recipe;
    }
    
    /**
     * Retrieves all recipes with a specific difficulty level.
     *
     * @param difficultyLevel the difficulty level to filter by (e.g., "Easy", "Medium", "Hard")
     * @return a list of recipes with the specified difficulty level
     */
    @Transactional(readOnly = true)
    public List<Recipe> getRecipesByDifficulty(@NotBlank String difficultyLevel) {
        log.debug("Fetching recipes by difficulty: {}", difficultyLevel);
        List<Recipe> recipes = recipeRepository.findByDifficultyLevel(difficultyLevel);
        log.debug("Found {} recipes with difficulty '{}'", recipes.size(), difficultyLevel);
        return recipes;
    }
    
    /**
     * Retrieves all recipes with a specific difficulty level with pagination.
     *
     * @param difficultyLevel the difficulty level to filter by
     * @param pageable pagination information
     * @return a page of recipes with the specified difficulty level
     */
    @Transactional(readOnly = true)
    public Page<Recipe> getRecipesByDifficulty(@NotBlank String difficultyLevel, @NotNull Pageable pageable) {
        log.debug("Fetching recipes by difficulty '{}' with pagination: {}", difficultyLevel, pageable);
        return recipeRepository.findByDifficultyLevel(difficultyLevel, pageable);
    }
    
    /**
     * Retrieves all recipes of a specific cuisine type.
     *
     * @param cuisineType the cuisine type to filter by (e.g., "Italian", "Mexican", "Asian")
     * @return a list of recipes with the specified cuisine type
     */
    @Transactional(readOnly = true)
    public List<Recipe> getRecipesByCuisine(@NotBlank String cuisineType) {
        log.debug("Fetching recipes by cuisine: {}", cuisineType);
        List<Recipe> recipes = recipeRepository.findByCuisineType(cuisineType);
        log.debug("Found {} recipes with cuisine '{}'", recipes.size(), cuisineType);
        return recipes;
    }
    
    /**
     * Retrieves all recipes of a specific cuisine type with pagination.
     *
     * @param cuisineType the cuisine type to filter by
     * @param pageable pagination information
     * @return a page of recipes with the specified cuisine type
     */
    @Transactional(readOnly = true)
    public Page<Recipe> getRecipesByCuisine(@NotBlank String cuisineType, @NotNull Pageable pageable) {
        log.debug("Fetching recipes by cuisine '{}' with pagination: {}", cuisineType, pageable);
        return recipeRepository.findByCuisineType(cuisineType, pageable);
    }
    
    /**
     * Searches for recipes whose names contain the specified search term (case-insensitive).
     *
     * @param searchTerm the term to search for in recipe names
     * @return a list of matching recipes
     */
    @Transactional(readOnly = true)
    public List<Recipe> searchRecipes(@NotBlank String searchTerm) {
        log.debug("Searching recipes with term: {}", searchTerm);
        List<Recipe> recipes = recipeRepository.findByNameContainingIgnoreCase(searchTerm);
        log.debug("Found {} recipes matching '{}'", recipes.size(), searchTerm);
        return recipes;
    }
    
    /**
     * Searches for recipes whose names contain the specified search term with pagination.
     *
     * @param searchTerm the term to search for in recipe names
     * @param pageable pagination information
     * @return a page of matching recipes
     */
    @Transactional(readOnly = true)
    public Page<Recipe> searchRecipes(@NotBlank String searchTerm, @NotNull Pageable pageable) {
        log.debug("Searching recipes with term '{}' and pagination: {}", searchTerm, pageable);
        return recipeRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    }
    
    /**
     * Saves a new recipe or updates an existing one.
     *
     * @param recipe the recipe to save (must be valid)
     * @return the saved recipe with generated ID if new
     * @throws jakarta.validation.ConstraintViolationException if the recipe is invalid
     */
    @Transactional
    public Recipe saveRecipe(@NotNull @Valid Recipe recipe) {
        if (recipe.getId() == null) {
            log.info("Creating new recipe: {}", recipe.getName());
        } else {
            log.info("Updating recipe with id: {}", recipe.getId());
        }
        Recipe saved = recipeRepository.save(recipe);
        log.debug("Recipe saved successfully with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Deletes a recipe by its unique identifier.
     *
     * @param id the unique identifier of the recipe to delete (must be positive)
     * @throws ResourceNotFoundException if the recipe does not exist
     */
    @Transactional
    public void deleteRecipe(@NotNull @Min(1) Long id) {
        log.info("Attempting to delete recipe with id: {}", id);
        if (!recipeRepository.existsById(id)) {
            log.warn("Recipe not found with id: {}", id);
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
        log.info("Recipe deleted successfully with id: {}", id);
    }
    
    /**
     * Find recipes that can be made based on available ingredients in the pantry
     * NOTE: This method is intentionally left incomplete for workshop participants
     * Participants will use GitHub Copilot to implement this recommendation logic
     */
    // TODO: Implement method to recommend recipes based on pantry ingredients
    
    /**
     * Get recipes that match specific dietary requirements or filters
     * NOTE: This is a more advanced feature to be implemented during the workshop
     */
    // TODO: Implement advanced filtering logic
}
