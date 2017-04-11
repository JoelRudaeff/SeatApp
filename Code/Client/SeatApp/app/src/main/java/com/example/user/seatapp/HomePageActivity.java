package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.*;
import java.net.*;
import java.net.InetAddress;


public class HomePageActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String HOST = "10.10.0.11";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        final EditText UserName = (EditText) findViewById(R.id.username);
        final EditText Password = (EditText) findViewById(R.id.password);

        final Button SignIn = (Button) findViewById(R.id.Sign_in);


    }

    public void startMainActivitySignIn(View view)
    {
        final EditText UserName = (EditText) findViewById(R.id.username);
        final EditText Password = (EditText) findViewById(R.id.password);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username", UserName.getText().toString());
        intent.putExtra("Password", Password.getText().toString());

        startActivity(intent);
    }

    public void startSignUpActivity(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
