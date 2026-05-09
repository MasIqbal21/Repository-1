package kutukan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A named stage boss.
 *
 * Bosses share all {@link Enemy} behaviour but also:
 * <ul>
 *   <li>Carry a dramatic title shown during the intro announcement.</li>
 *   <li>Enter <b>Phase 2</b> once HP drops to 50 %, gaining a permanent
 *       bonus to ATK and DEF for the remainder of the fight.</li>
 *   <li>Use a special boss skill every 4th turn (costs MP; falls back
 *       to the normal pattern cycle if MP is insufficient).</li>
 * </ul>
 */
public class Boss extends Enemy {

    // ── Fields ──────────────────────────────────────────────────────────────
    private final String      title;
    private int               phase            = 1;
    private final int         phaseThresholdHp; // absolute HP that triggers Phase 2
    private boolean           phase2Triggered  = false;
    private final List<Skill> bossSkills       = new ArrayList<>();
    private int               skillTurn        = 0;

    // ── Constructor ─────────────────────────────────────────────────────────

    /**
     * @param title dramatic prefix, e.g. "The Bone Lord" (shown as "The Bone Lord Drakkar")
     */
    public Boss(String name, String title,
                int maxHp, int maxMp, int attack, int defense,
                Reward reward, AttackPattern... patterns) {
        super(name, maxHp, maxMp, attack, defense, reward, patterns);
        this.title            = title;
        this.phaseThresholdHp = maxHp / 2;
        initBossSkills();
    }

    // ── Initialisation ──────────────────────────────────────────────────────

    private void initBossSkills() {
        bossSkills.add(Skill.fireball());   // Phase 1 skill
        bossSkills.add(new Skill(           // Phase 2 exclusive
                "Devastate",
                "Channels all power into one catastrophic strike!",
                25, Skill.SkillType.DAMAGE, 3.5));
    }

    // ── Attack override ──────────────────────────────────────────────────────

    /**
     * Every 4th turn the boss casts a skill if it has enough MP.
     * Phase 2 is checked before each attack so the transition fires
     * exactly when the HP threshold is crossed.
     */
    @Override
    public int performAttack() {
        checkPhaseTransition();
        skillTurn++;

        if (skillTurn % 4 == 0) {
            Skill skill = bossSkills.get(phase == 2 ? 1 : 0);
            if (mp >= skill.getMpCost()) {
                consumeMp(skill.getMpCost());
                currentPattern = AttackPattern.SPECIAL;
                return (int) (attack * skill.getEffectValue());
            }
        }
        return super.performAttack();
    }

    // ── Phase management ─────────────────────────────────────────────────────

    private void checkPhaseTransition() {
        if (!phase2Triggered && hp <= phaseThresholdHp) {
            phase           = 2;
            phase2Triggered = true;
            attack          = (int) (attack  * 1.3);
            defense         = (int) (defense * 1.2);
        }
    }

    // ── Accessors ────────────────────────────────────────────────────────────
    public int     getPhase()           { return phase; }
    public boolean isPhase2Triggered()  { return phase2Triggered; }
    public String  getTitle()           { return title; }
    /** Returns the full display name, e.g. "The Bone Lord Drakkar". */
    public String  getFullName()        { return title + " " + name; }
}