package com.example.user.seatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SignUpActivity extends ActionBarActivity
{

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


    public int SendingDetailsToServer(String NewUsername, String NewPassword, String NewEmail)
    {
        int ret = 0;
        String username_length = String.valueOf(NewUsername.length());
        String password_length = String.valueOf(NewPassword.length());
        String Email_length = String.valueOf(NewEmail.length());
        String data_from_server = "";
        String read = null;

        // The data that the client sends to the server when he signs-up
        String string_to_send = "r" + username_length + NewUsername + password_length + NewPassword + Email_length + NewEmail;

        try
        {
            Socket s = new Socket("127.0.0.1", 7000); //TODO: Change the IP and the port number
            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            output.writeUTF(string_to_send); //The sending to the server

            //read input stream
            DataInputStream input = new DataInputStream(s.getInputStream());
            InputStreamReader input_reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

            while ( (read = br.readLine()) != null) // The data which sent back by the server
            {
                data_from_server = data_from_server + read;
            }

            //print the input to the application screen
            final TextView receivedMsg = (TextView) findViewById(R.id.cur); //TODO: change the id of the text view!
            receivedMsg.setText(read);

            output.close();
            input_reader.close();
            input.close();
            s.close();

            ret = 1;
        }

        catch (IOException e)
        {
            e.printStackTrace();
            ret = 0; //Because an error was occurred
        }

        return ret;
    }


    public void startHomePage(View view)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);
        final EditText RePassword = (EditText) findViewById(R.id.SignUpPasswordAgain);
        final EditText NewEmail = (EditText) findViewById(R.id.SignUpEmail);

        String name = NewUserName.getText().toString();

        //Checking if the username was written by the rules
        if (! (name.matches("^[a-zA-Z0-9]{4,12}+") )) //if the username contains special characters or shorter/longer than the usual
        {
            if (name.length() < 4 || name.length() > 12)
            {
                Toast name_length = Toast.makeText(context, "The username needs to be between 4 to 12 characters", duration);
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
            if (!NewPassword.getText().toString().equals(RePassword.getText().toString())) //if the password and the re-password doesn't equal
            {
                Toast toast_passwords = Toast.makeText(context, "Please enter again your password and re-enter it!", duration);
                toast_passwords.show();
            }

            else
            {
                int ret;

                Intent intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("NewUsername", NewUserName.getText().toString());
                intent.putExtra("NewPassword", NewPassword.getText().toString());

                //Sending the necessary details to the server, and getting back the answer from it
                //ret = SendingDetailsToServer(NewUserName.getText().toString(), NewPassword.getText().toString(), NewEmail.getText().toString());

                Toast toast_successful = Toast.makeText(context, "Successfully signing-up!", duration);
                toast_successful.show();

                startActivity(intent);
            }
        }
    }

}
