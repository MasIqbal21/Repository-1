package kutukan.model;

/**
 * Represents an active skill a player (or boss) can use in battle.
 * Skills cost MP and produce a typed effect: DAMAGE, HEAL, BUFF, or DEBUFF.
 *
 * Static factory methods provide the pre-defined skill roster; new skills
 * can be added here without touching any other class.
 */
public class Skill {

    // ── Inner enum ──────────────────────────────────────────────────────────
    public enum SkillType {
        DAMAGE,   // deals (attack * effectValue) damage to the enemy
        HEAL,     // restores (effectValue) HP to the caster
        BUFF,     // increases caster's attack by (effectValue)
        DEBUFF    // reduces target's defense by (effectValue)
    }

    // ── Fields ──────────────────────────────────────────────────────────────
    private final String    name;
    private final String    description;
    private final int       mpCost;
    private final SkillType type;
    /**
     * Meaning depends on type:
     *   DAMAGE  → multiplier applied to caster's attack stat
     *   HEAL    → flat HP restored
     *   BUFF    → flat ATK increase
     *   DEBUFF  → flat DEF reduction on target
     */
    private final double effectValue;

    // ── Constructor ─────────────────────────────────────────────────────────
    public Skill(String name, String description,
                 int mpCost, SkillType type, double effectValue) {
        this.name        = name;
        this.description = description;
        this.mpCost      = mpCost;
        this.type        = type;
        this.effectValue = effectValue;
    }

    // ── Static Skill Roster ─────────────────────────────────────────────────

    /** Unlocked at start — quick blade slash. */
    public static Skill slash() {
        return new Skill("Slash", "A swift blade slash dealing moderate damage.",
                5, SkillType.DAMAGE, 1.4);
    }

    /** Unlocked at Lv 2 — magical fire projectile. */
    public static Skill fireball() {
        return new Skill("Fireball", "Hurls a blazing ball of fire at the enemy.",
                15, SkillType.DAMAGE, 2.5);
    }

    /** Unlocked at start — restorative magic. */
    public static Skill heal() {
        return new Skill("Heal", "Channels healing energy to restore 60 HP.",
                10, SkillType.HEAL, 60);
    }

    /** Unlocked at Lv 3 — weakens enemy armour. */
    public static Skill shieldBreak() {
        return new Skill("Shield Break", "Shatters the enemy's guard, reducing DEF by 5.",
                12, SkillType.DEBUFF, 5);
    }

    /** Unlocked at Lv 4 — lightning devastation. */
    public static Skill thunder() {
        return new Skill("Thunder", "Calls a bolt of lightning from the sky.",
                20, SkillType.DAMAGE, 3.0);
    }

    // ── Utility ─────────────────────────────────────────────────────────────

    /** Returns true only when the creature has enough MP to cast this skill. */
    public boolean canUse(Creature user) {
        return user.getMp() >= mpCost;
    }

    // ── Accessors ───────────────────────────────────────────────────────────
    public String    getName()        { return name; }
    public String    getDescription() { return description; }
    public int       getMpCost()      { return mpCost; }
    public SkillType getType()        { return type; }
    public double    getEffectValue() { return effectValue; }

    @Override
    public String toString() {
        return String.format("%s (MP: %d) — %s", name, mpCost, description);
    }
}