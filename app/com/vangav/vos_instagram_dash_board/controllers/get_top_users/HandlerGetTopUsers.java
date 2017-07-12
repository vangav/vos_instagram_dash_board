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

package com.vangav.vos_instagram_dash_board.controllers.get_top_users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.vangav.backend.cassandra.Cassandra;
import com.vangav.backend.cassandra.formatting.CalendarFormatterInl;
import com.vangav.backend.data_structures_and_algorithms.tuple.Pair;
import com.vangav.backend.metrics.time.CalendarAndDateOperationsInl;
import com.vangav.backend.play_framework.param.ParamParsersInl;
import com.vangav.backend.play_framework.request.Request;
import com.vangav.backend.play_framework.request.RequestJsonBody;
import com.vangav.backend.play_framework.request.response.ResponseBody;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.CountPerWeek;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.CountTotal;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.FollowerCount;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.UserPostsCount;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.UsersInfo;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.UsersRankWorld;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_auth.UsersCredIds;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_top_users.response_json.ResponseTopUser;
import com.vangav.vos_instagram_dash_board.controllers.get_top_users.response_json.ResponseWeek;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetTopUsers
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetTopUsers extends CommonPlayHandler {

  private static final String kName = "GetTopUsers";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetTopUsers();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetTopUsers();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetTopUsers requestGetTopUsers =
      (RequestGetTopUsers)request.getRequestJsonBody();
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetTopUsers.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetTopUsers.from_date);
    
    if (requestGetTopUsers.isValidParam(
          RequestGetTopUsers.kToDateName) == true) {
     
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetTopUsers.to_date);
    }
    
    ArrayList<Pair<Calendar,Calendar> > calendarRangeWeeks =
      CalendarAndDateOperationsInl.getWeekCalendarsRanges(
        fromCalendar,
        toCalendar);
    
    ArrayList<Calendar> calendarRange = new ArrayList<Calendar>();
    
    for (Pair<Calendar,Calendar> calendarRangeWeek : calendarRangeWeeks) {
      
      calendarRange.add(calendarRangeWeek.getFirst() );
    }
    
    // won't get more than 52 weeks per-request
    calendarRange =
      (ArrayList<Calendar>)calendarRange.subList(
        0,
        Math.min(calendarRange.size(), 52) );
    
    // store bound statements
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (Calendar calendar : calendarRange) {
      
      boundStatements.add(
        UsersRankWorld.i().getBoundStatementSelectTopLimit(
          CalendarFormatterInl.concatCalendarFields(
            calendar,
            Calendar.YEAR,
            Calendar.WEEK_OF_YEAR) ) );
    }
    
    // execute bound statements
    ArrayList<ResultSet> usersRankWorldResultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    String currWeekDate;
    ResultSet currUsersRankWorldResultSet;
    
    // per-top-user result sets
    ResultSet currUsersCredIdsResultSet;
    ResultSet currUsersInfoResultSet;
    ResultSet currCountPerWeekResultSet;
    ResultSet currFollowerCountResultSet;
    ResultSet currUserPostsCountResultSet;
    ResultSet currCountTotalResultSet;
    
    Row currRow;
    
    ArrayList<ResponseWeek> responseWeeks = new ArrayList<ResponseWeek>();
    ArrayList<ResponseTopUser> weekResponseTopUsers;
    
    // from ig_app_data.users_rank_world
    double currUserRank;
    UUID currUserId;
    
    // from ig_auth.users_cred_ids
    String currUserEmail;
    
    // from ig_app_data.users_info
    String currUserName;
    long currUserRegistrationTime;
    
    // formatting results of select from ig_app_data.users_info;
    String currUserFormattedRegistrationDate;
    
    // from ig_app_data.count_per_week
    long currUserFollowCountLastWeek;
    long currUserUnfollowCountLastWeek;
    long currUserPostsCountLastWeek;
    long currUserLikesCountLastWeek;
    long currUserCommentsCountLastWeek;
    
    // from ig_app_data.follower_count
    long currUserFollowerCountTotal;
    
    // from ig_app_data.user_posts_count
    long currUserPostsCountTotal;
    
    // from ig_app_data.count_total
    long currUserLikesCountTotal;
    long currUserCommentsCountTotal;
    
    // for ig_app_data.count_per_week
    Calendar currPastWeekCalendar;
    
    // for each calendar date
    for (int i = 0; i < calendarRange.size(); i ++) {
      
      // format date into string, e.g.: 22/10/2017
      currWeekDate =
        CalendarAndDateOperationsInl.getFormattedDate(calendarRange.get(i) );
      
      currUsersRankWorldResultSet = usersRankWorldResultSets.get(i);
      
      // no top users for that date?
      if (currUsersRankWorldResultSet.isExhausted() == true) {
        
        responseWeeks.add(new ResponseWeek(currWeekDate) );
        
        continue;
      }
      
      // store a week of top ranked users
      weekResponseTopUsers = new ArrayList<ResponseTopUser>();
      
      // get past week
      currPastWeekCalendar = (Calendar)calendarRange.get(i).clone();
      currPastWeekCalendar.set(Calendar.WEEK_OF_YEAR, -1);
      
      // for each top ranked user of a week
      for (Row row : currUsersRankWorldResultSet) {
        
        // get user's rank and uuid
        currUserRank = row.getDouble(UsersRankWorld.kRankColumnName);
        currUserId = row.getUUID(UsersRankWorld.kUserIdColumnName);
        
        // select from ig_auth.users_cred_ids
        currUsersCredIdsResultSet =
          UsersCredIds.i().executeSyncSelectEmail(currUserId);
        
        // get selected row
        currRow = currUsersCredIdsResultSet.one();
        
        // extract user's email
        currUserEmail = currRow.getString(UsersCredIds.kEmailColumnName);
        
        // select from ig_app_data.users_info
        currUsersInfoResultSet =
          UsersInfo.i().executeSyncSelectAll(currUserId);
        
        // get selected row
        currRow = currUsersInfoResultSet.one();
        
        // extract user's name and registration time
        currUserName =
          currRow.getString(UsersInfo.kNameColumnName);
        currUserRegistrationTime =
          currRow.getLong(UsersInfo.kRegistrationTimeColumnName);
        
        // format user's registration time into date
        currUserFormattedRegistrationDate =
          CalendarAndDateOperationsInl.getFormattedDate(
            CalendarAndDateOperationsInl.getCalendarFromUnixTime(
              currUserRegistrationTime) );
        
        // select from ig_app_data.count_per_week
        currCountPerWeekResultSet =
          CountPerWeek.i().executeSyncSelect(
            currUserId
            + "_"
            + CalendarFormatterInl.concatCalendarFields(
              currPastWeekCalendar,
              Calendar.YEAR,
              Calendar.WEEK_OF_YEAR) );
        
        // get selected row
        currRow = currCountPerWeekResultSet.one();
        
        // extract's user's last week counts
        //   (follow, unfollow, posts, likes, comments)
        currUserFollowCountLastWeek =
          currRow.getLong(CountPerWeek.kFollowerCountColumnName);
        currUserUnfollowCountLastWeek =
          currRow.getLong(CountPerWeek.kUnfollowerCountColumnName);
        currUserPostsCountLastWeek =
          currRow.getLong(CountPerWeek.kPostsCountColumnName);
        currUserLikesCountLastWeek =
          currRow.getLong(CountPerWeek.kLikesReceivedCountColumnName);
        currUserCommentsCountLastWeek =
          currRow.getLong(CountPerWeek.kCommentsReceivedCountColumnName);
        
        // select from ig_app_data.follower_count
        currFollowerCountResultSet =
          FollowerCount.i().executeSyncSelect(currUserId);
        
        // get selected row
        currRow = currFollowerCountResultSet.one();
        
        // extract user's total followers count
        currUserFollowerCountTotal =
          currRow.getLong(FollowerCount.kFollowerCountColumnName);
        
        // select from ig_app_data.user_posts_count
        currUserPostsCountResultSet =
          UserPostsCount.i().executeSyncSelect(currUserId);
        
        // get selected row
        currRow = currUserPostsCountResultSet.one();
        
        // extract user's posts count
        currUserPostsCountTotal =
          currRow.getLong(UserPostsCount.kPostsCountColumnName);
        
        // select from ig_app_data.count_total
        currCountTotalResultSet =
          CountTotal.i().executeSyncSelect(currUserId);
        
        // get selected row
        currRow = currCountTotalResultSet.one();
        
        // extract user's likes and comments total count
        currUserLikesCountTotal =
          currRow.getLong(CountTotal.kLikesReceivedCountColumnName);
        currUserCommentsCountTotal =
          currRow.getLong(CountTotal.kCommentsReceivedCountColumnName);
        
        // add top ranked user to this week of top ranked users
        weekResponseTopUsers.add(
          new ResponseTopUser(
            currUserRank,
            currUserEmail,
            currUserName,
            currUserFormattedRegistrationDate,
            currUserFollowCountLastWeek,
            currUserUnfollowCountLastWeek,
            currUserPostsCountLastWeek,
            currUserLikesCountLastWeek,
            currUserCommentsCountLastWeek,
            currUserFollowerCountTotal,
            currUserPostsCountTotal,
            currUserLikesCountTotal,
            currUserCommentsCountTotal) );
      } // end: for (Row row : currUsersRankWorldResultSet) {
      
      // add response's week of top ranked users
      responseWeeks.add(
        new ResponseWeek(
          currWeekDate,
          weekResponseTopUsers.toArray(new ResponseTopUser[0] ) ) );
    } // end: for (int i = 0; i < calendarRange.size(); i ++) {
    
    // set response
    ((ResponseGetTopUsers)request.getResponseBody() ).set(
      responseWeeks.toArray(new ResponseWeek[0] ) );
  }
}
