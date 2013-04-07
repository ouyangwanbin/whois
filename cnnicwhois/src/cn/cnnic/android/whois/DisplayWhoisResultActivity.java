package cn.cnnic.android.whois;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class DisplayWhoisResultActivity extends Activity {
	private static final int PROGRESS_DIALOG = 0;
	private ProgressDialog progressDialog;
	private Dialog domainDetailDialog;
	private List<TldEntity> preList;
	private ListView listView;
	private static ExecutorService pool;
	private static int resultSize = 0;
	private TextView domainDetailText;
	private List<Domain> domainList = new ArrayList<Domain>();
	private static String queryType;
	private Button backQuery;
	
	
	
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		        if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
		            
		        } else {
		        	
		        }
    }
	
	private Handler mHandler = new Handler(){
		private int tmp = 0;
		@Override
		public void handleMessage(Message msg) {
			    Domain domain = (Domain)msg.obj;
				domainList.add(domain);
				progressDialog.setProgress(100*tmp/resultSize);
				tmp++;
				if(tmp == resultSize){
					dismissDialog(PROGRESS_DIALOG);
					backQuery.setVisibility(View.VISIBLE);
	                listView.setAdapter(new WhoisAdapter(DisplayWhoisResultActivity.this,domainList,R.layout.result_item));
				}
			}		
	};
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(DisplayWhoisResultActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setProgress(0);
            return progressDialog;
        default:
            return null;
        }
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPreferenceService us = new UserPreferenceService();
        Intent intent = getIntent();
        Domain domain = new Domain();
        String domainName = intent.getExtras().getString("domainName");
        queryType = intent.getExtras().getString("queryType");
        //精确查询
        if("accurate".equals(queryType)){
        	try {
        			preList = us.getUserPreference("tld-recommend.xml",true);
        		for(TldEntity tld : preList){
        			if(tld.getTldName().equals(LanguageUtil.getDomainTld(domainName))){
        				WhoisService whoisService = new WhoisService();
        				whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(domainName));
        				whoisService.setmHandler(mHandler);
        				whoisService.setEncoding("UTF-8");
        				whoisService.setTldEntity(tld);
        				domain = whoisService.execute();
        				break;
        			}
        		}
        		Intent aintent = new Intent(this,WhoisActivity.class);
	        	aintent.putExtra("domainResult",domain.getWhoisResult());
	        	aintent.putExtra("domainName", domain.getDomainName());
	        	setResult(Activity.RESULT_OK,aintent);
	        	finish();
     		} catch (Exception e1) {
     			Toast.makeText(getApplicationContext(), R.string.query_validate_unpass, 1).show();
     			e1.printStackTrace();
     		}
        }else{
        	//模糊查询
        	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        	setContentView(R.layout.result);
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
            listView = (ListView)this.findViewById(R.id.listView);
            backQuery = (Button)this.findViewById(R.id.backQuery);
        	showDialog(PROGRESS_DIALOG);
        	domainDetailDialog = new Dialog(DisplayWhoisResultActivity.this);
        	domainDetailDialog.setContentView(R.layout.result_detail);
        	domainDetailText=(TextView) domainDetailDialog.findViewById(R.id.domainWhoisDetail);
        	String tlds = intent.getExtras().getString("tlds");
            String[] tldArray = tlds.split(",");
            try {
            	preList = us.getUserPreference("tld-recommend.xml",true);
    			pool = Executors.newFixedThreadPool(tldArray.length);
    			resultSize = tldArray.length;
    			List<TldEntity> filteredList =  filterTheTldsUnchoose(tldArray,preList);
    			for(TldEntity tldEntity : filteredList){
    				WhoisService whoisService = new WhoisService();
    				whoisService.setDomainNameWithoutTld(domainName);
    				whoisService.setmHandler(mHandler);
    				whoisService.setEncoding("UTF-8");
    				whoisService.setTldEntity(tldEntity);
    				pool.execute(whoisService);
    			}
    		} catch (Exception e) {
    			Toast.makeText(getApplicationContext(), R.string.query_validate_unpass, 1).show();
    			e.printStackTrace();
    		}
            listView.setOnItemClickListener(new ItemClick(listView));
            domainDetailText.setOnClickListener(new DomainDetailClick());
        }
    }
    
    protected final class ItemClick implements OnItemClickListener{
		private ListView listView;
		public ItemClick(ListView listView) {
			this.listView = listView; 
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Domain domain = (Domain)listView.getItemAtPosition(position);
			domainDetailDialog.setTitle(domain.getDomainName());
            domainDetailText.setText(domain.getWhoisResult());
            domainDetailDialog.show();
		}
    	
    }
    
    /**
     * 捕获双击事件，然后关闭dialog
     * @author ouyangwanbin
     *
     */
    public final class DomainDetailClick implements OnClickListener{
    	private long clickmil;
		@Override
		public void onClick(View v) {
			if(System.currentTimeMillis()-clickmil < 200){
				domainDetailDialog.dismiss();
			}
			clickmil=System.currentTimeMillis();
		}
		
    }
    
    public void backToQuery(View v){
    	this.finish();
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		if(pool != null){
			pool.shutdown();
		}
	}



	private List<TldEntity> filterTheTldsUnchoose(String[] tldArray,List<TldEntity> preList){
    	List<TldEntity> filteredTld = new ArrayList<TldEntity>();
    	for(String stld : tldArray){
    		for(TldEntity tldEntity : preList){
    			if(stld.equals(tldEntity.getTldName())){
    				filteredTld.add(tldEntity);
    				break;
    			}
    		}
    	}
    	return filteredTld;
    }
}