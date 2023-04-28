package nl.wouterdebruijn.slurp.helper.game.entity;

public class Consumable {
    private int sips;
    private int shots;

    public void set(Consumable consumable) {
        sips = consumable.getSips();
        shots = consumable.getShots();
    }

    public int getSips() {
        return sips;
    }

    public int getShots() {
        return shots;
    }

    public void setSips(int sips) {
        this.sips = sips;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public String toString() {
        return "Sips: " + sips + ", Shots: " + shots;
    }
}
