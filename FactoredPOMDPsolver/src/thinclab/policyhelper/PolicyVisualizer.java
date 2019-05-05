package thinclab.policyhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.CenterEdgeArrowRenderingSupport;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class PolicyVisualizer {
	
	private PolicyGraph policyGraph;
	
	public PolicyVisualizer(PolicyGraph policyGraph) {
		
		this.policyGraph = policyGraph;

		SpringLayout<PolicyNode, PolicyEdge> layout = 
				new SpringLayout<PolicyNode, PolicyEdge>(this.policyGraph.prettyPolicyGraph);
		CircleLayout<PolicyNode, PolicyEdge> circleLayout = 
				new CircleLayout<PolicyNode, PolicyEdge>(this.policyGraph.prettyPolicyGraph);
		
//		System.out.println(layout.getForceMultiplier());
//		System.out.println(layout.getRepulsionRange());
//		System.out.println(layout.getStretch());
//		System.out.println(layout.getSize());
//		
//		layout.setForceMultiplier((double) 0.1);
//		layout.setRepulsionRange(10000);
//		layout.setStretch((double) 1.0);

		VisualizationViewer<PolicyNode, PolicyEdge> vv = 
				new VisualizationViewer<PolicyNode, PolicyEdge>(circleLayout, new Dimension(1000, 1000));

		// Define transformer to print Node
		Transformer<PolicyNode, String> nodeTransformer = new Transformer<PolicyNode, String>() {
			@Override
			public String transform(PolicyNode node) {
				return "<html> ACTION: " + node.actName + node.getBeliefLabel();
			}
		};

		// Add Transformer to visualizer
		vv.getRenderContext().setVertexLabelTransformer(nodeTransformer);
		
		// Define transformer to print edge
		Transformer<PolicyEdge, String>transformer = new Transformer<PolicyEdge, String>() {
			@Override
			public String transform(PolicyEdge polEdge) {
				return "OBS: " + polEdge.observation;
			}
		};

		// Add Transformer to visualizer
		vv.getRenderContext().setEdgeLabelTransformer(transformer);
		
		// Define Shape transformer
		Transformer<PolicyNode, Shape> shapeTransformer = new Transformer<PolicyNode, Shape>() {
			@Override
			public Shape transform(PolicyNode node) {
				Ellipse2D.Double rNode = new Ellipse2D.Double(-50, -25, 100, 50);
				return rNode;
			}
		};
		vv.getRenderContext().setVertexShapeTransformer(shapeTransformer);
		vv.getRenderer().getEdgeRenderer().setEdgeArrowRenderingSupport(new CenterEdgeArrowRenderingSupport<PolicyNode, PolicyEdge>());
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
