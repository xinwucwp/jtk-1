package edu.mines.jtk.opt.test;

import junit.framework.*;
import edu.mines.jtk.opt.ScalarSolver;

import static edu.mines.jtk.opt.ScalarSolver.Function;

/** Wrap edu.mines.jtk.opt.ScalarSolver for junit testing.
    (junit.jar must be in CLASSPATH)
*/
public class ScalarSolverTest extends TestCase {


  public static void testLinearObjFunc() throws Exception {
    // test linear objective function
    final double answer = 1./3.;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return Math.abs(scalar - answer);
	}
      });
    // s_LOG.fine = true;
    double xmin = solver.solve(0., 1., 0.001, 0.001, 20, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("1. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 14):("calls[0] == 14 != "+calls[0]);
  }

  public static void testNonUnitScalarRange() throws Exception {
    // test non-unit scalar range
    final double answer = 1./3.;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return Math.abs(scalar - answer);
	}
      });
    double xmin = solver.solve(-1., 2., 0.001, 0.001, 20, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("2. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 15):("calls[0] == 15 != "+calls[0]);
  }

  
  public static void testRightHandSide() throws Exception {
    // test right hand side
    final double answer = 0.03;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return Math.abs(scalar - answer);
	}
      });
    double xmin = solver.solve(0., 1., 0.001, 0.001, 20, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("3. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 16):("calls[0] == 16 != "+calls[0]);
  }

  public static void testLeftHandSide2() throws Exception {
    final double answer = 0.98;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return Math.abs(scalar - answer);
	}
      });
    double xmin = solver.solve(0., 1., 0.001, 0.001, 20, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("4. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 12):("calls[0] == 12 != "+calls[0]);
  }

  public static void testParabola() throws Exception {
    final double answer = 1./3.;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return (scalar - answer)*(scalar - answer);
	}
      });
    double xmin = solver.solve(0., 1., 0.001, 0.001, 7, null); // fewest iterations
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("5. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 6):( "Number == 6 != "+calls[0]);
  }

  public static void testPositiveCurvature() throws Exception {
    final double answer = 1./3.;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  return Math.sqrt(Math.abs(scalar - answer));
	}
      });
    double xmin = solver.solve(0., 1., 0.001, 0.001, 20, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("6. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 16):( "Number == 16 != "+calls[0]);
  }

  public static void testStepFunction() throws Exception {
    final double answer = 1./3.;
    final int[] calls = new int[]{0};
    ScalarSolver solver = new ScalarSolver(new Function() {
	public double function(double scalar) {
	  ++calls[0];
	  if (scalar < answer) return 3.;
	  return scalar - answer;
	}
      });
    double xmin = solver.solve(0., 1., 0.001, 0.001, 50, null);
    assert (xmin > answer - 0.001 ):(  "xmin > answer - 0.001");
    assert (xmin > answer*(1. - 0.001) ):(  "xmin > answer*(1. - 0.001)");
    assert (xmin < answer + 0.001 ):(  "xmin < answer - 0.001");
    assert (xmin < answer*(1. + 0.001) ):(  "xmin < answer*(1. + 0.001)");
    // LOG.fine("6. result="+answer+"="+xmin+" calls="+calls[0]);
    assert (calls[0] == 29):( "Number == 29 != "+calls[0]);
  }

  /* Initialize objects used by all test methods */
  @Override protected void setUp() throws Exception { super.setUp();}

  /* Destruction of stuff used by all tests: rarely necessary */
  @Override protected void tearDown() throws Exception { super.tearDown();}

  // NO NEED TO CHANGE THE FOLLOWING

  /** Standard constructor calls TestCase(name) constructor */
  public ScalarSolverTest(String name) {super (name);}

  /** This automatically generates a suite of all "test" methods */
  public static junit.framework.Test suite() {
    try {assert false; throw new IllegalStateException("need -ea");}
    catch (AssertionError e) {}
    return new TestSuite(ScalarSolverTest.class);
  }

  /** Run all tests with text gui if this class main is invoked */
  public static void main (String[] args) {
    junit.textui.TestRunner.run (suite());
  }
}
