package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.ui.activity.QuizResultActivity;

/**
 * Created by alexnavratil on 27/12/15.
 */
public class QuizResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy");
    private static Drawable okDrawable;
    private static Drawable nokDrawable;


    private List<QuizResult> dataList = new ArrayList<>();
    private Context context;

    public QuizResultAdapter(Context context, List<QuizResult> quizResultItemList) {
        this.context = context;

        okDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check_light_green_a700_36dp);
        nokDrawable = ContextCompat.getDrawable(context, R.drawable.ic_close_red_a700_36dp);

        dataList.addAll(quizResultItemList);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public QuizResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.quiz_result_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        vh.imageView = (ImageView) itemView.findViewById(R.id.icon);
        vh.passedView = (ImageView) itemView.findViewById(R.id.passedView);
        vh.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        vh.dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);

        return vh;
    }

    public void setDataList(List<QuizResult> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final QuizResult item = dataList.get(position);

        ViewHolder vh = (ViewHolder) holder;

        if (item.getQuiz() != null) {
            String shortcodeQuiz = "";
            if (!item.getQuiz().getName().isEmpty()) {
                shortcodeQuiz = item.getQuiz().getName().substring(0, 1).toUpperCase();
            }

            TextDrawable td = TextDrawable.builder(context).buildRound(shortcodeQuiz, ContextCompat.getColor(context, R.color.materialRed));

            vh.imageView.setImageDrawable(td);
            vh.titleTextView.setText(item.getQuiz().getName());
            vh.dateTextView.setText(sdf.format(item.getStartStamp()));

            if (item.isPassed()) {
                vh.passedView.setImageDrawable(okDrawable);
            } else {
                vh.passedView.setImageDrawable(nokDrawable);
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent quizResultIntent = new Intent(context, QuizResultActivity.class);
                    quizResultIntent.putExtra(QuizResultActivity.KEY_QUIZ_SESSION_ID, item.getSession_ID());

                    context.startActivity(quizResultIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView dateTextView;
        ImageView passedView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
