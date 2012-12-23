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

package org.nextreamlabs.simplex.model.converter;

import org.nextreamlabs.simplex.command.Command;
import org.nextreamlabs.simplex.model.LinearModel;
import org.nextreamlabs.simplex.model.StandardLinearModel;
import org.nextreamlabs.simplex.model.component.*;
import org.nextreamlabs.simplex.ui.Ui;
import org.nextreamlabs.simplex.ui.command.SetConstraintConstantTermCommand;
import org.nextreamlabs.simplex.ui.command.SetConstraintSignCommand;
import org.nextreamlabs.simplex.ui.command.SetConstraintVariableCommand;
import org.nextreamlabs.simplex.ui.command.SetObjfuncVariableCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert a linear model into a standard linear model
 */
public class StandardModelConverter implements ModelConverter {
  private LinearModel model;

  // { Constructors and Factories

  private StandardModelConverter(LinearModel model) {
    // { Preconditions
    assert (model != null) : "The model argument cannot be null";
    // }

    this.model = model;
  }

  public static ModelConverter create(LinearModel model) {
    return new StandardModelConverter(model);
  }

  // }

  /**
   * Convert from the model passed in the construction phase into a standard linear model.
   * The solutions history is reset and only the current one will be kept
   * @return The new standard linear model equivalent to the model field
   * @throws CannotConvertModelException
   */
  @Override
  public List<Command<Ui>> convert() throws CannotConvertModelException {
    List<Command<Ui>> commands = new ArrayList<Command<Ui>>();

    ImmutableLinearObjfunc objfunc = this.model.getObjfunc();
    ImmutableSolution solution = this.model.getCurrentSolution();
    List<ImmutableSolution> solutionHistory = new ArrayList<ImmutableSolution>();
    List<ImmutableLinearConstraint> constraints = this.model.getConstraints();

    // ==> Convertion based on the objective (it must be minimization in a standard linear model)
    if (!this.model.getObjective().equals(Objective.MIN)) {
      objfunc = objfunc.multiply(BigDecimal.valueOf(-1));
      if (solution != null) {
        solution = DefaultImmutableSolution.create(objfunc, solution.getVarsValues(),
            solution.getCoefficient().multiply(BigDecimal.valueOf(-1)));
      }
    }
    // ==> Convertion based on the constraints
    for (Integer i = 0; i < constraints.size(); i++) {
      // ==> All of the constant-terms must be positive
      ImmutableLinearConstraint constraint = constraints.get(i);
      if (constraint.getConstantTerm().compareTo(BigDecimal.ZERO) == -1) {
        constraint = constraint.multiply(BigDecimal.valueOf(-1));
      }
      // ==> All of the constraints must be equations
      if (!constraint.getSign().equals(ConstraintSign.Equal)) {
        Variable augmentedVar = SmartVariable.create(
            (constraint.getSign().equals(ConstraintSign.LessEqual) ? BigDecimal.ONE :
                BigDecimal.valueOf(-1))
            , true);
        // Add a slack/surplus variable to each constraints
        // (augmented variable with coefficient -1/1)
        constraint = constraint.addVariable(augmentedVar)
            .duplicate(null, ConstraintSign.Equal, null);
        for (Integer j = 0; j < constraints.size(); j++) {
          // Add the slack/surplus into the constraint j (!= i)
          // (it's not the slack/surplus for the constraint j => augmented var with coefficient 0)
          ImmutableLinearConstraint otherConstraint = constraints.get(j);
          if (!j.equals(i)) {
            otherConstraint = otherConstraint
                .addVariable(
                    SmartVariable.createFromExistingId(augmentedVar.getId(), BigDecimal.ZERO));
            constraints.set(j, otherConstraint); // Update constraints with the modified constraint
          }
        }
        // Add the slack/surplus variable into the objective-function
        // The coefficient is zero because it should not modify the objective value of the model
        objfunc = objfunc.addVariable(
            SmartVariable.createFromExistingId(augmentedVar.getId(), BigDecimal.ZERO));
      }
      constraints.set(i, constraint); // Update constraints with the modified constraint
    }

    solutionHistory.add(solution);

    this.model.setObjfunc(objfunc);
    this.model.setConstraints(constraints);
    this.model.setObjective(Objective.MIN);
    this.model.setSolutionHistory(solutionHistory);

    // { Create the commands
    for (Integer idx = 0; idx < constraints.size(); idx++) {
      ImmutableLinearConstraint constraint = constraints.get(idx);
      for (Variable var : constraint.getVariables()) {
        commands.add(new SetConstraintVariableCommand(idx, var.getId(), var.getCoefficient()));
      }
      commands.add(new SetConstraintSignCommand(idx, constraint.getSign()));
      commands.add(new SetConstraintConstantTermCommand(idx, constraint.getConstantTerm()));
    }

    for (Variable var : objfunc.getVariables()) {
      commands.add(new SetObjfuncVariableCommand(var.getId(), var.getCoefficient()));
    }
    // }

    return commands;
  }

  @Override
  public LinearModel getModel() { return this.model; }

}
