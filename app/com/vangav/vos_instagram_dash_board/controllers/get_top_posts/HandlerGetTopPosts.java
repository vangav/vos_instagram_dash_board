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

package com.vangav.vos_instagram_dash_board.controllers.get_top_posts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.vangav.backend.cassandra.Cassandra;
import com.vangav.backend.cassandra.formatting.CalendarFormatterInl;
import com.vangav.backend.geo.reverse_geo_coding.ReverseGeoCode;
import com.vangav.backend.geo.reverse_geo_coding.ReverseGeoCoding;
import com.vangav.backend.metrics.time.CalendarAndDateOperationsInl;
import com.vangav.backend.play_framework.param.ParamParsersInl;
import com.vangav.backend.play_framework.request.Request;
import com.vangav.backend.play_framework.request.RequestJsonBody;
import com.vangav.backend.play_framework.request.response.ResponseBody;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.CountPerWeek;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.PostCommentsCount;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.PostLikesCount;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.Posts;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.PostsRankWorld;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_app_data.UsersInfo;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_auth.UsersCredIds;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_top_posts.response_json.ResponseDay;
import com.vangav.vos_instagram_dash_board.controllers.get_top_posts.response_json.ResponseTopPost;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetTopPosts
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetTopPosts extends CommonPlayHandler {

  private static final String kName = "GetTopPosts";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetTopPosts();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetTopPosts();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetTopPosts requestGetTopPosts =
      (RequestGetTopPosts)request.getRequestJsonBody();
    
    // set dates range
    
    Calendar fromCalendar =
      ParamParsersInl.parseCalendar(requestGetTopPosts.from_date);
    Calendar toCalendar =
      ParamParsersInl.parseCalendar(requestGetTopPosts.from_date);
    
    if (requestGetTopPosts.isValidParam(
          RequestGetTopPosts.kToDateName) == true) {
      
      toCalendar =
        ParamParsersInl.parseCalendar(requestGetTopPosts.to_date);
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
    
    // store bound statements
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (Calendar calendar : calendarRange) {
      
      boundStatements.add(
        PostsRankWorld.i().getBoundStatementSelectTopLimit(
          CalendarFormatterInl.concatCalendarFields(
            calendar,
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.DAY_OF_MONTH) ) );
    }
    
    // execute bound statements
    ArrayList<ResultSet> postsRankWorldResultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    String currDate;
    ResultSet currPostsRankWorldResultSet;
    
    // per-top-post result sets
    ResultSet currPostsResultSet;
    ResultSet currPostLikesCountResultSet;
    ResultSet currPostCommentsCountResultSet;
    ResultSet currUsersCredIdsResultSet;
    ResultSet currUsersInfoResultSet;
    ResultSet currCountPerWeekResultSet;
    
    Row currRow;
    
    ArrayList<ResponseDay> responseDays = new ArrayList<ResponseDay>();
    ArrayList<ResponseTopPost> dayResponseTopPosts;
    
    // from ig_app_data.posts_rank_world
    double currPostRank;
    UUID currPostId;
    
    // from ig_app_data.posts
    long currPostTime;
    String currPostCaption;
    UUID currUserId;
    double currPostLatitude;
    double currPostLongitude;
    
    // formatting results of select from ig_app_data.posts
    String currPostFormattedDate;
    String currPostFormattedTime;
    ReverseGeoCode currReverseGeoCode;
    String currPostCity;
    String currPostCountry;
    
    // from ig_app_data.post_likes_count
    long currPostLikesCount;
    
    // from ig_app_data.post_comments_count
    long currPostCommentsCount;
    
    // from ig_auth.users_cred_ids
    String currUserEmail;
    
    // from ig_app_data.users_info
    String currUserName;
    long currUserRegistrationTime;
    
    // formatting results of select from ig_app_data.users_info
    String currUserFormattedRegistrationDate;
    
    // from ig_app_data.count_per_week
    long currUserFollowCountLastWeek;
    long currUserUnfollowCountLastWeek;
    long currUserPostsCountLastWeek;
    long currUserLikesCountLastWeek;
    long currUserCommentsCountLastWeek;
    
    // for ig_app_data.count_per_week
    Calendar currPastWeekCalendar;
    
    // for each calendar date
    for (int i = 0; i < calendarRange.size(); i ++) {
      
      // format date into string, e.g.: 22/10/2017
      currDate =
        CalendarAndDateOperationsInl.getFormattedDate(calendarRange.get(i) );
      
      currPostsRankWorldResultSet = postsRankWorldResultSets.get(i);
      
      // no top posts for that date?
      if (currPostsRankWorldResultSet.isExhausted() == true) {
        
        responseDays.add(new ResponseDay(currDate) );
        
        continue;
      }
      
      // store a day of top ranked posts
      dayResponseTopPosts = new ArrayList<ResponseTopPost>();
      
      // get past week
      currPastWeekCalendar = (Calendar)calendarRange.get(i).clone();
      currPastWeekCalendar.set(Calendar.WEEK_OF_YEAR, -1);
      
      // for each top ranked post on a day
      for (Row row : currPostsRankWorldResultSet) {
        
        // get post's rank and uuid
        currPostRank = row.getDouble(PostsRankWorld.kRankColumnName);
        currPostId = row.getUUID(PostsRankWorld.kPostIdColumnName);
        
        // select from ig_app_data.posts
        currPostsResultSet = Posts.i().executeSyncSelect(currPostId);
        
        // get selected row
        currRow = currPostsResultSet.one();
        
        // extract post's time, caption, user uuid, latitude and longitude
        currPostTime = currRow.getLong(Posts.kPostTimeColumnName);
        currPostCaption = currRow.getString(Posts.kCaptionColumnName);
        currUserId = currRow.getUUID(Posts.kUserIdColumnName);
        currPostLatitude = currRow.getDouble(Posts.kLatitudeColumnName);
        currPostLongitude = currRow.getDouble(Posts.kLongitudeColumnName);
        
        // format post's date/time and get post's city/country
        currPostFormattedDate =
          CalendarAndDateOperationsInl.getFormattedDate(
            CalendarAndDateOperationsInl.getCalendarFromUnixTime(
              currPostTime) );
        currPostFormattedTime =
          CalendarAndDateOperationsInl.getFormattedTime(
            CalendarAndDateOperationsInl.getCalendarFromUnixTime(
              currPostTime) );
        currReverseGeoCode =
          ReverseGeoCoding.i().getReverseGeoCode(
            currPostLatitude,
            currPostLongitude);
        currPostCity = currReverseGeoCode.getMajorCity();
        currPostCountry = currReverseGeoCode.getCountry();
        
        // select from ig_app_data.post_likes_count
        currPostLikesCountResultSet =
          PostLikesCount.i().executeSyncSelect(currPostId);
        
        // get selected row
        currRow = currPostLikesCountResultSet.one();
        
        // extract post's likes count
        currPostLikesCount =
          currRow.getLong(PostLikesCount.kLikesCountColumnName);
        
        // select from ig_app_data.post_comments_count
        currPostCommentsCountResultSet =
          PostCommentsCount.i().executeSyncSelect(currPostId);
        
        // get selected row
        currRow = currPostCommentsCountResultSet.one();
        
        // extract post's comments count
        currPostCommentsCount =
          currRow.getLong(PostCommentsCount.kCommentsCountColumnName);
        
        // select from ig_auth.users_cred_ids
        currUsersCredIdsResultSet =
          UsersCredIds.i().executeSyncSelectEmail(currUserId);
        
        // get selected row
        currRow = currUsersCredIdsResultSet.one();
        
        // extract user's email
        currUserEmail =
          currRow.getString(UsersCredIds.kEmailColumnName);
        
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
        
        // format user's registration date
        currUserFormattedRegistrationDate =
          CalendarAndDateOperationsInl.getFormattedDate(
            CalendarAndDateOperationsInl.getCalendarFromUnixTime(
              currUserRegistrationTime) );
        
        // select from ig_app_data.count_per_week
        currCountPerWeekResultSet =
          CountPerWeek.i().executeSyncSelect(
            currUserId.toString()
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
        
        // add top ranked post to this day of top ranked posts
        dayResponseTopPosts.add(
          new ResponseTopPost(
            currPostRank,
            currUserEmail,
            currUserName,
            currUserFormattedRegistrationDate,
            currUserFollowCountLastWeek,
            currUserUnfollowCountLastWeek,
            currUserPostsCountLastWeek,
            currUserLikesCountLastWeek,
            currUserCommentsCountLastWeek,
            currPostFormattedDate,
            currPostFormattedTime,
            currPostCity,
            currPostCountry,
            currPostCaption,
            currPostLikesCount,
            currPostCommentsCount) );
      } // end: for (Row row : currPostsRankWorldResultSet) {
      
      // add response's day of top ranked posts
      responseDays.add(
        new ResponseDay(
          currDate,
          dayResponseTopPosts.toArray(new ResponseTopPost[0] ) ) );
    } // end: for (int i = 0; i < calendarRange.size(); i ++) {
    
    // set response
    ((ResponseGetTopPosts)request.getResponseBody() ).set(
      responseDays.toArray(new ResponseDay[0] ) );
  }
}
