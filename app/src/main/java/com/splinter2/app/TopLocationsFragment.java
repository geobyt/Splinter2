package com.splinter2.app;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.splinter2.app.Database.DBAdapter;
import com.splinter2.app.Model.Coordinate;
import com.splinter2.app.Service.JsonParser;
import com.splinter2.app.Service.WebServiceListener;
import com.splinter2.app.Service.WebServiceTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopLocationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopLocationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TopLocationsFragment
        extends ListFragment
        implements WebServiceListener, SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    TextView loadingText;
    ArrayAdapter<String> adapter; //for list view
    private DBAdapter mDbHelper;
    List<Coordinate> coordinates;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopLocationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopLocationsFragment newInstance(String param1, String param2) {
        TopLocationsFragment fragment = new TopLocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public TopLocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_top_locations, container, false);

        loadingText = (TextView) v.findViewById(R.id.loading_text);

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.top_locations);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //only get data if list is null (prevents refresh of list on nav back)
        if (adapter == null) {
            swipeLayout.setRefreshing(true);
            loadingText.setVisibility(View.VISIBLE);
            refreshList();
        }

        //open sql database
        try {
            mDbHelper = new DBAdapter(getActivity());
            mDbHelper.open();
        }
        catch (Exception ex){
            Log.w("MyLocationsFragment", ex.getMessage());
        }

        return v;
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //store in db
        if (coordinates != null) {
            Coordinate c = coordinates.get(position);
            mDbHelper.createCoordinateIfNotExists(c);

            //nav to my locations tab
            if (mListener != null) {
                Uri coordinateUri = Uri.parse("//coordinate/add/" + c.getLocationId());
                mListener.onFragmentInteraction(coordinateUri);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void responsePost(String result){

    }

    public void responseGet(String result){
        swipeLayout.setRefreshing(false);
        loadingText.setVisibility(View.GONE);

        coordinates = JsonParser.ParseLocationsJson(result);

        List<String> values = new ArrayList<String>();

        for (Coordinate c : coordinates){
            values.add(c.getDescription());
        }

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.rowmessage, R.id.label, values);
        setListAdapter(adapter);
    }

    private void refreshList(){
        WebServiceTask webServiceTask = new WebServiceTask();
        webServiceTask.delegate = this;
        webServiceTask.execute("http://lyraserver.azurewebsites.net/locations");
    }

}
