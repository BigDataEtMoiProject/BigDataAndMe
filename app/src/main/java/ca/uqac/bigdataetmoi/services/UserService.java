package ca.uqac.bigdataetmoi.service;

import ca.uqac.bigdataetmoi.dto.CoordinateDto;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.dto.PhotoDto;
import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @POST("users")
    Call<User> register(@Body UserAuthenticationDto userAuthenticationDto);

    @POST("coordinates")
    Call<User> sendCoordinate(@Body CoordinateDto coordinate);

    @POST("photo")
    Call<User> sendPhoto(@Body PhotoDto photo);

    @POST("messages")
    Call<User> sendMessages(@Body MessageDto[]  messageList);

    @GET("users/me")
    Call<User> getCurrentUserInformations();
}