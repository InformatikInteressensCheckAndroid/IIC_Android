package at.ac.htlstp.app.iic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IICFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public abstract class IICFragment extends Fragment {
    public String title;
    public String action_key;

    protected OnFragmentInteractionListener interactionListener;
    protected OnFragmentDisplayedListener displayedListener;

    public IICFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        displayedListener.onFragmentDisplayed(title, action_key);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (context instanceof OnFragmentDisplayedListener) {
            displayedListener = (OnFragmentDisplayedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentDisplayedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
        displayedListener = null;
    }

    public String getTitle() {
        return title;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String fragment_key);
    }

    public interface OnFragmentDisplayedListener {
        void onFragmentDisplayed(String title, String action_key);
    }
}
