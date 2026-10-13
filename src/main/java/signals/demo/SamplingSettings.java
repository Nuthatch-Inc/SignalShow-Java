package signals.demo;

/** Sampling choices belong to the document, independently of rebuilt plots. */
final class SamplingSettings {
    int period = 1;
    double filterWidth;
    boolean reconstruct;
    int spatialTab, frequencyTab;

    SamplingSettings(int dimension) {
        filterWidth = dimension;
    }

    SamplingSettings copy() {
        SamplingSettings copy = new SamplingSettings(0);
        copy.period = period; copy.filterWidth = filterWidth; copy.reconstruct = reconstruct;
        copy.spatialTab = spatialTab; copy.frequencyTab = frequencyTab;
        return copy;
    }
}
