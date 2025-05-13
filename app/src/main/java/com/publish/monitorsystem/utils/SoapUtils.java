package com.publish.monitorsystem.utils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SoapUtils {
    private static final String TAG = "SoapUtils";
    private static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");
    private static SoapUtils instance;
    private final OkHttpClient client;

    private SoapUtils() {
        client = new OkHttpClient();
    }

    public static SoapUtils getInstance() {
        if (instance == null) {
            instance = new SoapUtils();
        }
        return instance;
    }

    public interface SoapCallback {
        void onSuccess(JSONObject result);
        void onFailure(String error);
    }

    public void callSoapService(String url, String soapAction, String soapEnvelope, final SoapCallback callback) {
        RequestBody body = RequestBody.create(soapEnvelope, XML);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("SOAPAction", soapAction)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Unexpected response " + response);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    // Parse SOAP response to JSON
                    JSONObject result = parseSoapResponse(responseBody);
                    callback.onSuccess(result);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing SOAP response", e);
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    private JSONObject parseSoapResponse(String soapResponse) throws JSONException {
        // Basic SOAP response parsing
        // You might need to adjust this based on your specific SOAP response format
        String jsonStr = soapResponse
                .replaceAll("<[^>]*>", "") // Remove XML tags
                .trim();
        return new JSONObject(jsonStr);
    }
} 