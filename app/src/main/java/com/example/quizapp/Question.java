package com.example.quizapp;

public class Question {

    private int mTextResId;
    private boolean correctAnswer;

    private boolean skipped;
    private boolean answered;

    public Question(int textResId, boolean answer) {
        mTextResId = textResId;
        correctAnswer = answer;
        skipped = false;
        answered = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public boolean isAnswerTrue() {
        return correctAnswer;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void markedAsSkipped() {
        this.skipped = true;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void markedAsAnswered() {
        answered = true;
    }

    public void reset() {
        skipped = false;
        answered = false;
    }


}
