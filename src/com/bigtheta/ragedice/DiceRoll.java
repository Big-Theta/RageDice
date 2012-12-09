package com.bigtheta.ragedice;

public class DiceRoll {
    private long id;
    private long roll_result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRollResult() {
        return roll_result;
    }

    public void setRollResult(int result) {
        this.roll_result = result;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return "DiceRoll(" + Long.toString(id) + " : " + Long.toString(roll_result);
    }
}
