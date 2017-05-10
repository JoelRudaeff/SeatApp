package com.example.user.seatapp;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity
{

    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("Username");

        final Button Train_schedule_button = (Button) findViewById(R.id.Train_button);
        final Button Bus_schedule_button = (Button) findViewById(R.id.Bus_button);
    }

    public void startBuses(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, BusesActivity.class);
        startActivity(intent);
    }

    public void startTrains(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, TrainsActivity.class);
        startActivity(intent);
    }


    @Override
    //when logging of the app, send the application exit message
    public void onBackPressed() {

        MyClientTask myClientTask = new MyClientTask('E',username,null,null);
        myClientTask.execute();
        int times = 0;

        while ( myClientTask.sent_to_server = false)
        {
            if (times < 5)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            times++;
        }
        finish();
    }


}
