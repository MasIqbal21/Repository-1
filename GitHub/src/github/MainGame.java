package github;

import github.*;

public class MainGame {
    
    public static void main(String[] args) {
        
        Hero pemain = new Hero("iqbal ganteng", new Pedang());
        
        // Menyerang musuh dengan pedang
        pemain.serangMusuh("Goblin");
        pemain.serangMusuh("Orc");

        // Ganti ke senjata Panah, musuhnya itu terbang
        pemain.gantiSenjata(new Panah());
        pemain.serangMusuh("Naga Terbang");

        // Ganti ke senjata Sihir, musuhnya butuh damage besar
        pemain.gantiSenjata(new Sihir());
        pemain.serangMusuh("Raja Iblis");
    }
}