package ca.uqac.bigdataetmoi.service;

import android.content.Context;
import android.support.annotation.NonNull;

import ca.uqac.bigdataetmoi.BuildConfig;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient<T> {

    private Retrofit retrofit;

    public HttpClient(@NonNull Context context) {
        Context context1 = context.getApplicationContext();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new BasicAuthInterceptor(
                        Prefs.getString(context1, Constants.SHARED_PREFS, Constants.USER_EMAIL),
                        Prefs.getString(context1, Constants.SHARED_PREFS, Constants.USER_PASSWORD)))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public T create(Class<T> service) {
        return retrofit.create(service);
    }
}
