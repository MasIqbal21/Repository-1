package kutukan.view;

import kutukan.controller.GameState;
import kutukan.model.*;
import java.util.List;

/**
 * Kontrak (contract) yang harus dipenuhi oleh setiap implementasi view.
 *
 * Dengan pola ini, GameManager tidak perlu tahu apakah tampilan yang
 * digunakan adalah GUI atau CLI — ia hanya memanggil method di interface ini.
 * Ini adalah penerapan prinsip Dependency Inversion (SOLID).
 */
public interface GameView {

    /** Tampilkan layar utama / title screen. */
    void showMainMenu();

    /**
     * Tampilkan layar pemilihan stage.
     * @param stages daftar semua stage yang tersedia
     * @param player data player saat ini (untuk ditampilkan di header)
     */
    void showStageSelect(List<Stage> stages, Player player);

    /**
     * Tampilkan layar pertarungan.
     * @param player  data player saat ini
     * @param enemy   musuh yang sedang dilawan
     * @param battleLog log teks semua aksi yang telah terjadi
     */
    void showBattle(Player player, Creature enemy, String battleLog);

    /**
     * Tampilkan layar hasil (menang atau kalah).
     * @param victory        true = menang, false = kalah
     * @param reward         hadiah yang didapat (bisa null jika kalah)
     * @param player         data player saat ini
     * @param isGameComplete true jika semua stage sudah diselesaikan
     */
    void showResult(boolean victory, Reward reward, Player player, boolean isGameComplete);

    /** Tampilkan layar game over. */
    void showGameOver();
}