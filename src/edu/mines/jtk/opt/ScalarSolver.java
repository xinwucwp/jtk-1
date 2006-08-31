package edu.mines.jtk.opt;

import edu.mines.jtk.opt.Almost;
import edu.mines.jtk.opt.LogMonitor;
import edu.mines.jtk.opt.Monitor;
import edu.mines.jtk.opt.IndexSorter;
import java.util.logging.*;

/** Search a single variable for a value that minimizes a function 
    @author W.S. Harlan, Landmark Graphics
 */
public class ScalarSolver {
  @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger("edu.mines.jtk.opt");
  private static final double GOLD = 0.5*(Math.sqrt(5.) - 1.); // 0.618034

  private Function _function = null;

  /** Implement a function of one variable to be minimized */
  public interface Function {
    /** Return a single value as a function of the argument
        @param scalar Argument to be optimized within a known range.
        @return value to be minimized
    */
    public double function(double scalar);
  }

  /** Constructor
      @param function Objective function to be minimized.
   */
  public ScalarSolver(Function function) {
    _function = function;
  }

  /** Minimize a function of scalar and return the optimum value.
      @param scalarMin The minimum value allowed for the argument scalar.
      @param scalarMax The maximum value allowed for the argument scalar.
      @param okError The unknown error in scalar should be less than this
      fraction of the range: dscalar/(scalarMax-scalarMin) <= okError,
      where dscalar is the error bound on the returned value of scalar.
      @param okFraction The error in scalar should be less than
      this fraction of the minimum possible range for scalar:
      dscalar <= okFraction*(scalar-scalarMin),
      where dscalar is the error bound on the returned value of scalar.
      @param numberIterations The maximum number of iterations if greater
      than 6.  The optimization performs at least 6 iterations --
      the minimum necessary for a genuinely parabolic function.
      I recommend at least 20.
      @param monitor For reporting progress.  Ignored if null.
      @return The optimum value minimizing the function.
   */
  public double solve(double scalarMin, double scalarMax,
                      double okError, double okFraction,
                      int numberIterations, Monitor monitor)
  {
    if (monitor == null) monitor = new LogMonitor(null, null);
    monitor.report(0.);

    int iter =0, nter = numberIterations;
    if (nter < 6) nter = 6;
    double[] xs  = {0., 0.25, 0.75, 1.}; // Assume bias toward endpoints
    double[] fs  = new double[4];
    for (int i=0; i<fs.length; ++i) {
      fs[i] = function(xs[i], scalarMin, scalarMax);
    }
    iter = 4;

    double xmin = Float.MAX_VALUE;
    double error = 1.;
    double previousError = 1.;
    double previousPreviousError = 1.;
    double fraction = 1.;
    while (true) {
      monitor.report(((double)iter)/nter);
      // Find new minimum point
      int imin = sort(xs, fs);
      xmin = xs[imin];

      // update error estimate
      previousPreviousError = previousError;
      previousError = error;
      if (imin == 0) {
        error = xs[1] - xs[0];
      } else if (imin == 3) {
        error = xs[3] - xs[2];
      } else if (imin == 1 || imin == 2) {
        error = xs[imin+1] - xs[imin-1];
      } else {
        assert (false):("impossible imin="+imin);
      }
      fraction = Almost.FLOAT.divide(error, xmin, 0.);
      /*
      LOG.fine ("iter="+iter);
      LOG.fine ("xs[0]="+xs[0]+" xs[1]="+xs[1]+" xs[2]="+xs[2]+" xs[3]="+xs[3]);
      LOG.fine ("fs[0]="+fs[0]+" fs[1]="+fs[1]+" fs[2]="+fs[2]+" fs[3]="+fs[3]);
      LOG.fine ("imin="+imin+" xmin="+xmin+" fraction="+fraction);
      LOG.fine (" error="+error+ " previousError="+previousError
             +" previousPreviousError="+previousPreviousError);
      */

      // Is it time to stop and use the current answer?
      if (iter >= nter || (error < okError && fraction < okFraction)) {
        // LOG.fine("DONE");
        break;
      }

      // Find next point to evaluate function
      double xnew = Float.MAX_VALUE;
      if (imin == 0) { // Move fast toward left endpoint
        assert (xs[imin] == 0. ):(  "Left endpoint should be zero");
        xnew = xs[1] * 0.1;
        // LOG.fine("Move rapidly to left");
      } else if (imin == 3) { // Move fast toward right endpoint
        assert (xs[imin] == 1. ):(  "Right endpoint should be one");
        xnew = 1. - 0.1 * (1.-xs[2]);
        // LOG.fine("Move rapidly to right");
      } else if (imin == 1 || imin == 2) { // Have bounded minimum
        boolean goodConvergence = false;
        if (error < previousPreviousError * 0.501) { // converging linearly?
          // Try parabolic method for hyperlinear convergence.
          // LOG.fine("good convergence so far");
          try {
            xnew = minParabola(xs[imin-1], fs[imin-1],
                               xs[imin], fs[imin],
                               xs[imin+1], fs[imin+1]);
            // LOG.fine("Good parabola ");
            goodConvergence = true;
          } catch (BadParabolaException e) {
            // LOG.fine("Bad parabola: "+e.getLocalizedMessage());
            goodConvergence = false;
          }
        }
        if ( !goodConvergence ) {
          /*
          LOG.fine("Use GOLDEN section: xs[imin-1]="+xs[imin-1]
                +" xs[imin-1]="+xs[imin-1]
                +" xs[imin]="+xs[imin]
                +" xs[imin+1]="+xs[imin+1]);
          */
          // Converging badly.  Resort to golden section.
          if (xs[imin] - xs[imin-1] >= xs[imin+1] - xs[imin]) {
            // Left side is larger.
            // LOG.fine("left side is larger");
            xnew = xs[imin-1] + GOLD*(xs[imin] - xs[imin-1]);
          } else { // Right side is larger
            // LOG.fine("right side is larger");
            xnew = xs[imin+1] - GOLD*(xs[imin+1] - xs[imin]);
          }
        }
      } else {assert (false):( "Impossible imin="+imin);}
      // LOG.fine("xnew="+xnew);
      assert (xnew!=Float.MAX_VALUE ):(  "bad xnew");

      // evaluate function at new point
      double fnew = Float.MAX_VALUE;
      // don't repeat a calculation
      for (int i=0; i< xs.length; ++i) {
        if (Almost.FLOAT.equal(xnew, xs[i])) {
          // LOG.fine("Recycle xs["+i+"]="+xs[i]+" fs["+i+"]="+xs[i]);
          fnew = fs[i];
        }
      }
      if (fnew == Float.MAX_VALUE) { // call expensive function
        fnew = function(xnew, scalarMin, scalarMax);
      }
      // LOG.fine("fnew="+fnew);

      // save on top of discarded value before resorting
      if (imin <2) {
        xs[3] = xnew;
        fs[3] = fnew;
        // LOG.fine("updated xs[3]="+xs[3]+ " fs[3]="+fs[3]);
      } else {
        xs[0] = xnew;
        fs[0] = fnew;
        // LOG.fine("updated xs[0]="+xs[0]+ " fs[0]="+fs[0]);
      }

      // completed an iteration
      ++iter;
    }

    assert (xmin >=0. && xmin <= 1.):( "Impossible xmin="+xmin);

    double result = scalarMin + xmin*(scalarMax - scalarMin);
    // LOG.fine("result="+result);
    monitor.report(1.);
    return result;
  }

