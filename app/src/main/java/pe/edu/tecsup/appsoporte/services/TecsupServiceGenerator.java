package pe.edu.tecsup.appsoporte.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.activities.LoginActivity;
import pe.edu.tecsup.appsoporte.activities.MainActivity;
import pe.edu.tecsup.appsoporte.models.APIError;
import pe.edu.tecsup.appsoporte.util.CurrentActivityListener;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ebenites on 07/01/2017.
 * Best Practice: TecsupServiceGenerator https://futurestud.io/blog/retrofit-getting-started-and-android-client#servicegenerator
 */
public final class TecsupServiceGenerator {

//    public static final String API_BASE_URL = "https://apidev.tecsup.edu.pe/tecsup-api/";
//    public static final String API_BASE_URL = "https://qas.tecsup.edu.pe/tecsup-api/";
    public static final String API_BASE_URL = "https://api.tecsup.edu.pe/tecsup-api/";
//    public static final String API_BASE_URL = "http://10.0.2.2:8080/";

    public static final String PHOTO_URL = API_BASE_URL + "/api/user/picture/";

    private static final String TAG = TecsupServiceGenerator.class.getSimpleName();

    private static Retrofit retrofit;

    private TecsupServiceGenerator() {
    }

    public static <S> S createService(final Class<S> serviceClass) {

        if(retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Set Timeout
            httpClient.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);

            // Retrofit Debug: https://futurestud.io/blog/retrofit-2-log-requests-and-responses
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            // Retrofit Token: https://futurestud.io/tutorials/retrofit-token-authentication-on-android
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    try {

                        Request originalRequest = chain.request();

                        // Load Token from SharedPreferences (firsttime is null)
                        String token = PreferencesManager.getInstance().get(PreferencesManager.PREF_TOKEN);
                        Log.d(TAG, "Loaded Token: " + token);

                        if(token == null){
                            // Firsttime assuming login
                            return chain.proceed(originalRequest);
                        }

                        // Request customization: add request headers
                        Request modifiedRequest = originalRequest.newBuilder()
                                .header("Authorization", token)
                                .build();

                        Response response = chain.proceed(modifiedRequest); // Call request with token

                        // If 'token_expired' -> refresh token
                        if (response.code() == 401) {

                            Log.d(TAG, "Response " + response.code() + ": token_expired, refreshing token...");

//                            // https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests
//                            Retrofit retrofit =  new Retrofit.Builder()
//                                    .baseUrl(TecsupService.API_BASE_URL)
//                                    .addConverterFactory(GsonConverterFactory.create())
//                                    .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
//                                    .build();
//                            retrofit2.Response<User> tokenResponse = retrofit.create(TecsupService.class).refreshToken(token).execute();
//
//                            if (tokenResponse.isSuccessful()) {
//
//                                User newtoken = tokenResponse.body();
//                                Log.d(TAG, "Refreshed Token: " + newtoken);
//
//                                // Save new token in SharedPreferences
//                                PreferencesManager.getInstance(currentActivity).set(PreferencesManager.PREF_TOKEN, newtoken.getToken());
//
//                                modifiedRequest = originalRequest.newBuilder()
//                                        .header("Authorization", newtoken.getToken())
//                                        .build();
//
//                                response = chain.proceed(modifiedRequest);  // Call request with refreshed token
//
//                            } else {
//
//                                APIError error = TecsupServiceGenerator.parseError(tokenResponse);
//                                Log.e(TAG, "Error refreshing: " + error);

                                // error refresing -> Logout ...
                                MainActivity.removeSession();

                                Activity currentActivity = CurrentActivityListener.getCurrentActivity();

                                Bundle bundle = new Bundle();
                                bundle.putString(LoginActivity.ARG_MESSAGE, currentActivity.getString(R.string.expired_sesison_error));
                                currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class).putExtras(bundle));

//                            }

                        } else {
                            Log.d(TAG, "Response " + response.code() + ": OK");
                        }

                        return response;

                    }catch (Exception e){
                        Log.e(TAG, e.toString());
                        Crashlytics.logException(e);
                        throw e;
                    }

                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(TecsupServiceGenerator.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()).build();
        }

        return retrofit.create(serviceClass);
    }

    // Best Practice APIError: https://futurestud.io/blog/retrofit-2-simple-error-handling
    public static APIError parseError(retrofit2.Response<?> response) {
        if(response.code() == 404)
            return new APIError("Servicio no disponible");
        Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
        try {
            return converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError("Error en el servicio");
        }
    }

}
