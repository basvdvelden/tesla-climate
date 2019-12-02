package nl.vandervelden.teslaclimate;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import nl.vandervelden.teslaclimate.api.AuthBody;
import nl.vandervelden.teslaclimate.api.CommandResponse;
import nl.vandervelden.teslaclimate.api.TeslaApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TeslaDataSource {
    private static final String TAG = "TeslaDataSource";
    private static final String baseUrl = "https://owner-api.teslamotors.com/";
    private final SharedPreferences sharedPref;
    private TeslaApi api;

    public TeslaDataSource(final SharedPreferences sharedPreferences) {
        this.sharedPref = sharedPreferences;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request original = chain.request();
                        String accessToken = sharedPreferences.getString("accessToken", null);
                        Log.i(TAG, accessToken);
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + accessToken)
                                //.header("Accept", "application/vnd.yourapi.v1.full+json")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(httpClient)
                .build();

        api = retrofit.create(TeslaApi.class);

    }

    public void refreshToken() {
        try {
            String refreshToken = sharedPref.getString("refreshToken", null);

            Log.i(TAG, refreshToken);

            AuthBody body = new AuthBody(refreshToken);
            Call<AuthResponse> call = api.refreshToken(body);
            Response<AuthResponse> response = call.clone().execute();

            Log.d(TAG, String.format("response code: %d", response.code()));

            AuthResponse authResponse = response.body();
            if (response.code() > 199 && response.code() < 300) {
                Log.i(TAG, authResponse.getAccess_token());
                Log.i(TAG, authResponse.getRefresh_token());
                sharedPref.edit()
                        .putString("accessToken", authResponse.getAccess_token())
                        .putString("refreshToken", authResponse.getRefresh_token()).commit();
            } else if (response.code() == 502) {
                refreshToken();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO error: ", e);
        }
    }

    public int turnOnClimate() {
        try {
            Long id = 8886870316587915L;
//            Long id = 7417815502175756L; // TOM's auto id
            Call<CommandResponse> call = api.turnOnClimate(id);
            Log.d(TAG, call.request().url().toString());
            Response<CommandResponse> response = call.clone().execute();
            Log.d(TAG, "RESPONSE CODE: " + response.code());
            if (response.code() == 401 || response.code() == 403) {
                Log.i(TAG, response.message());
                refreshToken();
                return turnOnClimate();
            } else if (response.code() == 502) {
                return turnOnClimate();
            }
            return response.code();
        } catch (IOException e) {
            Log.e(TAG, "IO error: ", e);
            return 101;
        }
    }
}
