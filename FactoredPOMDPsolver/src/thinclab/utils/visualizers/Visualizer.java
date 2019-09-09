package thinclab.utils.visualizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedKAryTree;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.renderers.CenterEdgeArrowRenderingSupport;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

import thinclab.symbolicperseus.DD;

public class Visualizer {
	
	/*
	 * JUNG visualizer which uses VizGraph object for rendering graphs
	 */

	public Visualizer(VizGraph vizGraph) {
		
		FRLayout<VizNode, VizEdge> layout = new FRLayout<VizNode, VizEdge>(vizGraph.graph); 

		VisualizationViewer<VizNode, VizEdge> vv = 
				new VisualizationViewer<VizNode, VizEdge>(layout,
				new Dimension(1000, 1000));

		// --------------------------------------------------------------------
		/*
		 *  Define transformer to print Node
		 */
		Transformer<VizNode, String> nodeTransformer = new Transformer<VizNode, String>() {
			
			@Override
			public String transform(VizNode node) {
				return "<html>" + node.data;
			}
		};

		/*
		 *  Add Transformer to visualizer
		 */
		vv.getRenderContext().setVertexLabelTransformer(nodeTransformer);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setLabelOffset(-1); 
		
		// ----------------------------------------------------------------------
		/*
		 * Define transformer to print edge
		 */
		Transformer<VizEdge, String> transformer = new Transformer<VizEdge, String>() {
			@Override
			public String transform(VizEdge edge) {
				return edge.label;
			}
		};

		/*
		 * Add Transformer to visualizer
		 */
		vv.getRenderContext().setEdgeLabelTransformer(transformer);

		// -------------------------------------------------------------------
		/*
		 * Define Shape transformer
		 */
		
		Transformer<VizNode, Shape> vertexShapeTransformer = new Transformer<VizNode, Shape>() {
			
			@Override
			public Shape transform(VizNode node) {
				
				return 
						new Rectangle2D.Float(
								-4 * node.width, 
								-12 * node.height, 
								node.width * 8, 
								node.height * 24);
			}
		};

		vv.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer);

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		/*
		 * Font Transformer
		 */
		Transformer<VizNode, Font> vertexFont = new Transformer<VizNode, Font>() {
			public Font transform(VizNode node) {
				Font font = new Font("Calibri", Font.PLAIN, 15);
				return font;
			}
		};
		
		Transformer<VizEdge, Font> edgeFont = new Transformer<VizEdge, Font>() {
			public Font transform(VizEdge edge) {
				Font font = new Font("Mono", Font.BOLD, 14);
				return font;
			}
		};

		vv.getRenderContext().setVertexFontTransformer(vertexFont);
		vv.getRenderContext().setEdgeFontTransformer(edgeFont);

		// --------------------------------------------------------------------
		/*
		 * Color Transformer
		 */
		Transformer<VizNode, Paint> vertexPaint = new Transformer<VizNode, Paint>() {
			public Paint transform(VizNode node) {
				return Color.GRAY;
			}
		};

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
		// --------------------------------------------------------------------
		/*
		 * Edge Stroke Transformer
		 */
		
		Transformer<VizEdge, Stroke> edgeStroke = new Transformer<VizEdge, Stroke>() {

		    public Stroke transform(VizEdge s) {

		    	return new BasicStroke(new Float(0.5));
		    }
		};
		
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);
		
		// --------------------------------------------------------------------
		/*
		 * The following code adds capability for mouse picking of vertices/edges. 
		 * Vertices can even be moved!
		 */
		final DefaultModalGraphMouse<String, Number> graphMouse = 
				new DefaultModalGraphMouse<String, Number>();
		
		vv.setGraphMouse(graphMouse);
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		JFrame frame = new JFrame();
		frame.getContentPane().add(vv);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

} // public class PolicyVisualizer
