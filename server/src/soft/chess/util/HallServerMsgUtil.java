package soft.chess.util;
//分类服务器的接收信号，根据收到信号的字符串，解析出类型再提供相应的服务
public class HallServerMsgUtil {
	//定义消息头
	//登录操作
	public  static final String LOGIN_ACTION="login#";
	//注册操作
	public static final String REGIS_ACTION="regis#";
	//修改玩家信息操作
	public static final String UPDATE_PLAYER_ACTION="update_player#";
	//查询所有玩家信息的操作
	public static final String QUERY_ALL_PLAYERS_ACTION="query_all_players#";
	//查询玩家自己的游戏记录
	public static final String QUERY_MY_RECORDS_ACTION="query_my_records#";
	//查询公告信息
	public static final String QUERY_ALL_ANN_ACTION="query_all_ann#";
	//查询棋局信息
	public static final String QUERY_CHESSES="query_chesses#";
	//获取图片资源
	public static final String GET_IMG="get_img#";
	//获取房间内图片资源
	public static final String GET_ROOM_IMG="get_room_img#";
	private HallServerMsgUtil(){}
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
	//生成带有用户ID的消息头,用于用户获取自己的图片资源
	//type#图片名称(用户ID+_Icon.后缀名)
}
