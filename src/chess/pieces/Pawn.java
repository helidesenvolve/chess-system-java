package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		if(getColor() == Color.WHITE){
			p.setValues(position.getRow() - 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 2, position.getColumn());            //primaira jogada(0) pode pular 2 casas
			Position p2 = new Position(position.getRow() - 1, position.getColumn());  //PARA ANDAR 2 CASAS TEMOS QUE TESTAR SE A PRIMEIRA CASA TB ESTA VAZIA (P2)
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0){  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() -1); //PAWN PODE IR P A DIAGONAL SE LA ESTIVER UM OPONENTE
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() +1); //PAWN PODE IR P A DIAGONAL SE LA ESTIVER UM OPONENTE
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
		}	
		else{      //SE NAO FOR BRANCO SERA AS PEÇAS PRETAS
			p.setValues(position.getRow() + 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 2, position.getColumn());            //primaira jogada(0) pode pular 2 casas
			Position p2 = new Position(position.getRow() + 1, position.getColumn());  //PARA ANDAR 2 CASAS TEMOS QUE TESTAR SE A PRIMEIRA CASA TB ESTA VAZIA (P2)
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0){  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() -1); //PAWN PODE IR P A DIAGONAL SE LA ESTIVER UM OPONENTE
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() +1); //PAWN PODE IR P A DIAGONAL SE LA ESTIVER UM OPONENTE
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		return mat;
	}
	
	@Override
	public String toString(){
		return "P";
	}
}
