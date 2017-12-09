package hanscott.cs301.cs.wm.edu.amazebyhanscott.generation;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Arrays;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.CardinalDirection;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Cells;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Wall;

/**
  * This class has the responsibility to create a maze of given dimensions (width, height) 
 * together with a solution based on a distance matrix.
 * The MazeBuilder implements Runnable such that it can be run a separate thread.
 * The MazeFactory has a MazeBuilder and handles the thread management.   

 * 
 * The maze is built with a randomized version of Prim's algorithm. 
 * This means a spanning tree is expanded into a set of cells by removing walls from the maze.
 * Algorithm leaves walls in tact that carry the border flag.
 * Borders are used to keep the outside surrounding of the maze enclosed and 
 * to make sure that rooms retain outside walls and do not end up as open stalls. 
 *   
 * @author Jones.Andrew, refactored pk
 */

public class MazeBuilderEller extends MazeBuilder implements Runnable {
	
	// declare additional instance variables
	private int[][] cellSets; // cellSets holds the setName of each cell
	
	
	// ROW SPECIFIC VARIABLES (reset whenever determineUniqueSet is called)
	int[][] uniqueSetID; //unique set has 2 dimension
	// uniqueSetID is specific for each row of cellSets
	// each row of uniqueSetID contains info for 1 unique set
	// first column (i = 0) of each row in uniqueSetID contains the name of the set
	// second column (i=1) contains numElements
	// 3rd column ( i <=2) and on on contains cell index or indexes
	
	// number of unique sets in each row of cells
	int numSets;

	
	
	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	public MazeBuilderEller(boolean det) {
		super(det);
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}

	/**
	 * This method generates pathways into the maze by using Eller's algorithm
	 */
	@Override
	protected void generatePathways() {
		// populate cellSets 
		int val = 1;
		cellSets = new int[height][width];
		for (int j=0;j<height;j++){
			for (int i=0;i<width;i++){
				cellSets[j][i] = val;
				val++;
				
			}
		}
		
		// iterate through the rows of matrix
		// functions are performed on each row
		int nUniqueSets ;
		for (int j=0;j<height;j++){
			determineUniqueSet(j);
			if (j== height-1){ // special procedure for last row
				joinAllUniqueSets(j);
				break;}
			joinAdjacentSetRandomly(j);	
			determineUniqueSet(j);
			chooseVerticalDirectionRandomly(j);
		}

	}
	
	private int[] getRowSets(int rowNumber){
		return cellSets[rowNumber];
	}
	/**
	 * Category: row specific function
	 * Role: initiate a uniqueSetID array, which contains information 
	 * about which set a cell from the row belongs to 
	 * @param rowNumber
	 * Supporting methods: makeNewSet(int cellSet,int cellPosition), addToSet(int setIndex, int cellPosition)
	 */
	private void determineUniqueSet(int rowNumber){
		uniqueSetID = new int[width][width+2];
		// case 0: all cells has the same set so we have width number of elements + 2 extra column for setName and numElements
		// case 1: all cells has unique sets so we have width number of sets
		// predefine the size as above will avoid array out of bound error
		// remember default value is 0, so no setName should == 0!
		numSets = 0;
		// iterate thru the whole row
		for (int x=0;x<width;x++){
			int cellSet = cellSets[rowNumber][x];
			int setIndex = isMemberSet(cellSet);
			if (setIndex == -1){
				makeNewSet(cellSet,x);
			}
			else{
				addToSet(setIndex,x);
			}
		}
	}
	
