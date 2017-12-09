package hanscott.cs301.cs.wm.edu.amazebyhanscott;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;
import android.widget.*;

public class AMazeActivity extends AppCompatActivity {

    private static final String TAG = "AMazeActivity";

    Bundle basket = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Spinner driver = (Spinner) findViewById(R.id.spinner);
        driver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String item = (String) parentView.getItemAtPosition(position);
                String message = "Driver Selected: "+item;
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
                basket.putString("driver",item);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String message = "Default Driver Manual";
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
            }

        });

        Spinner maze = (Spinner) findViewById(R.id.spinner2);
        maze.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String item = (String) parentView.getItemAtPosition(position);
                String message = "Maze Generator Selected: "+item;
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
                basket.putString("maze",item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String message = "Default Generator DFS";
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
            }

        });

        Spinner skill  = (Spinner) findViewById(R.id.spinner3);
        skill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String item = (String) parentView.getItemAtPosition(position);
                String message = "Skill Level: "+item;
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
                basket.putString("skill",item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String message = "Default Skill 0";
                Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v(TAG,message);
            }

        });

        Button revisit = (Button) findViewById(R.id.revisit);
        revisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AMazeActivity.this,"You clicked Explore button",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(view.getContext(), GeneratingActivity.class);
                basket.putBoolean("revisit",true);
                myIntent.putExtras(basket);
                startActivity(myIntent);
            }
        });


        Button explore = (Button) findViewById(R.id.button);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AMazeActivity.this,"You clicked Explore button",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(view.getContext(), GeneratingActivity.class);
                basket.putBoolean("revisit",false);
                myIntent.putExtras(basket);
                startActivity(myIntent);
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

    public void buttonClickHandler(View view) {
        String message = "You clicked Explore button";
        Toast.makeText(AMazeActivity.this,message,Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(view.getContext(), GeneratingActivity.class);
        startActivity(myIntent);
        Log.v(TAG,message);

    }

    public void revisitClickHandler(View view) {
        String message = "You clicked REVISIT button";
        Toast.makeText(AMazeActivity.this,message,Toast.LENGTH_SHORT).show();
        Log.v(TAG,message);

    }
}
