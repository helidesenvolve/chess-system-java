package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString(){
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() { //por padrao boolean inicia false (todas posições da matriz iniciam com falso)
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];  //retornar uma matriz do tamanho do tabuleiro
		return mat;
	}

}
