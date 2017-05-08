package com.example.user.seatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.start;
import static android.R.attr.width;
import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class TrainSeatsActivity extends AppCompatActivity
{
    private int maximum_seats_per_line = 10;
    private int starting_y = 78;
    private int starting_x = 71;
    private int x_difference_between_seats = 61;
    private int y_difference_between_lnes = 44;
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

                parse_message(msg, msg_length);
            }
        }

    }



    public void parse_message(String msg, int msg_length)
    {
        int i;
        int j;

        //screen variables
        int screen_height = getWindowManager().getDefaultDisplay().getHeight();
        int screen_width = getWindowManager().getDefaultDisplay().getWidth();

        int diff_height = 100; //The height difference between each 2 line - starting value
        int diff_width = 61; //The width diferrence between each 2 seats in a line - starting value

        float current_x;
        float current_y = (float)((findViewById(R.id.SeatsTitle)).getY()+0.1*screen_height);
        final float inPixels= this.getResources().getDimension(R.dimen.dimen_entry_in_dp);

        int dpWidthInPx  = (int) (diff_width);
        int dpHeightInPx = (int) (diff_height);

        char seat = '0';
        String curr_line = "";


        TextView title = (TextView) findViewById(R.id.SeatsTitle);
        AbsoluteLayout absoluteLayout1 = (AbsoluteLayout) findViewById(R.id.absoluteLayout1);

        ArrayList<String> msg_list = new ArrayList<String>(Arrays.asList(msg.split(";")));
        ArrayList<String> seats_list = new ArrayList<String>(Arrays.asList(msg_list.get(2).split("\\|"))); //The split should be written like that (not "|" but "\\|")
        ArrayList<TextView> text_lines_list = new ArrayList<TextView>();

        //MAXIMUM OF 10 LINES PER PAGE
        if (seats_list.size() < 10)
            diff_height = (int)((screen_height) / seats_list.size()); // dynamic height to be divided by all of the lines

        else
            diff_height = (int) ((screen_height) / 10);



        for(i=0; i<seats_list.size(); i++)
        {
            try
            {
                curr_line = seats_list.get(i).substring(2).replaceAll("_", "");; //insert the line's seats into a parameter. Also start from the seats' statuses

                //Line number text
                TextView line_number = new TextView(this);
                line_number.setY(current_y);
                line_number.setX(44);
                line_number.setText(String.valueOf(i+1));
                line_number.setTextSize(22);
                absoluteLayout1.addView(line_number);
                line_number.setVisibility(View.VISIBLE);

                diff_width = (int)(screen_width*0.8 / curr_line.length()); // dynamic width to be divided by all of the line's seats
                current_x = 78+line_number.getX();

                for (j = 0; j < curr_line.length(); j++) // for each seat in the line
                {
                    seat = curr_line.charAt(j);

                    ImageView seat_image = new ImageView(this);
                    seat_image.setLayoutParams(new android.view.ViewGroup.LayoutParams(dpWidthInPx, dpHeightInPx)); //TODO: Changing the resolution!
                    seat_image.setX(current_x);
                    seat_image.setY(current_y);


                    if (seat == '0')
                        seat_image.setImageResource(R.mipmap.ic_empty_seat);

                    else if (seat == '1')
                        seat_image.setImageResource(R.mipmap.ic_seat);


                    absoluteLayout1.addView(seat_image);
                    seat_image.setVisibility(View.VISIBLE);

                    if (title.getHeight() <current_y)
                        title.getLayoutParams().height = (int)current_y + diff_height;


                    current_x+=diff_width;
                }

                current_y+=diff_height; //The y cordinate is changed between each 2 lines
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }

            WindowManager.LayoutParams params = getWindow().getAttributes();
            //params.height = (int) ( (diff_height) * (seats_list.size() ) * (inPixels/5));
            params.height = screen_height ;
        }
    }

}
