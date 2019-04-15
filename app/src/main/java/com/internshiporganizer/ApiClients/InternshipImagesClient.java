package com.internshiporganizer.ApiClients;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class InternshipImagesClient extends BaseClient {
    private static final String internshipsUrl = "internships/";
    private RequestQueue queue;
    private Context context;

    public InternshipImagesClient(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public void updateImageCount(long internshipId, int imageCount) {
        String url = baseUrl + internshipsUrl + internshipId + "/updateImageCount/" + imageCount;
        update(url);
    }

    private void update(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, null, null);

        queue.add(stringRequest);
    }
}
