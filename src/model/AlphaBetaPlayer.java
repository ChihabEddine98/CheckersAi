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
			
		//TODO
	}
}
