package com.uugty.uu.common.myview.calendar;

import java.io.Serializable;

public class CustomDate implements Serializable {

	private static final long serialVersionUID = 1L;
	public int year;
	public int month;
	public int day;
	public int week;
	public String today;

	public CustomDate(int year, int month, int day) {
		if (month > 12) {
			month = 1;
			year++;
		} else if (month < 1) {
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public CustomDate() {
		this.year = DateUtil.getYear();
		this.month = DateUtil.getMonth();
		this.day = DateUtil.getCurrentMonthDay();
	}

	public static CustomDate modifiDayForObject(CustomDate date, int day) {
		CustomDate modifiDate = new CustomDate(date.year, date.month, day);
		return modifiDate;
	}

	@Override
	public String toString() {
		return year + "-" + month + "-" + day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getToday() {
		String strMonth="";
		String strDay="";
		if (this.month < 10) {
			strMonth = "0" + this.month;
		}else{
			strMonth=String.valueOf(this.month);
		}
		if (this.day < 10) {
			strDay = "0" + this.day;
		}else{
			strDay = String.valueOf(this.day);
		}
		this.today = this.year + strMonth + strDay;
		return today;
	}
}
