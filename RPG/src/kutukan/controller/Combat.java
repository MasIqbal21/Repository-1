package kutukan.controller;

import kutukan.model.*;

/**
 * Stateless combat resolver.
 *
 * Every public method accepts the current combatants and an action choice,
 * resolves both the player's turn and the enemy's counter-attack, and
 * returns a {@link CombatResult} describing everything that happened.
 * No mutable game state is held here — that is {@link GameManager}'s job.
 */
public final class Combat {

    private Combat() { /* utility class — no instantiation */ }

    // ── Result types ─────────────────────────────────────────────────────────

    /** Codes describing the high-level outcome of one full round. */
    public enum ActionResult {
        CONTINUE,        // both alive, battle goes on
        ENEMY_DEFEATED,  // enemy HP reached 0
        PLAYER_DEFEATED, // player HP reached 0
        INVALID_ACTION   // e.g. insufficient MP / empty inventory
    }

    /**
     * Full report of one combat round:
     * the player's action log, the enemy's response log,
     * whether either combatant was defeated, and whether a boss phase changed.
     */
    public static final class CombatResult {
        public final ActionResult actionResult;
        public final String       playerLog;
        public final String       enemyLog;
        public final int          damageDealt;    // damage the player dealt
        public final int          damageReceived; // damage the enemy dealt back
        public final boolean      phaseChanged;   // boss phase-2 triggered this turn

        public CombatResult(ActionResult actionResult, String playerLog, String enemyLog,
                            int damageDealt, int damageReceived, boolean phaseChanged) {
            this.actionResult   = actionResult;
            this.playerLog      = playerLog;
            this.enemyLog       = enemyLog;
            this.damageDealt    = damageDealt;
            this.damageReceived = damageReceived;
            this.phaseChanged   = phaseChanged;
        }
    }

    // ── Public actions ───────────────────────────────────────────────────────

    /** Player performs a basic attack on the enemy. */
    public static CombatResult playerAttack(Player player, Creature enemy) {
        int raw    = player.performAttack();
        int dealt  = enemy.takeDamage(raw);
        String pLog = String.format("%s attacks %s for %d damage!",
                player.getName(), enemy.getName(), dealt);

        if (!enemy.isAlive()) {
            return new CombatResult(ActionResult.ENEMY_DEFEATED,
                    pLog, enemy.getName() + " has been defeated!", dealt, 0, false);
        }
        return resolveEnemyTurn(player, enemy, pLog, dealt);
    }

    /** Player uses a skill from their skill list. */
    public static CombatResult playerUseSkill(Player player, Creature enemy, Skill skill) {
        if (!skill.canUse(player)) {
            return new CombatResult(ActionResult.INVALID_ACTION,
                    "Not enough MP to use " + skill.getName() + "!", "", 0, 0, false);
        }

        player.consumeMp(skill.getMpCost());
        int dealt = 0;
        String pLog;

        switch (skill.getType()) {
            case DAMAGE: {
                int raw = (int) (player.getAttack() * skill.getEffectValue());
                dealt = enemy.takeDamage(raw);
                pLog = String.format("%s uses %s → %d damage to %s!",
                        player.getName(), skill.getName(), dealt, enemy.getName());
                break;
            }
            case HEAL: {
                int restored = (int) skill.getEffectValue();
                player.heal(restored);
                pLog = String.format("%s uses %s → restored %d HP!",
                        player.getName(), skill.getName(), restored);
                break;
            }
            case DEBUFF: {
                int reduction = (int) skill.getEffectValue();
                enemy.reduceDefense(reduction);
                pLog = String.format("%s uses %s → %s's DEF reduced by %d!",
                        player.getName(), skill.getName(), enemy.getName(), reduction);
                break;
            }
            case BUFF: {
                int boost = (int) skill.getEffectValue();
                player.boostAttack(boost);
                pLog = String.format("%s uses %s → ATK increased by %d!",
                        player.getName(), skill.getName(), boost);
                break;
            }
            default:
                pLog = player.getName() + " uses " + skill.getName() + "!";
        }

        if (!enemy.isAlive()) {
            return new CombatResult(ActionResult.ENEMY_DEFEATED,
                    pLog, enemy.getName() + " has been defeated!", dealt, 0, false);
        }
        return resolveEnemyTurn(player, enemy, pLog, dealt);
    }

    /** Player uses a consumable item from their inventory. */
    public static CombatResult playerUseItem(Player player, Creature enemy, Item item) {
        player.removeItem(item);
        int dealt = 0;
        String pLog;

        switch (item.getType()) {
            case HEAL_HP:
                player.heal(item.getEffectValue());
                pLog = String.format("%s uses %s → restored %d HP!",
                        player.getName(), item.getName(), item.getEffectValue());
                break;
            case HEAL_MP:
                player.restoreMp(item.getEffectValue());
                pLog = String.format("%s uses %s → restored %d MP!",
                        player.getName(), item.getName(), item.getEffectValue());
                break;
            case HEAL_BOTH:
                player.heal(item.getEffectValue());
                player.restoreMp(item.getEffectValue() / 2);
                pLog = String.format("%s uses %s → restored %d HP and %d MP!",
                        player.getName(), item.getName(),
                        item.getEffectValue(), item.getEffectValue() / 2);
                break;
            case BUFF_ATK:
                player.boostAttack(item.getEffectValue());
                pLog = String.format("%s uses %s → ATK +%d this battle!",
                        player.getName(), item.getName(), item.getEffectValue());
                break;
            case BUFF_DEF:
                player.setDefense(player.getDefense() + item.getEffectValue());
                pLog = String.format("%s uses %s → DEF +%d this battle!",
                        player.getName(), item.getName(), item.getEffectValue());
                break;
            default:
                pLog = player.getName() + " uses " + item.getName() + "!";
        }

        return resolveEnemyTurn(player, enemy, pLog, dealt);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Resolves the enemy's counter-attack after the player acted.
     * Detects boss phase transitions and appends an appropriate warning line.
     */
    private static CombatResult resolveEnemyTurn(Player player, Creature enemy,
                                                 String pLog, int damageDealt) {
        boolean wasPhase1      = isBossPhase1(enemy);
        int     rawEnemyDamage = enemy.performAttack();
        boolean phaseChanged   = wasPhase1 && isBossPhase2(enemy);

        int     received   = player.takeDamage(rawEnemyDamage);
        String  patternName = (enemy instanceof Enemy)
                ? ((Enemy) enemy).getCurrentPattern().getDisplayName()
                : "Attack";
        String  eName       = (enemy instanceof Boss)
                ? ((Boss) enemy).getFullName()
                : enemy.getName();

        StringBuilder eLog = new StringBuilder();
        if (phaseChanged) {
            eLog.append("⚠  ").append(eName).append(" enters PHASE 2! Stats surged!\n");
        }
        eLog.append(String.format("%s uses %s → %d damage to %s!",
                eName, patternName, received, player.getName()));

        ActionResult result = player.isAlive() ? ActionResult.CONTINUE
                                               : ActionResult.PLAYER_DEFEATED;
        return new CombatResult(result, pLog, eLog.toString(), damageDealt, received, phaseChanged);
    }

    private static boolean isBossPhase1(Creature c) {
        return (c instanceof Boss) && ((Boss) c).getPhase() == 1;
    }

    private static boolean isBossPhase2(Creature c) {
        return (c instanceof Boss) && ((Boss) c).getPhase() == 2;
    }
}