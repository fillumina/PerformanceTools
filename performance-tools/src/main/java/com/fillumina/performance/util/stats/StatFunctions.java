package com.fillumina.performance.util.stats;

/**
 * @see <a href='http://statpages.info/pdfs.html'>
 * Probability Distribution Functions by John C. Pezzullo</a>
 */
public class StatFunctions {

    /*
    var Pi=Math.PI; var PiD2=Pi/2; var PiD4=Pi/4; var Pi2=2*Pi
    var e=2.718281828459045235; var e10 = 1.105170918075647625
    var Deg=180/Pi
     */
    private static final double Pi = Math.PI;
    private static final double PiD2 = Pi / 2;

    /*
    function ChiSq(x,n) {
        var fn=Math.floor(n); var cn=Math.ceil(n)
        if( fn!=n ) {
        Xf = Math.log( ChiSq(x,fn) ); Xc = Math.log( ChiSq(x,cn) )
        return Math.exp( (cn-n)*Xf + (n-fn)*Xc )
        }
        if(x>1000 | n>1000) {
            var q=Norm((Math.pow(x/n,1/3)+2/(9*n)-1)/Math.sqrt(2/(9*n)))/2;
            if (x>n)
                {return q}
                {return 1-q}
        }
        var p=Math.exp(-0.5*x); if((n%2)==1) { p=p*Math.sqrt(2*x/Pi) }
        var k=n; while(k>=2) { p=p*x/k; k=k-2 }
        var t=p; var a=n; while(t>1e-15*p) { a=a+2; t=t*x/a; p=p+t }
        return 1-p
    }
    */
    public static double chiSq(double x, double n) {
        double fn = Math.floor(n);
        double cn = Math.ceil(n);
        if (fn != n) {
            double Xf = Math.log(chiSq(x, fn));
            double Xc = Math.log(chiSq(x, cn));
            return Math.exp((cn - n) * Xf + (n - fn) * Xc);
        }
        if (x > 1000 || n > 1000) {
            double q = norm((Math.pow(x / n, 1 / 3) + 2 / (9 * n) - 1) /
                    Math.sqrt(2 / (9 * n))) / 2;
            if (x > n) {
                return q;
            } else {
                return 1 - q;
            }
        }
        double p = Math.exp(-0.5 * x);
        if ((n % 2) == 1) {
            p *= Math.sqrt(2 * x / Pi);
        }
        double k = n;
        while (k >= 2) {
            p *= x / k;
            k -= 2;
        }
        double t = p;
        double a = n;
        while (t > 1E-15 * p) {
            a += 2;
            t *= x / a;
            p += t;
        }
        return 1 - p;
    }

    /*
    function Norm(z) {
        var q=z*z
        if(Math.abs(z)>7) {
            return (1-1/q+3/(q*q))*Math.exp(-q/2)/(Math.abs(z)*Math.sqrt(PiD2))
        }
        {return ChiSq(q,1) }
    }
     */
    public static double norm(double z) {
        double q = z * z;
        if (Math.abs(z) > 7) {
            return (1 - 1 / q + 3 / (q * q)) *
                    Math.exp(-q / 2) / (Math.abs(z) * Math.sqrt(PiD2));
        }
        return chiSq(q, 1);
    }

