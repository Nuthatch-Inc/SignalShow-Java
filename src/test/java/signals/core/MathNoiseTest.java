package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import signals.gui.Borders;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.functionterm.*;
import signals.gui.ParameterEditor;
import signals.gui.datagenerator.*;
import signals.operation.SquaredMagnitudeOp;

class MathNoiseTest extends MathAuditSupport {
    private Borders previousBorders;
    @BeforeEach void initializeEditorServices() {previousBorders=Core.get().borders;Core.get().borders=new Borders();}
    @AfterEach void restoreEditorServices() {Core.get().borders=previousBorders;}

    private static final List<String> IMAGE_KINDS=List.of("Gaussian","Uniform","Exponential","Rayleigh","Poisson","Lorentz","SaltAndPepper");

    @ParameterizedTest
    @ValueSource(strings={"Gaussian","Uniform","Exponential","Rayleigh","Poisson","Lorentz","SaltAndPepper","Binomial"})
    void ordinaryNoiseParametersHaveTheExpectedDistribution(String kind) throws Exception {
        NoiseFunctionTerm1D term=noise1D(kind);
        configure(term,kind);
        for(int seed:new int[]{17,91}) {
            term.setSeed(seed);double[] samples=term.create(32768);
            assertArrayEquals(samples,term.create(32768),0,"same seed regenerates exactly");
            for(double value:samples)assertTrue(Double.isFinite(value));
            if(kind.equals("Lorentz")) {
                assertEquals(.25,fractionAtMost(samples,1.4),.015);
                assertEquals(.50,fractionAtMost(samples,2),.015);
                assertEquals(.75,fractionAtMost(samples,2.6),.015);
                continue; // Cauchy/Lorentz has no finite mean or variance.
            }
            double expectedMean=kind.equals("Poisson")?4:2;
            double expectedVariance=switch(kind) {
                case "Gaussian" -> .36;
                case "Uniform" -> .12; // Width is labeled half-width, not standard deviation.
                case "Exponential","Poisson" -> 4;
                case "Rayleigh" -> 4*(4/Math.PI-1);
                case "SaltAndPepper" -> .2*.6*.6;
                case "Binomial" -> 8*.25*.75;
                default -> throw new AssertionError(kind);
            };
            double mean=Arrays.stream(samples).average().orElseThrow(),variance=0;
            for(double value:samples)variance+=(value-mean)*(value-mean)/samples.length;
            assertEquals(expectedMean,mean,.04,kind+" mean");
            assertEquals(expectedVariance,variance,Math.max(.012,.08*expectedVariance),kind+" variance");
            if(kind.equals("Uniform"))for(double value:samples)assertTrue(value>=1.4&&value<=2.6);
            if(kind.equals("Poisson")||kind.equals("Binomial"))for(double value:samples) {
                assertEquals(Math.rint(value),value,0);assertTrue(value>=0);
                if(kind.equals("Binomial"))assertTrue(value<=8);
            }
            if(kind.equals("Exponential"))assertEquals(.5,fractionAtMost(samples,2*Math.log(2)),.015);
            if(kind.equals("Rayleigh"))assertEquals(.5,fractionAtMost(samples,2/Math.sqrt(Math.PI/2)*Math.sqrt(2*Math.log(2))),.015);
            if(kind.equals("SaltAndPepper")) {
                long centerCount=0;
                for(double value:samples) {assertTrue(value==1.4||value==2||value==2.6);if(value==2)centerCount++;}
                assertEquals(.8,centerCount/(double)samples.length,.015);
            }
        }
    }

    static Stream<Arguments> images() {
        return IMAGE_KINDS.stream().flatMap(kind->Stream.of(
                Arguments.of(kind,128,64,true),Arguments.of(kind,128,64,false),
                Arguments.of(kind,64,128,true),Arguments.of(kind,64,128,false)));
    }

