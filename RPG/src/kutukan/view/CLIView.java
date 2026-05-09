package kutukan.view;

import kutukan.controller.GameState;
import kutukan.model.*;
import java.util.List;

/**
 * Implementasi GameView berbasis teks (Command Line Interface).
 *
 * Berguna untuk:
 *   - Testing logika game tanpa membuka jendela GUI
 *   - Debugging cepat
 *   - Referensi jika ingin membuat bot/tester otomatis
 *
 * Untuk menggunakannya, ganti parameter di GameManager dengan CLIView
 * alih-alih MainFrame.
 */
public class CLIView implements GameView {

    private static final String DIVIDER = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    @Override
    public void showMainMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("         ⚔  RPG ADVENTURE — KUTUKAN  ⚔");
        System.out.println(DIVIDER);
        System.out.println("  1. New Game");
        System.out.println("  2. Quit");
        System.out.println(DIVIDER);
    }

    @Override
    public void showStageSelect(List<Stage> stages, Player player) {
        System.out.println("\n" + DIVIDER);
        System.out.printf("  STAGE SELECT  |  %s  Lv.%d  |  HP:%d/%d  |  Gold:%d%n",
            player.getName(), player.getLevel(),
            player.getHp(), player.getMaxHp(), player.getGold());
        System.out.println(DIVIDER);
        for (int i = 0; i < stages.size(); i++) {
            Stage s = stages.get(i);
            System.out.printf("  %d. %-20s [Rec. Lv.%-2d]  Encounters: %d%s%n",
                i + 1,
                s.getName(),
                s.getRecommendedLevel(),
                s.getTotalEncounters(),
                s.hasBoss() ? " (incl. BOSS)" : "");
            System.out.printf("     %s%n", s.getDescription());
        }
        System.out.println(DIVIDER);
        System.out.print("  Pilih stage (1-" + stages.size() + "): ");
    }

    @Override
    public void showBattle(Player player, Creature enemy, String battleLog) {
        System.out.println("\n" + DIVIDER);

        // Enemy bar
        String eName = (enemy instanceof Boss) ? ((Boss) enemy).getFullName() : enemy.getName();
        System.out.printf("  [ENEMY]  %-25s  HP: %d/%d%n",
            eName, enemy.getHp(), enemy.getMaxHp());

        // Player bar
        System.out.printf("  [YOU]    %-25s  HP: %d/%d  MP: %d/%d%n",
            player.getName(), player.getHp(), player.getMaxHp(),
            player.getMp(), player.getMaxMp());

        System.out.println(DIVIDER);
        System.out.println(battleLog);
        System.out.println(DIVIDER);
        System.out.println("  [1] Attack  [2] Skill  [3] Item  [4] Flee");
        System.out.print("  Pilih aksi: ");
    }

    @Override
    public void showResult(boolean victory, Reward reward, Player player, boolean isGameComplete) {
        System.out.println("\n" + DIVIDER);
        if (victory) {
            if (isGameComplete) {
                System.out.println("  🎉  CONGRATULATIONS! Game Complete!");
                System.out.println("  Kamu telah menaklukkan semua stage!");
            } else {
                System.out.println("  🏆  STAGE CLEAR!");
            }
            if (reward != null) {
                System.out.println("  " + reward);
            }
        } else {
            System.out.println("  💀  GAME OVER");
            System.out.println("  " + (player != null ? player.getName() : "Hero") + " telah tumbang...");
        }
        System.out.println(DIVIDER);
        System.out.println("  [1] Kembali ke Stage Select  [2] Main Menu");
    }

    @Override
    public void showGameOver() {
        System.out.println("\n💀 Game Over. Coba lagi!");
    }
}