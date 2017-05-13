package soft.chess.client;

import javax.swing.JFrame;

import soft.chess.client.GameHall.ShowRecordsPanel;
import soft.chess.domain.Player;
import soft.chess.util.FrameUtil;

public class ShowRecordsClient extends JFrame{
	private static ShowRecordsPanel srp;
	private static ShowRecordsClient src;
	private ShowRecordsClient() {
		// TODO Auto-generated constructor stub
		setLayout(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(330, 500);
		
		srp=new ShowRecordsPanel();
		srp.setBounds(10, 10, 300, 460);
		FrameUtil.baseSetFrame(this, srp);
	}
	public static ShowRecordsClient getInstance(Player myself){
		if(src==null){
			src=new ShowRecordsClient();
		}
		srp.setMyself(myself);
		src.setVisible(true);
		return src;
	}
	public void hasRecords(Player player){
		srp.receiveRecords(player);
	}
}
