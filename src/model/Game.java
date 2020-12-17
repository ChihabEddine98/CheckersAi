/* Name: Game
 * Author: Devon McGrath
 * Description: This class represents a game of checkers. It provides a method
 * to update the game state and keep track of who's turn it is.
 */

package src.model;

import java.awt.Point;
import java.util.List;

import src.logic.MoveGenerator;
import src.logic.MoveLogic;

/**
 * The {@code Game} class represents a game of checkers and ensures that all
 * moves made are valid as per the rules of checkers.
 */
public class Game {
	
	public static int piece_val = 30;
	public static int king_val = 80;
	public static int move_val = 2;
	public static int jumps = 6;
	public static int piece_row_advance = 1;
	public static int piece_middle_center_squares = 4;
	public static int piece_middle_side_squares = -2;
	public static int piece_center_goalies = 10;
	public static int piece_side_goalies = 8;
	public static int piece_double_corner = 4;
	public static int is_home_free = 15;
	public static int dist_factor = 5;

	/** The current state of the checker board. */
	private Board board;
	
	/** The flag indicating if it is player 1's turn. */
	private boolean isP2Turn;
	
	/** The index of the last skip, to allow for multiple skips in a turn. */
	private int skipIndex;
	
	public Game() {
		restart();
	}
	
	public Game(String state) {
		setGameState(state);
	}
	
	public Game(Board board, boolean isP1Turn, int skipIndex) {
		this.board = (board == null)? new Board() : board;
		this.isP2Turn = isP1Turn;
		this.skipIndex = skipIndex;
	}
	
