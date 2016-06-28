package com.fillumina.performance.util.stats;

/**
 * Various statistical functions extracted and adapted to JAVA from the
 * free Cephes statistical library.
 *
 * @see <a href='http://netlib.org/cephes/'>Cephes</a>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class Cephes {

    // For IEEE arithmetic (IBMPC):
    // machine roundoff error
    private static final double MACHEP = 1.11022302462515654042E-16; // 2**-53
    // maximum log on the machine
    private static final double MAXLOG = 7.09782712893383996843E2; // log(2**1024)
    // minimum log on the machine
    private static final double MINLOG = -7.08396418532264106224E2; // log(2**-1022)
    // largest number represented
    private static final double MAXNUM = 1.7976931348623158E308; // 2**1024

    private static final double MAXGAM = 171.624376956302725;

    private static final double BIG = 4.503599627370496e15;
    private static final double BIGINV = 2.22044604925031308085e-16;

    // FI tentative
    private static final double NPY_INFINITY = Double.POSITIVE_INFINITY;

    private static double fabs(double x) {
        return Math.abs(x);
    }

    static double stdtri(int k, double p) {
        double t, rk, z;
        int rflg;

        if( k <= 0 || p <= 0.0 || p >= 1.0 )  {
            throw new IllegalArgumentException();
        }

        rk = k;

        if( p > 0.25 && p < 0.75 ) {
            if( p == 0.5 ) {
                return( 0.0 );
            }
            z = 1.0 - 2.0 * p;
            z = incbi( 0.5, 0.5*rk, fabs(z) );
            t = Math.sqrt(rk * z / (1.0 - z));
            if( p < 0.5 ) {
                t = -t;
            }
            return  t;
        }
        rflg = -1;
        if( p >= 0.5) {
            p = 1.0 - p;
            rflg = 1;
        }
        z = incbi( 0.5 * rk, 0.5, 2.0 * p);

        if(MAXNUM * z < rk) {
            return rflg *  MAXNUM;
        }
        t = Math.sqrt( rk/z - rk );
        return rflg * t;
    }

    private static class Incbi {
        private double a, b, md_y0, d, y, x, x0, x1, lgm, yp, di, dithresh, yl, yh, xt;
        private int i, rflg, dir, nflg;
        private double aa, bb, yy0;

        Incbi(double aa, double bb, double yy0) {
            this.aa = aa;
            this.bb = bb;
            this.yy0 = yy0;
        }

        private double get() {
            i = 0;
            if( yy0 <= 0 ) {
                return 0.0;
            }
            if( yy0 >= 1.0 ) {
                return 1.0;
            }
            x0 = 0.0;
            yl = 0.0;
            x1 = 1.0;
            yh = 1.0;
            nflg = 0;

            if( aa <= 1.0 || bb <= 1.0 ) {
                dithresh = 1.0e-6;
                rflg = 0;
                a = aa;
                b = bb;
                md_y0 = yy0;
                x = a/(a+b);
                y = incbet( a, b, x );
                return ihalve();
            } else {
                dithresh = 1.0e-4;
            }
            /* approximation to inverse function */

            yp = -ndtri(yy0);

            if( yy0 > 0.5 ) {
                rflg = 1;
                a = bb;
                b = aa;
                md_y0 = 1.0 - yy0;
                yp = -yp;
            } else {
                rflg = 0;
                a = aa;
                b = bb;
                md_y0 = yy0;
            }

            lgm = (yp * yp - 3.0)/6.0;
            x = 2.0/( 1.0/(2.0*a-1.0)  +  1.0/(2.0*b-1.0) );
            d = yp * Math.sqrt( x + lgm ) / x
                    - ( 1.0/(2.0*b-1.0) - 1.0/(2.0*a-1.0) )
                    * (lgm + 5.0/6.0 - 2.0/(3.0*x));
            d = 2.0 * d;
            if (d < MINLOG) {
                x = 1.0;
                //goto under;
                return under();
            }
            x = a/( a + b * Math.exp(d) );
            y = incbet( a, b, x );
            yp = (y - md_y0)/md_y0;
            if (fabs(yp) < 0.2) {
                //goto newt;
                return newt();
            }

            /* Resort to interval halving if not close enough. */
            return ihalve();
        }

        double ihalve() {
            //ihalve:

            dir = 0;
            di = 0.5;
            for( i=0; i<100; i++ ) {
                if( i != 0 ) {
                    x = x0  +  di * (x1 - x0);
                    if( x == 1.0 ) {
                        x = 1.0 - MACHEP;
                    }
                    if( x == 0.0 ) {
                        di = 0.5;
                        x = x0  +  di * (x1 - x0);
                        if( x == 0.0 ) {
                            //goto under;
                            return under();
                        }
                    }
                    y = incbet( a, b, x );
                    yp = (x1 - x0)/(x1 + x0);
                    if (fabs(yp) < dithresh) {
                        //goto newt;
                        return newt();
                    }
                    yp = (y-md_y0)/md_y0;
                    if (fabs(yp) < dithresh) {
                        //goto newt;
                        return newt();
                    }
                }
                if( y < md_y0 ) {
                    x0 = x;
                    yl = y;
                    if( dir < 0 ) {
                        dir = 0;
                        di = 0.5;
                    } else if( dir > 3 ) {
                        di = 1.0 - (1.0 - di) * (1.0 - di);
                    } else if( dir > 1 ) {
                        di = 0.5 * di + 0.5;
                    } else {
                        di = (md_y0 - y)/(yh - yl);
                    }
                    dir += 1;
                    if( x0 > 0.75 ) {
                        if( rflg == 1 ) {
                            rflg = 0;
                            a = aa;
                            b = bb;
                            md_y0 = yy0;
                        } else {
                            rflg = 1;
                            a = bb;
                            b = aa;
                            md_y0 = 1.0 - yy0;
                        }
                        x = 1.0 - x;
                        y = incbet( a, b, x );
                        x0 = 0.0;
                        yl = 0.0;
                        x1 = 1.0;
                        yh = 1.0;
                        //goto ihalve;
                        return ihalve();
                    }
                } else {
                    x1 = x;
                    if( rflg == 1 && x1 < MACHEP ) {
                        x = 0.0;
                        //goto done;
                        return done();
                    }
                    yh = y;
                    if( dir > 0 ) {
                        dir = 0;
                        di = 0.5;
                    } else if( dir < -3 ) {
                        di *= di;
                    } else if( dir < -1 ) {
                        di = 0.5 * di;
                    } else {
                        di = (y - md_y0)/(yh - yl);
                    }
                    dir -= 1;
                }
            }
            //mtherr( "incbi", PLOSS );
            if( x0 >= 1.0 ) {
                x = 1.0 - MACHEP;
                //goto done;
                return done();
            }
            if( x <= 0.0 ) {
//                //under:
//                //mtherr( "incbi", UNDERFLOW );
//                x = 0.0;
//                //goto done;
//                return done();
                  return under();
            }

            //newt:
            return newt();
        }

        double under() {
            //under:
            //mtherr( "incbi", UNDERFLOW );
            x = 0.0;
            //goto done;
            return done();
        }

        double newt() {
            if (nflg != 0) {
                //goto done;
                return done();
            }

            nflg = 1;
            lgm = lgam(a+b) - lgam(a) - lgam(b);

            for( i=0; i<8; i++ ) {
                /* Compute the function at this point. */
                if( i != 0 ) {
                    y = incbet(a,b,x);
                }
                if( y < yl ) {
                    x = x0;
                    y = yl;
                } else if( y > yh ) {
                    x = x1;
                    y = yh;
                } else if( y < md_y0 ) {
                    x0 = x;
                    yl = y;
                } else {
                    x1 = x;
                    yh = y;
                }
                if(x == 1.0 || x == 0.0)
                    break;
                /* Compute the derivative of the function at this point. */
                d = (a - 1.0) * Math.log(x) + (b - 1.0) * Math.log(1.0-x) + lgm;
                if (d < MINLOG) {
                    //goto done;
                    return done();
                }
                if (d > MAXLOG) {
                    break;
                }
                d = Math.exp(d);
                /* Compute the step to the next approximation of x. */
                d = (y - md_y0)/d;
                xt = x - d;
                if (xt <= x0) {
                    y = (x - x0) / (x1 - x0);
                    xt = x0 + 0.5 * y * (x - x0);
                    if (xt <= 0.0) {
                        break;
                    }
                }
                if (xt >= x1) {
                    y = (x1 - x) / (x1 - x0);
                    xt = x1 - 0.5 * y * (x1 - x);
                    if( xt >= 1.0 ) {
                        break;
                    }
                }
                x = xt;
                if (fabs(d/x) < 128.0 * MACHEP) {
                    //goto done;
                    return done();
                }
            }
            /* Did not converge.  */
            dithresh = 256.0 * MACHEP;

            //goto ihalve;
            return ihalve();
        }

        double done() {
            if (rflg != 0) {
                if (x <= MACHEP) {
                    x = 1.0 - MACHEP;
                } else {
                    x = 1.0 - x;
                }
            }
            return x;
        }

    }

    private static double incbi(double aa, double bb, double yy0) {
        return new Incbi(aa, bb, yy0).get();
    }

    private static double incbet(double aa, double bb, double xx) {
        double a, b, t, x, xc, w, y;
        int flag;

        if (aa <= 0.0 || bb <= 0.0) {
            //goto domerr;
            throw new RuntimeException("incbet");
        }

        if ((xx <= 0.0) || (xx >= 1.0)) {
            if (xx == 0.0) {
                return (0.0);
            }
            if (xx == 1.0) {
                return (1.0);
            }
            //domerr:
            throw new RuntimeException("incbet");
            //return NPY_NAN;
        }

        flag = 0;
        if ((bb * xx) <= 1.0 && xx <= 0.95) {
            t = pseries(aa, bb, xx);
            //goto done;
            if (flag == 1) {
                if (t <= MACHEP) {
                    t = 1.0 - MACHEP;
                } else {
                    t = 1.0 - t;
                }
            }
            return (t);
        }

        w = 1.0 - xx;

        /* Reverse a and b if x is greater than the mean. */
        if (xx > (aa / (aa + bb))) {
            flag = 1;
            a = bb;
            b = aa;
            xc = xx;
            x = w;
        }
        else {
            a = aa;
            b = bb;
            xc = w;
            x = xx;
        }

        if (flag == 1 && (b * x) <= 1.0 && x <= 0.95) {
            t = pseries(a, b, x);
            //goto done;
            if (flag == 1) {
                if (t <= MACHEP) {
                    t = 1.0 - MACHEP;
                } else {
                    t = 1.0 - t;
                }
            }
            return (t);
        }

        /* Choose expansion for better convergence. */
        y = x * (a + b - 2.0) - (a - 1.0);
        if (y < 0.0) {
            w = incbcf(a, b, x);
        } else {
            w = incbd(a, b, x) / xc;
        }

        /* Multiply w by the factor
         * a      b   _             _     _
         * x  (1-x)   | (a+b) / ( a | (a) | (b) ) .   */

        y = a * Math.log(x);
        t = b * Math.log(xc);
        if ((a + b) < MAXGAM && fabs(y) < MAXLOG && fabs(t) < MAXLOG) {
            t = Math.pow(xc, b);
            t *= Math.pow(x, a);
            t /= a;
            t *= w;
            t *= 1.0 / beta(a, b);
            //goto done;
            if (flag == 1) {
                if (t <= MACHEP) {
                    t = 1.0 - MACHEP;
                } else {
                    t = 1.0 - t;
                }
            }
            return (t);

        }
        /* Resort to logarithms.  */
        y += t - lbeta(a,b);
        y += Math.log(w / a);
        if (y < MINLOG) {
            t = 0.0;
        } else {
            t = Math.exp(y);
        }

        //done:
        if (flag == 1) {
            if (t <= MACHEP) {
                t = 1.0 - MACHEP;
            } else {
                t = 1.0 - t;
            }
        }
        return (t);
    }

    private static double incbcf(double a, double b, double x) {
        double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
        double k1, k2, k3, k4, k5, k6, k7, k8;
        double r, t, ans, thresh;
        int n;

        k1 = a;
        k2 = a + b;
        k3 = a;
        k4 = a + 1.0;
        k5 = 1.0;
        k6 = b - 1.0;
        k7 = k4;
        k8 = a + 2.0;

        pkm2 = 0.0;
        qkm2 = 1.0;
        pkm1 = 1.0;
        qkm1 = 1.0;
        ans = 1.0;
        r = 1.0;
        n = 0;
        thresh = 3.0 * MACHEP;

        do {
            xk = -(x * k1 * k2) / (k3 * k4);
            pk = pkm1 + pkm2 * xk;
            qk = qkm1 + qkm2 * xk;
            pkm2 = pkm1;
            pkm1 = pk;
            qkm2 = qkm1;
            qkm1 = qk;

            xk = (x * k5 * k6) / (k7 * k8);
            pk = pkm1 + pkm2 * xk;
            qk = qkm1 + qkm2 * xk;
            pkm2 = pkm1;
            pkm1 = pk;
            qkm2 = qkm1;
            qkm1 = qk;

            if (qk != 0) {
                r = pk / qk;
            }
            if (r != 0) {
                t = fabs((ans - r) / r);
                ans = r;
            } else {
                t = 1.0;
            }

            if (t < thresh) {
                //goto cdone;
                return ans;
            }

            k1 += 1.0;
            k2 += 1.0;
            k3 += 2.0;
            k4 += 2.0;
            k5 += 1.0;
            k6 -= 1.0;
            k7 += 2.0;
            k8 += 2.0;

            if ((fabs(qk) + fabs(pk)) > BIG) {
                pkm2 *= BIGINV;
                pkm1 *= BIGINV;
                qkm2 *= BIGINV;
                qkm1 *= BIGINV;
            }
            if ((fabs(qk) < BIGINV) || (fabs(pk) < BIGINV)) {
                pkm2 *= BIG;
                pkm1 *= BIG;
                qkm2 *= BIG;
                qkm1 *= BIG;
            }
            n++;
        } while (n < 300);

        //cdone:
        return ans;
    }

    private static double incbd(double a, double b, double x) {
        double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
        double k1, k2, k3, k4, k5, k6, k7, k8;
        double r, t, ans, z, thresh;
        int n;

        k1 = a;
        k2 = b - 1.0;
        k3 = a;
        k4 = a + 1.0;
        k5 = 1.0;
        k6 = a + b;
        k7 = a + 1.0;
        k8 = a + 2.0;

        pkm2 = 0.0;
        qkm2 = 1.0;
        pkm1 = 1.0;
        qkm1 = 1.0;
        z = x / (1.0 - x);
        ans = 1.0;
        r = 1.0;
        n = 0;
        thresh = 3.0 * MACHEP;
        do {

            xk = -(z * k1 * k2) / (k3 * k4);
            pk = pkm1 + pkm2 * xk;
            qk = qkm1 + qkm2 * xk;
            pkm2 = pkm1;
            pkm1 = pk;
            qkm2 = qkm1;
            qkm1 = qk;

            xk = (z * k5 * k6) / (k7 * k8);
            pk = pkm1 + pkm2 * xk;
            qk = qkm1 + qkm2 * xk;
            pkm2 = pkm1;
            pkm1 = pk;
            qkm2 = qkm1;
            qkm1 = qk;

            if (qk != 0) {
                r = pk / qk;
            }
            if (r != 0) {
                t = fabs((ans - r) / r);
                ans = r;
            } else {
                t = 1.0;
            }

            if (t < thresh) {
                //goto cdone;
                return ans;
            }

            k1 += 1.0;
            k2 -= 1.0;
            k3 += 2.0;
            k4 += 2.0;
            k5 += 1.0;
            k6 += 1.0;
            k7 += 2.0;
            k8 += 2.0;

            if ((fabs(qk) + fabs(pk)) > BIG) {
                pkm2 *= BIGINV;
                pkm1 *= BIGINV;
                qkm2 *= BIGINV;
                qkm1 *= BIGINV;
            }
            if ((fabs(qk) < BIGINV) || (fabs(pk) < BIGINV)) {
                pkm2 *= BIG;
                pkm1 *= BIG;
                qkm2 *= BIG;
                qkm1 *= BIG;
            }
            n++;
        } while (n < 300);

        //cdone:
        return ans;
    }

