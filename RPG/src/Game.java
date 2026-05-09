package kutukan;

import kutukan.view.gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point aplikasi RPG Adventure — Kutukan.
 *
 * Aturan utama Swing: SEMUA operasi UI harus dijalankan di
 * Event Dispatch Thread (EDT), bukan di thread utama main().
 * Itulah mengapa kita membungkus pembuatan MainFrame di dalam
 * SwingUtilities.invokeLater().
 *
 * Konfigurasi di NetBeans:
 *   Project Properties → Run → Main Class → kutukan.Game
 */
public class Game {

    public static void main(String[] args) {
        // Jalankan UI di Event Dispatch Thread (best practice Swing)
        SwingUtilities.invokeLater(() -> {
            // Gunakan tampilan sesuai sistem operasi (Windows/macOS/Linux)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Jika gagal, Swing akan pakai default Look & Feel
            }

            // Buat dan tampilkan jendela utama
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}