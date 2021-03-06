package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.AuthCredentials;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Updatable;

import org.json.JSONException;
import org.json.JSONObject;


public class AuthCredentialsClient extends BaseClient {
    private static final String authUrl = "auth";
    private RequestQueue queue;
    private Context context;
    private Updatable<Employee> updatable;

    public AuthCredentialsClient(Context context, Updatable<Employee> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void tryLogin(AuthCredentials authCredentials) {
        String url = baseUrl + authUrl;
        login(url, authCredentials);
    }

    private void login(String url, AuthCredentials authCredentials) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(authCredentials));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Employee employee = gson.fromJson(response.toString(), new TypeToken<Employee>() {
                }.getType());

                if (updatable != null) {
                    updatable.update(employee);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot auth", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
