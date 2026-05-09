package kutukan.view.gui;

import kutukan.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * Panel layar hasil pertarungan.
 *
 * Menampilkan tiga kondisi:
 * 1. STAGE CLEAR   — player menang, stage belum selesai semua
 * 2. GAME COMPLETE — player menang, semua stage sudah ditaklukkan
 * 3. GAME OVER     — player kalah
 *
 * Method refresh() dipanggil dari MainFrame setiap kali layar ini ditampilkan.
 */
public class ResultPanel extends JPanel {

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Runnable onContinue;    // lanjut ke stage select
    private Runnable onMainMenu;    // kembali ke main menu

    // ── Komponen ──────────────────────────────────────────────────────────────
    private JLabel   titleLabel;
    private JLabel   subtitleLabel;
    private JTextArea infoArea;
    private JLabel   playerInfoLabel;
    private JButton  continueBtn;
    private JButton  menuBtn;

    // ── Warna tema ─────────────────────────────────────────────────────────────
    private static final Color BG   = new Color(15, 15, 25);
    private static final Color GOLD = new Color(220, 180, 50);

    // ── Constructor ────────────────────────────────────────────────────────────
    public ResultPanel() {
        initUI();
    }

    // ── Inisialisasi UI ────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.gridx     = 0;
        gbc.insets    = new Insets(10, 60, 10, 60);

        // ① Judul besar (STAGE CLEAR! / GAME OVER)
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 46));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // ② Subjudul
        subtitleLabel = new JLabel("", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // ③ Kotak info reward / pesan kalah
        infoArea = new JTextArea(4, 30);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setBackground(new Color(28, 28, 45));
        infoArea.setForeground(new Color(200, 200, 200));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 85)));
        gbc.gridy = 2;
        add(infoScroll, gbc);

        // ④ Info player (level, gold)
        playerInfoLabel = new JLabel("", SwingConstants.CENTER);
        playerInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        playerInfoLabel.setForeground(new Color(160, 140, 200));
        gbc.gridy = 3;
        add(playerInfoLabel, gbc);

        // ⑤ Tombol Continue
        continueBtn = mkBtn("▶   Continue", new Color(45, 95, 55));
        continueBtn.addActionListener(e -> { if (onContinue != null) onContinue.run(); });
        gbc.gridy   = 4;
        gbc.gridwidth = 1;
        gbc.insets  = new Insets(14, 60, 10, 6);
        add(continueBtn, gbc);

        // ⑥ Tombol Main Menu
        menuBtn = mkBtn("⌂   Main Menu", new Color(55, 55, 75));
        menuBtn.addActionListener(e -> { if (onMainMenu != null) onMainMenu.run(); });
        gbc.gridx   = 1;
        gbc.insets  = new Insets(14, 6, 10, 60);
        add(menuBtn, gbc);
    }

    // ── Refresh ────────────────────────────────────────────────────────────────
    /**
     * Memperbarui seluruh tampilan sesuai kondisi hasil pertarungan.
     *
     * @param victory        true = player menang
     * @param reward         hadiah yang diterima (boleh null)
     * @param player         data player saat ini (boleh null)
     * @param isGameComplete true jika semua stage sudah selesai
     */
    public void refresh(boolean victory, Reward reward, Player player, boolean isGameComplete) {
        if (victory) {
            if (isGameComplete) {
                // ─ GAME COMPLETE ──────────────────────────────────────────────
                titleLabel.setText("🎉  GAME COMPLETE!");
                titleLabel.setForeground(GOLD);
                subtitleLabel.setText("Kamu telah menaklukkan semua rintangan!");
                infoArea.setText(
                    "Selamat!\n\n" +
                    "Kutukan telah terangkat. Dunia kembali damai berkat keberanianmu.\n\n" +
                    "Terima kasih telah bermain RPG Adventure — Kutukan!");
                continueBtn.setVisible(false);
            } else {
                // ─ STAGE CLEAR ────────────────────────────────────────────────
                titleLabel.setText("🏆  STAGE CLEAR!");
                titleLabel.setForeground(new Color(80, 200, 80));
                subtitleLabel.setText("Kerja bagus, petarung!");
                if (reward != null) {
                    infoArea.setText("Hadiah yang diterima:\n\n" + reward.toString());
                } else {
                    infoArea.setText("Stage berhasil diselesaikan!");
                }
                continueBtn.setVisible(true);
            }
        } else {
            // ─ GAME OVER ──────────────────────────────────────────────────────
            titleLabel.setText("💀  GAME OVER");
            titleLabel.setForeground(new Color(200, 60, 60));
            subtitleLabel.setText("Kamu telah tumbang dalam pertempuran...");
            infoArea.setText(
                "Perjalananmu berakhir di sini.\n\n" +
                "Tapi legenda seorang pejuang tidak pernah padam.\n\n" +
                "Bangkit dan coba lagi!");
            continueBtn.setVisible(false);
        }

        // Update info player
        if (player != null) {
            playerInfoLabel.setText(String.format(
                "<html><center>%s  |  Level %d  |  %d Gold</center></html>",
                player.getName(), player.getLevel(), player.getGold()));
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────────
    private JButton mkBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 48));
        return btn;
    }

    // ── Setter callback ────────────────────────────────────────────────────────
    public void setOnContinue(Runnable cb) { this.onContinue = cb; }
    public void setOnMainMenu(Runnable cb) { this.onMainMenu = cb; }
}