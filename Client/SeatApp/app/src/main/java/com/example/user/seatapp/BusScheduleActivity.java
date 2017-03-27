package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class BusScheduleActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_schedule);

        Intent intent = getIntent();
        TextView cur = (TextView) findViewById(R.id.b_cur);
        TextView dest = (TextView) findViewById(R.id.b_dest);
        LinkedList<String> places_linkedlist = new LinkedList<String>(); // a linked list of the place
        LinkedList<Integer> times_linkedlist = new LinkedList<Integer>(); // a linked list of the time to go from a place after the place before
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);

        int i;
        int add_to_lt = 0; //add this time to the leaving time - the time difference between 2 trains

        String current_place = "";
        String destination_place = "";
        String company = "";
        String leaving_time = "";
        String NextLeavingTime;
        String arriving_time;
        String TrainNumber;

        if( !( intent.getExtras().getString("current").isEmpty() )) //If the user entered her/his current place
        {
            cur.setText(intent.getExtras().getString("current"));
            current_place = intent.getExtras().getString("current");
        }

        if( !( intent.getExtras().getString("destination").isEmpty() )) //If the user entered her/his current place
        {
            dest.setText(intent.getExtras().getString("destination"));
            destination_place = intent.getExtras().getString("destination");
        }

        if( !( intent.getExtras().getString("company").isEmpty() )) //If the user entered her/his current place
        {
            company = intent.getExtras().getString("company");
        }

        if( !( intent.getExtras().getString("leaving time").isEmpty() )) //If the user entered her/his current place
        {
            leaving_time = intent.getExtras().getString("leaving time");
        }


    }
}
