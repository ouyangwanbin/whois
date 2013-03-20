package cn.cnnic.android.whois;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class MainActivity extends Activity {
	
	private CheckBox allCheckBox;
	private EditText domainText;
	private Button button;
	private TableLayout table;
	private TableLayout chtable;
	private static final int QUERY_ACCURATE = 999;
	private TextView domainResult;
	private ProgressDialog loadingDialog;
	private Dialog domainDetailDialog;
	private TextView domainDetailText;

	//屏幕转换不重新onCreate
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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
        button = (Button)this.findViewById(R.id.button);
        table = (TableLayout) this.findViewById(R.id.table);
        allCheckBox = (CheckBox)this.findViewById(R.id.allcheck);
        domainResult = (TextView)this.findViewById(R.id.domainResult);
        for(int i=0 ; i < table.getChildCount();i++){
    		TableRow row = (TableRow) table.getChildAt(i);
    		for(int j = 0 ; j < row.getChildCount();j++){
    			CheckBox cbx = (CheckBox) row.getChildAt(j);
    			cbx.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(!isChecked){
							allCheckBox.setChecked(false);
						}
					}});
    		}
        }
        chtable = (TableLayout) this.findViewById(R.id.chtable);
        for(int i=0 ; i < chtable.getChildCount();i++){
    		TableRow row = (TableRow) chtable.getChildAt(i);
    		for(int j = 0 ; j < row.getChildCount();j++){
    			CheckBox cbx = (CheckBox) row.getChildAt(j);
    			cbx.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(!isChecked){
							allCheckBox.setChecked(false);
						}
					}});
    		}
        }
        domainText = (EditText) this.findViewById(R.id.domain);
        domainText.addTextChangedListener(new DomainTextWatcher());
    }
    
    private class DomainTextWatcher implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			domainResult.setVisibility(View.GONE);
			allCheckBox.setVisibility(View.VISIBLE);
			String str = s.toString();
			if(LanguageUtil.isChineseDomain(str)){
				//如果是中文
				table.setVisibility(View.GONE);
				chtable.setVisibility(View.VISIBLE);
			}else{
				//如果是英文
				table.setVisibility(View.VISIBLE);
				chtable.setVisibility(View.GONE);
			}
			if(str.indexOf(".")>-1){
				changeTableCheckClickStatus(table,false);
				changeTableCheckClickStatus(chtable,false);
				allCheckBox.setClickable(false);
			}else{
				changeTableCheckClickStatus(table,true);
				changeTableCheckClickStatus(chtable,true);
				allCheckBox.setClickable(true);
			}
			
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
    	
    }

	/**
     * 
     * 全选按钮
     * @param v
     */
    public void changeCheckBox(View v){
    	boolean checked = allCheckBox.isChecked();
    	if(table.getVisibility() == View.VISIBLE){
    		for(int i=0 ; i < table.getChildCount();i++){
	        		TableRow row = (TableRow) table.getChildAt(i);
	        		for(int j = 0 ; j < row.getChildCount();j++){
	        			CheckBox cbx = (CheckBox) row.getChildAt(j);
	        			cbx.setChecked(checked);
        		}
        	}
    	}else if(chtable.getVisibility() == View.VISIBLE){
    		for(int i=0 ; i < chtable.getChildCount();i++){
	        		TableRow row = (TableRow) chtable.getChildAt(i);
	        		for(int j = 0 ; j < row.getChildCount();j++){
	        			CheckBox cbx = (CheckBox) row.getChildAt(j);
	        			cbx.setChecked(checked);
        		}
        	}
    	}else{
    		
    	}
    	
    }
    
    public void queryDomain(View v){
    	//点击查询，隐藏键盘
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(button.getApplicationWindowToken(),0);
    	
    	String domainName = domainText.getText().toString();
    	if(domainName == null || domainName.trim().length() == 0){
    		Toast toast = Toast.makeText(getApplicationContext(), R.string.emptyinput, 1);
    			   toast.setGravity(Gravity.CENTER, 0, 0);
    			   toast.show();
    		return;
    	}
    	List<String> domainList = new ArrayList<String>();
    	if(domainName.indexOf(".")>-1 && domainName.indexOf(".") != (domainName.length()-1)){
    		Intent intent = new Intent(this,DisplayResultActivity.class);
    		intent.putExtra("domainName", domainName);
    		intent.putExtra("queryType", "accurate");
    		loadingDialog = new ProgressDialog(this);
    		loadingDialog.setMessage(getResources().getString(R.string.loading));
    		loadingDialog.setCancelable(false);
    		loadingDialog.show();
    		startActivityForResult(intent, QUERY_ACCURATE);
    	}else{
        	if(table.getVisibility() == View.VISIBLE){
        		for(int i=0 ; i < table.getChildCount();i++){
            		TableRow row = (TableRow) table.getChildAt(i);
            		for(int j = 0 ; j < row.getChildCount();j++){
            			CheckBox cbx = (CheckBox) row.getChildAt(j);
            			if(cbx.isChecked()){
            				domainList.add(cbx.getText().toString());
            			}
            		}
            	}
        	}else if(chtable.getVisibility() == View.VISIBLE){
        		for(int i=0 ; i < chtable.getChildCount();i++){
            		TableRow row = (TableRow) chtable.getChildAt(i);
            		for(int j = 0 ; j < row.getChildCount();j++){
            			CheckBox cbx = (CheckBox) row.getChildAt(j);
            			if(cbx.isChecked()){
            				if(".cn".equals(cbx.getText().toString())){
            					domainList.add(".cdn");
            				}else{
                				domainList.add(cbx.getText().toString());
            				}
            			}
            		}
            	}
        	}else{
        		
        	}
        	
        	if(domainList.size() > 0){
        		StringBuilder sb = new StringBuilder();
        		Intent intent = new Intent(this,DisplayResultActivity.class);
        		intent.putExtra("domainName", domainName);
        		for(String tld : domainList){
        			sb.append(tld).append(",");
        		}
        		intent.putExtra("tlds",sb.deleteCharAt(sb.length()-1).toString());
        		startActivity(intent);
        	}else{
        		Toast toast = Toast.makeText(getApplicationContext(), R.string.query_validate_unpass, 1);
 			   	toast.setGravity(Gravity.CENTER, 0, 0);
 			   	toast.show();
        	}
    	}

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == QUERY_ACCURATE) {
			loadingDialog.dismiss();
			domainDetailDialog = new Dialog(MainActivity.this);
			domainDetailDialog.setContentView(R.layout.result_detail);
        	domainDetailText=(TextView) domainDetailDialog.findViewById(R.id.domainWhoisDetail);
        	domainDetailDialog.setTitle(data.getStringExtra("domainName"));
        	domainDetailText.setText(data.getStringExtra("domainResult"));
        	domainDetailText.setOnClickListener(new DomainDetailClick());
            domainDetailDialog.show();

		}
	}
    
	private void changeTableCheckClickStatus(TableLayout table , boolean status){
		for(int i=0 ; i < table.getChildCount();i++){
    		TableRow row = (TableRow) table.getChildAt(i);
    		for(int j = 0 ; j < row.getChildCount();j++){
    			CheckBox cbx = (CheckBox) row.getChildAt(j);
    			cbx.setClickable(status);
    		}
    	}
	}
    
	 /**
     * 捕获双击事件，然后关闭dialog
     * @author ouyangwanbin
     *
     */
    private final class DomainDetailClick implements OnClickListener{
    	private long clickmil;
		@Override
		public void onClick(View v) {
			if(System.currentTimeMillis()-clickmil < 200){
				domainDetailDialog.dismiss();
			}
			clickmil=System.currentTimeMillis();
		}
		
    }
}