package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import net.sf.json.JSONObject;
import soft.chess.action.PlayerAction;
import soft.chess.action.RoomAction;
import soft.chess.client.GameHallClient;
import soft.chess.client.GameRoomClient;
import soft.chess.client.GameRoomSettingClient;
import soft.chess.client.ShowRecordsClient;
import soft.chess.common.PageBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.receiveData.ReceiveDataFromServer;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.FrameUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.OnlyImgButton;
import soft.chess.util.RoundRectButton;
import soft.chess.util.SocketUtil;
import soft.chess.util.SoftConstant;

/**
 * 
 * ClassName: GameRoomSearchPanel 
 * @Description: 显示标题，搜索房间(按房间号搜索),搜索等待中的房间或已开始游戏的房间
 * @author wangsuqi
 * @date 2016年10月8日
 * @see
 */
public class GameHallMainPanel extends BackgroundImagePanel{
	//开启房间设置界面
	private GameRoomInfoPanel currenRoomp;
	private volatile GameRoomSettingClient grsc;
	private ShowRecordsClient src;
	private GameRoomClient grc;
	//引入通讯模块
	private RoomAction roomAction;
	private PlayerAction playerAction;
	//登录的用户
	private Player player;
	//只记录本页的房间信息
	private List<RoomBean> rooms;
	//用于分页查询
	private PageBean roomPage;
	//房间记录数
	private int rowCount;
	
	private volatile static boolean islogin=false; 
	//游戏大厅
	private GameHallClient gameHall=null;
	//显示游客界面
	private VisitorPanel visitorp;	
	//游客登录后切换玩家界面
	private PlayerInfoPanel playerp;
//	//玩家操作界面
//	private static PlayerOperationPanel operationPanel;
	//显示在线玩家
	private OnlinePlayerPanel OnlinePlayerListp;
	//显示时间
	private JPanel timep;
	//顶头的组件
	private JTextField text;
	private JLabel txtLabel;
	//查询房间
	private JButton searchBtn,showWaitBtn,showGamingBtn;
	//显示房间列表的翻页
	private JButton prebtn,nextbtn,gobtn;
	private JComboBox<String> pageBox;
	//公告更新提示
	private int annCount=0;
	private JLabel updatetip,numtip;
	private JButton logoutBtn=new RoundRectButton("注销",120,50),
	updateInfoButton = new RoundRectButton("编辑资料",120,50),
	showRecordButton = new RoundRectButton("查看战绩",120,50),
	creatRoomButton = new RoundRectButton("创建房间", 120,50),
	showAnnButton = new RoundRectButton("显示公告", 120,50);
	
	//事件监听类
	private static HallMainAction MainAction=null;
	
