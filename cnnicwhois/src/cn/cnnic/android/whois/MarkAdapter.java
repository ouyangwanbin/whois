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
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import cn.cnnic.android.whois.utils.LanguageUtil;

@SuppressWarnings("unused")
public class MarkAdapter extends BaseAdapter {
	private List<DomainMark> markList;
	private int resultItem;
	private LayoutInflater layoutInflater;
	private Context context;
	private UserPreferenceService up ;
	private Pattern pattern = Pattern.compile("Expiration Date.*|Expiry date.*|expires.*|renewal[\\s]*:.*|Expire Date.*|\\[\\$BM-8z4\\|8B\\(B\\][\\s]*.*|Expired.*",Pattern.CASE_INSENSITIVE);
	private WhoisService whoisService;
	
	private Map<String , String> cache = new HashMap<String,String>();

	public MarkAdapter(Context context ,List<DomainMark> markList, int resultItem) {
		this.markList = markList;
		this.resultItem = resultItem;
		this.context = context;
		this.up= new UserPreferenceService(context);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return markList.size();
	}

	
	public Object getItem(int position) {
		return markList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void addItem(DomainMark domainMark)
	{
		markList.add(domainMark);
	}
	
	public void deleteItem(DomainMark domainMark)
	{
		for(int i=0 ; i<markList.size();i++){
			if(markList.get(i).getDomainName().equals(domainMark.getDomainName())){
				markList.remove(i);
				break;
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView domainView;
		TextView expireView;
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			domainView = (TextView)convertView.findViewById(R.id.result_mark);
			expireView = (TextView)convertView.findViewById(R.id.result_expireDate);
			convertView.setTag(new DataWrapper(domainView , expireView));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			domainView = dataWrapper.domainView;
			expireView = dataWrapper.expireView;
		}
		DomainMark domainMark = markList.get(position);
		String domainName = domainMark.getDomainName();
		String whoisServer = domainMark.getWhoisServer();
		domainView.setText(domainName);
		expireView.setText(context.getResources().getString(R.string.loading));
		expireView.setTag(domainName);
		if(null == cache.get(domainName)){
			asyncLoadExpire(expireView,domainName,whoisServer);
		}else{
			expireView.setText(cache.get(domainName));
		}
		return convertView;
	}
	
	private void asyncLoadExpire(TextView expireView, String domainName,
			String whoisServer) {
		AsyncExpireTask asyncExpireTask = new AsyncExpireTask(expireView);
		asyncExpireTask.execute(domainName,whoisServer);
	}
	
	private final class AsyncExpireTask extends AsyncTask<String,Integer,Domain>{
		private TextView expireView;
		public AsyncExpireTask(TextView expireView){
			this.expireView = expireView;
		}
		
		@Override
		protected Domain doInBackground(String... params) {
			WhoisService whoisService = new WhoisService();
			whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(params[0]));
			whoisService.setEncoding("UTF-8");
			TldEntity tld = new TldEntity(LanguageUtil.getDomainTld(params[0]),params[1], 1);
			whoisService.setTldEntity(tld);
			Domain result = new Domain();
			try {
				result = whoisService.execute();
			} catch (Exception e) {
				result.setDomainInfo("exception");
				e.printStackTrace();
			
			}
			return result;
		}

		@Override
		protected void onPostExecute(Domain domain) {
			String result = "";
			if(domain != null && expireView != null && expireView.getTag().equals(domain.getDomainName())){
				if("unregistered".equals(domain.getDomainInfo())){
					result = context.getResources().getString(R.string.domain_has_not_registered);
				}else if("registered".equals(domain.getDomainInfo())){
					Matcher matcher = pattern.matcher(domain.getWhoisResult());
					if(matcher.find()){
						result =matcher.group();
					}
				}else{
					result = context.getResources().getString(R.string.data_query_error);
				}
			}
			cache.put(domain.getDomainName(),result);
			expireView.setText(result);
		}
		
	}

	private final class DataWrapper{
		public DataWrapper(TextView domainView , TextView expireView) {
			this.domainView = domainView;
			this.expireView = expireView;
		}
		public TextView domainView;
		public TextView expireView;
	}

}