	/**
	 * Category: set specific function
	 * Role: merge the cells of 2 sets together into one set, merge the set that has a higher
	 * value name to the set with a lower value name
	 * @param setName1, setName2, rowNumber
	 * Supporting methods: isMemberSet(int setName), getNumOfElements(int setName)
	 */
	private void mergeSet(int setName1, int setName2, int rowNumber){
		// all set must have been added to uniqueSet array already before merging
		// get their indexes
		//System.out.println("merging setname "+setName1+" and "+ setName2);
		int setIndex1 = isMemberSet(setName1);
		int setIndex2 = isMemberSet(setName2);
		
		// we will merge into smaller setName
		int preySetIndex;
		int predatorSetIndex;
		if (setName1 > setName2){
			preySetIndex = setIndex1;
			predatorSetIndex = setIndex2;
		}
		else {
			preySetIndex = setIndex2;
			predatorSetIndex = setIndex1;
		}
		
		int preySetNumElements = getNumOfElements(preySetIndex);
		int predatorSetNumElements = getNumOfElements(predatorSetIndex);
		for (int i=0;i<preySetNumElements;i++){
			int preyCellIndex = getCellIndex(preySetIndex,i);  // 1 is second column, 1+1 is first cell index
			int predatorAddIndex = 1+predatorSetNumElements+1+i; // where we can start adding cell indexes from prey set
			//System.out.println("predatorAddIndex = "+predatorAddIndex);
			uniqueSetID[predatorSetIndex][predatorAddIndex] = preyCellIndex; 
			// updates the values of our cellSets array accordingly
			int predatorSetName = getSetName(predatorSetIndex);
			// since we merged, the cell in preySet should now have setName of predatorSet
			cellSets[rowNumber][preyCellIndex] = predatorSetName;
		}
		
		// update num of elements of predator and prey set
		uniqueSetID[predatorSetIndex][1] = predatorSetNumElements+preySetNumElements;
		// deactivate prey set by setting its num of element to 0;
		uniqueSetID[preySetIndex][1] = 0;
		// update the cell values accordingly
		
	}
	
	/**
	 * Category: set specific function
	 * Role: add information about a cell and its set into uniqueSetID array
	 * @param setName, cellIndex
	 */
	private void makeNewSet(int setName,int cellIndex){
		// make new set with cellval's index
		// update number of sets
		numSets++;
		// put new set name into array, we want the index of uniqueSetID to starts at 0
		int setIndex = numSets-1;
		uniqueSetID[setIndex][0] = setName;
		uniqueSetID[setIndex][1] = 1; 
		uniqueSetID[setIndex][2] = cellIndex;
	}
	
	/**
	 * Category: set specific function
	 * Role: retrieve the name of a set in uniqueSetID array based on the set's index in the array
	 * @param setIndex
	 * @return setName
	 */
	private int getSetName(int setIndex){
		return uniqueSetID[setIndex][0];
	}
	
	/**
	 * Category: set specific function
	 * Role: retrieve the number of cells in a set in uniqueSetID array based on the set's index in the array
	 * @param setIndex
	 */
	private int getNumOfElements(int setIndex){
		// 2nd column contains number of elements
		return uniqueSetID[setIndex][1];
	}
	
	/**
	 * Category: set specific function
	 * Role: add a cell's location from the cell row into a set listed in uniqueSetID array
	 * @param setIndex, cellIndex
	 */
	private void addToSet(int setIndex,int cellIndex){
		// add Cell val to already existed set
		int numElements = getNumOfElements(setIndex);
		uniqueSetID[setIndex][1+numElements+1] = cellIndex;
		// update num of elements in set
		uniqueSetID[setIndex][1] = numElements+1;
	}
	
	/**
	 * Category: set specific function
	 * Role: retrieve the index of a set on the uniqueSetID array based on its name, if the set is not listed in the array
	 * return -1 
	 * @param setName
	 * @return setIndex
	 */
	private int isMemberSet(int setName){
		// return setIndex if set exist, return -1 if not
		for (int i=0;i<numSets;i++){
			int setIndex = i;
			boolean memberCheck = setName == getSetName(setIndex);
			if (memberCheck){
				return setIndex;
			}
		}
		return -1;
	} 
	
