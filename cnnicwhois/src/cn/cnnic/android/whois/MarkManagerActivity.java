package cn.cnnic.android.whois;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import cn.cnnic.android.whois.entity.DomainMark;
import cn.cnnic.android.whois.service.UserPreferenceService;
@SuppressWarnings("unused")
public class MarkManagerActivity extends Activity {
	private ListView markListView;
	private MarkAdapter markAdapter;
	private ImageView tldBackImage;
	private Resources resources;
	private static final int QUERY_ACCURATE = 999;
	private UserPreferenceService up = new UserPreferenceService(this);
	private ProgressDialog loadingDialog;
	private Dialog domainDetailDialog;
	private TextView domainDetailText;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadingDialog = new ProgressDialog(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.mark_manage);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_mark);
		resources = getResources();
		
		markListView = (ListView)this.findViewById(R.id.markListView);
		Intent intent = getIntent();		
		@SuppressWarnings("unchecked")
		List<DomainMark> markList =(List<DomainMark>) intent.getSerializableExtra("markList");
		markAdapter = new MarkAdapter(this,markList,R.layout.mark_item);
		markListView.setAdapter(markAdapter);
		markListView.setOnItemClickListener(new ItemClick(markListView));
		
		tldBackImage = (ImageView)this.findViewById(R.id.tld_back_btn);
		tldBackImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MarkManagerActivity.this,WhoisActivity.class);
				MarkManagerActivity.this.startActivity(intent);
				MarkManagerActivity.this.finish();
			}
			
		});
	}
	protected final class ItemClick  implements OnItemClickListener{
		private UserPreferenceService up = new UserPreferenceService(MarkManagerActivity.this);
		private ListView markListView;
		public ItemClick(ListView markListView) {
			this.markListView = markListView; 
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
    		loadingDialog.setMessage(getResources().getString(R.string.loading));
    		loadingDialog.setCancelable(true);
    		loadingDialog.show();
    		
    		final DomainMark domainMark = (DomainMark)markListView.getItemAtPosition(position);
    		//¾«È·²éÑ¯
			Intent intent = new Intent(MarkManagerActivity.this,DisplayWhoisResultActivity.class);
    		intent.putExtra("domainName", domainMark.getDomainName());
    		intent.putExtra("queryType", "accurate");
    		loadingDialog.setMessage(getResources().getString(R.string.loading));
    		loadingDialog.setCancelable(true);
    		loadingDialog.show();
    		startActivityForResult(intent, QUERY_ACCURATE);
		}
	
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == QUERY_ACCURATE) {
			loadingDialog.dismiss();
			domainDetailDialog = new Dialog(MarkManagerActivity.this);
			domainDetailDialog.setContentView(R.layout.result_detail);
			domainDetailText = (TextView) domainDetailDialog
					.findViewById(R.id.domainWhoisDetail);
			final String domainName = data.getStringExtra("domainName");
			domainDetailDialog.setTitle(domainName);
			final DomainMark domainMark = new DomainMark(domainName);
			Button markBtn = (Button)domainDetailDialog.findViewById(R.id.domainMarkBtn);
			markBtn.setText(R.string.mark_delete);
			markBtn.setVisibility(View.VISIBLE);
			markBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						try {
							up.updateMarkList(domainName, "delete");
							markAdapter.deleteItem(domainMark);
							domainDetailDialog.dismiss();
							markAdapter.notifyDataSetChanged(); 
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), R.string.tld_delete_error,1);
							e.printStackTrace();
						}
					}
	            	
	            });
			domainDetailText.setText(data.getStringExtra("domainResult"));
			domainDetailDialog.show();
		}
	}
}
