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

package com.vangav.vos_instagram_dash_board.controllers.get_annual_regional_counters.response_json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mustapha
 * fb.com/mustapha.abdallah
 */
/**
 * ResponseRegionTotal represents a region's total count for all the requested
 *   years
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseRegionTotal {

  @JsonProperty
  public String region;
  @JsonProperty
  public long new_users;
  @JsonProperty
  public long new_posts;
  @JsonProperty
  public long sent_follows;
  @JsonProperty
  public long received_follows;
  @JsonProperty
  public long sent_unfollows;
  @JsonProperty
  public long received_unfollows;
  @JsonProperty
  public long sent_likes;
  @JsonProperty
  public long received_likes;
  @JsonProperty
  public long sent_comments;
  @JsonProperty
  public long received_comments;
  
  /**
   * Constructor - ResponseRegionTotal
   * @param region
   * @return new ResponseRegionTotal Object
   * @throws Exception
   */
  @JsonIgnore
  public ResponseRegionTotal (
    String region) throws Exception {
    
    this(region, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
  }
  
  /**
   * Constructor - ResponseRegionTotal
   * @param region
   * @param newUsers
   * @param newPosts
   * @param sentFollows
   * @param receivedFollows
   * @param sentUnfollows
   * @param receivedUnfollows
   * @param sentLikes
   * @param receivedLikes
   * @param sentComments
   * @param receivedComments
   * @return new ResponseRegionTotal Object
   * @throws Exception
   */
  @JsonIgnore
  public ResponseRegionTotal (
    String region,
    long newUsers,
    long newPosts,
    long sentFollows,
    long receivedFollows,
    long sentUnfollows,
    long receivedUnfollows,
    long sentLikes,
    long receivedLikes,
    long sentComments,
    long receivedComments) throws Exception {
    
    this.region = region;
    this.new_users = newUsers;
    this.new_posts = newPosts;
    this.sent_follows = sentFollows;
    this.received_follows = receivedFollows;
    this.sent_unfollows = sentUnfollows;
    this.received_unfollows = receivedUnfollows;
    this.sent_likes = sentLikes;
    this.received_likes = receivedLikes;
    this.sent_comments = sentComments;
    this.received_comments = receivedComments;
  }
  
  /**
   * add
   * @param responseRegionTotal
   * @return a new ResponseRegionTotal with counter values representing the
   *           addition of this Object's counter values + param
   *           responseRegionTotal's counter values
   * @throws Exception
   */
  @JsonIgnore
  public ResponseRegionTotal add (
    ResponseRegionTotal responseRegionTotal) throws Exception {
    
    return new ResponseRegionTotal(
      this.region,
      this.new_users + responseRegionTotal.new_users,
      this.new_posts + responseRegionTotal.new_posts,
      this.sent_follows + responseRegionTotal.sent_follows,
      this.received_follows + responseRegionTotal.received_follows,
      this.sent_unfollows + responseRegionTotal.sent_unfollows,
      this.received_unfollows + responseRegionTotal.received_unfollows,
      this.sent_likes + responseRegionTotal.sent_likes,
      this.received_likes + responseRegionTotal.received_likes,
      this.sent_comments + responseRegionTotal.sent_comments,
      this.received_comments + responseRegionTotal.received_comments);
  }
}
