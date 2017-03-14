package com.example.user.seatapp;

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

        TextView st = (TextView) findViewById(R.id.cur);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.places_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        current_spinner.setAdapter(adapter);
        destination_spinner.setAdapter(adapter);

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

        //String current_place = current_spinner.getSelectedItem().toString();
        //String destination_place = destination_spinner.getSelectedItem().toString();
    }
}
