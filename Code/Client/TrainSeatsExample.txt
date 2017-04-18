package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class TrainSeatsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_seats);

        Intent intent = getIntent();

        int i;
        int msg_length;
        String msg = "";

        if( !( intent.getExtras().getString("message").isEmpty() ))
        {
            msg = intent.getExtras().getString("message");

            if(msg.startsWith("g")) // if the message it's a "Get Seats" message from the server
            {
                msg_length = msg.length();

                analyze_message_to_seats_statusses(msg, msg_length);
            }
        }

    }


    public int analyze_message_to_seats_statusses(String msg, int msg_length)
    {
        int i;
        int ret = 0;
        int line_number;
        int seats_data_length = Character.getNumericValue(msg.charAt(1)); // getting the 2nd element of the string and converting it to an int type

        String line = "";

        ArrayList<String> msg_list = new ArrayList<String>(Arrays.asList(msg.split(";")));
        ArrayList<String> seats_list = new ArrayList<String>(Arrays.asList(msg_list.get(2).split("\\|"))); //The split should be written like that (not "|" but "\\|")

        ArrayList<TextView> lines_list = new ArrayList<TextView>();

        TextView Cl1 = (TextView) findViewById(R.id.cl1); //Chair line 1
        TextView Cl2 = (TextView) findViewById(R.id.cl2); //Chair line 2
        TextView Cl3 = (TextView) findViewById(R.id.cl3); //Chair line 3
        TextView Cl4 = (TextView) findViewById(R.id.cl4); //Chair line 4
        TextView Cl5 = (TextView) findViewById(R.id.cl5); //Chair line 5
        TextView Cl6 = (TextView) findViewById(R.id.cl6); //Chair line 6
        TextView Cl7 = (TextView) findViewById(R.id.cl7); //Chair line 7
        TextView Cl8 = (TextView) findViewById(R.id.cl8); //Chair line 8
        TextView Cl9 = (TextView) findViewById(R.id.cl9); //Chair line 9
        TextView Cl10 = (TextView) findViewById(R.id.cl10); //Chair line 10

        lines_list.add(Cl1);
        lines_list.add(Cl2);
        lines_list.add(Cl3);
        lines_list.add(Cl4);
        lines_list.add(Cl5);
        lines_list.add(Cl6);
        lines_list.add(Cl8);
        lines_list.add(Cl9);
        lines_list.add(Cl10);

        for(i=0; i<9; i++)
        {
            line_number = Character.getNumericValue(seats_list.get(i).charAt(0)); //getting the first char of each line's seats, and convert it to an int
            line = seats_list.get(i).substring(2); //insert the line's seats into a parameter. Also start from the seats' statusses

            set_seats_statusses(lines_list.get(i), line.length(), line);

        }

        return ret;
    }

    public void set_seats_statusses(TextView line, int line_length, String seats)
    {
        line.setText(seats);
    }
}
