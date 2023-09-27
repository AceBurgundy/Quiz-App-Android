package com.example.quizapp;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;

    private boolean mSkipped;
    private boolean answered;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mSkipped = false;
        answered = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean ismSkipped() {
        return mSkipped;
    }

    public void setmSkipped() {
        this.mSkipped = true;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered() {
        answered = true;
    }

    public void reset() {
        mSkipped = false;
        answered = false;
    }


}
