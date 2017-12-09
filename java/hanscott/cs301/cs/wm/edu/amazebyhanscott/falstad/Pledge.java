package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Direction;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Turn;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;

public class Pledge implements RobotDriver {

	private Robot robot;
	private int width;
	private int height;
	private Distance dists;
	private float startBatteryLevel;
	private boolean pause;
	
	public Pledge(){
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
	public void setDistance(Distance distance) {
		dists = distance;
	}

	@Override
	public boolean keyDown(MazeController.UserInput key, int value){ return true;};
	@Override
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	@Override
	public boolean drive2Exit() throws Exception {
		int leftSensor;
		int frontSensor;
		
		int turns =0;

		while ( (!robot.canSeeExit(Direction.FORWARD) || !robot.isAtExit()) && !pause) {
			
			// check if enough battery
			if (robot.getBatteryLevel() < 1) {break;}
			frontSensor = robot.distanceToObstacle(Direction.FORWARD);
			if (frontSensor > 0){
				if (turns==0){
					
					for (int i=0;i<frontSensor;i++){
						// check if enough battery
						if (robot.getBatteryLevel() < robot.getEnergyForStepForward()) {break;}
						// move until hit obstacle
						robot.move(1,false);
					}
					
					// check if enough battery then turn right
					if (robot.getBatteryLevel() < robot.getEnergyForFullRotation()/4) {break;}
					robot.rotate(Turn.RIGHT); turns++;}
				
				else {
					
					//move 1 cell
					if (robot.getBatteryLevel() < robot.getEnergyForStepForward()) {break;}
					robot.move(1,false);}
			}
			else if (frontSensor==0) {
				// check if enough battery
				if (robot.getBatteryLevel() < robot.getEnergyForFullRotation()/4) {break;}
				robot.rotate(Turn.RIGHT); turns++;}
		
			// check if enough battery
			if (robot.getBatteryLevel() < 1) {break;}
			leftSensor = robot.distanceToObstacle(Direction.LEFT);
			if (leftSensor > 0) {
				// check if enough battery
				if (robot.getBatteryLevel() < robot.getEnergyForFullRotation()/4) {break;}
				robot.rotate(Turn.LEFT); turns--;} 
			
			// check if enough battery for next iteration while check
			if (robot.getBatteryLevel() < 1) {break;}
			
		}

		if (robot.isAtExit() && robot.canSeeExit(Direction.FORWARD)){ robot.move(1,false);}
		
		return robot.isAtExit();
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
