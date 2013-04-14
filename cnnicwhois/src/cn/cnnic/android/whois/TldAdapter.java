package cn.cnnic.android.whois;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;

public class TldAdapter extends BaseAdapter {
	private List<TldEntity> tlds;
	private int resultItem;
	private LayoutInflater layoutInflater;
	private Context context;
	private UserPreferenceService up ;

	public TldAdapter(Context context ,List<TldEntity> tlds, int resultItem) {
		this.tlds = tlds;
		this.resultItem = resultItem;
		this.context = context;
		this.up= new UserPreferenceService(context);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return tlds.size();
	}

	
	public Object getItem(int position) {
		return tlds.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void addItem(TldEntity tld)
	{
		tlds.add(tld);
	}
	
	public void deleteItem(TldEntity tld)
	{
		for(int i=0 ; i<tlds.size();i++){
			if(tlds.get(i).getTldName().equals(tld.getTldName())){
				tlds.remove(i);
				break;
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		final TextView tldTextView;
		final ImageView imageView;
		final Drawable drawableImportant = context.getResources().getDrawable(R.drawable.important);
		final Drawable drawableNotImportant = context.getResources().getDrawable(R.drawable.notimportant);
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			tldTextView = (TextView)convertView.findViewById(R.id.result_tld);
			imageView = (ImageView)convertView.findViewById(R.id.result_favor);
			convertView.setTag(new DataWrapper(tldTextView , imageView));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			tldTextView = dataWrapper.tldTextView;
			imageView = dataWrapper.imageView;
		}
		final TldEntity tldEntity = tlds.get(position);
		tldTextView.setText(tldEntity.getTldName());
		if(tldEntity.getShow() == 1){
			//¿ÉÏÔÊ¾
			imageView.setImageDrawable(drawableImportant);
		}else{
			imageView.setImageDrawable(drawableNotImportant);
		}
		imageView.setTag(position);
		imageView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				TldEntity tld = tlds.get(index);
				if(drawableNotImportant == imageView.getDrawable()){
					tld.setShow(1);
					tlds.set(index, tld);
					imageView.setImageDrawable(drawableImportant);
				}else{
					tld.setShow(0);
					tlds.set(index, tld);
					imageView.setImageDrawable(drawableNotImportant);
				}
				try {
					up.saveUserPreference(tlds);
				} catch (Exception e) {
					e.printStackTrace();
				}
				notifyDataSetChanged();
			}
			
		});
//		modifyButton.setTag(position);
//		modifyButton.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, tldEntity.getTldName(), 2).show();
//			}
//			
//		});
//		deleteButton.setTag(position);
//		deleteButton.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setMessage(context.getResources().getString(R.string.confirm_tld_delete)+tldEntity.getTldName()+"?")
//				       .setCancelable(false)
//				       .setPositiveButton(context.getResources().getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
//				           public void onClick(DialogInterface dialog, int id) {
//				        	   	tlds.remove(index);
//								notifyDataSetChanged();
//								Toast.makeText(context, tldEntity.getTldName(), 2).show();
//				           }
//				       })
//				       .setNegativeButton(context.getResources().getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
//				           public void onClick(DialogInterface dialog, int id) {
//				                dialog.cancel();
//				           }
//				       });
//				AlertDialog alert = builder.create();
//				alert.show();
//			}
//		});
		return convertView;
	}
	
	private final class DataWrapper{
		public DataWrapper(TextView tldTextView , ImageView imageView) {
			this.tldTextView = tldTextView;
			this.imageView = imageView;
		}
		public TextView tldTextView;
		public ImageView imageView;
	}

}
