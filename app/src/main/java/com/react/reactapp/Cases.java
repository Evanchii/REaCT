package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Cases extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private CommonFunctions cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        setContentView(R.layout.cases);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Stats/Dagupan City");

        cf = new CommonFunctions();
        cf.fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dbRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot entry : snapshot.getChildren()) {
                    Log.d("Cases(72)", "Now loading data");
                    String daily = String.valueOf(entry.child("daily").getValue());
                    if(!daily.equals("#")) {
                        ((ImageView) findViewById(R.id.cases_imgStats)).setImageBitmap(cf.getBitmapFromURL(daily));
                    } else {
                        ((ImageView) findViewById(R.id.cases_imgStats)).setImageResource(R.drawable.nd_daily);
                    }

//                TextView cases = findViewById(R.id.cases_txtCase),
//                        death = findViewById(R.id.cases_txtDeath),
//                        recoveries = findViewById(R.id.cases_txtRecoveries),
//                        active = findViewById(R.id.cases_txtActive),
//                        tested = findViewById(R.id.cases_txtTested);
//                TextView[] covStatus = new TextView[] {findViewById(R.id.cases_txtActive), findViewById(R.id.cases_txtCase), findViewById(R.id.cases_txtDeath), findViewById(R.id.cases_txtRecoveries), findViewById(R.id.cases_txtTested)};
                    TextView active = findViewById(R.id.cases_txtActive),
                            newCase = findViewById(R.id.cases_txtCase),
                            death = findViewById(R.id.cases_txtDeath),
                            recov = findViewById(R.id.cases_txtRecoveries),
                            test = findViewById(R.id.cases_txtTested);

                    active.setText(entry.child("tActive").getValue().toString());
                    newCase.setText(entry.child("nCases").getValue().toString());
                    death.setText(entry.child("tDeaths").getValue().toString());
                    recov.setText(entry.child("tRecoveries").getValue().toString());
                    test.setText(entry.child("tTested").getValue().toString());


//                int x = 0;

//                for(DataSnapshot data : snapshot.child("covStatus").getChildren()) {
//                    covStatus[x++].setText(data.getValue().toString());
//                }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if(CommonFunctions.menu(this, item, "COVID Cases"))
            finish();
        return true;
    }
}