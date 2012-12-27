/**************************************************************************************************
 * Copyright (C) 2012  nextreamlabs                                                               *
 * This program is free software: you can redistribute it and/or modify                           *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 * This program is distributed in the hope that it will be useful,                                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 * You should have received a copy of the GNU General Public License                              *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.                          *
 **************************************************************************************************/

package org.nextreamlabs.simplex.solver.pivot_solver;

import org.nextreamlabs.simplex.command.Command;
import org.nextreamlabs.simplex.model.BigDecimalUtil;
import org.nextreamlabs.simplex.model.LinearModel;
import org.nextreamlabs.simplex.model.component.DefaultImmutableLinearConstraint;
import org.nextreamlabs.simplex.model.component.DefaultImmutableSolution;
import org.nextreamlabs.simplex.model.component.ImmutableLinearConstraint;
import org.nextreamlabs.simplex.model.component.ImmutableLinearObjfunc;
import org.nextreamlabs.simplex.model.component.SmartVariable;
import org.nextreamlabs.simplex.model.component.Variable;
import org.nextreamlabs.simplex.model.converter.CannotConvertModelException;
import org.nextreamlabs.simplex.model.converter.StandardModelConverter;
import org.nextreamlabs.simplex.solver.Solver;
import org.nextreamlabs.simplex.solver.SolverEventListener;
import org.nextreamlabs.simplex.solver.SolverEventSource;
import org.nextreamlabs.simplex.ui.Ui;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Helper class to solve a simplex problem using the pivot algorithm
 */
public class DefaultPivotSolverStepHelper implements PivotSolverStepHelper, SolverEventSource {

  private static final Logger logger;

  private final LinearModel model;
  private final Map<Integer, Integer> base;

  private final Set<SolverEventListener> listeners;

  static {
    logger = Logger.getLogger("org.nextreamlabs.simplex.DefaultPivotSolverStepHelper");
  }

  // { Constructors and Factories

  private DefaultPivotSolverStepHelper(LinearModel model) {
    this.model = model;
    this.base = new HashMap<Integer, Integer>(this.model.getConstraints().size());

    this.listeners = new HashSet<SolverEventListener>();
  }

  public static PivotSolverStepHelper create(LinearModel model) {
    return new DefaultPivotSolverStepHelper(model);
  }

  // }

  /**
   * Fix the model representation
   * - Convert the model in the standard linear form
   * @return True if the fixing was successful, otherwise false
   */
  @Override
  public Boolean fixModel() {
    List<Command<Ui>> commands;
    try {
      commands = StandardModelConverter.create(this.model).convert();
    } catch (CannotConvertModelException exc) {
      return Boolean.FALSE;
    }
    logger.info("Model conversion into:\n" + this.model.toString());
    for (Command<Ui> command : commands) {
      this.notifyCommandRequest(command);
    }

    return Boolean.TRUE;
  }

  /**
   * Solves a simplex problem to create an initial solution admissible for the original model
   * NOTE: It also removes old solutions
   */
  @Override
  public void findInitialSolution() throws UnlimitedProblemException {
    logger.info("Getting the initial solution through the two-phases method");

    // ==> Create the augmented model for the first phase
    Collection<Integer> firstPhaseAugmentedVariablesIds = new ArrayList<Integer>();
    // Set the values that assumes the already present variables in the initial solution for
    // the first-phase model
    Map<Integer, BigDecimal> solutionVarsValues = new HashMap<Integer, BigDecimal>();
    for (Variable var : this.model.getObjfunc().getVariables()) {
      solutionVarsValues.put(var.getId(), BigDecimal.ZERO);
    }
    ImmutableLinearObjfunc originalObjfunc = this.model.getObjfunc();
    // Set the objective function
    this.model.setObjfunc(originalObjfunc.multiply(BigDecimal.ZERO));
    // For each constraint we need to add a new variable in the model
    for (Integer idx = 0; idx < this.model.getConstraints().size(); idx++) {
      List<Variable> currentConstraintVars = new ArrayList<Variable>();
      // Add the variable for the objective-function
      Variable varForObjfunc = SmartVariable.create(BigDecimal.ONE, true);
      currentConstraintVars.add(varForObjfunc);
      // Add the variable for each constraint
      for (Integer j = 0; j < this.model.getConstraints().size(); j++) {
        currentConstraintVars.add(SmartVariable.createFromExistingId(varForObjfunc.getId(),
            idx.equals(j) ? BigDecimal.ONE : BigDecimal.ZERO));
      }
      this.model.addVariable(currentConstraintVars);
      // Add the variable value into the initial solution
      solutionVarsValues.put(varForObjfunc.getId(),
          this.model.getConstraints().get(idx).getConstantTerm());
      // Add the variable into the base
      this.base.put(idx, varForObjfunc.getId());
      // Keep track of the variable added
      firstPhaseAugmentedVariablesIds.add(varForObjfunc.getId());
    }
    // Clear the solution history
    this.model.resetSolutionHistory();
    // Add the initial solution to the solution history
    this.model.addSolution(
        DefaultImmutableSolution.create(this.model.getObjfunc(), solutionVarsValues,
            BigDecimal.ONE));

    logger.info("Created the following model for the first-phase:\n" + this.model.toString());

    // ==> Put in the base canonical form
    for (Integer firstPhaseVarId : firstPhaseAugmentedVariablesIds) {
      Integer pivotHoriz = null, pivotVert = firstPhaseVarId;
      for (Map.Entry<Integer, Integer> baseEntry : this.base.entrySet()) {
        if (baseEntry.getValue().equals(firstPhaseVarId)) {
          pivotHoriz = baseEntry.getKey();
        }
      }
      assert (pivotHoriz != null) : "Find initial solution bug.";
      this.doPivot(pivotHoriz, pivotVert);
    }

    // ==> Optimize the first-phase while the solution isn't optimal. When it is, we found the
    //     initial solution for the original model
    while(!this.isSolutionOptimal()) {
      Integer[] pivot = this.findPivot();
      this.doPivot(pivot[0], pivot[1]);
    }

    // ==> Remove the augmented variable used for the first phase
    for (Integer varId : firstPhaseAugmentedVariablesIds) {
      this.model.removeVariable(varId);
    }
    // ==> Restore the objective-function
    this.model.setObjfunc(originalObjfunc);
    this.model.addSolution(DefaultImmutableSolution.create(originalObjfunc,
        this.model.getCurrentSolution().getVarsValues(),
        this.model.getCurrentSolution().getCoefficient()));
  }

