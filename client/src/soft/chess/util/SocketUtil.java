package soft.chess.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class SocketUtil {
	//大厅服务器所在端口
	public static final String SERVER_ADDRESS="127.0.0.1";
	public static final int HALL_SERVER_PORT=6666;
	public static final int ROOM_SERVER_PORT=6667;
	public static final int GAME_SERVER_PORT=8008;
	//使用常量作为本程序的多点广播IP地址
	private static final String BROADCAST_IP = "230.0.0.1";
	//使用常量作为游戏大厅内的多点广播目的端口
	private static final int HALL_BROADCAST_PORT = 30000;
	//使用常量作为游戏房间内的多点广播目的端口
	private static final int ROOM_BROADCAST_PORT = 29999;
	//游戏的组播端口
	private static final int GAME_BROADCAST_PORT = 29998;
	//广播的地址
	private static InetAddress broadcastAddress=null;
	//游戏大厅内的广播套接字
	private static MulticastSocket socket = null;
	//游戏房间内的广播套接字
	private static MulticastSocket roomSocket = null; 
	//联机游戏的广播套接字
	private static MulticastSocket gameSocket = null;
	//获取本地socket
	private static DatagramSocket singleSocket=null;
	
	public static MulticastSocket joinHallBroadcast() throws IOException{
		//建立网络连接
		if(broadcastAddress==null){
			broadcastAddress = InetAddress.getByName(BROADCAST_IP);
		}
		if(socket==null){
			socket=new MulticastSocket(HALL_BROADCAST_PORT);
			//将该socket加入指定的多点广播地址
			socket.joinGroup(broadcastAddress);
			//设置本MulticastSocket发送的数据报不被回送到自身
			socket.setLoopbackMode(false);
		}
		return socket;
	}
	//获取发送到大厅组播的数据报
	public static DatagramPacket getHallBroadcastPacket(){
		return new DatagramPacket(new byte[0] , 0 ,
				broadcastAddress , HALL_BROADCAST_PORT);
	}
	/**
	 * 
	 * @Description: 游戏房间的广播
	 * @param @return
	 * @param @throws IOException   
	 * @return MulticastSocket  
	 * @throws
	 * @author wangsuqi
	 * @date 2016年10月11日
	 */
	//获取加入游戏房间组播的socket
	public static MulticastSocket joinRoomBroadcast() throws IOException{
		//建立网络连接
		if(broadcastAddress==null){
			broadcastAddress = InetAddress.getByName(BROADCAST_IP);
		}
		if(roomSocket==null){
			roomSocket=new MulticastSocket(ROOM_BROADCAST_PORT);
			//将该socket加入指定的多点广播地址
			roomSocket.joinGroup(broadcastAddress);
			//设置本MulticastSocket发送的数据报不被回送到自身
			roomSocket.setLoopbackMode(false);
		}
		return roomSocket;
	}
	//获取发送到大厅组播的数据报
	public static DatagramPacket getRoomBroadcastPacket(){
		return new DatagramPacket(new byte[0] , 0 ,
				broadcastAddress , ROOM_BROADCAST_PORT);
	}
	/**
	 * 
	 * @Description: 联机游戏的广播
	 * @param @return
	 * @param @throws IOException   
	 * @return MulticastSocket  
	 * @throws
	 * @author wangsuqi
	 * @date 2016年10月11日
	 */
	//获取加入游戏房间组播的socket
	public static MulticastSocket joinGameBroadcast() throws IOException{
		//建立网络连接
		if(broadcastAddress==null){
			broadcastAddress = InetAddress.getByName(BROADCAST_IP);
		}
		if(gameSocket==null){
			gameSocket=new MulticastSocket(GAME_BROADCAST_PORT);
			//将该socket加入指定的多点广播地址
			gameSocket.joinGroup(broadcastAddress);
			//设置本MulticastSocket发送的数据报不被回送到自身
			gameSocket.setLoopbackMode(false);
		}
		return gameSocket;
	}
	//获取发送到联机游戏组播的数据报
	public static DatagramPacket getGameBroadcastPacket(){
		return new DatagramPacket(new byte[0] , 0 ,
				broadcastAddress , GAME_BROADCAST_PORT);
	}
	//将套接字从组播移除
	public static void leaveBroadcast(MulticastSocket socket) throws IOException{
		socket.leaveGroup(broadcastAddress);
	}
	//获取发送到大厅组播的数据报
	public static DatagramPacket getHallServerPacket() throws UnknownHostException{
		return new DatagramPacket(new byte[0], 0,
				InetAddress.getByName(SocketUtil.SERVER_ADDRESS),
				SocketUtil.HALL_SERVER_PORT);
	}
	//获取发送到房间服务器的数据报
	public static DatagramPacket getRoomServerPacket() throws UnknownHostException{
		return new DatagramPacket(new byte[0], 0,
				InetAddress.getByName(SocketUtil.SERVER_ADDRESS),
				SocketUtil.ROOM_SERVER_PORT);
	}
	//获取发送到游戏服务器的数据报
	public static DatagramPacket getChessGameServerPacket() throws UnknownHostException{
		return new DatagramPacket(new byte[0], 0,
				InetAddress.getByName(SocketUtil.SERVER_ADDRESS),
				SocketUtil.GAME_SERVER_PORT);
	}
	public static DatagramSocket getSingleSocket() throws SocketException {
		
		if(singleSocket==null){
			//使用随机端口
			singleSocket=new DatagramSocket();
		}
		return singleSocket;
	}
}
