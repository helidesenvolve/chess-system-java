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
		List<ChessPiece> captured = new ArrayList<>(); //lista de pe�as capturadas
		
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
				// sobrecarga - mais de uma vera�o do msm m�todo, variando a lista de parametros (printBoard)
				
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				 if(capturedPiece != null){   
					 captured.add(capturedPiece);   //adiciona na lista de pe�as capturadas
				 }
				 
				 if(chessMatch.getPromoted() != null){
					 System.out.print("Enter piece for pormotion (B/N/Q/R): ");
					 String type = sc.nextLine().toUpperCase();   //letra mauiscula
					 while(!type.equals("B") && !type.equals("N") && !type.equals("R") &&! type.equals("Q")){  //enquanto n digitar um valor valido...
						 System.out.print("Invalid value! Enter piece for pormotion (B/N/Q/R): ");  //pedir p digitar um valor valido
						 type = sc.nextLine().toUpperCase();   //letra mauiscula
					 }
					 chessMatch.replacePromotedPiece(type); //por padrao a pe�a sera rainha mas o usuario podera trocar, apos digitar a pe�a, ela sera colocada no jogo
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
