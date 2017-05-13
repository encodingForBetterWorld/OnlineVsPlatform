package soft.chess.client;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import soft.chess.domain.Player;

public class UpdatePlayerInfoClient extends JFrame {
	private static UpdatePlayerInfoClient upic=null;
	private static Player myself;
	
	private JButton submitButton = null,
			updatePhotoButton = null;
	
	private JLabel nameLabel = null,
			inconLabel = null;
	
	private JTextField nameTextf = null;
	
	private JFileChooser jfile = null;
	private UpdatePlayerInfoClient() {
		// TODO Auto-generated constructor stub
		submitButton=new JButton("确认修改");
		updatePhotoButton=new JButton("修改头像");
		
		nameLabel=new JLabel("学生姓名");
	}
	public static UpdatePlayerInfoClient getInstance(Player player){
		if(upic==null){
			upic=new UpdatePlayerInfoClient();
		}
		setMyself(player);
		return upic;
	}
	//注入对象
	private static void setMyself(Player myself){
		UpdatePlayerInfoClient.myself=myself;
		
	}
}
