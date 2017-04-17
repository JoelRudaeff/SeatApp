package com.example.user.seatapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends ActionBarActivity
{

    String username;
    String host = "192.168.1.42";
    boolean sent_data_flag = false;

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String NewUsername;
        String dstAddress = host;
        int dstPort = 8888;


        public MyClientTask(String NU)
        {
            NewUsername = NU;
        }
        //Execute()
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String username_length = String.valueOf(NewUsername.length());

                // The data that the client sends to the server when he signs-up
                String string_to_send = ";E;" + username_length + ";" + NewUsername;

                Socket socket = new Socket(dstAddress, dstPort);


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(string_to_send); //The msg to the server\
                sent_data_flag = true;
                output.flush(); // Send off the data
                output.close();
                socket.close();
            }
            catch ( Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("Username");

        final Button Train_schedule_button = (Button) findViewById(R.id.Train_button);
        final Button Bus_schedule_button = (Button) findViewById(R.id.Bus_button);
        final Button Taxi_schedule_button = (Button) findViewById(R.id.Taxi_button);
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

    public void startTaxis(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, TaxisActivity.class);
        startActivity(intent);
    }


    @Override
    //when logging of the app, send the application exit message
    public void onBackPressed() {

        MyClientTask myClientTask = new MyClientTask(username);
        myClientTask.execute();
        int times = 0;

        while ( sent_data_flag = false)
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
