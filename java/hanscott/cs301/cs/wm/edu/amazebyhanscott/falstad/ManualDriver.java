package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeController.UserInput;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Direction;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot.Turn;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;

public class ManualDriver implements RobotDriver {

	Robot robot;
	int mazeWidth;
	int mazeHeight;
	Distance distance;
	float startBatteryLevel;
	boolean pause;
	
	public ManualDriver(){ 
	}
	
	public ManualDriver(Robot r){
		setRobot(r);
	}
	
	@Override
	public void setRobot(Robot r) {
		this.robot = r;
		startBatteryLevel = robot.getBatteryLevel();
	}

	@Override
	public void setDimensions(int width, int height) {
		mazeWidth = width;
		mazeHeight = height;
	}

	@Override
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	@Override
	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	@Override
	public boolean drive2Exit() throws Exception {
		// manual drive doesn't use this method
		return false;
	}

	@Override
	public float getEnergyConsumption() {
		return startBatteryLevel - robot.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}

	@Override
	public boolean keyDown(UserInput key,int value){
		System.out.println("key input is processed by Driver.keyDown");	
		switch (key){
		case Up:
			robot.move(1, true);
			break;
		case Left:
			robot.rotate(Turn.LEFT);
			break;
		case Right:
			robot.rotate(Turn.RIGHT);
			break;
		case Down:
			robot.move(-1,true);
			break;
		default:
			System.out.println("key input not supported by Driver.keyDown");	
			break;
		}
		return true;
	}
}
