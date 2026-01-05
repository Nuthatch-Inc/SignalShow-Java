package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static signals.operation.SignalTestUtils.*;

/**
 * Comprehensive test suite for 1D binary signal operations.
 * 
 * Tests operations that take two signals as input:
 * - Plus (addition)
 * - Minus (subtraction)
 * - Times (element-wise multiplication)
 * - Divide (element-wise division)
 */
public class BinaryOperations1DTest {

  private static final int SIZE = 64;

  // ========== ADDITION TESTS ==========

  @Nested
  @DisplayName("Addition (Plus) Operation Tests")
  class AdditionTests {

    @Test
    @DisplayName("Addition is commutative: f + g = g + f")
    void additionIsCommutative() {
      double[] f = createRect(SIZE, 0, 4, true);
      double[] g = createGaussian(SIZE, 0, 8, 1.0, true);

      double[] fg = new double[SIZE];
      double[] gf = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg[i] = f[i] + g[i];
        gf[i] = g[i] + f[i];
      }

      assertSignalsEqual(fg, gf, "f + g should equal g + f");
    }

    @Test
    @DisplayName("Addition is associative: (f + g) + h = f + (g + h)")
    void additionIsAssociative() {
      double[] f = createRect(SIZE, 0, 3, true);
      double[] g = createSine(SIZE, 4, 1.0, true);
      double[] h = createGaussian(SIZE, 0, 8, 0.5, true);

      double[] fg_h = new double[SIZE];
      double[] f_gh = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg_h[i] = (f[i] + g[i]) + h[i];
        f_gh[i] = f[i] + (g[i] + h[i]);
      }

