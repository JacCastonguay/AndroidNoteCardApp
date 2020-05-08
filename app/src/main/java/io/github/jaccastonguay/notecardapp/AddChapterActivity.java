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

public class AddChapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);
        setTitle("Add a new chapter");

        findViewById(R.id.relativeLayout).setOnTouchListener(new View.OnTouchListener() {
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
        EditText editChapter = findViewById(R.id.editChapter);
        EditText editDesc = findViewById(R.id.editDesc);
        final String chapter = editChapter.getText().toString();
        final String desc = editDesc.getText().toString();

        if(!chapter.equals("")){
            //Add to Parse first to get id for local save
            final ParseObject chapterParseObject = new ParseObject("Chapter");
            chapterParseObject.put("chapter", chapter);
            chapterParseObject.put("description", desc);
            chapterParseObject.put("userID", ParseUser.getCurrentUser().getUsername());

            chapterParseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        String objectId = chapterParseObject.getObjectId();

                        //AddLocally
                        //SQL injection
                        String sql  = "INSERT INTO Chapters (chapter, description, user, parseObjectId) VALUES (?, ?, ?, ?)";
                        SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
                        //1 based counting system.
                        statement.bindString(1, chapter);
                        statement.bindString(2, desc);
                        statement.bindString(3, ParseUser.getCurrentUser().getUsername());
                        statement.bindString(4, objectId);

                        statement.execute();

                        Log.i("Object ID", objectId);

                    }else{
                        Toast.makeText(getApplicationContext(), "Sorry, an error has occurred.", Toast.LENGTH_SHORT).show();
                        Log.i("Parse upload error", e.getMessage());
                    }
                }
            });



            //Need to add to listView on way back
            Intent intent = new Intent();
            intent.putExtra("NewChapter", chapter);
            intent.putExtra("NewDesc", desc);
            setResult(Activity.RESULT_OK, intent);

            finish();
        } else {
            Toast.makeText(this, "Chapter cannot be left blank", Toast.LENGTH_SHORT).show();
        }
    }

}
