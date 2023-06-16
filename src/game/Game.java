package game;

import ia.Agent;
import ia.Agent1;
import ia.Agent2;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game extends JFrame {

    private static final List<Agent> AGENTS = List.of(
//            new Agent1("A", 0, 0),
//            new Agent1("B", 0, 4)
            new Agent2("A", 0, 0),
            new Agent2("B", 0, 4)
    );

    private static final List<Box> BOXES = List.of(
            new Box(0, 4),
            new Box(0, 0)
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
        List<Color> colors = List.of(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN);
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
