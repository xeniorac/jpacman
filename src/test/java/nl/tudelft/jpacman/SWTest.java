package nl.tudelft.jpacman;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.npc.ghost.Navigation;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.ui.PacManUI;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CosNaming.IstringHelper;

import com.google.common.collect.Lists;

public class SWTest {

	private static Launcher testLauncher;

	@Before
	public void initPacman() {
		testLauncher = new Launcher();
		testLauncher.launch();
	}

	@After
	public void stopPacman() {
		testLauncher.dispose();
	}

	// Test for Scenario 1.1
	@Test
	public void scn11() {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game and check that the game should be in progress
		testGame.start();
		assertTrue(testGame.isInProgress());
		// level should be in in progress
		assertTrue(testGame.getLevel().isInProgress());
		// There should be at least one player alive
		assertTrue(testGame.getLevel().isAnyPlayerAlive());
	}

	// Test for Scenario 2.1
	@Test
	public void scn21() {
		// launch the game
		Game testGame = testLauncher.getGame();
		// start the game
		testGame.start();
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// get square to the west of the player and check if there is Pellet
		Square testSqr = testPlayer.getSquare().getSquareAt(Direction.WEST);
		// get occupants of the square
		List<Unit> testOccupants = testSqr.getOccupants();
		// there should be a pellet in the test square
		assertEquals(Pellet.class, testOccupants.get(0).getClass());
		// move the player to west, test square where there is pellet
		testGame.move(testPlayer, Direction.WEST);
		// player's points should be 10
		assertEquals(10, testPlayer.getScore());
		// move the player back to starting position
		testGame.move(testPlayer, Direction.EAST);
		// get occupants of the square
		testOccupants = testSqr.getOccupants();
		// the occupants in the square should be zero indicating that pellet has
		// disappeared
		assertEquals(0, testOccupants.size());
	}

	// Test for Scenario 2.2
	@Test
	public void scn22() {
		// launch the game
		Game testGame = testLauncher.getGame();
		// start the game
		testGame.start();
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// move the player to a square having pellet and score should be 10
		testGame.move(testPlayer, Direction.WEST);
		assertEquals(10, testPlayer.getScore());
		// move back to original position which is empty and score should not
		// increase
		testGame.move(testPlayer, Direction.EAST);
		assertEquals(10, testPlayer.getScore());
		// move again to west, where pellet is no longer present and score
		// should not increase
		testGame.move(testPlayer, Direction.WEST);
		assertEquals(10, testPlayer.getScore());
	}

	// Test for Scenario 2.3
	@Test
	public void scn23() {
		// launch the game
		Game testGame = testLauncher.getGame();
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// get the board
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(10, 15);
		// set the ghost to the west of player
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		testGhost.occupy(testBoard.squareAt(10, 15));
		// start the game
		testGame.start();
		// initially player is alive
		assertTrue(testPlayer.isAlive());
		// move the player to the square where ghost is located
		testGame.move(testPlayer, Direction.WEST);
		// now player should die
		assertFalse(testPlayer.isAlive());
		// game should have ended
		assertFalse(testGame.isInProgress());
	}

	// Test for Scenario 2.4
	@Test
	public void scn24() {
		// launch the game
		Game testGame = testLauncher.getGame();
		// start the game
		testGame.start();
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// get initial position of the player
		Square initSqr = testPlayer.getSquare();
		// per the initial board, Player has wall to south, So move player to
		// south
		testGame.move(testPlayer, Direction.SOUTH);
		// Player's current square should be same as previous square
		assertEquals(initSqr, testPlayer.getSquare());
	}

	// Test for scenario 2.5
	@Test
	public void scn25() {
		// create a custom board with only one pellet to the east of player
		PacManSprites spriteStr = new PacManSprites();
		List<String> custBoard = Lists.newArrayList("#######", "#P.  G#", "#######");
		MapParser parser = new MapParser(new LevelFactory(spriteStr, new GhostFactory(spriteStr)),
				new BoardFactory(spriteStr));
		GameFactory testGF = new GameFactory(new PlayerFactory(spriteStr));
		Level testLevel = parser.parseMap(custBoard);
		Game testGame = testGF.createSinglePlayerGame(testLevel);
		// start the game
		testGame.start();
		//initially only one pellet in the game
		assertEquals(1, testLevel.remainingPellets());
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// game should be in progress
		assertTrue(testGame.isInProgress());
		// move the player to the only pellet on board
		testGame.move(testPlayer, Direction.EAST);
		//remaining pellets in the game should be zero
		assertEquals(0, testLevel.remainingPellets());
		// game should have ended
		assertFalse(testGame.isInProgress());
		// player should be alive
		assertTrue(testPlayer.isAlive());
	}

