package cn.cnnic.android.whois;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.cnnic.android.whois.entity.TldEntity;

public class TldAdapter extends BaseAdapter {
	private List<TldEntity> tlds;
	private int resultItem;
	private LayoutInflater layoutInflater;
	private Context context;

	public TldAdapter(Context context ,List<TldEntity> tlds, int resultItem) {
		this.tlds = tlds;
		this.resultItem = resultItem;
		this.context = context;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		TextView tldTextView = null;
		Button modifyButton = null;
		Button deleteButton = null;
		if(convertView == null){
			convertView = layoutInflater.inflate(resultItem, null);
			tldTextView = (TextView)convertView.findViewById(R.id.result_tld);
			modifyButton = (Button)convertView.findViewById(R.id.tld_modify_btn);
			deleteButton = (Button)convertView.findViewById(R.id.tld_delete_btn);
			convertView.setTag(new DataWrapper(tldTextView,modifyButton,deleteButton));
		}else{
			DataWrapper dataWrapper = (DataWrapper)convertView.getTag();
			tldTextView = dataWrapper.tldTextView;
			modifyButton = dataWrapper.modifyButton;
			deleteButton = dataWrapper.deleteButton;
		}
		final TldEntity tldEntity = tlds.get(position);
		tldTextView.setText(tldEntity.getTldName());
		modifyButton.setTag(position);
		modifyButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(context, tldEntity.getTldName(), 2).show();
			}
			
		});
		deleteButton.setTag(position);
		deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(context.getResources().getString(R.string.confirm_tld_delete)+tldEntity.getTldName()+"?")
				       .setCancelable(false)
				       .setPositiveButton(context.getResources().getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   	tlds.remove(index);
								notifyDataSetChanged();
								Toast.makeText(context, tldEntity.getTldName(), 2).show();
				           }
				       })
				       .setNegativeButton(context.getResources().getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		return convertView;
	}
	
	private final class DataWrapper{
		public DataWrapper(TextView tldTextView , Button modifyButton , Button deleteButton) {
			this.tldTextView = tldTextView;
			this.modifyButton = modifyButton;
			this.deleteButton = deleteButton;
		}
		public TextView tldTextView;
		public Button modifyButton;
		public Button deleteButton;
	}

}
