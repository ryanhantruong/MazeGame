package hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad;



import hanscott.cs301.cs.wm.edu.amazebyhanscott.falstad.Constants.StateGUI;

/**
 * This is a default implementation of the Viewer interface
 * with methods that do nothing but providing debugging output
 * such that subclasses of this class can selectively overwrite
 * those methods that are truly needed.
 * 
 * TODO: use logger instead of Sys.out
 * 
 * @author Kemper
 *
 */
public class DefaultViewer implements Viewer {

	@Override
	public void redraw(Object gc, StateGUI state, int px, int py,
			int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		dbg("redraw") ;
	}
	
	@Override
	public void redraw(Object gc, StateGUI state, float energyConsumed, int pathLength, boolean win)  {
		dbg("redraw") ;
	}

	@Override
	public void incrementMapScale() {
		dbg("incrementMapScale") ;
	}

	@Override
	public void decrementMapScale() {
		dbg("decrementMapScale") ;
	}


	private void dbg(String str) {
		//System.out.println("DefaultViewer" + str);
	}
}
