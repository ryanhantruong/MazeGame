package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.CardinalDirection;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Direction;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Turn;

public class Wizard implements RobotDriver {

	private Robot robot;
	private int width;
	private int height;
	private Distance dists;
	private float startBatteryLevel;
	private boolean pause;
	
	public Wizard(){
		// do nothing for now
	}
	
	@Override
	public void setRobot(Robot r) {
		robot = r;
		startBatteryLevel = robot.getBatteryLevel();
	}

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}



	@Override
	public boolean keyDown(MazeController.UserInput key, int value){ return true;};

	@Override
	public void setDistance(Distance distance) {
		dists = distance;
	}

	@Override
	public boolean drive2Exit() throws Exception {
		//find neighbor that is closer to exit
		Direction dir;
		// get current position
		int x;
		int y;
		try {
			int [] pos = robot.getCurrentPosition();
			x = pos[0];
			y = pos[1];
		} catch (Exception e) {
			throw e;
		}
		dir = getNeighborCloserToExit(x,y);
		// check if method fails, then we return false
		if (dir==null) return false;
		// turn to neighbor 
		turnToNeighbor(dir);
		
		while ( (!robot.isAtExit() || !robot.canSeeExit(Direction.FORWARD)) && !pause){// wont be executed if we are at exit
			if (robot.hasStopped()) {break;}
			// move to neighbor
			robot.move(1,false);
			// get current position
			try {
				int [] pos = robot.getCurrentPosition();
				x = pos[0];
				y = pos[1];
			} catch (Exception e) {
				throw e;
			}
			dir = getNeighborCloserToExit(x,y);
			// check if method fails, then we return false
			if (dir==null)return false;
			// turn to neighbor
			turnToNeighbor(dir);
		}
		
		if (robot.isAtExit() && robot.canSeeExit(Direction.FORWARD)){ robot.move(1,false);}
		
		return robot.isAtExit() && robot.canSeeExit(Direction.FORWARD);
	}
	
	private void turnToNeighbor(Direction dir){
		switch (dir){
		case BACKWARD:
			robot.rotate(Turn.AROUND);
			break;
		case RIGHT:
			// because map in display mode is flipped, a turn right will be equal to to turn left
			robot.rotate(Turn.RIGHT);
			break;
		case LEFT:
			robot.rotate(Turn.LEFT);
			break;
		default:
			// do nothing
			break;
		}
	}
	@Override
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	private Direction getNeighborCloserToExit(int x,int y) throws Exception {
		
		// find best candidate
		int dnext = getDistanceToExit(x,y) ;
		Direction result = null;
		int[] dir;
		
		// get robot current cardinal direction
		CardinalDirection cdOriginal = robot.getCurrentDirection();
		CardinalDirection cd = null;
		System.out.println("current robot cd: "+cdOriginal);
		for (Direction direction: Direction.values()) {
			// check if there's wall or border
			int distance;
			try {
				distance = robot.distanceToObstacle(direction);
			} catch (UnsupportedOperationException e){
				return null;
			}
			
			// there is a wall or border
			if (distance==0){
				System.out.println("obstacle hit at "+direction);
				continue; }
			// can see exit, return this direction cause this is the one we want to move forward in
			if (distance==Integer.MAX_VALUE){ // method will run into error if codes below are executed
				result = direction;
				return result;
			}

			// no wall
			// convert to Cardinal Direction from robot current direction
			cd = convertToCardinalDirection(direction);
			
			// convert to coordinate dir
			dir = cd.getDirection();
			int dn = getDistanceToExit(x+dir[0], y+dir[1]);
			if (dn < dnext) {
				// update neighbor position with min distance
				result = direction;
				dnext = dn ;
			}	
		}
		
		if (getDistanceToExit(x, y) <= dnext) 
		{
			throw new Exception("ERROR: Wizard.getNeighborCloserToExit cannot identify direction towards solution: stuck at: " + x + ", "+ y );
			
		}
		
		return result;
	}
	
	/**
	 * convert Direction to Cardinal Direction base on robot's current Cardinal Direction
	 * param Direction direction
	 * @return CardinalDirection cd 
	 */
	private CardinalDirection convertToCardinalDirection(Direction direction){
		CardinalDirection cd;
		// get robot current cardinal direction
		CardinalDirection cdOriginal = robot.getCurrentDirection();
		
		// no wall
		// convert direction to cardinal direction
		switch (direction){
		case BACKWARD:
			cd = cdOriginal.oppositeDirection();
			break;
		case RIGHT:
			// because map in display mode is flipped, a turn right will be equal to to turn left
			cd = cdOriginal.rotateClockwise();
			cd = cd.oppositeDirection();
			break;
		case LEFT:
			cd = cdOriginal.rotateClockwise();
			
			break;
		default:
			cd = cdOriginal;
			break;
		}
		return cd;
	}
	
	private int getDistanceToExit(int x, int y) {
		return dists.getDistance(x, y) ;
	}
	
	@Override
	public float getEnergyConsumption() {
		return startBatteryLevel - robot.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}

}
