package com.example.uuushiro.akashi;

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
        postTest(SYUKKIN_CODE, "token");
    }

    @OnClick(R.id.button_taikin)
    void clickButtonTaikin() {
        postTest(TAIKIN_CODE, "token");
    }

    @OnClick(R.id.button_chokko)
    void clickButtonChokko() {
        postTest(CHOKKO_CODE, "token");
    }

    @OnClick(R.id.button_chokki)
    void clickButtonChokki() {
        postTest(CHOKKI_CODE, "token");
    }

    private void getTest() {
        Request request = new Request.Builder()
                .url("http://weather.livedoor.com/forecast/webservice/json/v1?city=130010")     // 130010->東京
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
                    JSONArray weathers = resJson.getJSONArray("pinpointLocations");     // 例として "pinpointLocations" を取り出す
                    JSONObject weather = weathers.getJSONObject(0);                     // 2番目のオブジェクトにアクセスしたい場合は"1"
                    String description = weather.getString("name");                     // 例として "name" を取り出す

                    // UI反映
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

    private void postTest(Integer type, String token) {
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
                res = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("onResponse", res);
                        Toast.makeText(getActivity(), "打刻しました", Toast.LENGTH_LONG).show();
                    }
                });
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
}
