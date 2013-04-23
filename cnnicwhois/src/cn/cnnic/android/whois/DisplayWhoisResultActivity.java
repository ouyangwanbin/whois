package cn.cnnic.android.whois;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;
import cn.cnnic.android.whois.RegistrarManageActivity;

public class DisplayWhoisResultActivity extends Activity {
	private static final int PROGRESS_DIALOG = 0;
	private Dialog domainDetailDialog;
	private List<TldEntity> preList;
	private ListView listView;
	private static ExecutorService pool;
	private static int resultSize = 0;
	private TextView domainDetailText;
	private List<Domain> domainList = new ArrayList<Domain>();
	private static String queryType;
	private UserPreferenceService up;
	private Button markBtn;
	private Button registerBtn;
	private WhoisAdapter whoisAdapter;

	
	
	
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		        if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
		            
		        } else {
		        	
		        }
    }
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        up = new UserPreferenceService(this);
        Intent intent = getIntent();
        Domain domain = new Domain();
        String domainName = intent.getExtras().getString("domainName");
        queryType = intent.getExtras().getString("queryType");
        //精确查询
        if("accurate".equals(queryType)){
        	try {
        			preList = up.getUserPreference(true);
        		for(TldEntity tld : preList){
        			if(tld.getTldName().equals(LanguageUtil.getDomainTld(domainName))){
        				WhoisService whoisService = new WhoisService();
        				whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(domainName));
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

        	final ImageView backImage = (ImageView)this.findViewById(R.id.tld_back_btn);
        	backImage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					backImage.setVisibility(View.GONE);
					DisplayWhoisResultActivity.this.finish();
				}
        		
        	});
        	
        	
            listView = (ListView)this.findViewById(R.id.listView);
        	showDialog(PROGRESS_DIALOG);
        	domainDetailDialog = new Dialog(DisplayWhoisResultActivity.this);
        	domainDetailDialog.setContentView(R.layout.result_detail);
        	domainDetailText=(TextView) domainDetailDialog.findViewById(R.id.domainWhoisDetail);
        	markBtn=(Button)domainDetailDialog.findViewById(R.id.domainMarkBtn);
        	registerBtn=(Button)domainDetailDialog.findViewById(R.id.registerBtn);
        	final Intent it = new Intent(this,RegistrarManageActivity.class);
        	registerBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					domainDetailDialog.dismiss();
					DisplayWhoisResultActivity.this.startActivity(it);
					DisplayWhoisResultActivity.this.finish();
				}
        		
        	});
        	
        	
        	String tlds = intent.getExtras().getString("tlds");
            String[] tldArray = tlds.split(",");
            List<Domain> domainList = new ArrayList<Domain>();
            try {
    			preList = up.getUserPreference(true);
    			resultSize = tldArray.length;
    			List<TldEntity> filteredList =  filterTheTldsUnchoose(tldArray,preList);
    			for(TldEntity tldEntity : filteredList){
    				domainList.add(new Domain(domainName+tldEntity.getTldName()));
    			}
    		} catch (Exception e) {
    			Toast.makeText(getApplicationContext(), R.string.query_validate_unpass, 1).show();
    			e.printStackTrace();
    		}
            whoisAdapter=new WhoisAdapter(DisplayWhoisResultActivity.this,domainList,R.layout.result_item);
            listView.setAdapter(whoisAdapter);
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
			final Domain domain = (Domain)listView.getItemAtPosition(position);
			if(!"unregistered".equals(domain.getDomainInfo())){
				registerBtn.setVisibility(View.GONE);
			}
			
			domainDetailDialog.setTitle(domain.getDomainName());
            domainDetailText.setText(domain.getWhoisResult());
            //如果已经收藏，则不会出现收藏按钮
            try {
				if(up.isMarked(domain.getDomainName())){
					markBtn.setVisibility(View.GONE);
				}else{
					markBtn.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
            domainDetailDialog.show();
            
            markBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					try {
						up.updateMarkList(domain.getDomainName(), "add");
						whoisAdapter.notifyDataSetChanged();
						domainDetailDialog.dismiss();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), R.string.tld_save_error,1);
						e.printStackTrace();
					}
				}
            	
            });
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