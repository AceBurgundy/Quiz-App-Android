package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String ANSWER_IS_TRUE = "com.example.quizapp.answer_is_true";
    private static final String ANSWER_SHOWN = "com.example.quizapp.answer_shown";
    private static final String KEY_ANSWER_SHOWN = "answer_shown";
    private static final String KEY_ANSWER_TEXT = "answer_text";

    private boolean answerWasShown;
    private String answerText;

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean answerIsTrue;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cheat);

        Button showAnswerButton;
        TextView answerTextView;

        answerTextView = findViewById(R.id.answer_text_view);
        answerIsTrue = getIntent().getBooleanExtra(ANSWER_IS_TRUE, false);

        if (savedInstanceState != null) {
            answerWasShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
            answerText = savedInstanceState.getString(KEY_ANSWER_TEXT, "");
            if (answerWasShown) {
                setAnswerShownResult();
                answerTextView.setText(answerText);
            }
        }

        showAnswerButton = findViewById(R.id.show_answer_button);
        showAnswerButton.setOnClickListener(v -> {
            answerText = answerIsTrue ? getString(R.string.true_button) : getString(R.string.false_button);
            answerTextView.setText(answerText);
            answerWasShown = true;
            setAnswerShownResult();
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, answerWasShown);
        savedInstanceState.putString(KEY_ANSWER_TEXT, answerText);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setAnswerShownResult() {
        Intent data = new Intent();
        data.putExtra(ANSWER_SHOWN, true);
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }
}
