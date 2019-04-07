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
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.InternshipParticipant;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;

import java.util.List;

public class InternshipParticipantClient extends BaseClient {
    private static final String internshipParticipantsUrl = "internshipParticipants";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<InternshipParticipant>> updatable;

    public InternshipParticipantClient(Context context, Updatable<List<InternshipParticipant>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByInternship(long internshipId) {
        String url = baseUrl + internshipParticipantsUrl;
        get(url);
    }

    public void add(InternshipParticipant internshipParticipant) {
        String url = baseUrl + internshipParticipantsUrl;

        add(url, internshipParticipant);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<InternshipParticipant> list = gson.fromJson(response.toString(), new TypeToken<List<InternshipParticipant>>() {
                }.getType());

                updatable.update(list);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load internship participant", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void add(String url, InternshipParticipant internshipParticipant) {

    }
}
