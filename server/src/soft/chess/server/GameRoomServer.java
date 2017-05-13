package soft.chess.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import soft.chess.common.PageBean;
import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.server.gameHall.RegisterServer;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.RoomServerMsgUtil;
import soft.chess.util.SocketUtil;

/**
 * 
 * ClassName: GameRoomServer 
 * @Description: 负责接受游戏房间的信号，并向游戏房间组播发送信号，
 * 和数据关系:只在游戏结束后添加游戏记录进数据库
 * @author wangsuqi
 * @date 2016年10月12日
 * @see
 */
public class GameRoomServer {
	//存储当前的所有房间信息,针对该集合进行正删改查操作，都会发送该集合的后续状态到--大厅组播--中
	//针对该对象的读写操作必须是synchronize线程同步的！
	private ConcurrentHashMap< Long, RoomBean> rooms;
	//生成房间ID
	private AtomicLong room_id=new AtomicLong(0);
	private static final String SERVER_ADDRESS="127.0.0.1";
	private static final int SERVER_PORT=6667;
	//加入到大厅组播的套接字
	private static MulticastSocket hallCastSocket = null;
	//数据报为4K
	private static final int BUFF_SIZE=4096*2;
	private static DatagramSocket serverSocket = null;
	private static InetAddress serverAddress = null;
	
