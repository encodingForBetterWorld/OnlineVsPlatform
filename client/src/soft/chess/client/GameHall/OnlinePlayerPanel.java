package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.sf.json.JSONObject;
import soft.chess.action.PlayerAction;
import soft.chess.domain.Player;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.OnlyImgButton;
import soft.chess.util.SoftConstant;

public class OnlinePlayerPanel extends BackgroundImagePanel{
	//窗体的组件
	private JLabel tiplabel;
	//显示玩家列表
	private static final String[] COLUMNS={"等级","昵称","状态"};
	private String[][] datas;
	private TableModel tableModel;
	private JTable playerTable;
	private JScrollPane scrollPane;
	//显示列表按钮
	private JButton showlistBtn;
	//隐藏列表按钮
	private JButton hidelistBtn;
	private HashMap<Long, Integer> playersMap;
	public OnlinePlayerPanel() {
		super(SoftConstant.online_player_panel_bgimg);
		setLayout(null);				
		playersMap=new HashMap<>();
		
		tiplabel=new JLabel("玩家列表");
		tiplabel.setFont(SoftConstant.OnlienPlayerTitlefont);
		tiplabel.setBounds(5, 20, 90, 40);

		playerTable=new JTable();
		playerTable.setEnabled(false);
		scrollPane=new JScrollPane(playerTable,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 70, 165, 390);
		scrollPane.setVisible(false);
		
		showlistBtn=new OnlyImgButton(OnlyImgButton.SHOWLIST_BTN, 64, 64);
		hidelistBtn=new OnlyImgButton(OnlyImgButton.HIDELIST_BTN, 64, 64);
		
		showlistBtn.setBounds(110, 5, 64, 64);
		hidelistBtn.setBounds(110, 5, 64, 64);
		hidelistBtn.setVisible(false);
		
		showlistBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Thread(new SlideAnimate(10, SlideAnimate.DOWN)).start();				
			}
		});
		
		hidelistBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Thread(new SlideAnimate(10, SlideAnimate.UP)).start();
			}
		});
		
		add(tiplabel);
		add(scrollPane);
		add(showlistBtn);
		add(hidelistBtn);
		
		//初始化玩家列表
		PlayerAction playerAction=new PlayerAction.Builder("")
				.setOutterPanel(OnlinePlayerPanel.this).build();
		playerAction.getInitPlayers();
	}
	//得到数据库中的玩家信息
	public synchronized void playerInfoInit(List<Player> players){
		datas=new String[players.size()][3];
		for(int i=0;i<players.size();i++){
			datas[i][0]=players.get(i).getLevel()+"级";
			datas[i][1]=players.get(i).getName();
			switch(players.get(i).getPlayerState()){
			case 0:datas[i][2]="已离线";break;
			case 1:datas[i][2]="大厅中";break;
			case 2:datas[i][2]="房间中";break;
			}
			playersMap.put(players.get(i).getId(), i);
		}
		tableModel=new DefaultTableModel(datas, COLUMNS);
		playerTable.setModel(tableModel);
	}
	//回调方法
	public synchronized void palyerStateChange(Player player) {
		//闪动字体
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					twinklingText();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();	
		//更改列表中玩家的状态
		if(datas==null)return;
		int count=0;
		for(long id:playersMap.keySet()){
			//如果当前列表中包含更新的用户
			if(player.getId()==id){
				int index=playersMap.get(id);
				switch(player.getPlayerState()){
					case 0:tableModel.setValueAt("已离线", index, 2);;break;
					case 1:tableModel.setValueAt("大厅中", index, 2);;break;
					case 2:tableModel.setValueAt("房间中", index, 2);;break;
				}
				break;
			}
			count++;
		}
		//如果是新注册进来的玩家
		if(count==playersMap.size()){
			String[][] newdatas=new String[datas.length+1][3];
			//拷贝
			for(int i=0;i<=datas.length;i++){
				if(i==datas.length){
					newdatas[i][0]=player.getLevel()+"级";
					newdatas[i][1]=player.getName();
					switch(player.getPlayerState()){
						case 0:newdatas[i][2]="已离线";break;
						case 1:newdatas[i][2]="大厅中";break;
						case 2:newdatas[i][2]="房间中";break;
					}
					playersMap.put(player.getId(), i);
				}
				else{
					newdatas[i][0]=datas[i][0];
					newdatas[i][1]=datas[i][1];
					newdatas[i][2]=datas[i][2];
				}
			}
			tableModel=new DefaultTableModel(newdatas, COLUMNS);
			playerTable.setModel(tableModel);
		}
	}
	
	//闪动字体提示有用户上线
	public synchronized void twinklingText() throws InterruptedException{
		for(int i=0;i<4;i++){
			Thread.sleep(90);
			if(i==0||i==2){
				tiplabel.setForeground(Color.white);
			}
			else{
				tiplabel.setForeground(Color.black);
			}
		}
	}
	private class SlideAnimate implements Runnable{
		public static final int DOWN=1;
		public static final int UP=2;
		private int _sleepTime,_direct;
		public SlideAnimate(int sleepTime,int direct) {
			// TODO Auto-generated constructor stub
			_sleepTime=sleepTime;
			_direct=direct;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(DOWN==_direct){
				scrollPane.setVisible(true);
				for(int i=0;i<60;i++){
					try {
						Thread.sleep(_sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OnlinePlayerPanel.this.setBounds(890, 65, 190, 80+i*7);
				}
				showlistBtn.setVisible(false);
				hidelistBtn.setVisible(true);
			}
			if(UP==_direct){	
				for(int i=0;i<60;i++){
					try {
						Thread.sleep(_sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OnlinePlayerPanel.this.setBounds(890, 65, 190, 500-i*7);
				}
				scrollPane.setVisible(false);
				hidelistBtn.setVisible(false);
				showlistBtn.setVisible(true);
			}
		}
	}
}

