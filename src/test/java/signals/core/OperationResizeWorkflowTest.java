package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.ConstantFunctionTerm2D;
import signals.functionterm.XYFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.OperationPreviewPanel;
import signals.gui.datagenerator.OperatorSystemToolBar;
import signals.gui.datagenerator.OperatorSystemToolBar1D;
import signals.gui.datagenerator.OperatorSystemToolBar2D;
import signals.operation.RealOp;
import signals.operation.TimesOp;

class OperationResizeWorkflowTest {
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

  @ParameterizedTest @ValueSource(booleans={false,true})
  void resizeMakesLinkedResultUnavailableUntilBothInputsAgreeThenRecovers(boolean image) {
    Function a=source(image,2,3), b=source(image,5,-1);
    OperatorSystem product=system(image,new FunctionProducer[]{a,b},new TimesOp(new ParameterBlock()),FUNCTION,BINARY_OP_TIER_2,FUNCTION);
    OperatorSystem real=system(image,new FunctionProducer[]{product},new RealOp(new ParameterBlock()),UNARY_OP_TIER_2,FUNCTION);
    assertConstant(real.getFunction(),13,0, image ? 8192 : 128);
    for (int width : new int[]{256,64}) {
      resize(a,width,64);
      assertNull(product.getFunction(),"A size mismatch must not crash or truncate either operand");
      assertNull(real.getFunction(),"Downstream consumers must not reuse the previous result");
      BufferedImage thumbnail=new BufferedImage(170,170,BufferedImage.TYPE_INT_ARGB);
      var graphics=thumbnail.createGraphics();
      try { real.paintGraphic(graphics,170,170); } finally { graphics.dispose(); }
      // Reopening/duplicating a linked expression must tolerate its unavailable source.
      assertNull(real.clone().getFunction());
      real.setInputList(new ArrayList<>(Arrays.asList(product)));
      assertNull(real.getFunction());
      resize(b,width,64);
      assertConstant(product.getFunction(),13,13,image ? width*64 : width);
      Function result=real.getFunction();
      assertConstant(result,13,0,image ? width*64 : width);
      assertSame(result,real.getFunction());
    }
  }

  @ParameterizedTest @CsvSource({"false,true", "false,false", "true,true", "true,false"})
  void constantImageChannelsCoverTheWholeGridAfterResize(boolean polar, boolean centered) {
    FunctionPart2D first = imageConstant(polar ? 5 : 2);
    FunctionPart2D second = imageConstant(polar ? 0.5 : 3);
    Function2D input = polar
        ? new MagPhaseFunction2D(first, second, "constant polar", 128, 64, centered)
        : new RealImagFunction2D(first, second, "constant Cartesian", 128, 64, centered);
    Function2D multiplier = new RealImagFunction2D(imageConstant(2), imageConstant(0),
        "multiplier", 128, 64, centered);
    OperatorSystem product = system(true, new FunctionProducer[]{input,multiplier},
        new TimesOp(new ParameterBlock()), FUNCTION, BINARY_OP_TIER_2, FUNCTION);
    double real = polar ? 5 * Math.cos(0.5) : 2;
    double imaginary = polar ? 5 * Math.sin(0.5) : 3;
    assertConstant(product.getFunction(), 2*real, 2*imaginary, 128*64);
    for (int[] size : new int[][]{{256,128},{64,32}}) {
      resize(input,size[0],size[1]);
      assertNull(product.getFunction());
      resize(multiplier,size[0],size[1]);
      assertConstant(input,real,imaginary,size[0]*size[1]);
      assertConstant(product.getFunction(),2*real,2*imaginary,size[0]*size[1]);
    }
    ConstantFunctionTerm2D term = (ConstantFunctionTerm2D) first.getTermsList().get(0);
    term.setAmplitude(polar ? 3 : -1);
    input.setPartA(first);
    real = polar ? 3*Math.cos(0.5) : -1;
    imaginary = polar ? 3*Math.sin(0.5) : 3;
    assertConstant(product.getFunction(),2*real,2*imaginary,64*32);
  }

  private static FunctionPart2D imageConstant(double value) {
    ConstantFunctionTerm2D term = (ConstantFunctionTerm2D) new ConstantFunctionTerm2D().getDefaultInstance();
    term.setAmplitude(value);
    term.setWidth(128); term.setHeight(64); // Initial image-editor extent, retained on resize.
    return new FunctionPart2D(new ArrayList<>(Arrays.asList(term)),
        new CombineTermsRule(new int[]{CombineTermsRule.FUNCTION_TERM}));
  }

