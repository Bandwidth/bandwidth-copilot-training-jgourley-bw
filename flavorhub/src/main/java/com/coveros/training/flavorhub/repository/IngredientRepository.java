package com.coveros.training.flavorhub.repository;

import com.coveros.training.flavorhub.model.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Ingredient entities
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    /**
     * Find an ingredient by name (case-insensitive)
     */
    Optional<Ingredient> findByNameIgnoreCase(String name);
    
    /**
     * Find all ingredients in a specific category
     */
    List<Ingredient> findByCategory(String category);
    
    /**
     * Find ingredients with names containing the search term (case-insensitive)
     */
    List<Ingredient> findByNameContainingIgnoreCase(String searchTerm);
    
    /**
     * Find ingredients with names containing the search term (case-insensitive) with pagination
     *
     * @param searchTerm the search term to look for in ingredient names
     * @param pageable pagination information
     * @return a page of ingredients matching the search term
     */
    Page<Ingredient> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);
    
    /**
     * Find all ingredients in a specific category with pagination
     *
     * @param category the category to filter by
     * @param pageable pagination information
     * @return a page of ingredients in the specified category
     */
    Page<Ingredient> findByCategory(String category, Pageable pageable);
}
