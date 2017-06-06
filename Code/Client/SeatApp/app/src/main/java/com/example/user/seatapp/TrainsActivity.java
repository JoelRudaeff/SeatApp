package com.example.user.seatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class TrainsActivity extends ActionBarActivity
{

    private boolean is_north = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trains);

        Spinner current_spinner = (Spinner) findViewById(R.id.SourceSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.train_stations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears - the spinner type
        places_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        current_spinner.setAdapter(places_adapter);


        Switch temp = (Switch) findViewById(R.id.NorthSwitch);
        temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_north = isChecked;
            }
        });
    }

    //TODO:
    public void startTrainActivity(View view)
    {
        String current = String.valueOf(((Spinner) findViewById(R.id.SourceSpinner)).getSelectedItemPosition());
        String to = "";
        String from = "";

        if (is_north==true) {
            to = "North";
            from = "South";
        }
        else {
            to = "South";
            from = "North";
        }
        RadioGroup radioChoice = (RadioGroup) findViewById(R.id.radioChoice);
        RadioButton radioChoiceChecked= (RadioButton) findViewById(radioChoice.getCheckedRadioButtonId());

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;


        try
        {
            MyClientTask myClientTask;
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            //view vehicle
            if (radioChoiceChecked.getText().toString().equals("View information about the vehicle") )
                myClientTask = new MyClientTask('v', "Train", "none", from, "none", null);
            else //get seats
                myClientTask = new MyClientTask('g',"Train","none", from, "none", current);


            myClientTask.execute(); //will run like a thread

            int times = 0;
            //wait for the client to get response from the server, if it doesn't connect in a few seconds, terminate the waiting
            while (myClientTask.response_from_server.equals("-"))
            {
                if (times < 5)
                    Thread.sleep(1000);
                times++;
            }
            progress.hide();

            //success
            if (myClientTask.response_from_server.contains("v;"))
            {
                // v;len(total startTime_EndTime);0800_0830|0835_0905;len(data);Rabin_0|Big_25
                String time = myClientTask.response_from_server.split(";")[2];
                String data = myClientTask.response_from_server.split(";")[4];
                myClientTask.response_from_server = "-";

                Intent intent = new Intent(this, ScheduleActivity.class);
                intent.putExtra("city", to);
                intent.putExtra("time",time);
                intent.putExtra("data",data);

                myClientTask.response_from_server = "-";
                startActivity(intent);
            }
            else if(myClientTask.response_from_server.contains("g;"))
            {
                if (myClientTask.response_from_server.equals("g;0"))
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the train info. Try again!", duration);
                    toast_unsuccessful.show();
                }
                else
                {
                    Intent intent = new Intent(this, SeatsActivity.class);
                    intent.putExtra("message", myClientTask.response_from_server);
                    intent.putExtra("title","Train - " + from + " -> " + to);
                    intent.putExtra("direction",to);
                    Toast toast_successful = Toast.makeText(context, "Your train's seats", duration);
                    toast_successful.show();

                    myClientTask.response_from_server = "-";
                    startActivity(intent);
                }

            }
            //failure or not connected
            else
            {
                if (myClientTask.response_from_server.equals("0"))
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the train info. Try again!", duration);
                    toast_unsuccessful.show();
                }

                else
                {
                    Toast toast_unsuccessful_connection = Toast.makeText(context, "Couldn't connect to server, try again!", duration);
                    toast_unsuccessful_connection.show();
                }

            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
