package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.RequestAttachment;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestAttachmentClient extends BaseClient {
    private static final String requestAttachmentUrl = "requestAttachments/";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<RequestAttachment>> updatable;

    public RequestAttachmentClient(Context context, Updatable<List<RequestAttachment>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByRequest(long requestId) {
        String url = baseUrl + requestAttachmentUrl + "byRequest/" + requestId;
        get(url);
    }

    public void add(RequestAttachment requestAttachment) {
        String url = baseUrl + requestAttachmentUrl;
        add(url, requestAttachment);
    }

    private void get(String url) {
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<RequestAttachment> list = gson.fromJson(response.toString(), new TypeToken<List<RequestAttachment>>() {
                }.getType());

                updatable.update(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load request attachments", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsArrayRequest);
    }

    private void add(String url, RequestAttachment newAttachment) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newAttachment));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                RequestAttachment attachment = gson.fromJson(response.toString(), new TypeToken<RequestAttachment>() {
                }.getType());
                ArrayList<RequestAttachment> arrayList = new ArrayList<>();
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

