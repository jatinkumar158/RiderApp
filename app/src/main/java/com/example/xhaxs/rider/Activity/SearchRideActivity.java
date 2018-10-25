package com.example.xhaxs.rider.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.xhaxs.rider.Adapter.SRPosAdapter;
import com.example.xhaxs.rider.AsyncTasks.RideDetailTask;
import com.example.xhaxs.rider.Datatype.CreateRideDetailData;
import com.example.xhaxs.rider.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class SearchRideActivity extends AppCompatActivity {

    private static final String LOG_CLASS = CreateRideActivity.class.getName();
    private static final String URL_AUTO_PLACE_CONST = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private final Activity activity = this;

    private PlaceAutocompleteFragment placeAutocompleteFragmentFrom;
    private PlaceAutocompleteFragment placeAutocompleteFragmentTo;

    private ImageButton mFabCreateNewRide;

    private RecyclerView mRVPossibleLocations;
    private SRPosAdapter mSRPosAdapter;
    private RecyclerView.LayoutManager mLayoutManagerPos;

    private ImageButton mSearchAvailRides;

    private RideDetailTask mRideDetailTask;
    private CreateRideDetailData[] mCreateRideDetailData;
    private Place fromLocationFinal;
    private Place toLocationFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);

        getSupportActionBar().setElevation(0);

        mFabCreateNewRide = findViewById(R.id.fab_create_new_ride_sr);

        mFabCreateNewRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchRideActivity.this, CreateRideActivity.class);
                startActivity(intent);
            }
        });

        mSearchAvailRides = findViewById(R.id.ib_search_src_dest_sr);
        mRideDetailTask = null;

        placeAutocompleteFragmentFrom = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);
        placeAutocompleteFragmentTo = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);

        placeAutocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fromLocationFinal = place;
            }

            @Override
            public void onError(Status status) {
                fromLocationFinal = null;
                Toast.makeText(SearchRideActivity.this, status.toString(), Toast.LENGTH_LONG).show();
            }
        });

        placeAutocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                toLocationFinal = place;
            }

            @Override
            public void onError(Status status) {
                toLocationFinal = null;
                Toast.makeText(SearchRideActivity.this, status.toString(), Toast.LENGTH_LONG).show();
            }
        });

        mRVPossibleLocations = findViewById(R.id.rv_sr_locations_possible);
        mRVPossibleLocations.setHasFixedSize(true);
        mLayoutManagerPos = new LinearLayoutManager(this);
        mRVPossibleLocations.setLayoutManager(mLayoutManagerPos);
        mCreateRideDetailData = new CreateRideDetailData[0];
        mSRPosAdapter = new SRPosAdapter(SearchRideActivity.this, mCreateRideDetailData);
        mRVPossibleLocations.setAdapter(mSRPosAdapter);

        mSearchAvailRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRideDetailTask != null) {
                    mRideDetailTask.cancel(true);
                    mRideDetailTask = null;
                }
                if(checkAvailData() == false){
                    Toast.makeText(SearchRideActivity.this, "Error Value: from or To Location not Available", Toast.LENGTH_LONG).show();
                    return;
                }
                mRVPossibleLocations.setVisibility(View.GONE);
                mRideDetailTask = new RideDetailTask(SearchRideActivity.this);
                mRideDetailTask.execute(makePosRideURL());
            }
        });
    }

    public void showDetails(CreateRideDetailData createRideDetailData) {
        Log.d(this.getClass().getName(), "Showing result for ------------------------------------------------ ++++++++++++\n\t" + createRideDetailData.toString());
        Intent intent = new Intent(this, RideSummaryActivity.class);
        intent.putExtra("RideDetail", createRideDetailData);
        startActivity(intent);
    }

    public void swapPosData(CreateRideDetailData[] createRideDetailData) {
        mRVPossibleLocations.setVisibility(View.VISIBLE);
        mCreateRideDetailData = null;
        if (createRideDetailData != null) {
            mCreateRideDetailData = createRideDetailData;
        }
        mSRPosAdapter.swapList(mCreateRideDetailData);
    }

    public String makePosRideURL() {
        return "temporary URL -> " + fromLocationFinal.toString() + " - " + toLocationFinal.toString();
    }

    public Place getFromValue() {
        return fromLocationFinal;
    }

    public Place getTovalue() {
        return toLocationFinal;
    }


    boolean checkAvailData(){
        return fromLocationFinal != null && toLocationFinal != null;
    }
}