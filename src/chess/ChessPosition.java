package chess;

import boardgame.Position;

public class ChessPosition { //posição no tabuleiro. coluna de 'a' até 'h' e linha de 1 a 8.

	private char column;
	private int row;
	
	public ChessPosition(char column, int row){
		if(column < 'a' || column > 'h' || row < 1 || row >8){
			throw new ChessException("Error instantiating ChessProsition. Valid values are from a1 to h8.");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() { //retirar os setters para n mudar livremente o numero de linhas e colunas
		return column;
	}

	public int getRow() {
		return row;
	}
	
	//linha (row) da chessPosition (posicao no xadrez) = 8 - row.chessMatch (tabuleiro)
	//coluna (column) da chessPosition (posição no xadrez) = column.chessColumn - 'a'
	
	protected Position toPosition(){
		return new Position(8 - row, column - 'a');
	}
	
	protected static ChessPosition fromPosition(Position position){
		return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	}
	
	@Override
	public String toString(){
		return "" + column + row; //"" forçar o compilador a entender que é uma concatenação de Strings ( ex.: a1)
	}


	
	
}

