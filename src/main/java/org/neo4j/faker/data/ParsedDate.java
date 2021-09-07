package org.neo4j.faker.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ParsedDate {
	Date dStart;
	final static long MILLIS_PER_DAY = 24 * 3600 * 1000;
	int daysDiff;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	Random rnd = new Random();
	public ParsedDate(String def) throws Exception {
		String[] parms = def.split(",");
		if (parms.length > 3 || parms.length < 2) {
			throw new Exception("The date function does not have the correct parameters, check help!");
		}
		
		dStart = sdf.parse(parms[0]);
	    Date dEnd = sdf.parse(parms[1]);
	    
	    long msDiff = dEnd.getTime() - dStart.getTime();
	    if (msDiff < 1) {
	    	throw new Exception(" start date must before enddate");
	    }
	    daysDiff =(int) Math.round(msDiff / ((double) MILLIS_PER_DAY));
	    if (parms.length == 3) {
	    	sdf2 = new SimpleDateFormat(parms[2]);
	    }
	}
	public long getRandomDateTime() {
		int days = rnd.nextInt(daysDiff);
		long dayPart = Math.round(rnd.nextDouble() * MILLIS_PER_DAY );
		return (dStart.getTime() + (days * MILLIS_PER_DAY) + dayPart);
	}
	public int getRandomDate() {
		int days = rnd.nextInt(daysDiff);
		long newDate = dStart.getTime() + (days * MILLIS_PER_DAY);
		Date d = new Date();
		d.setTime(newDate);
		String s = sdf2.format(d);
		return Integer.valueOf(s);
	}
	public Date getRandomDateObject() {
		int days = rnd.nextInt(daysDiff);
		long newDate = dStart.getTime() + (days * MILLIS_PER_DAY);
		Date d = new Date();
		d.setTime(newDate);
		return d;
	}

	public String getRandomDateString() {
		int days = rnd.nextInt(daysDiff);
		long newDate = dStart.getTime() + (days * MILLIS_PER_DAY);
		Date d = new Date();
		d.setTime(newDate);
		String s = sdf.format(d);
		return s;
	}
	
}
