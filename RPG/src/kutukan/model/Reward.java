package kutukan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable value object describing what the player earns when an enemy
 * (or boss) is defeated: experience points, gold, and any item drops.
 */
public class Reward {

    // ── Fields ──────────────────────────────────────────────────────────────
    private final int        exp;
    private final int        gold;
    private final List<Item> items;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Reward(int exp, int gold, List<Item> items) {
        this.exp   = exp;
        this.gold  = gold;
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
    }

    /** Convenience constructor — no item drops. */
    public Reward(int exp, int gold) {
        this(exp, gold, null);
    }

    // ── Accessors ───────────────────────────────────────────────────────────
    public int        getExp()   { return exp; }
    public int        getGold()  { return gold; }
    public List<Item> getItems() { return new ArrayList<>(items); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EXP +").append(exp).append("  |  Gold +").append(gold);
        if (!items.isEmpty()) {
            sb.append("\nItem drops: ");
            items.forEach(i -> sb.append(i.getName()).append("  "));
        }
        return sb.toString();
    }
}