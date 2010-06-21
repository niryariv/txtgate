package txtgate.niryariv.itp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TARGET_URL = "http://qkhack.appspot.com/itpdemo"; 
	private static final String IDENTIFIER = "itp"; // in order for an SMS to be processed by TxtGate it should start with this string (case insensitive, use "" to include any SMS message) 
	
	
	@Override
	// source: http://www.devx.com/wireless/Article/39495/1954
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			return;
		}

		SmsMessage msgs[] = getMessagesFromIntent(intent);

		for (int i = 0; i < msgs.length; i++) {
			String message = msgs[i].getDisplayMessageBody();
			String sender = msgs[i].getDisplayOriginatingAddress();
			
			if (message != null && message.length() > 0 
					&& (message.toLowerCase().startsWith(IDENTIFIER) || IDENTIFIER == "")) {
				Log.d("MessageListener", "MSG RCVD:\"" + message + "\" from: " + sender);
				
				String resp = openURL(sender, message).toString();
				
				// SMS back the response
				SmsManager smgr = SmsManager.getDefault();
				smgr.sendTextMessage(sender, null, resp, null, null);
				
			}
		}

	}

	// source: http://www.devx.com/wireless/Article/39495/1954
	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				retMsgs[n] = SmsMessage.createFromPdu(byteData);
			}

		} catch (Exception e) {
			Log.e("GetMessages", "fail", e);
		}
		return retMsgs;
	}
	

	public String openURL(String sender, String message) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(TARGET_URL);

	    String respTxt = "";
	    	
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("sender", sender));
	        nameValuePairs.add(new BasicNameValuePair("msg", message));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        int status = response.getStatusLine().getStatusCode();

		     // we assume that the response body contains the error message
		     if (status != HttpStatus.SC_OK) {
		         ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		         response.getEntity().writeTo(ostream);
		         Log.e("HTTP CLIENT", ostream.toString());
		     } else {
		         InputStream content = response.getEntity().getContent();
		         // <consume response>
		         respTxt = streamread(content);
		         Log.e("HTTP RESP", respTxt);
		         content.close(); // this will also close the connection
		     }

	        
	    } catch (ClientProtocolException e) {
	    	Log.e("openURL", "Protocolx", e);
	    	return ("Protocol Error");
	    } catch (IOException e) {
	    	Log.e("openURL", "IOx", e);
	    	return ("IO Error");
	    }
	    
        return (respTxt);
	} 

	
	public String streamread(InputStream in) throws IOException {
   	 StringBuffer stream = new StringBuffer();
   	 byte[] b = new byte[4096];
   	 for (int n; (n = in.read(b)) != -1;) {
   		 stream.append(new String(b, 0, n));
   	 }
   	 return stream.toString();
    }
}
