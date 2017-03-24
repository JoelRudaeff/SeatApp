package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TrainsActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trains);

        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner leaving_time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.places_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> leaving_time_adapter = ArrayAdapter.createFromResource(this, R.array.trains_time, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears - the spinner type
        places_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaving_time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        current_spinner.setAdapter(places_adapter);
        destination_spinner.setAdapter(places_adapter);
        leaving_time_spinner.setAdapter(leaving_time_adapter);

        current_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object item = parent.getItemAtPosition(pos);
                TextView cur = (TextView) findViewById(R.id.cur);
                cur.setText(item.toString());
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        destination_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object item = parent.getItemAtPosition(pos);
                TextView dest = (TextView) findViewById(R.id.dest);
                dest.setText(item.toString());
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        leaving_time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object item = parent.getItemAtPosition(pos);
                TextView company = (TextView) findViewById(R.id.company);
                company.setText(item.toString());
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
            }

        });

    }

    public void startTrainSchedule(View view)
    {
        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner leaving_time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        Intent intent = new Intent(this, TrainScheduleActivity.class);
        intent.putExtra("current", current_spinner.getSelectedItem().toString());
        intent.putExtra("destination", destination_spinner.getSelectedItem().toString());
        intent.putExtra("leaving time", leaving_time_spinner.getSelectedItem().toString());

        startActivity(intent);
    }

}
