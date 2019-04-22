package com.internshiporganizer.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EmployeeAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<Employee> objects;

    public EmployeeAdapter(Context context, ArrayList<Employee> employees) {
        objects = employees;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item_employee, parent, false);
        }

        final ImageView photoIV = view.findViewById(R.id.imageView);
        photoIV.setImageResource(R.drawable.icon_user);
        final Employee p = getEmployee(position);
        String name = p.getFirstName() + " " + p.getLastName();
        ((TextView) view.findViewById(R.id.textEmployeeName)).setText(name);
        ((TextView) view.findViewById(R.id.textEmployeeOffice)).setText(p.getCity());

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final File localFile;
        try {
            localFile = File.createTempFile("img" + p.getId(), "jpg");

            mStorageRef.child("images/employees/" + p.getId() + "/image.jpg").getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            photoIV.setImageURI(Uri.fromFile(localFile));
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    private Employee getEmployee(int position) {
        return ((Employee) getItem(position));
    }
}
