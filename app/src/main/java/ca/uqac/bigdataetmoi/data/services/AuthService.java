package ca.uqac.bigdataetmoi.data.services;

import ca.uqac.bigdataetmoi.dto.UserLoginDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @POST("login")
    Call<User> login(@Body UserLoginDto userLoginDto);
}