    /*
    function StudT(t,n) {
        var fn=Math.floor(n); var cn=Math.ceil(n)
        if( fn!=n ) {
            Tf = Math.log( StudT(t,fn) ); Tc = Math.log( StudT(t,cn) )
            return Math.exp( (cn-n)*Tf + (n-fn)*Tc )
        }
        t=Math.abs(t); var w=t/Math.sqrt(n); var th=Math.atan(w)
        if(n==1) { return 1-th/PiD2 }
        var sth=Math.sin(th); var cth=Math.cos(th)
        if((n%2)==1)
        { return 1-(th+sth*cth*StatCom(cth*cth,2,n-3,-1))/PiD2 }
        else
        { return 1-sth*StatCom(cth*cth,1,n-3,-1) }
    }
     */
    public static double studT(double t, double n) {
        double fn = Math.floor(n);
        double cn = Math.ceil(n);
        if (fn != n) {
            double Tf = Math.log(studT(t, fn));
            double Tc = Math.log(studT(t, cn));
            return Math.exp((cn - n) * Tf + (n - fn) * Tc);
        }
        double tt = Math.abs(t);
        double w = tt / Math.sqrt(n);
        double th = Math.atan(w);
        if (n == 1) {
            return 1 - th / PiD2;
        }
        double sth = Math.sin(th);
        double cth = Math.cos(th);
        if ((n % 2) == 1) {
            return 1 - (th + sth * cth * statCom(cth * cth, 2, n - 3, -1)) /
                    PiD2;
        } else {
            return 1 - sth * statCom(cth * cth, 1, n - 3, -1);
        }
    }

    /*
    function FishF(f,n1,n2) {
        var fn1=Math.floor(n1); var cn1=Math.ceil(n1); var fn2=Math.floor(n2); var cn2=Math.ceil(n2)
        if( fn1!=n1 | fn2!=n2 ) {
        Fff = Math.log( FishF(f,fn1,fn2) ); Ffc = Math.log( FishF(f,fn1,cn2) )
        Fcf = Math.log( FishF(f,cn1,fn2) ); Fcc = Math.log( FishF(f,cn1,cn2) )
        return Math.exp((cn1-n1)*(cn2-n2)*Fff+(n1-fn1)*(cn2-n2)*Fcf+(cn1-n1)*(n2-fn2)*Ffc+(n1-fn1)*(n2-fn2)*Fcc)
        }
        var x=n2/(n1*f+n2)
        if((n1%2)==0) { return StatCom(1-x,n2,n1+n2-4,n2-2)*Math.pow(x,n2/2) }
        if((n2%2)==0){ return 1-StatCom(x,n1,n1+n2-4,n1-2)*Math.pow(1-x,n1/2) }
        var th=Math.atan(Math.sqrt(n1*f/n2)); var a=th/PiD2; var sth=Math.sin(th); var cth=Math.cos(th)
        if(n2>1) { a=a+sth*cth*StatCom(cth*cth,2,n2-3,-1)/PiD2 }
        if(n1==1) { return 1-a }
        var c=4*StatCom(sth*sth,n2+1,n1+n2-4,n2-2)*sth*Math.pow(cth,n2)/Pi
        if(n2==1) { return 1-a+c/2 }
        var k=2; while(k<=(n2-1)/2) {c=c*k/(k-.5); k=k+1 }
        return 1-a+c
    }
     */
    public static double fishF(double f, double n1, double n2) {
        double fn1 = Math.floor(n1);
        double cn1 = Math.ceil(n1);
        double fn2 = Math.floor(n2);
        double cn2 = Math.ceil(n2);
        if (fn1 != n1 || fn2 != n2) {
            double Fff = Math.log(fishF(f, fn1, fn2));
            double Ffc = Math.log(fishF(f, fn1, cn2));
            double Fcf = Math.log(fishF(f, cn1, fn2));
            double Fcc = Math.log(fishF(f, cn1, cn2));
            return Math.exp(
                    (cn1 - n1) * (cn2 - n2) * Fff +
                    (n1 - fn1) * (cn2 - n2) * Fcf +
                    (cn1 - n1) * (n2 - fn2) * Ffc +
                    (n1 - fn1) * (n2 - fn2) * Fcc);
        }
        double x = n2 / (n1 * f + n2);
        if ((n1 % 2) == 0) {
            return statCom(1 - x, n2, n1 + n2 - 4, n2 - 2) * Math.pow(x, n2 / 2);
        }
        if ((n2 % 2) == 0) {
            return 1 - statCom(x, n1, n1 + n2 - 4, n1 - 2) * Math.pow(1 - x,
                    n1 / 2);
        }
        double th = Math.atan(Math.sqrt(n1 * f / n2));
        double a = th / PiD2;
        double sth = Math.sin(th);
        double cth = Math.cos(th);
        if (n2 > 1) {
            a += sth * cth * statCom(cth * cth, 2, n2 - 3, -1) / PiD2;
        }
        if (n1 == 1) {
            return 1 - a;
        }
        double c = 4 * statCom(sth * sth, n2 + 1, n1 + n2 - 4, n2 - 2) *
                sth * Math.pow(cth, n2) / Pi;
        if (n2 == 1) {
            return 1 - a + c / 2;
        }
        double k = 2;
        while (k <= (n2 - 1) / 2) {
            c = c * k / (k - .5);
            k += 1;
        }
        return 1 - a + c;
    }

