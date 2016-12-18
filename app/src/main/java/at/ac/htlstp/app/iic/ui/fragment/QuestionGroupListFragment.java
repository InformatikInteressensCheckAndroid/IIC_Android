package at.ac.htlstp.app.iic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.QuestionGroupAdapter;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.alexnavratil.navhelper.data.RecyclerViewAdvanced;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuestionGroupListFragment extends Fragment {
    @Bind(R.id.questionGroupRecycler)
    RecyclerViewAdvanced mQuestionGroupRecycler;

    @Bind(R.id.questionGroupListEmptyView)
    TextView emptyView;

    private OnQuestionGroupSelectedListener mQuestionGroupSelectedListener;

    private List<QuestionGroup> mQuestionGroupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_group_list_fragment, container, false);

        ButterKnife.bind(this, v);

        mQuestionGroupRecycler.setEmptyView(emptyView);

        renderQuestionGroupList();

        return v;
    }

    public void setQuestionGroupList(List<QuestionGroup> questionGroupList) {
        this.mQuestionGroupList = questionGroupList;
    }

    private void renderQuestionGroupList() {
        QuestionGroupAdapter adapter = new QuestionGroupAdapter(getContext(), mQuestionGroupList);
        mQuestionGroupRecycler.setAdapter(adapter);
        mQuestionGroupRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQuestionGroupSelectedListener) {
            mQuestionGroupSelectedListener = (OnQuestionGroupSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnQuestionGroupSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mQuestionGroupSelectedListener = null;
    }

    public interface OnQuestionGroupSelectedListener {
        void onQuestionGroupSelected(QuestionGroup group);
    }
}