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
import com.internshiporganizer.Fragments.InternshipsFragment;
import com.internshiporganizer.Fragments.Updatable;

import org.json.JSONArray;

import java.util.List;

public class InternshipClient extends BaseClient {
    private static final String internshipsUrl = "/internships";
    private RequestQueue queue;
    private Context context;
    private Updatable<Internship> fragment;

    public InternshipClient(Context context, Updatable<Internship> fragment) {
        this.context = context;
        this.fragment = fragment;
        queue = Volley.newRequestQueue(context);
    }

    public void GetAllForEmployee(long employeeId) {
        String url = baseUrl + internshipsUrl;
        GetAll(employeeId, url);
    }

    public void GetAllForAdministrator(long employeeId) {
        String url = baseUrl + internshipsUrl;
        GetAll(employeeId, url);
    }

    private void GetAll(long employeeId, String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Internship> list = gson.fromJson(response.toString(), new TypeToken<List<Internship>>() {
                }.getType());
                fragment.updateList(list);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load internships", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
