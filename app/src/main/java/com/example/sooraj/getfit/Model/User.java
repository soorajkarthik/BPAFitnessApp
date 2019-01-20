package com.example.sooraj.getfit.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    /**
     * Fields
     */
    private String email;
    private String username;
    private String password;
    private String gender;
    private int weight;
    private int height;
    private int age;
    private double bmi;
    private int steps;
    private int stepGoal;
    private int weightGoal;//lose = 0, maintain 1, gain(lean muscle) = 2
    private int caloriesBurned;
    private int calories;
    private int calorieGoal;
    private int carbs;
    private int fat;
    private int protein;
    private int carbGoal;
    private int fatGoal;
    private int proteinGoal;
    private int activityLevel;//0 = sedentary, 1 = light, 2 = moderate, 3 = heavy, 4 = very heavy
    private long lastSeen;
    private HashMap<String, Integer> stepsStorage;
    private HashMap<String, Integer> calorieStorage;
    private HashMap<String, Integer> weightStorage;
    private ArrayList<String> friendList;
    private HashMap<String, ArrayList<String>> workoutInvites;
    private HashMap<String, ArrayList<String>> acceptedWorkouts;
    private ArrayList<String> friendRequests;
    private boolean setUpCompleted = false;

    /**
     * Constructor
     * @param email    email of user
     * @param username username of user
     * @param password password of user
     */
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        stepsStorage = new HashMap<>();
        weightStorage = new HashMap<>();
        calorieStorage = new HashMap<>();
        friendList = new ArrayList<>();
        workoutInvites = new HashMap<>();
        acceptedWorkouts = new HashMap<>();
        friendRequests = new ArrayList<>();
    }

    /**
     * Empty constructor
     */
    public User() {
        stepsStorage = new HashMap<>();
        calorieStorage = new HashMap<>();
        weightStorage = new HashMap<>();
        friendList = new ArrayList<>();
        workoutInvites = new HashMap<>();
        acceptedWorkouts = new HashMap<>();
        friendRequests = new ArrayList<>();
    }

    /**
     * Start of "getter" methods
     */
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public int getSteps() {
        return steps;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public int getWeightGoal() {
        return weightGoal;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getCalories() {
        return calories;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFat() {
        return fat;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbGoal() {
        return carbGoal;
    }

    public int getFatGoal() {
        return fatGoal;
    }

    public int getProteinGoal() {
        return proteinGoal;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public HashMap<String, Integer> getStepsStorage() {
        return stepsStorage;
    }

    public HashMap<String, Integer> getCalorieStorage() {
        return calorieStorage;
    }

    public HashMap<String, Integer> getWeightStorage() {
        return weightStorage;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public HashMap<String, ArrayList<String>> getWorkoutInvites() {
        return workoutInvites;
    }

    public HashMap<String, ArrayList<String>> getAcceptedWorkouts() {
        return acceptedWorkouts;
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    /**
     * End of "getter" methods
     */

    /**
     * Start of "setter" methods
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public void setWeightGoal(int weightGoal) {
        this.weightGoal = weightGoal;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setCarbGoal(int carbGoal) {
        this.carbGoal = carbGoal;
    }

    public void setFatGoal(int fatGoal) {
        this.fatGoal = fatGoal;
    }

    public void setProteinGoal(int proteinGoal) {
        this.proteinGoal = proteinGoal;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public void setSetUpCompleted(boolean setUpCompleted) {
        this.setUpCompleted = setUpCompleted;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * End of "setter" methods
     */

    /**
     * Stores the amount steps on a given date in a storage HashMap
     * @param date date the steps were taken on
     * @param steps amount of steps taken
     */
    public void putStepsStorage(String date, int steps) {
        stepsStorage.put(date, steps);
    }

    /**
     * Stores the amount of calories eaten on a given date in a storage HashMap
     * @param date date the calories were eaten
     * @param calories amount of calories eaten
     */
    public void putCalorieStorage(String date, int calories) {
        calorieStorage.put(date, calories);
    }

    /**
     * Stores the user's weight on a given date in a storage HashMap
     * @param date date the weight is being recorded
     * @param weight weight of the user
     */
    public void putWeightStorage(String date, int weight) {
        weightStorage.put(date, weight);
    }

    /**
     * Adds a specified user to the current user's friend list
     * @param username username of the specified user
     */
    public void addFriend(String username) {
        friendList.add(username);
    }

    /**
     * Sets users calories, fat, carbohydrates, and protein for the day to zero
     * Used for resetting the user's calories and macro-nutrients everyday at midnight
     */
    public void resetFood() {
        calories = 0;
        fat = 0;
        carbs = 0;
        protein = 0;
    }

    /**
     * Adds a workout invitation to the user's pending invitation list
     * @param username username of the user who sent the workout invitation
     * @param date     the date on which the invite sender wants to workout
     * @param location the location at which the invite sender wants to workout
     */
    public void addWorkoutInvite(String username, String date, String location) {

        ArrayList<String> details = new ArrayList<>();
        details.add(date);
        details.add(location);
        workoutInvites.put(username, details);
    }

    /**
     * Check to see if user has a friend request from another specified user
     * @param username username of the specified user
     * @return true if request exists, and false if request does not exist
     */
    public boolean hasFriendRequestFromUser(String username) {
        return friendRequests.contains(username);
    }

    /**
     * Check to see if user is friends with another specified user
     * @param username username of specified user
     * @return true if user is friend of the specified user, false if not
     */
    public boolean isFriendOfUser(String username) {
        return friendList.contains(username);
    }

    /**
     * Adds a friend request to the user's list of friend requests
     * @param username username of the friend request sender
     */
    public void addFriendRequest(String username) {
        friendRequests.add(username);

    }

    /**
     * Deletes friend request from a specified user
     * @param username username of specified user
     */
    public void removeFriendRequestFromUser(String username) {
        friendRequests.remove(username);
    }

    /**
     * Removes a friend from the user's friend list
     * @param username username of the friend who is to be removed
     */
    public void removeFriend(String username) {
        friendList.remove(username);
    }

    /**
     * Adds workout invitation from a specified user to a list of accepted workout requests
     *
     * @param username username of specified user
     */
    public void acceptWorkoutInviteFromUser(String username) {
        acceptedWorkouts.put(username, workoutInvites.remove(username));
    }

    /**
     * Deletes workout invitation from a specified user
     * @param username username of specified user
     */
    public void declineWorkoutRequestFromUser(String username) {
        workoutInvites.remove(username);
    }

    /**
     * Deletes accepted workout invitation from a specified user
     * @param username username of specified user
     */
    public void cancelWorkoutFromUser(String username) {
        acceptedWorkouts.remove(username);
    }
}
