package signals.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.io.ImageWriter.ImageType;

/** Ordinary save paths must retain their parent folders and distinct basenames. */
class SavePathTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "/Users/roger.easton/SignalShow 1.2.5/result",
        "C:\\Users\\Roger.Easton\\SignalShow 1.2.5\\result",
        "C:/Users/Roger.Easton/SignalShow 1.2.5/result",
        "experiment.1/results/image"
    })
    void dotsInFoldersAreNotFilenameExtensions(String path) {
        assertNull(Utils.getExtension(path));
        assertEquals(path, Utils.getNameNoExtension(path));
        assertEquals(path + ".tiff", ImageWriter.replaceExtension(path, ImageType.TIFF));
        assertEquals(path, Utils.getNameNoExtension(path + ".txt"));
        assertEquals("txt", Utils.getExtension(path + ".txt"));
        assertEquals(path + ".png", ImageWriter.replaceExtension(path + ".txt", ImageType.PNG));
        assertNotEquals(Utils.getNameNoExtension(path), Utils.getNameNoExtension(path + " other"));
    }
}
