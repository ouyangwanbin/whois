package cn.cnnic.android.whois;

import java.util.List;
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

	public MarkAdapter(Context context ,List<DomainMark> markList, int resultItem) {
		this.markList = markList;
		this.resultItem = resultItem;
		this.context = context;
		this.up= new UserPreferenceService(context);
		this.whoisService = new WhoisService();
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
		final TextView domainView;
		final TextView expireView;
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
		final DomainMark domainMark = markList.get(position);
		String domainName = domainMark.getDomainName();
		String whoisServer = domainMark.getWhoisServer();
		domainView.setText(domainName);
		
		asyncLoadExpire(expireView,domainName,whoisServer);
	
		return convertView;
	}
	
//	private void asyncLoadExpire(final TextView expire, String domain , String whois){
//		final String domainName = domain;
//		final String whoisServer = whois;
//		final Handler handler = new Handler(){
//			
//			
//			@Override
//			public void handleMessage(Message msg) {
//				String expireDate = (String)msg.obj;
//				if(expireDate != null && expire != null){
//					expire.setText(expireDate);
//				}
//			}
//		};
//		
//		Runnable r = new Runnable(){
//			public void run() {
//				whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(domainName));
//				whoisService.setEncoding("UTF-8");
//				TldEntity tld = new TldEntity(LanguageUtil.getDomainTld(domainName),whoisServer, 1);
//				whoisService.setTldEntity(tld);
//				Domain result = new Domain();
//				try {
//					result = whoisService.execute();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if("registered".equals(result.getDomainInfo())){
//					Matcher matcher = pattern.matcher(result.getWhoisResult());
//					if(matcher.find()){
//						Log.i("whoisLog",matcher.group());
//						handler.sendMessage(handler.obtainMessage(10, matcher.group()));
//					}
//				}
//			}
//		};
//		new Thread(r).start();
//	}
	
	
	private void asyncLoadExpire(TextView expireView, String domainName,
			String whoisServer) {
		AsyncExpireTask asyncExpireTask = new AsyncExpireTask(expireView);
		asyncExpireTask.execute(domainName,whoisServer);
	}
	
	private final class AsyncExpireTask extends AsyncTask<String,Integer,String>{
		private TextView expireView;
		public AsyncExpireTask(TextView expireView){
			this.expireView = expireView;
		}
		
		@Override
		protected String doInBackground(String... params) {
			whoisService.setDomainNameWithoutTld(LanguageUtil.getDomainNameWithoutTld(params[0]));
			whoisService.setEncoding("UTF-8");
			TldEntity tld = new TldEntity(LanguageUtil.getDomainTld(params[0]),params[1], 1);
			whoisService.setTldEntity(tld);
			Domain result = new Domain();
			try {
				result = whoisService.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if("registered".equals(result.getDomainInfo())){
				Matcher matcher = pattern.matcher(result.getWhoisResult());
				if(matcher.find()){
					return matcher.group();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result != null && expireView != null){
				expireView.setText(result);
			}
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