  private double[] _doubleTemp = new double[4]; // reused after profiling

  /** Reorganize samples
      @param xs Sorted by increasing values
      @param fs Sorted the same way as xs
      @return sample for which fs is minimized */
  private int sort(double[] xs, double[] fs) {
    assert xs.length == 4;
    int[] sortedX = (new IndexSorter(xs)).getSortedIndices();
    System.arraycopy(xs, 0, _doubleTemp, 0, 4);
    for (int i=0; i<xs.length; ++i) {
      xs[i] = _doubleTemp[sortedX[i]];
    }
    System.arraycopy(fs, 0, _doubleTemp, 0, 4);
    for (int i=0; i<xs.length; ++i) {
      fs[i] = _doubleTemp[sortedX[i]];
    }
    int imin = 0;
    for (int i=1; i<fs.length; ++i) {
      if (fs[i] < fs[imin])
        imin = i;
    }
    return imin;
  }

  /** @param x A fraction of the distance between scalarMin and scalarMax
 * @param scalarMin Minimum scalar
 * @param scalarMax Maximum scalar
 * @return optimum fraction of distance between minimum and maximum scalars
   */
  private double function(double x, double scalarMin, double scalarMax) {
    return function(scalarMin + x*(scalarMax - scalarMin));
  }

  /** Evaluate function
   * @param scalar Argument for embedded function
    * @return Value of function
   */
  private double function(double scalar) {
    return _function.function(scalar);
  }

