package chess;

import boardgame.Board;

public class ChessMatch { //partida de xadrez (sera o coração do nosso pragrama)
	
	private Board board;

	public ChessMatch(){
		board = new Board(8, 8); //tabuleiro do xadrez
	}
	
	public ChessPiece[][]getPieces(){ //o pragram vai enxergar apenas a peça de xadrez(ChessPiece). e nao a camada de tabuleiro (PIece).
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for(int i = 0; i < board.getColumns(); i++){
			for (int j=0; j < board.getColumns(); j++){
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
}
