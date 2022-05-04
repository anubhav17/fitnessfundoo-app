package com.fitnessfundoo.SlidingMenuOptions;

/**
 * Created by Anubhav on 15-01-2016.
 */


import android.app.Fragment;
import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

import com.fitnessfundoo.R;

public class SignOut extends Fragment {

    public SignOut(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sign_out, container, false);


        return rootView;
    }


}