  /**
   * Check if the current solution for the model is optimal
   * @return True if the solution is optimal, otherwise false
   */
  @Override
  public Boolean isSolutionOptimal() {
    if (this.model.getCurrentSolution() == null) {
      return Boolean.FALSE;
    }

    for (Variable var : this.model.getObjfunc().getVariables()) {
      if (BigDecimalUtil.compare(var.getCoefficient(), BigDecimal.ZERO) < 0) {
        return Boolean.FALSE;
      }
    }
    return Boolean.TRUE;
  }

  /**
   * @return True if there is an initial solution available, otherwise false
   */
  @Override
  public Boolean checkInitialSolution() {
    return this.model.getCurrentSolution() != null;
  }

  /**
   * Find the pivot
   * @return The found pivot or null if the problem is unlimited
   */
  @Override
  public Integer[] findPivot() throws UnlimitedProblemException {
    // { Preconditions
    assert (this.model.getConstraints().size() > 0) : "Cannot do pivot without constraints";
    assert (this.model.getObjfuncVariables().size() > 0) :
        "Cannot do pivot with an empty objective-function";
    assert (this.model.hasSolution()) : "An initial solution is needed by the pivot algorithm";
    // }

    // ==> Select the pivot
    Integer pivotVert = null;
    for (Variable var : this.model.getObjfuncVariables()) {
      // Select the variable with the minor id (for the Bland rule)
      // and the coefficient lesser than zero (because it hasn't to be in the current base)
      if (BigDecimalUtil.compare(var.getCoefficient(), BigDecimal.ZERO) < 0) {
        if (pivotVert == null || (pivotVert.compareTo(var.getId()) > 0)) {
          pivotVert = var.getId();
        }
      }
    }
    assert (pivotVert != null) : "Pivot algorithm bug. The pivot variable cannot be null";

    Integer pivotHoriz = null;
    BigDecimal value = null;
    for (Integer idx = 0; idx < this.model.getConstraintsSize(); idx++) {
      ImmutableLinearConstraint constraint = this.model.getConstraint(idx);
      BigDecimal constraintCoeff = constraint.findVariableById(pivotVert).getCoefficient();
      BigDecimal constraintConstTerm = constraint.getConstantTerm();
      // Select the constraint with the minimum ratio
      // If there is a draw, select the one that has the minimum index
      if (BigDecimalUtil.compare(constraintCoeff, BigDecimal.ZERO) > 0) {
        BigDecimal ratio = BigDecimalUtil.divide(constraintConstTerm, constraintCoeff);
        if ((pivotHoriz == null) || (value == null)
            || ((ratio.compareTo(value) < 0)
                || ((ratio.compareTo(value) == 0) && (pivotHoriz > idx)))) {

          pivotHoriz = idx;
          value = BigDecimalUtil.divide(constraintConstTerm, constraintCoeff);
        }
      }
    }
    if (pivotHoriz == null) { // The problem is unlimited
      throw new UnlimitedProblemException();
    }

    logger.info("The pivot is: " + this.pivotToString(pivotHoriz, pivotVert));

    return new Integer[]{pivotHoriz, pivotVert};
  }

