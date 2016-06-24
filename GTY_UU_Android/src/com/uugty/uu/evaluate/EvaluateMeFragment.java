package com.uugty.uu.evaluate;

import com.uugty.uu.R;
import com.uugty.uu.evaluate.MeEvaluateFragment.ListAdapter.ViewManger;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EvaluateMeFragment extends android.support.v4.app.Fragment{

	private View view;
	private ListView list_view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return view=inflater.inflate(R.layout.me_relauate_two,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		list_view=(ListView) view.findViewById(R.id.melist_viewtwo);
		ListAdapter adapter=new ListAdapter(getActivity());
		list_view.setAdapter(adapter);
	}
	
	class ListAdapter extends BaseAdapter{

        private Context context;
		
		public ListAdapter(Context content){
			
			this.context=content;
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return 5;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewManger manger=null;
			if (convertView==null) {
				manger=new ViewManger();
				convertView=LayoutInflater.from(context).inflate(R.layout.activity_meevaluatedetailtwo, null);
				manger.txt_realuate=(TextView) convertView.findViewById(R.id.activity_me_evlauate);
				convertView.setTag(manger);
				
			}else {
				manger=(ViewManger) convertView.getTag();
			}
			
			//回复事件
			manger.txt_realuate.setOnClickListener(new RealuateLister());
			return convertView;
		}
		
		class ViewManger{
			
			TextView txt_realuate;
		}
		class RealuateLister implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(context, ReplyActivity.class)
				;
				startActivity(intent);
			}
			
		}
	}
}
