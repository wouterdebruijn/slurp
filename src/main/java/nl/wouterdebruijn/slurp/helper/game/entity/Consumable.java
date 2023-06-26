package nl.wouterdebruijn.slurp.helper.game.entity;

public class Consumable {
    private int sips;
    private int shots;

    public Consumable() {
        this.sips = 0;
        this.shots = 0;
    }

    public void set(Consumable consumable) {
        sips = consumable.getSips();
        shots = consumable.getShots();
    }

    public int getSips() {
        return sips;
    }

    public void setSips(int sips) {
        this.sips = sips;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public String toString() {
        return "Sips: " + sips + ", Shots: " + shots;
    }

    public Consumable add(Consumable consumable) {
        Consumable result = new Consumable();

        result.setSips(sips + consumable.getSips());
        result.setShots(shots + consumable.getShots());

        return result;
    }
}
