package com.davebsoft.sctw.ui

import _root_.scala.swing.Action
import scala.swing.Component
import javax.swing.{JToolBar, JToggleButton}

/**
 * Status pane tool bar
 * @author Dave Briccetti
 */
class StatusToolBar(session: Session, filtersDialog: FiltersDialog, apiHandlers: ApiHandlers, 
    statusPane: Component, clearTweets: => Unit) extends JToolBar {
  var tweetDetailPanel: TweetDetailPanel = _
  
  val showFiltersAction = new Action("Filter…") {
    toolTip = "Set filters for this stream"
    def apply = filtersDialog.visible = true
  }

  val clearAction = new Action("Clear") {
    toolTip = "Removes all tweets (including filtered-out ones)"
    def apply = clearTweets
  }

  val sendAction = new Action("Send…") {
    toolTip = "Opens a window from which you can send a tweet"
    def apply = (new SendMsgDialog(session, null, apiHandlers.sender, None, None)).visible = true
  }

  val clearRepliesAction = new Action("Clear") {
    toolTip = "Removes all mentions"
    def apply = clearTweets
  }

  var detailsButton: JToggleButton = _ 
  val showDetailsAction = new Action("Details") {
    toolTip = "Shows or hides the details panel"
    def apply = tweetDetailPanel.visible = detailsButton.isSelected
  }
  detailsButton = new JToggleButton(showDetailsAction.peer)
  detailsButton.setSelected(true)

  var geoButton: JToggleButton = _ 
  val geoAction = new Action("Geo") {
    toolTip = "Enables lookup of locations from latitude and longitude"
    def apply = tweetDetailPanel.geoEnabled = geoButton.isSelected
  }
  geoButton = new JToggleButton(geoAction.peer)
  geoButton.setSelected(true)

  var animButton: JToggleButton = _ 
  val animAction = new Action("Anim") {
    toolTip = "Enables simple, useful animations"
    def apply = tweetDetailPanel.enableAnimation(animButton.isSelected)
  }
  animButton = new JToggleButton(animAction.peer)
  animButton.setSelected(true)

  var dockedButton: JToggleButton = _ 
  val dockedAction = new Action("Docked") {
    toolTip = "Docks or frees the pane"
    def apply = {
      if (! dockedButton.isSelected) {
        session.windows.undock(statusPane)
      } else {
        session.windows.dock(statusPane)
      }
    }
  }
  dockedButton = new JToggleButton(dockedAction.peer)
  dockedButton.setSelected(true)

  setFloatable(false)
  add(sendAction.peer)
  add(showFiltersAction.peer)
  add(clearAction.peer)
  addSeparator
  add(dockedButton)
  add(detailsButton)
  add(geoButton)
  add(animButton)
}
  
