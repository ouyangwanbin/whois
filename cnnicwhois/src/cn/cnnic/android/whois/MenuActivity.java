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
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;

public class MenuActivity extends Activity {
	private Intent intentWhois;
	private Button settingBtn;
	private UserPreferenceService up;
	/** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	intentWhois = new Intent(this,WhoisActivity.class);
    	 up = new UserPreferenceService();
    	 super.onCreate(savedInstanceState);
    	 Log.i("whoisLog","menu acitvity has been created");
    	 requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
         setContentView(R.layout.menu);
         getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
    	 settingBtn = (Button)this.findViewById(R.id.showManage);
    	 settingBtn.setVisibility(View.VISIBLE);
    }
    
    //跳转到whois查询
    public void whoisQuery(View v){
    	this.startActivity(intentWhois);
    }
    
    
    public void showManage(View v){
    	final CharSequence[] items = {getResources().getText(R.string.domain_cat_manage),getResources().getText(R.string.domain_mark_manage)};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	if(item == 0){
    	    		//查询tld-recommend.xml
    	    		try {
						List<TldEntity> tlds = up.getUserPreference("tld-recommend.xml" , false);
						Intent intent = new Intent(MenuActivity.this,TldManagerActivity.class);
						intent.putExtra("tlds", (Serializable)tlds);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
    	    	}
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
}
