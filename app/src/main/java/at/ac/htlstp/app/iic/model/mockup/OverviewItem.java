package at.ac.htlstp.app.iic.model.mockup;

import android.content.Context;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.ac.htlstp.app.iic.R;

/**
 * Created by alexnavratil on 27/12/15.
 */
public class OverviewItem {
    public final static int ACTION_START_QUIZ = 1;
    public final static int ACTION_VIEW_RESULTS = 2;
    public final static int ACTION_SHOW_INTRODUCTION = 3;
    public final static int ACTION_SHOW_ACCOUNT_SETTINGS = 4;

    @DrawableRes
    private int imageResId;
    private String title;
    private int action;

    public OverviewItem(int imageResId, String title, int action) {
        this.imageResId = imageResId;
        this.title = title;
        this.action = action;
    }

    public static List<OverviewItem> getOverviewItemList(Context context) {
        final List<OverviewItem> overviewItemList = new ArrayList<>();
        overviewItemList.add(new OverviewItem(R.drawable.ic_help_outline, context.getString(R.string.start_a_quiz).toUpperCase(), ACTION_START_QUIZ));
        overviewItemList.add(new OverviewItem(R.drawable.ic_equalizer_white_48dp, context.getString(R.string.show_quiz_results).toUpperCase(), ACTION_VIEW_RESULTS));
        overviewItemList.add(new OverviewItem(R.drawable.ic_account_circle_white_48dp, context.getString(R.string.account_settings).toUpperCase(), ACTION_SHOW_ACCOUNT_SETTINGS));
        //overviewItemList.add(new OverviewItem(R.drawable.ic_info_outline_white_48dp, context.getString(R.string.show_introduction).toUpperCase(), ACTION_SHOW_INTRODUCTION));

        return Collections.unmodifiableList(overviewItemList);
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