  /**  Fit a parabola to three points and find the minimum point.
       where f(x1) = f1; f(x2) = f2; f(x3) = f3;
       and where x1 < x2 < x3; f(x2) < f(x1); f(x2) < f(x3).
       @param x1 A value of the argument to the parabolic function
       @param f1 The value of the function f(x1) at x1
       @param x2 A value of the argument to the parabolic function
       @param f2 The value of the function f(x2) at x2
       @param x3 A value of the argument to the parabolic function
       @param f3 The value of the function f(x3) at x3
       @return Value of x that minimizes function f(x)
       @exception BadParabolaException If the arguments
       describe a parabola that cannot be minimized in the range x1 < x <x3,
       or if the following strict inequalities
       are not true: x1 < x2 < x3; f(x2) < f(x1), f(x2) < f(x3),
       or if the x2 is too close to one of the endpoints
       to describe a parabola accurately.
 * @throws IllegalArgumentException If input values lead to degenerate
 * solutions.
   */
  private double minParabola(double x1, double f1,
                             double x2, double f2,
                             double x3, double f3)
    throws BadParabolaException, IllegalArgumentException {
    if (!Almost.FLOAT.le(x1,x2) || !Almost.FLOAT.le(x2,x3)) {
      throw new BadParabolaException
        ("Violates x1 <= x2 <= x3: x1="+x1+" x2="+x2+" x3="+x3);
    }
    if (Almost.FLOAT.equal(x1,x2)) {
      // LOG.fine("May have already converged x1="+x1+" x2="+x2+" x3="+x3);
      double result = x2 + 0.1*(x3-x2);
      // LOG.fine("Decimate other interval with "+result);
      return result;
    }
    if (Almost.FLOAT.equal(x2,x3)) {
      // LOG.fine("May have already converged x1="+x1+" x2="+x2+" x3="+x3);
      double result = x1 + 0.9*(x2-x1);
      // LOG.fine("Decimate other interval with "+result);
      return result;
    }
    if (!Almost.FLOAT.le(f2,f1) || !Almost.FLOAT.le(f2,f3)) {
      throw new BadParabolaException
        ("Violates f(x2) <= f(x1), f(x2) <= f(x3)"+
         ": f1="+f1+" f2="+f2+" f3="+f3);
    }
    double xm = Almost.FLOAT.divide((x2 - x1),(x3 - x1), 0.);
    if (xm < 0.001 || xm > 0.999) {
      throw new BadParabolaException
        ("Parabola is badly sampled x1="+x1+" x2="+x2+" x3="+x3);
    }
    double a = Almost.FLOAT.divide( ((f3-f1)*xm - (f2-f1)) , (xm - xm*xm), 0.);
    double b = f3 - f1 - a;
    if (Almost.FLOAT.ge(a*b, 0.) || 0.5 * Math.abs(b) > Math.abs(a)) {
      throw new BadParabolaException
        ("Poor numerical conditioning a="+a+" b="+b);
    }
    xm = Almost.FLOAT.divide(-0.5 * b , a, -1.);
    if (xm < 0. || xm > 1.) {
      throw new BadParabolaException
        ("Poor numerical conditioning a="+a+" b="+b+" xm="+xm);
    }
    double result = xm*(x3-x1) + x1;
    return result;
  }

  private static class BadParabolaException extends Exception {
    private static final long serialVersionUID = 1L;
    /** Available points do not describe a valid parabola
       @param message Error message
     */
    public BadParabolaException(String message) {super (message);}
  }

}
