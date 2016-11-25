package Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ziye.passby.DistrictSearchDemo;
import com.ziye.passby.R;
import com.ziye.passby.RoutePlanDemo;
import com.ziye.passby.Zhoubian;


public class MapArFragment extends Fragment {
    Button fbtn_location, fbtn_route, fbtn_city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapar, container, false);


        fbtn_city = (Button) rootView.findViewById(R.id.fbtn_city);
        fbtn_location = (Button) rootView.findViewById(R.id.fbtn_location);
        fbtn_route = (Button) rootView.findViewById(R.id.fbtn_route);

        fbtn_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), RoutePlanDemo.class));
            }
        });

        fbtn_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().startActivity(new Intent(getActivity(), DistrictSearchDemo.class));
            }
        });

        fbtn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), Zhoubian.class));
            }
        });

        return rootView;
    }
}
