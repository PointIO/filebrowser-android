package point.io.filebrowser;

import android.app.Application;

/**
 * Created by dylan on 10/27/14.
 */
public class FileBrowser extends Application{
    private String sessionKey;

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
    public String getSessionKey(){
        return this.sessionKey;
    }
}
