package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;


import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeController.UserInput;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.CardinalDirection;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Cells;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.MazeConfiguration;
import android.util.Log;

/**
 * This class represents a robot with the goal of navigating out of a maze
 * Responsibilities:
 * know info from distance sensors (where the walls are)
 * know if exit is in sight
 * know if we are at exit
 * know battery level
 * keep track of distance traveled (odometer)
 * update battery level after every move,rotation,check
 * rotate by updating screen graphics using MazeController
 * move by updating screen graphics using MazeController
 * 
 * Collaborators: MazeController
 * @author Han Truong
 */
public class BasicRobot implements Robot{

	// these variables serve as our sensor
	// encode the distance to wall
	// smallest value should be 0 when next to wall
	
	// these variables tell us battery level and distance traveled
	private float batteryLevel;
	private float startBatteryLevel = 3000; // default starting battery level
	private int odometer;
	
	/* these variables tell whether implementation has specific type of sensors*/
	private boolean forwardSensorSwitch = false;
	private boolean rightSensorSwitch = false;
	private boolean leftSensorSwitch = false;
	private boolean backwardSensorSwitch = false;
	private boolean roomSensorSwitch = false;
	
	// variables that refers to the maze the robot is in aka the MazeController implementation the robot operates on
	private MazeController maze;
	private MazeConfiguration mazeConfig;
	
	
	public BasicRobot(){
		// initiate robot in default configuration: all sensors on
		initRoomSensor();
		initAllDistanceSensor();
		setBatteryLevel(startBatteryLevel);
		resetOdometer();
	}
	
	public BasicRobot(boolean sensorOff){
		// when sensorOff is true then no sensor is on
		// have to call init sensor methods seperately
		if (!sensorOff){
		initRoomSensor();
		initAllDistanceSensor();}
		
		setBatteryLevel(startBatteryLevel);
		resetOdometer();
	}
	
	public BasicRobot(MazeController maze){
		// initiate robot in default configuration: all sensors on
		initRoomSensor();
		initAllDistanceSensor();
		setBatteryLevel(startBatteryLevel);
		resetOdometer();
		setMaze(maze);	
	}
	
	public BasicRobot(MazeController maze, float batteryLevel){
		// initiate robot in default configuration: all sensors on
		initRoomSensor();
		initAllDistanceSensor();
		setBatteryLevel(batteryLevel);
		resetOdometer();
		setMaze(maze);	
	}
	
	
	/**
	 * Initiate the distance sensor in direction wanted  which encodes the distance between robot and wall
	 * Distance is in unit of cells
	 * Turn on the switch for distance sensor 
	 */
	public void initDistanceSensor(Direction direction){
		switch(direction){
		case FORWARD:
			forwardSensorSwitch = true;
		case BACKWARD:
			backwardSensorSwitch = true;
		case LEFT:
			leftSensorSwitch = true;
		case RIGHT:
			rightSensorSwitch = true;
		default:
			// dont have this direction
		}
	}
	
	
	/**
	 * Initiate the 4 basic distance sensors  which encodes the distance between robot and wall
	 * Distance is in unit of cells
	 * Turn on the switch for distance sensor 
	 */
	public void initAllDistanceSensor(){
			forwardSensorSwitch = true;
			backwardSensorSwitch = true;
			leftSensorSwitch = true;
			rightSensorSwitch = true;
	}
	
	/**
	 * Initiate the room sensor which encodes whether robot is currently in a room or not
	 * Turn on the switch for distance sensor 
	 */
	public void initRoomSensor(){
		roomSensorSwitch = true;
	}
	
