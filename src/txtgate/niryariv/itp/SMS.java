package txtgate.niryariv.itp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SMS extends Activity {
	
	public static final String PREFS_NAME = "TxtGatePrefsFile";
	
	public String identifier = "";
	public String targetUrl = "";
	
    
	public void onResume() {
		Log.d("TXTGATE", "RESUME");
		super.onResume();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

//	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		this.identifier = settings.getString("pref_identifier", "itp");
		this.targetUrl =  settings.getString("pref_target_url", "http://qkhack.appspot.com/itpdemo");

		Log.d("TXTGATE", "ident:" + this.identifier +"\ntarget:" + this.targetUrl);
		
//		this.savePrefs();
		
		String infoText = new String();
		
		infoText = "All SMS messages";
		
		if (this.identifier.trim() != "") {
			infoText += " starting with <b>" + this.identifier + "</b>";
		}
		
		infoText += " are now directed to URL <b>" + this.targetUrl +"</b>";
		infoText += " - if the response body contains text, it will SMSed back to the sender.";

		infoText += "<br /><br />Press Menu to change SMS identifier or target URL.";
		
		infoText += "<br /><br />For questions or feedback, please contact <a href=\"mailto:niryariv@gmail.com\">niryariv@gmail.com</a>";
		
		TextView info = (TextView) this.findViewById(R.id.info);
        info.setText(Html.fromHtml(infoText));

	}	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d("TXTGATE", "STARTED");
        
//    	Button openMenuButton = (Button) findViewById(R.id.settingsButton);
//        openMenuButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//          });

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
//    	this.savePrefs();
    }
    
//    private void savePrefs(){
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("identifier", this.identifier);
//        editor.putString("target_url", this.targetUrl);
//
//        editor.commit();
//        
//        Log.d("TXTGATE", "savePrefs() ident:" + this.identifier +"\ntarget:" + this.targetUrl);
//    }
}
