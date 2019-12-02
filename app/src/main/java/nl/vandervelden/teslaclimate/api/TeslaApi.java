package nl.vandervelden.teslaclimate.api;

import nl.vandervelden.teslaclimate.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TeslaApi {

    @POST("oauth/token")
    Call<AuthResponse> refreshToken(@Body AuthBody authBody);

    @POST("api/1/vehicles/{id}/command/auto_conditioning_start")
    Call<CommandResponse> turnOnClimate(@Path("id") Long id);
}