/* Power series for incomplete beta integral.
 * Use when b*x is small and x not too close to 1.  */

    private static double pseries(double a, double b, double x) {
        double s, t, u, v, n, t1, z, ai;

        ai = 1.0 / a;
        u = (1.0 - b) * x;
        v = u / (a + 1.0);
        t1 = v;
        t = u;
        n = 2.0;
        s = 0.0;
        z = MACHEP * ai;
        while (fabs(v) > z) {
            u = (n - b) * x / n;
            t *= u;
            v = t / (a + n);
            s += v;
            n += 1.0;
        }
        s += t1;
        s += ai;

        u = a * Math.log(x);
        if ((a + b) < MAXGAM && fabs(u) < MAXLOG) {
            t = 1.0 / beta(a, b);
            s = s * t * Math.pow(x, a);
        }
        else {
            t = -lbeta(a,b) + u + Math.log(s);
            if (t < MINLOG) {
                s = 0.0;
            } else {
                s = Math.exp(t);
            }
        }
        return (s);
    }

    /* sqrt(2pi) */
    private static final double S2PI = 2.50662827463100050242E0;

    /* approximation for 0 <= |y - 0.5| <= 3/8 */
    private static final double P0[] = {
        -5.99633501014107895267E1,
        9.80010754185999661536E1,
        -5.66762857469070293439E1,
        1.39312609387279679503E1,
        -1.23916583867381258016E0,
    };

    private static final double Q0[] = {
        /* 1.00000000000000000000E0, */
        1.95448858338141759834E0,
        4.67627912898881538453E0,
        8.63602421390890590575E1,
        -2.25462687854119370527E2,
        2.00260212380060660359E2,
        -8.20372256168333339912E1,
        1.59056225126211695515E1,
        -1.18331621121330003142E0,
    };

    /* Approximation for interval z = sqrt(-2 log y ) between 2 and 8
     * i.e., y between exp(-2) = .135 and exp(-32) = 1.27e-14.
     */
    private static final double P1[] = {
        4.05544892305962419923E0,
        3.15251094599893866154E1,
        5.71628192246421288162E1,
        4.40805073893200834700E1,
        1.46849561928858024014E1,
        2.18663306850790267539E0,
        -1.40256079171354495875E-1,
        -3.50424626827848203418E-2,
        -8.57456785154685413611E-4,
    };

    private static final double Q1[] = {
        /*  1.00000000000000000000E0, */
        1.57799883256466749731E1,
        4.53907635128879210584E1,
        4.13172038254672030440E1,
        1.50425385692907503408E1,
        2.50464946208309415979E0,
        -1.42182922854787788574E-1,
        -3.80806407691578277194E-2,
        -9.33259480895457427372E-4,
    };

    /* Approximation for interval z = sqrt(-2 log y ) between 8 and 64
     * i.e., y between exp(-32) = 1.27e-14 and exp(-2048) = 3.67e-890.
     */

    private static final double P2[] = {
        3.23774891776946035970E0,
        6.91522889068984211695E0,
        3.93881025292474443415E0,
        1.33303460815807542389E0,
        2.01485389549179081538E-1,
        1.23716634817820021358E-2,
        3.01581553508235416007E-4,
        2.65806974686737550832E-6,
        6.23974539184983293730E-9,
    };

    private static final double Q2[] = {
        /*  1.00000000000000000000E0, */
        6.02427039364742014255E0,
        3.67983563856160859403E0,
        1.37702099489081330271E0,
        2.16236993594496635890E-1,
        1.34204006088543189037E-2,
        3.28014464682127739104E-4,
        2.89247864745380683936E-6,
        6.79019408009981274425E-9,
    };

    private static double ndtri(double y0) {
        double x, y, z, y2, x0, x1;
        int code;

        if (y0 <= 0.0) {
            throw new IllegalArgumentException("ndtri y0 <= 0");
            //return (-NPY_INFINITY);
        }
        if (y0 >= 1.0) {
            throw new IllegalArgumentException("ndtri y0 >= 1");
            //return (NPY_INFINITY);
        }
        code = 1;
        y = y0;
        if (y > (1.0 - 0.13533528323661269189)) {	/* 0.135... = exp(-2) */
            y = 1.0 - y;
            code = 0;
        }

        if (y > 0.13533528323661269189) {
            y -= 0.5;
            y2 = y * y;
            x = y + y * (y2 * polevl(y2, P0, 4) / p1evl(y2, Q0, 8));
            x *= S2PI;
            return (x);
        }

        x = Math.sqrt(-2.0 * Math.log(y));
        x0 = x - Math.log(x) / x;

        z = 1.0 / x;
        if (x < 8.0) {		/* y > exp(-32) = 1.2664165549e-14 */
            x1 = z * polevl(z, P1, 8) / p1evl(z, Q1, 8);
        } else {
            x1 = z * polevl(z, P2, 8) / p1evl(z, Q2, 8);
        }
        x = x0 - x1;
        if (code != 0) {
            x = -x;
        }
        return x;
    }

    private static double polevl(double x, double coef[], int N) {
        double ans;
        //double *p;
        int p = 0;

        //p = coef;
        //ans = *p++;
        ans = coef[p];
        p++;

        for (int i=0; i<N; i++) {
            //ans = ans * x + *p++;
            ans = ans * x + coef[p];
            p++;
        }

        return ans;
    }

    private static double p1evl(double x, double coef[], int N) {
        double ans;
        //double *p;
        int p = 0;

        //p = coef;
        //ans = x + *p++;
        ans = x + coef[p];
        p++;

        for (int i=1; i<N; i++) {
            //ans = ans * x + *p++;
            ans = ans * x + coef[p];
            p++;
        }

        return ans;
    }


    private static class Sign {
        private int value = 1;

        public Sign(int value) {
            this.value = value;
        }

        public void set(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }

        public void multiply(Sign s) {
            this.value *= s.get();
        }
    }

    private static final double ASYMP_FACTOR = 1e6;

    private static double beta(double a, double b) {
        double y;
        Sign sign = new Sign(1);

        if (a <= 0.0) {
            if (a == Math.floor(a)) {
                if (a == (int)a) {
                    return beta_negint((int)a, b);
                } else {
                    //goto overflow;
                    throw new RuntimeException("beta OVERFLOW");
                }
            }
        }

        if (b <= 0.0) {
            if (b == Math.floor(b)) {
                if (b == (int)b) {
                    return beta_negint((int)b, a);
                }
                else {
                    //goto overflow;
                    throw new RuntimeException("beta OVERFLOW");
                }
            }
        }

        if (fabs(a) < fabs(b)) {
            y = a;
            a = b;
            b = y;
        }

        if (fabs(a) > ASYMP_FACTOR * fabs(b) && a > ASYMP_FACTOR) {
            /* Avoid loss of precision in lgam(a + b) - lgam(a) */
            y = lbeta_asymp(a, b, sign);
            return sign.get() * Math.exp(y);
        }

        y = a + b;
        if (fabs(y) > MAXGAM || fabs(a) > MAXGAM || fabs(b) > MAXGAM) {
            Sign sgngam = new Sign(1);
            y = lgam_sgn(y, sgngam);
            sign.multiply(sgngam); /* keep track of the sign */
            y = lgam_sgn(b, sgngam) - y;
            sign.multiply(sgngam); /* keep track of the sign */
            y = lgam_sgn(a, sgngam) + y;
            sign.multiply(sgngam); /* keep track of the sign */
            if (y > MAXLOG) {
                //goto overflow;
                throw new RuntimeException("beta OVERFLOW");
            }
            return sign.get() * Math.exp(y);
        }

        y = Gamma(y);
        a = Gamma(a);
        b = Gamma(b);
        if (y == 0.0) {
            //goto overflow;
            throw new RuntimeException("beta OVERFLOW");
        }

        if (fabs(fabs(a) - fabs(y)) > fabs(fabs(b) - fabs(y))) {
            y = b / y;
            y *= a;
        }
        else {
            y = a / y;
            y *= b;
        }

        return (y);

//        overflow:
//            throw new RuntimeException("beta OVERFLOW");
//            //return (sign * NPY_INFINITY);
    }


    /* Natural log of |beta|. */
    private static double lbeta(double a, double b) {
        double y;
        Sign sign = new Sign(1);

        if (a <= 0.0) {
            if (a == Math.floor(a)) {
                if (a == (int)a) {
                    return lbeta_negint((int)a, b);
                } else {
                    //goto over;
                    throw new RuntimeException("lbeta overflow");
                }
            }
        }

        if (b <= 0.0) {
            if (b == Math.floor(b)) {
                if (b == (int)b) {
                    return lbeta_negint((int)b, a);
                } else {
                    //goto over;
                    throw new RuntimeException("lbeta overflow");
                }
            }
        }

        if (fabs(a) < fabs(b)) {
            y = a;
            a = b;
            b = y;
        }

        if (fabs(a) > ASYMP_FACTOR * fabs(b) && a > ASYMP_FACTOR) {
            /* Avoid loss of precision in lgam(a + b) - lgam(a) */

            y = lbeta_asymp(a, b, sign);
            return y;
        }

        y = a + b;
        if (fabs(y) > MAXGAM || fabs(a) > MAXGAM || fabs(b) > MAXGAM) {
            Sign sgngam = new Sign(1);
            y = lgam_sgn(y, sgngam);
            sign.multiply(sgngam); /* keep track of the sign */
            y = lgam_sgn(b, sgngam) - y;
            sign.multiply(sgngam); /* keep track of the sign */
            y = lgam_sgn(a, sgngam) + y;
            sign.multiply(sgngam); /* keep track of the sign */
            return y;
        }

        y = Gamma(y);
        a = Gamma(a);
        b = Gamma(b);
        if (y == 0.0) {
            //over:
            throw new RuntimeException("lbeta overflow");
            //return (sign * NPY_INFINITY);
        }

        if (fabs(fabs(a) - fabs(y)) > fabs(fabs(b) - fabs(y))) {
            y = b / y;
            y *= a;
        }
        else {
            y = a / y;
            y *= b;
        }

        if (y < 0) {
            y = -y;
        }

        return (Math.log(y));
    }

    /*
     * Asymptotic expansion for  ln(|B(a, b)|) for a > ASYMP_FACTOR*max(|b|, 1).
     */
    private static double lbeta_asymp(double a, double b, Sign sgn) {
        double r = lgam_sgn(b, sgn);
        r -= b * Math.log(a);

        r += b*(1-b)/(2*a);
        r += b*(1-b)*(1-2*b)/(12*a*a);
        r += - b*b*(1-b)*(1-b)/(12*a*a*a);

        return r;
    }


    /*
     * Special case for a negative integer argument
     */
    private static double beta_negint(int a, double b) {
        int sgn;
        if (b == (int)b && 1 - a - b > 0) {
            sgn = ((int)b % 2 == 0) ? 1 : -1;
            return sgn * beta(1 - a - b, b);
        } else {
            throw new RuntimeException("lbeta overflow");
            //return NPY_INFINITY;
        }
    }

    private static double lbeta_negint(int a, double b) {
        double r;
        if (b == (int)b && 1 - a - b > 0) {
            r = lbeta(1 - a - b, b);
            return r;
        } else {
            throw new RuntimeException("lbeta overflow");
            //return NPY_INFINITY;
        }
    }

    private static final double P[] = {
        1.60119522476751861407E-4,
        1.19135147006586384913E-3,
        1.04213797561761569935E-2,
        4.76367800457137231464E-2,
        2.07448227648435975150E-1,
        4.94214826801497100753E-1,
        9.99999999999999996796E-1
    };

    private static final double Q[] = {
        -2.31581873324120129819E-5,
        5.39605580493303397842E-4,
        -4.45641913851797240494E-3,
        1.18139785222060435552E-2,
        3.58236398605498653373E-2,
        -2.34591795718243348568E-1,
        7.14304917030273074085E-2,
        1.00000000000000000320E0
    };

    //#define MAXGAM 171.624376956302725
    private static final double LOGPI = 1.14472988584940017414;

    /* Stirling's formula for the Gamma function */
    private static final double STIR[] = {
        7.87311395793093628397E-4,
        -2.29549961613378126380E-4,
        -2.68132617805781232825E-3,
        3.47222221605458667310E-3,
        8.33333333333482257126E-2,
    };

    private static final double MAXSTIR = 143.01608;
    private static final double SQTPI = 2.50662827463100050242E0;

    /* Gamma function computed by Stirling's formula.
     * The polynomial STIR is valid for 33 <= x <= 172.
     */
    private static double stirf(double x) {
        double y, w, v;

        if (x >= MAXGAM) {
            return (NPY_INFINITY);
        }
        w = 1.0 / x;
        w = 1.0 + w * polevl(w, STIR, 4);
        y = Math.exp(x);
        if (x > MAXSTIR) {		/* Avoid overflow in pow() */
            v = Math.pow(x, 0.5 * x - 0.25);
            y = v * (v / y);
        }
        else {
            y = Math.pow(x, x - 0.5) / y;
        }
        y = SQTPI * y * w;
        return (y);
    }

    private static double Gamma(double x) {
        double p, q, z;
        int i;
        int sgngam = 1;

        if (Double.isInfinite(x) || Double.isNaN(x)) {
            return x;
        }
        q = fabs(x);

        if (q > 33.0) {
            if (x < 0.0) {
                p = Math.floor(q);
                if (p == q) {
                    //gamnan:
                    throw new RuntimeException("Gamma overflow");
                    //return (NPY_INFINITY);
                }
                i = (int)p;
                if ((i & 1) == 0) {
                    sgngam = -1;
                }
                z = q - p;
                if (z > 0.5) {
                    p += 1.0;
                    z = q - p;
                }
                z = q * Math.sin(Math.PI * z);
                if (z == 0.0) {
                    return (sgngam * NPY_INFINITY);
                }
                z = fabs(z);
                z = Math.PI / (z * stirf(q));
            }
            else {
                z = stirf(x);
            }
            return (sgngam * z);
        }

        z = 1.0;
        while (x >= 3.0) {
            x -= 1.0;
            z *= x;
        }

        while (x < 0.0) {
            if (x > -1.E-9) {
                //goto small;
                if (x == 0.0) {
                    //goto gamnan;
                    throw new RuntimeException("Gamma overflow");
                } else {
                    return z / ((1.0 + 0.5772156649015329 * x) * x);
                }
            }
            z /= x;
            x += 1.0;
        }

        while (x < 2.0) {
            if (x < 1.e-9) {
                //goto small;
                if (x == 0.0) {
                    //goto gamnan;
                    throw new RuntimeException("Gamma overflow");
                } else {
                    return z / ((1.0 + 0.5772156649015329 * x) * x);
                }
            }
            z /= x;
            x += 1.0;
        }

        if (x == 2.0)
            return (z);

        x -= 2.0;
        p = polevl(x, P, 6);
        q = polevl(x, Q, 7);
        return (z * p / q);

//        small:
//        if (x == 0.0) {
//            //goto gamnan;
//            throw new RuntimeException("Gamma overflow");
//        } else {
//            return z / ((1.0 + 0.5772156649015329 * x) * x);
//        }
    }



    /* A[]: Stirling's formula expansion of log Gamma
     * B[], C[]: log Gamma function between 2 and 3
     */
    private static final double A[] = {
        8.11614167470508450300E-4,
        -5.95061904284301438324E-4,
        7.93650340457716943945E-4,
        -2.77777777730099687205E-3,
        8.33333333333331927722E-2
    };

    private static final double B[] = {
        -1.37825152569120859100E3,
        -3.88016315134637840924E4,
        -3.31612992738871184744E5,
        -1.16237097492762307383E6,
        -1.72173700820839662146E6,
        -8.53555664245765465627E5
    };

    private static final double C[] = {
        /* 1.00000000000000000000E0, */
        -3.51815701436523470549E2,
        -1.70642106651881159223E4,
        -2.20528590553854454839E5,
        -1.13933444367982507207E6,
        -2.53252307177582951285E6,
        -2.01889141433532773231E6
    };

    /* log( sqrt( 2*pi ) ) */
    private static final double LS2PI = 0.91893853320467274178;

    private static final double MAXLGM = 2.556348e305;

    /* Logarithm of Gamma function */
    private static double lgam(double x) {
        //int sign;
        return lgam_sgn(x, new Sign(1));
    }

    private static double lgam_sgn(double x, Sign sign) {
        double p, q, u, w, z;
        int i;

        sign.set(1);

        if (Double.isInfinite(x) || Double.isNaN(x)) {
            return x;
        }

        if (x < -34.0) {
            q = -x;
            w = lgam_sgn(q, sign);
            p = Math.floor(q);
            if (p == q) {
                //lgsing:
                throw new RuntimeException("lgam overflow");
                //return (NPY_INFINITY);
            }
            i = (int) p;
            if ((i & 1) == 0) {
                sign.set(-1);
            } else {
                sign.set(1);
            }
            z = q - p;
            if (z > 0.5) {
                p += 1.0;
                z = p - q;
            }
            z = q * Math.sin(Math.PI * z);
            if (z == 0.0) {
                //goto lgsing;
                throw new RuntimeException("lgam overflow");
                //return (NPY_INFINITY);
            }
            /*     z = log(NPY_PI) - log( z ) - w; */
            z = LOGPI - Math.log(z) - w;
            return (z);
        }

        if (x < 13.0) {
            z = 1.0;
            p = 0.0;
            u = x;
            while (u >= 3.0) {
                p -= 1.0;
                u = x + p;
                z *= u;
            }
            while (u < 2.0) {
                if (u == 0.0) {
                    //goto lgsing;
                    throw new RuntimeException("lgam overflow");
                    //return (NPY_INFINITY);
                }
                z /= u;
                p += 1.0;
                u = x + p;
            }
            if (z < 0.0) {
                sign.set(-1);
                z = -z;
            } else {
                sign.set(1);
            }
            if (u == 2.0) {
                return Math.log(z);
            }
            p -= 2.0;
            x += p;
            p = x * polevl(x, B, 5) / p1evl(x, C, 6);
            return Math.log(z) + p;
        }

        if (x > MAXLGM) {
            return (sign.get() * NPY_INFINITY);
        }

        q = (x - 0.5) * Math.log(x) - x + LS2PI;
        if (x > 1.0e8) {
            return (q);
        }

        p = 1.0 / (x * x);
        if (x >= 1000.0) {
            q += ((7.9365079365079365079365e-4 * p
                   - 2.7777777777777777777778e-3) * p
                  + 0.0833333333333333333333) / x;
        } else {
            q += polevl(p, A, 4) / x;
        }
        return q;
    }
}
