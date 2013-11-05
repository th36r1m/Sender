package com.xortech.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater {
    private final static String APP_TITLE = "Sender";
    private final static String APP_PNAME = "com.xortech.sender";
    
    private final static int DAYS_UNTIL_PROMPT = 14;
    private final static int LAUNCHES_UNTIL_PROMPT = 30;
    private final static int ZERO = 0;
    
    static int option = 0;
    
    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", ZERO);
        
        SharedPreferences.Editor editor = prefs.edit();
        // Increment launch counter
        long counter = prefs.getLong("launch_count", ZERO);
        
        if (counter == ZERO) {
            editor.putBoolean("dontshowagain", false); 
            editor.putLong("launch_count", counter + 1);
            editor.putLong("date_firstlaunch", System.currentTimeMillis());
            System.out.println("Zero Runs");
        }
        else {
            editor.putLong("launch_count", counter + 1);
            // Return to main if don't show again is true
            if (prefs.getBoolean("dontshowagain", false)) { 
            	return; 
            }
            else {
            	// Get date of first launch
            	Long date_firstLaunch = prefs.getLong("date_firstlaunch", ZERO);
            
            	// Wait at least n days or n launches for prompt
            	if (counter >= LAUNCHES_UNTIL_PROMPT) {
            		showRateDialog(mContext, editor);
            	}
            	else if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
            		showRateDialog(mContext, editor);    
            	}
            }
        } 
        editor.commit();
    }   
    
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        
        Button b1 = new Button(mContext);
        b1.setText("Rate " + APP_TITLE);
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {  
            	editor.putBoolean("dontshowagain", true);     
            	editor.commit();
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	intent.setData(Uri.parse("market://details?id=" + APP_PNAME));
            	mContext.startActivity(intent);
                dialog.dismiss();
            }
        });        
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText("Remind me later");
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	editor.putLong("launch_count", 1); 
            	editor.commit();
            	dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("No, thanks");
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	editor.putBoolean("dontshowagain", true); 
            	editor.commit();
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);        
        dialog.show();        
    }
}