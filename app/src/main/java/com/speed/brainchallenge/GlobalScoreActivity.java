package com.speed.brainchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.speed.brainchallenge.globalscore.GlobalScoreListItem;
import com.speed.brainchallenge.globalscore.GlobalScoreListItemAdapter;
import com.speed.brainchallenge.utils.Constant;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class GlobalScoreActivity extends AppCompatActivity implements View.OnClickListener,TabLayout.OnTabSelectedListener {

    private RecyclerView recyclerView;
    private GlobalScoreListItemAdapter itemAdapter;
    private ArrayList<GlobalScoreListItem> itemList = new ArrayList<>();
    private String username;

    private ArrayList<String> usernameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_global_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the username from the intent
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.USERS, MODE_PRIVATE);
        username = sharedPreferences.getString(Constant.CURRENTUSER, "");

        // Get the tabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        RenderStage3Data();
    }

    private void RenderRecyclerView() {
        // Set the recyclerView content
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new GlobalScoreListItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            // go back to main menu
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int i = tab.getPosition();
            if (i == 0) {

            }
            if (i == 1) {
            }

            if (i == 2) {
                // update the stage 3 data
                RenderStage3Data();
            }

            if (i == 3) {
                // update the stage 4 data
                RenderStage4Data();
            }

            if (i == 4) {

            }

            if (i == 5) {

            }

            if (i == 6) {

            }

            if (i == 7) {

            }

        }

    private void RenderStage3Data() {
        // create an arraylist to store the object of the user
        ArrayList<UserData> userDataList = new ArrayList<>();
        itemList.clear();
        // Get all the user data from the shared preferences
        ArrayList<String> allUserName = getAllUserName();
        // Get the stage 3 record from the shared preferences
        for (String name : allUserName) {
            SharedPreferences data = getSharedPreferences(Constant.STAGETHREE + name, MODE_PRIVATE);
            int score = data.getInt(Constant.SCORE, 0);
            long time = data.getLong(Constant.TIME, 0);
            userDataList.add(new UserData(name, score, time));
        }

        // Sort the user data by time
        userDataList.sort((o1,o2) -> {
            if (o1.getScore() == o2.getScore()) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
            return Integer.compare(o2.getScore(), o1.getScore());
        });
        // map to the global score list item
        for (int i = 0; i < userDataList.size(); i++) {
            long time = userDataList.get(i).getTime();
            long minutes = time / 60;
            long seconds = time % 60;
            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            itemList.add(
                    new GlobalScoreListItem(
                            userDataList.get(i).getUsername(),
                            i + 1,
                            userDataList.get(i).getScore(),
                            timeFormatted
                    )
            );
        }
        // End of the for loop
        RenderRecyclerView();
    }

    private void RenderStage4Data() {
        // create an arraylist to store the object of the user
        ArrayList<UserData> userDataList = new ArrayList<>();
        itemList.clear();
        // Get all the user data from the shared preferences
        ArrayList<String> allUserName = getAllUserName();
        // Get the stage 3 record from the shared preferences
        for (String name : allUserName) {
            SharedPreferences data = getSharedPreferences(Constant.STAGEFOUR + name, MODE_PRIVATE);
            int score = data.getInt(Constant.SCORE, 0);
            long time = data.getLong(Constant.TIME, 0);
            userDataList.add(new UserData(name, score, time));
        }

        // Sort the user data by time
        userDataList.sort((o1,o2) -> {
            if (o1.getScore() == o2.getScore()) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
            return Integer.compare(o2.getScore(), o1.getScore());
        });
        // map to the global score list item
        for (int i = 0; i < userDataList.size(); i++) {
            long time = userDataList.get(i).getTime();
            long minutes = time / 60;
            long seconds = time % 60;
            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            itemList.add(
                    new GlobalScoreListItem(
                            userDataList.get(i).getUsername(),
                            i + 1,
                            userDataList.get(i).getScore(),
                            timeFormatted
                    )
            );
        }
        // End of the for loop
        RenderRecyclerView();
    }

    @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }

        public ArrayList<String> getAllUserName() {
            if (!usernameList.isEmpty()) {
                return usernameList;
            }

            SharedPreferences users = getSharedPreferences(Constant.USERS, MODE_PRIVATE);
            // get all the key without value
            Map<String, ?> allEntries = users.getAll();
            usernameList = new ArrayList<>();

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().equals(Constant.CURRENTUSER)) {
                    continue;
                }
                usernameList.add(entry.getKey());
            }
            return usernameList;
        }

}

