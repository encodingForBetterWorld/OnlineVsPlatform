package soft.chess.domain;

import java.util.List;
import java.util.Set;
import soft.chess.common.BaseEntity;

public class Player extends BaseEntity{
	private String name;
	private String password;
	private String icon;
	private int level=1;
	//玩家状态0:离线,1:在大厅里,2:在房间里,3:正在游戏
	private byte playerState=0;
	/**
	 * 如果使用hibernate默认的级联查询会导致数据量很大,造成不必要的资源浪费
	 * 并不是在任何情况下都需要用到级联查询
	 */
	private List<Announcement> anns;
	private List<GameRecord> win_records;
	private List<GameRecord> lose_records;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}	

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public byte getPlayerState() {
		return playerState;
	}
	public void setPlayerState(byte playerState) {
		this.playerState = playerState;
	}
	public List<Announcement> getAnns() {
		return anns;
	}
	public void setAnns(List<Announcement> anns) {
		this.anns = anns;
	}
	public List<GameRecord> getWin_records() {
		return win_records;
	}
	public void setWin_records(List<GameRecord> win_records) {
		this.win_records = win_records;
	}
	public List<GameRecord> getLose_records() {
		return lose_records;
	}
	public void setLose_records(List<GameRecord> lose_records) {
		this.lose_records = lose_records;
	}
	
	
}
