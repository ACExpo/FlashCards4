package com.example.fcards;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView question = findViewById(R.id.flashcard_question);
        TextView answer = findViewById(R.id.flashcard_answer);
        ImageView add = findViewById(R.id.add_button);
        ImageView next = findViewById(R.id.next_button);//By:ACExpo

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0){
            question.setText(allFlashcards.get(0).getQuestion());
            answer.setText(allFlashcards.get(0).getAnswer());
        }

        question.setOnClickListener(v -> {
            int cx = answer.getWidth();
            int cy = answer.getHeight() / 3;
            float finalRadius = (float) Math.hypot(cx, cy);
            overridePendingTransition(R.anim.anim1, R.anim.anim2);
            Animator anim1 = ViewAnimationUtils.createCircularReveal(answer, cx, cy, 0f, finalRadius);
            question.setVisibility(View.INVISIBLE);
            answer.setVisibility(View.VISIBLE);
            anim1.setDuration(800);
            anim1.start();
        });

        answer.setOnClickListener(v -> {
            answer.setVisibility(View.INVISIBLE);
            question.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.trash).setOnClickListener(v -> {
            flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
            allFlashcards = flashcardDatabase.getAllCards();
            Toast.makeText(MainActivity.this, "Question Deleted", Toast.LENGTH_SHORT).show();
            Log.i("Andreza", "Entered onClick");
        });

        add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
            MainActivity.this.startActivityForResult(intent, 100);
            startActivity(intent);
            overridePendingTransition(R.anim.anim1, R.anim.anim2);
        });

        next.setOnClickListener(v -> {
            overridePendingTransition(R.anim.anim2, R.anim.anim1);
            if (allFlashcards.size() == 0) {
                return;//By:ACExpo
            }

            currentCardDisplayedIndex++;

            if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                currentCardDisplayedIndex = 0;
            }

            question.setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
            question.setVisibility(View.VISIBLE);
            answer.setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
            answer.setVisibility(View.INVISIBLE);

            final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim2);
            final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim1);

            leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // this method is called when the animation first starts
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // this method is called when the animation is finished playing
                    question.startAnimation(rightInAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // we don't need to worry about this method
                }
            });
            question.startAnimation(leftOutAnim);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//By:ACExpo
        TextView questionTextView = findViewById(R.id.flashcard_question);
        TextView answerTextView = findViewById(R.id.flashcard_answer);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");
            questionTextView.setText(question);
            answerTextView.setText(answer);

            flashcardDatabase.insertCard(new Flashcard(question, answer));
            allFlashcards = flashcardDatabase.getAllCards();
        }
    }
}