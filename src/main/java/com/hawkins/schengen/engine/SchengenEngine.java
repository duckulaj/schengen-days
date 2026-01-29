package com.hawkins.schengen.engine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

public final class SchengenEngine {

    public record Stay(LocalDate entry, LocalDate exit) {
        public Stay {
            if (entry == null || exit == null)
                throw new IllegalArgumentException("Dates required");
            if (exit.isBefore(entry))
                throw new IllegalArgumentException("exit before entry");
        }
    }

    public static final class MergedStays {
        private final LocalDate[] starts;
        private final LocalDate[] ends;
        private final long[] prefixDays;

        private MergedStays(LocalDate[] starts, LocalDate[] ends, long[] prefixDays) {
            this.starts = starts;
            this.ends = ends;
            this.prefixDays = prefixDays;
        }

        public long overlapDays(LocalDate winStart, LocalDate winEnd) {
            if (starts.length == 0 || winEnd.isBefore(winStart))
                return 0;

            int l = firstIntervalWithEndGte(winStart);
            int r = lastIntervalWithStartLte(winEnd);
            if (l == -1 || r == -1 || l > r)
                return 0;

            long total = prefixDays[r] - (l > 0 ? prefixDays[l - 1] : 0);

            if (winStart.isAfter(starts[l]))
                total -= ChronoUnit.DAYS.between(starts[l], winStart);
            if (winEnd.isBefore(ends[r]))
                total -= ChronoUnit.DAYS.between(winEnd, ends[r]);

            return Math.max(0, total);
        }

        private int firstIntervalWithEndGte(LocalDate d) {
            int lo = 0, hi = ends.length - 1, ans = -1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                if (!ends[mid].isBefore(d)) {
                    ans = mid;
                    hi = mid - 1;
                } else
                    lo = mid + 1;
            }
            return ans;
        }

        private int lastIntervalWithStartLte(LocalDate d) {
            int lo = 0, hi = starts.length - 1, ans = -1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                if (!starts[mid].isAfter(d)) {
                    ans = mid;
                    lo = mid + 1;
                } else
                    hi = mid - 1;
            }
            return ans;
        }
    }

    @SuppressWarnings("null")
    public static MergedStays preprocess(List<@NonNull Stay> stays) {
        if (stays == null || stays.isEmpty())
            return new MergedStays(new LocalDate[0], new LocalDate[0], new long[0]);

        List<@NonNull Stay> sorted = new ArrayList<>(stays);
        sorted.sort(Comparator.comparing(Stay::entry).thenComparing(Stay::exit));

        List<LocalDate> ms = new ArrayList<>();
        List<LocalDate> me = new ArrayList<>();

        LocalDate curS = sorted.get(0).entry();
        LocalDate curE = sorted.get(0).exit();

        for (int i = 1; i < sorted.size(); i++) {
            Stay s = sorted.get(i);
            if (!s.entry().isAfter(curE.plusDays(1))) {
                if (s.exit().isAfter(curE))
                    curE = s.exit();
            } else {
                ms.add(curS);
                me.add(curE);
                curS = s.entry();
                curE = s.exit();
            }
        }
        ms.add(curS);
        me.add(curE);

        LocalDate[] starts = ms.toArray(new LocalDate[0]);
        LocalDate[] ends = me.toArray(new LocalDate[0]);
        long[] prefix = new long[starts.length];

        long run = 0;
        for (int i = 0; i < starts.length; i++) {
            run += ChronoUnit.DAYS.between(starts[i], ends[i]) + 1;
            prefix[i] = run;
        }
        return new MergedStays(starts, ends, prefix);
    }

    public static long usedDaysLast180(MergedStays merged, LocalDate referenceDate) {
        LocalDate windowStart = referenceDate.minusDays(179);
        return merged.overlapDays(windowStart, referenceDate);
    }

    public static long remainingDays(MergedStays merged, LocalDate referenceDate) {
        long used = usedDaysLast180(merged, referenceDate);
        return Math.max(0, 90 - used);
    }

    /** Works even if there are future stays (not monotone). */
    public static LocalDate nextLegalEntryDate(MergedStays merged, LocalDate referenceDate) {
        LocalDate d = referenceDate;
        LocalDate max = referenceDate.plusYears(2);
        while (!d.isAfter(max)) {
            if (usedDaysLast180(merged, d) <= 89)
                return d;
            d = d.plusDays(1);
        }
        return max.plusDays(1);
    }

    private SchengenEngine() {
    }
}
