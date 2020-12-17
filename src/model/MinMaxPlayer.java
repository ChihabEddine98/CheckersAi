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

		Move bestMove=minimax(game,2,player,null);
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

	private double maxValue(Game game,Move m)
	{
		if (game.isGameOver())
		{
			transpositionTableMax.add(game, (int) m.getWeight());
			return m.getWeight();
		}

		if (transpositionTableMax.getValue(game)!=null)
		{
			return transpositionTableMax.getValue(game);
		}

		double v=m.WEIGHT_INVALID;
		List<Move> moves = getMoves(game);

		for (Move move:moves)
		{
			v=Math.max(v,minValue(game,move));
		}

		return v;
	}

	private double minValue(Game game,Move m)
	{
		if (game.isGameOver())
		{
			transpositionTableMin.add(game, (int) m.getWeight());
			return m.getWeight();
		}

		if (transpositionTableMin.getValue(game)!=null)
		{
			return transpositionTableMin.getValue(game);
		}

		double v=Double.POSITIVE_INFINITY;
		List<Move> moves = getMoves(game);

		for (Move move:moves)
		{
			v=Math.min(v,maxValue(game,move));
		}

		return v;
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
