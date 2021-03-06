package org.talkingpuffin.filter

import scala.swing.Publisher
import org.talkingpuffin.ui.{Relationships}
import org.talkingpuffin.twitter.TwitterStatus
import org.talkingpuffin.filter.RetweetDetector._
import org.talkingpuffin.util.Loggable

/**
 * A set of all filters, and logic to apply them
 */
class FilterSet(tagUsers: TagUsers) extends Publisher with Loggable {
  
  var excludeFriendRetweets: Boolean = false
  var excludeNonFollowers: Boolean = false
  var useNoiseFilters: Boolean = false
  
  val includeSet = new InOutFilters(tagUsers)
  val excludeSet = new InOutFilters(tagUsers)
  
  val adder = new FilterAdder(this)
  
  /**
   * Filter the given list of statuses, returning a list of only those that pass the filters
   * in this set.
   */
  def filter(statuses: List[TwitterStatus], rels: Relationships): List[TwitterStatus] = {
    val friendUsernames = rels.friends.map(_.screenName)
    
    def includeStatus(status: TwitterStatus): Boolean = {
      def tagFiltersInclude = includeSet.tags == Nil || includeSet.tagMatches(status.user.id)
      def excludedByTags = excludeSet.tagMatches(status.user.id)
    
      def excludedByCompoundFilters: Boolean = {
        (includeSet.cpdFilters.list != Nil && !includeSet.cpdFilters.matchesAll(status)) ||
                excludeSet.cpdFilters.matchesAny(status)
      }

      tagFiltersInclude && ! excludedByTags && 
          ! (excludeFriendRetweets && status.isRetweetOfStatusFromFriend(friendUsernames)) &&
          ! (excludeNonFollowers && ! rels.followerIds.contains(status.user.id)) &&
          ! (useNoiseFilters && NoiseFilter.isNoise(status.text)) &&
          ! excludedByCompoundFilters
    }

    statuses.filter(includeStatus)
  }
  
  def publish: Unit = publish(new FilterSetChanged(this))
}

