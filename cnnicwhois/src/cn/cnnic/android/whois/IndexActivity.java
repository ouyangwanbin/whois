package cn.cnnic.android.whois;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class IndexActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 		// Òþ²Ø±êÌâÀ¸
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Òþ²Ø×´Ì¬À¸
        setContentView(R.layout.index);
        
        final Intent intent = new Intent(this,MenuActivity.class);
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				IndexActivity.this.startActivity(intent);
				IndexActivity.this.finish();
			}
        	
        }, 5000);
    }
}