package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class ClashOfClansApiService {

    private static Retrofit retrofit = null;

    public static Retrofit getInstance(){
        if(retrofit == null) {

            // La conversion con Gson me ha dado muchos problemas, con esto permite mayor flexibilidad
            // cuando serializa los datos a JSON
            // La solución real ha sido añadir en module-info.java la linea opens com.svalero.psaa2.domain to com.google.gson;
            // En esta version de Java es necesario si no Gson no puede acceder a las clases del dominio y serializarlas
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
