package com.example.sooraj.getfit;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sooraj.getfit.Model.Food;
import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodFragment extends Fragment {

    /**
     * Fields
     */
    FirebaseDatabase database;
    DatabaseReference users;
    String username;
    User user;
    ProgressBar progressCalories, progressFat, progressCarbs, progressProtein;
    TextView textCaloriesBar, textFatBar, textCarbsBar, textProteinBar;
    private ArrayList<Food> foodResults = new ArrayList<>();
    private ArrayList<Food> filteredFoodResults = new ArrayList<>();
    private View view;
    private SearchView search;
    private ListView searchResults;

    /**
     * Get reference to all components of the fragment's view
     * Get reference to Firebase Database, and the "Users" node
     * Get reference to current user from the current activity
     * @param inflater the LayoutInflater used by the MainActivity
     * @param container ViewGroup that this fragment is a part of
     * @param saveInstanceState state of the application the last time it was closed
     * @return the view corresponding to this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_food, container, false);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        searchResults = view.findViewById(R.id.search_results);
        progressCalories = view.findViewById(R.id.progressBarCalories);
        progressFat = view.findViewById(R.id.progressBarFat);
        progressCarbs = view.findViewById(R.id.progressBarCarbs);
        progressProtein = view.findViewById(R.id.progressBarProtein);
        textCaloriesBar = view.findViewById(R.id.textCaloriesBar);
        textFatBar = view.findViewById(R.id.textFatBar);
        textCarbsBar = view.findViewById(R.id.textCarbsBar);
        textProteinBar = view.findViewById(R.id.textProteinBar);
        searchResults.setVisibility(View.INVISIBLE);
        return view;
    }

    /**
     * Inflates options menu and configures search bar
     * @param menu Menu used by the current activity
     * @param inflater MenuInflater used by the current activity
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_food, menu);
        search = (SearchView) menu.getItem(0).getActionView();
        search.setQueryHint("Start typing to search...");


        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || search.getQuery().length() > 0)
                    setProgressInvisible();
                else
                    setProgressVisible();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 3) {
                    myAsyncTask m = new myAsyncTask();
                    m.execute(newText);
                }
                return false;
            }

        });

        updateDisplay();
        setProgressVisible();

    }

    /**
     * Filters search results based on user's input
     * @param newText user's input
     */
    public void filterFoodArray(String newText) {
        String fName;

        filteredFoodResults.clear();
        for (int i = 0; i < foodResults.size(); i++) {
            fName = foodResults.get(i).getName().toLowerCase();
            if (fName.contains(newText.toLowerCase())) {
                filteredFoodResults.add(foodResults.get(i));
            }
        }
    }

    /**
     * @return returns view corresponding to this fragment
     */
    private View getMyView() {
        return view;
    }

    /**
     * Updates progress bars and text to match values stored in Firebase
     * Animates progress bars
     */
    public void updateDisplay() {

        textCaloriesBar.setText(user.getCalories() + "/" + user.getCalorieGoal() + "\nCalories");
        textFatBar.setText(user.getFat() + "/" + user.getFatGoal() + "\nFat");
        textCarbsBar.setText(user.getCarbs() + "/" + user.getCarbGoal() + "\nCarbs");
        textProteinBar.setText(user.getProtein() + "/" + user.getProteinGoal() + "\nProtein");

        progressCalories.setProgress(0);
        progressFat.setProgress(0);
        progressCarbs.setProgress(0);
        progressProtein.setProgress(0);

        ObjectAnimator.ofInt(progressCalories, "progress", ((user.getCalories() * 10000) / user.getCalorieGoal())).setDuration(1000).start();
        ObjectAnimator.ofInt(progressFat, "progress", ((user.getFat() * 10000) / user.getFatGoal())).setDuration(1000).start();
        ObjectAnimator.ofInt(progressCarbs, "progress", ((user.getCarbs() * 10000) / user.getCarbGoal())).setDuration(1000).start();
        ObjectAnimator.ofInt(progressProtein, "progress", ((user.getProtein() * 10000) / user.getProteinGoal())).setDuration(1000).start();


    }

    /**
     * Sets search result's visibility to invisible
     * Sets progress bars and texts' visibility to visible
     */
    public void setProgressVisible() {
        searchResults.setVisibility(View.INVISIBLE);
        progressCalories.setVisibility(View.VISIBLE);
        progressFat.setVisibility(View.VISIBLE);
        progressCarbs.setVisibility(View.VISIBLE);
        progressProtein.setVisibility(View.VISIBLE);
        textCaloriesBar.setVisibility(View.VISIBLE);
        textFatBar.setVisibility(View.VISIBLE);
        textCarbsBar.setVisibility(View.VISIBLE);
        textProteinBar.setVisibility(View.VISIBLE);
    }

    /**
     * Sets search result's visibility to visible
     * Sets progress bars and texts' visibility to invisible
     */
    public void setProgressInvisible() {
        searchResults.setVisibility(View.VISIBLE);
        progressCalories.setVisibility(View.INVISIBLE);
        progressFat.setVisibility(View.INVISIBLE);
        progressCarbs.setVisibility(View.INVISIBLE);
        progressProtein.setVisibility(View.INVISIBLE);
        textCaloriesBar.setVisibility(View.INVISIBLE);
        textFatBar.setVisibility(View.INVISIBLE);
        textCarbsBar.setVisibility(View.INVISIBLE);
        textProteinBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Custom AsyncTask used to retrieve data from Nutritionix API
     */
    class myAsyncTask extends AsyncTask<String, Void, String> {


        String textSearch;
        RequestQueue rQueue;
        JsonObjectRequest request;
        private JSONArray foodList;

        /**
         * Creates and gets reference to a RequestQueue which is to be used to request JSONObjects from the Nutritionix API
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        /**
         * Searches Nutritionix API for food whose name contains the users input
         * @param sText array of user input into search bar
         * @return "Done" if search was successful
         *         "Exception Caught" if search was unsuccessful
         */
        @Override
        protected String doInBackground(String[] sText) {

            final String[] search = sText;
            String url = "https://api.nutritionix.com/v1_1/search/" + sText[0] + "?results=0%3A50&cal_min=0&cal_max=50000&fields=*&appId=b2f7efb9&appKey=6c6117ee833ec15cb3018340a39e5d3b";
            request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                foodList = response.getJSONArray("hits");
                                String s = getFoodList();
                                textSearch = search[0];

                                filterFoodArray(textSearch);
                                searchResults.setAdapter(new SearchResultsAdapter(getActivity(), filteredFoodResults));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            rQueue.add(request);
            return "Done";
        }

        /**
         * Unwraps JSONObject returned by API search and adds individual Food objects to an array of search results
         * @return "OK" if food objects were successfully initialized and added to foodResults array
         *         "Exception Caught" if any problem occurred in the process
         */
        public String getFoodList() {

            try {

                System.out.println(foodList.length());
                for (int i = 0; i < foodList.length(); i++) {

                    Food tempFood = new Food();
                    String matchFound = "N";


                    try {
                        JSONObject obj = foodList.getJSONObject(i);
                        JSONObject fields = obj.getJSONObject("fields");
                        tempFood.setCalories(fields.getInt("nf_calories"));
                        tempFood.setFat(fields.getInt("nf_total_fat"));
                        tempFood.setCarbs(fields.getInt("nf_total_carbohydrate"));
                        tempFood.setProtein(fields.getInt("nf_protein"));
                        tempFood.setId(fields.getString("item_id"));
                        tempFood.setName(fields.getString("item_name"));
                        tempFood.setBrand(fields.getString("brand_name"));
                        if (fields.getInt("nf_serving_size_qty") == 0) {
                            tempFood.setServingSize("1 serving(s)");
                        } else {
                            tempFood.setServingSize(fields.getInt("nf_serving_size_qty") + " " + fields.getString("nf_serving_size_unit") + "(s)");
                        }
                    } catch (Exception e) {
                        continue;
                    }


                    for (int j = 0; i < foodResults.size(); j++) {
                        if (foodResults.get(j).getId().equals(tempFood.getId())) {
                            matchFound = "Y";

                        }

                        if (j == foodResults.size() - 1) {
                            break;
                        }
                    }

                    if (matchFound.equals("N")) {
                        foodResults.add(tempFood);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception Caught";
            }

            return "OK";

        }

        /**
         * Notifies user if search was unsuccessful
         * @param results whether or not search was successful, the string returned by the doInBackground method
         */
        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            if (results.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getActivity(), "Unable to connect to server :/", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Custom adapter for searchResults ListView
     */
    class SearchResultsAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<Food> foodDetails = new ArrayList<>();

        /**
         * Constructor
         * @param context current application context
         * @param foodSearchResults search results from API search
         */
        public SearchResultsAdapter(Context context, ArrayList<Food> foodSearchResults) {
            layoutInflater = LayoutInflater.from(context);
            foodDetails.addAll(foodSearchResults);
            this.count = foodSearchResults.size();
            this.context = context;
        }

        /**
         * @return amount of elements there will be in ListView
         */
        @Override
        public int getCount() {
            return count;
        }

        /**
         * Returns food object at index i
         * @param i index of object
         * @return food object at index i
         */
        @Override
        public Object getItem(int i) {
            return foodDetails.get(i);
        }

        /**
         * Required method in order to be subclass of BaseAdapter
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Configures element of ListView
         * @param i index of element in ListView
         * @param view view of the element in index i
         * @param viewGroup the ListView
         * @return the view of the configured element in the ListView
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            SearchResultsHolder holder;
            final Food tempFood = foodDetails.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.search_results_view, null);
                holder = new SearchResultsHolder();
                holder.foodName = view.findViewById(R.id.foodName);
                holder.textCalories = view.findViewById(R.id.textCalories);
                holder.textFat = view.findViewById(R.id.textFat);
                holder.textCarbs = view.findViewById(R.id.textCarbs);
                holder.textProtein = view.findViewById(R.id.textProtein);
                holder.brandText = view.findViewById(R.id.brandText);
                holder.servingSize = view.findViewById(R.id.servingSize);
                holder.addFood = view.findViewById(R.id.addFood);

                holder.addFood.setOnClickListener(new View.OnClickListener() {

                    /**
                     * When addFood button is clicked, inflate an AlertDialog asking how many servings were eaten
                     * Calculate amount of calories and macro-nutrients based on how many servings were eaten
                     * Update the user's calorie and macro-nutrient totals for the day in Firebase
                     * Update the display using updateDisplay method
                     * @param view view of the addFood button
                     */
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Servings");
                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.serving_number_dialog, (ViewGroup) getMyView(), false);
                        final EditText input = viewInflated.findViewById(R.id.servingsNumber);
                        builder.setView(viewInflated);

                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                double numOfServings = Double.parseDouble(input.getText().toString());
                                user.setCalories(user.getCalories() + (int) (tempFood.getCalories() * numOfServings));
                                user.setFat(user.getFat() + (int) (tempFood.getFat() * numOfServings));
                                user.setCarbs(user.getCarbs() + (int) (tempFood.getCarbs() * numOfServings));
                                user.setProtein(user.getProtein() + (int) (tempFood.getProtein() * numOfServings));
                                users.child(username).child("calories").setValue(user.getCalories());
                                users.child(username).child("fat").setValue(user.getFat());
                                users.child(username).child("carbs").setValue(user.getCarbs());
                                users.child(username).child("protein").setValue(user.getProtein());
                                updateDisplay();
                                search.setQuery("", false);
                                search.clearFocus();

                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });

                view.setTag(holder);
            } else {
                holder = (SearchResultsHolder) view.getTag();
            }

            holder.foodName.setText(tempFood.getName());
            holder.textCalories.setText(tempFood.getCalories() + "");
            holder.textFat.setText(tempFood.getFat() + "");
            holder.textCarbs.setText(tempFood.getCarbs() + "");
            holder.textProtein.setText(tempFood.getProtein() + "");
            holder.servingSize.setText(tempFood.getServingSize());
            holder.brandText.setText(tempFood.getBrand());

            return view;
        }

        /**
         * Structure to hold all of the components of a list element
         */
        class SearchResultsHolder {
            TextView foodName;
            TextView textCalories;
            TextView textFat;
            TextView textCarbs;
            TextView textProtein;
            TextView brandText;
            TextView servingSize;
            Button addFood;

        }
    }
}
