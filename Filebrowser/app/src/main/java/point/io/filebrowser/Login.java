package point.io.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import point.io.util.LoginSuccessActivity;

/**
 * Created by dylan on 10/28/14.
 */
public class Login extends Activity {

    private EditText user;
    private EditText password;
    private Button loginBtn;
    private Button logoutBtn;
    private Button demoBtn;
    private CheckBox savePwd;
    private CheckBox autoLogin;
    private SharedPreferences userPreferences; // save users preference

    private HashMap<String, String> session =new HashMap<String, String>();
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.login_view);
        initWidget();

    }


    private void initWidget(){
//        userPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user = (EditText)findViewById(R.id.et_user);
        password = (EditText)findViewById(R.id.et_password);
        loginBtn = (Button)findViewById(R.id.btn_login);
        logoutBtn = (Button)findViewById(R.id.btn_logout);
        demoBtn = (Button)findViewById(R.id.btn_demo);
        savePwd = (CheckBox)findViewById(R.id.cb_savePwd);
        autoLogin = (CheckBox)findViewById(R.id.cb_autoLogin);

        // set up exit listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        // set up login listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WebServer Request URL
                String serverURL = "https://api.point.io/v2/auth.json";
                String usernameVal = user.getText().toString();
                String passwordVal = password.getText().toString();
                // Use AsyncTask execute Method To do POST and parse json
                mAuthTask = new UserLoginTask(serverURL,usernameVal,passwordVal);
                mAuthTask.execute((Void) null);
            }
        });

        demoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setText("dyang@point.io");
                password.setText("yyl1988223");
            }
        });
    }


    /*
	 * asynctask class takes 3 args: input type, progress type, return type
	 * custom interface
	 * reference:
	 * http://stackoverflow.com/questions/8623823/finish-the-calling-activity-when-asynctask-completes
	 * http://androidexample.com/Restful_Webservice_Call_And_Get_And_Parse_JSON_Data-_Android_Example/index.php?view=article_discription&aid=101&aaid=123
	 * https://developer.android.com/reference/android/os/AsyncTask.html#onPostExecute(Result)
	 */
    // Class with extends AsyncTask class
    private class UserLoginTask extends AsyncTask<Void,Void,Boolean>{
        private String mUsername;
        private String mSeverUrl;
        private String mPassword;
        private String rawContent;
        private JSONObject authJson;

        public UserLoginTask(String url,String username,String password){
            this.mUsername = username;
            this.mPassword = password;
            this.mSeverUrl = url;
        }

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            try{
                Log.d("Point.io", "starting async api AUTH request", null);
                loginBtn.setEnabled(false); // login button set unabled to click when logging
                Toast.makeText(Login.this, "Attempting to login...", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Call after onPreExecute method
        protected Boolean doInBackground(Void... args) {
//            loginBtn.setEnabled(true);
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            /************ Make Post Call To Web Server ***********/
            try{
                rawContent = this.login(mSeverUrl,mUsername,mPassword);
            }catch (Exception e){
                Log.d("Point.io",e.getMessage());
            }
            Log.d("Point.io", "finished async api AUTH request", null);
            Log.d("Point.io", rawContent, null);
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            // NOTE: You can call UI Element here.
            try{
                if(success){
                    JSONObject rc = new JSONObject(rawContent);
                    authJson = rc; // pass back the result info

                    if(authJson.getInt("ERROR")==0){
                        Toast.makeText(Login.this,"Login Success",Toast.LENGTH_SHORT).show();
                        session.put("SESSIONKEY",authJson.getJSONObject("RESULT").getString("SESSIONKEY"));
                        session.put("USERID",authJson.getJSONObject("RESULT").getString("USERID"));

                        // save session & pass it
//                        ((FileBrowser)getApplication()).setSessionKey(authJson.getJSONObject("RESULT").getString("SESSIONKEY"));
//                        Log.d("POINTIO", "Saved session key " + ((FileBrowser)getApplication()).getSessionKey());

                    // init new activity
                    Intent intent = new Intent(Login.this,LoginSuccessActivity.class);
                    /*
                     * use hashmap to save sessionKey
                     */
                    Bundle map = new Bundle();
                    Log.d("Point.io","SessionKey: " + rc.getJSONObject("RESULT").getString("SESSIONKEY"));


                    map.putSerializable("sessionid", session);
                    intent.putExtra("session", map);
                    //start new activity
                    Login.this.startActivity(intent);

                    }else{
                        Toast.makeText(Login.this,"username or password is invalid",Toast.LENGTH_LONG).show();
                    }
                }




            }catch(JSONException e){
                e.getMessage();
            }
        }


        private String login(String url,String username,String password){
            String result = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);

                List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
                nvPairs.add(new BasicNameValuePair("email", username.toLowerCase().trim()));
                nvPairs.add(new BasicNameValuePair("password", password.trim()));
                nvPairs.add(new BasicNameValuePair("apikey", "FA699A6D-6FEE-4E7D-90FD253DDA6EC1E4"));
                httpPost.setEntity(new UrlEncodedFormEntity(nvPairs));

                HttpResponse response = httpClient.execute(httpPost, localContext);
                Log.d("Point.io", "status code " + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);

            } catch (Exception e) {
                Log.d("Point.io",e.getMessage());
            }
            return result;
        }
    }





}
