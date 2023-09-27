package com.example.quizapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String QUESTION_INDEX = "index";
    private static final String HAS_CHEATED = "hasCheated";
    private boolean dialogIsOpen = false;
    private TextView mQuestionTextView;

    private static final Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_longest_mountain, true),
            new Question(R.string.question_capital_brazil, true),
            new Question(R.string.question_smallest_ocean, false),
            new Question(R.string.question_longest_european_river, true),
            new Question(R.string.question_land_rising_sun, false),
    };

        private static final int QUESTION_COUNT = mQuestionBank.length;
    private int score = 0;
    private int mCurrentIndex = 0;

    private int cheatedCount = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button mTrueButton;
        Button mFalseButton;
        Button mCheatButton;
        ImageButton mNextButton;
        ImageButton mPreviousButton;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(HAS_CHEATED, false);
            mCurrentIndex = savedInstanceState.getInt(QUESTION_INDEX, 0);
        }

        Log.d(TAG, String.format("OnCreate Cheater: %s", mIsCheater));

        mQuestionTextView = findViewById(R.id.question_text_view);

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mQuestionTextView.setOnClickListener(view -> {
            mQuestionBank[mCurrentIndex].setmSkipped();
            nextQuestion();
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(view -> {

            if (questionAlreadyAnswered()) return;

            mQuestionBank[mCurrentIndex].setAnswered();
            checkAnswer(true);
            nextQuestion();
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(view -> {

            if (questionAlreadyAnswered()) return;

            mQuestionBank[mCurrentIndex].setAnswered();
            checkAnswer(false);
            nextQuestion();
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(view -> {
            mQuestionBank[mCurrentIndex].setmSkipped();
            nextQuestion();
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(view -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
            cheatActivityResultLauncher.launch(intent);
        });

        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(view -> {
            mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
            int question1 = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question1);
            updateQuestion();
        });

        updateQuestion();
    }

    // Define an ActivityResultLauncher for CheatActivity
    private final ActivityResultLauncher<Intent> cheatActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        mIsCheater = CheatActivity.wasAnswerShown(data);
                    }
                }
            }
    );

    private boolean questionAlreadyAnswered() {
        boolean alreadyAnswered = mQuestionBank[mCurrentIndex].isAnswered();
        if (alreadyAnswered) {
            Toast.makeText(this, "Question already visited", Toast.LENGTH_SHORT).show();
        }
        return alreadyAnswered;
    }

    private void nextQuestion() {
        checkIfWon();
        if (dialogIsOpen) return;
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        int question1 = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question1);
        mIsCheater = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(QUESTION_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(HAS_CHEATED, mIsCheater);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private int countSkipped() {
        int skipped = 0;
        for (Question question : mQuestionBank) {
            if (question.ismSkipped()) skipped++;
        }
        return skipped;
    }

    private int countVisited() {
        int skipped = 0;
        for (Question question : mQuestionBank) {
            if (question.ismSkipped() || question.isAnswered()) skipped++;
        }
        return skipped;
    }

    private void resetQuestions() {
        for (Question question : mQuestionBank) {
            question.reset();
        }
        dialogIsOpen = false;
        mIsCheater = false;
        score = 0;
        nextQuestion();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        if (mIsCheater) {
            Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show();
            cheatedCount++;
            return;
        }
        if (userPressedTrue == answerIsTrue) score++;
    }

    @SuppressLint("DefaultLocale")
    private void checkIfWon() {
        if (countVisited() == QUESTION_COUNT) {
            int correctPercentage = (score * 100) / QUESTION_COUNT;
            if (score < QUESTION_COUNT) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialogIsOpen = true;
                builder.setMessage(String.format("Restart game?%n%nCheated: %s%nSkipped %s%nScore: %s out of %s answers%nPercentage: (%d%%)", cheatedCount, countSkipped(), score, QUESTION_COUNT, correctPercentage))
                        .setPositiveButton("Yes", (dialog, id) -> resetQuestions())
                        .setNegativeButton("No", (dialog, id) -> finish());
                builder.create().show();
            }
        }
    }

}