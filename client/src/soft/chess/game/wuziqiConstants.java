package soft.chess.game;
public class wuziqiConstants {
	private wuziqiConstants(){};
	public static enum PlayerRole{
		PLAYER1(1),PLAYER2(2),Audience(3);
		private int role;
		private PlayerRole(int role){
			this.role=role;
		}
		public int getRole(){
			return this.role;
		}
	}
	public static enum GameState{
		PLAYER1_WON(1),PLAYER2_WON(2),DRAW(3),CONTINUE(4);
		private int state;
		private GameState(int state){
			this.state=state;
		}
		public int getState(){
			return this.state;
		}
	}
}


