package github;

import github.*;

public class Hero {
    
    private String namaHero;
    private Senjata senjataAktif;

    // Constructor
    public Hero(String namaHero, Senjata senjataAwal) {
        this.namaHero = namaHero;
        this.senjataAktif = senjataAwal;
    }

    // ganti senjata (Polimorfisme)
    public void gantiSenjata(Senjata senjataBaru) {
        this.senjataAktif = senjataBaru;
        System.out.println("\n>> " + this.namaHero + " ganti senjata <<");
    }

    // menyerang
    public void serangMusuh(String namaMusuh) {
        System.out.print(this.namaHero + " beraksi: ");
        senjataAktif.serang(namaMusuh);
    }
}