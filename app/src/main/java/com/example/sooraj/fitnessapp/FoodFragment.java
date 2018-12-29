package com.example.sooraj.fitnessapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sooraj.fitnessapp.Model.Food;


import org.json.*;

import java.util.ArrayList;

public class FoodFragment extends Fragment {

    private View view;
    private SearchView search;
    private ListView searchResults;
    private String found = "N";

    ArrayList<Food> foodResults = new ArrayList<>();
    ArrayList<Food> filteredFoodResults = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_food, container, false);
        final MainActivity activity = (MainActivity) getActivity();
        searchResults = view.findViewById(R.id.search_results);
        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_food, menu);
        search = view.findViewById(R.id.food_search);
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

                if(newText.length() > 3) {
                    searchResults.setVisibility(View.VISIBLE);
                    myAsyncTask m = new myAsyncTask();
                    m.execute(newText);
                }
                else {
                    searchResults.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.food_search) {
            Toast.makeText(getActivity(), "Clicked on " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {


        JSONArray foodList;
        String url = new String();
        String textSearch;
        ProgressDialog pd;
        RequestQueue rQueue;
        JsonObjectRequest request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            foodList = new JSONArray();
            rQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

            request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                foodList = response.getJSONArray("hits");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected String doInBackground(String... sText) {

            String url = "https://api.nutritionix.com/v1_1/search/" + sText + "?results=0%3A20&cal_min=0&cal_max=50000&fields=*&appId=b2f7efb9&appKey=6c6117ee833ec15cb3018340a39e5d3b";
            String returnResult = getFoodList(url);
            this.textSearch = sText[0];
            return returnResult;
        }

        public String getFoodList(String url) {
            Food tempFood = new Food();
            String matchFound = "N";
            Log.d("AsyncTask", "FoodResults size: " + foodResults.size());

            try {

                rQueue.add(request);

                for(int i = 0; i < foodList.length(); i++) {
                    tempFood = new Food();

                    JSONObject obj = foodList.getJSONObject(i);

                    tempFood.setCalories(obj.getInt("nf_calories"));
                    tempFood.setFat(obj.getInt("nf_total_fat"));
                    tempFood.setCarbs(obj.getInt("nf_total_carbohydrates"));
                    tempFood.setProtein(obj.getInt("nf_protein"));
                    tempFood.setId(obj.getString("item_id"));
                    tempFood.setName(obj.getString("item_name"));

                    matchFound = "N";

                    for(int j = 0; i < foodResults.size(); j++) {
                        if(foodResults.get(j).getId().equals(tempFood.getId())) {
                            matchFound = "Y";
                        }
                    }

                    if(matchFound.equals("N")) {
                        foodResults.add(tempFood);
                    }


                }


            } catch (Exception e) {e.printStackTrace(); return "Exception Caught";}

            return "OK";

        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            if(results.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getActivity(), "Unable to connect to server :/", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            else {
                Log.d("AsyncTask", "existing onPostExecute");
                filterFoodArray(textSearch);
                searchResults.setAdapter(new SearchResultsAdapter(getActivity(), filteredFoodResults));
            }
        }
    }

    public void filterFoodArray(String newText) {
        String fName;

        filteredFoodResults.clear();
        for (int i = 0; i < foodResults.size(); i++) {
            fName = foodResults.get(i).getName().toLowerCase();
            if(fName.contains(newText.toLowerCase())) {
                filteredFoodResults.add(foodResults.get(i));
            }
        }
    }

    class SearchResultsAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ArrayList<Food> foodDetails = new ArrayList<>();
        int count;
        Context context;

        public SearchResultsAdapter(Context context, ArrayList<Food> food_details) {
            layoutInflater = LayoutInflater.from(context);

            this.foodDetails  = food_details;
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
            Food tempFood = foodDetails.get(i);
            if(view == null) {
                view = layoutInflater.inflate(R.layout.search_results_view, null);
                holder = new ViewHolder();
                holder.foodName = view.findViewById(R.id.foodName);
                holder.textCalories = view.findViewById(R.id.textCalories);
                holder.textFat = view.findViewById(R.id.textFat);
                holder.textCarbs = view.findViewById(R.id.textCarbs);
                holder.textProtein = view.findViewById(R.id.textProtein);
                holder.buttonAddFood = view.findViewById(R.id.buttonAddFood);
                holder.cals = view.findViewById(R.id.cals);
                holder.fat = view.findViewById(R.id.fat);
                holder.carbs = view.findViewById(R.id.carbs);
                holder.protein = view.findViewById(R.id.protein);
                holder.foodImage = view.findViewById(R.id.foodImage);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }

            holder.foodName.setText(tempFood.getName());
            holder.textCalories.setText(tempFood.getCalories() + "");
            holder.textFat.setText(tempFood.getFat() + "");
            holder.textCarbs.setText(tempFood.getCarbs() + "");
            holder.textProtein.setText(tempFood.getProtein() + "");
            holder.buttonAddFood.setText("Click to add food");


            return null;
        }

        class ViewHolder {
            TextView foodName;
            TextView textCalories;
            TextView textFat;
            TextView textCarbs;
            TextView textProtein;
            Button buttonAddFood;
            TextView cals;
            TextView fat;
            TextView carbs;
            TextView protein;
            ImageView foodImage;

        }
    }
}
