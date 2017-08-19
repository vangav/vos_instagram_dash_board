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

package com.vangav.vos_instagram_dash_board.controllers.get_daily_requests_counters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.vangav.backend.cassandra.Cassandra;
import com.vangav.backend.cassandra.formatting.CalendarFormatterInl;
import com.vangav.backend.metrics.time.CalendarAndDateOperationsInl;
import com.vangav.backend.play_framework.param.ParamParsersInl;
import com.vangav.backend.play_framework.request.Request;
import com.vangav.backend.play_framework.request.RequestJsonBody;
import com.vangav.backend.play_framework.request.response.ResponseBody;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_logging.DailyRequestsCounters;
import com.vangav.vos_instagram_dash_board.common.Constants;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_requests_counters.response_json.ResponseControllerTotal;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_requests_counters.response_json.ResponseDailyController;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_requests_counters.response_json.ResponseDay;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetDailyRequestsCounters
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetDailyRequestsCounters extends CommonPlayHandler {

  private static final String kName = "GetDailyRequestsCounters";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetDailyRequestsCounters();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetDailyRequestsCounters();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetDailyRequestsCounters requestGetDailyRequestsCounters =
      (RequestGetDailyRequestsCounters)request.getRequestJsonBody();
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyRequestsCounters.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyRequestsCounters.from_date);
    
    if (requestGetDailyRequestsCounters.isValidParam(
          RequestGetDailyRequestsCounters.kToDateName) == true) {
      
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetDailyRequestsCounters.to_date);
    }
    
    ArrayList<Calendar> calendarRange =
      CalendarAndDateOperationsInl.getCalendarsFromTo(
        fromCalendar,
        toCalendar);
    
    // won't get more than 31 days per-request
    
    List<Calendar> tempCalendarRange =
      calendarRange.subList(
        0,
        Math.min(calendarRange.size(), 31) );
    
    calendarRange = new ArrayList<Calendar>();
    calendarRange.addAll(tempCalendarRange);

    // make partition keys
    
    ArrayList<String> partitionKeys = new ArrayList<String>();
    
    for (Calendar calendar : calendarRange) {
      
      for (String controller : requestGetDailyRequestsCounters.controllers) {
        
        partitionKeys.add(
          CalendarFormatterInl.concatCalendarFields(
            calendar,
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.DAY_OF_MONTH)
            + Constants.kCassandraIdConcat
            + controller);
      }
    }
    
    // execute queries
    
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (String partitionKey : partitionKeys) {
      
      boundStatements.add(
        DailyRequestsCounters.i().getBoundStatementSelect(partitionKey) );
    }
    
    ArrayList<ResultSet> resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    Map<String, ResponseControllerTotal> controllersTotal =
      new HashMap<String, ResponseControllerTotal>();
    ArrayList<ResponseDay> perDay =
      new ArrayList<ResponseDay>();
    ArrayList<ResponseDailyController> dailyControllers =
      new ArrayList<ResponseDailyController>();
    
    int index = 0;
    ResultSet currResultSet;
    Row currRow;
    
    for (Calendar calendar : calendarRange) {
      
      for (String controller : requestGetDailyRequestsCounters.controllers) {
        
        currResultSet = resultSets.get(index);
        
        if (currResultSet.isExhausted() == true) {
          
          if (controllersTotal.containsKey(controller) == true) {
            
            controllersTotal.put(
              controller,
              controllersTotal.get(controller).add(
                new ResponseControllerTotal(
                  controller) ) );
          } else {
            
            controllersTotal.put(
              controller,
              new ResponseControllerTotal(
                controller) );
          }
          
          dailyControllers.add(
            new ResponseDailyController(
              controller) );
          
          index += 1;
          continue;
        }
        
        currRow = currResultSet.one();
        
        if (controllersTotal.containsKey(controller) == true) {
          
          controllersTotal.put(
            controller,
            controllersTotal.get(controller).add(
              new ResponseControllerTotal(
                controller,
                currRow.getLong(
                  DailyRequestsCounters.kRequestsColumnName),
                currRow.getLong(
                  DailyRequestsCounters.kOkResponsesColumnName),
                currRow.getLong(
                  DailyRequestsCounters.kBadRequestResponsesColumnName),
                currRow.getLong(
                  DailyRequestsCounters.kInternalErrorResponsesColumnName),
                currRow.getLong(
                  DailyRequestsCounters.kRunTimeMilliSecondsColumnName) ) ) );
        } else {
          
          controllersTotal.put(
            controller,
            new ResponseControllerTotal(
              controller,
              currRow.getLong(
                DailyRequestsCounters.kRequestsColumnName),
              currRow.getLong(
                DailyRequestsCounters.kOkResponsesColumnName),
              currRow.getLong(
                DailyRequestsCounters.kBadRequestResponsesColumnName),
              currRow.getLong(
                DailyRequestsCounters.kInternalErrorResponsesColumnName),
              currRow.getLong(
                DailyRequestsCounters.kRunTimeMilliSecondsColumnName) ) );
        }
        
        dailyControllers.add(
          new ResponseDailyController(
            controller,
            currRow.getLong(
              DailyRequestsCounters.kRequestsColumnName),
            currRow.getLong(
              DailyRequestsCounters.kOkResponsesColumnName),
            currRow.getLong(
              DailyRequestsCounters.kBadRequestResponsesColumnName),
            currRow.getLong(
              DailyRequestsCounters.kInternalErrorResponsesColumnName),
            currRow.getLong(
              DailyRequestsCounters.kRunTimeMilliSecondsColumnName) ) );
        
        index += 1;
      }
      
      perDay.add(
        new ResponseDay(
          Constants.kDateFormatDay.format(
            CalendarAndDateOperationsInl.getDateFromCalendar(calendar) ),
          dailyControllers.toArray(new ResponseDailyController[0] ) ) );
      
      dailyControllers = new ArrayList<ResponseDailyController>();
    }
    
    ResponseControllerTotal[] controllersTotalArr =
      new ResponseControllerTotal[
        requestGetDailyRequestsCounters.controllers.length];
    
    for (int i = 0;
         i < requestGetDailyRequestsCounters.controllers.length;
         i ++) {
      
      controllersTotalArr[i] =
        controllersTotal.get(requestGetDailyRequestsCounters.controllers[i] );
    }
    
    // set response
    ((ResponseGetDailyRequestsCounters)request.getResponseBody() ).set(
      controllersTotalArr,
      perDay.toArray(new ResponseDay[0] ) );
  }
}
