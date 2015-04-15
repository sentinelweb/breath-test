package co.uk.sentinelweb.pyst.test.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.uk.sentinelweb.pyst.test.view.BreathingView;
import co.uk.sentinelweb.pyst.test.view.ControlsView;
import sentinelweb.uk.co.pyst.test.R;


/**
 * The Fragment to display user data
 * Created by robert on 14/04/2015.
 */
public class ViewBreathingFragment extends Fragment {
    public static final String ARG_ID = "userId";
    /** the user id */
    Long mId;
    /** breath view */
    BreathingView mBreathView;
    /** Controls view */
    ControlsView mControlsView;
    /** Refresh button */
    Button mRefreshButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            if (getArguments().containsKey(ARG_ID)) {
                mId = getArguments().getLong(ARG_ID);
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_breath, container, false);
        mBreathView = (BreathingView)rootView.findViewById(R.id.view_breath);
        mRefreshButton = (Button)rootView.findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBreathView.setUpdate(!mBreathView.isUpdate());
                mBreathView.invalidate();
            }
        });
        mRefreshButton.setVisibility(View.GONE);
        mControlsView=(ControlsView) rootView.findViewById(R.id.view_controls);
        mControlsView.setTargetView(mBreathView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBreathView=null;
        mControlsView=null;
        mRefreshButton=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }








}