  /**
   * Do a pivot step, which is a step where the model gets optimized and it moves from
   * admissible solutions to admissible solutions and the model remains in the base canonical form
   */
  @Override
  public void doPivot(Integer pivotHoriz, Integer pivotVert) {
    // { Preconditions
    assert (this.model.getConstraints().size() > 0) : "Cannot do pivot without constraints";
    assert (this.model.getObjfuncVariables().size() > 0) :
        "Cannot do pivot with an empty objective-function";
    assert (this.model.hasSolution()) : "An initial solution is needed by the pivot algorithm";
    // }

    Variable pivotVariable = this.model.getConstraint(pivotHoriz).findVariableById(pivotVert);
    BigDecimal pivotCoefficient = pivotVariable.getCoefficient();

    // ==> Update the constraint that contains the pivot
    // Get the variables for the new pivot constraint
    List<Variable> vars = new ArrayList<Variable>();
    ImmutableLinearConstraint pivotConstraint = this.model.getConstraint(pivotHoriz);
    for (Variable var : pivotConstraint.getVariables()) {
      vars.add(SmartVariable.createFromExistingId(var.getId(),
          BigDecimalUtil.divide(var.getCoefficient(), pivotCoefficient)));
    }
    // Update the pivot constraint
    pivotConstraint = DefaultImmutableLinearConstraint.create(vars, pivotConstraint.getSign(),
        BigDecimalUtil.divide(pivotConstraint.getConstantTerm(), pivotCoefficient));
    // Update the pivot constraint into the model
    Boolean pivotConstraintChanged = this.model.setConstraint(pivotHoriz, pivotConstraint);
    assert (pivotConstraintChanged) : "Pivot algorithm bug. Updated a non-existing constraint";

    // ==> Update the constraints that doesn't contain the pivot
    for (Integer idx = 0; idx < this.model.getConstraintsSize(); idx++) {
      ImmutableLinearConstraint constraint = this.model.getConstraint(idx);

      BigDecimal secondOp = constraint.findVariableById(pivotVert).getCoefficient();

      if (!idx.equals(pivotHoriz)) {

        for (Variable var : constraint.getVariables()) {
          BigDecimal firstOp = pivotConstraint.findVariableById(var.getId()).getCoefficient();
          BigDecimal factor = firstOp.multiply(secondOp);
          this.model.setConstraintVariable(idx, var.getId(), var.getCoefficient().subtract(factor));
        }
        BigDecimal firstOp = pivotConstraint.getConstantTerm();
        BigDecimal factor = firstOp.multiply(secondOp);
        this.model.setConstraintConstantTerm(idx, constraint.getConstantTerm().subtract(factor));
      }
    }

    // ==> Update the objective-function
    BigDecimal secondOp = this.model.getObjfunc().findVariableById(pivotVert).getCoefficient();
    for (Variable var : this.model.getObjfunc().getVariables()) {
      BigDecimal firstOp = pivotConstraint.findVariableById(var.getId()).getCoefficient();
      BigDecimal factor = firstOp.multiply(secondOp);

      this.model.setObjfuncVariable(var.getId(), var.getCoefficient().subtract(factor));
    }

    // ==> Update the base
    this.base.put(pivotHoriz, pivotVert);
    logger.info("The base is: " + this.baseToString());

    // ==> Update the solution
    Map<Integer, BigDecimal> varsValues = new HashMap<Integer, BigDecimal>();
    // Update the solution for the variables that are in the base
    for (Map.Entry<Integer, Integer> baseEntry : this.base.entrySet()) {
      Integer constraintIndex = baseEntry.getKey();
      Integer varId = baseEntry.getValue();
      ImmutableLinearConstraint constraint = this.model.getConstraint(constraintIndex);
      varsValues.put(varId, constraint.getConstantTerm().multiply(
          constraint.findVariableById(varId).getCoefficient()));
    }
    // Update the solution for the variables that aren't in the base
    for (Variable var : this.model.getObjfunc().getVariables()) {
      if (varsValues.get(var.getId()) == null) {
        varsValues.put(var.getId(), BigDecimal.ZERO);
      }
    }
    this.model.addSolution(this.model.getCurrentSolution().duplicate(null, varsValues, null));

    logger.info("The model is:\n" + this.model.toString());

  }

  @Override
  public String baseToString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Integer, Integer> baseEntry : this.base.entrySet()) {
      sb.append("[").append("var_id=").append(baseEntry.getValue()).append("]");
    }
    return sb.toString();
  }

  private String pivotToString(Integer pivotHoriz, Integer pivotVert) {
    return "[constraint_idx=" + pivotHoriz + "][var_id=" + pivotVert + "]";
  }

  // { Listeners management

  @Override
  public void register(SolverEventListener listener) {
    this.listeners.add(listener);
  }

  // }

  // { Listeners notifiers

  private void notifyCommandRequest(Command<Ui> command) {
    for (SolverEventListener listener : this.listeners) {
      listener.commandRequestedForUi(command);
    }
  }

  // }
}
