/* Name: ComputerPlayer
 * Author: Devon McGrath
 * Description: This class represents a computer player which can update the
 * game state without user interaction.
 */

package src.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.logic.MoveGenerator;

/**
 * The {@code ComputerPlayer} class represents a computer player and updates
 * the board based on a model.
 */
public class MinMaxPlayer extends ComputerPlayer {

	protected boolean player;
	
	protected StateSet transpositionTableMax, transpositionTableMin;
	
	public MinMaxPlayer(boolean joueur) {
		this.player = joueur;
		this.transpositionTableMax = new StateSet();
		this.transpositionTableMin = new StateSet();
	}
	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void updateGame(Game game) {
		
		// Nothing to do
		if (game == null || game.isGameOver()) {
			return;
		}

		Move bestMove=minimax2(game,6,player,null);
		game.move(bestMove.getStartIndex(),bestMove.getEndIndex());

//		Game copy = game.copy();
//		List<Move> moves = getMoves(copy);
//
//		double bestWeight = Move.WEIGHT_INVALID;
//		Move bestMove=null;
//
//		if (player)
//		{
//			for (Move m:moves)
//			{
//				System.out.println(m.getWeight());
//				double minVal=minValue(copy.copy(),m);
//				if (m.getWeight() > minVal)
//				{
//					bestMove=m;
//				}
//			}
//
//			game.move(bestMove.getStartIndex(),bestMove.getEndIndex());
//		}

	}

	private Move minimax2(Game game,int depth,boolean aiPlayer,Move mv)
	{
		if (depth==0 ||  game.isGameOver()) {
			return mv;
		}

		Game copy = game.copy();


		if (aiPlayer)
		{
			return maxValue(copy,depth,mv);
		}
		else
		{
			return minValue(copy,depth,mv);
		}



	}
	private Move minimax(Game game,int depth,boolean aiPlayer,Move mv)
	{
		if (depth==0 ||  game.isGameOver()) {
			return mv;
		}

		Game copy = game.copy();
		List<Move> moves = getMoves(copy);
		double bestScore;
		Move bestMove=null;

		if (aiPlayer)
		{
			bestScore=Double.NEGATIVE_INFINITY;
			for (Move move:moves)
			{
				Game tempCopy=copy.copy();
				tempCopy.move(move.getStartIndex(),move.getEndIndex());
				Move resMove=minimax(tempCopy,depth-1,tempCopy.isP2Turn(),move);
				double score=resMove.getWeight();
				if (score > bestScore)
				{
					bestScore=score;
					bestMove=move;
				}
			}
		}
		else
		{
			bestScore=Double.POSITIVE_INFINITY;
			for (Move move:moves)
			{
				Game tempCopy=copy.copy();
				tempCopy.move(move.getStartIndex(),move.getEndIndex());
				Move resMove=minimax(tempCopy,depth-1,!tempCopy.isP2Turn(),move);
				double score=resMove.getWeight();
				if (score < bestScore)
				{
					bestScore=score;
					bestMove=move;
				}
			}
		}

		return bestMove;


	}


	// TODO : return double ( or int utility ) instead of move
	// TODO : Memoize already calculated values !
	private Move maxValue(Game game,int depth,Move m)
	{
		System.out.println(" Max :"+transpositionTableMax.getValue(game));
		if (game.isGameOver() || depth==0 || transpositionTableMax.getValue(game)!=null)
		{
			return m;
		}

		List<Move> moves = getMoves(game);
		double bestScore=Double.NEGATIVE_INFINITY;
		Move bestMove=moves.get(0);
		Move resMove;
		Game tempCopy=game;
		for (Move move:moves)
		{
			tempCopy=tempCopy.copy();
			tempCopy.move(move.getStartIndex(),move.getEndIndex());
			if (tempCopy.isP2Turn())
			{
				resMove=maxValue(tempCopy,depth-1,move);
			}
			else
			{
				resMove=minValue(tempCopy,depth-1,move);
			}

			double score=resMove.getWeight();
			if (score > bestScore)
			{
				bestScore=score;
				bestMove=move;
			}
		}


//		transpositionTableMax.add(game,game.goodHeuristic(true));
		return bestMove;
	}

	// TODO : return double ( or int utility ) instead of move
	private Move minValue(Game game,int depth,Move m)
	{
		System.out.println(" Min :"+transpositionTableMin.getValue(game));

		if (game.isGameOver() || depth==0 )
		{
			return m;
		}

		List<Move> moves = getMoves(game);
		double bestScore=Double.POSITIVE_INFINITY;
		Move bestMove=moves.get(0);
		Move resMove;
		Game tempCopy=game;

		for (Move move:moves)
		{
			tempCopy=tempCopy.copy();
			tempCopy.move(move.getStartIndex(),move.getEndIndex());
			if (tempCopy.isP2Turn())
			{
				resMove=maxValue(tempCopy,depth-1,move);
			}
			else
			{
				resMove=minValue(tempCopy,depth-1,move);
			}

			double score=resMove.getWeight();
			if (score < bestScore)
			{
				bestScore=score;
				bestMove=move;
			}
		}

//		transpositionTableMin.add(game,game.goodHeuristic(false));
		return bestMove;
	}



	
	/**
	 * Gets all the available moves and skips for the current player.
	 * 
	 * @param game	the current game state.
	 * @return a list of valid moves that the player can make.
	 */
	protected List<Move> getMoves(Game game) {
		
		// The next move needs to be a skip
		if (game.getSkipIndex() >= 0) {
			
			List<Move> moves = new ArrayList<>();
			List<Point> skips = MoveGenerator.getSkips(game.getBoard(),
					game.getSkipIndex());
			for (Point end : skips) {
				Game copy = game.copy();
				int startIndex = game.getSkipIndex(), endIndex = Board.toIndex(end);
				copy.move(startIndex,endIndex);
				moves.add(new Move(startIndex, endIndex, copy.goodHeuristic(!copy.isP2Turn())));
			}
			Collections.sort(moves);
			return moves;
		}
		
		// Get the checkers
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isP2Turn()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine if there are any skips
		List<Move> moves = new ArrayList<>();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = MoveGenerator.getSkips(b, index);
			for (Point end : skips) {
				Game copy = game.copy();
				int endIndex = Board.toIndex(end);
				copy.move(index,endIndex);
				Move m = new Move(index, endIndex, copy.goodHeuristic(!copy.isP2Turn()));
				moves.add(m);
			}
		}
		
		// If there are no skips, add the regular moves
		if (moves.isEmpty()) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				for (Point end : movesEnds) {
					Game copy = game.copy();
					int endIndex = Board.toIndex(end);
					copy.move(index,endIndex);
					moves.add(new Move(index, endIndex, copy.goodHeuristic(!copy.isP2Turn())));
				}
			}
		}
		Collections.sort(moves);
		return moves;
	}
}
