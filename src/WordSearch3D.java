import java.util.*;
import java.io.*;

/**
 * Implements a 3D word search puzzle program.
 */
public class WordSearch3D {
	public WordSearch3D () {
	}

	final int MAX_TRIES = 100;
	
	class Position {
		private int row, column, aisle;

		public Position() { //no inputs, assume (0,0,0) index
			this.row = 0;
			this.column = 0;
			this.aisle = 0;
		}

		public Position(int row, int column, int aisle) {
			this.row = row; //represents row index
			this.column = column; //represents column index
			this.aisle = aisle; //represents aisle index
		}

		public Position(Position position, Vector vector) {
			this.row = position.getRow()+vector.getX();
			this.column = position.getColumn()+vector.getY();
			this.aisle = position.getAisle()+vector.getZ();
		}

		public int getRow() { return this.row; }			
		public int getColumn() { return this.column; }		
		public int getAisle() { return this.aisle; }	
		public void randomize(int maxRow, int maxColumn, int maxAisle) {
			final Random rng = new Random();

			this.row = rng.nextInt(maxRow);
			this.column = rng.nextInt(maxColumn);
			this.aisle = rng.nextInt(maxAisle);
		} 
	}

	class Vector {
		private int x, y, z;
		
		public Vector() { //no inputs, assume <0,0,0> vector
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}

		public Vector(int x, int y, int z) {
			this.x = x; //represents movement on x (-1 = backwards on x, 0 = no movement on x, +1 = forward on x)
			this.y = y; //represents movement on y (" " for y)
			this.z = z; //represents movement on z (" " for z)
		}

		public int getX() { return this.x; }
		public int getY() { return this.y; }
		public int getZ() { return this.z; }
		public void randomize() {
			final Random rng = new Random();

			this.x = rng.nextInt(3)-1;
			this.y = rng.nextInt(3)-1;
			this.z = rng.nextInt(3)-1;
		}
	}

	class Composite {
		private Position position;
		private Vector vector;
		private String word;

		public Composite(Position position, Vector vector, String word) {
			this.position = position;
			this.vector = vector;
			this.word = word;
		}

		public Composite(String word) {
			this.position = new Position();
			this.vector = new Vector();
			this.word = word;
		}

		public Position getPosition() { return this.position; }
		public Vector getVector() { return this.vector; }
		public String getWord() { return this.word; }
		public void setWord(String word) { this.word = word; }
		public void randomize(int maxRow, int maxColumn, int maxAisle) {
			this.position.randomize(maxRow, maxColumn, maxAisle);
			this.vector.randomize();
		}
	}

	/**
	 * Searches for all the words in the specified list in the specified grid.
	 * You should not need to modify this method.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param words the words to search for
	 * @return a list of lists of locations of the letters in the words
	 */
	public int[][][] searchForAll (char[][][] grid, String[] words) {
		final int[][][] locations = new int[words.length][][];
		for (int i = 0; i < words.length; i++) {
			locations[i] = search(grid, words[i]);
		}
		return locations;
	}


