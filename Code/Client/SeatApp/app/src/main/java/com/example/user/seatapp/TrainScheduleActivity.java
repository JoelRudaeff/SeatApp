package com.example.user.seatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

public class TrainScheduleActivity extends AppCompatActivity
{

    final String host = "10.10.0.20"; //TODO:
    String response_from_server = "-";


    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String Vehicle_type,Vehicle_company, Vehicle_number;
        String dstAddress = host;
        int dstPort = 8888;
        String ret;

        public MyClientTask(String VT, String VC, String VN)
        {
            Vehicle_type = VT;
            Vehicle_company = VC;
            Vehicle_number = VN;
        }

        //Execute()
        @Override
        protected Void doInBackground(Void... arg0) {

            try
            {
                String vehicle_type_length = String.valueOf(Vehicle_type.length());
                String vehicle_company_length = String.valueOf(Vehicle_company.length());
                String vehicle_number_length = String.valueOf(Vehicle_number.length());
                String data_from_server;

                // The data that the client sends to the server when he signs-up
                String string_to_send = ";" + "g" + ";" + vehicle_type_length + ";" + Vehicle_type + ";"  + vehicle_company_length + ";"   + Vehicle_company + ";"   + vehicle_number_length + ";"   + Vehicle_number + ";"  ;

                Socket socket = new Socket(dstAddress, dstPort);


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(string_to_send); //The msg to the server
                output.flush(); // Send off the data

                //read input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());
                InputStreamReader input_reader = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

                data_from_server = br.readLine();

                if ((data_from_server).contains("g;"))
                    response_from_server = data_from_server;
                else
                    response_from_server = "0";

                //server.close();

                br.close();
                input.close();
                input_reader.close();
                output.close();
                socket.close();

            }
            catch ( Exception e)
            {
                e.printStackTrace();
                ret = "0";
            }
            return null;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule);

