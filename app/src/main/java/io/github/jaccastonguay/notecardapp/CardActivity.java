package io.github.jaccastonguay.notecardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    int index;
    ArrayList<Word> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent intent = getIntent();
        ArrayList<String> cardsSide1 = intent.getStringArrayListExtra("cardsSide1");
        ArrayList<String> cardsSide2 = intent.getStringArrayListExtra("cardsSide2");
        index = intent.getIntExtra("index", 0);
        //Fill List
        wordList = new ArrayList<>();
        for(int i = 0; i < cardsSide1.size(); i++){
            wordList.add( new Word(cardsSide1.get(i), cardsSide2.get(i)));
        }


        TextView textView = findViewById(R.id.cardTextView);
        textView.setText(cardsSide1.get(index));

        //Set Swipe controls
        textView.setOnTouchListener(new OnSwipeTouchListener(CardActivity.this) {
            public void onSwipeTop() {
                SwitchSide();
                Toast.makeText(CardActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(CardActivity.this, "right", Toast.LENGTH_SHORT).show();
                SwipeNext(true);
            }
            public void onSwipeLeft() {
                Toast.makeText(CardActivity.this, "left", Toast.LENGTH_SHORT).show();
                SwipeNext(false);
            }
            public void onSwipeBottom() {
                SwitchSide();
                Toast.makeText(CardActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        TextView swipeRightHint = findViewById(R.id.swipeRightHintTextView);
        TextView swipeLeftHint = findViewById(R.id.swipeLeftHintTextView);
        TextView swipeUpHint = findViewById(R.id.swipeUptextView);

        swipeRightHint.animate().alpha(0).setDuration(400).setStartDelay(8000);
        swipeLeftHint.animate().alpha(0).setDuration(400).setStartDelay(8000);
        swipeUpHint.animate().alpha(0).setDuration(400).setStartDelay(8000);

    }



    public void SwipeNext(boolean correct){


        index++;
        if(index >= wordList.size()){
            index = 0;
        }
        //Always want the next card to start on side 1
        TextView textView = findViewById(R.id.cardTextView);
        //textView.animate().translationXBy(100).setDuration(300).alpha(0);
        //textView.animate().setStartDelay(1000).translationX(0).alpha(1);

        //Will animate in a bit
        textView.setText(wordList.get(index).word1);
;
    }

    public void SwitchSide(){
        TextView textView = findViewById(R.id.cardTextView);

        if(textView.getText().toString() == wordList.get(index).word1){
            textView.setText(wordList.get(index).word2);
        }
        else {
            textView.setText(wordList.get(index).word1);
        }
    }


    private class Word{
        String word1;
        String word2;
        private Word(String word1, String word2){
            this.word1 = word1;
            this.word2 = word2;
        }
    }
}
