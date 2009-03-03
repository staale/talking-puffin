package com.davebsoft.sctw.ui

import _root_.scala.swing._
import _root_.scala.swing.event.ButtonClicked
import filter.TagsRepository
import java.awt.Dimension
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}
/**
 * Filters
 * @author Dave Briccetti
 */

class FiltersPane(tableModel: StatusTableModel) extends GridBagPanel {
  var selectedTags = List[String]()
  
  add(new Label("Tags"), new Constraints {gridx=0; gridy=0; gridwidth=3})
  
  add(new ScrollPane {
    preferredSize = new Dimension(100, 200)
    val listView = new ListView(TagsRepository.get)
    val selModel = listView.peer.getSelectionModel
    selModel.addListSelectionListener(new ListSelectionListener(){
      def valueChanged(e: ListSelectionEvent) = {
        if (! e.getValueIsAdjusting()) {
          selectedTags = List[String]()
          for (tag <- listView.peer.getSelectedValues) {
            selectedTags ::= tag.asInstanceOf[String]
          }
        }
      }
    })
    contents = listView
  }, new Constraints {gridx=0; gridy=1; gridwidth=3})

  val excludeNotToYouReplies = new CheckBox("Exclude replies not to you")
  add(excludeNotToYouReplies, new Constraints {gridx=0; gridy=2; gridwidth=3})
  listenTo(excludeNotToYouReplies)
  
  add(new Label("Include Matching"), new Constraints {gridx=0; gridy=3})
  val includeMatching = new TextField {
    columns = 40
  }
  add(includeMatching, new Constraints {gridx=1; gridy=3})
  
  val includeIsRegex = new CheckBox("Regular Expression")
  add(includeIsRegex, new Constraints {gridx=2; gridy=3})

  add(new Label("Exclude Matching"), new Constraints {gridx=0; gridy=4})
  val excludeMatching = new TextField {
    columns = 40
  }
  add(excludeMatching, new Constraints {gridx=1; gridy=4})

  val excludeIsRegex = new CheckBox("Regular Expression")
  add(excludeIsRegex, new Constraints {gridx=2; gridy=4})

  val applyButton = new Button("Apply")
  add(applyButton, new Constraints {gridx=0; gridy=20; gridwidth=3})
  listenTo(applyButton)

  reactions += {
    case ButtonClicked(b) => {
      if (b == applyButton) {
        tableModel.selectedTags = selectedTags
        tableModel.excludeNotToYouReplies = excludeNotToYouReplies.selected
        tableModel.setIncludeMatching(includeMatching.text, includeIsRegex.selected)
        tableModel.setExcludeMatching(excludeMatching.text, excludeIsRegex.selected)
        tableModel.applyFilters
      }
    }
  }
  add(new Label(""), new Constraints {gridx=4; gridy=0; fill=GridBagPanel.Fill.Horizontal; weightx=1; })
  add(new Label(""), new Constraints {gridx=0; gridy=21; fill=GridBagPanel.Fill.Vertical; weighty=1;})
}