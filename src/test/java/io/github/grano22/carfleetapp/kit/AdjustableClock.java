package io.github.grano22.carfleetapp.kit;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;

public class AdjustableClock extends Clock {
    private Instant instant;
    private final ZoneId zone;

    public static AdjustableClock systemUTC() {
        return new AdjustableClock(Instant.EPOCH, ZoneId.of("UTC"));
    }

    public AdjustableClock(Instant initialInstant, ZoneId zone) {
        this.instant = initialInstant;
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new AdjustableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void set(Instant newInstant) {
        this.instant = newInstant;
    }

    public void set(LocalDateTime newDate) {
        this.instant = newDate.atZone(zone).toInstant();
    }

    public void fastForward(TemporalAmount amount) {
        this.instant = this.instant.plus(amount);
    }

    public void rewind(TemporalAmount amount) {
        this.instant = this.instant.minus(amount);
    }
}