	public GameRoomServer() {
		// TODO Auto-generated constructor stub
		try {
			rooms=new ConcurrentHashMap<>();
//			RoomBean room=new RoomBean();
//			Player player=new Player();
//			player.setName("TEST");
//			room.setCreater(player);
//			long ii=20;
//			while(ii<37){
//				rooms.put(ii, room);
//				ii++;
//			}
			//游戏房间服务器的套接字
        	serverAddress=InetAddress.getByName(SERVER_ADDRESS);
        	serverSocket=new DatagramSocket(SERVER_PORT, serverAddress);
        	//获取一个加入大厅组播的套接字
        	hallCastSocket=SocketUtil.joinHallBroadcast();
        	while(true){
        		//每次循环新建packet，防止乱码
        		
        		DatagramPacket inpacket=new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
        		
				serverSocket.receive(inpacket);
				
				String info=new String(inpacket.getData());
				//由于socket缓冲区没有清空导致问题
				System.out.println("房间服务器收到:"+info);
				//执行调度
				//如果是分页查询房间
				if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.FIND_ROOMS_BYPAGE_ACTION, info)){
					new Thread(new findRoomsByPage(info,inpacket)).start();
				}
				//如果是添加房间的操作
				if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.SAVE_ROOM_ACTION, info)){
					new Thread(new saveRoom(info,inpacket)).start();
				}
				//如果是修改房间的操作
				if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.UPDATE_ROOM_ACTION, info)){
					new Thread(new updateRoom(info)).start();
				}
				//如果是删除房间的操作
				if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.DELETE_ROOM_ACTION, info)){
					new Thread(new delRoom(info)).start();
				}
        	}
        } catch (Exception exception) {  
            exception.printStackTrace();  
        }  
	}
	private synchronized long getid(){
		long _id=room_id.incrementAndGet();
		return _id;
	}
	//分页查询房间，收到并返回:pageBean对象的json字符串
	private class findRoomsByPage implements Runnable{
		private DatagramPacket outpacket;
		private String _info;
		private String send_info;
		public findRoomsByPage(String info,DatagramPacket inpacket) {
			_info=RoomServerMsgUtil.getMsg(info);
			outpacket=new DatagramPacket(new byte[0], 0, 
					inpacket.getAddress(), inpacket.getPort());
		}
		@Override
		public void run() {
			//发送当前房间集合给客户端
			System.out.println("分页发送房间");
			//解析JSON字符串
			List<RoomBean> roomlist=new ArrayList<>();
			for(RoomBean room:rooms.values()){
				roomlist.add(room);
			}
			if(roomlist.size()==0){
				send_info="nodata";
			}
			else{
				PageBean page=(PageBean) JsonUtil.StrParseObject(_info, PageBean.class);
				int currentPageOffset=page.getCurrentPageOffset();
				int currentPage=page.getCurrentPage();
				int pageSize=page.getPageSize().intValue();
				//总记录数
				int pageRows=roomlist.size();
				int totalPage=page.getTotalPages(pageRows);
				
				//判断当前页是否是最后一页，最后一页是否已满
				List<RoomBean> list = (currentPage!=totalPage||pageRows%pageSize==0)?
						roomlist.subList(currentPageOffset,currentPageOffset+pageSize):
							roomlist.subList(currentPageOffset,currentPageOffset+pageRows%pageSize);
				page.setList(list);
				send_info=JsonUtil.ObjectToStr(page);
			}
			try {
				send_info=RoomServerMsgUtil.setAction(RoomServerMsgUtil.FIND_ROOMS_BYPAGE_ACTION, send_info);
				outpacket.setData(send_info.getBytes());
				serverSocket.send(outpacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/*
	 * 接受一个Json字符创加入rooms中，
	 * 处理后把room发送到房间,
	 * 然后把rooms转json字符串发送到大厅组播
	 */
	private class saveRoom implements Runnable{
		//发送到大厅组播和房间的报文,内容是rooms转化成的json字符串
		private DatagramPacket outToHallpacket;
		private DatagramPacket outToRoompacket;
		private String inInfo;
		private RoomBean room;
		public saveRoom(String info,DatagramPacket inpacket) {
			// TODO Auto-generated constructor stub
			inInfo=RoomServerMsgUtil.getMsg(info);
			outToHallpacket=SocketUtil.getHallBroadcastPacket();
			outToRoompacket=new DatagramPacket(new byte[0], 0, 
					inpacket.getAddress(), inpacket.getPort());
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			room=(RoomBean) JsonUtil.StrParseObject(inInfo, RoomBean.class);
			//返回房间数据
			long room_id=getid();
			room.setRoomid(room_id);
			//存入rooms
			rooms.put(room_id, room);
			/*
			 * 传到房间的报文
			 */
			String send_info=JsonUtil.ObjectToStr(room);
			String send_to_room_info=RoomServerMsgUtil
					.setAction(RoomServerMsgUtil.SAVE_ROOM_ACTION, send_info);
			outToRoompacket.setData(send_to_room_info.getBytes());
			/*
			 * 传到大厅的报文
			 */
			//设定消息头
			String send_to_hall_info=BroadcastMsgUtil.parseMsg(BroadcastMsgUtil.SAVE_ROOM_HEAD, send_info);
			//加入报文
			outToHallpacket.setData(send_to_hall_info.getBytes());
			//发送到大厅中
			try {
				serverSocket.send(outToRoompacket);
				hallCastSocket.send(outToHallpacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	//引起房间状态发生变化的情况有两种：
	//1.有用户进入或离开房间;
	//2.房间开始或结束游戏;
	//需要接受一个RoomBean的json字符串，通过其对挂在服务器上的房间列表
	//作出修改，这个RoomBean发到客户端的大厅组播中
	private class updateRoom implements Runnable{
		private DatagramPacket outToHallpacket;
		private String _info;
		private RoomBean room;
		public updateRoom(String info) {
			// TODO Auto-generated constructor stub
			this._info=RoomServerMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			this.outToHallpacket=SocketUtil.getHallBroadcastPacket();
			this.room=(RoomBean) JsonUtil.StrParseObject(_info, RoomBean.class);
			long room_id=room.getRoomid();
			rooms.put(room_id, room);
			String send_to_hall_info=JsonUtil.ObjectToStr(room);
			send_to_hall_info=BroadcastMsgUtil
					.parseMsg(BroadcastMsgUtil.UPDATE_ROOM_HEAD, send_to_hall_info);
			outToHallpacket.setData(send_to_hall_info.getBytes());
			try {
				hallCastSocket.send(outToHallpacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	//房间因为没有人成为无效房间
	//把无效房间从挂在服务器上的房间列表上删除
	//把无效房间发到客户端的大厅组播中
	private class delRoom implements Runnable{
		private DatagramPacket outToHallpacket;
		private String _info;
		private RoomBean room;
		public delRoom(String info) {
			// TODO Auto-generated constructor stub
			this._info=RoomServerMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			this.outToHallpacket=SocketUtil.getHallBroadcastPacket();
			this.room=(RoomBean) JsonUtil.StrParseObject(_info, RoomBean.class);
			long room_id=room.getRoomid();
			rooms.remove(room_id);
			String send_to_hall_info=JsonUtil.ObjectToStr(room);
			send_to_hall_info=BroadcastMsgUtil
					.parseMsg(BroadcastMsgUtil.DELETE_ROOM_HEAD, send_to_hall_info);
			outToHallpacket.setData(send_to_hall_info.getBytes());
			try {
				hallCastSocket.send(outToHallpacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GameRoomServer();
	}

}
