package signals.core;

import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import signals.functionterm.ImageFunctionTerm2D;
import signals.functionterm.NoiseFunctionTerm2D;
import signals.functionterm.SeparableFunctionTerm2D;

/** Copies one editable definition, preserving sharing only inside its new document. */
final class FunctionCopy {
    private final Map<DataGenerator, DataGenerator> generators = new IdentityHashMap<>();

    FunctionPart1D copy(FunctionPart1D part) {
        ArrayList<FunctionTerm1D> terms = new ArrayList<>();
        for (FunctionTerm1D term : part.getTermsList()) terms.add((FunctionTerm1D) copyGenerator(term));
        return new FunctionPart1D(terms, new CombineTermsRule(part.getCombineTermsRule().getInfix().clone()));
    }

    FunctionPart2D copy(FunctionPart2D part) {
        ArrayList<FunctionTerm2D> terms = new ArrayList<>();
        for (FunctionTerm2D term : part.getTermsList()) terms.add((FunctionTerm2D) copyGenerator(term));
        return new FunctionPart2D(terms, new CombineTermsRule(part.getCombineTermsRule().getInfix().clone()));
    }

    private DataGenerator copyGenerator(DataGenerator original) {
        DataGenerator existing = generators.get(original);
        if (existing != null) return existing;
        ParameterBlock parameters = new ParameterBlock();
        for (Object value : original.getParamBlock().getParameters()) parameters.add(copyValue(value));
        if (original instanceof SeparableFunctionTerm2D) {
            // Editors replace these children without updating the original block's sources.
            SeparableFunctionTerm2D term = (SeparableFunctionTerm2D) original;
            parameters.addSource(copyGenerator(term.getFunctionTerm1DA()));
            parameters.addSource(copyGenerator(term.getFunctionTerm1DB()));
        } else {
            for (Object value : original.getParamBlock().getSources()) parameters.addSource(copyValue(value));
        }
        DataGenerator result = original.getInstance(parameters);
        if (result == null) throw new IllegalStateException("Cannot copy function term " + original.getClass().getName());
        generators.put(original, result);
        if (original instanceof NoiseFunctionTerm2D) {
            // Noise wrappers own their effective settings in their 1D generator.
            ((NoiseFunctionTerm2D) result).getFunctionTerm1D().setParamBlock(parameters);
        }
        if (original instanceof ImageFunctionTerm2D) {
            ((ImageFunctionTerm2D) result).setMode(((ImageFunctionTerm2D) original).getMode());
        }
        return result;
    }

    private Object copyValue(Object value) {
        if (value instanceof DataGenerator) return copyGenerator((DataGenerator) value);
        if (value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);
            Object result = Array.newInstance(value.getClass().getComponentType(), length);
            if (value.getClass().getComponentType().isPrimitive()) {
                System.arraycopy(value, 0, result, 0, length);
            } else {
                for (int i = 0; i < length; i++) Array.set(result, i, copyValue(Array.get(value, i)));
            }
            return result;
        }
        return value; // Scalar parameters and resource names are immutable.
    }
}
