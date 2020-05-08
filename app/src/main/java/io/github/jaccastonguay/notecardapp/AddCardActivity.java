package io.github.jaccastonguay.notecardapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddCardActivity extends AppCompatActivity {
    String chapter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Intent intent = getIntent();
        chapter = intent.getStringExtra("chapter");

        findViewById(R.id.addCardRelativeLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    public void CancelClicked(View view){
        finish();
    }

    public void AddClicked(View view){
        EditText editSideOne = findViewById(R.id.editSideOne);
        EditText editSideTwo = findViewById(R.id.editSideTwo);
        final String sideOne = editSideOne.getText().toString();
        final String sideTwo = editSideTwo.getText().toString();

        if(!sideOne.equals("") && !sideTwo.equals("")){

            //Parse object first
            final ParseObject cardParseObject = new ParseObject("Card");
            cardParseObject.put("chapter", chapter);
            cardParseObject.put("userId", ParseUser.getCurrentUser().getUsername());
            cardParseObject.put("sideOne", sideOne);
            cardParseObject.put("sideTwo", sideTwo);
            cardParseObject.put("timesRight", 0);
            cardParseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.i("Card obId", cardParseObject.getObjectId());
                        //SQL injection
                        String sql  = "INSERT INTO Card (chapter, sideOne, sideTwo, timesRightCounter, user, parseObjectId) VALUES (?, ?, ?, 0, ?, ?)";
                        SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
                        //1 based counting system.
                        statement.bindString(1, String.valueOf(cardParseObject.get("chapter")));
                        statement.bindString(2, sideOne);
                        statement.bindString(3, sideTwo);
                        statement.bindString(4, ParseUser.getCurrentUser().getUsername());
                        statement.bindString(5, cardParseObject.getObjectId());

                        statement.execute();

                    }else{
                        Toast.makeText(getApplicationContext(), "Sorry, an error has occurred.", Toast.LENGTH_SHORT).show();
                        Log.i("Parse upload error", e.getMessage());
                    }
                }
            });



            //Need to add to listView on way back
            Intent intent = new Intent();
            intent.putExtra("NewSideOne", sideOne);
            intent.putExtra("NewSideTwo", sideTwo);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "sides cannot be left blank", Toast.LENGTH_SHORT).show();
        }
    }
}
