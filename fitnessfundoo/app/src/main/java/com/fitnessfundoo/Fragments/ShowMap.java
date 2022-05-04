package com.fitnessfundoo.Fragments;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fitnessfundoo.R;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

/**
 * Created by Anubhav on 11-02-2016.
 */
public class ShowMap extends Fragment implements OnMapReadyCallback{

    public ShowMap(){}
    View view;
    // Google Map
    private GoogleMap mGoogleMap;
    double latitude,longitude;
    String mTitle;
    private SupportMapFragment supportMapFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_map, container, false);
        super.getActivity();


   /*     if (getActivity().findViewById(R.id.container) != null){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate();
        }
        else{getActivity().finish();}
     */   //getActivity().getFragmentManager().popBackStack();
        Bundle mBundle = getArguments();
        String lat = mBundle.getString("lat");
        String lng = mBundle.getString("lng");
        mTitle     = mBundle.getString("title");
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lng);

        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();

        Log.d("Show Map","I am at this fragment");
        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;

    }


    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (supportMapFragment == null) {
            supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;

                    // create marker
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(mTitle);

                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    // adding marker
                    mGoogleMap.addMarker(marker);

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(latitude, longitude)).zoom(12).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            });
            //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            // check if map is created successfully or not
            if (supportMapFragment == null) {
                Toast.makeText(getActivity(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //  setUpMap();
    }

}
