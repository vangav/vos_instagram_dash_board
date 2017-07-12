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

package com.vangav.vos_instagram_dash_board.controllers.get_daily_users_error_logs;

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
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_logging.DailyUsersErrorLogs;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_logging.ErrorLogs;
import com.vangav.vos_instagram_dash_board.common.Constants;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_error_logs.response_json.ResponseDayUserErrorLogs;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_error_logs.response_json.ResponseUserErrorLog;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_users_error_logs.response_json.ResponseUserErrorLogs;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetDailyUsersErrorLogs
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetDailyUsersErrorLogs extends CommonPlayHandler {

  private static final String kName = "GetDailyUsersErrorLogs";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetDailyUsersErrorLogs();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetDailyUsersErrorLogs();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetDailyUsersErrorLogs requestGetDailyUsersErrorLogs =
      (RequestGetDailyUsersErrorLogs)request.getRequestJsonBody();
    
    // get users' ids from request's user_emails
    
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (String userEmail : requestGetDailyUsersErrorLogs.user_emails) {
      
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
         i < requestGetDailyUsersErrorLogs.user_emails.length;
         i ++) {
      
      currResultSet = resultSets.get(i);
      
      if (currResultSet.isExhausted() == true) {
        
        throw new BadRequestException(
          404,
          1,
          "User email ["
            + requestGetDailyUsersErrorLogs.user_emails[i]
            + "] isn't registered",
          ExceptionClass.INVALID);
      }
      
      userIds.add(
        new Pair<UUID, String>(
          currResultSet.one().getUUID(EmailCreds.kUserIdColumnName),
          requestGetDailyUsersErrorLogs.user_emails[i] ) );
    }
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyUsersErrorLogs.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyUsersErrorLogs.from_date);
    
    if (requestGetDailyUsersErrorLogs.isValidParam(
          RequestGetDailyUsersErrorLogs.kToDateName) == true) {
      
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetDailyUsersErrorLogs.to_date);
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
        DailyUsersErrorLogs.i().getBoundStatementSelect(partitionKey) );
    }
    
    resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    ArrayList<ResponseUserErrorLogs> dailyUsersErrorLogs =
      new ArrayList<ResponseUserErrorLogs>();
    ArrayList<ResponseDayUserErrorLogs> dailyErrorLogs =
      new ArrayList<ResponseDayUserErrorLogs>();
    ArrayList<ResponseUserErrorLog> errorLogs =
      new ArrayList<ResponseUserErrorLog>();
    
    int index = 0;
    UUID errorLogId;
    ResultSet errorLogResultSet;
    Row errorLogRow;
    
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
          
          errorLogId = row.getUUID(DailyUsersErrorLogs.kLogIdColumnName);
          
          errorLogResultSet =
            ErrorLogs.i().executeSyncSelect(errorLogId);
          
          errorLogRow = errorLogResultSet.one();
          
          errorLogs.add(
            new ResponseUserErrorLog(
              errorLogId.toString(),
              Constants.kDateFormatTime.format(
                CalendarAndDateOperationsInl.getDateFromCalendar(
                  CalendarAndDateOperationsInl.getCalendarFromUnixTime(
                    errorLogRow.getLong(ErrorLogs.kLogTimeColumnName) ) ) ),
              errorLogRow.getString(ErrorLogs.kControllerNameColumnName),
              errorLogRow.getInt(ErrorLogs.kHttpStatusCodeColumnName),
              errorLogRow.getString(ErrorLogs.kRequestColumnName),
              errorLogRow.getString(ErrorLogs.kErrorResponseColumnName) ) );
        }
        
        dailyErrorLogs.add(
          new ResponseDayUserErrorLogs(
            Constants.kDateFormatDay.format(
              CalendarAndDateOperationsInl.getDateFromCalendar(calendar) ),
            errorLogs.toArray(new ResponseUserErrorLog[0] ) ) );
        
        errorLogs = new ArrayList<ResponseUserErrorLog>();
        
        index += 1;
      }
      
      dailyUsersErrorLogs.add(
        new ResponseUserErrorLogs(
          userId.getFirst().toString(),
          userId.getSecond(),
          dailyErrorLogs.toArray(new ResponseDayUserErrorLogs[0] ) ) );
      
      dailyErrorLogs = new ArrayList<ResponseDayUserErrorLogs>();
    }
    
    // set response
    ((ResponseGetDailyUsersErrorLogs)request.getResponseBody() ).set(
      dailyUsersErrorLogs.toArray(new ResponseUserErrorLogs[0] ) );
  }
}
