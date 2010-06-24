package txtgate.niryariv.itp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

public class SMS extends Activity {
	
	public static final String PREFS_NAME = "TxtGatePrefsFile";
	
	
	public void onResume() {
		Log.d("TXTGATE", "RESUME");
		super.onResume();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

//	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		String identifier = settings.getString("pref_identifier", "itp");
		String targetUrl =  settings.getString("pref_target_url", "http://qkhack.appspot.com/itpdemo");
		Log.d("TXTGATE", "ident:" + identifier +"\ntarget:" + targetUrl);
		
	}	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // read initial prefs
////	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//
//		String identifier = settings.getString("pref_identifier", "itp");
//		String targetUrl =  settings.getString("pref_target_url", "http://qkhack.appspot.com/itpdemo");

        setContentView(R.layout.main);
        Log.d("TXTGATE", "STARTED");
    }
    

    // first time the Menu key is pressed
	public boolean onCreateOptionsMenu(Menu menu) {
		startActivity(new Intent(this, Prefs.class));
		return(true);
	}

	// any other time the Menu key is pressed
	public boolean onPrepareOptionsMenu(Menu menu) {
		startActivity(new Intent(this, Prefs.class));
		return(true);
	}
	
    
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	// save prefs
//      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//      
//      SharedPreferences.Editor editor = settings.edit();
//      editor.putString("identifier", self.identifier);
//      editor.putString("target_url", self.target_url);
//
//      // Commit the edits!
//      editor.commit();
    }
}
