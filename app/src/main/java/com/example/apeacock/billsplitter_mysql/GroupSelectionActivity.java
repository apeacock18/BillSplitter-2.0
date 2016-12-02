package com.example.apeacock.billsplitter_mysql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GroupSelectionActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences(App.SHARED_PREF_KEY, this.MODE_PRIVATE);
        final String token = pref.getString("token", "DNE");
        final String user_id = pref.getString("id", "DNE");

        updateGroupInfo(token, user_id);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Token: " + token, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    void updateGroupInfo(final String token, final String user_id) {
        String url = "http://10.0.2.2:8000/group/info/";
        url += "?user_id=" + user_id;
        url += "&token=" + token;

        RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());

        Log.d("Id, Token", user_id + ", " + token);

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Group JSON info", response.toString());
                        TextView text = (TextView) findViewById(R.id.groupInfo);
                        String outString = "";

                        try {
                            if (response.getJSONObject(0).has("Error Code")) {
                                String error = "Undefined error";
                                try {
                                    error = response.getJSONObject(0).getString("Description");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(App.getAppContext(), error, Toast.LENGTH_LONG).show();
                            } else {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject data = response.getJSONObject(i);
                                    Integer id = data.getInt("id");
                                    String name = data.getString("name");
                                    outString += "Group name: " + name + ", ID: " + id + "\n";
                                }
                                text.setText(outString);
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
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                pref.edit().remove("token").apply();
                pref.edit().remove("id").apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
