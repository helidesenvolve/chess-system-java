package chess;

import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

public class ChessMatch { //partida de xadrez (sera o coração do nosso pragrama)
	
	private int turn;
	private Color currentPlayer;
	private Board board;  //tabuleiro
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();  //lista com as peças que estao no tabuleiro
	private List<Piece> capturedPieces = new ArrayList<>();  //lista das peças capturadas
	
	public ChessMatch(){
		board = new Board(8, 8); //tabuleiro do xadrez
		initialSetup();
		currentPlayer = Color.WHITE;   //o jogador no inicio da partida é o white
		turn = 1; //turno no inicio da partida vale 1
		check = false;  //por padrão uma propriedade boolean ja inicia com "false", so coloca aqui se quiser enfatizar
	}
	
	public int getTurn(){
		return turn;
	}
	
	public Color getCurrentPlayer(){
		return currentPlayer;
	}
	
	public boolean getCheck(){
		return check;
	}
	
	public boolean getCheckMate(){
		return checkMate;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){ //marcar as posiçoes possiveis no tabuleiro
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);             //validar a posição de origem, se não existit irá lança uma exceção
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);  //movimento 
		
		if(testCheck(currentPlayer)){        //se o jogador atual se coloca em cheque
			undoMove(source,  target, capturedPiece);  //desfaz a jogada
			throw new ChessException ("You can't put yourself in check");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;  //se o openente ficou em cheque recebe true senão false
		
		if(testCheckMate(opponent(currentPlayer))){
			checkMate = true;
		}
		else{
			nextTurn();  //proximo turno(jogada)
		}
			return (ChessPiece) capturedPiece;      //retornar a peça capturada. fazer um downcast pq a capturedPiece era do tipo Piece
	}
	
	private Piece makeMove (Position source, Position target){
		ChessPiece p = (ChessPiece)board.removePiece(source);   //retirar a peça da posição de origem
		p.increaseMoveCount();    //INCREMENTAR UM MOVIMENTO
		Piece capturedPiece = board.removePiece(target);    //remover uma possivel peça q esteja na posiçao de destino
		board.placePiece(p, target);   //tira uma peça da origem e coloca no destino
		
		if(capturedPiece != null){     //peça capturada
			piecesOnTheBoard.remove(capturedPiece);    //remover da lista de peças no tabuleiro
			capturedPieces.add(capturedPiece);    // adiciona na lista de peças capturadas
		}
		
		return capturedPiece;   //retorna a peça capturada
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece){  //desfazer o movimento
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null){    //tem voltar a peça na posição de destino
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);   //tira a peça de peças capturadas
			piecesOnTheBoard.add(capturedPiece);    //adiciona a peça na lista de peças no tabuleiro
		}
		
	}
	
	private void validateSourcePosition(Position position){
		if(!board.thereIsAPiece(position)){
			throw new ChessException("There is no piece on source position");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
			throw new ChessException("The chosen piece is not yours");
		}
				
		if(!board.piece(position).isThereAnyPossibleMove()){   //testar se há algum movimento possível
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target){
		if(!board.piece(source).possibleMove(target)){
			throw new ChessException("The chosen piece can´t move to target position.");
		}
	}
	
	private void nextTurn(){
		turn++; //incrementer o turno
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;  //condição condicional ternaria
	}
	
	private Color opponent (Color color){  //metodo que dado uma cor devolve o oponente desta cor
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king (Color color){
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list){
			if(p instanceof King){  //"é um"
				return (ChessPiece) p;   //downcast
			}
		}
		throw new IllegalStateException("There is no" + color + " king on the board"); // não é p acontecer, se acontecer é um erro no sistema	
	}
	
	private boolean testCheck(Color color){
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces){   //para cada peça p na lista de peças do oponente...
			boolean[][] mat = p.possibleMoves();   //matris de movimentos possiveis da peça adversaria p.
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]){  //se o rei na posição da matriz acima, o rei estara em cheque
				return true;
			}
		}
		return false;  //se nenhuma peça p tem movimento possivel ate a posição na matriz onde esta o rei.
	}
	
	private boolean testCheckMate(Color color){
		if(!testCheck(color)){  //se esta cor não esta em cheque significa que tb n esta em cheque mate
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list){
			boolean[][] mat = p.possibleMoves();
			for(int i= 0; i < board.getRows(); i++){
				for(int j = 0; j < board.getColumns(); j++){
					if(mat[i][j]){  //movimento possivel(possibleMoves)
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece); //defazer o movimento
						if(!testCheck){     
							return false;
						}
					}
				}
			}
		}
		return true; //esta em ccheckMate
	}
	
	
	private void placeNewPiece(char column, int row, ChessPiece piece){
		board.placePiece(piece,new ChessPosition(column, row).toPosition());  //colocar a peça no tabuleiro
		piecesOnTheBoard.add(piece);      //colocar a peça também na lista
		
	}
	
	private void initialSetup(){
	    placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));

	}	
}
