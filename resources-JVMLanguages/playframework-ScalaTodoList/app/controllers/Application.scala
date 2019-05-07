package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.Task

object Application extends Controller {

  val taskForm = Form(
	  "label" -> nonEmptyText
  )  

  // Actions are a scala type(class) that implements a specific behaviour
  // defined in a Scala "trait"
  def index = Action {
	  Redirect(routes.Application.tasks)
  }

  def tasks = Action {
	  Ok(views.html.index(Task.all(), taskForm))
  }



  // It is often useful to mark the request parameter
  // as implicit so it can be implicitely used
  def newTask = Action { implicit request =>
      taskForm.bindFromRequest.fold(
          errors => BadRequest(views.html.index(Task.all(), errors)),
          label => {
        	  Task.create(label)
        	  Redirect(routes.Application.tasks)
          }
      )
  }

  def deleteTask(id: Long) = Action {
	Task.delete(id)
	Redirect(routes.Application.tasks)
  }
  
}