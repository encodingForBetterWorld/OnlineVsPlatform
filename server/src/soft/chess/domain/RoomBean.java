package soft.chess.domain;

import java.util.Set;

import soft.chess.common.BaseEntity;
//不与数据库发生关系，不需要持久化，服务器重启后刷新
public class RoomBean{
	private long roomid;
	private String title;
	private long createtime;
	private Player creater;
	private Player joiner;
	//用于统计房间内的人数
	private int num=1;
	//房间状态,0:等待中,1:游戏中
	private byte roomState=0;
	//房间游戏类型,0:五子棋
	private byte gameType=0;
	//先手后手
	private Player blackPlayer;
	private Player whitePlayer;
	
	//比赛结果
	private GameRecord record;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public Player getCreater() {
		return creater;
	}
	public void setCreater(Player creater) {
		this.creater = creater;
	}
	public Player getJoiner() {
		return joiner;
	}
	public void setJoiner(Player joiner) {
		this.joiner = joiner;
	}

	public byte getRoomState() {
		return roomState;
	}
	public void setRoomState(byte roomState) {
		this.roomState = roomState;
	}
	public byte getGameType() {
		return gameType;
	}
	public void setGameType(byte gameType) {
		this.gameType = gameType;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getRoomid() {
		return roomid;
	}
	public void setRoomid(long roomid) {
		this.roomid = roomid;
	}
	public Player getBlackPlayer() {
		return blackPlayer;
	}
	public void setBlackPlayer(Player blackPlayer) {
		this.blackPlayer = blackPlayer;
	}
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	public void setWhitePlayer(Player whitePlayer) {
		this.whitePlayer = whitePlayer;
	}
	public GameRecord getRecord() {
		return record;
	}
	public void setRecord(GameRecord record) {
		this.record = record;
	}
	
	
}
