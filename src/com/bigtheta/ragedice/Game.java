package com.bigtheta.ragedice;

import org.apache.commons.math3.distribution.KolmogorovSmirnovDistribution;

public class Game {
    private String[] tableGame = {
        MySQLiteHelper.COLUMN_ID
    };

    long m_id;

    public Game(SQLiteDatabase database) {
        m_id = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));
    }

    public Game(Cursor cursor) {
        
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    /*
     * Determines how likely it is that the dice are fair.
     *
     * References:
     *     http://www.physics.csbsju.edu/stats/KS-test.html
     *    http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test
     */
    public double calculateKSProbability() {

        int numRolls = getNumDiceRolls();
        if (numRolls == 0) {
            return 0.0;
        }

        // Find the d statistic using the cff (cumulative fraction function).
        // The d statistic is the greatest deviation between the expected cff and
        // the observed cff.
        // Note: we could possibly make this statistic more extreme if we sorted the
        // observed data so that we evaluate all rolls that are less than the expected.
        double d = 0.0;
        double obs_cff = 0.0;
        double exp_cff = 0.0;
        for (int i = 2; i <=12; i++) {
            obs_cff += getCountForRoll(i) / (double)numRolls;
            exp_cff += getExpectedCount(i) / (double)numRolls;
            if (Math.abs(exp_cff - obs_cff) > d) {
                d = Math.abs(exp_cff - obs_cff);
            }
        }

        KolmogorovSmirnovDistribution dist = new KolmogorovSmirnovDistribution(numRolls);

        return dist.cdf(d);
    }

    /*
     * Determines how likely it is that the dice are fair. This test abuses the
     * data in order to come up with a more extreme statistic.
     *
     * References:
     *     http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test
     *    http://www.physics.csbsju.edu/stats/KS-test.html
     */
    public double calculateKSProbabilityMaximized() {
        int numRolls = getNumDiceRolls();
        if (numRolls == 0) {
            return 0.0;
        }

        double obs = 0.0;
        double exp = 0.0;
        double obs_cff = 0.0;
        double exp_cff = 0.0;

        // Create the cff in as an extreme a way as possible. To do this, I
        // am first taking out values where the observed count is less than
        // the expected count. After this is done, it doesn't matter what
        // the rest of the cff is, because the d statistic will be the difference
        // between the two cffs.
        for (int i = 2; i <= 12; i++) {
            exp = getExpectedCount(i);
            obs = getCountForRoll(i);
            if (obs < exp) {
                obs_cff += obs / (double)numRolls;
                exp_cff += exp / (double)numRolls;
            }
        }

        double d = exp_cff - obs_cff;
        KolmogorovSmirnovDistribution dist = new KolmogorovSmirnovDistribution(numRolls);
        return dist.cdf(d);
    }

    public double getExpectedCount(int diceResult) {
        int expected;
        switch (diceResult) {
            case 2:
            case 12:
                expected = 1;
                break;
            case 3:
            case 11:
                expected = 2;
                break;
            case 4:
            case 10:
                expected = 3;
                break;
            case 5:
            case 9:
                expected = 4;
                break;
            case 6:
            case 8:
                expected = 5;
                break;
            default:
                expected = 6;
        }

        return (expected / 36.0) * (double)getNumDiceRolls();
    }

}

