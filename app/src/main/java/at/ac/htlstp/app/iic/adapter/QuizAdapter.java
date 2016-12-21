package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.ui.activity.QuizPreviewActivity;

/**
 * Created by alexnavratil on 27/12/15.
 */
public class QuizAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Quiz> dataList = new ArrayList<>();
    private Context context;

    public QuizAdapter(Context context, List<Quiz> quizItemList) {
        this.context = context;

        dataList.addAll(quizItemList);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public QuizAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.quiz_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        vh.imageView = (ImageView) itemView.findViewById(R.id.icon);
        vh.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        vh.shortDescriptionTextView = (TextView) itemView.findViewById(R.id.shortDescriptionTextView);

        return vh;
    }

    public void setDataList(List<Quiz> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Quiz item = dataList.get(position);

        ViewHolder vh = (ViewHolder) holder;

        String shortcodeQuiz = "";
        if (!item.getName().isEmpty()) {
            shortcodeQuiz = item.getName().substring(0, 1).toUpperCase();
        }

        TextDrawable td = TextDrawable.builder().buildRound(shortcodeQuiz, ContextCompat.getColor(context, R.color.materialRed));
                                            //context
        vh.imageView.setImageDrawable(td);
        vh.titleTextView.setText(item.getName());

        if (item.getShortDescription().isEmpty()) {
            vh.shortDescriptionTextView.setVisibility(View.GONE);
        } else {
            vh.shortDescriptionTextView.setVisibility(View.VISIBLE);
            vh.shortDescriptionTextView.setText(item.getShortDescription());
        }

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizIntent = new Intent(context, QuizPreviewActivity.class);
                quizIntent.putExtra(QuizPreviewActivity.KEY_QUIZ_ID, item.getQuiz_ID());
                context.startActivity(quizIntent);
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
        TextView shortDescriptionTextView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
