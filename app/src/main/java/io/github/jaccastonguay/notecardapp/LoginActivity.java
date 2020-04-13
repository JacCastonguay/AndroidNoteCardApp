package io.github.jaccastonguay.notecardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    boolean signupMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void buttonClick(View view){
        if(signupMode){

        }else{
            EditText usernameEditText = findViewById(R.id.usernameEditText);
            EditText passwordEditText = findViewById(R.id.passwordEditText);
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("Login","ok!");
                        //Only Makes activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //Go to activity
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void switchButton(View view){
        Button loginButton = findViewById(R.id.loginButton);
        TextView switchTextView = findViewById(R.id.switchTextView);

        if(signupMode){
            loginButton.setText("Login");
            switchTextView.setText("Or, Sign Up");
        }else{
            loginButton.setText("Sign Up");
            switchTextView.setText("Or, Login");
        }
        signupMode = !signupMode;
    }
}
