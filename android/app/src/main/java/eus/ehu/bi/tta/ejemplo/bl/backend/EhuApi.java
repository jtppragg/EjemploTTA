package eus.ehu.bi.tta.ejemplo.bl.backend;


import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface EhuApi {
    @GET("getStatus")
    Call<UserProfile> getUserProfile(@Query("dni") String login);

    @GET("getTest")
    Call<Test> getTest(@Query("id") int id);

    @GET("getExercise")
    Call<Exercise> getExercise(@Query("id") int id);

    @POST("postChoice")
    Call<ResponseBody> uploadChoice(@Body ChoiceRequest resp);

    @Multipart
    @POST("postExercise")
    Call<ResponseBody> uploadSolution(@Query("user") int userId, @Query("id") int exId, @Part MultipartBody.Part file);

    class ChoiceRequest {
        public final int userId, choiceId;

        public ChoiceRequest(int userId, int choiceId) {
            this.userId = userId;
            this.choiceId = choiceId;
        }
    }
}
