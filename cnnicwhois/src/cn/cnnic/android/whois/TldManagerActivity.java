package cn.cnnic.android.whois;

import java.util.List;

import cn.cnnic.android.whois.entity.TldEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class TldManagerActivity extends Activity {
	private ListView tldListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tld_manage);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_tld);
		tldListView = (ListView)this.findViewById(R.id.tldListView);
		Intent intent = getIntent();		
		@SuppressWarnings("unchecked")
		List<TldEntity> tlds =(List<TldEntity>) intent.getSerializableExtra("tlds");
		tldListView.setAdapter(new TldAdapter(this,tlds,R.layout.tld_item));
	}
	
	public void modityTld(View v){
		
	}
	
	public void deleteTld(View v){
		
	}
}
