package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.functionterm.*;
import signals.operation.SquaredMagnitudeOp;

class MathStochasticProcessesTest extends MathAuditSupport {
    @ParameterizedTest
    @ValueSource(doubles={.1,.5,1,2})
    void poissonProcessHasIndependentPoissonOccupancyAtOrdinaryRates(double rate) {
        PoissonProcessFunctionTerm1D term=process(-2,rate,17);
        // P(N([x,x+1))>0)=1-exp(-rate); disjoint unit bins are independent.
        double p=1-Math.exp(-rate);
        for(int seed:new int[]{17,91})for(boolean centered:new boolean[]{false,true}) {
            term.setSeed(seed);double[] samples=term.create(65536,centered);
            long occupied=0,adjacent=0;
            for(int k=0;k<samples.length;k++) {
                assertTrue(samples[k]==0||samples[k]==-2,"one amplitude per occupied sample");
                if(samples[k]!=0)occupied++;
                if(k>0&&samples[k]!=0&&samples[k-1]!=0)adjacent++;
            }
            assertEquals(p,occupied/(double)samples.length,.01,"occupied fraction at rate "+rate);
            assertEquals(p*p,adjacent/(double)(samples.length-1),.01,"adjacent-bin independence at rate "+rate);
            assertArrayEquals(samples,term.create(65536,centered),0,"seed reproducibility");
        }
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void editedShotNoiseRatesPropagateThroughComplexImageAndIntensity(int w,int h,boolean centered) {
        PoissonProcessFunctionTerm1D xProcess=process(1.4,.1,17),yProcess=process(-.8,.5,29);
        ConstantFunctionTerm1D one=(ConstantFunctionTerm1D)new ConstantFunctionTerm1D().getDefaultInstance();
        RealImagFunction2D source=new RealImagFunction2D(
                part(new XYFunctionTerm2D(new ParameterBlock().addSource(xProcess).addSource(one))),
                part(new XYFunctionTerm2D(new ParameterBlock().addSource(one).addSource(yProcess))),"shot noise",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double rate:new double[]{.1,.5,2}) {
            xProcess.setWidth(rate);source.setPartA(source.getPartA());
            double[] xs=xProcess.create(w,centered),ys=yProcess.create(h,centered);
            double[] r=new double[w*h],i=new double[w*h],power=new double[w*h];
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                int k=row*w+col;r[k]=xs[col];i[k]=ys[h-1-row];power[k]=r[k]*r[k]+i[k]*i[k];
            }
            assertImage(source,r,i,w,h,centered);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
            assertImage(source.clone(),r,i,w,h,centered);
        }
    }

    @ParameterizedTest
    @CsvSource({"256,128,true", "256,128,false", "128,256,true", "128,256,false"})
    void uniformRandomPhaseCreatesUnitMagnitudeComplexImages(int w,int h,boolean centered) {
        RandomPhaseFunctionTerm2D term=(RandomPhaseFunctionTerm2D)new RandomPhaseFunctionTerm2D().getDefaultInstance();
        double[] phase=term.create(w,h,centered),amplitude=new double[w*h];Arrays.fill(amplitude,1.7);
        double cosine=0,sine=0,square=0;
        for(double angle:phase) {
            assertTrue(angle>=-Math.PI&&angle<=Math.PI);cosine+=Math.cos(angle);sine+=Math.sin(angle);square+=angle*angle;
        }
        assertEquals(0,cosine/phase.length,.02);assertEquals(0,sine/phase.length,.02);
        assertEquals(Math.PI*Math.PI/3,square/phase.length,.08);
        Function source=FunctionFactory.createMagPhaseFunction2D(amplitude,phase,centered,"random phase",w,h);
        double[] expected=new double[w*h];Arrays.fill(expected,1.7*1.7);
        assertImage(new SquaredMagnitudeOp(new ParameterBlock()).create(source),expected,new double[w*h],w,h,centered);
        assertArrayEquals(phase,term.create(w,h,centered),0);
    }

    private static PoissonProcessFunctionTerm1D process(double amplitude,double rate,int seed) {
        PoissonProcessFunctionTerm1D term=(PoissonProcessFunctionTerm1D)new PoissonProcessFunctionTerm1D().getDefaultInstance();
        term.setAmplitude(amplitude);term.setWidth(rate);term.setSeed(seed);return term;
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
