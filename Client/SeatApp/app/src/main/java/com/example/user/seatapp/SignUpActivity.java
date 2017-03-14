package com.example.user.seatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    public void startHomePage(View view)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);
        final EditText RePassword = (EditText) findViewById(R.id.SignUpPasswordAgain);

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

            else {
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("NewUsername", NewUserName.getText().toString());
                intent.putExtra("NewPassword", NewPassword.getText().toString());

                startActivity(intent);
            }
        }
    }

}
