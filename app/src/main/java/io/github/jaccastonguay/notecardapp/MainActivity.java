package io.github.jaccastonguay.notecardapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase sqLiteDatabase;
    ArrayList<String> chapters = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    public static ArrayAdapter<String> chaptersAdapter;
    ArrayAdapter<String> descriptionAdapter;
    ListView listView;

    public static final int REQUEST_CODE = 1014;

    //ArrayList<Word> wordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        //Local DB
        sqLiteDatabase = this.openOrCreateDatabase("NoteCards", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Chapters (chapter VARCHAR PRIMARY KEY, description VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Card (chapter VARCHAR, sideOne VARCHAR, sideTwo VARCHAR, timesRightCounter Int(2), FOREIGN KEY (chapter) REFERENCES Chapters (chapter))");
        //sqLiteDatabase.execSQL("INSERT INTO Chapters (chapter, description) VALUES ('Chapter 1', 'just the basics')");
        //sqLiteDatabase.execSQL("INSERT INTO Card (chapter, sideOne, sideTwo, timesRightCounter) VALUES ('Chapter 1', 'aula', 'clase', 0)");

        Log.i("Database", "successfully created/updated");

        //Parse connection
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        if(ParseUser.getCurrentUser() != null){
            Log.i("Current User", ParseUser.getCurrentUser().getUsername());
        }
        else{
            //No user signed in. For now I am using a test user until I create a user sign up.
            ParseUser.logInInBackground("Test",  "Test", new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("Login","ok!");
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }





        UpdateChapterListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView)view;
                String desc = descriptions.get(chapters.indexOf(tv.getText().toString()));
                Intent intent = new Intent(getApplicationContext(), CardListActivity.class);
                intent.putExtra("Chapter", tv.getText().toString());
                intent.putExtra("Description", desc);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Object to set menu. Then choose which menu to set.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chapter_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This method is used when a menu option is selected.
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.addChapter:
                Intent intent = new Intent(getApplicationContext(), AddChapterActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    //Used when returning to activity and need to make changes based on a result received (chapter added.)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    String chapter = data.getStringExtra("NewChapter");
                    chapters.add(chapter);
                    chaptersAdapter.notifyDataSetChanged();
                }
        }
    }

    public void UpdateChapterListView(){
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM Chapters", null);

        //We grab info in our loop based on the column's index.
        int chapterIndex = c.getColumnIndex("chapter");
        int descriptionIndex = c.getColumnIndex("description");

        //Set cursor to starting position
        c.moveToFirst();
        //Go through results
        while(!c.isAfterLast()){
            Log.i("chapter", c.getString(chapterIndex));
            Log.i("desc", c.getString(descriptionIndex));
            chapters.add(c.getString(chapterIndex));
            descriptions.add(c.getString(descriptionIndex));

            c.moveToNext();
        }
        c.close();

        chaptersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chapters);
        listView.setAdapter(chaptersAdapter);
    }



//    public void SwitchSide(View view){
//        TextView textView = (TextView)view;
//
//        if(textView.getText().toString() == wordList.get(index).word1){
//            textView.setText(wordList.get(index).word2);
//        }
//        else {
//            textView.setText(wordList.get(index).word1);
//        }
//    }
//
//    public void IndexChange(View view){
//        if(view.getId() == R.id.rightText){
//            //increment
//            if(index < wordList.size() - 1)
//                index++;
//            else
//                index = 0;
//
//        }else if(view.getId() == R.id.leftText){
//            //decrement
//            if(index > 0)
//                index--;
//            else
//                index = wordList.size() - 1;
//        }
//        TextView textView = findViewById(R.id.card);
//        textView.setText(wordList.get(index).word1);
//    }
//
//
//
//
//    private class Word{
//        String word1;
//        String word2;
//        private Word(String word1, String word2){
//            this.word1 = word1;
//            this.word2 = word2;
//        }
//    }
}
