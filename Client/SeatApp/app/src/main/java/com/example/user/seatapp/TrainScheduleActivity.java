package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TrainScheduleActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule);

        Intent intent = getIntent();
        TextView cur = (TextView) findViewById(R.id.cur);
        TextView dest = (TextView) findViewById(R.id.dest);
        TextView leaving_time = (TextView) findViewById(R.id.leaving_time);

        int add_to_lt = 0; //add this time to the leaving time - the time difference between 2 trains

        String NextLeavingTime;
        String ArrivingTime;
        String TrainNumber;

        if( !( intent.getExtras().getString("current").isEmpty() )) //If the user entered her/his current place
        {
            cur.setText(intent.getExtras().getString("current"));
        }

        if( !( intent.getExtras().getString("destination").isEmpty() )) //If the user entered her/his current place
        {
            dest.setText(intent.getExtras().getString("destination"));
        }

        if( !( intent.getExtras().getString("leaving time").isEmpty() )) //If the user entered her/his current place
        {
            leaving_time.setText(intent.getExtras().getString("leaving time"));
        }


        TextView table_leaving_time_1 = (TextView) findViewById(R.id.lt1);
        table_leaving_time_1.setText(leaving_time.getText().toString());

        TextView table_leaving_time_2 = (TextView) findViewById(R.id.lt2);
        add_to_lt += 22;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        table_leaving_time_2.setText(NextLeavingTime);

        TextView table_leaving_time_3 = (TextView) findViewById(R.id.lt3);
        add_to_lt += 27;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        table_leaving_time_3.setText(NextLeavingTime);

        TextView table_leaving_time_4 = (TextView) findViewById(R.id.lt4);
        add_to_lt += 20;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        table_leaving_time_4.setText(NextLeavingTime);

    }

    public String get_next_leaving_time (TextView leaving_time, int add_to_lt)
    {
        String NextLeavingTime = "";
        String[] LeavingTime = leaving_time.getText().toString().split(":");
        String LeavingTime_hours = LeavingTime[0];
        String LeavingTime_minutes = LeavingTime[1];

        int lt_hours = Integer.parseInt(LeavingTime_hours);
        int lt_minutes = Integer.parseInt(LeavingTime_minutes);

        if(lt_minutes + add_to_lt > 60)
        {
            lt_hours += (lt_minutes + add_to_lt) / 60; // add division, in an int number, to the hour number
            lt_minutes = (lt_minutes + add_to_lt) - 60;
        }

        else
        {
            lt_minutes += add_to_lt;
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
}
