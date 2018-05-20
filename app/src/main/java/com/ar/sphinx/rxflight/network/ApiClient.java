package com.ar.sphinx.rxflight.network;

import com.ar.sphinx.rxflight.app.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

	private static final String TAG = "ApiClient";
	private static int TIMEOUT = 60;
	private static Retrofit retrofit;
	private static OkHttpClient okHttpClient;

	private static void initOkhttpClient(){

		OkHttpClient.Builder okhttpClientBuild = new OkHttpClient.Builder()
				.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT,TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT,TimeUnit.SECONDS);

		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		okhttpClientBuild.addInterceptor(httpLoggingInterceptor);

		okhttpClientBuild.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request original = chain.request();
				Request.Builder builder = original.newBuilder()
						.addHeader("Accept", "application/json")
						.addHeader("Request-Type", "Android")
						.addHeader("Content-Type", "application/json");

				Request request = builder.build();
				return chain.proceed(request);
			}
		});
		okHttpClient = okhttpClientBuild.build();
	}

	public static Retrofit getClient(){
		if(okHttpClient == null){
			initOkhttpClient();
		}
		if(retrofit == null){
			retrofit = new Retrofit.Builder()
					.baseUrl(Constants.BASE_URL)
					.client(okHttpClient)
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}

}