    @ParameterizedTest
    @MethodSource("images")
    void realSeedControlEditsRegenerateNoiseAndComplexIntensityChains(String kind,int w,int h,boolean centered) throws Exception {
        NoiseFunctionTerm2D noise=(NoiseFunctionTerm2D) Class.forName("signals.functionterm."+kind+"NoiseFunctionTerm2D")
                .getDeclaredConstructor().newInstance();
        noise=(NoiseFunctionTerm2D)noise.getDefaultInstance();
        configure(noise.getFunctionTerm1D(),kind);
        NoiseFunctionTerm2D editedNoise=noise;
        GaussianNoiseFunctionTerm2D imaginary=(GaussianNoiseFunctionTerm2D)new GaussianNoiseFunctionTerm2D().getDefaultInstance();
        imaginary.getFunctionTerm1D().setCenter(-1);imaginary.getFunctionTerm1D().setAmplitude(.4);imaginary.getFunctionTerm1D().setSeed(61);
        RealImagFunction2D source=new RealImagFunction2D(part(noise),part(imaginary),"complex noise",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        SwingUtilities.invokeAndWait(()->{
            GUIEventBroadcaster broadcaster=new GUIEventBroadcaster();
            EditNoiseFunctionTerm2DPanel editor=new EditNoiseFunctionTerm2DPanel(broadcaster,GUIEvent.Descriptor.MODIFIED);
            editor.setIndices(w,h,centered);editor.setFunctionTerm(editedNoise);
            broadcaster.addGUIEventListener(event->{if(event.getDescriptor()==GUIEvent.Descriptor.MODIFIED)source.setPartA(source.getPartA());});
            ParameterEditor seed=seedEditor(editor);assertNotNull(seed);
            double[] previous=source.getReal().clone();
            for(int value:new int[]{17,29}) {
                seed.setText(Integer.toString(value));
                assertEquals(value,editedNoise.getFunctionTerm1D().getSeed(),"edited seed remains an integer");
                double[] r=editedNoise.create(w,h,centered),i=imaginary.create(w,h,centered),power=new double[w*h];
                assertFalse(Arrays.equals(previous,r),"seed changes the image");
                assertImage(source,r,i,w,h,centered);
                for(int k=0;k<r.length;k++)power[k]=r[k]*r[k]+i[k]*i[k];
                assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
                Function2D copy=source.clone();assertImage(copy,r,i,w,h,centered);
                previous=r.clone();
            }
        });
    }

    @Test void poissonSeedFieldAlsoWorksInTheOneDimensionalEditor() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            PoissonNoiseFunctionTerm1D term=(PoissonNoiseFunctionTerm1D)new PoissonNoiseFunctionTerm1D().getDefaultInstance();
            EditAnalyticFunctionTerm1DPanel editor=new EditAnalyticFunctionTerm1DPanel(new GUIEventBroadcaster(),GUIEvent.Descriptor.MODIFIED);
            double[] indices=new double[128];for(int k=0;k<128;k++)indices[k]=k-64;
            editor.setIndices(indices);editor.setFunctionTerm(term);
            ParameterEditor seed=seedEditor(editor);assertNotNull(seed);
            for(int value:new int[]{17,29}) {
                seed.setText(Integer.toString(value));assertEquals(value,term.getSeed());
                assertEquals(128,term.create(128).length);
            }
        });
    }

    private static NoiseFunctionTerm1D noise1D(String kind) throws Exception {
        DataGenerator prototype=(DataGenerator)Class.forName("signals.functionterm."+kind+"NoiseFunctionTerm1D").getDeclaredConstructor().newInstance();
        return (NoiseFunctionTerm1D)prototype.getDefaultInstance();
    }
    private static void configure(NoiseFunctionTerm1D term,String kind) {
        term.setCenter(kind.equals("Poisson")?4:2);
        if(kind.equals("Binomial"))term.setParameter(8,1);
        else if(term.hasAmplitude())term.setAmplitude(.6);
        if(kind.equals("SaltAndPepper"))term.setParameter(.2,2);
    }
    private static double fractionAtMost(double[] samples,double limit) {
        int count=0;for(double value:samples)if(value<=limit)count++;return count/(double)samples.length;
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
    private static ParameterEditor seedEditor(Container root) {
        for(Component child:root.getComponents()) {
            if(child instanceof JLabel label&&label.getText().startsWith("Seed")&&label.getLabelFor() instanceof ParameterEditor editor)return editor;
            if(child instanceof Container container) {ParameterEditor result=seedEditor(container);if(result!=null)return result;}
        }
        return null;
    }
}
