package com.Tornike.Gryphone.Events.Students;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class StudentEventItem {
	private String title, date, time, location, eligibility, topic,
			instructors, format, contact, registration, description, link,
			type;
	private int background, typeColor, availability;

	public StudentEventItem() {
		title = "";
		date = "";
		time = "";
		location = "";
		eligibility = "";
		topic = "";
		instructors = "";
		format = "";
		contact = "";
		availability = 0;
		registration = "";
	}

	public StudentEventItem(String t) {
		title = t;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		title = t;
	}

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int a) {
		availability = a;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		description = d;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String l) {
		link = l;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String d) {
		date = d;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String t) {
		time = t;
	}

	public String getEligibility() {
		return eligibility;
	}

	public void setEligibility(String e) {
		eligibility = e;
	}

	public int getTypeIcon() {
		return typeColor;
	}

	public void setTypeColor(int t) {
		typeColor = t;
	}

	public int getBackgroundColor() {
		return background;
	}

	public void setBackgroundColor(int b) {
		background = b;
	}

	public int getAvailabilityIcon() {
		if (eligibility.equals("FULL")) {
			return Color.RED;
		} else {
			return Color.GREEN;
		}
	}

	public String getEventType() {
		return type;
	}

	public void setEventType(String t) {
		type = t;
	}

	public String getIconText() {
		if (typeColor == Color.parseColor("#333333")) {
			return "Alumni Affairs & Development";
		}
		if (typeColor == Color.parseColor("#FF0000")) {
			return "Athletics";
		}
		if (typeColor == Color.parseColor("#FFCC00")) {
			return "Central Student Association";

		}
		if (typeColor == Color.parseColor("#66FF66")) {
			return "Centre for International Programs";
		}
		if (typeColor == Color.parseColor("#660000")) {
			return "Centre for Students with Disabilities";
		}
		if (typeColor == Color.parseColor("#6699CC")) {
			return "Co-operative Education & Career Services";
		}
		if (typeColor == Color.parseColor("#6600CC")) {
			return "CSA Student Help and Resource Centre";
		}
		if (typeColor == Color.parseColor("#006600")) {
			return "Environmental Science";
		}
		if (typeColor == Color.parseColor("#CC0000")) {
			return "ESL University Preparation Programs";
		}
		if (typeColor == Color.parseColor("#990099")) {
			return "Graduate Studies";
		}
		if (typeColor == Color.parseColor("#330000")) {
			return "Guelph Resource Ctr. for Gender Empow. & Diversity";
		}
		if (typeColor == Color.parseColor("#333366")) {
			return "International Student Organization";
		}
		if (typeColor == Color.parseColor("#FF99FF")) {
			return "Library";
		}
		if (typeColor == Color.parseColor("#66FFFF")) {
			return "Multi-Faith Resource Team";
		}
		if (typeColor == Color.parseColor("#33FF33")) {
			return "Office of Intercultural Affairs";
		}
		if (typeColor == Color.parseColor("#FF9900")) {
			return "Student Affairs";
		}
		if (typeColor == Color.parseColor("#CC9999")) {
			return "Student Life";
		}
		if (typeColor == Color.parseColor("#0033CC")) {
			return "Student Volunteer Connections";
		}
		if (typeColor == Color.parseColor("#336600")) {
			return "Sustainability Office";
		}
		if (typeColor == Color.parseColor("#999999")) {
			return "Teaching Support Services";
		}
		if (typeColor == Color.parseColor("#009966")) {
			return "The Learning Commons";
		}
		if (typeColor == Color.parseColor("#993399")) {
			return "Wellness Centre";
		}
		return "";
	}
}