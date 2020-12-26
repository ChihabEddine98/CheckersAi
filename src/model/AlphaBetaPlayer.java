/* Name: ComputerPlayer
 * Author: Devon McGrath
 * Description: This class represents a computer player which can update the
 * game state without user interaction.
 */

package src.model;

import java.util.List;

/**
 * The {@code ComputerPlayer} class represents a computer player and updates
 * the board based on a model.
 */
public class AlphaBetaPlayer extends MinMaxPlayer {

	//public boolean player;
	
	public AlphaBetaPlayer(boolean joueur) {
		super(joueur);
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
			
		Move best_move=minimax_alpha_beta(game,20);
		game.move(best_move);
	}
	private Move minimax_alpha_beta(Game game,int depth)
	{
		Game temp_game = game.copy();
		List<Move> moves=getMoves(temp_game);
		int best_score;
		int high_score=Integer.MIN_VALUE;
		int low_score=Integer.MAX_VALUE;
		Move best_move=null;

		this.transpositionTableMax = new StateSet();
		this.transpositionTableMin = new StateSet();

		for (Move move : moves)
		{
			best_score = player ? maxValue(temp_game,depth,high_score,low_score) :
					              minValue(temp_game,depth,high_score,low_score);

			if (player && best_score > high_score){
				high_score=best_score;
				best_move=move;
			}
			else if (!player && best_score < low_score)
			{
				low_score=best_score;
				best_move=move;
			}
		}
		return best_move;
	}


	private int maxValue(Game game,int depth,int alpha,int beta)
	{
		if (game.isGameOver() || depth==0 || transpositionTableMax.getValue(game)!=null)
		{
			if (transpositionTableMax.getValue(game)!= null){
				System.out.println(" Max :"+transpositionTableMax.getValue(game));
				return transpositionTableMax.getValue(game);
			}
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
			temp_game.move(move);

			res_score= temp_game.isP2Turn()==player ? maxValue(temp_game,depth-1, alpha, beta) :
					minValue(temp_game,depth-1, alpha, beta);

			best_score=Math.max(best_score,res_score);
			if(best_score>= beta) return best_score;
			alpha= Math.max(alpha,best_score);
		}

		transpositionTableMax=new StateSet();
		transpositionTableMax.add(temp_game,best_score);

		return best_score;
	}

	private int minValue(Game game,int depth,int alpha,int beta)
	{
		if (game.isGameOver() || depth==0 || transpositionTableMin.getValue(game)!= null)
		{
			if (transpositionTableMin.getValue(game)!= null){
				System.out.println(" Min :"+transpositionTableMin.getValue(game));
				return transpositionTableMin.getValue(game);
			}
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
			temp_game.move(move);

			res_score= temp_game.isP2Turn()== player ? maxValue(temp_game,depth-1, alpha, beta) :
					minValue(temp_game,depth-1, alpha, beta);

			best_score=Math.min(best_score,res_score);
			if ( best_score <= alpha) return best_score;
			beta=Math.min(beta,best_score);
		}

		transpositionTableMin=new StateSet();
		transpositionTableMin.add(temp_game,best_score);

		return best_score;
	}


}