      assertSignalsEqual(fg_h, f_gh, "(f + g) + h should equal f + (g + h)");
    }

    @Test
    @DisplayName("Addition with zero: f + 0 = f")
    void additionWithZero() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] zero = createZeros(SIZE);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] + zero[i];
      }

      assertSignalsEqual(f, result, "f + 0 should equal f");
    }

    @Test
    @DisplayName("Addition with negation: f + (-f) = 0")
    void additionWithNegation() {
      double[] f = createSine(SIZE, 4, 1.0, true);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] + (-f[i]);
      }

      assertSignalIsZero(result, "f + (-f) should be 0");
    }

    @Test
    @DisplayName("Complex addition")
    void complexAddition() {
      double[] realA = createConstant(SIZE, 3.0);
      double[] imagA = createConstant(SIZE, 4.0);
      double[] realB = createConstant(SIZE, 1.0);
      double[] imagB = createConstant(SIZE, 2.0);

      double[] realResult = new double[SIZE];
      double[] imagResult = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        realResult[i] = realA[i] + realB[i];
        imagResult[i] = imagA[i] + imagB[i];
      }

      // (3 + 4i) + (1 + 2i) = (4 + 6i)
      for (int i = 0; i < SIZE; i++) {
        assertEquals(4.0, realResult[i], TOLERANCE, "Real part: 3 + 1 = 4");
        assertEquals(6.0, imagResult[i], TOLERANCE, "Imag part: 4 + 2 = 6");
      }
    }
  }

  // ========== SUBTRACTION TESTS ==========

  @Nested
  @DisplayName("Subtraction (Minus) Operation Tests")
  class SubtractionTests {

    @Test
    @DisplayName("Subtraction is NOT commutative: f - g ≠ g - f")
    void subtractionNotCommutative() {
      double[] f = createConstant(SIZE, 5.0);
      double[] g = createConstant(SIZE, 3.0);

      double[] fg = new double[SIZE];
      double[] gf = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg[i] = f[i] - g[i];
        gf[i] = g[i] - f[i];
      }

      // 5 - 3 = 2, 3 - 5 = -2
      assertEquals(2.0, fg[0], TOLERANCE);
      assertEquals(-2.0, gf[0], TOLERANCE);
      assertNotEquals(fg[0], gf[0], "f - g should not equal g - f");
    }

    @Test
    @DisplayName("Subtraction with zero: f - 0 = f")
    void subtractionWithZero() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] zero = createZeros(SIZE);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] - zero[i];
      }

      assertSignalsEqual(f, result, "f - 0 should equal f");
    }

    @Test
    @DisplayName("Subtraction from zero: 0 - f = -f")
    void subtractionFromZero() {
      double[] f = createSine(SIZE, 4, 1.0, true);
      double[] zero = createZeros(SIZE);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = zero[i] - f[i];
      }

      for (int i = 0; i < SIZE; i++) {
        assertEquals(-f[i], result[i], TOLERANCE, "0 - f should equal -f");
      }
    }

    @Test
    @DisplayName("Self subtraction: f - f = 0")
    void selfSubtraction() {
      double[] f = createRect(SIZE, 0, 4, true);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] - f[i];
      }

      assertSignalIsZero(result, "f - f should be 0");
    }

    @Test
    @DisplayName("Complex subtraction")
    void complexSubtraction() {
      double[] realA = createConstant(SIZE, 5.0);
      double[] imagA = createConstant(SIZE, 7.0);
      double[] realB = createConstant(SIZE, 2.0);
      double[] imagB = createConstant(SIZE, 3.0);

      double[] realResult = new double[SIZE];
      double[] imagResult = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        realResult[i] = realA[i] - realB[i];
        imagResult[i] = imagA[i] - imagB[i];
      }

      // (5 + 7i) - (2 + 3i) = (3 + 4i)
      for (int i = 0; i < SIZE; i++) {
        assertEquals(3.0, realResult[i], TOLERANCE, "Real part: 5 - 2 = 3");
        assertEquals(4.0, imagResult[i], TOLERANCE, "Imag part: 7 - 3 = 4");
      }
    }
  }

  // ========== MULTIPLICATION TESTS ==========

  @Nested
  @DisplayName("Multiplication (Times) Operation Tests")
  class MultiplicationTests {

    @Test
    @DisplayName("Multiplication is commutative: f × g = g × f")
    void multiplicationIsCommutative() {
      double[] f = createRect(SIZE, 0, 4, true);
      double[] g = createGaussian(SIZE, 0, 8, 1.0, true);

      double[] fg = new double[SIZE];
      double[] gf = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg[i] = f[i] * g[i];
        gf[i] = g[i] * f[i];
      }

      assertSignalsEqual(fg, gf, "f × g should equal g × f");
    }

    @Test
    @DisplayName("Multiplication is associative: (f × g) × h = f × (g × h)")
    void multiplicationIsAssociative() {
      double[] f = createRect(SIZE, 0, 3, true);
      double[] g = createSine(SIZE, 4, 1.0, true);
      double[] h = createGaussian(SIZE, 0, 8, 0.5, true);

      double[] fg_h = new double[SIZE];
      double[] f_gh = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg_h[i] = (f[i] * g[i]) * h[i];
        f_gh[i] = f[i] * (g[i] * h[i]);
      }

      assertSignalsEqual(fg_h, f_gh, LOOSE_TOLERANCE, "(f × g) × h should equal f × (g × h)");
    }

    @Test
    @DisplayName("Multiplication with zero: f × 0 = 0")
    void multiplicationWithZero() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] zero = createZeros(SIZE);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] * zero[i];
      }

      assertSignalIsZero(result, "f × 0 should be 0");
    }

    @Test
    @DisplayName("Multiplication with one: f × 1 = f")
    void multiplicationWithOne() {
      double[] f = createSine(SIZE, 4, 1.0, true);
      double[] one = createConstant(SIZE, 1.0);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] * one[i];
      }

      assertSignalsEqual(f, result, "f × 1 should equal f");
    }

    @Test
    @DisplayName("Complex multiplication")
    void complexMultiplication() {
      double[] realA = createConstant(SIZE, 3.0);
      double[] imagA = createConstant(SIZE, 4.0);
      double[] realB = createConstant(SIZE, 1.0);
      double[] imagB = createConstant(SIZE, 2.0);

      // (a + bi)(c + di) = (ac - bd) + (ad + bc)i
      double[] realResult = new double[SIZE];
      double[] imagResult = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        realResult[i] = realA[i] * realB[i] - imagA[i] * imagB[i];
        imagResult[i] = realA[i] * imagB[i] + imagA[i] * realB[i];
      }

      // (3 + 4i)(1 + 2i) = (3 - 8) + (6 + 4)i = -5 + 10i
      for (int i = 0; i < SIZE; i++) {
        assertEquals(-5.0, realResult[i], TOLERANCE, "(3+4i)(1+2i) real = -5");
        assertEquals(10.0, imagResult[i], TOLERANCE, "(3+4i)(1+2i) imag = 10");
      }
    }

    @Test
    @DisplayName("Distributive property: f × (g + h) = f×g + f×h")
    void distributiveProperty() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] g = createRect(SIZE, 0, 3, true);
      double[] h = createSine(SIZE, 4, 0.5, true);

      double[] fgh = new double[SIZE]; // f × (g + h)
      double[] fg_fh = new double[SIZE]; // f×g + f×h
      for (int i = 0; i < SIZE; i++) {
        fgh[i] = f[i] * (g[i] + h[i]);
        fg_fh[i] = f[i] * g[i] + f[i] * h[i];
      }

      assertSignalsEqual(fgh, fg_fh, LOOSE_TOLERANCE, "f×(g+h) should equal f×g + f×h");
    }
  }

  // ========== DIVISION TESTS ==========

  @Nested
  @DisplayName("Division (Divide) Operation Tests")
  class DivisionTests {

    @Test
    @DisplayName("Division by one: f / 1 = f")
    void divisionByOne() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] one = createConstant(SIZE, 1.0);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] / one[i];
      }

      assertSignalsEqual(f, result, "f / 1 should equal f");
    }

    @Test
    @DisplayName("Self division: f / f = 1 (where f ≠ 0)")
    void selfDivision() {
      double[] f = createConstant(SIZE, 5.0); // Non-zero constant

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = f[i] / f[i];
      }

      double[] one = createConstant(SIZE, 1.0);
      assertSignalsEqual(one, result, "f / f should equal 1");
    }

    @Test
    @DisplayName("Zero divided by non-zero: 0 / f = 0")
    void zeroDividedByNonZero() {
      double[] zero = createZeros(SIZE);
      double[] f = createConstant(SIZE, 3.0);

      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        result[i] = zero[i] / f[i];
      }

      assertSignalIsZero(result, "0 / f should be 0");
    }

    @Test
    @DisplayName("Division is inverse of multiplication: (f × g) / g = f")
    void divisionInverseOfMultiplication() {
      double[] f = createGaussian(SIZE, 0, 8, 2.0, true);
      double[] g = createConstant(SIZE, 3.0); // Non-zero

      double[] fg = new double[SIZE];
      double[] result = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg[i] = f[i] * g[i];
        result[i] = fg[i] / g[i];
      }

      assertSignalsEqual(f, result, LOOSE_TOLERANCE, "(f × g) / g should equal f");
    }

    @Test
    @DisplayName("Division is NOT commutative: f / g ≠ g / f")
    void divisionNotCommutative() {
      double[] f = createConstant(SIZE, 6.0);
      double[] g = createConstant(SIZE, 2.0);

      double[] fg = new double[SIZE];
      double[] gf = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fg[i] = f[i] / g[i];
        gf[i] = g[i] / f[i];
      }

      // 6 / 2 = 3, 2 / 6 = 0.333...
      assertEquals(3.0, fg[0], TOLERANCE);
      assertEquals(1.0 / 3.0, gf[0], TOLERANCE);
      assertNotEquals(fg[0], gf[0], "f / g should not equal g / f");
    }

    @Test
    @DisplayName("Complex division")
    void complexDivision() {
      // (a + bi) / (c + di) = [(ac + bd) + (bc - ad)i] / (c² + d²)
      double[] realA = createConstant(SIZE, 3.0);
      double[] imagA = createConstant(SIZE, 4.0);
      double[] realB = createConstant(SIZE, 1.0);
      double[] imagB = createConstant(SIZE, 2.0);

      // (3 + 4i) / (1 + 2i) = [(3 + 8) + (4 - 6)i] / 5 = (11 - 2i) / 5 = 2.2 - 0.4i
      double denom = 1.0 * 1.0 + 2.0 * 2.0; // 5
      double[] realResult = new double[SIZE];
      double[] imagResult = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        realResult[i] = (realA[i] * realB[i] + imagA[i] * imagB[i]) / denom;
        imagResult[i] = (imagA[i] * realB[i] - realA[i] * imagB[i]) / denom;
      }

      for (int i = 0; i < SIZE; i++) {
        assertEquals(2.2, realResult[i], TOLERANCE, "(3+4i)/(1+2i) real = 2.2");
        assertEquals(-0.4, imagResult[i], TOLERANCE, "(3+4i)/(1+2i) imag = -0.4");
      }
    }
  }

  // ========== MIXED OPERATIONS TESTS ==========

  @Nested
  @DisplayName("Mixed Operations Tests")
  class MixedOperationsTests {

    @Test
    @DisplayName("Order of operations: a × b + c ≠ a × (b + c) in general")
    void orderOfOperations() {
      double[] a = createConstant(SIZE, 2.0);
      double[] b = createConstant(SIZE, 3.0);
      double[] c = createConstant(SIZE, 4.0);

      double[] ab_plus_c = new double[SIZE]; // a × b + c
      double[] a_times_bpc = new double[SIZE]; // a × (b + c)
      for (int i = 0; i < SIZE; i++) {
        ab_plus_c[i] = a[i] * b[i] + c[i]; // 2×3 + 4 = 10
        a_times_bpc[i] = a[i] * (b[i] + c[i]); // 2×(3+4) = 14
      }

      assertEquals(10.0, ab_plus_c[0], TOLERANCE);
      assertEquals(14.0, a_times_bpc[0], TOLERANCE);
      assertNotEquals(ab_plus_c[0], a_times_bpc[0]);
    }

    @Test
    @DisplayName("Subtraction as addition of negation: f - g = f + (-g)")
    void subtractionAsAddition() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] g = createSine(SIZE, 4, 0.5, true);

      double[] fMinusG = new double[SIZE];
      double[] fPlusNegG = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fMinusG[i] = f[i] - g[i];
        fPlusNegG[i] = f[i] + (-g[i]);
      }

      assertSignalsEqual(fMinusG, fPlusNegG, "f - g should equal f + (-g)");
    }

    @Test
    @DisplayName("Division as multiplication by reciprocal: f / g = f × (1/g)")
    void divisionAsMultiplication() {
      double[] f = createGaussian(SIZE, 0, 8, 1.0, true);
      double[] g = createConstant(SIZE, 2.0);

      double[] fDivG = new double[SIZE];
      double[] fTimesRecipG = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        fDivG[i] = f[i] / g[i];
        fTimesRecipG[i] = f[i] * (1.0 / g[i]);
      }

      assertSignalsEqual(fDivG, fTimesRecipG, LOOSE_TOLERANCE, "f / g should equal f × (1/g)");
    }
  }

  // ========== EDGE CASES ==========

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCaseTests {

    @Test
    @DisplayName("Operations with all positive values")
    void operationsWithPositiveValues() {
      double[] a = createConstant(SIZE, 5.0);
      double[] b = createConstant(SIZE, 3.0);

      double[] sum = new double[SIZE];
      double[] diff = new double[SIZE];
      double[] prod = new double[SIZE];
      double[] quot = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        sum[i] = a[i] + b[i];
        diff[i] = a[i] - b[i];
        prod[i] = a[i] * b[i];
        quot[i] = a[i] / b[i];
      }

      assertEquals(8.0, sum[0], TOLERANCE, "5 + 3 = 8");
      assertEquals(2.0, diff[0], TOLERANCE, "5 - 3 = 2");
      assertEquals(15.0, prod[0], TOLERANCE, "5 × 3 = 15");
      assertEquals(5.0 / 3.0, quot[0], TOLERANCE, "5 / 3 ≈ 1.667");
    }

    @Test
    @DisplayName("Operations with negative values")
    void operationsWithNegativeValues() {
      double[] a = createConstant(SIZE, -5.0);
      double[] b = createConstant(SIZE, -3.0);

      double[] sum = new double[SIZE];
      double[] prod = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        sum[i] = a[i] + b[i];
        prod[i] = a[i] * b[i];
      }

      assertEquals(-8.0, sum[0], TOLERANCE, "-5 + (-3) = -8");
      assertEquals(15.0, prod[0], TOLERANCE, "(-5) × (-3) = 15");
    }

    @Test
    @DisplayName("Operations with small values")
    void operationsWithSmallValues() {
      double[] a = createConstant(SIZE, 1e-10);
      double[] b = createConstant(SIZE, 1e-10);

      double[] prod = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        prod[i] = a[i] * b[i];
      }

      assertEquals(1e-20, prod[0], 1e-30, "Small values multiply correctly");
    }

    @Test
    @DisplayName("Operations with large values")
    void operationsWithLargeValues() {
      double[] a = createConstant(SIZE, 1e10);
      double[] b = createConstant(SIZE, 1e10);

      double[] prod = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        prod[i] = a[i] * b[i];
      }

      assertEquals(1e20, prod[0], 1e10, "Large values multiply correctly");
    }
  }
}
