package soft.chess.util;

public class GameChessServerMsgUtil {
	private GameChessServerMsgUtil() {
		// TODO Auto-generated constructor stub
	}
	//游戏开始
	//action$roomid*msg
	public static final String GAME_BEGIN_ACTION="GAME_BEGIN$";
	public static final String MOVE_CHESS="MOVE_CHESS$";
	//有玩家中途逃跑
	public static final String PLAYER_ESC="PLAYER_ESC$";
	//有观众中途离开
	public static final String AUDI_ESC="AUDI_ESC$";
	//有观众中途加入
	public static final String AUDI_JOIN="AUDI_JOIN$";
	public static String useGameServerMsg(long roomid,String type,String msg){
		String returnVal="";
		returnVal=type+roomid+"*"+msg;
		return returnVal;
	}
	//获取房间号
	public static long getRoomId(String msg){
		String _info=msg.trim();
		//去掉类别头
		_info=getMsgWithoutType(_info);
		_info=_info.substring(0,_info.indexOf('*'));
		return Long.parseLong(_info);
	}
	public static boolean isAction(String action,String info){
		boolean returnVal=false;
		info=info.substring(0,info.indexOf('$')+1);//取头部
		if(info.equals(action)){
			returnVal=true;
		}
		return returnVal;
	}
	//去除房间ID的消息头
	private static String getMsgWithoutType(String str){
		str=str.substring(str.indexOf('$')+1);//取除头部外的所有字符串
		return str;
	}
	//去除ID和类别的消息头
	public static String getMsg(String str){
		str=str.substring(str.indexOf('*')+1);//取除头部外的所有字符串
		return str.trim();
	}
}
