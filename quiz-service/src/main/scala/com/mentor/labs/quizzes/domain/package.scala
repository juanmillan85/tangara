package com.mentor.labs.quizzes

import shapeless.{:+:, CNil}

package object domain {

  case class Answer(id: Long, value: String)

  sealed trait QuestionType

  case class SimpleQuestionType(id: Long, title: String, answers: Seq[Answer], correct: Answer, topics: String)
    extends QuestionType

  case class MultiValueQuestionType(id: Long, title: String, answers: Seq[Answer], correct: Seq[Answer])
    extends QuestionType

  type Question = SimpleQuestionType :+: MultiValueQuestionType :+: CNil

  case class QuizTemplate(id: Long, title: String, description: String, questions: Seq[Question])

  case class Feedback(value: Boolean, description: String)

  case class QuizInfo(id: Long, title: String, description: String)

  case class EvaluatedQuestion(questionId: Long, score: Double)

  case class Quiz(quizId: Long, userId: Long, quizTemplateId: Option[Long], startTime: String, finishedTime: Option[String],
                  questions: Seq[Question], evaluatedQuestions: Seq[EvaluatedQuestion], status: Int, score: Double)

}
