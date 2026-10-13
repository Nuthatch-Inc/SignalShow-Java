package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ThumbnailListRenderer;
import signals.gui.VerticalThumbnailList;

class ThumbnailLifecycleWorkflowTest {
    private FunctionCreationOptions creation;
    private DisplayOptions display;
    private Colors colors;
    private Borders borders;

    @BeforeEach void initialize() {
        Core c = Core.get();
        creation=c.functionCreationOptions; display=c.displayOptions; colors=c.colors; borders=c.borders;
        c.functionCreationOptions=new FunctionCreationOptions(); c.displayOptions=new DisplayOptions();
        c.colors=new Colors(); c.borders=new Borders();
    }
    @AfterEach void restore() {
        Core c=Core.get(); c.functionCreationOptions=creation; c.displayOptions=display;
        c.colors=colors; c.borders=borders;
    }

    @ParameterizedTest @ValueSource(booleans={true,false})
    void deletingDocumentsReleasesTheRemovedThumbnailButKeepsLiveOnes(boolean deleteRendered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function a=FunctionFactory.createFunction2D(new double[256],new double[256],true,"a",16,16);
            Function b=FunctionFactory.createFunction2D(new double[256],new double[256],true,"b",16,16);
            VerticalThumbnailList list=new VerticalThumbnailList();
            list.setSelecting(false); list.setSize(180,360); list.addItem(a);
            if (!deleteRendered) list.addItem(b);
            ThumbnailListRenderer renderer=(ThumbnailListRenderer)list.getCellRenderer();
            renderer.getListCellRendererComponent(list,a,0,false,false);
            list.removeItem(deleteRendered ? a : b);
            try {
                Field owner=ThumbnailListRenderer.class.getDeclaredField("thumbnailProducer");
                owner.setAccessible(true);
                if(deleteRendered) assertNull(owner.get(renderer),"An empty renderer must not retain a deleted document graph");
                else assertSame(a,owner.get(renderer),"Removing another row must preserve the current renderer");
            } catch(ReflectiveOperationException e) { throw new AssertionError(e); }
            renderer.setSize(180,180);
            var graphics=new BufferedImage(180,180,BufferedImage.TYPE_INT_ARGB).createGraphics();
            try { assertDoesNotThrow(() -> renderer.paint(graphics)); } finally { graphics.dispose(); }
        });
    }
}
