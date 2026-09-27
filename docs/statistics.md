How the numbers are obtained
===========================

This document explains what PerformanceTools actually measures, which
statistical procedures it applies, and — just as importantly — where those
procedures stop being valid. It exists because the output looks like a table of
numbers and it would be easy to assume it is only running loops and dividing.

Contents
--------

1.  [The problem being solved](#the-problem-being-solved)
2.  [What one sample is](#what-one-sample-is)
3.  [Environmental control: interleaving and the canary](#environmental-control-interleaving-and-the-canary)
4.  [Warm-up without a warm-up phase](#warm-up-without-a-warm-up-phase)
5.  [Outlier elimination](#outlier-elimination)
6.  [From samples to an interval](#from-samples-to-an-interval)
7.  [The adaptive stopping rule](#the-adaptive-stopping-rule)
8.  [Comparing measures: ratios and significance](#comparing-measures-ratios-and-significance)
9.  [Reading the output](#reading-the-output)
10. [Where this stops being valid](#where-this-stops-being-valid)
11. [Provenance](#provenance)

The problem being solved
------------------------

A single timing figure is nearly worthless on its own. A laptop CPU between
1990 and 2020 already down-clocked under sustained load, changed frequency
with the ambient temperature, and moved work between cores. On current
hardware this is more pronounced, not less: a machine may have performance and
efficiency cores, an aggressive frequency governor, and a scheduler that is
free to migrate a running thread between them mid-measurement.

The framework takes three positions about that:

* It does not try to eliminate the environment. It cannot.
* It arranges for the environment to affect the compared variants roughly
  equally, so that it cancels out of the difference.
* It quantifies the residual, so that you can tell whether the remaining
  difference is real.

Everything below follows from those three positions.

What one sample is
------------------

A **sample** is one measurement of every compared test, performed back to back
in randomised order. Each sample runs each test for a number of **iterations**;
the per-iteration time is what is recorded. The framework chooses the
iteration count itself unless you pin it, aiming for roughly 250 ms of work
per sample, and raises the count when a test turns out to be very fast.

Statistics are then computed over the per-test series of sample values. With
33 samples you have 33 observations per variant. An adaptive run uses those
as exploration and then takes at least 33 more unfiltered samples for reporting.
Fixed-sample runs retain their configured count and do not add validation.

Environmental control: interleaving and the canary
---------------------------------------------------

**Interleaving.** Within a sample, every compared test is executed once, and
the order is **shuffled** before the round. This is the single most important
mechanism in the whole design. Without it, whichever variant consistently
occupies the cooler, higher-frequency position wins, and the result is an
artefact of ordering rather than a property of the code. With randomisation,
ordering bias is a zero-mean nuisance term that averages out over samples.

**The canary.** A separate mechanism guards against a slow drift in machine
state. A constant workload (an LFSR sequence, which neither allocates nor
performs I/O) is timed periodically. If that canary degrades by more than 20%
against its own running mean, the CPU is treated as down-clocked: the run is
paused and the framework sleeps, with the pause growing on each attempt up to
30 times, until the canary recovers. The canary is also warmed up before
measurement, to pull the core out of an idle frequency state.

This is why a slow result is not always a slow code — and why the output
annotates samples with `CPU is cool` when the intervention fired.

Warm-up without a warm-up phase
-------------------------------

The JIT compiles and optimises code while you measure it, so early samples
represent different code from late ones. Traditional tools answer this with an
explicit warm-up phase that you must size by hand, and get wrong.

**ConvergenceFilter** answers it structurally. It walks the samples
*backwards*, from the most recent, accumulating a running mean and standard
deviation. It tolerates up to 10 non-conforming samples before it decides it
has left the stable region, requires at least 33 stable samples to accept its
result, and if it cannot assemble those it returns the original series
unfiltered rather than returning something it does not trust. Exploration
uses the stable tail to choose when to stop, but the final adaptive result
comes from fresh unfiltered validation samples. There is no separate warm-up
parameter to tune; convergence during exploration does not prove the JIT will
remain stable during validation.

Outlier elimination
-------------------

**OutlierEliminatorFilter** repeatedly applies a z-score test, discarding
samples more than 3 standard deviations from the mean, using the
Bessel-corrected (N-1) standard deviation. It iterates until the survivor set
stops shrinking, so a single extreme sample cannot drag the mean and thereby
excuse further outliers.

The reason for filtering at all is external: an operating-system preemption, a
GC pause, or a background process produces a sample that is not a measurement
of your code. Those samples are real events and they belong in the record, but
they must not be allowed to masquerade as a property of the algorithm.
When any variant rejects an observation, filtering discards the entire round
from the exploratory comparison. The later validation run is not filtered,
to avoid selecting the data used for the reported interval.

From samples to an interval
---------------------------

For each test the framework keeps count, mean, variance, minimum and maximum.
The variance is Bessel-corrected. From it:

    standard deviation   s      = sqrt(variance)
    standard error       SE     = s / sqrt(N)
    margin of error      MoE    = SE * z(confidence)
    fractional uncertainty       = |MoE / mean|

`MoE` is what the framework reports as the tolerance on a ratio or a mean, and
it is the quantity the stopping rule drives. It is the half-width of a two-tailed
normal confidence interval, where the multiplier is obtained by inverting the
normal CDF: a 99% interval uses z = 2.576.

Because `MoE` falls as 1/sqrt(N), precision improves with sample count, and the
framework spends samples until the number is small enough to be worth acting
on. That is the whole argument for the next section.

The adaptive stopping rule
--------------------------

`RequiredMarginStrategy` is the default. It does not take a fixed number of
samples. After each sample it computes the largest margin of error across all
measures and all ratios, and keeps sampling while that exceeds a required
margin. Defaults: **33 samples minimum, 5% maximum margin, 99% confidence**.

A full run is therefore a loop: sample, filter, compute intervals, decide,
repeat. The reported error is also monotone in practice, which is what lets the
framework display a meaningful ETA, and a timeout guard aborts a pathological
run loudly instead of hanging.

This is why you do not choose an exploratory sample count. Once it stops, the
framework takes a separate fixed validation set of at least 33 samples and
reports only that set. If its ratio margin exceeds the requirement, or an
obvious lag-one serial dependence is detected, the run fails inconclusively
instead of selecting another favorable endpoint. Validation adds time and
remains subject to the overall timeout.

Comparing measures: ratios and significance
-------------------------------------------

Every measure is compared against the largest one, producing a *ratio*. A ratio
is the right unit of comparison here for two reasons: it is dimensionless, and
it is far more stable across environments than an absolute difference, because
a proportional slowdown of the whole machine affects numerator and denominator
alike.

Two levels of test are applied.

**Overall: one-way ANOVA.** The framework decomposes total variance into the
part explained by *which variant* was measured (between) and the part that is
within-variant noise. The ratio of the two mean squares is an F statistic,
reported as `ANOVA`. A value near 1.0 means the variants are not
distinguishable; values near 0 mean they clearly are.

**Per pair: Tukey-Kramer and Games-Howell.** For each pair of variants the
framework computes a studentised range and returns a p-value, shown in the
`significance` column.

The choice here is deliberate and is the part most tools get wrong. Timing data
is **heteroscedastic**: variance grows with the mean, so a 200 ns operation and
a 2000 ns operation do not have the same relative spread. Tukey's test assumes
equal variances and is therefore the wrong tool for this data. Games-Howell is
the Welch-style variant that does not assume them, and the framework uses it
for the pairwise comparisons. The Tukey-Kramer path is retained for callers that
have verified homoscedasticity.

Reading the output
------------------

    Required measure confidence : the confidence level applied throughout
    Max ratio percentage error  : the worst margin of error across all pairs;
                                  this is the number to look at
    ANOVA                       : overall separability of the variants
    ratio vs slower             : each variant relative to the largest
    average time, +/-           : mean and margin of error
    stdev                       : sample standard deviation
    uncertainty                 : margin of error as a percentage of the mean
    smpl                        : observations in the reported set (unfiltered
                                  for adaptive validation; possibly filtered
                                  for a fixed-sample run)
    significance                : 0.999 means "these two are different";
                                  0.100 means "no evidence of a difference"

The habit worth forming: read `significance` before `average time`. A 3%
difference with significance 0.100 is noise, and reporting it as a result is
the most common way a measurement gets misused. The `smpl` column is also
worth checking — a low retained count means filtering removed most of what was
collected and the result is thin.

Where this stops being valid
---------------------------

Stated plainly, because the value of everything above depends on it.

**Samples are assumed independent, and on a busy machine they are not.**
Successive timings are autocorrelated — a sample that runs slow makes the next
one more likely to run slow — and the confidence intervals above are the
independent-sample intervals. On a loaded or thermally throttling machine the
true uncertainty may therefore be larger than reported. A simple lag-one
check rejects strong dependence in adaptive validation; weaker or higher-lag
dependence can remain. The seeded coverage tests show undercoverage for strongly
autocorrelated data. Neither the filter nor the check is a complete model.
This is the direct price
of measuring in situ rather than in isolation, and it is the regime in which a
small reported difference should be trusted least.

**The margin of error uses the normal quantile, not Student's t.** The
Student-t and F distributions are implemented and are used for the pairwise
tests, but the margin-of-error path multiplies by a normal z. For 33 samples
the two are close; for very small sample counts the normal interval is
optimistic. If you pin the sample count low, treat the reported margin as a
floor rather than a bound.

**A single sample reports zero uncertainty.** With `smpl` of 1 the margin of
error is returned as zero. This is a deliberate shortcut in the code, not a
statistical statement, and it is a trap: a one-sample measurement looks
infinitely precise.

**No CPU pinning and no core-type awareness.** On a hybrid processor a thread
can be migrated onto an efficiency core mid-run, a two to three times penalty.
Neither the shuffling nor the canary corrects this, because the canary is a
separate workload that may land on a different core than the code being
measured. The framework absorbs it statistically — more samples, wider margin —
rather than mechanically. When chasing a small delta, pin the threads yourself
and treat the number as trustworthy.

**The independence of the compared tests is assumed, not enforced.** Two tests
that contend for the same resource, the same cache or the same lock are not
independent measurements, and no amount of statistics repairs that. Interleaved
execution also means the variants share the machine with each other; the
framework warns about this and suggests a low interleaving fraction when tests
are known to interfere.

**Absolute figures are session-local.** `28.7 us/op` describes that run, on
that machine, at that moment. It is not comparable across machines or runs. The
ratio is the portable quantity.

Provenance
----------

The distribution functions in `StatFunctions` are after John C. Pezzullo
(statpages.info). The incomplete-epsilon-difference engine beneath them, and
the studentized-range approximation in `Qsturng`, are after Roger Lew, funded
in part by NIH grant P20 RR016454, and implement Gleason's (1999) non-iterative
upper-quantile studentized range approximation. Both are retained with their
original attribution and licence notes.

The broader literature this design draws on is listed in the
[bibliography](../README.md#bibliography).
