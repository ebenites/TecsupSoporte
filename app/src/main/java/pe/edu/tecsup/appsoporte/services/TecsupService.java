package pe.edu.tecsup.appsoporte.services;

import java.util.List;

import pe.edu.tecsup.appsoporte.models.Incident;
import pe.edu.tecsup.appsoporte.models.User;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ebenites on 05/08/2016.
 */
public interface TecsupService {

    @FormUrlEncoded
    @POST("api/jwt/hacked_access_token")
    Call<User> authentihacked(@Field("username") String username, @Field("id_token") String id_token, @Field("instanceid") String instanceid,
                              @Field("app") String app, @Field("deviceid") String deviceid, @Field("manufacturer") String manufacturer, @Field("model") String model, @Field("device") String device, @Field("kernel") String kernel, @Field("version") String version, @Field("sdk") Integer sdk);

    @FormUrlEncoded
    @POST("api/jwt/google_access_token")
    Call<User> authenticate(@Field("id_token") String id_token, @Field("instanceid") String instanceid,
                            @Field("app") String app, @Field("deviceid") String deviceid, @Field("manufacturer") String manufacturer, @Field("model") String model, @Field("device") String device, @Field("kernel") String kernel, @Field("version") String version, @Field("sdk") Integer sdk);

//    @GET("/api/jwt/refresh_token")
//    Call<User> refreshToken(@Query("token") String token);

    @DELETE("api/jwt/destroy_token")
    Call<String> logout(@Query("token") String token);

    @GET("api/user/profile")
    Call<User> profile();

    @GET("api/teacher/incident/{status}")
    Call<List<Incident>> getIncidents(@Path("status") String status);

    @PATCH("api/teacher/incident/{id}")
    Call<Incident> updateIncident(@Path("id") Integer id, @Query("status") String status);

}
