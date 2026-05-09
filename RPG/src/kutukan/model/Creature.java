package kutukan.model;

/**
 * Abstract base class for every living entity in the game (Player, Enemy, Boss).
 *
 * Centralises common combat-related state (HP, MP, ATK, DEF) and the core
 * {@link #takeDamage} / {@link #heal} mechanics so subclasses only need to
 * implement {@link #performAttack} to express their own damage output.
 */
public abstract class Creature {

    // ── Core Stats ───────────────────────────────────────────────────────────
    protected String  name;
    protected int     hp;
    protected int     maxHp;
    protected int     mp;
    protected int     maxMp;
    protected int     attack;
    protected int     defense;
    protected boolean alive;

    // ── Constructor ──────────────────────────────────────────────────────────
    public Creature(String name, int maxHp, int maxMp, int attack, int defense) {
        this.name    = name;
        this.maxHp   = maxHp;
        this.hp      = maxHp;
        this.maxMp   = maxMp;
        this.mp      = maxMp;
        this.attack  = attack;
        this.defense = defense;
        this.alive   = true;
    }

    // ── Combat Primitives ────────────────────────────────────────────────────

    /**
     * Applies incoming damage after subtracting defense.
     * At least 1 damage is always dealt so fights always progress.
     *
     * @param rawDamage raw (pre-mitigation) damage value
     * @return the actual HP lost after defense reduction
     */
    public int takeDamage(int rawDamage) {
        int actualDamage = Math.max(1, rawDamage - defense);
        hp = Math.max(0, hp - actualDamage);
        if (hp == 0) alive = false;
        return actualDamage;
    }

    /** Restores HP without exceeding {@code maxHp}. */
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    /** Restores MP without exceeding {@code maxMp}. */
    public void restoreMp(int amount) {
        mp = Math.min(maxMp, mp + amount);
    }

    /** Reduces MP without going below zero. */
    public void consumeMp(int amount) {
        mp = Math.max(0, mp - amount);
    }

    /** Permanently increases attack (used by buff items/skills). */
    public void boostAttack(int amount) {
        attack += amount;
    }

    /** Reduces defense; floor is 0 (cannot become negative). */
    public void reduceDefense(int amount) {
        defense = Math.max(0, defense - amount);
    }

    // ── Abstract ─────────────────────────────────────────────────────────────

    /**
     * Returns the raw (pre-mitigation) damage this creature deals
     * when it attacks. Each subclass calculates this differently.
     */
    public abstract int performAttack();

    // ── Accessors ────────────────────────────────────────────────────────────
    public String  getName()    { return name; }
    public int     getHp()      { return hp; }
    public int     getMaxHp()   { return maxHp; }
    public int     getMp()      { return mp; }
    public int     getMaxMp()   { return maxMp; }
    public int     getAttack()  { return attack; }
    public int     getDefense() { return defense; }
    public boolean isAlive()    { return alive; }

    public void setAttack(int attack)   { this.attack  = Math.max(0, attack); }
    public void setDefense(int defense) { this.defense = Math.max(0, defense); }
}