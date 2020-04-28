package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>(); //lista de peças capturadas
		
		while(!chessMatch.getCheckMate()){
			try{
				UI.clearScreen(); //limpar a tela toda vez que voltar no while
					
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves); //versao que vai imprimir os movimentos possiveis
				// sobrecarga - mais de uma veraão do msm método, variando a lista de parametros (printBoard)
				
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				 if(capturedPiece != null){   
					 captured.add(capturedPiece);   //adiciona na lista de peças capturadas
				 }
			}
			catch(ChessException e){
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e){
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		} 
		UI.clearScreen();  //terminou o "while", limpa a tela e...
		UI.printMatch(chessMatch, captured); //mostra a tela finalizada
	}

}
