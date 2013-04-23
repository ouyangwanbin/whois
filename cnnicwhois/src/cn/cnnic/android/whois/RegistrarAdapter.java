package cn.cnnic.android.whois;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.DomainMark;
import cn.cnnic.android.whois.entity.Registrar;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;

@SuppressWarnings("unused")
public class RegistrarAdapter extends BaseAdapter {
	private List<Registrar> registrarList;
	private int resultItem;
	private LayoutInflater layoutInflater;
	private Context context;
	private UserPreferenceService up ;
	
	private Map<String , String> cache = new HashMap<String,String>();

	public RegistrarAdapter(Context context ,List<Registrar> registrarList, int resultItem) {
		this.registrarList = registrarList;
		this.resultItem = resultItem;
		this.context = context;
		this.up= new UserPreferenceService(context);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return registrarList.size();
	}

	
	public Object getItem(int position) {
		return registrarList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void addItem(Registrar registrar)
	{
		registrarList.add(registrar);
	}
	
	public void deleteItem(Registrar registrar)
	{
		registrarList.remove(registrar);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView registrarNameView;
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			registrarNameView = (TextView)convertView.findViewById(R.id.result_registrar);
			convertView.setTag(new DataWrapper(registrarNameView));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			registrarNameView = dataWrapper.registrarNameView;
		}
		Registrar registrar = registrarList.get(position);
		String registrarName = registrar.getRegistrarName();
		registrarNameView.setText(registrarName);

		return convertView;
	}
	private final class DataWrapper{
		public DataWrapper(TextView registrarNameView) {
			this.registrarNameView = registrarNameView;
		}
		public TextView registrarNameView;
	}

}
