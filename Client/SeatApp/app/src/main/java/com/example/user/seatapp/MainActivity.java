package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText Train_Current_place = (EditText) findViewById(R.id.Train_Current_place);
        final EditText Train_Destination_place = (EditText) findViewById(R.id.Train_Destination_place);
        final EditText Bus_Current_place = (EditText) findViewById(R.id.Bus_Current_place);
        final EditText Bus_Destination_place = (EditText) findViewById(R.id.Bus_Destination_place);
        final EditText Taxi_Current_place = (EditText) findViewById(R.id.Taxi_Current_place);
        final EditText Taxi_Destination_place = (EditText) findViewById(R.id.Taxi_Destination_place);

        final Button Train_schedule_button = (Button) findViewById(R.id.Train_schedule_button);
        final Button Bus_schedule_button = (Button) findViewById(R.id.Bus_schedule_button);
        final Button Taxi_schedule_button = (Button) findViewById(R.id.Taxi_schedule_button);

        final RadioButton Train_Radio_Button = (RadioButton) findViewById(R.id.train_radio_button);
        final RadioButton Bus_Radio_Button = (RadioButton) findViewById(R.id.bus_radio_button);
        final RadioButton Taxi_Radio_Button = (RadioButton) findViewById(R.id.Taxi_radio_button);

        Train_Radio_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked) //when the user chooses the train button, show the train's current place and destination place, and hide the other inputs
                {
                    Train_Current_place.setVisibility(buttonView.VISIBLE);
                    Train_Destination_place.setVisibility(buttonView.VISIBLE);
                    Train_schedule_button.setVisibility(buttonView.VISIBLE);
                    Bus_Radio_Button.setChecked(false);
                    Taxi_Radio_Button.setChecked(false);
                }

                else
                {
                    Train_Current_place.setVisibility(buttonView.INVISIBLE);
                    Train_Current_place.setText(""); //Erases the current place which the user entered before
                    Train_Destination_place.setVisibility(buttonView.INVISIBLE);
                    Train_Destination_place.setText(""); //Erases the destination place which the user entered before
                    Train_schedule_button.setVisibility(buttonView.INVISIBLE);
                }
            }
        });

        Bus_Radio_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) //when the user chooses the bus button, show the bus's current place and destination place, and hide the other inputs
                {
                    Bus_Current_place.setVisibility(buttonView.VISIBLE);
                    Bus_Destination_place.setVisibility(buttonView.VISIBLE);
                    Bus_schedule_button.setVisibility(buttonView.VISIBLE);
                    Train_Radio_Button.setChecked(false);
                    Taxi_Radio_Button.setChecked(false);
                }

                else
                {
                    Bus_Current_place.setVisibility(buttonView.INVISIBLE);
                    Bus_Current_place.setText(""); //Erases the current place which the user entered before
                    Bus_Destination_place.setVisibility(buttonView.INVISIBLE);
                    Bus_Destination_place.setText(""); //Erases the destination place which the user entered before
                    Bus_schedule_button.setVisibility(buttonView.INVISIBLE);
                }

            }
        });

        Taxi_Radio_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) //when the user chooses the bus button, show the bus's current place and destination place, and hide the other inputs
                {
                    Taxi_Current_place.setVisibility(buttonView.VISIBLE);
                    Taxi_Destination_place.setVisibility(buttonView.VISIBLE);
                    Taxi_schedule_button.setVisibility(buttonView.VISIBLE);
                    Train_Radio_Button.setChecked(false);
                    Bus_Radio_Button.setChecked(false);
                }

                else
                {
                    Taxi_Current_place.setVisibility(buttonView.INVISIBLE);
                    Taxi_Current_place.setText(""); //Erases the current place which the user entered before
                    Taxi_Destination_place.setVisibility(buttonView.INVISIBLE);
                    Taxi_Destination_place.setText(""); //Erases the destination place which the user entered before
                    Taxi_schedule_button.setVisibility(buttonView.INVISIBLE);
                }

            }
        });

    }

    public void startBusSchedule(View view) // when the button is clicked, open this activity
    {
        final EditText Bus_Current_place = (EditText) findViewById(R.id.Bus_Current_place);
        final EditText Bus_Destination_place = (EditText) findViewById(R.id.Bus_Destination_place);

        Intent intent = new Intent(this, BusScheduleActivity.class);
        intent.putExtra("Current", Bus_Current_place.getText().toString());
        intent.putExtra("Destination", Bus_Destination_place.getText().toString());

        startActivity(intent);
    }

    public void startTrainSchedule(View view) // when the button is clicked, open this activity
    {
        final EditText Train_Current_place = (EditText) findViewById(R.id.Train_Current_place);
        final EditText Train_Destination_place = (EditText) findViewById(R.id.Train_Destination_place);

        Intent intent = new Intent(this, TrainScheduleActivity.class);
        intent.putExtra("Current", Train_Current_place.getText().toString());
        intent.putExtra("Destination", Train_Destination_place.getText().toString());

        startActivity(intent);
    }

    public void startTaxiSchedule(View view) // when the button is clicked, open this activity
    {
        final EditText Taxi_Current_place = (EditText) findViewById(R.id.Taxi_Current_place);
        final EditText Taxi_Destination_place = (EditText) findViewById(R.id.Taxi_Destination_place);

        Intent intent = new Intent(this, TaxiScheduleActivity.class);
        intent.putExtra("Current", Taxi_Current_place.getText().toString());
        intent.putExtra("Destination", Taxi_Destination_place.getText().toString());

        startActivity(intent);
    }


}
