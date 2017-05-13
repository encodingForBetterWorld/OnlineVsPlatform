package soft.chess.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import soft.chess.domain.Player;

public class MyJsonUtil {
//	//单个对象转Json字符串
//	public static String parseJson(Object o){
//		String json="{";
//		Class<?> os=o.getClass();
//		Field[] fields=os.getDeclaredFields();
//		for(Field field:fields){
//			//如果检测到expose注解，则加入字符串
//			if(field.isAnnotationPresent(expose.class)){
//				json+="\""+field.getName()+"\":";
//				//如果是字符串类型，加引号
//				if(field.getType()==String.class){
//					
//				}
//				json+=",";
//			}
//		}
//		json+="}";
//		return json;
//	}
	public static void main(String[] args){
		List list = new ArrayList();
		list.add( "first" );
		list.add( "second" );
		JSONArray jsonArray2 = JSONArray.fromObject( list );
		//单个对象转json字符串
		Player player=new Player();
		player.setId(123);
		player.setName("wsq");
		JSONObject jsonOb = JSONObject.fromObject(player);
		String jstr=jsonOb.toString();
		System.out.println(jstr);
		
		//json字符串解析成对象		
		JSONObject jsonobject = JSONObject.fromObject(jstr);
		Player cloPlayer=(Player)JSONObject.toBean(jsonobject, Player.class);
		System.out.println("id:"+cloPlayer.getId()+",name:"+cloPlayer.getName());
	}
}
