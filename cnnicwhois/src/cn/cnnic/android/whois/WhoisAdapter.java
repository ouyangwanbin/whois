package cn.cnnic.android.whois;

import java.util.List;

import cn.cnnic.android.whois.entity.Domain;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WhoisAdapter extends BaseAdapter {
	private List<Domain> domainList;
	private int resultItem;
	private LayoutInflater layoutInflater;

	public WhoisAdapter(Context context ,List<Domain> domainList, int resultItem) {
		this.domainList = domainList;
		this.resultItem = resultItem;
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return domainList.size();
	}

	
	public Object getItem(int position) {
		return domainList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView domainNameTextView = null;
		TextView domainIsRegView = null;
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			domainNameTextView = (TextView)convertView.findViewById(R.id.resultDomain);
			domainIsRegView = (TextView)convertView.findViewById(R.id.resultText);
			convertView.setTag(new DataWrapper(domainNameTextView,domainIsRegView));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			domainNameTextView = dataWrapper.domainNameTextView;
			domainIsRegView = dataWrapper.domainIsRegView;
		}
		Domain domain = domainList.get(position);
		if("unregistered".equals(domain.getDomainInfo())){
			domainIsRegView.setText(R.string.domain_has_not_registered);
			domainIsRegView.setTextColor(Color.BLUE);
		}else if("registered".equals(domain.getDomainInfo())){
			domainIsRegView.setText(R.string.domain_has_registered);
			domainIsRegView.setTextColor(Color.RED);
		}else{
			domainIsRegView.setText(R.string.data_query_error);
		}
		domainNameTextView.setText(domain.getDomainName());
		return convertView;
	}
	
	private final class DataWrapper{
		public DataWrapper(TextView domainNameTextView, TextView domainIsRegView) {
			this.domainNameTextView = domainNameTextView;
			this.domainIsRegView = domainIsRegView;
		}
		public TextView domainNameTextView;
		public TextView domainIsRegView;
	}

}
