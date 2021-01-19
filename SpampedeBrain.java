package com.gradescope.spampede;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;

/**
 * SpampedeBrain - The "Controller" in MVC, which includes all of the AI and
 * handling key presses
 * 
 * @author CS60 instructors
 */
public class SpampedeBrain extends SpampedeBrainParent {

	/** The "View" in MVC */
	private SpampedeDisplay theDisplay; 
	
	/** The "Model" in MVC */
	private SpampedeData theData;
	
	/** Number of animated frames displayed so far */
	private int cycleNum = 0;

	// Mapping between direction (names) and keys
	private static final char REVERSE = 'r';
	private static final char UP      = 'i';
	private static final char DOWN    = 'k';
	private static final char LEFT    = 'j';
	private static final char RIGHT    = 'l';
	private static final char AI_MODE = 'a';
	private static final char PLAY_SPAM_NOISE = 's';


	/**
	 * Starts a new game.
	 */
	public void startNewGame() {
		this.theData = new SpampedeData();
		this.theData.placeSnakeAtStartLocation();
		this.theData.setStartDirection();
		
		this.theDisplay = new SpampedeDisplay(this.theData, this.screen,
				this.getSize().width, getSize().height);
		this.theDisplay.updateGraphics();
		
		this.playSound_spam();
		this.playSound_juicy();
		
		// Hack because pictures have a delay in loading, and we won't
		//    redraw the screen again until the game actually starts,
		//    which means we wouldn't see the image until the game
		//    does start.
		// Wait a fraction of a second (200 ms), by which time the
		//    picture should have been fetched from disk, and redraw.
		try { Thread.sleep(200); } catch (InterruptedException e) {};
		this.theDisplay.updateGraphics();
	}

	/**
	 * Declares the game over.
	 */
	public void gameOver() {
		super.pause(); // pause the game
		this.theData.setGameOver(); // tell the model that the game is over
		if (this.audioMeow != null) { // play a sound
			this.audioMeow.play();
		}
	}
	
	/* -------- */
	/* Gameplay */
	/* -------- */
	
	/**
	 * Moves the game forward one step (i.e., one frame of animation,
	 * which occurs every Preferences.SLEEP_TIME milliseconds)
	 */
	public void cycle() {

		// move the snake
		this.updateSnake();

		// update the list of Spam
		this.updateSpam();

		// draw the board
		this.theDisplay.updateGraphics();

		// make the new display visible - sends the drawing to the screen
		this.repaint();

		// update the cycle counter
		this.cycleNum++;
	}

    /** 
     * Reacts to characters typed by the user.
     * <p>
     * The provided code registers the SpampedeBrain as
     * an "observer" for key presses on the keyboard.
     * 
     * So, whenever the user presses a key,
     * Java automatically calls this keyPressed method and
     * passes it a KeyEvent describing the specific keypress.
     */
	public void keyPressed(KeyEvent evt) {
		
		switch (evt.getKeyChar()) {
		// The getKeyChar method of a keypress event
		//    returns the character corresponding to the pressed key.
		
		case UP:
			this.theData.setDirectionNorth();
			break;
		case DOWN:
			this.theData.setDirectionSouth();
			break;
		case RIGHT:
			this.theData.setDirectionEast();
			break;
		case LEFT:
			this.theData.setDirectionWest();
			break;
		case REVERSE:
			this.reverseSnake();
			break;
		case AI_MODE:
			this.theData.setMode_AI();
			break;
		case PLAY_SPAM_NOISE:
			this.playSound_spam();
			break;
		default:
			this.theData.setDirectionEast();
		}
	}


	/**
	 *  Moves the snake forward once every REFRESH_RATE cycles,
	 *  either in the current direction, or as directed by
	 *  the AI's breadth-first search.
	 *  <p>
	 *  Called by cycle(), test_eatSpam(), and test_noSpeamEaten()	
	 */
	void updateSnake() {
		if (this.cycleNum % Preferences.REFRESH_RATE == 0) {
			BoardCell nextCell;
			if (this.theData.inAImode()) {
				nextCell = this.getNextCellFromBFS();
			} else {
				nextCell = this.theData.getNextCellInDir();
			}
			this.advanceTheSnake(nextCell);
		}
	}

	/**
	 * Move the snake into the next cell (and possibly eat spam)
	 * <p>
	 * This method should probably be private, but we make it
	 * public anyway to permit unit testing.
	 * 
	 * @param nextCell  New location of the snake head (which
	 *                  must be horizontally or vertically adjacent
	 *                  to the old location of the snake head).
	 */
	public void advanceTheSnake(BoardCell nextCell) {
		// Note - do not modify provided code.
		if (nextCell.isWall() || nextCell.isBody()) {
			// Oops...we hit something.
			if(this.theData.lifeCount == 0) {
				this.gameOver();
			} else {
				this.resetGame();
			}
	
			
			return;
		} else if (nextCell.isSpam()) {
			this.playSound_spamEaten();
			this.theData.headHelperEatsSpam(nextCell);
			
		} else {
			// just regular movement into an open space
			this.theData.headHelperNoSpam(nextCell);
		}
		//helper functions are in SpampedeData.java (two helper functions)
	}
	
