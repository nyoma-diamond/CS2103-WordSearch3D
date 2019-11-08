import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * Code to test <tt>WordSearch3D</tt>.
 */
public class WordSearchTester {
	private WordSearch3D _wordSearch;

	@Test
	/**
	 * Verifies that make can generate a very simple puzzle that is effectively 1d.
	 */
	public void testMake1D() {
		final String[] words = new String[] {
			"java"
		};
		// Solution is either java or avaj
		final char[][][] grid = _wordSearch.make(words, 1, 1, 4);
		final char[] row = grid[0][0];
		assertTrue(Arrays.deepToString(grid), (row[0] == 'j' && row[1] == 'a' && row[2] == 'v' && row[3] == 'a') ||
			(row[3] == 'j' && row[2] == 'a' && row[1] == 'v' && row[0] == 'a'));
	}

	@Test
	/**
	 * Verifies that make returns null when it's impossible to construct a puzzle.
	 */
	public void testMakeImpossible() {
		char[][][] grid = _wordSearch.make(new String[]{"test"}, 1, 1, 1);
		String msg = Arrays.deepToString(grid);
		assertNull(msg, grid); //grid should be too small to insert "test"
		
		grid = _wordSearch.make(new String[]{"foo","bar","lin","tut"}, 1, 3, 3);
		msg = Arrays.deepToString(grid);
		assertNull(msg, grid); //there should be no way to fit the provided words into a 1x3x3 puzzle
	}

	@Test
	/**
	 *  Verifies that search works correctly in a tiny grid that is effectively 2D.
	 */
	public void testSearchSimple() {
		// Note: this grid is 1x2x2 in size
		final char[][][] grid = new char[][][] {{{'a','b','c'}, {'d', 'f','e'}}};
		final int[][] location = _wordSearch.search(grid, "be");
		assertNotNull(location);
		final String msg = Arrays.deepToString(location); //String of locations

		assertEquals(msg, 0, location[0][0]);
		assertEquals(msg, 0, location[0][1]);
		assertEquals(msg, 1, location[0][2]);
		assertEquals(msg, 0, location[1][0]);
		assertEquals(msg, 1, location[1][1]);
		assertEquals(msg, 2, location[1][2]);
	}

	@Test
	/**
	 * Verifies that make can generate a grid when it's *necessary* for words to share
	 * some common letter locations.
	 */
	public void testMakeWithIntersection() {
		final String[] words = new String[] {
			"amc",
			"dmf",
			"gmi",
			"jml",
			"nmo",
			"pmr",
			"smu",
			"vmx",
			"yma",
			"zmq"
		};
		final char[][][] grid = _wordSearch.make(words, 3, 3, 3);
		assertNotNull(grid);
	}

	@Test
	/**
	 * Verifies that make returns a grid of the appropriate size.
	 */
	public void testMakeGridSize() {
		final String[] words = new String[] {
			"at",
			"it",
			"ix",
			"ax"
		};
		final char[][][] grid = _wordSearch.make(words, 17, 11, 13);
		assertEquals(grid.length, 17);
		for (int x = 0; x < 2; x++) {
			assertEquals(grid[x].length, 11);
			for (int y = 0; y < 2; y++) {
				assertEquals(grid[x][y].length, 13);
			}
		}
	}


	@Test
	/**
	 * Verifies that search can find intersecting words in a 2D grid and returns null for words not in grid
	 */
	public void testSearch2D() {
		final String[] words = {"def","beh","gec","foo"}; 
		final char[][][] grid = {
			{
				{'a','b','c'},
				{'d','e','f'},
				{'g','h','i'}
			}
		};
		final int[][][] locations = _wordSearch.searchForAll(grid, words);
		final int[][][] expected = {
			{{0,1,0},{0,1,1},{0,1,2}},
			{{0,0,1},{0,1,1},{0,2,1}},
			{{0,2,0},{0,1,1},{0,0,2}},
			null
		};
		for (int i = 0; i < locations.length; i++) {
			assertArrayEquals(expected[i], locations[i]);
		}
	}

	@Test
	/**
	 * Verifies that search can find intersecting words in a 3D grid and returns null for words not in grid
	 */
	public void testSearch3D() {
		final String[] words = {"aku","ajs","amy","an0","clu","foo"};
		final char[][][] grid = {
			{
				{'a','b','c'},
				{'d','e','f'},
				{'g','h','i'}
			},
			{
				{'j','k','l'},
				{'m','n','o'},
				{'p','q','r'}
			},
			{
				{'s','t','u'},
				{'v','w','x'},
				{'y','z','0'}
			}
		};
		final int[][][] locations = _wordSearch.searchForAll(grid, words);
		final int[][][] expected = {
			{{0,0,0},{1,0,1},{2,0,2}},
			{{0,0,0},{1,0,0},{2,0,0}},
			{{0,0,0},{1,1,0},{2,2,0}},
			{{0,0,0},{1,1,1},{2,2,2}},
			{{0,0,2},{1,0,2},{2,0,2}},
			null
		};
		for (int i = 0; i < locations.length; i++) {
			assertArrayEquals(expected[i], locations[i]);
		}
	}

	@Test
	/**
	 * Verifies that giving make() an invalid size (any size < 0) returns null
	 */
	public void testMakeInvalidSize() {
		assertNull(_wordSearch.make(new String[]{"a"}, -1, 1, 1));
		assertNull(_wordSearch.make(new String[]{"a"}, 1, -1, 1));
		assertNull(_wordSearch.make(new String[]{"a"}, 1, 1, -1));
		assertNull(_wordSearch.make(new String[]{"a"}, -1, -1, -1));
	}
	
