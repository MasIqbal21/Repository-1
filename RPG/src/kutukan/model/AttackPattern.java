package kutukan.model;

/**
 * Defines the different attack patterns an enemy or boss can use.
 * Each pattern carries a display name, a damage multiplier, and a
 * short description that is shown to the player in the battle log.
 */
public enum AttackPattern {

    NORMAL("Normal Attack",  1.0, "A standard melee strike."),
    HEAVY ("Heavy Strike",   1.8, "A slow but devastating blow."),
    SPECIAL("Special Ability", 2.2, "Unleashes a powerful technique."),
    MULTI ("Multi Strike",   0.65, "Strikes rapidly in quick succession.");

    // ── Fields ──────────────────────────────────────────────────────────────
    private final String displayName;
    private final double damageMultiplier;
    private final String description;

    // ── Constructor ─────────────────────────────────────────────────────────
    AttackPattern(String displayName, double damageMultiplier, String description) {
        this.displayName       = displayName;
        this.damageMultiplier  = damageMultiplier;
        this.description       = description;
    }

    // ── Accessors ───────────────────────────────────────────────────────────
    public String getDisplayName()      { return displayName; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public String getDescription()      { return description; }
}