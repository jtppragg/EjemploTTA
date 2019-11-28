package eus.ehu.bi.tta.ejemplo.bl.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EhuBackend implements Backend {
    private static final String URL = "http://u017633.ehu.eus:28080/ServidorTta/rest/tta/";
    private EhuApi api;
    private String login;

    @Override
    public void setCredentials(String login, String passwd) {
        this.login = login;

        Gson gson = new GsonBuilder()
            //.setLenient()
            .create();

        OkHttpClient authClient = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", Credentials.basic(login, passwd));
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(authClient)
            .build();

        api = retrofit.create(EhuApi.class);
    }

    @Override
    public UserProfile getUserProfile() throws IOException {
        return api.getUserProfile(login).execute().body();
    }

    @Override
    public Test getTest(int id) throws IOException {
        return api.getTest(id).execute().body();
    }

    @Override
    public Exercise getExercise(int id) throws IOException {
        return api.getExercise(id).execute().body();
    }

    @Override
    public void uploadChoice(int userId, int choiceId) throws IOException {
        api.uploadChoice(new EhuApi.ChoiceRequest(userId, choiceId)).execute();
    }

    @Override
    public void uploadSolution(int userId, int exId, InputStream is, String name) throws IOException {
        byte[] data = new byte[is.available()];
        is.read(data);
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"),data);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", name, file);
        api.uploadSolution(userId, exId, part).execute();
    }
}
