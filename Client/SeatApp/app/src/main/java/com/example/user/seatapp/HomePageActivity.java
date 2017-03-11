package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomePageActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        final EditText UserName = (EditText) findViewById(R.id.username);
        final EditText Password = (EditText) findViewById(R.id.password);
        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);
        final EditText EnsurePassword = (EditText) findViewById(R.id.SignUpEnsurePasword);

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

    public void startMainActivitySignUp(View view)
    {
        final EditText NewUserName = (EditText) findViewById(R.id.SignUpUsername);
        final EditText NewPassword = (EditText) findViewById(R.id.SignUpPassword);
        final EditText EnsurePassword = (EditText) findViewById(R.id.SignUpEnsurePasword);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("New Username", NewUserName.getText().toString());
        intent.putExtra("New Password", NewPassword.getText().toString());
        intent.putExtra("Ensure Password", EnsurePassword.getText().toString());

        startActivity(intent);
    }
}
