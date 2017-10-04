package com.example.vinoth.childsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {


    private EditText etPassword;
    private EditText etUsername;

    public void check(View view){

        String pass=etPassword.getText().toString();
        String user=etUsername.getText().toString();

        if ((pass.equals("admin"))&&(user.equals("admin"))) {
            Toast.makeText(this,"password correct",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent(this,SettingActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"password wrong",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        etPassword=(EditText)findViewById(R.id.etPasswort);
        etUsername = (EditText) findViewById(R.id.etUsername);




    }



}
