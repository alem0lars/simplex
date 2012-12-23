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

package org.nextreamlabs.simplex.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilities for objects of type BigDecimal
 */
public class BigDecimalUtil {
  private BigDecimalUtil() {}

  private static final RoundingMode roundingMode;
  private static final Integer scale;
  private static final Integer prettyScale;
  private static final BigDecimal epsilon;

  static {
    roundingMode = RoundingMode.HALF_UP;
    scale = 10;
    prettyScale = 2;
    epsilon = new BigDecimal("1e-3");
  }

  public static RoundingMode getRoundingMode() { return roundingMode; }
  public static Integer getScale() { return scale; }

  public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
    return dividend.divide(divisor, BigDecimalUtil.getScale(), BigDecimalUtil.getRoundingMode());
  }

  public static Integer compare(BigDecimal a, BigDecimal b) {
    if (a.subtract(b).abs().compareTo(epsilon) <= 0) {
      return 0;
    } else {
      return a.compareTo(b);
    }
  }

  public static String toPrettyString(BigDecimal value) {
    return value.setScale(prettyScale, roundingMode).toString();
  }

}
