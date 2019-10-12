package thinclab.policyhelper;

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

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.renderers.CenterEdgeArrowRenderingSupport;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class PolicyVisualizer {

	private PolicyGraph policyGraph;

	public PolicyVisualizer(PolicyGraph policyGraph) {

		this.policyGraph = policyGraph;

//		SpringLayout<PolicyNode, PolicyEdge> layout = 
//				new SpringLayout<PolicyNode, PolicyEdge>(this.policyGraph.getGraph());
		FRLayout<PolicyNode, PolicyEdge> layout = 
				new FRLayout<PolicyNode, PolicyEdge>(this.policyGraph.getGraph());
		
		VisualizationViewer<PolicyNode, PolicyEdge> vv = 
				new VisualizationViewer<PolicyNode, PolicyEdge>(layout,
				new Dimension(1000, 1000));

		// --------------------------------------------------------------------
		// Define transformer to print Node
		Transformer<PolicyNode, String> nodeTransformer = new Transformer<PolicyNode, String>() {
			@Override
			public String transform(PolicyNode node) {
				if (node.startNode) {
					return "<html>" 
							+ node.actName 
							+ node.factoredBelief.toString();
				}
				
				else {
					return "<html>" + node.actName;// + node.getBeliefLabel();
				}
			}
		};

		// Add Transformer to visualizer
		vv.getRenderContext().setVertexLabelTransformer(nodeTransformer);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setLabelOffset(-1); 
		
		// ----------------------------------------------------------------------

		// Define transformer to print edge
		Transformer<PolicyEdge, String> transformer = new Transformer<PolicyEdge, String>() {
			@Override
			public String transform(PolicyEdge polEdge) {
				return "<html><bold>OBS: " + polEdge.observation + "</bold>";
			}
		};

		// Add Transformer to visualizer
		vv.getRenderContext().setEdgeLabelTransformer(transformer);

		// -------------------------------------------------------------------

		// Define Shape transformer

		Transformer<PolicyNode, Shape> vertexShapeTransformer = new Transformer<PolicyNode, Shape>() {
//			float size = 30;

			@Override
			public Shape transform(PolicyNode node) {
				
				if (node.startNode) {
					float height = (float) node.factoredBelief.size() * 20 + 40;
					return new Rectangle2D.Float(-110 , -1 * height / 2, 220, height);
				}
				
				else {
					float size = node.actName.length() * 5;
					return new Rectangle2D.Float(-1 * size, -15, size * 2, 30);
				}
			}
		};

		vv.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer);

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------

		// Font Transformer

		Transformer<PolicyNode, Font> vertexFont = new Transformer<PolicyNode, Font>() {
			public Font transform(PolicyNode node) {
				Font font = new Font("Calibri", Font.BOLD, 12);
				return font;
			}
		};
		
		Transformer<PolicyEdge, Font> edgeFont = new Transformer<PolicyEdge, Font>() {
			public Font transform(PolicyEdge edge) {
				Font font = new Font("Mono", Font.BOLD, 14);
				return font;
			}
		};

		vv.getRenderContext().setVertexFontTransformer(vertexFont);
		vv.getRenderContext().setEdgeFontTransformer(edgeFont);

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------

		// Color Transformer
		Transformer<PolicyNode, Paint> vertexPaint = new Transformer<PolicyNode, Paint>() {
			public Paint transform(PolicyNode node) {
				if (node.startNode)
					return Color.GREEN;
				else
					return Color.YELLOW;
			}
		};

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		
		// Edge Stroke Transformer
		
		Transformer<PolicyEdge, Stroke> edgeStroke = new Transformer<PolicyEdge, Stroke>() {
//		    float dash[] = { 10.0f };
		    public Stroke transform(PolicyEdge s) {
//		        return new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
//		                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		    	return new BasicStroke(new Float(0.5));
		    }
		};
		
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);
		
		// --------------------------------------------------------------------

//		vv.getRenderer().getEdgeRenderer()
//				.setEdgeArrowRenderingSupport(new CenterEdgeArrowRenderingSupport<PolicyNode, PolicyEdge>());
//		vv.getRenderContext().setArrowPlacementTolerance((float) 100.0);
//		vv.setVertexToolTipTransformer(vv.getRenderContext().getVertexLabelTransformer());

//		System.out.println(vv.getRenderContext().getArrowPlacementTolerance());
		// The following code adds capability for mouse picking of vertices/edges.
		// Vertices can even be moved!
		final DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<String, Number>();
		vv.setGraphMouse(graphMouse);
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		JFrame frame = new JFrame();
		frame.getContentPane().add(vv);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

//	static class MyRenderer implements Renderer.Vertex<PolicyNode, PolicyEdge> {
//		
//		@Override
//		public void paintVertex(RenderContext<PolicyNode, PolicyEdge> rc,
//								Layout<PolicyNode, PolicyEdge> layout,
//								PolicyNode vertex) {
//
//			GraphicsDecorator graphicsContext = rc.getGraphicsContext();
//			Point2D center = layout.transform(vertex);
//			Shape shape = shape = new Rectangle((int) center.getX() - 10, (int) center.getY() - 10, 100, 100);
//			Color color = new Color(127, 127, 0);
//			
//			graphicsContext.setPaint(color);
//			graphicsContext.fill(shape);
//		}
//	}
} // public class PolicyVisualizer
