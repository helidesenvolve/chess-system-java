package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch { //partida de xadrez (sera o coração do nosso pragrama)
	
	private int turn;
	private Color currentPlayer;
	private Board board;  //tabuleiro
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	
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
	
	public ChessPiece getEnPassantVulnerable(){
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted(){
		return promoted;
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
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);   //peça que se moveu esta no destino (target)
		
		//specialmove promotion (ANTES DO CHECK PQ APOS A PROMOÇÃO PODE COLOCAR O REI EM CHEQUE)
		promoted = null;
		if(movedPiece instanceof Pawn){      //se a peça movimentada é um peão
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)){
				promoted = (ChessPiece)board.piece(target);   //peça promovida: peão que esta na posição final
				promoted = replacePromotedPiece("Q");   //peça substituta por padrão sera rainha, mas podera troca-la.
			}
			
			
		}
		
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;  //se o openente ficou em cheque recebe true senão false
		
		if(testCheckMate(opponent(currentPlayer))){
			checkMate = true;
		}
		else{
			nextTurn();  //proximo turno(jogada)
		}
		
		//specialmove en passant                               //testar se o peão andou 2 casas
		if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)){
			enPassantVulnerable = movedPiece;   //peça movida é a peça vulnerável
		}
		else{
			enPassantVulnerable = null;
		}
		
		return (ChessPiece) capturedPiece;      //retornar a peça capturada. fazer um downcast pq a capturedPiece era do tipo Piece
	}
	
	public ChessPiece replacePromotedPiece(String type){
		if(promoted == null){
			throw new IllegalStateException("There is no to be promoted");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toPosition();   //posição da peça a ser promovida
		Piece p = board.removePiece(pos);   //peça p removida do tabuleiro
		piecesOnTheBoard.remove(p); //peça p removida da lista de peças no tabuleiro
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());  //peça da msm cor da peça promovida
		board.placePiece(newPiece, pos);   //nova peça no local da peca promovida (pos)
		piecesOnTheBoard.add(newPiece);   //add na lista a nova peça
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color){
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);

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
		
		//specialmoce castilig kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2){ //se a peça for um rei e a posição d edestino for 2 casas a direita da posição de inicio
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);  //torre vai ser removida da posição inicial
			board.placePiece(rook, targetT);  //torre vai para a posição final (target)
			rook.increaseMoveCount();    //incrementar um movimento p a torre
		}
		//specialmoce castilig queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2){ //se a peça for um rei e a posição d edestino for 2 casas a direita da posição de inicio
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);  //torre vai ser removida da posição inicial
			board.placePiece(rook, targetT);  //torre vai para a posição final (target)
			rook.increaseMoveCount();    //incrementar um movimento p a torre
		}
		
		//specialmove en passant
		if(p instanceof Pawn){
			if(source.getColumn() != target.getColumn() && capturedPiece == null){  //coluna inicial do peao dif da coluna final(qr dizer que o peao andou na diagonal - en passant)
				Position pawnPosition;
				if(p.getColor() == Color.WHITE){  //se a peça que capturou foi um peao branco...
					pawnPosition = new Position(target.getRow() + 1, target.getColumn()); //posição do peão que deve ser capturado
				}
				else{              //peças pretas
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());  //posição do peao que deve ser capturado
				}
				capturedPiece = board.removePiece(pawnPosition);   //remover o peão do tabuleiro
				capturedPieces.add(capturedPiece);  //add da lista de peças capturadas
				piecesOnTheBoard.remove(capturedPiece);  //remove a peça capturada da lista de peças do tabuleiro
			}
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
		
		//specialmoce castilig kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2){ //se a peça for um rei e a posição d edestino for 2 casas a direita da posição de inicio
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);  //torre vai ser removida da posição final
			board.placePiece(rook, sourceT);  //torre vai para a posição final (target)
			rook.decreaseMoveCount();    //incrementar um movimento p a torre
		}
		//specialmoce castilig queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2){ //se a peça for um rei e a posição d edestino for 2 casas a direita da posição de inicio
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);  //torre vai ser removida da posição inicial
			board.placePiece(rook, sourceT);  //torre vai para a posição final (target)
			rook.decreaseMoveCount();    //decrementar um movimento p a torre
		}
			
		//specialmove en passant   //DESFAZENDO A JOGADA
		if(p instanceof Pawn){
			if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){  
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if(p.getColor() == Color.WHITE){  //peao branco
					pawnPosition = new Position(3, target.getColumn()); //devolver para o linha 3
				}
				else{              //peças pretas
					pawnPosition = new Position(4, target.getColumn());  //devolver para a linha 4
				}
				board.placePiece(pawn, pawnPosition); //devolver o (peão, para a pawn position)
				//algoritimo generico ja fez a troca de listas
			}
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
	    placeNewPiece('b', 1, new Knight(board, Color.WHITE));
	    placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
	    placeNewPiece('d', 1, new Queen(board, Color.WHITE));
	    placeNewPiece('e', 1, new King(board, Color.WHITE, this)); //por causa da jogada especial
	    placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
	    placeNewPiece('g', 1, new Knight(board, Color.WHITE));
	    placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
	    placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
	    placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
	    placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

	}	
}
