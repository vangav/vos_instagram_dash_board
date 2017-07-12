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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vangav.backend.play_framework.request.response.ResponseBodyJson;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters.response_json.ResponseDay;
import com.vangav.vos_instagram_dash_board.controllers.get_daily_regional_counters.response_json.ResponseRegionTotal;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * ResponseGetDailyRegionalCounters represents the response's structure
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseGetDailyRegionalCounters extends ResponseBodyJson {

  @Override
  @JsonIgnore
  protected String getName () throws Exception {

    return "GetDailyRegionalCounters";
  }

  @Override
  @JsonIgnore
  protected ResponseGetDailyRegionalCounters getThis () throws Exception {

    return this;
  }

  @JsonProperty
  public ResponseRegionTotal[] regions_total;
  @JsonProperty
  public ResponseDay[] per_day;
  
  @JsonIgnore
  public void set (
    ResponseRegionTotal[] regions_total,
    ResponseDay[] per_day) throws Exception {
    
    this.regions_total = regions_total;
    this.per_day = per_day;
  }
}
