package io.github.jaccastonguay.notecardapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

public class AddCardActivity extends AppCompatActivity {
    String chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Intent intent = getIntent();
        chapter = intent.getStringExtra("chapter");
    }

    public void CancelClicked(View view){
        finish();
    }

    public void AddClicked(View view){
        EditText editSideOne = findViewById(R.id.editSideOne);
        EditText editSideTwo = findViewById(R.id.editSideTwo);
        String sideOne = editSideOne.getText().toString();
        String sideTwo = editSideTwo.getText().toString();

        if(!sideOne.equals("") && !sideTwo.equals("")){
            //SQL injection
            String sql  = "INSERT INTO Card (chapter, sideOne, sideTwo, timesRightCounter, user, parseObjectId) VALUES (?, ?, ?, 0, ?, ?)";
            SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
            //1 based counting system.
            statement.bindString(1, chapter);
            statement.bindString(2, sideOne);
            statement.bindString(3, sideTwo);
            statement.bindString(4, ParseUser.getCurrentUser().getUsername());
            statement.bindString(5, "test");

            statement.execute();

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
