package kutukan.model;

import java.util.Random;

/**
 * A regular battlefield enemy.
 *
 * Enemies cycle through one or more {@link AttackPattern}s each turn.
 * Every third turn the enemy escalates to its strongest pattern (if it
 * has more than one), keeping fights from feeling purely random.
 * Defeating an enemy yields the carried {@link Reward}.
 */
public class Enemy extends Creature {

    // ── Fields ──────────────────────────────────────────────────────────────
    protected AttackPattern         currentPattern;
    protected final AttackPattern[] patterns;
    protected final Reward          reward;

    private final Random random    = new Random();
    private int          turnCount = 0;

    // ── Constructor ─────────────────────────────────────────────────────────

    /**
     * @param patterns one or more attack patterns; at least NORMAL is expected.
     *                 If none supplied, defaults to {@link AttackPattern#NORMAL}.
     */
    public Enemy(String name, int maxHp, int maxMp,
                 int attack, int defense,
                 Reward reward, AttackPattern... patterns) {
        super(name, maxHp, maxMp, attack, defense);
        this.reward   = reward;
        this.patterns = (patterns.length > 0) ? patterns
                                              : new AttackPattern[]{AttackPattern.NORMAL};
        this.currentPattern = this.patterns[0];
    }

    // ── Attack logic ─────────────────────────────────────────────────────────

    /**
     * Selects the next pattern, then returns raw damage.
     * Raw damage = attack stat × pattern multiplier.
     */
    @Override
    public int performAttack() {
        selectNextPattern();
        return (int) (attack * currentPattern.getDamageMultiplier());
    }

    /**
     * Pattern selection strategy:
     * <ul>
     *   <li>Single-pattern enemies always use that pattern.</li>
     *   <li>Every 3rd turn, the hardest pattern is used (telegraphed surge).</li>
     *   <li>Other turns pick randomly from all patterns except the last.</li>
     * </ul>
     */
    protected void selectNextPattern() {
        turnCount++;
        if (patterns.length == 1) {
            currentPattern = patterns[0];
        } else if (turnCount % 3 == 0) {
            // Predictable spike every 3 turns
            currentPattern = patterns[patterns.length - 1];
        } else {
            currentPattern = patterns[random.nextInt(patterns.length - 1)];
        }
    }

    // ── Accessors ────────────────────────────────────────────────────────────
    public AttackPattern getCurrentPattern() { return currentPattern; }
    public Reward        getReward()         { return reward; }
}