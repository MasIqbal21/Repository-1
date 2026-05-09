package kutukan.controller;

import kutukan.model.*;
import kutukan.view.GameView;

import java.util.List;

/**
 * Central game controller — the single source of truth for game state.
 *
 * Responsibilities:
 * <ul>
 *   <li>Maintain the current {@link GameState} and transition between them.</li>
 *   <li>Own the {@link Player} for a full game session.</li>
 *   <li>Orchestrate sequential encounters within a stage.</li>
 *   <li>Delegate all visual output to the injected {@link GameView}.</li>
 * </ul>
 *
 * GUI panels hold a reference to this class and call its action methods
 * (e.g. {@link #playerAttack()}) in response to user interaction.
 */
public class GameManager {

    // ── Dependencies ─────────────────────────────────────────────────────────
    private final GameView    view;
    private final List<Stage> stageRoster; // immutable display list

    // ── Session state ────────────────────────────────────────────────────────
    private Player    player;
    private GameState state;

    // ── Battle state ─────────────────────────────────────────────────────────
    private Stage         currentStage;
    private List<Enemy>   currentEnemies;
    private int           enemyIndex;       // index into currentEnemies
    private Creature      currentEnemy;
    private boolean       fightingBoss;
    private StringBuilder battleLog;

    // ── Constructor ──────────────────────────────────────────────────────────

