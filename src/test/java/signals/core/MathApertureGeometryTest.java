package signals.core;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.*;
import signals.operation.SquaredMagnitudeOp;

class MathApertureGeometryTest extends MathAuditSupport {
    static Stream<Arguments> cases() {
        return Stream.of("Rectangle", "E", "FilledMultiarm", "CassegrainMultiarm").flatMap(kind -> Stream.of(
                Arguments.of(kind,64,32,true), Arguments.of(kind,64,32,false),
                Arguments.of(kind,32,64,true), Arguments.of(kind,32,64,false)));
    }

    @ParameterizedTest
    @MethodSource("cases")
    void apertureCoordinatesAndEditsSurviveComplexIntensityChain(String kind,int w,int h,boolean centered) {
        AnalyticFunctionTerm2D realTerm=aperture(kind,1.4,4),imagTerm=aperture(kind,-.8,3);
        RealImagFunction2D source=new RealImagFunction2D(part(realTerm),part(imagTerm),"aperture geometry",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        double baseX=centered?0:w/2.0,baseY=centered?0:h/2.0;
        for(double shift:new double[]{0,.25}) {
            double cx=baseX+3+shift,cy=baseY-2-shift;
            realTerm.setCenterX(cx); realTerm.setCenterY(cy);
            imagTerm.setCenterX(cx-1.5); imagTerm.setCenterY(cy+2.5);
            source.setPartA(source.getPartA()); source.setPartB(source.getPartB());
            double[] real=new double[w*h],imag=new double[w*h],power=new double[w*h];
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                int k=row*w+col;
                real[k]=contains(kind,x(col,w,centered)-cx,y(row,h,centered)-cy,4)?1.4:0;
                imag[k]=contains(kind,x(col,w,centered)-cx+1.5,y(row,h,centered)-cy-2.5,3)?-.8:0;
                power[k]=real[k]*real[k]+imag[k]*imag[k];
            }
            assertImage(source,real,imag,w,h,centered);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
        }
    }

    private static boolean contains(String kind,double x,double y,int arms) {
        // Java's existing analytic aperture controls specify half-extents.
        if(kind.equals("Rectangle")) return Math.abs(x)<=8 && Math.abs(y)<=5;
        if(kind.equals("E")) {
            boolean outer=Math.abs(x)<=8 && Math.abs(y)<=5;
            boolean notch=x>=-1.6 && x<=8 && Math.abs(y)<=3;
            boolean middle=Math.abs(x)<=4 && Math.abs(y)<=1;
            return outer && (!notch || middle);
        }
        // Independent explicit cardinal/equilateral geometry, with two holes per arm.
        double[][] directions=arms==4 ? new double[][]{{0,-1},{-1,0},{0,1},{1,0}}
                : new double[][]{{0,-1},{-Math.sqrt(3)/2,.5},{Math.sqrt(3)/2,.5}};
        for(double[] direction:directions) for(int distance:new int[]{7,14}) {
            double radius=Math.hypot(x-distance*direction[0],y-distance*direction[1]);
            if(radius<=2.4 && (kind.equals("FilledMultiarm") || radius>=.9)) return true;
        }
        return false;
    }

    private static AnalyticFunctionTerm2D aperture(String kind,double amplitude,int arms) {
        AnalyticFunctionTerm2D prototype=switch(kind) {
            case "Rectangle" -> new RectApertureFunctionTerm2D();
            case "E" -> new EApertureFunctionTerm2D();
            case "FilledMultiarm" -> new MultiArmFunctionTerm2D();
            case "CassegrainMultiarm" -> new CassegrainMultiArmFunctionTerm2D();
            default -> throw new AssertionError(kind);
        };
        AnalyticFunctionTerm2D term=(AnalyticFunctionTerm2D)prototype.getDefaultInstance();
        term.setAmplitude(amplitude);
        if(kind.endsWith("Multiarm")) {
            term.setWidth(2.4);
            int spacing=kind.equals("FilledMultiarm")?4:5;
            if(spacing==5) term.setParameter(.9,4);
            term.setParameter(7.0,spacing); term.setParameter(arms,spacing+1); term.setParameter(2,spacing+2);
        } else { term.setWidth(8); term.setHeight(5); }
        return term;
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
