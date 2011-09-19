package com.tinyspeck.sample;

import org.json.JSONObject;

import com.tinyspeck.android.Glitch;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;
import com.tinyspeck.android.GlitchSessionDelegate;
import com.tinyspeck.sample.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity implements GlitchRequestDelegate, GlitchSessionDelegate {
	
	//// Dialog Constants ////
	
	static final int DIALOG_LOGIN_FAIL_ID = 0;
	static final int DIALOG_REQUEST_FAIL_ID = 1;
	
	//// Private instance variables ////
	
	private Glitch glitch;
	private TextView nameTextView;
	
	
	//// Overridden methods ////
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set content view
		setContentView(R.layout.main);
		
		// Initialize new Glitch object with API key and redirect URI
		// !!! Get an API key and set up your redirect URI at http://api.glitch.com !!!
		// Sample API key: "98-abd43ea0d93cb45adb24e0b361f04e7b325183ee"
		// Sample RedirectURI: "glitchandroidsdk://auth"
		//glitch = new Glitch(null, null);
		glitch = new Glitch("98-abd43ea0d93cb45adb24e0b361f04e7b325183ee", "glitchandroidsdk://auth");
		
		// Associate our textview with private variable
        nameTextView = (TextView) findViewById(R.id.nameTextView);
		
        // Check our intent for browsable to see whether we came from the browser
        Intent intent = getIntent();
        if (intent.hasCategory(Intent.CATEGORY_BROWSABLE))
        {
        	final Uri uri = intent.getData();
        	
        	if (uri != null)
    		{
    			glitch.handleRedirect(uri, this);
    		}
        }
        // We didn't come from the browser, so let's authorize
        else
        {
        	glitch.authorize("identity", this);
        }
	}
	
	
	//// GlitchRequester interface methods ////

	public void requestFinished(GlitchRequest request) {
        if (request != null && request.method != null && request.method == "players.info")
        {
        	JSONObject response = request.response;
        	
        	if (response != null)
        	{
        		String playerName = response.optString("player_name");
        		nameTextView.setText("Hello " + playerName + "!");        		
        	}
    	}
	}


	public void requestFailed(GlitchRequest request) {
		showDialog(DIALOG_REQUEST_FAIL_ID);
	}
	
	
	//// GlitchSession interface methods ////

	public void glitchLoginSuccess() {
		GlitchRequest request = glitch.getRequest("players.info");
        request.execute(this);
	}

	public void glitchLoginFail() {
		showDialog(DIALOG_LOGIN_FAIL_ID);
	}

	public void glitchLoggedOut() {
		// Called when user logs out (method stub, not yet implemented)
	}
	
	
	//// Dialog Creation ////
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    
	    switch(id) {
	    case DIALOG_LOGIN_FAIL_ID:
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Login failure!")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Darn", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   dialog.cancel();
	    	           }
	    	       });
	    	dialog = builder.create();
	    	
	        break;
	        
	    case DIALOG_REQUEST_FAIL_ID:
	    	
	    	AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	    	builder1.setMessage("Request failure!")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Argh!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   dialog.cancel();
	    	           }
	    	       });
	    	dialog = builder1.create();
	    	
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}
}
