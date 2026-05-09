package kutukan.view.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Panel layar utama (title screen).
 *
 * Menampilkan:
 * - Judul game
 * - Input nama player
 * - Tombol New Game dan Quit
 *
 * Callback disuntikkan dari luar (MainFrame) sehingga panel ini
 * tidak perlu tahu apapun tentang GameManager secara langsung.
 */
public class MainMenuPanel extends JPanel {

    // ── Callbacks ────────────────────────────────────────────────────────────
    private Consumer<String> onNewGame; // menerima nama player
    private Runnable         onQuit;

    // ── Komponen ──────────────────────────────────────────────────────────────
    private JTextField nameField;

    // ── Warna tema ────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(18, 18, 30);
    private static final Color GOLD        = new Color(220, 180, 50);
    private static final Color PURPLE_MUTE = new Color(150, 120, 200);
    private static final Color FIELD_BG    = new Color(35, 35, 55);
    private static final Color FIELD_BORD  = new Color(100, 80, 180);

    // ── Constructor ───────────────────────────────────────────────────────────
    public MainMenuPanel() {
        initUI();
    }

    // ── Inisialisasi UI ───────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets    = new Insets(10, 30, 10, 30);
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridx     = 0;

        // ① Judul utama
        JLabel titleLabel = new JLabel("⚔  RPG Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 50));
        titleLabel.setForeground(GOLD);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // ② Subtitle / nama game
        JLabel subLabel = new JLabel("Kutukan", SwingConstants.CENTER);
        subLabel.setFont(new Font("Serif", Font.ITALIC, 24));
        subLabel.setForeground(PURPLE_MUTE);
        gbc.gridy = 1;
        add(subLabel, gbc);

        // ③ Separator tipis
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 90));
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 80, 4, 80);
        add(sep, gbc);
        gbc.insets = new Insets(10, 30, 10, 30);

        // ④ Label nama
        JLabel nameLabel = new JLabel("Masukkan Nama Karakter:", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 3;
        add(nameLabel, gbc);

        // ⑤ Text field nama
        nameField = new JTextField("Hero", 18);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(FIELD_BG);
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORD, 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        gbc.gridy = 4;
        add(nameField, gbc);

        // ⑥ Tombol New Game
        JButton newGameBtn = createStyledBtn("▶   New Game", new Color(50, 110, 65));
        newGameBtn.addActionListener(e -> {
            if (onNewGame != null) onNewGame.accept(nameField.getText().trim());
        });
        // Juga bisa tekan Enter di nameField
        nameField.addActionListener(e -> newGameBtn.doClick());
        gbc.gridy = 5;
        gbc.insets = new Insets(14, 80, 6, 80);
        add(newGameBtn, gbc);

        // ⑦ Tombol Quit
        JButton quitBtn = createStyledBtn("✖   Quit", new Color(110, 35, 35));
        quitBtn.addActionListener(e -> { if (onQuit != null) onQuit.run(); });
        gbc.gridy = 6;
        gbc.insets = new Insets(6, 80, 10, 80);
        add(quitBtn, gbc);

        // ⑧ Versi
        JLabel verLabel = new JLabel("v1.0  |  Turn-Based RPG", SwingConstants.CENTER);
        verLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        verLabel.setForeground(new Color(70, 70, 90));
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 30, 10, 30);
        add(verLabel, gbc);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private JButton createStyledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 17));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(240, 48));
        return btn;
    }

    // ── Setter callback ───────────────────────────────────────────────────────
    public void setOnNewGame(Consumer<String> cb) { this.onNewGame = cb; }
    public void setOnQuit(Runnable cb)             { this.onQuit    = cb; }
}
