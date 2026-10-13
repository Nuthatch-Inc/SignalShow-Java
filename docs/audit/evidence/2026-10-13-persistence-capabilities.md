# Persistence capabilities — October 13, 2026

The existing save/load actions preserve sampled functions, not editable projects. This closes the capability-scoping question; it does not add a project format or certify every storage failure case.

| Existing action | Preserved information | What reopening cannot restore |
| --- | --- | --- |
| Save Complex Data / Load Complex Data | 1D/2D dimensions, origin flag, full display name/descriptor, real samples and imaginary samples | Generator choices/parameters, expression tokens, operator settings, linked sources, document collection or open views |
| Export Function as Data or Image | Selected component samples as text/CSV, or rendered image pixels in the chosen format | Complex graph, analytic definition and editable operation chain; an image export is not a lossless complex-data project |
| Save Screen Shot | Displayed image | Signal definition and numerical state |
| Configuration on application exit | Properties such as display/export defaults and window size | Open documents and linked computations |

## Source trace

`GUI` registers four File-menu actions: component export, screenshot, complex save and complex load. `SaveComplexAction` extracts a Function or the evaluated result of an OperatorSystem, and `SaveComplexDialog` calls `ComplexIO.write`. `ComplexIO.write1D/write2D` serialize geometry/origin/name followed by the real and imaginary arrays. There are no generator or operation records in that format.

`LoadComplexAction` calls `ComplexIO.read`. Its1D/2D readers construct a new Function through FunctionFactory's sampled-data overloads. Those overloads create DataFunctionTerm1D/2D components and preserve the name as a descriptor/equation string; that string is not parsed back into an expression or used to reconnect source documents. Reopened data can still be used in new operations and edited with the sampled-data controls.

`SaveFunctionAction` likewise evaluates operation results before opening component export. SaveFunction1DDialog writes selected samples; SaveFunction2DDialog calls the text/CSV/image writers. Config saves a Java Properties file, not a document graph. No object/XML serialization writer appears in the inspected application source.

## Evidence and practical scope

Current source inspected at1e74162. Reuse the already completed both-origin1D/2D production round trips and native save/reopen/use/resave evidence in the linked file scenarios. The full578-test package includes ComplexDataWorkflowTest; no code changed and no redundant test run was needed for this documentation-only reconciliation. The new native rotation and translation exports further demonstrate that filenames/equation descriptions remain metadata alongside sampled arrays.

For now, use Save Complex Data to retain both channels for later operations, and separately retain parameter/workflow notes when an editable recipe matters. A future editable-project format must preserve stable source references, generator and operation parameters, dimensions/origins, versioning and graph reconstruction. Design that after ordinary usage stabilization; this audit does not silently treat missing Chirplet parity as a regression.