	//房间小面板的列表
	private List<GameRoomInfoPanel> roomps;
	//接收服务器信号
	private Thread receiveServerMsgThread=null;
	private Runnable receiveServerMsgRun=null;
	public GameHallMainPanel(GameHallClient gameHall) {
		super(SoftConstant.hall_panel_bgimg);
		
		setLayout(null);
		
		MainAction=new HallMainAction();
		this.rooms=new ArrayList<>();
		this.gameHall=gameHall;
		//每页包含9条房间信息
		this.roomPage=new PageBean();
		
		searchBtn=new OnlyImgButton(OnlyImgButton.SEARCH_BTN, 48, 48);
		
		updatetip=new JLabel();
		updatetip.setBounds(110, 400, 64, 64);
		updatetip.setLayout(null);

		//显示更新数字		
		numtip=new JLabel("");
		numtip.setFont(SoftConstant.mfont);
		numtip.setForeground(Color.white);
		
		updatetip.setVisible(false);
		updatetip.add(numtip);

		add(updatetip);
		//批量设置布局并添加监听事件
		FrameUtil.baseSetBtn(this, MainAction,
				80, 190, 28, 120, 50,
				SoftConstant.smfont,
				updateInfoButton,showRecordButton,creatRoomButton,showAnnButton,logoutBtn);
		//对游客隐藏
		updateInfoButton.setVisible(false);
		showRecordButton.setVisible(false);
		creatRoomButton.setVisible(false);
		showAnnButton.setVisible(false);
		logoutBtn.setVisible(false);
		
		visitorp=new VisitorPanel(gameHall);
		visitorp.setBounds(5, 5, 150, 225);
		
		playerp=new PlayerInfoPanel();
		playerp.setBounds(5, 5, 150, 180);
		playerp.setVisible(false);
		
		OnlinePlayerListp=new OnlinePlayerPanel();
		OnlinePlayerListp.setBounds(890, 65, 190, 80);
		OnlinePlayerListp.setBorder(SoftConstant.shadowborder);
				
		timep=new HallClock();
		timep.setBounds(950, 5, 130, 40);
		
		add(visitorp);
		add(playerp);
		add(OnlinePlayerListp);
		add(timep);
		
		//添加显示游戏房间的小面板3*3列表
		roomps=new ArrayList<>();
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				GameRoomInfoPanel sub_roomp=new GameRoomInfoPanel();
				sub_roomp.setBounds(j*250+200, i*180+55, 180, 136);
				//先隐藏起来
				sub_roomp.setVisible(false);
				sub_roomp.setGhmp(this);
				roomps.add(sub_roomp);
				this.add(sub_roomp);
			}
		}
		
		text=new JTextField();
		txtLabel=new JLabel("房间号:");
		
		showWaitBtn=new JButton("显示等待");
		showGamingBtn=new JButton("显示游戏中");
		
		prebtn=new OnlyImgButton(OnlyImgButton.PRE_PAGE_BTN, 32, 32);
		nextbtn=new OnlyImgButton(OnlyImgButton.NEXT_PAGE_BTN, 32, 32);
		gobtn=new OnlyImgButton(OnlyImgButton.GO_PAGE_BTN, 32, 32);
		
		pageBox=new JComboBox<>();
		pageBox.addItem("无记录");
		
		txtLabel.setFont(SoftConstant.userNamefont);
		txtLabel.setForeground(Color.white);
		txtLabel.setBounds(180, 10, 60, 28);
		add(txtLabel);
		
		text.setBounds(250, 10, 180, 28);
		text.setFont(SoftConstant.smfont);
		add(text);
		
		searchBtn.setBounds(450, 2, 48, 48);
		add(searchBtn);
		
		showWaitBtn.setBounds(510, 10, 100, 28);
		add(showWaitBtn);
		
		showGamingBtn.setBounds(620, 10, 100, 28);
		add(showGamingBtn);	
		
		RoomAction nextPage=new RoomAction
				.Builder(RoomAction.QUERY_NEXT_ROOM_BY_PAGE)
				.setOutterPanel(this).build();
		RoomAction prePage=new RoomAction
				.Builder(RoomAction.QUERY_PRE_ROOM_BY_PAGE)
				.setOutterPanel(this).build();
		RoomAction gotoPage=new RoomAction
				.Builder(RoomAction.QUERY_GOTO_ROOM_BY_PAGE)
				.setOutterPanel(this).build();
		prebtn.setBounds(750, 8, 32, 32);
		prebtn.addActionListener(prePage);
		pageBox.setBounds(790, 10, 80, 30);
		gobtn.setBounds(880, 8, 32, 32);
		gobtn.addActionListener(gotoPage);
		nextbtn.setBounds(915, 8, 32, 32);
		nextbtn.addActionListener(nextPage);
		add(prebtn);
		add(pageBox);
		add(gobtn);
		add(nextbtn);
		//发出获取第一页的房间请求
		roomAction=new RoomAction.Builder("")
				.setOutterPanel(this).build();
		roomAction.getInitRooms();
		
		//开始接收
		receiveServerMsgRun=new ReceiveDataFromServer(this);
		receiveServerMsgThread=new Thread(receiveServerMsgRun);
		receiveServerMsgThread.start();
	}
	//add方法被加了锁，当占用了comp的线程没有结束时，add和remove无法被其他线程调用
	public void hasLogin(Player playerBean){
		islogin=true;
		this.player=playerBean;
		//显示按钮
		updateInfoButton.setVisible(true);
		showRecordButton.setVisible(true);
		creatRoomButton.setVisible(true);
		showAnnButton.setVisible(true);
		logoutBtn.setVisible(true);
		//显示按钮
		gameHall.getSendp().hasLogin();
		
		visitorp.setVisible(!islogin);		
		//动态注入实体给用户详情面板
		playerp.hasLogin(playerBean);
		gameHall.setEnabled(islogin);
		//登录后，把玩家的信息发送到大厅中
		/**
		 * 这个功能是属于通讯的，应该放在视图模块吗？
		 * 显然不是的，应该放在action通讯模块中
		 */
		playerBean.setPlayerState((byte)1);
		playerAction=new PlayerAction.Builder("").build();
		playerAction.sendPlayerStateToHallBroadcast(playerBean);
		playerAction.sendPlayerStateToHallServer(playerBean);
	}
	//关闭大厅窗口或点击注销后触发
	public synchronized void hasLogout(){
		if(player==null)return;
		updateInfoButton.setVisible(false);
		showRecordButton.setVisible(false);
		creatRoomButton.setVisible(false);
		showAnnButton.setVisible(false);
		logoutBtn.setVisible(false);
		
		gameHall.getSendp().hasLogout();
		
		player.setPlayerState((byte)0);
		playerAction.sendPlayerStateToHallBroadcast(player);
		playerAction.sendPlayerStateToHallServer(player);
		visitorp.setVisible(true);
		playerp.setVisible(false);
	}
	//进入房间后会触发
	
	//回调方法,公告按钮上显示提示
	public void addNumtip() {
		if(!updatetip.isVisible()){
			updatetip.setIcon(ImageIconUtil.setImageIcon("assets/image/button/dot_red.png", updatetip));
			updatetip.setVisible(true);			
		}
		annCount++;				
		numtip.setText(""+annCount);
		int strSize=numtip.getText().length();
		//根据位数改变字体大小
		if(strSize==3&&!numtip.getFont().equals(SoftConstant.mmfont)){
			numtip.setFont(SoftConstant.mmfont);
		}
		if(strSize==4&&!numtip.getFont().equals(SoftConstant.lsmfont)){
			numtip.setFont(SoftConstant.lsmfont);
		}		
		numtip.setBounds(28-strSize*4, 17, 30, 30);
	}
	//跳页
	public boolean NextPage(){
		int cpage=roomPage.getCurrentPage();
		if(cpage==roomPage.getTotalPage()){
			JOptionPane.showMessageDialog(null, "现在是最后一页","错误提示",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		roomPage.setCurrentPage(++cpage);
		return true;
	}
	public boolean PrePage(){
		int cpage=roomPage.getCurrentPage();
		if(cpage==1){
			JOptionPane.showMessageDialog(null, "现在是第一页","错误提示",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		roomPage.setCurrentPage(--cpage);
		return true;
	}
	public void GotoPage(){
		int cpage=pageBox.getSelectedIndex();
		roomPage.setCurrentPage(++cpage);
	}
	//分页显示房间的记录
	public void ShowRoomsByPage(PageBean roompage){
		if(roompage==null)return;
		//隐藏先前的记录
		for(GameRoomInfoPanel pregrip:roomps){
			pregrip.setVisible(false);
		}
		/**
		 * 每次分页查询都可能改变的页码下拉框选项栏
		 */
		pageBox.removeAllItems();
		for(int i=1;i<=roompage.getTotalPage();i++){
			pageBox.addItem("第"+i+"页");
		}
		this.roomPage=roompage;
		/**
		 * 把一维数组转化为4*4的二维面板表显示，设置房间的布局
		 */
		//从服务器动态获取列表信息
		this.rooms=JsonUtil.<RoomBean>JSONArrayParseObjectArray(roompage.getList(),RoomBean.class);
		
		//每次查询初始化rowCount
		rowCount=roompage.getAllRows();
		
		int k=0;
		for(int i=0;i<Math.floor(rooms.size()/3);i++){
			for(int j=0;j<3;j++){
			//更新小面板状态
			roomps.get(i*3+j).changeContent(rooms.get(i*3+j));
			k++;
			}
		}
		//最后一行显示余数个
		for(int i=0;i<rooms.size()%3;i++){
			//更新小面板状态
			roomps.get(i+k).changeContent(rooms.get(i+k));
		}
		
	}
	//每次有用户添加房间,把房间添加上列表面板
	public synchronized void ShowSaveRoom(RoomBean room){
		//数据过多，加页
		rowCount++;
		int ppage=roomPage.getTotalPage();
		int npage=roomPage.getTotalPages(rowCount);
		if(ppage==0){
			pageBox.removeAllItems();
		}
		if(ppage<npage){
			pageBox.addItem("第"+npage+"页");
		}
		//如果正在观看最后一页，显示效果
		if(roomPage.getCurrentPage()==npage){
			roomps.get(rooms.size()).changeContent(room);
			rooms.add(room);
		}
	}
	//每次有房间状态发生变化，如果用户真在看本房间的面板，修改其状态
	public synchronized void ShowUpdateRoom(RoomBean room){
		for(int i=0;i<rooms.size();i++){
			if(rooms.get(i).getRoomid()
					==room.getRoomid()){
				rooms.set(i, room);
				break;
			}
		}
		for(int i=0;i<roomps.size();i++){
			if(roomps.get(i).getRoom().getRoomid()
					==room.getRoomid()){
				roomps.get(i).changeContent(room);
				break;
			}
		}
	}
	//每次有房间被删除，如果用户正在看本房间的面板，将其显示为已关闭
	public synchronized void HideDeleteRoom(RoomBean room){
		//数据过少，加页
		rowCount--;
		int ppage=roomPage.getTotalPage();
		int npage=roomPage.getTotalPages(rowCount);
		if(ppage>npage){
			System.out.println("remove....");
			pageBox.removeItemAt(npage);
		}
		for(int i=0;i<rooms.size();i++){
			if(rooms.get(i).getRoomid()
					==room.getRoomid()){
				rooms.remove(i);
				break;
			}
		}
		for(int i=0;i<roomps.size();i++){
			if(roomps.get(i).getRoom().getRoomid()
					==room.getRoomid()){
				roomps.get(i).hasExpired();
				break;
			}
		}
	}
	//回调方法,打开新窗口，原窗口设置为不可编辑
	public void setGameHallEnabled(boolean isEnabled){
		gameHall.setEnabled(isEnabled);
	}
	//回调方法，创建游戏成功，进入房间，关闭主窗口
	public void CreatedRoom(){
		try {
			gameHall.LeaveGameHall();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized OnlinePlayerPanel getOnlinePlayerListp() {
		return OnlinePlayerListp;
	}
	public synchronized Player getPlayer() {
		return player;
	}
	
	public synchronized PageBean getRoomPage() {
		return roomPage;
	}

	//事件监听类
	private class HallMainAction implements ActionListener{
		private PlayerAction queryRecordsAction;
		@Override
		public void actionPerformed(ActionEvent e) {
			// 编辑个人信息按钮的响应事件
			if(e.getSource().equals(updateInfoButton)){

			}
			//显示个人战绩的按钮
			else if(e.getSource().equals(showRecordButton)){
				src=ShowRecordsClient.getInstance(player);
				queryRecordsAction=new PlayerAction
						.Builder(PlayerAction.QUERY_MY_RECORDS)
						.setOutterPanel(GameHallMainPanel.this).build();
				queryRecordsAction.actionPerformed(e);
			}
			//创建房间
			else if(e.getSource().equals(creatRoomButton)){
				gameHall.setEnabled(false);							
				grsc=GameRoomSettingClient.getInstance(GameHallMainPanel.this);				
			}
			//查看公告
			else if(e.getSource().equals(showAnnButton)){
				annCount=0;
				if(!numtip.getFont().equals(SoftConstant.mfont)){
//					System.out.println("重设字体");
					numtip.setFont(SoftConstant.mfont);
				}
				updatetip.setVisible(false);
			}
			//注销操作
			else if(e.getSource().equals(logoutBtn)){
				hasLogout();
			}
			//查询房间
			else if(e.getSource().equals(searchBtn)){
				
			}
			//显示等待中的房间
			else if(e.getSource().equals(showWaitBtn)){
							
			}
			//显示游戏中的房间
			else if(e.getSource().equals(showGamingBtn)){
				
			}
		}		
	}
	public synchronized GameHallClient getGameHall() {
		return gameHall;
	}
	public synchronized GameRoomSettingClient getGrsc() {
		return grsc;
	}
	public synchronized ShowRecordsClient getSrc() {
		return src;
	}
	public synchronized VisitorPanel getVisitorp() {
		return visitorp;
	}
	public synchronized GameRoomInfoPanel getCurrenRoomp() {
		return currenRoomp;
	}
	public synchronized void setCurrenRoomp(GameRoomInfoPanel currenRoomp) {
		this.currenRoomp = currenRoomp;
	}
	public PlayerInfoPanel getPlayerp() {
		return playerp;
	}
	public synchronized GameRoomClient getGrc() {
		return grc;
	}
	public synchronized void setGrc(GameRoomClient grc) {
		this.grc = grc;
	} 
}
