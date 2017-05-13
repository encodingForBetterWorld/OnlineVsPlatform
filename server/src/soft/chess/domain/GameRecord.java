package soft.chess.domain;

import soft.chess.common.BaseEntity;

public class GameRecord extends BaseEntity {
	private long winner_id;
	private long loser_id;
	private long begintime;
	private long endtime;
	private long gametime;
	
	private String winner_name;
	private String loser_name;
	
	public long getWinner_id() {
		return winner_id;
	}
	public void setWinner_id(long winner_id) {
		this.winner_id = winner_id;
	}
	public long getLoser_id() {
		return loser_id;
	}
	public void setLoser_id(long loser_id) {
		this.loser_id = loser_id;
	}
	public long getBegintime() {
		return begintime;
	}
	public void setBegintime(long begintime) {
		this.begintime = begintime;
	}
	public long getEndtime() {
		return endtime;
	}
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	public long getGametime() {
		return gametime;
	}
	public void setGametime(long gametime) {
		this.gametime = gametime;
	}
	public String getWinner_name() {
		return winner_name;
	}
	public void setWinner_name(String winner_name) {
		this.winner_name = winner_name;
	}
	public String getLoser_name() {
		return loser_name;
	}
	public void setLoser_name(String loser_name) {
		this.loser_name = loser_name;
	}
	
}
