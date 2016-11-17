package cluster;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClusteringTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static ClusteringTest ct;

	private int NUM_CLUSTERS = 0;
	private int NUM_POINTS = 50;

	private static final int MIN_COORDINATE = 0;
	private static final int MAX_COORDINATE = 800;

	private ArrayList<Point> points;
	private ArrayList<Cluster> clusters;

	JPanel jp1 = new JPanel();
	PrintCanvas canvas = new PrintCanvas();
	JTextField clusterNum = new JTextField();
	JButton clusterNumBt = new JButton("Run");

	public ClusteringTest() {
		// TODO Auto-generated constructor stub

		this.points = new ArrayList<Point>();
		this.clusters = new ArrayList<Cluster>();
		uiCreate();
	}

	public void uiCreate() {
		JLabel la1 = new JLabel("ClusterNum");

		setTitle("Clustering");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		canvas.setSize(1000, 900);
		add("Center", canvas);

		clusterNum.setColumns(20);
		clusterNumBt.addActionListener(new ButtonListener());

		jp1.setLayout(new FlowLayout());
		jp1.setSize(1000, 100);
		jp1.add(la1);
		jp1.add(clusterNum);
		jp1.add(clusterNumBt);
		add("South", jp1);

		setSize(1000, 1000);
		setResizable(false);
		setVisible(true);
	}

	public void init() {
		points = Point.createRandomPoints(MIN_COORDINATE, MAX_COORDINATE, NUM_POINTS);

		for (int i = 0; i < NUM_CLUSTERS; i++) {
			Cluster cluster = new Cluster(i);
			Point centroid = Point.createRandomPoint(MIN_COORDINATE, MAX_COORDINATE);
			cluster.setCentroid(centroid);
			clusters.add(cluster);
		}

		for (Point point : points) {
			canvas.paint(canvas.getGraphics(), Color.blue, (int) point.getX(), (int) point.getY());
		}

		plotClusters();
	}

	private void plotClusters() {
		for (int i = 0; i < NUM_CLUSTERS; i++) {
			Cluster c = (Cluster) clusters.get(i);
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			Color color = new Color(r, g, b);
			for (Point point : c.getPoints()) {
				canvas.paint(canvas.getGraphics(), color, (int) point.getX(), (int) point.getY());
			}
			canvas.paint(canvas.getGraphics(), Color.RED, (int) c.getCentroid().getX(), (int) c.getCentroid().getY());

		}
	}

	// 클러스터계산
	public boolean calculate() {
		boolean finish = true;

		clearClusters();

		ArrayList<Point> lastCentroids = getCentroids();

		assignCluster();

		calculateCentroids();

		ArrayList<Point> currentCentroids = getCentroids();

		double distance = 0;
		for (int i = 0; i < lastCentroids.size(); i++) {
			distance += Point.distance((Point) lastCentroids.get(i), (Point) currentCentroids.get(i));
		}
		plotClusters();

		if (distance == 0) {
			finish = false;
		}
		return finish;

	}

	private void clearClusters() {
		for (Cluster cluster : clusters) {
			cluster.clear();
		}
	}

	private ArrayList<Point> getCentroids() {
		ArrayList<Point> centroids = new ArrayList<Point>(NUM_CLUSTERS);
		for (Cluster cluster : clusters) {
			Point aux = cluster.getCentroid();
			Point point = new Point(aux.getX(), aux.getY());
			centroids.add(point);
		}
		return centroids;
	}

	private void assignCluster() {
		double max = Double.MAX_VALUE;
		double min = max;
		int cluster = 0;
		double distance = 0.0;

		for (Point point : points) {
			min = max;
			for (int i = 0; i < NUM_CLUSTERS; i++) {
				Cluster c = clusters.get(i);
				distance = Point.distance(point, c.getCentroid());
				if (distance < min) {
					min = distance;
					cluster = i;
				}
			}
			point.setCluster(cluster);
			clusters.get(cluster).addPoint(point);
		}
	}

	private void calculateCentroids() {
		for (Cluster cluster : clusters) {
			double sumX = 0;
			double sumY = 0;
			ArrayList<Point> list = cluster.getPoints();
			int n_points = list.size();

			for (Point point : list) {
				sumX += point.getX();
				sumY += point.getY();
			}

			Point centroid = cluster.getCentroid();
			if (n_points > 0) {
				double newX = sumX / n_points;
				double newY = sumY / n_points;
				centroid.setX(newX);
				centroid.setY(newY);
			}
		}
	}

	class PrintCanvas extends Canvas {

		public void paint(Graphics g, Color color, int x, int y) {
			g.setColor(color);
			g.fillRect(x, y, 10, 10);
		}
	}

	public class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton source = (JButton) e.getSource();
			canvas.getGraphics().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());// 캔버스초기화
			if ("Run".equals(source.getText())) {
				if (clusterNum.getText() != null) {
					if (isNum(clusterNum.getText())) {
						NUM_CLUSTERS = Integer.parseInt(clusterNum.getText()); // 그룹개수입력
						source.setText("Next");
						ct.init();
					} else {
						JOptionPane.showMessageDialog(getContentPane(), "숫자가 아닙니다.", "경고", JOptionPane.WARNING_MESSAGE);
					}
				}
			} else if ("Next".equals(source.getText())) {
				boolean flag = ct.calculate();
				if (flag != true)
					source.setText("Run");
			} else {
				JOptionPane.showMessageDialog(getContentPane(), "값을 넣어주세요.", "경고", JOptionPane.WARNING_MESSAGE);
			}
		}

	}

	public static boolean isNum(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void main(String[] args) {

		ct = new ClusteringTest();
	}
}
