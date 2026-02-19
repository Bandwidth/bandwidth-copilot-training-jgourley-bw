package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.exception.ResourceNotFoundException;
import com.coveros.training.flavorhub.model.Ingredient;
import com.coveros.training.flavorhub.repository.IngredientRepository;
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
 * Service for managing ingredients.
 * Provides business logic for ingredient operations including CRUD operations,
 * searching, and categorization.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class IngredientService {
    
    private final IngredientRepository ingredientRepository;
    
    /**
     * Retrieves all ingredients from the database.
     *
     * @return a list of all ingredients
     */
    @Transactional(readOnly = true)
    public List<Ingredient> getAllIngredients() {
        log.debug("Fetching all ingredients");
        List<Ingredient> ingredients = ingredientRepository.findAll();
        log.debug("Found {} ingredients", ingredients.size());
        return ingredients;
    }
    
    /**
     * Retrieves all ingredients with pagination support.
     *
     * @param pageable pagination information including page number, size, and sorting
     * @return a page of ingredients
     */
    @Transactional(readOnly = true)
    public Page<Ingredient> getAllIngredients(@NotNull Pageable pageable) {
        log.debug("Fetching ingredients with pagination: {}", pageable);
        Page<Ingredient> ingredients = ingredientRepository.findAll(pageable);
        log.debug("Found {} ingredients on page {} of {}", 
                ingredients.getNumberOfElements(), 
                ingredients.getNumber(), 
                ingredients.getTotalPages());
        return ingredients;
    }
    
    /**
     * Retrieves an ingredient by its unique identifier.
     *
     * @param id the unique identifier of the ingredient (must be positive)
     * @return an Optional containing the ingredient if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Ingredient> getIngredientById(@NotNull @Min(1) Long id) {
        log.debug("Fetching ingredient with id: {}", id);
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        if (ingredient.isEmpty()) {
            log.warn("Ingredient not found with id: {}", id);
        }
        return ingredient;
    }
    
    /**
     * Retrieves an ingredient by its name (case-insensitive).
     *
     * @param name the name of the ingredient to search for
     * @return an Optional containing the ingredient if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Ingredient> getIngredientByName(@NotBlank String name) {
        log.debug("Fetching ingredient with name: {}", name);
        return ingredientRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Retrieves all ingredients in a specific category.
     *
     * @param category the category to filter ingredients by
     * @return a list of ingredients in the specified category
     */
    @Transactional(readOnly = true)
    public List<Ingredient> getIngredientsByCategory(@NotBlank String category) {
        log.debug("Fetching ingredients by category: {}", category);
        List<Ingredient> ingredients = ingredientRepository.findByCategory(category);
        log.debug("Found {} ingredients in category '{}'", ingredients.size(), category);
        return ingredients;
    }
    
    /**
     * Retrieves all ingredients in a specific category with pagination.
     *
     * @param category the category to filter ingredients by
     * @param pageable pagination information
     * @return a page of ingredients in the specified category
     */
    @Transactional(readOnly = true)
    public Page<Ingredient> getIngredientsByCategory(@NotBlank String category, @NotNull Pageable pageable) {
        log.debug("Fetching ingredients by category '{}' with pagination: {}", category, pageable);
        return ingredientRepository.findByCategory(category, pageable);
    }
    
    /**
     * Searches for ingredients whose names contain the specified search term (case-insensitive).
     *
     * @param searchTerm the term to search for in ingredient names
     * @return a list of matching ingredients
     */
    @Transactional(readOnly = true)
    public List<Ingredient> searchIngredients(@NotBlank String searchTerm) {
        log.debug("Searching ingredients with term: {}", searchTerm);
        List<Ingredient> ingredients = ingredientRepository.findByNameContainingIgnoreCase(searchTerm);
        log.debug("Found {} ingredients matching '{}'", ingredients.size(), searchTerm);
        return ingredients;
    }
    
    /**
     * Searches for ingredients whose names contain the specified search term with pagination.
     *
     * @param searchTerm the term to search for in ingredient names
     * @param pageable pagination information
     * @return a page of matching ingredients
     */
    @Transactional(readOnly = true)
    public Page<Ingredient> searchIngredients(@NotBlank String searchTerm, @NotNull Pageable pageable) {
        log.debug("Searching ingredients with term '{}' and pagination: {}", searchTerm, pageable);
        return ingredientRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    }
    
    /**
     * Saves a new ingredient or updates an existing one.
     *
     * @param ingredient the ingredient to save (must be valid)
     * @return the saved ingredient with generated ID if new
     * @throws jakarta.validation.ConstraintViolationException if the ingredient is invalid
     */
    @Transactional
    public Ingredient saveIngredient(@NotNull @Valid Ingredient ingredient) {
        if (ingredient.getId() == null) {
            log.info("Creating new ingredient: {}", ingredient.getName());
        } else {
            log.info("Updating ingredient with id: {}", ingredient.getId());
        }
        Ingredient saved = ingredientRepository.save(ingredient);
        log.debug("Ingredient saved successfully with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Deletes an ingredient by its unique identifier.
     *
     * @param id the unique identifier of the ingredient to delete (must be positive)
     * @throws ResourceNotFoundException if the ingredient does not exist
     */
    @Transactional
    public void deleteIngredient(@NotNull @Min(1) Long id) {
        log.info("Attempting to delete ingredient with id: {}", id);
        if (!ingredientRepository.existsById(id)) {
            log.warn("Ingredient not found with id: {}", id);
            throw new ResourceNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
        log.info("Ingredient deleted successfully with id: {}", id);
    }
}
