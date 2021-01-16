/* Name: OptionPanel
 * Author: Devon McGrath
 * Description: This class is a user interface to interact with a checkers
 * game window.
 */

package src.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.model.AlphaBetaPlayer;
import src.model.ComputerPlayer;
import src.model.HumanPlayer;
import src.model.MinMaxPlayer;
import src.model.NetworkPlayer;
import src.model.Player;

/**
 * The {@code OptionPanel} class provides a user interface component to control
 * options for the game of checkers being played in the window.
 */
public class OptionPanel extends JPanel {

	private static final long serialVersionUID = -4763875452164030755L;

	/** The checkers window to update when an option is changed. */
	private CheckersWindow window;
	
	/** The button that when clicked, restarts the game. */
	private JButton restartBtn;
	
	/** The combo box that changes what type of player player 1 is. */
	private JComboBox<String> player1Opts;
	
	/** The button to perform an action based on the type of player. */
	private JButton player1Btn;

	/** The combo box that changes what type of player player 2 is. */
	private JComboBox<String> player2Opts;
	/** The combo box that changes what type of player player 2 is. */
	private JComboBox<String> gameDif;
	
	/** The button to perform an action based on the type of player. */
	private JButton player2Btn;
	/** The button to perform an action based on the type of player. */
	private JButton gameDifBtn;
	static int level = -1 ;
	
	/**
	 * Creates a new option panel for the specified checkers window.
	 * 
	 * @param window	the window with the game of checkers to update.
	 */
	public OptionPanel(CheckersWindow window) {
		super(new GridLayout(0, 1));
		
		this.window = window;
		
		// Initialize the components
		OptionListener ol = new OptionListener();
		final String[] playerTypeOpts = {"Human", "Computer", "MinMaxComputer", "AlphaBetaComputer"/*, "Network"*/};
		final String[] gameDifTypeOpts = {"Easy", "Medium", "Hard"};
		this.restartBtn = new JButton("Restart");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.gameDif = new JComboBox<>(gameDifTypeOpts);
		this.restartBtn.addActionListener(ol);
		this.player1Opts.addActionListener(ol);
		this.player2Opts.addActionListener(ol);
		this.gameDif.addActionListener(ol);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.player1Btn = new JButton("Set Connection");
		this.player1Btn.addActionListener(ol);
		this.player1Btn.setVisible(false);
		this.player2Btn = new JButton("Set Connection");
		this.player2Btn.addActionListener(ol);
		this.player2Btn.setVisible(false);
		this.gameDifBtn = new JButton("Set Connection");
		this.gameDifBtn.addActionListener(ol);
		this.gameDifBtn.setVisible(false);
		
		// Add components to the layout
		top.add(restartBtn);
		middle.add(new JLabel("(white) Player 1: "));
		middle.add(player2Opts);
		middle.add(player2Btn);
		bottom.add(new JLabel("(black) Player 2: "));
		bottom.add(player1Opts);
		bottom.add(player1Btn);

		middle.add(new JLabel("Difficulty Level :"));
		middle.add(gameDif);
		middle.add(gameDifBtn);

		this.add(top);
		this.add(middle);
		this.add(bottom);
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}
	
	

	/**
	 *
	 * @param playerOpts
	 * @param level
	 * @param player1
	 * @return a new instance of a {Player} object that corresponds
	 * 	 * with the type of player selected.
	 */
	private static Player getPlayer(JComboBox<String> playerOpts,int level, boolean player1) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine the type
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("Computer")) {
			player = new ComputerPlayer();
		} else {
			if(type.equals("MinMaxComputer"))
				if (level >= 0 )
				{
					player = new MinMaxPlayer(player1,level);
				}
				else player = new MinMaxPlayer(player1);
			else {
				if(type.equals("AlphaBetaComputer"))
					if (level >= 0 )
					{
						player = new AlphaBetaPlayer(player1);
					}
					else player = new AlphaBetaPlayer(player1);
			}
		}
		
		return player;
	}
	
	/**
	 * The {@code OptionListener} class responds to the components within the
	 * option panel when they are clicked/updated.
	 */
	private class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// No window to update
			if (window == null) {
				return;
			}
			
			Object src = e.getSource();

			// Handle the user action
			JButton btn = null;
			boolean isNetwork = false, isP1 = true;
			if (src == restartBtn) {
				window.restart();
				System.out.println(gameDif.getSelectedIndex());
				OptionPanel.level = gameDif.getSelectedIndex();
			} else if (src == player1Opts) {
				Player player = getPlayer(player1Opts,OptionPanel.level,true);
				window.setPlayer1(player);
				isNetwork = (player instanceof NetworkPlayer);
				btn = player1Btn;
			} else if (src == player2Opts) {
				int level = gameDif.getSelectedIndex();
				Player player = getPlayer(player2Opts,level,false);
				window.setPlayer2(player);
				isNetwork = (player instanceof NetworkPlayer);
				btn = player2Btn;
				isP1 = false;
			}
			else if (src == gameDif) {
				OptionPanel.level = gameDif.getSelectedIndex();
//				System.out.println("Yooooooo ");
//				System.out.println(gameDif.getSelectedIndex());
			}

		}
	}
}
