package soft.chess.domain;

import soft.chess.common.BaseEntity;

public class Announcement extends BaseEntity {
	private long createtime;
	private String content;
	private long creater_id;
	private Player creater;
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreater_id() {
		return creater_id;
	}
	public void setCreater_id(long creater_id) {
		this.creater_id = creater_id;
	}
	public Player getCreater() {
		return creater;
	}
	public void setCreater(Player creater) {
		this.creater = creater;
	}
	
}