	/* This is a helper function we wrote to reset the Game */
	public void resetGame() {
		this.theData.placeSnakeAtStartLocation();
		this.theData.setStartDirection();
		
		this.theDisplay = new SpampedeDisplay(this.theData, this.screen,
				this.getSize().width, getSize().height);
		this.theDisplay.updateGraphics();
		
		//this.playSound_spam();
		
		// Hack because pictures have a delay in loading, and we won't
		//    redraw the screen again until the game actually starts,
		//    which means we wouldn't see the image until the game
		//    does start.
		// Wait a fraction of a second (200 ms), by which time the
		//    picture should have been fetched from disk, and redraw.
		try { Thread.sleep(200); } catch (InterruptedException e) {};
		this.theDisplay.updateGraphics();
		this.theData.lifeCount--;
	}


	/** 
	 * Every SPAM_ADD_RATE cycles, tries to add one new spam.
	 */
	void updateSpam() {
		if (this.theData.noSpam()) {
			this.theData.addSpam();
		} else if (this.cycleNum % Preferences.SPAM_ADD_RATE == 0) {
			this.theData.addSpam();
		}
	}


	/** 
	 * Uses BFS to search for the spam closest to the snake head.
	 * 
	 * @return Where to move the snake head, if we want to head
	 *         *one step* along the shortest path to (the nearest) 
	 *         spam cell.
	 */
	public BoardCell getNextCellFromBFS() {
		// Initialize the search.
		theData.resetCellsForNextSearch();
		
		// Initialize the cellsToSearch queue with the snake head;
		// as with any cell, we mark the head cells as having been added
		// to the queue
		Queue<BoardCell> cellsToSearch = new LinkedList<BoardCell>();
		BoardCell snakeHead = theData.getSnakeHead();
		snakeHead.setAddedToSearchList();
		cellsToSearch.add(snakeHead); //add appends to the end of the list
		
        // Variable to hold the closest spam cell, once we've found it.
		BoardCell closestSpamCell = null;
		// Search!
	
		//get the neighbors of a cell
		
		// search here!
		//while some condition
		while(!cellsToSearch.isEmpty()) {
			//pop off the front of the queue
			BoardCell currentCell = cellsToSearch.remove();
			if (currentCell.isSpam()) {
				return this.getFirstCellInPath(currentCell);
			}
			// now, it is not the spam so you
			// get the neighbors of current cell
			BoardCell[] newNeighbors = this.theData.getCellNeighbors(currentCell);
			
			// add the neighbors to the queue but first check stuff
			for(BoardCell neighbors: newNeighbors){ //for each loop
				//check if it is a wall, or like the body of the snake, or if its already added to the queue
				if (neighbors.isWall() || neighbors.isBody() || neighbors.inSearchListAlready() ) {
					
				}
				else {
				//if it was in the queue already: inSearchListAlready
				neighbors.setAddedToSearchList();
				cellsToSearch.add(neighbors);
				
				//set parents of neighbors to be your current
				neighbors.setParent(currentCell);
				}
				
			}
			
		}
		
		
		// Note: we encourage you to write the helper method 
		// getFirstCellInPath below to do the backtracking to calculate the next cell!

		// If the search fails, just move somewhere.
		return this.theData.getRandomNeighboringCell(snakeHead);
	}


	/**
	 * Follows parent pointers back from the closest spam cell
	 * to decide where the head should move. Specifically,
	 * follows the parent pointers back from the spam until we find
	 * the cell whose parent is the snake head (and which must therefore
	 * be adjacent to the previous snake head location).
	 * <p>
	 * Recursive or looping solutions are possible.
	 *  
	 * @param start   where to start following spam pointers; this will
	 *                 be (at least initially, if you use recursion) the
	 *                 location of the spam closest to the head.
	 * @return the new cell for the snake head.
	 */
	//if the neighbor is spam then use this function to decide where the head should move
	private BoardCell getFirstCellInPath(BoardCell start) {
		//while the parent of the pointer of the start is not equal to the head, keep setting parent of the pointer 
		BoardCell parentPointer = start;
		while(!parentPointer.getParent().isHead()){
			parentPointer = parentPointer.getParent();  //getParent built in gets us parent
		}
		return parentPointer; //at this point the parentPointer should be the snake head 
		                      //and the parent of that should be the head
	}


	/**
	 * Reverses the snake back-to-front and updates the movement 
	 * mode appropriately.
	 */
	public void reverseSnake() {
		this.theData.reverseSnakeHelp();
	}

	/* ------ */
	/* Sounds */
	/* ------ */
	
	/** Plays crunch noise */
	public void playSound_spamEaten() {
		if (this.audioCrunch != null) {
			this.audioCrunch.play();
		}
	}

	/** Plays spam noise */
	public void playSound_spam() {
		if (this.audioSpam != null) {
			this.audioSpam.play();
		}
	}

	/** Plays meow noise */
	public void playSound_meow() {
		if (this.audioMeow != null) {
			this.audioMeow.play();
		}
	}
	
	/** Plays juicy noise ;)*/
	public void playSound_juicy() {
		if (this.audioJuicy != null) {
			this.audioJuicy.play();
		}
	}
	

	// not used - a variable added to remove a Java warning:
	private static final long serialVersionUID = 1L;

	
	/* ---------------------- */
	/* Testing Infrastructure */
	/* ---------------------- */

	public static SpampedeBrain getTestGame(TestGame gameNum) {
		SpampedeBrain brain = new SpampedeBrain();
		brain.theData = new SpampedeData(gameNum);
		return brain;
	}
	
	public String testing_toStringParent() {
		return this.theData.toStringParents();
	}

	public BoardCell testing_getNextCellInDir() {
		return this.theData.getNextCellInDir();
	}

	public String testing_toStringSpampedeData() {
		return this.theData.toString();
	}
}