	/**
	 * Searches for the specified word in the specified grid.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param word the word to search for
	 * @return If the grid contains the
	 * word, then the method returns a list of the (3D) locations of its letters; if not, 
	 */
	public int[][] search(char[][][] grid, String word) {
		if (grid == null) return null;
		else if (word == null || word.isEmpty()) return null;
		Composite comp;
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				for (int a = 0; a < grid[r][c].length; a++) {
					comp = checkAt(grid, new Position(r, c, a), word);
					if (comp != null) return getCharPositions(comp);
				}
			}
		}
		return null;
	}

	/**
	 * Checks if the specified word is at specified position in specified grid
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param pos the position of the character we're checking
	 * @param word the word to search for
	 * @return Composite with Position of first character (if word found), Vector (direction), and word 
	 */
	public Composite checkAt(char[][][] grid, Position pos, String word) {
		Vector vec;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					vec = new Vector(x, y, z);
					if (checkLine(grid, pos, vec, word)) return new Composite(pos, vec, word);
				}
			}
		}
		return null;
	}

	/**
	 * Checks if the specified word is at given position in direction in provded grid (recursive)
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param pos the position of the character we're checking
	 * @param vec the direction we are checking in
	 * @param word the word to search for
	 * @return Boolean indicating if the word was found
	 */
	public Boolean checkLine(char[][][] grid, Position pos, Vector vec, String word) {
		final int row = pos.getRow();
		final int column = pos.getColumn();
		final int aisle = pos.getAisle();
		
		if (word.isEmpty()) return true;
		else if (
			row < 0 || row >= grid.length 
			|| column < 0 || column >= grid[0].length 
			|| aisle < 0 || aisle >= grid[0][0].length
			) return false;
		else if (getCharAtPos(grid, pos) == word.charAt(0)) {
			return checkLine(grid, new Position(pos, vec), vec, word.substring(1));
		}
		return false;
	}

	/**
	 * Gets the character at provided position in grid
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param pos the position to pull the character from
	 * @return character at position
	 */
	public char getCharAtPos(char[][][] grid, Position pos) {
		return grid[pos.getRow()][pos.getColumn()][pos.getAisle()];
	}

	/**
	 * Produces array representing the positions of characters of word in a 3Dimensional array
	 * @param comp Composite containing Position of word, direction (Vector) of word, and the word
	 * @return two-dimensional array of integers representing positions of each character in the word in a 3D word search puzzle
	 */
	public int[][] getCharPositions(Composite comp) {
		final Position pos = comp.getPosition();
		final Vector vec = comp.getVector();
		final int len = comp.getWord().length();

		int[][] arr = new int[len][3];

		for (int i = 0; i < len; i++) {
			arr[i] = new int[]{
				pos.getRow()+vec.getX()*i, 
				pos.getColumn()+vec.getY()*i, 
				pos.getAisle()+vec.getZ()*i
			};
		}
		return arr;
	}

	/**
	 * Tries to create a word search puzzle of the specified size with the specified
	 * list of words.
	 * @param words the list of words to embed in the grid
	 * @param sizeX size of the grid along first dimension
	 * @param sizeY size of the grid along second dimension
	 * @param sizeZ size of the grid along third dimension
	 * @return a 3D char array if successful that contains all the words, or null if
	 * no satisfying grid could be found or invalid size provided.
	 */
	public char[][][] make(String[] words, int sizeX, int sizeY, int sizeZ) {
		final String[] validWords = purgeInvalidWords(words); //purge invalid inputs (empty strings or null)
		if (
			(double)sizeX > Integer.MAX_VALUE || (double)sizeX < Integer.MIN_VALUE || sizeX < 0 
			|| (double)sizeY > Integer.MAX_VALUE || (double)sizeY < Integer.MIN_VALUE || sizeY < 0
			|| (double)sizeZ > Integer.MAX_VALUE || (double)sizeZ < Integer.MIN_VALUE || sizeZ < 0
			) return null; //return null if invalid size provided
		else if (validWords == null || validWords.length == 0) return makeRandom(sizeX, sizeY, sizeZ); //return random grid if null/empty list of words provided 
		else if (getLongest(validWords) > Math.max(sizeX, Math.max(sizeY, sizeZ))) return null; //return null if no possible fit for largest word
		
		final char[][][] grid = makeRandom(sizeX, sizeY, sizeZ);
		char[][][] temp;

		for (int i = 0; i < MAX_TRIES; i++) { //attempt MAX_TRIES times to build grid
			temp = tryMake(grid, validWords);
			if (temp != null) return temp; //grid built (tryMake succeeded)
		}
		return null; //return null if unable to build grid in fewer than MAX_TRIES attempts
	}

	/**
	 * Generates a grid with random characters
	 * @param sizeX number of rows of grid
	 * @param sizeY number of columns of grid
	 * @param sizeZ number of aisles of grid
	 * @return grid with specified rows, columns, and aisles
	 */
	public char[][][] makeRandom(int sizeX, int sizeY, int sizeZ) {
		final Random rng = new Random();
		char[][][] grid = new char[sizeX][sizeY][sizeZ];

		for (int r = 0; r < sizeX; r++) {
			for (int c = 0; c < sizeY; c++) {
				for (int a = 0; a < sizeZ; a++) {
					grid[r][c][a] = (char)(rng.nextInt(26)+'a');
				}
			}
		}
		return grid;
	}

	/**
	 * return length of longest word
	 * @param words list of words
	 * @return length of longest word
	 */
	public int getLongest(String[] words) {
		int max = words[0].length();
		for (String word : words) {
			if (word.length() > max) max = word.length();
		}
		return max;
	}

	/**
	 * Attempt to build a puzzle with provided words and base grid
	 * @param grid base grid to be inserted into
	 * @param words list of words to be inserted
	 * @return 3D puzzle containing provided words (or null if unable to insert a word))
	 */
	public char[][][] tryMake(char[][][] grid, String[] words) {
		char[][][] temp = duplicateGrid(grid);

		for (int i = 0; i < words.length; i++) {
			temp = tryWord(temp, words[i], Arrays.copyOfRange(words, 0, i+1)); //attempt to insert word into puzzle without altering previous words
			if (temp == null) return null; //fails to insert word (exceeded MAX_TRIES attempts)
		}
		return temp; //succeeded in inserting all words
	}

	/**
	 * Tries up to MAX_TRIES many times to insert provided word into a random spot of a puzzle containing some provided words
	 * @param grid base puzzle 
	 * @param word word to be inserted
	 * @param upTo words that should already be in the puzzle plus provided word to be inserted
	 * @return 3D puzzle containing all words in upTo (or null if unable to insert word)
	 */
	public char[][][] tryWord(char[][][] grid, String word, String[] upTo) {
		if (word.isEmpty()) return grid;
		Composite comp = new Composite(word);
		char[][][] temp;
		
		for (int i = 0; i < MAX_TRIES; i++) {
			comp.randomize(grid.length, grid[0].length, grid[0][0].length);
			temp = placeWord(grid, comp);
			if (temp != null && hasAll(temp, upTo)) return temp; //succeeded in inserting word and other words still present
		}
		return null; //fails to insert word after MAX_TRIES attempts
	}

	/**
	 * Attempt to insert word into 3D puzzle at specified position and direction
	 * @param grid puzzle to insert word into
	 * @param comp Composite containing word, position to insert at, and direction to insert in
	 * @return 3D puzzle now containing word (or null if attempting to insert outside of puzzle)
	 */
	public char[][][] placeWord(char[][][] grid, Composite comp) {
		final String word = comp.getWord();
		final Position pos = comp.getPosition();
		final Vector vec = comp.getVector();

		char[][][] temp = duplicateGrid(grid);
		int row, column, aisle;

		for (int i = 0; i < word.length(); i++) {
			row = pos.getRow()+vec.getX()*i;
			column = pos.getColumn()+vec.getY()*i;
			aisle = pos.getAisle()+vec.getZ()*i;
			
			if (
				row < 0 || row >= temp.length 
				|| column < 0 || column >= temp[0].length 
				|| aisle < 0 || aisle >= temp[0][0].length
				) return null; //would go out of bounds; not valid
			temp[row][column][aisle] = word.charAt(i);
		}
		return temp; //word inserted successfully
	}

	/**
	 * Produces a duplicate 3D char array as the input in order to avoid aliasing problems
	 * @param grid 3D char array to be duplicated
	 * @return duplicate array
	 */
	public char[][][] duplicateGrid(char[][][] grid) {
		char[][][] temp = new char[grid.length][grid[0].length][grid[0][0].length];
		for (int r = 0; r < temp.length; r++) {
			for (int c = 0; c < temp[0].length; c++) {
				for (int a = 0; a < temp[0][0].length; a++) {
					temp[r][c][a] = grid[r][c][a];
				}
			}
		}
		return temp;
	}

	/**
	 * checks if the provided puzzle has all of the provided words
	 * @param grid 3D puzzle to be searched
	 * @param words words to look for in the puzzle
	 * @return Boolean representing if all words were found
	 */
	public Boolean hasAll(char[][][] grid, String[] words) {
		final int[][][] locations = searchForAll(grid, words);
		for (int[][] loc : locations) if (loc == null) return false; //any word not found
		return true; //all words found
	}

	/**
	 * Purges provided array of Strings of any null or empty strings
	 * @param words array of Strings
	 * @return array of valid Strings
	 */
	public String[] purgeInvalidWords(String[] words) {
		if (words == null) return null;
		List<String> list = new ArrayList<String>();
		for (String word : words) if (word != null && !word.isEmpty()) list.add(word);
		return list.toArray(new String[list.size()]);
	}


	/**
	 * Exports to a file the list of lists of 3D coordinates.
	 * You should not need to modify this method.
	 * @param locations a list (for all the words) of lists (for the letters of each word) of 3D coordinates.
	 * @param filename what to name the exported file.
	 */
	public static void exportLocations(int[][][] locations, String filename) {
		// First determine how many non-null locations we have
		int numLocations = 0;
		for (int i = 0; i < locations.length; i++) {
			if (locations[i] != null) {
				numLocations++;
			}
		}

		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(numLocations);  // number of words
			pw.print('\n');
			for (int i = 0; i < locations.length; i++) {
				if (locations[i] != null) {
					pw.print(locations[i].length);  // number of characters in the word
					pw.print('\n');
					for (int j = 0; j < locations[i].length; j++) {
						for (int k = 0; k < 3; k++) {  // 3D coordinates
							pw.print(locations[i][j][k]);
							pw.print(' ');
						}
					}
					pw.print('\n');
				}
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Exports to a file the contents of a 3D grid.
	 * You should not need to modify this method.
	 * @param grid a 3D grid of characters
	 * @param filename what to name the exported file.
	 */
	public static void exportGrid (char[][][] grid, String filename) {
		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(grid.length);  // height
			pw.print(' ');
			pw.print(grid[0].length);  // width
			pw.print(' ');
			pw.print(grid[0][0].length);  // depth
			pw.print('\n');
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[0].length; y++) {
					for (int z = 0; z < grid[0][0].length; z++) {
						pw.print(grid[x][y][z]);
						pw.print(' ');
					}
				}
				pw.print('\n');
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Creates a 3D word search puzzle with some nicely chosen fruits and vegetables,
	 * and then exports the resulting puzzle and its solution to grid.txt and locations.txt
	 * files.
	 */
	public static void main (String[] args) {
		final WordSearch3D wordSearch = new WordSearch3D();
		final String[] words = new String[] { "apple", "orange", "pear", "peach", "durian", "lemon", "lime", "jackfruit", "plum", "grape", "apricot", "blueberry", "tangerine", "coconut", "mango", "lychee", "guava", "strawberry", "kiwi", "kumquat", "persimmon", "papaya", "longan", "eggplant", "cucumber", "tomato", "zucchini", "olive", "pea", "pumpkin", "cherry", "date", "nectarine", "breadfruit", "sapodilla", "rowan", "quince", "toyon", "sorb", "medlar" };
		final int xSize = 10, ySize = 10, zSize = 10;
		final char[][][] grid = wordSearch.make(words, xSize, ySize, zSize);
		exportGrid(grid, "grid.txt");

		final int[][][] locations = wordSearch.searchForAll(grid, words);
		exportLocations(locations, "locations.txt");
	}
}
