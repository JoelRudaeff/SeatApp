package com.example.user.seatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
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


    public void analyze_message_to_seats_statusses(String msg, int msg_length)
    {
        int i;
        int j;
        int n = 0;

        int line_number = 0; //The starting line number
        int seats_data_length = Character.getNumericValue(msg.charAt(1)); // getting the 2nd element of the string and converting it to an int type

        String line = "";

        ArrayList<String> msg_list = new ArrayList<String>(Arrays.asList(msg.split(";")));
        ArrayList<String> seats_list = new ArrayList<String>(Arrays.asList(msg_list.get(2).split("\\|"))); //The split should be written like that (not "|" but "\\|")

        ArrayList<TextView> lines_list = new ArrayList<TextView>();
        ArrayList<ImageView> image_list = new ArrayList<ImageView>();
        ImageView[] line_image_list = {null, null, null, null};

        create_image_array(image_list);



        for(i=0; i<image_list.size(); i+=4)
        {
            for(j=i;j<i+4;j++)
            {
                line_image_list[n] = image_list.get(j); // getting the images of each line
                n++;
            }

            n = 0;

            line = seats_list.get(line_number).substring(2); //insert the line's seats into a parameter. Also start from the seats' statusses

            set_seats_statusses(line_image_list, line_image_list.length, line);
            line_number++;
        }
    }

    public void set_seats_statusses(ImageView[] line, int line_length, String seats)
    {
        int i;
        char seat = '0';

        ColorFilter taken_seat = new LightingColorFilter(Color.RED, Color.RED); //the color of a taken seat
        ColorFilter empty_seat = new LightingColorFilter(Color.GREEN, Color.GREEN); //the color of a empty seat

        for (i=0; i<line_length; i++)
        {
            try
            {
                seat = seats.charAt(i);

                while (seat != '0' && seat!='1') //if somehow the substring didn't work as well
                {
                    seats = seats.replace("_", "");
                    seat = seats.charAt(i);
                }

                if (seat == '0')
                {
                    line[i].setColorFilter(taken_seat);
                }

                else if (seat == '1')
                {
                    line[i].setColorFilter(empty_seat);
                }
            }

            catch (Exception e)
            {
                line[i].setVisibility(View.INVISIBLE); //If there are less seats than the usual, delete their photo
            }
        }
    }

    public void create_image_array(ArrayList<ImageView> image_list)
    {
        image_list.add((ImageView) findViewById(R.id.cl1ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl1ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl1ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl1ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl2ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl2ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl2ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl2ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl3ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl3ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl3ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl3ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl4ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl4ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl4ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl4ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl5ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl5ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl5ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl5ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl6ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl6ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl6ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl6ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl7ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl7ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl7ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl7ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl8ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl8ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl8ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl8ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl9ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl9ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl9ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl9ImageView4));
        image_list.add((ImageView) findViewById(R.id.cl10ImageView1));
        image_list.add((ImageView) findViewById(R.id.cl10ImageView2));
        image_list.add((ImageView) findViewById(R.id.cl10ImageView3));
        image_list.add((ImageView) findViewById(R.id.cl10ImageView4));
    }

}
