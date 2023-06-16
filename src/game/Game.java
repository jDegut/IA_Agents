package game;

import ia.Agent;
import ia.CommunicativeAgent;
import ia.NaiveAgent;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game extends JFrame {

    private static final List<Agent> AGENTS = List.of(
//            new NaiveAgent("A", 1, 3),
//            new NaiveAgent("B", 3, 3),
//            new NaiveAgent("C", 0, 0),
//            new NaiveAgent("D", 4, 1)
            new CommunicativeAgent("A", 0, 4),
            new CommunicativeAgent("B", 2, 0),
            new CommunicativeAgent("C", 2, 2),
            new CommunicativeAgent("D", 4, 2),
            new CommunicativeAgent("E", 0, 3)
    );

    private static final List<Box> BOXES = List.of(
            new Box(0, 2),
            new Box(1, 0),
            new Box(1, 3),
            new Box(1, 1),
            new Box(3, 2)
    );
    private static final int CELL_SIZE = 100;

    private final Board board;
    private JPanel gridPanel;

    private final ExecutorService executor;

    public Game() {
        this.board = new Board(this);
        executor = Executors.newFixedThreadPool(4);
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
                for(Agent agent : AGENTS) {
                    if(agent.getXFinal() == j && agent.getYFinal() == i) {
                        cellPanel.setBackground(getLabelColor(agent));
                    }
                }
                gridPanel.add(cellPanel);
            }
        }
        getContentPane().add(gridPanel);
    }

    public synchronized  void updateAgents() {
        List<Agent> agents = board.getAgents();

        // Clear grid cells
        for (Component component : gridPanel.getComponents()) {
            if (component instanceof JPanel cellPanel) {
                SwingUtilities.invokeLater(cellPanel::removeAll);
            }
        }

        // Add AGENT_1s to the grid
        for (Agent agent : agents) {
            Box position = board.getPosition(agent);
            if (position != null) {
                int cellIndex = position.getX() + position.getY() * Board.BOARD_HEIGHT;
                JPanel cellPanel = (JPanel) gridPanel.getComponent(cellIndex);
                JLabel agentLabel = new JLabel(agent.getName());
                // Vérifie si l'agent1 est sur sa case finale
                if (position.getX() == agent.getXFinal() && position.getY() == agent.getYFinal())
                    agentLabel.setForeground(Color.BLACK); // Change la couleur du texte en noir
                else
                    agentLabel.setForeground(getLabelColor(agent));

                agentLabel.setFont(agentLabel.getFont().deriveFont(16f));
                cellPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                cellPanel.add(agentLabel, gbc);

                SwingUtilities.invokeLater(() -> cellPanel.add(agentLabel));
            }
        }


        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    private Color getLabelColor(Agent agent) {
        List<Color> colors = List.of(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.DARK_GRAY, Color.ORANGE, Color.PINK);
        return colors.get(agent.getId() % colors.size());
    }

    public void start() {
        for(Agent agent : AGENTS) {
            agent.setListener(board);
            board.addAgent(agent, BOXES.get(agent.getId()));
            System.out.println(agent.getName() + " have to go to " + agent.getXFinal() + ", " + agent.getYFinal());
        }

        for(Agent agent : AGENTS) {
            executor.execute(agent);
        }
    }
}
