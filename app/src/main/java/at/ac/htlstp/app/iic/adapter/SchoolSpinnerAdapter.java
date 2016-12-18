package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import at.ac.htlstp.app.iic.model.School;

/**
 * Created by alexnavratil on 05/12/15.
 */
public class SchoolSpinnerAdapter extends ArrayAdapter<School> {
    private List<School> mSchoolList;

    public SchoolSpinnerAdapter(Context context, List<School> schoolList) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        this.mSchoolList = schoolList;
    }

    @Override
    public int getCount() {
        if (mSchoolList == null) {
            return 0;
        }
        return mSchoolList.size();
    }

    @Override
    public School getItem(int position) {
        return mSchoolList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(super.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        text1.setText(getItem(position).getSchoolname());

        return convertView;
    }
}
