package soft.chess.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private DateUtil(){}
	public static String getDate(String formate,long ldate){
		Date date=new Date(ldate);
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);
		String timestr=dateFormat.format(date);
		return timestr;
	}
}
