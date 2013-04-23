package cn.cnnic.android.whois;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class TldManagerActivity extends Activity {
	private ListView tldListView;
	private TldAdapter tldAdapter;
	private ImageView tldAddImage;
	private ImageView tldBackImage;
	private Resources resources;
	private UserPreferenceService up = new UserPreferenceService(this);
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tld_manage);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_tld);
		resources = getResources();
		
		tldListView = (ListView)this.findViewById(R.id.tldListView);
		Intent intent = getIntent();		
		@SuppressWarnings("unchecked")
		List<TldEntity> tlds =(List<TldEntity>) intent.getSerializableExtra("tlds");
		tldAdapter = new TldAdapter(this,tlds,R.layout.tld_item);
		tldListView.setAdapter(tldAdapter);
		tldListView.setOnItemClickListener(new ItemClick(tldListView));
		
		tldBackImage = (ImageView)this.findViewById(R.id.tld_back_btn);
		tldBackImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TldManagerActivity.this,WhoisActivity.class);
				TldManagerActivity.this.startActivity(intent);
				TldManagerActivity.this.finish();
			}
			
		});
		
		tldAddImage = (ImageView)this.findViewById(R.id.tld_add_btn);
		tldAddImage.setOnClickListener(new OnClickListener(){

			private RadioButton radioShow;
			@SuppressWarnings("unused")
			private RadioButton radioHide;
			private EditText serverText;
			private EditText tldNameText;
			private Button detectBtn;
			private Button saveBtn;
			private Dialog dialog = new Dialog(TldManagerActivity.this);
			@Override
			public void onClick(View v) {
				final TldEntity tld = new TldEntity();
				dialog.setContentView(R.layout.tld_add_dialog);
				dialog.setTitle(resources.getString(R.string.tld_add_title));
				tldNameText = (EditText)dialog.findViewById(R.id.tld_name);
				serverText = (EditText) dialog.findViewById(R.id.tld_detail_dialog_server);
				radioShow=(RadioButton)dialog.findViewById(R.id.tld_radio_show);
				radioHide=(RadioButton)dialog.findViewById(R.id.tld_radio_hide);
				radioShow.setChecked(true);
				saveBtn = (Button)dialog.findViewById(R.id.tld_save_btn);
				detectBtn = (Button)dialog.findViewById(R.id.tld_available_test_btn);
				detectBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						//将tld的server设置成用户填写的
						tld.setTldServer(serverText.getText().toString());
						if(serverText.getText().toString() == null || serverText.getText().toString().trim().equals("")){
							Toast.makeText(getApplication(), R.string.please_add_server, 2).show();
							return;
						}
						
						WhoisService whoisService = new WhoisService();
	    				whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld("google"));
	    				whoisService.setEncoding("UTF-8");
	    				whoisService.setTldEntity(tld);
	    				try {
	    					Domain domain = whoisService.execute();
	    					if(domain.getWhoisResult() != null){
	    						//可用
	    						Toast.makeText(getApplication(), R.string.tld_available_success, 2).show();
	    					}
						} catch (Exception e) {
								//不可用
							Toast.makeText(getApplication(), R.string.tld_available_fail, 2).show();
						}
					}
					
				});
				
				saveBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if(serverText.getText().toString() == null || serverText.getText().toString().trim().equals("")){
							Toast.makeText(getApplication(), R.string.please_add_server, 1).show();
							return;
						}
						
						if(tldNameText.getText().toString() == null || tldNameText.getText().toString().trim().equals("")){
							Toast.makeText(getApplication(), R.string.please_add_name, 1).show();
							return;
						}
						tld.setTldName(tldNameText.getText().toString());
						tld.setTldServer(serverText.getText().toString());
						if(radioShow.isChecked()){
							tld.setShow(1);
						}else{
							tld.setShow(0);
						}
						try {
							List<TldEntity> tlds = up.getUserPreference(false);
							for(TldEntity tldTemp : tlds){
								if(tldTemp.getTldName().equals(tld.getTldName())){
									Toast.makeText(getApplication(), R.string.please_add_another_name, 1).show();
									return;
								}
							}
							up.updateTldList(tld, "add");
							tldAdapter.addItem(tld);
							tldAdapter.notifyDataSetChanged();
							dialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				});
				dialog.show();
			}
			
		});
	}

	protected final class ItemClick implements OnItemClickListener{
		private UserPreferenceService up = new UserPreferenceService(TldManagerActivity.this);
		private ListView tldListView;
		private RadioButton radioShow;
		private RadioButton radioHide;
		private EditText serverText;
		private Button detectBtn;
		private Button saveBtn;
		private Button deleteBtn;
		private ProgressDialog loadingDialog = new ProgressDialog(TldManagerActivity.this);
		private Dialog dialogUpdate = new Dialog(TldManagerActivity.this);
		public ItemClick(ListView tldListView) {
			this.tldListView = tldListView; 
		}
		
		/** 
	     * 用Handler来更新UI 
	     */  
	    private Handler handler = new Handler(){  
	  
	        @Override  
	        public void handleMessage(Message msg) { 
            	loadingDialog.dismiss();  
	            if(msg.what == 0){
	            	//连接可用
	            	Toast.makeText(getApplication(), R.string.tld_available_success, 1).show();
	            }else if(msg.what == 1){
	            	//连接不可用 
	            	Toast.makeText(getApplication(), R.string.tld_available_fail, 1).show();
	            }else if(msg.what == 3){
	            	Toast.makeText(getApplication(), R.string.tld_save_error, 1).show();
	            }else if(msg.what == 2){
					dialogUpdate.dismiss();
					tldAdapter.notifyDataSetChanged();
	            }
	        }};  
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final TldEntity tld = (TldEntity)tldListView.getItemAtPosition(position);
			dialogUpdate.setContentView(R.layout.tld_detail_dialog);
			dialogUpdate.setTitle(tld.getTldName());
			
			
    		loadingDialog.setMessage(getResources().getString(R.string.loading));
    		loadingDialog.setCancelable(true);
    		
    		
    		
			serverText = (EditText) dialogUpdate.findViewById(R.id.tld_detail_dialog_server);
			serverText.setText(tld.getTldServer());
			radioShow=(RadioButton)dialogUpdate.findViewById(R.id.tld_radio_show);
			radioHide=(RadioButton)dialogUpdate.findViewById(R.id.tld_radio_hide);
			detectBtn = (Button)dialogUpdate.findViewById(R.id.tld_available_test_btn);
			detectBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					loadingDialog.show();
					 new Thread(){  
		                    public void run(){  
		                        try{
		        					//将tld的server设置成用户填写的
		                        	tld.setTldServer(serverText.getText().toString());
		        					WhoisService whoisService = new WhoisService();
		            				whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld("google"));
		            				whoisService.setEncoding("UTF-8");
		            				whoisService.setTldEntity(tld);
		            				try {
		            					Domain domain = whoisService.execute();
		            					if(domain.getWhoisResult() != null){
		            						//可用
		            						//
		            						handler.sendEmptyMessage(0);
		            					}
		        					} catch (Exception e) {
		        						e.printStackTrace();
		        						handler.sendEmptyMessage(1);
		        							//不可用
		        					}
	            					loadingDialog.cancel();  
		                        }catch(Exception ex){ 
		                        	ex.printStackTrace();
		                        	loadingDialog.cancel();  
		                        }  
		                    }  
		                }.start(); 
		    		
				}
				
			});
			//保存按钮
			saveBtn = (Button)dialogUpdate.findViewById(R.id.tld_save_btn);
			saveBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
		    		loadingDialog.show();
		    		new Thread(){  
	                    public void run(){ 
		                    	//将tld的server设置成用户填写的
		    					tld.setTldServer(serverText.getText().toString());
		    					if(radioShow.isChecked()){
		    						tld.setShow(1);
		    					}else{
		    						tld.setShow(0);
		    					}
		    					try {
		    						up.updateTldList(tld,"update");
		    						handler.sendEmptyMessage(2);  
		    					} catch (Exception e) {
		    						e.printStackTrace();
		    						handler.sendEmptyMessage(3);
		    						//Toast.makeText(getApplication(), R.string.tld_save_error, 1).show();
		    					}
	                    	}
	                    }.start();
		    		
				}
				
			});
			
			//删除按钮
			deleteBtn = (Button)dialogUpdate.findViewById(R.id.tld_delete_btn);
			deleteBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(TldManagerActivity.this);
					builder.setMessage(resources.getString(R.string.confirm_tld_delete)+tld.getTldName()+"?")
					       .setCancelable(false)
					       .setPositiveButton(resources.getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   try {
										up.updateTldList(tld,"delete");
										tldAdapter.deleteItem(tld);
										dialog.dismiss();
										dialogUpdate.dismiss();
										tldAdapter.notifyDataSetChanged();  
									} catch (Exception e) {
										e.printStackTrace();
										Toast.makeText(TldManagerActivity.this, resources.getString(R.string.tld_delete_error),1).show();
									}
					           }
					       })
					       .setNegativeButton(resources.getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					                dialogUpdate.dismiss();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
				
			});
			
			//显示状态
			if(tld.getShow() == 1){
				radioShow.setChecked(true);
			}else{
				radioHide.setChecked(true);
			}
			dialogUpdate.show();
		}
	
    }
}
