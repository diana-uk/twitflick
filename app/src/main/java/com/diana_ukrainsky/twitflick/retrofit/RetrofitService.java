package com.diana_ukrainsky.twitflick.retrofit;

import com.diana_ukrainsky.twitflick.utils.Constants;
import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public RetrofitService() {
        initializeRetrofit();
    }
    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OMDB_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson ()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