        Intent intent = getIntent();
        TextView cur = (TextView) findViewById(R.id.t_cur);
        TextView dest = (TextView) findViewById(R.id.t_dest);
        LinkedList<String> places_linkedlist = new LinkedList<String>(); // a linked list of the place
        LinkedList<Integer> times_linkedlist = new LinkedList<Integer>(); // a linked list of the time to go from a place to the next place in the places' list
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.train_stations, android.R.layout.simple_spinner_item);

        int i;
        int add_to_lt = 0; //add this time to the leaving time - the time difference between 2 trains

        String current_place = "";
        String destination_place = "";
        String leaving_time = "";
        String NextLeavingTime;
        String arriving_time;
        String TrainNumber;

        if( !( intent.getExtras().getString("current").isEmpty() )) //If the user entered her/his current place
        {
            cur.setText(intent.getExtras().getString("current"));
            current_place = intent.getExtras().getString("current");
        }

        if( !( intent.getExtras().getString("destination").isEmpty() )) //If the user entered her/his current place
        {
            dest.setText(intent.getExtras().getString("destination"));
            destination_place = intent.getExtras().getString("destination");
        }

        if( !( intent.getExtras().getString("leaving time").isEmpty() )) //If the user entered her/his current place
        {
            leaving_time = intent.getExtras().getString("leaving time");
        }



        for(i=0 ; i<places_adapter.getCount() ; i++)
        {
            String s = places_adapter.getItem(i).toString();
            places_linkedlist.add(i, s);
        }

        times_linkedlist.add(0, 0);
        times_linkedlist.add(1, 7);
        times_linkedlist.add(2, 17);
        times_linkedlist.add(3, 20);
        times_linkedlist.add(4, 23);
        times_linkedlist.add(5, 25);
        times_linkedlist.add(6, 33);
        times_linkedlist.add(7, 38);
        times_linkedlist.add(8, 44);
        times_linkedlist.add(9, 92);
        times_linkedlist.add(10, 97);
        times_linkedlist.add(11, 100);
        times_linkedlist.add(12, 105);


        TextView table_leaving_time_1 = (TextView) findViewById(R.id.lt1);
        TextView table_arriving_time_1 = (TextView) findViewById(R.id.at1);
        Button table_train_number_1 = (Button) findViewById(R.id.tn1);
        arriving_time = getArrivingTime(current_place, destination_place, leaving_time, times_linkedlist, places_linkedlist);
        table_leaving_time_1.setText(leaving_time);
        table_arriving_time_1.setText(arriving_time);
        table_train_number_1.setText("101");

        TextView table_leaving_time_2 = (TextView) findViewById(R.id.lt2);
        TextView table_arriving_time_2 = (TextView) findViewById(R.id.at2);
        Button table_train_number_2 = (Button) findViewById(R.id.tn2);
        add_to_lt += 22;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_2.setText(NextLeavingTime);
        table_arriving_time_2.setText(arriving_time);
        table_train_number_2.setText("11");

        TextView table_leaving_time_3 = (TextView) findViewById(R.id.lt3);
        TextView table_arriving_time_3 = (TextView) findViewById(R.id.at3);
        Button table_train_number_3 = (Button) findViewById(R.id.tn3);
        add_to_lt += 18;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_3.setText(NextLeavingTime);
        table_arriving_time_3.setText(arriving_time);
        table_train_number_3.setText("21");

        TextView table_leaving_time_4 = (TextView) findViewById(R.id.lt4);
        TextView table_arriving_time_4 = (TextView) findViewById(R.id.at4);
        Button table_train_number_4 = (Button) findViewById(R.id.tn4);
        add_to_lt += 20;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_4.setText(NextLeavingTime);
        table_arriving_time_4.setText(arriving_time);
        table_train_number_4.setText("151");

        TextView table_leaving_time_5 = (TextView) findViewById(R.id.lt5);
        TextView table_arriving_time_5 = (TextView) findViewById(R.id.at5);
        Button table_train_number_5 = (Button) findViewById(R.id.tn5);
        add_to_lt += 22;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_5.setText(NextLeavingTime);
        table_arriving_time_5.setText(arriving_time);
        table_train_number_5.setText("153");

        TextView table_leaving_time_6 = (TextView) findViewById(R.id.lt6);
        TextView table_arriving_time_6 = (TextView) findViewById(R.id.at6);
        Button table_train_number_6 = (Button) findViewById(R.id.tn6);
        add_to_lt += 18;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_6.setText(NextLeavingTime);
        table_arriving_time_6.setText(arriving_time);
        table_train_number_6.setText("103");

        TextView table_leaving_time_7 = (TextView) findViewById(R.id.lt7);
        TextView table_arriving_time_7 = (TextView) findViewById(R.id.at7);
        Button table_train_number_7 = (Button) findViewById(R.id.tn7);
        add_to_lt += 20;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_7.setText(NextLeavingTime);
        table_arriving_time_7.setText(arriving_time);
        table_train_number_7.setText("23");

        TextView table_leaving_time_8 = (TextView) findViewById(R.id.lt8);
        TextView table_arriving_time_8 = (TextView) findViewById(R.id.at8);
        Button table_train_number_8 = (Button) findViewById(R.id.tn8);
        add_to_lt += 22;
        NextLeavingTime = get_next_leaving_time(leaving_time, add_to_lt);
        arriving_time = getArrivingTime(current_place, destination_place, NextLeavingTime, times_linkedlist, places_linkedlist);
        table_leaving_time_8.setText(NextLeavingTime);
        table_arriving_time_8.setText(arriving_time);
        table_train_number_8.setText("105");

    }

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

    public void SendingDetailsToServer(String TrainNumber)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        String ret = "0";
        String vehicle_type = "Bus";
        String vehicle_company = "Egged";
        String vehicle_number = "263";
        String data_from_server = "";
        String read = null;

        try
        {
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            MyClientTask myClientTask = new MyClientTask(vehicle_type, vehicle_company, vehicle_number);
            myClientTask.execute(); //will run like a thread

            int times = 0;
            //wait for the client to get response from the server, if it doesn't connect in a few seconds, terminate the waiting
            while (response_from_server == "-")
            {
                if (times < 5)
                    Thread.sleep(1000);
                times++;
            }
            progress.hide();

            //success
            if (response_from_server.contains("g;"))
            {
                Intent intent = new Intent(this, TrainSeatsActivity.class);
                intent.putExtra("message", response_from_server);

                Toast toast_successful = Toast.makeText(context, "Your train's seats", duration);
                toast_successful.show();

                response_from_server = "-";
                startActivity(intent);
            }
            //failure or not connected
            else
            {
                if (response_from_server == "0")
                {
                    Toast toast_unsuccessful = Toast.makeText(context, "Couldn't get the seats of this train. Try again!", duration);
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

    public void startTrain1(View view)
    {
        String ret = "1"; //TODO: cahnge it to "0"
        Button button_train_number = (Button) findViewById(R.id.tn1);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain2(View view)
    {
        String ret = "1"; //TODO: cahnge it to "0"
        Button button_train_number = (Button) findViewById(R.id.tn2);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain3(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn3);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain4(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn4);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain5(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn5);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain6(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn6);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain7(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn7);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }

    public void startTrain8(View view)
    {
        Button button_train_number = (Button) findViewById(R.id.tn8);
        String TrainNumber = button_train_number.getText().toString();

        SendingDetailsToServer(TrainNumber);
    }
}
