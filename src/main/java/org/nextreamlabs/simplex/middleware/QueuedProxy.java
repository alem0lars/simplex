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

package org.nextreamlabs.simplex.middleware;

import org.nextreamlabs.simplex.command.Command;
import org.nextreamlabs.simplex.command.CommandInvoker;
import org.nextreamlabs.simplex.command.QueuedCommandInvoker;
import org.nextreamlabs.simplex.model.converter.CannotConvertModelException;
import org.nextreamlabs.simplex.solver.MetaSolverCreator;
import org.nextreamlabs.simplex.solver.Solver;
import org.nextreamlabs.simplex.ui.Ui;

import java.util.List;
import java.util.logging.Logger;

/**
 * Proxy between the UI (User Interactions) and the Solver (Strategy to solve the problem)
 */
public class QueuedProxy implements Proxy {
  private final static Logger logger;

  private Solver solver;
  private final CommandInvoker<Ui> uiCommandInvoker;
  private final CommandInvoker<Solver> solverCommandInvoker;
  private final List<Ui> uiList;

  static {
    logger = Logger.getLogger("org.nextreamlabs.QueuedProxy");
  }

  // { Constructors and Factories

  private QueuedProxy(List<Ui> uiList) {
    this.uiList = uiList;
    this.solver = null;
    this.uiCommandInvoker = QueuedCommandInvoker.create();
    this.solverCommandInvoker = QueuedCommandInvoker.create();
  }

  /**
   * Create a new Proxy object
   * @param uiList List of UI to be notified for the solver events
   * @return The created Proxy object
   */
  public static Proxy create(List<Ui> uiList) {
    return new QueuedProxy(uiList);
  }

  // }

  // { SolverEventListener reactions

  /**
   * Callback to request a command on the UIs
   * @param command The command to be requested at the UIs
   */
  @Override
  public void commandRequestedForUi(Command<Ui> command) {
    this.uiCommandInvoker.addCommand(command);
    for (Ui ui : this.uiList) {
      this.uiCommandInvoker.executeAll(ui);
    }
    this.uiCommandInvoker.clear();
  }

  // }

  // { UiEventListener reactions

  /**
   * Callback to request a command on the solver
   * @param command The command to be requested at the solver
   */
  @Override
  public void commandRequestedForSolver(Command<Solver> command) {
    this.solverCommandInvoker.addCommand(command);
    if (this.solver != null) {
      this.solverCommandInvoker.executeAll(this.solver);
      this.solverCommandInvoker.clear();
    }
  }

  /**
   * Callback to select a solver
   * @param metaSolverCreator The creator object that represents the solver type and the creation
   *                          policy
   * @throws CannotConvertModelException
   */
  @Override
  public void solverSelected(MetaSolverCreator metaSolverCreator)
      throws CannotConvertModelException {
    this.solver = metaSolverCreator.create();
    this.solver.register(this);

    logger.info("Solver " + this.solver.getName() + " selected.");
    this.solverCommandInvoker.executeAll(this.solver);
    this.solverCommandInvoker.clear();
  }

  /**
   * Callback when an ui has been added
   * @param ui The added
   */
  @Override
  public void uiAdded(Ui ui) {
    this.uiList.add(ui);
  }

  // }

}
