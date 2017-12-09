package hanscott.cs301.cs.wm.edu.amazebyhanscott;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.R;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.*;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.generation.MazeConfiguration;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;


public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    MazePanel panel;
    FirstPersonDrawer firstDrawer;
    MapDrawer mapDrawer;
    MazeController controller;
    MazeConfiguration mazeConfig;
    RobotDriver robotDriver;
    Robot robot;
    Handler handler;
    LinearLayout layout;
    MediaPlayer player;
    ProgressBar energyBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        player = MediaPlayer.create(getApplicationContext(),R.raw.music);
        player.start();

        layout = findViewById(R.id.panel);

        panel = new MazePanel(this);
        controller = GlobalMazeController.getController();

        // get robot stuff from controller
        robotDriver = controller.getDriver();
        robot = controller.getRobot();

        // panel stuff to draw maze graphics
        controller.setPanel(panel);
        controller.setPlayActivity(this);

        layout.setBackground(new BitmapDrawable(getResources(), panel.getBitMap()));

        energyBar = (ProgressBar) findViewById(R.id.battery);
        energyBar.setMax(3000);
        handler = new Handler();

        controller.switchToPlayingScreen();



        Button up = (Button) findViewById(R.id.up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked UP button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                robotDriver.keyDown(MazeController.UserInput.Up,0);
                gameDone();
            }
        });

        Button down = (Button) findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked DOWN button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                //robotDriver.keyDown(MazeController.UserInput.Down,0);
                if (robotDriver instanceof ManualDriver){
                controller.keyDown(MazeController.UserInput.Down,0);}
            }
        });

        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked LEFT button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                robotDriver.keyDown(MazeController.UserInput.Left,0);
            }
        });

        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked RIGHT button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                robotDriver.keyDown(MazeController.UserInput.Right,0);
            }
        });

        ToggleButton map = (ToggleButton) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked MAP button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                controller.keyDown(MazeController.UserInput.ToggleFullMap,0);
            }
        });

        ToggleButton solution = (ToggleButton) findViewById(R.id.solution);
        solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked SOLUTION button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                controller.keyDown(MazeController.UserInput.ToggleSolution,0);
            }
        });

        ToggleButton wall = (ToggleButton) findViewById(R.id.wall);
        wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked WALL button";
                //Toast.makeText(PlayActivity.this,message,Toast.LENGTH_SHORT).show();
                Log.v(TAG,message);
                controller.keyDown(MazeController.UserInput.ToggleLocalMap,0);
            }
        });


        final ToggleButton startpause = (ToggleButton) findViewById(R.id.startpause);
        startpause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                writeLog("start drive2exit");
                                robotDriver.setPause(false);
                                robotDriver.drive2Exit();
                                gameDone();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //panel.invalidate();
                            // handler.postDelayed(this,0);
                        }
                    });
                    t.start();
                } else {
                    writeLog("set robot driver to pause");
                    robotDriver.setPause(true);
                }
            }
        });

        /*
        startpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeLog("You clicked START/PAUSE button");

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            robotDriver.drive2Exit();
                            gameDone();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //panel.invalidate();
                        // handler.postDelayed(this,0);
                    }
                });

                writeLog("about to t.start");
                t.start();
            }
        });*/

    }


    private void gameDone(){
        if (controller.gameOver()) {
            player.stop();
            Intent myIntent = new Intent(this, FinishActivity.class);
            startActivity(myIntent);
        }
    }

    private void writeLog(String msg){
        Log.v("PlayActivity",msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_amaze, menu);
        return true;
    }

    public void reset(){
        final Resources resource = this.getResources();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = panel.getBitMap();
                Drawable background = new BitmapDrawable(resource,bitmap);
                layout.setBackground(background);
                //panel.invalidate();
                if (energyBar!=null){
                energyBar.setProgress(Integer.valueOf((int) robot.getBatteryLevel()));
                writeLog("robot battery is "+robot.getBatteryLevel());}
                else{ writeLog("energyBar is null");}
            }
        },25);
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
}
