package com.example.user.seatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TrainsActivity extends ActionBarActivity
{

    final String host = "192.168.1.42"; //TODO:
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
                String string_to_send = ";" + "v" + ";" + vehicle_type_length + ";" + Vehicle_type + ";"  + vehicle_company_length + ";"   + Vehicle_company + ";"   + vehicle_number_length + ";"   + Vehicle_number  ;

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
        setContentView(R.layout.activity_trains);

        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner leaving_time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.train_stations, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> leaving_time_adapter = ArrayAdapter.createFromResource(this, R.array.trains_time, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears - the spinner type
        places_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaving_time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        current_spinner.setAdapter(places_adapter);
        destination_spinner.setAdapter(places_adapter);
        leaving_time_spinner.setAdapter(leaving_time_adapter);
    }

    public void startTrainSchedule(View view)
    {
        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner leaving_time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        Intent intent = new Intent(this, TrainScheduleActivity.class);
        intent.putExtra("current", current_spinner.getSelectedItem().toString());
        intent.putExtra("destination", destination_spinner.getSelectedItem().toString());
        intent.putExtra("leaving time", leaving_time_spinner.getSelectedItem().toString());

        startActivity(intent);
    }

}
