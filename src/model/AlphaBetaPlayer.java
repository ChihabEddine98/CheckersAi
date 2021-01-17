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

    public AlphaBetaPlayer(boolean joueur, int level) {
        super(joueur, level);
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
        /**
         * This variable {@depth} is changed whenever we change selection on the JComboBox for difficulty Level
         */
        int depth;
        switch (level) {
            case 1:
                depth = 9;
                break;
            case 2:
                depth = 12;
                break;
            default:
                depth = 3;
                break;
        }
        Move best_move = minimax_alpha_beta(game, depth);
        game.move(best_move);
    }

    /**
     * @param game  : The actual state of Game Board
     * @param depth : The maximum depth to search in
     * @return The best {@model.Move} for actual player standing on actual depth search
     */

    private Move minimax_alpha_beta(Game game, int depth) {
        // Backup our state
        Game temp_game = game.copy();
        // Get all available Moves
        List<Move> moves = getMoves(temp_game);

        int best_score;
        int high_score = Integer.MIN_VALUE;
        int low_score = Integer.MAX_VALUE;
        Move best_move = null;

        this.transpositionTableMax = new StateSet();
        this.transpositionTableMin = new StateSet();

        /**
         * Here we need to get the best available {@model.Move} based on scores
         */
        for (Move move : moves) {
            // We test whatever we are with max or min player and get the corresponding evaluation
            best_score = player ? maxValue(temp_game, depth, high_score, low_score) :
                    minValue(temp_game, depth, high_score, low_score);

            // If we are handling a Max Player so we need to take the argmax !
            if (player && best_score > high_score) {
                high_score = best_score;
                best_move = move;
            } // If we are handling a Min Player so we need to take the argmin !
            else if (!player && best_score < low_score) {
                low_score = best_score;
                best_move = move;
            }
        }
        return best_move;
    }


    /**
     * @param game  : The actual state of Game Board
     * @param depth : The maximum depth to search in
     * @return The max_score ( evaluation for the actual game if we go for depth = depth
     */
    private int maxValue(Game game, int depth, int alpha, int beta) {
        // If We are in the last level of depth or the game is in it end
        // Or If our actual state has been seen before no need to go further !
        if (game.isGameOver() || depth == 0 || transpositionTableMax.getValue(game) != null) {
            if (transpositionTableMax.getValue(game) != null) {
                return transpositionTableMax.getValue(game);
            }
            return game.goodHeuristic(true);
        }
        // Make Backup for Game instance
        Game temp_game = game;

        List<Move> moves = getMoves(temp_game);
        int best_score = Integer.MIN_VALUE;
        int res_score;

        for (Move move : moves) {
            temp_game = temp_game.copy();
            temp_game.move(move);
            // In case the player is about to make multiple moves ( if he can )
            res_score = temp_game.isP2Turn() == player ? maxValue(temp_game, depth - 1, alpha, beta) :
                    minValue(temp_game, depth - 1, alpha, beta);

            best_score = Math.max(best_score, res_score);
            if (best_score >= beta) return best_score;
            alpha = Math.max(alpha, best_score);
        }
        // Make sure to add our new state in The Transposition table
        transpositionTableMax = new StateSet();
        transpositionTableMax.add(temp_game, best_score);

        return best_score;
    }

    /**
     * @param game  : The actual state of Game Board
     * @param depth : The maximum depth to search in
     * @return The max_score ( evaluation for the actual game if we go for depth = depth
     */
    private int minValue(Game game, int depth, int alpha, int beta) {
        if (game.isGameOver() || depth == 0 || transpositionTableMin.getValue(game) != null) {
            if (transpositionTableMin.getValue(game) != null) {
                return transpositionTableMin.getValue(game);
            }
            return game.goodHeuristic(false);
        }
        // Make Backup to the game
        Game temp_game = game;

        List<Move> moves = getMoves(temp_game);
        int best_score = Integer.MAX_VALUE;
        int res_score;


        for (Move move : moves) {
            temp_game = temp_game.copy();
            temp_game.move(move);

            res_score = temp_game.isP2Turn() == player ? maxValue(temp_game, depth - 1, alpha, beta) :
                    minValue(temp_game, depth - 1, alpha, beta);

            best_score = Math.min(best_score, res_score);
            if (best_score <= alpha) return best_score;
            beta = Math.min(beta, best_score);
        }

        transpositionTableMin = new StateSet();
        transpositionTableMin.add(temp_game, best_score);

        return best_score;
    }


}
