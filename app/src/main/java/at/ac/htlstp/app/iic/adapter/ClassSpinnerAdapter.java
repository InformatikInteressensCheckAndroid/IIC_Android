package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import at.ac.htlstp.app.iic.model.UserClass;

/**
 * Created by alexnavratil on 05/12/15.
 */
public class ClassSpinnerAdapter extends ArrayAdapter<UserClass> {
    private List<UserClass> mClassList;

    public ClassSpinnerAdapter(Context context, List<UserClass> classList) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        this.mClassList = classList;
    }

    @Override
    public int getCount() {
        if (mClassList == null) {
            return 0;
        }
        return mClassList.size();
    }

    @Override
    public UserClass getItem(int position) {
        return mClassList.get(position);
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
        text1.setText(getItem(position).getClassname());

        return convertView;
    }
}
