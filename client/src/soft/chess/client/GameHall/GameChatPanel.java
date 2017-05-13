package soft.chess.client.GameHall;

import java.awt.Image;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.sf.json.JSONObject;
import soft.chess.client.GameHallClient;
import soft.chess.domain.Player;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.SoftConstant;
/**
 * 
 * ClassName: GameChatPanel 
 * @Description: 接收并显示大厅公共聊天
 * @author wangsuqi
 * @date 2016年10月8日
 * @see
 */
public class GameChatPanel extends BackgroundImagePanel{
	//窗体的组件
	//显示收到的信息
	private JTextArea jtaLog;
	private JScrollPane scrollPane;
	public GameChatPanel(){
		super(SoftConstant.chat_panel_bgimg);
		setLayout(null);
		jtaLog=new JTextArea();
		jtaLog.setEditable(false);
		jtaLog.setFont(SoftConstant.toosmfont);
		scrollPane=new JScrollPane(jtaLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 5, 610, 65);
		add(scrollPane);
	}	
	public void hasMsg(String msg){
		jtaLog.append("大厅--"+msg+"\n");
	}
	public void hasMsgFromRoom(String msg){
		jtaLog.append("房间--"+msg+"\n");
	}
	public void clearJtaLog(){
		jtaLog.setText("");
	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
}
