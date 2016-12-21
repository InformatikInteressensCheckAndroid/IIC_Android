package at.ac.htlstp.app.iic.adapter;

import android.content.Context;
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
import at.ac.htlstp.app.iic.model.Answer;
import at.ac.htlstp.app.iic.model.Question;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.ac.htlstp.app.iic.ui.fragment.QuestionGroupListFragment;

/**
 * Created by alexnavratil on 27/12/15.
 */
public class QuestionGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<QuestionGroup> dataList = new ArrayList<>();
    private QuestionGroupListFragment.OnQuestionGroupSelectedListener mListener;
    private Context context;

    public QuestionGroupAdapter(Context context, List<QuestionGroup> questionGroupList) {
        this.context = context;

        if (context instanceof QuestionGroupListFragment.OnQuestionGroupSelectedListener) {
            mListener = (QuestionGroupListFragment.OnQuestionGroupSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        dataList.addAll(questionGroupList);
        this.notifyDataSetChanged();
    }

    @Override
    public QuestionGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.question_group_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        vh.imageView = (ImageView) itemView.findViewById(R.id.icon);
        vh.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        vh.answerNumberView = (TextView) itemView.findViewById(R.id.answerNumberView);

        return vh;
    }

    public void setDataList(List<QuestionGroup> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final QuestionGroup item = dataList.get(position);

        ViewHolder vh = (ViewHolder) holder;

        String shortcodeQuiz = "";
        if (!item.getTitle().isEmpty()) {
            shortcodeQuiz = item.getTitle().substring(0, 1).toUpperCase();
        }

        TextDrawable td = TextDrawable.builder().buildRound(shortcodeQuiz, ContextCompat.getColor(context, R.color.materialRed));
                                            //context
        vh.imageView.setImageDrawable(td);
        vh.titleTextView.setText(item.getTitle());
        vh.answerNumberView.setText(String.format(context.getString(R.string.x_of_x_answered), getAnsweredQuestionsNumber(item), getNumberOfQuestions(item)));


        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuestionGroupSelected(item);
            }
        });
    }

    private int getNumberOfQuestions(QuestionGroup group) {
        return group.getQuestionList().size();
    }

    private int getAnsweredQuestionsNumber(QuestionGroup group) {
        int sum = 0;
        for (Question q : group.getQuestionList()) {
            for (Answer a : q.getAnswerList()) {
                if (a.getAnswerOfUser() != null) {
                    sum++;
                    break;
                }
            }
        }

        return sum;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView answerNumberView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
