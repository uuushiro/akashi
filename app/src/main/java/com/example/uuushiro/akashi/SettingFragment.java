package com.example.uuushiro.akashi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.button_register)
    void registerApiToken() {
        EditText editText = (EditText) view.findViewById(R.id.editText);
        String str = editText.getText().toString();

        SharedPreferences data = getActivity().getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("ApiToken", str);
        editor.apply();
        Toast.makeText(getActivity(), "登録しました", Toast.LENGTH_LONG).show();
    }
}
