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

import java.util.ArrayList;
import java.util.List;

public class InsureActivity extends BaseActivity implements OnClickListener{
	private ImageView tourist_list_back;
	private TextView tourist_list_confirm;
	private ListView mListView;
	private List<Tourist> list = new ArrayList<Tourist>(); //联系人数据
	private PersonAdapter adapter;
	private RelativeLayout mInsureRelative;//选择保险类型

	@Override
	protected int getContentLayout() {
		return R.layout.insure_card_list_layout;
	}

	@Override
	protected void initGui() {
		if(getIntent()!=null){
			list = (List<TouristEntity.Tourist>) getIntent().getSerializableExtra("list");
		}

		tourist_list_back=(ImageView) findViewById(R.id.tourist_list_back);
		tourist_list_confirm=(TextView) findViewById(R.id.tourist_list_confirm);
		mInsureRelative = (RelativeLayout) findViewById(R.id.insure_selected_relative);
		mListView=(ListView) findViewById(R.id.person_card_list);
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
				startActivity(intent);
				break;
		case R.id.tourist_list_confirm:
//
//			if(ilist.size()<1){
//				CustomToast.makeText(ctx, 0, "请选择出行人", 300).show();
//			}else{
//				intent.setClass(ctx, UUPayActivity.class);
//				intent.putExtra("num", ilist.size()+"");
//				intent.putExtra("name", name);
//				intent.putExtra("allId",all);
//				setResult(RESULT_OK, intent);
//				finish();
//			}
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
//			case ADDTOURIST:
//				onRefresh();
//				break;
			default:
				break;
			}
		}
	}
	@Override  
	protected void initData() {

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
