package signals.demo;

/** Compact choices shared with the live view and copied when a demo is duplicated. */
final class FrequencyLocationSettings {
    double innerRadius = 8, outerRadius = 24;
    boolean dc, imaginary;
    boolean low = true, mid = true, high = true, absolute = true;

    FrequencyLocationSettings copy() {
        FrequencyLocationSettings copy = new FrequencyLocationSettings();
        copy.innerRadius = innerRadius; copy.outerRadius = outerRadius;
        copy.dc = dc; copy.low = low; copy.mid = mid; copy.high = high;
        copy.absolute = absolute; copy.imaginary = imaginary;
        return copy;
    }
}
