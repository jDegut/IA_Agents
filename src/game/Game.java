package game;

import ia.Agent;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game extends JFrame {
    private static final int CELL_SIZE = 100;

    private final Board board;
    private JPanel gridPanel;

    private final ExecutorService executor;

    public Game() {
        this.board = new Board(this);
        executor = Executors.newFixedThreadPool(System.getenv("NUMBER_OF_PROCESSORS").length());
        setTitle("Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
        start();
        updateAgents();
    }

    private void initComponents() {
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(Board.BOARD_SIZE, Board.BOARD_HEIGHT));

        // Create grid cells
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_HEIGHT; j++) {
                JPanel cellPanel = new JPanel();
                cellPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cellPanel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                gridPanel.add(cellPanel);
            }
        }

        getContentPane().add(gridPanel);
    }

    public void updateAgents() {
        List<Agent> agents = board.getAgents();

        // Clear grid cells
        for (Component component : gridPanel.getComponents()) {
            if (component instanceof JPanel cellPanel) {
                cellPanel.removeAll();
            }
        }

        // Add agents to the grid
        for (Agent agent : agents) {
            Box position = board.getPosition(agent);
            if (position != null) {
                JPanel cellPanel = (JPanel) gridPanel.getComponent(position.getX() + position.getY() * Board.BOARD_HEIGHT);
                JLabel agentLabel = new JLabel(agent.getName());
                cellPanel.add(agentLabel);
            }
        }
        revalidate();
        repaint();
    }

    public void start() {
        Agent a = new Agent("A", 0, 0);
        Agent b = new Agent("B", 4, 0);
        a.setListener(board);
        b.setListener(board);
        board.addAgent(a, new Box(3, 0));
        board.addAgent(b, new Box(1, 0));

        for(Agent agent : board.getAgents()) {
            executor.execute(agent);
        }
    }
}
