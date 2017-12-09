package hanscott.cs301.cs.wm.edu.amazebyhanscott;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.BasicRobot;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeController;
import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Robot;

public class FinishActivity extends AppCompatActivity {

    private boolean win = false;
    private static final String TAG = "FinishActivity";
    MazeController controller;
    Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView result = findViewById(R.id.result);
        TextView energy = findViewById(R.id.energy);
        TextView pathlength = findViewById(R.id.pathlength);

        controller = GlobalMazeController.getController();

        String result_str;
        if (controller.gameResult()){
            result_str = "CONGRATS! YOU WIN";
        } else{
            result_str = "GAME OVER! YOU LOSE";
        }
        result.setText(result_str);


        String energy_str = "Energy Consumed: "+controller.getEnergyConsumed();
        energy.setText(energy_str);
        String pathlength_str = "Path Length: "+controller.getPathLength();
        pathlength.setText(pathlength_str);







        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You clicked START OVER button";
                Toast.makeText(FinishActivity.this,message,Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(view.getContext(), AMazeActivity.class);
                startActivity(myIntent);
                Log.v(TAG,message);
            }
        });

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
}
