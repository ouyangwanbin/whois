package cn.cnnic.android.whois;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class WhoisAdapter extends BaseAdapter {
	private List<Domain> domainList;
	private int resultItem;
	private LayoutInflater layoutInflater;
	private UserPreferenceService up ;
	private List<TldEntity> tldList;

	public WhoisAdapter(Context context ,List<Domain> domainList, int resultItem) {
		this.domainList = domainList;
		this.resultItem = resultItem;
		up = new UserPreferenceService(context);
		try {
			this.tldList = up.getUserPreference(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return domainList.size();
	}

	
	public Object getItem(int position) {
		return domainList.get(position);
//		WhoisService whoisService = new WhoisService();
//		whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(domain.getDomainName()));
//		whoisService.setEncoding("UTF-8");
//		String tldName = LanguageUtil.getDomainTld(domain.getDomainName());
//		String whoisServer = "";
//		for(TldEntity t : tldList){
//			if(tldName.equals(t.getTldName())){
//				whoisServer = t.getTldServer();
//			}
//		}
//		TldEntity tld = new TldEntity(LanguageUtil.getDomainTld(domain.getDomainName()),whoisServer, 1);
//		whoisService.setTldEntity(tld);
//		try {
//			domain = whoisService.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return domain;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView domainNameTextView = null;
		TextView domainIsRegView = null;
		ImageView imageView = null;
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			domainNameTextView = (TextView)convertView.findViewById(R.id.resultDomain);
			domainIsRegView = (TextView)convertView.findViewById(R.id.resultText);
			imageView = (ImageView)convertView.findViewById(R.id.favorite);
			convertView.setTag(new DataWrapper(domainNameTextView,domainIsRegView,imageView));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			domainNameTextView = dataWrapper.domainNameTextView;
			domainIsRegView = dataWrapper.domainIsRegView;
			imageView = dataWrapper.imageView;
		}
		Domain domain = domainList.get(position);
		try {
			if(up.isMarked(domain.getDomainName())){
				Log.i("whoisLog", "mark : " + domain.getDomainName());
				imageView.setVisibility(View.VISIBLE);
			}else{
				imageView.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncLoadResult(domainIsRegView,domain.getDomainName(),position);
		domainNameTextView.setText(domain.getDomainName());
		return convertView;
	}
	
	
	private void asyncLoadResult(TextView domainIsRegView, String domainName , int position) {
		AsyncExpireTask asyncExpireTask = new AsyncExpireTask(domainIsRegView);
		asyncExpireTask.execute(domainName,String.valueOf(position));
	}
	
	private final class AsyncExpireTask extends AsyncTask<String,Integer,Domain>{
		private TextView domainIsRegView;
		public AsyncExpireTask(TextView domainIsRegView){
			this.domainIsRegView = domainIsRegView;
		}
		
		@Override
		protected Domain doInBackground(String... params) {
			WhoisService whoisService = new WhoisService();
			whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(params[0]));
			whoisService.setEncoding("UTF-8");
			String tldName = LanguageUtil.getDomainTld(params[0]);
			String whoisServer = "";
			for(TldEntity t : tldList){
				if(tldName.equals(t.getTldName())){
					whoisServer = t.getTldServer();
				}
			}
			TldEntity tld = new TldEntity(LanguageUtil.getDomainTld(params[0]),whoisServer, 1);
			whoisService.setTldEntity(tld);
			Domain result = new Domain();
			try {
				result = whoisService.execute();
				Domain domain = domainList.get(Integer.parseInt(params[1]));
				domain.setWhoisResult(result.getWhoisResult());
				domain.setDomainInfo(result.getWhoisResult());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
			
		}
		
		@Override
		protected void onPostExecute(Domain result) {
			if(result != null && domainIsRegView != null){
				if("unregistered".equals(result.getDomainInfo())){
					domainIsRegView.setText(R.string.domain_has_not_registered);
					domainIsRegView.setTextColor(Color.BLUE);
				}else if("registered".equals(result.getDomainInfo())){
					domainIsRegView.setText(R.string.domain_has_registered);
					domainIsRegView.setTextColor(Color.RED);
				}else{
					domainIsRegView.setText(R.string.data_query_error);
				}
			}
		}
	}
	
	private final class DataWrapper{
		public DataWrapper(TextView domainNameTextView, TextView domainIsRegView , ImageView imageView) {
			this.domainNameTextView = domainNameTextView;
			this.domainIsRegView = domainIsRegView;
			this.imageView = imageView;
		}
		public TextView domainNameTextView;
		public TextView domainIsRegView;
		public ImageView imageView;
	}

}
