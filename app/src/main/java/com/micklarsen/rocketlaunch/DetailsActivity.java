package com.micklarsen.rocketlaunch;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public static final int SHOW_INFO = 1;
    public static final int SHOW_LOCATION = 2;
    public static final int SHOW_WEATHER = 3;
    public static final String LAUNCH_ID = "launch_id";
    public static final String PAGE_TYPE = "page_type";

    private RocketLaunch mLaunch;
    private RequestQueue mRequestQueue;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimer = new Runnable() {
        @Override
        public void run() {
            updateTimer();
            mHandler.postDelayed(mUpdateTimer, 1000);
        }
    };

    private View mLayoutInformation, mLayoutLocation, mLayoutWeather;
    private TextView mTextViewTimer, mTextViewLocationName, mTextViewProviderName, mTextViewCountryCode,
            mTextViewInformation, mTextViewLocation, mTextViewWeather;

    //information
    private TextView mTextViewDate, mTextViewEstimatedLaunch, mTextViewLocationInfo, mTextViewProvider, mTextViewType,
            mTextViewMission, mTextViewDescription, mTextViewRocket;
    private ImageView mImageViewProvider, mImageViewRocket;

    //location
    private TextView mTextViewLocationLoc, mTextViewCountry, mTextViewWikiLink, mTextViewLatitude, mTextViewLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mLaunch = Data.getLaunchById(getIntent().getStringExtra(LAUNCH_ID));
        mRequestQueue = Volley.newRequestQueue(this);

        mLayoutInformation = findViewById(R.id.llInformation);
        mLayoutLocation = findViewById(R.id.llLocation);
        mLayoutWeather = findViewById(R.id.llWeather);
        mTextViewTimer = findViewById(R.id.tvTimer);
        mTextViewLocationName = findViewById(R.id.tvLocationName);
        mTextViewProviderName = findViewById(R.id.tvProviderName);
        mTextViewCountryCode = findViewById(R.id.tvCountry);
        mTextViewInformation = findViewById(R.id.tvInformation);
        mTextViewLocation = findViewById(R.id.tvLocation);
        mTextViewWeather = findViewById(R.id.tvWeather);

        mTextViewLocationName.setText(mLaunch.getLocation());
        mTextViewProviderName.setText(mLaunch.getProviderName());
        mTextViewCountryCode.setText(mLaunch.getCountryCode());
        mUpdateTimer.run();

        mTextViewInformation.setOnClickListener(v -> setInformation());
        mTextViewLocation.setOnClickListener(v -> setLocation());
        mTextViewWeather.setOnClickListener(v -> setWeather());

        switch (getIntent().getIntExtra(PAGE_TYPE, SHOW_INFO)) {
            case SHOW_INFO:
                setInformation();
                break;
            case SHOW_LOCATION:
                setLocation();
                break;
            case SHOW_WEATHER:
                setWeather();
                break;
        }

        //information
        mTextViewDate = findViewById(R.id.tvDate);
        mTextViewEstimatedLaunch = findViewById(R.id.tvEstimatedLaunch);
        mTextViewLocationInfo = findViewById(R.id.tvLocationInfo);
        mTextViewProvider = findViewById(R.id.tvLaunchProviderName);
        mTextViewType = findViewById(R.id.tvType);
        mTextViewMission = findViewById(R.id.tvMissionName);
        mTextViewDescription = findViewById(R.id.tvMissionDetails);
        mTextViewRocket = findViewById(R.id.tvRocket);
        mImageViewProvider = findViewById(R.id.ivProviderLogo);
        mImageViewRocket = findViewById(R.id.ivRocket);
        mTextViewDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(mLaunch.getLaunchDate()));
        mTextViewEstimatedLaunch.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(mLaunch.getLaunchDate()) + " EST");
        mTextViewLocationInfo.setText(mLaunch.getLocation() + "\n" + mLaunch.getCountryCode());
        mTextViewProvider.setText(mLaunch.getProviderName());
        mTextViewType.setText(mLaunch.getLaunchType());
        mTextViewMission.setText(mLaunch.getMissionName());
        mTextViewDescription.setText(mLaunch.getMissionDetails());
        mTextViewRocket.setText(mLaunch.getRocketName());
        Glide.with(this).load(mLaunch.getRocketImage()).into(mImageViewRocket);
        getProvider();

        //location
        mTextViewLocationLoc = findViewById(R.id.tvLocationLoc);
        mTextViewCountry = findViewById(R.id.tvCountryLoc);
        mTextViewWikiLink = findViewById(R.id.tvWikiLink);
        mTextViewLatitude = findViewById(R.id.tvLat);
        mTextViewLongitude = findViewById(R.id.tvLong);
        mTextViewLocationLoc.setText(mLaunch.getLocation());
        mTextViewCountry.setText(mLaunch.getCountryCode());
        mTextViewLatitude.setText(String.valueOf(mLaunch.getLatitude()));
        mTextViewLongitude.setText(String.valueOf(mLaunch.getLongitude()));
        if (mLaunch.getWikiLink().equals(getString(R.string.unknown)))
            mTextViewWikiLink.setText(mLaunch.getWikiLink());
        else {
            mTextViewWikiLink.setMovementMethod(LinkMovementMethod.getInstance());
            mTextViewWikiLink.setText(Html.fromHtml("<a href=" + mLaunch.getWikiLink() + ">" + mLaunch.getPadName() + "</a>"));
        }
    }

    private void updateTimer() {
        mTextViewTimer.setText(mLaunch.getTimerString());
    }

    private void setInformation() {
        mTextViewInformation.setTextColor(getResources().getColor(R.color.green_light));
        mTextViewLocation.setTextColor(getResources().getColor(R.color.orange));
        mTextViewWeather.setTextColor(getResources().getColor(R.color.orange));
        mLayoutInformation.setVisibility(View.VISIBLE);
        mLayoutLocation.setVisibility(View.GONE);
        mLayoutWeather.setVisibility(View.GONE);
    }

    private void setLocation() {
        mTextViewInformation.setTextColor(getResources().getColor(R.color.orange));
        mTextViewLocation.setTextColor(getResources().getColor(R.color.green_light));
        mTextViewWeather.setTextColor(getResources().getColor(R.color.orange));
        mLayoutInformation.setVisibility(View.GONE);
        mLayoutLocation.setVisibility(View.VISIBLE);
        mLayoutWeather.setVisibility(View.GONE);
    }

    private void setWeather() {
        mTextViewInformation.setTextColor(getResources().getColor(R.color.orange));
        mTextViewLocation.setTextColor(getResources().getColor(R.color.orange));
        mTextViewWeather.setTextColor(getResources().getColor(R.color.green_light));
        mLayoutInformation.setVisibility(View.GONE);
        mLayoutLocation.setVisibility(View.GONE);
        mLayoutWeather.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mUpdateTimer);
        super.onDestroy();
    }

    private void getProvider() {
        com.android.volley.Response.Listener<String> responseListener = (Response.Listener<String>) response -> {
            Log.i("Response", response);
            try {
                JSONObject responseJSON = new JSONObject(response);
                Glide.with(DetailsActivity.this).load(responseJSON.getString("logo_url")).into(mImageViewProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        com.android.volley.Response.ErrorListener errorListener = Throwable::printStackTrace;

        Uri uri = Uri.parse(mLaunch.getProviderUrl())
                .buildUpon()
                .build();
        StringRequest request = new StringRequest(Request.Method.GET, uri.toString(), responseListener, errorListener);
        mRequestQueue.add(request);
    }
}