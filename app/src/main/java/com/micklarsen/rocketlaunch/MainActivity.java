package com.micklarsen.rocketlaunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private RequestQueue mRequestQueue;
    private RecyclerView mRecyclerViewLaunches;

    private Handler mHandler = new Handler();
    private Runnable mTimerUpdate = new Runnable() {
        @Override
        public void run() {
            updateAdapter();
            mHandler.postDelayed(mTimerUpdate, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestQueue = Volley.newRequestQueue(this);
        mProgressBar = findViewById(R.id.progress_bar);

        mRecyclerViewLaunches = findViewById(R.id.rvLaunches);
        mRecyclerViewLaunches.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLaunches.setAdapter(new LaunchAdapter());
        mTimerUpdate.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLaunchList();
    }

    private void updateAdapter() {
        mRecyclerViewLaunches.getAdapter().notifyDataSetChanged();
    }

    public class LaunchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewTimer, mTextViewLocationName, mTextViewProviderName, mTextViewCountryCode,
                mTextViewInformation, mTextViewLocation, mTextViewWeather, mTextViewNextLaunches;
        private RocketLaunch mLaunch;

        public LaunchHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTextViewTimer = itemView.findViewById(R.id.tvTimer);
            mTextViewLocationName = itemView.findViewById(R.id.tvLocationName);
            mTextViewProviderName = itemView.findViewById(R.id.tvProviderName);
            mTextViewCountryCode = itemView.findViewById(R.id.tvCountry);
            mTextViewInformation = itemView.findViewById(R.id.tvInformation);
            mTextViewLocation = itemView.findViewById(R.id.tvLocation);
            mTextViewWeather = itemView.findViewById(R.id.tvWeather);
            mTextViewNextLaunches = itemView.findViewById(R.id.tvNextLaunches);
        }

        public void bindItem(RocketLaunch launch) {
            mLaunch = launch;
            mTextViewTimer.setText(launch.getTimerString());
            mTextViewLocationName.setText(launch.getLocation());
            mTextViewProviderName.setText(launch.getProviderName());
            mTextViewCountryCode.setText(launch.getCountryCode());
            mTextViewInformation.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.PAGE_TYPE, DetailsActivity.SHOW_INFO);
                intent.putExtra(DetailsActivity.LAUNCH_ID, launch.getId());
                startActivity(intent);
            });
            mTextViewLocation.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.PAGE_TYPE, DetailsActivity.SHOW_LOCATION);
                intent.putExtra(DetailsActivity.LAUNCH_ID, launch.getId());
                startActivity(intent);
            });
            mTextViewWeather.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.PAGE_TYPE, DetailsActivity.SHOW_WEATHER);
                intent.putExtra(DetailsActivity.LAUNCH_ID, launch.getId());
                startActivity(intent);
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(DetailsActivity.PAGE_TYPE, DetailsActivity.SHOW_INFO);
            intent.putExtra(DetailsActivity.LAUNCH_ID, mLaunch.getId());
            startActivity(intent);
        }
    }

    public class LaunchAdapter extends RecyclerView.Adapter<LaunchHolder> {

        @NonNull
        @Override
        public LaunchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_launch, parent, false);
            return new LaunchHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LaunchHolder holder, int position) {
            holder.bindItem(Data.sRocketLaunches.get(position));
            if (Data.sRocketLaunches.size() > 2 && position == 1) {
                holder.mTextViewNextLaunches.setText(String.format(getString(R.string.next_launches), Data.sRocketLaunches.size() - 1));
                holder.mTextViewNextLaunches.setVisibility(View.VISIBLE);
            } else {
                holder.mTextViewNextLaunches.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return Data.sRocketLaunches.size();
        }
    }

    private void getLaunchList() {
        mProgressBar.setVisibility(View.VISIBLE);
        Data.sRocketLaunches.clear();
        com.android.volley.Response.Listener<String> responseListener = (Response.Listener<String>) response -> {
            Log.i("Response", response);
            mProgressBar.setVisibility(View.GONE);
            try {
                JSONObject responseJSON = new JSONObject(response);
                JSONArray array = responseJSON.getJSONArray("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    RocketLaunch launch = new RocketLaunch();
                    launch.setId(object.getString("id"));
                    launch.setName(object.getString("name"));
                    launch.setLaunchDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(object.getString("window_start")));
                    if (!object.isNull("infographic"))
                        launch.setRocketImage(object.getString("infographic"));
                    else launch.setRocketImage(object.getString("image"));

                    JSONObject objectProvider = object.getJSONObject("launch_service_provider");
                    launch.setProviderName(objectProvider.getString("name"));
                    launch.setLaunchType(objectProvider.getString("type"));
                    launch.setProviderUrl(objectProvider.getString("url"));

                    if (!object.isNull("mission")) {
                        JSONObject objectMission = object.getJSONObject("mission");
                        launch.setMissionName(objectMission.getString("name"));
                        launch.setMissionDetails(objectMission.getString("description"));
                    } else {
                        launch.setMissionName(getString(R.string.unknown));
                        launch.setMissionDetails(getString(R.string.unknown));
                    }

                    if (!object.isNull("pad")) {
                        JSONObject objectPad = object.getJSONObject("pad");
                        launch.setLatitude(objectPad.getDouble("latitude"));
                        launch.setLongitude(objectPad.getDouble("longitude"));
                        launch.setWikiLink(objectPad.getString("wiki_url"));
                        launch.setPadName(objectPad.getString("name"));
                        JSONObject objectLocation = objectPad.getJSONObject("location");
                        launch.setLocation(objectLocation.getString("name"));
                        launch.setCountryCode(objectLocation.getString("country_code"));
                    } else {
                        launch.setLocation(getString(R.string.unknown));
                        launch.setCountryCode(getString(R.string.unknown));
                        launch.setWikiLink(getString(R.string.unknown));
                        launch.setPadName(getString(R.string.unknown));
                    }

                    JSONObject objectRocket = object.getJSONObject("rocket");
                    JSONObject objectConfiguration = objectRocket.getJSONObject("configuration");
                    launch.setRocketName(objectConfiguration.getString("name"));

                    Data.sRocketLaunches.add(launch);
                }
                updateAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        com.android.volley.Response.ErrorListener errorListener = error -> {
            error.printStackTrace();
            mProgressBar.setVisibility(View.GONE);
            updateAdapter();
        };

        Uri uri = Uri.parse(Data.sUrl + "nextlaunch/upcoming")
                .buildUpon()
                .build();
        StringRequest request = new StringRequest(Request.Method.GET, uri.toString(), responseListener, errorListener);
        mRequestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTimerUpdate);
    }
}