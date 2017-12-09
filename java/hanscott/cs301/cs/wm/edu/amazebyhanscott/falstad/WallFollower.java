package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.PlayActivity;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Direction;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Turn;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;

import android.os.SystemClock;
import android.util.Log;


public class WallFollower implements RobotDriver{

	private Robot robot;
	private int width;
	private int height;
	private Distance dists;
	private float startBatteryLevel;
	private MazePanel panel;
	public PlayActivity play;
	private boolean pause = false;
	
	public WallFollower(){
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


	public void setPanel(MazePanel panel) {
		this.panel = panel;
	}

	@Override
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public MazePanel getPanel() {
		return panel;
	}

	public void setPlayActivity(PlayActivity play){
		this.play = play;
	}

	@Override
	public void setDistance(Distance distance) {
		dists = distance;
	}

	private void writeLog(String msg){
		Log.v("MazeController",msg);
	}


	@Override
	public boolean drive2Exit() {
		int leftSensor;
		int frontSensor;

		writeLog("inside Solver: in drive2Exit");

		leftSensor = robot.distanceToObstacle(Direction.LEFT);
		if (leftSensor > 0) robot.rotate(Turn.LEFT);

		while ( (!robot.canSeeExit(Direction.FORWARD) || !robot.isAtExit()) && !pause) {

			// for toogle button pause driver
			//if (pause){break;}

			//Thread.sleep(1000);
			//SystemClock.sleep(100000);

			// check if enough battery
			if (robot.getBatteryLevel() < 1) {break;}
			frontSensor = robot.distanceToObstacle(Direction.FORWARD);
			if (frontSensor >0){
				// check if enough battery
				if (robot.getBatteryLevel() < robot.getEnergyForStepForward()) {break;}
				robot.move(1, false);}
			else if (frontSensor ==0) {
				// check if enough battery
				if (robot.getBatteryLevel() < robot.getEnergyForFullRotation()/4) {break;}
				robot.rotate(Turn.RIGHT);}

			// check if enough battery
			if (robot.getBatteryLevel() < 1) {break;}
			leftSensor = robot.distanceToObstacle(Direction.LEFT);
			if (leftSensor > 0){
				// check if enough battery
				if (robot.getBatteryLevel() < robot.getEnergyForFullRotation()/4) {break;}
				robot.rotate(Turn.LEFT);
			}

			// check if enough battery for next iteration while check
			if (robot.getBatteryLevel() < 1) {break;}
		}

		if (robot.isAtExit() && robot.canSeeExit(Direction.FORWARD)){ robot.move(1,false);}
		writeLog("inside Solver: about to return from drive2Exit");
		return  robot.isAtExit() && robot.canSeeExit(Direction.FORWARD);

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
