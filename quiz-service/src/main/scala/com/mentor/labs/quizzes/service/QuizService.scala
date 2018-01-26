package com.mentor.labs.quizzes.service

import com.mentor.labs.quizzes.domain.{Feedback, Question, QuizInfo}

/**
  * Created by juancifuentes on 05/01/18.
  */
trait QuizService {
  def getPendingQuizzes(userId: String): Seq[QuizInfo]

  def getDescription(id: Long): String

  def askQuestion(uid: Long, id: String): Question

  def submitAnswer(uid: Long, id: String*): Feedback
}
