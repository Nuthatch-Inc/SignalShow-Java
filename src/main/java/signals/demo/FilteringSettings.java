package signals.demo;

/** Document-owned controls only; no views, source functions or result arrays. */
final class FilteringSettings {
    enum Type { LOW_PASS, HIGH_PASS, BAND_PASS, BAND_REJECT }
    Type type = Type.LOW_PASS;
    boolean gaussian;
    Object[] parameters;
    int spatialTab, frequencyTab;

    FilteringSettings copy() {
        FilteringSettings copy = new FilteringSettings();
        copy.type = type; copy.gaussian = gaussian;
        copy.parameters = parameters == null ? null : parameters.clone();
        copy.spatialTab = spatialTab; copy.frequencyTab = frequencyTab;
        return copy;
    }
}
