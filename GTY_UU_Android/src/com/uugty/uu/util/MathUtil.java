package com.uugty.uu.util;

public class MathUtil {

	public static double gapRandom(){
		double d = 1-Math.random()/5;
		return ((System.currentTimeMillis()%2)*2-1)*d;
	}

}
