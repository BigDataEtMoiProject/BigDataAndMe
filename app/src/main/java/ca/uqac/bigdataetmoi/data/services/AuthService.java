package ca.uqac.bigdataetmoi.data.services;

import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/login")
    Call<User> login();
}