	/**
	 * Creates a copy of this game such that any modifications made to one are
	 * not made to the other.
	 * 
	 * @return an exact copy of this game.
	 */
	public Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isP2Turn = isP2Turn;
		g.skipIndex = skipIndex;
		return g;
	}
	
	/**
	 * Resets the game of checkers to the initial state.
	 */
	public void restart() {
		this.board = new Board();
		this.isP2Turn = false;
		this.skipIndex = -1;
	}
	
	/**
	 * Attempts to make a move from the start point to the end point.
	 * 
	 * @param start	the start point for the move.
	 * @param end	the end point for the move.
	 * @return true if and only if an update was made to the game state.
	 * @see {@link #move(int, int)}
	 */
	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}
	
	/**
	 * Attempts to make a move given the start and end index of the move.
	 * 
	 * @param startIndex	the start index of the move.
	 * @param endIndex		the end index of the move.
	 * @return true if and only if an update was made to the game state.
	 * @see {@link #move(Point, Point)}
	 */
	public boolean move(int startIndex, int endIndex) {
		
		// Validate the move
		if (!MoveLogic.isValidMove(this, startIndex, endIndex)) {
			return false;
		}
		
		// Make the move
		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		
		// Make the checker a king if necessary
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == 0 && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == 7 && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		
		// Check if the turn should switch (i.e. no more skips)
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipIndex = endIndex;
		}
		if (!midValid || MoveGenerator.getSkips(
				board.copy(), endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isP2Turn = !isP2Turn;
			this.skipIndex = -1;
		}
		
		return true;
	}
	
	/**
	 * Gets a copy of the current board state.
	 * 
	 * @return a non-reference to the current game board state.
	 */
	public Board getBoard() {
		return board.copy();
	}
	
	/**
	 * Determines if the game is over. The game is over if one or both players
	 * cannot make a single move during their turn.
	 * 
	 * @return true if the game is over.
	 */
	public boolean isGameOver() {

		// Ensure there is at least one of each checker
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));
		if (black.isEmpty()) {
			return true;
		}
		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		if (white.isEmpty()) {
			return true;
		}
		
		// Check that the current player can move
		List<Point> test = isP2Turn? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!MoveGenerator.getMoves(board, i).isEmpty() ||
					!MoveGenerator.getSkips(board, i).isEmpty()) {
				return false;
			}
		}
		
		// No moves
		return true;
	}
	
	public int baciscHeuristic(boolean player) {
		int value = 0;
		
		if(player) {
			value += 2 * board.find(Board.BLACK_CHECKER).size();
			value += 3 * board.find(Board.BLACK_KING).size();
			value -= 2 * board.find(Board.WHITE_CHECKER).size();
			value -= 3 * board.find(Board.WHITE_KING).size();
		}
		else {
			value -= 2 * board.find(Board.BLACK_CHECKER).size();
			value -= 3 * board.find(Board.BLACK_KING).size();
			value += 2 * board.find(Board.WHITE_CHECKER).size();
			value += 3 * board.find(Board.WHITE_KING).size();
		}
		return value;
	}
	
	public int goodHeuristic(boolean player) {
		int score = 0;
		  int min_material, max_material;
		  int max_red_row = -1;
		  int min_black_row = 8;
		  
		  /*
		    Bonus for each non-king piece proportional to how close it is to
		    opponents home row (closer is better).  Similar penalty for
		    opponent's pieces that are close to our home row.
		    */
		  List<Point> checkers1 = board.find(Board.BLACK_CHECKER);
		  int black_pieces = checkers1.size();
		  for(int i = 0; i < checkers1.size(); i++) {
			  int tscore = 0;
			  Point point = checkers1.get(i);
			  int index = Board.toIndex(point);
			  if (index == 13 || index == 14 || index == 17 || index == 18)
				  tscore += piece_middle_center_squares;
				else if (index == 12 || index == 16 || index == 15 || index == 19)
			          tscore += piece_middle_side_squares;
				else if (index == 0 || index == 3)
				    tscore += piece_side_goalies;
				  else if (index == 1 || index == 2)
				    tscore += piece_center_goalies;
				  if (index == 0 || index == 4)
				    tscore += piece_double_corner;
				  tscore += point.y * piece_row_advance;
				  if (point.y < min_black_row)
				      min_black_row = point.y;
				  if(player)
					  score += tscore;
				  else
					  score -= tscore;
		  }
		  List<Point> kings1 = board.find(Board.BLACK_KING);
		  int black_kings = kings1.size();
		  for(int i = 0; i < kings1.size(); i++) {
			  Point point = kings1.get(i);
			  if (point.y < min_black_row)
				      min_black_row = point.y;
		  }
		  List<Point> checkers2 = board.find(Board.WHITE_CHECKER);
		  int red_pieces = checkers2.size();
		  for(int i = 0; i < checkers2.size(); i++) {
			  int tscore = 0;
			  Point point = checkers2.get(i);
			  int index = Board.toIndex(point);
			  if (index == 13 || index == 14 || index == 17 || index == 18)
				  tscore += piece_middle_center_squares;
				else if (index == 12 || index == 16 || index == 15 || index == 19)
			          tscore += piece_middle_side_squares;
				else if (index == 28 || index == 31)
				    tscore += piece_side_goalies;
				  else if (index == 29 || index == 30)
				    tscore += piece_center_goalies;
				  if (index == 27 || index == 31)
				    tscore += piece_double_corner;
				  tscore += (7 - point.y) * piece_row_advance;
				  if (point.y > max_red_row)
				      max_red_row = point.y;
				  if(player)
					  score -= tscore;
				  else
					  score += tscore;
				  
		  }
		  List<Point> kings2 = board.find(Board.WHITE_KING);
		  int red_kings = kings2.size();
		  for(int i = 0; i < kings2.size(); i++) {
			  Point point = kings2.get(i);
				  if (point.y > max_red_row)
				      max_red_row = point.y;
		  }
		  for(int i = 0; i < checkers1.size(); i++) {
			  Point point = checkers1.get(i);
			  if (point.y >= max_red_row) { 
				  if(player)
					  score += is_home_free;
				  else
					  score -= is_home_free;
			  }
		  }
		  for(int i = 0; i < checkers2.size(); i++) {
			  Point point = checkers2.get(i);
			  if (point.y <= min_black_row) {
				  if(player)
					  score -= is_home_free;
				  else
					  score += is_home_free;
			  }
		  }
		  
		  int black_material = black_pieces * piece_val + black_kings * king_val, red_material = red_pieces * piece_val + red_kings * king_val;

		  if (red_material > black_material) {
		    max_material = red_material;
		    min_material = black_material;
		  } else {
		    max_material = black_material;
		    min_material = red_material;
		  }
		  if (min_material == 0)
		    min_material = 1;

		  if(player)
			  score += (int) ((black_material - red_material) * (((float) max_material) / min_material));
		  else
			  score += (int) ((red_material - black_material) * (((float) max_material) / min_material));
		  
		  /*
		    Bonus for each possible legal move, penalty for each of opponent's
		    possible legal moves.
		    */
		  
		  int moves = Move.getMovesStats(this, true, true);
		  if(moves == 0) 
			  score += Move.getMovesStats(this, true, false) * move_val;
		  else
			  score += moves * jumps;

		  moves = Move.getMovesStats(this, false, true);
		  if(moves == 0) 
			  score -= Move.getMovesStats(this, false, false) * move_val;
		  else
			  score -= moves * jumps;
		  
		  /* bonus for each man who has a free shot at becoming king, penalty
		     for each opponent who has a free path.
		     */
		  
		  int outnumber = player? black_kings - red_kings: red_kings - black_kings;
		  boolean end = player? red_pieces < 5: black_pieces < 5;

		  if (outnumber > 0 && end) {
			  float dist = 0;
			  if(player) {
				  for(int i = 0; i < checkers2.size(); i++) {
					  Point point = checkers2.get(i);
					  for(int j = 0; j < kings1.size(); j++) {
						  Point point2 = kings1.get(j);
						  dist += point.distance(point2);
					  }
				  }
			  }
			  else {
				  for(int i = 0; i < checkers1.size(); i++) {
					  Point point = checkers1.get(i);
					  for(int j = 0; j < kings2.size(); j++) {
						  Point point2 = kings2.get(j);
						  dist += point.distance(point2);
					  }
				  }
			  }
			  score -= ((int) dist) * dist_factor;
		  }
		  return score;
	}
	
	public boolean isP2Turn() {
		return isP2Turn;
	}
	
	public void setP1Turn(boolean isP1Turn) {
		this.isP2Turn = isP1Turn;
	}
	
	public int getSkipIndex() {
		return skipIndex;
	}
	
	/**
	 * Gets the current game state as a string of data that can be parsed by
	 * {@link #setGameState(String)}.
	 * 
	 * @return a string representing the current game state.
	 * @see {@link #setGameState(String)}
	 */
	public String getGameState() {
		
		// Add the game board
		String state = "";
		for (int i = 0; i < 32; i ++) {
			state += "" + board.get(i);
		}
		
		// Add the other info
		state += (isP2Turn? "1" : "0");
		state += skipIndex;
		
		return state;
	}
	
	/**
	 * Parses a string representing a game state that was generated from
	 * {@link #getGameState()}.
	 * 
	 * @param state	the game state.
	 * @see {@link #getGameState()}
	 */
	public void setGameState(String state) {
		
		restart();
		
		// Trivial cases
		if (state == null || state.isEmpty()) {
			return;
		}
		
		// Update the board
		int n = state.length();
		for (int i = 0; i < 32 && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {}
		}
		
		// Update the other info
		if (n > 32) {
			this.isP2Turn = (state.charAt(32) == '1');
		}
		if (n > 33) {
			try {
				this.skipIndex = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipIndex = -1;
			}
		}
	}
	
	public String toString() {
		return (this.isP2Turn?"1":"0") + this.board.toString();
	}
}
