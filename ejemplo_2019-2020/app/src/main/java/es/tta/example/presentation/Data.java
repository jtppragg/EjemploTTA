package es.tta.example.presentation;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.tta.example.model.Exercise;
import es.tta.example.model.Test;

/**
 * Created by gorka on 20/10/15.
 */
public class Data {
    private final static String EXTRA_USER = "es.tta.example.user";
    private final static String EXTRA_AUTH = "es.tta.example.auth";
    private final static String EXTRA_NAME = "es.tta.example.name";
    private final static String EXTRA_NUMBER = "es.tta.example.number";
    private final static String EXTRA_LESSON = "es.tta.example.lesson";
    private final static String EXTRA_TEST = "es.tta.example.test";
    private final static String EXTRA_EXERCISE_ID = "es.tta.example.exerciseId";
    private final static String EXTRA_EXERCISE_WORDING = "es.tta.example.exerciseWording";
    private final static String EXTRA_NEXT_TEST = "es.tta.example.nextTestId";
    private final static String EXTRA_NEXT_EXERCISE = "es.tta.example.nextExerciseId";

    private final Bundle bundle;

    public Data(Bundle bundle) {
        if (bundle == null)
            bundle = new Bundle();
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void putUserId(int id) {
        bundle.putInt(EXTRA_USER, id);
    }

    public int getUserId() {
        return bundle.getInt(EXTRA_USER);
    }

    public void putAuthToken(String auth) {
        bundle.putString(EXTRA_AUTH, auth);
    }

    public String getAuthToken() {
        return bundle.getString(EXTRA_AUTH);
    }

    public void putUserName(String name) {
        bundle.putString(EXTRA_NAME, name);
    }

    public String getUserName() {
        return bundle.getString(EXTRA_NAME);
    }

    public void putLessonNumber(int lesson) {
        bundle.putInt(EXTRA_NUMBER, lesson);
    }

    public int getLessonNumber() {
        return bundle.getInt(EXTRA_NUMBER);
    }

    public void putLessonTitle(String title) {
        bundle.putString(EXTRA_LESSON, title);
    }

    public String getLessonTitle() {
        return bundle.getString(EXTRA_LESSON);
    }

    public void putNextTest( int id ) {
        bundle.putInt(EXTRA_NEXT_TEST, id);
    }

    public int getNextTest() {
        return bundle.getInt(EXTRA_NEXT_TEST);
    }

    public void putNextExercise( int id ) {
        bundle.putInt(EXTRA_NEXT_EXERCISE, id);
    }

    public int getNextExercise() {
        return bundle.getInt(EXTRA_NEXT_EXERCISE);
    }

    public void putTest(Test test) {
        try {
            JSONObject json = new JSONObject();
            json.put("wording", test.getWording());
            JSONArray array = new JSONArray();
            for(Test.Choice choice : test.getChoices() ) {
                JSONObject item = new JSONObject();
                item.put("id",choice.getId());
                item.put("wording",choice.getWording());
                item.put("correct",choice.isCorrect());
                item.put("advise",choice.getAdvise());
                item.put("mime",choice.getMime());
                array.put(item);
            }
            json.put("choices",array);
            bundle.putString(EXTRA_TEST, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Test getTest() {
        try {
            Test test = new Test();
            JSONObject json = new JSONObject(bundle.getString(EXTRA_TEST));
            test.setWording(json.getString("wording"));
            JSONArray array = json.getJSONArray("choices");
            for( int i = 0; i < array.length(); i++ ) {
                JSONObject item = array.getJSONObject(i);
                Test.Choice choice = new Test.Choice();
                choice.setId(item.getInt("id"));
                choice.setWording(item.getString("wording"));
                choice.setCorrect(item.getBoolean("correct"));
                choice.setAdvise(item.optString("advise", null));
                choice.setMime(item.optString("mime", null));
                test.getChoices().add(choice);
            }
            return test;
        } catch (JSONException e) {
            return null;
        }
    }

    public void putExercise(Exercise exercise) {
        bundle.putInt(EXTRA_EXERCISE_ID, exercise.getId());
        bundle.putString(EXTRA_EXERCISE_WORDING, exercise.getWording());
    }

    public Exercise getExercise() {
        Exercise exercise = new Exercise();
        exercise.setId(bundle.getInt(EXTRA_EXERCISE_ID));
        exercise.setWording(bundle.getString(EXTRA_EXERCISE_WORDING));
        return exercise;
    }
}