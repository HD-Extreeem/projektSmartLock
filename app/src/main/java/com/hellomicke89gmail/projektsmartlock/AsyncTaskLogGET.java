package com.hellomicke89gmail.projektsmartlock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AsyncTaskLogGET extends AsyncTask<String,Void,Integer>{
  private ArrayList<LogInfo> logList =new ArrayList<>();
  private String username;
  private String password;
  private MainActivity mainActivity;
  private String authString;
  private String logType;
  
        AsyncTaskLogGET(MainActivity mainActivity, String authString, String logType){
            this.mainActivity = mainActivity; // använd samma objekt istället för att skapa ett nytt.
            this.authString=authString;
            this.logType=logType;
        }
        
        @Override
        protected  Integer doInBackground(String... params){
           String parts[];
           String text;
           URL url = null;


           Integer result = 0;


            try{
                if(logType.equals("")){
                    url =new URL("https://" + authString +"@lockdroid.se/log?/");
                }
                else{
                    url=new URL("https://" + authString +"@lockdroid.se/log?search="+logType);
                }

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic " + authString);
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                if (statusCode==200) {
                    BufferedReader read = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    text = read.readLine();
                    for(int i =0; i<2;i++){
                        text = read.readLine();
                    }

                    while (text!= null) {
                      LogInfo currentPerson = new LogInfo();
                      parts = text.split(" ");
                      for(int i = 0; i < 4; i++) {
                        if(i==0) {

                          currentPerson.setName(parts[i]); //hämta namn
                        } else if (i==1) {
                          currentPerson.setDate(parts[i]); // hämta datum
                        } else if (i==2) {
                          currentPerson.setTime(parts[i]); // hämta tid
                        } else if (i==3) {
                          currentPerson.setStatus(parts[i]); // hämta status
                        }
                      }
                      logList.add(currentPerson);
                      text = read.readLine();
                    }
/*
                    BufferedReader read = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = read.readLine()) != null) {
                        response.append(line);
                    }
                    parseJson(response.toString());
*/
                    result = 1;
                }
               
                else{
                    //urlConnection.disconnect();
                    result = 0;
                }

            }catch (Exception e){
                Log.d("AsynTaskLogGet", e.getLocalizedMessage());
            }
            return result;
        }
    @Override
    protected void onPostExecute (Integer result){
        if (result==1){
            mainActivity.updateLogAdapter(logList);
            mainActivity.errorToast("Log updated");
        }
        else {
            System.out.println("Failed to update log");
            mainActivity.errorToast("Failed to update log");
        }
    }

    /*private void parseJson(String result){
        try{

            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("Log");

            logList = new ArrayList<>();

            for(int i = 0; i<posts.length();i++){
                JSONObject post = posts.getJSONObject(i);
                 LogInfo info = new LogInfo();

                info.setName(post.optString("Name"));
                info.setDate(post.optString("Date"));
                info.setTime(post.optString("Time"));
                info.setStatus(post.optString("Status"));

                logList.add(info);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }*/
}