	@Test
	/**
	 * Verifies that giving make() a size of 0 and no words produces a 0x0x0 grid, null if provided words
	 */
	public void testMakeZeroSize() {
		final char[][][] grid = _wordSearch.make(new String[0], 0, 0, 0);
		
		assertNotNull(grid);
		assertEquals(0, grid.length);
		assertNull(_wordSearch.make(new String[]{"a"}, 0, 0, 0));
	}
	
	/**
	 * Tests that a valid grid is made given provided list of words and dimensions
	 * @param words words to be inserted
	 * @param x size of the grid along first dimension
	 * @param y size of the grid along second dimension
	 * @param z size of the grid along third dimension
	 */
	public void testMakesValidGrid(String[] words, int x, int y, int z) {
		final char[][][] grid = _wordSearch.make(words, x, y, z);
		final String msg = (grid == null) ? "null" : Arrays.deepToString(grid);

		assertNotNull(msg, grid);
		assertEquals(msg, x, grid.length);
		for (int r = 0; r < x; r++) {
			assertEquals(msg, y, grid[r].length);
			for (int c = 0; c < y; c++) {
				assertEquals(msg, z, grid[r][c].length);
				for (int a = 0; a < z; a++) assertTrue(msg, Character.isLetter(grid[r][c][a]));
			}
		}
	}

	@Test
	/**
	 * Verifies that calling make() with an invalid array of words (empty or null)
	 * produces a puzzle with no words (random characters) (still valid grid)
	 */
	public void testMakeInvalidWords() {
		testMakesValidGrid(new String[]{}, 10, 11, 12); //provided empty array
		testMakesValidGrid(new String[0], 9, 8, 7); //provided array of length 0
		testMakesValidGrid(null, 7, 8, 9); //provided null	
	}

	@Test
	/**
	 * Verifies that calling make with an array of words containing an empty word 
	 * still works (acts as if empty word is not there)
	 */
	public void testMakeHasEmpty() {
		final char[][][] grid = _wordSearch.make(new String[]{"foo","","bar"}, 4, 5, 6); //provided two valid words and an empty one
		
		assertNotNull(grid);
		testMakesValidGrid(new String[]{"",""}, 6, 5, 4); //provided only empty words
		assertNotNull(_wordSearch.search(grid, "foo"));
		assertNotNull(_wordSearch.search(grid, "bar"));
		assertNull(_wordSearch.search(grid, ""));
	}

	@Test
	/**
	 * Verifies that calling make with an array of words containing null
	 * still works (acts as if null is not there)
	 */
	public void testMakeHasNull() {
		final char[][][] grid = _wordSearch.make(new String[]{"foo",null,"bar"}, 4, 5, 6); //provided two valid words and an empty one
		
		assertNotNull(grid);
		testMakesValidGrid(new String[]{null,null}, 6, 5, 4); //provided only empty words
		assertNotNull(_wordSearch.search(grid, "foo"));
		assertNotNull(_wordSearch.search(grid, "bar"));
		assertNull(_wordSearch.search(grid, null));
	}

	@Test
	/**
	 * Verifies searching for an empty word returns null (cannot find empty word)
	 */
	public void testSearchEmpty() {
		final char[][][] grid = _wordSearch.makeRandom(5, 6, 7);
		final int[][] s = _wordSearch.search(grid, "");
		final String msg = Arrays.deepToString(s);
		assertNull(msg, s);
	}

	@Test
	/**
	 * Verifies that searching for null returns null (cannot find null word)
	 */
	public void testSearchNullWord() {
		final char[][][] grid = _wordSearch.makeRandom(5, 6, 7);
		final int[][] s = _wordSearch.search(grid, null);
		final String msg = Arrays.deepToString(s);
		assertNull(msg, s);
	}

	@Test
	/**
	 * Verifies that searching for anything inside an invalid grid (null or of size 0) returns null
	 */
	public void testSearchInvalidGrid() {
		assertNull(_wordSearch.search(null, "foo"));
		assertNull(_wordSearch.search(null, ""));
		assertNull(_wordSearch.search(null, null));
		assertNull(_wordSearch.search(new char[0][0][0], "bar"));
		assertNull(_wordSearch.search(new char[0][0][0], ""));
		assertNull(_wordSearch.search(new char[0][0][0], null));
	}

	@Test
	/**
	 * Verifies that attempting call make() with a size larger than Integer.MAX_VALUE returns null (doesn't overflow)
	 */
	public void testMakeMaxValue() {
		final String[] words = {"foo", "bar"};
		assertNull(_wordSearch.make(words, Integer.MAX_VALUE+1, 5, 5));
		assertNull(_wordSearch.make(words, 5, Integer.MAX_VALUE+1, 5));
		assertNull(_wordSearch.make(words, 5, 5, Integer.MAX_VALUE+1));
	}

	@Test
	/**
	 * Verifies that attempting call make() with a size smaller than Integer.MIN_VALUE returns null (doesn't underflow)
	 */
	public void testMakeMinValue() {
		final String[] words = {"foo", "bar"};
		assertNull(_wordSearch.make(words, Integer.MIN_VALUE+1, 5, 5));
		assertNull(_wordSearch.make(words, 5, Integer.MIN_VALUE+1, 5));
		assertNull(_wordSearch.make(words, 5, 5, Integer.MIN_VALUE+1));
	}















	@Before
	public void setUp() {
		_wordSearch = new WordSearch3D();
	}
}
