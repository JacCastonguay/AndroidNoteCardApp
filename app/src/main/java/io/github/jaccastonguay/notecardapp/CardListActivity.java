package io.github.jaccastonguay.notecardapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class CardListActivity extends AppCompatActivity {
    ArrayList<String> cardsSide1 = new ArrayList<>();
    ArrayList<String> cardsSide2 = new ArrayList<>();
    ArrayAdapter<String> cardsAdapter;
    public static final int REQUEST_CODE_ADD_CARD = 1015;
    String chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        final ListView listView = findViewById(R.id.cardListView);
        //Get stuff that was passed.
        Intent intent = getIntent();
        chapter = getStringSafe(intent.getStringExtra("Chapter"));
        String desc = getStringSafe(intent.getStringExtra("Description"));

        Log.i("chapter", chapter);
        Log.i("Desc", desc);
        setTitle(chapter);
        //Query for chapter cards.
        Cursor c  = MainActivity.sqLiteDatabase.rawQuery(String.format("SELECT * FROM Card WHERE chapter = '%s' AND user = '%s'",chapter, ParseUser.getCurrentUser().getUsername()), null);
        int cardsSide1Index = c.getColumnIndex("sideOne");
        int cardsSide2Index = c.getColumnIndex("sideTwo");

        c.moveToFirst();
        while(!c.isAfterLast()){
            cardsSide1.add(c.getString(cardsSide1Index));
            cardsSide2.add(c.getString(cardsSide2Index));

            c.moveToNext();
        }
        c.close();

        cardsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cardsSide1);//For now just showing side 1
        listView.setAdapter(cardsAdapter);

        //Clicking on an item to go to the card
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("click listener", "index " + String.valueOf(i));
                Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                intent.putStringArrayListExtra("cardsSide1", cardsSide1);
                intent.putStringArrayListExtra("cardsSide2", cardsSide2);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });

        //Delete Item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int ind = i;
                //Ask to delete
                new AlertDialog.Builder(CardListActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure you want to delete this card?")
                        .setMessage("card: " + cardsSide1.get(i))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteCard(ind);

                            }
                        })
                        .setNegativeButton("No", null) /*null will just let the pop up close*/
                        .show();


                return true;
            }
        });

    }

    private void DeleteCard(final int ind) {
        final String sideOne = cardsSide1.get(ind);

        //Parse first
        //Get objectId to delete
        Cursor c  = MainActivity.sqLiteDatabase.rawQuery(String.format("SELECT * FROM Card WHERE chapter = '%s' AND user = '%s' AND sideOne = '%s'",chapter, ParseUser.getCurrentUser().getUsername(), sideOne), null);

        //We grab info in our loop based on the column's index.
        int parseObjectIdcolumn = c.getColumnIndex("parseObjectId");
        c.moveToFirst();
        String objectId = c.getString(parseObjectIdcolumn);

        //Now on to parse query
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Card");
        parseQuery.whereEqualTo("objectId", objectId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){

                                //Removing from view
                                cardsSide1.remove(ind);
                                cardsSide2.remove(ind);
                                cardsAdapter.notifyDataSetChanged();
                                //Removing from DB
                                String sql  = "Delete From Card where sideOne = '" + sideOne+"' and chapter = '" + chapter + "'";
                                SQLiteStatement statement = MainActivity.sqLiteDatabase.compileStatement(sql);
                                //statement.bindString(1, sideOne);
                                //statement.bindString(2, chapter);
                                statement.execute();

                            }else{
                                Log.i("Delete Error", e.getMessage());
                            }
                        }
                    });
                }else{
                    Log.i("Retrieve Error", e.getMessage());
                }
            }
        });



    }

    private String getStringSafe(String s){
        if(s != null)
            return s;
        else
            return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Object to set menu. Then choose which menu to set.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.card_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This method is used when a menu option is selected.
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.addCard:
                Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
                intent.putExtra("chapter", chapter);
                startActivityForResult(intent, REQUEST_CODE_ADD_CARD);
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
            case REQUEST_CODE_ADD_CARD:
                if(resultCode == Activity.RESULT_OK){
                    String sideOne = data.getStringExtra("NewSideOne");
                    String sideTwo = data.getStringExtra("NewSideTwo");
                    cardsSide1.add(sideOne);
                    cardsSide2.add(sideTwo);
                    cardsAdapter.notifyDataSetChanged();
                }
        }
    }


}
