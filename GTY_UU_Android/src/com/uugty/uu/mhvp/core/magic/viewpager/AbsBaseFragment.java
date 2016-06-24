package com.uugty.uu.mhvp.core.magic.viewpager;


import android.support.v4.app.Fragment;


public class AbsBaseFragment extends Fragment implements InnerScrollerContainer{

	 protected OuterScroller mOuterScroller;
	 protected int mIndex;
	@Override
	public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
		// TODO Auto-generated method stub
		if(outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if(getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
	}
	@Override
	public InnerScroller getInnerScroller() {
		// TODO Auto-generated method stub
		return null;
	}

}
