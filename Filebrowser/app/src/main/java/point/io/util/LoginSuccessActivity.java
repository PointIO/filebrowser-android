package point.io.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;

import point.io.filebrowser.R;

/**
 * Created by dylan on 10/16/14.
 */
public class LoginSuccessActivity extends Activity{
    TextView get_session;
    TextView sessionLabel;
    TextView username;
    TextView get_username;


    private HashMap<String,String> session;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success);
        get_session=(TextView)findViewById(R.id.tv_sessionid_show);
        sessionLabel = (TextView)findViewById(R.id.tv_sessionidlabel);
        username = (TextView)findViewById(R.id.tv_userLabel);
        get_username = (TextView)findViewById(R.id.tv_useridshow);

        //Get the sessionkey from intent
        session =  (HashMap<String, String>) this.getIntent().
                getBundleExtra("session").getSerializable("sessionid");
        //read session info,
        String sessionkey=session.get("SESSIONKEY");
        //display the info
        Log.d("Point.io", "session_info--------" + session.get("SESSIONKEY"));
        Log.d("Point.io", "session_info--------" + session.get("USERID"));

        get_session.setText( session.get("SESSIONKEY"));
        get_username.setText(session.get("USERID"));
    }



}
