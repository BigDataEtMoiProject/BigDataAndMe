package ca.uqac.bigdataetmoi.data.services;

import ca.uqac.bigdataetmoi.BuildConfig;
import ca.uqac.bigdataetmoi.utils.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient<T> {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor(Constants.USER_EMAIL, Constants.USER_PASSWORD))
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public T create(Class<T> service) {
        return retrofit.create(service);
    }
}
