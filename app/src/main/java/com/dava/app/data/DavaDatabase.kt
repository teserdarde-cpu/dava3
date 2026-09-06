package com.dava.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dava.app.model.Contact
import com.dava.app.model.FollowUp
import com.dava.app.model.NoteItem
import com.dava.app.model.Project
import org.json.JSONArray
import org.json.JSONObject

class DavaDatabase(context: Context) : SQLiteOpenHelper(context, "dava2.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE contacts(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,role TEXT,district TEXT,category TEXT,phone TEXT,lastContact TEXT,nextContact TEXT,note TEXT)")
        db.execSQL("CREATE TABLE followups(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,context TEXT,dueDate TEXT,status TEXT,priority INTEGER,progress INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE projects(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,description TEXT,progress INTEGER,status TEXT)")
        db.execSQL("CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,body TEXT,category TEXT,createdAt TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun addContact(v: Contact): Long = writableDatabase.insert("contacts", null, ContentValues().apply {
        put("name", v.name); put("role", v.role); put("district", v.district); put("category", v.category); put("phone", v.phone)
        put("lastContact", v.lastContact); put("nextContact", v.nextContact); put("note", v.note)
    })
    fun contacts(): List<Contact> = readableDatabase.rawQuery("SELECT * FROM contacts ORDER BY name COLLATE NOCASE", null).use { c ->
        buildList { while (c.moveToNext()) add(Contact(c.getLong(0), c.getString(1), c.getString(2) ?: "", c.getString(3) ?: "", c.getString(4) ?: "Kişi", c.getString(5) ?: "", c.getString(6) ?: "", c.getString(7) ?: "", c.getString(8) ?: "")) }
    }
    fun deleteContact(id: Long) { writableDatabase.delete("contacts", "id=?", arrayOf(id.toString())) }

    fun addFollowUp(v: FollowUp): Long = writableDatabase.insert("followups", null, ContentValues().apply {
        put("title", v.title); put("context", v.context); put("dueDate", v.dueDate); put("status", v.status); put("priority", v.priority); put("progress", v.progress.coerceIn(0,100))
    })
    fun followUps(): List<FollowUp> = readableDatabase.rawQuery("SELECT * FROM followups ORDER BY CASE status WHEN 'Tamam' THEN 1 ELSE 0 END, priority DESC, id DESC", null).use { c ->
        buildList { while (c.moveToNext()) add(FollowUp(c.getLong(0), c.getString(1), c.getString(2) ?: "", c.getString(3) ?: "", c.getString(4) ?: "Açık", c.getInt(5), c.getInt(6))) }
    }
    fun updateFollowUp(v: FollowUp) {
        writableDatabase.update("followups", ContentValues().apply {
            put("title", v.title); put("context", v.context); put("dueDate", v.dueDate); put("status", v.status); put("priority", v.priority); put("progress", v.progress.coerceIn(0,100))
        }, "id=?", arrayOf(v.id.toString()))
    }
    fun deleteFollowUp(id: Long) { writableDatabase.delete("followups", "id=?", arrayOf(id.toString())) }

    fun addProject(v: Project): Long = writableDatabase.insert("projects", null, ContentValues().apply {
        put("title", v.title); put("description", v.description); put("progress", v.progress.coerceIn(0,100)); put("status", v.status)
    })
    fun projects(): List<Project> = readableDatabase.rawQuery("SELECT * FROM projects ORDER BY id DESC", null).use { c ->
        buildList { while (c.moveToNext()) add(Project(c.getLong(0), c.getString(1), c.getString(2) ?: "", c.getInt(3), c.getString(4) ?: "Planlama")) }
    }
    fun updateProject(v: Project) {
        writableDatabase.update("projects", ContentValues().apply {
            put("title", v.title); put("description", v.description); put("progress", v.progress.coerceIn(0,100)); put("status", v.status)
        }, "id=?", arrayOf(v.id.toString()))
    }
    fun deleteProject(id: Long) { writableDatabase.delete("projects", "id=?", arrayOf(id.toString())) }

    fun addNote(v: NoteItem): Long = writableDatabase.insert("notes", null, ContentValues().apply {
        put("title", v.title); put("body", v.body); put("category", v.category); put("createdAt", v.createdAt)
    })
    fun notes(): List<NoteItem> = readableDatabase.rawQuery("SELECT * FROM notes ORDER BY id DESC", null).use { c ->
        buildList { while (c.moveToNext()) add(NoteItem(c.getLong(0), c.getString(1), c.getString(2) ?: "", c.getString(3) ?: "Not", c.getString(4) ?: "")) }
    }
    fun deleteNote(id: Long) { writableDatabase.delete("notes", "id=?", arrayOf(id.toString())) }

    fun clearAll() {
        writableDatabase.beginTransaction()
        try {
            writableDatabase.delete("contacts", null, null); writableDatabase.delete("followups", null, null)
            writableDatabase.delete("projects", null, null); writableDatabase.delete("notes", null, null)
            writableDatabase.setTransactionSuccessful()
        } finally { writableDatabase.endTransaction() }
    }

    fun exportJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("contacts", JSONArray().apply { contacts().forEach { v -> put(JSONObject().apply {
            put("name",v.name); put("role",v.role); put("district",v.district); put("category",v.category); put("phone",v.phone); put("lastContact",v.lastContact); put("nextContact",v.nextContact); put("note",v.note)
        }) } })
        root.put("followups", JSONArray().apply { followUps().forEach { v -> put(JSONObject().apply {
            put("title",v.title); put("context",v.context); put("dueDate",v.dueDate); put("status",v.status); put("priority",v.priority); put("progress",v.progress)
        }) } })
        root.put("projects", JSONArray().apply { projects().forEach { v -> put(JSONObject().apply {
            put("title",v.title); put("description",v.description); put("progress",v.progress); put("status",v.status)
        }) } })
        root.put("notes", JSONArray().apply { notes().forEach { v -> put(JSONObject().apply {
            put("title",v.title); put("body",v.body); put("category",v.category); put("createdAt",v.createdAt)
        }) } })
        return root.toString(2)
    }

    fun importJson(text: String) {
        val root = JSONObject(text)
        val cs = root.optJSONArray("contacts") ?: JSONArray()
        val fs = root.optJSONArray("followups") ?: JSONArray()
        val ps = root.optJSONArray("projects") ?: JSONArray()
        val ns = root.optJSONArray("notes") ?: JSONArray()
        clearAll()
        for (i in 0 until cs.length()) cs.getJSONObject(i).let { o -> addContact(Contact(name=o.optString("name"), role=o.optString("role"), district=o.optString("district"), category=o.optString("category","Kişi"), phone=o.optString("phone"), lastContact=o.optString("lastContact"), nextContact=o.optString("nextContact"), note=o.optString("note"))) }
        for (i in 0 until fs.length()) fs.getJSONObject(i).let { o -> addFollowUp(FollowUp(title=o.optString("title"), context=o.optString("context"), dueDate=o.optString("dueDate"), status=o.optString("status","Açık"), priority=o.optInt("priority",1), progress=o.optInt("progress",0))) }
        for (i in 0 until ps.length()) ps.getJSONObject(i).let { o -> addProject(Project(title=o.optString("title"), description=o.optString("description"), progress=o.optInt("progress",0), status=o.optString("status","Planlama"))) }
        for (i in 0 until ns.length()) ns.getJSONObject(i).let { o -> addNote(NoteItem(title=o.optString("title"), body=o.optString("body"), category=o.optString("category","Not"), createdAt=o.optString("createdAt"))) }
    }

    fun exportCsv(): String = buildString {
        appendLine("TYPE;TITLE/NAME;DETAIL;DISTRICT/STATUS;PROGRESS")
        contacts().forEach { appendLine("CONTACT;${csv(it.name)};${csv(it.role)};${csv(it.district)};") }
        followUps().forEach { appendLine("FOLLOWUP;${csv(it.title)};${csv(it.context)};${csv(it.status)};${it.progress}") }
        projects().forEach { appendLine("PROJECT;${csv(it.title)};${csv(it.description)};${csv(it.status)};${it.progress}") }
        notes().forEach { appendLine("NOTE;${csv(it.title)};${csv(it.body)};${csv(it.category)};") }
    }

    private fun csv(s: String): String = "\"${s.replace("\"", "\"\"")}\""
}
