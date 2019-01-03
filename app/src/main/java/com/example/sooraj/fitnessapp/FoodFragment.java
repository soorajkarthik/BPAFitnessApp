package com.example.sooraj.fitnessapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sooraj.fitnessapp.Model.Food;
import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference users;
    String username;
    User user;
    private ArrayList<Food> foodResults = new ArrayList<>();
    private ArrayList<Food> filteredFoodResults = new ArrayList<>();
    private View view;
    private SearchView search;
    private ListView searchResults;

    @Override
    public void onResume() {
        super.onResume();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_food, container, false);
        searchResults = view.findViewById(R.id.search_results);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_food, menu);
        search = (SearchView) menu.getItem(0).getActionView();
        search.setQueryHint("Start typing to search...");
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

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
                    searchResults.setVisibility(View.VISIBLE);
                    myAsyncTask m = new myAsyncTask();
                    m.execute(newText);
                } else {
                    searchResults.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.food_search) {
            Toast.makeText(getActivity(), "Clicked on " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

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

    private View getMyView() {
        return view;
    }

    public void updateDisplay() {

    }

    class myAsyncTask extends AsyncTask<String, Void, String> {


        String textSearch;
        RequestQueue rQueue;
        JsonObjectRequest request;
        private JSONArray foodList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        @Override
        protected String doInBackground(String... sText) {

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
                                //pd.dismiss();


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


        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            if (results.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getActivity(), "Unable to connect to server :/", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class SearchResultsAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<Food> foodDetails = new ArrayList<>();

        public SearchResultsAdapter(Context context, ArrayList<Food> food_details) {
            layoutInflater = LayoutInflater.from(context);

            foodDetails.addAll(food_details);
            this.count = food_details.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return foodDetails.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder;
            final Food tempFood = foodDetails.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.search_results_view, null);
                holder = new ViewHolder();
                holder.foodName = view.findViewById(R.id.foodName);
                holder.textCalories = view.findViewById(R.id.textCalories);
                holder.textFat = view.findViewById(R.id.textFat);
                holder.textCarbs = view.findViewById(R.id.textCarbs);
                holder.textProtein = view.findViewById(R.id.textProtein);
                holder.brandText = view.findViewById(R.id.brandText);
                holder.cals = view.findViewById(R.id.cals);
                holder.fat = view.findViewById(R.id.fat);
                holder.carbs = view.findViewById(R.id.carbs);
                holder.protein = view.findViewById(R.id.protein);
                holder.addFood = view.findViewById(R.id.addFood);

                holder.addFood.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Servings");
                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.serving_number_dialog, (ViewGroup) getMyView(), false);
                        final EditText input = (EditText) viewInflated.findViewById(R.id.servingsNumber);
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
                                searchResults.setVisibility(View.INVISIBLE);
                                updateDisplay();

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
                holder = (ViewHolder) view.getTag();
            }

            holder.foodName.setText(tempFood.getName());
            holder.textCalories.setText(tempFood.getCalories() + "");
            holder.textFat.setText(tempFood.getFat() + "");
            holder.textCarbs.setText(tempFood.getCarbs() + "");
            holder.textProtein.setText(tempFood.getProtein() + "");
            holder.brandText.setText(tempFood.getBrand());

            return view;
        }

        class ViewHolder {
            TextView foodName;
            TextView textCalories;
            TextView textFat;
            TextView textCarbs;
            TextView textProtein;
            TextView brandText;
            TextView cals;
            TextView fat;
            TextView carbs;
            TextView protein;
            Button addFood;

        }
    }
}
