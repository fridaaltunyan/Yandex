package com.example.location;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yandex.mapkit.geometry.Point;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPlaces {
    private OkHttpClient client;
    private Point point;
    private Context context;
    private InfoPlace infoPlace;
    MainActivity activity;
    public GetPlaces(Point point) {
        this.point = point;
    }

    void request(String url, Callback callback) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://search-maps.yandex.ru/v1/" +
                        "?apikey=fd454f51-a1fd-4586-9952-05cf16ffdfd6"
                        + "&text = " + point.getLatitude() + point.getLongitude()
                        + "&lang = en_RU " + "&results = 1")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String myResponse = response.body().string();
                    infoPlace.setLocation(myResponse);
                    Log.e("ufff",infoPlace.getLocation());
                  // throw new IOException("Unexpected code " + response);
                    activity.runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            JSONObject obj =jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("properties");

//                            infoPlace.setName(jsonObject.getJSONObject("properties").getString("name"));
//                            infoPlace.setLocation(jsonObject.getJSONObject("properties").getString("description"));
                            Log.d("TAG",response.body().string());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    Toast.makeText(context, "Connection failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
