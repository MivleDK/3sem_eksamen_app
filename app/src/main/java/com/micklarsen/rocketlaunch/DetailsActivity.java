package com.micklarsen.rocketlaunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int SHOW_INFO = 1;
    public static final int SHOW_LOCATION = 2;
    public static final int SHOW_WEATHER = 3;
    public static final String LAUNCH_ID = "launch_id";
    public static final String PAGE_TYPE = "page_type";

    private RocketLaunch mLaunch;
    private RequestQueue mRequestQueue;
    private Handler mHandler = new Handler();
    private GoogleMap mMap;
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

    //weather
    private TextView mTextViewLocationWeather, mTextViewDateWeather, mTextViewTemperature, mTextViewWeatherDescription, mTextViewWeatherDetails,
            mTextViewFeelsLike, mTextViewWind, mTextViewPressure, mTextViewHumidity, mTextViewSunrise, mTextViewSunset, mTextViewForecast;
    private ImageView mImageViewWeather, mImageViewCompass;
    private RecyclerView mRecyclerViewWeatherForecast;

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

        findViewById(R.id.logo_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //weather
        mTextViewLocationWeather = findViewById(R.id.tvLocationWeather);
        mTextViewDateWeather = findViewById(R.id.tvDateWeather);
        mTextViewTemperature = findViewById(R.id.tvTemperature);
        mTextViewWeatherDescription = findViewById(R.id.tvWeatherDescription);
        mTextViewWeatherDetails = findViewById(R.id.tvWeatherDetails);
        mTextViewFeelsLike = findViewById(R.id.tvFeelsLike);
        mTextViewWind = findViewById(R.id.tvWind);
        mTextViewPressure = findViewById(R.id.tvPressure);
        mTextViewHumidity = findViewById(R.id.tvHumidity);
        mTextViewSunset = findViewById(R.id.tvSunset);
        mTextViewSunrise = findViewById(R.id.tvSunrise);
        mTextViewForecast = findViewById(R.id.tvWeatherForecast);
        mImageViewWeather = findViewById(R.id.ivWeather);
        mImageViewCompass = findViewById(R.id.ivCompass);
        mRecyclerViewWeatherForecast = findViewById(R.id.rvWeatherForecast);
        mRecyclerViewWeatherForecast.setLayoutManager(new GridLayoutManager(DetailsActivity.this, 2));
        mTextViewLocationWeather.setText(mLaunch.getLocation());
        Calendar c = Calendar.getInstance();
        c.setTime(mLaunch.getLaunchDate());
        int dayInMonth = c.get(Calendar.DAY_OF_MONTH);
        mTextViewDateWeather.setText(new SimpleDateFormat("EEEE, dd'" + Util.getDayOfMonthSuffix(dayInMonth) + "' MMMM yyyy", Locale.getDefault()).format(mLaunch.getLaunchDate()));
        getWeather();
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

    public class DayWeatherHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewWeekDay, mTextViewMaxTemp, mTextViewMinTemp, mTextViewDescription, mTextViewWind;
        private ImageView mImageViewIcon, mImageViewCompass;

        public DayWeatherHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewWeekDay = itemView.findViewById(R.id.tvWeekDay);
            mTextViewMaxTemp = itemView.findViewById(R.id.tvTempMax);
            mTextViewMinTemp = itemView.findViewById(R.id.tvTempMin);
            mTextViewDescription = itemView.findViewById(R.id.tvDescription);
            mTextViewWind = itemView.findViewById(R.id.tvWind);
            mImageViewIcon = itemView.findViewById(R.id.ivIcon);
            mImageViewCompass = itemView.findViewById(R.id.ivCompass);
        }

        public void bindItem(DayWeather weather) {
            mTextViewWeekDay.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(weather.getDate()));
            mTextViewMaxTemp.setText(String.format(Locale.getDefault(), "%d°", weather.getTempMax()));
            mTextViewMinTemp.setText(String.format(Locale.getDefault(), "%d°", weather.getTempMin()));
            mTextViewDescription.setText(weather.getDescription());
            mTextViewWind.setText(String.format(Locale.getDefault(), getString(R.string.wind_speed_m_s), weather.getWindSpeed(),
                    Util.getWindDirectionString(weather.getWindDirection())));
            mImageViewCompass.setRotation(weather.getWindDirection());
            Glide.with(DetailsActivity.this).load(Util.getWeatherIcon(weather.getIcon())).into(mImageViewIcon);
        }
    }

    public class DayWeatherAdapter extends RecyclerView.Adapter<DayWeatherHolder> {

        public ArrayList<DayWeather> mDays;

        public DayWeatherAdapter(ArrayList<DayWeather> days) {
            mDays = days;
        }

        @NonNull
        @Override
        public DayWeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_day_weather, parent, false);
            return new DayWeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DayWeatherHolder holder, int position) {
            holder.bindItem(mDays.get(position));
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }
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

    private void getWeather() {
        com.android.volley.Response.Listener<String> responseListener = (Response.Listener<String>) response -> {
            Log.i("Response", response);
            try {
                JSONObject responseJSON = new JSONObject(response);

                JSONObject objectCurrent = responseJSON.getJSONObject("current");
                mTextViewTemperature.setText(String.format(Locale.getDefault(), getString(R.string.temp_c), objectCurrent.getInt("temp")));
                mTextViewFeelsLike.setText(String.format(Locale.getDefault(), getString(R.string.feels_like_c), objectCurrent.getInt("feels_like")));
                mTextViewWind.setText(String.format(Locale.getDefault(), getString(R.string.wind_speed_m_s), objectCurrent.getDouble("wind_speed"),
                        Util.getWindDirectionString(objectCurrent.getInt("wind_deg"))));
                mImageViewCompass.setRotation(objectCurrent.getInt("wind_deg"));
                mTextViewPressure.setText(String.format(Locale.getDefault(), getString(R.string.pressure_pa), objectCurrent.getInt("pressure")));
                mTextViewHumidity.setText(String.format(Locale.getDefault(), getString(R.string.humidity_), objectCurrent.getInt("humidity")));
                SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm", Locale.getDefault());
                Date sunrise = new Date(objectCurrent.getLong("sunrise") * 1000);
                Date sunset = new Date(objectCurrent.getLong("sunset") * 1000);
                mTextViewSunrise.setText(String.format(Locale.getDefault(), getString(R.string.sunrise_), timeFormat.format(sunrise)));
                mTextViewSunset.setText(String.format(Locale.getDefault(), getString(R.string.sunset_), timeFormat.format(sunset)));

                JSONObject objectWeather = objectCurrent.getJSONArray("weather").getJSONObject(0);
                mTextViewWeatherDescription.setText(objectWeather.getString("main"));
                mTextViewWeatherDetails.setText(objectWeather.getString("description"));
                Glide.with(DetailsActivity.this).load(Util.getWeatherIcon(objectWeather.getString("icon"))).into(mImageViewWeather);

                JSONArray dailyWeather = responseJSON.getJSONArray("daily");
                mTextViewForecast.setText(String.format(Locale.getDefault(), getString(R.string.day_weather_forecast), dailyWeather.length()));
                ArrayList<DayWeather> list = new ArrayList<>();
                for (int i = 0; i < dailyWeather.length(); i++) {
                    JSONObject object = dailyWeather.getJSONObject(i);
                    DayWeather weather = new DayWeather();
                    weather.setDate(new Date(object.getLong("dt") * 1000));
                    weather.setWindSpeed(object.getDouble("wind_speed"));
                    weather.setWindDirection(object.getInt("wind_deg"));

                    JSONObject objectDayWeather = object.getJSONArray("weather").getJSONObject(0);
                    weather.setDescription(objectDayWeather.getString("description"));
                    weather.setIcon(objectDayWeather.getString("icon"));

                    JSONObject objectTemp = object.getJSONObject("temp");
                    weather.setTempMax(objectTemp.getInt("max"));
                    weather.setTempMin(objectTemp.getInt("min"));
                    list.add(weather);
                }
                mRecyclerViewWeatherForecast.setAdapter(new DayWeatherAdapter(list));

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        com.android.volley.Response.ErrorListener errorListener = Throwable::printStackTrace;

        Uri uri = Uri.parse(Data.sUrl + "/weather/" + mLaunch.getLatitude() + "/" + mLaunch.getLongitude() + "/5a5aa77c1ab574a00a9c0b16e5ad50fa")
                .buildUpon()
                .build();
        StringRequest request = new StringRequest(Request.Method.GET, uri.toString(), responseListener, errorListener);
        mRequestQueue.add(request);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng position = new LatLng(mLaunch.getLatitude(), mLaunch.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(mLaunch.getPadName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        NestedScrollView scrollView = findViewById(R.id.scrollView); //parent scrollview in xml, give your scrollview id value
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
    }

    public static class DayWeather {

        private Date mDate;
        private String mIcon;
        private int mTempMin;
        private int mTempMax;
        private String mDescription;
        private double mWindSpeed;
        private int mWindDirection;

        public DayWeather() {
        }

        public Date getDate() {
            return mDate;
        }

        public void setDate(Date date) {
            mDate = date;
        }

        public String getIcon() {
            return mIcon;
        }

        public void setIcon(String icon) {
            mIcon = icon;
        }

        public int getTempMin() {
            return mTempMin;
        }

        public void setTempMin(int tempMin) {
            mTempMin = tempMin;
        }

        public int getTempMax() {
            return mTempMax;
        }

        public void setTempMax(int tempMax) {
            mTempMax = tempMax;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public double getWindSpeed() {
            return mWindSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            mWindSpeed = windSpeed;
        }

        public int getWindDirection() {
            return mWindDirection;
        }

        public void setWindDirection(int windDirection) {
            mWindDirection = windDirection;
        }
    }
}