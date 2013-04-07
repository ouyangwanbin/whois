package cn.cnnic.android.whois;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class WhoisActivity extends Activity {

	private EditText domainText;
	private List<TldEntity> tlds;
	private UserPreferenceService up;
	private Button button;
	private TableLayout table;
	private static final int QUERY_ACCURATE = 999;
	private ProgressDialog loadingDialog;
	private Dialog domainDetailDialog;
	private TextView domainDetailText;
	private Resources resources;
	
	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);     
	private LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);     
	
	private TableLayout.LayoutParams TP_FW = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
	


	// 屏幕转换不重新onCreate
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else {

		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		up = new UserPreferenceService();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		resources = getResources();
		LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);//垂直布局
        Drawable drawable = resources.getDrawable(R.drawable.background);
        linearLayout.setBackgroundDrawable(drawable);
        View partView = getPartUI();
        linearLayout.addView(partView,LP_FW);
       
		//动态添加table
		table = new TableLayout(this);
		try {
			tlds = up.getUserPreference("tld-recommend.xml",true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(tlds != null && tlds.size()>0){
			int size = tlds.size();
			int rownum = (size/3)+1;
			for(int i = 0 ; i<rownum ; i++){
				TableRow row = new TableRow(this);
				row.setGravity(Gravity.CENTER);
				for(int j=0; j<3 && size > (3*i+j);j++){
					CheckBox checkBox = new CheckBox(this);
					checkBox.setText(tlds.get(3*i+j).getTldName());
					row.addView(checkBox);
				}
				table.addView(row);
			}
			linearLayout.addView(table,TP_FW);
		}
		setContentView(linearLayout,LP_FF);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		button = (Button) this.findViewById(R.id.button);
		domainText = (EditText) this.findViewById(R.id.domain);
		domainText.addTextChangedListener(new DomainTextWatcher());
	}

	
	private class DomainTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String str = s.toString();
			if (LanguageUtil.isChineseDomain(str)) {
				// 如果是中文
				for (int i = 0; i < table.getChildCount(); i++) {
					TableRow row = (TableRow) table.getChildAt(i);
					for (int j = 0; j < row.getChildCount(); j++) {
						CheckBox cbx = (CheckBox) row.getChildAt(j);
						if(cbx.getText().equals(".公司") || cbx.getText().equals(".网络") || cbx.getText().equals(".中国") ||  cbx.getText().equals(".cn")){
							cbx.setClickable(true);
						}else{
							cbx.setChecked(false);
							cbx.setClickable(false);
						}
					}
				}
			} else {
				// 如果是英文
				for (int i = 0; i < table.getChildCount(); i++) {
					TableRow row = (TableRow) table.getChildAt(i);
					for (int j = 0; j < row.getChildCount(); j++) {
						CheckBox cbx = (CheckBox) row.getChildAt(j);
						if(cbx.getText().equals(".公司") || cbx.getText().equals(".网络")){
							cbx.setChecked(false);
							cbx.setClickable(false);
						}else{
							cbx.setClickable(true);
						}
					}
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

	}

	public void queryDomain(View v) {
		// 点击查询，隐藏键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(button.getApplicationWindowToken(), 0);

		String domainName = domainText.getText().toString();
		if (domainName == null || domainName.trim().length() == 0) {
			Toast toast = Toast.makeText(getApplicationContext(),
					R.string.emptyinput, 1);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		List<String> domainList = new ArrayList<String>();
		for (int i = 0; i < table.getChildCount(); i++) {
			TableRow row = (TableRow) table.getChildAt(i);
			for (int j = 0; j < row.getChildCount(); j++) {
				CheckBox cbx = (CheckBox) row.getChildAt(j);
				if (cbx.isChecked()) {
					domainList.add(cbx.getText().toString());
				}
			}
		}
		if (domainList.size() > 0) {
			if(domainList.size() == 1){
				//精确查询
				Intent intent = new Intent(this,DisplayWhoisResultActivity.class);
	    		intent.putExtra("domainName", domainName+domainList.get(0));
	    		intent.putExtra("queryType", "accurate");
	    		loadingDialog = new ProgressDialog(this);
	    		loadingDialog.setMessage(getResources().getString(R.string.loading));
	    		loadingDialog.setCancelable(false);
	    		loadingDialog.show();
	    		startActivityForResult(intent, QUERY_ACCURATE);
			}else{
				//批量查询
				StringBuilder sb = new StringBuilder();
				Intent intent = new Intent(this, DisplayWhoisResultActivity.class);
				intent.putExtra("domainName", domainName);
				for (String tld : domainList) {
					sb.append(tld).append(",");
				}
				intent.putExtra("tlds", sb.deleteCharAt(sb.length() - 1).toString());
				startActivity(intent);
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					R.string.query_validate_unpass, 1);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == QUERY_ACCURATE) {
			loadingDialog.dismiss();
			domainDetailDialog = new Dialog(WhoisActivity.this);
			domainDetailDialog.setContentView(R.layout.result_detail);
			domainDetailText = (TextView) domainDetailDialog
					.findViewById(R.id.domainWhoisDetail);
			domainDetailDialog.setTitle(data.getStringExtra("domainName"));
			domainDetailText.setText(data.getStringExtra("domainResult"));
			domainDetailText.setOnClickListener(new DomainDetailClick());
			domainDetailDialog.show();

		}
	}

	/**
	 * 捕获双击事件，然后关闭dialog
	 * 
	 * @author ouyangwanbin
	 * 
	 */
	private final class DomainDetailClick implements OnClickListener {
		private long clickmil;

		@Override
		public void onClick(View v) {
			if (System.currentTimeMillis() - clickmil < 200) {
				domainDetailDialog.dismiss();
			}
			clickmil = System.currentTimeMillis();
		}

	}
	
	 private View getPartUI(){
	    	//布局填充服务，android内置服务
	    	LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	return inflater.inflate(R.layout.main, null);
	    }
}