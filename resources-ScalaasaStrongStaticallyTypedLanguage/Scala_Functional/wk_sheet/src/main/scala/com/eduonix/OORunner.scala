package com.eduonix

abstract class Entity(id:Int, name: String) {

  def entityId = id;
  def entityName = name;
}

trait EntityToJson {
  def exportToJson(e :Entity) { println(s"id:${e.entityId},  name:${e.entityName}") } }

trait EntityToXml {
  def exportToXml(e :Entity) { println(s"<entity><${e.entityId}/> <name=${e.entityName}/></entity>") }}

class User (id:Int, name: String)  extends Entity (id, name)  with EntityToJson with EntityToXml {   }

object Runner extends App  {
	val admin = new User(1,"admin");
	admin.exportToJson(admin)
	admin.exportToXml(admin)
}

