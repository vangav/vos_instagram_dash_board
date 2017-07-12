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

package com.vangav.vos_instagram_dash_board.controllers.get_top_users.response_json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mustapha
 * fb.com/mustapha.abdallah
 */
/**
 * ResponseTopUser represents a response's top user
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTopUser {

  @JsonProperty
  public double user_rank;
  @JsonProperty
  public String user_email;
  @JsonProperty
  public String user_name;
  @JsonProperty
  public String user_registration_date;
  @JsonProperty
  public long user_follow_count_last_week;
  @JsonProperty
  public long user_unfollow_count_last_week;
  @JsonProperty
  public long user_posts_count_last_week;
  @JsonProperty
  public long user_likes_count_last_week;
  @JsonProperty
  public long user_comments_count_last_week;
  @JsonProperty
  public long user_follow_count_total;
  @JsonProperty
  public long user_posts_count_total;
  @JsonProperty
  public long user_likes_count_total;
  @JsonProperty
  public long user_comments_count_total;
  
  /**
   * Constructor - ResponseTopUser
   * @param userRank
   * @param userEmail
   * @param userName
   * @param userRegistrationDate
   * @param userFollowCountLastWeek
   * @param userUnfollowCountLastWeek
   * @param userPostsCountLastWeek
   * @param userLikesCountLastWeek
   * @param userCommentsCountLastWeek
   * @param userFollowCountTotal
   * @param userPostsCountTotal
   * @param userLikesCountTotal
   * @param userCommentsCountTotal
   * @return new ResponseTopUser Object
   * @throws Exception
   */
  @JsonIgnore
  public ResponseTopUser (
    double userRank,
    String userEmail,
    String userName,
    String userRegistrationDate,
    long userFollowCountLastWeek,
    long userUnfollowCountLastWeek,
    long userPostsCountLastWeek,
    long userLikesCountLastWeek,
    long userCommentsCountLastWeek,
    long userFollowCountTotal,
    long userPostsCountTotal,
    long userLikesCountTotal,
    long userCommentsCountTotal) throws Exception {
    
    this.user_rank = userRank;
    this.user_email = userEmail;
    this.user_name = userName;
    this.user_registration_date = userRegistrationDate;
    this.user_follow_count_last_week = userFollowCountLastWeek;
    this.user_unfollow_count_last_week = userUnfollowCountLastWeek;
    this.user_posts_count_last_week = userPostsCountLastWeek;
    this.user_likes_count_last_week = userLikesCountLastWeek;
    this.user_comments_count_last_week = userCommentsCountLastWeek;
    this.user_follow_count_total = userFollowCountTotal;
    this.user_posts_count_total = userPostsCountTotal;
    this.user_likes_count_total = userLikesCountTotal;
    this.user_comments_count_total = userCommentsCountTotal;
  }
}
