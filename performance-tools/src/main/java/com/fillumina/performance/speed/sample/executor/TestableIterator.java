package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.Testable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * After a certain number of iterations (about 100_000 as default) the JVM
 * optimizes the loop and is able to provide very accurate results. But if
 * more than one test is executed consecutively the previous optimizations
 * are removed and the next test will be unoptimized and so much less accurate
 * because of the overhead.
 * To avoid that this class has as many iterator methods as needed for each
 * testable class so that the JVM can optimize any of them and each will be
 * used only with the same class.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public final class TestableIterator {
    private final AtomicInteger counter = new AtomicInteger();
    private final Map<Class<?>, Integer> map = new IdentityHashMap<>(1024);

    public static final TestableIterator INSTANCE = new TestableIterator();

    /* test */ TestableIterator() {}

    /* test */ int getCounter() {
        return counter.get();
    }

    /* test */ int getIndexFor(Class<?> clazz) {
        return map.get(clazz);
    }

    /**
     * Registers the given {@link Testable}.
     *
     * @return true if the {@link Testable} was already registered
     */
    public synchronized boolean register(Testable testable) {
        final Class<? extends Testable> clazz = testable.getClass();
        Integer index = map.get(clazz);
        if (index == null) {
            index = counter.getAndIncrement();
            map.put(clazz, index);
            return false;
        }
        return true;
    }

    /**
     * Iterates over the {@link Testable#test() } method of {@link Testable}
     * for {@code iterations} times and returns how many nanoseconds it takes.
     * <p>
     * <b>IMPORTANT:</b> each {@link Testable} must be registered using
     * {@link #register(Testable) } before iterating.
     *
     * @param testable    the test to measureIterationTime
     * @param iterations  number of iterations
     * @return the elapsed nanoseconds
     */
    public long measureIterationTime(Testable testable, int iterations) {
        final long time = System.nanoTime();
        iterate(testable, iterations);
        return System.nanoTime() - time;
    }

    public void iterate(Testable testable, int iterations) {
        int index;
        try {
            index = map.get(testable.getClass());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("class " +
                    testable.getClass().getCanonicalName() +
                    " has not been registerd.", e);
        }
        switch (index) {
            case 0: l_0(testable, iterations); break;
            case 1: l_1(testable, iterations); break;
            case 2: l_2(testable, iterations); break;
            case 3: l_3(testable, iterations); break;
            case 4: l_4(testable, iterations); break;
            case 5: l_5(testable, iterations); break;
            case 6: l_6(testable, iterations); break;
            case 7: l_7(testable, iterations); break;
            case 8: l_8(testable, iterations); break;
            case 9: l_9(testable, iterations); break;
            case 10: l_10(testable, iterations); break;
            case 11: l_11(testable, iterations); break;
            case 12: l_12(testable, iterations); break;
            case 13: l_13(testable, iterations); break;
            case 14: l_14(testable, iterations); break;
            case 15: l_15(testable, iterations); break;
            case 16: l_16(testable, iterations); break;
            case 17: l_17(testable, iterations); break;
            case 18: l_18(testable, iterations); break;
            case 19: l_19(testable, iterations); break;
            case 20: l_20(testable, iterations); break;
            case 21: l_21(testable, iterations); break;
            case 22: l_22(testable, iterations); break;
            case 23: l_23(testable, iterations); break;
            case 24: l_24(testable, iterations); break;
            case 25: l_25(testable, iterations); break;
            case 26: l_26(testable, iterations); break;
            case 27: l_27(testable, iterations); break;
            case 28: l_28(testable, iterations); break;
            case 29: l_29(testable, iterations); break;
            case 30: l_30(testable, iterations); break;
            case 31: l_31(testable, iterations); break;
            case 32: l_32(testable, iterations); break;
            case 33: l_33(testable, iterations); break;
            case 34: l_34(testable, iterations); break;
            case 35: l_35(testable, iterations); break;
            case 36: l_36(testable, iterations); break;
            case 37: l_37(testable, iterations); break;
            case 38: l_38(testable, iterations); break;
            case 39: l_39(testable, iterations); break;
            case 40: l_40(testable, iterations); break;
            case 41: l_41(testable, iterations); break;
            case 42: l_42(testable, iterations); break;
            case 43: l_43(testable, iterations); break;
            case 44: l_44(testable, iterations); break;
            case 45: l_45(testable, iterations); break;
            case 46: l_46(testable, iterations); break;
            case 47: l_47(testable, iterations); break;
            case 48: l_48(testable, iterations); break;
            case 49: l_49(testable, iterations); break;
            case 50: l_50(testable, iterations); break;
            case 51: l_51(testable, iterations); break;
            case 52: l_52(testable, iterations); break;
            case 53: l_53(testable, iterations); break;
            case 54: l_54(testable, iterations); break;
            case 55: l_55(testable, iterations); break;
            case 56: l_56(testable, iterations); break;
            case 57: l_57(testable, iterations); break;
            case 58: l_58(testable, iterations); break;
            case 59: l_59(testable, iterations); break;
            case 60: l_60(testable, iterations); break;
            case 61: l_61(testable, iterations); break;
            case 62: l_62(testable, iterations); break;
            case 63: l_63(testable, iterations); break;
            case 64: l_64(testable, iterations); break;
            case 65: l_65(testable, iterations); break;
            case 66: l_66(testable, iterations); break;
            case 67: l_67(testable, iterations); break;
            case 68: l_68(testable, iterations); break;
            case 69: l_69(testable, iterations); break;
            case 70: l_70(testable, iterations); break;
            case 71: l_71(testable, iterations); break;
            case 72: l_72(testable, iterations); break;
            case 73: l_73(testable, iterations); break;
            case 74: l_74(testable, iterations); break;
            case 75: l_75(testable, iterations); break;
            case 76: l_76(testable, iterations); break;
            case 77: l_77(testable, iterations); break;
            case 78: l_78(testable, iterations); break;
            case 79: l_79(testable, iterations); break;
            case 80: l_80(testable, iterations); break;
            case 81: l_81(testable, iterations); break;
            case 82: l_82(testable, iterations); break;
            case 83: l_83(testable, iterations); break;
            case 84: l_84(testable, iterations); break;
            case 85: l_85(testable, iterations); break;
            case 86: l_86(testable, iterations); break;
            case 87: l_87(testable, iterations); break;
            case 88: l_88(testable, iterations); break;
            case 89: l_89(testable, iterations); break;
            case 90: l_90(testable, iterations); break;
            case 91: l_91(testable, iterations); break;
            case 92: l_92(testable, iterations); break;
            case 93: l_93(testable, iterations); break;
            case 94: l_94(testable, iterations); break;
            case 95: l_95(testable, iterations); break;
            case 96: l_96(testable, iterations); break;
            case 97: l_97(testable, iterations); break;
            case 98: l_98(testable, iterations); break;
            case 99: l_99(testable, iterations); break;
            case 100: l_100(testable, iterations); break;
            case 101: l_101(testable, iterations); break;
            case 102: l_102(testable, iterations); break;
            case 103: l_103(testable, iterations); break;
            case 104: l_104(testable, iterations); break;
            case 105: l_105(testable, iterations); break;
            case 106: l_106(testable, iterations); break;
            case 107: l_107(testable, iterations); break;
            case 108: l_108(testable, iterations); break;
            case 109: l_109(testable, iterations); break;
            case 110: l_110(testable, iterations); break;
            case 111: l_111(testable, iterations); break;
            case 112: l_112(testable, iterations); break;
            case 113: l_113(testable, iterations); break;
            case 114: l_114(testable, iterations); break;
            case 115: l_115(testable, iterations); break;
            case 116: l_116(testable, iterations); break;
            case 117: l_117(testable, iterations); break;
            case 118: l_118(testable, iterations); break;
            case 119: l_119(testable, iterations); break;
            case 120: l_120(testable, iterations); break;
            case 121: l_121(testable, iterations); break;
            case 122: l_122(testable, iterations); break;
            case 123: l_123(testable, iterations); break;
            case 124: l_124(testable, iterations); break;
            case 125: l_125(testable, iterations); break;
            case 126: l_126(testable, iterations); break;
            case 127: l_127(testable, iterations); break;
            case 128: l_128(testable, iterations); break;
            case 129: l_129(testable, iterations); break;
            case 130: l_130(testable, iterations); break;
            case 131: l_131(testable, iterations); break;
            case 132: l_132(testable, iterations); break;
            case 133: l_133(testable, iterations); break;
            case 134: l_134(testable, iterations); break;
            case 135: l_135(testable, iterations); break;
            case 136: l_136(testable, iterations); break;
            case 137: l_137(testable, iterations); break;
            case 138: l_138(testable, iterations); break;
            case 139: l_139(testable, iterations); break;
            case 140: l_140(testable, iterations); break;
            case 141: l_141(testable, iterations); break;
            case 142: l_142(testable, iterations); break;
            case 143: l_143(testable, iterations); break;
            case 144: l_144(testable, iterations); break;
            case 145: l_145(testable, iterations); break;
            case 146: l_146(testable, iterations); break;
            case 147: l_147(testable, iterations); break;
            case 148: l_148(testable, iterations); break;
            case 149: l_149(testable, iterations); break;
            case 150: l_150(testable, iterations); break;
            case 151: l_151(testable, iterations); break;
            case 152: l_152(testable, iterations); break;
            case 153: l_153(testable, iterations); break;
            case 154: l_154(testable, iterations); break;
            case 155: l_155(testable, iterations); break;
            case 156: l_156(testable, iterations); break;
            case 157: l_157(testable, iterations); break;
            case 158: l_158(testable, iterations); break;
            case 159: l_159(testable, iterations); break;
            case 160: l_160(testable, iterations); break;
            case 161: l_161(testable, iterations); break;
            case 162: l_162(testable, iterations); break;
            case 163: l_163(testable, iterations); break;
            case 164: l_164(testable, iterations); break;
            case 165: l_165(testable, iterations); break;
            case 166: l_166(testable, iterations); break;
            case 167: l_167(testable, iterations); break;
            case 168: l_168(testable, iterations); break;
            case 169: l_169(testable, iterations); break;
            case 170: l_170(testable, iterations); break;
            case 171: l_171(testable, iterations); break;
            case 172: l_172(testable, iterations); break;
            case 173: l_173(testable, iterations); break;
            case 174: l_174(testable, iterations); break;
            case 175: l_175(testable, iterations); break;
            case 176: l_176(testable, iterations); break;
            case 177: l_177(testable, iterations); break;
            case 178: l_178(testable, iterations); break;
            case 179: l_179(testable, iterations); break;
            case 180: l_180(testable, iterations); break;
            case 181: l_181(testable, iterations); break;
            case 182: l_182(testable, iterations); break;
            case 183: l_183(testable, iterations); break;
            case 184: l_184(testable, iterations); break;
            case 185: l_185(testable, iterations); break;
            case 186: l_186(testable, iterations); break;
            case 187: l_187(testable, iterations); break;
            case 188: l_188(testable, iterations); break;
            case 189: l_189(testable, iterations); break;
            case 190: l_190(testable, iterations); break;
            case 191: l_191(testable, iterations); break;
            case 192: l_192(testable, iterations); break;
            case 193: l_193(testable, iterations); break;
            case 194: l_194(testable, iterations); break;
            case 195: l_195(testable, iterations); break;
            case 196: l_196(testable, iterations); break;
            case 197: l_197(testable, iterations); break;
            case 198: l_198(testable, iterations); break;
            case 199: l_199(testable, iterations); break;
            case 200: l_200(testable, iterations); break;
            case 201: l_201(testable, iterations); break;
            case 202: l_202(testable, iterations); break;
            case 203: l_203(testable, iterations); break;
            case 204: l_204(testable, iterations); break;
            case 205: l_205(testable, iterations); break;
            case 206: l_206(testable, iterations); break;
            case 207: l_207(testable, iterations); break;
            case 208: l_208(testable, iterations); break;
            case 209: l_209(testable, iterations); break;
            case 210: l_210(testable, iterations); break;
            case 211: l_211(testable, iterations); break;
            case 212: l_212(testable, iterations); break;
            case 213: l_213(testable, iterations); break;
            case 214: l_214(testable, iterations); break;
            case 215: l_215(testable, iterations); break;
            case 216: l_216(testable, iterations); break;
            case 217: l_217(testable, iterations); break;
            case 218: l_218(testable, iterations); break;
            case 219: l_219(testable, iterations); break;
            case 220: l_220(testable, iterations); break;
            case 221: l_221(testable, iterations); break;
            case 222: l_222(testable, iterations); break;
            case 223: l_223(testable, iterations); break;
            case 224: l_224(testable, iterations); break;
            case 225: l_225(testable, iterations); break;
            case 226: l_226(testable, iterations); break;
            case 227: l_227(testable, iterations); break;
            case 228: l_228(testable, iterations); break;
            case 229: l_229(testable, iterations); break;
            case 230: l_230(testable, iterations); break;
            case 231: l_231(testable, iterations); break;
            case 232: l_232(testable, iterations); break;
            case 233: l_233(testable, iterations); break;
            case 234: l_234(testable, iterations); break;
            case 235: l_235(testable, iterations); break;
            case 236: l_236(testable, iterations); break;
            case 237: l_237(testable, iterations); break;
            case 238: l_238(testable, iterations); break;
            case 239: l_239(testable, iterations); break;
            case 240: l_240(testable, iterations); break;
            case 241: l_241(testable, iterations); break;
            case 242: l_242(testable, iterations); break;
            case 243: l_243(testable, iterations); break;
            case 244: l_244(testable, iterations); break;
            case 245: l_245(testable, iterations); break;
            case 246: l_246(testable, iterations); break;
            case 247: l_247(testable, iterations); break;
            case 248: l_248(testable, iterations); break;
            case 249: l_249(testable, iterations); break;
            case 250: l_250(testable, iterations); break;
            case 251: l_251(testable, iterations); break;
            case 252: l_252(testable, iterations); break;
            case 253: l_253(testable, iterations); break;
            case 254: l_254(testable, iterations); break;
            case 255: l_255(testable, iterations); break;
            case 256: l_256(testable, iterations); break;
            case 257: l_257(testable, iterations); break;
            case 258: l_258(testable, iterations); break;
            case 259: l_259(testable, iterations); break;
            case 260: l_260(testable, iterations); break;
            case 261: l_261(testable, iterations); break;
            case 262: l_262(testable, iterations); break;
            case 263: l_263(testable, iterations); break;
            case 264: l_264(testable, iterations); break;
            case 265: l_265(testable, iterations); break;
            case 266: l_266(testable, iterations); break;
            case 267: l_267(testable, iterations); break;
            case 268: l_268(testable, iterations); break;
            case 269: l_269(testable, iterations); break;
            case 270: l_270(testable, iterations); break;
            case 271: l_271(testable, iterations); break;
            case 272: l_272(testable, iterations); break;
            case 273: l_273(testable, iterations); break;
            case 274: l_274(testable, iterations); break;
            case 275: l_275(testable, iterations); break;
            case 276: l_276(testable, iterations); break;
            case 277: l_277(testable, iterations); break;
            case 278: l_278(testable, iterations); break;
            case 279: l_279(testable, iterations); break;
            case 280: l_280(testable, iterations); break;
            case 281: l_281(testable, iterations); break;
            case 282: l_282(testable, iterations); break;
            case 283: l_283(testable, iterations); break;
            case 284: l_284(testable, iterations); break;
            case 285: l_285(testable, iterations); break;
            case 286: l_286(testable, iterations); break;
            case 287: l_287(testable, iterations); break;
            case 288: l_288(testable, iterations); break;
            case 289: l_289(testable, iterations); break;
            case 290: l_290(testable, iterations); break;
            case 291: l_291(testable, iterations); break;
            case 292: l_292(testable, iterations); break;
            case 293: l_293(testable, iterations); break;
            case 294: l_294(testable, iterations); break;
            case 295: l_295(testable, iterations); break;
            case 296: l_296(testable, iterations); break;
            case 297: l_297(testable, iterations); break;
            case 298: l_298(testable, iterations); break;
            case 299: l_299(testable, iterations); break;
            case 300: l_300(testable, iterations); break;
            case 301: l_301(testable, iterations); break;
            case 302: l_302(testable, iterations); break;
            case 303: l_303(testable, iterations); break;
            case 304: l_304(testable, iterations); break;
            case 305: l_305(testable, iterations); break;
            case 306: l_306(testable, iterations); break;
            case 307: l_307(testable, iterations); break;
            case 308: l_308(testable, iterations); break;
            case 309: l_309(testable, iterations); break;
            case 310: l_310(testable, iterations); break;
            case 311: l_311(testable, iterations); break;
            case 312: l_312(testable, iterations); break;
            case 313: l_313(testable, iterations); break;
            case 314: l_314(testable, iterations); break;
            case 315: l_315(testable, iterations); break;
            case 316: l_316(testable, iterations); break;
            case 317: l_317(testable, iterations); break;
            case 318: l_318(testable, iterations); break;
            case 319: l_319(testable, iterations); break;
            case 320: l_320(testable, iterations); break;
            case 321: l_321(testable, iterations); break;
            case 322: l_322(testable, iterations); break;
            case 323: l_323(testable, iterations); break;
            case 324: l_324(testable, iterations); break;
            case 325: l_325(testable, iterations); break;
            case 326: l_326(testable, iterations); break;
            case 327: l_327(testable, iterations); break;
            case 328: l_328(testable, iterations); break;
            case 329: l_329(testable, iterations); break;
            case 330: l_330(testable, iterations); break;
            case 331: l_331(testable, iterations); break;
            case 332: l_332(testable, iterations); break;
            case 333: l_333(testable, iterations); break;
            case 334: l_334(testable, iterations); break;
            case 335: l_335(testable, iterations); break;
            case 336: l_336(testable, iterations); break;
            case 337: l_337(testable, iterations); break;
            case 338: l_338(testable, iterations); break;
            case 339: l_339(testable, iterations); break;
            case 340: l_340(testable, iterations); break;
            case 341: l_341(testable, iterations); break;
            case 342: l_342(testable, iterations); break;
            case 343: l_343(testable, iterations); break;
            case 344: l_344(testable, iterations); break;
            case 345: l_345(testable, iterations); break;
            case 346: l_346(testable, iterations); break;
            case 347: l_347(testable, iterations); break;
            case 348: l_348(testable, iterations); break;
            case 349: l_349(testable, iterations); break;
            case 350: l_350(testable, iterations); break;
            case 351: l_351(testable, iterations); break;
            case 352: l_352(testable, iterations); break;
            case 353: l_353(testable, iterations); break;
            case 354: l_354(testable, iterations); break;
            case 355: l_355(testable, iterations); break;
            case 356: l_356(testable, iterations); break;
            case 357: l_357(testable, iterations); break;
            case 358: l_358(testable, iterations); break;
            case 359: l_359(testable, iterations); break;
            case 360: l_360(testable, iterations); break;
            case 361: l_361(testable, iterations); break;
            case 362: l_362(testable, iterations); break;
            case 363: l_363(testable, iterations); break;
            case 364: l_364(testable, iterations); break;
            case 365: l_365(testable, iterations); break;
            case 366: l_366(testable, iterations); break;
            case 367: l_367(testable, iterations); break;
            case 368: l_368(testable, iterations); break;
            case 369: l_369(testable, iterations); break;
            case 370: l_370(testable, iterations); break;
            case 371: l_371(testable, iterations); break;
            case 372: l_372(testable, iterations); break;
            case 373: l_373(testable, iterations); break;
            case 374: l_374(testable, iterations); break;
            case 375: l_375(testable, iterations); break;
            case 376: l_376(testable, iterations); break;
            case 377: l_377(testable, iterations); break;
            case 378: l_378(testable, iterations); break;
            case 379: l_379(testable, iterations); break;
            case 380: l_380(testable, iterations); break;
            case 381: l_381(testable, iterations); break;
            case 382: l_382(testable, iterations); break;
            case 383: l_383(testable, iterations); break;
            case 384: l_384(testable, iterations); break;
            case 385: l_385(testable, iterations); break;
            case 386: l_386(testable, iterations); break;
            case 387: l_387(testable, iterations); break;
            case 388: l_388(testable, iterations); break;
            case 389: l_389(testable, iterations); break;
            case 390: l_390(testable, iterations); break;
            case 391: l_391(testable, iterations); break;
            case 392: l_392(testable, iterations); break;
            case 393: l_393(testable, iterations); break;
            case 394: l_394(testable, iterations); break;
            case 395: l_395(testable, iterations); break;
            case 396: l_396(testable, iterations); break;
            case 397: l_397(testable, iterations); break;
            case 398: l_398(testable, iterations); break;
            case 399: l_399(testable, iterations); break;
            case 400: l_400(testable, iterations); break;
            case 401: l_401(testable, iterations); break;
            case 402: l_402(testable, iterations); break;
            case 403: l_403(testable, iterations); break;
            case 404: l_404(testable, iterations); break;
            case 405: l_405(testable, iterations); break;
            case 406: l_406(testable, iterations); break;
            case 407: l_407(testable, iterations); break;
            case 408: l_408(testable, iterations); break;
            case 409: l_409(testable, iterations); break;
            case 410: l_410(testable, iterations); break;
            case 411: l_411(testable, iterations); break;
            case 412: l_412(testable, iterations); break;
            case 413: l_413(testable, iterations); break;
            case 414: l_414(testable, iterations); break;
            case 415: l_415(testable, iterations); break;
            case 416: l_416(testable, iterations); break;
            case 417: l_417(testable, iterations); break;
            case 418: l_418(testable, iterations); break;
            case 419: l_419(testable, iterations); break;
            case 420: l_420(testable, iterations); break;
            case 421: l_421(testable, iterations); break;
            case 422: l_422(testable, iterations); break;
            case 423: l_423(testable, iterations); break;
            case 424: l_424(testable, iterations); break;
            case 425: l_425(testable, iterations); break;
            case 426: l_426(testable, iterations); break;
            case 427: l_427(testable, iterations); break;
            case 428: l_428(testable, iterations); break;
            case 429: l_429(testable, iterations); break;
            case 430: l_430(testable, iterations); break;
            case 431: l_431(testable, iterations); break;
            case 432: l_432(testable, iterations); break;
            case 433: l_433(testable, iterations); break;
            case 434: l_434(testable, iterations); break;
            case 435: l_435(testable, iterations); break;
            case 436: l_436(testable, iterations); break;
            case 437: l_437(testable, iterations); break;
            case 438: l_438(testable, iterations); break;
            case 439: l_439(testable, iterations); break;
            case 440: l_440(testable, iterations); break;
            case 441: l_441(testable, iterations); break;
            case 442: l_442(testable, iterations); break;
            case 443: l_443(testable, iterations); break;
            case 444: l_444(testable, iterations); break;
            case 445: l_445(testable, iterations); break;
            case 446: l_446(testable, iterations); break;
            case 447: l_447(testable, iterations); break;
            case 448: l_448(testable, iterations); break;
            case 449: l_449(testable, iterations); break;
            case 450: l_450(testable, iterations); break;
            case 451: l_451(testable, iterations); break;
            case 452: l_452(testable, iterations); break;
            case 453: l_453(testable, iterations); break;
            case 454: l_454(testable, iterations); break;
            case 455: l_455(testable, iterations); break;
            case 456: l_456(testable, iterations); break;
            case 457: l_457(testable, iterations); break;
            case 458: l_458(testable, iterations); break;
            case 459: l_459(testable, iterations); break;
            case 460: l_460(testable, iterations); break;
            case 461: l_461(testable, iterations); break;
            case 462: l_462(testable, iterations); break;
            case 463: l_463(testable, iterations); break;
            case 464: l_464(testable, iterations); break;
            case 465: l_465(testable, iterations); break;
            case 466: l_466(testable, iterations); break;
            case 467: l_467(testable, iterations); break;
            case 468: l_468(testable, iterations); break;
            case 469: l_469(testable, iterations); break;
            case 470: l_470(testable, iterations); break;
            case 471: l_471(testable, iterations); break;
            case 472: l_472(testable, iterations); break;
            case 473: l_473(testable, iterations); break;
            case 474: l_474(testable, iterations); break;
            case 475: l_475(testable, iterations); break;
            case 476: l_476(testable, iterations); break;
            case 477: l_477(testable, iterations); break;
            case 478: l_478(testable, iterations); break;
            case 479: l_479(testable, iterations); break;
            case 480: l_480(testable, iterations); break;
            case 481: l_481(testable, iterations); break;
            case 482: l_482(testable, iterations); break;
            case 483: l_483(testable, iterations); break;
            case 484: l_484(testable, iterations); break;
            case 485: l_485(testable, iterations); break;
            case 486: l_486(testable, iterations); break;
            case 487: l_487(testable, iterations); break;
            case 488: l_488(testable, iterations); break;
            case 489: l_489(testable, iterations); break;
            case 490: l_490(testable, iterations); break;
            case 491: l_491(testable, iterations); break;
            case 492: l_492(testable, iterations); break;
            case 493: l_493(testable, iterations); break;
            case 494: l_494(testable, iterations); break;
            case 495: l_495(testable, iterations); break;
            case 496: l_496(testable, iterations); break;
            case 497: l_497(testable, iterations); break;
            case 498: l_498(testable, iterations); break;
            case 499: l_499(testable, iterations); break;
            case 500: l_500(testable, iterations); break;
            case 501: l_501(testable, iterations); break;
            case 502: l_502(testable, iterations); break;
            case 503: l_503(testable, iterations); break;
            case 504: l_504(testable, iterations); break;
            case 505: l_505(testable, iterations); break;
            case 506: l_506(testable, iterations); break;
            case 507: l_507(testable, iterations); break;
            case 508: l_508(testable, iterations); break;
            case 509: l_509(testable, iterations); break;
            case 510: l_510(testable, iterations); break;
            case 511: l_511(testable, iterations); break;
            case 512: l_512(testable, iterations); break;
            case 513: l_513(testable, iterations); break;
            case 514: l_514(testable, iterations); break;
            case 515: l_515(testable, iterations); break;
            case 516: l_516(testable, iterations); break;
            case 517: l_517(testable, iterations); break;
            case 518: l_518(testable, iterations); break;
            case 519: l_519(testable, iterations); break;
            case 520: l_520(testable, iterations); break;
            case 521: l_521(testable, iterations); break;
            case 522: l_522(testable, iterations); break;
            case 523: l_523(testable, iterations); break;
            case 524: l_524(testable, iterations); break;
            case 525: l_525(testable, iterations); break;
            case 526: l_526(testable, iterations); break;
            case 527: l_527(testable, iterations); break;
            case 528: l_528(testable, iterations); break;
            case 529: l_529(testable, iterations); break;
            case 530: l_530(testable, iterations); break;
            case 531: l_531(testable, iterations); break;
            case 532: l_532(testable, iterations); break;
            case 533: l_533(testable, iterations); break;
            case 534: l_534(testable, iterations); break;
            case 535: l_535(testable, iterations); break;
            case 536: l_536(testable, iterations); break;
            case 537: l_537(testable, iterations); break;
            case 538: l_538(testable, iterations); break;
            case 539: l_539(testable, iterations); break;
            case 540: l_540(testable, iterations); break;
            case 541: l_541(testable, iterations); break;
            case 542: l_542(testable, iterations); break;
            case 543: l_543(testable, iterations); break;
            case 544: l_544(testable, iterations); break;
            case 545: l_545(testable, iterations); break;
            case 546: l_546(testable, iterations); break;
            case 547: l_547(testable, iterations); break;
            case 548: l_548(testable, iterations); break;
            case 549: l_549(testable, iterations); break;
            case 550: l_550(testable, iterations); break;
            case 551: l_551(testable, iterations); break;
            case 552: l_552(testable, iterations); break;
            case 553: l_553(testable, iterations); break;
            case 554: l_554(testable, iterations); break;
            case 555: l_555(testable, iterations); break;
            case 556: l_556(testable, iterations); break;
            case 557: l_557(testable, iterations); break;
            case 558: l_558(testable, iterations); break;
            case 559: l_559(testable, iterations); break;
            case 560: l_560(testable, iterations); break;
            case 561: l_561(testable, iterations); break;
            case 562: l_562(testable, iterations); break;
            case 563: l_563(testable, iterations); break;
            case 564: l_564(testable, iterations); break;
            case 565: l_565(testable, iterations); break;
            case 566: l_566(testable, iterations); break;
            case 567: l_567(testable, iterations); break;
            case 568: l_568(testable, iterations); break;
            case 569: l_569(testable, iterations); break;
            case 570: l_570(testable, iterations); break;
            case 571: l_571(testable, iterations); break;
            case 572: l_572(testable, iterations); break;
            case 573: l_573(testable, iterations); break;
            case 574: l_574(testable, iterations); break;
            case 575: l_575(testable, iterations); break;
            case 576: l_576(testable, iterations); break;
            case 577: l_577(testable, iterations); break;
            case 578: l_578(testable, iterations); break;
            case 579: l_579(testable, iterations); break;
            case 580: l_580(testable, iterations); break;
            case 581: l_581(testable, iterations); break;
            case 582: l_582(testable, iterations); break;
            case 583: l_583(testable, iterations); break;
            case 584: l_584(testable, iterations); break;
            case 585: l_585(testable, iterations); break;
            case 586: l_586(testable, iterations); break;
            case 587: l_587(testable, iterations); break;
            case 588: l_588(testable, iterations); break;
            case 589: l_589(testable, iterations); break;
            case 590: l_590(testable, iterations); break;
            case 591: l_591(testable, iterations); break;
            case 592: l_592(testable, iterations); break;
            case 593: l_593(testable, iterations); break;
            case 594: l_594(testable, iterations); break;
            case 595: l_595(testable, iterations); break;
            case 596: l_596(testable, iterations); break;
            case 597: l_597(testable, iterations); break;
            case 598: l_598(testable, iterations); break;
            case 599: l_599(testable, iterations); break;
            case 600: l_600(testable, iterations); break;
            case 601: l_601(testable, iterations); break;
            case 602: l_602(testable, iterations); break;
            case 603: l_603(testable, iterations); break;
            case 604: l_604(testable, iterations); break;
            case 605: l_605(testable, iterations); break;
            case 606: l_606(testable, iterations); break;
            case 607: l_607(testable, iterations); break;
            case 608: l_608(testable, iterations); break;
            case 609: l_609(testable, iterations); break;
            case 610: l_610(testable, iterations); break;
            case 611: l_611(testable, iterations); break;
            case 612: l_612(testable, iterations); break;
            case 613: l_613(testable, iterations); break;
            case 614: l_614(testable, iterations); break;
            case 615: l_615(testable, iterations); break;
            case 616: l_616(testable, iterations); break;
            case 617: l_617(testable, iterations); break;
            case 618: l_618(testable, iterations); break;
            case 619: l_619(testable, iterations); break;
            case 620: l_620(testable, iterations); break;
            case 621: l_621(testable, iterations); break;
            case 622: l_622(testable, iterations); break;
            case 623: l_623(testable, iterations); break;
            case 624: l_624(testable, iterations); break;
            case 625: l_625(testable, iterations); break;
            case 626: l_626(testable, iterations); break;
            case 627: l_627(testable, iterations); break;
            case 628: l_628(testable, iterations); break;
            case 629: l_629(testable, iterations); break;
            case 630: l_630(testable, iterations); break;
            case 631: l_631(testable, iterations); break;
            case 632: l_632(testable, iterations); break;
            case 633: l_633(testable, iterations); break;
            case 634: l_634(testable, iterations); break;
            case 635: l_635(testable, iterations); break;
            case 636: l_636(testable, iterations); break;
            case 637: l_637(testable, iterations); break;
            case 638: l_638(testable, iterations); break;
            case 639: l_639(testable, iterations); break;
            case 640: l_640(testable, iterations); break;
            case 641: l_641(testable, iterations); break;
            case 642: l_642(testable, iterations); break;
            case 643: l_643(testable, iterations); break;
            case 644: l_644(testable, iterations); break;
            case 645: l_645(testable, iterations); break;
            case 646: l_646(testable, iterations); break;
            case 647: l_647(testable, iterations); break;
            case 648: l_648(testable, iterations); break;
            case 649: l_649(testable, iterations); break;
            case 650: l_650(testable, iterations); break;
            case 651: l_651(testable, iterations); break;
            case 652: l_652(testable, iterations); break;
            case 653: l_653(testable, iterations); break;
            case 654: l_654(testable, iterations); break;
            case 655: l_655(testable, iterations); break;
            case 656: l_656(testable, iterations); break;
            case 657: l_657(testable, iterations); break;
            case 658: l_658(testable, iterations); break;
            case 659: l_659(testable, iterations); break;
            case 660: l_660(testable, iterations); break;
            case 661: l_661(testable, iterations); break;
            case 662: l_662(testable, iterations); break;
            case 663: l_663(testable, iterations); break;
            case 664: l_664(testable, iterations); break;
            case 665: l_665(testable, iterations); break;
            case 666: l_666(testable, iterations); break;
            case 667: l_667(testable, iterations); break;
            case 668: l_668(testable, iterations); break;
            case 669: l_669(testable, iterations); break;
            case 670: l_670(testable, iterations); break;
            case 671: l_671(testable, iterations); break;
            case 672: l_672(testable, iterations); break;
            case 673: l_673(testable, iterations); break;
            case 674: l_674(testable, iterations); break;
            case 675: l_675(testable, iterations); break;
            case 676: l_676(testable, iterations); break;
            case 677: l_677(testable, iterations); break;
            case 678: l_678(testable, iterations); break;
            case 679: l_679(testable, iterations); break;
            case 680: l_680(testable, iterations); break;
            case 681: l_681(testable, iterations); break;
            case 682: l_682(testable, iterations); break;
            case 683: l_683(testable, iterations); break;
            case 684: l_684(testable, iterations); break;
            case 685: l_685(testable, iterations); break;
            case 686: l_686(testable, iterations); break;
            case 687: l_687(testable, iterations); break;
            case 688: l_688(testable, iterations); break;
            case 689: l_689(testable, iterations); break;
            case 690: l_690(testable, iterations); break;
            case 691: l_691(testable, iterations); break;
            case 692: l_692(testable, iterations); break;
            case 693: l_693(testable, iterations); break;
            case 694: l_694(testable, iterations); break;
            case 695: l_695(testable, iterations); break;
            case 696: l_696(testable, iterations); break;
            case 697: l_697(testable, iterations); break;
            case 698: l_698(testable, iterations); break;
            case 699: l_699(testable, iterations); break;
            case 700: l_700(testable, iterations); break;
            case 701: l_701(testable, iterations); break;
            case 702: l_702(testable, iterations); break;
            case 703: l_703(testable, iterations); break;
            case 704: l_704(testable, iterations); break;
            case 705: l_705(testable, iterations); break;
            case 706: l_706(testable, iterations); break;
            case 707: l_707(testable, iterations); break;
            case 708: l_708(testable, iterations); break;
            case 709: l_709(testable, iterations); break;
            case 710: l_710(testable, iterations); break;
            case 711: l_711(testable, iterations); break;
            case 712: l_712(testable, iterations); break;
            case 713: l_713(testable, iterations); break;
            case 714: l_714(testable, iterations); break;
            case 715: l_715(testable, iterations); break;
            case 716: l_716(testable, iterations); break;
            case 717: l_717(testable, iterations); break;
            case 718: l_718(testable, iterations); break;
            case 719: l_719(testable, iterations); break;
            case 720: l_720(testable, iterations); break;
            case 721: l_721(testable, iterations); break;
            case 722: l_722(testable, iterations); break;
            case 723: l_723(testable, iterations); break;
            case 724: l_724(testable, iterations); break;
            case 725: l_725(testable, iterations); break;
            case 726: l_726(testable, iterations); break;
            case 727: l_727(testable, iterations); break;
            case 728: l_728(testable, iterations); break;
            case 729: l_729(testable, iterations); break;
            case 730: l_730(testable, iterations); break;
            case 731: l_731(testable, iterations); break;
            case 732: l_732(testable, iterations); break;
            case 733: l_733(testable, iterations); break;
            case 734: l_734(testable, iterations); break;
            case 735: l_735(testable, iterations); break;
            case 736: l_736(testable, iterations); break;
            case 737: l_737(testable, iterations); break;
            case 738: l_738(testable, iterations); break;
            case 739: l_739(testable, iterations); break;
            case 740: l_740(testable, iterations); break;
            case 741: l_741(testable, iterations); break;
            case 742: l_742(testable, iterations); break;
            case 743: l_743(testable, iterations); break;
            case 744: l_744(testable, iterations); break;
            case 745: l_745(testable, iterations); break;
            case 746: l_746(testable, iterations); break;
            case 747: l_747(testable, iterations); break;
            case 748: l_748(testable, iterations); break;
            case 749: l_749(testable, iterations); break;
            case 750: l_750(testable, iterations); break;
            case 751: l_751(testable, iterations); break;
            case 752: l_752(testable, iterations); break;
            case 753: l_753(testable, iterations); break;
            case 754: l_754(testable, iterations); break;
            case 755: l_755(testable, iterations); break;
            case 756: l_756(testable, iterations); break;
            case 757: l_757(testable, iterations); break;
            case 758: l_758(testable, iterations); break;
            case 759: l_759(testable, iterations); break;
            case 760: l_760(testable, iterations); break;
            case 761: l_761(testable, iterations); break;
            case 762: l_762(testable, iterations); break;
            case 763: l_763(testable, iterations); break;
            case 764: l_764(testable, iterations); break;
            case 765: l_765(testable, iterations); break;
            case 766: l_766(testable, iterations); break;
            case 767: l_767(testable, iterations); break;
            case 768: l_768(testable, iterations); break;
            case 769: l_769(testable, iterations); break;
            case 770: l_770(testable, iterations); break;
            case 771: l_771(testable, iterations); break;
            case 772: l_772(testable, iterations); break;
            case 773: l_773(testable, iterations); break;
            case 774: l_774(testable, iterations); break;
            case 775: l_775(testable, iterations); break;
            case 776: l_776(testable, iterations); break;
            case 777: l_777(testable, iterations); break;
            case 778: l_778(testable, iterations); break;
            case 779: l_779(testable, iterations); break;
            case 780: l_780(testable, iterations); break;
            case 781: l_781(testable, iterations); break;
            case 782: l_782(testable, iterations); break;
            case 783: l_783(testable, iterations); break;
            case 784: l_784(testable, iterations); break;
            case 785: l_785(testable, iterations); break;
            case 786: l_786(testable, iterations); break;
            case 787: l_787(testable, iterations); break;
            case 788: l_788(testable, iterations); break;
            case 789: l_789(testable, iterations); break;
            case 790: l_790(testable, iterations); break;
            case 791: l_791(testable, iterations); break;
            case 792: l_792(testable, iterations); break;
            case 793: l_793(testable, iterations); break;
            case 794: l_794(testable, iterations); break;
            case 795: l_795(testable, iterations); break;
            case 796: l_796(testable, iterations); break;
            case 797: l_797(testable, iterations); break;
            case 798: l_798(testable, iterations); break;
            case 799: l_799(testable, iterations); break;
            case 800: l_800(testable, iterations); break;
            case 801: l_801(testable, iterations); break;
            case 802: l_802(testable, iterations); break;
            case 803: l_803(testable, iterations); break;
            case 804: l_804(testable, iterations); break;
            case 805: l_805(testable, iterations); break;
            case 806: l_806(testable, iterations); break;
            case 807: l_807(testable, iterations); break;
            case 808: l_808(testable, iterations); break;
            case 809: l_809(testable, iterations); break;
            case 810: l_810(testable, iterations); break;
            case 811: l_811(testable, iterations); break;
            case 812: l_812(testable, iterations); break;
            case 813: l_813(testable, iterations); break;
            case 814: l_814(testable, iterations); break;
            case 815: l_815(testable, iterations); break;
            case 816: l_816(testable, iterations); break;
            case 817: l_817(testable, iterations); break;
            case 818: l_818(testable, iterations); break;
            case 819: l_819(testable, iterations); break;
            case 820: l_820(testable, iterations); break;
            case 821: l_821(testable, iterations); break;
            case 822: l_822(testable, iterations); break;
            case 823: l_823(testable, iterations); break;
            case 824: l_824(testable, iterations); break;
            case 825: l_825(testable, iterations); break;
            case 826: l_826(testable, iterations); break;
            case 827: l_827(testable, iterations); break;
            case 828: l_828(testable, iterations); break;
            case 829: l_829(testable, iterations); break;
            case 830: l_830(testable, iterations); break;
            case 831: l_831(testable, iterations); break;
            case 832: l_832(testable, iterations); break;
            case 833: l_833(testable, iterations); break;
            case 834: l_834(testable, iterations); break;
            case 835: l_835(testable, iterations); break;
            case 836: l_836(testable, iterations); break;
            case 837: l_837(testable, iterations); break;
            case 838: l_838(testable, iterations); break;
            case 839: l_839(testable, iterations); break;
            case 840: l_840(testable, iterations); break;
            case 841: l_841(testable, iterations); break;
            case 842: l_842(testable, iterations); break;
            case 843: l_843(testable, iterations); break;
            case 844: l_844(testable, iterations); break;
            case 845: l_845(testable, iterations); break;
            case 846: l_846(testable, iterations); break;
            case 847: l_847(testable, iterations); break;
            case 848: l_848(testable, iterations); break;
            case 849: l_849(testable, iterations); break;
            case 850: l_850(testable, iterations); break;
            case 851: l_851(testable, iterations); break;
            case 852: l_852(testable, iterations); break;
            case 853: l_853(testable, iterations); break;
            case 854: l_854(testable, iterations); break;
            case 855: l_855(testable, iterations); break;
            case 856: l_856(testable, iterations); break;
            case 857: l_857(testable, iterations); break;
            case 858: l_858(testable, iterations); break;
            case 859: l_859(testable, iterations); break;
            case 860: l_860(testable, iterations); break;
            case 861: l_861(testable, iterations); break;
            case 862: l_862(testable, iterations); break;
            case 863: l_863(testable, iterations); break;
            case 864: l_864(testable, iterations); break;
            case 865: l_865(testable, iterations); break;
            case 866: l_866(testable, iterations); break;
            case 867: l_867(testable, iterations); break;
            case 868: l_868(testable, iterations); break;
            case 869: l_869(testable, iterations); break;
            case 870: l_870(testable, iterations); break;
            case 871: l_871(testable, iterations); break;
            case 872: l_872(testable, iterations); break;
            case 873: l_873(testable, iterations); break;
            case 874: l_874(testable, iterations); break;
            case 875: l_875(testable, iterations); break;
            case 876: l_876(testable, iterations); break;
            case 877: l_877(testable, iterations); break;
            case 878: l_878(testable, iterations); break;
            case 879: l_879(testable, iterations); break;
            case 880: l_880(testable, iterations); break;
            case 881: l_881(testable, iterations); break;
            case 882: l_882(testable, iterations); break;
            case 883: l_883(testable, iterations); break;
            case 884: l_884(testable, iterations); break;
            case 885: l_885(testable, iterations); break;
            case 886: l_886(testable, iterations); break;
            case 887: l_887(testable, iterations); break;
            case 888: l_888(testable, iterations); break;
            case 889: l_889(testable, iterations); break;
            case 890: l_890(testable, iterations); break;
            case 891: l_891(testable, iterations); break;
            case 892: l_892(testable, iterations); break;
            case 893: l_893(testable, iterations); break;
            case 894: l_894(testable, iterations); break;
            case 895: l_895(testable, iterations); break;
            case 896: l_896(testable, iterations); break;
            case 897: l_897(testable, iterations); break;
            case 898: l_898(testable, iterations); break;
            case 899: l_899(testable, iterations); break;
            case 900: l_900(testable, iterations); break;
            case 901: l_901(testable, iterations); break;
            case 902: l_902(testable, iterations); break;
            case 903: l_903(testable, iterations); break;
            case 904: l_904(testable, iterations); break;
            case 905: l_905(testable, iterations); break;
            case 906: l_906(testable, iterations); break;
            case 907: l_907(testable, iterations); break;
            case 908: l_908(testable, iterations); break;
            case 909: l_909(testable, iterations); break;
            case 910: l_910(testable, iterations); break;
            case 911: l_911(testable, iterations); break;
            case 912: l_912(testable, iterations); break;
            case 913: l_913(testable, iterations); break;
            case 914: l_914(testable, iterations); break;
            case 915: l_915(testable, iterations); break;
            case 916: l_916(testable, iterations); break;
            case 917: l_917(testable, iterations); break;
            case 918: l_918(testable, iterations); break;
            case 919: l_919(testable, iterations); break;
            case 920: l_920(testable, iterations); break;
            case 921: l_921(testable, iterations); break;
            case 922: l_922(testable, iterations); break;
            case 923: l_923(testable, iterations); break;
            case 924: l_924(testable, iterations); break;
            case 925: l_925(testable, iterations); break;
            case 926: l_926(testable, iterations); break;
            case 927: l_927(testable, iterations); break;
            case 928: l_928(testable, iterations); break;
            case 929: l_929(testable, iterations); break;
            case 930: l_930(testable, iterations); break;
            case 931: l_931(testable, iterations); break;
            case 932: l_932(testable, iterations); break;
            case 933: l_933(testable, iterations); break;
            case 934: l_934(testable, iterations); break;
            case 935: l_935(testable, iterations); break;
            case 936: l_936(testable, iterations); break;
            case 937: l_937(testable, iterations); break;
            case 938: l_938(testable, iterations); break;
            case 939: l_939(testable, iterations); break;
            case 940: l_940(testable, iterations); break;
            case 941: l_941(testable, iterations); break;
            case 942: l_942(testable, iterations); break;
            case 943: l_943(testable, iterations); break;
            case 944: l_944(testable, iterations); break;
            case 945: l_945(testable, iterations); break;
            case 946: l_946(testable, iterations); break;
            case 947: l_947(testable, iterations); break;
            case 948: l_948(testable, iterations); break;
            case 949: l_949(testable, iterations); break;
            case 950: l_950(testable, iterations); break;
            case 951: l_951(testable, iterations); break;
            case 952: l_952(testable, iterations); break;
            case 953: l_953(testable, iterations); break;
            case 954: l_954(testable, iterations); break;
            case 955: l_955(testable, iterations); break;
            case 956: l_956(testable, iterations); break;
            case 957: l_957(testable, iterations); break;
            case 958: l_958(testable, iterations); break;
            case 959: l_959(testable, iterations); break;
            case 960: l_960(testable, iterations); break;
            case 961: l_961(testable, iterations); break;
            case 962: l_962(testable, iterations); break;
            case 963: l_963(testable, iterations); break;
            case 964: l_964(testable, iterations); break;
            case 965: l_965(testable, iterations); break;
            case 966: l_966(testable, iterations); break;
            case 967: l_967(testable, iterations); break;
            case 968: l_968(testable, iterations); break;
            case 969: l_969(testable, iterations); break;
            case 970: l_970(testable, iterations); break;
            case 971: l_971(testable, iterations); break;
            case 972: l_972(testable, iterations); break;
            case 973: l_973(testable, iterations); break;
            case 974: l_974(testable, iterations); break;
            case 975: l_975(testable, iterations); break;
            case 976: l_976(testable, iterations); break;
            case 977: l_977(testable, iterations); break;
            case 978: l_978(testable, iterations); break;
            case 979: l_979(testable, iterations); break;
            case 980: l_980(testable, iterations); break;
            case 981: l_981(testable, iterations); break;
            case 982: l_982(testable, iterations); break;
            case 983: l_983(testable, iterations); break;
            case 984: l_984(testable, iterations); break;
            case 985: l_985(testable, iterations); break;
            case 986: l_986(testable, iterations); break;
            case 987: l_987(testable, iterations); break;
            case 988: l_988(testable, iterations); break;
            case 989: l_989(testable, iterations); break;
            case 990: l_990(testable, iterations); break;
            case 991: l_991(testable, iterations); break;
            case 992: l_992(testable, iterations); break;
            case 993: l_993(testable, iterations); break;
            case 994: l_994(testable, iterations); break;
            case 995: l_995(testable, iterations); break;
            case 996: l_996(testable, iterations); break;
            case 997: l_997(testable, iterations); break;
            case 998: l_998(testable, iterations); break;
            case 999: l_999(testable, iterations); break;
            case 1000: l_1000(testable, iterations); break;
            case 1001: l_1001(testable, iterations); break;
            case 1002: l_1002(testable, iterations); break;
            case 1003: l_1003(testable, iterations); break;
            case 1004: l_1004(testable, iterations); break;
            case 1005: l_1005(testable, iterations); break;
            case 1006: l_1006(testable, iterations); break;
            case 1007: l_1007(testable, iterations); break;
            case 1008: l_1008(testable, iterations); break;
            case 1009: l_1009(testable, iterations); break;
            case 1010: l_1010(testable, iterations); break;
            case 1011: l_1011(testable, iterations); break;
            case 1012: l_1012(testable, iterations); break;
            case 1013: l_1013(testable, iterations); break;
            case 1014: l_1014(testable, iterations); break;
            case 1015: l_1015(testable, iterations); break;
            case 1016: l_1016(testable, iterations); break;
            case 1017: l_1017(testable, iterations); break;
            case 1018: l_1018(testable, iterations); break;
            case 1019: l_1019(testable, iterations); break;
            case 1020: l_1020(testable, iterations); break;
            case 1021: l_1021(testable, iterations); break;
            case 1022: l_1022(testable, iterations); break;
            case 1023: l_1023(testable, iterations); break;
            default:
                throw new IllegalStateException(
                        "too many classes tested: " + index);
        }
    }

    private void l_0(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_2(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_3(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_4(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_5(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_6(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_7(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_8(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_9(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_10(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_11(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_12(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_13(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_14(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_15(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_16(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_17(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_18(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_19(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_20(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_21(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_22(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_23(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_24(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_25(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_26(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_27(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_28(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_29(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_30(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_31(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_32(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_33(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_34(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_35(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_36(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_37(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_38(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_39(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_40(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_41(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_42(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_43(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_44(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_45(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_46(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_47(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_48(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_49(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_50(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_51(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_52(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_53(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_54(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_55(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_56(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_57(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_58(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_59(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_60(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_61(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_62(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_63(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_64(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_65(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_66(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_67(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_68(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_69(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_70(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_71(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_72(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_73(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_74(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_75(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_76(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_77(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_78(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_79(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_80(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_81(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_82(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_83(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_84(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_85(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_86(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_87(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_88(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_89(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_90(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_91(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_92(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_93(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_94(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_95(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_96(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_97(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_98(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_99(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_100(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_101(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_102(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_103(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_104(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_105(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_106(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_107(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_108(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_109(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_110(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_111(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_112(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_113(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_114(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_115(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_116(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_117(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_118(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_119(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_120(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_121(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_122(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_123(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_124(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_125(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_126(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_127(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_128(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_129(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_130(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_131(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_132(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_133(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_134(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_135(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_136(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_137(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_138(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_139(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_140(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_141(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_142(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_143(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_144(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_145(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_146(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_147(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_148(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_149(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_150(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_151(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_152(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_153(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_154(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_155(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_156(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_157(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_158(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_159(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_160(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_161(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_162(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_163(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_164(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_165(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_166(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_167(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_168(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_169(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_170(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_171(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_172(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_173(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_174(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_175(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_176(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_177(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_178(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_179(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_180(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_181(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_182(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_183(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_184(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_185(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_186(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_187(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_188(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_189(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_190(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_191(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_192(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_193(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_194(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_195(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_196(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_197(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_198(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_199(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_200(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_201(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_202(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_203(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_204(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_205(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_206(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_207(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_208(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_209(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_210(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_211(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_212(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_213(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_214(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_215(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_216(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_217(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_218(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_219(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_220(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_221(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_222(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_223(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_224(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_225(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_226(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_227(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_228(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_229(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_230(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_231(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_232(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_233(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_234(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_235(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_236(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_237(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_238(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_239(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_240(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_241(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_242(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_243(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_244(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_245(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_246(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_247(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_248(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_249(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_250(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_251(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_252(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_253(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_254(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_255(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_256(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_257(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_258(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_259(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_260(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_261(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_262(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_263(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_264(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_265(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_266(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_267(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_268(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_269(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_270(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_271(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_272(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_273(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_274(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_275(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_276(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_277(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_278(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_279(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_280(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_281(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_282(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_283(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_284(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_285(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_286(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_287(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_288(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_289(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_290(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_291(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_292(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_293(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_294(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_295(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_296(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_297(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_298(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_299(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_300(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_301(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_302(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_303(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_304(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_305(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_306(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_307(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_308(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_309(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_310(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_311(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_312(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_313(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_314(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_315(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_316(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_317(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_318(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_319(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_320(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_321(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_322(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_323(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_324(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_325(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_326(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_327(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_328(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_329(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_330(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_331(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_332(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_333(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_334(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_335(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_336(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_337(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_338(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_339(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_340(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_341(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_342(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_343(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_344(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_345(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_346(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_347(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_348(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_349(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_350(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_351(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_352(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_353(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_354(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_355(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_356(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_357(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_358(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_359(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_360(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_361(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_362(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_363(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_364(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_365(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_366(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_367(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_368(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_369(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_370(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_371(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_372(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_373(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_374(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_375(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_376(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_377(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_378(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_379(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_380(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_381(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_382(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_383(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_384(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_385(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_386(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_387(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_388(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_389(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_390(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_391(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_392(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_393(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_394(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_395(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_396(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_397(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_398(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_399(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_400(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_401(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_402(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_403(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_404(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_405(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_406(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_407(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_408(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_409(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_410(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_411(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_412(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_413(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_414(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_415(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_416(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_417(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_418(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_419(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_420(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_421(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_422(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_423(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_424(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_425(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_426(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_427(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_428(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_429(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_430(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_431(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_432(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_433(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_434(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_435(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_436(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_437(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_438(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_439(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_440(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_441(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_442(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_443(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_444(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_445(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_446(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_447(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_448(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_449(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_450(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_451(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_452(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_453(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_454(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_455(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_456(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_457(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_458(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_459(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_460(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_461(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_462(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_463(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_464(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_465(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_466(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_467(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_468(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_469(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_470(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_471(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_472(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_473(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_474(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_475(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_476(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_477(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_478(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_479(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_480(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_481(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_482(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_483(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_484(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_485(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_486(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_487(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_488(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_489(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_490(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_491(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_492(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_493(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_494(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_495(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_496(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_497(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_498(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_499(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_500(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_501(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_502(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_503(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_504(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_505(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_506(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_507(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_508(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_509(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_510(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_511(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_512(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_513(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_514(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_515(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_516(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_517(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_518(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_519(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_520(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_521(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_522(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_523(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_524(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_525(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_526(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_527(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_528(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_529(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_530(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_531(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_532(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_533(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_534(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_535(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_536(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_537(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_538(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_539(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_540(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_541(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_542(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_543(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_544(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_545(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_546(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_547(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_548(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_549(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_550(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_551(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_552(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_553(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_554(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_555(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_556(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_557(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_558(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_559(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_560(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_561(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_562(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_563(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_564(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_565(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_566(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_567(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_568(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_569(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_570(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_571(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_572(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_573(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_574(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_575(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_576(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_577(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_578(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_579(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_580(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_581(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_582(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_583(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_584(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_585(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_586(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_587(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_588(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_589(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_590(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_591(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_592(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_593(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_594(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_595(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_596(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_597(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_598(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_599(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_600(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_601(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_602(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_603(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_604(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_605(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_606(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_607(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_608(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_609(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_610(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_611(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_612(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_613(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_614(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_615(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_616(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_617(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_618(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_619(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_620(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_621(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_622(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_623(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_624(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_625(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_626(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_627(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_628(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_629(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_630(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_631(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_632(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_633(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_634(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_635(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_636(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_637(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_638(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_639(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_640(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_641(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_642(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_643(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_644(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_645(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_646(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_647(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_648(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_649(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_650(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_651(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_652(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_653(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_654(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_655(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_656(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_657(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_658(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_659(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_660(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_661(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_662(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_663(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_664(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_665(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_666(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_667(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_668(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_669(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_670(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_671(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_672(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_673(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_674(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_675(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_676(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_677(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_678(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_679(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_680(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_681(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_682(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_683(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_684(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_685(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_686(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_687(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_688(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_689(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_690(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_691(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_692(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_693(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_694(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_695(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_696(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_697(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_698(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_699(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_700(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_701(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_702(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_703(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_704(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_705(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_706(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_707(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_708(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_709(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_710(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_711(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_712(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_713(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_714(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_715(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_716(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_717(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_718(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_719(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_720(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_721(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_722(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_723(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_724(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_725(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_726(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_727(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_728(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_729(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_730(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_731(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_732(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_733(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_734(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_735(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_736(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_737(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_738(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_739(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_740(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_741(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_742(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_743(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_744(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_745(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_746(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_747(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_748(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_749(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_750(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_751(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_752(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_753(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_754(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_755(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_756(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_757(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_758(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_759(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_760(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_761(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_762(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_763(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_764(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_765(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_766(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_767(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_768(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_769(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_770(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_771(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_772(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_773(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_774(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_775(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_776(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_777(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_778(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_779(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_780(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_781(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_782(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_783(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_784(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_785(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_786(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_787(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_788(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_789(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_790(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_791(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_792(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_793(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_794(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_795(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_796(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_797(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_798(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_799(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_800(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_801(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_802(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_803(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_804(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_805(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_806(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_807(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_808(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_809(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_810(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_811(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_812(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_813(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_814(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_815(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_816(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_817(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_818(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_819(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_820(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_821(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_822(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_823(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_824(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_825(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_826(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_827(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_828(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_829(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_830(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_831(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_832(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_833(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_834(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_835(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_836(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_837(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_838(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_839(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_840(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_841(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_842(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_843(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_844(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_845(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_846(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_847(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_848(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_849(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_850(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_851(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_852(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_853(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_854(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_855(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_856(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_857(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_858(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_859(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_860(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_861(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_862(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_863(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_864(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_865(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_866(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_867(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_868(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_869(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_870(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_871(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_872(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_873(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_874(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_875(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_876(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_877(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_878(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_879(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_880(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_881(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_882(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_883(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_884(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_885(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_886(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_887(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_888(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_889(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_890(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_891(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_892(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_893(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_894(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_895(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_896(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_897(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_898(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_899(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_900(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_901(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_902(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_903(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_904(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_905(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_906(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_907(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_908(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_909(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_910(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_911(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_912(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_913(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_914(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_915(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_916(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_917(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_918(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_919(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_920(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_921(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_922(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_923(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_924(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_925(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_926(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_927(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_928(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_929(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_930(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_931(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_932(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_933(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_934(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_935(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_936(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_937(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_938(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_939(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_940(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_941(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_942(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_943(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_944(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_945(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_946(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_947(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_948(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_949(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_950(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_951(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_952(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_953(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_954(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_955(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_956(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_957(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_958(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_959(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_960(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_961(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_962(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_963(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_964(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_965(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_966(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_967(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_968(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_969(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_970(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_971(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_972(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_973(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_974(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_975(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_976(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_977(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_978(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_979(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_980(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_981(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_982(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_983(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_984(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_985(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_986(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_987(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_988(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_989(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_990(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_991(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_992(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_993(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_994(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_995(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_996(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_997(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_998(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_999(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1000(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1001(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1002(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1003(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1004(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1005(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1006(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1007(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1008(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1009(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1010(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1011(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1012(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1013(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1014(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1015(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1016(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1017(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1018(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1019(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1020(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1021(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1022(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
    private void l_1023(Testable t, int l) {for (int i=0; i<l; i++) {t.test();}}
}