	/**
	 * Category: set specific function
	 * Role: retrieve the index of cell from the cell row listed in a specific set in uniqueSetID array
	 * return -1 
	 * @param setIndex, elementIndex
	 * @return cellIndex
	 */
	private int getCellIndex(int setIndex, int elementIndex){
		return uniqueSetID[setIndex][1+1+elementIndex];
	}

	
	private void joinAllUniqueSets(int rowNumber){
		//iterate through each set
		for (int x=0;x<width;x++){
			//int endIndex = uniqueSetID[i][1];
			int endIndex = x;
			// dont mess with last cell next to border
			if (endIndex == width - 1) {continue;}
			
			// check if cells are of different set 
			boolean newSet = cellSets[rowNumber][endIndex] != cellSets[rowNumber][endIndex+1];
			//int a= endIndex+1;
			//System.out.println("last row,cell col+1: "+a+" setName ="+ cellSets[rowNumber][endIndex+1]);
			if (newSet){
				//join with set to the right
				//removes the wall between them  
				Wall wallRemove = new Wall(endIndex,rowNumber,CardinalDirection.East);
				cells.deleteWall(wallRemove);
				// redefine set value of the cell we merge together
				int setName1 = cellSets[rowNumber][endIndex];
				int setName2 =  cellSets[rowNumber][endIndex+1];
				mergeSet(setName1,setName2,rowNumber);
				}
			}
	}

	private void joinAdjacentSetRandomly(int rowNumber){
		//iterate through each set
		for (int x=0;x<width;x++){
			//int endIndex = uniqueSetID[i][1];
			int endIndex = x;
			// dont mess with last cell next to border
			if (endIndex == width - 1) {continue;}
			
			// randomly decide to join or not
			int joinDecision = random.nextIntWithinInterval(0,1);
			// check if cells are of different set 
			boolean newSet = cellSets[rowNumber][endIndex] != cellSets[rowNumber][endIndex+1];
			if (joinDecision==1 && newSet){ 
				//join with set to the right
				//removes the wall between them  
				Wall wallRemove = new Wall(endIndex,rowNumber,CardinalDirection.East);
				cells.deleteWall(wallRemove);
				// redefine set value of the cell we merge together
				int setName1 = cellSets[rowNumber][endIndex];
				int setName2 =  cellSets[rowNumber][endIndex+1];
				mergeSet(setName1,setName2,rowNumber);
				}
			}
	}
	
	private void chooseVerticalDirectionRandomly(int rowNumber){
		// these variables reset every time we want to drop a cell
		int cellColToDrop;
		int elementIndex;
		int setIndex;
		Wall wallRemove ;
		// iterate thru each set
		for (int i=0;i<numSets;i++){
			setIndex = i;
			// get number of elements the set has
			int numElements = getNumOfElements(setIndex);
			if (numElements == 0)continue; // the set is empty so no drop
			
			
			// choose a random element in the set to drop
			int dropElementIndex = random.nextIntWithinInterval(0,numElements-1);
			cellColToDrop = getCellIndex(setIndex,dropElementIndex);
			// drop this element
			cellSets[rowNumber+1][cellColToDrop] = cellSets[rowNumber][cellColToDrop];
			// delete the wall
			wallRemove = new Wall(cellColToDrop,rowNumber,CardinalDirection.South);
			cells.deleteWall(wallRemove);
			
			// iterate through every element in the set
			// for numElements==1, will get into this for loop once
			for (int k=0;k<numElements;k++){
				// because k starts at 0 but numElements starts at 1
				if (k==dropElementIndex) continue; // we already dropped this element, so no coin flip for dropping
				
				// flip coin if we should drop
				int dropDecision = random.nextIntWithinInterval(0,1);
				if (dropDecision == 1){
					elementIndex = k;
					cellColToDrop = getCellIndex(setIndex,elementIndex);
					cellSets[rowNumber+1][cellColToDrop] = cellSets[rowNumber][cellColToDrop];
					
					// delete the wall
					wallRemove = new Wall(cellColToDrop,rowNumber,CardinalDirection.South);
					cells.deleteWall(wallRemove);
				}
			}	
		}
	}
	

	


}