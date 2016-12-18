package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.model.Language;

/**
 * Created by alexnavratil on 29/12/15.
 */
public class LanguageAdapter extends ArrayAdapter<Language> {
    private List<Language> mLanguageList = new ArrayList<>();

    public LanguageAdapter(Context context, List<Language> languageList) {
        super(context, android.R.layout.simple_list_item_1);
        this.mLanguageList.addAll(languageList);
    }

    @Override
    public int getCount() {
        return mLanguageList.size();
    }

    @Override
    public Language getItem(int position) {
        return mLanguageList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Language lang = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        text1.setText(lang.getInLanguage());

        return convertView;
    }

    public void setLanguageList(List<Language> languageList) {
        mLanguageList.clear();
        mLanguageList.addAll(languageList);
        notifyDataSetChanged();
    }
}
