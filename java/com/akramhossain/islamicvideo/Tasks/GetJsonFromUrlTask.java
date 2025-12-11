package com.akramhossain.islamicvideo.Tasks;

/**
 * Created by Lenovo on 8/4/2018.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.islamicvideo.MainActivity;
import com.akramhossain.islamicvideo.R;
import com.akramhossain.islamicvideo.app.AppController;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetJsonFromUrlTask{

    private Activity activity;
    private String url;
    private ProgressDialog dialog;
    private final static String TAG = GetJsonFromUrlTask.class.getSimpleName();
    ProgressBar progressBar;

    public GetJsonFromUrlTask(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        getData();
    }

    private void getData(){
        // Tag used to cancel the request
        String tag_string_req = "home_pai";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Api Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    ((MainActivity) activity).parseJsonResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Api error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Api Error: " + error.getMessage());
                Toast.makeText(activity.getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent",
                        "Mozilla/5.0 (Linux; Android 12; Pixel 5) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/119.0 Mobile Safari/537.36");
                headers.put("Accept", "application/json,text/html,application/xhtml+xml");
                headers.put("Accept-Language", "en-US,en;q=0.9");
                headers.put("Referer", "https://google.com/");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected String doInBackground(Void... params) {
//
//        // call load JSON from url method
//        return loadJSON(this.url).toString();
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        ((MainActivity) activity).parseJsonResponse(result);
//        //dialog.dismiss();
//        progressBar.setVisibility(View.GONE);
//        Log.i(TAG, result);
//    }
//
//    public JSONObject loadJSON(String url) {
//        // Creating JSON Parser instance
//        JSONGetter jParser = new JSONGetter();
//
//        // getting JSON string from URL
//        JSONObject json = jParser.getJSONFromUrl(url);
//        if(json.length()==0) {
//            return null;
//        }else {
//            return json;
//        }
//    }
//
//    private class JSONGetter {
//
//        private InputStream is = null;
//        private JSONObject jObj = null;
//        private String json = "";
//        HttpURLConnection urlConnection = null;
//
//        // constructor
//        public JSONGetter() {
//
//        }
//
//        public JSONObject getJSONFromUrl(String url) {
//
//            // Making HTTP request
//            try {
//                URL remoteUrl = new URL(url);
//                urlConnection = (HttpURLConnection) remoteUrl.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//                int lengthOfFile = urlConnection.getContentLength();
//                // Read the input stream into a String
//                is = urlConnection.getInputStream();
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"),
//                        8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                is.close();
//                json = sb.toString();
//            } catch (Exception e) {
//                Log.e("Buffer Error", "Error converting result " + e.toString());
//            }
//
//            // try parse the string to a JSON object
//            try {
//                jObj = new JSONObject(json);
//            } catch (JSONException e) {
//                Log.e("JSON Parser", "Error parsing data " + e.toString());
//            }
//
//            // return JSON String
//            return jObj;
//
//        }
//    }
}
