package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private ArrayAdapter<String> time_adapter;
    private ArrayList<String> time_list=new ArrayList<String>();

	private ArrayAdapter<String> station_adapter;
	private ArrayList<String> station_list = new ArrayList<String>();
	
	
    private void update_start_end_time(String start_end)
    {
        String[] start_end_arr = start_end.split("\\|"); // start_end|start1_end
        for (int i =0 ; i< start_end_arr.length ; i++)
        {
            time_list.add(start_end_arr[i].replace("_", "                                                        "));
        }
    }


		
	private void prepare_stations(String data)
	{
		String[] station_delay = data.split("\\|");
		for ( int i = 0 ; i<   station_delay.length ; i++)
		{
			station_list.add(station_delay[i]);
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();

        int i;
        int add_to_lt = 0; //add this time to the leaving time - the time difference between 2 trains

        String company = "";
        String city = "";
        String number=  "";
        String time = "";
        String data = "";

        if( !( intent.getExtras().getString("city").isEmpty() )) //If the user entered her/his current place
        {
            city = intent.getExtras().getString("city");
        }

        if( !( intent.getExtras().getString("time").isEmpty() )) //If the user entered her/his current place
        {
            time = intent.getExtras().getString("time");
            update_start_end_time(time);
        }
        if( !( intent.getExtras().getString("data").isEmpty() )) //If the user entered her/his current place
        {
            data = intent.getExtras().getString("data");
			prepare_stations(data);
        }

        //if the vehicle is train, used in order to prevent crash
        try
        {
            if( !( intent.getExtras().getString("company").isEmpty() )) //If the user entered her/his current place
            {
                company = intent.getExtras().getString("company");
            }
            if( !( intent.getExtras().getString("number").isEmpty() )) //If the user entered her/his current place
            {
                number = intent.getExtras().getString("number");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        ListView time_list_view = (ListView) findViewById(R.id.timelist);
        time_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,time_list);
        time_list_view.setAdapter(time_adapter);
		
		ListView station_list_view = (ListView) findViewById(R.id.stationlist);
		station_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,station_list);
        station_list_view.setAdapter(station_adapter);


    }
	
}
