package io.github.jaccastonguay.notecardapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase sqLiteDatabase;
    ArrayList<String> chapters = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    public static ArrayAdapter<String> chaptersAdapter;
    ArrayAdapter<String> descriptionAdapter;
    ListView listView;

    public static final int REQUEST_CODE = 1014;
    public static final int REQUEST_CODE_USER_LOGIN = 1016;

    //ArrayList<Word> wordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        //Local DB
        sqLiteDatabase = this.openOrCreateDatabase("NoteCards", MODE_PRIVATE, null);
//        try {
//            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Card");
//            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Chapters");
//            Log.i("Tables dropped", "success");
//        }catch (Exception exception){
//            Log.i("Didn't work", exception.getMessage());
//        }

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Chapters (chapter VARCHAR, description VARCHAR, user VARCHAR, parseObjectId VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Card (chapter VARCHAR, sideOne VARCHAR, sideTwo VARCHAR, timesRightCounter Int(2), user VARCHAR, parseObjectId VARCHAR, FOREIGN KEY (chapter) REFERENCES Chapters (chapter))");
        //sqLiteDatabase.execSQL("INSERT INTO Chapters (chapter, description) VALUES ('Chapter 1', 'just the basics')");
        //sqLiteDatabase.execSQL("INSERT INTO Card (chapter, sideOne, sideTwo, timesRightCounter) VALUES ('Chapter 1', 'aula', 'clase', 0)");

        Log.i("Database", "successfully created/updated");

        //Parse connection
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        if(ParseUser.getCurrentUser() != null){
            Log.i("Current User", ParseUser.getCurrentUser().getUsername());
        }
        else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_USER_LOGIN);
        }

        UpdateChapterListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView)view;
                //String desc = descriptions.get(chapters.indexOf(tv.getText().toString()));
                Intent intent = new Intent(getApplicationContext(), CardListActivity.class);
                intent.putExtra("Chapter", tv.getText().toString());
                //intent.putExtra("Description", desc);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int ind = i;
                //Ask to delete
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure you want to delete this chapter?")
                        .setMessage("card: " + chapters.get(i))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteChapter(ind);

                            }
                        })
                        .setNegativeButton("No", null) /*null will just let the pop up close*/
                        .show();


                return true;
            }
        });

    }

    private void DeleteChapter(final int ind){
        final String chapter = chapters.get(ind);

        //Parse first
        //Delete cards with said chapter
        ParseQuery<ParseObject> cardQuery = new ParseQuery<ParseObject>("Card");
        cardQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getUsername());
        cardQuery.whereEqualTo("chapter", chapter);
        cardQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.i("Query success", "cards from chapter");
                    for(ParseObject obj : objects){
                        obj.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.i("delete success", "cards from chapter");
                                }else{
                                    Log.i("failed to delete", e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.i("failed to query", e.getMessage());
                }
            }
        });


        //Delete cards locally
        String sql  = "Delete From Card where user = '" + ParseUser.getCurrentUser().getUsername() + "' AND chapter = '" + chapter + "'";
        SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
        statement.execute();

        //Delete Chapter from parse
        ParseQuery<ParseObject> chapterQuery = new ParseQuery<ParseObject>("Chapter");
        chapterQuery.whereEqualTo("userID", ParseUser.getCurrentUser().getUsername());
        chapterQuery.whereEqualTo("chapter", chapter);
        chapterQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.i("Query success", "chapter");
                    for(ParseObject obj : objects){
                        obj.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.i("delete success", "chapter");
                                }else{
                                    Log.i("failed to delete", e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.i("failed to query", e.getMessage());
                }
            }
        });

        //Delete chapter locally
        String chapterSql  = "Delete From Chapters where user = '" + ParseUser.getCurrentUser().getUsername() + "' AND chapter = '" + chapter + "'";
        SQLiteStatement chapterStatement = MainActivity.sqLiteDatabase.compileStatement(chapterSql);
        chapterStatement.execute();

        chapters.remove(ind);
        descriptions.remove(ind);
        chaptersAdapter.notifyDataSetChanged();

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
            case R.id.logout:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivityForResult(intent1, REQUEST_CODE);
                        }else{
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    String desc = data.getStringExtra("NewDesc");
                    descriptions.add(desc);
                    chaptersAdapter.notifyDataSetChanged();
                }
                break;
            case REQUEST_CODE_USER_LOGIN:
                UpdateChapterListView();
        }
    }

    public void UpdateChapterListView(){

        String query = "SELECT * FROM Chapters";
        if(ParseUser.getCurrentUser() != null){
            query = "SELECT * FROM Chapters where user = '" + ParseUser.getCurrentUser().getUsername() +"'";
        }

        Cursor c = sqLiteDatabase.rawQuery(query, null);

        //We grab info in our loop based on the column's index.
        int chapterIndex = c.getColumnIndex("chapter");
        int descriptionIndex = c.getColumnIndex("description");
        chapters.clear();
        descriptions.clear();
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

    public void onBackPressed() {
        //Disallowing backbutton for logged in users so multiple logins are not created
        if (ParseUser.getCurrentUser() != null) {

        } else {
            super.onBackPressed();
        }
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
