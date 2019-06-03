package ca.uqac.bigdataetmoi.repositories;

import android.content.Context;

import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;

public class UserRepository {

    public static Call<User> getUserFromApi(Context context) {
        return new HttpClient<UserService>(context).create(UserService.class).getCurrentUserInformations();
    }
}
