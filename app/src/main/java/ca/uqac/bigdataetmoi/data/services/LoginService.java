package ca.uqac.bigdataetmoi.data.services;

import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("login")
    Call<User> login(@Body UserAuthenticationDto userAuthenticationDto);
}
