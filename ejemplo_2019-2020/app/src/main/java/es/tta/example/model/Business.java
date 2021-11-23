package es.tta.example.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import es.tta.prof.comms.RestClient;

/**
 * Created by gorka on 19/10/15.
 */
public class Business {
    private final RestClient rest;

    public Business( RestClient rest ) {
        this.rest = rest;
    }

    public Status getStatus( String dni ) throws IOException, JSONException {
        Status status = new Status();
        JSONObject json = rest.getJson(String.format("getStatus?dni=%s", dni));
        status.setUserId(json.getInt("id"));
        status.setUserName(json.getString("user"));
        status.setLessonNumber(json.getInt("lessonNumber"));
        status.setLessonTitle(json.getString("lessonTitle"));
        return status;
    }

    public Test getTest( int id ) throws IOException, JSONException {
        JSONObject json = rest.getJson(String.format("getTest?id=%d", id));
        Test test = new Test();
        test.setWording(json.getString("wording"));
        JSONArray array = json.getJSONArray("choices");
        for( int i = 0; i < array.length(); i++ ) {
            JSONObject item = array.getJSONObject(i);
            Test.Choice choice = new Test.Choice();
            choice.setId(item.getInt("id"));
            choice.setWording(item.getString("answer"));
            choice.setCorrect(item.getBoolean("correct"));
            choice.setAdvise(item.getString("advise"));
            if( !item.isNull("resourceType") )
                choice.setMime(item.getJSONObject("resourceType").getString("mime"));
            test.getChoices().add(choice);
        }
        return test;
    }

    public Exercise getExercise( int id ) throws IOException, JSONException {
        JSONObject json = rest.getJson(String.format("getExercise?id=%d", id));
        Exercise exercise = new Exercise();
        exercise.setId(json.getInt("id"));
        exercise.setWording(json.getString("wording"));
        return exercise;
    }

    public void uploadSolution( int userId, int exerciseId, InputStream is, String fileName) throws IOException {
        rest.postFile(
                String.format("postExercise?user=%d&id=%d", userId, exerciseId),
                is, fileName
        );
    }

    public void uploadChoice( int userId, int choiceId ) throws JSONException, IOException {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("choiceId", choiceId);
        rest.postJson(json,"postChoice");
    }
}
