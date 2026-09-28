#!/usr/bin/env bash
# Summarize observational allocation comparisons; statistical misses do not fail this job.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."

runs=${RUNS:-5}
if ! [[ $runs =~ ^[1-9][0-9]?$ ]] || ((runs > 20)); then
    echo "RUNS must be between 1 and 20" >&2
    exit 2
fi

classpath=performance-tools/target/classes:performance-tools/target/test-classes
probe=com.fillumina.performance.mem.ThreadAllocationCalibration
jvm=$(java -version 2>&1)
jvm=${jvm%%$'\n'*}
equal_false_gates=0
unequal_misses=0
small_increase_misses=0
invalid=0
start=$SECONDS
for ((i = 1; i <= runs; i++)); do
    if output=$(java -cp "$classpath" "$probe" 2>&1); then
        :
    else
        echo "Calibration JVM $i failed: $output" >&2
        exit 1
    fi
    line=${output##*$'\n'}
    if [[ ! $line =~ ^equal_false_gates=([0-9]+)\ unequal_misses=([0-9]+)\ small_increase_misses=([0-9]+)\ invalid=([0-9]+)$ ]]; then
        echo "Calibration JVM $i was unsupported or returned unexpected output: $output" >&2
        exit 1
    fi
    equal_false_gates=$((equal_false_gates + ${BASH_REMATCH[1]}))
    unequal_misses=$((unequal_misses + ${BASH_REMATCH[2]}))
    small_increase_misses=$((small_increase_misses + ${BASH_REMATCH[3]}))
    invalid=$((invalid + ${BASH_REMATCH[4]}))
    printf 'JVM %d/%d: %s\n' "$i" "$runs" "$line"
done

summary=$(printf '### Allocation comparison observations\n\nJVM: %s  \nFresh JVMs: %d  \nComparisons per case: %d (20 per JVM)  \nElapsed: %d seconds\n\n| Equal false gates | Doubled-allocation misses | Small-increase misses | Invalid intervals |\n| ---: | ---: | ---: | ---: |\n| %d | %d | %d | %d |\n\nStatistical misses are reported, not treated as workflow failures. These synthetic controls do not establish reliability for application workloads.\n' \
    "$jvm" "$runs" "$((runs * 20))" "$((SECONDS - start))" \
    "$equal_false_gates" "$unequal_misses" "$small_increase_misses" "$invalid")
printf '%s\n' "$summary"
if [[ -n ${GITHUB_STEP_SUMMARY:-} ]]; then
    printf '%s\n' "$summary" >> "$GITHUB_STEP_SUMMARY"
fi
