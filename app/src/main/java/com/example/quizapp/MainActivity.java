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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String CHEATED_COUNT = "cheated_count";
    private static final String DIALOG_OPEN = "dialog_open";
    private static final String HAS_CHEATED = "hasCheated";
    private static final String QUESTION_INDEX = "index";
    private static final String CURRENT_SCORE = "score";

    private boolean dialogIsOpen = false;
    private TextView questionTextView;

    private static final Question[] questionBank = new Question[] {
            new Question(R.string.question_longest_european_river, true),
            new Question(R.string.question_longest_mountain, true),
            new Question(R.string.question_land_rising_sun, false),
            new Question(R.string.question_smallest_ocean, false),
            new Question(R.string.question_capital_brazil, true),
    };

    private static final int QUESTION_COUNT = questionBank.length;
    private int currentQuestionIndex = 0;
    private boolean playerIsCheater;
    private int cheatedCount = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button trueButton;
        Button falseButton;
        Button cheatButton;
        ImageButton nextButton;
        ImageButton previousButton;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            playerIsCheater = savedInstanceState.getBoolean(HAS_CHEATED, false);
            currentQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX, 0);
            dialogIsOpen = savedInstanceState.getBoolean(DIALOG_OPEN, false);
            cheatedCount = savedInstanceState.getInt(CHEATED_COUNT, 0);
            score = savedInstanceState.getInt(CURRENT_SCORE, 0);
        }

        questionTextView = findViewById(R.id.question_text_view);

        int question = questionBank[currentQuestionIndex].getTextResId();
        questionTextView.setText(question);

        questionTextView.setOnClickListener(view -> {
            questionBank[currentQuestionIndex].markedAsSkipped();
            nextQuestion();
        });

        trueButton = findViewById(R.id.true_button);
        trueButton.setOnClickListener(view -> {

            if (questionAlreadyAnswered()) return;

            questionBank[currentQuestionIndex].markedAsAnswered();
            checkAnswer(true);
            nextQuestion();
        });

        falseButton = findViewById(R.id.false_button);
        falseButton.setOnClickListener(view -> {

            if (questionAlreadyAnswered()) return;

            questionBank[currentQuestionIndex].markedAsAnswered();
            checkAnswer(false);
            nextQuestion();
        });

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(view -> {
            questionBank[currentQuestionIndex].markedAsSkipped();
            nextQuestion();
        });

        cheatButton = findViewById(R.id.cheat_button);
        cheatButton.setOnClickListener(view -> {
            boolean answerIsTrue = questionBank[currentQuestionIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
            cheatActivityResultLauncher.launch(intent);
        });

        previousButton = findViewById(R.id.previous_button);
        previousButton.setOnClickListener(view -> {
            currentQuestionIndex = (currentQuestionIndex - 1 + questionBank.length) % questionBank.length;
            int question1 = questionBank[currentQuestionIndex].getTextResId();
            questionTextView.setText(question1);
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
                        playerIsCheater = CheatActivity.wasAnswerShown(data);
                    }
                }
            }
    );

    private boolean questionAlreadyAnswered() {
        boolean alreadyAnswered = questionBank[currentQuestionIndex].isAnswered();
        if (alreadyAnswered) {
            Toast.makeText(this, "Question already visited", Toast.LENGTH_SHORT).show();
        }
        return alreadyAnswered;
    }

    private void nextQuestion() {
        checkIfWon();
        if (dialogIsOpen) return;
        currentQuestionIndex = (currentQuestionIndex + 1) % questionBank.length;
        int question1 = questionBank[currentQuestionIndex].getTextResId();
        questionTextView.setText(question1);
        playerIsCheater = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(QUESTION_INDEX, currentQuestionIndex);
        savedInstanceState.putBoolean(DIALOG_OPEN, dialogIsOpen);
        savedInstanceState.putBoolean(HAS_CHEATED, playerIsCheater);
        savedInstanceState.putInt(CHEATED_COUNT, cheatedCount);
        savedInstanceState.putInt(CURRENT_SCORE, score);
    }

    private void updateQuestion() {
        int question = questionBank[currentQuestionIndex].getTextResId();
        questionTextView.setText(question);
    }

    private int countSkipped() {
        int skipped = 0;
        for (Question question : questionBank) {
            if (question.isSkipped()) skipped++;
        }
        return skipped;
    }

    private int countVisited() {
        int skipped = 0;
        for (Question question : questionBank) {
            if (question.isSkipped() || question.isAnswered()) skipped++;
        }
        return skipped;
    }

    private void resetQuestions() {
        for (Question question : questionBank) {
            question.reset();
        }
        dialogIsOpen = false;
        playerIsCheater = false;
        score = 0;
        nextQuestion();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = questionBank[currentQuestionIndex].isAnswerTrue();
        if (playerIsCheater) {
            Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show();
            cheatedCount++;
            return;
        }
        if (userPressedTrue == answerIsTrue) score++;
    }

    @SuppressLint("DefaultLocale")
    private void checkIfWon() {
        if (countVisited() == QUESTION_COUNT) {
            if (QUESTION_COUNT == 0) return;
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