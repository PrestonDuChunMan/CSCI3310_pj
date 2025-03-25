package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.cuhk.csci3310.basketball_app.models.Properties;

public class CourtDetailActivity extends AppCompatActivity {
    private TextView nameView, addressView;
    private TextView nSearch1View, nSearch2View, nSearch3View, nSearch4View, nSearch5View, nSearch6View;

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
    }
}
