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
	private Move best_move=null;
	
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

		System.out.println(" Before :"+best_move);
		minimax(game,3);
		System.out.println(" After :"+best_move);

		game.move(best_move.getStartIndex(),best_move.getEndIndex());



	}

	private int minimax(Game game,int depth)
	{
		Game copy = game.copy();

		if (player)
		{
			return maxValue(copy,depth);
		}
		else
		{
			return minValue(copy,depth);
		}

	}


	// TODO : return double ( or int utility ) instead of move
	// TODO : Memoize already calculated values !
	private int maxValue(Game game,int depth)
	{
//		System.out.println(" Max :"+transpositionTableMax.getValue(game));
		if (game.isGameOver() || depth==0 || transpositionTableMax.getValue(game)!=null)
		{
			System.out.println(" Max :"+game.goodHeuristic(true));
			return game.goodHeuristic(true);
		}
		// Make Backup for Game instance
		Game temp_game=game;

		List<Move> moves = getMoves(temp_game);
		int best_score=Integer.MIN_VALUE;
		int res_score;

		for (Move move:moves)
		{
			temp_game=temp_game.copy();
			temp_game.move(move.getStartIndex(),move.getEndIndex());

			if (temp_game.isP2Turn()==player)
			{
				res_score=maxValue(temp_game,depth-1);
			}
			else
			{
				res_score=minValue(temp_game,depth-1);
			}

			if (res_score > best_score)
			{
				best_score=res_score;
				best_move=move;
			}
		}


//		transpositionTableMax.add(game,game.goodHeuristic(true));
		return best_score;
	}

	// TODO : return double ( or int utility ) instead of move
	private int minValue(Game game,int depth)
	{


		if (game.isGameOver() || depth==0 )
		{
			System.out.println(" Min-depth :"+depth);
			System.out.println(" Min :"+game.goodHeuristic(false));
			return game.goodHeuristic(false);
		}
		// Make Backup to the game
		Game temp_game=game;

		List<Move> moves = getMoves(temp_game);
		int best_score=Integer.MAX_VALUE;
		int res_score;


		for (Move move:moves)
		{
			temp_game=temp_game.copy();
			temp_game.move(move.getStartIndex(),move.getEndIndex());

			if (temp_game.isP2Turn()==player)
			{
				res_score=maxValue(temp_game,depth-1);
			}
			else
			{
				res_score=minValue(temp_game,depth-1);
			}

			if (res_score < best_score)
			{
				best_score=res_score;
				best_move=move;
			}
		}

//		transpositionTableMin.add(game,game.goodHeuristic(false));
		return best_score;
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
