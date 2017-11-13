package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.util.Holder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JVM optimizes a code after a certain number of executions
 * (about 100_000 as default).
 * But if the same looping code is used with a different payload
 * the JVM must unoptimize it. If multiple tests use the same loop they
 * share a critical code that will be optimized and de-optimized
 * influencing the measurement.
 * To avoid that a different looping code is used for every
 * different {@link Runnable}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public final class RunnableIterator {
    private static final int MAX_RUNNABLE = 1024;

    public final static Dispatcher DISPATCHER = new Dispatcher();

    static class Dispatcher {
        private final AtomicInteger counter = new AtomicInteger();
        private final Map<Object, RunnableIterator> map =
                new IdentityHashMap<>(MAX_RUNNABLE);

        /* test */ int getCounter() {
            return counter.get();
        }

        /* test */ int getIndexFor(Object obj) {
            return map.get(obj).index;
        }

        public synchronized RunnableIterator getIterator(Runnable runnable) {
            RunnableIterator runnableIterator = map.get(runnable);
            if (runnableIterator == null) {
                int index = counter.getAndIncrement();
                if (index >= MAX_RUNNABLE) {
                    throw new IllegalStateException(
                        "maximum number of runnable reached = " + MAX_RUNNABLE);
                }
                runnableIterator = new RunnableIterator(runnable, index);
                map.put(runnable, runnableIterator);
            }
            return runnableIterator;
        }
    }

    private final Runnable runnable;
    private final int index;

    private RunnableIterator(Runnable runnable, int index) {
        this.runnable = runnable;
        this.index = index;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RunnableIterator other = (RunnableIterator) obj;
        return this.index == other.index;
    }

    /**
     * Iterates over the {@link Runnable#run() } method of {@link Runnable}
     * for {@code iterations} times and returns how many nanoseconds it takes.
     * <p>
     * <b>IMPORTANT:</b> each {@link Runnable} must be registered using
     * {@link #getIterator(Runnable) } before iterating.
     *
     * @param runnable    the run to measureIterationTime
     * @param iterations  number of iterations
     * @return the elapsed nanoseconds
     */
    public long measureIterationTimeNs(int iterations) {
        final long time = System.nanoTime();
        iterate(iterations);
        return System.nanoTime() - time;
    }

    /**
     * It's the same as {@link #measureIterationTimeNs(int) } but executing
     * the test in a separate thread. This helps avoiding premature
     * optimizations from the JVM.
     *
     * @param iterations
     * @return
     */
    public long measureIterationTimeNsInNewThread(final int iterations) {
        Holder.Long elapsed = new Holder.Long(-1);
        final Thread thread = new Thread(runnable) {
            @Override
            public void run() {
                final long startTime = System.nanoTime();
                iterate(iterations);
                elapsed.setValue(System.nanoTime() - startTime);
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return elapsed.getValue();
    }

    public void iterate(int iterations) {
        switch (index) {
            case 0: l_0(runnable, iterations); break;
            case 1: l_1(runnable, iterations); break;
            case 2: l_2(runnable, iterations); break;
            case 3: l_3(runnable, iterations); break;
            case 4: l_4(runnable, iterations); break;
            case 5: l_5(runnable, iterations); break;
            case 6: l_6(runnable, iterations); break;
            case 7: l_7(runnable, iterations); break;
            case 8: l_8(runnable, iterations); break;
            case 9: l_9(runnable, iterations); break;
            case 10: l_10(runnable, iterations); break;
            case 11: l_11(runnable, iterations); break;
            case 12: l_12(runnable, iterations); break;
            case 13: l_13(runnable, iterations); break;
            case 14: l_14(runnable, iterations); break;
            case 15: l_15(runnable, iterations); break;
            case 16: l_16(runnable, iterations); break;
            case 17: l_17(runnable, iterations); break;
            case 18: l_18(runnable, iterations); break;
            case 19: l_19(runnable, iterations); break;
            case 20: l_20(runnable, iterations); break;
            case 21: l_21(runnable, iterations); break;
            case 22: l_22(runnable, iterations); break;
            case 23: l_23(runnable, iterations); break;
            case 24: l_24(runnable, iterations); break;
            case 25: l_25(runnable, iterations); break;
            case 26: l_26(runnable, iterations); break;
            case 27: l_27(runnable, iterations); break;
            case 28: l_28(runnable, iterations); break;
            case 29: l_29(runnable, iterations); break;
            case 30: l_30(runnable, iterations); break;
            case 31: l_31(runnable, iterations); break;
            case 32: l_32(runnable, iterations); break;
            case 33: l_33(runnable, iterations); break;
            case 34: l_34(runnable, iterations); break;
            case 35: l_35(runnable, iterations); break;
            case 36: l_36(runnable, iterations); break;
            case 37: l_37(runnable, iterations); break;
            case 38: l_38(runnable, iterations); break;
            case 39: l_39(runnable, iterations); break;
            case 40: l_40(runnable, iterations); break;
            case 41: l_41(runnable, iterations); break;
            case 42: l_42(runnable, iterations); break;
            case 43: l_43(runnable, iterations); break;
            case 44: l_44(runnable, iterations); break;
            case 45: l_45(runnable, iterations); break;
            case 46: l_46(runnable, iterations); break;
            case 47: l_47(runnable, iterations); break;
            case 48: l_48(runnable, iterations); break;
            case 49: l_49(runnable, iterations); break;
            case 50: l_50(runnable, iterations); break;
            case 51: l_51(runnable, iterations); break;
            case 52: l_52(runnable, iterations); break;
            case 53: l_53(runnable, iterations); break;
            case 54: l_54(runnable, iterations); break;
            case 55: l_55(runnable, iterations); break;
            case 56: l_56(runnable, iterations); break;
            case 57: l_57(runnable, iterations); break;
            case 58: l_58(runnable, iterations); break;
            case 59: l_59(runnable, iterations); break;
            case 60: l_60(runnable, iterations); break;
            case 61: l_61(runnable, iterations); break;
            case 62: l_62(runnable, iterations); break;
            case 63: l_63(runnable, iterations); break;
            case 64: l_64(runnable, iterations); break;
            case 65: l_65(runnable, iterations); break;
            case 66: l_66(runnable, iterations); break;
            case 67: l_67(runnable, iterations); break;
            case 68: l_68(runnable, iterations); break;
            case 69: l_69(runnable, iterations); break;
            case 70: l_70(runnable, iterations); break;
            case 71: l_71(runnable, iterations); break;
            case 72: l_72(runnable, iterations); break;
            case 73: l_73(runnable, iterations); break;
            case 74: l_74(runnable, iterations); break;
            case 75: l_75(runnable, iterations); break;
            case 76: l_76(runnable, iterations); break;
            case 77: l_77(runnable, iterations); break;
            case 78: l_78(runnable, iterations); break;
            case 79: l_79(runnable, iterations); break;
            case 80: l_80(runnable, iterations); break;
            case 81: l_81(runnable, iterations); break;
            case 82: l_82(runnable, iterations); break;
            case 83: l_83(runnable, iterations); break;
            case 84: l_84(runnable, iterations); break;
            case 85: l_85(runnable, iterations); break;
            case 86: l_86(runnable, iterations); break;
            case 87: l_87(runnable, iterations); break;
            case 88: l_88(runnable, iterations); break;
            case 89: l_89(runnable, iterations); break;
            case 90: l_90(runnable, iterations); break;
            case 91: l_91(runnable, iterations); break;
            case 92: l_92(runnable, iterations); break;
            case 93: l_93(runnable, iterations); break;
            case 94: l_94(runnable, iterations); break;
            case 95: l_95(runnable, iterations); break;
            case 96: l_96(runnable, iterations); break;
            case 97: l_97(runnable, iterations); break;
            case 98: l_98(runnable, iterations); break;
            case 99: l_99(runnable, iterations); break;
            case 100: l_100(runnable, iterations); break;
            case 101: l_101(runnable, iterations); break;
            case 102: l_102(runnable, iterations); break;
            case 103: l_103(runnable, iterations); break;
            case 104: l_104(runnable, iterations); break;
            case 105: l_105(runnable, iterations); break;
            case 106: l_106(runnable, iterations); break;
            case 107: l_107(runnable, iterations); break;
            case 108: l_108(runnable, iterations); break;
            case 109: l_109(runnable, iterations); break;
            case 110: l_110(runnable, iterations); break;
            case 111: l_111(runnable, iterations); break;
            case 112: l_112(runnable, iterations); break;
            case 113: l_113(runnable, iterations); break;
            case 114: l_114(runnable, iterations); break;
            case 115: l_115(runnable, iterations); break;
            case 116: l_116(runnable, iterations); break;
            case 117: l_117(runnable, iterations); break;
            case 118: l_118(runnable, iterations); break;
            case 119: l_119(runnable, iterations); break;
            case 120: l_120(runnable, iterations); break;
            case 121: l_121(runnable, iterations); break;
            case 122: l_122(runnable, iterations); break;
            case 123: l_123(runnable, iterations); break;
            case 124: l_124(runnable, iterations); break;
            case 125: l_125(runnable, iterations); break;
            case 126: l_126(runnable, iterations); break;
            case 127: l_127(runnable, iterations); break;
            case 128: l_128(runnable, iterations); break;
            case 129: l_129(runnable, iterations); break;
            case 130: l_130(runnable, iterations); break;
            case 131: l_131(runnable, iterations); break;
            case 132: l_132(runnable, iterations); break;
            case 133: l_133(runnable, iterations); break;
            case 134: l_134(runnable, iterations); break;
            case 135: l_135(runnable, iterations); break;
            case 136: l_136(runnable, iterations); break;
            case 137: l_137(runnable, iterations); break;
            case 138: l_138(runnable, iterations); break;
            case 139: l_139(runnable, iterations); break;
            case 140: l_140(runnable, iterations); break;
            case 141: l_141(runnable, iterations); break;
            case 142: l_142(runnable, iterations); break;
            case 143: l_143(runnable, iterations); break;
            case 144: l_144(runnable, iterations); break;
            case 145: l_145(runnable, iterations); break;
            case 146: l_146(runnable, iterations); break;
            case 147: l_147(runnable, iterations); break;
            case 148: l_148(runnable, iterations); break;
            case 149: l_149(runnable, iterations); break;
            case 150: l_150(runnable, iterations); break;
            case 151: l_151(runnable, iterations); break;
            case 152: l_152(runnable, iterations); break;
            case 153: l_153(runnable, iterations); break;
            case 154: l_154(runnable, iterations); break;
            case 155: l_155(runnable, iterations); break;
            case 156: l_156(runnable, iterations); break;
            case 157: l_157(runnable, iterations); break;
            case 158: l_158(runnable, iterations); break;
            case 159: l_159(runnable, iterations); break;
            case 160: l_160(runnable, iterations); break;
            case 161: l_161(runnable, iterations); break;
            case 162: l_162(runnable, iterations); break;
            case 163: l_163(runnable, iterations); break;
            case 164: l_164(runnable, iterations); break;
            case 165: l_165(runnable, iterations); break;
            case 166: l_166(runnable, iterations); break;
            case 167: l_167(runnable, iterations); break;
            case 168: l_168(runnable, iterations); break;
            case 169: l_169(runnable, iterations); break;
            case 170: l_170(runnable, iterations); break;
            case 171: l_171(runnable, iterations); break;
            case 172: l_172(runnable, iterations); break;
            case 173: l_173(runnable, iterations); break;
            case 174: l_174(runnable, iterations); break;
            case 175: l_175(runnable, iterations); break;
            case 176: l_176(runnable, iterations); break;
            case 177: l_177(runnable, iterations); break;
            case 178: l_178(runnable, iterations); break;
            case 179: l_179(runnable, iterations); break;
            case 180: l_180(runnable, iterations); break;
            case 181: l_181(runnable, iterations); break;
            case 182: l_182(runnable, iterations); break;
            case 183: l_183(runnable, iterations); break;
            case 184: l_184(runnable, iterations); break;
            case 185: l_185(runnable, iterations); break;
            case 186: l_186(runnable, iterations); break;
            case 187: l_187(runnable, iterations); break;
            case 188: l_188(runnable, iterations); break;
            case 189: l_189(runnable, iterations); break;
            case 190: l_190(runnable, iterations); break;
            case 191: l_191(runnable, iterations); break;
            case 192: l_192(runnable, iterations); break;
            case 193: l_193(runnable, iterations); break;
            case 194: l_194(runnable, iterations); break;
            case 195: l_195(runnable, iterations); break;
            case 196: l_196(runnable, iterations); break;
            case 197: l_197(runnable, iterations); break;
            case 198: l_198(runnable, iterations); break;
            case 199: l_199(runnable, iterations); break;
            case 200: l_200(runnable, iterations); break;
            case 201: l_201(runnable, iterations); break;
            case 202: l_202(runnable, iterations); break;
            case 203: l_203(runnable, iterations); break;
            case 204: l_204(runnable, iterations); break;
            case 205: l_205(runnable, iterations); break;
            case 206: l_206(runnable, iterations); break;
            case 207: l_207(runnable, iterations); break;
            case 208: l_208(runnable, iterations); break;
            case 209: l_209(runnable, iterations); break;
            case 210: l_210(runnable, iterations); break;
            case 211: l_211(runnable, iterations); break;
            case 212: l_212(runnable, iterations); break;
            case 213: l_213(runnable, iterations); break;
            case 214: l_214(runnable, iterations); break;
            case 215: l_215(runnable, iterations); break;
            case 216: l_216(runnable, iterations); break;
            case 217: l_217(runnable, iterations); break;
            case 218: l_218(runnable, iterations); break;
            case 219: l_219(runnable, iterations); break;
            case 220: l_220(runnable, iterations); break;
            case 221: l_221(runnable, iterations); break;
            case 222: l_222(runnable, iterations); break;
            case 223: l_223(runnable, iterations); break;
            case 224: l_224(runnable, iterations); break;
            case 225: l_225(runnable, iterations); break;
            case 226: l_226(runnable, iterations); break;
            case 227: l_227(runnable, iterations); break;
            case 228: l_228(runnable, iterations); break;
            case 229: l_229(runnable, iterations); break;
            case 230: l_230(runnable, iterations); break;
            case 231: l_231(runnable, iterations); break;
            case 232: l_232(runnable, iterations); break;
            case 233: l_233(runnable, iterations); break;
            case 234: l_234(runnable, iterations); break;
            case 235: l_235(runnable, iterations); break;
            case 236: l_236(runnable, iterations); break;
            case 237: l_237(runnable, iterations); break;
            case 238: l_238(runnable, iterations); break;
            case 239: l_239(runnable, iterations); break;
            case 240: l_240(runnable, iterations); break;
            case 241: l_241(runnable, iterations); break;
            case 242: l_242(runnable, iterations); break;
            case 243: l_243(runnable, iterations); break;
            case 244: l_244(runnable, iterations); break;
            case 245: l_245(runnable, iterations); break;
            case 246: l_246(runnable, iterations); break;
            case 247: l_247(runnable, iterations); break;
            case 248: l_248(runnable, iterations); break;
            case 249: l_249(runnable, iterations); break;
            case 250: l_250(runnable, iterations); break;
            case 251: l_251(runnable, iterations); break;
            case 252: l_252(runnable, iterations); break;
            case 253: l_253(runnable, iterations); break;
            case 254: l_254(runnable, iterations); break;
            case 255: l_255(runnable, iterations); break;
            case 256: l_256(runnable, iterations); break;
            case 257: l_257(runnable, iterations); break;
            case 258: l_258(runnable, iterations); break;
            case 259: l_259(runnable, iterations); break;
            case 260: l_260(runnable, iterations); break;
            case 261: l_261(runnable, iterations); break;
            case 262: l_262(runnable, iterations); break;
            case 263: l_263(runnable, iterations); break;
            case 264: l_264(runnable, iterations); break;
            case 265: l_265(runnable, iterations); break;
            case 266: l_266(runnable, iterations); break;
            case 267: l_267(runnable, iterations); break;
            case 268: l_268(runnable, iterations); break;
            case 269: l_269(runnable, iterations); break;
            case 270: l_270(runnable, iterations); break;
            case 271: l_271(runnable, iterations); break;
            case 272: l_272(runnable, iterations); break;
            case 273: l_273(runnable, iterations); break;
            case 274: l_274(runnable, iterations); break;
            case 275: l_275(runnable, iterations); break;
            case 276: l_276(runnable, iterations); break;
            case 277: l_277(runnable, iterations); break;
            case 278: l_278(runnable, iterations); break;
            case 279: l_279(runnable, iterations); break;
            case 280: l_280(runnable, iterations); break;
            case 281: l_281(runnable, iterations); break;
            case 282: l_282(runnable, iterations); break;
            case 283: l_283(runnable, iterations); break;
            case 284: l_284(runnable, iterations); break;
            case 285: l_285(runnable, iterations); break;
            case 286: l_286(runnable, iterations); break;
            case 287: l_287(runnable, iterations); break;
            case 288: l_288(runnable, iterations); break;
            case 289: l_289(runnable, iterations); break;
            case 290: l_290(runnable, iterations); break;
            case 291: l_291(runnable, iterations); break;
            case 292: l_292(runnable, iterations); break;
            case 293: l_293(runnable, iterations); break;
            case 294: l_294(runnable, iterations); break;
            case 295: l_295(runnable, iterations); break;
            case 296: l_296(runnable, iterations); break;
            case 297: l_297(runnable, iterations); break;
            case 298: l_298(runnable, iterations); break;
            case 299: l_299(runnable, iterations); break;
            case 300: l_300(runnable, iterations); break;
            case 301: l_301(runnable, iterations); break;
            case 302: l_302(runnable, iterations); break;
            case 303: l_303(runnable, iterations); break;
            case 304: l_304(runnable, iterations); break;
            case 305: l_305(runnable, iterations); break;
            case 306: l_306(runnable, iterations); break;
            case 307: l_307(runnable, iterations); break;
            case 308: l_308(runnable, iterations); break;
            case 309: l_309(runnable, iterations); break;
            case 310: l_310(runnable, iterations); break;
            case 311: l_311(runnable, iterations); break;
            case 312: l_312(runnable, iterations); break;
            case 313: l_313(runnable, iterations); break;
            case 314: l_314(runnable, iterations); break;
            case 315: l_315(runnable, iterations); break;
            case 316: l_316(runnable, iterations); break;
            case 317: l_317(runnable, iterations); break;
            case 318: l_318(runnable, iterations); break;
            case 319: l_319(runnable, iterations); break;
            case 320: l_320(runnable, iterations); break;
            case 321: l_321(runnable, iterations); break;
            case 322: l_322(runnable, iterations); break;
            case 323: l_323(runnable, iterations); break;
            case 324: l_324(runnable, iterations); break;
            case 325: l_325(runnable, iterations); break;
            case 326: l_326(runnable, iterations); break;
            case 327: l_327(runnable, iterations); break;
            case 328: l_328(runnable, iterations); break;
            case 329: l_329(runnable, iterations); break;
            case 330: l_330(runnable, iterations); break;
            case 331: l_331(runnable, iterations); break;
            case 332: l_332(runnable, iterations); break;
            case 333: l_333(runnable, iterations); break;
            case 334: l_334(runnable, iterations); break;
            case 335: l_335(runnable, iterations); break;
            case 336: l_336(runnable, iterations); break;
            case 337: l_337(runnable, iterations); break;
            case 338: l_338(runnable, iterations); break;
            case 339: l_339(runnable, iterations); break;
            case 340: l_340(runnable, iterations); break;
            case 341: l_341(runnable, iterations); break;
            case 342: l_342(runnable, iterations); break;
            case 343: l_343(runnable, iterations); break;
            case 344: l_344(runnable, iterations); break;
            case 345: l_345(runnable, iterations); break;
            case 346: l_346(runnable, iterations); break;
            case 347: l_347(runnable, iterations); break;
            case 348: l_348(runnable, iterations); break;
            case 349: l_349(runnable, iterations); break;
            case 350: l_350(runnable, iterations); break;
            case 351: l_351(runnable, iterations); break;
            case 352: l_352(runnable, iterations); break;
            case 353: l_353(runnable, iterations); break;
            case 354: l_354(runnable, iterations); break;
            case 355: l_355(runnable, iterations); break;
            case 356: l_356(runnable, iterations); break;
            case 357: l_357(runnable, iterations); break;
            case 358: l_358(runnable, iterations); break;
            case 359: l_359(runnable, iterations); break;
            case 360: l_360(runnable, iterations); break;
            case 361: l_361(runnable, iterations); break;
            case 362: l_362(runnable, iterations); break;
            case 363: l_363(runnable, iterations); break;
            case 364: l_364(runnable, iterations); break;
            case 365: l_365(runnable, iterations); break;
            case 366: l_366(runnable, iterations); break;
            case 367: l_367(runnable, iterations); break;
            case 368: l_368(runnable, iterations); break;
            case 369: l_369(runnable, iterations); break;
            case 370: l_370(runnable, iterations); break;
            case 371: l_371(runnable, iterations); break;
            case 372: l_372(runnable, iterations); break;
            case 373: l_373(runnable, iterations); break;
            case 374: l_374(runnable, iterations); break;
            case 375: l_375(runnable, iterations); break;
            case 376: l_376(runnable, iterations); break;
            case 377: l_377(runnable, iterations); break;
            case 378: l_378(runnable, iterations); break;
            case 379: l_379(runnable, iterations); break;
            case 380: l_380(runnable, iterations); break;
            case 381: l_381(runnable, iterations); break;
            case 382: l_382(runnable, iterations); break;
            case 383: l_383(runnable, iterations); break;
            case 384: l_384(runnable, iterations); break;
            case 385: l_385(runnable, iterations); break;
            case 386: l_386(runnable, iterations); break;
            case 387: l_387(runnable, iterations); break;
            case 388: l_388(runnable, iterations); break;
            case 389: l_389(runnable, iterations); break;
            case 390: l_390(runnable, iterations); break;
            case 391: l_391(runnable, iterations); break;
            case 392: l_392(runnable, iterations); break;
            case 393: l_393(runnable, iterations); break;
            case 394: l_394(runnable, iterations); break;
            case 395: l_395(runnable, iterations); break;
            case 396: l_396(runnable, iterations); break;
            case 397: l_397(runnable, iterations); break;
            case 398: l_398(runnable, iterations); break;
            case 399: l_399(runnable, iterations); break;
            case 400: l_400(runnable, iterations); break;
            case 401: l_401(runnable, iterations); break;
            case 402: l_402(runnable, iterations); break;
            case 403: l_403(runnable, iterations); break;
            case 404: l_404(runnable, iterations); break;
            case 405: l_405(runnable, iterations); break;
            case 406: l_406(runnable, iterations); break;
            case 407: l_407(runnable, iterations); break;
            case 408: l_408(runnable, iterations); break;
            case 409: l_409(runnable, iterations); break;
            case 410: l_410(runnable, iterations); break;
            case 411: l_411(runnable, iterations); break;
            case 412: l_412(runnable, iterations); break;
            case 413: l_413(runnable, iterations); break;
            case 414: l_414(runnable, iterations); break;
            case 415: l_415(runnable, iterations); break;
            case 416: l_416(runnable, iterations); break;
            case 417: l_417(runnable, iterations); break;
            case 418: l_418(runnable, iterations); break;
            case 419: l_419(runnable, iterations); break;
            case 420: l_420(runnable, iterations); break;
            case 421: l_421(runnable, iterations); break;
            case 422: l_422(runnable, iterations); break;
            case 423: l_423(runnable, iterations); break;
            case 424: l_424(runnable, iterations); break;
            case 425: l_425(runnable, iterations); break;
            case 426: l_426(runnable, iterations); break;
            case 427: l_427(runnable, iterations); break;
            case 428: l_428(runnable, iterations); break;
            case 429: l_429(runnable, iterations); break;
            case 430: l_430(runnable, iterations); break;
            case 431: l_431(runnable, iterations); break;
            case 432: l_432(runnable, iterations); break;
            case 433: l_433(runnable, iterations); break;
            case 434: l_434(runnable, iterations); break;
            case 435: l_435(runnable, iterations); break;
            case 436: l_436(runnable, iterations); break;
            case 437: l_437(runnable, iterations); break;
            case 438: l_438(runnable, iterations); break;
            case 439: l_439(runnable, iterations); break;
            case 440: l_440(runnable, iterations); break;
            case 441: l_441(runnable, iterations); break;
            case 442: l_442(runnable, iterations); break;
            case 443: l_443(runnable, iterations); break;
            case 444: l_444(runnable, iterations); break;
            case 445: l_445(runnable, iterations); break;
            case 446: l_446(runnable, iterations); break;
            case 447: l_447(runnable, iterations); break;
            case 448: l_448(runnable, iterations); break;
            case 449: l_449(runnable, iterations); break;
            case 450: l_450(runnable, iterations); break;
            case 451: l_451(runnable, iterations); break;
            case 452: l_452(runnable, iterations); break;
            case 453: l_453(runnable, iterations); break;
            case 454: l_454(runnable, iterations); break;
            case 455: l_455(runnable, iterations); break;
            case 456: l_456(runnable, iterations); break;
            case 457: l_457(runnable, iterations); break;
            case 458: l_458(runnable, iterations); break;
            case 459: l_459(runnable, iterations); break;
            case 460: l_460(runnable, iterations); break;
            case 461: l_461(runnable, iterations); break;
            case 462: l_462(runnable, iterations); break;
            case 463: l_463(runnable, iterations); break;
            case 464: l_464(runnable, iterations); break;
            case 465: l_465(runnable, iterations); break;
            case 466: l_466(runnable, iterations); break;
            case 467: l_467(runnable, iterations); break;
            case 468: l_468(runnable, iterations); break;
            case 469: l_469(runnable, iterations); break;
            case 470: l_470(runnable, iterations); break;
            case 471: l_471(runnable, iterations); break;
            case 472: l_472(runnable, iterations); break;
            case 473: l_473(runnable, iterations); break;
            case 474: l_474(runnable, iterations); break;
            case 475: l_475(runnable, iterations); break;
            case 476: l_476(runnable, iterations); break;
            case 477: l_477(runnable, iterations); break;
            case 478: l_478(runnable, iterations); break;
            case 479: l_479(runnable, iterations); break;
            case 480: l_480(runnable, iterations); break;
            case 481: l_481(runnable, iterations); break;
            case 482: l_482(runnable, iterations); break;
            case 483: l_483(runnable, iterations); break;
            case 484: l_484(runnable, iterations); break;
            case 485: l_485(runnable, iterations); break;
            case 486: l_486(runnable, iterations); break;
            case 487: l_487(runnable, iterations); break;
            case 488: l_488(runnable, iterations); break;
            case 489: l_489(runnable, iterations); break;
            case 490: l_490(runnable, iterations); break;
            case 491: l_491(runnable, iterations); break;
            case 492: l_492(runnable, iterations); break;
            case 493: l_493(runnable, iterations); break;
            case 494: l_494(runnable, iterations); break;
            case 495: l_495(runnable, iterations); break;
            case 496: l_496(runnable, iterations); break;
            case 497: l_497(runnable, iterations); break;
            case 498: l_498(runnable, iterations); break;
            case 499: l_499(runnable, iterations); break;
            case 500: l_500(runnable, iterations); break;
            case 501: l_501(runnable, iterations); break;
            case 502: l_502(runnable, iterations); break;
            case 503: l_503(runnable, iterations); break;
            case 504: l_504(runnable, iterations); break;
            case 505: l_505(runnable, iterations); break;
            case 506: l_506(runnable, iterations); break;
            case 507: l_507(runnable, iterations); break;
            case 508: l_508(runnable, iterations); break;
            case 509: l_509(runnable, iterations); break;
            case 510: l_510(runnable, iterations); break;
            case 511: l_511(runnable, iterations); break;
            case 512: l_512(runnable, iterations); break;
            case 513: l_513(runnable, iterations); break;
            case 514: l_514(runnable, iterations); break;
            case 515: l_515(runnable, iterations); break;
            case 516: l_516(runnable, iterations); break;
            case 517: l_517(runnable, iterations); break;
            case 518: l_518(runnable, iterations); break;
            case 519: l_519(runnable, iterations); break;
            case 520: l_520(runnable, iterations); break;
            case 521: l_521(runnable, iterations); break;
            case 522: l_522(runnable, iterations); break;
            case 523: l_523(runnable, iterations); break;
            case 524: l_524(runnable, iterations); break;
            case 525: l_525(runnable, iterations); break;
            case 526: l_526(runnable, iterations); break;
            case 527: l_527(runnable, iterations); break;
            case 528: l_528(runnable, iterations); break;
            case 529: l_529(runnable, iterations); break;
            case 530: l_530(runnable, iterations); break;
            case 531: l_531(runnable, iterations); break;
            case 532: l_532(runnable, iterations); break;
            case 533: l_533(runnable, iterations); break;
            case 534: l_534(runnable, iterations); break;
            case 535: l_535(runnable, iterations); break;
            case 536: l_536(runnable, iterations); break;
            case 537: l_537(runnable, iterations); break;
            case 538: l_538(runnable, iterations); break;
            case 539: l_539(runnable, iterations); break;
            case 540: l_540(runnable, iterations); break;
            case 541: l_541(runnable, iterations); break;
            case 542: l_542(runnable, iterations); break;
            case 543: l_543(runnable, iterations); break;
            case 544: l_544(runnable, iterations); break;
            case 545: l_545(runnable, iterations); break;
            case 546: l_546(runnable, iterations); break;
            case 547: l_547(runnable, iterations); break;
            case 548: l_548(runnable, iterations); break;
            case 549: l_549(runnable, iterations); break;
            case 550: l_550(runnable, iterations); break;
            case 551: l_551(runnable, iterations); break;
            case 552: l_552(runnable, iterations); break;
            case 553: l_553(runnable, iterations); break;
            case 554: l_554(runnable, iterations); break;
            case 555: l_555(runnable, iterations); break;
            case 556: l_556(runnable, iterations); break;
            case 557: l_557(runnable, iterations); break;
            case 558: l_558(runnable, iterations); break;
            case 559: l_559(runnable, iterations); break;
            case 560: l_560(runnable, iterations); break;
            case 561: l_561(runnable, iterations); break;
            case 562: l_562(runnable, iterations); break;
            case 563: l_563(runnable, iterations); break;
            case 564: l_564(runnable, iterations); break;
            case 565: l_565(runnable, iterations); break;
            case 566: l_566(runnable, iterations); break;
            case 567: l_567(runnable, iterations); break;
            case 568: l_568(runnable, iterations); break;
            case 569: l_569(runnable, iterations); break;
            case 570: l_570(runnable, iterations); break;
            case 571: l_571(runnable, iterations); break;
            case 572: l_572(runnable, iterations); break;
            case 573: l_573(runnable, iterations); break;
            case 574: l_574(runnable, iterations); break;
            case 575: l_575(runnable, iterations); break;
            case 576: l_576(runnable, iterations); break;
            case 577: l_577(runnable, iterations); break;
            case 578: l_578(runnable, iterations); break;
            case 579: l_579(runnable, iterations); break;
            case 580: l_580(runnable, iterations); break;
            case 581: l_581(runnable, iterations); break;
            case 582: l_582(runnable, iterations); break;
            case 583: l_583(runnable, iterations); break;
            case 584: l_584(runnable, iterations); break;
            case 585: l_585(runnable, iterations); break;
            case 586: l_586(runnable, iterations); break;
            case 587: l_587(runnable, iterations); break;
            case 588: l_588(runnable, iterations); break;
            case 589: l_589(runnable, iterations); break;
            case 590: l_590(runnable, iterations); break;
            case 591: l_591(runnable, iterations); break;
            case 592: l_592(runnable, iterations); break;
            case 593: l_593(runnable, iterations); break;
            case 594: l_594(runnable, iterations); break;
            case 595: l_595(runnable, iterations); break;
            case 596: l_596(runnable, iterations); break;
            case 597: l_597(runnable, iterations); break;
            case 598: l_598(runnable, iterations); break;
            case 599: l_599(runnable, iterations); break;
            case 600: l_600(runnable, iterations); break;
            case 601: l_601(runnable, iterations); break;
            case 602: l_602(runnable, iterations); break;
            case 603: l_603(runnable, iterations); break;
            case 604: l_604(runnable, iterations); break;
            case 605: l_605(runnable, iterations); break;
            case 606: l_606(runnable, iterations); break;
            case 607: l_607(runnable, iterations); break;
            case 608: l_608(runnable, iterations); break;
            case 609: l_609(runnable, iterations); break;
            case 610: l_610(runnable, iterations); break;
            case 611: l_611(runnable, iterations); break;
            case 612: l_612(runnable, iterations); break;
            case 613: l_613(runnable, iterations); break;
            case 614: l_614(runnable, iterations); break;
            case 615: l_615(runnable, iterations); break;
            case 616: l_616(runnable, iterations); break;
            case 617: l_617(runnable, iterations); break;
            case 618: l_618(runnable, iterations); break;
            case 619: l_619(runnable, iterations); break;
            case 620: l_620(runnable, iterations); break;
            case 621: l_621(runnable, iterations); break;
            case 622: l_622(runnable, iterations); break;
            case 623: l_623(runnable, iterations); break;
            case 624: l_624(runnable, iterations); break;
            case 625: l_625(runnable, iterations); break;
            case 626: l_626(runnable, iterations); break;
            case 627: l_627(runnable, iterations); break;
            case 628: l_628(runnable, iterations); break;
            case 629: l_629(runnable, iterations); break;
            case 630: l_630(runnable, iterations); break;
            case 631: l_631(runnable, iterations); break;
            case 632: l_632(runnable, iterations); break;
            case 633: l_633(runnable, iterations); break;
            case 634: l_634(runnable, iterations); break;
            case 635: l_635(runnable, iterations); break;
            case 636: l_636(runnable, iterations); break;
            case 637: l_637(runnable, iterations); break;
            case 638: l_638(runnable, iterations); break;
            case 639: l_639(runnable, iterations); break;
            case 640: l_640(runnable, iterations); break;
            case 641: l_641(runnable, iterations); break;
            case 642: l_642(runnable, iterations); break;
            case 643: l_643(runnable, iterations); break;
            case 644: l_644(runnable, iterations); break;
            case 645: l_645(runnable, iterations); break;
            case 646: l_646(runnable, iterations); break;
            case 647: l_647(runnable, iterations); break;
            case 648: l_648(runnable, iterations); break;
            case 649: l_649(runnable, iterations); break;
            case 650: l_650(runnable, iterations); break;
            case 651: l_651(runnable, iterations); break;
            case 652: l_652(runnable, iterations); break;
            case 653: l_653(runnable, iterations); break;
            case 654: l_654(runnable, iterations); break;
            case 655: l_655(runnable, iterations); break;
            case 656: l_656(runnable, iterations); break;
            case 657: l_657(runnable, iterations); break;
            case 658: l_658(runnable, iterations); break;
            case 659: l_659(runnable, iterations); break;
            case 660: l_660(runnable, iterations); break;
            case 661: l_661(runnable, iterations); break;
            case 662: l_662(runnable, iterations); break;
            case 663: l_663(runnable, iterations); break;
            case 664: l_664(runnable, iterations); break;
            case 665: l_665(runnable, iterations); break;
            case 666: l_666(runnable, iterations); break;
            case 667: l_667(runnable, iterations); break;
            case 668: l_668(runnable, iterations); break;
            case 669: l_669(runnable, iterations); break;
            case 670: l_670(runnable, iterations); break;
            case 671: l_671(runnable, iterations); break;
            case 672: l_672(runnable, iterations); break;
            case 673: l_673(runnable, iterations); break;
            case 674: l_674(runnable, iterations); break;
            case 675: l_675(runnable, iterations); break;
            case 676: l_676(runnable, iterations); break;
            case 677: l_677(runnable, iterations); break;
            case 678: l_678(runnable, iterations); break;
            case 679: l_679(runnable, iterations); break;
            case 680: l_680(runnable, iterations); break;
            case 681: l_681(runnable, iterations); break;
            case 682: l_682(runnable, iterations); break;
            case 683: l_683(runnable, iterations); break;
            case 684: l_684(runnable, iterations); break;
            case 685: l_685(runnable, iterations); break;
            case 686: l_686(runnable, iterations); break;
            case 687: l_687(runnable, iterations); break;
            case 688: l_688(runnable, iterations); break;
            case 689: l_689(runnable, iterations); break;
            case 690: l_690(runnable, iterations); break;
            case 691: l_691(runnable, iterations); break;
            case 692: l_692(runnable, iterations); break;
            case 693: l_693(runnable, iterations); break;
            case 694: l_694(runnable, iterations); break;
            case 695: l_695(runnable, iterations); break;
            case 696: l_696(runnable, iterations); break;
            case 697: l_697(runnable, iterations); break;
            case 698: l_698(runnable, iterations); break;
            case 699: l_699(runnable, iterations); break;
            case 700: l_700(runnable, iterations); break;
            case 701: l_701(runnable, iterations); break;
            case 702: l_702(runnable, iterations); break;
            case 703: l_703(runnable, iterations); break;
            case 704: l_704(runnable, iterations); break;
            case 705: l_705(runnable, iterations); break;
            case 706: l_706(runnable, iterations); break;
            case 707: l_707(runnable, iterations); break;
            case 708: l_708(runnable, iterations); break;
            case 709: l_709(runnable, iterations); break;
            case 710: l_710(runnable, iterations); break;
            case 711: l_711(runnable, iterations); break;
            case 712: l_712(runnable, iterations); break;
            case 713: l_713(runnable, iterations); break;
            case 714: l_714(runnable, iterations); break;
            case 715: l_715(runnable, iterations); break;
            case 716: l_716(runnable, iterations); break;
            case 717: l_717(runnable, iterations); break;
            case 718: l_718(runnable, iterations); break;
            case 719: l_719(runnable, iterations); break;
            case 720: l_720(runnable, iterations); break;
            case 721: l_721(runnable, iterations); break;
            case 722: l_722(runnable, iterations); break;
            case 723: l_723(runnable, iterations); break;
            case 724: l_724(runnable, iterations); break;
            case 725: l_725(runnable, iterations); break;
            case 726: l_726(runnable, iterations); break;
            case 727: l_727(runnable, iterations); break;
            case 728: l_728(runnable, iterations); break;
            case 729: l_729(runnable, iterations); break;
            case 730: l_730(runnable, iterations); break;
            case 731: l_731(runnable, iterations); break;
            case 732: l_732(runnable, iterations); break;
            case 733: l_733(runnable, iterations); break;
            case 734: l_734(runnable, iterations); break;
            case 735: l_735(runnable, iterations); break;
            case 736: l_736(runnable, iterations); break;
            case 737: l_737(runnable, iterations); break;
            case 738: l_738(runnable, iterations); break;
            case 739: l_739(runnable, iterations); break;
            case 740: l_740(runnable, iterations); break;
            case 741: l_741(runnable, iterations); break;
            case 742: l_742(runnable, iterations); break;
            case 743: l_743(runnable, iterations); break;
            case 744: l_744(runnable, iterations); break;
            case 745: l_745(runnable, iterations); break;
            case 746: l_746(runnable, iterations); break;
            case 747: l_747(runnable, iterations); break;
            case 748: l_748(runnable, iterations); break;
            case 749: l_749(runnable, iterations); break;
            case 750: l_750(runnable, iterations); break;
            case 751: l_751(runnable, iterations); break;
            case 752: l_752(runnable, iterations); break;
            case 753: l_753(runnable, iterations); break;
            case 754: l_754(runnable, iterations); break;
            case 755: l_755(runnable, iterations); break;
            case 756: l_756(runnable, iterations); break;
            case 757: l_757(runnable, iterations); break;
            case 758: l_758(runnable, iterations); break;
            case 759: l_759(runnable, iterations); break;
            case 760: l_760(runnable, iterations); break;
            case 761: l_761(runnable, iterations); break;
            case 762: l_762(runnable, iterations); break;
            case 763: l_763(runnable, iterations); break;
            case 764: l_764(runnable, iterations); break;
            case 765: l_765(runnable, iterations); break;
            case 766: l_766(runnable, iterations); break;
            case 767: l_767(runnable, iterations); break;
            case 768: l_768(runnable, iterations); break;
            case 769: l_769(runnable, iterations); break;
            case 770: l_770(runnable, iterations); break;
            case 771: l_771(runnable, iterations); break;
            case 772: l_772(runnable, iterations); break;
            case 773: l_773(runnable, iterations); break;
            case 774: l_774(runnable, iterations); break;
            case 775: l_775(runnable, iterations); break;
            case 776: l_776(runnable, iterations); break;
            case 777: l_777(runnable, iterations); break;
            case 778: l_778(runnable, iterations); break;
            case 779: l_779(runnable, iterations); break;
            case 780: l_780(runnable, iterations); break;
            case 781: l_781(runnable, iterations); break;
            case 782: l_782(runnable, iterations); break;
            case 783: l_783(runnable, iterations); break;
            case 784: l_784(runnable, iterations); break;
            case 785: l_785(runnable, iterations); break;
            case 786: l_786(runnable, iterations); break;
            case 787: l_787(runnable, iterations); break;
            case 788: l_788(runnable, iterations); break;
            case 789: l_789(runnable, iterations); break;
            case 790: l_790(runnable, iterations); break;
            case 791: l_791(runnable, iterations); break;
            case 792: l_792(runnable, iterations); break;
            case 793: l_793(runnable, iterations); break;
            case 794: l_794(runnable, iterations); break;
            case 795: l_795(runnable, iterations); break;
            case 796: l_796(runnable, iterations); break;
            case 797: l_797(runnable, iterations); break;
            case 798: l_798(runnable, iterations); break;
            case 799: l_799(runnable, iterations); break;
            case 800: l_800(runnable, iterations); break;
            case 801: l_801(runnable, iterations); break;
            case 802: l_802(runnable, iterations); break;
            case 803: l_803(runnable, iterations); break;
            case 804: l_804(runnable, iterations); break;
            case 805: l_805(runnable, iterations); break;
            case 806: l_806(runnable, iterations); break;
            case 807: l_807(runnable, iterations); break;
            case 808: l_808(runnable, iterations); break;
            case 809: l_809(runnable, iterations); break;
            case 810: l_810(runnable, iterations); break;
            case 811: l_811(runnable, iterations); break;
            case 812: l_812(runnable, iterations); break;
            case 813: l_813(runnable, iterations); break;
            case 814: l_814(runnable, iterations); break;
            case 815: l_815(runnable, iterations); break;
            case 816: l_816(runnable, iterations); break;
            case 817: l_817(runnable, iterations); break;
            case 818: l_818(runnable, iterations); break;
            case 819: l_819(runnable, iterations); break;
            case 820: l_820(runnable, iterations); break;
            case 821: l_821(runnable, iterations); break;
            case 822: l_822(runnable, iterations); break;
            case 823: l_823(runnable, iterations); break;
            case 824: l_824(runnable, iterations); break;
            case 825: l_825(runnable, iterations); break;
            case 826: l_826(runnable, iterations); break;
            case 827: l_827(runnable, iterations); break;
            case 828: l_828(runnable, iterations); break;
            case 829: l_829(runnable, iterations); break;
            case 830: l_830(runnable, iterations); break;
            case 831: l_831(runnable, iterations); break;
            case 832: l_832(runnable, iterations); break;
            case 833: l_833(runnable, iterations); break;
            case 834: l_834(runnable, iterations); break;
            case 835: l_835(runnable, iterations); break;
            case 836: l_836(runnable, iterations); break;
            case 837: l_837(runnable, iterations); break;
            case 838: l_838(runnable, iterations); break;
            case 839: l_839(runnable, iterations); break;
            case 840: l_840(runnable, iterations); break;
            case 841: l_841(runnable, iterations); break;
            case 842: l_842(runnable, iterations); break;
            case 843: l_843(runnable, iterations); break;
            case 844: l_844(runnable, iterations); break;
            case 845: l_845(runnable, iterations); break;
            case 846: l_846(runnable, iterations); break;
            case 847: l_847(runnable, iterations); break;
            case 848: l_848(runnable, iterations); break;
            case 849: l_849(runnable, iterations); break;
            case 850: l_850(runnable, iterations); break;
            case 851: l_851(runnable, iterations); break;
            case 852: l_852(runnable, iterations); break;
            case 853: l_853(runnable, iterations); break;
            case 854: l_854(runnable, iterations); break;
            case 855: l_855(runnable, iterations); break;
            case 856: l_856(runnable, iterations); break;
            case 857: l_857(runnable, iterations); break;
            case 858: l_858(runnable, iterations); break;
            case 859: l_859(runnable, iterations); break;
            case 860: l_860(runnable, iterations); break;
            case 861: l_861(runnable, iterations); break;
            case 862: l_862(runnable, iterations); break;
            case 863: l_863(runnable, iterations); break;
            case 864: l_864(runnable, iterations); break;
            case 865: l_865(runnable, iterations); break;
            case 866: l_866(runnable, iterations); break;
            case 867: l_867(runnable, iterations); break;
            case 868: l_868(runnable, iterations); break;
            case 869: l_869(runnable, iterations); break;
            case 870: l_870(runnable, iterations); break;
            case 871: l_871(runnable, iterations); break;
            case 872: l_872(runnable, iterations); break;
            case 873: l_873(runnable, iterations); break;
            case 874: l_874(runnable, iterations); break;
            case 875: l_875(runnable, iterations); break;
            case 876: l_876(runnable, iterations); break;
            case 877: l_877(runnable, iterations); break;
            case 878: l_878(runnable, iterations); break;
            case 879: l_879(runnable, iterations); break;
            case 880: l_880(runnable, iterations); break;
            case 881: l_881(runnable, iterations); break;
            case 882: l_882(runnable, iterations); break;
            case 883: l_883(runnable, iterations); break;
            case 884: l_884(runnable, iterations); break;
            case 885: l_885(runnable, iterations); break;
            case 886: l_886(runnable, iterations); break;
            case 887: l_887(runnable, iterations); break;
            case 888: l_888(runnable, iterations); break;
            case 889: l_889(runnable, iterations); break;
            case 890: l_890(runnable, iterations); break;
            case 891: l_891(runnable, iterations); break;
            case 892: l_892(runnable, iterations); break;
            case 893: l_893(runnable, iterations); break;
            case 894: l_894(runnable, iterations); break;
            case 895: l_895(runnable, iterations); break;
            case 896: l_896(runnable, iterations); break;
            case 897: l_897(runnable, iterations); break;
            case 898: l_898(runnable, iterations); break;
            case 899: l_899(runnable, iterations); break;
            case 900: l_900(runnable, iterations); break;
            case 901: l_901(runnable, iterations); break;
            case 902: l_902(runnable, iterations); break;
            case 903: l_903(runnable, iterations); break;
            case 904: l_904(runnable, iterations); break;
            case 905: l_905(runnable, iterations); break;
            case 906: l_906(runnable, iterations); break;
            case 907: l_907(runnable, iterations); break;
            case 908: l_908(runnable, iterations); break;
            case 909: l_909(runnable, iterations); break;
            case 910: l_910(runnable, iterations); break;
            case 911: l_911(runnable, iterations); break;
            case 912: l_912(runnable, iterations); break;
            case 913: l_913(runnable, iterations); break;
            case 914: l_914(runnable, iterations); break;
            case 915: l_915(runnable, iterations); break;
            case 916: l_916(runnable, iterations); break;
            case 917: l_917(runnable, iterations); break;
            case 918: l_918(runnable, iterations); break;
            case 919: l_919(runnable, iterations); break;
            case 920: l_920(runnable, iterations); break;
            case 921: l_921(runnable, iterations); break;
            case 922: l_922(runnable, iterations); break;
            case 923: l_923(runnable, iterations); break;
            case 924: l_924(runnable, iterations); break;
            case 925: l_925(runnable, iterations); break;
            case 926: l_926(runnable, iterations); break;
            case 927: l_927(runnable, iterations); break;
            case 928: l_928(runnable, iterations); break;
            case 929: l_929(runnable, iterations); break;
            case 930: l_930(runnable, iterations); break;
            case 931: l_931(runnable, iterations); break;
            case 932: l_932(runnable, iterations); break;
            case 933: l_933(runnable, iterations); break;
            case 934: l_934(runnable, iterations); break;
            case 935: l_935(runnable, iterations); break;
            case 936: l_936(runnable, iterations); break;
            case 937: l_937(runnable, iterations); break;
            case 938: l_938(runnable, iterations); break;
            case 939: l_939(runnable, iterations); break;
            case 940: l_940(runnable, iterations); break;
            case 941: l_941(runnable, iterations); break;
            case 942: l_942(runnable, iterations); break;
            case 943: l_943(runnable, iterations); break;
            case 944: l_944(runnable, iterations); break;
            case 945: l_945(runnable, iterations); break;
            case 946: l_946(runnable, iterations); break;
            case 947: l_947(runnable, iterations); break;
            case 948: l_948(runnable, iterations); break;
            case 949: l_949(runnable, iterations); break;
            case 950: l_950(runnable, iterations); break;
            case 951: l_951(runnable, iterations); break;
            case 952: l_952(runnable, iterations); break;
            case 953: l_953(runnable, iterations); break;
            case 954: l_954(runnable, iterations); break;
            case 955: l_955(runnable, iterations); break;
            case 956: l_956(runnable, iterations); break;
            case 957: l_957(runnable, iterations); break;
            case 958: l_958(runnable, iterations); break;
            case 959: l_959(runnable, iterations); break;
            case 960: l_960(runnable, iterations); break;
            case 961: l_961(runnable, iterations); break;
            case 962: l_962(runnable, iterations); break;
            case 963: l_963(runnable, iterations); break;
            case 964: l_964(runnable, iterations); break;
            case 965: l_965(runnable, iterations); break;
            case 966: l_966(runnable, iterations); break;
            case 967: l_967(runnable, iterations); break;
            case 968: l_968(runnable, iterations); break;
            case 969: l_969(runnable, iterations); break;
            case 970: l_970(runnable, iterations); break;
            case 971: l_971(runnable, iterations); break;
            case 972: l_972(runnable, iterations); break;
            case 973: l_973(runnable, iterations); break;
            case 974: l_974(runnable, iterations); break;
            case 975: l_975(runnable, iterations); break;
            case 976: l_976(runnable, iterations); break;
            case 977: l_977(runnable, iterations); break;
            case 978: l_978(runnable, iterations); break;
            case 979: l_979(runnable, iterations); break;
            case 980: l_980(runnable, iterations); break;
            case 981: l_981(runnable, iterations); break;
            case 982: l_982(runnable, iterations); break;
            case 983: l_983(runnable, iterations); break;
            case 984: l_984(runnable, iterations); break;
            case 985: l_985(runnable, iterations); break;
            case 986: l_986(runnable, iterations); break;
            case 987: l_987(runnable, iterations); break;
            case 988: l_988(runnable, iterations); break;
            case 989: l_989(runnable, iterations); break;
            case 990: l_990(runnable, iterations); break;
            case 991: l_991(runnable, iterations); break;
            case 992: l_992(runnable, iterations); break;
            case 993: l_993(runnable, iterations); break;
            case 994: l_994(runnable, iterations); break;
            case 995: l_995(runnable, iterations); break;
            case 996: l_996(runnable, iterations); break;
            case 997: l_997(runnable, iterations); break;
            case 998: l_998(runnable, iterations); break;
            case 999: l_999(runnable, iterations); break;
            case 1000: l_1000(runnable, iterations); break;
            case 1001: l_1001(runnable, iterations); break;
            case 1002: l_1002(runnable, iterations); break;
            case 1003: l_1003(runnable, iterations); break;
            case 1004: l_1004(runnable, iterations); break;
            case 1005: l_1005(runnable, iterations); break;
            case 1006: l_1006(runnable, iterations); break;
            case 1007: l_1007(runnable, iterations); break;
            case 1008: l_1008(runnable, iterations); break;
            case 1009: l_1009(runnable, iterations); break;
            case 1010: l_1010(runnable, iterations); break;
            case 1011: l_1011(runnable, iterations); break;
            case 1012: l_1012(runnable, iterations); break;
            case 1013: l_1013(runnable, iterations); break;
            case 1014: l_1014(runnable, iterations); break;
            case 1015: l_1015(runnable, iterations); break;
            case 1016: l_1016(runnable, iterations); break;
            case 1017: l_1017(runnable, iterations); break;
            case 1018: l_1018(runnable, iterations); break;
            case 1019: l_1019(runnable, iterations); break;
            case 1020: l_1020(runnable, iterations); break;
            case 1021: l_1021(runnable, iterations); break;
            case 1022: l_1022(runnable, iterations); break;
            case 1023: l_1023(runnable, iterations); break;
            default:
                throw new IllegalStateException(
                        "too many classes tested: " + index);
        }
    }

    private void l_0(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_2(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_3(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_4(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_5(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_6(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_7(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_8(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_9(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_10(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_11(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_12(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_13(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_14(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_15(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_16(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_17(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_18(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_19(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_20(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_21(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_22(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_23(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_24(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_25(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_26(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_27(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_28(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_29(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_30(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_31(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_32(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_33(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_34(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_35(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_36(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_37(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_38(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_39(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_40(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_41(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_42(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_43(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_44(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_45(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_46(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_47(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_48(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_49(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_50(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_51(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_52(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_53(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_54(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_55(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_56(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_57(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_58(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_59(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_60(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_61(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_62(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_63(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_64(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_65(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_66(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_67(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_68(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_69(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_70(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_71(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_72(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_73(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_74(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_75(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_76(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_77(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_78(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_79(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_80(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_81(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_82(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_83(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_84(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_85(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_86(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_87(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_88(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_89(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_90(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_91(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_92(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_93(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_94(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_95(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_96(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_97(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_98(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_99(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_100(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_101(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_102(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_103(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_104(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_105(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_106(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_107(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_108(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_109(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_110(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_111(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_112(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_113(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_114(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_115(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_116(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_117(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_118(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_119(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_120(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_121(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_122(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_123(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_124(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_125(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_126(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_127(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_128(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_129(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_130(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_131(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_132(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_133(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_134(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_135(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_136(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_137(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_138(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_139(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_140(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_141(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_142(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_143(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_144(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_145(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_146(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_147(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_148(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_149(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_150(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_151(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_152(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_153(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_154(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_155(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_156(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_157(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_158(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_159(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_160(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_161(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_162(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_163(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_164(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_165(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_166(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_167(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_168(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_169(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_170(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_171(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_172(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_173(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_174(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_175(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_176(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_177(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_178(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_179(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_180(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_181(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_182(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_183(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_184(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_185(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_186(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_187(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_188(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_189(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_190(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_191(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_192(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_193(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_194(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_195(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_196(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_197(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_198(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_199(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_200(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_201(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_202(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_203(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_204(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_205(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_206(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_207(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_208(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_209(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_210(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_211(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_212(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_213(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_214(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_215(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_216(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_217(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_218(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_219(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_220(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_221(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_222(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_223(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_224(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_225(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_226(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_227(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_228(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_229(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_230(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_231(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_232(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_233(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_234(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_235(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_236(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_237(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_238(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_239(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_240(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_241(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_242(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_243(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_244(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_245(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_246(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_247(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_248(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_249(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_250(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_251(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_252(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_253(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_254(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_255(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_256(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_257(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_258(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_259(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_260(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_261(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_262(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_263(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_264(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_265(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_266(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_267(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_268(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_269(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_270(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_271(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_272(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_273(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_274(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_275(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_276(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_277(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_278(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_279(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_280(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_281(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_282(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_283(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_284(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_285(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_286(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_287(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_288(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_289(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_290(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_291(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_292(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_293(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_294(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_295(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_296(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_297(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_298(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_299(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_300(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_301(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_302(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_303(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_304(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_305(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_306(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_307(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_308(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_309(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_310(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_311(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_312(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_313(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_314(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_315(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_316(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_317(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_318(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_319(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_320(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_321(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_322(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_323(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_324(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_325(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_326(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_327(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_328(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_329(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_330(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_331(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_332(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_333(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_334(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_335(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_336(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_337(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_338(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_339(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_340(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_341(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_342(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_343(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_344(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_345(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_346(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_347(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_348(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_349(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_350(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_351(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_352(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_353(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_354(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_355(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_356(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_357(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_358(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_359(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_360(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_361(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_362(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_363(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_364(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_365(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_366(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_367(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_368(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_369(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_370(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_371(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_372(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_373(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_374(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_375(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_376(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_377(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_378(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_379(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_380(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_381(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_382(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_383(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_384(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_385(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_386(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_387(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_388(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_389(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_390(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_391(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_392(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_393(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_394(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_395(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_396(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_397(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_398(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_399(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_400(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_401(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_402(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_403(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_404(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_405(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_406(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_407(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_408(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_409(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_410(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_411(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_412(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_413(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_414(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_415(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_416(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_417(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_418(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_419(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_420(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_421(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_422(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_423(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_424(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_425(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_426(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_427(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_428(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_429(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_430(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_431(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_432(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_433(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_434(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_435(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_436(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_437(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_438(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_439(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_440(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_441(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_442(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_443(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_444(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_445(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_446(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_447(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_448(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_449(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_450(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_451(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_452(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_453(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_454(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_455(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_456(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_457(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_458(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_459(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_460(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_461(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_462(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_463(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_464(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_465(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_466(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_467(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_468(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_469(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_470(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_471(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_472(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_473(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_474(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_475(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_476(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_477(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_478(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_479(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_480(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_481(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_482(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_483(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_484(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_485(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_486(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_487(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_488(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_489(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_490(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_491(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_492(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_493(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_494(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_495(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_496(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_497(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_498(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_499(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_500(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_501(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_502(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_503(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_504(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_505(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_506(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_507(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_508(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_509(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_510(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_511(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_512(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_513(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_514(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_515(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_516(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_517(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_518(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_519(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_520(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_521(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_522(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_523(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_524(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_525(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_526(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_527(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_528(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_529(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_530(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_531(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_532(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_533(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_534(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_535(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_536(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_537(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_538(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_539(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_540(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_541(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_542(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_543(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_544(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_545(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_546(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_547(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_548(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_549(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_550(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_551(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_552(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_553(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_554(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_555(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_556(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_557(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_558(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_559(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_560(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_561(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_562(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_563(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_564(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_565(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_566(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_567(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_568(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_569(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_570(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_571(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_572(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_573(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_574(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_575(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_576(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_577(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_578(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_579(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_580(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_581(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_582(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_583(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_584(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_585(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_586(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_587(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_588(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_589(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_590(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_591(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_592(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_593(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_594(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_595(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_596(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_597(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_598(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_599(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_600(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_601(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_602(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_603(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_604(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_605(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_606(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_607(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_608(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_609(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_610(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_611(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_612(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_613(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_614(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_615(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_616(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_617(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_618(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_619(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_620(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_621(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_622(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_623(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_624(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_625(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_626(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_627(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_628(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_629(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_630(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_631(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_632(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_633(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_634(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_635(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_636(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_637(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_638(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_639(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_640(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_641(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_642(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_643(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_644(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_645(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_646(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_647(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_648(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_649(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_650(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_651(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_652(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_653(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_654(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_655(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_656(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_657(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_658(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_659(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_660(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_661(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_662(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_663(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_664(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_665(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_666(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_667(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_668(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_669(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_670(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_671(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_672(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_673(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_674(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_675(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_676(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_677(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_678(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_679(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_680(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_681(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_682(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_683(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_684(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_685(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_686(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_687(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_688(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_689(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_690(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_691(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_692(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_693(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_694(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_695(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_696(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_697(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_698(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_699(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_700(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_701(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_702(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_703(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_704(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_705(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_706(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_707(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_708(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_709(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_710(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_711(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_712(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_713(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_714(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_715(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_716(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_717(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_718(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_719(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_720(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_721(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_722(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_723(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_724(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_725(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_726(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_727(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_728(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_729(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_730(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_731(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_732(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_733(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_734(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_735(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_736(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_737(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_738(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_739(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_740(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_741(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_742(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_743(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_744(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_745(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_746(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_747(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_748(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_749(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_750(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_751(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_752(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_753(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_754(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_755(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_756(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_757(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_758(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_759(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_760(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_761(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_762(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_763(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_764(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_765(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_766(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_767(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_768(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_769(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_770(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_771(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_772(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_773(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_774(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_775(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_776(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_777(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_778(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_779(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_780(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_781(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_782(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_783(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_784(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_785(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_786(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_787(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_788(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_789(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_790(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_791(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_792(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_793(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_794(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_795(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_796(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_797(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_798(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_799(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_800(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_801(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_802(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_803(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_804(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_805(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_806(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_807(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_808(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_809(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_810(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_811(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_812(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_813(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_814(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_815(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_816(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_817(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_818(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_819(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_820(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_821(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_822(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_823(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_824(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_825(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_826(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_827(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_828(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_829(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_830(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_831(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_832(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_833(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_834(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_835(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_836(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_837(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_838(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_839(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_840(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_841(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_842(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_843(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_844(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_845(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_846(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_847(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_848(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_849(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_850(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_851(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_852(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_853(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_854(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_855(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_856(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_857(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_858(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_859(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_860(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_861(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_862(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_863(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_864(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_865(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_866(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_867(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_868(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_869(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_870(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_871(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_872(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_873(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_874(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_875(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_876(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_877(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_878(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_879(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_880(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_881(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_882(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_883(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_884(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_885(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_886(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_887(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_888(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_889(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_890(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_891(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_892(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_893(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_894(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_895(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_896(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_897(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_898(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_899(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_900(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_901(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_902(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_903(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_904(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_905(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_906(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_907(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_908(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_909(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_910(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_911(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_912(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_913(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_914(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_915(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_916(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_917(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_918(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_919(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_920(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_921(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_922(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_923(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_924(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_925(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_926(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_927(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_928(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_929(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_930(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_931(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_932(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_933(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_934(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_935(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_936(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_937(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_938(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_939(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_940(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_941(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_942(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_943(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_944(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_945(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_946(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_947(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_948(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_949(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_950(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_951(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_952(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_953(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_954(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_955(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_956(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_957(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_958(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_959(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_960(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_961(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_962(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_963(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_964(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_965(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_966(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_967(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_968(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_969(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_970(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_971(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_972(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_973(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_974(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_975(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_976(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_977(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_978(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_979(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_980(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_981(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_982(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_983(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_984(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_985(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_986(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_987(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_988(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_989(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_990(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_991(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_992(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_993(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_994(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_995(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_996(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_997(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_998(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_999(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1000(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1001(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1002(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1003(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1004(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1005(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1006(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1007(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1008(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1009(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1010(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1011(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1012(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1013(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1014(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1015(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1016(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1017(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1018(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1019(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1020(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1021(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1022(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
    private void l_1023(Runnable t, int l) {for (int i=0; i<l; i++) {t.run();}}
}
