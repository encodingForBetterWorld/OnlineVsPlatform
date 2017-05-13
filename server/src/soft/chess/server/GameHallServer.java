package soft.chess.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import soft.chess.server.gameHall.GetImgServer;
import soft.chess.server.gameHall.LoginServer;
import soft.chess.server.gameHall.QueryAllAnnsServer;
import soft.chess.server.gameHall.QueryAllPlayersServer;
import soft.chess.server.gameHall.QueryMyRecordsServer;
import soft.chess.server.gameHall.RegisterServer;
import soft.chess.server.gameHall.UpdatePlayerServer;
import soft.chess.util.HallServerMsgUtil;

/**
 * 
 * ClassName: GameHallServer 
 * @Description: 负责收发报文与数据交互，与客户端通信，
 * 游戏大厅的总服务器,负责调度和启动服务线程
 * @author wangsuqi
 * @date 2016年10月7日
 * @see
 */
public class GameHallServer {
	private static final String SERVER_ADDRESS="127.0.0.1";
	private static final int SERVER_PORT=6666;
	
	//数据报为4K
	private static final int BUFF_SIZE=4096;
	private static DatagramSocket serverSocket = null;
	private static InetAddress serverAddress = null;
	public GameHallServer(){
	 try {
        	serverAddress=InetAddress.getByName(SERVER_ADDRESS);
        	serverSocket=new DatagramSocket(SERVER_PORT, serverAddress);
        	
        	while(true){
        		//每次循环新建packet，防止乱码
        		
        		DatagramPacket inpacket=new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
        		
				serverSocket.receive(inpacket);
				
				String info=new String(inpacket.getData());
				//由于socket缓冲区没有清空导致问题
				System.out.println("大厅服务器收到:"+info);
				//执行调度
				//如果是登录操作，开启登录线程
				if(HallServerMsgUtil.isAction(HallServerMsgUtil.LOGIN_ACTION, info)){
					new Thread(new LoginServer(info, inpacket, serverSocket)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.REGIS_ACTION, info)){
					new Thread(new RegisterServer(info, inpacket, serverSocket)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.UPDATE_PLAYER_ACTION, info)){
					new Thread(new UpdatePlayerServer(info)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_ALL_PLAYERS_ACTION, info)){
					new Thread(new QueryAllPlayersServer(inpacket, serverSocket)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_ALL_ANN_ACTION, info)){
					new Thread(new QueryAllAnnsServer(inpacket, serverSocket)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_MY_RECORDS_ACTION, info)){
					new Thread(new QueryMyRecordsServer(inpacket, serverSocket)).start();
				}
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.GET_IMG, info)){
					new Thread(new GetImgServer(HallServerMsgUtil.GET_IMG,info, inpacket, serverSocket)).start();
				} 
				else if(HallServerMsgUtil.isAction(HallServerMsgUtil.GET_ROOM_IMG, info)){
					new Thread(new GetImgServer(HallServerMsgUtil.GET_ROOM_IMG,info, inpacket, serverSocket)).start();
				} 
        	}
        } catch (Exception exception) {  
            exception.printStackTrace();  
        }  
	}
}
