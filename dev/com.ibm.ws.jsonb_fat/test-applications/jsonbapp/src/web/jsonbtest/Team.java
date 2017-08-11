package web.jsonbtest;

public class Team {
    public String name;
    public int size;
    public float winLossRatio;

    @Override
    public String toString() {
        return "name=" + name + "  size=" + size + "  winLossRatio=" + winLossRatio;
    }
}