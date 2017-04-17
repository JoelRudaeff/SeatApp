package com.example.user.seatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class SignUpActivity extends ActionBarActivity
{
    String host = "192.168.1.42";
    char response_from_server = '-';


    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String NewPassword,NewUsername,NewEmail;
        String dstAddress = host;
        int dstPort = 8888;
        char ret;

        public MyClientTask(String NU, String NP, String NE)
        {
            NewPassword = NP;
            NewUsername = NU;
            NewEmail = NE;
        }

        //Execute()
        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                String username_length = String.valueOf(NewUsername.length());
                String password_length = String.valueOf(NewPassword.length());
                String email_length = String.valueOf(NewEmail.length());
                String data_from_server;

                // The data that the client sends to the server when he signs-up
                String string_to_send = ";r;" + username_length + ";" + NewUsername + ";" + password_length + ";" + NewPassword + ";" + email_length + ";" + NewEmail;


                Socket socket = new Socket(dstAddress, dstPort);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(string_to_send); //The msg to the server
                output.flush(); // Send off the data

                //read input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());
                InputStreamReader input_reader = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

                data_from_server = br.readLine();

                if ((data_from_server).contains("r;0"))
                    response_from_server = '0';
                else if ((data_from_server).contains("r;1"))
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
        setContentView(R.layout.activity_sign_up);

        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);
        final EditText EnsurePassword = (EditText) findViewById(R.id.SignUpPasswordAgain);

        final Button SignUp = (Button) findViewById(R.id.SignUpButton);

    }


    public void startHomePage(View view)
    {
        Context context = getApplicationContext();

        String email_format_regex = "^(.+)@(.+)$";
        String username_format_regex = "^[a-zA-Z0-9]{4,12}+";

        int duration = Toast.LENGTH_SHORT;
        int min_username_length = 4, max_username_length = 12;
        int min_password_length = 8, max_password_length = 20;

        String name = ((EditText) findViewById(R.id.SignUpUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.SignUpPassword)).getText().toString();
        String repassword = ((EditText) findViewById(R.id.SignUpPasswordAgain)).getText().toString();
        String email = ((EditText) findViewById(R.id.SignUpEmail)).getText().toString();



        //Checking if the username was written by the rules
        if (! (name.matches(username_format_regex) )) //if the username contains special characters or shorter/longer than the usual
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

        else
        {
            //Checking the password and it's re-password
            if (!password.equals(repassword) || password.equals(""))
            {   //if the password and the re-password aren't equal OR both of them are empty
                Toast toast_passwords = Toast.makeText(context, "Please enter again your password and re-enter it!", duration);
                toast_passwords.show();
            }
            else if ( password.length() < min_password_length || password.length() > max_password_length)
            {
                //length of the password is not by our standards
                Toast toast_password_length = Toast.makeText(context, "The passwords needs to be between " + String.valueOf(min_password_length) + " to " + String.valueOf(max_password_length) + " characters", duration);
                toast_password_length.show();
            }
            else
            {

                //checking the email format
                if (! email.matches(email_format_regex) || email.equals(""))
                {
                    Toast toast_email = Toast.makeText(context, "Please enter again your email!", duration);
                    toast_email.show();
                }
                else
                {
                    char ret;

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("NewUsername", name);
                    intent.putExtra("NewPassword", password);
                    try
                    {
                        ProgressDialog progress = new ProgressDialog(this);
                        progress.setTitle("Loading");
                        progress.setMessage("Wait while loading...");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();

                        MyClientTask myClientTask = new MyClientTask(name,password,email);
                        myClientTask.execute();

                        int times = 0;
                        //wait for the client to get response from the server, if it doesn't connect in a few seconds, terminate the waiting
                        while (response_from_server== '-')
                        {
                            if (times < 10)
                                Thread.sleep(1000);
                            times++;
                        }
                        progress.hide();

                        //Sending the necessary details to the server, and getting back the answer from it
                        if( response_from_server == '1')
                        {
                            Toast toast_successful = Toast.makeText(context, "Successfully signing-up!", duration);
                            toast_successful.show();
                            startActivity(intent);
                        }
                        else
                        {
                            if ( response_from_server == '0')
                            {
                                Toast toast_unsuccessful = Toast.makeText(context, "registration failed, try again!", duration);
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
            }
        }
    }
}
