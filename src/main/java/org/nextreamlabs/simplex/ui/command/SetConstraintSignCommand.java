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
import org.nextreamlabs.simplex.model.component.ConstraintSign;
import org.nextreamlabs.simplex.ui.Ui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Command to set the constraint sign
 */
public class SetConstraintSignCommand extends AbstractCommand<Ui> {
  private final Integer constraintIndex;
  private final ConstraintSign sign;

  public SetConstraintSignCommand(Integer constraintIndex, ConstraintSign sign) {
    this.constraintIndex = constraintIndex;
    this.sign = sign;
  }

  @Override
  protected Boolean run(Ui target) {
    return target.setConstraintSign(this.constraintIndex, this.sign);
  }

  @Override
  protected Map<String, String> getLoggingInfo() {
    Map<String, String> loggingInfo = new HashMap<String, String>();
    loggingInfo.put("constraint_idx", this.constraintIndex.toString());
    loggingInfo.put("sign", "'" + this.sign.toString() + "'");
    return loggingInfo;
  }

  @Override
  public String getName() {
    return "Set Constraint Sign";
  }

  @Override
  public Integer getPriority() {
    return 5;
  }
}
