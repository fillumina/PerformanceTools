# Templates

There are 4 templates available:

1.  [SimplePerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/SimplePerformanceTemplate.java)
    is just a skeleton that enables some basic templating but isn't really
    useful per-se.

2.  [AutoProgressionPerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/SimplePerformanceTemplate.java)
    allows to set a target stability value expressed by the maximum allowed
    standard deviation that a serie of samples of a certain number
    of iterations of the tests must have. In order to stabilize up to this value this
    template will repeat the serie of iterations incrementing its value over time.
    It's useful because it doesn't force to discover manually the number of
    iterations at which the test stabilizes.

    The JUnit template here is just an adaptation of the original template
    and can be found in the module ```performance-tools-junit```
    (it's in a different module to avoid having JUnit as a dependence on the
    core module).

    ```java
    public class DivisionByTwoPerformanceTest
            extends JUnitAutoProgressionPerformanceTemplate {

        public static void main(final String[] args) {
            new DivisionByTwoPerformanceTest().executeWithIntermediateOutput();
        }

        @Override
        public void init(ProgressionConfigurator config) {
            config.setMaxStandardDeviation(2);
        }

        @Override
        public void addTests(TestsContainer tests) {
            final Random rnd = new Random(System.currentTimeMillis());

            tests.addTest("math", new RunnableSink() {

                @Override
                public Object sink() {
                    return rnd.nextInt() / 2;
                }
            });

            tests.addTest("binary", new RunnableSink() {

                @Override
                public Object sink() {
                    return rnd.nextInt() >> 1;
                }
            });
        }

        @Override
        public void addAssertions(PerformanceAssertion assertion) {
            assertion.withPercentageTolerance(2)
                    .assertTest("binary").fasterThan("math");
        }
    }
    ```

3.  [ParametrizedPerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedPerformanceTemplate.java)
    allows to define a test that accepts a parameter and then to define and pass to
    the test several parameters. It is useful to check which of a serie of
    alternative objects performs better (i.e. which different `Map` perform
    better writing objects randomly).

    ```java
    public class MapMultiThreadedPerformanceTest
            extends JUnitParametrizedPerformanceTemplate<Map<Integer, String>> {
        private static final int MAX_CAPACITY = 128;

        public static void main(final String[] args) {
            new MapMultiThreadedPerformanceTest().executeWithOutput();
        }

        @Override
        public void init(final ProgressionConfigurator config) {
            config.setConcurrencyLevel(32)
                    .setBaseIterations(1_000)
                    .setMaxStandardDeviation(25)
                    .setTimeoutSeconds(100);
        }

        @Override
        public void addParameters(
                final ParametersContainer<Map<Integer, String>> parameters) {
            parameters.addParameter("SynchronizedLinkedHashMap",
                    Collections.synchronizedMap(
                        new LinkedHashMap<Integer, String>(MAX_CAPACITY)));

            parameters.addParameter("ConcurrentHashMap",
                    new ConcurrentHashMap<Integer, String>(MAX_CAPACITY));

            parameters.addParameter("SynchronizedHashMap",
                    Collections.synchronizedMap(
                        new HashMap<Integer, String>(MAX_CAPACITY)));
        }

        @Override
        public void executeTests(
                final ParametrizedExecutor<Map<Integer, String>> executor) {

            executor.executeTest("CONCURRENT RANDOM READ",
                    new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

                @Override
                public void setUp(final Map<Integer, String> map) {
                    fillUpMap(map, MAX_CAPACITY);
                }

                @Override
                protected Random createLocalObject() {
                    return new Random(System.currentTimeMillis());
                }

                @Override
                public Object call(final Random rnd, final Map<Integer, String> map) {
                    assertNotNull(map.get(rnd.nextInt(MAX_CAPACITY)));
                    return map;
                }
            });

            executor.executeTest("CONCURRENT RANDOM WRITE",
                    new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

                @Override
                protected Random createLocalObject() {
                    return new Random(System.currentTimeMillis());
                }

                @Override
                public Object call(final Random rnd, final Map<Integer, String> map) {
                    map.put(rnd.nextInt(MAX_CAPACITY), "xyz");
                    return map;
                }
            });
        }

        @Override
        public void addAssertions(final SuiteExecutionAssertion assertion) {
            assertion.forExecution("CONCURRENT RANDOM READ")
                .withPercentageTolerance(7)
                .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

            assertion.forExecution("CONCURRENT RANDOM WRITE")
                .withPercentageTolerance(7)
                .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");
        }

        private static void fillUpMap(final Map<Integer, String> map,
                final int maxCapacity) {
            map.clear();
            final List<Integer> list = new ArrayList<>(maxCapacity);
            for (int i=0; i<maxCapacity; i++) {
                list.add(i);
            }
            Collections.shuffle(list, new Random(System.currentTimeMillis()));
            for (int i=0; i<maxCapacity; i++) {
                map.put(list.get(i), "xyz");
            }
        }
    }
    ```

