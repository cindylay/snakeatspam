package com.gradescope.spampede;

import java.awt.Color;
import java.awt.Font;

/**
 * Preferences - to avoid "magic numbers" within other code all unchanging
 * variables should be stored here!
 * 
 * @author CS60 instructors
 */
public final class Preferences {
	// Timing:
	public static final int REFRESH_RATE = 1;
	public static final int SPAM_ADD_RATE = 50;
	public static final int SLEEP_TIME = 30; // milliseconds between updates
	// Sizing:
	public static final int NUM_CELLS_WIDE = 50;
	public static final int NUM_CELLS_TALL = 30;
	public static final int CELL_SIZE = 10;
	private static final int SPACE_FOR_BUTTONS = 190;
	public static final int GAMEBOARDHEIGHT = NUM_CELLS_TALL * CELL_SIZE
			+ SPACE_FOR_BUTTONS;
	// Colors:
	public static final Color COLOR_BACKGROUND = Color.PINK;
	public static final Color COLOR_WALL = Color.BLUE;
	public static final Color COLOR_SPAM = Color.ORANGE;
	public static final Color COLOR_OPEN = Color.WHITE;
	public static final Color COLOR_HEAD = Color.BLACK;
	public static final Color COLOR_BODY = Color.GREEN;
	// Text display - Title:
	public static final int TITLE_X = 60;
	public static final int TITLE_Y = 30;
	public static final Font TITLE_FONT = new Font("Rockwell", Font.PLAIN, 30);	
	public static final Color TITLE_COLOR = Color.BLUE;
	public static final String TITLE = "JUICYpede";
	// Text display - Game Over
	public static final int GAME_OVER_X = 150;
	public static final int GAME_OVER_Y = 200;
	public static final Font GAME_OVER_FONT = new Font("Helvetica", Font.PLAIN, 60);	
	public static final Color GAME_OVER_COLOR = Color.BLUE;
	public static final String GAME_OVER_TEXT = "Game Over";
	//Spam counter
	public static final int SPAM_COUNTER_X = 420;
	public static final int SPAM_COUNTER_Y = 15;
	public static final Font SPAM_COUNTER_FONT = new Font("Rockwell", Font.PLAIN, 18);	
	public static final Color SPAM_COUNTER_COLOR = Color.BLUE;
	public static final String SPAM_COUNTER_TEXT = "SPAM Eaten:";
	//Life counter
	public static final int LIFE_COUNTER_X = 420;
	public static final int LIFE_COUNTER_Y = 30;
	public static final Font LIFE_COUNTER_FONT = new Font("Rockwell", Font.PLAIN, 18);	
	public static final Color LIFE_COUNTER_COLOR = Color.BLUE;
	public static final String LIFE_COUNTER_TEXT = "Lives Left:";
	//juicy Title
	public static final int JUICY_X = 10;
	public static final int JUICY_Y = 50;
	public static final Font JUICY_FONT = new Font("Rockwell", Font.PLAIN, 17);	
	public static final Color JUICY_COLOR = Color.BLACK;
	public static final String JUICY_TEXT = "If you can it see from the head(front) wait till you see it from the tail(back)";
}
