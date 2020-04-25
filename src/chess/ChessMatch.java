package chess;

import boardgame.Board;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch { //partida de xadrez (sera o coração do nosso pragrama)
	
	private Board board;

	public ChessMatch(){
		board = new Board(8, 8); //tabuleiro do xadrez
		initialSetup();
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
	
	private void placeNewPiece(char column, int row, ChessPiece piece){
		board.placePiece(piece,new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup(){
		placeNewPiece('b' , 6, new Rook(board, Color.WHITE));
		placeNewPiece('e' , 8, new King(board, Color.BLACK));
		placeNewPiece('e' , 1, new King (board, Color.WHITE));
		
	}
}
