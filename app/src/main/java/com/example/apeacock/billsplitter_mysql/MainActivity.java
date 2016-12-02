package com.example.apeacock.billsplitter_mysql;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = this.getSharedPreferences(App.SHARED_PREF_KEY, this.MODE_PRIVATE);
        String token = sharedPref.getString("token", "DNE");
        if (token.equals("DNE")) {
            Log.d("LOGIN ATTEMPT", "Token not found in shared preferences");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            attemptSignIn(token);
        }
    }

    public void attemptSignIn(String token) {
        String url = "http://10.0.2.2:8000/login/?token=" + token;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest tokenLoginRequest = new JsonObjectRequest(
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("Error Code")) {
                                String description = response.getString("error_description");
                                Integer code = response.getInt("error_code");
                                Log.d("Error", "CODE " + code + " - " + description);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                            else if (response.has("id")){
                                Integer id = response.getInt("id");
                                Log.d("User ID", Integer.toString(id));
                                finishSignIn(id);
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                }
        );
        requestQueue.add(tokenLoginRequest);
    }

    public void finishSignIn(Integer id) {
        sharedPref.edit().putString("id", Integer.toString(id));
        startActivity(new Intent(this, GroupSelectionActivity.class));
        finish();
    }
}
