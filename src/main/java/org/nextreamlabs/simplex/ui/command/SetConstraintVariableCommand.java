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

package org.nextreamlabs.simplex.ui.command;

import org.nextreamlabs.simplex.command.AbstractCommand;
import org.nextreamlabs.simplex.ui.Ui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Command to set a constraint variable
 */
public class SetConstraintVariableCommand extends AbstractCommand<Ui> {
  private final Integer constraintIndex;
  private final Integer id;
  private final BigDecimal coefficient;

  public SetConstraintVariableCommand(Integer constraintIndex, Integer id, BigDecimal coefficient) {
    this.constraintIndex = constraintIndex;
    this.id = id;
    this.coefficient = coefficient;
  }

  @Override
  protected Boolean run(Ui target) {
    return target.setConstraintVariable(this.constraintIndex, this.id, this.coefficient);
  }

  @Override
  protected Map<String, String> getLoggingInfo() {
    Map<String, String> loggingInfo = new HashMap<String, String>();
    loggingInfo.put("constraint_idx", this.constraintIndex.toString());
    loggingInfo.put("var_id", this.id.toString());
    loggingInfo.put("coefficient", this.coefficient.toString());
    return loggingInfo;
  }

  @Override
  public String getName() {
    return "Set Constraint Variable";
  }

  @Override
  public Integer getPriority() {
    return 5;
  }
}
