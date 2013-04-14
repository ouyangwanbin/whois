package cn.cnnic.android.whois;

import cn.cnnic.android.whois.service.FileService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class IndexActivity extends Activity {
	private FileService fileService;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 		// Òþ²Ø±êÌâÀ¸
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Òþ²Ø×´Ì¬À¸
        setContentView(R.layout.index);
        fileService = new FileService(getApplicationContext());
        
        final Intent intent = new Intent(this,WhoisActivity.class);
        try {
			fileService.init();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), R.string.init_error, 1);
		}
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				IndexActivity.this.startActivity(intent);
				IndexActivity.this.finish();
			}
        	
        }, 5000);
    }
}