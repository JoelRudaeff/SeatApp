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

        CheckBox train_CheckBox = (CheckBox) findViewById(R.id.train_checkBox);
        CheckBox bus_CheckBox = (CheckBox) findViewById(R.id.bus_checkBox);

        final EditText Train_Current_place = (EditText) findViewById(R.id.Train_Current_place);
        final EditText Train_Destination_place = (EditText) findViewById(R.id.Train_Destination_place);

        train_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

        @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked)
                {
                    Train_Current_place.setVisibility(buttonView.VISIBLE);
                    Train_Destination_place.setVisibility(buttonView.VISIBLE);
                }

                else
                {
                    Bus_Current_place.setVisibility(buttonView.INVISIBLE);
                    Bus_Destination_place.setVisibility(buttonView.INVISIBLE);
                }
            }
        });

        bus_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked)
                {
                    Current_place.setVisibility(buttonView.VISIBLE);
                    Destination_place.setVisibility(buttonView.VISIBLE);
                }

                else
                {
                    Current_place.setVisibility(buttonView.INVISIBLE);
                    Destination_place.setVisibility(buttonView.INVISIBLE);
                }
            }
        });

        Button schedule_button = (Button) findViewById(R.id.schedule);
        schedule_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.schedule_screen);
            }
        });
    }
}
