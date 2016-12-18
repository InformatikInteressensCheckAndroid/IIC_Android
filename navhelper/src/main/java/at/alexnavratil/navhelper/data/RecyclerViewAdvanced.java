package at.alexnavratil.navhelper.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by alexnavratil on 30/01/16.
 */
public class RecyclerViewAdvanced extends android.support.v7.widget.RecyclerView {
    private View emptyView;
    private ProgressBar indeterminateProgressBar;
    private boolean loading = false;

    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            recheckEmptyState();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            recheckEmptyState();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            recheckEmptyState();
        }
    };

    public RecyclerViewAdvanced(Context context) {
        this(context, null, 0);
    }

    public RecyclerViewAdvanced(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewAdvanced(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;

        if (loading) {
            indeterminateProgressBar.setVisibility(View.VISIBLE);
        } else {
            indeterminateProgressBar.setVisibility(View.GONE);
        }

        recheckEmptyState(); //remove empty view on load
    }

    public ProgressBar getIndeterminateProgressBar() {
        return indeterminateProgressBar;
    }

    public void setIndeterminateProgressBar(ProgressBar indeterminateProgressBar) {
        this.indeterminateProgressBar = indeterminateProgressBar;
        this.indeterminateProgressBar.setIndeterminate(true);

        setLoading(loading);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        recheckEmptyState();
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        Adapter oldAdapter = getAdapter();
        oldAdapter.unregisterAdapterDataObserver(observer);

        super.swapAdapter(adapter, removeAndRecycleExistingViews);

        adapter.registerAdapterDataObserver(observer);
        recheckEmptyState();
    }

    private void recheckEmptyState() {
        if (emptyView != null) {
            //loading == false => don't conflict with progress bar
            if (loading == false && getAdapter() != null && getAdapter().getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }
}
