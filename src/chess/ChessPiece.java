package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece{
	
	private Color color;

	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() { //tiramos o set pois a cor da peça não pode ser alterada. sera somente acessada.
		return color;
	}
	
	public ChessPosition getChessPosition(){
		return ChessPosition.fromPosition(position);  //transforma uma "position" em "chessPosition"
	}
	
	protected boolean isThereOpponentPiece(Position position){
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p != null && p.getColor() != color;
		
	}
	

}
