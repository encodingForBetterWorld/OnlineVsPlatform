package soft.chess.client.GameHall;

import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import soft.chess.domain.GameRecord;
import soft.chess.domain.Player;
import soft.chess.util.BoundUtil;
import soft.chess.util.DateUtil;
import soft.chess.util.FontUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.SoftConstant;

public class ShowRecordsPanel extends JPanel{
	//显示用户的资料
	private topPanel topPanel;
	//显示用户的战绩
	private bottomPanel bottomPanel;
	
	private Player myself;
	
	public ShowRecordsPanel() {
		// TODO Auto-generated constructor stub
		setLayout(null);
		topPanel=new topPanel();
		topPanel.setBounds(0, 0, 300, 200);
		add(topPanel);
		
		bottomPanel=new bottomPanel();
		bottomPanel.setBounds(0, 200, 300, 280);
		add(bottomPanel);
	}	
	public void setMyself(Player myself) {
		this.myself = myself;
		topPanel.hasLogin();
	}

	public void receiveRecords(Player player){
		List<GameRecord> win_records=JsonUtil.JSONArrayParseObjectArray(player.getWin_records(), GameRecord.class);
		List<GameRecord> lose_records=JsonUtil.JSONArrayParseObjectArray(player.getLose_records(), GameRecord.class);
		topPanel.hasRecords(win_records.size(), lose_records.size());
		bottomPanel.hasRecords(win_records, lose_records);
	}
	private class topPanel extends JPanel{
		private PlayerInfoPanel playerInfoP;
		private JLabel l1,l2,l3;
		private JLabel n1,n2,n3;
		public topPanel() {
			// TODO Auto-generated constructor stub
			setLayout(null);
			playerInfoP=new PlayerInfoPanel();
			playerInfoP.setBounds(5, 5, 150, 200);
			add(playerInfoP);
			
			l1=new JLabel("胜率:");
			l2=new JLabel("胜场:");
			l3=new JLabel("负场:");
			
			n1=new JLabel();
			n2=new JLabel();
			n3=new JLabel();
			
			FontUtil.setComsFont(SoftConstant.userNamefont, l1,l2,l3,n1,n2,n3);
			
			BoundUtil.setCompsBound(BoundUtil.vertical, 160, 10, 60, 30, 0, 20, l1,l2,l3);
			
			BoundUtil.setCompsBound(BoundUtil.vertical, 230, 10, 60, 30, 0, 20, n1,n2,n3);
			
			add(l1);
			add(l2);
			add(l3);
			add(n1);
			add(n2);
			add(n3);
		}
		public void hasLogin(){
			playerInfoP.hasLogin(myself);
		}
		public void hasRecords(int win,int lose){
			double w=0.000;
			double l=0.000;
			w+=win;
			l+=lose;
			double r=w/(w+l);
			float ratio=Math.round(r*100);
			n1.setText(""+ratio+"%");
			n2.setText(""+win);
			n3.setText(""+lose);
		}
	}
	private class bottomPanel extends JPanel{
		private JLabel l1,l2;
		private JScrollPane jsc1,jsc2;
		private JTable wt,lt;
		private TableModel wtm,ltm;
		private final String[] WIN_COLUMNS={"战胜","时间","时长"};
		private final String[] LOSE_COLUMNS={"负于","时间","时长"};
		private String[][] win_datas,lose_datas;
		public bottomPanel() {
			setLayout(null);
			
			l1=new JLabel("胜场列表");
			l2=new JLabel("负场列表");
			
			l1.setFont(SoftConstant.OnlienPlayerTitlefont);
			l2.setFont(SoftConstant.OnlienPlayerTitlefont);
			
			l1.setHorizontalTextPosition(JLabel.CENTER);
			l2.setHorizontalTextPosition(JLabel.CENTER);
			
			l1.setForeground(Color.red);
			
			l1.setBounds(0, 0, 150, 40);
			l2.setBounds(150, 0, 150, 40);
			
			add(l1);
			add(l2);
			
			wt=new JTable();
			lt=new JTable();
			wt.setEnabled(false);
			lt.setEnabled(false);
			
			jsc1=new JScrollPane(wt,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsc2=new JScrollPane(lt,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			jsc1.setBounds(0, 40, 150, 220);
			jsc2.setBounds(150, 40, 150, 220);
			
			add(jsc1);
			add(jsc2);
		}
		public void hasRecords(List<GameRecord> winrs,List<GameRecord> losers){
			win_datas=new String[winrs.size()][3];
			lose_datas=new String[losers.size()][3];
			
			for(int i=0;i<winrs.size();i++){
				GameRecord _wr=winrs.get(i);
				win_datas[i][0]=_wr.getLoser_name();
				win_datas[i][1]=DateUtil.getDate("yyyy-MM-dd HH:mm:ss", _wr.getEndtime());
				win_datas[i][2]=DateUtil.getDate("mm:ss", _wr.getGametime());
			}
			for(int i=0;i<losers.size();i++){
				GameRecord _lr=losers.get(i);
				lose_datas[i][0]=_lr.getWinner_name();
				lose_datas[i][1]=DateUtil.getDate("yyyy-MM-dd HH:mm:ss", _lr.getEndtime());;
				lose_datas[i][2]=DateUtil.getDate("mm:ss", _lr.getGametime());
			}
			
			wtm=new DefaultTableModel(win_datas,WIN_COLUMNS);
			ltm=new DefaultTableModel(lose_datas,LOSE_COLUMNS);
			
			wt.setModel(wtm);
			lt.setModel(ltm);
		}
	}
}
