package com.fillumina.performance.util.stats;

import com.fillumina.performance.util.ExpBinarySearcher;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The student function is very computationally expensive for high values of n.
 * Tests show that for every p in the range (0..1] if n is bigger than
 * {@link #FIRST_N} the result of student(p,n) is always
 * {@link #ASYMPTOTE} with {@code index = (int) Math.round(p * 100.0)}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StudentFunction {

    static final double[] ASYMPTOTE = {
        1.907352270791307E-6,	// p=0.0
        0.01253188978734987,	// p=0.01
        0.025067110879098742,	// p=0.02
        0.037608478383486466,	// p=0.03
        0.050153330302113774,	// p=0.04
        0.06270788951476747,	// p=0.05
        0.07527066964459683,	// p=0.06
        0.08784502988893061,	// p=0.07
        0.10043510474627282,	// p=0.08
        0.11303638945913641,	// p=0.09
        0.12566370161392482,	// p=0.1
        0.13830394691990877,	// p=0.11
        0.15096845576145013,	// p=0.12
        0.16365960790232403,	// p=0.13
        0.1763750305711933,	// p=0.14
        0.18911783717580888,	// p=0.15
        0.20189171035649522,	// p=0.16
        0.21470092836506272,	// p=0.17
        0.2275446437978661,	// p=0.18
        0.24042804382646388,	// p=0.19
        0.25334499929478316,	// p=0.2
        0.2663135495994222,	// p=0.21
        0.2793159907178535,	// p=0.22
        0.29237792628125336,	// p=0.23
        0.3054817544602284,	// p=0.24
        0.31864174025457936,	// p=0.25
        0.33185318033902944,	// p=0.26
        0.3451249329731201,	// p=0.27
        0.3584596689148398,	// p=0.28
        0.3718532086086217,	// p=0.29
        0.38532311293957866,	// p=0.3
        0.3988585821124502,	// p=0.31
        0.41246325975855846,	// p=0.32
        0.4261489289357361,	// p=0.33
        0.43991255413871055,	// p=0.34
        0.45375898048762897,	// p=0.35
        0.46770170512601594,	// p=0.36
        0.48173018497322206,	// p=0.37
        0.4958501547810954,	// p=0.38
        0.5100765280405999,	// p=0.39
        0.5243987892920383,	// p=0.4
        0.5388327145184251,	// p=0.41
        0.5533860917949827,	// p=0.42
        0.5680485229859102,	// p=0.43
        0.5828374588280758,	// p=0.44
        0.5977619240626686,	// p=0.45
        0.6128117338353682,	// p=0.46
        0.6280061979300902,	// p=0.47
        0.6433454426916001,	// p=0.48
        0.6588400193635957,	// p=0.49
        0.6744905031251698,	// p=0.5
        0.690308311813085,	// p=0.51
        0.7063056319463654,	// p=0.52
        0.7224841398388193,	// p=0.53
        0.7388454969072848,	// p=0.54
        0.7554148572499992,	// p=0.55
        0.7721951994483522,	// p=0.56
        0.7891895396732769,	// p=0.57
        0.8064258273468052,	// p=0.58
        0.8238959141430833,	// p=0.59
        0.8416166413173674,	// p=0.6
        0.8596191293676156,	// p=0.61
        0.8778963354573426,	// p=0.62
        0.8964677795662948,	// p=0.63
        0.9153682318514433,	// p=0.64
        0.9345920954071296,	// p=0.65
        0.9541620541721179,	// p=0.66
        0.9741171243424793,	// p=0.67
        0.9944535954624476,	// p=0.68
        1.0152289141807254,	// p=0.69
        1.0364257831465693,	// p=0.7
        1.0581217785908041,	// p=0.71
        1.080318066208243,	// p=0.72
        1.1030658210889022,	// p=0.73
        1.1263855485210676,	// p=0.74
        1.1503517010848388,	// p=0.75
        1.174990562241499,	// p=0.76
        1.2003668061425339,	// p=0.77
        1.2265312795946883,	// p=0.78
        1.253557934914829,	// p=0.79
        1.2815465958789356,	// p=0.8
        1.3105853940160506,	// p=0.81
        1.3407490747065625,	// p=0.82
        1.3722043499703638,	// p=0.83
        1.4050680526438923,	// p=0.84
        1.4395359982876794,	// p=0.85
        1.4758010058319364,	// p=0.86
        1.514100480006138,	// p=0.87
        1.5547731935152203,	// p=0.88
        1.598199108970261,	// p=0.89
        1.6448602374021966,	// p=0.9
        1.6953879689275269,	// p=0.91
        1.750680734301139,	// p=0.92
        1.8119043405040411,	// p=0.93
        1.8807824390779966,	// p=0.94
        1.959955286319985,	// p=0.95
        2.0537431488697457,	// p=0.96
        2.1701061160322883,	// p=0.97
        2.326341701719992,	// p=0.98
        2.575853061335843	// p=0.99
    };

    static final int[] FIRST_N = {
        0,	 // p=0.0
        8341,	 // p=0.01
        30326,	 // p=0.02
        4196,	 // p=0.03
        6794,	 // p=0.04
        4820,	 // p=0.05
        6282,	 // p=0.06
        9037,	 // p=0.07
        6866,	 // p=0.08
        135171,	 // p=0.09
        6688,	 // p=0.1
        15942,	 // p=0.11
        21847,	 // p=0.12
        11341,	 // p=0.13
        12971,	 // p=0.14
        23233,	 // p=0.15
        53252,	 // p=0.16
        25821,	 // p=0.17
        23545,	 // p=0.18
        12852,	 // p=0.19
        75532,	 // p=0.2
        11894,	 // p=0.21
        965858,	 // p=0.22
        12765,	 // p=0.23
        19801,	 // p=0.24
        15414,	 // p=0.25
        28628,	 // p=0.26
        33850,	 // p=0.27
        23008,	 // p=0.28
        149288,	 // p=0.29
        17542,	 // p=0.3
        15945,	 // p=0.31
        30659,	 // p=0.32
        26224,	 // p=0.33
        39264,	 // p=0.34
        166554,	 // p=0.35
        20315,	 // p=0.36
        19724,	 // p=0.37
        37898,	 // p=0.38
        21656,	 // p=0.39
        61706,	 // p=0.4
        144747,	 // p=0.41
        30247,	 // p=0.42
        109565,	 // p=0.43
        267339,	 // p=0.44
        30424,	 // p=0.45
        56893,	 // p=0.46
        41790,	 // p=0.47
        43831,	 // p=0.48
        31182,	 // p=0.49
        40213,	 // p=0.5
        51609,	 // p=0.51
        30696,	 // p=0.52
        25580,	 // p=0.53
        64680,	 // p=0.54
        51964,	 // p=0.55
        38638,	 // p=0.56
        80189,	 // p=0.57
        30795,	 // p=0.58
        40075,	 // p=0.59
        191535,	 // p=0.6
        44697,	 // p=0.61
        57433,	 // p=0.62
        316913,	 // p=0.63
        41473,	 // p=0.64
        44025,	 // p=0.65
        111567,	 // p=0.66
        44438,	 // p=0.67
        149870,	 // p=0.68
        35237,	 // p=0.69
        1770623,	 // p=0.7
        68046,	 // p=0.71
        83854,	 // p=0.72
        52245,	 // p=0.73
        209908,	 // p=0.74
        59977,	 // p=0.75
        54663,	 // p=0.76
        42630,	 // p=0.77
        60874,	 // p=0.78
        369155,	 // p=0.79
        170718,	 // p=0.8
        54081,	 // p=0.81
        208774,	 // p=0.82
        87722,	 // p=0.83
        138836,	 // p=0.84
        69632,	 // p=0.85
        54111,	 // p=0.86
        117044,	 // p=0.87
        110251,	 // p=0.88
        75358,	 // p=0.89
        76369,	 // p=0.9
        398987,	 // p=0.91
        195622,	 // p=0.92
        221763,	 // p=0.93
        457838,	 // p=0.94
        296063,	 // p=0.95
        222791,	 // p=0.96
        88736,	 // p=0.97
        249743,	 // p=0.98
        102118,	 // p=0.99
    };

    private static final Cache<Double,StudentAsymptote> CACHE = new Cache<>(64);

    private static class Cache<K,V> extends LinkedHashMap<K,V> {
        private static final long serialVersionUID = 1L;
        private final int maxSize;

        public Cache(int maxSize) {
            super(maxSize);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
            return size() == maxSize;
        }
    }

    private static class StudentAsymptote {
        private final int firstN;
        private final double asymptote;

        public StudentAsymptote(int firstN, double asymptote) {
            this.firstN = firstN;
            this.asymptote = asymptote;
        }
    }

    public static double student(double p, double n) {
        // use table if p is in there (probable)
        int index = (int) Math.round(p * 100.0);
        if (index / 100.0 == p &&
                index >= 0 && index < 100 && n >= FIRST_N[index]) {
            //check(index, p, n);
            return ASYMPTOTE[index];
        }

        // if n is low then calculation is reasonably fast, no need to cache
        if (n < 3E3) {
            return StatFunctions.student(p, n);
        }

        // cache heavy calculations for p
        double student;
        StudentAsymptote sa = get(p);
        if (sa == null) {
            double s = StatFunctions.student(p, 3E7);
            int firstN = ExpBinarySearcher.searchGreaterOrEquals(
                    1, Integer.MAX_VALUE,
                    (int v) -> StatFunctions.student(p, 1.0 * v) == s);
            sa = new StudentAsymptote(firstN, s);
            put(p, sa);
            student = s;
        }
        if (n > sa.firstN) {
            student = sa.asymptote;
        } else {
            student = StatFunctions.student(p, n);
        }
        return student;
    }

    private synchronized static StudentAsymptote get(double p) {
        return CACHE.get(p);
    }

    private synchronized static void put(double p, StudentAsymptote sa) {
        CACHE.put(p, sa);
    }

    private static void check(int index, double p, double n)
            throws AssertionError {
        final double asymptote = ASYMPTOTE[index];
        final double student = StatFunctions.student(p, n);
        System.out.println("CHECK: p=" + p + ", n=" + n +
                    ", asymptote=" + asymptote +
                    ", student=" + student);
        if (asymptote != student) {
            throw new AssertionError("p=" + p + ", n=" + n +
                    ", asymptote=" + asymptote +
                    ", student=" + student);
        }
    }
}
