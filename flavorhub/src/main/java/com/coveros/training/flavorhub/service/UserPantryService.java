package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.exception.ResourceNotFoundException;
import com.coveros.training.flavorhub.model.Ingredient;
import com.coveros.training.flavorhub.model.UserPantry;
import com.coveros.training.flavorhub.repository.IngredientRepository;
import com.coveros.training.flavorhub.repository.UserPantryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user pantry.
 * Provides business logic for pantry operations including adding, updating,
 * and removing ingredients from a user's pantry inventory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserPantryService {
    
    private final UserPantryRepository userPantryRepository;
    private final IngredientRepository ingredientRepository;
    
    /**
     * Retrieves all pantry items for a specific user.
     *
     * @param userId the unique identifier of the user (must be positive)
     * @return a list of all pantry items belonging to the user
     */
    @Transactional(readOnly = true)
    public List<UserPantry> getUserPantry(@NotNull @Min(1) Long userId) {
        log.debug("Fetching pantry items for user: {}", userId);
        List<UserPantry> pantryItems = userPantryRepository.findByUserId(userId);
        log.debug("Found {} pantry items for user {}", pantryItems.size(), userId);
        return pantryItems;
    }
    
    /**
     * Retrieves a specific pantry item by its unique identifier.
     *
     * @param id the unique identifier of the pantry item (must be positive)
     * @return an Optional containing the pantry item if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<UserPantry> getPantryItemById(@NotNull @Min(1) Long id) {
        log.debug("Fetching pantry item with id: {}", id);
        Optional<UserPantry> pantryItem = userPantryRepository.findById(id);
        if (pantryItem.isEmpty()) {
            log.warn("Pantry item not found with id: {}", id);
        }
        return pantryItem;
    }
    
    /**
     * Adds a new item to the user's pantry.
     *
     * @param pantryItem the pantry item to add (must be valid)
     * @return the saved pantry item with generated ID
     * @throws jakarta.validation.ConstraintViolationException if the pantry item is invalid
     */
    @Transactional
    public UserPantry addPantryItem(@NotNull @Valid UserPantry pantryItem) {
        log.info("Adding new pantry item for user: {}", pantryItem.getUserId());
        UserPantry saved = userPantryRepository.save(pantryItem);
        log.debug("Pantry item added successfully with id: {}", saved.getId());
        return saved;
    }
    
    /**
     * Updates an existing pantry item with new information.
     *
     * @param id the unique identifier of the pantry item to update (must be positive)
     * @param updatedPantryItem the updated pantry item data (must be valid)
     * @return the updated pantry item
     * @throws ResourceNotFoundException if the pantry item does not exist
     */
    @Transactional
    public UserPantry updatePantryItem(@NotNull @Min(1) Long id, @NotNull @Valid UserPantry updatedPantryItem) {
        log.info("Attempting to update pantry item with id: {}", id);
        return userPantryRepository.findById(id)
            .map(existing -> {
                log.debug("Updating pantry item {} - quantity: {} {}", 
                        id, updatedPantryItem.getQuantity(), updatedPantryItem.getUnit());
                existing.setQuantity(updatedPantryItem.getQuantity());
                existing.setUnit(updatedPantryItem.getUnit());
                existing.setNotes(updatedPantryItem.getNotes());
                UserPantry saved = userPantryRepository.save(existing);
                log.info("Pantry item updated successfully with id: {}", id);
                return saved;
            })
            .orElseThrow(() -> {
                log.warn("Pantry item not found with id: {}", id);
                return new ResourceNotFoundException("Pantry item not found with id: " + id);
            });
    }
    
    /**
     * Deletes a pantry item by its unique identifier.
     *
     * @param id the unique identifier of the pantry item to delete (must be positive)
     * @throws ResourceNotFoundException if the pantry item does not exist
     */
    @Transactional
    public void deletePantryItem(@NotNull @Min(1) Long id) {
        log.info("Attempting to delete pantry item with id: {}", id);
        if (!userPantryRepository.existsById(id)) {
            log.warn("Pantry item not found with id: {}", id);
            throw new ResourceNotFoundException("Pantry item not found with id: " + id);
        }
        userPantryRepository.deleteById(id);
        log.info("Pantry item deleted successfully with id: {}", id);
    }
    
    /**
     * Clears all pantry items for a specific user.
     * This is a bulk operation that should be used with caution.
     *
     * @param userId the unique identifier of the user whose pantry should be cleared (must be positive)
     * @return the number of pantry items that were deleted
     */
    @Transactional
    public int clearUserPantry(@NotNull @Min(1) Long userId) {
        log.info("Attempting to clear all pantry items for user: {}", userId);
        List<UserPantry> items = userPantryRepository.findByUserId(userId);
        int count = items.size();
        if (count > 0) {
            log.warn("Clearing {} pantry items for user {}", count, userId);
            userPantryRepository.deleteByUserId(userId);
            log.info("Successfully cleared {} pantry items for user {}", count, userId);
        } else {
            log.debug("No pantry items to clear for user {}", userId);
        }
        return count;
    }
    
    /**
     * Check if user has sufficient quantity of an ingredient
     * NOTE: This method is intentionally left incomplete for workshop participants
     */
    // TODO: Implement method to check if user has enough of an ingredient
    
    /**
     * Get list of ingredient names that user has in pantry
     * NOTE: Workshop participants will implement this using Copilot
     */
    // TODO: Implement method to get ingredient names from user's pantry
}
