package com.example.sooraj.getfit.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String email;//
    private String username;//
    private String password;//
    private String gender;
    private int weight;//
    private int height;//
    private int age;//
    private double bmi;//
    private int steps;
    private int stepGoal;//
    private int weightGoal;//lose = 0, maintain 1, gain(lean muscle) = 2
    private int caloriesBurned;
    private int calories;
    private int calorieGoal;//
    private int carbs;
    private int fat;
    private int protein;
    private int carbGoal;//
    private int fatGoal;//
    private int proteinGoal;//
    private int activityLevel;//0 = sedentary, 1 = light, 2 = moderate, 3 = heavy, 4 = very heavy
    private String lastSeen;
    private HashMap<String, Integer> stepsStorage;
    private HashMap<String, Integer> calorieStorage;
    private HashMap<String, Integer> weightStorage;
    private ArrayList<String> friendList;
    private HashMap<String, ArrayList<String>> workoutInvites;
    private HashMap<String, ArrayList<String>> workoutCalendar;
    private ArrayList<String> friendRequests;


    private boolean setUpCompleted = false;

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        stepsStorage = new HashMap<>();
        weightStorage = new HashMap<>();
        calorieStorage = new HashMap<>();
        friendList = new ArrayList<>();
        workoutInvites = new HashMap<>();
        workoutCalendar = new HashMap<>();
        friendRequests = new ArrayList<>();
    }

    public User() {
        stepsStorage = new HashMap<>();
        calorieStorage = new HashMap<>();
        weightStorage = new HashMap<>();
        friendList = new ArrayList<>();
        workoutInvites = new HashMap<>();
        workoutCalendar = new HashMap<>();
        friendRequests = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(int weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getCarbGoal() {
        return carbGoal;
    }

    public void setCarbGoal(int carbGoal) {
        this.carbGoal = carbGoal;
    }

    public int getFatGoal() {
        return fatGoal;
    }

    public void setFatGoal(int fatGoal) {
        this.fatGoal = fatGoal;
    }

    public int getProteinGoal() {
        return proteinGoal;
    }

    public void setProteinGoal(int proteinGoal) {
        this.proteinGoal = proteinGoal;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public boolean isSetUpCompleted() {
        return setUpCompleted;
    }

    public void setSetUpCompleted(boolean setUpCompleted) {
        this.setUpCompleted = setUpCompleted;
    }

    public HashMap<String, Integer> getStepsStorage() {
        return stepsStorage;
    }

    public HashMap<String, Integer> getCalorieStorage() {
        return calorieStorage;
    }

    public void putStepsStorage(String date, int steps) {
        stepsStorage.put(date, steps);
    }

    public void putCalorieStorage(String date, int calories) {
        calorieStorage.put(date, calories);
    }

    public void putWeightStorage(String date, int weight) {
        weightStorage.put(date, weight);
    }

    public HashMap<String, Integer> getWeightStorage() {
        return weightStorage;
    }

    public void resetFood() {
        calories = 0;
        fat = 0;
        carbs = 0;
        protein = 0;
    }

    public void addFriend(String username) {
        friendList.add(username);
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void addWorkoutInvite(String username, String date, String location) {

        ArrayList<String> details = new ArrayList<>();
        details.add(date);
        details.add(location);
        workoutInvites.put(username, details);
    }

    public void acceptWorkoutInvite(String username) {

        ArrayList<String> details = new ArrayList<>();
        details.addAll(workoutInvites.get(username));


    }

    public HashMap<String, ArrayList<String>> getWorkoutInvites() {
        return workoutInvites;
    }

    public HashMap<String, ArrayList<String>> getWorkoutCalendar() {
        return workoutCalendar;
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public boolean hasFriendRequestFromUser(String username) {
        return friendRequests.contains(username);
    }

    public boolean isFriendOfUser(String username) {
        return friendList.contains(username);
    }

    public void addFriendRequest(String username) {
        friendRequests.add(username);

    }

    public void removeFriendRequestFromUser(String username) {
        friendRequests.remove(username);
    }

    public void removeFriend(String username) {
        friendList.remove(username);
    }

}
