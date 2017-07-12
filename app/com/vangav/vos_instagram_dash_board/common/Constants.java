/**
 * "First, solve the problem. Then, write the code. -John Johnson"
 * "Or use Vangav M"
 * www.vangav.com
 * */

/**
 * MIT License
 *
 * Copyright (c) 2016 Vangav
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * */

/**
 * Community
 * Facebook Group: Vangav Open Source - Backend
 *   fb.com/groups/575834775932682/
 * Facebook Page: Vangav
 *   fb.com/vangav.f
 * 
 * Third party communities for Vangav Backend
 *   - play framework
 *   - cassandra
 *   - datastax
 *   
 * Tag your question online (e.g.: stack overflow, etc ...) with
 *   #vangav_backend
 *   to easier find questions/answers online
 * */

package com.vangav.vos_instagram_dash_board.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author mustapha
 * fb.com/mustapha.abdallah
 */
/**
 * Constants has vos_instagram_dash_board constants
 */
public class Constants {
  
  /**
   * kCassandraIdConcat is the String used to concat multi-part IDs
   *   e.g.: year_month_day
   */
  public static final String kCassandraIdConcat = "_";
  
  /**
   * kCassandraPrefetchLimit the number of remaining rows at which to start
   *   asynchronously fetching more rows
   */
  public static final int kCassandraPrefetchLimit = 100;
  
  /**
   * kDateFormatDay is used to format response's dates
   */
  public static final DateFormat kDateFormatDay =
    new SimpleDateFormat("dd/MM/yyyy");
  
  /**
   * kDateFormatTime is used to format response's times
   */
  public static final DateFormat kDateFormatTime =
    new SimpleDateFormat("HH:mm");
}
