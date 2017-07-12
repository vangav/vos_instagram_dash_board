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

package com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_analytics.DailyRegionalCounters;
import com.vangav.vos_instagram_dash_board.common.Constants;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters.response_json.ResponseDailyRegion;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters.response_json.ResponseDay;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters.response_json.ResponseRegionTotal;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetDailyRegionalCounters
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetDailyRegionalCounters extends CommonPlayHandler {

  private static final String kName = "GetDailyRegionalCounters";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetDailyRegionalCounters();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetDailyRegionalCounters();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetDailyRegionalCounters requestGetDailyRegionalCounters =
      (RequestGetDailyRegionalCounters)request.getRequestJsonBody();
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyRegionalCounters.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetDailyRegionalCounters.from_date);
    
    if (requestGetDailyRegionalCounters.isValidParam(
          RequestGetDailyRegionalCounters.kToDateName) == true) {
      
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetDailyRegionalCounters.to_date);
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
    
    for (Calendar calendar : calendarRange) {
      
      for (String region : requestGetDailyRegionalCounters.regions) {
        
        partitionKeys.add(
          CalendarFormatterInl.concatCalendarFields(
            calendar,
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.DAY_OF_MONTH)
            + Constants.kCassandraIdConcat
            + region);
      }
    }
    
    // execute queries
    
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (String partitionKey : partitionKeys) {
      
      boundStatements.add(
        DailyRegionalCounters.i().getBoundStatementSelect(partitionKey) );
    }
    
    ArrayList<ResultSet> resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    Map<String, ResponseRegionTotal> regionsTotal =
      new HashMap<String, ResponseRegionTotal>();
    ArrayList<ResponseDay> perDay =
      new ArrayList<ResponseDay>();
    ArrayList<ResponseDailyRegion> dailyRegions =
      new ArrayList<ResponseDailyRegion>();
    
    int index = 0;
    ResultSet currResultSet;
    Row currRow;
    
    for (Calendar calendar : calendarRange) {
      
      for (String region : requestGetDailyRegionalCounters.regions) {
        
        currResultSet = resultSets.get(index);
        
        if (currResultSet.isExhausted() == true) {
          
          if (regionsTotal.containsKey(region) == true) {
            
            regionsTotal.put(
              region,
              regionsTotal.get(region).add(
                new ResponseRegionTotal(
                  region) ) );
          } else {
            
            regionsTotal.put(
              region,
              new ResponseRegionTotal(
                region) );
          }
          
          dailyRegions.add(
            new ResponseDailyRegion(
              region) );
          
          index += 1;
          continue;
        }
        
        currRow = currResultSet.one();
        
        if (regionsTotal.containsKey(region) == true) {
          
          regionsTotal.put(
            region,
            regionsTotal.get(region).add(
              new ResponseRegionTotal(
                region,
                currRow.getLong(
                  DailyRegionalCounters.kNewUsersColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kNewPostsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kSentFollowsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kReceivedFollowsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kSentUnfollowsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kReceivedUnfollowsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kSentLikesColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kReceivedLikesColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kSentCommentsColumnName),
                currRow.getLong(
                  DailyRegionalCounters.kReceivedCommentsColumnName) ) ) );
        } else {
          
          regionsTotal.put(
            region,
            new ResponseRegionTotal(
              region,
              currRow.getLong(
                DailyRegionalCounters.kNewUsersColumnName),
              currRow.getLong(
                DailyRegionalCounters.kNewPostsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kSentFollowsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kReceivedFollowsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kSentUnfollowsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kReceivedUnfollowsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kSentLikesColumnName),
              currRow.getLong(
                DailyRegionalCounters.kReceivedLikesColumnName),
              currRow.getLong(
                DailyRegionalCounters.kSentCommentsColumnName),
              currRow.getLong(
                DailyRegionalCounters.kReceivedCommentsColumnName) ) );
        }
        
        dailyRegions.add(
          new ResponseDailyRegion(
            region,
            currRow.getLong(
              DailyRegionalCounters.kNewUsersColumnName),
            currRow.getLong(
              DailyRegionalCounters.kNewPostsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kSentFollowsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kReceivedFollowsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kSentUnfollowsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kReceivedUnfollowsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kSentLikesColumnName),
            currRow.getLong(
              DailyRegionalCounters.kReceivedLikesColumnName),
            currRow.getLong(
              DailyRegionalCounters.kSentCommentsColumnName),
            currRow.getLong(
              DailyRegionalCounters.kReceivedCommentsColumnName) ) );

        index += 1;
      }
      
      perDay.add(
        new ResponseDay(
          Constants.kDateFormatDay.format(
            CalendarAndDateOperationsInl.getDateFromCalendar(calendar) ),
          dailyRegions.toArray(new ResponseDailyRegion[0] ) ) );
      
      dailyRegions = new ArrayList<ResponseDailyRegion>();
    }
    
    ResponseRegionTotal[] regionsTotalArr =
      new ResponseRegionTotal[requestGetDailyRegionalCounters.regions.length];
    
    for (int i = 0;
         i < requestGetDailyRegionalCounters.regions.length;
         i ++) {
      
      regionsTotalArr[i] =
        regionsTotal.get(requestGetDailyRegionalCounters.regions[i] );
    }
    
    // set response
    ((ResponseGetDailyRegionalCounters)request.getResponseBody() ).set(
      regionsTotalArr,
      perDay.toArray(new ResponseDay[0] ) );
  }
}