4.  [ParametrizedSequencePerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedSequencePerformanceTemplate.java)
    in addition to accepting a parameter it accepts an elemnet of a sequence too. It
    is useful to check which of a series of alternative objects perform better on
    different input (i.e. which different `Map` perform better writing objects
    randomly in a map of different sizes).

    ```java
    public class SearchTypePerformanceTest
            extends JUnitParametrizedSequencePerformanceTemplate<Searcher, String[]>{

        public interface Searcher {
            int position(final String[] strings, final String str);
        }

        static class LinearSearcher implements Searcher {

            @Override
            public int position(final String[] strings, final String str) {
                for (int i=0,max=strings.length; i<max; i++) {
                    if (strings[i].equals(str)) {
                        return i;
                    }
                }
                throw new AssertionError();
            }
        }

        static class BinarySearcher implements Searcher {

            @Override
            public int position(final String[] strings, final String str) {
                return Arrays.binarySearch(strings, str);
            }
        }

        public static void main(final String[] args) {
            new SearchTypePerformanceTest().executeWithOutput();
        }

        @Override
        public void init(final ProgressionConfigurator config) {
            config.setMaxStandardDeviation(2)
                    .setPrintOutStdDeviation(false);
        }

        @Override
        public void addParameters(final ParametersContainer<Searcher> parameters) {
            parameters.addParameter("linear", new LinearSearcher())
                    .addParameter("binary", new BinarySearcher());

        }

        @Override
        public void addSequence(final SequenceContainer<?, String[]> sequences) {
            final String[] locales = Locale.getISOCountries();
            Arrays.sort(locales);
            sequences.setSequence(Arrays.copyOf(locales, 10),
                    Arrays.copyOf(locales, 30));

            sequences.setSequenceNominator(new SequenceNominator<String[]>() {

                @Override
                public String toString(final String[] sequenceItem) {
                    return String.valueOf(sequenceItem.length);
                }
            });
        }

        @Override
        public void addIntermediateAssertions(final PerformanceAssertion assertion) {
            assertion.withPercentageTolerance(5F)
                    .assertPercentageFor("linear").greaterThan(50);
        }

        @Override
        public void addAssertions(final AssertionSuiteBuilder assertionBuilder) {
            SuiteExecutionAssertion assertion = assertionBuilder.withTolerance(5F);

            assertion.forExecution("test-10")
                .assertTest("linear").fasterThan("binary");

            assertion.forExecution("test-30")
                .assertTest("binary").fasterThan("linear");
        }

        @Override
        public ParametrizedSequenceRunnable<Searcher, String[]> getTest() {
            return new ParametrizedSequenceRunnable<Searcher, String[]>() {
                final Random rnd = new Random(System.currentTimeMillis());

                @Override
                public Object sink(final Searcher param, final String[] sequence) {
                    final int pos = rnd.nextInt(sequence.length);
                    final int result = param.position(sequence, sequence[pos]);
                    assertEquals(pos, result);
                    return null;
                }
            };
        }

    }
    ```

[Back to index](documentation_index.md)