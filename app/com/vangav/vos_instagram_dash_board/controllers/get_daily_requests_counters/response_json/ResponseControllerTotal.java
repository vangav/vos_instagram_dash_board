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

package com.vangav.vos_instagram_dash_board.controllers.get_daily_requests_counters.response_json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mustapha
 * fb.com/mustapha.abdallah
 */
/**
 * ResponseControllerTotal represents a controller's total count for all the
 *   requested dates
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseControllerTotal {

  @JsonProperty
  public String controller;
  @JsonProperty
  public long requests;
  @JsonProperty
  public long ok_responses;
  @JsonProperty
  public long bad_request_responses;
  @JsonProperty
  public long internal_error_responses;
  @JsonProperty
  public long run_time_milli_seconds;
  @JsonProperty
  public long average_run_time_milli_seconds;
  
  /**
   * Constructor - ResponseControllerTotal
   * @param controller
   * @return new ResponseControllerTotal Object
   * @throws Exception
   */
  @JsonIgnore
  public ResponseControllerTotal (
    String controller) throws Exception {
    
    this(controller, 0L, 0L, 0L, 0L, 0L);
  }
  
  /**
   * Constructor - ResponseControllerTotal
   * @param controller
   * @param requests
   * @param okResponses
   * @param badRequestResponses
   * @param internalErrorResponses
   * @param runTimeMilliSeconds
   * @return new ResponseControllerTotal Object
   * @throws Exception
   */
  @JsonIgnore
  public ResponseControllerTotal (
    String controller,
    long requests,
    long okResponses,
    long badRequestResponses,
    long internalErrorResponses,
    long runTimeMilliSeconds) throws Exception {
    
    this.controller = controller;
    this.requests = requests;
    this.ok_responses = okResponses;
    this.bad_request_responses = badRequestResponses;
    this.internal_error_responses = internalErrorResponses;
    this.run_time_milli_seconds = runTimeMilliSeconds;
    
    if (this.requests == 0L) {
      
      this.average_run_time_milli_seconds = 0L;
    } else {
      
      this.average_run_time_milli_seconds =
        (this.run_time_milli_seconds / this.requests);
    }
  }
  
  /**
   * add
   * @param responseControllerTotal
   * @return a new ResponseControllerTotal with counter values representing the
   *           addition of this Object's counter values + param
   *           responseControllerTotal counter values
   * @throws Exception
   */
  @JsonIgnore
  public ResponseControllerTotal add (
    ResponseControllerTotal responseControllerTotal) throws Exception {
    
    return new ResponseControllerTotal(
      this.controller,
      this.requests
      + responseControllerTotal.requests,
      this.ok_responses
      + responseControllerTotal.ok_responses,
      this.bad_request_responses
      + responseControllerTotal.bad_request_responses,
      this.internal_error_responses
      + responseControllerTotal.internal_error_responses,
      this.run_time_milli_seconds
      + responseControllerTotal.run_time_milli_seconds);
  }
}
