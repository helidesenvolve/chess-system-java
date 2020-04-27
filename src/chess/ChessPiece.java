package chess;

import boardgame.Board;
import boardgame.Piece;

public abstract class ChessPiece extends Piece{
	
	private Color color;

	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() { //tiramos o set pois a cor da peça não pode ser alterada. sera somente acessada.
		return color;
	}
	

	
	

}
