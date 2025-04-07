package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.env.ApiKey;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Request;

public class ClashOfClansApiService {

    private static Retrofit retrofit = null;

    public static Retrofit getInstance(){
        if(retrofit == null) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", ApiKey.TOKEN)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .build();

            // La conversion con Gson me ha dado muchos problemas, con esto permite mayor flexibilidad
            // cuando serializa los datos a JSON
            // La solución real ha sido añadir en module-info.java la linea opens com.svalero.psaa2.domain to com.google.gson;
            // En esta version de Java es necesario si no Gson no puede acceder a las clases del dominio y serializarlas
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
