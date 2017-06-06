package com.example.user.seatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class BusesActivity extends ActionBarActivity
{
    private boolean is_city_touched = false, is_company_touched = false;

    private ArrayList<String> numbers = new ArrayList<>();
    private ArrayAdapter<String> numbers_adapter;

    private ArrayList<String> stops = new ArrayList<>();
    private ArrayAdapter<String> stops_adapter;

    //will load by city + company lists;
    private void update_lines(Spinner city_spinner, Spinner company_spinner,Spinner number_spinner)
    {
        String company =company_spinner.getSelectedItem().toString();
        String city =city_spinner.getSelectedItem().toString();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        try
        {
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            MyClientTask myClientTask = new MyClientTask('N',"Bus",company, city, null, null);
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
            if (myClientTask.response_from_server.contains("N;"))
            {
                // N;len(list);list
                String[] temp = ((myClientTask.response_from_server.split(";"))[2]).split("\\|"); //turns the given lines into an array
                numbers = new ArrayList<String>(Arrays.asList(temp));
                numbers_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
                number_spinner.setAdapter(numbers_adapter);
                stops.clear();
                stops_adapter.notifyDataSetChanged();
                myClientTask.response_from_server = "-";
            }
            //failure or not connected
            else
            {
                if (myClientTask.response_from_server.equals("0"))
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get supported lines. Try again!", duration);
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


    //will load by city + company lists;
    private void update_stops(Spinner city_spinner, Spinner company_spinner,Spinner number_spinner,Spinner stops_spinner)
    {
        String company =company_spinner.getSelectedItem().toString();
        String city =city_spinner.getSelectedItem().toString();
        String number = number_spinner.getSelectedItem().toString();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        try
        {
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            MyClientTask myClientTask = new MyClientTask('S',"Bus",company, city, number, null);
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
            if (myClientTask.response_from_server.contains("S;"))
            {
                // S;len(stops);stops
                String[] temp = ((myClientTask.response_from_server.split(";"))[2]).split("\\|"); //turns the given lines into an array
                stops = new ArrayList<String>(Arrays.asList(temp));
                stops_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stops);
                stops_spinner.setAdapter(stops_adapter);
                myClientTask.response_from_server = "-";
            }
            //failure or not connected
            else
            {
                if (myClientTask.response_from_server.equals("0"))
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the stops of this bus. Try again!", duration);
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        final Spinner company_spinner = (Spinner) findViewById(R.id.CompanySpinner);
        final Spinner city_spinner = (Spinner) findViewById(R.id.CitySpinner);
        final Spinner number_spinner = (Spinner) findViewById(R.id.NumberSpinner);
        final Spinner stop_spinner = (Spinner) findViewById(R.id.StopSpinner);

        city_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_city_touched = true;
                return false;
            }
        });
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
                if (is_city_touched)
                    update_lines(city_spinner,company_spinner,number_spinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //update_lines(city_spinner,company_spinner,number_spinner);
            }});

        company_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_company_touched = true;
                return false;
            }
        });
        company_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
                if (is_company_touched && is_city_touched) {
                    update_lines(city_spinner, company_spinner, number_spinner);
                    is_city_touched = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //update_lines(city_spinner,company_spinner,number_spinner);
            }});

        number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
                update_stops(city_spinner,company_spinner,number_spinner,stop_spinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //update_lines(city_spinner,company_spinner,number_spinner);
            }});

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> city_adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> companies_adapter = ArrayAdapter.createFromResource(this, R.array.bus_companies_array, android.R.layout.simple_spinner_item);
        numbers_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
        stops_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stops);

        // Specify the layout to use when the list of choices appears - the spinner type
        city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companies_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numbers_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stops_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        city_spinner.setAdapter(city_adapter);
        company_spinner.setAdapter(companies_adapter);
        number_spinner.setAdapter(numbers_adapter);
        stop_spinner.setAdapter(stops_adapter);
    }

    public void startBusActivity(View view)
    {
        RadioGroup radioChoice = (RadioGroup) findViewById(R.id.radioChoice);
        RadioButton radioChoiceChecked= (RadioButton) findViewById(radioChoice.getCheckedRadioButtonId());
        String company = ((Spinner) findViewById(R.id.CompanySpinner)).getSelectedItem().toString();
        String city = ((Spinner) findViewById(R.id.CitySpinner)).getSelectedItem().toString();
        String number = ((Spinner) findViewById(R.id.NumberSpinner)).getSelectedItem().toString();
        String stop = String.valueOf(((Spinner) findViewById(R.id.StopSpinner)).getSelectedItemPosition());
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
                myClientTask = new MyClientTask('v', "Bus", company, city, number, null);
            else //get seats
                myClientTask = new MyClientTask('g',"Bus",company, city, number, stop);


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
                intent.putExtra("company", company);
                intent.putExtra("city", city);
                intent.putExtra("number", number);
                intent.putExtra("time",time);
                intent.putExtra("data",data);

                myClientTask.response_from_server = "-";
                startActivity(intent);
            }
            else if(myClientTask.response_from_server.contains("g;"))
            {
                if (myClientTask.response_from_server.equals("g;0"))
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the bus info. Try again!", duration);
                    toast_unsuccessful.show();
                    return;
                }
                else
                {
                    Intent intent = new Intent(this, SeatsActivity.class);
                    intent.putExtra("message", myClientTask.response_from_server);
                    intent.putExtra("title","Bus - " + company + " - " + number);
                    Toast toast_successful = Toast.makeText(context, "Your bus's seats", duration);
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
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the bus info. Try again!", duration);
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
