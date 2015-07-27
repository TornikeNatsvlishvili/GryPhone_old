package com.Tornike.Gryphone.CourseSchedule;

import java.sql.Time;
import java.util.*;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class CourseItem implements Parcelable {
	private String courseTitle;
	private String classLocation, labLocation;
	private String classRoomNum, labRoomNum;
	private Calendar classTime, labTime;
	private boolean classDays[], labDays[];
	private int classID, labID, classLocationSpinnerIndx,
			labLocationSpinnerIndx;

	public CourseItem() {
		init();
	}

	public CourseItem(Parcel parcel) {
		init();

		courseTitle = parcel.readString();
		parcel.readBooleanArray(classDays);
		classTime = Calendar.getInstance();
		int tempClassTime[] = { -1, -1 };
		parcel.readIntArray(tempClassTime);
		classTime.set(Calendar.HOUR_OF_DAY, tempClassTime[0]);
		classTime.set(Calendar.MINUTE, tempClassTime[1]);
		classTime.set(Calendar.SECOND, 0);
		classLocation = parcel.readString();
		classLocationSpinnerIndx = parcel.readInt();
		classRoomNum = parcel.readString();
		parcel.readBooleanArray(labDays);
		int tempLabTime[] = { -1, -1 };
		parcel.readIntArray(tempLabTime);
		labTime.set(Calendar.HOUR_OF_DAY, tempLabTime[0]);
		labTime.set(Calendar.MINUTE, tempLabTime[1]);
		labTime.set(Calendar.SECOND, 0);
		labLocation = parcel.readString();
		labLocationSpinnerIndx = parcel.readInt();
		labRoomNum = parcel.readString();
		classID = parcel.readInt();
		labID = parcel.readInt();
	}

	private void init() {
		courseTitle = "";
		classLocation = "";
		labLocation = "";
		classRoomNum = "";
		labRoomNum = "";
		classTime = Calendar.getInstance();
		labTime = Calendar.getInstance();
		classLocationSpinnerIndx = -1;
		labLocationSpinnerIndx = -1;
		classDays = new boolean[] { false, false, false, false, false };
		labDays = new boolean[] { false, false, false, false, false };
		classID = -1;
		labID = -1;
	}

	public void setTitle(String t) {
		courseTitle = t;
	}

	public String getTitle() {
		return courseTitle;
	}

	public void setClassLocation(String cL) {
		classLocation = cL;
	}

	public String getClassLocation() {
		return classLocation;
	}

	public void setLabLocation(String lL) {

		labLocation = lL;
	}

	public String getLabLocation() {
		return labLocation;
	}

	public void setClassRoomNum(String cN) {
		classRoomNum = cN;
	}

	public String getClassRoomNum() {
		return classRoomNum;
	}

	public void setLabRoomNum(String lN) {
		labRoomNum = lN;
	}

	public String getLabRoomNum() {
		return labRoomNum;
	}

	public void setClassTime(Calendar cT) {
		classTime = cT;
	}

	public void setClassTime(int arr[]) {
		classTime.set(Calendar.HOUR_OF_DAY, arr[0]);
		classTime.set(Calendar.MINUTE, arr[1]);
	}

	public Calendar getClassTime() {
		classTime.set(Calendar.MILLISECOND, 0);
		return classTime;
	}

	public void setLabTime(Calendar lT) {
		labTime = lT;
	}

	public void setLabTime(int arr[]) {
		labTime.set(Calendar.HOUR_OF_DAY, arr[0]);
		labTime.set(Calendar.MINUTE, arr[1]);
	}

	public Calendar getLabTime() {
		return labTime;
	}

	public void setClassDates(boolean[] cD) {
		classDays = cD;
	}

	public boolean[] getClassDates() {
		return classDays;
	}

	public void setLabDates(boolean[] lD) {
		labDays = lD;
	}

	public boolean[] getLabDates() {
		return labDays;
	}

	public void setClassID(int id) {
		classID = id;
	}

	public int getMakeClassID() {
		if (classID == -1) {
			classID = Math.abs((int) (classTime.getTimeInMillis() + System
					.currentTimeMillis()));
		}
		return classID;
	}

	public void setLabID(int id) {
		labID = id;
	}

	public int getMakeLabID() {
		if (labID == -1) {
			labID = Math.abs((int) (classTime.getTimeInMillis() + System
					.currentTimeMillis())) - 1;
		}
		return labID;
	}

	public void setClassLocationSpinnerIndx(int indx) {
		classLocationSpinnerIndx = indx;
	}

	public int getClassLocationSpinnerIndx() {
		return classLocationSpinnerIndx;
	}

	public void setLabLocationSpinnerIndx(int indx) {
		labLocationSpinnerIndx = indx;
	}

	public int getLabLocationSpinnerIndx() {
		return labLocationSpinnerIndx;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(courseTitle);
		dest.writeBooleanArray(classDays);
		dest.writeIntArray(new int[] { classTime.get(Calendar.HOUR_OF_DAY),
				classTime.get(Calendar.MINUTE) });
		dest.writeString(classLocation);
		dest.writeInt(classLocationSpinnerIndx);
		dest.writeString(classRoomNum);
		dest.writeBooleanArray(labDays);
		dest.writeIntArray(new int[] { labTime.get(Calendar.HOUR_OF_DAY),
				labTime.get(Calendar.MINUTE) });
		dest.writeString(labLocation);
		dest.writeInt(labLocationSpinnerIndx);
		dest.writeString(labRoomNum);
		dest.writeInt(classID);
		dest.writeInt(labID);
	}

	public static Creator<CourseItem> CREATOR = new Creator<CourseItem>() {
		public CourseItem createFromParcel(Parcel parcel) {
			return new CourseItem(parcel);
		}

		public CourseItem[] newArray(int size) {
			return new CourseItem[size];
		}
	};

	public String toString() {
		String phrase = "";
		phrase += "(NEW COURSE START)" + courseTitle + "\n";
		phrase += booleanArrayToString(classDays);
		phrase += (60 * classTime.get(Calendar.HOUR_OF_DAY) + classTime
				.get(Calendar.MINUTE)) + "\n";
		phrase += classLocation + "\n";
		phrase += classLocationSpinnerIndx + "\n";
		phrase += classRoomNum + "\n";
		phrase += booleanArrayToString(labDays);
		phrase += (60 * labTime.get(Calendar.HOUR_OF_DAY) + labTime
				.get(Calendar.MINUTE)) + "\n";
		phrase += labLocation + "\n";
		phrase += labLocationSpinnerIndx + "\n";
		phrase += labRoomNum + "\n";
		phrase += classID + "\n";
		phrase += labID + "(END)\n";
		return phrase;

	}

	public void parseCourseItem(String def) {
		int loc1 = 0, loc2 = 0;

		loc2 = def.indexOf("\n", loc1);
		courseTitle = def.substring(loc1, loc2).replace("(NEW COURSE START)",
				"");

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		classDays = stringToBooleanArray(def.substring(loc1, loc2));

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		int tempI = Integer.parseInt(def.substring(loc1, loc2));
		classTime.set(Calendar.HOUR_OF_DAY, (tempI - (tempI % 60)) / 60);
		classTime.set(Calendar.MINUTE, tempI % 60);
		classTime.set(Calendar.SECOND, 0);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		classLocation = def.substring(loc1, loc2);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		classLocationSpinnerIndx = Integer.parseInt(def.substring(loc1, loc2));

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		classRoomNum = def.substring(loc1, loc2);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		labDays = stringToBooleanArray(def.substring(loc1, loc2));

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		tempI = Integer.parseInt(def.substring(loc1, loc2));
		labTime.set(Calendar.HOUR_OF_DAY, (tempI - (tempI % 60)) / 60);
		labTime.set(Calendar.MINUTE, tempI % 60);
		labTime.set(Calendar.SECOND, 0);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		labLocation = def.substring(loc1, loc2);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		labLocationSpinnerIndx = Integer.parseInt(def.substring(loc1, loc2));

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		labRoomNum = def.substring(loc1, loc2);

		loc1 = loc2 + 1;
		loc2 = def.indexOf("\n", loc1);
		classID = Integer.parseInt(def.substring(loc1, loc2));

		loc1 = loc2 + 1;
		loc2 = def.indexOf("(END)", loc1);
		labID = Integer.parseInt(def.substring(loc1, loc2));
	}

	public String booleanArrayToString(boolean[] array) {
		String out = "";
		for (boolean a : array) {
			if (a) {
				out += "t";
			} else {
				out += "f";
			}
		}
		out += "\n";
		return out;
	}

	public boolean[] stringToBooleanArray(String string) {
		boolean[] out = new boolean[string.length()];
		for (int a = 0; a < string.length(); a++) {
			if (string.charAt(a) == 't') {
				out[a] = true;
			} else {
				out[a] = false;
				;
			}
		}
		return out;
	}
}
