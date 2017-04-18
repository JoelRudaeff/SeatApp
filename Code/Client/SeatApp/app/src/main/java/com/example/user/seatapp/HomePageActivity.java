package com.example.user.seatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.os.Handler;
import android.content.DialogInterface;
import java.io.*;
import java.net.*;



public class HomePageActivity extends ActionBarActivity
{

    final String host = "10.10.0.14"; //TODO:
    char response_from_server = '-';


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
                String data_from_server;

                // The data that the client sends to the server when he signs-up
                String string_to_send = ";l;" + username_length + ";" + NewUsername + ";" + password_length + ";" + NewPassword;

                Socket socket = new Socket(dstAddress, dstPort);


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(string_to_send); //The msg to the server
                output.flush(); // Send off the data

                //read input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());
                InputStreamReader input_reader = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

                data_from_server = br.readLine();

                if ((data_from_server).contains("l;0"))
                    response_from_server = '0';
                else if ((data_from_server).contains("l;1"))
                    response_from_server = '1';

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
                ret = '0';
            }
            return null;
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
                    ProgressDialog progress = new ProgressDialog(this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    MyClientTask myClientTask = new MyClientTask(name,password);
                    myClientTask.execute(); //will run like a thread

                    int times = 0;
                    //wait for the client to get response from the server, if it doesn't connect in a few seconds, terminate the waiting
                    while (response_from_server== '-')
                    {
                        if (times < 5)
                            Thread.sleep(1000);
                        times++;
                    }
                    progress.hide();

                    //success
                    if ( response_from_server == '1' )
                    {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("Username", name);
                        intent.putExtra("Password", password);

                        Toast toast_successful = Toast.makeText(context, "Successfully logged in!", duration);
                        toast_successful.show();

                        response_from_server = '-';
                        startActivity(intent);
                    }
                    //failure or not connected
                    else
                    {
                        if ( response_from_server == '0')
                        {
                            Toast toast_unsuccessful = Toast.makeText(context, "Username and Password combination wasn't found, try again!", duration);
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
