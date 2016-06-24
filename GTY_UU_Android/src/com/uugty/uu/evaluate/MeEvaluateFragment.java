package com.uugty.uu.evaluate;



import com.uugty.uu.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MeEvaluateFragment extends android.support.v4.app.Fragment{

	private View view;
	private ListView list_view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return view=inflater.inflate(R.layout.me_relauate,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		list_view=(ListView) view.findViewById(R.id.melist_view);
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
				convertView=LayoutInflater.from(context).inflate(R.layout.activity_meevaluatedetail, null);
				convertView.setTag(manger);
				
			}else {
				manger=(ViewManger) convertView.getTag();
			}
			
			return convertView;
		}
		
		class ViewManger{
			
			
		}
		
	}
}
