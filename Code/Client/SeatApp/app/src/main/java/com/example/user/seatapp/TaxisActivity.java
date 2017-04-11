package com.example.user.seatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TaxisActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxis);

        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears - the spinner type
        places_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        current_spinner.setAdapter(places_adapter);
        destination_spinner.setAdapter(places_adapter);

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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object item = parent.getItemAtPosition(pos);
                TextView dest = (TextView) findViewById(R.id.dest);
                dest.setText(item.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