  @Test void equalSampleCountDoesNotMakeDifferentImageShapesCompatible() {
    Function a=source(true,2,3), b=source(true,5,-1);
    resize(b,64,128);
    OperatorSystem p=system(true,new FunctionProducer[]{a,b},new TimesOp(new ParameterBlock()),FUNCTION,BINARY_OP_TIER_2,FUNCTION);
    assertNull(p.getFunction(),"128x64 and 64x128 have different spatial coordinates");
    resize(b,128,64);
    assertConstant(p.getFunction(),13,13,8192);
  }

  @Test void unavailableOutputHidesThePreviousImagePreviewAndCanShowARecoveredResult() throws Exception {
    SwingUtilities.invokeAndWait(()->{
      OperationPreviewPanel preview=new OperationPreviewPanel(true);
      Function input=source(true,2,3);
      preview.updatePreview(input);
      assertTrue(preview.getComponent(0).isVisible());
      preview.updatePreview(null);
      assertFalse(preview.getComponent(0).isVisible(),"Do not leave old image data looking like a current result");
      preview.updatePreview(input);
      assertTrue(preview.getComponent(0).isVisible());
    });
  }

  @ParameterizedTest @ValueSource(booleans={false,true})
  void unavailableOutputViewActionsKeepTheEditorUsable(boolean image) throws Exception {
    SwingUtilities.invokeAndWait(()->{
      int[] edits={0};
      OperatorSystem empty=image ? new OperatorSystem2D() : new OperatorSystem1D();
      OperatorSystemToolBar toolbar=image ? new OperatorSystemToolBar2D(empty) {
        @Override public void edit() { edits[0]++; }
      } : new OperatorSystemToolBar1D(empty) {
        @Override public void edit() { edits[0]++; }
      };
      toolbar.plot(); toolbar.plotFFT(); toolbar.plotHistogram();
      assertEquals(3,edits[0]);
    });
  }

  private static ConstantFunctionTerm1D constant(double value) {
    ConstantFunctionTerm1D result=(ConstantFunctionTerm1D)new ConstantFunctionTerm1D().getDefaultInstance();
    result.setAmplitude(value); return result;
  }
  private static FunctionPart1D part1D(double value) {
    return new FunctionPart1D(new ArrayList<>(Arrays.asList(constant(value))),new CombineTermsRule(new int[]{CombineTermsRule.FUNCTION_TERM}));
  }
  private static FunctionPart2D part2D(double value) {
    FunctionTerm2D term=new XYFunctionTerm2D(new ParameterBlock().addSource(constant(value)).addSource(constant(1)));
    return new FunctionPart2D(new ArrayList<>(Arrays.asList(term)),new CombineTermsRule(new int[]{CombineTermsRule.FUNCTION_TERM}));
  }
  private static Function source(boolean image,double real,double imaginary) {
    return image ? new RealImagFunction2D(part2D(real),part2D(imaginary),"source",128,64,true)
        : new RealImagFunction1D(part1D(real),part1D(imaginary),"source",128,true);
  }
  private static void resize(Function function,int width,int height) {
    if(function instanceof Function2D f) { f.setDimensionX(width); f.setDimensionY(height); }
    else ((Function1D)function).setDimension(width);
  }
  private static OperatorSystem system(boolean image,FunctionProducer[] inputs,Operation op,int... codes) {
    ArrayList<Operation> ops=new ArrayList<>(Arrays.asList(op));
    CombineOpsRule rule=new CombineOpsRule(codes,ops);
    ArrayList<FunctionProducer> sources=new ArrayList<>(Arrays.asList(inputs));
    return image ? new OperatorSystem2D(rule,sources,ops) : new OperatorSystem1D(rule,sources,ops);
  }
  private static void assertConstant(Function result,double real,double imaginary,int count) {
    assertNotNull(result); assertEquals(count,result.getReal().length); assertEquals(count,result.getImaginary().length);
    for(int i=0;i<count;i++) { assertEquals(real,result.getReal()[i],1e-12); assertEquals(imaginary,result.getImaginary()[i],1e-12); }
  }
}
