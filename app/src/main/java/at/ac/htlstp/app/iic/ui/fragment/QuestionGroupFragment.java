package at.ac.htlstp.app.iic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.ac.htlstp.app.iic.ui.activity.QuizActivity;
import at.alexnavratil.navhelper.web.ReadabilityWebView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuestionGroupFragment extends Fragment {
    @Bind(R.id.questionGroupTitle)
    TextView mQuestionGroupTitle;

    @Bind(R.id.questionGroupDescription)
    ReadabilityWebView mQuestionGroupDescription;

    @Bind(R.id.btnBeginQuestions)
    Button mBeginQuestionsButton;

    private OnBeginQuestionGroupListener mBeginQuestionGroupListener;

    private QuestionGroup mQuestionGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_group_fragment, container, false);

        ButterKnife.bind(this, v);

        mBeginQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeginQuestionGroupListener.onBeginQuestionGroup(mQuestionGroup);
            }
        });

        mQuestionGroupDescription.enableFullscreenSupport((QuizActivity) getActivity());

        renderQuestionGroup();

        return v;
    }

    public void setQuestionGroup(QuestionGroup questionGroup) {
        this.mQuestionGroup = questionGroup;
    }

    private void renderQuestionGroup() {
        mQuestionGroupTitle.setText(mQuestionGroup.getTitle());

        if (mQuestionGroup.getDescription().isEmpty()) {
            mQuestionGroupDescription.loadData(String.format("<div>%s</div>", getContext().getString(R.string.no_further_information_available)));
        } else {
            mQuestionGroupDescription.loadData(mQuestionGroup.getDescription());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBeginQuestionGroupListener) {
            mBeginQuestionGroupListener = (OnBeginQuestionGroupListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBeginQuestionGroupListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mQuestionGroupDescription.clearCache(true);
        mQuestionGroupDescription.loadData("");

        mBeginQuestionGroupListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        renderQuestionGroup();
    }

    @Override
    public void onStop() {
        super.onStop();
        mQuestionGroupDescription.loadData("");
    }

    public interface OnBeginQuestionGroupListener {
        void onBeginQuestionGroup(QuestionGroup group);
    }
}

