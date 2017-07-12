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

package com.vangav.vos_instagram_dash_board.controllers.get_annual_regional_counters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.vangav.backend.cassandra.Cassandra;
import com.vangav.backend.play_framework.request.Request;
import com.vangav.backend.play_framework.request.RequestJsonBody;
import com.vangav.backend.play_framework.request.response.ResponseBody;
import com.vangav.vos_instagram_dash_board.cassandra_keyspaces.ig_analytics.AnnualRegionalCounters;
import com.vangav.vos_instagram_dash_board.common.Constants;
import com.vangav.vos_instagram_dash_board.controllers.CommonPlayHandler;
import com.vangav.vos_instagram_dash_board.controllers.get_annual_regional_counters.response_json.ResponseAnnualRegion;
import com.vangav.vos_instagram_dash_board.controllers.get_annual_regional_counters.response_json.ResponseRegionTotal;
import com.vangav.vos_instagram_dash_board.controllers.get_annual_regional_counters.response_json.ResponseYear;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * HandlerGetAnnualRegionalCounters
 *   handles request-to-response processing
 *   also handles after response processing (if any)
 * */
public class HandlerGetAnnualRegionalCounters extends CommonPlayHandler {

  private static final String kName = "GetAnnualRegionalCounters";

  @Override
  protected String getName () {

    return kName;
  }

  @Override
  protected RequestJsonBody getRequestJson () {

    return new RequestGetAnnualRegionalCounters();
  }

  @Override
  protected ResponseBody getResponseBody () {

    return new ResponseGetAnnualRegionalCounters();
  }

  @Override
  protected void processRequest (final Request request) throws Exception {

    // use the following request Object to process the request and set
    //   the response to be returned
    RequestGetAnnualRegionalCounters requestGetAnnualRegionalCounters =
      (RequestGetAnnualRegionalCounters)request.getRequestJsonBody();
    
    // set to-year
    
    int toYear = requestGetAnnualRegionalCounters.from_year;
    
    if (requestGetAnnualRegionalCounters.isValidParam(
          RequestGetAnnualRegionalCounters.kToYearName) == true) {
      
      if (requestGetAnnualRegionalCounters.to_year >
          requestGetAnnualRegionalCounters.from_year) {
        
        // won't get more than 10 years per-request
        toYear =
          Math.min(
            requestGetAnnualRegionalCounters.to_year,
            requestGetAnnualRegionalCounters.from_year + 9);
      }
    }

    // make partition keys
    
    ArrayList<String> partitionKeys = new ArrayList<String>();
    
    for (int year = requestGetAnnualRegionalCounters.from_year;
         year <= toYear;
         year ++) {
      
      for (String region : requestGetAnnualRegionalCounters.regions) {
        
        partitionKeys.add(
          year
          + Constants.kCassandraIdConcat
          + region);
      }
    }
    
    // execute queries
    
    ArrayList<BoundStatement> boundStatements =
      new ArrayList<BoundStatement>();
    
    for (String partitionKey : partitionKeys) {
      
      boundStatements.add(
        AnnualRegionalCounters.i().getBoundStatementSelect(partitionKey) );
    }
    
    ArrayList<ResultSet> resultSets =
      Cassandra.i().executeSync(
        boundStatements.toArray(new BoundStatement[0] ) );
    
    // build response
    
    Map<String, ResponseRegionTotal> regionsTotal =
      new HashMap<String, ResponseRegionTotal>();
    ArrayList<ResponseYear> perYear =
      new ArrayList<ResponseYear>();
    ArrayList<ResponseAnnualRegion> annualRegions =
      new ArrayList<ResponseAnnualRegion>();
    
    int index = 0;
    ResultSet currResultSet;
    Row currRow;
    
    for (int year = requestGetAnnualRegionalCounters.from_year;
         year <= toYear;
         year ++) {
      
      for (String region : requestGetAnnualRegionalCounters.regions) {
       
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
          
          annualRegions.add(
            new ResponseAnnualRegion(
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
                  AnnualRegionalCounters.kNewUsersColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kNewPostsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kSentFollowsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kReceivedFollowsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kSentUnfollowsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kReceivedUnfollowsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kSentLikesColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kReceivedLikesColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kSentCommentsColumnName),
                currRow.getLong(
                  AnnualRegionalCounters.kReceivedCommentsColumnName) ) ) );
        } else {
          
          regionsTotal.put(
            region,
            new ResponseRegionTotal(
              region,
              currRow.getLong(
                AnnualRegionalCounters.kNewUsersColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kNewPostsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kSentFollowsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kReceivedFollowsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kSentUnfollowsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kReceivedUnfollowsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kSentLikesColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kReceivedLikesColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kSentCommentsColumnName),
              currRow.getLong(
                AnnualRegionalCounters.kReceivedCommentsColumnName) ) );
        }
        
        annualRegions.add(
          new ResponseAnnualRegion(
            region,
            currRow.getLong(
              AnnualRegionalCounters.kNewUsersColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kNewPostsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kSentFollowsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kReceivedFollowsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kSentUnfollowsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kReceivedUnfollowsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kSentLikesColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kReceivedLikesColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kSentCommentsColumnName),
            currRow.getLong(
              AnnualRegionalCounters.kReceivedCommentsColumnName) ) );
        
        index += 1;
      }
      
      perYear.add(
        new ResponseYear(
          year,
          annualRegions.toArray(new ResponseAnnualRegion[0] ) ) );
      
      annualRegions = new ArrayList<ResponseAnnualRegion>();
    }
    
    ResponseRegionTotal[] regionsTotalArr =
      new ResponseRegionTotal[requestGetAnnualRegionalCounters.regions.length];
    
    for (int i = 0;
         i < requestGetAnnualRegionalCounters.regions.length;
         i ++) {
      
      regionsTotalArr[i] =
        regionsTotal.get(requestGetAnnualRegionalCounters.regions[i] );
    }
    
    // set response
    ((ResponseGetAnnualRegionalCounters)request.getResponseBody() ).set(
      regionsTotalArr,
      perYear.toArray(new ResponseYear[0] ) );
  }
}
