package kutukan.model;

/**
 * Consumable item a player can carry and use during battle.
 * Each item belongs to one {@link ItemType} category and carries
 * a numeric effect value whose meaning depends on the category.
 *
 * Static factory methods provide the pre-defined item catalogue.
 */
public class Item {

    // ── Inner enum ──────────────────────────────────────────────────────────
    public enum ItemType {
        HEAL_HP,   // restores (effectValue) HP
        HEAL_MP,   // restores (effectValue) MP
        HEAL_BOTH, // restores HP by effectValue and MP by effectValue/2
        BUFF_ATK,  // permanently increases ATK by effectValue for this battle
        BUFF_DEF   // permanently increases DEF by effectValue for this battle
    }

    // ── Fields ──────────────────────────────────────────────────────────────
    private final String   name;
    private final String   description;
    private final ItemType type;
    private final int      effectValue;

    // ── Constructor ─────────────────────────────────────────────────────────
    public Item(String name, String description, ItemType type, int effectValue) {
        this.name        = name;
        this.description = description;
        this.type        = type;
        this.effectValue = effectValue;
    }

    // ── Item Catalogue ──────────────────────────────────────────────────────

    /** Restores 50 HP. */
    public static Item healthPotion() {
        return new Item("Health Potion", "Restores 50 HP.", ItemType.HEAL_HP, 50);
    }

    /** Restores 30 MP. */
    public static Item manaPotion() {
        return new Item("Mana Potion", "Restores 30 MP.", ItemType.HEAL_MP, 30);
    }

    /** Restores 100 HP and 50 MP. */
    public static Item elixir() {
        return new Item("Elixir", "Restores 100 HP and 50 MP.", ItemType.HEAL_BOTH, 100);
    }

    /** Boosts ATK by 10 for the current battle. */
    public static Item strengthTonic() {
        return new Item("Strength Tonic", "Boosts Attack by 10 for this battle.",
                ItemType.BUFF_ATK, 10);
    }

    /** Boosts DEF by 8 for the current battle. */
    public static Item ironShield() {
        return new Item("Iron Shield", "Boosts Defense by 8 for this battle.",
                ItemType.BUFF_DEF, 8);
    }

    // ── Accessors ───────────────────────────────────────────────────────────
    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public ItemType getType()        { return type; }
    public int      getEffectValue() { return effectValue; }

    @Override
    public String toString() { return name; }
}