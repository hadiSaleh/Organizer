package com.internshiporganizer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.EmployeeCheckable;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class EmployeeCheckableAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<EmployeeCheckable> objects;

    public EmployeeCheckableAdapter(Context context, ArrayList<EmployeeCheckable> employees) {
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
            view = lInflater.inflate(R.layout.list_item_employee_checkable, parent, false);
        }

        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(myCheckChangeList);
        checkBox.setTag(position);
        checkBox.setChecked(false);

        final ImageView photoIV = view.findViewById(R.id.imageView);
        photoIV.setImageResource(R.drawable.icon_user);
        final Employee p = getEmployee(position);
        String name = p.getFirstName() + " " + p.getLastName();
        ((TextView) view.findViewById(R.id.textEmployeeName)).setText(name);
        ((TextView) view.findViewById(R.id.textEmployeeOffice)).setText(p.getOffice().getName());

        return view;
    }

    // обработчик для чекбоксов
    private CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getEmployee((Integer) buttonView.getTag()).setChecked(isChecked);
        }
    };

    private EmployeeCheckable getEmployee(int position) {
        return ((EmployeeCheckable) getItem(position));
    }
}
