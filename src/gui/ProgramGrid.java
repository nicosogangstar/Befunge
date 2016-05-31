package gui;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;

public class ProgramGrid {

	int xdim, ydim, zdim;
	float sidelength;
	String program;

	// Although only one string is used for input, it can be broken down based on the other dimensions.
	public ProgramGrid(int xdim, int ydim, int zdim, float sidelength, String program) {
		this.xdim = xdim;
		this.ydim = ydim;
		this.zdim = zdim;
		this.sidelength = sidelength;
		this.program = program;
	}

	private TransformGroup drawOrthogonalLine(int plane, float pos1, float pos2, Appearance a){
		int linesize = 5;
		// plane 1=XY, plane 2=YZ, plane 3=XZ
		// Create a new Transform Group and apply a transformation
		TransformGroup nodeTrans = new TransformGroup();
		
		LineArray l = new LineArray(2,LineArray.COORDINATES);
		Point3f[] p = new Point3f[2];
		switch(plane){
		case 0:
			p[0] = new Point3f(pos1 * sidelength, pos2 * sidelength, 0);
			break;
		case 1:
			p[0] = new Point3f(0, pos1 * sidelength, pos2 * sidelength);
			break;
		case 2:
			p[0] = new Point3f(pos1 * sidelength, 0, pos2 * sidelength);
			break;
		}

		// Add the new cube to the group, and the new group to the root
		switch(plane){
		case 0:
			p[1] =  new Point3f(pos1 * sidelength + linesize, pos2 * sidelength + linesize, sidelength * (float)zdim);
			break;
		case 1:
			p[1] = new Point3f(sidelength * (float)xdim, pos1 * sidelength + linesize, pos2 * sidelength + linesize);
			break;
		case 2:
			p[1] = new Point3f(pos1 * sidelength + linesize, sidelength * (float)ydim, pos2 * sidelength + linesize);
			break;
		}
		l.setCoordinates(0, p);
		
		nodeTrans.addChild(new Shape3D(l,a));
		return nodeTrans;
	}
	
	public TransformGroup highlightBox(int x, int y, int z, Color3f color) {

		// Check to make sure that the coordinates are in the prism
		if(x > xdim-1 || y > ydim-1 || z > zdim-1 || x < 0 || y < 0 || z < 0) {
			System.out.println("Cannot highlight box (invalid coordinates)");
			return null;
		}

		// Initialize the transform group
		TransformGroup transformGroup = new TransformGroup();

		// Set up coloring
		ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);

		// Set up appearance
		Appearance ap = new Appearance();
		ap.setColoringAttributes(ca);

		// Set up box
		Box b = new Box(.1f, .1f, .1f, ap);

		// Set offset based on input
		Vector3f vector = new Vector3f(
				sidelength / 2 + sidelength * x,
				sidelength / 2 + sidelength * y,
				sidelength / 2 + sidelength * z);

		// Add offset
		Transform3D transform = new Transform3D();
		transform.setTranslation(vector);

		// Add the transform to the new group
		transformGroup.setTransform(transform);

		// Add box
		transformGroup.addChild(b);
		return transformGroup;
	}

	public BranchGroup getBranchGroup() {
		// Create the root node of the content branch
		BranchGroup nodeRoot = new BranchGroup();

		// Render as a wireframe
		Appearance ap = new Appearance();
		PolygonAttributes polyAttrbutes = new PolygonAttributes();
		polyAttrbutes.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(polyAttrbutes);

		// Given the dimensions, draw lines which go perpendicular to the planes

		// XY plane
		for(int x = 0; x <= xdim; x++) {
			for(int y = 0; y <= ydim; y++) {
				nodeRoot.addChild(drawOrthogonalLine(0, x, y, ap));
			}
		}

		// YZ plane
		for(int y = 0; y <= ydim; y++) {
			for(int z = 0; z <= zdim; z++) {
				nodeRoot.addChild(drawOrthogonalLine(1, y, z, ap));
			}
		}

		// XZ plane
		for(int x = 0; x <= xdim; x++) {
			for(int z = 0; z <= zdim; z++) {
				nodeRoot.addChild(drawOrthogonalLine(2, x, z, ap));
			}
		}
		
		// 0,0,0 is the farthest bottom left corner
		nodeRoot.addChild(highlightBox(0, 0, 0, new Color3f(0.0f, 0.0f, 1.0f)));

		// Compile to perform optimizations on this content branch.
		nodeRoot.compile();

		return nodeRoot;
	}

}
