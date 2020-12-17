/* Name: Move
 * Author: Devon McGrath
 * Description: This class represents a move.
 */

package src.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import src.logic.MoveGenerator;

/**
 * The {@code Move} class represents a move and contains a weight associated
 * with the move.
 */
public class Move implements Comparable<Move>{
	
	/** The weight corresponding to an invalid move. */
	public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;

	/** The start index of the move. */
	private byte startIndex;
	
	/** The end index of the move. */
	private byte endIndex;
	
	/** The weight associated with the move. */
	private double weight;
	
	/** The value of a move*/
	private int value;
	
	public Move(int startIndex, int endIndex, int value) {
		setStartIndex(startIndex);
		setEndIndex(endIndex);
		this.value = value;
	}
	
	public Move(Point start, Point end, int value) {
		setStartIndex(Board.toIndex(start));
		setEndIndex(Board.toIndex(end));
		this.value = value;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = (byte) startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this.endIndex = (byte) endIndex;
	}
	
	public Point getStart() {
		return Board.toPoint(startIndex);
	}
	
	public void setStart(Point start) {
		setStartIndex(Board.toIndex(start));
	}
	
	public Point getEnd() {
		return Board.toPoint(endIndex);
	}
	
	public void setEnd(Point end) {
		setEndIndex(Board.toIndex(end));
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void changeWeight(double delta) {
		this.weight += delta;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[startIndex=" + startIndex + ", "
				+ "endIndex=" + endIndex + ", weight=" + weight + ", value=" + this.value +  "]";
	}
	
	public int compareTo(Move m) {
		return m.value - this.value; 
	}
	
	protected static int getMovesStats(Game game, boolean player, boolean skipsOnly) {
		
		// Get the checkers
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isP2Turn() == player) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		int stat = 0;
		if(skipsOnly) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> skips = MoveGenerator.getSkips(b, index);
				stat += skips.size();
			}
		}
		else {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				stat += movesEnds.size();
			}
		}
		return stat;
	}
}
