package soft.chess.util;

public class BroadcastInRoomMsgUtil {
	private BroadcastInRoomMsgUtil() {
		// TODO Auto-generated constructor stub
	}
	//房间加入玩家的消息
	public static final String JOIN_HEAD="JOIN$";
	//房间加入观众的消息
	public static final String JOIN_AUDI_HEAD="JOIN_AUDI$";
	//房间加入玩家的消息
	public static final String LEAVE_HEAD="LEAVE$";
	//更新聊天信息的消息
	public static final String CHAT_HEAD="CHAT$";
	
	public static final String GAME_CHAT_HEAD="GAME_CHAT$";
	//开启游戏的消息
	public static final String BEGIN_GAME="BEGIN$";
	//准备游戏的消息
	public static final String READY_GAME="READY$";
	//取消准备游戏的消息
	public static final String DISREADY_GAME="DISREADY$";
	//棋子的位置消息
	public static final String CHESS_MOVE="CHESS_MOVE$";
	//游戏结束的消息
	public static final String GAME_OVER="GAME_OVER$";
	//处理房间内的广播信号
	//信号头为房间号+*
	public static String useMyRoomMsg(long roomid,String type,String msg){
		String returnVal="";
		returnVal=roomid+"*"+type+msg;
		return returnVal;
	}
	//判断信号的类别
	public static boolean isMyMsg(long roomid,String type,String msg){
		boolean flag=false;
		//先判断是否是发到本房间的信号
		if(!isMyRoomMsg(roomid, msg))return flag;
		else{
			//判断信号类别
			String _typeinfo=getMsgWithoutId(msg);
			_typeinfo=_typeinfo.substring(0,_typeinfo.indexOf('$')+1);
			if(type.equals(_typeinfo)){
				flag=true;
			}
		}
		return flag;
	}
	//判断接收到的信号是否是发给本房间的
	private static boolean isMyRoomMsg(long roomid,String msg){
		boolean flag=false;
		String _info=msg.trim();
		_info=_info.substring(0,_info.indexOf('*'));
		long _rid=Long.parseLong(_info);
		if(roomid==_rid){
			flag=true;
		}
		return flag;
	}
	//去除房间ID的消息头
	private static String getMsgWithoutId(String str){
		str=str.substring(str.indexOf('*')+1);//取除头部外的所有字符串
		return str;
	}
	//去除ID和类别的消息头
	public static String getMsg(String str){
		str=str.substring(str.indexOf('$')+1);//取除头部外的所有字符串
		return str.trim();
	}
}
