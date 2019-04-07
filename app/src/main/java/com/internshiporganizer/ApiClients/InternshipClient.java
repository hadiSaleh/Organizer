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
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InternshipClient extends BaseClient {
    private static final String internshipsUrl = "internships";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<Internship>> updatable;

    public InternshipClient(Context context, Updatable<List<Internship>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void getAllByEmployee(long employeeId) {
        String url = baseUrl + internshipsUrl;
        get(url);
    }

    public void getAllByAdministrator(long employeeId) {
        String url = baseUrl + internshipsUrl;
        get(url);
    }

    public void create(Internship newInternship) {
        String url = baseUrl + internshipsUrl;
        create(url, newInternship);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Internship> list = gson.fromJson(response.toString(), new TypeToken<List<Internship>>() {
                }.getType());

                updatable.update(list);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load internships", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void create(String url, Internship newInternship) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newInternship));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Internship internship = gson.fromJson(response.toString(), new TypeToken<Internship>() {
                }.getType());
                ArrayList<Internship> arrayList = new ArrayList<>();
                arrayList.add(internship);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot create internship", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
