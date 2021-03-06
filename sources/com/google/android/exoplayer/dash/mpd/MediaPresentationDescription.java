package com.google.android.exoplayer.dash.mpd;

import com.google.android.exoplayer.util.ManifestFetcher.RedirectingManifest;
import java.util.Collections;
import java.util.List;

public class MediaPresentationDescription implements RedirectingManifest {
    public final long availabilityStartTime;
    public final long duration;
    public final boolean dynamic;
    public final String location;
    public final long minBufferTime;
    public final long minUpdatePeriod;
    private final List<Period> periods;
    public final long timeShiftBufferDepth;
    public final UtcTimingElement utcTiming;

    public MediaPresentationDescription(long availabilityStartTime, long duration, long minBufferTime, boolean dynamic, long minUpdatePeriod, long timeShiftBufferDepth, UtcTimingElement utcTiming, String location, List<Period> periods) {
        this.availabilityStartTime = availabilityStartTime;
        this.duration = duration;
        this.minBufferTime = minBufferTime;
        this.dynamic = dynamic;
        this.minUpdatePeriod = minUpdatePeriod;
        this.timeShiftBufferDepth = timeShiftBufferDepth;
        this.utcTiming = utcTiming;
        this.location = location;
        if (periods == null) {
            periods = Collections.emptyList();
        }
        this.periods = periods;
    }

    public final String getNextManifestUri() {
        return this.location;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int index) {
        return (Period) this.periods.get(index);
    }

    public final long getPeriodDuration(int index) {
        if (index == this.periods.size() - 1) {
            return this.duration == -1 ? -1 : this.duration - ((Period) this.periods.get(index)).startMs;
        } else {
            return ((Period) this.periods.get(index + 1)).startMs - ((Period) this.periods.get(index)).startMs;
        }
    }
}
