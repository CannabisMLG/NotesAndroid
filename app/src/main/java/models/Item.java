package models;

public class Item {
    private String name;
    private double myRate, kpRate;
    private int ch;
    private String type;

    public Item()
    {
        this("", 0, 0, 0, "");
    }

    public Item(String name, double myRate, double kpRate, int ch, String type)
    {
        setName(name);
        setMyRate(myRate);
        setKpRate(kpRate);
        setCh(ch);
        setType(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKpRate(double kpRate) {
        this.kpRate = kpRate;
    }

    public void setMyRate(double myRate) {
        this.myRate = myRate;
    }

    public void setCh(int ch) {
        this.ch = ch;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String isCh() {
        return ch+"";
    }

    public String getKpRate() {
        return kpRate+"";
    }

    public String getMyRate() {
        return myRate+"";
    }

    public String getType() {
        return type;
    }
}
