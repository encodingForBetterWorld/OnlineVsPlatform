package soft.chess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;


public class JsonUtil{
	private static final JsonConfig LENIENT_JSONCONFIG=new JsonConfig();
	//字符串转对象
	public static Object StrParseObject(String str,Class bean_class){
		JSONObject getjson=JSONObject.fromObject(str);
		return JSONObject.toBean(getjson, bean_class);
	}
	//字符串转map
	public static Object StrParseMap(String str,Class bean_class,Map map){
		JSONObject getjson=JSONObject.fromObject(str);
		return JSONObject.toBean(getjson, bean_class,map);
	}
	//字符串转数组,使用泛型进行类型检查
	public static <T>List<T> StrParseArray(String str,Class<T> claz){
		//获取json对象数组
		JSONArray arr=JSONArray.fromObject(str);		
		return (List<T>) JSONArray.toCollection(arr, claz);
	}
	//Json对象中的集合转指定集合
	public static <T>List<T> JSONArrayParseObjectArray(List arr,Class<T> claz){
		JSONArray jarr=JSONArray.fromObject(arr);	
		return (List<T>) JSONArray.toCollection(jarr, claz);
	}
	//对象转字符串
	public static String ObjectToStr(Object entity){
		JSONObject sendjson=JSONObject.fromObject(entity,LenientConfig());
		return sendjson.toString();
	}
	//数组转字符串
	public static <E>String ArrayToStr(List<E> list){
		JSONArray json=JSONArray.fromObject(list,LenientConfig());
		return json.toString();
	}
	//防止创建Json字符串时的递归调用死循环
	public static final JsonConfig LenientConfig(){
		LENIENT_JSONCONFIG.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		return LENIENT_JSONCONFIG;
	}
}
