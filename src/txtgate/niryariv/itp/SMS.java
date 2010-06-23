package txtgate.niryariv.itp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SMS extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("TXTGATE", "STARTED");
    }
}

