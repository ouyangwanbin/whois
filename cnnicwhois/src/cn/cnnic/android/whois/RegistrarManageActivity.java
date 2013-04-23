package cn.cnnic.android.whois;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.Registrar;
import cn.cnnic.android.whois.service.UserPreferenceService;

public class RegistrarManageActivity extends Activity {
	private ListView registrarListView;
	private RegistrarAdapter registrarAdapter;
	private ImageView tldBackImage;
	private UserPreferenceService up = new UserPreferenceService(this);
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.registrar_manage);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_registrar);
		registrarListView = (ListView)this.findViewById(R.id.registrarListView);

		List<Registrar> registrarList = new ArrayList<Registrar>();
		try {
			registrarList = up.getRegistrars();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.data_load_error, 1);
			e.printStackTrace();
		}
		registrarAdapter = new RegistrarAdapter(this,registrarList,R.layout.registrar_item);
		registrarListView.setAdapter(registrarAdapter);
		registrarListView.setOnItemClickListener(new ItemClick(registrarListView));
		
		tldBackImage = (ImageView)this.findViewById(R.id.tld_back_btn);
		tldBackImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				RegistrarManageActivity.this.finish();
			}
			
		});
	}
	protected final class ItemClick  implements OnItemClickListener{
		private ListView registrarListView;
		public ItemClick(ListView registrarListView) {
			this.registrarListView = registrarListView; 
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
    		final Registrar registrar = (Registrar)registrarListView.getItemAtPosition(position);
    		
    		Uri uri = Uri.parse(registrar.getAddress());  
 		    Intent it = new Intent(Intent.ACTION_VIEW, uri);  
 		    startActivity(it);
		}
	
    }
}
