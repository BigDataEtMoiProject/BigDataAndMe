package ca.uqac.bigdataetmoi.services;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    BasicAuthInterceptor(String username, String password) {
        credentials = Credentials.basic(username, password);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .header("Authorization", credentials)
                .build();

        return chain.proceed(request);
    }
}
