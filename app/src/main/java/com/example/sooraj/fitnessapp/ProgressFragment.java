package com.example.sooraj.fitnessapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.sooraj.fitnessapp.Model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ProgressFragment extends Fragment {

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
    private int chartType;

    @Override
    public void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        stepData = user.getStepsStorage();
        calorieData = user.getCalorieStorage();
        weightData = user.getWeightStorage();
        timeFrame = 7;
        chartType = 0;
        draw();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_progress, container, false);
        barChart = view.findViewById(R.id.barChart);
        lineChart = view.findViewById(R.id.lineChart);
        toolbar = getActivity().findViewById(R.id.toolbar);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_progress, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_chart) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Data Type");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.select_chart_type, (ViewGroup) view, false);
            final Spinner input = viewInflated.findViewById(R.id.chartType);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    chartType = input.getSelectedItemPosition();
                    draw();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else if (item.getItemId() == R.id.action_change_time) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Time Frame");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.change_time_frame, (ViewGroup) view, false);
            final Spinner input = viewInflated.findViewById(R.id.timeFrame);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switch (input.getSelectedItemPosition()) {
                        case 0:
                            timeFrame = 7;
                            break;
                        case 1:
                            timeFrame = 14;
                            break;
                        case 2:
                            timeFrame = 30;
                            break;
                        case 3:
                            timeFrame = 90;
                            break;
                        case 4:
                            timeFrame = 180;
                            break;
                        case 5:
                            timeFrame = 365;
                            break;
                        case 6:
                            timeFrame = stepData.keySet().size();
                            break;
                        default:
                            break;
                    }
                    draw();
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

    public void setStepsChart() {

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
        barChart.setBackgroundColor(Color.parseColor("#ededed"));
        barChart.setDescription("");
        barChart.setGridBackgroundColor(Color.parseColor("#FFFFFF"));
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setDrawLabels(false);
        toolbar.setTitle("Steps History");

        for (int i = timeFrame; i > 0; i--) {
            int position = timeFrame - i;
            long subtract = (long) i * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(date);

            if (stepData.containsKey(dateString)) {
                int steps = stepData.get(dateString);
                barEntries.add(new BarEntry(steps, position));
            } else {
                barEntries.add(new BarEntry(0, position));
            }
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Steps");


        for (int j = timeFrame; j > 0; j--) {
            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
        }

        BarData barData = new BarData(dates, dataSet);
        barChart.setData(barData);
        barChart.getBarData().setValueTextSize(10f);

    }

    public void setCaloriesChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
        barChart.setBackgroundColor(Color.parseColor("#ededed"));
        barChart.setDescription("");
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setDrawLabels(false);
        toolbar.setTitle("Eating History");

        for (int i = timeFrame; i > 0; i--) {
            int position = timeFrame - i;
            long subtract = (long) i * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(date);

            if (calorieData.containsKey(dateString)) {
                int steps = calorieData.get(dateString);
                barEntries.add(new BarEntry(steps, position));
            } else {
                barEntries.add(new BarEntry(0, position));
            }
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Calories");


        for (int j = timeFrame; j > 0; j--) {
            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
        }

        BarData barData = new BarData(dates, dataSet);
        barChart.setData(barData);
        barChart.getBarData().setValueTextSize(10f);
    }

    public void setWeightChart() {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        lineChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.INVISIBLE);
        lineChart.setBackgroundColor(Color.parseColor("#ededed"));
        lineChart.setDescription("");
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawLabels(false);

        for (int i = timeFrame; i > 0; i--) {
            int position = timeFrame - i;
            long subtract = (long) i * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(date);

            if (calorieData.containsKey(dateString)) {
                int steps = calorieData.get(dateString);
                entries.add(new Entry(steps, position));
            } else {
                entries.add(new Entry(0, position));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Calories");


        for (int j = timeFrame; j > 0; j--) {
            long subtract = (long) j * 24 * 60 * 60 * 1000;
            Date date = new Date(System.currentTimeMillis() - subtract);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String dateString = sdf.format(date);
            dates.add(dateString);
            System.out.println(date);
        }

        LineData lineData = new LineData(dates, dataSet);
        lineChart.setData(lineData);
        lineChart.getLineData().setValueTextSize(10f);


        toolbar.setTitle("Weight History");
    }
}
