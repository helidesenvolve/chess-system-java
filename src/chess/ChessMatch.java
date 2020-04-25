package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch { //partida de xadrez (sera o cora��o do nosso pragrama)
	
	private Board board;  //tabuleiro

	public ChessMatch(){
		board = new Board(8, 8); //tabuleiro do xadrez
		initialSetup();
	}
	
	public ChessPiece[][]getPieces(){ //o pragram vai enxergar apenas a pe�a de xadrez(ChessPiece). e nao a camada de tabuleiro (PIece).
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for(int i = 0; i < board.getColumns(); i++){
			for (int j=0; j < board.getColumns(); j++){
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);             //validar a posi��o de origem, se n�o existit ir� lan�a uma exce��o
		Piece capturedPiece = makeMove(source, target);  //movimento 
		return (ChessPiece) capturedPiece;      //retornar a pe�a capturada. fazer um downcast pq a capturedPiece era do tipo Piece
	}
	
	private Piece makeMove (Position source, Position target){
		Piece p = board.removePiece(source);   //retirar a pe�a da posi��o de origem
		Piece capturedPiece = board.removePiece(target);    //remover uma possivel pe�a q esteja na posi�ao de destino
		board.placePiece(p, target);
		return capturedPiece;                      //retorna a pe�a capturada
	}
	
	private void validateSourcePosition(Position position){
		if(!board.thereIsAPiece(position)){
			throw new ChessException("There is no piece on source position");
		}
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece){
		board.placePiece(piece,new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup(){
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));
        
        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));

	}		
	
}
