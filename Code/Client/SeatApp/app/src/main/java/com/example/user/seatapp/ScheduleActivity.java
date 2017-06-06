package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems=new ArrayList<String>();

    private void update_start_end_time(String start_end)
    {
        String[] start_end_arr = start_end.split("\\|"); // start_end|start1_end
        for (int i =0 ; i< start_end_arr.length ; i++)
            listItems.add(start_end_arr[i].replace("_"," "));

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



        ListView time_list = (ListView) findViewById(R.id.timelist);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        time_list.setAdapter(adapter);


    }
/*

    public String get_next_leaving_time (String leaving_time, int add_to_lt)
    {
        String NextLeavingTime = "";
        String[] LeavingTime = leaving_time.split(":");
        String LeavingTime_hours = LeavingTime[0];
        String LeavingTime_minutes = LeavingTime[1];

        int lt_hours = Integer.parseInt(LeavingTime_hours);
        int lt_minutes = Integer.parseInt(LeavingTime_minutes);
        int times;

        if(lt_minutes + add_to_lt >= 60)
        {
            lt_hours += (lt_minutes + add_to_lt) / 60; // add division, in an int number, to the hour number
            times = (lt_minutes+add_to_lt) / 60;
            lt_minutes = (lt_minutes + add_to_lt) - 60*times;
        }

        else
        {
            lt_minutes += add_to_lt;
        }

        if(lt_hours >= 24)
        {
            lt_hours -= 24; //if the hour is after midnight, turn it to 0:00 and so on..
        }

        LeavingTime_hours = String.valueOf(lt_hours);

        if(lt_minutes < 10)
        {
            LeavingTime_minutes = "0" + String.valueOf(lt_minutes);
        }

        else
        {
            LeavingTime_minutes = String.valueOf(lt_minutes);
        }

        NextLeavingTime = LeavingTime_hours + ":" + LeavingTime_minutes;
        return NextLeavingTime;
    }

    public String getArrivingTime(String current_place, String destination_place, String leaving_time, LinkedList<Integer> times_list, LinkedList<String> places_list)
    {
        String arriving_time = "";
        int i;
        int cur_pos = 0;
        int dest_pos = 0;
        int time_difference = 0;

        //getting the position of the 2 places, to calculate the time differnce between them
        for(i=0; i<places_list.size(); i++)
        {
            if(current_place.equals(places_list.get(i)))
            {
                cur_pos = i;
            }

            if(destination_place.equals(places_list.get(i)))
            {
                dest_pos = i;
            }
        }

        if(cur_pos < dest_pos) //if the current place is found before the destination place in the places' linked list
        {
            time_difference = times_list.get(dest_pos) - times_list.get(cur_pos);
        }

        else if (dest_pos < cur_pos) //if the current place is found after the destination place in the places' linked list
        {
            time_difference = times_list.get(cur_pos) - times_list.get(dest_pos);
        }

        arriving_time = get_next_leaving_time(leaving_time, time_difference); // This function return the time after X minutes, like we need

        return arriving_time;
    }

    public void startBus2(View view)
    {
        String ret = "1"; //TODO: cahnge it to "0"
        Button button_bus_number = (Button) findViewById(R.id.bn2);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus3(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn3);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus4(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn4);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus5(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn5);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus6(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn6);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus7(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn7);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }

    public void startBus8(View view)
    {
        Button button_bus_number = (Button) findViewById(R.id.bn8);
        String BusNumber = button_bus_number.getText().toString();

        SendingDetailsToServer(BusNumber);
    }
*/
}
