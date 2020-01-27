package com.ium.unito.progetto_ium_tweb1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.concurrent.ExecutionException;

public class HomepageActivity extends AppCompatActivity{
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.user_textView);
        SharedPreferences preferences = getSharedPreferences("user_information", MODE_PRIVATE);
        String username = preferences.getString("username", "Ospite");
        usernameTextView.setText("Benvenuto/a " + username);

        if(preferences.getBoolean("ospite",false)){
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_slideshow).setVisible(false);
            nav_Menu.findItem(R.id.nav_gallery).setTitle("Ripetizioni disponibili");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void gotoLogin(MenuItem item) {
        AsyncHttpRequest logout = new AsyncHttpRequest();
        logout.execute(new AsyncHttpRequest.Ajax(AsyncHttpRequest.URL_LOGOUT));
        try {
            logout.get(); // riceve la pagina di login del web. Ignorata
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Intent homepageIntent = new Intent(this, LoginActivity.class);
        startActivity(homepageIntent);
    }
}
