package kutukan.view.gui;

import kutukan.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Panel utama pertarungan (battle screen).
 *
 * Layout:
 * ┌─────────────────────────────────────────────┐
 * │  [KOTAK MUSUH]         [KOTAK PLAYER]        │  ← NORTH (120px)
 * ├─────────────────────────────────────────────┤
 * │                                             │
 * │            BATTLE LOG (scroll)              │  ← CENTER
 * │                                             │
 * ├─────────────────────────────────────────────┤
 * │  [Attack]  [Skill ▾]  [Item ▾]  [Flee]     │  ← SOUTH (55px)
 * └─────────────────────────────────────────────┘
 *
 * Skill dan Item menggunakan JPopupMenu yang muncul di atas tombol.
 */
public class BattlePanel extends JPanel {

    // ── Callback (dipasang dari MainFrame) ────────────────────────────────────
    private Runnable   onAttack;
    private IntConsumer onSkill;    // index skill yang dipilih
    private IntConsumer onItem;     // index item yang dipilih
    private Runnable   onFlee;

    // ── Warna tema ─────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(15, 15, 25);
    private static final Color BOX_BG      = new Color(25, 25, 40);
    private static final Color ENEMY_COL   = new Color(200, 60,  60);
    private static final Color PLAYER_COL  = new Color(60,  120, 210);
    private static final Color HP_GREEN    = new Color(60,  180, 60);
    private static final Color HP_YELLOW   = new Color(220, 180, 50);
    private static final Color HP_RED      = new Color(220, 50,  50);
    private static final Color MP_BLUE     = new Color(80,  80,  220);

    // ── Komponen stats musuh ───────────────────────────────────────────────────
    private JLabel        enemyNameLabel;
    private JProgressBar  enemyHpBar;
    private JLabel        enemyHpText;

    // ── Komponen stats player ──────────────────────────────────────────────────
    private JLabel        playerNameLabel;
    private JLabel        playerLevelLabel;
    private JProgressBar  playerHpBar;
    private JLabel        playerHpText;
    private JProgressBar  playerMpBar;
    private JLabel        playerMpText;

    // ── Battle log ─────────────────────────────────────────────────────────────
    private JTextArea    battleLogArea;

    // ── Tombol aksi ────────────────────────────────────────────────────────────
    private JButton attackBtn;
    private JButton skillBtn;
    private JButton itemBtn;
    private JButton fleeBtn;

    // ── Referensi state saat ini (dipakai popup menu) ──────────────────────────
    private Player   currentPlayer;
    private Creature currentEnemy;

    // ── Constructor ────────────────────────────────────────────────────────────
    public BattlePanel() {
        initUI();
    }

    // ── Inisialisasi UI ────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout(0, 8));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        add(buildStatsPanel(),   BorderLayout.NORTH);
        add(buildLogPanel(),     BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
    }

