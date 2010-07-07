package txtgate.niryariv.itp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	
	@Override
	// source: http://www.devx.com/wireless/Article/39495/1954
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			return;
		}

		// get settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		String identifier = settings.getString("pref_identifier", "itp");
		String targetUrl =  settings.getString("pref_target_url", "http://qkhack.appspot.com/itpdemo");

		SmsMessage msgs[] = getMessagesFromIntent(intent);

		for (int i = 0; i < msgs.length; i++) {
			SmsMessage mesg = msgs[i];
			String message = mesg.getDisplayMessageBody();
			String sender = mesg.getDisplayOriginatingAddress();
			
			if (message != null && message.length() > 0 
					&& (message.toLowerCase().startsWith(identifier) || identifier.trim() == "")) {

				Log.d("TXTGATE", "MSG RCVD:\"" + message + "\" from: " + sender);
				
				// send the message to the URL
				String resp = openURL(sender, message, targetUrl).toString();
				
				// SMS back the response
				if (resp.trim().length() > 0) {
					SmsManager smgr = SmsManager.getDefault();
					smgr.sendTextMessage(sender, null, resp, null, null);
				}
				
				// delete SMS from inbox, to prevent it from filling up
				DeleteSMSFromInbox(context, mesg);
								
			}
		}

	}

	private void DeleteSMSFromInbox(Context context, SmsMessage mesg) {
		Log.d("TXTGATE", "try to delete SMS");
		
		try {
			Uri uriSms = Uri.parse("content://sms/inbox");
			
			StringBuilder sb = new StringBuilder();
			sb.append("address='" + mesg.getOriginatingAddress() + "' AND ");
			sb.append("body='" + mesg.getMessageBody() + "'");
			Cursor c = context.getContentResolver().query(uriSms, null, sb.toString(), null, null);
			c.moveToFirst();
			int thread_id = c.getInt(1);
			context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id), null, null);
			c.close();
		} catch (Exception ex) {
			// deletions don't work most of the time since the timing of the
			// receipt and saving to the inbox
			// makes it difficult to match up perfectly. the SMS might not be in
			// the inbox yet when this receiver triggers!
			Log.d("SmsReceiver", "Error deleting sms from inbox: " + ex.getMessage());
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
			Log.e("TXTGATE", "GetMessages ERROR\n" + e);
		}
		return retMsgs;
	}
	

	public String openURL(String sender, String message, String targetUrl) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(targetUrl);

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
		         Log.e("TXTGATE", "HTTP CLIENT:" + ostream.toString());
		     } else {
		         InputStream content = response.getEntity().getContent();
		         // <consume response>
		         respTxt = streamread(content);
		         Log.e("TXTGATE", "HTTP RESP" + respTxt);
		         content.close(); // this will also close the connection
		     }

	        
	    } catch (ClientProtocolException e) {
	    	Log.e("TXTGATE", "openURL ERROR: Protocolx\n" + e);
	    	return ("Protocol Error");
	    } catch (IOException e) {
	    	Log.e("TXTGATE", "openURL ERROR IOx\n" + e);
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
