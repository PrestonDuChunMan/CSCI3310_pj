package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import edu.cuhk.csci3310.basketball_app.adapters.CourtEventAdapter;
import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventListResponse;
import edu.cuhk.csci3310.basketball_app.models.gov.Properties;
import edu.cuhk.csci3310.basketball_app.models.server.SimpleCourtEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtDetailActivity extends AppCompatActivity {
    private static final String TAG = "court_detail";

    private TextView nameView, addressView;
    private TextView nSearch1View, nSearch2View, nSearch3View, nSearch4View, nSearch5View, nSearch6View;
    private RecyclerView recyclerView;
    private CourtEventAdapter adapter;

    private ApiHandler apiHandler;

    private Properties mProperties;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);

        this.nameView = findViewById(R.id.value_name);
        this.addressView = findViewById(R.id.value_address);
        this.nSearch1View = findViewById(R.id.value_nsearch1);
        this.nSearch2View = findViewById(R.id.value_nsearch2);
        this.nSearch3View = findViewById(R.id.value_nsearch3);
        this.nSearch4View = findViewById(R.id.value_nsearch4);
        this.nSearch5View = findViewById(R.id.value_nsearch5);
        this.nSearch6View = findViewById(R.id.value_nsearch6);

        Intent intent = getIntent();
        this.mProperties = Properties.fromIntent(intent);

        this.nameView.setText(this.mProperties.NAME_EN);
        this.addressView.setText(this.mProperties.ADDRESS_EN);
        this.nSearch1View.setText(this.mProperties.NSEARCH01_EN == null || this.mProperties.NSEARCH01_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH01_EN);
        this.nSearch2View.setText(this.mProperties.NSEARCH02_EN == null || this.mProperties.NSEARCH02_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH02_EN);
        this.nSearch3View.setText(this.mProperties.NSEARCH03_EN == null || this.mProperties.NSEARCH03_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH03_EN);
        this.nSearch4View.setText(this.mProperties.NSEARCH04_EN == null || this.mProperties.NSEARCH04_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH04_EN);
        this.nSearch5View.setText(this.mProperties.NSEARCH05_EN == null || this.mProperties.NSEARCH05_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH05_EN);
        this.nSearch6View.setText(this.mProperties.NSEARCH06_EN == null || this.mProperties.NSEARCH06_EN.equals("N.A.") ? "" : this.mProperties.NSEARCH06_EN);

        this.recyclerView = findViewById(R.id.list_event);
        this.adapter = new CourtEventAdapter(this.mProperties.getId(), new ArrayList<>()); // initially empty, get data from server later
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.adapter);

        this.apiHandler = ApiHandler.getInstance();
        this.refresh(recyclerView); // parameter doesn't matter
    }

    private void failureToast() {
        this.failureToast("Failed to get court events");
    }

    private void failureToast(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    public void addEvent(View view) {
        Intent intent = new Intent(this.getApplicationContext(), CourtEventAddActivity.class);
        intent.putExtra("courtId", this.mProperties.getId());
        startActivity(intent);
    }

    public void refresh(View view) {
        this.adapter.updateData(new ArrayList<>());
        Call<CourtEventListResponse> events = this.apiHandler.getCourtEvents(this.mProperties.getId());
        events.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CourtEventListResponse> call, Response<CourtEventListResponse> response) {
                CourtEventListResponse res = response.body();
                if (res == null) {
                    failureToast();
                    return;
                } else if (!res.isSuccess()) {
                    if (res.getError() != null) failureToast(res.getError());
                    else failureToast();
                    return;
                } else if (res.getData() == null) {
                    failureToast();
                    return;
                }
                adapter.updateData(res.getData().stream().sorted((a, b) -> b.getTime().compareTo(a.getTime())).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(Call<CourtEventListResponse> call, Throwable t) {
                Log.e(TAG, "Failed to get court events", t);
                failureToast();
            }
        });
    }
}
