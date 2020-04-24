package boardgame;

public class Piece {
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;   //posicao vai iniciar nula(não é obrigado colocar null, por padrao o java ja colocaria)
	}

	protected Board getBoard() {
		return board;
	}

	
	
	
	
	

}
