package kutukan.view.gui;

import kutukan.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel pemilihan stage.
 *
 * Menampilkan setiap stage sebagai "kartu" berisi:
 * - Nama stage dan nomor urut
 * - Level yang direkomendasikan
 * - Deskripsi singkat
 * - Tombol "Enter Stage" (berwarna berbeda jika player underleveled)
 *
 * Panel ini bisa di-refresh kapan saja (setelah player naik level, dll.)
 * dengan memanggil refresh(stages, player).
 */
public class StageSelectPanel extends JPanel {

    // ── Callbacks ──────────────────────────────────────────────────────────────
    private Consumer<Integer> onStageSelected;  // index stage yang dipilih
    private Runnable          onBack;

    // ── Komponen ───────────────────────────────────────────────────────────────
    private JLabel playerInfoLabel;
    private JPanel stageListPanel;

    // ── Warna tema ─────────────────────────────────────────────────────────────
    private static final Color BG       = new Color(18, 18, 30);
    private static final Color CARD_BG  = new Color(30, 30, 50);
    private static final Color CARD_BD  = new Color(70, 60, 110);
    private static final Color GOLD     = new Color(220, 180, 50);
    private static final Color LAVENDER = new Color(180, 160, 220);

    // ── Constructor ────────────────────────────────────────────────────────────
    public StageSelectPanel() {
        initUI();
    }

    // ── Inisialisasi UI ────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Header (atas) ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setBackground(BG);

        JLabel title = new JLabel("Pilih Stage", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(GOLD);
        header.add(title, BorderLayout.NORTH);

        playerInfoLabel = new JLabel("", SwingConstants.CENTER);
        playerInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        playerInfoLabel.setForeground(Color.LIGHT_GRAY);
        header.add(playerInfoLabel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ── Daftar stage (tengah, scrollable) ─────────────────────────────────
        stageListPanel = new JPanel();
        stageListPanel.setLayout(new BoxLayout(stageListPanel, BoxLayout.Y_AXIS));
        stageListPanel.setBackground(BG);

        JScrollPane scroll = new JScrollPane(stageListPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // ── Footer (bawah) — tombol kembali ───────────────────────────────────
        JButton backBtn = new JButton("← Main Menu");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setBackground(new Color(50, 50, 70));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { if (onBack != null) onBack.run(); });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        footer.setBackground(BG);
        footer.add(backBtn);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Refresh data ───────────────────────────────────────────────────────────
    /**
     * Dipanggil setiap kali layar ini ditampilkan agar data selalu up-to-date.
     */
    public void refresh(List<Stage> stages, Player player) {
        // Update info player
        playerInfoLabel.setText(String.format(
            "<html><center>" +
            "Hero: <b>%s</b>  |  Level <b>%d</b>  |  " +
            "HP: <b>%d/%d</b>  |  Gold: <b>%d</b>" +
            "</center></html>",
            player.getName(), player.getLevel(),
            player.getHp(), player.getMaxHp(), player.getGold()));

        // Bangun ulang daftar kartu stage
        stageListPanel.removeAll();
        for (int i = 0; i < stages.size(); i++) {
            stageListPanel.add(buildStageCard(stages.get(i), i, player));
            stageListPanel.add(Box.createVerticalStrut(14));
        }
        stageListPanel.revalidate();
        stageListPanel.repaint();
    }

    // ── Builder kartu stage ────────────────────────────────────────────────────
    private JPanel buildStageCard(Stage stage, int index, Player player) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BD, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // ── Kiri: info stage ─────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(CARD_BG);

        JLabel nameLabel = new JLabel("Stage " + stage.getId() + " — " + stage.getName());
        nameLabel.setFont(new Font("Serif", Font.BOLD, 17));
        nameLabel.setForeground(GOLD);
        info.add(nameLabel);

        JLabel metaLabel = new JLabel(String.format(
            "Rec. Level %d  |  %d encounter%s%s",
            stage.getRecommendedLevel(),
            stage.getTotalEncounters(),
            stage.getTotalEncounters() > 1 ? "s" : "",
            stage.hasBoss() ? "  (includes BOSS)" : ""));
        metaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        metaLabel.setForeground(LAVENDER);
        info.add(Box.createVerticalStrut(3));
        info.add(metaLabel);

        // Teks deskripsi dibungkus HTML agar otomatis wrap
        JLabel descLabel = new JLabel(
            "<html><p style='width:400px'>" + stage.getDescription() + "</p></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(180, 180, 180));
        info.add(Box.createVerticalStrut(4));
        info.add(descLabel);

        card.add(info, BorderLayout.CENTER);

        // ── Kanan: tombol enter ───────────────────────────────────────────────
        boolean underleveled = player.getLevel() < stage.getRecommendedLevel();
        JButton enterBtn = new JButton(underleveled ? "⚠ Enter Stage" : "▶ Enter Stage");
        enterBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        enterBtn.setBackground(underleveled ? new Color(110, 80, 20) : new Color(45, 95, 55));
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFocusPainted(false);
        enterBtn.setBorderPainted(false);
        enterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enterBtn.setPreferredSize(new Dimension(128, 42));
        if (underleveled) {
            enterBtn.setToolTipText("Level kamu lebih rendah dari yang direkomendasikan!");
        }
        enterBtn.addActionListener(e -> {
            if (onStageSelected != null) onStageSelected.accept(index);
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setBackground(CARD_BG);
        btnWrapper.add(enterBtn);
        card.add(btnWrapper, BorderLayout.EAST);

        return card;
    }

    // ── Setter callback ────────────────────────────────────────────────────────
    public void setOnStageSelected(Consumer<Integer> cb) { this.onStageSelected = cb; }
    public void setOnBack(Runnable cb)                   { this.onBack          = cb; }
}