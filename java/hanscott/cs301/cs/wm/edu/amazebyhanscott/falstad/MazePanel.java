package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.*;
import android.graphics.*;
import java.lang.Runnable;


/*import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.RenderingHints;*/


/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 * 
 * @author pk
 *
 */
public class MazePanel extends View {
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	// bufferImage can only be initialized if the container is displayable,
	// uses a delayed initialization and relies on client class to call initBufferImage()
	// before first use
	//private Image bufferImage;
	//private Graphics2D graphics; // obtained from bufferImage,
	// graphics is stored to allow clients to draw on same graphics object repeatedly
	// has benefits if color settings should be remembered for subsequent drawing operations

	// Android graphics
	public Paint paint;
	//private static Canvas canVAS = new Canvas();

	public static Canvas canvas;
	public Bitmap bitmap;// = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
	private Rect rect;

	/**
	 * Constructor. Object is not focusable.
	 */
	/*
	public MazePanel() {
		// added for title screen manupilation
		///
		setFocusable(false);
		//bufferImage = null; // bufferImage initialized separately and later
		//graphics = null;
		// instantiate paint object for use with Canvas
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(Color.red(10));
		linePaint.setStrokeWidth(1f);
	}*/

	/**
	 * Constructor for Android. Object is not focusable.
	 */
	public MazePanel(Context context, AttributeSet attrs){
		super(context, attrs);
		//setFocusable(false);
		// instantiate paint object for use with Canvas
		init();
	}

	/**
	 * Constructor for Android. Object is not focusable.
	 */
	public MazePanel(Context context){
		super(context);
		//setFocusable(false);
		init();
	}

	/**
	 * Constructor for Android. Object is not focusable.
	 */
	public MazePanel(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		//setFocusable(false);
		// instantiate paint object for use with Canvas
		init();

	}
	private void init(){
		paint = new Paint();
		bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		//bitmap = Bitmap.createBitmap(190, 190, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		//setWillNotDraw(false);
	}


	private static final String TAG = "MazePanel";
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		writeLog("in panel - onDraw called");
		canvas.drawBitmap(bitmap, null, new Rect(0,0,canvas.getWidth(),canvas.getHeight()), paint);
	}

	private void writeLog(String msg){
		Log.v("MazeController",msg);
	}

	@Override
	public void onMeasure(int width, int height) {
		super.onMeasure(width, height);
		setMeasuredDimension(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
	}

	public Bitmap getBitMap() {
		return bitmap;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void setBitMap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public static Object getColor(String str){
		Object color = null;
		str = str.toLowerCase();
		switch (str){
		case "black":
			color = Color.BLACK;
			break;
		case "blue":
			color = Color.BLUE;
			break;
		case "white":
			color = Color.WHITE;
			break;
		case "red":
			color = Color.RED;
			break;
		case "yellow":
			color = Color.YELLOW;
			break;
		case "orange":
			color = Color.rgb(255,165,0);
			break;
		case "darkgray":
			color = Color.DKGRAY;
			break;
		case "gray":
			color = Color.GRAY;
			break;
		default:
			System.out.println("MazePanel.getColor cannot determine color");
			break;
		}
		return color;
	}
	
	public static Object getColor(int x, int y, int z){
		//Object color = new Color(x, y, z);
		return Color.rgb(x,y,z);
	}

	public void clearBitmap(){
		bitmap.eraseColor(Color.TRANSPARENT);
	}

	public static int getRGB(Object color){
		return Color.CYAN;
	}
	
	public static void setGraphicsColor(Object color, Object paint){
		int cl = (int) color;
		Paint p = (Paint) paint;
		p.setColor(cl);
	}
	
	public static void fillGraphicsRect(int a, int b, int c, int d, Object paint){
		Paint p = (Paint) paint;
		p.setStyle(Style.FILL);
		canvas.drawRect((float)a,(float)b,(float)c,(float)d,p);
	}
	
	public static void drawGraphicsLine(int a, int b, int c, int d, Object paint){
		//Canvas can = (Canvas) canvas;
		Paint p = (Paint) paint;
		canvas.drawLine(a,b,c,d,p);
	}
	
	public static void fillGraphicsPolygon(int[] a, int[] b, int c, Object paint){
		Paint p = (Paint) paint;
		p.setStyle(Style.FILL);

		Path polyPath = new Path();
		polyPath.moveTo(a[0],b[0]);
		for (int i =0; i <c; i++){
			polyPath.lineTo(a[i],b[i]);
		}
		polyPath.lineTo(a[0],b[0]);

		canvas.drawPath(polyPath,p);
	}


	
	public static void fillGraphicsOval(int a, int b, int c, int d, Object paint){
		//Graphics gc = (Graphics) graphic;
		//gc.fillOval(a,b,c,d);
		Paint p = (Paint) paint;
		p.setStyle(Style.FILL);
		float a1 = (float) a;
		float b1 = (float) b;
		float c1 = (float) c;
		float d1 = (float) d;
		canvas.drawOval(a1,b1,c1,d1,p);
	}
	

	/*
	@Override
	public void update(Graphics g) {
		paint(g); 
	}*/
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass. The method is used in the MazeController.
	 * Warning: do not override getGraphics() or drawing might fail. 
	 */
	public void update() {
		//paint(getGraphics());
		writeLog("in panel - about to invalidate");
		invalidate();
		writeLog("in panel - passed invalidate");
	}
	

	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 */
	/*
	@Override
	public void paint(Graphics g) {
		if (null == g) {
			System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
		}
		else {
			g.drawImage(bufferImage,0,0,null);	
		}
	}*/


	public void initBufferImage() {
		//bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		//if (null == bufferImage)
		//{
		//	System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
		//}
	}

	/**
	 * Obtains a graphics object that can be used for drawing.
	 * The object internally stores the graphics object and will return the
	 * same graphics object over multiple method calls. 
	 * To make the drawing visible on screen, one needs to trigger 
	 * a call of the paint method, which happens 
	 * when calling the update method. 
	 * @return graphics object to draw on, null if impossible to obtain image
	 */

	public Object getBufferGraphics() {
		/*if (null == graphics) {
			// instantiate and store a graphics object for later use
			if (null == bufferImage)
				initBufferImage();
			if (null == bufferImage)
				return null;
			graphics = (Graphics2D) bufferImage.getGraphics();
			if (null == graphics) {
				System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
			}
			// success case
			
			//System.out.println("MazePanel: Using Rendering Hint");
			// For drawing in FirstPersonDrawer, setting rendering hint
			// became necessary when lines of polygons 
			// that were not horizontal or vertical looked ragged
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		
		return graphics;*/
		return paint;
	}

}