	// Test for scenario 3.1
	@Test
	public void scn31() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game
		testGame.start();
		// get the ghost
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(10, 15);
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		Square ghstSqr = testGhost.getSquare();
		// sleep for tick event interval
		Thread.sleep(testGhost.getInterval());
		// Ghost should move to empty square i.e. not matching its previous
		// square
		assertFalse(ghstSqr == testGhost.getSquare());
	}

	// Test for scenario 3.2
	@Test
	public void scn32() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game
		testGame.start();
		// get the ghost
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(1, 1);
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		Square ghstSqr = testGhost.getSquare();
		// place the ghost in area with pellet in all directions
		testGhost.occupy(testSqr);
		// sleep for tick event interval
		Thread.sleep(testGhost.getInterval());
		// Now ghost should be on square with pellet and pellet should not
		// visible
		Square currSqr = testGhost.getSquare();
		List<Unit> occpnts = currSqr.getOccupants();
		assertEquals(testGhost, occpnts.get(occpnts.size() - 1));
		assertEquals(Pellet.class, occpnts.get(occpnts.size() - 2).getClass());
	}

	// Test for scenario 3.3
	@Test
	public void scn33() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game
		testGame.start();
		// get the ghost
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(1, 1);
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		// place the ghost in area with pellet in all directions
		testGhost.occupy(testSqr);
		// sleep for tick event interval
		Thread.sleep(testGhost.getInterval());
		// Now ghost should be on square with pellet and pellet should not be visible
		Square currSqr = testGhost.getSquare();
		List<Unit> occpnts = currSqr.getOccupants();
		assertEquals(testGhost, occpnts.get(occpnts.size() - 1));
		assertEquals(Pellet.class, occpnts.get(occpnts.size() - 2).getClass());
		
		//sleep for tick event interval
		Thread.sleep(testGhost.getInterval());
		
		//now check Ghost has moved away from previous square and pellet is visible
		occpnts = currSqr.getOccupants();
		assertFalse(currSqr == testGhost.getSquare());
		assertEquals(Pellet.class, occpnts.get(occpnts.size()- 1).getClass());
	}
	
	//Test for scenario 3.4
	@Test
	public void scn34() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// get the player
		Player testPlayer = testGame.getPlayers().get(0);
		// get the board
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(10, 15);
		// set the ghost to the west of player
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		testGhost.occupy(testBoard.squareAt(10, 15));
		// start the game
		testGame.start();
		// initially player is alive
		assertTrue(testPlayer.isAlive());
		// sleep for tick event interval 
		Thread.sleep(4*testGhost.getInterval());
		// now player should die
		assertFalse(testPlayer.isAlive());
		// game should have ended
		assertFalse(testGame.isInProgress());
	}

	// Test for scenario 4.1
	@Test
	public void scn41() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game and check that the game should be in progress
		testGame.start();
		assertTrue(testGame.isInProgress());
		// stop the game to suspend it
		testGame.stop();
		// game should not be in progress
		assertFalse(testGame.isInProgress());
		// get the positions of player and ghost
		Player testPlayer = testGame.getPlayers().get(0);
		Square plyrSqr = testPlayer.getSquare();
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(10, 15);
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		Square ghstSqr = testGhost.getSquare();
		// let some ticks elapse
		Thread.sleep(1000);
		// both player and ghost should be at previous square
		assertEquals(ghstSqr, testGhost.getSquare());
		assertEquals(plyrSqr, testPlayer.getSquare());
	}

	// Test for scenario 4.2
	@Test
	public void scn42() throws InterruptedException {
		// launch the game
		Game testGame = testLauncher.getGame();
		// Initially the game should not be in progress
		assertFalse(testGame.isInProgress());
		// Start the game and check that the game should be in progress
		testGame.start();
		assertTrue(testGame.isInProgress());
		// stop the game to suspend it
		testGame.stop();
		// game should not be in progress
		assertFalse(testGame.isInProgress());
		// get the position of ghost
		Board testBoard = testGame.getLevel().getBoard();
		Square testSqr = testBoard.squareAt(10, 15);
		Ghost testGhost = (Ghost) Navigation.findNearest(Ghost.class, testSqr);
		Square ghstSqr = testGhost.getSquare();
		// Resume the game
		testGame.start();
		// Game should be in progress
		assertTrue(testGame.isInProgress());
		// Let few ticks elapse
		Thread.sleep(1000);
		// Ghost should have moved from its previous square indicating the game
		// has resumed
		assertTrue(ghstSqr != testGhost.getSquare());
	}

}
