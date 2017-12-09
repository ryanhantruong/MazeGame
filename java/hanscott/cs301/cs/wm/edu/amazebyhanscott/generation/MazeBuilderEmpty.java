package hanscott.cs301.cs.wm.edu.amazebyhanscott.generation;

public class MazeBuilderEmpty extends MazeBuilder implements Runnable {
	
	public MazeBuilderEmpty(){
		super();
		// empty maze so set rooms to 0
		System.out.println("MazeBuilderEmpty will generate Maze with no walls only borders.");
	}
	
	public MazeBuilderEmpty(boolean det){
		super(det);
		// empty maze so set rooms to 0
		System.out.println("MazeBuilderEmpty will generate Maze with no walls only borders.");
	}
	
	/**
	 * This method generates pathways into the maze by using Prim's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wall separating these cells in the maze. 
	 */
	@Override
	protected void generatePathways() {
	
		// delete the walls by row
		// delete east wall
		CardinalDirection cd;
		Wall wallRemove = new Wall(0,0,CardinalDirection.East);
		for (int j=0;j<height;j++){
			for (int i=0;i<width;i++){
				
				// delete east wall if not right border cell
				if (i!=width-1){
					cd = CardinalDirection.East;
					wallRemove.setWall(i, j, cd);
					cells.deleteWall(wallRemove);}
				
				// delete west wall if not a left border cell
				if (i!=0){
					cd = CardinalDirection.West;
					wallRemove.setWall(i, j, cd);
					cells.deleteWall(wallRemove);}
				
				// delete south wall if not bottom border cell
				if (j!=height-1){
					cd = CardinalDirection.South;
					wallRemove.setWall(i, j, cd);
					cells.deleteWall(wallRemove);}
				
				// delete north wall if not top border cell
				if (j!=0){
					cd = CardinalDirection.North;
					wallRemove.setWall(i, j, cd);
					cells.deleteWall(wallRemove);}
			}
		}
	}
	

	

}
