# FEATURE REQUESTS

This file documents feature requests and enhancements for the SignalShow-Java repository. Use the entries below to collect feature requests with detailed specifications and priority.

ID format: FEAT-### (e.g. FEAT-001)

---

## Template

- ID: FEAT-XXX
- Title: Short descriptive title
- Status: open / in-progress / completed / deferred
- Requested by: <name>
- Date: YYYY-MM-DD
- Priority: low / medium / high
- Description: Detailed description
- Specification:
  - Detail 1
  - Detail 2
- Use cases:
- Implementation notes:
- Related issues:

---

## Active Feature Requests

- ID: FEAT-001
- Title: Change default 1-D graph format from area to line
- Status: completed
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: low
- Description: Change the default 1-D graph display format from "area" to "line" for better visualization.
- Specification:
  - Update default 1-D graph rendering to use line style instead of area fill
- Use cases: Clearer visualization of 1-D functions
- Implementation notes: Completed
- Related issues: N/A

---

- ID: FEAT-002
- Title: Create default configuration file for user preferences
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: medium
- Description: Implement a configuration file system that saves user preferences (such as default graph formats) and loads them on startup.
- Specification:
  - Create config file to store user preferences
  - Save preferences on exit
  - Load preferences on startup
  - Include settings like:
    - Default 1-D graph format
    - Default 2-D display settings
    - Other user-configurable defaults
- Use cases: Users can customize the application to their preferred defaults without changing code
- Implementation notes: Should use standard Java properties or JSON format
- Related issues: FEAT-001

---

- ID: FEAT-003
- Title: Implement Radon transform with configurable angles and filters
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: high
- Description: Add Radon transform capability with selectable number of angles and multiple high-boost filter options for data recovery.
- Specification:
  - Forward Radon transform:
    - Sum rows and columns to get initial projections
    - Rotate image by configurable angle increments
    - Use interpolation during rotation
    - Constrain image to central circle to avoid edge artifacts
    - Generate sinogram display
  - Inverse Radon transform with filter options:
    - Rho filter (basic)
    - Shepp-Logan filter (rolled-off rho filter to reduce noise amplification)
    - Other filter variations as needed
  - Configurable parameters:
    - Number of projection angles
    - Filter type selection
    - Interpolation method
- Use cases:
  - Teaching CT and MRI reconstruction principles
  - Demonstrating tomographic imaging
- Implementation notes:
  - Required components already exist (row/column summation, rotation, filtering, display)
  - Start with forward transform displaying sinogram
  - Add inverse transform and filters in second phase
  - Reference: Roger Easton's book on Fourier Methods in Imaging
- Related issues: N/A

---

- ID: FEAT-004
- Title: Implement Wigner Distribution Function (WDF) for 1-D signals
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: medium
- Description: Add WDF capability to create 2-D time-frequency representations from 1-D signals.
- Specification:
  - Calculate 1-D Fourier transform of f[u + x/2] f^\*[u - x/2]
  - Transform x -> ξ (xi)
  - Display output as 2-D function of u and ξ
  - Provides hybrid space-frequency representation
- Use cases:
  - Time-frequency analysis of 1-D signals
  - Visualizing signal characteristics in joint time-frequency domain
  - Educational demonstrations
- Implementation notes:
  - Long-term addition
  - Doubles dimensionality (1-D input -> 2-D output)
  - Useful only for 1-D functions
  - Should implement one at a time (WDF first, then ambiguity function)
  - Roger Easton has reference materials/book chapters available
- Related issues: FEAT-005, FEAT-006

---

- ID: FEAT-005
- Title: Implement Ambiguity Function for 1-D signals
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: medium
- Description: Add ambiguity function capability as a complement to WDF, creating 2-D renderings from 1-D functions.
- Specification:
  - Calculate Fourier transform of f[x + u/2] f^\*[x - u/2]
  - Transform x -> ξ (xi)
  - Display result as 2-D function of u and ξ
  - Note: x and u are in different positions compared to WDF
- Use cases:
  - Radar signal processing
  - Complementary analysis to WDF
  - Educational demonstrations
- Implementation notes:
  - Long-term addition
  - Related to WDF but with x and u exchanged
  - Implement after WDF is complete
  - Graduate student interested in this functionality
  - Roger Easton has reference materials from dissertation work (1980s)
- Related issues: FEAT-004, FEAT-006

---

- ID: FEAT-006
- Title: Implement Fractional Fourier Transform
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: low
- Description: Add fractional Fourier transform capability for interpolating between space and frequency domains.
- Specification:
  - Display space and frequency domains simultaneously along orthogonal axes
  - Calculate fractional Fourier transform at different angles between axes
  - Doubles dimensionality (1-D input -> 2-D output)
- Use cases:
  - Advanced signal analysis
  - Research and educational applications
  - Demonstrating space-frequency relationships
- Implementation notes:
  - Long-term addition
  - Lower priority than WDF/ambiguity function
  - Roger Easton still researching this himself
  - Associated with work by Ingrid Daubechies
- Related issues: FEAT-004, FEAT-005

---

- ID: FEAT-007
- Title: Add CSV export format for 2D data
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: medium
- Description: Add CSV (Comma-Separated Values) export format option for 2D data alongside existing TIFF and TXT formats.
- Specification:
  - Add CSV export option to 2D data export menu
  - Format should include proper headers
  - Handle complex data appropriately (separate columns for real/imaginary or magnitude/phase)
  - Ensure real and imaginary parts are normalized to same scale
- Use cases:
  - Easier data analysis in spreadsheet applications
  - Better integration with data analysis tools
  - Standard format for numerical data exchange
- Implementation notes: Note that BUG-005 has been resolved - real and imaginary parts are now normalized to same scale when exported together
- Related issues: BUG-005 (resolved)

---

- ID: FEAT-008
- Title: Rename binary operations for clarity
- Status: open
- Requested by: Roger Easton Jr
- Date: 2024-12-03
- Priority: low
- Description: Rename operations in the "f box g" menu for better clarity and mathematical accuracy.
- Specification:
  - Rename "plus" to "sum"
  - Rename "minus" to "difference"
  - Rename "times" to "multiply"
- Use cases:
  - Clearer mathematical terminology
  - Better alignment with standard mathematical language
- Implementation notes: Simple UI text changes
- Related issues: N/A

---

## Reference Materials Needed

For FEAT-004, FEAT-005, FEAT-006:

- Roger Easton's book: "Fourier Methods in Imaging" (PDF to be provided)
- Wigner Distribution Function references
- Ambiguity Function references from Roger Easton's dissertation (1980s)
- Fractional Fourier Transform references

## Notes from Planning Meeting (2024-12-03)

Meeting participants: Roger Easton Jr (advisor), Juliet Fiss (developer)

Key priorities discussed:

1. Radon transform is high priority - useful for teaching CT/MRI
2. WDF/Ambiguity function are medium priority - longer-term additions
3. Start with simpler implementations before adding all options
4. For Radon transform: start with forward transform and sinogram display, add inverse and filters later
5. For WDF: implement one function at a time, get feedback before implementing variations

Connection to other work:

- Project relates to manuscript imaging work (palimpsests)
- Potential application to multispectral imaging and AI recovery
- Educational use in imaging science courses
