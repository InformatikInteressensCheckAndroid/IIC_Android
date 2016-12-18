package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.mockup.OverviewItem;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import at.ac.htlstp.app.iic.ui.fragment.IICFragment;

/**
 * Created by alexnavratil on 27/12/15.
 */
public class OverviewItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OverviewItem> dataList = new ArrayList<>();
    private IICFragment.OnFragmentInteractionListener mListener;
    private Context context;

    public OverviewItemAdapter(Context context, List<OverviewItem> overviewItemList) {
        this.context = context;

        if (context instanceof IICFragment.OnFragmentInteractionListener) {
            mListener = (IICFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        dataList.addAll(overviewItemList);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public OverviewItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.overview_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        vh.imageView = (ImageView) itemView.findViewById(R.id.icon);
        vh.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OverviewItem item = dataList.get(position);

        ViewHolder vh = (ViewHolder) holder;
        vh.imageView.setImageResource(item.getImageResId());
        vh.titleTextView.setText(item.getTitle());

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (item.getAction()) {
                    case OverviewItem.ACTION_START_QUIZ:
                        mListener.onFragmentInteraction(MainActivity.FRAGMENT_KEY_QUIZ_LIST);
                        break;
                    case OverviewItem.ACTION_VIEW_RESULTS:
                        mListener.onFragmentInteraction(MainActivity.FRAGMENT_KEY_QUIZ_RESULT_LIST);
                        break;
                    case OverviewItem.ACTION_SHOW_ACCOUNT_SETTINGS:
                        mListener.onFragmentInteraction(MainActivity.FRAGMENT_KEY_ACCOUNT_SETTINGS);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
