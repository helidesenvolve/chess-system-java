package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
	
	private ChessMatch chessMatch;   //criar o atributo p associar o rei à partida, para fazer o roque!
                                            
	public King(Board board, Color color, ChessMatch chessMatch) { //colocar a partida (chessmatch) no construtor para fazer associação
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString(){
		return "K";
	}

	
	private boolean canMove(Position position){
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p == null || p.getColor() != getColor();	
	}
	
	private boolean testRookCastling(Position position){   //testar se a torre pode fazer o roque
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p !=null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0; 
	}
	
	@Override
	public boolean[][] possibleMoves() { //por padrao boolean inicia false (todas posições da matriz iniciam com falso)
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];  //retornar uma matriz do tamanho do tabuleiro
		
		Position p = new Position(0, 0);
		
		//above
		p.setValues(position.getRow() - 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//below
		p.setValues(position.getRow() + 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//left
		p.setValues(position.getRow(), position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//right
		p.setValues(position.getRow(), position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//nw
		p.setValues(position.getRow() - 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//ne
		p.setValues(position.getRow()- 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//sw
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//se
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// specialmove castling
		if(getMoveCount()== 0 && !chessMatch.getCheck()){ //SE O REI NÃO SE MOVEU E NÃO ESTA EM CHEQUE...
			//SPECIALMOVE CASTLING KINGSIDE ROOK (roque com a torre do lado do rei - roque pequeno
			Position posT1 = new Position(position.getRow(), position.getColumn() + 3); //posição q a torre deve estar
			if(testRookCastling(posT1)){   //testar se a torre nessa posição pode fazer o roque
				Position p1 = new Position(position.getRow(), position.getColumn() + 2);   //casa entre o rei  e a torre
				Position p2 = new Position(position.getRow(), position.getColumn() + 1);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null){
					mat[position.getRow()][position.getColumn() + 2] = true;
				}
			}
			//SPECIALMOVE CASTLING QUEENGSIDE ROOK (roque com a torre do lado da rainha - roque grande
			Position posT2 = new Position(position.getRow(), position.getColumn() - 4); //posição q a torre deve estar
			if(testRookCastling(posT2)){   //testar se a torre nessa posição pode fazer o roque
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);   //casa entre o rei  e a torre
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null){
					mat[position.getRow()][position.getColumn() - 2] = true;
				}
			}
		}
		
		return mat;
	}
}


