package com.uugty.uu.discount.c;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.discount.c.DiscountSelectActivity.DiscountSelAdapter.ViewHolder;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;
import com.uugty.uu.order.UUOrederPayActivity;
import com.uugty.uu.order.UUPayActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscountSelectActivity extends BaseActivity{
	
	private ListView mListView;//代金券列表
	private TextView mDiscountImg;//选择代金券
	private LinearLayout mDiscountLayout;
	private Button mButton;//确定
	private Drawable noclick,click;
	
	private String isNotRec = "1";//是否选择暂不使用
	private String from = "";//从哪个页面跳转
	private String DiscountId; //代金券id
	private String UserId;//用户和代金券关联表主键id
	private String discountMoney = "0";//代金券金额
	private DiscountSelAdapter adapter;
	private List<DiscountEntity> ls = new ArrayList<DiscountEntity>();

	@Override
	protected int getContentLayout() {
		return R.layout.activity_discount_selected;
	}

	@Override
	protected void initGui() {
		
		if (null != getIntent()) {
			ls = (List<DiscountEntity>) getIntent().getSerializableExtra("list");
			from = getIntent().getStringExtra("from");
		}
		Resources res = ctx.getResources();
		noclick = res.getDrawable(R.drawable.pay_noclick);
		click = res.getDrawable(R.drawable.pay_click);
		
		//display对象获取屏幕的宽高
		Display dis = getWindowManager().getDefaultDisplay();
		//调整弹出框位置
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = dis.getHeight()/2;
		lp.gravity = Gravity.BOTTOM;
		win.setAttributes(lp);
		
		mListView = (ListView) findViewById(R.id.discount_selected_listview);
		mDiscountImg = (TextView) findViewById(R.id.discount_img);
		mDiscountLayout = (LinearLayout) findViewById(R.id.discount_linear);
		mButton = (Button) findViewById(R.id.discount_selected_commit);
		adapter = new DiscountSelAdapter(ctx, ls);
		mListView.setAdapter(adapter);
		mDiscountLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 重置listview所有状态为未选中
				for (String key : adapter.states.keySet()) {
					adapter.states.put(key, false);

				}
				isNotRec = "1";
				adapter.notifyDataSetChanged();
				mDiscountImg.setBackgroundDrawable(click);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				// 重置，确保最多只有一项被选中
				for (String key : adapter.states.keySet()) {
					adapter.states.put(key, false);

				}
				//改变状态值
				adapter.states.put(String.valueOf(position), true);
				isNotRec = "0";
				DiscountId = ls.get(position).getCouponId();
				UserId = ls.get(position).getCouponUserId();
				discountMoney = ls.get(position).getCouponMoney();
				mDiscountImg.setBackgroundDrawable(noclick);
				//刷新adapter的值
				adapter.notifyDataSetChanged();
			}
		});
		 
	}
	

	@Override
	protected void initAction() {
		Resources res = ctx.getResources();
		noclick = res.getDrawable(R.drawable.pay_noclick);
		click = res.getDrawable(R.drawable.pay_click);
		
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", DiscountId);
				intent.putExtra("userId", UserId);
				intent.putExtra("notRec", isNotRec);
				intent.putExtra("discountMoney", discountMoney);
				if("write".equals(from)){
					intent.setClass(DiscountSelectActivity.this, UUPayActivity.class);
				}
				if("modify".equals(from)){
					intent.setClass(DiscountSelectActivity.this, UUOrederPayActivity.class);
				}
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		isNotRec = "1";
		mDiscountImg.setBackgroundDrawable(click);
	}
	
	
	class DiscountSelAdapter extends BaseAdapter {
		private Context context;
		private List<DiscountEntity> list;
		HashMap<String,Boolean> states=new HashMap<String,Boolean>();

		public DiscountSelAdapter(Context context, List<DiscountEntity> ls) {
			super();
			this.context = context;
			this.list = ls;
		}
		
		@Override
		public int getCount() {
			if(list != null){
				return list.size();
			}else{
				return 0;
			}
			
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.activity_discountsel_list, null);
				holder.mDiscount = (TextView) view.findViewById(R.id.discount_sel_tv);
				holder.mRadio = (RadioButton) view.findViewById(R.id.discount_img); 
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (list.size()>0) {
				holder.mDiscount.setText(list.get(position).getCouponMoney() + "元" + list.get(position).getCouponName());
				if(list.get(position).getCouponUserStatus().equals("1")){
					holder.mDiscount.setText(list.get(position).getCouponMoney() + "元" + list.get(position).getCouponName()+"(待领取)");
				}
				//根据状态初始化radio状态
				boolean res = false;
				if (adapter.states.get(String.valueOf(position)) == null || adapter.states.get(String.valueOf(position)) == false) {
					res = false;
					adapter.states.put(String.valueOf(position), false);
				} else
					res = true;

				holder.mRadio.setChecked(res);

			}
			return view;
		}

		class ViewHolder {
			private TextView mDiscount;
			private RadioButton mRadio;
		}
	}
}
