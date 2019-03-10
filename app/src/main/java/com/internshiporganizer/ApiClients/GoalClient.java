package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;

import java.util.List;

public class GoalClient extends BaseClient {
    private static final String goalsUrl = "/goals";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<Goal>> updatable;

    public GoalClient(Context context, Updatable<List<Goal>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByEmployeeAndInternship(long employeeId, long internshipId) {
        String url = baseUrl + goalsUrl;
        get(url);
    }

    public void getById(long id){
        String url = baseUrl + goalsUrl;
        get(url);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Goal> list = gson.fromJson(response.toString(), new TypeToken<List<Goal>>() {
                }.getType());
                updatable.update(list);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load goals", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
