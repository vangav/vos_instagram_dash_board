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

package com.vangav.vos_instagram_dash_board.controllers.get_daily_users_logs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.vangav.backend.cassandra.Cassandra;
import com.vangav.backend.cassandra.formatting.CalendarFormatterInl;
import com.vangav.backend.data_structures_and_algorithms.tuple.Pair;
import com.vangav.backend.exceptions.BadRequestException;
import com.vangav.backend.exceptions.VangavException.ExceptionClass;
import com.vangav.backend.metrics.time.CalendarAndDateOperationsInl;
import com.vangav.backend.play_framework.param.ParamParsersInl;
import com.vangav.backend.play_framework.request.Request;
import com.vangav.backend.play_framework.request.RequestJsonBody;
import com.vangav.backend.play_framework.request.response.ResponseBody;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_auth.EmailCreds;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_logging.DailyUsersLogs;
import com.vangav.vos_instagram_dash_board.common.Constants;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_logs.response_json.ResponseDayUserLogs;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_logs.response_json.ResponseUserLog;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_logs.response_json.ResponseUserLogs;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetDailyUsersLogs
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetDailyUsersLogs extends CommonPlayHandler {

  private static final String kName = "GetDailyUsersLogs";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetDailyUsersLogs();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetDailyUsersLogs();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetDailyUsersLogs requestGetDailyUsersLogs =
      (RequestGetDailyUsersLogs)request.getRequestJsonBody();
    
    // get users' ids from request's user_emails
    
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (String userEmail : requestGetDailyUsersLogs.user_emails) {
      
      boundStatements.add(
        EmailCreds.i().getBoundStatementSelect(
          userEmail) );
    }
    
    ArrayList<ResultSet> resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    ArrayList<Pair<UUID, String> > userIds =
      new ArrayList<Pair<UUID, String> >();
    
    ResultSet currResultSet;
    
    for (int i = 0;
         i < requestGetDailyUsersLogs.user_emails.length;
         i ++) {
      
      currResultSet = resultSets.get(i);
      
      if (currResultSet.isExhausted() == true) {
        
        throw new BadRequestException(
          405,
          1,
          "User email ["
            + requestGetDailyUsersLogs.user_emails[i]
            + "] isn't registered",
          ExceptionClass.INVALID);
      }
      
      userIds.add(
        new Pair<UUID, String>(
          currResultSet.one().getUUID(EmailCreds.kUserIdColumnName),
          requestGetDailyUsersLogs.user_emails[i] ) );
    }
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyUsersLogs.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyUsersLogs.from_date);
    
    if (requestGetDailyUsersLogs.isValidParam(
          RequestGetDailyUsersLogs.kToDateName) == true) {
      
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetDailyUsersLogs.to_date);
    }
    
    ArrayList<Calendar> calendarRange =
      CalendarAndDateOperationsInl.getCalendarsFromTo(
        fromCalendar,
        toCalendar);
    
    // won't get more than 31 days per-request
    calendarRange =
      (ArrayList<Calendar>)calendarRange.subList(
        0,
        Math.min(calendarRange.size(), 31) );
    
    // make partition keys
    
    ArrayList<String> partitionKeys = new ArrayList<String>();
    
    for (Pair<UUID, String> userId : userIds) {
      
      for (Calendar calendar : calendarRange) {
        
        partitionKeys.add(
          CalendarFormatterInl.concatCalendarFields(
            calendar,
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.DAY_OF_MONTH)
            + Constants.kCassandraIdConcat
            + userId.getFirst().toString() );
      }
    }
    
    // execute queries
    
    boundStatements = new ArrayList<BoundStatement>();
    
    for (String partitionKey : partitionKeys) {
      
      boundStatements.add(
        DailyUsersLogs.i().getBoundStatementSelect(partitionKey) );
    }
    
    resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    ArrayList<ResponseUserLogs> dailyUsersLogs =
      new ArrayList<ResponseUserLogs>();
    ArrayList<ResponseDayUserLogs> dailyLogs =
      new ArrayList<ResponseDayUserLogs>();
    ArrayList<ResponseUserLog> logs =
      new ArrayList<ResponseUserLog>();
    
    int index = 0;
    
    for (Pair<UUID, String> userId : userIds) {
      
      for (Calendar calendar : calendarRange) {
        
        currResultSet = resultSets.get(index);
        
        if (currResultSet.isExhausted() == true) {
          
          index += 1;
          continue;
        }
        
        for (Row row : currResultSet) {
          
          if (currResultSet.getAvailableWithoutFetching() <=
              Constants.kCassandraPrefetchLimit &&
              currResultSet.isFullyFetched() == false) {
            
            // this is asynchronous
            currResultSet.fetchMoreResults();
          }
          
          logs.add(
            new ResponseUserLog(
              Constants.kDateFormatTime.format(
                CalendarAndDateOperationsInl.getDateFromCalendar(
                  CalendarAndDateOperationsInl.getCalendarFromUnixTime(
                    row.getLong(DailyUsersLogs.kLogTimeColumnName) ) ) ),
              row.getUUID(DailyUsersLogs.kRequestIdColumnName).toString(),
              row.getString(DailyUsersLogs.kControllerNameColumnName),
              row.getString(DailyUsersLogs.kRequestColumnName),
              row.getString(DailyUsersLogs.kResponseStatusCodeColumnName),
              row.getString(DailyUsersLogs.kResponseColumnName),
              row.getInt(DailyUsersLogs.kRunTimeMilliSecondsColumnName) ) );
        }
        
        dailyLogs.add(
          new ResponseDayUserLogs(
            Constants.kDateFormatDay.format(
              CalendarAndDateOperationsInl.getDateFromCalendar(calendar) ),
            logs.toArray(new ResponseUserLog[0] ) ) );
        
        logs = new ArrayList<ResponseUserLog>();
        
        index += 1;
      }
      
      dailyUsersLogs.add(
        new ResponseUserLogs(
          userId.getFirst().toString(),
          userId.getSecond(),
          dailyLogs.toArray(new ResponseDayUserLogs[0] ) ) );
      
      dailyLogs = new ArrayList<ResponseDayUserLogs>();
    }
    
    // set response
    ((ResponseGetDailyUsersLogs)request.getResponseBody() ).set(
      dailyUsersLogs.toArray(new ResponseUserLogs[0] ) );
  }
}
