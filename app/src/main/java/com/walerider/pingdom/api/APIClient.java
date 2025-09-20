package com.walerider.pingdom.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    //10.0.2.2
    private static final String BASE_URL = "https://185.185.143.72:443";
    private static Retrofit retrofit = null;
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB cache
    public static API getApi(Context context) throws NoSuchAlgorithmException, KeyManagementException {
        if(retrofit == null){
            Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Создаем SSL context с нашим TrustManager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Создаем socket factory
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Создаем клиент который игнорирует SSL ошибки
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .cache(cache)
                    .addInterceptor(logging)
                    .addInterceptor(new OfflineCacheInterceptor())
                    .addInterceptor(new RetryInterceptor(3))
                    .addNetworkInterceptor(new OnlineCacheInterceptor());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit.create(API.class);
    }
    private static class OfflineCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!isNetworkAvailable()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        }

        private boolean isNetworkAvailable() {
            // Реализуйте проверку сети

            return true;
        }
    }

    // Интерцептор для онлайн кэша
    private static class OnlineCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(5, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        }
    }
}
class RetryInterceptor implements Interceptor {
    private int maxRetries;

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException exception = null;

        for (int i = 0; i <= maxRetries; i++) {
            try {
                response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                exception = e;
                if (i == maxRetries) {
                    break;
                }
                try {
                    Thread.sleep(1000 * (i + 1)); // Экспоненциальная задержка
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted", ie);
                }
            }
        }

        if (exception != null) {
            throw exception;
        }

        if (response != null) {
            return response;
        }

        throw new IOException("Unknown error");
    }
}