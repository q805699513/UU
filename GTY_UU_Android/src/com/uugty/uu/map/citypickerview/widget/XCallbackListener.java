package com.uugty.uu.map.citypickerview.widget;


public abstract class XCallbackListener {

	protected abstract void callback(Object... obj);

	public void call(Object... obj) {
			callback(obj);
	}

}
