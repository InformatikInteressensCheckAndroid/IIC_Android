package at.ac.htlstp.app.iic.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.OverviewItemAdapter;
import at.ac.htlstp.app.iic.model.mockup.OverviewItem;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

public class OverviewFragment extends IICFragment {
    @Bind(R.id.overview_recycler)
    RecyclerView overviewRecycler;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, contentView);

        List<OverviewItem> mOverviewItemList = OverviewItem.getOverviewItemList(getContext());

        super.title = getContext().getString(R.string.title_overview);
        super.action_key = MainActivity.FRAGMENT_KEY_OVERVIEW;

        OverviewItemAdapter adapter = new OverviewItemAdapter(this.getContext(), mOverviewItemList);
        overviewRecycler.setAdapter(adapter);
        overviewRecycler.setLayoutManager(new LinearLayoutManager(this.getContext())/* {
        DISABLED, because it doesn't work right on smaller screens (content get's cut off)
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        }*/);

        return contentView;
    }
}
