package kutukan.view.gui;

import kutukan.controller.GameManager;
import kutukan.model.*;
import kutukan.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Jendela utama aplikasi (JFrame).
 *
 * Tanggung jawab:
 * 1. Menginisialisasi semua JPanel dan mendaftarkannya ke CardLayout.
 * 2. Mengimplementasikan GameView sehingga GameManager bisa meminta
 *    tampilan berubah tanpa tahu detail Swing.
 * 3. Menyambungkan callback setiap panel ke method GameManager
 *    (pattern: panel punya setter callback, MainFrame menyuntikkan lambda).
 *
 * Alur kontrol:
 *   User klik tombol di panel
 *     → lambda callback terpanggil
 *       → method GameManager terpanggil
 *         → GameManager memanggil view.showXxx()
 *           → MainFrame memperbarui UI di EDT via SwingUtilities.invokeLater()
 */
public class MainFrame extends JFrame implements GameView {

    // ── Konstanta nama layar (untuk CardLayout) ────────────────────────────────
    private static final String SCREEN_MAIN_MENU    = "MAIN_MENU";
    private static final String SCREEN_STAGE_SELECT = "STAGE_SELECT";
    private static final String SCREEN_BATTLE       = "BATTLE";
    private static final String SCREEN_RESULT       = "RESULT";

    // ── Controller ─────────────────────────────────────────────────────────────
    private GameManager gameManager;

    // ── Layout ─────────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     cardPanel;

    // ── Panel-panel ────────────────────────────────────────────────────────────
    private MainMenuPanel    mainMenuPanel;
    private StageSelectPanel stageSelectPanel;
    private BattlePanel      battlePanel;
    private ResultPanel      resultPanel;

    // ── Constructor ────────────────────────────────────────────────────────────
    public MainFrame() {
        super("RPG Adventure — Kutukan");
        setupFrame();
        buildPanels();
        // GameManager dibuat SETELAH panel tersedia agar view siap di-inject
        this.gameManager = new GameManager(this);
        wireCallbacks();
        gameManager.startGame();
    }

    // ── Setup frame ───────────────────────────────────────────────────────────
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 660);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null); // tengah layar
        setResizable(true);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        setContentPane(cardPanel);
    }

    // ── Bangun panel ──────────────────────────────────────────────────────────
    private void buildPanels() {
        mainMenuPanel    = new MainMenuPanel();
        stageSelectPanel = new StageSelectPanel();
        battlePanel      = new BattlePanel();
        resultPanel      = new ResultPanel();

        // Daftarkan ke CardLayout — nama string harus sama dengan konstanta di atas
        cardPanel.add(mainMenuPanel,    SCREEN_MAIN_MENU);
        cardPanel.add(stageSelectPanel, SCREEN_STAGE_SELECT);
        cardPanel.add(battlePanel,      SCREEN_BATTLE);
        cardPanel.add(resultPanel,      SCREEN_RESULT);
    }

    // ── Sambungkan callback ───────────────────────────────────────────────────
    /**
     * Setiap panel punya setter untuk lambda callback.
     * Di sini kita "pasang" lambda yang memanggil GameManager.
     *
     * Pola: panel.setOnXxx(() -> gameManager.doSomething())
     */
    private void wireCallbacks() {
        // ── Main Menu ──────────────────────────────────────────────────────────
        mainMenuPanel.setOnNewGame(playerName -> gameManager.newGame(playerName));
        mainMenuPanel.setOnQuit(() -> System.exit(0));

        // ── Stage Select ───────────────────────────────────────────────────────
        stageSelectPanel.setOnStageSelected(index -> gameManager.selectStage(index));
        stageSelectPanel.setOnBack(() -> gameManager.returnToMainMenu());

        // ── Battle ─────────────────────────────────────────────────────────────
        battlePanel.setOnAttack(() -> gameManager.playerAttack());
        battlePanel.setOnSkill(index -> gameManager.playerUseSkill(index));
        battlePanel.setOnItem(index -> gameManager.playerUseItem(index));
        battlePanel.setOnFlee(() -> gameManager.playerFlee());

        // ── Result ─────────────────────────────────────────────────────────────
        resultPanel.setOnContinue(() -> gameManager.returnToStageSelect());
        resultPanel.setOnMainMenu(() -> gameManager.returnToMainMenu());
    }

    // =========================================================================
    // Implementasi GameView interface
    // Setiap method dipanggil oleh GameManager dan harus berjalan di EDT.
    // =========================================================================

    @Override
    public void showMainMenu() {
        SwingUtilities.invokeLater(() ->
            cardLayout.show(cardPanel, SCREEN_MAIN_MENU));
    }

    @Override
    public void showStageSelect(List<Stage> stages, Player player) {
        SwingUtilities.invokeLater(() -> {
            stageSelectPanel.refresh(stages, player);
            cardLayout.show(cardPanel, SCREEN_STAGE_SELECT);
        });
    }

    @Override
    public void showBattle(Player player, Creature enemy, String battleLog) {
        SwingUtilities.invokeLater(() -> {
            battlePanel.refresh(player, enemy, battleLog);
            cardLayout.show(cardPanel, SCREEN_BATTLE);
        });
    }

    @Override
    public void showResult(boolean victory, Reward reward, Player player, boolean isGameComplete) {
        SwingUtilities.invokeLater(() -> {
            resultPanel.refresh(victory, reward, player, isGameComplete);
            cardLayout.show(cardPanel, SCREEN_RESULT);
        });
    }

    @Override
    public void showGameOver() {
        SwingUtilities.invokeLater(() -> {
            resultPanel.refresh(false, null, null, false);
            cardLayout.show(cardPanel, SCREEN_RESULT);
        });
    }
}