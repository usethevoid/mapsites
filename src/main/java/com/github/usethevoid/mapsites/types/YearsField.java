package com.github.usethevoid.mapsites.types;

public class YearsField {
	public static final String TYPE = "years";
	
	static public class View {
		public String startYear;
		public String endYear;
		public String startEpoch;
		public String endEpoch;
	}
	
	static public class Model {
		public Integer start;
		public Integer end;
	}
	
	public static Model view2model(View in) {
		Model out = new Model();
		if (in.startYear != null && in.startEpoch != null) {
			out.start = Integer.parseInt(in.startYear);
			if (in.startEpoch.equals("BC"))
				out.start = -out.start;
		}
		if (in.endYear != null && in.endEpoch != null) {
			out.end = Integer.parseInt(in.endYear);
			if (in.endEpoch.equals("BC"))
				out.end = -out.end;
		}
		return out;
	}
}
