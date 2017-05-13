package soft.chess.util;

//分类大厅组播信号
public class BroadcastMsgUtil {
	//定义消息头
	//更新公告的消息
	public static final String ANN_HEAD="ANN$";
	//用户状态发送了变更
	public static final String PLAYER_HEAD="PLAYER$";
	//更新聊天信息的消息
	public static final String CHAT_HEAD="CHAT$";
	//获取刚添加的房间
	public static final String SAVE_ROOM_HEAD="SAVE_ROOM$";
	//获取刚添加的房间
	public static final String UPDATE_ROOM_HEAD="UPDATE_ROOM$";
	//获取刚删除的房间
	public static final String DELETE_ROOM_HEAD="DELETE_ROOM$";
	private BroadcastMsgUtil(){};
	//去除消息头
	public static String getMsg(String str){
		str=str.substring(str.indexOf('$')+1);//取除头部外的所有字符串
		return str.trim();
	}
	//发送端，生成带类别的信号
	public static String parseMsg(String msgType,String str){
		String Msg=msgType+str;
		return Msg;
	}
	//接收，判断信号类别，提供相应处理
	public static boolean isMsg(String msgType,String str){
		boolean returnVal=false;
		str=str.substring(0,str.indexOf('$')+1);//取头部
		if(str.equals(msgType)){
			returnVal=true;
		}
		return returnVal;
	}
}
