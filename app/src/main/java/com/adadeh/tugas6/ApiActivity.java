package com.adadeh.tugas6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.adadeh.tugas6.databinding.ActivityApiBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiActivity extends AppCompatActivity implements
        View.OnClickListener{
    private ActivityApiBinding binding;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abut;
    //declaration variable
    String index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        //setup view binding
        binding = ActivityApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.fetchButton.setOnClickListener(this);

        dl = (DrawerLayout) findViewById(R.id.dl);
        abut = new ActionBarDrawerToggle(this, dl, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        abut.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abut);
        abut.syncState();
        actionBar.setDisplayHomeAsUpEnabled(true);
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_cuaca) {
                    Intent a = new Intent(ApiActivity.this, MainActivity.class);
                    startActivity(a);}
                else if (id == R.id.nav_setting) {
                    Intent a = new Intent(ApiActivity.this, MainActivity2.class);
                    startActivity(a);
                } else if (id == R.id.nav_profile) {
                    Intent a = new Intent(ApiActivity.this, MainActivity3.class);
                    startActivity(a);
                }else if (id == R.id.nav_Alarm) {
                    Intent a = new Intent(ApiActivity.this, MainActivity4.class);
                    startActivity(a);
                }else if (id == R.id.nav_Sqlite) {
                    Intent a = new Intent(ApiActivity.this, MainActivity6.class);
                    startActivity(a);
                }else if (id == R.id.nav_Wheater) {
                    Intent a = new Intent(ApiActivity.this, ApiActivity.class);
                    startActivity(a);
                }
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abut.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    //onclik button fetch
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fetch_button){
            index = binding.inputId.getText().toString();
            try {
                getData();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    //get data using api link
    public void getData() throws MalformedURLException {
        Uri uri = Uri.parse("https://goweather.herokuapp.com/weather/Indonesia").buildUpon().build();
        URL url = new URL(uri.toString());
        new DOTask().execute(url);
    }
    class DOTask extends AsyncTask<URL, Void, String>{
        //connection request
        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls [0];
            String data = null;
            try {
                data = NetworkUtlis.makeHTTPRequest(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String s){
            try {
                parseJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //get data json
        public void parseJson(String data) throws JSONException{
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            }catch (JSONException e){
                e.printStackTrace();
            }

            JSONArray cityArray = jsonObject.getJSONArray("forecast");
            for (int i =0; i <cityArray.length(); i++){
                JSONObject obj = cityArray.getJSONObject(i);
                String Sobj = obj.get("day").toString();
                if (Sobj.equals(index)){
                    String temperature = obj.get("temperature").toString();
                    binding.resultSuhu.setText(temperature);
                    String hari = obj.get("day").toString();
                    binding.resultHari.setText(hari);
                    String kecepatan = obj.get("wind").toString();
                    binding.resultKecepatan.setText(kecepatan);
                    break;
                }
                else{
                    binding.resultSuhu.setText("Not Found");
                    binding.resultHari.setText("Not Found");
                    binding.resultKecepatan.setText("Not Found");
                }
            }
        }
    }
}