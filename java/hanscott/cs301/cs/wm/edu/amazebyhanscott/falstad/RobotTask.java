package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;


/**
 * Created by Ryan on 12/4/17.
 */

public class RobotTask implements Runnable {

    Robot robot;
    String instruction;
    public RobotTask(Robot robot,String instruction){
        this.robot = robot;
        this.instruction = instruction;
    }

/*
    public void setInstruction(String instruction){
        this.instruction = instruction;
    }

    public void setRobot(Robot robot){
        this.robot = robot;
    }*/

    @Override
    public void run() {
        switch (instruction){
            case "move":
                robot.move(1,false);
                break;
            case "rotate":
                robot.rotate(Robot.Turn.RIGHT);
                break;
        }

    }
}