    // ─────────────────────── NORTH: Stats ─────────────────────────────────────

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setBackground(BG);
        panel.setPreferredSize(new Dimension(0, 115));
        panel.add(buildEnemyBox());
        panel.add(buildPlayerBox());
        return panel;
    }

    /** Kotak info musuh — nama + HP bar */
    private JPanel buildEnemyBox() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BOX_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ENEMY_COL, 2),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        // Inisialisasi komponen sebagai field class
        enemyNameLabel = mkLabel("Enemy", ENEMY_COL, "Serif", Font.BOLD, 18);
        enemyHpBar     = mkBar(HP_GREEN);
        enemyHpText    = mkLabel("0/0", Color.LIGHT_GRAY, "SansSerif", Font.PLAIN, 11);

        p.add(enemyNameLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(mkBarRow("HP", enemyHpBar, enemyHpText));
        return p;
    }

    /** Kotak info player — nama, level, HP bar, MP bar */
    private JPanel buildPlayerBox() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BOX_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PLAYER_COL, 2),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        playerNameLabel  = mkLabel("Hero", PLAYER_COL, "Serif", Font.BOLD, 18);
        playerLevelLabel = mkLabel("Level 1", new Color(180, 160, 220), "SansSerif", Font.PLAIN, 12);
        playerHpBar      = mkBar(HP_GREEN);
        playerHpText     = mkLabel("0/0", Color.LIGHT_GRAY, "SansSerif", Font.PLAIN, 11);
        playerMpBar      = mkBar(MP_BLUE);
        playerMpText     = mkLabel("0/0", Color.LIGHT_GRAY, "SansSerif", Font.PLAIN, 11);

        p.add(playerNameLabel);
        p.add(playerLevelLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(mkBarRow("HP", playerHpBar, playerHpText));
        p.add(Box.createVerticalStrut(3));
        p.add(mkBarRow("MP", playerMpBar, playerMpText));
        return p;
    }

    /** Baris label + progress bar + teks angka */
    private JPanel mkBarRow(String label, JProgressBar bar, JLabel txt) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(BOX_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        JLabel lbl = mkLabel(label + ":", Color.LIGHT_GRAY, "SansSerif", Font.BOLD, 11);
        lbl.setPreferredSize(new Dimension(24, 16));
        txt.setPreferredSize(new Dimension(82, 16));
        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        row.add(txt, BorderLayout.EAST);
        return row;
    }

    // ─────────────────────── CENTER: Log ──────────────────────────────────────

    private JScrollPane buildLogPanel() {
        battleLogArea = new JTextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        battleLogArea.setBackground(new Color(18, 18, 30));
        battleLogArea.setForeground(new Color(205, 205, 205));
        battleLogArea.setLineWrap(true);
        battleLogArea.setWrapStyleWord(true);
        battleLogArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(battleLogArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 85)));
        scroll.getViewport().setBackground(new Color(18, 18, 30));
        return scroll;
    }

    // ─────────────────────── SOUTH: Aksi ──────────────────────────────────────

    private JPanel buildActionsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        p.setPreferredSize(new Dimension(0, 55));

        attackBtn = mkActionBtn("⚔  Attack",  new Color(160, 55, 55));
        skillBtn  = mkActionBtn("✦  Skill",   new Color(55, 75, 160));
        itemBtn   = mkActionBtn("⊕  Item",    new Color(50, 110, 55));
        fleeBtn   = mkActionBtn("↩  Flee",    new Color(75, 75, 75));

        attackBtn.addActionListener(e -> { if (onAttack != null) onAttack.run(); });
        skillBtn.addActionListener(e  -> showSkillMenu());
        itemBtn.addActionListener(e   -> showItemMenu());
        fleeBtn.addActionListener(e   -> { if (onFlee != null) onFlee.run(); });

        p.add(attackBtn);
        p.add(skillBtn);
        p.add(itemBtn);
        p.add(fleeBtn);
        return p;
    }

    // ─────────────────────── Popup Skill ──────────────────────────────────────

    /**
     * Memunculkan menu popup berisi daftar skill player.
     * Skill yang tidak bisa digunakan (MP kurang) ditampilkan disabled.
     */
    private void showSkillMenu() {
        if (currentPlayer == null) return;
        List<Skill> skills = currentPlayer.getSkills();
        if (skills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada skill yang tersedia!");
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        for (int i = 0; i < skills.size(); i++) {
            final int idx = i;
            Skill sk    = skills.get(i);
            boolean ok  = sk.canUse(currentPlayer);

            // Label dengan HTML untuk warna abu-abu jika tidak bisa pakai
            String label = String.format(
                "<html><b>%s</b>  <span style='color:#999'>(MP: %d)</span>"
                + " &mdash; %s</html>",
                sk.getName(), sk.getMpCost(), sk.getDescription());

            JMenuItem mi = new JMenuItem(label);
            mi.setEnabled(ok);
            mi.setFont(new Font("SansSerif", Font.PLAIN, 13));
            mi.addActionListener(e -> { if (onSkill != null) onSkill.accept(idx); });
            menu.add(mi);
        }

        // Hitung tinggi menu dulu, lalu tampilkan DI ATAS tombol
        menu.pack();
        menu.show(skillBtn, 0, -menu.getHeight());
    }

    // ─────────────────────── Popup Item ───────────────────────────────────────

    /** Memunculkan menu popup berisi daftar item di inventori player. */
    private void showItemMenu() {
        if (currentPlayer == null) return;
        List<Item> inv = currentPlayer.getInventory();
        if (inv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventori kosong!");
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        for (int i = 0; i < inv.size(); i++) {
            final int idx = i;
            Item it = inv.get(i);
            JMenuItem mi = new JMenuItem(it.getName() + "  —  " + it.getDescription());
            mi.setFont(new Font("SansSerif", Font.PLAIN, 13));
            mi.addActionListener(e -> { if (onItem != null) onItem.accept(idx); });
            menu.add(mi);
        }

        menu.pack();
        menu.show(itemBtn, 0, -menu.getHeight());
    }

    // ─────────────────────── Refresh ──────────────────────────────────────────

    /**
     * Dipanggil setiap kali state game berubah (setiap aksi).
     * Memperbarui semua komponen UI sekaligus.
     */
    public void refresh(Player player, Creature enemy, String log) {
        this.currentPlayer = player;
        this.currentEnemy  = enemy;

        // ── Update kotak musuh ─────────────────────────────────────────────
        String eName = (enemy instanceof Boss)
                ? ((Boss) enemy).getFullName()
                : enemy.getName();
        enemyNameLabel.setText(eName);

        int ePct = (int)(100.0 * enemy.getHp() / enemy.getMaxHp());
        enemyHpBar.setValue(ePct);
        enemyHpBar.setForeground(ePct <= 25 ? HP_RED : ePct <= 50 ? HP_YELLOW : HP_GREEN);
        enemyHpText.setText(enemy.getHp() + "/" + enemy.getMaxHp());

        // ── Update kotak player ────────────────────────────────────────────
        playerNameLabel.setText(player.getName());
        playerLevelLabel.setText("Lv." + player.getLevel()
            + "   ATK:" + player.getAttack()
            + "   DEF:" + player.getDefense());

        int hPct = (int)(100.0 * player.getHp() / player.getMaxHp());
        playerHpBar.setValue(hPct);
        playerHpBar.setForeground(hPct <= 25 ? HP_RED : hPct <= 50 ? HP_YELLOW : HP_GREEN);
        playerHpText.setText(player.getHp() + "/" + player.getMaxHp());

        int mPct = (int)(100.0 * player.getMp() / player.getMaxMp());
        playerMpBar.setValue(mPct);
        playerMpText.setText(player.getMp() + "/" + player.getMaxMp());

        // ── Update battle log ──────────────────────────────────────────────
        battleLogArea.setText(log);
        // Scroll otomatis ke bawah setelah update
        battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength());
    }

    // ─────────────────────── Helper factories ─────────────────────────────────

    private JLabel mkLabel(String text, Color fg, String fontName, int style, int size) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(fontName, style, size));
        lbl.setForeground(fg);
        return lbl;
    }

    private JProgressBar mkBar(Color fg) {
        JProgressBar b = new JProgressBar(0, 100);
        b.setValue(100);
        b.setForeground(fg);
        b.setBackground(new Color(50, 50, 70));
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(0, 14));
        return b;
    }

    private JButton mkActionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─────────────────────── Setter callback ──────────────────────────────────
    public void setOnAttack(Runnable cb)    { this.onAttack = cb; }
    public void setOnSkill(IntConsumer cb)  { this.onSkill  = cb; }
    public void setOnItem(IntConsumer cb)   { this.onItem   = cb; }
    public void setOnFlee(Runnable cb)      { this.onFlee   = cb; }
}