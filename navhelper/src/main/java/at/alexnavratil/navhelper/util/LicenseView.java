package at.alexnavratil.navhelper.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.alexnavratil.navhelper.R;

/**
 * Created by alexnavratil on 12/02/16.
 */
public class LicenseView extends RecyclerView {
    private List<LicenseItem> licenseList = new ArrayList<>();

    public LicenseView(Context context) {
        this(context, null, 0);
    }

    public LicenseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LicenseView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LicenseAdapter adapter = new LicenseAdapter(licenseList);
        super.setAdapter(adapter);
        super.setLayoutManager(new LinearLayoutManager(context));
    }

    public List<LicenseItem> getLicenseList() {
        return Collections.unmodifiableList(licenseList);
    }

    public void addLicense(LicenseItem item) {
        this.licenseList.add(item);
        getAdapter().notifyDataSetChanged();
    }

    public boolean removeLicense(LicenseItem item) {
        boolean ret = this.licenseList.remove(item);
        getAdapter().notifyDataSetChanged();
        return ret;
    }

    @Override
    public final void setAdapter(Adapter adapter) {
        throw new UnsupportedOperationException("don't call setAdapter. Just add LicenseItems using the method addLicense");
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        throw new UnsupportedOperationException("don't call swapAdapter. Just add LicenseItems using the method addLicense");
    }

    class LicenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<LicenseItem> licenseList;

        public LicenseAdapter(List<LicenseItem> licenseList) {
            this.licenseList = licenseList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.license_item_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);

            vh.libraryTitleText = (TextView) v.findViewById(R.id.library_title);
            vh.libraryDeveloperText = (TextView) v.findViewById(R.id.library_dev);
            vh.libraryDescriptionText = (TextView) v.findViewById(R.id.library_description);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final LicenseItem item = licenseList.get(position);

            ViewHolder vh = (ViewHolder) holder;

            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                vh.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                        getContext().startActivity(urlIntent);
                    }
                });
            }

            vh.libraryTitleText.setText(item.getLibraryName());
            vh.libraryDeveloperText.setText(String.format("by %s", item.getDeveloper()));
            vh.libraryDescriptionText.setText(item.getInformation());
        }

        @Override
        public int getItemCount() {
            return licenseList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView libraryTitleText;
            TextView libraryDeveloperText;
            TextView libraryDescriptionText;
            View itemView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }
}
