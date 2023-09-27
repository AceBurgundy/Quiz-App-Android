package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.quizapp.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.quizapp.answer_shown";
    private static final String KEY_ANSWER_SHOWN = "answer_shown";
    private static final String KEY_ANSWER_TEXT = "answer_text";

    private boolean mAnswerShown = false;
    private String mAnswerText;

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean mAnswerIsTrue;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        Button mShowAnswerButton;
        TextView mAnswerTextView;
        mAnswerTextView = findViewById(R.id.answer_text_view);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if (savedInstanceState != null) {
            mAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
            mAnswerText = savedInstanceState.getString(KEY_ANSWER_TEXT, "");
            if (mAnswerShown) {
                setAnswerShownResult();
                mAnswerTextView.setText(mAnswerText);
            }
        }

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(v -> {
            mAnswerText = mAnswerIsTrue ? getString(R.string.true_button) : getString(R.string.false_button);
            mAnswerTextView.setText(mAnswerText);
            mAnswerShown = true;
            setAnswerShownResult();
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, mAnswerShown);
        savedInstanceState.putString(KEY_ANSWER_TEXT, mAnswerText);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setAnswerShownResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, true);
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }
}
