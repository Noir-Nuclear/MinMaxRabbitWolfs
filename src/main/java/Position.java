package main.java;

public class Position implements Cloneable {

    public Integer i, j;

    public Position() {
        i = j = 0;
    }

    public Position clone() {
        Position newP = new Position();
        newP.i = i;
        newP.j = j;
        return newP;
    }
}
