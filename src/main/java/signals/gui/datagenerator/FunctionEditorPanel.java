package signals.gui.datagenerator;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import signals.core.Deconstructable;

/** Owns the editor lifetime while keeping global options in the existing layout. */
@SuppressWarnings("serial")
final class FunctionEditorPanel extends JPanel implements Deconstructable {
    private final CreateFunctionPart2DPanel editor;

    FunctionEditorPanel(CreateFunctionPart2DPanel editor, JPanel globalOptions) {
        super(new BorderLayout());
        this.editor = editor;
        add(editor, BorderLayout.CENTER);
        add(globalOptions, BorderLayout.NORTH);
    }

    @Override public void deconstruct() {
        editor.deconstruct();
    }
}
