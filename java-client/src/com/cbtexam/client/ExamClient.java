package com.cbtexam.client;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

/**
 * CBT Exam Client - locked-down kiosk window.
 *
 * Runs the exam inside an embedded JavaFX WebView rather than the system
 * browser, so the window has no title bar, no minimize/maximize/close
 * buttons, and no address bar - identical in spirit to the dedicated
 * "Java Client" kiosk software used at real CBT exam centres.
 *
 * Exit is only possible via a hidden invigilator shortcut (Ctrl+Alt+F12),
 * never by the candidate.
 */
public class ExamClient extends JFrame {

    private static final String DEFAULT_SERVER_IP = "192.168.138.235";
    private static final String DEFAULT_SERVER_PORT = "8080";
    private static final String PREF_KEY_IP = "serverIp";
    private static final String PREF_KEY_PORT = "serverPort";

    private final Preferences prefs = Preferences.userNodeForPackage(ExamClient.class);
    private JTextField ipField;
    private JTextField portField;
    private JLabel statusLabel;
    private JFXPanel jfxPanel;
    private WebEngine webEngine;

    public ExamClient() {
        super("CBT Exam Login");
        buildLoginUi();
    }

    // ---------------------------------------------------------------
    // Step 1: IP-entry screen (still exit-able by the operator setting
    // things up before candidates arrive - Esc works here only).
    // ---------------------------------------------------------------
    private void buildLoginUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(new Color(0x1b2a4a));
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 320));
        card.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JLabel title = new JLabel("Computer Based Test");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(0x1b2a4a));
        gbc.gridy = 0;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Exam Client");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(subtitle, gbc);

        JLabel ipLabel = new JLabel("Server IP Address");
        ipLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 2, 0);
        card.add(ipLabel, gbc);

        ipField = new JTextField(prefs.get(PREF_KEY_IP, DEFAULT_SERVER_IP));
        ipField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(ipField, gbc);

        JLabel portLabel = new JLabel("Port");
        portLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 2, 0);
        card.add(portLabel, gbc);

        portField = new JTextField(prefs.get(PREF_KEY_PORT, DEFAULT_SERVER_PORT));
        portField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(portField, gbc);

        JButton connectBtn = new JButton("Connect to Exam Server");
        connectBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        connectBtn.setBackground(new Color(0x1b2a4a));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setFocusPainted(false);
        connectBtn.setPreferredSize(new Dimension(0, 42));
        connectBtn.addActionListener(this::onConnect);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(connectBtn, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0xc62828));
        gbc.gridy = 7;
        card.add(statusLabel, gbc);

        add(card);

        getRootPane().registerKeyboardAction(
                e -> System.exit(0),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void onConnect(ActionEvent e) {
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();

        if (ip.isEmpty() || port.isEmpty()) {
            statusLabel.setText("Please enter both IP address and port.");
            return;
        }

        prefs.put(PREF_KEY_IP, ip);
        prefs.put(PREF_KEY_PORT, port);

        String url = "http://" + ip + ":" + port + "/login.jsp";
        switchToExamView(url);
    }

    // ---------------------------------------------------------------
    // Step 2: locked-down exam view - embedded browser, no window
    // chrome, keyboard lockdown, auto-refocus if focus is lost.
    // ---------------------------------------------------------------
    private void switchToExamView(String url) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webEngine = webView.getEngine();

            StackPane root = new StackPane(webView);
            Scene scene = new Scene(root);
            webView.prefWidthProperty().bind(root.widthProperty());
            webView.prefHeightProperty().bind(root.heightProperty());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                System.out.println("[ExamClient] WebView load state: " + newState);
            });
            webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldEx, newEx) -> {
                if (newEx != null) {
                    System.out.println("[ExamClient] WebView load error: " + newEx);
                    newEx.printStackTrace();
                }
            });

            jfxPanel.setScene(scene);
            System.out.println("[ExamClient] Loading URL: " + url);
            webEngine.load(url);
        });

        getRootPane().registerKeyboardAction(
                e -> { /* Esc does nothing once the exam has started */ },
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        installProctorExitShortcut();
        installFocusGuard();

        revalidate();
        repaint();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        toFront();
        requestFocus();
    }

    /** Ctrl+Alt+F12: the only way out, intended for the invigilator only. */
    private void installProctorExitShortcut() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED
                    && e.getKeyCode() == KeyEvent.VK_F12
                    && e.isControlDown()
                    && e.isAltDown()) {
                System.exit(0);
            }
            return false;
        });
    }

    /**
     * Best-effort lockdown: if the window ever loses focus (Alt+Tab, Win key,
     * clicking another app) or stops being topmost, snap it back within a
     * fraction of a second. This cannot be made 100% unbreakable without a
     * kernel-level hook, but it makes leaving the exam window impractical.
     */
    private void installFocusGuard() {
        Timer guard = new Timer(150, e -> {
            if (!isActive() || !isFocused()) {
                SwingUtilities.invokeLater(() -> {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    setAlwaysOnTop(true);
                    toFront();
                    requestFocus();
                });
            }
        });
        guard.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamClient().setVisible(true));
    }
}
