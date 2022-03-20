package com.akramhossain.islamicvideo.Tasks;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.islamicvideo.CategoryDetailActivity;
import com.akramhossain.islamicvideo.R;
import com.akramhossain.islamicvideo.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Lenovo on 8/20/2018.
 */

public class CategoryDetailsJsonFromUrlTask{

    private Activity activity;
    private String url;
    private final static String TAG = CategoryDetailsJsonFromUrlTask.class.getSimpleName();
    ProgressBar progressBar;

    public CategoryDetailsJsonFromUrlTask(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        getData();
    }

    private void getData(){
        // Tag used to cancel the request
        String tag_string_req = "category_detail_api";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Api Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    ((CategoryDetailActivity) activity).parseJsonResponse(response);
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
        }) {};

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
//        // call load JSON from url method
//        return loadJSON(this.url).toString();
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        ((CategoryDetailActivity) activity).parseJsonResponse(result);
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
//            // try parse the string to a JSON object
//            try {
//                jObj = new JSONObject(json);
//            } catch (JSONException e) {
//                Log.e("JSON Parser", "Error parsing data " + e.toString());
//            }
//            // return JSON String
//            return jObj;
//
//        }
//    }
}
