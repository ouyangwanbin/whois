package cn.cnnic.android.whois.service;

import android.app.Activity;
import android.content.Intent;

public class MailService {
	
    public static void sendMailByIntent(Activity activity , String[] receiveAdress ,String subject, String cc ,String type ) {  
        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);  
        myIntent.setType(type);  
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receiveAdress);  
        myIntent.putExtra(android.content.Intent.EXTRA_CC, cc);  
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);  
        activity.startActivity(Intent.createChooser(myIntent, "mail"));  
  }  
}
