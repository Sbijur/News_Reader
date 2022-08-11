package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> titles=new ArrayList<>();
    ArrayList<String> links=new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ListView l;
    public class DownloadTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String result="";
            URL url;
            HttpURLConnection c=null;
            try {
                url=new URL(params[0]);
                c=(HttpURLConnection) url.openConnection();
                InputStream inputStream=c.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char) data;
                    result=result+current;
                    data=reader.read();
                }
                Log.i("Result",result);
                return result;
            } catch (MalformedURLException e) {
                Log.i("here","here");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("there","there");
                e.printStackTrace();
            }
            return "FAILED";

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l=(ListView) findViewById(R.id.lists);
        try {
            DownloadTask task=new DownloadTask();
            String result=null;
            try {
                result=task.execute("https://newsdata.io/api/1/news?apikey=pub_2077fc2722db597bab0763606f0f88ecd8f8&language=en&country=in").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject j=new JSONObject(result);
            String title=j.getString("results");
            JSONObject js;
            JSONArray arr=new JSONArray(title);
            for(int i=0;i<arr.length();i++)
            {
                js=arr.getJSONObject(i);
                titles.add(js.getString("title"));
                links.add(js.getString("link"));
            }
            arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,titles);
            l.setAdapter(arrayAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("link",links.get(position));
                startActivity(intent);
            }
        });

    }
}