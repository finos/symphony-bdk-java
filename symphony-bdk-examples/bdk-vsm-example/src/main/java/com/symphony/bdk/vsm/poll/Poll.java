package com.symphony.bdk.vsm.poll;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Getter
@Setter
public class Poll {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Enumerated(EnumType.STRING)
  private PollStatus status = PollStatus.IN_CREATION;

  private String roomId;
  private String roomName;
  private String pollMessageId;
  private String creationMessageId;
  private Long userId;

  private String title;
  private String description;

  private String option1;
  private String option2;
  private String option3;
  private String option4;

  @OneToMany(mappedBy = "poll")
  private List<Vote> votes = new ArrayList<>();
}
