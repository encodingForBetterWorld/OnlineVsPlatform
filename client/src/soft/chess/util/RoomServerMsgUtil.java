package soft.chess.util;

public class RoomServerMsgUtil {
	private RoomServerMsgUtil() {}
	/**
	 * 需要接受一个JSON字符串
	 */
	//添加房间的操作
	public static final String SAVE_ROOM_ACTION="save#";
	//修改房间状态的操作
	public static final String UPDATE_ROOM_ACTION="update#";
	/**
	 * 需要接受一个ID值或状态值
	 */
	//删除房间的操作
	public static final String DELETE_ROOM_ACTION="delete#";
	//查找所有房间
	public static final String FIND_ROOMS_BYPAGE_ACTION="find_by_page#";
	//查找指定房间号的房间
	
	public static final String FIND_ROOM_BYID_ACTION="find_id#";
	//查找指定状态的房间
	public static final String FIND_ROOM_BYSTATE_ACTION="find_state#";
	
	//去除消息头
	public static String getMsg(String str){
		str=str.substring(str.indexOf('#')+1);//取除头部外的所有字符串
		return str.trim();
	}
	public static String setAction(String action,String info){
		String Msg=action+info;
		return Msg;
	}
	public static boolean isAction(String action,String info){
		boolean returnVal=false;
		info=info.substring(0,info.indexOf('#')+1);//取头部
		if(info.equals(action)){
			returnVal=true;
		}
		return returnVal;
	}
}
