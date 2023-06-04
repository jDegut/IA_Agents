package game;

import ia.Agent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game extends JFrame {
    private static final int CELL_SIZE = 100;

    private final Board board;
    private JPanel gridPanel;

    private final ExecutorService executor;

    public Game() {
        this.board = new Board(this);
        executor = Executors.newFixedThreadPool(3);
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

    public synchronized  void updateAgents() {
        List<Agent> agents = board.getAgents();

        // Clear grid cells
        for (Component component : gridPanel.getComponents()) {
            if (component instanceof JPanel cellPanel) {
                SwingUtilities.invokeLater(cellPanel::removeAll);
                cellPanel.setBackground(Color.WHITE); // Réinitialise la couleur de fond de toutes les cellules à blanc
            }
        }

        // Add agents to the grid
        for (Agent agent : agents) {
            Box position = board.getPosition(agent);
            if (position != null) {
                int cellIndex = position.getX() + position.getY() * Board.BOARD_HEIGHT;
                JPanel cellPanel = (JPanel) gridPanel.getComponent(cellIndex);
                JLabel agentLabel = new JLabel(agent.getName());

                // Vérifie si l'agent est sur sa case finale
                if (position.getX() == agent.getXFinal() && position.getY() == agent.getYFinal()) {
                    cellPanel.setBackground(getLabelColor(agent));
                    agentLabel.setForeground(Color.BLACK); // Change la couleur du texte en noir
                }
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
        List<Agent> agents = List.of(
                new Agent("A", 0, 0),
                new Agent("B", 0, 1),
                new Agent("C", 0, 2)
        );

        List<Box> boxes = List.of(
                new Box(4, 4),
                new Box(2, 1),
                new Box(4, 0)
        );
        for(Agent agent : agents) {
            agent.setListener(board);
            board.addAgent(agent, boxes.get(agent.getId()));
            System.out.println(agent.getName() + " have to go to " + agent.getXFinal() + ", " + agent.getYFinal());
        }

        for(Agent agent : agents) {
            executor.execute(agent);
        }
    }
}
