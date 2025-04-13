package com.svalero.psaa2.service;

import com.svalero.psaa2.utils.ErrorLogger;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Request;

import java.util.HashMap;
import java.util.Map;

public class ApiService {

    // Create instance per api and only one of each
    private static final Map<String, Retrofit> retrofitInstances = new HashMap<>();

    public static Retrofit getInstance(String token, String url){
        if (!retrofitInstances.containsKey(url)) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                // Add token in all requests
                                .header("Authorization", token)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        okhttp3.Response response =  chain.proceed(request);
                        // Log HTTP error
                        if (!response.isSuccessful()) {
                            ErrorLogger.log("HTTP Error: " + response.code() + " " + response.message());
                        }
                        return response;
                    })
                    .build();

            // La conversion con Gson me ha dado muchos problemas, con esto permite mayor flexibilidad
            // cuando serializa los datos a JSON
            // La solución real ha sido añadir en module-info.java la linea opens com.svalero.psaa2.domain to com.google.gson;
            // En esta version de Java es necesario si no Gson no puede acceder a las clases del dominio y serializarlas
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

            retrofitInstances.put(url, retrofit);
        }
        return retrofitInstances.get(url);
    }
}