    public GameManager(GameView view) {
        this.view        = view;
        this.stageRoster = Stages.getAllStages(); // display list (never modified)
        this.state       = GameState.MAIN_MENU;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** Called once by the view on startup. */
    public void startGame() {
        state = GameState.MAIN_MENU;
        view.showMainMenu();
    }

    /** Creates a new player and moves to stage selection. */
    public void newGame(String playerName) {
        player = new Player(playerName.isBlank() ? "Hero" : playerName.trim());
        state  = GameState.STAGE_SELECT;
        view.showStageSelect(stageRoster, player);
    }

    // ── Stage entry ──────────────────────────────────────────────────────────

    /**
     * Loads a fresh copy of the chosen stage (so enemy HP is always full)
     * and begins the first encounter.
     */
    public void selectStage(int index) {
        // Always build fresh — this resets all enemy HP across replays
        currentStage   = Stages.getAllStages().get(index);
        currentEnemies = currentStage.getEnemies();
        enemyIndex     = 0;
        fightingBoss   = false;
        battleLog      = new StringBuilder();

        player.restoreForNewBattle();

        // Determine first opponent
        if (!currentEnemies.isEmpty()) {
            currentEnemy = currentEnemies.get(0);
        } else if (currentStage.hasBoss()) {
            currentEnemy = currentStage.getBoss();
            fightingBoss = true;
        }

        appendLog("⚔  Entering " + currentStage.getName() + "...\n");
        appendLog("A wild " + currentEnemy.getName() + " appears!\n");
        state = GameState.BATTLE;
        view.showBattle(player, currentEnemy, battleLog.toString());
    }

    // ── Player actions ───────────────────────────────────────────────────────

    public void playerAttack() {
        if (state != GameState.BATTLE) return;
        processResult(Combat.playerAttack(player, currentEnemy));
    }

    public void playerUseSkill(int skillIndex) {
        if (state != GameState.BATTLE) return;
        List<Skill> skills = player.getSkills();
        if (skillIndex < 0 || skillIndex >= skills.size()) return;
        processResult(Combat.playerUseSkill(player, currentEnemy, skills.get(skillIndex)));
    }

    public void playerUseItem(int itemIndex) {
        if (state != GameState.BATTLE) return;
        List<Item> inv = player.getInventory();
        if (itemIndex < 0 || itemIndex >= inv.size()) return;
        processResult(Combat.playerUseItem(player, currentEnemy, inv.get(itemIndex)));
    }

    public void playerFlee() {
        if (state != GameState.BATTLE) return;
        if (fightingBoss) {
            appendLog("You cannot flee from a boss!\n");
            view.showBattle(player, currentEnemy, battleLog.toString());
            return;
        }
        appendLog(player.getName() + " fled from the battle!\n");
        state = GameState.STAGE_SELECT;
        view.showStageSelect(stageRoster, player);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public void returnToMainMenu() {
        state = GameState.MAIN_MENU;
        view.showMainMenu();
    }

    public void returnToStageSelect() {
        state = GameState.STAGE_SELECT;
        view.showStageSelect(stageRoster, player);
    }

    // ── Internal result processing ───────────────────────────────────────────

    private void processResult(Combat.CombatResult result) {
        // Invalid action (e.g. not enough MP)
        if (result.actionResult == Combat.ActionResult.INVALID_ACTION) {
            appendLog(result.playerLog + "\n");
            view.showBattle(player, currentEnemy, battleLog.toString());
            return;
        }

        appendLog(result.playerLog + "\n");

        if (result.phaseChanged) {
            appendLog("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            appendLog("⚠  BOSS PHASE 2 ACTIVATED!\n");
            appendLog("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        }

        if (result.actionResult == Combat.ActionResult.ENEMY_DEFEATED) {
            appendLog(result.enemyLog + "\n");
            handleEnemyDefeated();
            return;
        }

        appendLog(result.enemyLog + "\n");

        if (result.actionResult == Combat.ActionResult.PLAYER_DEFEATED) {
            handlePlayerDefeated();
            return;
        }

        view.showBattle(player, currentEnemy, battleLog.toString());
    }

    private void handleEnemyDefeated() {
        // Award this enemy's reward immediately
        Reward reward = fightingBoss
                ? currentStage.getBoss().getReward()
                : currentEnemies.get(enemyIndex).getReward();

        boolean leveledUp = player.gainExp(reward.getExp());
        player.addGold(reward.getGold());
        reward.getItems().forEach(player::addItem);

        appendLog(String.format("\n%s defeated!  +%d EXP  +%d Gold\n",
                currentEnemy.getName(), reward.getExp(), reward.getGold()));
        if (!reward.getItems().isEmpty()) {
            appendLog("Item(s) obtained: ");
            reward.getItems().forEach(i -> appendLog(i.getName() + "  "));
            appendLog("\n");
        }
        if (leveledUp) {
            appendLog("★ LEVEL UP!  Now Level " + player.getLevel() + "!\n");
        }

        advanceEncounter();
    }

    /** Determines the next combatant after a defeat. */
    private void advanceEncounter() {
        if (!fightingBoss) {
            enemyIndex++;
            if (enemyIndex < currentEnemies.size()) {
                // Next regular enemy
                currentEnemy = currentEnemies.get(enemyIndex);
                appendLog("\n" + "A " + currentEnemy.getName() + " appears!\n");
                view.showBattle(player, currentEnemy, battleLog.toString());
            } else if (currentStage.hasBoss()) {
                // Transition to boss
                fightingBoss = true;
                currentEnemy = currentStage.getBoss();
                Boss boss = (Boss) currentEnemy;
                appendLog("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                appendLog("  ⚠  BOSS:  " + boss.getFullName() + " appears!\n");
                appendLog("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                view.showBattle(player, currentEnemy, battleLog.toString());
            } else {
                stageCleared();
            }
        } else {
            stageCleared();
        }
    }

    private void stageCleared() {
        boolean isLastStage = stageRoster.indexOf(currentStage) == stageRoster.size() - 1
                || currentStage.getId() == stageRoster.get(stageRoster.size() - 1).getId();
        state = isLastStage ? GameState.VICTORY : GameState.STAGE_RESULT;

        appendLog("\n🏆  " + currentStage.getName() + " cleared!\n");
        view.showResult(true, null, player, state == GameState.VICTORY);
    }

    private void handlePlayerDefeated() {
        appendLog("\n💀  " + player.getName() + " has fallen...\n");
        state = GameState.GAME_OVER;
        view.showResult(false, null, player, false);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void appendLog(String line) {
        battleLog.append(line);
    }

    // ── Accessors (read-only) ─────────────────────────────────────────────────
    public Player    getPlayer()       { return player; }
    public GameState getState()        { return state; }
    public Stage     getCurrentStage() { return currentStage; }
    public List<Stage> getStageRoster(){ return stageRoster; }
}