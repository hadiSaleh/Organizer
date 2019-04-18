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
import com.internshiporganizer.Entities.InternshipAttachment;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InternshipAttachmentClient extends BaseClient {
    private static final String internshipAttachmentUrl = "internshipAttachments/";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<InternshipAttachment>> updatable;

    public InternshipAttachmentClient(Context context, Updatable<List<InternshipAttachment>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByInternship(long internshipId) {
        String url = baseUrl + internshipAttachmentUrl + "/byInternship/" + internshipId;
        get(url);
    }

    public void add(InternshipAttachment internshipAttachment) {
        String url = baseUrl + internshipAttachmentUrl;
        add(url, internshipAttachment);
    }

    private void get(String url) {
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<InternshipAttachment> list = gson.fromJson(response.toString(), new TypeToken<List<InternshipAttachment>>() {
                }.getType());

                updatable.update(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load internship attachments", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsArrayRequest);
    }

    private void add(String url, InternshipAttachment newAttachment) {
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
                InternshipAttachment attachment = gson.fromJson(response.toString(), new TypeToken<InternshipAttachment>() {
                }.getType());
                ArrayList<InternshipAttachment> arrayList = new ArrayList<>();
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
