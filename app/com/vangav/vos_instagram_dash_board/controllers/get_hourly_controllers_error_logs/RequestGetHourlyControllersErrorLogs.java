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

package com.vangav.vos_instagram_dash_board.controllers.get_hourly_controllers_error_logs;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vangav.backend.play_framework.param.ParamOptionality;
import com.vangav.backend.play_framework.param.ParamType;
import com.vangav.backend.play_framework.request.RequestJsonBodyGet;

/**
 * GENERATED using ControllersGeneratorMain.java
 */
/**
 * RequestGetHourlyControllersErrorLogs represents the request's structure
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestGetHourlyControllersErrorLogs extends RequestJsonBodyGet {

  @Override
  @JsonIgnore
  protected String getName () throws Exception {

    return "GetHourlyControllersErrorLogs";
  }

  @Override
  @JsonIgnore
  protected RequestGetHourlyControllersErrorLogs getThis () throws Exception {

    return this;
  }

  @JsonIgnore
  public static final String kFromDateName = "from_date";
  @JsonProperty
  public String from_date;

  @JsonIgnore
  public static final String kToDateName = "to_date";
  @JsonProperty
  public String to_date;

  @JsonIgnore
  public static final String kFromHourName = "from_hour";
  @JsonProperty
  public int from_hour;

  @JsonIgnore
  public static final String kToHourName = "to_hour";
  @JsonProperty
  public int to_hour;

  @JsonIgnore
  public static final String kControllersName = "controllers";
  @JsonProperty
  public String[] controllers;

  @Override
  @JsonIgnore
  public RequestGetHourlyControllersErrorLogs fromQueryString (
    Map<String, String[]> query) throws Exception {

    this.from_date = this.getString(kFromDateName, query);
    this.to_date = this.getString(kToDateName, query);
    this.from_hour = this.getInt(kFromHourName, query);
    this.to_hour = this.getInt(kToHourName, query);
    this.controllers = this.getStringArray(kControllersName, query);

    return this;
  }

  @Override
  @JsonIgnore
  public void validate () throws Exception {

    this.validate(
      kFromDateName,
      this.from_date,
      ParamType.DATE,
      ParamOptionality.MANDATORY);

    this.validate(
      kToDateName,
      this.to_date,
      ParamType.DATE,
      ParamOptionality.OPTIONAL);

    this.validate(
      kFromHourName,
      this.from_hour,
      ParamType.INT,
      ParamOptionality.MANDATORY);

    this.validate(
      kToHourName,
      this.to_hour,
      ParamType.INT,
      ParamOptionality.OPTIONAL);

    this.validate(
      kControllersName,
      this.controllers,
      ParamType.ALPHA_NUMERIC_UNDERSCORE,
      ParamOptionality.MANDATORY);

  }
}
