package kutukan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single stage (area) the player can enter.
 *
 * A stage is a sequential encounter: the player fights each regular
 * {@link Enemy} in order, then (optionally) the {@link Boss}.
 * Clearing all encounters counts as a stage victory.
 */
public class Stage {

    // ── Fields ──────────────────────────────────────────────────────────────
    private final int         id;
    private final String      name;
    private final String      description;
    private final String      backgroundTheme;   // e.g. "forest" / "dungeon" / "castle"
    private final List<Enemy> enemies;
    private final Boss        boss;              // may be null for non-boss stages
    private final int         recommendedLevel;

    // ── Constructor ─────────────────────────────────────────────────────────
    public Stage(int id, String name, String description, String backgroundTheme,
                 List<Enemy> enemies, Boss boss, int recommendedLevel) {
        this.id               = id;
        this.name             = name;
        this.description      = description;
        this.backgroundTheme  = backgroundTheme;
        this.enemies          = (enemies != null) ? new ArrayList<>(enemies) : new ArrayList<>();
        this.boss             = boss;
        this.recommendedLevel = recommendedLevel;
    }

    // ── Accessors ────────────────────────────────────────────────────────────
    public int         getId()                { return id; }
    public String      getName()              { return name; }
    public String      getDescription()       { return description; }
    public String      getBackgroundTheme()   { return backgroundTheme; }
    /** Returns a defensive copy so callers cannot mutate the stage's list. */
    public List<Enemy> getEnemies()           { return new ArrayList<>(enemies); }
    public Boss        getBoss()              { return boss; }
    public boolean     hasBoss()              { return boss != null; }
    public int         getRecommendedLevel()  { return recommendedLevel; }
    public int         getTotalEncounters()   { return enemies.size() + (boss != null ? 1 : 0); }

    @Override
    public String toString() {
        return String.format("Stage %d: %s  (Recommended Lv.%d)", id, name, recommendedLevel);
    }
}