package com.example.sooraj.getfit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.sooraj.getfit.Model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ProgressFragment extends Fragment {

    /**
     * Fields
     */
    private static int chartType; //0 = steps, 1 = calories, 2 = weight
    private View view;
    private BarChart barChart;
    private LineChart lineChart;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private User user;
    private String username;
    private HashMap<String, Integer> stepData;
    private HashMap<String, Integer> calorieData;
    private HashMap<String, Integer> weightData;
    private android.support.v7.widget.Toolbar toolbar;
    private int timeFrame;
    private int timeFramePosition; //Current index of the timeFrame spinner in dialog

    /**
     * Changes the text of the toolbar based on which chart is being displayed
     * @param toolbar the toolbar to change the text of
     */
    public static void setToolbarText(Toolbar toolbar) {
        switch (chartType) {
            case 0:
                toolbar.setTitle("Steps History");
                break;
            case 1:
                toolbar.setTitle("Eating History");
                break;
            case 2:
                toolbar.setTitle("Weight History");
                break;
            default:
                break;
        }
    }

    /**
     * Get reference to Firebase Database, and the "Users" node
     * Get reference to current user from the current activity
     * Retrieve data and initialize fields using the data
     * Get reference to all components of the fragment's view
     * Draw the default chant (A steps chart showing one week of history)
     * @param inflater the LayoutInflater used by the MainActivity
     * @param container ViewGroup that this fragment is a part of
     * @param saveInstanceState the last saved state of the application
     * @return the view corresponding to this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        stepData = user.getStepsStorage();
        calorieData = user.getCalorieStorage();
        weightData = user.getWeightStorage();

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_progress, container, false);
        barChart = view.findViewById(R.id.barChart);
        lineChart = view.findViewById(R.id.lineChart);
        toolbar = getActivity().findViewById(R.id.toolbar);

        timeFrame = 7;
        timeFramePosition = 0;
        chartType = 0;
        configureCharts();
        draw();
        return view;
    }

    /**
     * Inflates options menu
     * Animates charts
     * @param menu Menu used by the current activity
     * @param inflater MenuInflater used by the current activity
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_progress, menu);
        barChart.animateXY(2000, 2000);
        lineChart.animateXY(2000, 2000);
    }

    /**
     * Inflates dialog to either change time frame or which data the chart displays depending on the selected item
     * @param item the item selected by the user
     * @return true because there is no need for system processing, all processing necessary processing is done in the method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Dialog to change which data is being displayed(steps, calories, or weight)
        if (item.getItemId() == R.id.action_change_chart) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Data Type");

            View viewInflated = LayoutInflater.from(getContext())
                        .inflate(R.layout.select_chart_type,
                            (ViewGroup) view,
                            false);

            final Spinner input = viewInflated.findViewById(R.id.chartType);
            builder.setView(viewInflated);
            input.setSelection(chartType);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    chartType = input.getSelectedItemPosition();
                    draw();
                    setToolbarText(toolbar);
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

        //Dialog to change time frame
        else if (item.getItemId() == R.id.action_change_time) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Time Frame");

            View viewInflated = LayoutInflater.from(getContext())
                        .inflate(R.layout.change_time_frame,
                            (ViewGroup) view,
                            false);

            final Spinner input = viewInflated.findViewById(R.id.timeFrame);
            builder.setView(viewInflated);
            input.setSelection(timeFramePosition);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    timeFramePosition = input.getSelectedItemPosition();

                    switch (timeFramePosition) {

                        //1 week
                        case 0:
                            timeFrame = 7;
                            break;

                        //2 weeks
                        case 1:
                            timeFrame = 14;
                            break;

                        //1 Month
                        case 2:
                            timeFrame = 30;
                            break;

                        //3 months
                        case 3:
                            timeFrame = 90;
                            break;

                        //6 months
                        case 4:
                            timeFrame = 180;
                            break;

                        //1 year
                        case 5:
                            timeFrame = 365;
                            break;

                        //Full History (All of the data available)
                        case 6:
                            timeFrame = stepData.keySet().size();
                            break;

                        default:
                            break;
                    }

                    draw();
                    setToolbarText(toolbar);
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
        return true;
    }

    /**
     * Calls method to draw chart based on which chart type is currently selected
     */
    public void draw() {

        switch (chartType) {
            case 0:
                setStepsChart();
                break;

            case 1:
                setCaloriesChart();
                break;

            case 2:
                setWeightChart();
                break;

            default:
                break;
        }
    }

    /**
     * Configures charts to make them more visually appealing
     * Deletes background grids, axis labels, description, and legend
     */
    public void configureCharts() {

        barChart.setDescription("");
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getLegend().setEnabled(false);

        lineChart.setDescription("");
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);
    }

    /**
     * Draws and animates a bar chart to display steps history
     */
    public void setStepsChart() {

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
        barChart.setDescription("");
        int colors[] = new int[timeFrame];

        //Adds entries to bar chart for everyday in time frame starting from one day before the current day
        for (int i = timeFrame; i > 0; i--) {

            int position = timeFrame - i; //Index of entry in bar chart

            long subtract = (long) i * 24 * 60 * 60 * 1000; //Amount of time to subtract from current time to get to the correct day

            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            String dateString = sdf.format(date); //Format date in same format that is stored in Firebase as the keys to number of steps

            int steps = 0; //Sets steps to a default value of zero if no data is found in Firebase for the day being searched for

            if (stepData.containsKey(dateString))  { //Checks if data is present to avoid null pointer exceptions

                steps = stepData.get(dateString);
            }

            barEntries.add(new BarEntry(steps, position));

            //Sets color of bar based on how much of step goal was met
            if (steps < user.getStepGoal() * .5) {

                colors[position] = Color.parseColor("#ff3838");
            }

            else if (steps < user.getStepGoal()) {

                colors[position] = Color.parseColor("#ff9926");
            }

            else {

                colors[position] = Color.parseColor("#3fc138");
            }
        }

        /*
         * Creates data set for chart with the previously created entries
         * Assigns color array to data set to set bar colors
         */
        BarDataSet dataSet = new BarDataSet(barEntries, "Steps");
        dataSet.setValueFormatter(new MyValueFormatter()); //Formats values so no decimal places are displayed
        dataSet.setColors(colors);

        //Fills ArrayList with dates in time frame
        for (int j = timeFrame; j > 0; j--) {

            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
        }

        /*
         * Creates the BarData which will be displayed to the user
         * BarData consists of Date, BarEntry pairs
         * Sets BarData to BarChart and animates chart
         * Prevents users from highlighting bars, and decreases value text size
         */
        BarData barData = new BarData(dates, dataSet);
        barData.setHighlightEnabled(false);
        barChart.setData(barData);
        barChart.animateXY(2000, 2000);
        barChart.getBarData().setValueTextSize(10f);

    }

    /**
     * Draws and animates a bar chart to display calorie intake history
     */
    public void setCaloriesChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
        int colors[] = new int[timeFrame];

        //Adds entries to bar chart for everyday in time frame starting from one day before the current day
        for (int i = timeFrame; i > 0; i--) {

            int position = timeFrame - i; //Index of entry in bar chart

            long subtract = (long) i * 24 * 60 * 60 * 1000;  //Amount of time to subtract from current time to get to the correct day

            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            String dateString = sdf.format(date); //Format date in same format that is stored in Firebase as the keys to number of calories

            int calories = 0;  //Sets calories to a default value of zero if no data is found in Firebase for the day being searched for

            if (calorieData.containsKey(dateString)) { //Checks if data is present to avoid null pointer exceptions
                calories = calorieData.get(dateString);

            }

            barEntries.add(new BarEntry(calories, position));

            /*
             * Sets color of bar based on how much of their calorie goal the user consumed
             * Too much is also bad in this case
             */
            if (calories < user.getCalorieGoal() * .8 || calories > user.getCalorieGoal() * 1.2) {

                colors[position] = Color.parseColor("#ff3838");
            }

            else if (calories < user.getCalorieGoal() * .95 || calories > user.getCalorieGoal() * 1.05) {

                colors[position] = Color.parseColor("#ff9926");
            }

            else {

                colors[position] = Color.parseColor("#3fc138");
            }
        }

        /*
         * Creates data set for chart with the previously created entries
         * Assigns color array to data set to set bar colors
         */
        BarDataSet dataSet = new BarDataSet(barEntries, "Calories");
        dataSet.setValueFormatter(new MyValueFormatter()); //Formats values so no decimal places are displayed
        dataSet.setColors(colors);

        //Fills ArrayList with dates in time frame
        for (int j = timeFrame; j > 0; j--) {

            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
        }

        /*
         * Creates the BarData which will be displayed to the user
         * BarData consists of Date, BarEntry pairs
         * Sets BarData to BarChart and animates chart
         * Prevents users from highlighting bars, and decreases value text size
         */
        BarData barData = new BarData(dates, dataSet);
        barData.setHighlightEnabled(false);
        barChart.setData(barData);
        barChart.animateXY(2000, 2000);
        barChart.getBarData().setValueTextSize(10f);

    }

    /**
     * Draws and animates a line chart to display weight history
     */
    public void setWeightChart() {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        lineChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.INVISIBLE);

        //Adds entries to line chart for everyday in time frame starting from one day before the current day
        for (int i = timeFrame; i > 0; i--) {
            int position = timeFrame - i; //Index of entry in line chart

            long subtract = (long) i * 24 * 60 * 60 * 1000; //Amount of time to subtract from current time to get to the correct day

            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            String dateString = sdf.format(date); //Format date in same format that is stored in Firebase as the keys to user's weight

            int weight = 0; //Sets weight to a default value of zero if no data is found in Firebase for the day being searched for

            if (weightData.containsKey(dateString)) { //Checks if data is present to avoid null pointer exceptions
                weight = weightData.get(dateString);
            }

            entries.add(new Entry(weight, position));
        }

        /*
         * Creates data set for chart with the previously created entries
         * Sets color of line and points on chart
         */
        LineDataSet dataSet = new LineDataSet(entries, "Weight");
        dataSet.setValueFormatter(new MyValueFormatter());
        dataSet.setCircleColor(Color.parseColor("#ff3838"));
        dataSet.setColor(Color.parseColor("#ff3838"));

        //Fills ArrayList with dates in time frame
        for (int j = timeFrame; j > 0; j--) {

            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
            System.out.println(date);
        }

        /*
         * Creates the BarData which will be displayed to the user
         * BarData consists of Date, BarEntry pairs
         * Sets BarData to BarChart and animates chart
         * Prevents users from highlighting points, and decreases value text size
         */
        LineData lineData = new LineData(dates, dataSet);
        lineData.setHighlightEnabled(false);
        lineChart.setData(lineData);
        lineChart.animateXY(2000, 2000);
        lineChart.getLineData().setValueTextSize(10f);

    }

    /**
     * Custom value formatter for use by bar and line charts
     */
    class MyValueFormatter implements ValueFormatter {

        private DecimalFormat df;

        /**
         * Constructor
         * Initializes DecimalFormat to show only whole numbers
         */
        public MyValueFormatter() {
            df = new DecimalFormat("#");
        }

        /**
         * Formats value of chart entries
         * @param value value that is to be formatted
         * @param entry the entry the value is from
         * @param dataSetIndex the index of the entry on the chart
         * @param viewPortHandler ViewPortHandler responsible for handling the current viewing state of the chart
         * @return formatted value, value without decimal places, if value is zero, empty string
         */
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value == 0f) {
                return "";
            }
            return df.format(value);
        }
    }
}