	/**
	 * Turn robot on the spot for amount of degrees. 
	 * If robot runs out of energy, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * param direction to turn and relative to current forward direction.
	 */
	public void rotate(Turn turn){
		// check for enough energy to turn
		if (getBatteryLevel() < getEnergyForFullRotation()/4){
			System.out.println("not enough battery");
			maze.switchToFinishScreen(getEnergyConsumption(), getOdometerReading(),false);
			// reset parameters for next run/maze/game
			setBatteryLevel(startBatteryLevel);resetOdometer();
			return;}
		
		int dir; 
		// look at MazeController rotate method lead to beliebe that +1 is right and -1 is left
		// could be wrong
		//System.out.println("Before turn cd: "+getCurrentDirection());
		boolean keySucess;
		switch (turn){
		case LEFT: 
			dir = -1;
			keySucess = maze.keyDown(UserInput.Left, 0);
			//maze.rotate(dir);
			// cost 1/4 energy of full rotation
			if (keySucess) {
			adjustBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/4);
			System.out.println("Turn left");}
			break;
		case RIGHT:
			dir = 1;
			keySucess = maze.keyDown(UserInput.Right, 0);
			if (keySucess){
			// cost 1/4 energy of full rotation
			adjustBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/4);
			System.out.println("Turn right");}
			break;
		case AROUND:
			// special case so gotta check again before rotate
			if (getBatteryLevel() < 2*getEnergyForFullRotation()/4){
				return;}
			dir = 1;
			// do 2 quarter rotations
			keySucess = maze.keyDown(UserInput.Right, 0);
			keySucess = keySucess && maze.keyDown(UserInput.Right, 0);
			if (keySucess) {
			// cost 1/2 energy of full rotation
			adjustBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/2);
			System.out.println("Turn around"); }
			break;
		}
		//System.out.println("After turn cd: "+getCurrentDirection());
		writeLog("in Robot - battery after turn "+getBatteryLevel());
	}

	private void writeLog(String msg){
		Log.v("MazeController",msg);
	}
	
	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it depends on the mode of operation
	 * what happens. If an algorithm drives the robot, it remains at the position in front 
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual maze
	 * do not share a consistent view on where walls are and where not.
	 * If a user manually operates the robot, this behavior is inconvenient for a user,
	 * such that in case of a manual operation the robot remains at the position in front
	 * of the obstacle but hasStopped() == false and the game can continue.
	 * @param distance is the number of cells to move in the robot's current forward direction 
	 * @param manual is true if robot is operated manually by user, false otherwise
	 * @precondition distance >= 0
	 */
	public void move(int distance, boolean manual){	
		if (distance < 0){ 
			System.out.println("robot cant move backward");
			return;
		}
		
		for (int i=0;i<distance;i++){	
			
			// only check for hasStopped if not in manual mode, 
			if (!manual){
				// if hasStopped is true then we cant walk
				System.out.println("Not in manual mode");
				if (hasStopped()) {
					System.out.println("hasStopped");
					return;
				}
			} else {
				// check if we're facing exit cell
				if (canSeeExit(Direction.FORWARD) && isAtExit()){
					writeLog("in manual mode and about to enter exit");
					maze.switchToFinishScreen(getEnergyConsumption(), getOdometerReading(),true);
					// reset parameters for next run/maze/game
					setBatteryLevel(startBatteryLevel);resetOdometer();
					return;
				}
			}
			
			// always check for battery level regardless of mode
			if (getBatteryLevel() < getEnergyForStepForward()){
				System.out.println("not enough battery");
				maze.switchToFinishScreen(getEnergyConsumption(), getOdometerReading(),false);
				// reset parameters for next run/maze/game
				setBatteryLevel(startBatteryLevel);resetOdometer();
				return;
			}
			
			// switch to another method call
			maze.keyDown(UserInput.Up,0);
			
			// update the distance we traveled
			odometer++;
			
			// each step cost energy
			adjustBatteryLevel(getBatteryLevel() - getEnergyForStepForward());
		}
		writeLog("in Robot - battery after move "+getBatteryLevel());
	}
	
	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * @postcondition 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 * @throws Exception if position is outside of the maze
	 */
	public int[] getCurrentPosition() throws Exception {
		return maze.getCurrentPosition();
	}
	
	/**
	 * Provides the robot with a reference to the maze it is currently in.
	 * The robot memorizes the maze such that this method is most likely called only once
	 * and for initialization purposes. The maze serves as the main source of information
	 * for the robot about the current position, the presence of walls, the reaching of an exit.
	 * @param maze is the current maze
	 * @precondition maze != null, maze refers to a fully operational, configured maze configuration
	 */
	public void setMaze(MazeController maze) {
		this.maze =  maze;
		mazeConfig = getMazeConfig();
		// at this point these references still point to null
	}
	
	/**
	 * Tells if current position (x,y) is right at the exit but still inside the maze. 
	 * Used to recognize termination of a search.
	 * @return true if robot is at the exit, false otherwise
	 */
	public boolean isAtExit() {
		// WARNING: coordinate system for ROBOT and for Cells must agree
		// check that getCurrentDirection() in this class has "converted it" 
		int[] pos;
		try {
			pos = getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Cannot determine current position so method fails");
			return false;
		}
		int px = pos[0];
		int py = pos[1];
		// call cells method and return its return value
		return getCells().isExitPosition(px, py);
	}
	
	/**
	 * Tells if a sensor can identify the exit in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 */
	public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {
		adjustBatteryLevel(getBatteryLevel()+getEnergyForSensing());
		return distanceToObstacle(direction) == Integer.MAX_VALUE;
	}
	
	/**
	 * get energy cost if we sense distance to obstacle or check for exit in sight
	 * @return
	 */
	public float getEnergyForSensing(){
		return 1;
	}
	
	
	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	public boolean isInsideRoom() throws UnsupportedOperationException {
		// if robot has no room sensor then throw unsupported
		if (!hasRoomSensor())throw new UnsupportedOperationException();
		
		int[] pos;	
		try {
			pos = getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot determine current position so cannot determine if we are in a room");
			return false;
		}
		// call cells method and return its return value
		return getCells().isInRoom(pos[0],pos[1]);
	}
	
	/**
	 * Tells if the robot has a room sensor.
	 */
	public boolean hasRoomSensor() {
		return roomSensorSwitch;
	}
	
	/**
	 * Provides the current cardinal direction.
	 * 
	 * @return cardinal direction is robot's current direction in absolute terms
	 */	
	public CardinalDirection getCurrentDirection() {
		return maze.getCurrentDirection();
	}
	
	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	public float getBatteryLevel() {
		return batteryLevel;
	}
	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @precondition level >= 0 
	 */
	public void setBatteryLevel(float level) {
		if (level<0){
			System.out.println("Warning: battery level is smaller than 0");
		}
		startBatteryLevel = level;
		batteryLevel = level;
	}
	
	/** 
	 * Gets the distance traveled by the robot.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	public int getOdometerReading(){
		return odometer;
	}
	
	/** 
     * Resets the odomoter counter to zero.
     * The robot has an odometer that calculates the distance the robot has moved.
     * Whenever the robot moves forward, the distance that it moves is added to the odometer counter.
     * The odometer reading gives the path length if its setting is 0 at the start of the game.
     */
	public void resetOdometer(){
		odometer = 0;
	}
	
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	public float getEnergyForFullRotation(){
		return 3*4;
	}
	
	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	public float getEnergyForStepForward(){
		return 5;
	}
	
	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	public boolean hasStopped(){
		// lack of energy
		if (batteryLevel <= 0){ 
			// notify controller to switch to finish screen
			maze.switchToFinishScreen(getEnergyConsumption(),getOdometerReading(),false);
			// reset parameters for next run/maze/game
			setBatteryLevel(startBatteryLevel);resetOdometer();
			return true;}
		
		// check if hitting an obstacle in front of robot
		int dist = distanceToObstacle(Direction.FORWARD); 
		// distance method automatically used energy but here we shouldn't cause hasStopped is an internal operation
		adjustBatteryLevel(getBatteryLevel()+getEnergyForSensing());
		if (dist == 0){
			return true;}
		
		// check if hitting exit or not
		// changes so canSeeExit doesnt cost energy
		if (canSeeExit(Direction.FORWARD) && isAtExit()){
			// notify controller to switch to finish screen
			maze.switchToFinishScreen(getEnergyConsumption(),getOdometerReading(),true);
			// reset parameters for next run/maze/game
			setBatteryLevel(startBatteryLevel);resetOdometer();
			return true;}
		
		// false otherwise
		return false;
	}
	
	/**
	 * give amount of energy robot consumed so far through operations
	 * @return amount of energy robot used in float
	 */
	public float getEnergyConsumption(){
		return startBatteryLevel - getBatteryLevel();
	}
	
	/**
	 * adjust the current battery level of robot
	 * used in hasStopped() to prevent decrease battery level  
	 * for use with internal methods only
	 * param battery level
	 */
	private void adjustBatteryLevel(float level){
		batteryLevel = level;
	}
	
	/**
	 * Tells the distance to an obstacle (a wall or border) 
	 * in a direction as given and relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if current cell has a wall in this direction, 
	 * 1 if it is one step forward before directly facing a wall,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException{		
		// make sure we have enough energy
		if (getBatteryLevel() < getEnergyForSensing()) {
			System.out.println("not enough battery");
			maze.switchToFinishScreen(getEnergyConsumption(), getOdometerReading(),false);
			// reset parameters for next run/maze/game
			setBatteryLevel(startBatteryLevel);resetOdometer();
		}
		
		// make sure robot has distance sensor in current direction
		if (!hasDistanceSensor(direction)) throw new UnsupportedOperationException("Don't have "+direction+" sensor");
		
		// use energy for this method
		adjustBatteryLevel(getBatteryLevel() - getEnergyForSensing());
		
		// get current position
		int[] pos;
		try {
			pos = getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Cannot determine current position so method fails");
			throw new UnsupportedOperationException();
		}
		
		int px = pos[0];
		int py = pos[1];
		
		// translate our robot direction to CardinalDirection
		CardinalDirection cd = convertToCardinalDirection(direction);
		
		
		int wallDist = 0; //this means there's wall next to us
		// determine if there's obstacle in current position at given direction
		boolean faceObstacle = mazeConfig.hasWall(px, py, cd);
		// check that our has border agrees with cells natural has border
		
		boolean exitCheck;
		int old_px = px;
		int old_py = py;
		//System.out.println("distance sense at: "+px+","+py);
		//System.out.println("About to enter while loop to determine distance");
		// if not, go into loop until we do
		while (!faceObstacle) {// while we havent face a wall yet	
			// increment our position in current direction using another func()
			pos = getNextPosition(old_px,old_py,cd);
			px = pos[0];
			py = pos[1];
			//System.out.println("checking obstacle in cell (x,y): "+px+","+py);
			
			// check if new position is a valid position
			if (!mazeConfig.isValidPosition(px,py)){
				// not a valid position, this means the current position is one cell out of our maze
				// could be due to the fact that it is an exit
				// check if current position is exit position
				exitCheck = getCells().isExitPosition(old_px, old_py);
				if (exitCheck){
					System.out.println("found exit while distance sensing in robot pos: "+old_px+","+old_py+" "+direction+" and cd:"+cd);
					return Integer.MAX_VALUE;
				}
			
				// has to exit out of loop cause code below will fail
				// if it is not give an error
				System.out.println("Invalid maze position while distance sensing but not exit in robot pos: "+old_px+","+old_py+" "+direction+" and cd:"+cd);
				faceObstacle = true;
				break;
			}
			
			// determine if there's obstacle
			faceObstacle = mazeConfig.hasWall(px,py, cd);
			//faceBorder = cells.hasBorderCardinal(px, py, cd);
			//faceObstacle = faceBorder || faceWall;
			//System.out.println("Facewall? "+faceWall);
			//System.out.println("Faceborder? "+faceBorder);
			// increment our distance
			wallDist++;
			
			//save old px py for if statement
			old_px = px;
			old_py = py;
		}
		
		return wallDist;
	}
	
	/**
	 * translate robot relative direction to absolute map CardinalDirection
	 * WARNING: check that this agrees with cells coordinate system
	 * @param direction
	 * @return
	 */
	public CardinalDirection convertToCardinalDirection(Direction direction){
		// translate our robot direction to CardinalDirection
		// get current direction of robot in terms of cardinalDirection
		CardinalDirection cd = getCurrentDirection();
		// READ WARNING BELOW: and P3.pdf for sanity check
		// flipped to agree with maze cardinal direction for left and right
		switch (direction){
		case RIGHT:
			// rotate right from our current direction means clockwise
			cd = cd.rotateClockwise();
			cd = cd.oppositeDirection(); 
			break;
		case LEFT:
			cd = cd.rotateClockwise();  
			break;
		case BACKWARD:
			cd = cd.oppositeDirection(); 
			break;
		default:
			// do nothing cause its the forward direction
		}
		// print out results for manual checking
		//System.out.println("convert robot dir: "+direction+" to Caridinal: "+cd);
		return cd;
	}
	
	/**
	 * get the coordinate x,y for the cell in front of robot with respect to robot current direction in map
	 * @param x current pos
	 * @param y current pos
	 * param CardinalDirection want to move in
	 * @return integer array of x,y
	 */
	private int[] getNextPosition(int x,int y,CardinalDirection cd){
		// currently assuming same coordinate system with Cells and MazeGen 
		// might need to change later for south and north
		switch (cd){
		case South:
			y++;
			break;
		case North:
			y--;
			break;
		case West: 
			x--;
			break;
		case East:
			x++;
			break;
		}
		
		int[] result = new int[2];
		result[0] = x;
		result[1] = y;
		return result;
	}
	
	/**
	 * get the MazeConfiguration reference that our robot is in
	 * @return MazeConfiguration 
	 */
	protected MazeConfiguration getMazeConfig(){
		if (maze == null){
			System.out.println("cannot get mazeConfig because no reference to mazeController");
			return null;
		}
		return maze.getMazeConfiguration() ;
	}

	
	/**
	 * get the MazeBuilder reference that our robot is in
	 * @return MazeBuilder 
	 * precondition: has called setMaze before and 
	 */
	protected MazeController getMazeController(){
		if (maze == null){
			System.out.println("cannot get mazeController because no reference to mazeController");
			return null;
		}
		return maze;
	}
	
	/**
	 * get the MazeBuilder reference that our robot is in
	 * @return MazeBuilder 
	 * precondition: has called setMaze before and 
	 */
	private Cells getCells(){
		if (mazeConfig == null){
			System.out.println("cannot get cells because no reference to mazeConfig");
			return null;
		}
		return mazeConfig.getMazecells();
	}
	
	/**
	 * get the MazeBuilder reference that our robot is in
	 * @return MazeBuilder 
	 * precondition: has called setMaze before and 
	 */
	private Distance getDists(){
		if (mazeConfig == null){
			System.out.println("cannot get cells because no reference to mazeConfig");
			return null;
		}
		return mazeConfig.getMazedists();
	}
	
	
	/**
	 * Tells if the robot has a distance sensor for the given direction.
	 * Since this interface is generic and may be implemented with robots 
	 * that are more or less equipped. The purpose is to allow for a flexible
	 * robot driver to adapt its driving strategy according the features it
	 * finds supported by a robot.
	 */
	public boolean hasDistanceSensor(Direction direction){
		switch(direction){
		case FORWARD:
			return forwardSensorSwitch;
		case BACKWARD:
			return backwardSensorSwitch;
		case LEFT:
			return leftSensorSwitch;
		case RIGHT:
			return rightSensorSwitch;
		default:
			// dont have this direction
			return false;
		}
	}
}
