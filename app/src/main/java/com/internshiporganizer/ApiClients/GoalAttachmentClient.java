package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.GoalAttachment;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoalAttachmentClient extends BaseClient {
    private static final String goalAttachmentUrl = "goalAttachments/";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<GoalAttachment>> updatable;

    public GoalAttachmentClient(Context context, Updatable<List<GoalAttachment>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByGoal(long goalId) {
        String url = baseUrl + goalAttachmentUrl + "byGoal/" + goalId;
        get(url);
    }

    public void add(GoalAttachment goalAttachment) {
        String url = baseUrl + goalAttachmentUrl;
        add(url, goalAttachment);
    }

    private void get(String url) {
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<GoalAttachment> list = gson.fromJson(response.toString(), new TypeToken<List<GoalAttachment>>() {
                }.getType());

                updatable.update(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load goal attachments", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsArrayRequest);
    }

    private void add(String url, GoalAttachment newAttachment) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newAttachment));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                GoalAttachment attachment = gson.fromJson(response.toString(), new TypeToken<GoalAttachment>() {
                }.getType());
                ArrayList<GoalAttachment> arrayList = new ArrayList<>();
                arrayList.add(attachment);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot add attachment", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
