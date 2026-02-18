package com.coveros.training.flavorhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe with its ingredients and instructions
 */
@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Recipe name is required")
    @Size(min = 3, max = 100, message = "Recipe name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Prep time is required")
    @Column(name = "prep_time")
    @Min(value = 0, message = "Prep time must be positive")
    private Integer prepTime; // in minutes
    
    @NotNull(message = "Cook time is required")
    @Column(name = "cook_time")
    @Min(value = 0, message = "Cook time must be positive")
    private Integer cookTime; // in minutes
    
    @NotNull(message = "Servings is required")
    @Column(name = "servings")
    @Min(value = 1, message = "Servings must be at least 1")
    private Integer servings;
    
    @Pattern(regexp = "Easy|Medium|Hard", message = "Difficulty level must be Easy, Medium, or Hard")
    @Column(name = "difficulty_level")
    private String difficultyLevel; // e.g., "Easy", "Medium", "Hard"
    
    @Size(max = 50, message = "Cuisine type must not exceed 50 characters")
    @Column(name = "cuisine_type")
    private String cuisineType; // e.g., "Italian", "Mexican", "Asian"
    
    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "recipe_instructions", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "step_number")
    private List<String> instructions = new ArrayList<>();
    
    @Column(name = "image_url")
    private String imageUrl;
    
    public Recipe(String name, String description, Integer prepTime, Integer cookTime, 
                  Integer servings, String difficultyLevel, String cuisineType) {
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.servings = servings;
        this.difficultyLevel = difficultyLevel;
        this.cuisineType = cuisineType;
    }
}
