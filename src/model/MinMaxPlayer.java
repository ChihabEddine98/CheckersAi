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

    public MinMaxPlayer(boolean joueur, int level) {
        this.player = joueur;
        this.transpositionTableMax = new StateSet();
        this.transpositionTableMin = new StateSet();
        this.level = level;
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
                depth = 6;
                break;
            case 2:
                depth = 9;
                break;
            default:
                depth = 3;
                break;
        }
        Move best_move = minimax(game, depth);
        game.move(best_move);
    }

    /**
     * @param game  : The actual state of Game Board
     * @param depth : The maximum depth to search in
     * @return The best {@model.Move move } move available for the current player ( With max value if it is Player Max and Min otherwise )
     */
    private Move minimax(Game game, int depth) {
        Game temp_game = game.copy();
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
            best_score = player ? maxValue(temp_game, depth) : minValue(temp_game, depth);

            // If we are handling a Max Player so we need to take the argmax !
            if (player && best_score > high_score) {
                high_score = best_score;
                best_move = move;
            }// If we are handling a Min Player so we need to take the argmin !
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
    private int maxValue(Game game, int depth) {
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

        // Get all available moves
        List<Move> moves = getMoves(temp_game);
        int best_score = Integer.MIN_VALUE;
        int res_score;

        for (Move move : moves) {
            temp_game = temp_game.copy();
            temp_game.move(move);
            // In case the player is about to make multiple moves ( if he can )
            res_score = temp_game.isP2Turn() == player ? maxValue(temp_game, depth - 1) :
                    minValue(temp_game, depth - 1);
            // Update our score
            best_score = Math.max(best_score, res_score);
        }
        // Make sure to add our new state in The Transposition table
        transpositionTableMax = new StateSet();
        transpositionTableMax.add(temp_game, best_score);

        return best_score;
    }

    /**
     * @param game  : The actual state of Game Board
     * @param depth : The maximum depth to search in
     * @return The min_score ( evaluation for the actual game if we go for depth = depth
     */
    private int minValue(Game game, int depth) {
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

            res_score = temp_game.isP2Turn() == player ? maxValue(temp_game, depth - 1) :
                    minValue(temp_game, depth - 1);

            best_score = Math.min(best_score, res_score);
        }

        transpositionTableMin = new StateSet();
        transpositionTableMin.add(temp_game, best_score);

        return best_score;
    }


    /**
     * Gets all the available moves and skips for the current player.
     *
     * @param game the current game state.
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
                copy.move(startIndex, endIndex);
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
                copy.move(index, endIndex);
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
                    copy.move(index, endIndex);
                    moves.add(new Move(index, endIndex, copy.goodHeuristic(!copy.isP2Turn())));
                }
            }
        }
        Collections.sort(moves);
        return moves;
    }
}
