package com.tddapps.utils;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.UtcNow;

public class NowReaderImpl implements NowReader {
    @Override
    public Date ReadUtc() {
        return UtcNow();
    }

    @Override
    public long ReadEpochSecond() {
        return EpochSecondsNow();
    }
}
