package hanscott.cs301.cs.wm.edu.amazebyhanscott;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Handler;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.BasicRobot;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.ManualDriver;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeController;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.MazeConfiguration;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.MazeContainer;

import java.util.concurrent.TimeUnit;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeFileReader;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeFileWriter;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.RobotDriver;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Distance;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.MazeFactory;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.Order;
import java.io.File;

public class GeneratingActivity extends AppCompatActivity {



    private static final String TAG = "GeneratingActivity";
    private MazeController controller;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initiate progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        // get info from AMazeActivity
        Bundle gt = getIntent().getExtras();
        String driver = gt.getString("driver");
        writeLog("drive is "+driver);
        String maze = gt.getString("maze");
        String skill = gt.getString("skill");
        final int skillNum = Integer.valueOf(skill);
        boolean revisit = gt.getBoolean("revisit");

        String message;
        controller = new MazeController();

        // for progress bar
        controller.setGeneratingActivity(this);
        handler = new Handler();


        // make robot
        BasicRobot robot = new BasicRobot(controller);
        // make driver
        ManualDriver manualDriver = new ManualDriver(robot);
        // pass robot stuff to controller
        controller.setRobot(robot);
        controller.passManualDriver(manualDriver);
        // pass other info
        controller.setDriver(driver);

        final String fileStr = getApplicationContext().getFilesDir()+"/maze"+skillNum+".xml";
        File f = new File(fileStr);

        if (revisit && f.exists() && skillNum <= 3){

            controller.setFilename(fileStr);
            controller.init();

        } else{

            // pass other info
            controller.setSkillLevel(skillNum);
            controller.init();

            boolean deterministic = false;
            final MazeFactory factory = new MazeFactory(deterministic);

            controller.setBuilder(convertBuilderToEnum(maze));

            writeLog("about to send");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    factory.order(controller);
                    writeLog("before delivered");
                    factory.waitTillDelivered();
                    writeLog("after delivered");


                    //writeLog("about to save maze");
                    if (skillNum <=3){
                        MazeConfiguration config = controller.getMazeConfiguration();
                        config.getHeight();
                        MazeFileWriter writer = new MazeFileWriter();
                        writeLog("about to save maze");
                        writeLog(fileStr);
                        writer.store(fileStr,config.getWidth(),config.getHeight(),0,0,config.getRootnode(),config.getMazecells(),config.getMazedists().getDists(),config.getStartingPosition()[0],config.getStartingPosition()[1]);
                    }
                }
            });
            t.start();

        }

        // send order for maze generation
        GlobalMazeController.setController(controller);

    }

    private void writeLog(String msg){
        Log.v("MazeController",msg);
    }

    private Order.Builder convertBuilderToEnum(String builder){
        Order.Builder b;
        switch (builder) {
            case "DFS":
                b = Order.Builder.DFS;
                break;
            case "Prim":
                b = Order.Builder.Prim;
                break;
            case "Eller":
                b = Order.Builder.Eller;
                break;
            default:
                b = Order.Builder.DFS;
                break;
        }
        return b;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_amaze, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateProgress(final int percentdone) {
        //progressBar.setProgress(0);
        //final AppCompatActivity gen = this;

       /* Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBar.getProgress() < percentdone) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBar.getProgress()+1);
                            writeLog("- in generate updated progress to " + progressBar.getProgress());
                            //writeLog(controller.getPercentDone()+ "/" + 100);
                        }
                    });
                }
                if (percentdone==100){
                // switch to play screen after we are done updating progress
                Intent myIntent = new Intent(GeneratingActivity.this, PlayActivity.class);
                startActivity(myIntent);}
            }
        });
        t.start();*/
            //writeLog("in while loop current progress "+progressBar.getProgress());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(percentdone);
                    writeLog("updated progress to " + progressBar.getProgress());
                    if (percentdone == 100) {
                        // switch to play screen after we are done updating progress
                        Intent myIntent = new Intent(GeneratingActivity.this, PlayActivity.class);
                        startActivity(myIntent);
                    }
                }
            },200);

    }

}
