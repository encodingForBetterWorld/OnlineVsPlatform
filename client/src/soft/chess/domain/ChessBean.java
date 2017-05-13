package soft.chess.domain;
//不与数据库发生关系，不需要持久化，服务器重启后刷新
public class ChessBean {
	//棋子在棋盘中的横轴坐标
	private byte x;
	//棋子在棋盘中的纵轴坐标
	private byte y;
	//棋子的颜色:1为黑色,0为白色
	private byte color;
	//游戏状态:1为黑方获胜，2为白方获胜，3为和局，4为继续
	private int chessState=4;
	
	public byte getX() {
		return x;
	}
	public void setX(byte x) {
		this.x = x;
	}
	public byte getY() {
		return y;
	}
	public void setY(byte y) {
		this.y = y;
	}
	public byte getColor() {
		return color;
	}
	public void setColor(byte color) {
		this.color = color;
	}
	public int getChessState() {
		return chessState;
	}
	public void setChessState(int chessState) {
		this.chessState = chessState;
	}
	
}
