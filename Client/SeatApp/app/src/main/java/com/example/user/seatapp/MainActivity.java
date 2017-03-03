package com.example.user.seatapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

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
        Button schedule_button = (Button) findViewById(R.id.schedule);

        CheckBox Train_CheckBox = (CheckBox) findViewById(R.id.train_checkBox);
        CheckBox Bus_CheckBox = (CheckBox) findViewById(R.id.bus_checkBox);

        Train_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

        @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked) //when the user chooses the train button, show the train's current place and destination place, and hide the other inputs
                {
                    Train_Current_place.setVisibility(buttonView.VISIBLE);
                    Train_Destination_place.setVisibility(buttonView.VISIBLE);
                }

                else
                {
                    Train_Current_place.setVisibility(buttonView.INVISIBLE);
                    Train_Destination_place.setVisibility(buttonView.INVISIBLE);
                }
            }
        });

        Bus_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked) //when the user chooses the bus button, show the bus's current place and destination place, and hide the other inputs
                {
                    Bus_Current_place.setVisibility(buttonView.VISIBLE);
                    Bus_Destination_place.setVisibility(buttonView.VISIBLE);
                }

                else
                {
                    Bus_Current_place.setVisibility(buttonView.INVISIBLE);
                    Bus_Destination_place.setVisibility(buttonView.INVISIBLE);
                }
            }
        });

        schedule_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                setContentView(R.layout.train_schedule_screen);
            }
        });
    }
}
