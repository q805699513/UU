package com.uugty.uu.order.insure;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.entity.TouristEntity;
import com.uugty.uu.entity.TouristEntity.Tourist;
import com.uugty.uu.order.UUPayActivity;

import java.util.ArrayList;
import java.util.List;

public class InsureActivity extends BaseActivity implements OnClickListener{
	private ImageView tourist_list_back;
	private TextView tourist_list_confirm;
	private TextView mInsureType;
	private ListView mListView;
	private List<Tourist> list = new ArrayList<Tourist>(); //联系人数据
	private List<String> contactId = new ArrayList<String>(); //联系人数据
	private PersonAdapter adapter;
	private RelativeLayout mInsureRelative;//选择保险类型
	private String type="0";//返回选择的保险类型
	private String alltype="0";//返回选择的保险类型

	@Override
	protected int getContentLayout() {
		return R.layout.insure_card_list_layout;
	}

	@Override
	protected void initGui() {
		tourist_list_back=(ImageView) findViewById(R.id.tourist_list_back);
		tourist_list_confirm=(TextView) findViewById(R.id.tourist_list_confirm);
		mInsureType=(TextView) findViewById(R.id.insure_txt);
		mInsureRelative = (RelativeLayout) findViewById(R.id.insure_selected_relative);
		mListView=(ListView) findViewById(R.id.person_card_list);

	}

	@Override
	protected void initData() {
		if(getIntent()!=null){
			list = (List<TouristEntity.Tourist>) getIntent().getSerializableExtra("list");
			if(null != getIntent().getStringExtra("type") && !"".equals(getIntent().getStringExtra("type"))){
				alltype = getIntent().getStringExtra("type");
				if(alltype.contains("1")){
					mInsureType.setText("￥5/天");
				}else if(alltype.contains("2")){
					mInsureType.setText("￥10/天");
				}else if(alltype.contains("3")){
					mInsureType.setText("￥15/天");
				}else{
					mInsureType.setText(" ");
				}
			}
		}
		adapter=new PersonAdapter(ctx, list);
		mListView.setAdapter(adapter);
	}

	@Override
	protected void initAction() {
		tourist_list_confirm.setOnClickListener(this);
		tourist_list_back.setOnClickListener(this);
		mInsureRelative.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		Intent intent=new Intent();
		switch (v.getId()) {
			case R.id.insure_selected_relative:
				intent.setClass(InsureActivity.this,SelectActivity.class);
				startActivityForResult(intent,1);
				break;
		case R.id.tourist_list_confirm:
			String allId = "";
			contactId.clear();
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getContactStatus().equals("1")){
					contactId.add(list.get(i).getContactId());
				}
			}
			for (int i = 0; i < contactId.size(); i++) {
				if (i == contactId.size() - 1) {
					allId+=contactId.get(i);
					alltype+=type;
				}else{
					allId+=contactId.get(i)+",";
					alltype+=type+",";
				}
			}

			intent.setClass(ctx, UUPayActivity.class);
			intent.putExtra("type",alltype);
			intent.putExtra("num", contactId.size()+"");
			intent.putExtra("allId",allId);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.tourist_list_back:
			finish();
		break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			switch (requestCode) {
			case 1:
				type = data.getStringExtra("type");
				if(null != type) {
					if ("1".equals(type)) {
						mInsureType.setText("￥5/天");
					} else if ("2".equals(type)) {
						mInsureType.setText("￥10/天");
					} else if ("3".equals(type)) {
						mInsureType.setText("￥15/天");
					}
				}else{
					mInsureType.setText("去选择");
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		list.clear();
	}

	class PersonAdapter extends BaseAdapter{
		private Context context;
		private List<Tourist> ls=new ArrayList<Tourist>();
		
		public PersonAdapter(Context context, List<Tourist> ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			return ls.size();
		}

		@Override
		public Object getItem(int position) {
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if(view==null){
				holder=new ViewHolder();
				view=LayoutInflater.from(context).inflate(R.layout.insure_card_list_item_layout, null);
				holder.touristname_selected_img=(ImageView) view.findViewById(R.id.touristname_selected_img);
				holder.touristname_text=(TextView) view.findViewById(R.id.touristname_text);
				holder.tourist_id_card_text=(TextView) view.findViewById(R.id.tourist_id_card_text);
				holder.contact_check = (LinearLayout) view.findViewById(R.id.contact_check);

				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}

			holder.touristname_text.setText(ls.get(position).getContactName());
//			holder.tourist_id_card_text.setText(replaceSubString(ls.get(position).getContactIDCard()));
			holder.tourist_id_card_text.setText(ls.get(position).getContactIDCard());
			if (ls.get(position).getContactStatus().equals("1")) {
				holder.touristname_selected_img.setImageResource(R.drawable.lzh_check_on);
			} else {
				holder.touristname_selected_img.setImageResource(R.drawable.lzh_check_off);
				}
			holder.contact_check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(ls.get(position).getContactStatus().equals("1")){
							ls.get(position).setContactStatus("0");
						}else{
							ls.get(position).setContactStatus("1");	
						}
						notifyDataSetChanged();	
				}
			});
			return view;
		}
		class ViewHolder{
			ImageView touristname_selected_img;
			LinearLayout contact_check;
			TextView touristname_text,tourist_id_card_text;
		}
	}
}
