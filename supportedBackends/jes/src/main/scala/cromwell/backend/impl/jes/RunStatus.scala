package cromwell.backend.impl.jes

import cromwell.core.ExecutionEvent

sealed trait RunStatus {
  import RunStatus._

  // Could be defined as false for Initializing and true otherwise, but this is more defensive.
  def isRunningOrComplete = this match {
    case Running | _: TerminalRunStatus => true
    case _ => false
  }
}

object RunStatus {
  case object Initializing extends RunStatus
  case object Running extends RunStatus

  sealed trait TerminalRunStatus extends RunStatus {
    def eventList: Seq[ExecutionEvent]
    def machineType: Option[String]
    def zone: Option[String]
    def instanceName: Option[String]
  }

  sealed trait UnsuccessfulRunStatus extends TerminalRunStatus {
    def errorMessage: Option[String]
    def errorCode: Int
  }

  case class Success(eventList: Seq[ExecutionEvent],
                     machineType: Option[String],
                     zone: Option[String],
                     instanceName: Option[String]) extends TerminalRunStatus {
    override def toString = "Success"
  }

  final case class Failed(errorCode: Int,
                          errorMessage: Option[String],
                          eventList: Seq[ExecutionEvent],
                          machineType: Option[String],
                          zone: Option[String],
                          instanceName: Option[String]) extends UnsuccessfulRunStatus {
    override def toString = "Failed"
  }

  final case class Preempted(errorCode: Int,
                          errorMessage: Option[String],
                          eventList: Seq[ExecutionEvent],
                          machineType: Option[String],
                          zone: Option[String],
                          instanceName: Option[String]) extends UnsuccessfulRunStatus {
    override def toString = "Preempted"
  }
}
