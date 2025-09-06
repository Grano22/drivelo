package io.github.grano22.carfleetapp.shared;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ViewFormatters {
    private static DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.atOffset(ZoneOffset.UTC).format(isoFormatter);
    }
}
