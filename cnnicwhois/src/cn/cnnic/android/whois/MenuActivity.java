package cn.cnnic.android.whois;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.DomainMark;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;

public class MenuActivity extends Activity {
	private Button settingBtn;
	private UserPreferenceService  up = new UserPreferenceService(this);
	/** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 Log.i("whoisLog","menu acitvity has been created");
    	 requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
         setContentView(R.layout.menu);
         getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
    	 settingBtn = (Button)this.findViewById(R.id.showManage);
    	 settingBtn.setVisibility(View.VISIBLE);
    	 
    }
    

    
    public void showManage(View v){
    	final CharSequence[] items = {getResources().getText(R.string.domain_cat_manage),getResources().getText(R.string.domain_mark_manage)};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	Log.i("whoisLog","item is : " + String.valueOf(item));
    	    	if(item == 0){
    	    		//≤È—Øtld-recommend.xml
    	    		try {
						List<TldEntity> tlds =  up.getUserPreference(false);
						Intent intent = new Intent(MenuActivity.this,TldManagerActivity.class);
						intent.putExtra("tlds", (Serializable)tlds);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
    	    	}
    	    	if(item == 1){
    	    		//≤È—Ødomain-mark.xml
    	    		List<DomainMark> markList = null;
					try {
						markList = up.getUsersDomainMark();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), R.string.data_load_error, 1).show();
						e.printStackTrace();
					}
    	    		Intent intent = new Intent(MenuActivity.this,MarkManagerActivity.class);
					intent.putExtra("markList", (Serializable)markList);
					startActivity(intent);
    	    	}
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
}
