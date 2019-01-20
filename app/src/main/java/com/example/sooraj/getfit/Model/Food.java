package com.example.sooraj.getfit.Model;

public class Food {

    /**
     * Fields
     */
    private int calories;
    private int fat;
    private int carbs;
    private int protein;
    private String id;
    private String name;
    private String brand;
    private String servingSize;

    /**
     * Default empty constructor
     */
    public Food() {

    }

    /**
     * Start of "getter" methods
     *
     */
    public int getCalories() {
        return calories;
    }

    public int getFat() {
        return fat;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getProtein() {
        return protein;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getServingSize() {
        return servingSize;
    }
    /**
     * End of "getter" methods
     */


    /**
     * Start of "setter" methods
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }
    /**
     * End of "setter" methods
     */
}