    /*
    function StatCom(q,i,j,b) {
        var zz=1; var z=zz; var k=i; while(k<=j) { zz=zz*q*k/(k-b); z=z+zz; k=k+2 }
        return z
    }
     */
    public static double statCom(double q, double i, double j, double b) {
        double zz = 1;
        double z = zz;
        double k = i;
        while (k <= j) {
            zz *= q * k / (k - b);
            z += zz;
            k += 2;
        }
        return z;
    }

    /** Two tails function. */
    public static double zeta(double t) {
        return inverseNorm(1 - t);
    }

    /*
    function ANorm(p) { var v=0.5; var dv=0.5; var z=0
        while(dv>1e-6) { z=1/v-1; dv=dv/2; if(Norm(z)>p) { v=v-dv } else { v=v+dv } }
        return z
    }
     */
    public static double inverseNorm(double p) {
        double v = 0.5;
        double dv = 0.5;
        double z = 0;
        while (dv > 1E-6) {
            z = 1 / v - 1;
            dv /= 2;
            if (norm(z) > p) {
                v -= dv;
            } else {
                v += dv;
            }
        }
        return z;
    }

    /*
    function AChiSq(p,n) { var v=0.5; var dv=0.5; var x=0
        while(dv>1e-10) { x=1/v-1; dv=dv/2; if(ChiSq(x,n)>p) { v=v-dv } else { v=v+dv } }
        return x
    }
     */
    public static double inverseChiSq(double p, double n) {
        double v = 0.5;
        double dv = 0.5;
        double x = 0;
        while (dv > 1e-10) {
            x = 1 / v - 1;
            dv /= 2;
            if (chiSq(x, n) > p) {
                v -= dv;
            } else {
                v += dv;
            }
        }
        return x;
    }

    /** Two tails function. */
    public static double student(double p, double n) {
        return inverseStudT(1 - p, n);
    }

    /*

    function AStudT(p,n) { var v=0.5; var dv=0.5; var t=0
        while(dv>1e-6) { t=1/v-1; dv=dv/2; if(StudT(t,n)>p) { v=v-dv } else { v=v+dv } }
        return t
    }
     */
    public static double inverseStudT(double p, double n) {
        double v = 0.5;
        double dv = 0.5;
        double t = 0;
        while (dv > 1E-6) {
            t = 1 / v - 1;
            dv /= 2;
            if (studT(t, n) > p) {
                v -= dv;
            } else {
                v += dv;
            }
        }
        return t;
    }

    /*

    function AFishF(p,n1,n2) { var v=0.5; var dv=0.5; var f=0
        while(dv>1e-10) { f=1/v-1; dv=dv/2; if(FishF(f,n1,n2)>p) { v=v-dv } else { v=v+dv } }
        return f
    }
     */
    public static double inverseFishF(double p, double n1, double n2) {
        double v = 0.5;
        double dv = 0.5;
        double f = 0;
        while (dv > 1E-10) {
            f = 1 / v - 1;
            dv /= 2;
            if (fishF(f, n1, n2) > p) {
                v -= dv;
            } else {
                v += dv;
            }
        }
        return f;
    }

}
