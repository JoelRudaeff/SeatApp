package com.example.user.seatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.os.AsyncTask;


import java.io.*;
import java.net.*;
import java.net.InetAddress;


public class HomePageActivity extends ActionBarActivity
{

    final String host = "192.168.1.42"; //TODO:
    char response_from_server = '-';

    //TODO: New part
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String NewPassword,NewUsername;
        String dstAddress = host;
        int dstPort = 8888;
        char ret;

        public MyClientTask(String NU, String NP)
        {
            NewPassword = NP;
            NewUsername = NU;
        }

        //Execute()
        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                String username_length = String.valueOf(NewUsername.length());
                String password_length = String.valueOf(NewPassword.length());
                String data_from_server = "";
                String read = null;

                // The data that the client sends to the server when he signs-up
                String string_to_send = "l;" + username_length + ";" + NewUsername + ";" + password_length + ";" + NewPassword;
                Socket socket = new Socket(dstAddress, dstPort);


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(string_to_send); //The msg to the server
                output.flush(); // Send off the data

                //read input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());
                InputStreamReader input_reader = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

                while ( (read = br.readLine()) != null) // The data which sent back by the server
                    data_from_server = data_from_server + read;


                //server.close();
                output.close();
                input_reader.close();
                input.close();
                socket.close();


                // l;0/1
                if ( data_from_server.startsWith("l"))
                    ret = data_from_server.charAt(2); // 1 - success, 0 - failure
                else
                    ret = '0'; //replied msg is not defined by the protocol.

            }
            catch ( Exception e)
            {
                e.printStackTrace();
                ret = '0';
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            response_from_server = ret;
            super.onPostExecute(result);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        final EditText UserName = (EditText) findViewById(R.id.username);
        final EditText Password = (EditText) findViewById(R.id.password);

        final Button SignIn = (Button) findViewById(R.id.Sign_in);


    }

    public void startMainActivitySignIn(View view)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        char r; //return value of success or failure from the server
        final String name = ((EditText) findViewById(R.id.username)).getText().toString();
        final String password = ((EditText) findViewById(R.id.password)).getText().toString();

        String username_format_regex = "^[a-zA-Z0-9]{4,12}+";
        int min_username_length = 4, max_username_length = 12;
        int min_password_length = 8, max_password_length = 20;

        // if the username and the password are valid by our format
        if (name.matches(username_format_regex))
        {
            if (password.length() >= min_password_length && password.length() <= max_password_length)
            {
                try
                {
                    MyClientTask myClientTask = new MyClientTask(name,password);
                    myClientTask.execute(); //will run like a thread
                    int times = 0;
                    //wait for the client to get response from the server, if it doesn't connect in a few seconds, terminate the waiting
                    while (response_from_server== '-') {
                        if ( times < 5)
                            Thread.sleep(1500);
                        else
                            break;
                        times++;
                    }
                    //success
                    if ( response_from_server == '1' )
                    {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("Username", name);
                        intent.putExtra("Password", password);

                        Toast toast_successful = Toast.makeText(context, "Successfully logged in!", duration);
                        toast_successful.show();
                        startActivity(intent);
                    }
                    //failure or not connected
                    else
                    {
                        Toast toast_unsuccessful = Toast.makeText(context, "Couldn't log in, try again!", duration);
                        toast_unsuccessful.show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
            else // password length doesn't match our format ( below minimum or above maximum )
            {
                Toast toast_password_length = Toast.makeText(context, "The passwords needs to be between " + String.valueOf(min_password_length) + " to " + String.valueOf(max_password_length) + " characters", duration);
                toast_password_length.show();
            }

        }
        else //username is not by our foramt
        {
            if (name.length() < min_username_length || name.length() > max_username_length)
            {
                Toast name_length = Toast.makeText(context, "The username needs to be between " + String.valueOf(min_username_length) + " to " + String.valueOf(max_username_length) + " characters", duration);
                name_length.show();
            }

            else
            {
                Toast name_special_characters = Toast.makeText(context, "Username can't include special characters!", duration);
                name_special_characters.show();
            }
        }

    }

    public void startSignUpActivity(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
