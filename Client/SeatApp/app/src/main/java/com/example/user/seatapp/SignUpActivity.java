package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);

        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("NewUsername", NewUserName.getText().toString());
        intent.putExtra("NewPassword", NewPassword.getText().toString());

        startActivity(intent);
    }

}
