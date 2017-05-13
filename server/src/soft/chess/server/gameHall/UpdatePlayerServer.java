package soft.chess.server.gameHall;

import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;

public class UpdatePlayerServer implements Runnable {
	private String info;
	private Player playerBean;
	public UpdatePlayerServer(String info) {
		// TODO Auto-generated constructor stub
		this.info=HallServerMsgUtil.getMsg(info);
	}
	@Override
	public void run() {
		playerBean=(Player) JsonUtil.StrParseObject(info, Player.class);
		Dao dao=Dao.getInstance();
		dao.updateObject(playerBean);
	}

}
