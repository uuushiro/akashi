package com.example.uuushiro.akashi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private static final int SYUKKIN_CODE = 11;
    private static final int TAIKIN_CODE = 12;
    private static final int CHOKKO_CODE = 21;
    private static final int CHOKKI_CODE = 22;
    private String res = "";
    private static final String COMPANY_ID = "stmn1234";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_syukkin)
    void clickButtonShukkin() {
        postRequest(SYUKKIN_CODE, getApiToken());
    }

    @OnClick(R.id.button_taikin)
    void clickButtonTaikin() {
        postRequest(TAIKIN_CODE, getApiToken());
    }

    @OnClick(R.id.button_chokko)
    void clickButtonChokko() {
        postRequest(CHOKKO_CODE, getApiToken());
    }

    @OnClick(R.id.button_chokki)
    void clickButtonChokki() {
        postRequest(CHOKKI_CODE, getApiToken());
    }

    private void getTest() {
        Request request = new Request.Builder()
                .url("http://weather.livedoor.com/forecast/webservice/json/v1?city=130010")
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failMessage();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                res = response.body().string();
                try {
                    JSONObject resJson = new JSONObject(res);
                    JSONArray weathers = resJson.getJSONArray("pinpointLocations");
                    JSONObject weather = weathers.getJSONObject(0);

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("onResponse", res);
                        }
                    });
                } catch(JSONException e) {
                    failMessage();
                    e.printStackTrace();
                }
            }
        });
    }

    private void postRequest(Integer type, String token) {
        RequestBody formBody = new FormBody.Builder()
                .add("type", String.valueOf(type))
                .build();

        Request request = new Request.Builder()
                .url("https://atnd.ak4.jp/api/cooperation/" + COMPANY_ID + "/stamps?token=" + token)
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("onFailure", res);
                        Toast.makeText(getActivity(), "失敗しました", Toast.LENGTH_LONG).show();
                    }
                });
                failMessage();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String message;
                res = response.body().string();
                try {
                    JSONObject resJson = new JSONObject(res);
                    String result = resJson.getString("success");
                    if (result.equals("true")) {
                        message = "打刻しました";
                    } else {
                        message = resJson.getJSONArray("errors").getJSONObject(0).getString("message");
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("onResponse", res);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch(JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "失敗しました。開発者にお問い合わせ下さい。", Toast.LENGTH_LONG).show();
                        }
                    });
                    failMessage();
                    e.printStackTrace();
                }
            }
        });
    }

    private void failMessage() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.d("failMessage", res);
            }
        });
    }

    private String getApiToken() {
        SharedPreferences data = getActivity().getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String apiToken = data.getString("ApiToken","" );
        return apiToken;
    }
}
