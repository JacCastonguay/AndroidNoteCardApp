package io.github.jaccastonguay.notecardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    boolean signupMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    public void buttonClick(View view){
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        if(signupMode){
            ParseUser user = new ParseUser();
            user.setUsername(usernameEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Signup", "Success");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //Go to activity
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                        Log.i("Login error", e.getMessage());
                    }
                }
            });
        }else{
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

    public void onBackPressed() {
        if (ParseUser.getCurrentUser() == null) {

        } else {
            super.onBackPressed();
        }
    }
}
