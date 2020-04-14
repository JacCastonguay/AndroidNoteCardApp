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

public class AddChapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);
        setTitle("Add a new chapter");
    }

    public void CancelClicked(View view){
        finish();
    }

    public void AddClicked(View view){
        EditText editChapter = findViewById(R.id.editChapter);
        EditText editDesc = findViewById(R.id.editDesc);
        String chapter = editChapter.getText().toString();
        String desc = editDesc.getText().toString();

        if(!chapter.equals("")){
            //SQL injection
            String sql  = "INSERT INTO Chapters (chapter, description, user, parseObjectId) VALUES (?, ?, ?, ?)";
            SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
            //1 based counting system.
            statement.bindString(1, chapter);
            statement.bindString(2, desc);
            statement.bindString(3, ParseUser.getCurrentUser().getUsername());
            statement.bindString(4, "test");

            statement.execute();

            //Need to add to listView on way back
            Intent intent = new Intent();
            intent.putExtra("NewChapter", chapter);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Chapter cannot be left blank", Toast.LENGTH_SHORT).show();
        }
    }
}
