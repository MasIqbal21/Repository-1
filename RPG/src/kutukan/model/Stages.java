package kutukan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static factory that builds all game stages.
 *
 * Each call to {@link #getAllStages()} returns brand-new {@link Stage}
 * instances with freshly constructed {@link Enemy} and {@link Boss} objects,
 * so replaying stages always starts with full enemy HP.
 *
 * Add new stages here — the rest of the code picks them up automatically.
 */
public class Stages {

    // ── Public API ──────────────────────────────────────────────────────────

    /**
     * Returns an ordered list of every stage in the game.
     * Always creates fresh objects; safe to call multiple times.
     */
    public static List<Stage> getAllStages() {
        List<Stage> stages = new ArrayList<>();
        stages.add(createStage1());
        stages.add(createStage2());
        stages.add(createStage3());
        return stages;
    }

    // ── Stage Definitions ───────────────────────────────────────────────────

    /** Stage 1 — Whispering Woods (no boss, tutorial-friendly) */
    private static Stage createStage1() {
        List<Enemy> enemies = Arrays.asList(
            new Enemy("Goblin Scout", 40, 0, 12, 3,
                new Reward(30, 15),
                AttackPattern.NORMAL),
            new Enemy("Forest Wolf", 55, 0, 15, 5,
                new Reward(40, 20),
                AttackPattern.NORMAL, AttackPattern.MULTI)
        );
        return new Stage(
            1,
            "Whispering Woods",
            "A murky forest crawling with goblins and wild beasts. " +
            "Perfect for a warrior just starting their journey.",
            "forest",
            enemies,
            null,   // no boss
            1
        );
    }

    /** Stage 2 — Cursed Dungeon (3 regular enemies + boss) */
    private static Stage createStage2() {
        List<Enemy> enemies = Arrays.asList(
            new Enemy("Dungeon Rat",       50, 10, 14,  4,
                new Reward(45, 20),
                AttackPattern.NORMAL, AttackPattern.MULTI),
            new Enemy("Skeleton Warrior",  70,  0, 18,  8,
                new Reward(55, 25),
                AttackPattern.NORMAL, AttackPattern.HEAVY),
            new Enemy("Dark Mage",         60, 40, 22,  6,
                new Reward(65, 30),
                AttackPattern.NORMAL, AttackPattern.SPECIAL)
        );
        Boss boss = new Boss(
            "Drakkar", "The Bone Lord",
            180, 50, 25, 12,
            new Reward(150, 80, Arrays.asList(Item.elixir())),
            AttackPattern.NORMAL, AttackPattern.HEAVY, AttackPattern.SPECIAL
        );
        return new Stage(
            2,
            "Cursed Dungeon",
            "Ancient catacombs haunted by the restless undead. " +
            "The Bone Lord Drakkar guards its deepest chamber.",
            "dungeon",
            enemies,
            boss,
            2
        );
    }

    /** Stage 3 — Dragon's Lair (3 elite enemies + final boss) */
    private static Stage createStage3() {
        List<Enemy> enemies = Arrays.asList(
            new Enemy("Black Knight",     90, 20, 26, 14,
                new Reward(80, 40),
                AttackPattern.NORMAL, AttackPattern.HEAVY),
            new Enemy("Fire Drake",      100, 30, 28, 10,
                new Reward(90, 45),
                AttackPattern.SPECIAL, AttackPattern.MULTI),
            new Enemy("Shadow Assassin",  80, 25, 30,  8,
                new Reward(85, 40),
                AttackPattern.MULTI, AttackPattern.SPECIAL)
        );
        Boss boss = new Boss(
            "Malachar", "The Eternal Dragon",
            350, 80, 38, 18,
            new Reward(400, 200,
                Arrays.asList(Item.elixir(), Item.strengthTonic())),
            AttackPattern.NORMAL, AttackPattern.HEAVY, AttackPattern.SPECIAL
        );
        return new Stage(
            3,
            "Dragon's Lair",
            "The volcanic fortress of the legendary Eternal Dragon. " +
            "Only the strongest adventurers dare venture here.",
            "castle",
            enemies,
            boss,
            4
        );
    }
}