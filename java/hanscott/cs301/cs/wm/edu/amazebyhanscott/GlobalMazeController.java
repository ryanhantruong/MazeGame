package hanscott.cs301.cs.wm.edu.amazebyhanscott;

import android.app.Application;

import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.MazeController;

/**
 * Created by Ryan on 12/3/17.
 */

public  class GlobalMazeController extends Application {

        private static MazeController mazeController;

        public static MazeController getController() {
            return mazeController;
        }

        public static void setController(MazeController controller) {
            mazeController= controller;
        }
}

