package kutukan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The player-controlled hero.
 *
 * Beyond the base stats inherited from {@link Creature}, the Player tracks:
 * <ul>
 *   <li>Level &amp; experience (auto-levels up when EXP threshold is reached)</li>
 *   <li>Gold currency</li>
 *   <li>A {@link Skill} list (new skills unlock at certain levels)</li>
 *   <li>An item inventory consumed during battle</li>
 * </ul>
 */
public class Player extends Creature {

    // ── Level / EXP constants ────────────────────────────────────────────────
    private static final int   BASE_EXP     = 100;
    private static final float EXP_SCALING  = 1.5f;

    // ── Fields ──────────────────────────────────────────────────────────────
    private int          level;
    private int          exp;
    private int          expToNextLevel;
    private int          gold;
    private final List<Skill> skills;
    private final List<Item>  inventory;

    // ── Constructor ─────────────────────────────────────────────────────────

    /**
     * Creates a fresh Level-1 hero with starter skills and items.
     * @param name chosen by the player at the title screen
     */
    public Player(String name) {
        super(name, 120, 60, 20, 8);
        this.level         = 1;
        this.exp           = 0;
        this.expToNextLevel = BASE_EXP;
        this.gold          = 50;
        this.skills        = new ArrayList<>();
        this.inventory     = new ArrayList<>();
        initStarterLoadout();
    }

    // ── Initialisation ──────────────────────────────────────────────────────

    private void initStarterLoadout() {
        // Starting skills
        skills.add(Skill.slash());
        skills.add(Skill.heal());
        // Starting items
        inventory.add(Item.healthPotion());
        inventory.add(Item.healthPotion());
        inventory.add(Item.manaPotion());
    }

    // ── Abstract impl ───────────────────────────────────────────────────────

    /** Player's raw attack output (flat ATK stat; modified by Skill multipliers in Combat). */
    @Override
    public int performAttack() {
        return attack;
    }

    // ── Level / EXP ─────────────────────────────────────────────────────────

    /**
     * Awards EXP and triggers a level-up if the threshold is reached.
     * @return true if the player levelled up as a result of this call
     */
    public boolean gainExp(int amount) {
        exp += amount;
        if (exp >= expToNextLevel) {
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        level++;
        exp           -= expToNextLevel;
        expToNextLevel = (int) (BASE_EXP * Math.pow(EXP_SCALING, level - 1));

        // Stat growth on level-up
        maxHp  += 15;
        hp      = maxHp;       // full HP restore on level-up
        maxMp  += 10;
        mp      = maxMp;
        attack  += 4;
        defense += 2;

        // Skill unlock table
        unlockSkillForLevel(level);
    }

    private void unlockSkillForLevel(int lvl) {
        switch (lvl) {
            case 2: skills.add(Skill.fireball());    break;
            case 3: skills.add(Skill.shieldBreak()); break;
            case 4: skills.add(Skill.thunder());     break;
            default: break; // no new skill at this level
        }
    }

    // ── Inventory helpers ────────────────────────────────────────────────────

    public void addGold(int amount)    { gold += amount; }
    public void addItem(Item item)     { inventory.add(item); }
    public boolean removeItem(Item item) { return inventory.remove(item); }

    // ── Between-battle restoration ───────────────────────────────────────────

    /** Partial MP recovery between battles (HP is NOT restored — stay strategic). */
    public void restoreForNewBattle() {
        restoreMp(maxMp / 3);
    }

    /** Full HP+MP restore (used on new game start / testing). */
    public void fullRestore() {
        hp = maxHp;
        mp = maxMp;
    }

    // ── Accessors ────────────────────────────────────────────────────────────
    public int       getLevel()          { return level; }
    public int       getExp()            { return exp; }
    public int       getExpToNextLevel() { return expToNextLevel; }
    public int       getGold()           { return gold; }
    public List<Skill> getSkills()       { return skills; }
    public List<Item>  getInventory()    { return inventory; }
}