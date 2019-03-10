package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;


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

    public void tryToLogin(AuthCredentials authCredentials) {
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

//        jsonObject = new JSONObject();
//        try {
//            jsonObject.put("email", "test@test.test");
//            jsonObject.put("passwordHash", "A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Employee employee = gson.fromJson(response.toString(), new TypeToken<Employee>() {
                }.getType());
                updatable.update(employee);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot auth", Toast.LENGTH_SHORT).show();
            }
        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                return headers;
//            }
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
        };

        queue.add(jsObjRequest);
    }
}
