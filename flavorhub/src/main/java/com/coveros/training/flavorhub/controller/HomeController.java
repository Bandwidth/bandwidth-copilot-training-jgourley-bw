package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

/**
 * Controller for serving the main web pages
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final RecipeService recipeService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    /**
     * Display the recipes browsing page with optional filtering
     * @param difficulty Optional difficulty level filter (empty string or null shows all)
     * @param cuisine Optional cuisine type filter (empty string or null shows all)
     * @param search Optional search term filter (empty string or null shows all)
     * @param model Model to hold recipe data
     * @return the recipes view template
     */
    @GetMapping("/recipes")
    public String recipes(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Recipe> recipes;
        
        // Filter recipes based on parameters
        // Empty string or null means "All" - show all recipes
        boolean hasDifficultyFilter = difficulty != null && !difficulty.isEmpty();
        boolean hasCuisineFilter = cuisine != null && !cuisine.isEmpty();
        boolean hasSearchFilter = search != null && !search.isEmpty();
        
        // Start with all recipes or search results
        if (hasSearchFilter) {
            recipes = recipeService.searchRecipes(search);
        } else {
            recipes = recipeService.getAllRecipes();
        }
        
        // Apply additional filters if specified
        if (hasDifficultyFilter) {
            recipes = recipes.stream()
                    .filter(r -> Objects.equals(difficulty, r.getDifficultyLevel()))
                    .toList();
        }
        
        if (hasCuisineFilter) {
            recipes = recipes.stream()
                    .filter(r -> Objects.equals(cuisine, r.getCuisineType()))
                    .toList();
        }
        
        model.addAttribute("recipes", recipes);
        return "recipes";
    }
}
