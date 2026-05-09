package kutukan.controller;

/**
 * Represents every high-level state the game can occupy at any moment.
 * {@link GameManager} transitions between these states and instructs the
 * active {@link kutukan.view.GameView} to display the appropriate screen.
 */
public enum GameState {
    /** The title / main-menu screen. */
    MAIN_MENU,

    /** Stage-selection screen — player chooses which stage to enter. */
    STAGE_SELECT,

    /** Active battle — player is fighting an enemy or boss. */
    BATTLE,

    /** Post-battle results screen (victory with rewards, or defeat). */
    STAGE_RESULT,

    /** All three stages cleared — final victory screen. */
    VICTORY,

    /** Player's HP reached zero — game over screen. */
    GAME_OVER
}